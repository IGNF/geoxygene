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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamFilenamePopulationEdgesNetwork;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui.DisplayToolBarNetworkDataMatching;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui.EditParamPanel;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.DatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamProjectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopologyTreatmentNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
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

  /** Parameters and datasets. */
  private ParamPluginNetworkDataMatching paramPlugin = new ParamPluginNetworkDataMatching();
  private DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
  private DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
  
  /** Displayed colors. */
  private final static Color nodeColor = new Color(255, 216, 0);
  private final static Color liensGeneriquesColor = new Color(44, 66, 71);
  
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
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 2);
  }
  
  /**
   * Initialize default parameters from an XML files.
   */
  private void initializeParam() {

    // Filename
    // 
    // String filename1 = ParamParserTest.class.getClassLoader().getResource("data/reseau.shp").getPath();
    // String filename1 = application.getProperties().getLastOpenedFile();
    // String filename1 = "D:\\Data\\Appariement\\MesTests\\T3\\bdcarto_route.shp";
    // String filename1 = "D:\\Data\\Appariement\\Kusay\\CGDep84_extraction.shp";
    String filename1 = "D:\\Data\\Appariement\\Kusay\\GGraph2.shp";
      
    ParamFilenamePopulationEdgesNetwork paramFilename1 = new ParamFilenamePopulationEdgesNetwork();
    paramFilename1.addFilename(filename1);
    paramPlugin.setParamFilenameNetwork1(paramFilename1);
    
    // String filename2 = "D:\\Data\\Appariement\\MesTests\\T3\\bdtopo_route.shp";
    // String filename2 = "D:\\Data\\Appariement\\Kusay\\RIUV1BDCARTO_extractionV2.shp";
    String filename2 = "D:\\Data\\Appariement\\Kusay\\RIUV1BDCARTO_extraction.shp";
    ParamFilenamePopulationEdgesNetwork paramFilename2 = new ParamFilenamePopulationEdgesNetwork();
    paramFilename2.addFilename(filename2);
    paramPlugin.setParamFilenameNetwork2(paramFilename2);
    
    // -----------------------------------------------------------------------------------------
    // Param
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    
    // Direction
    ParamDirectionNetworkDataMatching paramDirection1 = new ParamDirectionNetworkDataMatching();
    paramDirection1.setOrientationDouble(true);
    param.setParamDirectionNetwork1(paramDirection1);
    
    ParamDirectionNetworkDataMatching paramDirection2 = new ParamDirectionNetworkDataMatching();
    paramDirection2.setOrientationDouble(true);
    /*paramDirection2.setAttributOrientation("sens_de_circulation");
    Map<Integer, String> orientationMap2 = new HashMap<Integer, String>();
    orientationMap2.put(OrientationInterface.SENS_DIRECT, "Sens direct");
    orientationMap2.put(OrientationInterface.SENS_INVERSE, "Sens inverse");
    orientationMap2.put(OrientationInterface.DOUBLE_SENS, "Double sens");
    paramDirection2.setOrientationMap(orientationMap2);*/
    param.setParamDirectionNetwork2(paramDirection2);
    
    // Distance
    ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
    float distanceNoeudsMax = 20;
    paramDistance.setDistanceNoeudsMax(distanceNoeudsMax);
    paramDistance.setDistanceArcsMax(2 * distanceNoeudsMax);
    paramDistance.setDistanceArcsMin(distanceNoeudsMax);
    param.setParamDistance(paramDistance);
    
    // Topologie
    ParamTopologyTreatmentNetwork paramTopo1 = new ParamTopologyTreatmentNetwork();
    paramTopo1.setGraphePlanaire(true);
    paramTopo1.setFusionArcsDoubles(true);
    paramTopo1.setSeuilFusionNoeuds(0.1);
    param.setParamTopoNetwork1(paramTopo1);
    
    ParamTopologyTreatmentNetwork paramTopo2 = new ParamTopologyTreatmentNetwork();
    paramTopo2.setGraphePlanaire(false);
    paramTopo2.setFusionArcsDoubles(false);
    // paramTopo2.setSeuilFusionNoeuds(0.1);
    param.setParamTopoNetwork1(paramTopo2);
    
    // Projection
    ParamProjectionNetworkDataMatching paramProj1 = new ParamProjectionNetworkDataMatching();
    paramProj1.setProjeteNoeuds1SurReseau2(true);
    paramProj1.setProjeteNoeuds1SurReseau2DistanceNoeudArc(distanceNoeudsMax);
    paramProj1.setProjeteNoeuds1SurReseau2DistanceProjectionNoeud(2 * distanceNoeudsMax);
    param.setParamProjNetwork1(paramProj1);
    
    ParamProjectionNetworkDataMatching paramProj2 = new ParamProjectionNetworkDataMatching();
    paramProj2.setProjeteNoeuds1SurReseau2(true);
    paramProj2.setProjeteNoeuds1SurReseau2DistanceNoeudArc(distanceNoeudsMax);
    paramProj2.setProjeteNoeuds1SurReseau2DistanceProjectionNoeud(2 * distanceNoeudsMax);
    paramProj2.setProjeteNoeuds1SurReseau2ImpassesSeulement(true);
    param.setParamProjNetwork2(paramProj2);
    
    //
    paramPlugin.setParamNetworkDataMatching(param);
    
    // -----------------------------------------------------------------------------------------
    // Actions
    paramPlugin.setDoRecalage(true);
  
    
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
      String lastFile = "";
      for (int i = 0; i < paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().size(); i++) {
        String filename = paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().get(i);
        lastFile = filename;
        IPopulation<IFeature> reseau = ShapefileReader.read(filename);
        datasetNetwork1.addPopulationsArcs(reseau);
      }
      for (int i = 0; i < paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().size(); i++) {
        String filename = paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().get(i);
        lastFile = filename;
        IPopulation<IFeature> reseau = ShapefileReader.read(filename);
        datasetNetwork2.addPopulationsArcs(reseau);
      }

      // Set last Opened file
      application.getProperties().setLastOpenedFile(lastFile);
      MainFrame.getFilechooser().setPreviousDirectory(new File(lastFile));
      application.getProperties().marshall(application.getPropertiesFile().getFile());
      
      // -------------------------------------------------------------------------------
      
      // Parse new parameter object to old parameter
      // ParametresApp paramOld = paramPlugin.getParamNetworkDataMatching().paramNDMToParamApp();
      // paramOld.populationsArcs1 = datasetNetwork1.getPopulationsArcs();
      // paramOld.populationsArcs2 = datasetNetwork2.getPopulationsArcs();
      
      // paramOld.varianteFiltrageImpassesParasites = false;
      
      // moins detaille
      // paramOld.varianteForceAppariementSimple = false;
      // paramOld.varianteRedecoupageArcsNonApparies = false;
      
      // paramOld.debugTirets = true;
      // paramOld.debugBilanSurObjetsGeo = true;
      // paramOld.debugAffichageCommentaires = 1;
      
      // Log parameters
      // LOGGER.info(paramPlugin.toString());
      
      NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(paramPlugin.getParamNetworkDataMatching(),
          datasetNetwork1, datasetNetwork2);
      networkDataMatchingProcess.setActions(paramPlugin.getDoRecalage(), paramPlugin.getDoLinkExport());
      ResultNetworkDataMatching resultatAppariement = networkDataMatchingProcess.networkDataMatching();
      
      // Logs
      LOGGER.info("Nb arcs du réseau 1 calculés : " + resultatAppariement.getReseauStat1().getReseauApp().getListeArcs().size()
          + " >= " + datasetNetwork1.getPopulationsArcs().size());
      LOGGER.info("Nb arcs du réseau 2 calculés : " + resultatAppariement.getReseauStat2().getReseauApp().getListeArcs().size()
          + " >= " + datasetNetwork2.getPopulationsArcs().size());
      
      EnsembleDeLiens liens = resultatAppariement.getLiens();
      // Recalage
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseauStat1().getReseauApp(), resultatAppariement.getReseauStat2().getReseauApp(), liens);
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();
      
      // Statistic information
      ResultNetworkStat resultNetwork = resultatAppariement.getResultStat();
      LOGGER.debug(resultNetwork.getStatsEdgesOfNetwork1().toString());
      LOGGER.debug(resultNetwork.getStatsNodesOfNetwork1().toString());
      LOGGER.debug(resultNetwork.getStatsEdgesOfNetwork2().toString());
      LOGGER.debug(resultNetwork.getStatsNodesOfNetwork2().toString());
  
      // Split les multi des liens
      Population<DefaultFeature> collection = new Population<DefaultFeature>();
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
      newFeatureType.setTypeName("aaa");
      newFeatureType.setGeometryType(GM_Polygon.class);
      SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
      schemaDefaultFeature.setNom("aaa");
      schemaDefaultFeature.setNomSchema("aaa");
      schemaDefaultFeature.setFeatureType(newFeatureType);
      collection.setFeatureType(newFeatureType);
      
      for (Lien lien : liens) {
        IGeometry geom = lien.getGeom();
        if (geom instanceof GM_Aggregate<?>) {
          GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
          for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
            if (lineGeom instanceof GM_LineString) {
              multiCurve.add((GM_LineString) lineGeom);
            } else if (lineGeom instanceof GM_MultiCurve<?>) {
                multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
            } else if (lineGeom instanceof GM_Polygon) {
                DefaultFeature defaultFeature = new DefaultFeature();
                
                defaultFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
                defaultFeature.setSchema(schemaDefaultFeature);
                defaultFeature.setGeom((GM_Polygon)lineGeom);
                collection.add(defaultFeature);
            } 
          }
          lien.setGeom(multiCurve);
        }
      }
      
      // Liens generiques
      /*Population<DefaultFeature> collection2 = new Population<DefaultFeature>();
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType2 = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
      newFeatureType2.setTypeName("aaa");
      newFeatureType2.setGeometryType(GM_LineString.class);
      SchemaDefaultFeature schemaDefaultFeature2 = new SchemaDefaultFeature();
      schemaDefaultFeature2.setNom("aaa");
      schemaDefaultFeature2.setNomSchema("aaa");
      schemaDefaultFeature2.setFeatureType(newFeatureType2);
      collection.setFeatureType(newFeatureType2);
      collection2.setFeatureType(newFeatureType2);*/
      
      EnsembleDeLiens liensGeneriques = resultatAppariement.getLiensGeneriques();
      LOGGER.trace("Liens generiques = " + liensGeneriques.size());
      for (Lien lien : liensGeneriques) {
          
          /*DefaultFeature defaultFeature = new DefaultFeature();
          defaultFeature.setFeatureType(schemaDefaultFeature2.getFeatureType());
          defaultFeature.setSchema(schemaDefaultFeature2);
          defaultFeature.setGeom(lien.getGeom());*/
          
          LOGGER.trace("Objets reseau 1 comme chaine = " + lien.getObjetsRefAsString());
          LOGGER.trace("Objets reseau 2 comme chaine = " + lien.getCorrespondantsAsString());
          
          // On ajoute l'attribut "nature" qui vaut i*0.05 et qui est de type "double"
          // AttributeManager.addAttribute(defaultFeature, "evaluation", "0.5", "Double");
          // AttributeManager.addAttribute(defaultFeature, "objetsRef", "44", "String");
          // AttributeManager.addAttribute(defaultFeature, "objetsComp", "33", "String");
          
          /*List<GF_AttributeType> listAttributs = lien.getFeatureType().getFeatureAttributes();
          AttributeType aT = new AttributeType();
          aT.setMemberName("objetsRef");
          aT.setNomField("objetsRef");
          aT.setValueType("String");
          listAttributs.add(aT);
          lien.getFeatureType().setFeatureAttributes(listAttributs);
          
          LOGGER.trace("Nb d'attributs = " + lien.getFeatureType().getFeatureAttributes().size());
          for (GF_AttributeType attribute : lien.getFeatureType().getFeatureAttributes()) {
              String nomAttribut = attribute.getMemberName();
              String value = attribute.
              LOGGER.trace(nomAttribut);
          }*/
          
          // collection2.add(defaultFeature);
      }
  
      LOGGER.trace("----------------------------------------------------------");
      LOGGER.trace("Enregistrement des résultats en fichier shape");
      // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
      
      // String repResultat = "D:\\Data\\Appariement\\MesTests\\EXTRAITS-GPS\\R4\\";
      // ShapefileWriter.write(arcs, repResultat + "ReseauApparie.shp");
      // ShapefileWriter.write(liens, repResultat + "Liens.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopArcs(), repResultat + "ArcTopoReseau1.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau1().getPopNoeuds(), repResultat + "NoeudTopoReseau1.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopArcs(), repResultat + "ArcTopoReseau2.shp");
      // ShapefileWriter.write(resultatAppariement.getReseau2().getPopNoeuds(), repResultat + "NoeudTopoReseau2.shp");
      
      LOGGER.trace("----------------------------------------------------------");
      
      // A garder ??
      // this.application.getFrame().getDesktopPane().removeAll();
      
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
      LOGGER.info("Nombre d'arcs de la carte topo n°1 = " + resultatAppariement.getReseauStat1().getReseauApp().getListeArcs().size());
      LOGGER.info("Nombre de noeuds de la carte topo n°1 = " + resultatAppariement.getReseauStat1().getReseauApp().getListeNoeuds().size());
      
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
      
      Layer l1bis = p2.addUserLayer(arcs, "Réseau 1 recale", null);
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
      
      l1 = p3.addUserLayer(resultatAppariement.getReseauStat1().getReseauApp().getPopArcs(), "CT 1 - Arcs", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      
      Layer lp1 = p3.addUserLayer(resultatAppariement.getReseauStat1().getReseauApp().getPopNoeuds(), "CT 1 - Noeuds", null);
      Layer lp2 = p3.addUserLayer(resultatAppariement.getReseauStat2().getReseauApp().getPopNoeuds(), "CT 2 - Noeuds", null);
      // lp1.getSymbolizer().getStroke().setColor(network1Color);
      // lp1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      
      l2 = p3.addUserLayer(resultatAppariement.getReseauStat2().getReseauApp().getPopArcs(), "CT 2 - Arcs", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      
      /*Layer l3 = p3.addUserLayer(liens, "Liens", null);
      l3.getSymbolizer().getStroke().setColor(linkColorDoubtfull);
      l3.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);*/
      
      Layer l4 = p3.addUserLayer(collection, "Liens noeuds", null);
      l4.getSymbolizer().getStroke().setColor(nodeColor);
      l4.getSymbolizer().getStroke().setStrokeOpacity(new Float(0.4));
      
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
      
      DisplayToolBarNetworkDataMatching resultToolBar = new DisplayToolBarNetworkDataMatching(p3, resultatAppariement, 
          paramPlugin.getParamNetworkDataMatching());
      JMenuBar menuBar = new JMenuBar();
      p3.setJMenuBar(menuBar);   
      p3.getJMenuBar().add(resultToolBar, 0);
      
      // ---------------------------------------------------------------------------------
      // Frame n°4
      //    
      ProjectFrame p4 = this.application.getFrame().newProjectFrame();
      p4.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p4.getLayerViewPanel());
      p4.setTitle("Export liens");
      
      l1 = p4.addUserLayer(datasetNetwork1.getPopulationsArcs().get(0), "Réseau 1", null);
      l1.getSymbolizer().getStroke().setColor(network1Color);
      l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      l2 = p4.addUserLayer(datasetNetwork2.getPopulationsArcs().get(0), "Réseau 2", null);
      l2.getSymbolizer().getStroke().setColor(network2Color);
      l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
      
      Layer l5 = p4.addUserLayer(liensGeneriques, "Liens generiques", null);
      // Layer l5 = p4.addUserLayer(collection2, "Liens generiques", null);
      l5.getSymbolizer().getStroke().setColor(liensGeneriquesColor);
      l5.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH * 3);
  
      p4.setSize(widthProjectFrame, heightProjectFrame);
      p4.setLocation(widthProjectFrame / 2, heightProjectFrame / 2);
      
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
