package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.SyncRenderingManager;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodBuilder;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.ImageComparator;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
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

public class LayerViewGLPanel extends LayerViewPanel implements ItemListener, ActionListener {

    private static final long serialVersionUID = -7181604491025859187L; // serializable
    public static final URL SOMETHING_WENT_WRONG_TEXTURE = LayerViewPanel.class.getClassLoader().getResource("images/texture_error.png");

    private static Logger logger = Logger.getLogger(LayerViewGLPanel.class.getName());

    private SyncRenderingManager renderingManager = null;
    private LayerViewGLCanvas glCanvas = null; // canvas containing the GL
    // context
    private static GLSimpleComplex screenQuad; // quad drawn on the full screen
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
    private boolean useContinuousRendering = false;

    // private final JLayeredPane layeredPane = null;
    // private final JPanel glPanel = null;
    // private final Component glassPanel = null;

    /**
     * 
     */
    public LayerViewGLPanel() {
        super();
        this.addPaintListener(new ScalePaintListener());
        this.addPaintListener(new CompassPaintListener());
        this.addPaintListener(new LegendPaintListener());
        // this.setBackground(new Color(255, 255, 220));
        this.renderingManager = new SyncRenderingManager(this, RenderingType.LWJGL);
        this.glCanvas = (LayerViewGL4Canvas) LayerViewPanelFactory.newLayerViewGLCanvas(this);
        this.setLayout(new BorderLayout());
        // Attach LWJGL to the created canvas
        this.setGLComponent(this.glCanvas);
        this.loadDefaultRenderingMethods();

        // Default antialiazing
        try {
            this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize, 1);
        } catch (GLException e) {
            e.printStackTrace();
        }
        // The gl canvas listen for resizing events
        this.addComponentListener(this.glCanvas);

    }

    private void loadDefaultRenderingMethods() {
        ResourcesManager m = ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName);
        if (m == null) {
            m = ResourcesManager.Root().addSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName);
            String methods_path = RenderingMethodBuilder.DEFAULT_METHODS_LOCATION_DIR.getFile();
            File[] methods = new File(methods_path).listFiles();
            for (File method : methods) {
                RenderingMethodDescriptor mdesc = RenderingMethodBuilder.build(method.getAbsoluteFile().toURI());
                if (mdesc != null) {
                    ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).registerResource(mdesc.getName(), mdesc, true);
                }
            }
        }
    }

    public StyledLayerDescriptor getSld() {
        return this.getProjectFrame().getSld();
    }

    @Override
    public void displayGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getReloadShadersButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getAnimationButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getStatisticsButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();

        this.getProjectFrame().getMainFrame().getMenuBar().add(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();

    }

    @Override
    public void hideGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getReloadShadersButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getAnimationButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getStatisticsButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();
        this.getProjectFrame().getMainFrame().getMenuBar().remove(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();
    }

    /**
     * @return the useContinuousRendering
     */
    public boolean useContinuousRendering() {
        return this.useContinuousRendering;
    }

    /**
     * @return the useContinuousRendering
     */
    public boolean loadSecondSLD() {
        return this.useContinuousRendering;
    }

    /**
     * @param useContinuousRendering
     *            the useContinuousRendering to set
     */
    public void setContinuousRendering(boolean useContinuousRendering) {
        this.useContinuousRendering = useContinuousRendering;
    }

    public boolean setAntialiasing(int b) {
        try {
            if (this.glCanvas instanceof LayerViewGL4Canvas) {
                LayerViewGL4Canvas gl4canvas = (LayerViewGL4Canvas) this.glCanvas;

                int prev_aa = (int) this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize);
                this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize, b);

                int fboSizeMax = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
                if (gl4canvas.getFBOImageHeight() > fboSizeMax || gl4canvas.getFBOImageWidth() > fboSizeMax) {
                    this.getGlContext().setSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize, prev_aa);

                    return false;
                } else {
                    gl4canvas.updateFBODimensions();
                }
            }
        } catch (GLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
    public LayerViewGL4Canvas getGLCanvas() {
        return (LayerViewGL4Canvas) this.glCanvas;
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
            this.wireframeToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/wireframe.png")));
            this.wireframeToggleButton.setToolTipText(I18N.getString("RenderingGL.ToggleWireframe"));
            this.wireframeToggleButton.setSelected(this.useWireframe());
            this.wireframeToggleButton.addItemListener(this);
        }
        return this.wireframeToggleButton;
    }

    private JToggleButton getFBOButton() {
        if (this.fboToggleButton == null) {
            this.fboToggleButton = new JToggleButton();
            this.fboToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/fbo.png")));
            this.fboToggleButton.setToolTipText(I18N.getString("RenderingGL.ToggleFBO"));
            this.fboToggleButton.setSelected(this.glCanvas.isFBOActivated());
            this.fboToggleButton.addItemListener(this);
        }
        return this.fboToggleButton;
    }

    private JToggleButton getAnimationButton() {
        if (this.animationButton == null) {
            this.animationButton = new JToggleButton();
            this.animationButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/animate.png")));
            this.animationButton.setToolTipText(I18N.getString("RenderingGL.ToggleAnimation"));
            this.animationButton.setSelected(this.useContinuousRendering());
            this.animationButton.addItemListener(this);
        }
        return this.animationButton;
    }

    private JToggleButton getStatisticsButton() {
        if (this.statisticsButton == null) {
            this.statisticsButton = new JToggleButton();
            this.statisticsButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/statistics.png")));
            this.statisticsButton.setToolTipText(I18N.getString("RenderingGL.ToggleStatistics"));
            this.statisticsButton.setSelected(this.useContinuousRendering());
            this.statisticsButton.addItemListener(this);
        }
        return this.statisticsButton;
    }

    private JButton getAntialiasingButton() {
        if (this.antialiasingButton == null) {
            this.antialiasingButton = new JButton();
            this.antialiasingButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/antialiasing.png")));
            this.antialiasingButton.setToolTipText(I18N.getString("RenderingGL.LoopAntialiasing"));

            int aa = 1;
            try {
                if (this.getGlContext() != null) {
                    aa = (int) this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize);
                }
            } catch (GLException e) {
                e.printStackTrace();
            }
            this.antialiasingButton.setText(String.valueOf(aa));
            this.antialiasingButton.addActionListener(this);
        }
        return this.antialiasingButton;
    }

    private JButton getAWTComparButton() {
        if (this.awtComparButton == null) {
            this.awtComparButton = new JButton();
            this.awtComparButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/compare.gif")));
            this.awtComparButton.setToolTipText(I18N.getString("RenderingGL.ImageComparison"));
            this.awtComparButton.addActionListener(this);
        }
        return this.awtComparButton;
    }

    private JButton getReloadShadersButton() {
        if (this.reloadShadersButton == null) {
            this.reloadShadersButton = new JButton();
            this.reloadShadersButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/refresh.png")));
            this.reloadShadersButton.setToolTipText(I18N.getString("RenderingGL.ReloadShaders"));
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
            this.clearCacheButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/clear.png")));
            this.clearCacheButton.setToolTipText(I18N.getString("RenderingGL.ClearCache"));
            this.clearCacheButton.addActionListener(this);
        }
        return this.clearCacheButton;
    }

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
        this.removeAll();
        this.add(glComponent, BorderLayout.CENTER);
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
    public int print(final Graphics arg0, final PageFormat arg1, final int arg2) throws PrinterException {
        logger.error("LayerViewGlPanel::print(...) not implemented yet");
        return 0;
    }

    @Override
    public IEnvelope getEnvelope() {
        if (this.getRenderingManager().getLayers().isEmpty()) {
            return null;
        }
        List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager().getLayers());
        Iterator<Layer> layerIterator = copy.iterator();
        IEnvelope envelope = layerIterator.next().getFeatureCollection().envelope();
        while (layerIterator.hasNext()) {
            IFeatureCollection<? extends IFeature> collection = layerIterator.next().getFeatureCollection();
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
        this.saveAsImage(fileName, this.getWidth(), this.getHeight(), false);
    }

    @Override
    public void saveAsImage(String fileName, int width, int height, boolean doSaveWorldFile) {
        int tmpw = this.getWidth();
        int tmph = this.getHeight();
        // We save the old extent to force the Viewport to keep the old
        // window in world coordinates.
        IEnvelope env = this.getViewport().getEnvelopeInModelCoordinates();
        // Artificially resize the canvas to the image dimensions.
        this.setSize(width, height);
        this.glCanvas.setSize(width, height);
        try {
            // We zoom to the old extent in the resized canvas.
            this.getViewport().zoom(env);
        } catch (NoninvertibleTransformException e2) {
            logger.error("In Image Export : failed to zoom in the correct extent.");
            e2.printStackTrace();
        }
        // Render and save the result in an image.
        this.glCanvas.renderToImage();
        this.glCanvas.doPaint();
        try {
            ImageIO.write(this.glCanvas.offscreenRenderedImg, "png", new File(fileName));

            // Save the World File if needed
            if (doSaveWorldFile) {
                String wld = FilenameUtils.removeExtension(fileName) + ".wld";
                AffineTransform t = this.getViewport().getModelToViewTransform();
                fr.ign.cogit.geoxygene.util.conversion.WorldFileWriter.write(new File(wld), t.getScaleX(), t.getScaleY(), this.getViewport().getViewOrigin().getX(), this.getViewport().getViewOrigin()
                        .getY(), this.getHeight());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoninvertibleTransformException e) {
            logger.error("Failed to save the world file associated with the image file " + fileName);
            e.printStackTrace();
        } finally {
            // Finally, rollback the canvas to its original size.
            this.glCanvas.setSize(tmpw, tmph);
            this.setSize(tmpw, tmph);
            this.getProjectFrame().validate();
            try {
                // Zoom back to the "normal" extent
                this.getViewport().zoom(env);
            } catch (NoninvertibleTransformException e2) {
                logger.error("In Image Export : failed to zoom back to the original LayerViewPanel extent.");
                e2.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == this.getWireframeButton()) {
            this.setWireframe(this.getWireframeButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getFBOButton()) {
            this.glCanvas.setFBO(this.getFBOButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getAnimationButton()) {
            this.setContinuousRendering(this.getAnimationButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getStatisticsButton()) {
            RenderingStatistics.setStatistics(this.getStatisticsButton().isSelected());
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getGLInformationMenu()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), new JTextArea(this.getGLInformation(), 80, 40), "GL Information", JOptionPane.INFORMATION_MESSAGE);
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
            this.reset();
            this.repaint();
        } else if (e.getSource() == this.getReloadShadersButton()) {
            this.setContinuousRendering(this.getAnimationButton().isSelected());
            this.repaint();
        } else if (e.getSource() == this.getAntialiasingButton()) {
            int antialiasingValue = Integer.parseInt(this.getAntialiasingButton().getText());
            antialiasingValue++;
            int aa = 10;
            try {
                aa = (int) this.getGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_AntialiasingSize);
            } catch (GLException e1) {
                e1.printStackTrace();
            }
            if (aa >= 9) {
                antialiasingValue = 0;
            }
            this.getAntialiasingButton().setText(String.valueOf(antialiasingValue));
            if (!this.setAntialiasing(antialiasingValue)) {
                logger.warn("Reaching maximum antialiasing level supported(" + (antialiasingValue - 1) + ")");
                this.getAntialiasingButton().setText(String.valueOf(0));
                this.setAntialiasing(0);
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
            logger.error("Layer " + layer.getName() + " has no associated renderer. Cannot reset it...");
            return;
        }
        renderer.reset();
    }

    private String getGLInformation() {
        this.activateGLContext();
        StringBuilder str = new StringBuilder();
        str.append("GLInformations\n");
        Class<?> contextClass = org.lwjgl.opengl.GLContext.getCapabilities().getClass();
        Field[] declaredFields = contextClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    str.append("\t" + field.getName() + " : " + field.getBoolean(field) + "\n");
                } catch (Exception e) {
                    str.append("\t" + field.getName() + " : error: " + e.getMessage() + "\n");
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
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(LayerViewGLPanel.screenQuad.addVertex(new GLSimpleVertex(new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        LayerViewGLPanel.screenQuad.setColor(Color.blue);
        LayerViewGLPanel.screenQuad.setOverallOpacity(0.5);
    }

}
