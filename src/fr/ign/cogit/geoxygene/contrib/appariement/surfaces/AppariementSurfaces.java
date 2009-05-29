/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.contrib.appariement.surfaces;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.graphe.ARM;
import fr.ign.cogit.geoxygene.contrib.operateurs.Ensemble;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Appariement de surfaces.
 * Processus défini dans la thèse de Atef Bel Hadj Ali (2001),
 * et resume dans le rapport [Mustiere 2002]:
 * ("Description des processus d'appariement mis en oeuvre au COGIT",SR/2002.0072, chap.6).
 * 
 * @author Braun & Mustière - Laboratoire COGIT
 * version 1.0
 * 
 */

public abstract class AppariementSurfaces {

	/**
	 * Appariement entre deux ensembles de surfaces.
	 * Processus inspiré de celui défini dans la thèse de Atef Bel Hadj Ali (2001),
	 * et resumé dans le rapport de Seb
	 * ("Description des processus d'appariement mise en oeuvre au COGIT",SR/2002.0072, chap.6).
	 * 
	 * NB 1 : LE CAS DES LIENS N-M N'EST PAS VRAIMENT SATIFAISANT
	 * ET DOIT ENCORE ETRE REVU (reflechir aux mesures). Néanmoins...
	 * le processus a été amélioré pour mieux raffiner le traitement des liens n-m :
	 * un lien n-m issu du regroupement des liens 1-1 peut être redécoupé en
	 * plusieurs liens n'-m', alors que le processus d'Atef ne semble permettre
	 * que de simplifier ce groupe n-m en UN seul groupe n'-m' (n'<=n, m'<=m)
	 * 
	 * NB 2 :Les liens finaux sont qualifiés (evaluation) par la mesure de
	 * distance surfacique entre groupes de surfaces.
	 * 
	 * NB 3 : si la population de référence n'est pas indexée, elle le sera pendant le calcul
	 * 
	 * NB 4 : l'appariement est symétrique (si ref et comp sont échangés, les résultats sont identiques)
	 * 
	 * @param popRef : population des objets de référence.
	 * 		Ces objets doivent avoir une géométrie "geom" de type GM_Polygon
	 * @param popComp : population des objets de comparaison
	 * 		Ces objets doivent avoir une géométrie "geom" de type GM_Polygon
	 * @param param : paramètres de l'appariement
	 * 
	 * 
	 * @return : liens d'appariement calculés. Ces liens peuvent être de type n-m.
	 */
	public static EnsembleDeLiens appariementSurfaces(FT_FeatureCollection<FT_Feature> popRef, FT_FeatureCollection<FT_Feature> popComp, ParametresAppSurfaces param) {
		EnsembleDeLiens liensPreApp, liensRegroupes, liensFiltres;

		// indexation au besoin des surfaces de comparaison
		if ( ! popComp.hasSpatialIndex() ) {
			System.out.println("-- Indexation spatiale des surfaces de comparaison "+new Time(System.currentTimeMillis()));
			popComp.initSpatialIndex(Tiling.class, true);
		}

		// pré-appariement selon un test sur la surface de l'intersection
		// entre les surfaces de référence et de comparaison
		System.out.println("-- Pre-appariement sur des critères d'intersection "+new Time(System.currentTimeMillis()));
		liensPreApp = preAppariementSurfaces(popRef, popComp, param);

		// appariement par recherche des regroupements optimaux
		if ( param.regroupementOptimal ) {
			System.out.println("-- Appariement par recherche des regroupements optimaux : "+new Time(System.currentTimeMillis()));
			liensRegroupes = rechercheRegroupementsOptimaux(liensPreApp, popRef, popComp, param);
		}
		else liensRegroupes = liensPreApp;

		// recollage des petites surfaces non encore appariées
		if ( param.ajoutPetitesSurfaces ) {
			System.out.println("-- Recollage des petites surfaces non encore appariées : "+new Time(System.currentTimeMillis()));
			ajoutPetitesSurfaces(liensRegroupes, popRef, popComp, param);
		}

		// filtrage final pour n'accepter que les appariements suffisament bons
		if ( param.filtrageFinal) {
			System.out.println("-- Filtrage final : "+new Time(System.currentTimeMillis()));
			liensFiltres = filtreLiens(liensRegroupes, param);
		}
		else liensFiltres = liensRegroupes;


		System.out.println("-- Création de la géométrie des liens : "+new Time(System.currentTimeMillis()));
		creeGeometrieDesLiens(liensFiltres, param.persistant);

		System.out.println("Fin du traitement");

		return liensFiltres;
	}


	/** 2 surfaces sont pré-appariées si elles respectent le "test d'association"
	 * défini par Atef Bel Hadj Ali (2001). C'est-à-dire si :
	 * 1/ l'intersection des surfaces a une taille supérieure au seuil "surface_min"
	 * ET
	 * 2/ l'intersection fait au moins la taille d'une des surfaces multipliée
	 * 	   par le paramètre "pourcentage_min".
	 * 
	 * NB 1 : Par construction : chaque lien pointe vers UN SEUL objet de la population
	 * de référence et vers UN SEUL objet de la population de comparaison.
	 * NB 2 : Aucune géométrie n'est instanciée pour les liens créés.
	 * NB 3 : l'appariement est symétrique.
	 * NB 4 : la population de comparaison est indexée si elle ne l'était pas avant
	 * 
	 * @param popRef : population des objets de référence.
	 * @param popComp : population des objets de comparaison.
	 * @param param : paramètres de l'appariement.
	 * 
	 * @return : liens de pré-appariement calculés.
	 */
	public static EnsembleDeLiens preAppariementSurfaces(FT_FeatureCollection<?> popRef, FT_FeatureCollection<?> popComp, ParametresAppSurfaces param) {

		EnsembleDeLiens preAppLiens = new EnsembleDeLiens();
		Lien lien ;
		FT_FeatureCollection<?> candidatComp;
		FT_Feature featureRef, featureComp;
		GM_Object geomRef, geomComp;
		double surfaceIntersection, pourcentageRecouvrement;

		if ( ! popComp.hasSpatialIndex() ) {
			System.out.println("Indexation spatiale de la poplation des surfaces de comparaison");
			popComp.initSpatialIndex(Tiling.class, true);
		}

		Iterator<? extends FT_Feature> iterator = popRef.iterator();
		while (iterator.hasNext()) {
			featureRef = iterator.next();
			geomRef = featureRef.getGeom();
			if ( !(geomRef instanceof GM_Surface)) {
				System.out.println("Objet de la population de référence sans géométrie surfacique COGITID = "+featureRef.getId());
				continue;
			}

			// Test d'association sur tous les objets comp intersectant l'objet ref
			candidatComp = popComp.select(geomRef);
			iterator = candidatComp.iterator();
			while (iterator.hasNext()) {
				featureComp = iterator.next();
				geomComp = featureComp.getGeom();
				if ( !(geomComp instanceof GM_Surface)) {
					System.out.println("Objet de la population de comparaison sans géométrie surfacique COGITID = "+featureComp.getId());
					continue;
				}
				// création éventuelle d'un nouveau lien de pré-appariement
				GM_Object inter = Operateurs.intersectionRobuste(geomRef,geomComp,param.resolutionMin,param.resolutionMax);
				if (inter == null) continue; // si plantage aux calculs d'intersection
				surfaceIntersection = inter.area();
				if (surfaceIntersection<=param.surface_min_intersection) continue;
				pourcentageRecouvrement= Math.max(surfaceIntersection/geomRef.area(), surfaceIntersection/geomComp.area());
				if ( pourcentageRecouvrement<param.pourcentage_min_intersection) continue; //intersection pas suffisante
				lien = preAppLiens.nouvelElement();
				lien.addObjetRef(featureRef);
				lien.addObjetComp(featureComp);
				lien.setEvaluation(pourcentageRecouvrement);
			}
		}
		System.out.println("   Nombre de liens (1-1) créés au pré-appariement "+preAppLiens.size());
		return preAppLiens;
	}

	/** On recherche les regroupements optimaux de liens de pré-appariement, pour
	 * maximiser la distance surfacique entre les groupes de référence et de comparaison.
	 * 
	 * NB : l'appariement est symétrique
	 * 
	 * @param param : paramètres de l'appariement
	 * @param liensPreApp : liens issus du pré-appariement
	 * @return : liens d'appariement calculés (contient des objets de la classe Lien).
	 * Ces liens sont des liens n-m.
	 */
	public static EnsembleDeLiens rechercheRegroupementsOptimaux(EnsembleDeLiens liensPreApp, FT_FeatureCollection<FT_Feature> popRef, FT_FeatureCollection<FT_Feature> popComp, ParametresAppSurfaces param) {
		EnsembleDeLiens liensGroupes;
		Lien lienGroupe, lienArc;
		double distSurfMin, distExacMax, dist;
		CarteTopo grapheDesLiens ;
		Groupe groupeTotal, groupeConnexe, groupeLight;
		Iterator<Groupe> itGroupes;
		Iterator<List<Object>> itCombinaisons;
		Iterator<Noeud> itNoeuds;
		Iterator<Arc> itArcs;
		List<Groupe> groupesConnexes ;
		List<Arc> arcsEnlevables;
		List<Object> arcsNonEnlevables, arcsDuGroupeEnleves, arcsDuGroupeEnlevesFinal;
		Noeud noeud;
		FT_Feature feat;
		int i=0;
		List<Groupe> groupesGardes = new ArrayList<Groupe>();
		List<Groupe> groupesDecomposes ;

		//on crée les liens n-m (groupes connexes du graphe des liens)
		grapheDesLiens = liensPreApp.transformeEnCarteTopo(popRef, popComp);
		groupeTotal = grapheDesLiens.getPopGroupes().nouvelElement();
		groupeTotal.setListeArcs(grapheDesLiens.getListeArcs());
		groupeTotal.setListeNoeuds(grapheDesLiens.getListeNoeuds());
		groupesConnexes = groupeTotal.decomposeConnexes();
		System.out.println("Nombre de liens (n-m) à traiter "+groupesConnexes.size());

		// on parcours tous les liens n-m créés
		itGroupes = groupesConnexes.iterator();
		while (itGroupes.hasNext()) {
			i++;
			if ( i % 10000  == 0 ) System.out.println("Nb de groupes traités : "+i+"     "+new Time(System.currentTimeMillis()));
			groupeConnexe = itGroupes.next();

			// pour les objets isolés ou les liens 1-1, on ne fait rien de plus
			if ( groupeConnexe.getListeArcs().size() == 0 ) continue;
			if ( groupeConnexe.getListeArcs().size() == 1 ) {
				groupesGardes.add(groupeConnexe);
				continue;
			}

			// pour les groupes n-m, on va essayer d'enlever des arcs
			// mais on garde à coup sûr les liens avec suffisament de recouvremnt
			arcsEnlevables =  new ArrayList<Arc>(groupeConnexe.getListeArcs());
			arcsNonEnlevables=new ArrayList<Object>();
			itArcs = arcsEnlevables.iterator();
			while (itArcs.hasNext()) {
				Arc arc =  itArcs.next();
				lienArc= (Lien)arc.getCorrespondant(0);
				if (lienArc.getEvaluation()>param.pourcentage_intersection_sur) arcsNonEnlevables.add(arc);
			}
			arcsEnlevables.removeAll(arcsNonEnlevables);
			if (arcsEnlevables.size() == 0) { //si on ne peut rien enlever, on s'arrête là
				groupesGardes.add(groupeConnexe);
				continue;
			}

			//on cherche à enlever toutes les combinaisons possibles d'arcs virables
			itCombinaisons = Ensemble.combinaisons(new ArrayList<Object>(arcsEnlevables)).iterator();
			distSurfMin = 2; //	cas de distance surfacique à minimiser
			distExacMax = 0; // cas de completude / exactitude à maximiser
			arcsDuGroupeEnlevesFinal = new ArrayList<Object>();
			while (itCombinaisons.hasNext()) { //boucle sur les combinaisons possibles
				arcsDuGroupeEnleves = itCombinaisons.next();
				groupeLight = groupeConnexe.copie();
				groupeLight.getListeArcs().removeAll(arcsDuGroupeEnleves);
				if (groupeLight.getListeArcs().size() == 0) continue; //on refuse la solution extreme de ne rien garder comme apparié
				dist = mesureEvaluationGroupe(groupeLight, popRef, popComp, param);
				if ( param.minimiseDistanceSurfacique ) {
					//	cas de distance surfacique à minimiser
					if ( dist < distSurfMin ) {
						distSurfMin = dist ;
						arcsDuGroupeEnlevesFinal = arcsDuGroupeEnleves;
					}
				}
				else {
					// cas de completude / exactitude à maximiser
					if ( dist > distExacMax ) {
						distExacMax = dist ;
						arcsDuGroupeEnlevesFinal = arcsDuGroupeEnleves;
					}
				}
			} //fin boucle sur les combinaisons possibles
			groupeConnexe.getListeArcs().removeAll(arcsDuGroupeEnlevesFinal); //simplification finale des liens d'appariement du groupe
			groupesDecomposes = groupeConnexe.decomposeConnexes(); //création des groupes finaux gardés
			groupesGardes.addAll(groupesDecomposes);
		}

		// création des liens retenus (passage de structure graphe à liens):
		// les groupes qui restent dans la carte topo représentent les liens finaux.
		liensGroupes = new EnsembleDeLiens();
		liensGroupes.setNom("Liens issus d'un appariement de surfaces");
		// on parcours tous les groupes connexes créés
		itGroupes = groupesGardes.iterator();
		while (itGroupes.hasNext()) {
			groupeConnexe = itGroupes.next();
			if ( groupeConnexe.getListeArcs().size() == 0 ) continue; // cas des noeuds isolés
			//if ( groupeConnexe.getListenoeuds().size() == 0 ) continue;
			lienGroupe = liensGroupes.nouvelElement();
			itNoeuds = groupeConnexe.getListeNoeuds().iterator();
			while (itNoeuds.hasNext()) {
				noeud = itNoeuds.next();
				feat = noeud.getCorrespondant(0);
				if ( popRef.getElements().contains(feat) ) lienGroupe.addObjetRef(feat);
				if ( popComp.getElements().contains(feat) ) lienGroupe.addObjetComp(feat);
				// nettoyage de la carteTopo créée
				noeud.setCorrespondants(new ArrayList<FT_Feature>());
			}
		}
		return liensGroupes;
	}

	/** Distance surfacique ou complétude "étendue" sur un groupe.
	 * Attention: vide le groupe au passage.
	 * MESURE NON SATISFAISANTE POUR LIENS N-M : moyenne entre parties connexes A REVOIR */
	@SuppressWarnings("unchecked")
	private static double mesureEvaluationGroupe(Groupe groupe, FT_FeatureCollection<?> popRef, FT_FeatureCollection<?> popComp, ParametresAppSurfaces param) {
		List<?> groupesConnexes;
		Iterator<?> itGroupes, itNoeuds;
		GM_MultiSurface geomRef, geomComp;
		double dist, distTot = 0;
		Groupe groupeConnnexe;
		Noeud noeud;
		FT_Feature feat;


		// on décompose le groupe en parties connexes
		groupesConnexes = groupe.decomposeConnexes();
		itGroupes = groupesConnexes.iterator();
		while (itGroupes.hasNext()){
			// on mesure la qualité de l'appariement pour chaque partie connexe,
			groupeConnnexe = (Groupe)itGroupes.next();
			if ( groupeConnnexe.getListeArcs().size() == 0 ) continue;
			geomRef = new GM_MultiSurface<GM_OrientableSurface>();
			geomComp = new GM_MultiSurface<GM_OrientableSurface>();
			itNoeuds = groupeConnnexe.getListeNoeuds().iterator();
			while ( itNoeuds.hasNext() ) {
				noeud = (Noeud)itNoeuds.next();
				feat = noeud.getCorrespondant(0);
				//if ( feat.getPopulation()==popRef ) geomRef.add(feat.getGeom());
				//if ( feat.getPopulation()==popComp ) geomComp.add(feat.getGeom());
				if ( popRef.getElements().contains(feat) ) geomRef.add(feat.getGeom());
				if ( popComp.getElements().contains(feat) ) geomComp.add(feat.getGeom());
			}
			if ( param.minimiseDistanceSurfacique ){
				dist = Distances.distanceSurfaciqueRobuste(geomRef, geomComp);
			}
			else dist = Distances.exactitude(geomRef, geomComp) + Distances.completude(geomRef, geomComp);
			// on combine les mesures des parties connexes
			distTot = distTot + dist;
		}

		return distTot; //    / groupesConnexes.size();
	}

	/**
	 * Bourrin: a optimiser
	 * @param liens
	 * @param popRef
	 * @param popComp
	 * @param param
	 */
	public static void ajoutPetitesSurfaces(EnsembleDeLiens liens, FT_FeatureCollection<?> popRef, FT_FeatureCollection<?> popComp, ParametresAppSurfaces param) {
		Iterator<?> itRef = popRef.getElements().iterator();
		Iterator<?> itLiens = liens.getElements().iterator();
		Set<FT_Feature> objetsRefLies = new HashSet<FT_Feature>();
		FT_FeatureCollection<?> objetsAdjacents;
		Lien lienAdjacent, lien2;
		double surfAdj;
		boolean tousPareils;

		if ( ! popRef.hasSpatialIndex() ) {
			System.out.println("Indexation spatiale des surfaces de référence "+new Time(System.currentTimeMillis()));
			popRef.initSpatialIndex(Tiling.class, true);
		}

		while (itLiens.hasNext()) {
			Lien lien = (Lien) itLiens.next();
			objetsRefLies.addAll(lien.getObjetsRef());
		}

		while (itRef.hasNext()) {
			FT_Feature objetRef = (FT_Feature) itRef.next();
			if (objetsRefLies.contains(objetRef)) continue;
			// cas d'un objet non apparié
			objetsAdjacents=popRef.select(objetRef.getGeom());
			if (objetsAdjacents == null) continue;
			if (objetsAdjacents.size() == 1) continue;
			objetsAdjacents.remove(objetRef);
			lienAdjacent = liensDeObj(objetsAdjacents.get(0), liens);
			surfAdj = ((FT_Feature)objetsAdjacents.get(0)).getGeom().area();
			if (lienAdjacent == null) continue;
			tousPareils=true;
			for(int i=1;i<objetsAdjacents.size();i++) {
				lien2 = liensDeObj(objetsAdjacents.get(i), liens);
				if (lienAdjacent!=lien2) {
					tousPareils=false;
					break;
				}
				surfAdj = surfAdj + ((FT_Feature)objetsAdjacents.get(i)).getGeom().area();
			}
			if (!tousPareils) continue;
			if ( objetRef.getGeom().area() / surfAdj > param.seuilPourcentageTaillePetitesSurfaces) continue;
			lienAdjacent.addObjetRef(objetRef);
		}
	}

	private static Lien liensDeObj(FT_Feature objet, EnsembleDeLiens liens) {
		Iterator<?> itLiens = liens.getElements().iterator();

		while (itLiens.hasNext()) {
			Lien lien = (Lien) itLiens.next();
			if (lien.getObjetsRef().contains(objet))return lien;
		}
		return null;
	}


	public static EnsembleDeLiens filtreLiens(EnsembleDeLiens liensRegroupes,ParametresAppSurfaces param) {
		EnsembleDeLiens liensFiltres = new EnsembleDeLiens();
		Lien lien, lienOK ;
		double distSurf;

		Iterator<?> itGroupes = liensRegroupes.getElements().iterator();
		while (itGroupes.hasNext()) {
			lien = (Lien) itGroupes.next();
			// si on depasse le seuil acceptable: on refuse.
			if ( param.minimiseDistanceSurfacique) {
				distSurf = lien.distanceSurfaciqueRobuste();
				if (distSurf < param.distSurfMaxFinal ) {
					lienOK = liensFiltres.nouvelElement();
					lienOK.copie(lien);
					lienOK.setEvaluation(distSurf);
				}
			}
			else {
				if ((lien.exactitude() > param.completudeExactitudeMinFinal )
						&& (lien.completude() > param.completudeExactitudeMinFinal)){
					lienOK = liensFiltres.nouvelElement();
					lienOK.copie(lien);
				}
			}
		}
		System.out.println("   Nombre de liens retenus après filtrage : "+liensFiltres.size());
		return liensFiltres;
	}


	public static void creeGeometrieDesLiens(EnsembleDeLiens liens, boolean persistant) {
		FT_FeatureCollection<FT_Feature> objetsRef;
		FT_FeatureCollection<FT_Feature> objetsComp;
		FT_Feature objetComp;
		GM_Point pointAccrocheRef, pointAccrocheComp;
		CarteTopo armRef, armComp;
		Iterator<?> itLiens = liens.getElements().iterator();
		Iterator<?> itArcs, itNoeuds, itObjetsComp;
		Arc arc;
		Noeud noeud;
		GM_Aggregate<GM_Object> geomLien;


		if (persistant) DataSet.db.begin();
		System.out.println("Création de la géométrie des liens");
		while (itLiens.hasNext()) {
			Lien lien = (Lien) itLiens.next();
			if (lien.getObjetsRef().size() == 0) {
				System.out.println("Attention, lien sans objet de référence");
				continue;
			}
			if (lien.getObjetsComp().size() == 0) {
				System.out.println("Attention, lien sans objet de comparaison");
				continue;
			}

			if (persistant) DataSet.db.makePersistent(lien);
			geomLien = new GM_Aggregate<GM_Object>();
			lien.setGeom(geomLien);


			objetsRef = new FT_FeatureCollection<FT_Feature>();
			objetsRef.getElements().addAll(lien.getObjetsRef());
			armRef=ARM.creeARMsurObjetsQuelconques(objetsRef);
			// ajout des traits reliant les objets ref
			itArcs=armRef.getPopArcs().getElements().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				geomLien.add(arc.getGeometrie());
			}
			// ajout des points centroides des objets ref
			itNoeuds=armRef.getPopNoeuds().getElements().iterator();
			while (itNoeuds.hasNext()) {
				noeud = (Noeud) itNoeuds.next();
				geomLien.add(noeud.getGeometrie());
			}

			objetsComp = new FT_FeatureCollection<FT_Feature>();
			objetsComp.getElements().addAll(lien.getObjetsComp());
			armComp=ARM.creeARMsurObjetsQuelconques(objetsComp);
			// ajout des traits reliant les objets ref
			itArcs=armComp.getPopArcs().getElements().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				geomLien.add(arc.getGeometrie());
			}
			// ajout des points centroides des objets ref
			itNoeuds=armComp.getPopNoeuds().getElements().iterator();
			while (itNoeuds.hasNext()) {
				noeud = (Noeud) itNoeuds.next();
				geomLien.add(noeud.getGeometrie());
			}


			//ajout des traits reliant les objets comp au point d'accroche ref
			pointAccrocheRef=(armRef.getPopNoeuds().getElements().get(0)).getGeometrie();
			itObjetsComp = lien.getObjetsComp().iterator();
			while (itObjetsComp.hasNext()) {
				objetComp = (FT_Feature) itObjetsComp.next();
				pointAccrocheComp = new GM_Point(objetComp.getGeom().centroid());
				if (pointAccrocheComp==null) continue;
				GM_LineString trait = new GM_LineString();
				trait.addControlPoint(pointAccrocheRef.getPosition());
				trait.addControlPoint(pointAccrocheComp.getPosition());
				geomLien.add(trait);
				// pour faire joli :
				//Vecteur V = new Vecteur();
				//V.setX(0.1);V.setY(0);
				//geomLien.add(V.translate(trait));
			}

		}
		if (persistant) {
			System.out.println("-- Stockage: commit: "+new Time(System.currentTimeMillis()));
			DataSet.db.commit();
		}
	}

}