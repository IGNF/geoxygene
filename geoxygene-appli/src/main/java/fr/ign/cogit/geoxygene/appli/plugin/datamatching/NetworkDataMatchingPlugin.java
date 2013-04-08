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
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.ShpFiles;

import com.vividsolutions.jts.geom.Geometry;

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
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

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

  /** Parameters. */
  private ParamNetworkDataMatching newParam = null;
  
  /** Displayed colors. */
  private final static Color linkColor = new Color(254, 107, 19);
  private final static Color network1Color = new Color(11, 73, 157);
  private final static Color network2Color = new Color(35, 140, 69);
  private final static Color matchedNetworkColor = new Color(136, 64, 153);
  private final static int LINE_WIDTH = 3;
  
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
    
    // Refresh menu of the application
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 1);
  }

  /**
   * 
   */
  @Override
  public void actionPerformed(final ActionEvent e) {
    
    // Launch parameter network data matching panel.
    EditParamPanel dialogParamDataMatchingNetwork = new EditParamPanel(this);
    
    if (dialogParamDataMatchingNetwork.getAction().equals("LAUNCH")) {
      
      // Replace parameters to demonstrateur parameters
      
      // Dataset  
      // IPopulation<IFeature> reseau1 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\1-RoadL_Paris_2000_extract.shp");
      // IPopulation<IFeature> reseau2 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\2-Streets_extract.shp");
      // IPopulation<IFeature> reseau1 = ShapefileReader.read("D:\\DATA\\Appariement\\MesTests\\T3\\bdcarto_route.shp");
      // IPopulation<IFeature> reseau2 = ShapefileReader.read("D:\\DATA\\Appariement\\MesTests\\T3\\bdtopo_route.shp");
      IPopulation<IFeature> reseau1 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau1.shp");
      IPopulation<IFeature> reseau2 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau2.shp");
      // IPopulation<IFeature> reseau1 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\Reseau\\ERM\\RoadL_Paris_2000.shp");
      // IPopulation<IFeature> reseau2 = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\Reseau\\Navstreets\\Streets.shp");
      
      ParamDatasetNetworkDataMatching paramDataset = new ParamDatasetNetworkDataMatching();
      paramDataset.addPopulationsArcs1(reseau1);
      paramDataset.addPopulationsArcs2(reseau2);
      newParam.setParamDataset(paramDataset);
      
      // Direction
      ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
      paramDirection.setPopulationsArcsAvecOrientationDouble(true);
      newParam.setParamDirection(paramDirection);
      
      // Distance
      ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
      float distanceNoeudsMax = 50; //(float) 0.001;
      paramDistance.setDistanceNoeudsMax(distanceNoeudsMax);
      paramDistance.setDistanceArcsMax(2 * distanceNoeudsMax);
      paramDistance.setDistanceArcsMin(distanceNoeudsMax);
      newParam.setParamDistance(paramDistance);
      
      // Parse new parameter object to old parameter
      ParametresApp paramOld = newParam.paramNDMToParamApp();
      
      paramOld.topologieGraphePlanaire1 = false;  // true
      paramOld.topologieFusionArcsDoubles1 = true;  // true
      paramOld.topologieSeuilFusionNoeuds1 = 0.1;  // 0.1
      
      paramOld.topologieGraphePlanaire2 = false;  // true
      paramOld.topologieFusionArcsDoubles2 = true;  // true
      paramOld.topologieSeuilFusionNoeuds2 = 0.1;  // 0.1
      
      paramOld.varianteFiltrageImpassesParasites = false;
      
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
      
      // paramOld.debugTirets = true;
      paramOld.debugBilanSurObjetsGeo = false;
      // paramOld.debugAffichageCommentaires = 1;
      
      // Log parameters
      LOGGER.info(newParam.toString());
      
      NetworkDataMatching networkDataMatching = new NetworkDataMatching(paramOld);
      ResultNetworkDataMatching resultatAppariement = networkDataMatching.networkDataMatching();
      
      // Logs
      LOGGER.info("Nb arcs du réseau 1 calculés : " + resultatAppariement.getReseau1().getListeArcs().size()
          + " >= " + reseau1.size());
      LOGGER.info("Nb arcs du réseau 2 calculés : " + resultatAppariement.getReseau2().getListeArcs().size()
          + " >= " + reseau2.size());
      
      EnsembleDeLiens liens = resultatAppariement.getLinkDataSet();
      ResultNetworkStat resultNetwork = resultatAppariement.getResultStat();
      
      // Debug
      // Liens
      
      // Log stats
      LOGGER.info(resultNetwork.getStatsEdgesOfNetwork1().toString());
      LOGGER.info(resultNetwork.getStatsNodesOfNetwork1().toString());
      LOGGER.info(resultNetwork.getStatsEdgesOfNetwork2().toString());
      LOGGER.info(resultNetwork.getStatsNodesOfNetwork2().toString());
      
      // Recalage
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseau1(), 
          resultatAppariement.getReseau2(), liens);
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();
      LOGGER.info(arcs.getNom());
  
      // Qu'est-ce que ca fait ??
      int nb_1_1 = 0;
      int nb_1_n = 0;
      int nb_n_1 = 0;
      int nb_0_0 = 0;
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
        
        // On compte les types de liens
        if (lien.getObjetsRef().size() < 1 && lien.getObjetsComp().size() < 1) {
          nb_0_0++;
        }
        if (lien.getObjetsRef().size() == 1 && lien.getObjetsComp().size() == 1) {
          nb_1_1++;
        }
        if (lien.getObjetsRef().size() == 1 && lien.getObjetsComp().size() > 1) {
          nb_1_n++;
        }
        if (lien.getObjetsRef().size() == 1 && lien.getObjetsComp().size() > 1) {
          nb_n_1++;
        }
      }
  
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Taille popRef = " + newParam.getParamDataset().getPopulationsArcs1().get(0).size());
      LOGGER.trace("Taille popComp = " + newParam.getParamDataset().getPopulationsArcs2().get(0).size());
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.info("Nb de liens 0-0 = " + nb_0_0);
      LOGGER.info("Nb de liens 1-1 = " + nb_1_1);
      LOGGER.info("Nb de liens 1-N = " + nb_1_n);
      LOGGER.info("Nb de liens N-1 = " + nb_n_1);
      LOGGER.trace("----------------------------------------------------------");
  
      // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
      
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Enregistrement des résultats en fichier shape");
      
      ShapefileWriter.write(arcs, "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\reseau1-Apparie.shp");
      ShapefileWriter.write(liens, "D:\\Data\\Appariement\\ESPON_DB\\Reseau\\liens.shp");
      
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-ArcTopo.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-NoeudTopo.shp");
      
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-ArcTopo.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-NoeudTopo.shp");
      
      LOGGER.trace("----------------------------------------------------------");
      
      
      this.application.getFrame().getDesktopPane().removeAll();
      
      Dimension desktopSize = this.application.getFrame().getDesktopPane().getSize();
      int widthProjectFrame = desktopSize.width / 2;
      int heightProjectFrame = desktopSize.height / 2;
  
      // Frame n°1
      ProjectFrame p1 = this.application.getFrame().newProjectFrame();
      p1.setTitle("Reseau 1 + reseau 2");
      Layer l1 = p1.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l2 = p1.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      p1.setSize(widthProjectFrame, heightProjectFrame);
      p1.setLocation(0, 0);
      Viewport viewport = p1.getLayerViewPanel().getViewport();
  
      ProjectFrame p2 = this.application.getFrame().newProjectFrame();
      p2.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
      p2.setTitle("Reseau 1 après recalage");
      l1 = p2.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      l2 = p2.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l1bis = p2.addUserLayer(arcs, "Reseau 1 recale", null);
      l1bis.getSymbolizer().getStroke().setColor(matchedNetworkColor);
      l1bis.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      p2.setSize(widthProjectFrame, heightProjectFrame);
      p2.setLocation(0, heightProjectFrame); 
      
      ProjectFrame p3 = this.application.getFrame().newProjectFrame();
      p3.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
      p3.setTitle("Liens d'appariement");
      l1 = p3.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      l2 = p3.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      Layer l3 = p3.addUserLayer(liens, "Liens", null);
      l3.getSymbolizer().getStroke().setColor(linkColor);
      l3.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      p3.setSize(widthProjectFrame, heightProjectFrame * 2);
      p3.setLocation(widthProjectFrame, 0);
      
      DisplayToolBarNetworkDataMatching resultToolBar = new DisplayToolBarNetworkDataMatching(p3, resultNetwork, newParam);
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
  public void setNewParam(ParamNetworkDataMatching p) {
    newParam = p;
  }

}
