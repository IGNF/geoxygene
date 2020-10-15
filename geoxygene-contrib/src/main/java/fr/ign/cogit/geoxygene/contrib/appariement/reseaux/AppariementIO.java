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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Utilitary class for importing and exporting data and matching them.
 * <p>
 * Méthodes d'import et export pour l'appariement sur des données Géographiques
 * quelconques (création des réseaux, lancement de l'appariement, export des
 * résultats).
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 */
public final class AppariementIO {
  
    /** Private constructor. Not used: this is a utilitary class. */
    private AppariementIO() {
    }

    /** Static logger. */
    private static final Logger LOGGER = LogManager.getLogger(AppariementIO.class.getName());

  /**
   * Lancement de l'appariement de réseaux sur des objets Géographiques : 1-
   * Transformation des données initales en deux graphes, en fonction des
   * paramètres d'import, 2- Lancement du calcul d'appariement générique sur les
   * deux réseaux, 3- Analyse et export des résultats éventuellement.
   * 
   * @return L'ensemble des liens en sortie (de la classe EnsembleDeLiens).
   * 
   * @param paramApp Les paramètres de l'appariement (seuls de distance,
   *          préparation topologique des données...)
   * 
   * @param cartesTopo Liste en entrée/sortie qui permet de Récupèrer en sortie
   *          les graphes intermédiaires créés pendant le calcul (de type
   *          Reseau_App, spécialisation de CarteTopo). - Si on veut Récupèrer
   *          les graphes : passer une liste vide - new ArrayList() - mais non
   *          nulle. Elle contient alors en sortie 2 éléments : dans l'ordre les
   *          cartes topo de référence et comparaison. Elle peut contenir un
   *          3eme élément: le graphe ref recalé sur comp si cela est demandé
   *          dans les paramètres. - Si on ne veut rien Récupèrer : passer Null
   *          
   * bientot deprecated : utiliser de préférence la méthode "" qui retourne 
   *    un ResultatAppariement et non un ensemble de liens. La méthode n'a pas changé sinon         
   */
  public static EnsembleDeLiens appariementDeJeuxGeo(
      final ParametresApp paramApp, final List<ReseauApp> cartesTopo) {
    
//      switch (paramApp.debugAffichageCommentaires) {
//        case 0:
//          AppariementIO.LOGGER.setLevel(Level.ERROR);
//          break;
//        case 1:
//          AppariementIO.LOGGER.setLevel(Level.INFO);
//          break;
//        default:
//          AppariementIO.LOGGER.setLevel(Level.DEBUG);
//          break;
//      }
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(""); //$NON-NLS-1$
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.MatchingStart")); //$NON-NLS-1$
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.MatchingInfo")); //$NON-NLS-1$
        AppariementIO.LOGGER.info(""); //$NON-NLS-1$
      }
      
      // ---------------------------------------------------------------------
      // Organisation des données en réseau et prétraitements topologiques
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("DATA STRUCTURING");
        LOGGER.info("Topological structuring");
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.StructuringStart" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.Network1Creation" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
      }
      ReseauApp reseauRef = AppariementIO.importData(paramApp, true);
      if (cartesTopo != null) {
        cartesTopo.add(reseauRef);
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.Network2Creation" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
      }
      ReseauApp reseauComp = AppariementIO.importData(paramApp, false);
      if (cartesTopo != null) {
        cartesTopo.add(reseauComp);
      }

      // NB: l'ordre dans lequel les projections sont faites n'est pas neutre
      if (paramApp.projeteNoeuds2SurReseau1) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.ProjectionOfNetwork2OnNetwork1" //$NON-NLS-1$
              ) + (new Time(System.currentTimeMillis())).toString());
        }
        reseauRef.projete(reseauComp,
            paramApp.projeteNoeuds2SurReseau1DistanceNoeudArc,
            paramApp.projeteNoeuds2SurReseau1DistanceProjectionNoeud,
            paramApp.projeteNoeuds2SurReseau1ImpassesSeulement);
      }
      if (paramApp.projeteNoeuds1SurReseau2) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.ProjectionOfNetwork1OnNetwork2" //$NON-NLS-1$
              ) + (new Time(System.currentTimeMillis())).toString());
        }
        reseauComp.projete(reseauRef,
            paramApp.projeteNoeuds1SurReseau2DistanceNoeudArc,
            paramApp.projeteNoeuds1SurReseau2DistanceProjectionNoeud,
            paramApp.projeteNoeuds1SurReseau2ImpassesSeulement);
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.AttributeFilling" //$NON-NLS-1$
            ) + (new Time(System.currentTimeMillis())).toString());
      }
      reseauRef.instancieAttributsNuls(paramApp.distanceNoeudsMax);
      reseauComp.initialisePoids();
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(I18N
            .getString("AppariementIO.StructuringFinished")); //$NON-NLS-1$
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.Network1") //$NON-NLS-1$
            + reseauRef.getPopArcs().size()
            + I18N.getString("AppariementIO.Edges") //$NON-NLS-1$
            + reseauRef.getPopNoeuds().size()
            + I18N.getString("AppariementIO.Nodes")); //$NON-NLS-1$
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.Network2") //$NON-NLS-1$
            + reseauComp.getPopArcs().size()
            + I18N.getString("AppariementIO.Edges") //$NON-NLS-1$
            + reseauComp.getPopNoeuds().size()
            + I18N.getString("AppariementIO.Nodes")); //$NON-NLS-1$
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N.getString("AppariementIO.StructuringEnd") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      
      // APPARIEMENT
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(""); //$NON-NLS-1$
        AppariementIO.LOGGER
            .info(I18N.getString("AppariementIO.NetworkMatching")); //$NON-NLS-1$
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.NetworkMatchingStart") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      EnsembleDeLiens liens = Appariement.appariementReseaux(reseauRef, reseauComp, paramApp);
      
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(I18N
            .getString("AppariementIO.NetworkMatchingFinished")); //$NON-NLS-1$
        AppariementIO.LOGGER.info("  " + liens.size() + I18N.getString(//$NON-NLS-1$
            "AppariementIO.MatchingLinksFound")); //$NON-NLS-1$
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.NetworkMatchingEnd") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      
      // EXPORT
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(""); //$NON-NLS-1$
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.Conclusion")); //$NON-NLS-1$
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N.getString("AppariementIO.ExportStart") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      if (paramApp.debugBilanSurObjetsGeo) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.LinkTransformation") //$NON-NLS-1$
              + new Time(System.currentTimeMillis()).toString());
        }
        EnsembleDeLiens liensGeneriques = LienReseaux.exportLiensAppariement(
            liens, reseauRef, paramApp);
        Appariement.nettoyageLiens(reseauRef, reseauComp);
        if (AppariementIO.LOGGER.isInfoEnabled()) {
          AppariementIO.LOGGER.info(I18N.getString("AppariementIO.MatchingEnd")); //$NON-NLS-1$
        }
        return liensGeneriques;
      }
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N.getString("AppariementIO.LinkGeometry") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      LienReseaux.exportAppCarteTopo(liens, paramApp);
      if (AppariementIO.LOGGER.isInfoEnabled()) {
        AppariementIO.LOGGER.info(I18N.getString("AppariementIO.MatchingEnd")); //$NON-NLS-1$
      }
   
      return liens;
  }
  

  /**
   * Création d'une carte topo à partir des objets Géographiques initiaux.
   * 
   * @param paramApp Les paramètres de l'appariement (seuls de distance,
   *          préparation topologique des données...)
   * @param ref true = on traite le réseau de référence false = on traite le
   *          réseau de comparaison
   * @return Le réseau créé
   */
  public static ReseauApp importData(final ParametresApp paramApp,
      final boolean ref) {
//    switch (paramApp.debugAffichageCommentaires) {
//      case 0:
//        AppariementIO.LOGGER.setLevel(Level.ERROR);
//        break;
//      case 1:
//        AppariementIO.LOGGER.setLevel(Level.INFO);
//        break;
//      default:
//        AppariementIO.LOGGER.setLevel(Level.DEBUG);
//        break;
//    }
    ReseauApp reseau = null;
    if (ref) {
      LOGGER.info(I18N.getString("AppariementIO.ReferenceNetwork"));
      reseau = new ReseauApp(I18N.getString("AppariementIO.ReferenceNetwork")); //$NON-NLS-1$
    } else {
      LOGGER.info(I18N.getString("AppariementIO.ComparisonNetwork"));
      reseau = new ReseauApp(I18N.getString("AppariementIO.ComparisonNetwork")); //$NON-NLS-1$
    }
    IPopulation<? extends IFeature> popArcApp = reseau.getPopArcs();
    IPopulation<? extends IFeature> popNoeudApp = reseau.getPopNoeuds();
    LOGGER.info(popArcApp.size() + " arcs");
    LOGGER.info(popNoeudApp.size() + " noeuds");
    // /////////////////////////
    // import des arcs
    Iterator<IFeatureCollection<? extends IFeature>> itPopArcs = null;
    boolean populationsArcsAvecOrientationDouble = true;
    if (ref) {
      itPopArcs = paramApp.populationsArcs1.iterator();
      populationsArcsAvecOrientationDouble = paramApp.populationsArcsAvecOrientationDouble1;
      LOGGER.info(paramApp.populationsArcs1.size() + " pops");
    } else {
      populationsArcsAvecOrientationDouble = paramApp.populationsArcsAvecOrientationDouble2;
      itPopArcs = paramApp.populationsArcs2.iterator();
      LOGGER.info(paramApp.populationsArcs2.size() + " pops");
    }
    while (itPopArcs.hasNext()) {
      IFeatureCollection<? extends IFeature> popGeo = itPopArcs.next();
      LOGGER.info(popGeo.size() + " objects");
      // import d'une population d'arcs
      for (IFeature element : popGeo) {
        ArcApp arc = (ArcApp) popArcApp.nouvelElement();
        ILineString ligne = new GM_LineString((IDirectPositionList) element
            .getGeom().coord().clone());
        arc.setGeometrie(ligne);
        if (populationsArcsAvecOrientationDouble) {
          arc.setOrientation(2);
        } else {
          String attribute = (ref) ? paramApp.attributOrientation1
              : paramApp.attributOrientation2;
          Map<Object, Integer> orientationMap = (ref) ? paramApp.orientationMap1
              : paramApp.orientationMap2;
          if (attribute.isEmpty()) {
            arc.setOrientation(1);
          } else {
            Object value = element.getAttribute(attribute);
            // System.out.println(attribute + " = " + value);
            if (orientationMap != null) {
              Integer orientation = orientationMap.get(value);
              if (orientation != null) {
                arc.setOrientation(orientation.intValue());
              }
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
                    if (v.equalsIgnoreCase("direct")) { //$NON-NLS-1$
                      arc.setOrientation(1);
                    } else {
                      if (v.equalsIgnoreCase("inverse")) { //$NON-NLS-1$
                        arc.setOrientation(-1);
                      } else {
                        arc.setOrientation(2);
                      }
                    }
                  }
                } else {
                  AppariementIO.LOGGER
                      .error("Attribute " //$NON-NLS-1$
                          + attribute
                          + " is neither Number nor String. It can't be used as an orientation"); //$NON-NLS-1$
                }
              }
            }
          }
        }
        arc.addCorrespondant(element);
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
    Iterator<?> itPopNoeuds = null;
    if (ref) {
      itPopNoeuds = paramApp.populationsNoeuds1.iterator();
    } else {
      itPopNoeuds = paramApp.populationsNoeuds2.iterator();
    }
    while (itPopNoeuds.hasNext()) {
      IFeatureCollection<?> popGeo = (IFeatureCollection<?>) itPopNoeuds.next();
      // import d'une population de noeuds
      for (IFeature element : popGeo.getElements()) {
        NoeudApp noeud = (NoeudApp) popNoeudApp.nouvelElement();
        // noeud.setGeometrie((GM_Point)element.getGeom());
        noeud.setGeometrie(new GM_Point((IDirectPosition) ((GM_Point) element
            .getGeom()).getPosition().clone()));
        noeud.addCorrespondant(element);
        noeud.setTaille(paramApp.distanceNoeudsMax);
        // Le code ci-dessous permet un import plus fin mais a été
        // réalisé pour des données spécifiques et n'est pas encore
        // codé très générique.
        // Il est donc commenté dans cette version du code.
        // if ( paramApp.distanceNoeudsConstante )
        // noeud.setTaille(paramApp.distanceNoeuds);
        // else noeud.setTaille(tailleNoeud(element, paramApp));
      }
    }

    // Indexation spatiale des arcs et noeuds
    // On crée un dallage régulier avec en moyenne 20 objets par case
    if (AppariementIO.LOGGER.isDebugEnabled()) {
      AppariementIO.LOGGER.debug(I18N
          .getString("AppariementIO.SpatialIndexing") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    int nb = (int) Math.sqrt(reseau.getPopArcs().size() / 20);
    if (nb == 0) {
      nb = 1;
    }
    reseau.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
    reseau.getPopNoeuds().initSpatialIndex(
        reseau.getPopArcs().getSpatialIndex());
    // Instanciation de la topologie
    // 1- création de la topologie arcs-noeuds, rendu du graphe planaire

    if ((ref && paramApp.topologieGraphePlanaire1)
        || (!ref && paramApp.topologieGraphePlanaire2)) {
      // cas où on veut une topologie planaire
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N.getString("AppariementIO.PlanarGraph") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
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
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N.getString("AppariementIO.Topology") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      reseau.creeNoeudsManquants(0.1);
      reseau.filtreDoublons(0.1);
      reseau.creeTopologieArcsNoeuds(0.1);
    }

    // 2- On fusionne les noeuds proches
    if (ref) {
      if (paramApp.topologieSeuilFusionNoeuds1 >= 0) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.NodesFusion") //$NON-NLS-1$
              + new Time(System.currentTimeMillis()).toString());
        }
        reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds1);
      }
      if (paramApp.topologieSurfacesFusionNoeuds1 != null) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.NodesFusionInsideASurface") //$NON-NLS-1$
              + (new Time(System.currentTimeMillis())).toString());
        }
        reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds1);
      }
    } else {
      if (paramApp.topologieSeuilFusionNoeuds2 >= 0) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.NodesFusion") //$NON-NLS-1$
              + new Time(System.currentTimeMillis()).toString());
        }
        reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds2);
      }
      if (paramApp.topologieSurfacesFusionNoeuds2 != null) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.NodesFusionInsideASurface") //$NON-NLS-1$
              + new Time(System.currentTimeMillis()).toString());
        }
        reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds2);
      }
    }
    // 3- On enlève les noeuds isolés
    if (AppariementIO.LOGGER.isDebugEnabled()) {
      AppariementIO.LOGGER.debug(I18N
          .getString("AppariementIO.IsolatedNodesFiltering") //$NON-NLS-1$
          + new Time(System.currentTimeMillis()).toString());
    }
    reseau.filtreNoeudsIsoles();
    // 4- On filtre les noeuds simples (avec 2 arcs incidents)
    if ((ref && paramApp.topologieElimineNoeudsAvecDeuxArcs1)
        || (!ref && paramApp.topologieElimineNoeudsAvecDeuxArcs2)) {
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.NodesFiltering") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      reseau.filtreNoeudsSimples();
    }

    // 5- On fusionne des arcs en double
    if (ref && paramApp.topologieFusionArcsDoubles1) {
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.EdgesFiltering") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      reseau.filtreArcsDoublons();
    }
    if (!ref && paramApp.topologieFusionArcsDoubles2) {
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER.debug(I18N
            .getString("AppariementIO.EdgesFiltering") //$NON-NLS-1$
            + new Time(System.currentTimeMillis()).toString());
      }
      reseau.filtreArcsDoublons();
    }
    // 6 - On crée la topologie de faces
    if (!ref && paramApp.varianteChercheRondsPoints) {
      if (AppariementIO.LOGGER.isDebugEnabled()) {
        AppariementIO.LOGGER
            .debug(I18N.getString("AppariementIO.FaceTopology")); //$NON-NLS-1$
      }
      reseau.creeTopologieFaces();
    }
    // 7 - On double la taille de recherche pour les impasses
    if (paramApp.distanceNoeudsImpassesMax >= 0) {
      if (ref) {
        if (AppariementIO.LOGGER.isDebugEnabled()) {
          AppariementIO.LOGGER.debug(I18N
              .getString("AppariementIO.DoublingOfSearchRadius")); //$NON-NLS-1$
        }
        Iterator<?> itNoeuds = reseau.getPopNoeuds().getElements().iterator();
        while (itNoeuds.hasNext()) {
          NoeudApp noeud2 = (NoeudApp) itNoeuds.next();
          if (noeud2.arcs().size() == 1) {
            noeud2.setTaille(paramApp.distanceNoeudsImpassesMax);
          }
        }
      }
    }
    return reseau;
  }

  // METHODES D'EXPORT
  /**
   * Methode utile principalement pour analyser les résultats d'un appariement,
   * qui découpe un réseau en plusieurs réseaux selon les valeurs de l'attribut
   * "ResultatAppariement" des arcs et noeuds du réseau apparié.
   * @param reseauRef reference network
   * @param valeursClassement sorting values
   * @return sorted networks
   */
  public static List<ReseauApp> scindeSelonValeursResultatsAppariement(
      final ReseauApp reseauRef, final List<String> valeursClassement) {
    List<ReseauApp> cartesTopoClassees = new ArrayList<ReseauApp>();
    Iterator<?> itArcs = reseauRef.getPopArcs().getElements().iterator();
    Iterator<?> itNoeuds = reseauRef.getPopNoeuds().getElements().iterator();
    ArcApp arc, arcClasse;
    NoeudApp noeud, noeudClasse;
    int i;
    for (i = 0; i < valeursClassement.size(); i++) {
      cartesTopoClassees.add(new ReseauApp(I18N
          .getString("AppariementIO.Evaluation") //$NON-NLS-1$
          + valeursClassement.get(i)));
    }
    while (itArcs.hasNext()) {
      arc = (ArcApp) itArcs.next();
      for (i = 0; i < valeursClassement.size(); i++) {
        if (arc.getResultatAppariement() == null) {
          continue;
        }
        if (arc.getResultatAppariement().startsWith(valeursClassement.get(i))) {
          arcClasse = (ArcApp) cartesTopoClassees.get(i).getPopArcs()
              .nouvelElement();
          arcClasse.setGeom(arc.getGeom());
          arcClasse.setResultatAppariement(arc.getResultatAppariement());
        }
      }
    }
    while (itNoeuds.hasNext()) {
      noeud = (NoeudApp) itNoeuds.next();
      for (i = 0; i < valeursClassement.size(); i++) {
        if (noeud.getResultatAppariement() == null) {
          continue;
        }
        if (noeud.getResultatAppariement().startsWith(valeursClassement.get(i))) {
          noeudClasse = (NoeudApp) cartesTopoClassees.get(i).getPopNoeuds()
              .nouvelElement();
          noeudClasse.setGeom(noeud.getGeom());
          noeudClasse.setResultatAppariement(noeud.getResultatAppariement());
        }
      }
    }
    return cartesTopoClassees;
  }
}
