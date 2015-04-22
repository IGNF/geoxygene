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

package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JOptionPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.algorithms.SwingingArmNonConvexHull;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Plugin for create non convex hull with swinging arm method.
 * 
 * @author Julien Perret
 */
public class SwingingArmPlugin extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(SwingingArmPlugin.class.getName());

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    
    JMenu menu = addMenu("Geometry Algorithms", "SwingingArmNonConvexHull");
    application.getMainFrame().getMenuBar().add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getMainFrame().getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel().getSelectedLayers();
    if (selectedLayers.size() != 1) {
      javax.swing.JOptionPane.showMessageDialog(null, "You need to select one (and only one) layer.");
      LOGGER.error("You need to select one (and only one) layer."); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    
    double radius = Double.parseDouble(JOptionPane.showInputDialog("radius")); //$NON-NLS-1$
    List<IDirectPosition> list = new ArrayList<IDirectPosition>();
    for (IFeature f : layer.getFeatureCollection()) {
      
      if (f.getGeom() instanceof GM_MultiCurve) {
        GM_MultiCurve<?> multi = (GM_MultiCurve<?>) f.getGeom();
        if (multi.size() > 0) {
          ILineString line = (GM_LineString) multi.get(0);
          line = Operateurs.echantillonePasVariable(line, radius / 2);
          list.addAll(line.coord().getList());
        }
      } else if (f.getGeom() instanceof GM_Point) {
        GM_Point pt = (GM_Point) f.getGeom();
        list.add(pt.getPosition());
      }
    }
    LOGGER.error("Points added : " + list.size());
    LOGGER.setLevel(Level.TRACE);
    
    SwingingArmNonConvexHull swinging = new SwingingArmNonConvexHull(list, radius);
    IGeometry characteristicShape = swinging.compute();
    // logger.error("Shape : " + characteristicShape.getClass());
    Population<Face> popTriangles = new Population<Face>("NonConvexHull_" + radius); //$NON-NLS-1$
    popTriangles.setClasse(Face.class);
    popTriangles.setPersistant(false);
    popTriangles.nouvelElement(characteristicShape);
    
    /** créer un featuretype de jeu correspondant */
    FeatureType newFeatureTypeExterieurs = new FeatureType();
    newFeatureTypeExterieurs.setGeometryType(GM_Polygon.class);
    popTriangles.setFeatureType(newFeatureTypeExterieurs);
    
    // DataSet.getInstance().addPopulation(popTriangles);
    LOGGER.info(popTriangles.size());
    // LOGGER.info(characteristicShape);
    project.addUserLayer(popTriangles, popTriangles.getNom(), null);
    
  }
}
