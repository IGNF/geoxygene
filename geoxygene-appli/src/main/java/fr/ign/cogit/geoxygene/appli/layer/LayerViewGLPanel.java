package fr.ign.cogit.geoxygene.appli.layer;

import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.gl.GLBezierShadingVertex;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.GLPaintingVertex;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.Subshader;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.render.GeoxRendererManager;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.SyncRenderingManager;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.appli.render.texture.ShaderFactory;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ShaderDescriptor;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;
import fr.ign.cogit.geoxygene.util.ImageComparator;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * LayerViewGLPanel is the basic implementation of a GL Viewer. It adds a glass
 * Pane over the GL window in order to draw Swing drawings over the GL view. !!
 * Do not add children to this element, use setGLComponent() only !!
 * 
 * @author turbet
 * 
 */
public class LayerViewGLPanel extends LayerViewPanel implements ItemListener,
        ActionListener {

    private static final String backgroundVertexShaderFilename = "./src/main/resources/shaders/bg.vert.glsl";
    private static final String backgroundFragmentShaderFilename = "./src/main/resources/shaders/bg.frag.glsl";
    private static final String gradientVertexShaderFilename = "./src/main/resources/shaders/gradient.vert.glsl";
    private static final String gradientFragmentShaderFilename = "./src/main/resources/shaders/gradient.frag.glsl";
    private static final String antialiasedFragmentShaderFilename = "./src/main/resources/shaders/antialiased.frag.glsl";
    private static final String textScreenspaceVertexShaderFilename = "./src/main/resources/shaders/text-screenspace.vert.glsl";
    private static final String textLayerFragmentShaderFilename = "./src/main/resources/shaders/text-layer.frag.glsl";
    private static final String colorFragmentShaderFilename = "./src/main/resources/shaders/polygon.color.frag.glsl";
    private static final String textureFragmentShaderFilename = "./src/main/resources/shaders/polygon.texture.frag.glsl";
    private static final String screenspaceVertexShaderFilename = "./src/main/resources/shaders/screenspace.vert.glsl";
    private static final String bezierFragmentShaderFilename = "./src/main/resources/shaders/bezier.frag.glsl";
    private static final String bezierVertexShaderFilename = "./src/main/resources/shaders/bezier.vert.glsl";
    private static final String linePaintingFragmentShaderFilename = "./src/main/resources/shaders/linepainting.frag.glsl";
    private static final String linePaintingVertexShaderFilename = "./src/main/resources/shaders/linepainting.vert.glsl";
    private static final String worldspaceVertexShaderFilename = "./src/main/resources/shaders/worldspace.vert.glsl";
    private static final long serialVersionUID = -7181604491025859187L; // serializable
                                                                        // UID
    // private static final int GLASS_LAYER_INDEX = 10; // layer index on which
    // the overlay Swing stuff will be drawn
    // private static final int GL_LAYER_INDEX = 1; // layer index on which the
    // GL
    // stuff will be rendered
    private static Logger logger = Logger.getLogger(LayerViewGLPanel.class
            .getName());

    private SyncRenderingManager renderingManager = null;
    private LayerViewGLCanvas glCanvas = null; // canvas containing the GL
                                               // context
    private static GLSimpleComplex screenQuad; // quad drawn on the full screen
    private LayerViewGLCanvasType glType = null;
    private JToggleButton wireframeToggleButton = null;
    private JToggleButton fboToggleButton = null;
    private JToggleButton animationButton = null;
    private JToggleButton statisticsButton = null;
    private JButton antialiasingButton = null;
    private JButton clearCacheButton = null;
    private JButton awtComparButton = null;
    private JButton reloadShadersButton = null;
    private JToolBar.Separator toolbarSeparator = null;
    private JMenu glMenu = null;
    private JMenuItem glInformationMenu = null;
    private boolean wireframe = false;
    private int antialiasing = 2;
    private boolean useFBO = true;
    private boolean useContinuousRendering = false;
    private int fboId = -1;
    private int fboTextureId = -1;

    public enum LayerViewGLCanvasType {
        GL1, GL4
    }

    // private final JLayeredPane layeredPane = null;
    // private final JPanel glPanel = null;
    // private final Component glassPanel = null;

    /**
     * 
     */
    public LayerViewGLPanel(final LayerViewGLCanvasType glType) {
        super();
        this.addPaintListener(new ScalePaintListener());
        this.addPaintListener(new CompassPaintListener());
        this.addPaintListener(new LegendPaintListener());
        // this.setBackground(new Color(255, 255, 220));
        this.renderingManager = new SyncRenderingManager(this,
                RenderingType.LWJGL);

        this.glCanvas = LayerViewPanelFactory
                .newLayerViewGLCanvas(this, glType);
        this.setGlType(glType);
        this.setLayout(new BorderLayout());
        // Attach LWJGL to the created canvas
        this.setGLComponent(this.glCanvas);

    }

    public StyledLayerDescriptor getSld() {
        return this.getProjectFrame().getSld();
    }

    @Override
    public void displayGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getReloadShadersButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getAnimationButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getStatisticsButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();

        this.getProjectFrame().getMainFrame().getMenuBar()
                .add(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();

    }

    @Override
    public void hideGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getReloadShadersButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getAnimationButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getStatisticsButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();

        this.getProjectFrame().getMainFrame().getMenuBar()
                .remove(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();
    }

    public boolean useFBO() {
        return this.useFBO;
    }

    public void setFBO(boolean useFBO) {
        this.useFBO = useFBO;
    }

    /**
     * @return the fboId
     */
    public int getFboId() {
        if (this.fboId == -1) {
            // generate an ID for the FBO
            this.fboId = glGenFramebuffers();
            if (this.fboId < 0) {
                logger.error("Unable to create frame buffer for FBO rendering");
            }

        }
        return this.fboId;
    }

    public int getFBOTextureId() {
        // System.err.println("get FBO texture ID : " + this.fboTextureId);
        if (this.fboTextureId == -1) {
            this.fboTextureId = GL11.glGenTextures();
            // System.err.println("generated FBO texture ID : "
            // + this.fboTextureId);
            if (this.fboTextureId < 0) {
                logger.error("Unable to use FBO texture");
            }
        }
        return this.fboTextureId;
    }

    /**
     * @return the useContinuousRendering
     */
    public boolean useContinuousRendering() {
        return this.useContinuousRendering;
    }

    /**
     * @param useContinuousRendering
     *            the useContinuousRendering to set
     */
    public void setContinuousRendering(boolean useContinuousRendering) {
        this.useContinuousRendering = useContinuousRendering;
    }

    public int getAntialiasingSize() {
        return this.antialiasing;
    }

    public void setAntialiasing(int b) {
        this.antialiasing = b;
    }

    public boolean useWireframe() {
        return this.wireframe;
    }

    public void setWireframe(boolean b) {
        this.wireframe = b;
    }

    @Override
    public SyncRenderingManager getRenderingManager() {
        return this.renderingManager;
    }

    /**
     * activate the GL context
     */
    public void activateGLContext() {
        this.glCanvas.activateContext();
    }

    /**
     * @return the lwJGLCanvas
     */
    public LayerViewGLCanvas getLwJGLCanvas() {
        return this.glCanvas;
    }

    /**
     * @param lwJGLCanvas
     *            the lwJGLCanvas to set
     */
    public void setGLCanvas(LayerViewGLCanvas lwJGLCanvas) {
        this.glCanvas = lwJGLCanvas;
    }

    private JToolBar.Separator getToolbarSeparator() {
        if (this.toolbarSeparator == null) {
            this.toolbarSeparator = new JToolBar.Separator();
        }
        return this.toolbarSeparator;
    }

    private JToggleButton getWireframeButton() {
        if (this.wireframeToggleButton == null) {
            this.wireframeToggleButton = new JToggleButton();
            this.wireframeToggleButton.setIcon(new ImageIcon(
                    MainFrameToolBar.class
                            .getResource("/images/icons/16x16/wireframe.png")));
            this.wireframeToggleButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleWireframe"));
            this.wireframeToggleButton.setSelected(this.useWireframe());
            this.wireframeToggleButton.addItemListener(this);
        }
        return this.wireframeToggleButton;
    }

    private JToggleButton getFBOButton() {
        if (this.fboToggleButton == null) {
            this.fboToggleButton = new JToggleButton();
            this.fboToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/fbo.png")));
            this.fboToggleButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleFBO"));
            this.fboToggleButton.setSelected(this.useFBO());
            this.fboToggleButton.addItemListener(this);
        }
        return this.fboToggleButton;
    }

    private JToggleButton getAnimationButton() {
        if (this.animationButton == null) {
            this.animationButton = new JToggleButton();
            this.animationButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/animate.png")));
            this.animationButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleAnimation"));
            this.animationButton.setSelected(this.useContinuousRendering());
            this.animationButton.addItemListener(this);
        }
        return this.animationButton;
    }

    private JToggleButton getStatisticsButton() {
        if (this.statisticsButton == null) {
            this.statisticsButton = new JToggleButton();
            this.statisticsButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/statistics.png")));
            this.statisticsButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleStatistics"));
            this.statisticsButton.setSelected(this.useContinuousRendering());
            this.statisticsButton.addItemListener(this);
        }
        return this.statisticsButton;
    }

    private JButton getAntialiasingButton() {
        if (this.antialiasingButton == null) {
            this.antialiasingButton = new JButton();
            this.antialiasingButton
                    .setIcon(new ImageIcon(
                            MainFrameToolBar.class
                                    .getResource("/images/icons/16x16/antialiasing.png")));
            this.antialiasingButton.setToolTipText(I18N
                    .getString("RenderingGL.LoopAntialiasing"));

            this.antialiasingButton.setText(String.valueOf(this
                    .getAntialiasingSize()));
            this.antialiasingButton.addActionListener(this);
        }
        return this.antialiasingButton;
    }

    private JButton getAWTComparButton() {
        if (this.awtComparButton == null) {
            this.awtComparButton = new JButton();
            this.awtComparButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/compare.gif")));
            this.awtComparButton.setToolTipText(I18N
                    .getString("RenderingGL.ImageComparison"));
            this.awtComparButton.addActionListener(this);
        }
        return this.awtComparButton;
    }

    private JButton getReloadShadersButton() {
        if (this.reloadShadersButton == null) {
            this.reloadShadersButton = new JButton();
            this.reloadShadersButton.setIcon(new ImageIcon(
                    MainFrameToolBar.class
                            .getResource("/images/icons/16x16/refresh.png")));
            this.reloadShadersButton.setToolTipText(I18N
                    .getString("RenderingGL.ReloadShaders"));
            this.reloadShadersButton.addActionListener(this);
        }
        return this.reloadShadersButton;
    }

    private JMenuItem getGLInformationMenu() {
        if (this.glInformationMenu == null) {
            this.glInformationMenu = new JMenuItem("Information");
            this.glInformationMenu.addActionListener(this);
        }
        return this.glInformationMenu;
    }

    private JMenu getGLMenu() {
        if (this.glMenu == null) {
            this.glMenu = new JMenu("GL");
            this.glMenu.add(this.getGLInformationMenu());
        }
        return this.glMenu;
    }

    private JButton getClearCacheButton() {
        if (this.clearCacheButton == null) {
            this.clearCacheButton = new JButton();
            this.clearCacheButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/clear.png")));
            this.clearCacheButton.setToolTipText(I18N
                    .getString("RenderingGL.ClearCache"));
            this.clearCacheButton.addActionListener(this);
        }
        return this.clearCacheButton;
    }

    // @Override
    // public final void repaint() {
    // if (this.glCanvas != null) {
    // this.glCanvas.repaint();
    // }
    // if (this.useContinuousRendering()) {
    // this.repaint();
    // }
    // }

    public GLContext getGlContext() throws GLException {
        return this.glCanvas.getGlContext();
    }

    @Override
    public final void paintComponent(final Graphics g) {
        try {
            this.glCanvas.doPaint();
        } catch (Exception e1) {
            // e1.printStackTrace();
            logger.error(I18N.getString("LayerViewPanel.PaintError") + " " + e1.getMessage()); //$NON-NLS-1$
        }
        if (this.useContinuousRendering()) {
            this.repaint();
        }
    }

    /**
     * Repaint the panel using the repaint method of the super class
     * {@link JPanel}. Called in order to perform the progressive rendering.
     * 
     * @see #paintComponent(Graphics)
     */
    @Override
    public final void superRepaint() {
        Container parent = this.getParent();
        if (parent != null) {
            parent.repaint();
        }
    }

    /** Dispose panel and its rendering manager. */
    @Override
    public void dispose() {
        this.reset();
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().dispose();
        }
        this.setViewport(null);
        // this.glPanel.setVisible(false);
        // TODO: properly close GL stuff
    }

    /**
     * Set the child Component where GL will be rendered
     * 
     * @param glComponent
     */
    protected void setGLComponent(final Component glComponent) {
        // this.add(glComponent, BorderLayout.CENTER);
        this.removeAll();
        this.add(glComponent, BorderLayout.CENTER);
        // glComponent.setBounds(0, 0, 800, 800);

    }

    @Override
    public synchronized void layerAdded(final Layer l) {
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().addLayer(l);
        }
        try {
            IEnvelope env = l.getFeatureCollection().getEnvelope();
            if (env == null) {
                env = l.getFeatureCollection().envelope();
            }
            this.getViewport().zoom(env);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void layerOrderChanged(final int oldIndex, final int newIndex) {
        this.repaint();
    }

    @Override
    public void layersRemoved(final Collection<Layer> layers) {
        this.repaint();
    }

    @Override
    public int print(final Graphics arg0, final PageFormat arg1, final int arg2)
            throws PrinterException {
        logger.error("LayerViewGlPanel::print(...) not implemented yet");
        return 0;
    }

    @Override
    public IEnvelope getEnvelope() {
        if (this.getRenderingManager().getLayers().isEmpty()) {
            return null;
        }
        List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager()
                .getLayers());
        Iterator<Layer> layerIterator = copy.iterator();
        IEnvelope envelope = layerIterator.next().getFeatureCollection()
                .envelope();
        while (layerIterator.hasNext()) {
            IFeatureCollection<? extends IFeature> collection = layerIterator
                    .next().getFeatureCollection();
            if (collection != null) {
                IEnvelope env = collection.getEnvelope();
                if (envelope == null) {
                    envelope = env;
                } else {
                    envelope.expand(env);
                }
            }
        }
        return envelope;
    }

    @Override
    public void saveAsImage(final String fileName) {
        logger.error("LayerViewGLPanel::saveAsImage(...) not implemented yet");

    }

    public LayerViewGLCanvasType getGlType() {
        return this.glType;
    }

    private final void setGlType(LayerViewGLCanvasType glType) {
        this.glType = glType;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == this.getWireframeButton()) {
            this.setWireframe(this.getWireframeButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getFBOButton()) {
            this.setFBO(this.getFBOButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getAnimationButton()) {
            this.setContinuousRendering(this.getAnimationButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getStatisticsButton()) {
            RenderingStatistics.setStatistics(this.getStatisticsButton()
                    .isSelected());
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getGLInformationMenu()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    new JTextArea(this.getGLInformation(), 80, 40),
                    "GL Information", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == this.getClearCacheButton()) {
            this.reset(); // reload shaders
            this.resetRenderers(); // refresh geometry
            this.repaint();
        } else if (e.getSource() == this.getAWTComparButton()) {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
            ImageComparator imageComparator = new ImageComparator(this);

            dialog.setSize(this.getWidth(), this.getHeight());
            dialog.setLocation(50, 50);
            // dialog.setModalityType(ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(imageComparator.getGui());
            dialog.setVisible(true);
            imageComparator.update();
        } else if (e.getSource() == this.getReloadShadersButton()) {
            GeoxRendererManager.reset();
            this.reset();
            this.repaint();
        } else if (e.getSource() == this.getReloadShadersButton()) {
            this.setContinuousRendering(this.getAnimationButton().isSelected());
            this.repaint();
        } else if (e.getSource() == this.getAntialiasingButton()) {
            int antialiasingValue = 1;
            try {
                antialiasingValue = Integer.parseInt(this
                        .getAntialiasingButton().getText());
                antialiasingValue++;
                if (this.antialiasing >= 9) {
                    antialiasingValue = 0;
                }
                this.getAntialiasingButton().setText(
                        String.valueOf(antialiasingValue));
                this.setAntialiasing(antialiasingValue);
            } catch (Exception e2) {
                this.getAntialiasingButton().setText(String.valueOf(1));
                this.setAntialiasing(1);
            }
            this.repaint();
        } else {
            // old SLD events....
            this.repaint();
        }

    }

    /**
     * clear renderer caches. It forces recomputation of all geometries
     */
    public void resetRenderers() {
        // empty cache of all renderers
        for (LayerRenderer renderer : this.getRenderingManager().getRenderers()) {
            renderer.reset();
        }
    }

    /**
     * clear renderer caches. It forces recomputation of a layer geometry
     */
    public void resetLayerRenderer(Layer layer) {

        LayerRenderer renderer = this.getRenderingManager().getRenderer(layer);
        if (renderer == null) {
            logger.error("Layer " + layer.getName()
                    + " has no associated renderer. Cannot reset it...");
            return;
        }
        renderer.reset();
    }

    private String getGLInformation() {
        this.activateGLContext();
        StringBuilder str = new StringBuilder();
        str.append("GLInformations\n");
        Class<?> contextClass = org.lwjgl.opengl.GLContext.getCapabilities()
                .getClass();
        Field[] declaredFields = contextClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    str.append("\t" + field.getName() + " : "
                            + field.getBoolean(field) + "\n");
                } catch (Exception e) {
                    str.append("\t" + field.getName() + " : error: "
                            + e.getMessage() + "\n");
                }
            }
        }
        System.err.println(str.toString());
        return str.toString();
    }

    /**
     * 
     */
    @Override
    public void reset() {
        try {
            this.activateGLContext();
            GLTools.glCheckError("GL Context activation for program destruction");
            this.getGlContext().disposeContext();
            GLTools.glCheckError("GLContext destruction");
        } catch (GLException e) {
            logger.error("Error in dispose context : " + e.getMessage());
            e.printStackTrace();
        }
        if (this.glCanvas != null) {
            this.glCanvas.reset();
        }
        if (this.renderingManager != null) {
            this.renderingManager.dispose();
        }
    }

    /**
     * @return the screenQuad
     */
    public static final GLSimpleComplex getScreenQuad() {
        if (LayerViewGLPanel.screenQuad == null) {
            LayerViewGLPanel.initializeScreenQuad();
        }
        return LayerViewGLPanel.screenQuad;
    }

    /**
     * 
     */
    private static final void initializeScreenQuad() {
        LayerViewGLPanel.screenQuad = new GLSimpleComplex("screen", 0f, 0f);
        GLMesh mesh = LayerViewGLPanel.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        LayerViewGLPanel.screenQuad.setColor(Color.blue);
        LayerViewGLPanel.screenQuad.setOverallOpacity(0.5);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // private static GLContext glContext = null;

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";

    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";
    public static final String fboWidthUniformVarName = "fboWidth";
    public static final String fboHeightUniformVarName = "fboHeight";
    public static final String globalOpacityUniformVarName = "globalOpacity";
    public static final String objectOpacityUniformVarName = "objectOpacity";
    public static final String colorTexture1UniformVarName = "colorTexture1";
    public static final String colorTexture2UniformVarName = "colorTexture2";
    public static final String gradientTextureUniformVarName = "gradientTexture";
    public static final String textureScaleFactorUniformVarName = "textureScaleFactor";
    public static final String antialiasingSizeUniformVarName = "antialiasingSize";

    public static final String paperTextureUniformVarName = "paperSampler";
    public static final String brushTextureUniformVarName = "brushSampler";
    public static final String brushWidthUniformVarName = "brushWidth";
    public static final String brushHeightUniformVarName = "brushHeight";
    public static final String brushStartWidthUniformVarName = "brushStartWidth";
    public static final String brushEndWidthUniformVarName = "brushEndWidth";
    // width of one brush pixel (mm)
    public static final String brushScaleUniformVarName = "brushScale";
    public static final String paperScaleUniformVarName = "paperScale";
    public static final String paperDensityUniformVarName = "paperDensity";
    public static final String brushDensityUniformVarName = "brushDensity";
    public static final String strokePressureUniformVarName = "strokePressure";
    public static final String sharpnessUniformVarName = "sharpness";

    public static final String basicProgramName = "Basic";
    public static final String linePaintingProgramName = "LinePainting";
    public static final String bezierLineProgramName = "BezierPainting";
    public static final String worldspaceColorProgramName = "WorldspaceColor";
    public static final String worldspaceTextureProgramName = "WorldspaceTexture";
    public static final String screenspaceColorProgramName = "ScreenspaceColor";
    public static final String screenspaceTextureProgramName = "ScreenspaceTexture";
    public static final String backgroundProgramName = "BackgroundTexture";
    public static final String screenspaceAntialiasedTextureProgramName = "ScreenspaceAntialiasedTexture";
    public static final String textLayerProgramName = "TextLayer";
    public static final String gradientProgramName = "GradientTexture";

    private final GLProgramAccessor basicAccessor = null;
    private GLProgramAccessor screenspaceTextureAccessor = null;
    private GLProgramAccessor screenspaceColorAccessor = null;
    private final GLProgramAccessor screenspaceAntialiasedAccessor = null;
    private GLProgramAccessor backgroundAccessor = null;
    private GLProgramAccessor textLayerAccessor = null;
    private GLProgramAccessor worldspaceColorAccessor = null;
    private GLProgramAccessor worldspaceTextureAccessor = null;

    // public List<Integer> getWorldspaceVertexShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.worldspaceVertexShaderId == null) {
    // this.worldspaceVertexShaderId = GLProgram
    // .createVertexShaders(worldspaceVertexShaderFilename);
    // }
    // return this.worldspaceVertexShaderId;
    // } else {
    // return GLProgram
    // .createVertexShaders(worldspaceVertexShaderFilename);
    // }
    // }
    //
    // public List<Integer> getLinePaintingVertexShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.linePaintingVertexShaderId == null) {
    // this.linePaintingVertexShaderId = GLProgram
    // .createVertexShaders(linePaintingVertexShaderFilename);
    // }
    // return this.linePaintingVertexShaderId;
    // } else {
    // return GLProgram
    // .createVertexShaders(linePaintingVertexShaderFilename);
    // }
    // }
    //
    // public List<Integer> getLinePaintingFragmentShaderId() throws GLException
    // {
    // if (this.shareShaders) {
    // if (this.linePaintingFragmentShaderId == null) {
    // this.linePaintingFragmentShaderId = GLProgram
    // .createFragmentShaders(linePaintingFragmentShaderFilename);
    // }
    // return this.linePaintingFragmentShaderId;
    // } else {
    // return GLProgram
    // .createFragmentShaders(linePaintingFragmentShaderFilename);
    // }
    // }
    //
    // public List<Integer> getBezierVertexShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.bezierVertexShaderId == null) {
    // this.bezierVertexShaderId = GLProgram
    // .createVertexShaders(bezierVertexShaderFilename);
    // }
    // return this.bezierVertexShaderId;
    // } else {
    // return GLProgram.createVertexShaders(bezierVertexShaderFilename);
    // }
    // }
    //
    // public List<Integer> createBezierFragmentShaderId(
    // ShaderDescriptor descriptor) throws GLException {
    // if (this.shareShaders) {
    // if (descriptor instanceof UserShaderDescriptor) {
    // UserShaderDescriptor userShaderDescriptor = (UserShaderDescriptor)
    // descriptor;
    // return GLProgram.createFragmentShaders(
    // bezierFragmentShaderFilename,
    // userShaderDescriptor.getFilename());
    // } else {
    // return GLProgram
    // .createFragmentShaders(bezierFragmentShaderFilename);
    // }
    // } else {
    // if (descriptor instanceof UserShaderDescriptor) {
    // UserShaderDescriptor userShaderDescriptor = (UserShaderDescriptor)
    // descriptor;
    // return GLProgram.createFragmentShaders(
    // bezierFragmentShaderFilename,
    // userShaderDescriptor.getFilename());
    // } else {
    // return GLProgram
    // .createFragmentShaders(bezierFragmentShaderFilename);
    // }
    // }
    // }
    //
    // public List<Integer> getScreenspaceVertexShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.screenspaceVertexShaderId == null) {
    // this.screenspaceVertexShaderId = GLProgram
    // .createVertexShaders(screenspaceVertexShaderFilename);
    // }
    // return this.screenspaceVertexShaderId;
    // } else {
    // return GLProgram
    // .createVertexShaders(screenspaceVertexShaderFilename);
    // }
    // }
    //
    // public List<Integer> getTextureFragmentShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.textureFragmentShaderId == null) {
    // this.textureFragmentShaderId = GLProgram
    // .createFragmentShaders(textureFragmentShaderFilename);
    // }
    // return this.textureFragmentShaderId;
    // } else {
    // return GLProgram
    // .createFragmentShaders(textureFragmentShaderFilename);
    // }
    // }
    //
    // private List<Integer> getColorFragmentShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.colorFragmentShaderId == null) {
    // this.colorFragmentShaderId = GLProgram
    // .createFragmentShaders(colorFragmentShaderFilename);
    // }
    // return this.colorFragmentShaderId;
    // } else {
    // return GLProgram.createFragmentShaders(colorFragmentShaderFilename);
    // }
    // }
    //
    // private List<Integer> getAntialiasedFragmentShaderId() throws GLException
    // {
    // if (this.shareShaders) {
    // if (this.antialiasedFragmentShaderId == null) {
    // this.antialiasedFragmentShaderId = GLProgram
    // .createFragmentShaders(antialiasedFragmentShaderFilename);
    // }
    // return this.antialiasedFragmentShaderId;
    // } else {
    // return GLProgram
    // .createFragmentShaders(antialiasedFragmentShaderFilename);
    // }
    // }
    //
    // private List<Integer> getBackgroundFragmentShaderId() throws GLException
    // {
    // if (this.shareShaders) {
    // if (this.bgFragmentShaderId == null) {
    // this.bgFragmentShaderId = GLProgram
    // .createFragmentShaders(backgroundFragmentShaderFilename);
    // }
    // return this.bgFragmentShaderId;
    // } else {
    // return GLProgram
    // .createFragmentShaders(backgroundFragmentShaderFilename);
    // }
    // }
    //
    // private List<Integer> getBackgroundVertexShaderId() throws GLException {
    // if (this.shareShaders) {
    // if (this.bgVertexShaderId == null) {
    // this.bgVertexShaderId = GLProgram
    // .createVertexShaders(backgroundVertexShaderFilename);
    // }
    // return this.bgVertexShaderId;
    // } else {
    // return GLProgram
    // .createVertexShaders(backgroundVertexShaderFilename);
    // }
    // }

    /**
     * This static method creates one GLContext containing all programs used to
     * render GeOxygene graphics elements
     * 
     * @return
     * @throws GLException
     */
    public GLContext createNewGL4Context() throws GLException {
        GLContext glContext = new GLContext();
        glContext.addProgram(worldspaceColorProgramName,
                this.getWorldspaceColorAccessor());
        glContext.addProgram(worldspaceTextureProgramName,
                this.getWorldspaceTextureAccessor());
        glContext.addProgram(screenspaceColorProgramName,
                this.getScreenspaceColorAccessor());
        glContext.addProgram(screenspaceTextureProgramName,
                this.getScreenspaceTextureAccessor());
        // the null value for screenspace is special. It creates a program with
        // a LayerFilterIdentity but does not add the filter name at the end of
        // the program. This is just because this shader is used directly by
        // some rendering process. It avoids to do setCurrentProgram(
        // screenspaceAntialiasedTextureProgramName +
        // LayerFilterIdentity.getClass().getSimpleName() )
        glContext.addProgram(screenspaceAntialiasedTextureProgramName,
                this.createScreenspaceAntialiasedAccessor(null));
        glContext.addProgram(backgroundProgramName,
                this.getBackgroundAccessor());
        glContext.addProgram(textLayerProgramName, this.getTextLayerAccessor());
        // bezier & line painting programs are not created here because we don't
        // know the SLD content at this point.
        // idem for screenspace program (parameterized by a LayerFilter)
        return glContext;
    }

    /**
     * @return
     * @throws GLException
     */
    public GLProgramAccessor getWorldspaceColorAccessor() {
        if (this.worldspaceColorAccessor == null) {
            this.worldspaceColorAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this
                                .createWorldspaceColorProgram();
                    } catch (IOException e) {
                        throw new GLException(e);
                    }
                }
            };
        }
        return this.worldspaceColorAccessor;
    }

    /**
     * @return
     * @throws GLException
     */
    public GLProgramAccessor getWorldspaceTextureAccessor() {
        if (this.worldspaceTextureAccessor == null) {
            this.worldspaceTextureAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this
                                .createWorldspaceTextureProgram();
                    } catch (IOException e) {
                        throw new GLException(e);

                    }
                }
            };

        }
        return this.worldspaceTextureAccessor;
    }

    /**
     * @param screenspaceVertexShaderId
     * @return
     */
    public GLProgramAccessor getScreenspaceColorAccessor() {
        if (this.screenspaceColorAccessor == null) {
            this.screenspaceColorAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this
                                .createScreenspaceColorProgram();
                    } catch (IOException e) {
                        throw new GLException(e);

                    }
                }
            };
        }
        return this.screenspaceColorAccessor;
    }

    /**
     * @param screenspaceVertexShaderId
     * @return
     */
    public GLProgramAccessor getScreenspaceTextureAccessor() {
        if (this.screenspaceTextureAccessor == null) {
            this.screenspaceTextureAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this
                                .createScreenspaceTextureProgram();
                    } catch (IOException e) {
                        throw new GLException(e);

                    }
                }
            };
        }
        return this.screenspaceTextureAccessor;
    }

    /**
     * @return
     */
    public GLProgramAccessor getBackgroundAccessor() {
        if (this.backgroundAccessor == null) {
            this.backgroundAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this.createBackgroundProgram();
                    } catch (IOException e) {
                        throw new GLException(e);

                    }
                }
            };
        }
        return this.backgroundAccessor;
    }

    /**
     * @return
     */
    public GLProgramAccessor getTextLayerAccessor() {
        if (this.textLayerAccessor == null) {
            this.textLayerAccessor = new GLProgramAccessor() {

                @Override
                public GLProgram getGLProgram() throws GLException {
                    try {
                        return LayerViewGLPanel.this.createTextLayerProgram();
                    } catch (IOException e) {
                        throw new GLException(e);

                    }
                }
            };
        }
        return this.textLayerAccessor;
    }

    /**
     * @param screenspaceVertexShaderId
     * @return
     */
    public GLProgramAccessor createScreenspaceAntialiasedAccessor(
            LayerFilter filter) {
        return new GLProgramAccessorScreenspace(filter);
        // if (this.screenspaceAntialiasedAccessor == null) {
        // this.screenspaceAntialiasedAccessor = new GLProgramAccessor() {
        //
        // @Override
        // public GLProgram getGLProgram() throws GLException {
        // try {
        // return LayerViewGLPanel.this
        // .createScreenspaceAntialiasedProgram();
        // } catch (IOException e) {
        // throw new GLException(e);
        //
        // }
        // }
        // };
        // }
        // return this.screenspaceAntialiasedAccessor;
    }

    /**
     * @return
     */
    public GLProgramAccessor createBezierAccessor(
            final ShaderDescriptor shaderDescriptor) {
        return new GLProgramAccessorBezier(shaderDescriptor);
    }

    /**
     * @return
     */
    public GLProgramAccessor createLinePaintingAccessor(
            final ShaderDescriptor shaderDescriptor) {
        return new GLProgramAccessorLinePainting(shaderDescriptor);
    }

    /**
     * @return
     */
    public GLProgramAccessor createGradientSubshaderAccessor(
            final ShaderDescriptor shaderDescriptor) {
        return new GLProgramAccessorGradientSubshader(shaderDescriptor);
    }

    /**
     * @author JeT This accessor returns a program created using the given
     *         shader descriptor
     */
    private class GLProgramAccessorBezier implements GLProgramAccessor {

        private ShaderDescriptor descriptor = null;

        /**
         * @param program
         */
        public GLProgramAccessorBezier(ShaderDescriptor descriptor) {
            super();
            this.descriptor = descriptor;
        }

        @Override
        public GLProgram getGLProgram() throws GLException {
            return LayerViewGLPanel.this.createBezierProgram(this.descriptor);
        }
    }

    /**
     * @author JeT This accessor returns a program created using the given
     *         shader descriptor
     */
    private class GLProgramAccessorLinePainting implements GLProgramAccessor {

        private ShaderDescriptor descriptor = null;

        /**
         * @param program
         */
        public GLProgramAccessorLinePainting(ShaderDescriptor descriptor) {
            super();
            this.descriptor = descriptor;
        }

        @Override
        public GLProgram getGLProgram() throws GLException {
            try {
                return LayerViewGLPanel.this
                        .createLinePaintingProgram(this.descriptor);
            } catch (IOException e) {
                throw new GLException(e);
            }
        }
    }

    /**
     * @author JeT This accessor returns a program created using the given
     *         shader descriptor
     */
    private class GLProgramAccessorGradientSubshader implements
            GLProgramAccessor {

        private ShaderDescriptor descriptor = null;

        /**
         * @param program
         */
        public GLProgramAccessorGradientSubshader(ShaderDescriptor descriptor) {
            super();
            this.descriptor = descriptor;
        }

        @Override
        public GLProgram getGLProgram() throws GLException {
            try {
                return LayerViewGLPanel.this
                        .createGradientSubshaderProgram(this.descriptor);
            } catch (IOException e) {
                throw new GLException(e);
            }
        }
    }

    /**
     * @author JeT This accessor returns a program created using the given
     *         shader descriptor
     */
    private class GLProgramAccessorScreenspace implements GLProgramAccessor {

        private LayerFilter filter = null;

        /**
         * @param program
         */
        public GLProgramAccessorScreenspace(LayerFilter filter) {
            super();
            this.filter = filter;
        }

        @Override
        public GLProgram getGLProgram() throws GLException {
            try {
                return LayerViewGLPanel.this
                        .createScreenspaceAntialiasedProgram(this.filter);
            } catch (IOException e) {
                throw new GLException(e);
            }
        }
    }

    /**
     * gradient program use a subshader
     */
    private GLProgram createGradientSubshaderProgram(
            ShaderDescriptor shaderDescriptor) throws GLException, IOException {
        // basic program
        Subshader shader = ShaderFactory.createShader(shaderDescriptor);

        GLProgram program = new GLProgram(gradientProgramName);
        program.addVertexShader(
                GLTools.readFileAsString(gradientVertexShaderFilename),
                gradientVertexShaderFilename);
        program.addFragmentShader(
                GLTools.readFileAsString(gradientFragmentShaderFilename),
                gradientFragmentShaderFilename);
        shader.configureProgram(program);

        program.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        program.addInputLocation(GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        program.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        program.addUniform(m00ModelToViewMatrixUniformVarName);
        program.addUniform(m02ModelToViewMatrixUniformVarName);
        program.addUniform(m11ModelToViewMatrixUniformVarName);
        program.addUniform(m12ModelToViewMatrixUniformVarName);

        program.addUniform(screenWidthUniformVarName);
        program.addUniform(screenHeightUniformVarName);
        program.addUniform(fboWidthUniformVarName);
        program.addUniform(fboHeightUniformVarName);
        program.addUniform(globalOpacityUniformVarName);
        program.addUniform(objectOpacityUniformVarName);
        program.addUniform(gradientTextureUniformVarName);
        shader.declareUniforms(program);

        return program;
    }

    /**
     * bezier painting program
     */
    private GLProgram createBezierProgram(ShaderDescriptor shaderDescriptor)
            throws GLException {
        // basic program
        Subshader shader = ShaderFactory.createShader(shaderDescriptor);

        GLProgram program = new GLProgram(bezierLineProgramName);
        try {
            program.addVertexShader(
                    GLTools.readFileAsString(bezierVertexShaderFilename),
                    bezierVertexShaderFilename);
            program.addFragmentShader(
                    GLTools.readFileAsString(bezierFragmentShaderFilename),
                    bezierFragmentShaderFilename);
        } catch (IOException e) {
            throw new GLException(e);
        }
        shader.configureProgram(program);

        program.addInputLocation(
                GLBezierShadingVertex.vertexPositionVariableName,
                GLBezierShadingVertex.vertexPositionLocation);
        program.addInputLocation(GLBezierShadingVertex.vertexUsVariableName,
                GLBezierShadingVertex.vertexUsLocation);
        program.addInputLocation(GLBezierShadingVertex.vertexColorVariableName,
                GLBezierShadingVertex.vertexColorLocation);
        program.addInputLocation(
                GLBezierShadingVertex.vertexLineWidthVariableName,
                GLBezierShadingVertex.vertexLineWidthLocation);
        program.addInputLocation(GLBezierShadingVertex.vertexMaxUVariableName,
                GLBezierShadingVertex.vertexMaxULocation);
        program.addInputLocation(GLBezierShadingVertex.vertexP0VariableName,
                GLBezierShadingVertex.vertexP0Location);
        program.addInputLocation(GLBezierShadingVertex.vertexP1VariableName,
                GLBezierShadingVertex.vertexP1Location);
        program.addInputLocation(GLBezierShadingVertex.vertexP2VariableName,
                GLBezierShadingVertex.vertexP2Location);
        program.addInputLocation(GLBezierShadingVertex.vertexN0VariableName,
                GLBezierShadingVertex.vertexN0Location);
        program.addInputLocation(GLBezierShadingVertex.vertexN2VariableName,
                GLBezierShadingVertex.vertexN2Location);
        program.addInputLocation(
                GLBezierShadingVertex.vertexPaperUVVariableName,
                GLBezierShadingVertex.vertexPaperUVLocation);
        program.addUniform(m00ModelToViewMatrixUniformVarName);
        program.addUniform(m02ModelToViewMatrixUniformVarName);
        program.addUniform(m11ModelToViewMatrixUniformVarName);
        program.addUniform(m12ModelToViewMatrixUniformVarName);

        program.addUniform(screenWidthUniformVarName);
        program.addUniform(screenHeightUniformVarName);
        program.addUniform(fboWidthUniformVarName);
        program.addUniform(fboHeightUniformVarName);
        program.addUniform(paperTextureUniformVarName);
        program.addUniform(brushTextureUniformVarName);
        program.addUniform(brushWidthUniformVarName);
        program.addUniform(brushHeightUniformVarName);
        program.addUniform(brushStartWidthUniformVarName);
        program.addUniform(brushEndWidthUniformVarName);
        program.addUniform(brushScaleUniformVarName);
        program.addUniform(paperScaleUniformVarName);
        program.addUniform(paperDensityUniformVarName);
        program.addUniform(brushDensityUniformVarName);
        program.addUniform(strokePressureUniformVarName);
        program.addUniform(sharpnessUniformVarName);
        program.addUniform(globalOpacityUniformVarName);
        program.addUniform(objectOpacityUniformVarName);
        program.addUniform(textureScaleFactorUniformVarName);

        shader.declareUniforms(program);

        return program;
    }

    /**
     * line painting program
     */
    private GLProgram createLinePaintingProgram(
            ShaderDescriptor shaderDescriptor) throws GLException, IOException {
        Subshader shader = ShaderFactory.createShader(shaderDescriptor);
        // basic program
        GLProgram program = new GLProgram(linePaintingProgramName);
        program.addVertexShader(
                GLTools.readFileAsString(linePaintingVertexShaderFilename),
                linePaintingVertexShaderFilename);
        program.addFragmentShader(
                GLTools.readFileAsString(linePaintingFragmentShaderFilename),
                linePaintingFragmentShaderFilename);
        shader.configureProgram(program);
        program.addInputLocation(GLPaintingVertex.vertexPositionVariableName,
                GLPaintingVertex.vertexPositionLocation);
        program.addInputLocation(GLPaintingVertex.vertexUVVariableName,
                GLPaintingVertex.vertexUVLocation);
        program.addInputLocation(GLPaintingVertex.vertexNormalVariableName,
                GLPaintingVertex.vertexNormalLocation);
        program.addInputLocation(GLPaintingVertex.vertexCurvatureVariableName,
                GLPaintingVertex.vertexCurvatureLocation);
        program.addInputLocation(GLPaintingVertex.vertexThicknessVariableName,
                GLPaintingVertex.vertexThicknessLocation);
        program.addInputLocation(GLPaintingVertex.vertexColorVariableName,
                GLPaintingVertex.vertexColorLocation);
        program.addInputLocation(GLPaintingVertex.vertexMaxUVariableName,
                GLPaintingVertex.vertexMaxULocation);
        program.addInputLocation(GLPaintingVertex.vertexPaperUVVariableName,
                GLPaintingVertex.vertexPaperUVLocation);
        program.addUniform(m00ModelToViewMatrixUniformVarName);
        program.addUniform(m02ModelToViewMatrixUniformVarName);
        program.addUniform(m00ModelToViewMatrixUniformVarName);
        program.addUniform(m11ModelToViewMatrixUniformVarName);
        program.addUniform(m12ModelToViewMatrixUniformVarName);

        program.addUniform(screenWidthUniformVarName);
        program.addUniform(screenHeightUniformVarName);
        program.addUniform(fboWidthUniformVarName);
        program.addUniform(fboHeightUniformVarName);
        program.addUniform(paperTextureUniformVarName);
        program.addUniform(brushTextureUniformVarName);
        program.addUniform(brushWidthUniformVarName);
        program.addUniform(brushHeightUniformVarName);
        program.addUniform(brushStartWidthUniformVarName);
        program.addUniform(brushEndWidthUniformVarName);
        program.addUniform(brushScaleUniformVarName);
        program.addUniform(paperScaleUniformVarName);
        program.addUniform(paperDensityUniformVarName);
        program.addUniform(brushDensityUniformVarName);
        program.addUniform(strokePressureUniformVarName);
        program.addUniform(sharpnessUniformVarName);
        program.addUniform(globalOpacityUniformVarName);
        program.addUniform(objectOpacityUniformVarName);
        program.addUniform(textureScaleFactorUniformVarName);
        shader.declareUniforms(program);

        return program;
    }

    /**
     * @param worldspaceVertexShaderId
     * @throws GLException
     */
    public GLProgram createScreenspaceAntialiasedProgram(LayerFilter filter)
            throws GLException, IOException {

        // color program
        String filterCode = "";
        Subshader shader = null;
        // special cases with null, the program name is not changed but a
        // LayerFilterIdentity is used
        if (filter != null) {
            filterCode = filter.getClass().getSimpleName();
            shader = ShaderFactory.createFilterShader(filter);
        } else {
            shader = ShaderFactory
                    .createFilterShader(new LayerFilterIdentity());
        }
        GLProgram program = new GLProgram(
                screenspaceAntialiasedTextureProgramName + filterCode);
        program.addVertexShader(
                GLTools.readFileAsString(screenspaceVertexShaderFilename),
                screenspaceVertexShaderFilename);
        program.addFragmentShader(
                GLTools.readFileAsString(antialiasedFragmentShaderFilename),
                antialiasedFragmentShaderFilename);
        shader.configureProgram(program);
        program.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        program.addInputLocation(GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        program.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        program.addUniform(globalOpacityUniformVarName);
        program.addUniform(objectOpacityUniformVarName);
        program.addUniform(colorTexture1UniformVarName);
        program.addUniform(textureScaleFactorUniformVarName);
        program.addUniform(antialiasingSizeUniformVarName);
        shader.declareUniforms(program);
        return program;
    }

    /**
     * @param worldspaceVertexShaderId
     * @throws GLException
     */
    public GLProgram createTextLayerProgram() throws GLException, IOException {

        GLProgram program = new GLProgram(textLayerProgramName);
        program.addVertexShader(
                GLTools.readFileAsString(textScreenspaceVertexShaderFilename),
                textScreenspaceVertexShaderFilename);
        program.addFragmentShader(
                GLTools.readFileAsString(textLayerFragmentShaderFilename),
                textLayerFragmentShaderFilename);
        program.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        program.addInputLocation(GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        program.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        program.addUniform(globalOpacityUniformVarName);
        program.addUniform(objectOpacityUniformVarName);
        program.addUniform(colorTexture2UniformVarName);
        return program;
    }

    /**
     * @throws GLException
     */
    private GLProgram createScreenspaceColorProgram() throws GLException,
            IOException {
        // basic program
        GLProgram screenspaceColorProgram = new GLProgram(
                screenspaceColorProgramName);
        screenspaceColorProgram.addVertexShader(
                GLTools.readFileAsString(screenspaceVertexShaderFilename),
                screenspaceVertexShaderFilename);
        screenspaceColorProgram.addFragmentShader(
                GLTools.readFileAsString(textureFragmentShaderFilename),
                textureFragmentShaderFilename);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceColorProgram.addUniform(globalOpacityUniformVarName);
        screenspaceColorProgram.addUniform(objectOpacityUniformVarName);

        return screenspaceColorProgram;
    }

    /**
     * @throws GLException
     */
    private GLProgram createScreenspaceTextureProgram() throws GLException,
            IOException {
        // basic program
        GLProgram screenspaceTextureProgram = new GLProgram(
                screenspaceTextureProgramName);
        screenspaceTextureProgram.addVertexShader(
                GLTools.readFileAsString(screenspaceVertexShaderFilename),
                screenspaceVertexShaderFilename);
        screenspaceTextureProgram.addFragmentShader(
                GLTools.readFileAsString(textureFragmentShaderFilename),
                textureFragmentShaderFilename);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceTextureProgram.addUniform(globalOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(objectOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(colorTexture1UniformVarName);
        screenspaceTextureProgram.addUniform(textureScaleFactorUniformVarName);
        return screenspaceTextureProgram;
    }

    /**
     * @throws GLException
     */
    private GLProgram createBackgroundProgram() throws GLException, IOException {

        // basic program
        GLProgram backgroundTextureProgram = new GLProgram(
                backgroundProgramName);
        backgroundTextureProgram.addVertexShader(
                GLTools.readFileAsString(backgroundVertexShaderFilename),
                backgroundVertexShaderFilename);
        backgroundTextureProgram.addFragmentShader(
                GLTools.readFileAsString(backgroundFragmentShaderFilename),
                backgroundFragmentShaderFilename);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        backgroundTextureProgram.addUniform(colorTexture1UniformVarName);

        return backgroundTextureProgram;
    }

    /**
     * @param worldspaceVertexShaderId
     * @throws GLException
     */
    private GLProgram createWorldspaceColorProgram() throws GLException,
            IOException {

        // color program
        GLProgram colorProgram = new GLProgram(worldspaceColorProgramName);
        colorProgram.addVertexShader(
                GLTools.readFileAsString(worldspaceVertexShaderFilename),
                worldspaceVertexShaderFilename);
        colorProgram.addFragmentShader(
                GLTools.readFileAsString(colorFragmentShaderFilename),
                colorFragmentShaderFilename);
        colorProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        colorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        colorProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(screenWidthUniformVarName);
        colorProgram.addUniform(screenHeightUniformVarName);
        colorProgram.addUniform(fboWidthUniformVarName);
        colorProgram.addUniform(fboHeightUniformVarName);
        colorProgram.addUniform(globalOpacityUniformVarName);
        colorProgram.addUniform(objectOpacityUniformVarName);
        colorProgram.addUniform(colorTexture1UniformVarName);

        return colorProgram;
    }

    /**
     * @param worldspaceVertexShaderId
     * @throws GLException
     */
    private GLProgram createWorldspaceTextureProgram() throws GLException,
            IOException {

        // color program
        GLProgram textureProgram = new GLProgram(worldspaceTextureProgramName);
        textureProgram.addVertexShader(
                GLTools.readFileAsString(worldspaceVertexShaderFilename),
                worldspaceVertexShaderFilename);
        textureProgram.addFragmentShader(
                GLTools.readFileAsString(textureFragmentShaderFilename),
                textureFragmentShaderFilename);
        textureProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        textureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        textureProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(screenWidthUniformVarName);
        textureProgram.addUniform(screenHeightUniformVarName);
        textureProgram.addUniform(fboWidthUniformVarName);
        textureProgram.addUniform(fboHeightUniformVarName);
        textureProgram.addUniform(globalOpacityUniformVarName);
        textureProgram.addUniform(objectOpacityUniformVarName);
        textureProgram.addUniform(colorTexture1UniformVarName);
        textureProgram.addUniform(textureScaleFactorUniformVarName);

        return textureProgram;
    }

}
