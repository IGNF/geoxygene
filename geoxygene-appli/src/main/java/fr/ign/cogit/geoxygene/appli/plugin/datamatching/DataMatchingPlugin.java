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
package fr.ign.cogit.geoxygene.appli.plugin.datamatching;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Data matching plugin.
 * @author Julien Perret
 */
public class DataMatchingPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  private static Logger LOGGER = Logger.getLogger(DataMatchingPlugin.class
      .getName());
  private GeOxygeneApplication application;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    
      this.application = application;
      JMenu menu = null;
      String menuName = I18N.getString("DataMatchingPlugin.DataMatching"); //$NON-NLS-1$
      for (Component c : application.getFrame().getJMenuBar().getComponents()) {
        if (c instanceof JMenu) {
          JMenu aMenu = (JMenu) c;
          if (aMenu.getText() != null
              && aMenu.getText().equalsIgnoreCase(menuName)) { 
            menu = aMenu;
          }
        }
      }
      if (menu == null) {
        menu = new JMenu(menuName);
      }
    
    JMenuItem menuItem = new JMenuItem(
        I18N.getString("DataMatchingPlugin.OpenDataMatchingEnvironment" //$NON-NLS-1$
        ));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    IPopulation<IFeature> popRef = ShapefileReader.chooseAndReadShapefile();
    IPopulation<IFeature> popComp = ShapefileReader.chooseAndReadShapefile();
    this.application.getFrame().getDesktopPane().removeAll();
    List<ReseauApp> reseaux = new ArrayList<ReseauApp>();
    ParametresApp param = new ParametresApp();
    param.populationsArcs1.add(popRef);
    param.populationsArcs2.add(popComp);
    param.topologieFusionArcsDoubles1 = true;
    param.topologieFusionArcsDoubles2 = true;
    param.topologieGraphePlanaire1 = true;
    param.topologieGraphePlanaire2 = true;
    param.topologieSeuilFusionNoeuds2 = 1;
    param.varianteFiltrageImpassesParasites = false;
    param.projeteNoeuds1SurReseau2 = true;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = 8; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 20; // 50
    param.projeteNoeuds2SurReseau1 = true;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = 8; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 20; // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    param.varianteForceAppariementSimple = true;
    param.distanceArcsMax = 20; // 50
    param.distanceArcsMin = 8; // 30
    param.distanceNoeudsMax = 20; // 50
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugTirets = false;
    param.debugBilanSurObjetsGeo = false;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugAffichageCommentaires = 2;
    EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, reseaux);
    for (Lien feature : liens) {
      Lien lien = feature;
      DataMatchingPlugin.LOGGER.info("Lien = " + lien); //$NON-NLS-1$
      DataMatchingPlugin.LOGGER.info("Ref = " + lien.getObjetsRef()); //$NON-NLS-1$
      DataMatchingPlugin.LOGGER.info("Comp = " + lien.getObjetsComp()); //$NON-NLS-1$
      DataMatchingPlugin.LOGGER.info("Evaluation = " + lien.getEvaluation()); //$NON-NLS-1$
    }
    CarteTopo reseauRecale = Recalage.recalage(reseaux.get(0), reseaux.get(1), liens);
    IPopulation<Arc> arcs = reseauRecale.getPopArcs();
    DataMatchingPlugin.LOGGER.info(arcs.getNom());
    for (Lien lien : liens) {
      IGeometry geom = lien.getGeom();
      if (geom instanceof GM_Aggregate<?>) {
        GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
        for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
          if (lineGeom instanceof GM_LineString) {
            multiCurve.add((GM_LineString) lineGeom);
          } else {
            DataMatchingPlugin.LOGGER
                .error(lineGeom.getClass().getSimpleName());
          }
        }
        lien.setGeom(multiCurve);
      } else {
        DataMatchingPlugin.LOGGER.info(geom.getClass().getSimpleName());
      }
    }
    DataSet.getInstance().addPopulation(popRef);
    DataSet.getInstance().addPopulation(popComp);
    DataMatchingPlugin.LOGGER.info(arcs.getNom());
    DataSet.getInstance().addPopulation(reseauRecale.getPopArcs());
    DataMatchingPlugin.LOGGER.info(arcs.getNom());
    DataSet.getInstance().addPopulation(liens);
    ProjectFrame p1 = this.application.getFrame().newProjectFrame();
    p1.setTitle("Reference Pop"); //$NON-NLS-1$
    Viewport viewport = p1.getLayerViewPanel().getViewport();
    ProjectFrame p2 = this.application.getFrame().newProjectFrame();
    p2.setTitle("Comparison Pop"); //$NON-NLS-1$
    p2.addFeatureCollection(popComp, popComp.getNom(),null);
    p2.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
    ProjectFrame p3 = this.application.getFrame().newProjectFrame();
    p3.setTitle("Corrected Pop"); //$NON-NLS-1$
    p3.addFeatureCollection(arcs, arcs.getNom(),null);
    p3.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
    ProjectFrame p4 = this.application.getFrame().newProjectFrame();
    p4.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p4.getLayerViewPanel());
    p4.setTitle("Links"); //$NON-NLS-1$
    p4.addFeatureCollection(popRef, popRef.getNom(),null);
    p4.addFeatureCollection(popComp, popComp.getNom(),null);
    Layer layer = p4.addFeatureCollection(liens, liens.getNom(),null);
    layer.getSymbolizer().getStroke().setStrokeWidth(2);
    DataMatchingPlugin.LOGGER.info("Finished"); //$NON-NLS-1$
  }
}
