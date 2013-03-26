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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

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
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
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
      
      // Direction
      ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
      paramDirection.setPopulationsArcsAvecOrientationDouble(true);
      newParam.setParamDirection(paramDirection);
      
      // Distance
      ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
      float distanceNoeudsMax = 30;
      paramDistance.setDistanceNoeudsMax(distanceNoeudsMax);
      paramDistance.setDistanceArcsMax(2 * distanceNoeudsMax);
      paramDistance.setDistanceArcsMin(distanceNoeudsMax);
      newParam.setParamDistance(paramDistance);
      
      // Parse new parameter object to old parameter
      ParametresApp paramOld = newParam.paramNDMToParamApp();
      
      paramOld.topologieGraphePlanaire1 = true;
      paramOld.topologieFusionArcsDoubles1 = true;
      paramOld.topologieSeuilFusionNoeuds1 = 0.1;
      
      paramOld.topologieGraphePlanaire2 = true;
      paramOld.topologieFusionArcsDoubles2 = true;
      paramOld.topologieSeuilFusionNoeuds2 = 0.1;
      
      paramOld.varianteFiltrageImpassesParasites = false;
      
      paramOld.projeteNoeuds1SurReseau2 = true;
      paramOld.projeteNoeuds1SurReseau2DistanceNoeudArc = distanceNoeudsMax;
      paramOld.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 2 * distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1 = true;
      paramOld.projeteNoeuds2SurReseau1DistanceNoeudArc = distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 2 * distanceNoeudsMax;
      paramOld.projeteNoeuds2SurReseau1ImpassesSeulement = false;
      
      // moins detaille
      paramOld.varianteForceAppariementSimple = false;
      paramOld.varianteRedecoupageArcsNonApparies = false;
      
      // paramOld.debugTirets = true;
      // paramOld.debugBilanSurObjetsGeo = true;
      // paramOld.debugAffichageCommentaires = 1;
      
      // Log parameters
      LOGGER.info(newParam.toString());
      
      NetworkDataMatching networkDataMatching = new NetworkDataMatching(paramOld);
      ResultNetworkDataMatching resultatAppariement = networkDataMatching.networkDataMatching();
      EnsembleDeLiens liens = resultatAppariement.getLinkDataSet();
      ResultNetworkStat resultNetwork = resultatAppariement.getResultStat();
      // Log stats
      LOGGER.info(resultNetwork.getEdgesStatNetwork1().toString());
      LOGGER.info(resultNetwork.getNodesEvaluationRef().toString());
      LOGGER.info(resultNetwork.getEdgesEvaluationComp().toString());
      LOGGER.info(resultNetwork.getNodesEvaluationComp().toString());
      
      // Recalage
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseau1(), 
          resultatAppariement.getReseau2(), liens);
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();
      LOGGER.info(arcs.getNom());
  
      // Qu'est-ce que ca fait ??
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
      LOGGER.info(arcs.getNom());
  
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Taille popRef = " + newParam.getParamDataset().getPopulationsArcs1().get(0).size());
      LOGGER.trace("Taille popComp = " + newParam.getParamDataset().getPopulationsArcs2().get(0).size());
      LOGGER.trace("----------------------------------------------------------");
  
      // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
      
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Enregistrement des résultats en fichier shape");
      ShapefileWriter.write(arcs, "D:\\Data\\Appariement\\SDET\\Res\\SDET-Apparie.shp");
      ShapefileWriter.write(liens, "D:\\Data\\Appariement\\SDET\\Res\\SDET-Liens.shp");
      
      ShapefileWriter.write(resultatAppariement.getReseau1().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-ArcTopo.shp");
      ShapefileWriter.write(resultatAppariement.getReseau1().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\SDET-NoeudTopo.shp");
      
      ShapefileWriter.write(resultatAppariement.getReseau2().getPopArcs(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-ArcTopo.shp");
      ShapefileWriter.write(resultatAppariement.getReseau2().getPopNoeuds(), "D:\\Data\\Appariement\\SDET\\Res\\BDUni-NoeudTopo.shp");
      
      LOGGER.trace("----------------------------------------------------------");
      
      
      this.application.getFrame().getDesktopPane().removeAll();
      
      Dimension desktopSize = this.application.getFrame().getDesktopPane().getSize();
      int widthProjectFrame = desktopSize.width / 2;
      int heightProjectFrame = desktopSize.height / 2;
      
      // SLD
      // StyledLayerDescriptor sld = StyledLayerDescriptor
      //    .unmarshall("./src/main/resources/sld/appariementSLD.xml");
      // System.out.println("SLD, nombre de layers = " + sld.getLayers().size());
  
      // Frame n°1
      ProjectFrame p1 = this.application.getFrame().newProjectFrame();
      p1.setTitle("Reference Pop + Comparaison Pop");
      // Layer popRef
      // Layer l = sld.getLayer("popRef");
      // p1.setSld(sld);
      p1.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "1 - Utilisateur", null);
      p1.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "2 - BDUni", null);
      p1.addUserLayer(resultatAppariement.getReseau1().getPopArcs(), "Arcs utilisateur 1", null);
      p1.addUserLayer(resultatAppariement.getReseau1().getPopNoeuds(), "Noeuds utilisateur 1", null);
      p1.addUserLayer(resultatAppariement.getReseau2().getPopArcs(), "Arcs BDUni 2", null);
      p1.addUserLayer(resultatAppariement.getReseau2().getPopNoeuds(), "Noeuds BDUni 2", null);
      p1.setSize(widthProjectFrame, heightProjectFrame);
      p1.setLocation(0, 0);
      Viewport viewport = p1.getLayerViewPanel().getViewport();
  
      ProjectFrame p3 = this.application.getFrame().newProjectFrame();
      p3.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
      p3.setTitle("Corrected Pop après recalage"); //$NON-NLS-1$
      p3.addUserLayer(arcs, "Utilisateur recale", null);
      p3.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "Utilisateur brut", null);
      p3.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "2 - BDUni", null);
      p3.setSize(widthProjectFrame, heightProjectFrame);
      p3.setLocation(0, heightProjectFrame); 
      
      ProjectFrame p4 = this.application.getFrame().newProjectFrame();
      p4.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p4.getLayerViewPanel());
      p4.setTitle("Links"); //$NON-NLS-1$
      p4.addUserLayer(newParam.getParamDataset().getPopulationsArcs1().get(0), "1 - Utilisateur", null);
      p4.addUserLayer(newParam.getParamDataset().getPopulationsArcs2().get(0), "2 - BDUni", null);
      Layer layer = p4.addUserLayer(liens, "Liens", null);
      layer.getSymbolizer().getStroke().setStrokeWidth(2);
      // p4.addUserLayer(((LienApp)liens.getElements())., "2 - BDUni", null);
      p4.setSize(widthProjectFrame, heightProjectFrame * 2);
      p4.setLocation(widthProjectFrame, 0);
      
      DisplayToolBarNetworkDataMatching resultToolBar = new DisplayToolBarNetworkDataMatching(p4, resultNetwork, newParam);
      JMenuBar menuBar = new JMenuBar();
      p4.setJMenuBar(menuBar);   
      p4.getJMenuBar().add(resultToolBar, 0);
  
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
