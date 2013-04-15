/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.datamatching;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.network.DisplayToolBarNetworkDataMatching;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.network.EditParamPanel;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.DatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamFilenameNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;


/**
 * Data matching plugin.
 * Display 2 types of results :
 *   - links + stats + param 
 *   - corrected network + comparison network
 *   
 * @since 1.6 manage new Result structure
 * @author Julien Perret
 */
public class NetworkDataMatchingPlugin implements GeOxygeneApplicationPlugin,
  ActionListener {
  
  /** Classic logger. */
  private static Logger LOGGER = Logger.getLogger(NetworkDataMatchingPlugin.class.getName());
  
  /** GeOxygeneApplication. */
  private GeOxygeneApplication application;

  /** Parameters and datasets. */
  private ParamPluginNetworkDataMatching paramPlugin = new ParamPluginNetworkDataMatching();
  private DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
  private DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
  
  /** Displayed colors. */
  private final static Color linkColorOk = new Color(77, 146, 33);
  private final static Color linkColorNull = new Color(239, 59, 44);
  private final static Color linkColorDoubtfull = new Color(240, 157, 18);
  
  private final static Color network1Color = new Color(11, 73, 157);
  private final static Color network1DColor = new Color(67, 144, 193);
  private final static Color network1NColor = new Color(111, 173, 209);
  
  private final static Color network2Color = new Color(145, 100, 10);
  private final static Color matchedNetworkColor = new Color(136, 64, 153);
  private final static int LINE_WIDTH = 3;
  private final static int LINE_WIDTH_LINK = 1;
  
  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    
    this.application = application;
    
    // Check if the DataMatching menu exists. If not we create it.
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
    
    // Add network data matching menu item to the menu.
    JMenuItem menuItem = new JMenuItem(
        I18N.getString("DataMatchingPlugin.NDM.OpenDataMatchingEnvironment" //$NON-NLS-1$
        ));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // initialize Param
    initializeParam();
    
    // Refresh menu of the application
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 1);
  }
  
  /**
   * 
   */
  private void initializeParam() {
    
    String filename1 = "D:\\DATA\\Appariement\\MesTests\\T3\\bdcarto_route.shp";
    String filename2 = "D:\\DATA\\Appariement\\MesTests\\T3\\bdtopo_route.shp";
    // String filename1 = "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau1.shp";
    // String filename2 = "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau2.shp";
    // "D:\\Data\\Appariement\\ESPON_DB\\1-RoadL_Paris_2000_extract.shp"
    // "D:\\Data\\Appariement\\ESPON_DB\\2-Streets_extract.shp"
    // "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\ERM\\RoadL_Paris_2000.shp"
    // "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\Navstreets\\Streets.shp"
    
    // Dataset  
    ParamFilenameNetworkDataMatching paramFilename1 = new ParamFilenameNetworkDataMatching();
    paramFilename1.setListNomFichiersPopArcs(filename1);
    paramPlugin.setParamFilenameNetwork1(paramFilename1);
    
    ParamFilenameNetworkDataMatching paramFilename2 = new ParamFilenameNetworkDataMatching();
    paramFilename2.setListNomFichiersPopArcs(filename2);
    paramPlugin.setParamFilenameNetwork2(paramFilename2);
    
    // Param
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    
    // Direction
    ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
    paramDirection.setOrientationDouble(true);
    param.setParamDirectionNetwork1(paramDirection);
    
    // Distance
    ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
    float distanceNoeudsMax = 50;
    paramDistance.setDistanceNoeudsMax(distanceNoeudsMax);
    paramDistance.setDistanceArcsMax(2 * distanceNoeudsMax);
    paramDistance.setDistanceArcsMin(distanceNoeudsMax);
    param.setParamDistance(paramDistance);
    
    paramPlugin.setParamNetworkDataMatching(param);
  }

  /**
   * 
   */
  @Override
  public void actionPerformed(final ActionEvent e) {
    
    // Launch parameter network data matching panel.
    EditParamPanel dialogParamDataMatchingNetwork = new EditParamPanel(this);
    
    if (dialogParamDataMatchingNetwork.getAction().equals("LAUNCH")) {
      
      // -------------------------------------------------------------------------------
      // Dataset
      IPopulation<IFeature> reseau1 = ShapefileReader.read(paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs());
      datasetNetwork1.addPopulationsArcs(reseau1);
      IPopulation<IFeature> reseau2 = ShapefileReader.read(paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs());
      datasetNetwork2.addPopulationsArcs(reseau2);
      
      // -------------------------------------------------------------------------------
      
      // Parse new parameter object to old parameter
      ParametresApp paramOld = paramPlugin.getParamNetworkDataMatching().paramNDMToParamApp();
      
      paramOld.populationsArcs1 = datasetNetwork1.getPopulationsArcs();
      paramOld.populationsArcs2 = datasetNetwork2.getPopulationsArcs();
      
      paramOld.topologieGraphePlanaire1 = false;  // true
      paramOld.topologieFusionArcsDoubles1 = true;  // true
      paramOld.topologieSeuilFusionNoeuds1 = 0.1;  // 0.1
      
      paramOld.topologieGraphePlanaire2 = false;  // true
      paramOld.topologieFusionArcsDoubles2 = true;  // true
      paramOld.topologieSeuilFusionNoeuds2 = 0.1;  // 0.1
      
      paramOld.varianteFiltrageImpassesParasites = false;
      
      float distanceNoeudsMax = 50;
      
      paramOld.projeteNoeuds1SurReseau2 = true;   // true
      paramOld.projeteNoeuds1SurReseau2DistanceNoeudArc = distanceNoeudsMax;
      paramOld.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 2 * distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1 = true;   // true
      paramOld.projeteNoeuds2SurReseau1DistanceNoeudArc = distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 2 * distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1ImpassesSeulement = true;
      paramOld.topologieElimineNoeudsAvecDeuxArcs1=false;
      paramOld.topologieElimineNoeudsAvecDeuxArcs2=false;
      
      // moins detaille
      paramOld.varianteForceAppariementSimple = false;
      paramOld.varianteRedecoupageArcsNonApparies = false;
      
      paramOld.debugTirets = true;
      paramOld.debugBilanSurObjetsGeo = true;
      // paramOld.debugAffichageCommentaires = 1;
      
      // Log parameters
      LOGGER.info(paramPlugin.toString());
      
      NetworkDataMatching networkDataMatching = new NetworkDataMatching(paramOld);
      ResultNetworkDataMatching resultatAppariement = networkDataMatching.networkDataMatching();
      
      // Logs
      LOGGER.info("Nb arcs du réseau 1 calculés : " + resultatAppariement.getReseau1().getListeArcs().size()
          + " >= " + reseau1.size());
      LOGGER.info("Nb arcs du réseau 2 calculés : " + resultatAppariement.getReseau2().getListeArcs().size()
          + " >= " + reseau2.size());
      
      EnsembleDeLiens liens = resultatAppariement.getLinkDataSet();
      // if (paramOld.debugBilanSurObjetsGeo) {
        // liens = resultatAppariement.getLiensGeneriques();
      //}
      
      ResultNetworkStat resultNetwork = resultatAppariement.getResultStat();
      
      // Debug
      // Liens
      
      // Log stats
      LOGGER.info(resultNetwork.getStatsEdgesOfNetwork1().toString());
      LOGGER.info(resultNetwork.getStatsNodesOfNetwork1().toString());
      LOGGER.info(resultNetwork.getStatsEdgesOfNetwork2().toString());
      LOGGER.info(resultNetwork.getStatsNodesOfNetwork2().toString());
      
      // Recalage
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseau1(), resultatAppariement.getReseau2(), liens);
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();
      LOGGER.info(arcs.getNom());
  
      // Split les multi
      for (Lien lien : liens) {
        IGeometry geom = lien.getGeom();
        if (geom instanceof GM_Aggregate<?>) {
          GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
          for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
            if (lineGeom instanceof GM_LineString) {
              multiCurve.add((GM_LineString) lineGeom);
            } else {
              if (lineGeom instanceof GM_MultiCurve<?>) {
                multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
              }
            }
          }
          lien.setGeom(multiCurve);
        }
      }
  
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Taille popRef = " + datasetNetwork1.getPopulationsArcs().get(0).size());
      LOGGER.trace("Taille popComp = " + datasetNetwork2.getPopulationsArcs().get(0).size());
      LOGGER.trace("----------------------------------------------------------");
  
      // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
      
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Enregistrement des résultats en fichier shape");
      
      // ShapefileWriter.write(arcs, "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau1-Apparie.shp");
      // ShapefileWriter.write(liens, "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\liens.shp");
      
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-ArcTopo.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-NoeudTopo.shp");
      
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-ArcTopo.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-NoeudTopo.shp");
      
      LOGGER.trace("----------------------------------------------------------");
      
      
      this.application.getFrame().getDesktopPane().removeAll();
      
      Dimension desktopSize = this.application.getFrame().getDesktopPane().getSize();
      int widthProjectFrame = desktopSize.width / 2;
      int heightProjectFrame = desktopSize.height / 2;
  
      // ---------------------------------------------------------------------------------
      // Frame n°1
      // 
      ProjectFrame p1 = this.application.getFrame().newProjectFrame();
      p1.setTitle("Reseau 1 + reseau 2");
      Layer l1 = p1.addUserLayer(datasetNetwork1.getPopulationsArcs().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l2 = p1.addUserLayer(datasetNetwork2.getPopulationsArcs().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      p1.setSize(widthProjectFrame, heightProjectFrame);
      p1.setLocation(0, 0);
      Viewport viewport = p1.getLayerViewPanel().getViewport();
  
      // ---------------------------------------------------------------------------------
      // Frame n°2
      //    
      ProjectFrame p2 = this.application.getFrame().newProjectFrame();
      p2.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
      p2.setTitle("Reseau 1 après recalage");
      
      List<String> valeursClassement = new ArrayList<String>();
      valeursClassement.add(I18N.getString("Appariement.Matched"));
      valeursClassement.add(I18N.getString("Appariement.Uncertain"));
      valeursClassement.add(I18N.getString("Appariement.Unmatched"));

      LOGGER.info("----");
      LOGGER.info("Nombre d'arcs de la carte topo n°1 = " + resultatAppariement.getReseau1().getListeArcs().size());
      LOGGER.info("Nombre de noeuds de la carte topo n°1 = " + resultatAppariement.getReseau1().getListeNoeuds().size());
      
      /*List<ReseauApp> cartesTopoReferenceValuees = AppariementIO
          .scindeSelonValeursResultatsAppariement(resultatAppariement.getReseau1(), valeursClassement);
      IPopulation<Arc> arcsReferenceApparies = cartesTopoReferenceValuees.get(0).getPopArcs();
      IPopulation<Arc> arcsReferenceIncertains = cartesTopoReferenceValuees.get(1).getPopArcs();
      IPopulation<Arc> arcsReferenceNonApparies = cartesTopoReferenceValuees.get(2).getPopArcs();
      // IPopulation<Noeud> noeudsReferenceApparies = cartesTopoReferenceValuees.get(0).getPopNoeuds();
      // IPopulation<Noeud> noeudsReferenceIncertains = cartesTopoReferenceValuees.get(1).getPopNoeuds();
      // IPopulation<Noeud> noeudsReferenceNonApparies = cartesTopoReferenceValuees.get(2).getPopNoeuds();
      
      l1 = p2.addUserLayer(arcsReferenceApparies, "Arcs réseau 1 appariés", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l1D = p2.addUserLayer(arcsReferenceIncertains, "Arcs réseau 1 incertains", null);
      l1D.getSymbolizer().getStroke().setColor(network1DColor);
      l1D.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l1N = p2.addUserLayer(arcsReferenceNonApparies, "Arcs réseau 1 non appariés", null);
      l1N.getSymbolizer().getStroke().setColor(network1NColor);
      l1N.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);*/
      l1 = p2.addUserLayer(datasetNetwork1.getPopulationsArcs().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      
      l2 = p2.addUserLayer(datasetNetwork2.getPopulationsArcs().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l1bis = p2.addUserLayer(arcs, "Reseau 1 recale", null);
      l1bis.getSymbolizer().getStroke().setColor(matchedNetworkColor);
      l1bis.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      p2.setSize(widthProjectFrame, heightProjectFrame);
      p2.setLocation(0, heightProjectFrame); 
      
      // ---------------------------------------------------------------------------------
      // Frame n°3
      //   
      ProjectFrame p3 = this.application.getFrame().newProjectFrame();
      p3.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
      p3.setTitle("Liens d'appariement");
      l1 = p3.addUserLayer(datasetNetwork1.getPopulationsArcs().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      l2 = p3.addUserLayer(datasetNetwork2.getPopulationsArcs().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      /*Layer l3 = p3.addUserLayer(liens, "Liens", null);
      l3.getSymbolizer().getStroke().setColor(linkColor);
      l3.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);*/
      
      List<Double> valeursClassementL = new ArrayList<Double>();
      valeursClassementL.add(new Double(0.5));
      valeursClassementL.add(new Double(1));
      
      List<EnsembleDeLiens> liensClasses = liens.classeSelonSeuilEvaluation(valeursClassementL);
      EnsembleDeLiens liensNuls = liensClasses.get(0);
      EnsembleDeLiens liensIncertains = liensClasses.get(1);
      EnsembleDeLiens liensSurs = liensClasses.get(2);
      
      Layer link_ok_layer = p3.addUserLayer(liensSurs, "Liens sûrs", null);
      link_ok_layer.getSymbolizer().getStroke().setColor(linkColorOk);
      link_ok_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);
      
      Layer link_null_layer = p3.addUserLayer(liensNuls, "Liens nulls", null);
      link_null_layer.getSymbolizer().getStroke().setColor(linkColorNull);
      link_null_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);
      
      Layer link_doubtfull_layer = p3.addUserLayer(liensIncertains, "Liens incertains", null);
      link_doubtfull_layer.getSymbolizer().getStroke().setColor(linkColorDoubtfull);
      link_doubtfull_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);
      
      p3.setSize(widthProjectFrame, heightProjectFrame * 2);
      p3.setLocation(widthProjectFrame, 0);
      
      DisplayToolBarNetworkDataMatching resultToolBar = new DisplayToolBarNetworkDataMatching(p3, resultNetwork, 
          paramPlugin.getParamNetworkDataMatching());
      JMenuBar menuBar = new JMenuBar();
      p3.setJMenuBar(menuBar);   
      p3.getJMenuBar().add(resultToolBar, 0);
  
      // 
      LOGGER.info("Finished");
    }
  }

  /**
   * @param f The parameters XML Filename to set
   */
  public void setParamPlugin(ParamPluginNetworkDataMatching p) {
    paramPlugin = p;
  }
  
  public ParamPluginNetworkDataMatching getParamPlugin() {
    return paramPlugin;
  }

}
