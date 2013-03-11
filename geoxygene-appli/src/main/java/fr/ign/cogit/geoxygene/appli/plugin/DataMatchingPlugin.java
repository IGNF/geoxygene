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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParametresAppData;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
// import fr.ign.cogit.geoxygene.contrib.appariement.stockageLiens.StockageLiens;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.appli.gui.ParamDataMatchingNetwork;

/**
 * Data matching plugin.
 * @author Julien Perret
 */
public class DataMatchingPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {

  private static Logger LOGGER = Logger.getLogger(DataMatchingPlugin.class
      .getName());
  private GeOxygeneApplication application;

  /** Reference Shape Filename. */
  private String refShapeFilename = null;
  /** Comparative Shape Filename. */
  private String compShapeFilename = null;
  /** Parameters XML filename. */
  private String paramFilename = null;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu(I18N.getString("DataMatchingPlugin.DataMatching" //$NON-NLS-1$
        ));
    JMenuItem menuItem = new JMenuItem(
        I18N.getString("DataMatchingPlugin.OpenDataMatchingEnvironment" //$NON-NLS-1$
        ));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 1);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    ParamDataMatchingNetwork dialogParamDataMatchingNetwork = new ParamDataMatchingNetwork(this);
    if (refShapeFilename == null || compShapeFilename == null || paramFilename == null) {
      return;
    }

    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("Fichier de référence : " + refShapeFilename);
      LOGGER.info("Fichier de comparaison : " + compShapeFilename);
      LOGGER.info("Fichier paramètres : " + paramFilename);
    }

    /*
     * IPopulation<IFeature> popRef = ShapefileReader.chooseAndReadShapefile();
     * popRef.setNom("popRef"); IPopulation<IFeature> popComp =
     * ShapefileReader.chooseAndReadShapefile(); popComp.setNom("popComp");
     */
    IPopulation<IFeature> popRef = ShapefileReader.read(refShapeFilename);
    popRef.setNom("popRef");
    IPopulation<IFeature> popComp = ShapefileReader.read(compShapeFilename);
    popComp.setNom("popComp");

    // On charge
    ParametresAppData paramAppData = null;
    paramAppData = ParametresAppData.unmarshall(paramFilename);
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("Paramètres chargés");
    }

    ParametresApp param = new ParametresApp();
    // Population
    param.populationsArcs1.add(popRef);
    param.populationsArcs2.add(popComp);
    // Ecarts de distance autorisés
    param.distanceArcsMax = paramAppData.getNoeudsMax();
    param.distanceArcsMin = paramAppData.getArcsMin();
    param.distanceNoeudsMax = paramAppData.getArcsMax();
    param.distanceNoeudsImpassesMax = paramAppData.getNoeudsImpassesMax();
    // 
    param.topologieFusionArcsDoubles1 = true;
    param.topologieFusionArcsDoubles2 = true;
    param.topologieGraphePlanaire1 = true;
    param.topologieGraphePlanaire2 = true;
    param.topologieSeuilFusionNoeuds2 = 0.1;
    param.varianteFiltrageImpassesParasites = false;
    param.projeteNoeuds1SurReseau2 = false;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1 = false;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    param.varianteForceAppariementSimple = true;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugTirets = false;
    param.debugBilanSurObjetsGeo = false;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugAffichageCommentaires = 2;

    List<ReseauApp> reseaux = new ArrayList<ReseauApp>();
    EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, reseaux);

    LOGGER.info("Paramétrage = " + liens.getParametrage());
    LOGGER.info("Evaluation interne = " + liens.getEvaluationInterne());
    LOGGER.info("Evaluation globale = " + liens.getEvaluationGlobale());
    for (Lien feature : liens) {
      Lien lien = feature;
      LOGGER.info("Lien = " + lien); //$NON-NLS-1$
      LOGGER.info("Ref = " + lien.getObjetsRef().toString()); //$NON-NLS-1$
      LOGGER.info("Comp = " + lien.getObjetsComp()); //$NON-NLS-1$
      LOGGER.info("Evaluation = " + lien.getEvaluation()); //$NON-NLS-1$
    }

    CarteTopo reseauRecale = Recalage.recalage(reseaux.get(0), reseaux.get(1),
        liens);
    IPopulation<Arc> arcs = reseauRecale.getPopArcs();
    LOGGER.info(arcs.getNom());

    for (Lien lien : liens) {
      IGeometry geom = lien.getGeom();
      if (geom instanceof GM_Aggregate<?>) {
        GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
        for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
          if (lineGeom instanceof GM_LineString) {
            multiCurve.add((GM_LineString) lineGeom);
          } else {
            LOGGER.error(lineGeom.getClass().getSimpleName());
          }
        }
        lien.setGeom(multiCurve);
      } else {
        LOGGER.info(geom.getClass().getSimpleName());
      }
    }
    LOGGER.info(arcs.getNom());

    LOGGER.trace("----------------------------------------------------------");
    LOGGER.trace("Taille popRef = " + popRef.size());
    LOGGER.trace("Taille popComp = " + popComp.size());
    LOGGER.trace("Nom    popRef = " + popRef.getNom());
    LOGGER.trace("Nom    popComp = " + popComp.getNom());
    LOGGER.trace("----------------------------------------------------------");

    // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
    
    this.application.getFrame().getDesktopPane().removeAll();
    
    Dimension desktopSize = this.application.getFrame().getDesktopPane().getSize();
    int widthProjectFrame = desktopSize.width / 2;
    int heightProjectFrame = desktopSize.height / 2;

    ProjectFrame p1 = this.application.getFrame().newProjectFrame();
    p1.setTitle("Reference Pop"); //$NON-NLS-1$
    p1.addUserLayer(popRef, "Reference Network", null);
    p1.setSize(widthProjectFrame, heightProjectFrame);
    p1.setLocation(0, 0);

    Viewport viewport = p1.getLayerViewPanel().getViewport();

    ProjectFrame p2 = this.application.getFrame().newProjectFrame();
    p2.setTitle("Comparaison Pop"); //$NON-NLS-1$
    p2.addUserLayer(popComp, "Comparison Network", null);
    p2.setSize(widthProjectFrame, heightProjectFrame);
    p2.setLocation(widthProjectFrame, 0);
    p2.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p2.getLayerViewPanel());

    ProjectFrame p3 = this.application.getFrame().newProjectFrame();
    p3.setTitle("Corrected Pop"); //$NON-NLS-1$
    p3.addUserLayer(arcs, "Corrected network", null);
    p3.addUserLayer(popComp, "Comparison Network", null);
    p3.setSize(widthProjectFrame, heightProjectFrame);
    p3.setLocation(0, heightProjectFrame);
    p3.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p3.getLayerViewPanel());

    ProjectFrame p4 = this.application.getFrame().newProjectFrame();
    p4.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p4.getLayerViewPanel());
    p4.setTitle("Links"); //$NON-NLS-1$
    p4.addUserLayer(popRef, "Reference Network", null);
    p4.addUserLayer(popComp, "Comparison Network", null);
    Layer layer = p4.addUserLayer(liens, "Links", null);
    p4.setSize(widthProjectFrame, heightProjectFrame);
    p4.setLocation(widthProjectFrame, heightProjectFrame);

    layer.getSymbolizer().getStroke().setStrokeWidth(2);
    LOGGER.info("Finished"); //$NON-NLS-1$
    
  }

  /**
   * @param f The reference Shape Filename to set
   */
  public void setRefShapeFilename(String f) {
    refShapeFilename = f;
  }

  /**
   * @param f The comparative Shape Filename to set
   */
  public void setCompShapeFilename(String f) {
    compShapeFilename = f;
  }

  /**
   * @param f The parameters XML Filename to set
   */
  public void setParamFilename(String f) {
    paramFilename = f;
  }

}
