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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Tin;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Triangulation plugin.
 * @author Julien Perret
 */
public class TINPlugin implements GeOxygeneApplicationPlugin, ActionListener {
  /** Logger. */
  static Logger logger = Logger.getLogger(Triangulation.class.getName());

  private GeOxygeneApplication application = null;

  /**
   * Initialize the plugin.
   * @param app the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication app) {
    this.application = app;
    JMenu menu = null;
    for (Component c : app.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase("TIN")) { //$NON-NLS-1$
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu("TIN");//$NON-NLS-1$
    }
    JMenuItem menuItem = new JMenuItem("Create TIN" //$NON-NLS-1$
    );
    menuItem.addActionListener(this);
    menu.add(menuItem);
    app.getMainFrame().getMenuBar()
        .add(menu, app.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getMainFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      TINPlugin.logger.error("You need to select one (and only one) layer."); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();
    IDirectPositionList list = new DirectPositionList();
    for (IFeature f : layer.getFeatureCollection()) {
      list.add(f.getGeom().centroid());
    }
    // TODO ADD StopLines and BreakLines to the Plugin
    // for (IFeature f : popSites) {
    // list.add(f.getGeom().centroid());
    // }
    // List<ILineString> breaklines = new ArrayList<ILineString>();
    // for (IFeature f : popBreakLines) {
    // if (IMultiCurve.class.isAssignableFrom(f.getGeom().getClass())) {
    // for (IOrientableCurve curve : ((IMultiCurve<? extends IOrientableCurve>)
    // f
    // .getGeom()).getList()) {
    // breaklines.add(curve.getPrimitive().asLineString(0, 0, 0));
    // }
    // } else {
    // breaklines.add((ILineString) f.getGeom());
    // }
    // }
    // List<ILineString> stoplines = new ArrayList<ILineString>();
    // for (IFeature f : popStopLines) {
    // if (IMultiCurve.class.isAssignableFrom(f.getGeom().getClass())) {
    // for (IOrientableCurve curve : ((IMultiCurve<? extends IOrientableCurve>)
    // f
    // .getGeom()).getList()) {
    // stoplines.add(curve.getPrimitive().asLineString(0, 0, 0));
    // }
    // } else {
    // stoplines.add((ILineString) f.getGeom());
    // }
    // }
    // GM_Tin tin = new GM_Tin(list, stoplines, breaklines,
    // Float.POSITIVE_INFINITY);
    GM_Tin tin = new GM_Tin(list, null, null, Float.POSITIVE_INFINITY);
    logger.info(tin.getlTriangles().size() + " triangles found");
    Population<DefaultFeature> popTriangles = new Population<DefaultFeature>(
        "TIN"); //$NON-NLS-1$
    popTriangles.setClasse(DefaultFeature.class);
    popTriangles.setPersistant(false);
    for (ITriangle t : tin.getlTriangles()) {
      popTriangles.nouvelElement(t);
    }
    /** créer un featuretype de jeu correspondant */
    fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
    newFeatureType.setGeometryType(GM_Polygon.class);
    popTriangles.setFeatureType(newFeatureType);
    project.getDataSet().addPopulation(popTriangles);
    project.addFeatureCollection(popTriangles, popTriangles.getNom(), null);
  }
}
