/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.LayerFactory.LayerType;
import fr.ign.cogit.geoxygene.appli.plugin.GeometryToolBar;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.GPSTextfileReader;
import fr.ign.cogit.geoxygene.util.conversion.RoadNetworkTextfileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Project Frame.
 * 
 * @author Julien Perret
 */
public class ProjectFrame extends JInternalFrame implements ActionListener {
    /**
     * Logger of the application.
     */
    private static Logger logger = Logger.getLogger(ProjectFrame.class
            .getName());

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The layer view panel.
     */
    private LayerViewPanel layerViewPanel = null;

    /**
     * @return The {@link LayerViewPanel}
     */
    public final LayerViewPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    /**
     * The layer legend panel.
     */
    private LayerLegendPanel layerLegendPanel = null;

    /**
     * @return The layer legend panel.
     */
    public LayerLegendPanel getLayerLegendPanel() {
        return this.layerLegendPanel;
    }

    /**
     * The split pane.
     */
    private JSplitPane splitPane = new JSplitPane();

    /**
     * @return The split pane
     */
    public JSplitPane getSplitPane() {
        return this.splitPane;
    }

    /**
     * The project styled layer descriptor.
     */
    private StyledLayerDescriptor sld = new StyledLayerDescriptor(new DataSet());

    public void setSld(StyledLayerDescriptor sld) {
        this.sld = sld;
    }

    /**
     * @return The project styled layer descriptor
     */
    public StyledLayerDescriptor getSld() {
        return this.sld;
    }

    /**
     * The default frame width.
     */
    private static final int DEFAULT_WIDTH = 600;
    /**
     * The default frame height.
     */
    private static final int DEFAULT_HEIGHT = 400;
    /**
     * The default frame divider location.
     */
    private static final int DEFAULT_DIVIDER_LOCATION = 200;

    private MainFrame mainFrame = null;

    public MainFrame getMainFrame() {
        return this.mainFrame;
    }

    private static int PFID = 1;

    /**
     * Constructor.
     * 
     * @param iconImage
     *            the project icon image
     */
    public ProjectFrame(final MainFrame frame, final ImageIcon iconImage) {
        super("ProjectFrame " + ProjectFrame.PFID++, true, true, true, true); //$NON-NLS-1$
        // Setting the tool tip text to the frame and its sub components
        this.setToolTipText(this.getTitle());
        this.getDesktopIcon().setToolTipText(this.getTitle());
        for (Component c : this.getDesktopIcon().getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setToolTipText(this.getTitle());
            }
        }
        this.setSize(ProjectFrame.DEFAULT_WIDTH, ProjectFrame.DEFAULT_HEIGHT);
        this.setFrameIcon(iconImage);
        this.mainFrame = frame;
        this.layerViewPanel = new LayerViewPanel(this);
        this.layerViewPanel.setModel(this.sld);
        this.layerLegendPanel = new LayerLegendPanel(this);
        this.layerLegendPanel.setModel(this.sld);
        this.getContentPane().setLayout(new BorderLayout());
        this.splitPane.setBorder(null);
        this.layerLegendPanel.setLayout(new BoxLayout(this.layerLegendPanel,
                BoxLayout.PAGE_AXIS));
        this.getContentPane().add(this.splitPane, BorderLayout.CENTER);
        this.splitPane.add(this.layerLegendPanel, JSplitPane.LEFT);
        this.splitPane.add(this.layerViewPanel, JSplitPane.RIGHT);
        this.splitPane
                .setDividerLocation(ProjectFrame.DEFAULT_DIVIDER_LOCATION);
        this.splitPane.setOneTouchExpandable(true);
        this.pack();
        ShapefileReader.addActionListener(this);
    }

    public void addLayer(Layer l) {
        this.sld.add(l);
    }

    public void addLayer(File file) {
        if (file != null) {
            String fileName = file.getAbsolutePath();
            String extention = fileName
                    .substring(fileName.lastIndexOf('.') + 1);
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
                this.sld.add(l);
            }
            return;
        }
    }

    public void askAndAddNewLayer() {
        File file = MainFrame.getFilechooser().getFile(this.getMainFrame());
        this.addLayer(file);
    }
  public void addGpsTxtLayer(String fileName) {
    int lastIndexOfSeparator = fileName.lastIndexOf(File.separatorChar);
    String populationName = fileName.substring(lastIndexOfSeparator + 1,
        fileName.lastIndexOf(".")); //$NON-NLS-1$
    logger.info(populationName);
    Population<DefaultFeature> population = GPSTextfileReader.read(fileName, populationName, this.getDataSet(), true);
    logger.info(population.size());

    if (population != null) {
      this.addFeatureCollection(population, population.getNom());
      if (this.getLayers().size() == 1) {
        try {
          this.layerViewPanel.getViewport().zoom(population.envelope());
        } catch (NoninvertibleTransformException e1) {
          e1.printStackTrace();
        }
      }    
    }
  }
  public void addRoadNetworkTxtLayer(String fileName) {
    int lastIndexOfSeparator = fileName.lastIndexOf(File.separatorChar);
    String populationName = fileName.substring(lastIndexOfSeparator + 1,
        fileName.lastIndexOf(".")); //$NON-NLS-1$
    logger.info(populationName);
    Population<DefaultFeature> population = RoadNetworkTextfileReader.read(fileName, populationName, this.getDataSet(), true);
    logger.info(population.size());

    if (population != null) {
      this.addFeatureCollection(population, population.getNom());
      if (this.getLayers().size() == 1) {
        try {
          this.layerViewPanel.getViewport().zoom(population.envelope());
        } catch (NoninvertibleTransformException e1) {
          e1.printStackTrace();
        }
      }    
    }
  }

    /**
     * Add a feature collection to the project.
     * 
     * @param population
     *            the population to add
     * @param name
     *            the name of the population
     */
    public final Layer addFeatureCollection(
            final IPopulation<? extends IFeature> population,
            final String name, CoordinateReferenceSystem crs) {
      LayerFactory factory = new LayerFactory(this.getSld());
        Layer layer = factory.createLayer(name, population
                .getFeatureType().getGeometryType());
        layer.setCRS(crs);
        this.sld.add(layer);
        return layer;
    }

    /**
     * @author Bertrand Dumenieu
     * @deprecated This function should not be longer used since layers include
     *             now a CRS. Only here to ensure backward compatibility
     */

    @Deprecated
    public final Layer addFeatureCollection(final IPopulation<?> population,
            final String name) {
        return this.addFeatureCollection(population, name, null);
    }

    /**
     * Dispose of the frame an its {@link LayerViewPanel}.
     */
    @Override
    public final void dispose() {
        this.layerViewPanel.dispose();
        super.dispose();
    }

    /**
     * @return The list of the project's layers
     */
    public final List<Layer> getLayers() {
        return this.sld.getLayers();
    }

    /**
     * @param name
     *            name of the layer
     * @return The layer with the given name
     */
    public final Layer getLayer(String name) {
        return this.sld.getLayer(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == 2) {
            // loading finished
            IPopulation<?> p = (IPopulation<?>) e.getSource();
            Layer l = this.getLayer(p.getNom());
            this.getLayerViewPanel()
                    .getRenderingManager()
                    .render(this.getLayerViewPanel().getRenderingManager()
                            .getRenderer(l));
            this.getLayerViewPanel().superRepaint();
        }
    }

    /**
     * Clear the selection.
     */
    public void clearSelection() {
        this.getLayerViewPanel().getSelectedFeatures().clear();
        this.getLayerViewPanel().getRenderingManager().getSelectionRenderer()
                .clearImageCache();
        this.getLayerViewPanel().superRepaint();
    }

    public Layer getLayerFromFeature(IFeature ft) {
        for (Layer layer : this.getSld().getLayers()) {
            if (layer.getFeatureCollection().contains(ft)) {
                return layer;
            }
        }
        return null;
    }

    private GeometryToolBar geometryToolBar = null;

    public void setGeometryToolsVisible(boolean b) {
        if (b) {
            if (this.geometryToolBar == null) {
                this.geometryToolBar = new GeometryToolBar(this);
                this.getContentPane().add(this.geometryToolBar,
                        BorderLayout.EAST);
            }
            this.geometryToolBar.setVisible(true);
            this.validate();
        } else {
            this.geometryToolBar.setVisible(false);
            this.validate();
        }
    }

    Map<IFeature, BufferedImage> featureToImageMap = new HashMap<IFeature, BufferedImage>();

    public void addImage(String name, BufferedImage image, double[][] range) {
        DefaultFeature feature = new DefaultFeature(new GM_Envelope(
                range[0][0], range[0][1], range[1][0], range[1][1]).getGeom());
        this.featureToImageMap.put(feature, image);
        Population<DefaultFeature> population = new Population<DefaultFeature>(
                name);
        population.add(feature);
        this.getDataSet().addPopulation(population);
        LayerFactory factory = new LayerFactory(this.getSld());
        Layer layer = factory.createLayer(name);
        this.sld.add(layer);
    }

    public BufferedImage getImage(IFeature feature) {
        logger.error(this.featureToImageMap.size() + " elements in map"); //$NON-NLS-1$
        if (feature == null) {
            if (this.featureToImageMap.isEmpty()) {
                return null;
            }
            return this.featureToImageMap.values().iterator().next();
        }
        return this.featureToImageMap.get(feature);
    }

    /**
     * Save the map into an image file. The file format is determined by the
     * given file extension. If there is none or if the given extension is
     * unsupported, the image is saved in PNG format.
     * 
     * @param fileName
     *            the image file to save into.
     */
    public void saveAsImage(String fileName) {
        this.getLayerViewPanel().saveAsImage(fileName);
    }

    /**
     * Saves a layer into an ESRI Shapefile
     * 
     * @param fileName
     * @param layer
     */
    public void saveAsShp(String fileName, Layer layer) {
        try {
            ShapefileWriter.write(layer.getFeatureCollection(), fileName,
                    layer.getCRS());
        } catch (Exception e) {
            ProjectFrame.logger
                    .error("Shapefile export failed! See stack trace below : "); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    public void removeLayers(List<Layer> toRemove) {
        System.out.println("removing +" + toRemove.size() + "+layers"); //$NON-NLS-1$ //$NON-NLS-2$
        this.sld.remove(toRemove);
        for (Layer layer : toRemove) {
            this.layerViewPanel.getRenderingManager().removeLayer(layer);
        }
    }
    public DataSet getDataSet() {
      return this.sld.getDataSet();
    }
}
