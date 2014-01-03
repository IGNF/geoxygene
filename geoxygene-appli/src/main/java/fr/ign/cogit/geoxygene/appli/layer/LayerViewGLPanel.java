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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

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
                                                                        // UID
    // private static final int GLASS_LAYER_INDEX = 10; // layer index on which
    // the overlay Swing stuff will be drawn
    // private static final int GL_LAYER_INDEX = 1; // layer index on which the GL
    // stuff will be rendered
    private static Logger logger = Logger.getLogger(LayerViewGLPanel.class.getName());
    private SyncRenderingManager renderingManager = null;
    private LayerViewGLCanvas glCanvas = null; // canvas containing the GL
    private LayerViewGLCanvasType glType = null;
    private JToggleButton wireframeToggleButton = null;
    private JButton clearCacheButton = null;
    private JToolBar.Separator toolbarSeparator = null;
    private boolean wireframe = false;

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
        this.setBackground(new Color(255, 255, 220));
        this.renderingManager = new SyncRenderingManager(this, RenderingType.LWJGL);

        this.glCanvas = LayerViewPanelFactory.newLayerViewGLCanvas(this, glType);
        this.setGlType(glType);
        this.setLayout(new BorderLayout());
        // Attach LWJGL to the created canvas
        this.setGLComponent(this.glCanvas);

    }

    @Override
    public void displayGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().add(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();
    }

    @Override
    public void hideGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().remove(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar().revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();
    }

    @Override
    public SyncRenderingManager getRenderingManager() {
        return this.renderingManager;
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
            this.wireframeToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class.getResource("/images/icons/16x16/wireframe.png")));
            this.wireframeToggleButton.setToolTipText(I18N.getString("RenderingGL.ToggleWireframe"));
            this.wireframeToggleButton.setSelected(this.isWireframe());
            this.wireframeToggleButton.addItemListener(this);
        }
        return this.wireframeToggleButton;
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

    /**
     * @return the wireframe
     */
    public boolean isWireframe() {
        return this.wireframe;
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
                logger.error("An error occurred releasing GL context " + e.getMessage());
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
            this.wireframe = this.getWireframeButton().isSelected();
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getClearCacheButton()) {
            for (LayerRenderer renderer : this.getRenderingManager().getRenderers()) {
                renderer.reset();
            }
            this.repaint();
        } else {
            // old SLD events....
            this.repaint();
        }

    }

}
