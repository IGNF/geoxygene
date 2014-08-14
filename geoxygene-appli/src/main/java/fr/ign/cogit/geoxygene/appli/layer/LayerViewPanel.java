package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.print.Printable;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.api.SldListener;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.mode.AbstractGeometryEditMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateInteriorRingMode;
import fr.ign.cogit.geoxygene.appli.mode.CreateLineStringMode;
import fr.ign.cogit.geoxygene.appli.mode.CreatePolygonMode;
import fr.ign.cogit.geoxygene.appli.mode.Mode;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.appli.render.RenderingManager;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.BackgroundDescriptor;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Extracted interface from previous LayerViewPanel which is LayerViewAwtPanel
 * now. This interface extraction is done in order to use GL layers
 * 
 * @author JeT
 */
public abstract class LayerViewPanel extends JComponent implements Printable,
        SldListener, fr.ign.cogit.geoxygene.style.SldListener {

    /** Serializable UID. */
    private static final long serialVersionUID = -1275390035288869114L;

    /** The logger. */
    private static Logger LOGGER = Logger.getLogger(LayerViewPanel.class
            .getName());

    /**
     * Taille d'un pixel en mètres (la longueur d'un coté de pixel de l'écran)
     * utilisé pour le calcul de l'echelle courante de la vue. Elle est calculée
     * à partir de la résolution de l'écran en DPI. Par exemple si la résolution
     * est 90DPI, c'est: 90 pix/inch = 1/90 inch/pix = 0.0254/90 meter/pix.
     */
    private final static double METERS_PER_PIXEL = 0.02540005 / Toolkit
            .getDefaultToolkit().getScreenResolution();

    private boolean recording = false;
    private String recordFileName = "";
    private int recordIndex = 0;

    private Viewport viewport = null; // Viewport (coordinate systems
                                      // conversion)
    private ProjectFrame projectFrame = null; // main parent Frame

    private BackgroundDescriptor viewBackground; // bg color

    /** Private selected features. Use getter and setter. */
    private final Set<IFeature> selectedFeatures = new HashSet<IFeature>(0);

    /***********************************************************************
     * Default Constructor. The parent project frame has to be set before using
     * it
     */
    public LayerViewPanel() {
        super();
        this.projectFrame = null;
        this.viewport = new Viewport(this);
    }

    // /***********************************************************************
    // * Default Constructor. Set the parent project frame and create a new
    // * Viewport
    // */
    // public LayerViewPanel(final ProjectFrame frame) {
    // super();
    // this.projectFrame = frame;
    // this.viewport = new Viewport(this);
    // }

    /**
     * Returns the size of a pixel in meters.
     * 
     * @return Taille d'un pixel en mètres (la longueur d'un coté de pixel de
     *         l'écran).
     */
    public static double getMETERS_PER_PIXEL() {
        return LayerViewPanel.METERS_PER_PIXEL;
    }

    public boolean isRecording() {
        return this.recording;
    }

    // @Override
    public void setRecord(boolean b) {
        this.recording = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#getRecordFileName()
     */
    // @Override
    public String getRecordFileName() {
        return this.recordFileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#setRecordFileName
     * (java.lang.String)
     */
    // @Override
    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
        this.recordIndex = 0;
    }

    protected void paintGeometryEdition(Graphics g) {
        Mode mode = this.getProjectFrame().getMainFrame().getMode()
                .getCurrentMode();
        g.setColor(new Color(1f, 0f, 0f));
        if (mode instanceof AbstractGeometryEditMode) {
            IDirectPositionList points = new DirectPositionList();
            points.addAll(((AbstractGeometryEditMode) mode).getPoints());
            if (mode instanceof CreateLineStringMode) {
                if (!points.isEmpty()) {
                    points.add(((AbstractGeometryEditMode) mode)
                            .getCurrentPoint());
                    RenderUtil.draw(new GM_LineString(points),
                            this.getViewport(), (Graphics2D) g, 1.0f);// FIXME
                                                                      // OPACITY
                                                                      // FIX
                }
            } else {
                if (mode instanceof CreatePolygonMode) {
                    if (!points.isEmpty()) {
                        IDirectPosition start = points.get(0);
                        points.add(((AbstractGeometryEditMode) mode)
                                .getCurrentPoint());
                        if (points.size() > 2) {
                            points.add(start);
                            RenderUtil.draw(new GM_Polygon(new GM_LineString(
                                    points)), this.getViewport(),
                                    (Graphics2D) g, 1.0f);// FIXME OPACITY FIX
                        } else {
                            if (points.size() == 2) {
                                points.add(start);
                                RenderUtil.draw(new GM_LineString(points),
                                        this.getViewport(), (Graphics2D) g,
                                        1.0f);// FIXME OPACITY FIX
                            }
                        }
                    }
                } else {
                    if (mode instanceof CreateInteriorRingMode) {
                    } else {
                    }
                }
            }
        }
    }

    /**
     * The viewport of the panel.
     * 
     * @return the viewport of the panel
     */
    public Viewport getViewport() {

        return this.viewport;
    }

    /**
     * @param viewport
     *            viewport to set
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;

    }

    /**
     * @param projectFrame
     *            the projectFrame to set
     */
    public void setProjectFrame(ProjectFrame projectFrame) {
        this.projectFrame = projectFrame;
    }

    /** Get parent project frame */
    public ProjectFrame getProjectFrame() {
        return this.projectFrame;
    }

    /** Model */
    // private StyledLayerDescriptor sldmodel;

    /** @return The rendering manager handling the rendering of the layers */
    public abstract RenderingManager getRenderingManager();

    // /**
    // * The rendering manager handling the rendering of the layers
    // */
    // public abstract void setRenderingManager(RenderingManager manager);
    //
    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#getSelectedFeatures
     * ()
     */
    public final Set<IFeature> getSelectedFeatures() {
        return this.selectedFeatures;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.LayerViewPanelExtracted#getFeatures()
     */
    /**
     * Getter for the selected feature.
     * 
     * @return the features selected by the user
     */
    public final Set<IFeature> getFeatures() {
        Set<IFeature> features = new HashSet<IFeature>();
        for (Layer layer : this.getProjectFrame().getLayers()) {
            features.addAll(layer.getFeatureCollection());
        }
        return features;
    }

    /**
     * Get the envelope.
     * 
     * @return The envelope of all layers of the panel in model coordinates
     */
    public abstract IEnvelope getEnvelope();

    public void setViewBackground(BackgroundDescriptor background) {
        this.viewBackground = background;

    }

    public BackgroundDescriptor getViewBackground() {
        return this.viewBackground;
    }

    /**
     * Save the map into an image file. The file format is determined by the
     * given file extension. If there is none or if the given extension is
     * unsupported, the image is saved in PNG format.
     * 
     * @param fileName
     *            the image file to save into.
     */
    public abstract void saveAsImage(String fileName);

    // public abstract void layerAdded(Layer l);

    // public abstract void layerOrderChanged(int oldIndex, int newIndex);

    // public abstract void layersRemoved(Collection<Layer> layers);

    public abstract void dispose();

    @Override
    public abstract void repaint();

    /**
     * Repaint the panel using the repaint method of the super class
     * {@link JPanel}. Called in order to perform the progressive rendering.
     * 
     * @see #paintComponent(Graphics)
     */
    public abstract void superRepaint();

    /********************************* Paint listener management */
    private final Set<PaintListener> overlayListeners = new HashSet<PaintListener>(
            0);

    public void addPaintListener(PaintListener listener) {
        this.overlayListeners.add(listener);
    }

    public Set<PaintListener> getOverlayListeners() {
        return this.overlayListeners;
    }

    public void paintOverlays(final Graphics graphics) {
        for (PaintListener listener : this.getOverlayListeners()) {
            listener.paint(this, graphics);
        }
    }

    /**
     * add GUI elements relative to this layer view panel. This method should be
     * called when the layer view becomes active
     */
    public abstract void displayGui();

    /**
     * remove GUI elements relative to this layer view panel. This method should
     * be called when the layer view becomes inactive
     */
    public abstract void hideGui();

    /**
     * refresh the layer view panel (empty caches, reset renderers, etc...)
     * default baheviour is to reset all renderers. Override for another
     * behaviour
     */
    public void refresh() {
        // default behaviour is to reset renderers.
        for (LayerRenderer layerRenderer : this.getRenderingManager()
                .getRenderers()) {
            layerRenderer.reset();
        }
        this.repaint();
    }

    /** Paint listener management *********************************/

}