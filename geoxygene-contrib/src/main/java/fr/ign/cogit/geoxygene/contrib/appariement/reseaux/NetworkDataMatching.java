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
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.DatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopologyTreatmentNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 * 
 *
 */
public class NetworkDataMatching {
  
  /** logger. */
  private static final Logger LOGGER = Logger.getLogger(NetworkDataMatching.class.getName());

  /** Parameters, Dataset and Actions. */
  private ParamNetworkDataMatching inputParam;
  private DatasetNetworkDataMatching dataset1;
  private DatasetNetworkDataMatching dataset2;
  private boolean doRecalage;
  
  /**
   * Constructor.
   * @param paramApp
   */
  public NetworkDataMatching(ParamNetworkDataMatching param, DatasetNetworkDataMatching network1, DatasetNetworkDataMatching network2,
      boolean doRecalage) {
    this.inputParam = param;
    this.dataset1 = network1;
    this.dataset2 = network2;
    this.doRecalage = doRecalage;
  }
  
  /**
   * Appariement de réseaux.
   * @param paramApp, les paramètres de l'appariement
   * @return
   */
  public ResultNetworkDataMatching networkDataMatching() {
    
    // Temporaire
    ParametresApp paramApp = inputParam.paramNDMToParamApp();
    // + Dataset
    paramApp.populationsArcs1 = dataset1.getPopulationsArcs();
    paramApp.populationsArcs2 = dataset2.getPopulationsArcs();
    
    /*List<ReseauApp> reseaux = new ArrayList<ReseauApp>();
    ResultNetworkDataMatching resultNetworkDataMatching = AppariementIO.appariementDeJeuxGeo2(paramApp, reseaux);
    resultNetworkDataMatching.setReseau1(reseaux.get(0));
    resultNetworkDataMatching.setReseau2(reseaux.get(1));
    
    return resultNetworkDataMatching;*/
    
    // For result
    ResultNetworkDataMatching resultatAppariement = new ResultNetworkDataMatching();
    
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("------------------------------------------------------------------");
      LOGGER.info("NETWORK MATCHING START");
      LOGGER.info("1 = least detailled data;");
      LOGGER.info("2 = most detailled data");
      LOGGER.info("");
    }
    
    // ---------------------------------------------------------------------
    // Organisation des données en réseau et prétraitements topologiques
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("DATA STRUCTURING");
      LOGGER.info("Topological structuring");
    }
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      LOGGER.debug("START OF STRUCTURING " + (new Time(System.currentTimeMillis())).toString());
    }
    
    // 
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      LOGGER.debug("creation of network 1 " + (new Time(System.currentTimeMillis())).toString());
    }
    ReseauApp reseau1 = importData("Réseau 1", dataset1,
        inputParam.getParamDirectionNetwork1(),
        inputParam.getParamDistance().getDistanceNoeudsMax(),
        inputParam.getParamTopoNetwork1());
    
    // 
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      LOGGER.debug("creation of network 2 " + (new Time(System.currentTimeMillis())).toString());
    }
    ReseauApp reseau2 = importData("Réseau 2", dataset2,
        inputParam.getParamDirectionNetwork2(),
        inputParam.getParamDistance().getDistanceNoeudsMax(),
        inputParam.getParamTopoNetwork2());
    
    //    resultatAppariement.setReseau1(reseau1);
    //    resultatAppariement.setReseau2(reseau2);
    //    if (true) return resultatAppariement;
    
    // ---------------------------------------------------------------------------------------------
    // NB: l'ordre dans lequel les projections sont faites n'est pas neutre
    if (inputParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2()) {
    // if (paramApp.projeteNoeuds2SurReseau1) {
      if (LOGGER.isEnabledFor(Level.DEBUG)) {
        LOGGER.debug("Projection of network 2 onto network1 " + (new Time(System.currentTimeMillis())).toString());
      }
      //reseau1.projete(reseau2,
      //    paramApp.projeteNoeuds2SurReseau1DistanceNoeudArc,
      //    paramApp.projeteNoeuds2SurReseau1DistanceProjectionNoeud,
      //    paramApp.projeteNoeuds2SurReseau1ImpassesSeulement);
      reseau1.projete(reseau2,
          inputParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceNoeudArc(),
          inputParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(),
          inputParam.getParamProjNetwork2().getProjeteNoeuds1SurReseau2ImpassesSeulement());
    }
    // if (paramApp.projeteNoeuds1SurReseau2) {
    if (inputParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2()) {
      if (LOGGER.isEnabledFor(Level.DEBUG)) {
        LOGGER.debug("Projection of network 1 onto network2 " + (new Time(System.currentTimeMillis())).toString());
      }
      //reseau2.projete(reseau1,
      //    paramApp.projeteNoeuds1SurReseau2DistanceNoeudArc,
      //    paramApp.projeteNoeuds1SurReseau2DistanceProjectionNoeud,
      //    paramApp.projeteNoeuds1SurReseau2ImpassesSeulement);
      reseau2.projete(reseau1,
          inputParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceNoeudArc(),
          inputParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2DistanceProjectionNoeud(),
          inputParam.getParamProjNetwork1().getProjeteNoeuds1SurReseau2ImpassesSeulement());
    }
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      LOGGER.debug("Filling of edges and nodes attributes " + (new Time(System.currentTimeMillis())).toString());
    }
    reseau1.instancieAttributsNuls(inputParam.getParamDistance().getDistanceNoeudsMax());
    reseau2.initialisePoids();
    
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("Data Structuring finished : ");
      LOGGER.info("network 1 : "
          + reseau1.getPopArcs().size() + " Edges, "
          + reseau1.getPopNoeuds().size() + " Nodes.");
      LOGGER.info("network 2 : "
          + reseau2.getPopArcs().size() + " Edges, "
          + reseau2.getPopNoeuds().size() + " Nodes.");
    }
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      LOGGER.debug("END OF STRUCTURING " + new Time(System.currentTimeMillis()).toString());
    }
    
    // --------------------------------------------------------------------------------------
    // APPARIEMENT
    // --------------------------------------------------------------------------------------
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info(""); 
      LOGGER.info("NETWORK MATCHING");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("    NETWORK MATCHING START");
    }
    resultatAppariement = Appariement.appariementReseaux(reseau1, reseau2, paramApp);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("    Network Matching finished");
      LOGGER.info("  " + resultatAppariement.getLiens().size() + "matching links found");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("    END OF NETWORK MATCHING");
    }
    
    // --------------------------------------------------------------------------------------
    // EXPORT
    // --------------------------------------------------------------------------------------
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("");
      LOGGER.info("ASSESSMENT AND RESULT EXPORT");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("START OF EXPORT ");
    }
    
    // Avant le nettoyage des liens ou maintenant ???
    resultatAppariement.setReseau1(reseau1);
    resultatAppariement.setReseau2(reseau2);
    
    // if (paramApp.debugBilanSurObjetsGeo) {
    /*if (doRecalage) {
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Transformation of matching links to generic links");
      }
      
      EnsembleDeLiens liensGeneriques = LienReseaux.exportLiensAppariement(resultatAppariement.getLiens(), reseau1, paramApp);
      Appariement.nettoyageLiens(reseau1, reseau2);
      resultatAppariement.setLiensGeneriques(liensGeneriques);
      
    } */
    
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Link geometry assignment");
    }
    LienReseaux.exportAppCarteTopo(resultatAppariement.getLiens(), paramApp);
        
    if (LOGGER.isInfoEnabled()) {
        LOGGER.info("######## NETWORK MATCHING END #########");
    }
    
    
    
    // 
    return resultatAppariement;
      
  }
  
  
  /**
   * Création d'une carte topo à partir des objets Géographiques initiaux.
   * 
   * @return Le réseau créé
   */
  public static ReseauApp importData(String networkName,
      DatasetNetworkDataMatching network1, ParamDirectionNetworkDataMatching direction,
      float distanceNoeudsMax, ParamTopologyTreatmentNetwork topo) {
    
    boolean populationsArcsAvecOrientationDouble = direction.getOrientationDouble();
    String attributOrientation = direction.getAttributOrientation();
    Map<Integer, String> orientationMap = direction.getOrientationMap();
    
    boolean topologieGraphePlanaire = topo.getGraphePlanaire();
    double topologieSeuilFusionNoeuds = topo.getSeuilFusionNoeuds();
    IPopulation<?> topologieSurfacesFusionNoeuds = topo.getSurfacesFusionNoeuds();
    boolean topologieElimineNoeudsAvecDeuxArcs = topo.getElimineNoeudsAvecDeuxArcs();
    boolean topologieFusionArcsDoubles = topo.getFusionArcsDoubles();
    
    ReseauApp reseau = new ReseauApp(networkName);
    IPopulation<? extends IFeature> popArcApp = reseau.getPopArcs();
    IPopulation<? extends IFeature> popNoeudApp = reseau.getPopNoeuds();
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    
    // /////////////////////////
    // import des arcs
    List<IFeatureCollection<? extends IFeature>> populationsArcs = network1.getPopulationsArcs();
    Iterator<IFeatureCollection<? extends IFeature>> itPopArcs = null;
    itPopArcs = populationsArcs.iterator();
    LOGGER.info(populationsArcs.size() + " pops");
    
    
    LOGGER.info("Import des " + itPopArcs + " populations d'arcs.");
    while (itPopArcs.hasNext()) {
      
      IFeatureCollection<? extends IFeature> popGeo = itPopArcs.next();
      LOGGER.info(popGeo.size() + " objects");
      // import d'une population d'arcs
      for (IFeature element : popGeo) {
        
        ArcApp arc = (ArcApp) popArcApp.nouvelElement();
        ILineString ligne = new GM_LineString((IDirectPositionList) element.getGeom().coord().clone());
        arc.setGeometrie(ligne);
        
        if (populationsArcsAvecOrientationDouble) {
          arc.setOrientation(2);
        } else {
          String attribute = attributOrientation;
          if (attribute.isEmpty()) {
            arc.setOrientation(1);
            LOGGER.info("Populations avec orientation simple");
          } else {
            Object value = element.getAttribute(attribute);
            // System.out.println(attribute + " = " + value);
            if (orientationMap != null) {
              for (int mapKey : orientationMap.keySet()) {
                String valAttribut = orientationMap.get(mapKey);
                if (valAttribut.equals(value)) {
                  arc.setOrientation(mapKey);
                  // System.out.println(attribute + " = " + value + " ? " + valAttribut);
                }
              }
              /*Integer orientation = orientationMap.get(value);
              if (orientation != null) {
                arc.setOrientation(orientation.intValue());
              }*/
            } else {
              if (value instanceof Number) {
                Number v = (Number) value;
                arc.setOrientation(v.intValue());
              } else {
                if (value instanceof String) {
                  String v = (String) value;
                  try {
                    arc.setOrientation(Integer.parseInt(v));
                  } catch (Exception e) {
                    // FIXME Pretty specfific to BDTOPO Schema... no time to
                    // make it better
                    if (v.equalsIgnoreCase("direct")) {
                      arc.setOrientation(1);
                    } else {
                      if (v.equalsIgnoreCase("inverse")) {
                        arc.setOrientation(-1);
                      } else {
                        arc.setOrientation(2);
                      }
                    }
                  }
                } else {
                  LOGGER.error("Attribute " + attribute
                          + " is neither Number nor String. It can't be used as an orientation");
                }
              }
            }
          }
        }
        arc.addCorrespondant(element);
        //LOGGER.info("arc ajoute");
        // Le code ci-dessous permet un import plus fin mais a été
        // réalisé pour des données spécifiques et n'est pas encore
        // codé très générique.
        // Il est donc commenté dans cette version du code.
        // element = (FT_Feature)itElements.next();
        // if ( ref && paramApp.filtrageRef ) {
        // if ( filtrageTroncon(element) ) continue;
        // }
        // if ( !ref && paramApp.filtrageComp ) {
        // if ( filtrageTroncon(element) ) continue;
        // }
        // arc = (Arc_App)popArcApp.nouvelElement();
        // GM_LineString ligne = new GM_LineString((DirectPositionList)
        // element.getGeom().coord().clone());
        // arc.setGeometrie(ligne);
        // if ( paramApp.orientationConstante) {
        // if (paramApp.orientationDouble) arc.setOrientation(2);
        // else arc.setOrientation(1);
        // }
        // else arc.setOrientation(orientationTroncon(element));
        // arc.addCorrespondant(element);
      }
    }
    
    
    //    if (true) return reseau;
    // import des noeuds
    List<IFeatureCollection<? extends IFeature>> populationsNoeuds = network1.getPopulationsNoeuds();
    Iterator<?> itPopNoeuds = null;
    itPopNoeuds = populationsNoeuds.iterator();
    while (itPopNoeuds.hasNext()) {
      IFeatureCollection<?> popGeo = (IFeatureCollection<?>) itPopNoeuds.next();
      // import d'une population de noeuds
      for (IFeature element : popGeo.getElements()) {
        NoeudApp noeud = (NoeudApp) popNoeudApp.nouvelElement();
        // noeud.setGeometrie((GM_Point)element.getGeom());
        noeud.setGeometrie(new GM_Point((IDirectPosition) ((GM_Point) element
            .getGeom()).getPosition().clone()));
        noeud.addCorrespondant(element);
        noeud.setTaille(distanceNoeudsMax);
        // Le code ci-dessous permet un import plus fin mais a été
        // réalisé pour des données spécifiques et n'est pas encore
        // codé très générique.
        // Il est donc commenté dans cette version du code.
        // if ( paramApp.distanceNoeudsConstante )
        // noeud.setTaille(paramApp.distanceNoeuds);
        // else noeud.setTaille(tailleNoeud(element, paramApp));
      }
    }
    
    LOGGER.info("==================================");
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("==================================");
    
    // Indexation spatiale des arcs et noeuds
    // On crée un dallage régulier avec en moyenne 20 objets par case
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("    Spatial Index creation for nodes and edges");
    }
    int nb = (int) Math.sqrt(reseau.getPopArcs().size() / 20);
    if (nb == 0) {
      nb = 1;
    }
    reseau.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
    reseau.getPopNoeuds().initSpatialIndex(
        reseau.getPopArcs().getSpatialIndex());
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    // Instanciation de la topologie
    // 1- création de la topologie arcs-noeuds, rendu du graphe planaire
    if (topologieGraphePlanaire) {
      // cas où on veut une topologie planaire
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Making the graph planar and instantiation of node-edge topology");
      }
      // Debut Ajout
      reseau.creeTopologieArcsNoeuds(0.1);
      reseau.creeNoeudsManquants(0.1);
      reseau.filtreDoublons(0.1);
      reseau.filtreArcsDoublons();
      // Fin Ajout
      reseau.rendPlanaire(0.1);
      reseau.filtreDoublons(0.1);
    } else {
      // cas où on ne veut pas nécessairement rendre planaire la
      // topologie
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Topology instanciation (not making the graph planar)");
      }
      reseau.creeNoeudsManquants(0.1);
      reseau.filtreDoublons(0.1);
      reseau.creeTopologieArcsNoeuds(0.1);
    }
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");

    // 2- On fusionne les noeuds proches
    if (topologieSeuilFusionNoeuds >= 0) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Nodes Fusion");
      }
      reseau.fusionNoeuds(topologieSeuilFusionNoeuds);
    }
    if (topologieSurfacesFusionNoeuds != null) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Nodes Fusion inside a surface");
      }
      reseau.fusionNoeuds(topologieSurfacesFusionNoeuds);
    }
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    // 3- On enlève les noeuds isolés
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("    Isolated nodes filtering");
    }
    reseau.filtreNoeudsIsoles();
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    // 4- On filtre les noeuds simples (avec 2 arcs incidents)
    if (topologieElimineNoeudsAvecDeuxArcs) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Filtering of nodes with only 2 incoming edges");
      }
      reseau.filtreNoeudsSimples();
    }
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");

    // 5- On fusionne des arcs en double
    if (topologieFusionArcsDoubles) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Double edges filtering");
      }
      reseau.filtreArcsDoublons();
    }
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    // 6 - On crée la topologie de faces
    /*if (!ref && paramApp.varianteChercheRondsPoints) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("    Face topology creation");
      }
      reseau.creeTopologieFaces();
    }
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    // 7 - On double la taille de recherche pour les impasses
    if (paramApp.distanceNoeudsImpassesMax >= 0) {
      if (ref) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("    Doubling of search radius for nodes around deadends");
        }
        Iterator<?> itNoeuds = reseau.getPopNoeuds().getElements().iterator();
        while (itNoeuds.hasNext()) {
          NoeudApp noeud2 = (NoeudApp) itNoeuds.next();
          if (noeud2.arcs().size() == 1) {
            noeud2.setTaille(paramApp.distanceNoeudsImpassesMax);
          }
        }
      }
    }*/
    
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    LOGGER.info("**********************************");
    
    return reseau;
  }
}
