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

package fr.ign.cogit.geoxygene.tutorial.exemple.appariement;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/** Exemple de paramètres pour l'appariement
 *
 *  @author Sébastien Mustière - IGN / Laboratoire COGIT
 *  @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class Parametres {

	/** paramètres d'appariement par défaut avec utilisation de deux collections
	 *  de Tronçons en entrée
	 *  
	 * @param populationsArcs1
	 * @param populationsArcs2
	 * @return a set of default parameters to be used with two sets of edges
	 */
	@SuppressWarnings("unchecked")
	public static ParametresApp parametresDefaut (
			FT_FeatureCollection<? extends FT_Feature> populationsArcs1,
			FT_FeatureCollection<? extends FT_Feature> populationsArcs2) {
		
		
		ParametresApp param = new ParametresApp();

		/////////////////////////////////////////////////////////////////////////////////
		/////////////   PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES   ////////////
		/////////////////////////////////////////////////////////////////////////////////

		//Liste des classes d'arcs de la BD 1 (la moins d�taill�e) concernés par l'appariement
		param.populationsArcs1.add((FT_FeatureCollection<Arc>) populationsArcs1);
		// Liste des classes d'arcs de la BD 2 (la plus d�taill�e) concernés par l'appariement
		param.populationsArcs2.add((FT_FeatureCollection<Arc>) populationsArcs2);

		param.populationsArcsAvecOrientationDouble = true;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////////             TAILLES DE RECHERCHE        ///////////////////////
		/////////////////        Ecarts de distance autoris�s     ///////////////////////
		/////////////////      CE SONT LES PARAMETRES PRINCIPAUX  ///////////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.distanceNoeudsMax = 150;
		param.distanceNoeudsImpassesMax = -1;
		param.distanceArcsMax = 100;
		param.distanceArcsMin = 30;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////         TRAITEMENTS TOPOLOGIQUES A L'IMPORT       /////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.topologieSeuilFusionNoeuds1 = -1;
		param.topologieSeuilFusionNoeuds2 = -1;
		param.topologieSurfacesFusionNoeuds1 = null;
		param.topologieSurfacesFusionNoeuds2 = null;
		param.topologieElimineNoeudsAvecDeuxArcs1 = false;
		param.topologieElimineNoeudsAvecDeuxArcs2 = false;
		param.topologieGraphePlanaire1 = false;
		param.topologieGraphePlanaire2 = false;
		param.topologieFusionArcsDoubles1 = false;
		param.topologieFusionArcsDoubles2 = false;

		/////////////////////////////////////////////////////////////////////////////////
		//////////   TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT       ///////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.projeteNoeuds1SurReseau2 = false;
		param.projeteNoeuds1SurReseau2_DistanceNoeudArc = 0;
		param.projeteNoeuds1SurReseau2_DistanceProjectionNoeud = 0;
		param.projeteNoeuds1SurReseau2_ImpassesSeulement = false;
		param.projeteNoeud2surReseau1 = false;
		param.projeteNoeud2surReseau1_DistanceNoeudArc = 0;
		param.projeteNoeud2surReseau1_DistanceProjectionNoeud = 0;
		param.projeteNoeud2surReseau1_ImpassesSeulement = false;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////            VARIANTES DU PROCESSUS GENERAL    //////////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.varianteForceAppariementSimple = false;
		param.varianteRedecoupageArcsNonApparies = false;
		param.varianteRedecoupageNoeudsNonApparies = false;
		param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;
		param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;
		param.varianteFiltrageImpassesParasites = false;
		param.varianteChercheRondsPoints = false;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////        OPTIONS D'EXPORT                                ////////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.exportGeometrieLiens2vers1 = true;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////        OPTIONS DE DEBUG                                ////////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.debugAffichageCommentaires = 1;
		param.debugBilanSurObjetsGeo = true;
		param.debugTirets = true;
		param.debugPasTirets = 50;
		param.debugBuffer = false;
		param.debugTailleBuffer = 10;
		
		return param;
	}
	
	/** paramètres d'appariement par défaut avec utilisation de deux collections
	 *  de Tronçons et deux collections de noeuds en entrée
	 * 
	 * @param populationsArcs1
	 * @param populationsNoeuds1
	 * @param populationsArcs2
	 * @param populationsNoeuds2
	 * @return default parameters to be used with two collections of edges and nodes
	 */
	@SuppressWarnings("unchecked")
	public static ParametresApp parametresAvecNoeudsDefaut (
			FT_FeatureCollection<? extends FT_Feature> populationsArcs1,
			FT_FeatureCollection<? extends FT_Feature> populationsNoeuds1,
			FT_FeatureCollection<? extends FT_Feature> populationsArcs2,
			FT_FeatureCollection<? extends FT_Feature> populationsNoeuds2) {
		
		
		ParametresApp param = new ParametresApp();

		/////////////////////////////////////////////////////////////////////////////////
		/////////////   PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES   ////////////
		/////////////////////////////////////////////////////////////////////////////////

		//Liste des classes d'arcs de la BD 1 (la moins d�taill�e) concernés par l'appariement
		param.populationsArcs1.add((FT_FeatureCollection<Arc>) populationsArcs1);
		//Liste des classes de noeuds de la BD 1 (la moins d�taill�e) concernés par l'appariement
		param.populationsNoeuds1.add((FT_FeatureCollection<Noeud>) populationsNoeuds1);
		// Liste des classes d'arcs de la BD 2 (la plus d�taill�e) concernés par l'appariement
		param.populationsArcs2.add((FT_FeatureCollection<Arc>) populationsArcs2);
		//Liste des classes de noeuds de la BD 2 (la plus d�taill�e) concernés par l'appariement
		param.populationsNoeuds2.add((FT_FeatureCollection<Noeud>) populationsNoeuds2);

		param.populationsArcsAvecOrientationDouble = true;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////////             TAILLES DE RECHERCHE        ///////////////////////
		/////////////////        Ecarts de distance autoris�s     ///////////////////////
		/////////////////      CE SONT LES PARAMETRES PRINCIPAUX  ///////////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.distanceNoeudsMax = 150;
		param.distanceNoeudsImpassesMax = -1;
		param.distanceArcsMax = 100;
		param.distanceArcsMin = 30;
		
		/////////////////////////////////////////////////////////////////////////////////
		/////////////         TRAITEMENTS TOPOLOGIQUES A L'IMPORT       /////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.topologieSeuilFusionNoeuds1 = -1;
		param.topologieSeuilFusionNoeuds2 = -1;
		param.topologieSurfacesFusionNoeuds1 = null;
		param.topologieSurfacesFusionNoeuds2 = null;
		param.topologieElimineNoeudsAvecDeuxArcs1 = false;
		param.topologieElimineNoeudsAvecDeuxArcs2 = false;
		param.topologieGraphePlanaire1 = false;
		param.topologieGraphePlanaire2 = false;
		param.topologieFusionArcsDoubles1 = false;
		param.topologieFusionArcsDoubles2 = false;

		/////////////////////////////////////////////////////////////////////////////////
		//////////   TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT       ///////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.projeteNoeuds1SurReseau2 = false;
		param.projeteNoeuds1SurReseau2_DistanceNoeudArc = 0;
		param.projeteNoeuds1SurReseau2_DistanceProjectionNoeud = 0;
		param.projeteNoeuds1SurReseau2_ImpassesSeulement = false;
		param.projeteNoeud2surReseau1 = false;
		param.projeteNoeud2surReseau1_DistanceNoeudArc = 0;
		param.projeteNoeud2surReseau1_DistanceProjectionNoeud = 0;
		param.projeteNoeud2surReseau1_ImpassesSeulement = false;
		
		/////////////////////////////////////////////////////////////////////////////////
		/////////////            VARIANTES DU PROCESSUS GENERAL    //////////////////////
		/////////////////////////////////////////////////////////////////////////////////

		param.varianteForceAppariementSimple = false;
		param.varianteRedecoupageArcsNonApparies = false;
		param.varianteRedecoupageNoeudsNonApparies = false;
		param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;
		param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;
		param.varianteFiltrageImpassesParasites = false;
		param.varianteChercheRondsPoints = false;
		
		/////////////////////////////////////////////////////////////////////////////////
		/////////////        OPTIONS D'EXPORT                                ////////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.exportGeometrieLiens2vers1 = true;

		/////////////////////////////////////////////////////////////////////////////////
		/////////////        OPTIONS DE DEBUG                                ////////////
		/////////////////////////////////////////////////////////////////////////////////
		
		param.debugAffichageCommentaires = 1;
		param.debugBilanSurObjetsGeo = true;
		param.debugTirets = true;
		param.debugPasTirets = 50;
		param.debugBuffer = false;
		param.debugTailleBuffer = 10;
		
		
		return param;
	}
	
}