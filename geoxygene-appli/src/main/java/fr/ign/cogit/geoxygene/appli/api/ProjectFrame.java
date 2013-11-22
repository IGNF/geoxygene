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
package fr.ign.cogit.geoxygene.appli.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.xml.bind.JAXBException;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.LayerLegendPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * A projectFrame is a Windowed graphic area contained into a MainFrame and
 * containing some layers to display, a selection, an Sld, ...
 * 
 * @author turbet
 * 
 */
public interface ProjectFrame {

    /**
     * get the main GUI component of this Project Frame
     */
    public abstract JComponent getGui();

    /** @return the main frame containing all project frames */
    public abstract MainFrame getMainFrame();

    /** @return The {@link LayerViewPanel} */
    public abstract LayerViewPanel getLayerViewPanel();

    public abstract void setSld(StyledLayerDescriptor sld);

    /** @return The project styled layer descriptor */
    public abstract StyledLayerDescriptor getSld();

    /**
     * Add a layer at the end of the sld of this ProjectFrame.
     * 
     * @param l
     *            the new layer
     */
    public abstract void addLayer(Layer l);

    /**
     * Inserts the specified layer at the specified position in this
     * ProjectFrame.
     * 
     * @param l
     *            the new layer
     * @param index
     *            the position of the new layer
     */
    public abstract void addLayer(Layer l, int index);

    public abstract void addLayer(File file);

    public abstract void askAndAddNewLayer();

    public abstract void addGpsTxtLayer(String fileName);

    public abstract void addRoadNetworkTxtLayer(String fileName);

    /**
     * Add a feature collection to the project.
     * 
     * @param population
     *            the population to add
     * @param name
     *            the name of the population
     */
    public abstract Layer addFeatureCollection(IPopulation<? extends IFeature> population, String name, CoordinateReferenceSystem crs);

    /**
     * Add a feature collection to the project.
     * 
     * @param population
     *            the population to add
     * @param name
     *            the name of the population
     */
    public abstract Layer addUserLayer(IFeatureCollection<? extends IFeature> collection, String name, CoordinateReferenceSystem crs);

    /**
     * @author Bertrand Dumenieu
     * @deprecated This function should not be longer used since layers include
     *             now a CRS. Only here to ensure backward compatibility
     */
    @Deprecated
    public abstract Layer addFeatureCollection(IPopulation<?> population, String name);

    /** @return The list of the project's layers */
    public abstract List<Layer> getLayers();

    /** Dispose of the frame an its {@link LayerViewPanel}. */
    public abstract void dispose();

    /**
     * @param name
     *            name of the layer
     * @return The layer with the given name
     */
    public abstract Layer getLayer(String name);

    /** Clear the selection. */
    public abstract void clearSelection();

    public abstract Layer getLayerFromFeature(IFeature ft);

    public abstract void setGeometryToolsVisible(boolean b);

    public abstract void addImage(String name, BufferedImage image, double[][] range);

    public abstract BufferedImage getImage(IFeature feature);

    /**
     * Save the map into an image file. The file format is determined by the
     * given
     * file extension. If there is none or if the given extension is
     * unsupported,
     * the image is saved in PNG format.
     * 
     * @param fileName
     *            the image file to save into.
     */
    public abstract void saveAsImage(String fileName);

    /**
     * Saves a layer into an ESRI Shapefile
     * 
     * @param fileName
     * @param layer
     */
    public abstract void saveAsShp(String fileName, Layer layer);

    /**
     * SCH Save the current styles into an xml file.
     * 
     * @param fileName
     *            the xml file to save into.
     */

    public void saveAsSLD(String fileName);

    /**
     * SCH load the described styles in an xml file and apply them to a
     * predefined
     * dataset.
     * 
     * @param fileName
     *            the xml file to load.
     * @throws JAXBException
     * @throws FileNotFoundException
     */

    public void loadSLD(File file) throws FileNotFoundException, JAXBException;

    public abstract void removeLayers(List<Layer> toRemove);

    public abstract DataSet getDataSet();

    /**
     * repaint UI
     */
    public abstract void repaint();

    public abstract LayerLegendPanel getLayerLegendPanel();

    /**
     * set the project frame title
     */
    public abstract void setTitle(String string);

    /**
     * get the project frame title
     */
    public abstract String getTitle();

    /**
     * get project name
     */
    public abstract String getName();

    public abstract void setMenuBar(JMenuBar menuBar);

    public abstract JMenuBar getMenuBar();

    public abstract void setSize(int widthProjectFrame, int i);

    public abstract void setLocation(int widthProjectFrame, int i);

    /**
     * Add a component to this ProjectFrame
     */
    public void addComponentInProjectFrame(JComponent component, String layout);

    /**
     * Validates this container and all of its subcomponents.
     */
    public abstract void validate();

}
