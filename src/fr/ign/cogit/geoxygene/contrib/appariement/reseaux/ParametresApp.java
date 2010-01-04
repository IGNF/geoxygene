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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * paramètres de l'appariement.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */
public class ParametresApp implements Cloneable {

	/////////////////////////////////////////////////////////////////////////////////
	/////////////   PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES   ////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Liste des classes d'arcs de la BD 1 (la moins détaillée) concernés par l'appariement */
	public List<FT_FeatureCollection<Arc>> populationsArcs1 = new ArrayList<FT_FeatureCollection<Arc>>();

	/** Liste des classes de noeuds de la BD 1 (la moins détaillée) concernés par l'appariement */
	public List<FT_FeatureCollection<Noeud>> populationsNoeuds1 = new ArrayList<FT_FeatureCollection<Noeud>>();

	/** Liste des classes d'arcs de la BD 2 (la plus détaillée) concernés par l'appariement */
	public List<FT_FeatureCollection<Arc>> populationsArcs2 = new ArrayList<FT_FeatureCollection<Arc>>();

	/** Liste des classes de noeuds de la BD 2 (la plus détaillée) concernés par l'appariement */
	public List<FT_FeatureCollection<Noeud>> populationsNoeuds2 = new ArrayList<FT_FeatureCollection<Noeud>>();

	/** Prise en compte de l'orientation des arcs sur le terrain (sens de circulation).
	 * Si true : on suppose tous les arcs en double sens.
	 * Si false: on suppose tous les arcs en sens unique, celui défini par la géométrie.
	 * NB: ne pas confondre cette orientation 'Géographique réelle', avec l'orientation de la géométrie.
	 * 
	 * Utile ensuite pour l'appariement des arcs.
	 */
	public boolean populationsArcsAvecOrientationDouble = true;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////////             TAILLES DE RECHERCHE        ///////////////////////
	/////////////////        Ecarts de distance autorisés     ///////////////////////
	/////////////////      CE SONT LES PARAMETRES PRINCIPAUX  ///////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Distance maximale autorisée entre deux noeuds appariés.
	 */
	public float distanceNoeudsMax = 150;

	/** Distance maximale autorisée entre deux noeuds appariés, quand le noeud du réseau 1
	 *  est une impasse uniquement.
	 *  Ce paramètre permet de prendre en compte le fait que la localisation exacte des extrémités d'impasse
	 *  est assez imprécise dans certains réseaux (où commence exactement une rivière par exemple?).
	 * 
	 *  Si cette distance est strictement négative, alors ce paramètre n'est pas pris en compte,
	 *  et la distance maximale est la même pour tous les noeuds, y-compris aux extrémités.
	 */
	public float distanceNoeudsImpassesMax = -1;

	/** Distance maximum autorisée entre les arcs des deux réseaux.
	 * La distance est définie au sens de la "demi-distance de Hausdorf" des arcs
	 * du réseau 2 vers les arcs du réseau 1.
	 */
	public float distanceArcsMax = 100;

	/** Distance minimum sous laquelle l'écart de distance pour divers arcs du réseaux 2
	 * (distance vers les arcs du réseau 1) n'a plus aucun sens.
	 *  Cette distance est typiquement de l'ordre de la précision géométrique du réseau le moins précis.
	 */
	public float distanceArcsMin = 30;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////         TRAITEMENTS TOPOLOGIQUES A L'IMPORT       /////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Les noeuds proches du réseau 1 sont fusionés en un seul noeud
	 *  (proches = éloignés de moins que ce paramètre).
	 *  Si ce paramètre est >0, les noeuds sont fusionés à une position moyenne, et les arcs sont déformés pour suivre.
	 *  Si ce paramètre est =0, seul les noeuds strictement superposés sont fusionés.
	 *  Si ce paramètre est <0 (défaut), aucune fusion n'est faite.
	 */
	public double topologieSeuilFusionNoeuds1 = -1;

	/** Les noeuds proches du réseau 2 sont fusionés en un seul noeud
	 *  (proches = éloignés de moins que ce paramètre).
	 *  Si ce paramètre est >0, les noeuds sont fusionés à une position moyenne, et les arcs sont déformés pour suivre.
	 *  Si ce paramètre est =0, seul les noeuds strictement superposés sont fusionés.
	 *  Si ce paramètre est <0 (défaut), aucune fusion n'est faite.
	 */
	public double topologieSeuilFusionNoeuds2 = -1;

	/** Les noeuds du réseau 1 contenus dans une même surface de la population en paramètre
	 *  seront fusionés en un seul noeud pour l'appariement.
	 *  Ce paramètre peut être null (défaut), auquel il est sans influence.
	 *  Exemple typique: on fusionne toutes les extrémités de lignes ferrés arrivant dans une même aire de triage,
	 *  si les aires de triage sont définies par des surfaces dans les données.
	 */
	public Population<?> topologieSurfacesFusionNoeuds1 = null;

	/** Les noeuds du réseau 2 contenus dans une même surface de la population en paramètre
	 *  seront fusionés en un seul noeud pour l'appariement.
	 *  Ce paramètre peut être null (défaut), auquel il est sans influence.
	 *  Exemple typique: on fusionne toutes les extrémités de lignes ferrés arrivant dans une même aire de triage,
	 *  si les aires de triage sont définies par des surfaces dans les données.
	 */
	public Population<?> topologieSurfacesFusionNoeuds2 = null;

	/** Doit-on eliminer pour l'appariement les noeuds du réseau 1 qui n'ont que 2 arcs incidents
	 *  et fusionner ces arcs ?
	 */
	public boolean topologieElimineNoeudsAvecDeuxArcs1 = false;

	/** Doit-on eliminer pour l'appariement les noeuds du réseau 2 qui n'ont que 2 arcs incidents
	 *  et fusionner ces arcs ?
	 */
	public boolean topologieElimineNoeudsAvecDeuxArcs2 = false;

	/** Doit-on rendre le réseau 1 planaire (créer des noeuds aux intersections d'arcs)?
	 */
	public boolean topologieGraphePlanaire1 = false;

	/** Doit-on rendre le réseau 2 planaire (créer des noeuds aux intersections d'arcs)?
	 */
	public boolean topologieGraphePlanaire2 = false;

	/** Fusion des arcs doubles.
	 * Si true: si le réseau 1 contient des arcs en double (même géométrie exactement),
	 * alors on les fusionne en un seul arc pour l'appariement.
	 * Si false: rien n'est fait.
	 */
	public boolean topologieFusionArcsDoubles1 = false;

	/** Fusion des arcs doubles.
	 * Si true: si le réseau 2 contient des arcs en double (même géométrie exactement),
	 * alors on les fusionne en un seul arc pour l'appariement.
	 * Si false: rien n'est fait.
	 */
	public boolean topologieFusionArcsDoubles2 = false;


	/////////////////////////////////////////////////////////////////////////////////
	//////////   TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT       ///////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Doit on projeter les noeuds du réseau 1 sur le réseau 2 pour découper ce dernier ?
	 * Ce traitement réalise un surdécoupage du réseau 2 qui facilite l'appariement dans certains cas
	 * (par exemple si les réseaux ont des niveaux de détail proches), mais qui va aussi un peu
	 * à l'encontre de la philosophie générale du processus d'appariement.
	 * A utiliser avec modération donc.
	 */
	public boolean projeteNoeuds1SurReseau2 = false;

	/** Distance max de la projection des noeuds 2 sur le réseau 1.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public double projeteNoeuds1SurReseau2DistanceNoeudArc = 0;

	/** Distance min entre la projection d'un noeud sur un arc et les extrémités de cet arc
	 * pour créer un nouveau noeud sur le réseau 2.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public double projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;

	/** Si true: on ne projete que les impasses du réseau 1 sur le réseau 2
	 *  Si false: on projete tous les noeuds du réseau 1 sur le réseau 2.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public boolean projeteNoeuds1SurReseau2ImpassesSeulement = false;


	/** Doit on projeter les noeuds du réseau 2 sur le réseau 1 pour découper ce dernier ?
	 * Ce traitement réalise un surdécoupage du réseau 1 qui facilite l'appariement dans certains cas
	 * (par exemple si les réseaux ont des niveaux de détail proches), mais qui va aussi un peu
	 * à l'encontre de la philosophie générale du processus d'appariement.
	 * A utiliser avec modération donc.
	 */
	public boolean projeteNoeud2surReseau1 = false;

	/** Distance max de la projection des noeuds 1 sur le réseau 2.
	 *  Utile uniquement si projeteNoeuds2SurReseau1 = true.
	 */
	public double projeteNoeud2surReseau1DistanceNoeudArc = 0;

	/** Distance min entre la projection d'un noeud sur un arc et les extrémités de cet arc
	 * pour créer un nouveau noeud sur le réseau 1.
	 *  Utile uniquement si projeteNoeuds2SurReseau1 = true.
	 */
	public double projeteNoeud2surReseau1DistanceProjectionNoeud = 0;

	/** Si true: on ne projete que les impasses du réseau 2 sur le réseau 1
	 *  Si false: on projete tous les noeuds du réseau 2 sur le réseau 1.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public boolean projeteNoeud2surReseau1ImpassesSeulement = false;



	/////////////////////////////////////////////////////////////////////////////////
	/////////////            VARIANTES DU PROCESSUS GENERAL    //////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Niveau de complexité: recherche ou non de liens 1-n aux noeuds.
	 *  Si true: un noeud du réseau 1 est toujours apparié avec au plus un noeud du réseau 2 (1-1).
	 *  Si false (défaut): on recherche liens 1-n aux noeuds .
	 * 
	 *  NB: dans le cas simple, le processus est énormêment simplifié !!!!!!!!
	 *  Ceci peut être pertinent si les données ont le même niveau de détail par exemple.
	 */
	public boolean varianteForceAppariementSimple = false;


	/** Appariement en deux passes qui tente un surdécoupage du réseau pour les arcs non appariés en Première passe.
	 * Si true: les arcs du réseau 1 non appariés dans une Première passe sont redécoupés de manière
	 *          à introduire un noeud dans le réseau 1 aux endroits où il s'éloigne trop du réseau 2.
	 *          Le "trop" est égal à projeteNoeud2surReseau1_DistanceProjectionNoeud.
	 * Si false (défaut): processus en une seule passe.
	 * 
	 * NB: pour l'instant, après ce re-découpage l'appariement est entièrement refait, ce qui est long
	 * et très loin d'être optimisé: code à revoir !!!
	 */
	public boolean varianteRedecoupageArcsNonApparies = false;

	/** Appariement en deux passes qui tente un surdécoupage du réseau pour les noeuds non appariés en Première passe.
	 * Si true: les noeuds du réseau 1 non appariés dans une Première passe sont projetés au plus proche
	 * 			sur le réseau 2 pour le découper
	 * Si false (défaut): processus en une seule passe.
	 * 
	 * Il s'agit en fait de la même opération que celle qui est faite quand 'projeteNoeuds1SurReseau2'='true',
	 * mais uniquement pour les noeuds non appariés en Première passe, et avec des seuils éventuellement
	 * différents et définis par les paramètres suivants :
	 * - redecoupageNoeudsNonAppariesDistanceNoeudArc.
	 * - redecoupageNoeudsNonAppariesDistanceProjectionNoeud
	 * 
	 * NB: pour l'instant, après ce re-découpage l'appariement est entièrement refait, ce qui est long
	 * et très loin d'être optimal: à revoir à l'occasion,
	 */
	public boolean varianteRedecoupageNoeudsNonApparies = false;


	/** Distance max de la projection des noeuds du réseau 1 sur le réseau 2.
	 *  Utilisé uniquement si varianteRedecoupageNoeudsNonApparies = true.
	 */
	public double varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;

	/** Distance min entre la projection d'un noeud sur un arc et les extrémités de cet arc
	 *  pour créer un nouveau noeud sur le réseau 2 ?
	 *  Utilisé uniquement si varianteRedecoupageNoeudsNonApparies = true.
	 */
	public double varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;

	/** Quand un arc est apparié à un ensemble d'arcs, élimine de cet ensemble
	 *  les petites impasses qui créent des aller-retour parasites (de longueur inférieure à distanceNoeuds).
	 *  NB: paramètre spécifique aux réseaux simples, qui permet d'améliorer le recalage.
	 */
	public boolean varianteFiltrageImpassesParasites = false;

	/** Recherche des ronds-points (faces circulaires) dans le réseau 2, pour éviter
	 *  d'apparier un noeud du réseau 1 à une partie seulement d'un rond-point
	 * (si une partie seulement est appariée, tout le rond point devient apparié).
	 *  NB: paramètre utile uniquement pour les réseaux routiers a priori.
	 */
	public boolean varianteChercheRondsPoints = false;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////        OPTIONS D'EXPORT                                ////////////
	/////////////////////////////////////////////////////////////////////////////////
	/** Si true, la géométrie des liens est calculée des objets2 vers les objets 1
	 * si false, c'est l'inverse
	 */
	public boolean exportGeometrieLiens2vers1 = true;

	/////////////////////////////////////////////////////////////////////////////////
	/////////////        OPTIONS DE DEBUG                                ////////////
	/////////////////////////////////////////////////////////////////////////////////
	/** paramètre pour gérer l'affichage des commentaires dans la fenêtre de contrôle
	 * Si c'est égal à 0 : aucun commentaire n'est affiché
	 * Si c'est égal à 1 : le début des grandes étapes et les principaux résultats sont signalés
	 * Si c'est égal à 2 (debug) : tous les messages sont affichés
	 */
	public int debugAffichageCommentaires = 1;

	/** Pour debug uniquement. Sur quels objets fait-on le bilan?
	 *  Si true (normal) : On fait le bilan au niveau des objets Géographiques initiaux,
	 *  		  et on exporte des liens de la classe appariement.Lien entre objets Géographiques initiaux;
	 *  Si false (pour étude/débug): On fait le bilan au niveau des arcs des cartes topo ayant servi au calcul
	 *            et on exporte des liens de la classe appariementReseaux.LienReseaux entre objets des cartes topo.
	 */
	public boolean debugBilanSurObjetsGeo = true;

	/** Pour la représentation graphique des liens d'appariement entre cartes topos.
	 *  Si true : On représente les liens entre arcs par des tirets réguliers,
	 *  Si false : On représente les liens entre arcs par un trait reliant les milieux.
	 */
	public boolean debugTirets = true;

	/** Pour la représentation graphique des liens d'appariement entre cartes topos.
	 * Si on représente les liens entre arcs par des tirets réguliers, pas entre les tirets.
	 */
	public double debugPasTirets = 50;

	/** Pour la représentation graphique des liens d'appariement entre cartes topos.
	 *  Si true : On représente les liens entre des objets et un noeud, avec un buffer autour de ces objets
	 *  Si false : On représente les liens entre objets 2 à 2 par des trait reliant les milieux.
	 */
	public boolean debugBuffer = false;

	/** Pour la représentation graphique des liens d'appariement entre cartes topos.
	 * pour la représentation des liens d'appariement, taille du buffer autour des objets appariés à un noeud.
	 */
	public double debugTailleBuffer = 10;

}