package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.SyncRenderingManager;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.ImageComparator;

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
    private LayerViewGLCanvasType glType = null;
    private JToggleButton wireframeToggleButton = null;
    private JToggleButton fboToggleButton = null;
    private JButton antialiasingButton = null;
    private JButton clearCacheButton = null;
    private JButton awtComparButton = null;
    private JToolBar.Separator toolbarSeparator = null;
    private boolean wireframe = false;
    private int antialiasing = 2;
    private boolean useFBO = true;

    public enum LayerViewGLCanvasType {
        GL1, GL4
    }

    // private final JLayeredPane layeredPane = null;
    // private final JPanel glPanel = null;
    // private final Component glassPanel = null;

    /**
     * 
     * @param frame
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
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();
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
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();
    }

    public boolean useFBO() {
        return this.useFBO;
    }

    public void setFBO(boolean useFBO) {
        this.useFBO = useFBO;
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

    private JButton getAntialiasingButton() {
        if (this.antialiasingButton == null) {
            this.antialiasingButton = new JButton();
            this.antialiasingButton
                    .setIcon(new ImageIcon(
                            MainFrameToolBar.class
                                    .getResource("/images/icons/16x16/antialiasing.png")));
            this.antialiasingButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleAntialiasing"));

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

    @Override
    public final void repaint() {
        if (this.glCanvas != null) {
            this.glCanvas.repaint();
        }

    }

    @Override
    public final void paintComponent(final Graphics g) {
        try {

            this.glCanvas.doPaint();
        } catch (Exception e1) {
            // e1.printStackTrace();
            logger.error(I18N.getString("LayerViewPanel.PaintError") + " " + e1.getMessage()); //$NON-NLS-1$
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
        if (this.glCanvas != null) {
            try {
                if (this.glCanvas.getContext() != null) {
                    this.glCanvas.releaseContext();
                }
                this.glCanvas = null;
            } catch (Exception e) {
                logger.error("An error occurred releasing GL context "
                        + e.getMessage());
            }
        }

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getClearCacheButton()) {
            for (LayerRenderer renderer : this.getRenderingManager()
                    .getRenderers()) {
                renderer.reset();
            }
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
        } else if (e.getSource() == this.getAntialiasingButton()) {
            int antialiasingValue = 1;
            try {
                antialiasingValue = Integer.parseInt(this
                        .getAntialiasingButton().getText());
                antialiasingValue++;
                if (this.antialiasing >= 4) {
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

}
