package fr.ign.cogit.geoxygene.appli;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory.LayerType;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserLayerFactory;
import fr.ign.cogit.geoxygene.util.conversion.GPSTextfileReader;
import fr.ign.cogit.geoxygene.util.conversion.RoadNetworkTextfileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Common implementation of all project frames
 * 
 * @author JeT
 * 
 */

public abstract class AbstractProjectFrame implements ProjectFrame {

    /** Logger of the application. */
    private static Logger logger = Logger.getLogger(AbstractProjectFrame.class.getName());
    private MainFrame mainFrame = null;
    private final Map<IFeature, BufferedImage> featureToImageMap = new HashMap<IFeature, BufferedImage>();
    private LayerLegendPanel layerLegendPanel = null; // The layer legend panel
    private StyledLayerDescriptor sld = null; // The project styled layer
    private JSplitPane splitPane = null; // The split pane
    private static final int DEFAULT_DIVIDER_LOCATION = 200;
    private static final int DEFAULT_DIVIDER_SIZE = 5;
    private LayerViewPanel layerViewPanel = null; // Layer View Panel
    private ImageIcon iconImage = null; // ProjectFrame icon
    private static int PFID = 1; // Frame id counter
    private String title = "untitled"; // tab title

    // descriptor

    /**
     * Constructor
     */
    public AbstractProjectFrame(final MainFrame frame, final LayerViewPanel layerViewPanel, final ImageIcon iconImage) {
        super();
        this.setIconImage(iconImage);
        this.setMainFrame(frame);
        this.setLayerViewPanel(layerViewPanel);
        this.title = "Project #" + AbstractProjectFrame.PFID++;
        if (layerViewPanel instanceof LayerViewGLPanel) {
            this.title += " (GL)";
        }
        this.sld = new StyledLayerDescriptor(new DataSet());

        // this.layerViewPanel.setModel(this.sld);
        // this.layerLegendPanel.setModel(this.sld);

        this.sld.addSldListener(this.getLayerViewPanel());
        this.sld.addSldListener(this.getLayerLegendPanel());
    }

    //    /**
    //     * Constructor
    //     */
    //    public AbstractProjectFrame(final MainFrame frame, final ImageIcon iconImage) {
    //        super();
    //        this.setIconImage(iconImage);
    //        this.setMainFrame(frame);
    //        this.title = "Project #" + AbstractProjectFrame.PFID++;
    //        this.sld = new StyledLayerDescriptor(new DataSet());
    //        // this.layerViewPanel.setModel(this.sld);
    //        // this.layerLegendPanel.setModel(this.sld);
    //        this.sld.addSldListener(this.getLayerViewPanel());
    //        this.sld.addSldListener(this.getLayerLegendPanel());
    //    }

    @Override
    public final void setTitle(final String string) {
        this.title = string;
    }

    @Override
    public final String getTitle() {
        return this.title;
    }

    @Override
    public final String getName() {
        return this.title;
    }

    /**
     * @param mainFrame
     *            the mainFrame to set
     */
    public final void setMainFrame(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getMainFrame()
     */
    @Override
    public MainFrame getMainFrame() {
        return this.mainFrame;
    }

    /**
     * @return the iconImage
     */
    public final ImageIcon getIconImage() {
        return this.iconImage;
    }

    /**
     * @param iconImage
     *            the iconImage to set
     */
    public final void setIconImage(final ImageIcon iconImage) {
        this.iconImage = iconImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getLayerViewPanel()
     */
    @Override
    public final LayerViewPanel getLayerViewPanel() {
        if (this.layerViewPanel == null) {
            this.layerViewPanel = LayerViewPanelFactory.newLayerViewPanel();
        }
        return this.layerViewPanel;
    }

    /**
     * @param layerViewPanel
     *            the layerViewPanel to set
     */
    public final void setLayerViewPanel(final LayerViewPanel layerViewPanel) {
        this.layerViewPanel = layerViewPanel;
        this.layerViewPanel.setProjectFrame(this);
    }

    /**
     * @param layerLegendPanel
     *            the layerLegendPanel to set
     */
    public final void setLayerLegendPanel(final LayerLegendPanel layerLegendPanel) {
        this.layerLegendPanel = layerLegendPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getLayerViewPanel()
     */
    @Override
    public final LayerLegendPanel getLayerLegendPanel() {
        if (this.layerLegendPanel == null) {
            this.layerLegendPanel = new LayerLegendPanel(this);
        }
        return this.layerLegendPanel;
    }

    /** @return The split pane */
    public JSplitPane getSplitPane() {
        if (this.splitPane == null) {
            this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            this.splitPane.setBorder(BorderFactory.createEmptyBorder());
            this.splitPane.setLeftComponent(this.getLayerLegendPanel());
            this.splitPane.setRightComponent(this.getLayerViewPanel());
            this.splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
            this.splitPane.setOneTouchExpandable(true);
            this.splitPane.setDividerSize(DEFAULT_DIVIDER_SIZE);
            this.splitPane.setResizeWeight(0.);
        }
        return this.splitPane;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addLayer(fr.ign.cogit.geoxygene
     * .style.Layer)
     */
    @Override
    public final void addLayer(final Layer l) {
        synchronized (this.getSld().lock) {
            this.getSld().add(l);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addLayer(fr.ign.cogit.geoxygene
     * .style.Layer, int)
     */
    @Override
    public final void addLayer(final Layer l, final int index) {
        synchronized (this.getSld().lock) {
            this.getSld().add(l, index);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#addLayer(java.io.File)
     */
    @Override
    public final void addLayer(final File file) {
        if (file != null) {
            String fileName = file.getAbsolutePath();
            String extention = fileName.substring(fileName.lastIndexOf('.') + 1);
            LayerFactory factory = new LayerFactory(this.getSld());
            Layer l = null;
            if (extention.equalsIgnoreCase("shp")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.SHAPEFILE);
            } else if (extention.equalsIgnoreCase("tif")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.GEOTIFF);
            } else if (extention.equalsIgnoreCase("asc")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.ASC);
            } else if (extention.equalsIgnoreCase("txt")) { //$NON-NLS-1$
                l = factory.createLayer(fileName, LayerType.TXT);
            }
            if (l != null) {
                synchronized (this.getSld().lock) {
                    this.getSld().add(l);
                }
            }
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#askAndAddNewLayer()
     */
    @Override
    public final void askAndAddNewLayer() {
        File[] files = MainFrameMenuBar.fc.getFiles(this.getMainFrame().getGui());
        this.addLayerFromFileOrDirectory(files);
    }

    private void addLayerFromFileOrDirectory(File... files) {
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.addLayerFromFileOrDirectory(file.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return MainFrameMenuBar.fc.getFileChooser().getFileFilter().accept(pathname);
                    }
                }));
            } else {
                this.addLayer(file);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addGpsTxtLayer(java.lang.String)
     */
    @Override
    public final void addGpsTxtLayer(final String fileName) {
        int lastIndexOfSeparator = fileName.lastIndexOf(File.separatorChar);
        String populationName = fileName.substring(lastIndexOfSeparator + 1, fileName.lastIndexOf(".")); //$NON-NLS-1$
        logger.info(populationName);
        Population<DefaultFeature> population = GPSTextfileReader.read(fileName, populationName, this.getDataSet(), true);
        logger.info(population.size());

        if (population != null) {
            this.addFeatureCollection(population, population.getNom());
            if (this.getLayers().size() == 1) {
                try {
                    this.getLayerViewPanel().getViewport().zoom(population.envelope());
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addRoadNetworkTxtLayer(java.lang
     * .String)
     */
    @Override
    public final void addRoadNetworkTxtLayer(final String fileName) {
        int lastIndexOfSeparator = fileName.lastIndexOf(File.separatorChar);
        String populationName = fileName.substring(lastIndexOfSeparator + 1, fileName.lastIndexOf(".")); //$NON-NLS-1$
        logger.info(populationName);
        Population<DefaultFeature> population = RoadNetworkTextfileReader.read(fileName, populationName, this.getDataSet(), true);
        logger.info(population.size());

        if (population != null) {
            this.addFeatureCollection(population, population.getNom());
            if (this.getLayers().size() == 1) {
                try {
                    this.getLayerViewPanel().getViewport().zoom(population.envelope());
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addFeatureCollection(fr.ign.cogit
     * .geoxygene.api.feature.IPopulation, java.lang.String,
     * org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    @Override
    public final Layer addFeatureCollection(final IPopulation<? extends IFeature> population, final String name, final CoordinateReferenceSystem crs) {
        LayerFactory factory = new LayerFactory(this.getSld());
        Layer layer = factory.createLayer(name, population.getFeatureType().getGeometryType());
        layer.setCRS(crs);

        synchronized (this.getSld().lock) {
            this.getSld().add(layer);
        }
        return layer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addUserLayer(fr.ign.cogit.geoxygene
     * .api.feature.IFeatureCollection, java.lang.String,
     * org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    @Override
    public final Layer addUserLayer(final IFeatureCollection<? extends IFeature> collection, final String name, final CoordinateReferenceSystem crs) {
        UserLayerFactory factory = new UserLayerFactory();
        factory.setModel(this.getSld());
        factory.setName(name);

        factory.setGeometryType(collection.getFeatureType().getGeometryType());
        factory.setCollection(collection);
        Layer layer = factory.createLayer();
        layer.setCRS(crs);
        this.getSld().add(layer);
        return layer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#addFeatureCollection(fr.ign.cogit
     * .geoxygene.api.feature.IPopulation, java.lang.String)
     */
    @Override
    @Deprecated
    public final Layer addFeatureCollection(final IPopulation<?> population, final String name) {
        return this.addFeatureCollection(population, name, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getLayers()
     */
    @Override
    public final List<Layer> getLayers() {
        return this.getSld().getLayers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#dispose()
     */
    /**
     * Adapter::dispose() do nothing. let this behavior to subclasses
     */
    @Override
    public void dispose() {
        // do nothing. Override it in derivative classes
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getLayer(java.lang.String)
     */
    @Override
    public final Layer getLayer(final String name) {
        return this.getSld().getLayer(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#clearSelection()
     */
    @Override
    public void clearSelection() {
        this.getLayerViewPanel().getSelectedFeatures().clear();
        this.getLayerViewPanel().getRenderingManager().getSelectionRenderer().clearImageCache();
        this.getLayerViewPanel().superRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#getLayerFromFeature(fr.ign.cogit
     * .geoxygene.api.feature.IFeature)
     */
    @Override
    public final Layer getLayerFromFeature(final IFeature ft) {
        for (Layer layer : this.getSld().getLayers()) {
            if (layer.getFeatureCollection().contains(ft)) {
                return layer;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#setGeometryToolsVisible(boolean)
     */
    /**
     * Adapter::setGeometryToolsVisible() do nothing. let this behavior to
     * subclasses
     */
    @Override
    public void setGeometryToolsVisible(final boolean b) {
        // do nothing. Override it in derivative classes
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#setSld(fr.ign.cogit.geoxygene
     * .style.StyledLayerDescriptor)
     */
    @Override
    public final void setSld(final StyledLayerDescriptor sld) {
        synchronized (this.getSld().lock) {
            if (this.sld != null) {
                this.sld.removeSldListener(this.getLayerViewPanel());
                this.sld.removeSldListener(this.getLayerLegendPanel());
            }
            this.sld = sld;
            if (this.sld != null) {
                this.sld.addSldListener(this.getLayerViewPanel());
                this.sld.addSldListener(this.getLayerLegendPanel());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getSld()
     */
    @Override
    public final StyledLayerDescriptor getSld() {
        return this.sld;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#addImage(java.lang.String,
     * java.awt.image.BufferedImage, double[][])
     */
    @Override
    public void addImage(final String name, final BufferedImage image, final double[][] range) {
        DefaultFeature feature = new DefaultFeature(new GM_Envelope(range[0][0], range[0][1], range[1][0], range[1][1]).getGeom());
        this.featureToImageMap.put(feature, image);
        Population<DefaultFeature> population = new Population<DefaultFeature>(name);
        population.add(feature);
        this.getDataSet().addPopulation(population);
        LayerFactory factory = new LayerFactory(this.getSld());
        Layer layer = factory.createLayer(name);
        synchronized (this.getSld().lock) {
            this.getSld().add(layer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#getImage(fr.ign.cogit.geoxygene
     * .api.feature.IFeature)
     */
    @Override
    public BufferedImage getImage(final IFeature feature) {
        logger.error(this.featureToImageMap.size() + " elements in map"); //$NON-NLS-1$
        if (feature == null) {
            if (this.featureToImageMap.isEmpty()) {
                return null;
            }
            return this.featureToImageMap.values().iterator().next();
        }
        return this.featureToImageMap.get(feature);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#saveAsImage(java.lang.String)
     */
    @Override
    public final void saveAsImage(final String fileName) {
        this.getLayerViewPanel().saveAsImage(fileName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#saveAsShp(java.lang.String,
     * fr.ign.cogit.geoxygene.style.Layer)
     */
    @Override
    public final void saveAsShp(final String fileName, final Layer layer) {
        try {
            // do we have to add ".shp" extension ? (FileUtil.changeExtension(fileName, "shp"))
            ShapefileWriter.write(layer.getFeatureCollection(), fileName, layer.getCRS());
        } catch (Exception e) {
            logger.error("Shapefile export failed! See stack trace below : "); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    /**
     * Save the current styles into an xml file.
     * 
     * @param fileName
     *            the xml file to save into.
     */

    @Override
    public void saveAsSLD(String fileName) {
        logger.info(" " + this.sld);
        String newPath = FileUtil.changeExtension(fileName, "xml");
        this.sld.marshall(newPath);
    }

    /**
     * load the described styles in an xml file and apply them to a predefined
     * dataset.
     * 
     * @param fileName
     *            the xml file to load.
     * @throws JAXBException
     * @throws FileNotFoundException
     */

    @Override
    public void loadSLD(File file) throws FileNotFoundException, JAXBException {

        if (file.isFile() && (file.getAbsolutePath().endsWith(".xml") //$NON-NLS-1$
                || file.getAbsolutePath().endsWith(".XML"))) //$NON-NLS-1$
        {

            StyledLayerDescriptor new_sld;
            synchronized (this.getSld().lock) {
                new_sld = StyledLayerDescriptor.unmarshall(file.getAbsolutePath(), this.getDataSet());
                if (new_sld != null) {
                    for (int i = 0; i < this.getLayers().size(); i++) {
                        String name = this.getLayers().get(i).getName();
                        //logger.debug(name);
                        //vérifier que le layer est décrit dans le SLD
                        if (new_sld.getLayer(name) != null) {
                            if (new_sld.getLayer(name).getStyles() != null) {
                                // logger.debug(new_sld.getLayer(name).getStyles());
                                this.getLayers().get(i).setStyles(new_sld.getLayer(name).getStyles());

                            } else {
                                logger.trace("Le layer " + name + " n'a pas de style défini dans le SLD");
                            }
                        } else {
                            logger.trace("Le layer " + name + " n'est pas décrit dans le SLD");
                            this.getLayers().get(i).setStyles(this.sld.getLayer(name).getStyles());
                        }
                    }

                    this.layerLegendPanel.repaint();
                    this.layerViewPanel.repaint();

                    /**
                     * // loading finished
                     */
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.ProjectFrame#removeLayers(java.util.List)
     */
    @Override
    public final void removeLayers(final List<Layer> toRemove) {
        synchronized (this.getSld().lock) {
            this.getSld().remove(toRemove);
            for (Layer layer : toRemove) {
                this.getLayerViewPanel().getRenderingManager().removeLayer(layer);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.ProjectFrame#getDataSet()
     */
    @Override
    public final DataSet getDataSet() {
        synchronized (this.getSld().lock) {
            return this.getSld().getDataSet();
        }
    }

    /**
     * Adapter::repaint() do nothing. let this behavior to subclasses
     */
    @Override
    public void repaint() {
        // do nothing. let this behavior to subclasses
    }

    @Override
    public void validate() {
        this.getGui().validate();
    }

}
