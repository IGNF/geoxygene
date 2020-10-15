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

package fr.ign.cogit.geoxygene.contrib.appariement.surfaces;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.operateurs.Ensemble;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Appariement de surfaces. Processus défini dans la thèse de Atef Bel Hadj Ali
 * (2001), et resume dans le rapport [Mustiere 2002] :
 * ("Description des processus d'appariement mis en oeuvre au COGIT"
 * ,SR/2002.0072, chap.6).
 * @author Arnaud Braun
 * @author Sébastien Mustière
 */
public abstract class AppariementSurfaces {
  public static Logger LOGGER = LogManager.getLogger(AppariementSurfaces.class.getName());

  /**
   * Appariement entre deux ensembles de surfaces. Processus inspiré de celui
   * défini dans la thèse de Atef Bel Hadj Ali (2001), et résumé dans le rapport
   * de Seb
   * ("Description des processus d'appariement mise en oeuvre au COGIT",SR
   * /2002.0072, chap.6).
   * <ul>
   * <li>NB 1 : LE CAS DES LIENS N-M N'EST PAS VRAIMENT SATIFAISANT ET DOIT ENCORE ETRE REVU
   * (reflechir aux mesures). Néanmoins... le processus a été amélioré pour mieux raffiner le
   * traitement des liens n-m : un lien n-m issu du regroupement des liens 1-1 peut être redécoupé
   * en plusieurs liens n'-m', alors que le processus d'Atef ne semble permettre que de simplifier
   * ce groupe n-m en UN seul groupe n'-m' (n'<=n, m'<=m)
   * <li>NB 2 :Les liens finaux sont qualifiés (evaluation) par la mesure de distance surfacique
   * entre groupes de surfaces.
   * <li>NB 3 : si la population de référence n'est pas indexée, elle le sera pendant le calcul
   * <li>NB 4 : l'appariement est symétrique (si ref et comp sont échangés, les résultats sont
   * identiques)
   * </ul>
   * @param popRef
   *        : population des objets de référence. Ces objets doivent
   *        avoir une géométrie "geom" de type GM_Polygon
   * @param popComp
   *        : population des objets de comparaison Ces objets doivent
   *        avoir une géométrie "geom" de type GM_Polygon
   * @param param
   *        : paramètres de l'appariement
   * @return liens d'appariement calculés. Ces liens peuvent être de type n-m.
   */
  public static EnsembleDeLiens appariementSurfaces(IFeatureCollection<? extends IFeature> popRef,
      IFeatureCollection<? extends IFeature> popComp, ParametresAppSurfaces param) {
    // indexation au besoin des surfaces de comparaison
    if (!popComp.hasSpatialIndex()) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
            "SpatialIndexComparisonSurfaces" //$NON-NLS-1$
        ) + " " + new Time(System.currentTimeMillis()));
      }
      popComp.initSpatialIndex(Tiling.class, true);
    }
    // pré-appariement selon un test sur la surface de l'intersection
    // entre les surfaces de référence et de comparaison
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
          "PrematchingUsingIntersectionCriteria" //$NON-NLS-1$
      ) + " " + new Time(System.currentTimeMillis()));
    }
    EnsembleDeLiens liensPreApp = AppariementSurfaces.preAppariementSurfaces(popRef, popComp, param);
    EnsembleDeLiens liensRegroupes = liensPreApp;
    // appariement par recherche des regroupements optimaux
    if (param.regroupementOptimal) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
            "MatchingBySearchingOptimalGroups" //$NON-NLS-1$
        ) + " " + new Time(System.currentTimeMillis()));
      }
      liensRegroupes = AppariementSurfaces.rechercheRegroupementsOptimaux(liensPreApp, popRef,
          popComp, param);
    }
    // recollage des petites surfaces non encore appariées
    if (param.ajoutPetitesSurfaces) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
            "MergingUnmatchedSmallSurfaces" //$NON-NLS-1$
        ) + " " + new Time(System.currentTimeMillis()));
      }
      AppariementSurfaces.ajoutPetitesSurfaces(liensRegroupes, popRef, popComp, param);
    }
    EnsembleDeLiens liensFiltres = liensRegroupes;
    // filtrage final pour n'accepter que les appariements suffisament bons
    if (param.filtrageFinal) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
            "FinalFiltering" //$NON-NLS-1$
        ) + " " + new Time(System.currentTimeMillis()));
      }
      liensFiltres = AppariementSurfaces.filtreLiens(liensRegroupes, param);
    }
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
          "LinkGeometryCreation" //$NON-NLS-1$
      ) + " " + new Time(System.currentTimeMillis()));
    }
    AppariementSurfaces.creeGeometrieDesLiens(liensFiltres, param.persistant);
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces.ProcessEnd")); //$NON-NLS-1$
    }
    return liensFiltres;
  }

  /**
   * 2 surfaces sont pré-appariées si elles respectent le "test d'association"
   * défini par Atef Bel Hadj Ali (2001). C'est-à-dire si :
   * <ul>
   * <li>1/ l'intersection des surfaces a une taille supérieure au seuil "surface_min" ET
   * <li>2/ l'intersection fait au moins la taille d'une des surfaces multipliée par le paramètre
   * "pourcentage_min".
   * </ul>
   * NB:
   * <ul>
   * <li>NB 1 : Par construction : chaque lien pointe vers UN SEUL objet de la population de
   * référence et vers UN SEUL objet de la population de comparaison.
   * <li>NB 2 : Aucune géométrie n'est instanciée pour les liens créés.
   * <li>NB 3 : l'appariement est symétrique.
   * <li>NB 4 : la population de comparaison est indexée si elle ne l'était pas avant
   * </ul>
   * @param popRef
   *        : population des objets de référence.
   * @param popComp
   *        : population des objets de comparaison.
   * @param param
   *        : paramètres de l'appariement.
   * @return : liens de pré-appariement calculés.
   */
  public static EnsembleDeLiens preAppariementSurfaces(IFeatureCollection<?> popRef,
      IFeatureCollection<?> popComp, ParametresAppSurfaces param) {
    EnsembleDeLiens preAppLiens = new EnsembleDeLiens();
    if (!popComp.hasSpatialIndex()) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." + //$NON-NLS-1$
            "SpatialIndexComparisonSurfacesPopulation")); //$NON-NLS-1$
      }
      popComp.initSpatialIndex(Tiling.class, true);
    }
    for (IFeature featureRef : popRef) {
      IGeometry geomRef = featureRef.getGeom();
      if (!(geomRef instanceof ISurface)) {
        if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
          AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces." //$NON-NLS-1$
              + "ReferenceObjectWithNoSurfaceGeometry") //$NON-NLS-1$
              + " " + featureRef.getId());
        }
        continue;
      }
      // Test d'association sur tous les objets comp intersectant l'objet ref
      Collection<? extends IFeature> candidatComp = popComp.select(geomRef);
      for (IFeature featureComp : candidatComp) {
        IGeometry geomComp = featureComp.getGeom();
        if (!(geomComp instanceof ISurface)) {
          if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
            AppariementSurfaces.LOGGER
                .debug(I18N.getString("AppariementSurfaces.ComparisonObjectWithNoSurfaceGeometry") + featureComp.getId()); //$NON-NLS-1$
          }
          continue;
        }
        // création éventuelle d'un nouveau lien de pré-appariement
        IGeometry inter = Operateurs.intersectionRobuste(geomRef, geomComp, param.resolutionMin,
            param.resolutionMax);
        if (inter == null) {
          continue; // si plantage aux calculs d'intersection
        }
        double surfaceIntersection = inter.area();
        if (surfaceIntersection <= param.surface_min_intersection) {
          continue;
        }
        double pourcentageRecouvrement = Math.max(surfaceIntersection / geomRef.area(),
            surfaceIntersection / geomComp.area());
        if (pourcentageRecouvrement < param.pourcentage_min_intersection) {
          continue; // intersection pas suffisante
        }
        Lien lien = preAppLiens.nouvelElement();
        lien.addObjetRef(featureRef);
        lien.addObjetComp(featureComp);
        lien.setEvaluation(pourcentageRecouvrement);
        // precompute
        if (param.minimiseDistanceSurfacique) {
          lien.getDistanceSurfacique();
        } else {
          lien.getExactitude();
          lien.getCompletude();
        }
      }
    }
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER
          .debug(I18N.getString("AppariementSurfaces.NumberOf1-1LinksCreatedDuringPrematching") + preAppLiens.size()); //$NON-NLS-1$
    }
    return preAppLiens;
  }

  /**
   * On recherche les regroupements optimaux de liens de pré-appariement, pour
   * maximiser la distance surfacique entre les groupes de référence et de
   * comparaison.
   * <p>
   * NB : l'appariement est symétrique
   * @param param
   *        : paramètres de l'appariement
   * @param liensPreApp
   *        : liens issus du pré-appariement
   * @return liens d'appariement calculés (contient des objets de la classe
   *         Lien). Ces liens sont des liens n-m.
   */
  public static EnsembleDeLiens rechercheRegroupementsOptimaux(EnsembleDeLiens liensPreApp,
      IFeatureCollection<? extends IFeature> popRef,
      IFeatureCollection<? extends IFeature> popComp, ParametresAppSurfaces param) {
    List<Groupe> groupesGardes = new ArrayList<>();
    // on crée les liens n-m (groupes connexes du graphe des liens)
    CarteTopo grapheDesLiens = liensPreApp.transformeEnCarteTopo(popRef, popComp);
    Groupe groupeTotal = grapheDesLiens.getPopGroupes().nouvelElement();
    groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
    groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
    List<IOrientableSurface> listRef = new ArrayList<>(popRef.size());
    for (IFeature f : popRef) {
      listRef.add((IOrientableSurface) f.getGeom());
    }
    List<IOrientableSurface> listComp = new ArrayList<>(popComp.size());
    for (IFeature f : popComp) {
      listComp.add((IOrientableSurface) f.getGeom());
    }
    List<Groupe> groupesConnexes = groupeTotal.decomposeConnexes(false);
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER
          .debug(I18N.getString("AppariementSurfaces.NumberOfN-MLinksToHandle") + " " + groupesConnexes.size()); //$NON-NLS-1$
    }
    // on parcours tous les liens n-m créés
    int i = 0;
    for (Groupe groupeConnexe : groupesConnexes) {
      i++;
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        if (i % 1000 == 0) {
          AppariementSurfaces.LOGGER.debug(I18N
              .getString("AppariementSurfaces." + "NumberOfHandledGroups") //$NON-NLS-1$ //$NON-NLS-2$
              + i + "     " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
        }
      }
      // pour les objets isolés ou les liens 1-1, on ne fait rien de plus
      if (groupeConnexe.getListeArcs().isEmpty()) {
        continue;
      }
      if (groupeConnexe.getListeArcs().size() == 1) {
        groupesGardes.add(groupeConnexe);
        continue;
      }
      // pour les groupes n-m, on va essayer d'enlever des arcs
      // mais on garde à coup sûr les liens avec suffisament de recouvremnt
      List<Arc> arcsEnlevables = new ArrayList<>(groupeConnexe.getListeArcs());
      List<Arc> arcsNonEnlevables = new ArrayList<>();
      Set<IOrientableSurface> geomRef = new HashSet<>();
      Set<IOrientableSurface> geomComp = new HashSet<>();
      for (Arc arc : arcsEnlevables) {
        Lien lienArc = (Lien) arc.getCorrespondant(0);
        if (lienArc.getEvaluation() > param.pourcentage_intersection_sur) {
          arcsNonEnlevables.add(arc);
        }
        for (IFeature f : lienArc.getObjetsRef()) {
          geomRef.add((IOrientableSurface) f.getGeom());
        }
        for (IFeature f : lienArc.getObjetsComp()) {
          geomComp.add((IOrientableSurface) f.getGeom());
        }
      }
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        IMultiSurface<IOrientableSurface> aggrRef = new GM_MultiSurface<>();
        aggrRef.addAll(geomRef);
        IMultiSurface<IOrientableSurface> aggrComp = new GM_MultiSurface<>();
        aggrComp.addAll(geomComp);
        AppariementSurfaces.LOGGER
            .debug("Ref = " + aggrRef + " " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
        AppariementSurfaces.LOGGER
            .debug("Comp = " + aggrComp + " " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
      }
      arcsEnlevables.removeAll(arcsNonEnlevables);
      if (arcsEnlevables.isEmpty()) { // si on ne peut rien enlever, on s'arrête là
        groupesGardes.add(groupeConnexe);
        continue;
      }
      // on cherche à enlever toutes les combinaisons possibles d'arcs virables
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER
            .debug("Combinaisons de " + arcsEnlevables.size() + " arcs sur " + groupeConnexe.getListeArcs().size() + " " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
      }
      List<List<Arc>> combinaisons = Ensemble.combinaisons(new ArrayList<>(arcsEnlevables));
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER.debug(combinaisons.size()
            + " combinaisons trouvées " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
      }
      double distSurfMin = 2; // cas de distance surfacique à minimiser
      double distExacMax = 0; // cas de completude / exactitude à maximiser
      List<Arc> arcsDuGroupeEnlevesFinal = new ArrayList<>();
      int comb = 0;
      long start = System.currentTimeMillis();
      for (List<Arc> arcsDuGroupeEnleves : combinaisons) { // boucle sur les combinaisons possibles
        Groupe groupeLight = groupeConnexe.copie(false);
        groupeLight.getListeArcs().removeAll(arcsDuGroupeEnleves);
        int size = groupeLight.getListeArcs().size();
        if (groupeLight.getListeArcs().isEmpty()) {
          continue; // on refuse la solution extreme de ne rien garder comme apparié
        }
        // if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        // AppariementSurfaces.LOGGER
        // .debug("\tGroupe de " + groupeLight.getListeArcs().size());
        // }
        double dist = AppariementSurfaces.mesureEvaluationGroupe(groupeLight, popRef, popComp,
            param);
        if (param.minimiseDistanceSurfacique) {
          // cas de distance surfacique à minimiser
          if (dist < distSurfMin) {
            distSurfMin = dist;
            arcsDuGroupeEnlevesFinal = arcsDuGroupeEnleves;
          }
        } else {
          // cas de completude / exactitude à maximiser
          if (dist > distExacMax) {
            distExacMax = dist;
            arcsDuGroupeEnlevesFinal = arcsDuGroupeEnleves;
          }
        }
        if (comb != 0 && comb % 1000 == 0) {
          long end = System.currentTimeMillis();
          if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
            AppariementSurfaces.LOGGER.debug("\tCombinaison " + comb + " (" + size + ") "
                + (end - start));
          }
          start = end;
        }
        comb++;
        // if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        // AppariementSurfaces.LOGGER
        // .debug("\t\t " + (end - start));
        // }
      } // fin boucle sur les combinaisons possibles
      // simplification finale des liens d'appariement du groupe
      groupeConnexe.getListeArcs().removeAll(arcsDuGroupeEnlevesFinal);
      // création des groupes finaux gardés
      List<Groupe> groupesDecomposes = groupeConnexe.decomposeConnexes(false);
      groupesGardes.addAll(groupesDecomposes);
    }
    // création des liens retenus (passage de structure graphe à liens):
    // les groupes qui restent dans la carte topo représentent les liens finaux.
    EnsembleDeLiens liensGroupes = new EnsembleDeLiens();
    liensGroupes.setNom(I18N.getString("AppariementSurfaces.LinksCreatedFromSurfaceMatching")); //$NON-NLS-1$
    // on parcours tous les groupes connexes créés
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER.debug(groupesGardes.size()
          + " Groupes gardés " + new Time(System.currentTimeMillis())); //$NON-NLS-1$
    }
    for (Groupe groupeConnexe : groupesGardes) {
      if (groupeConnexe.getListeArcs().isEmpty()) {
        continue; // cas des noeuds isolés
      }
      // if ( groupeConnexe.getListenoeuds().size() == 0 ) continue;
      Lien lienGroupe = liensGroupes.nouvelElement();
      for (Noeud noeud : groupeConnexe.getListeNoeuds()) {
        IFeature feat = noeud.getCorrespondant(0);
        if (popRef.getElements().contains(feat)) {
          lienGroupe.addObjetRef(feat);
        }
        if (popComp.getElements().contains(feat)) {
          lienGroupe.addObjetComp(feat);
        }
        // nettoyage de la carteTopo créée
        noeud.setCorrespondants(new ArrayList<>(0));
      }
      lienGroupe.setArcs(groupeConnexe.getListeArcs());
    }
    return liensGroupes;
  }

  /**
   * Distance surfacique ou complétude "étendue" sur un groupe.
   * <p>
   * Attention: vide le groupe au passage.
   * <p>
   * MESURE NON SATISFAISANTE POUR LIENS N-M : moyenne entre parties connexes
   * <p>
   * A REVOIR
   * @param groupe
   * @param popRef
   * @param popComp
   * @param param
   */
  @SuppressWarnings("unchecked")
  private static double mesureEvaluationGroupe(Groupe groupe, IFeatureCollection<?> popRef,
      IFeatureCollection<?> popComp, ParametresAppSurfaces param) {
    /*
     * double result = 0;
     * double ds = 1;
     * double exactitude = 1;
     * double completude = 1;
     * for (Arc arc : groupe.getListeArcs()) {
     * Lien lien = (Lien) arc.getCorrespondant(0);
     * // IFeature ref = lien.getObjetsRef().get(0);
     * // IFeature comp = lien.getObjetsComp().get(0);
     * // IMultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<IOrientableSurface>();
     * // IMultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<IOrientableSurface>();
     * // if (ref.getGeom() instanceof IMultiSurface) {
     * // geomRef = (IMultiSurface<IOrientableSurface>) ref.getGeom();
     * // } else {
     * // geomRef.add((IOrientableSurface) ref.getGeom());
     * // }
     * // if (comp.getGeom() instanceof IMultiSurface) {
     * // geomComp = (IMultiSurface<IOrientableSurface>) comp.getGeom();
     * // } else {
     * // geomComp.add((IOrientableSurface) comp.getGeom());
     * // }
     * if (param.minimiseDistanceSurfacique) {
     * ds *= lien.getDistanceSurfacique();
     * } else {
     * exactitude *= lien.getExactitude();
     * completude *= lien.getCompletude();
     * }
     * }
     * if (param.minimiseDistanceSurfacique) {
     * result = ds;
     * } else {
     * result = exactitude + completude;
     * }
     */
    double result = param.minimiseDistanceSurfacique ? 2 : -1;
    // on décompose le groupe en parties connexes
    long start = System.currentTimeMillis();
    List<Groupe> groupesConnexes = groupe.decomposeConnexes(false);
    long end = System.currentTimeMillis();
    long duration = (end - start);
    if (duration > 5) {
      LOGGER.info("decomposeConnexes " + duration);
    }
    for (Groupe groupeConnnexe : groupesConnexes) {
      // on mesure la qualité de l'appariement pour chaque partie connexe,
      if (groupeConnnexe.getListeArcs().isEmpty()) {
        continue;
      }
      // IMultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<IOrientableSurface>();
      // IMultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<IOrientableSurface>();
      List<IOrientableSurface> listRef = new ArrayList<>();
      List<IOrientableSurface> listComp = new ArrayList<>();
      for (Noeud noeud : groupeConnnexe.getListeNoeuds()) {
        IFeature feat = noeud.getCorrespondant(0);
        // if ( feat.getPopulation()==popRef ) geomRef.add(feat.getGeom());
        // if ( feat.getPopulation()==popComp ) geomComp.add(feat.getGeom());
        if (popRef.getElements().contains(feat)) {
          listRef.add((IOrientableSurface) feat.getGeom());
        }
        if (popComp.getElements().contains(feat)) {
          listComp.add((IOrientableSurface) feat.getGeom());
        }
      }
      IGeometry unionRef = JtsAlgorithms.union(listRef);
      IGeometry unionComp = JtsAlgorithms.union(listComp);
      IMultiSurface<IOrientableSurface> geomRef = new GM_MultiSurface<>();
      IMultiSurface<IOrientableSurface> geomComp = new GM_MultiSurface<>();
      if (unionRef instanceof IMultiSurface<?>) {
        geomRef = (IMultiSurface<IOrientableSurface>) unionRef;
      } else {
        geomRef.add((IOrientableSurface) unionRef);
      }
      if (unionComp instanceof IMultiSurface<?>) {
        geomComp = (IMultiSurface<IOrientableSurface>) unionComp;
      } else {
        geomComp.add((IOrientableSurface) unionComp);
      }
      // on combine les mesures des parties connexes
      if (param.minimiseDistanceSurfacique) {
        double value = Distances.distanceSurfaciqueRobuste(geomRef, geomComp);
        result = Math.min(result, value);
      } else {
        double value = Distances.exactitude(geomRef, geomComp)
            + Distances.completude(geomRef, geomComp);
        result = Math.max(result, value);
      }
    }
    return result; // / groupesConnexes.size();
  }

  /**
   * Bourrin: a optimiser.
   * @param liens
   * @param popRef
   * @param popComp
   * @param param
   */
  public static void ajoutPetitesSurfaces(EnsembleDeLiens liens, IFeatureCollection<?> popRef,
      IFeatureCollection<?> popComp, ParametresAppSurfaces param) {
    Set<IFeature> objetsRefLies = new HashSet<>();
    if (!popRef.hasSpatialIndex()) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER
            .debug(I18N.getString("AppariementSurfaces.SpatialIndexReferenceSurfaces") + new Time(System.currentTimeMillis())); //$NON-NLS-1$
      }
      popRef.initSpatialIndex(Tiling.class, true);
    }
    for (Lien lien : liens) {
      objetsRefLies.addAll(lien.getObjetsRef());
    }
    for (IFeature objetRef : popRef) {
      if (objetsRefLies.contains(objetRef)) {
        continue;
      }
      // cas d'un objet non apparié
      Collection<? extends IFeature> objetsAdjacents = popRef.select(objetRef.getGeom());
      if (objetsAdjacents == null || objetsAdjacents.size() == 1) {
        continue;
      }
      objetsAdjacents.remove(objetRef);
      Iterator<? extends IFeature> iterator = objetsAdjacents.iterator();
      IFeature feature = iterator.next(); // the first element
      Lien lienAdjacent = AppariementSurfaces.liensDeObj(feature, liens);
      double surfAdj = feature.getGeom().area();
      if (lienAdjacent == null) {
        continue;
      }
      boolean tousPareils = true;
      while (iterator.hasNext()) {
        feature = iterator.next();
        Lien lien2 = AppariementSurfaces.liensDeObj(feature, liens);
        if (lienAdjacent != lien2) {
          tousPareils = false;
          break;
        }
        surfAdj = surfAdj + feature.getGeom().area();
      }
      if (!tousPareils) {
        continue;
      }
      if (objetRef.getGeom().area() / surfAdj > param.seuilPourcentageTaillePetitesSurfaces) {
        continue;
      }
      lienAdjacent.addObjetRef(objetRef);
    }
  }

  private static Lien liensDeObj(IFeature objet, EnsembleDeLiens liens) {
    for (Lien lien : liens) {
      if (lien.getObjetsRef().contains(objet)) {
        return lien;
      }
    }
    return null;
  }

  public static EnsembleDeLiens filtreLiens(EnsembleDeLiens liensRegroupes,
      ParametresAppSurfaces param) {
    EnsembleDeLiens liensFiltres = new EnsembleDeLiens();
    for (Lien lien : liensRegroupes) {
      // si on depasse le seuil acceptable: on refuse.
      if (param.minimiseDistanceSurfacique) {
        double distSurf = lien.getDistanceSurfacique();
        if (distSurf < param.distSurfMaxFinal) {
          Lien lienOK = liensFiltres.nouvelElement();
          lienOK.copie(lien);
          lienOK.setEvaluation(distSurf);
          lienOK.setArcs(lien.getArcs());
        }
      } else {
        if ((lien.exactitude() > param.completudeExactitudeMinFinal)
            && (lien.completude() > param.completudeExactitudeMinFinal)) {
          Lien lienOK = liensFiltres.nouvelElement();
          lienOK.copie(lien);
          lienOK.setEvaluation(lien.getExactitude() + lien.getCompletude());
          lienOK.setArcs(lien.getArcs());
        }
      }
    }
    for (Lien lien : liensFiltres) {
      LOGGER.info("lien " + lien.getObjetsRef().size() + " - " + lien.getObjetsComp().size() + " "
          + lien.getEvaluation());
    }
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER
          .debug(I18N.getString("AppariementSurfaces.NumberOfRemainingLinksAfterFiltering") + liensFiltres.size()); //$NON-NLS-1$
    }
    return liensFiltres;
  }

  public static void creeGeometrieDesLiens(EnsembleDeLiens liens, boolean persistant) {
    if (persistant) {
      DataSet.db.begin();
    }
    if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
      AppariementSurfaces.LOGGER.debug(I18N.getString("AppariementSurfaces.LinkGeometryCreation")); //$NON-NLS-1$
    }
    for (Lien lien : liens) {
      if (lien.getObjetsRef().isEmpty()) {
        if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
          AppariementSurfaces.LOGGER.debug(I18N
              .getString("AppariementSurfaces.WarningLinkWithoutReferenceObject")); //$NON-NLS-1$
        }
        continue;
      }
      if (lien.getObjetsComp().isEmpty()) {
        if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
          AppariementSurfaces.LOGGER.debug(I18N
              .getString("AppariementSurfaces.WarningLinkWithoutComparisonObject")); //$NON-NLS-1$
        }
        continue;
      }
      if (persistant) {
        DataSet.db.makePersistent(lien);
      }
      GM_Aggregate<IGeometry> geomLien = new GM_Aggregate<>();
      lien.setGeom(geomLien);
      for (Arc arc : lien.getArcs()) {
        Lien l = (Lien) arc.getCorrespondant(0);
        IDirectPosition p1 = l.getObjetsRef().get(0).getGeom().centroid();
        IDirectPosition p2 = l.getObjetsComp().get(0).getGeom().centroid();
        geomLien.add(new GM_LineString(p1, p2));
      }
      /*
       * Collection<IFeature> objetsRef = new HashSet<IFeature>();
       * objetsRef.addAll(lien.getObjetsRef());
       * CarteTopo armRef = ARM.creeARMsurObjetsQuelconques(objetsRef);
       * // ajout des traits reliant les objets ref
       * for (Arc arc : armRef.getPopArcs()) {
       * geomLien.add(arc.getGeometrie());
       * }
       * // ajout des points centroides des objets ref
       * for (Noeud noeud : armRef.getPopNoeuds()) {
       * geomLien.add(noeud.getGeometrie());
       * }
       * Collection<IFeature> objetsComp = new HashSet<IFeature>();
       * objetsComp.addAll(lien.getObjetsComp());
       * CarteTopo armComp = ARM.creeARMsurObjetsQuelconques(objetsComp);
       * // ajout des traits reliant les objets ref
       * for (Arc arc : armComp.getPopArcs()) {
       * geomLien.add(arc.getGeometrie());
       * }
       * // ajout des points centroides des objets ref
       * for (Noeud noeud : armComp.getPopNoeuds()) {
       * geomLien.add(noeud.getGeometrie());
       * }
       * // ajout des traits reliant les objets comp au point d'accroche ref
       * IPoint pointAccrocheRef = (armRef.getPopNoeuds().getElements().get(0)) .getGeometrie();
       * for (IFeature objetComp : lien.getObjetsComp()) {
       * GM_Point pointAccrocheComp = new GM_Point(objetComp.getGeom().centroid());
       * // if (pointAccrocheComp==null) continue;
       * GM_LineString trait = new GM_LineString(pointAccrocheRef.getPosition(), pointAccrocheComp
       * .getPosition());
       * geomLien.add(trait);
       * // pour faire joli :
       * // Vecteur V = new Vecteur();
       * // V.setX(0.1);V.setY(0);
       * // geomLien.add(V.translate(trait));
       * }
       */
    }
    if (persistant) {
      if (AppariementSurfaces.LOGGER.isDebugEnabled()) {
        AppariementSurfaces.LOGGER
            .debug(I18N.getString("AppariementSurfaces.Commit") + new Time(System.currentTimeMillis())); //$NON-NLS-1$
      }
      DataSet.db.commit();
    }
  }

  @SuppressWarnings("unchecked")
  public static void writeShapefile(EnsembleDeLiens liens, String file) throws SchemaException, IOException {
    ShapefileDataStore store = new ShapefileDataStore(new File(file).toURI().toURL());
    String specs = "the_geom:LineString,linkId:Integer,edgeId:Integer,refId:Integer,compId:Integer,evalLink:Double,surfDist:Double,exact:Double,compl:Double"; //$NON-NLS-1$
    SimpleFeatureType type = DataUtilities.createType("Link", specs);
    store.createSchema(type);
    FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) store.getFeatureSource("Link");
    Transaction t = new DefaultTransaction();
    DefaultFeatureCollection collection = new DefaultFeatureCollection("coll",type);
    
    int linkId = 1;
    int edgeId = 1;
    GeometryFactory factory = new GeometryFactory();
    for (Lien feature : liens) {
      for (Arc edge : feature.getArcs()) {
        Lien l = (Lien) edge.getCorrespondant(0);
        List<Object> liste = new ArrayList<>(5);
        IFeature source = l.getObjetsRef().get(0);
        IFeature target = l.getObjetsComp().get(0);
        IDirectPosition sourcePosition = source.getGeom().centroid();
        IDirectPosition targetPosition = target.getGeom().centroid();
        liste.add(factory.createLineString(new Coordinate[]{new Coordinate(sourcePosition.getX(), sourcePosition.getY()), new Coordinate(targetPosition.getX(), targetPosition.getY())}));
        liste.add(linkId);
        liste.add(edgeId);
        liste.add(source.getAttribute("buildingId"));
        liste.add(target.getAttribute("buildingId"));
        liste.add(feature.getEvaluation());
        liste.add(feature.getDistanceSurfacique());
        liste.add(feature.getExactitude());
        liste.add(feature.getCompletude());
        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(), String
            .valueOf(edgeId++));
        collection.add(simpleFeature);
      }
      linkId++;
    }
    featureStore.addFeatures(collection);
    t.commit();
    t.close();
    store.dispose();
  }
}
