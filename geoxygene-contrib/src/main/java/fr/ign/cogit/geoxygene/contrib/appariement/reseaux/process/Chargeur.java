package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 *
 */
public class Chargeur {
  
  /** logger. */
  private static final Logger LOGGER = LogManager.getLogger(Chargeur.class.getName());
  
  
  public static ReseauApp importCarteTopo1(DataSet dataset1, ParametresApp param) {
    
    // ---------------------------------------------------------------------
    // Organisation des données en réseau et prétraitements topologiques
    if (LOGGER.isInfoEnabled()) {
        LOGGER.info("DATA STRUCTURING");
        LOGGER.info("Topological structuring");
    }
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("START OF STRUCTURING " + (new Time(System.currentTimeMillis())).toString());
    }

    //
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("creation of network 1 " + (new Time(System.currentTimeMillis())).toString());
    }
    
    ReseauApp reseau1 = importAsEdgesAndNodes("Réseau 1", dataset1, param.populationsArcsAvecOrientationDouble1,
        param.attributOrientation1, param.orientationMap1, param.distanceNoeudsMax);
    // Chargeur.importAsEdges(edges, map, orientationAttribute, orientationMap, groundPositionAttribute, tolerance);
    
    importData(reseau1, param.topologieGraphePlanaire1, param.topologieElimineNoeudsAvecDeuxArcs1, 
        param.topologieFusionArcsDoubles1, param.topologieSeuilFusionNoeuds1,
        param.varianteChercheRondsPoints, param.distanceNoeudsImpassesMax);
    
    return reseau1;

  }
  
  public static ReseauApp importCarteTopo2(DataSet dataset2, ParametresApp param) {
    
    // ---------------------------------------------------------------------
    // Organisation des données en réseau et prétraitements topologiques
    if (LOGGER.isInfoEnabled()) {
        LOGGER.info("DATA STRUCTURING");
        LOGGER.info("Topological structuring");
    }
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("START OF STRUCTURING " + (new Time(System.currentTimeMillis())).toString());
    }

    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("creation of network 2 " + (new Time(System.currentTimeMillis())).toString());
    }
    ReseauApp reseau2 = importAsEdgesAndNodes("Réseau 2", dataset2, param.populationsArcsAvecOrientationDouble2,
        param.attributOrientation2, param.orientationMap2, param.distanceNoeudsMax);
    
    importData(reseau2, param.topologieGraphePlanaire2, param.topologieElimineNoeudsAvecDeuxArcs2, 
        param.topologieFusionArcsDoubles2, param.topologieSeuilFusionNoeuds2,
        param.varianteChercheRondsPoints, param.distanceNoeudsImpassesMax);

    return reseau2;
    
  }
  
  /**
   * 
   * @param networkName
   * @param network1
   * @param direction
   * @return
   */
  private static ReseauApp importAsEdgesAndNodes(String networkName, DataSet network,
      boolean orientationDouble, String attributOrientation, Map<Object, Integer> orientationMap,
      float distanceNoeudsMax) {

      // Reseau
      ReseauApp reseau = new ReseauApp(networkName);
      IPopulation<? extends IFeature> popArcApp = reseau.getPopArcs();
      IPopulation<? extends IFeature> popNoeudApp = reseau.getPopNoeuds();
      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      
      // Ajout du schema 
      DefaultFeature featureRef = (DefaultFeature) network.getPopulation("Edge").get(0);
      SchemaDefaultFeature schemaDefaultFeature = featureRef.getSchema();
      FeatureType newFeatureType = (FeatureType) featureRef.getFeatureType();
      popArcApp.setFeatureType(newFeatureType);

      // /////////////////////////
      // import des arcs
      List<IFeatureCollection<? extends IFeature>> populationsArcs = new ArrayList<IFeatureCollection<? extends IFeature>>();
      populationsArcs.add(network.getPopulation("Edge"));
      Iterator<IFeatureCollection<? extends IFeature>> itPopArcs = null;
      itPopArcs = populationsArcs.iterator();
      LOGGER.info(populationsArcs.size() + " pops");

      //
      LOGGER.info("Import des populations d'arcs.");
      while (itPopArcs.hasNext()) {

          IFeatureCollection<? extends IFeature> popGeo = itPopArcs.next();

          // import d'une population d'arcs
          for (IFeature element : popGeo) {

              ArcApp arc = (ArcApp) popArcApp.nouvelElement();
              ILineString ligne = new GM_LineString((IDirectPositionList) element.getGeom().coord().clone());
              arc.setGeometrie(ligne);
              arc.setPoids(ligne.length());
              arc.addCorrespondant(element);
              
              // Transfert des attributs
              arc.setSchema(schemaDefaultFeature);
              Object[] valAttribute = new Object[element.getFeatureType().getFeatureAttributes().size()];
              for (int j = 0; j < element.getFeatureType().getFeatureAttributes().size(); j++) {
                  GF_AttributeType attributeType = element.getFeatureType().getFeatureAttributes().get(j);
                  String name = attributeType.getMemberName();
                  valAttribute[j] = element.getAttribute(name);
              }
              arc.setAttributes(valAttribute);

              // Gestion de la direction
              if (orientationDouble) {
                  LOGGER.debug("Population " + networkName + " avec direction double sens");
                  arc.setOrientation(2);
              } else {
                  LOGGER.debug("Population " + networkName + " avec direction dynamique");
                  String attribute = attributOrientation;
                  if (attribute.isEmpty()) {
                      arc.setOrientation(1);
                      LOGGER.debug("Populations avec orientation simple");
                  } else {
                      Object value = element.getAttribute(attribute);
                      // System.out.println(attribute + " = " + value);
                      if (orientationMap != null) {
                          /*for (int mapKey : orientationMap.keySet()) {
                              String valAttribut = orientationMap.get(mapKey);
                              LOGGER.debug("Population " + networkName + " - " + attribute + " = " +
                                      value + " ? " + valAttribut);
                              if (valAttribut.equals(value.toString())) {
                                  arc.setOrientation(mapKey);
                                  LOGGER.trace("Population " + networkName + " - orientation arc = " + mapKey);
                              }
                          } */
                          for (Object valAttribut : orientationMap.keySet()) {
                            LOGGER.debug("Population " + networkName + " - " + attribute + " = " +
                                value + " ? " + valAttribut);
                            if (value != null && valAttribut.equals(value.toString())) {
                                arc.setOrientation(orientationMap.get(valAttribut));
                                LOGGER.debug("Population " + networkName + " - orientation arc = " + orientationMap.get(valAttribut));
                            }
                          }
                          /*
                           * Integer orientation = orientationMap.get(value);
                           * if (orientation != null) {
                           * arc.setOrientation(orientation.intValue()); }
                           */
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
                                      // FIXME Pretty specific to BDTOPO Schema
                                      // ... no time to make it better
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
              
          }
      }

      // if (true) return reseau;
      // import des noeuds
      List<IFeatureCollection<? extends IFeature>> populationsNoeuds = new ArrayList<IFeatureCollection<? extends IFeature>>();
      populationsNoeuds.add(network.getPopulation("Node"));
      Iterator<?> itPopNoeuds = null;
      itPopNoeuds = populationsNoeuds.iterator();
      while (itPopNoeuds.hasNext()) {
          IFeatureCollection<?> popGeo = (IFeatureCollection<?>) itPopNoeuds.next();
          // import d'une population de noeuds
          if (popGeo != null && popGeo.size() > 0) {
          for (IFeature element : popGeo.getElements()) {
              NoeudApp noeud = (NoeudApp) popNoeudApp.nouvelElement();
              // noeud.setGeometrie((GM_Point)element.getGeom());
              noeud.setGeometrie(new GM_Point((IDirectPosition) ((GM_Point) element.getGeom()).getPosition().clone()));
              noeud.addCorrespondant(element);
              noeud.setTaille(distanceNoeudsMax);
          }}
      }

      LOGGER.info("==================================");
      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      LOGGER.info("==================================");

      return reseau;
  }

  /**
   * Création d'une carte topo à partir des objets géographiques initiaux.
   *     // IPopulation<?> topologieSurfacesFusionNoeuds
   * @return Le réseau créé
   */
  private static void importData(ReseauApp reseau, boolean planaire, boolean elimineNoeudsAvecDeuxArcs,
      boolean fusionArcsDoubles, double seuilFusionNoeuds,
      boolean chercheRondPoint,
          float distanceNoeudsImpassesMax) {

      IPopulation<? extends IFeature> popArcApp = reseau.getPopArcs();
      IPopulation<? extends IFeature> popNoeudApp = reseau.getPopNoeuds();

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
      reseau.getPopNoeuds().initSpatialIndex(reseau.getPopArcs().getSpatialIndex());

      // Instanciation de la topologie
      // 1- création de la topologie arcs-noeuds, rendu du graphe planaire
      if (planaire) {
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
          reseau.rendPlanaire2(0.1);
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
      if (seuilFusionNoeuds >= 0) {
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Nodes Fusion");
          }
          reseau.fusionNoeuds(seuilFusionNoeuds);
      }
      /*if (topologieSurfacesFusionNoeuds != null) {
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Nodes Fusion inside a surface");
          }
          reseau.fusionNoeuds(topologieSurfacesFusionNoeuds);
      }*/

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
      if (elimineNoeudsAvecDeuxArcs) {
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Filtering of nodes with only 2 incoming edges");
          }
          // reseau.filtreNoeudsSimples();
      }

      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      LOGGER.info("**********************************");

      // 5- On fusionne des arcs en double
      if (fusionArcsDoubles) {
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Double edges filtering");
          }
          reseau.filtreArcsDoublons();
      }

      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      LOGGER.info("**********************************");

      // 6 - On crée la topologie de faces
      // suppression du test (!ref)
      if (chercheRondPoint) { 
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Face topology creation"); 
          }
          reseau.creeTopologieFaces(); 
      }
      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      LOGGER.info("**********************************");
       
      // 7 - On double la taille de recherche pour les impasses 
      if (distanceNoeudsImpassesMax >= 0) { 
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("    Doubling of search radius for nodes around deadends"); 
          } 
          Iterator<?> itNoeuds = reseau.getPopNoeuds().getElements().iterator(); 
          while (itNoeuds.hasNext()) { 
              NoeudApp noeud2 = (NoeudApp) itNoeuds.next();
              if (noeud2.arcs().size() == 1) {
                  noeud2.setTaille(distanceNoeudsImpassesMax); 
              } 
          }
      }

      LOGGER.info(popArcApp.size() + " arcs");
      LOGGER.info(popNoeudApp.size() + " noeuds");
      LOGGER.info("**********************************");

  }

}
