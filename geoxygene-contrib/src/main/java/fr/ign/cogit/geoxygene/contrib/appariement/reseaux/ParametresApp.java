/*
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
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.parameters.Parameter;
import fr.ign.parameters.ParameterComponent;
import fr.ign.parameters.Parameters;


/**
 * Paramètres de l'appariement.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */
public class ParametresApp implements Cloneable {

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////// PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES ////////////
  // ///////////////////////////////////////////////////////////////////////////////

  /**
   * Liste des classes d'arcs de la BD 1 (la moins détaillée) concernés par
   * l'appariement
   */
  public List<IFeatureCollection<? extends IFeature>> populationsArcs1 = new ArrayList<IFeatureCollection<? extends IFeature>>();

  /**
   * Liste des classes de noeuds de la BD 1 (la moins détaillée) concernés par
   * l'appariement
   */
  public List<IFeatureCollection<? extends IFeature>> populationsNoeuds1 = new ArrayList<IFeatureCollection<? extends IFeature>>();

  /**
   * Liste des classes d'arcs de la BD 2 (la plus détaillée) concernés par
   * l'appariement
   */
  public List<IFeatureCollection<? extends IFeature>> populationsArcs2 = new ArrayList<IFeatureCollection<? extends IFeature>>();

  /**
   * Liste des classes de noeuds de la BD 2 (la plus détaillée) concernés par
   * l'appariement
   */
  public List<IFeatureCollection<? extends IFeature>> populationsNoeuds2 = new ArrayList<IFeatureCollection<? extends IFeature>>();

  
  /**
   * Prise en compte de l'orientation des arcs sur le terrain (sens de
   * circulation). Si true : on suppose tous les arcs en double sens. Si false:
   * on suppose tous les arcs en sens unique, celui défini par la géométrie. NB:
   * ne pas confondre cette orientation 'géographique réelle', avec
   * l'orientation de la géométrie.
   * 
   * Utile ensuite pour l'appariement des arcs.
   */
  public boolean populationsArcsAvecOrientationDouble1 = true;
  public boolean populationsArcsAvecOrientationDouble2 = true;
  public String attributOrientation1 = "orientation"; //$NON-NLS-1$
  public String attributOrientation2 = "orientation"; //$NON-NLS-1$
  public Map<Object, Integer> orientationMap1 = null;
  public Map<Object, Integer> orientationMap2 = null;

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////////// TAILLES DE RECHERCHE ///////////////////////
  // /////////////// Ecarts de distance autorisés ///////////////////////
  // /////////////// CE SONT LES PARAMETRES PRINCIPAUX ///////////////////////
  // ///////////////////////////////////////////////////////////////////////////////

  /**
   * Distance maximale autorisée entre deux noeuds appariés.
   */
  public float distanceNoeudsMax = 150;

  /**
   * Distance maximale autorisée entre deux noeuds appariés, quand le noeud du
   * réseau 1 est une impasse uniquement. Ce paramètre permet de prendre en
   * compte le fait que la localisation exacte des extrémités d'impasse est
   * assez imprécise dans certains réseaux (où commence exactement une rivière
   * par exemple?).
   * 
   * Si cette distance est strictement négative, alors ce paramètre n'est pas
   * pris en compte, et la distance maximale est la même pour tous les noeuds,
   * y-compris aux extrémités.
   */
  public float distanceNoeudsImpassesMax = -1;

  /**
   * Distance maximum autorisée entre les arcs des deux réseaux. La distance est
   * définie au sens de la "demi-distance de Hausdorf" des arcs du réseau 2 vers
   * les arcs du réseau 1.
   */
  public float distanceArcsMax = 100;

  /**
   * Distance minimum sous laquelle l'écart de distance pour divers arcs du
   * réseaux 2 (distance vers les arcs du réseau 1) n'a plus aucun sens. Cette
   * distance est typiquement de l'ordre de la précision géométrique du réseau
   * le moins précis.
   */
  public float distanceArcsMin = 30;

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////// TRAITEMENTS TOPOLOGIQUES A L'IMPORT /////////////////
  // ///////////////////////////////////////////////////////////////////////////////

  /**
   * Les noeuds proches du réseau 1 sont fusionés en un seul noeud (proches =
   * éloignés de moins que ce paramètre). Si ce paramètre est >0, les noeuds
   * sont fusionés à une position moyenne, et les arcs sont déformés pour
   * suivre. Si ce paramètre est =0, seul les noeuds strictement superposés sont
   * fusionés. Si ce paramètre est <0 (défaut), aucune fusion n'est faite.
   */
  public double topologieSeuilFusionNoeuds1 = -1;

  /**
   * Les noeuds proches du réseau 2 sont fusionés en un seul noeud (proches =
   * éloignés de moins que ce paramètre). Si ce paramètre est >0, les noeuds
   * sont fusionés à une position moyenne, et les arcs sont déformés pour
   * suivre. Si ce paramètre est =0, seul les noeuds strictement superposés sont
   * fusionés. Si ce paramètre est <0 (défaut), aucune fusion n'est faite.
   */
  public double topologieSeuilFusionNoeuds2 = -1;

  /**
   * Les noeuds du réseau 1 contenus dans une même surface de la population en
   * paramètre seront fusionés en un seul noeud pour l'appariement. Ce paramètre
   * peut être null (défaut), auquel il est sans influence. Exemple typique: on
   * fusionne toutes les extrémités de lignes ferrés arrivant dans une même aire
   * de triage, si les aires de triage sont définies par des surfaces dans les
   * données.
   */
  public IPopulation<?> topologieSurfacesFusionNoeuds1 = null;

  /**
   * Les noeuds du réseau 2 contenus dans une même surface de la population en
   * paramètre seront fusionés en un seul noeud pour l'appariement. Ce paramètre
   * peut être null (défaut), auquel il est sans influence. Exemple typique: on
   * fusionne toutes les extrémités de lignes ferrés arrivant dans une même aire
   * de triage, si les aires de triage sont définies par des surfaces dans les
   * données.
   */
  public IPopulation<?> topologieSurfacesFusionNoeuds2 = null;

  /**
   * Doit-on eliminer pour l'appariement les noeuds du réseau 1 qui n'ont que 2
   * arcs incidents et fusionner ces arcs ?
   */
  public boolean topologieElimineNoeudsAvecDeuxArcs1 = false;

  /**
   * Doit-on eliminer pour l'appariement les noeuds du réseau 2 qui n'ont que 2
   * arcs incidents et fusionner ces arcs ?
   */
  public boolean topologieElimineNoeudsAvecDeuxArcs2 = false;

  /**
   * Doit-on rendre le réseau 1 planaire (créer des noeuds aux intersections
   * d'arcs)?
   */
  public boolean topologieGraphePlanaire1 = false;

  /**
   * Doit-on rendre le réseau 2 planaire (créer des noeuds aux intersections
   * d'arcs)?
   */
  public boolean topologieGraphePlanaire2 = false;

  /**
   * Fusion des arcs doubles. Si true: si le réseau 1 contient des arcs en
   * double (même géométrie exactement), alors on les fusionne en un seul arc
   * pour l'appariement. Si false: rien n'est fait.
   */
  public boolean topologieFusionArcsDoubles1 = false;

  /**
   * Fusion des arcs doubles. Si true: si le réseau 2 contient des arcs en
   * double (même géométrie exactement), alors on les fusionne en un seul arc
   * pour l'appariement. Si false: rien n'est fait.
   */
  public boolean topologieFusionArcsDoubles2 = false;

  // ///////////////////////////////////////////////////////////////////////////////
  // //////// TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT ///////////
  // ///////////////////////////////////////////////////////////////////////////////

  /**
   * Doit on projeter les noeuds du réseau 1 sur le réseau 2 pour découper ce
   * dernier ? Ce traitement réalise un surdécoupage du réseau 2 qui facilite
   * l'appariement dans certains cas (par exemple si les réseaux ont des niveaux
   * de détail proches), mais qui va aussi un peu à l'encontre de la philosophie
   * générale du processus d'appariement. A utiliser avec modération donc.
   */
  public boolean projeteNoeuds1SurReseau2 = false;

  /**
   * Distance max de la projection des noeuds 2 sur le réseau 1. Utile
   * uniquement si projeteNoeuds1SurReseau2 = true.
   */
  public double projeteNoeuds1SurReseau2DistanceNoeudArc = 0;

  /**
   * Distance min entre la projection d'un noeud sur un arc et les extrémités de
   * cet arc pour créer un nouveau noeud sur le réseau 2. Utile uniquement si
   * projeteNoeuds1SurReseau2 = true.
   */
  public double projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;

  /**
   * Si true: on ne projete que les impasses du réseau 1 sur le réseau 2 Si
   * false: on projete tous les noeuds du réseau 1 sur le réseau 2. Utile
   * uniquement si projeteNoeuds1SurReseau2 = true.
   */
  public boolean projeteNoeuds1SurReseau2ImpassesSeulement = false;

  /**
   * Doit on projeter les noeuds du réseau 2 sur le réseau 1 pour découper ce
   * dernier ? Ce traitement réalise un surdécoupage du réseau 1 qui facilite
   * l'appariement dans certains cas (par exemple si les réseaux ont des niveaux
   * de détail proches), mais qui va aussi un peu à l'encontre de la philosophie
   * générale du processus d'appariement. A utiliser avec modération donc.
   */
  public boolean projeteNoeuds2SurReseau1 = false;

  /**
   * Distance max de la projection des noeuds 1 sur le réseau 2. Utile
   * uniquement si projeteNoeuds2SurReseau1 = true.
   */
  public double projeteNoeuds2SurReseau1DistanceNoeudArc = 0;

  /**
   * Distance min entre la projection d'un noeud sur un arc et les extrémités de
   * cet arc pour créer un nouveau noeud sur le réseau 1. Utile uniquement si
   * projeteNoeuds2SurReseau1 = true.
   */
  public double projeteNoeuds2SurReseau1DistanceProjectionNoeud = 0;

  /**
   * Si true: on ne projete que les impasses du réseau 2 sur le réseau 1 Si
   * false: on projete tous les noeuds du réseau 2 sur le réseau 1. Utile
   * uniquement si projeteNoeuds1SurReseau2 = true.
   */
  public boolean projeteNoeuds2SurReseau1ImpassesSeulement = false;

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////// VARIANTES DU PROCESSUS GENERAL //////////////////////
  // ///////////////////////////////////////////////////////////////////////////////

  /**
   * Niveau de complexité: recherche ou non de liens 1-n aux noeuds. Si true: un
   * noeud du réseau 1 est toujours apparié avec au plus un noeud du réseau 2
   * (1-1). Si false (défaut): on recherche liens 1-n aux noeuds .
   * 
   * NB: dans le cas simple, le processus est énormément simplifié !!!!!!!! Ceci
   * peut être pertinent si les données ont le même niveau de détail par
   * exemple.
   */
  public boolean varianteForceAppariementSimple = false;

  /**
   * Appariement en deux passes qui tente un surdécoupage du réseau pour les
   * arcs non appariés en première passe. Si true: les arcs du réseau 1 non
   * appariés dans une première passe sont redécoupés de manière à introduire un
   * noeud dans le reséau 1 aux endroits où il s'éloigne trop du réseau 2. Le
   * "trop" est égal à projeteNoeud2surReseau1_DistanceProjectionNoeud. Si false
   * (défaut): processus en une seule passe.
   * 
   * NB: pour l'instant, après ce re-découpage l'appariement est entièrement
   * refait, ce qui est long et très loin d'être optimisé: code à revoir !!!
   */
  public boolean varianteRedecoupageArcsNonApparies = false;

  /**
   * Appariement en deux passes qui tente un surdécoupage du réseau pour les
   * noeuds non appariés en première passe. Si true: les noeuds du réseau 1 non
   * appariés dans une première passe sont projetés au plus proche sur le réseau
   * 2 pour le découper Si false (défaut): processus en une seule passe.
   * 
   * Il s'agit en fait de la même opération que celle qui est faite quand
   * 'projeteNoeuds1SurReseau2'='true', mais uniquement pour les noeuds non
   * appariés en première passe, et avec des seuils éventuellement différents et
   * définis par les paramètres suivants : -
   * redecoupageNoeudsNonAppariesDistanceNoeudArc. -
   * redecoupageNoeudsNonAppariesDistanceProjectionNoeud
   * 
   * NB: pour l'instant, après ce re-découpage l'appariement est entièrement
   * refait, ce qui est long et très loin d'être optimal: à revoir à l'occasion,
   */
  public boolean varianteRedecoupageNoeudsNonApparies = false;

  /**
   * Distance max de la projection des noeuds du réseau 1 sur le réseau 2.
   * Utilisé uniquement si varianteRedecoupageNoeudsNonApparies = true.
   */
  public double varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;

  /**
   * Distance min entre la projection d'un noeud sur un arc et les extrémités de
   * cet arc pour créer un nouveau noeud sur le réseau 2 ? Utilisé uniquement si
   * varianteRedecoupageNoeudsNonApparies = true.
   */
  public double varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;

  /**
   * Quand un arc est apparié à un ensemble d'arcs, élimine de cet ensemble les
   * petites impasses qui créent des aller-retour parasites (de longueur
   * inférieure à distanceNoeuds). NB: paramètre spécifique aux réseaux simples,
   * qui permet d'améliorer le recalage.
   */
  public boolean varianteFiltrageImpassesParasites = false;

  /**
   * Recherche des ronds-points (faces circulaires) dans le réseau 2, pour
   * éviter d'apparier un noeud du réseau 1 à une partie seulement d'un
   * rond-point (si une partie seulement est appariée, tout le rond point
   * devient apparié). NB: Paramètre utile uniquement pour les réseaux routiers
   * a priori.
   */
  public boolean varianteChercheRondsPoints = false;

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////// OPTIONS D'EXPORT ////////////
  // ///////////////////////////////////////////////////////////////////////////////
  /**
   * Si true, la géométrie des liens est calculée des objets2 vers les objets 1
   * si false, c'est l'inverse
   */
  public boolean exportGeometrieLiens2vers1 = true;

  // ///////////////////////////////////////////////////////////////////////////////
  // /////////// OPTIONS DE DEBUG ////////////
  // ///////////////////////////////////////////////////////////////////////////////
  /**
   * Paramètre pour gérer l'affichage des commentaires dans la fenêtre de
   * contrôle Si c'est égal à 0 : aucun commentaire n'est affiché Si c'est égal
   * à 1 : le début des grandes étapes et les principaux résultats sont signalés
   * Si c'est égal à 2 (debug) : tous les messages sont affichés
   */
  public int debugAffichageCommentaires = 1;

  /**
   * Pour debug uniquement. Sur quels objets fait-on le bilan? Si true (normal)
   * : On fait le bilan au niveau des objets géographiques initiaux, et on
   * exporte des liens de la classe appariement.Lien entre objets géographiques
   * initiaux; Si false (pour étude/débug): On fait le bilan au niveau des arcs
   * des cartes topo ayant servi au calcul et on exporte des liens de la classe
   * appariementReseaux.LienReseaux entre objets des cartes topo.
   */
  public boolean debugBilanSurObjetsGeo = true;

  /**
   * Pour la représentation graphique des liens d'appariement entre cartes
   * topos. Si true : On représente les liens entre arcs par des tirets
   * réguliers, Si false : On représente les liens entre arcs par un trait
   * reliant les milieux.
   */
  public boolean debugTirets = true;

  /**
   * Pour la représentation graphique des liens d'appariement entre cartes
   * topos. Si on représente les liens entre arcs par des tirets réguliers, pas
   * entre les tirets.
   */
  public double debugPasTirets = 50;

  /**
   * Pour la représentation graphique des liens d'appariement entre cartes
   * topos. Si true : On représente les liens entre des objets et un noeud, avec
   * un buffer autour de ces objets Si false : On représente les liens entre
   * objets 2 à 2 par des trait reliant les milieux.
   */
  public boolean debugBuffer = false;

  /**
   * Pour la représentation graphique des liens d'appariement entre cartes
   * topos. pour la représentation des liens d'appariement, taille du buffer
   * autour des objets appariés à un noeud.
   */
  public double debugTailleBuffer = 10;
  
  
  public Parameters convertParamAppToParameters() {
    Parameters p = new Parameters();
    
    p.set("distanceNoeudsMax", this.distanceNoeudsMax);
    p.set("distanceArcsMax", this.distanceArcsMax);
    p.set("distanceArcsMin", this.distanceArcsMin);
    p.set("distanceNoeudsImpassesMax", this.distanceNoeudsImpassesMax);
    
    p.set("topologieSeuilFusionNoeuds1", this.topologieSeuilFusionNoeuds1);
    p.set("topologieElimineNoeudsAvecDeuxArcs1", this.topologieElimineNoeudsAvecDeuxArcs1);
    p.set("topologieGraphePlanaire1", this.topologieGraphePlanaire1);
    p.set("topologieFusionArcsDoubles1", this.topologieFusionArcsDoubles1);

    p.set("topologieSeuilFusionNoeuds2", this.topologieSeuilFusionNoeuds2);
    p.set("topologieElimineNoeudsAvecDeuxArcs2", this.topologieElimineNoeudsAvecDeuxArcs2);
    p.set("topologieGraphePlanaire2", this.topologieGraphePlanaire2);
    p.set("topologieFusionArcsDoubles2", this.topologieFusionArcsDoubles2);
    
    p.set("projeteNoeuds1SurReseau2", this.projeteNoeuds1SurReseau2);
    p.set("projeteNoeuds1SurReseau2DistanceNoeudArc", this.projeteNoeuds1SurReseau2DistanceNoeudArc);
    p.set("projeteNoeuds1SurReseau2DistanceProjectionNoeud", this.projeteNoeuds1SurReseau2DistanceProjectionNoeud);
    p.set("projeteNoeuds1SurReseau2ImpassesSeulement", this.projeteNoeuds1SurReseau2ImpassesSeulement);
    p.set("projeteNoeuds2SurReseau1", this.projeteNoeuds2SurReseau1);
    p.set("projeteNoeuds2SurReseau1DistanceNoeudArc", this.projeteNoeuds2SurReseau1DistanceNoeudArc);
    p.set("projeteNoeuds2SurReseau1DistanceProjectionNoeud", this.projeteNoeuds2SurReseau1DistanceProjectionNoeud);
    p.set("projeteNoeuds2SurReseau1ImpassesSeulement", this.projeteNoeuds2SurReseau1ImpassesSeulement);
    
    p.set("varianteForceAppariementSimple", this.varianteForceAppariementSimple);
    p.set("varianteRedecoupageArcsNonApparies", this.varianteRedecoupageArcsNonApparies);
    p.set("varianteRedecoupageNoeudsNonApparies", this.varianteRedecoupageNoeudsNonApparies);
    p.set("varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc", this.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc);
    p.set("varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud", this.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud);
    p.set("varianteFiltrageImpassesParasites", this.varianteFiltrageImpassesParasites);
    p.set("varianteChercheRondsPoints", this.varianteChercheRondsPoints);
    
    // 
    Parameters direction1 = new Parameters();
    direction1.setDescription("direction1");
    if (this.populationsArcsAvecOrientationDouble1) {
      direction1.set("populationsArcsAvecOrientationDouble", "true");
    } else {
      direction1.set("populationsArcsAvecOrientationDouble", "false");
      direction1.set("attributOrientation", this.attributOrientation1);
      Parameters pAtt1 = new Parameters();
      pAtt1.setDescription("orientation1");
      for (Object valAttribut : this.orientationMap1.keySet()) {
        pAtt1.set(valAttribut.toString(), this.orientationMap1.get(valAttribut));
      }
      direction1.add(pAtt1);
    }
    p.add(direction1);
    
    Parameters direction2 = new Parameters();
    direction2.setDescription("direction2");
    if (this.populationsArcsAvecOrientationDouble2) {
      direction2.set("populationsArcsAvecOrientationDouble", "true");
    } else {
      direction2.set("populationsArcsAvecOrientationDouble", "false");
      direction2.set("attributOrientation", this.attributOrientation2);
      Parameters pAtt2 = new Parameters();
      pAtt2.setDescription("orientation2");
      for (Object valAttribut : this.orientationMap2.keySet()) {
        pAtt2.set(valAttribut.toString(), this.orientationMap2.get(valAttribut));
      }
      direction2.add(pAtt2);
    }
    p.add(direction2);
    
    return p;
  }
  
  /**
   * 
   * @param p
   * @return
   */
  public static ParametresApp convertParametersToParamApp(Parameters p) {
    
    ParametresApp param = new ParametresApp();

    //
    if (p.get("direction1", "populationsArcsAvecOrientationDouble") != null) {
      boolean double1 = Boolean.parseBoolean(p.get("direction1", "populationsArcsAvecOrientationDouble").toString());
      if (double1) {
        param.populationsArcsAvecOrientationDouble1 = true;
      } else {
        param.populationsArcsAvecOrientationDouble1 = false;
        if (p.get("direction1", "attributOrientation") != null) {
          param.attributOrientation1 = p.get("direction1", "attributOrientation").toString();
        } else {
          param.attributOrientation1 = "";
        }
        
        Parameters pAtt1 = p.getParameters("orientation1");
        Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
        if (pAtt1 != null) {
          for (ParameterComponent paramComp : pAtt1.entry) {
            Parameter po = (Parameter)paramComp;
            orientationMap1.put(po.getKey(), Integer.parseInt(po.getValue().toString()));
          }
        }
        param.orientationMap1 = orientationMap1;
      }
    } else {
      param.populationsArcsAvecOrientationDouble1 = true;
    }
    
    if (p.get("direction2", "populationsArcsAvecOrientationDouble") != null) {
      boolean double2 = Boolean.parseBoolean(p.get("direction2", "populationsArcsAvecOrientationDouble").toString());
      if (double2) {
        param.populationsArcsAvecOrientationDouble2 = true;
      } else {
        param.populationsArcsAvecOrientationDouble2 = false;
        if (p.get("direction2", "attributOrientation") != null) {
          param.attributOrientation2 = p.get("direction2", "attributOrientation").toString();
        } else {
          param.attributOrientation2 = "";
        }
        Parameters pAtt2 = p.getParameters("orientation2");
        Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
        if (pAtt2 != null) {
          for (ParameterComponent paramComp : pAtt2.entry) {
            Parameter po = (Parameter)paramComp;
            orientationMap2.put(po.getKey(), Integer.parseInt(po.getValue().toString()));
          }
        }
        param.orientationMap2 = orientationMap2;
      }
    } else {
      param.populationsArcsAvecOrientationDouble2 = true;
    }
    
    // Distance
    param.distanceNoeudsMax = p.getFloat("distanceNoeudsMax");
    param.distanceArcsMax = p.getFloat("distanceArcsMax");
    param.distanceArcsMin = p.getFloat("distanceArcsMin");
    param.distanceNoeudsImpassesMax = p.getFloat("distanceNoeudsImpassesMax");
    
    param.topologieSeuilFusionNoeuds1 = p.getDouble("topologieSeuilFusionNoeuds1");
    param.topologieElimineNoeudsAvecDeuxArcs1 = p.getBoolean("topologieElimineNoeudsAvecDeuxArcs1");
    param.topologieGraphePlanaire1 = p.getBoolean("topologieGraphePlanaire1");
    param.topologieFusionArcsDoubles1 = p.getBoolean("topologieFusionArcsDoubles1");

    param.topologieSeuilFusionNoeuds2 = p.getDouble("topologieSeuilFusionNoeuds2");
    param.topologieElimineNoeudsAvecDeuxArcs2 = p.getBoolean("topologieElimineNoeudsAvecDeuxArcs2");
    param.topologieGraphePlanaire2 = p.getBoolean("topologieGraphePlanaire2");
    param.topologieFusionArcsDoubles2 = p.getBoolean("topologieFusionArcsDoubles2");
    
    param.projeteNoeuds1SurReseau2 = p.getBoolean("projeteNoeuds1SurReseau2");
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = p.getDouble("projeteNoeuds1SurReseau2DistanceNoeudArc");
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = p.getDouble("projeteNoeuds1SurReseau2DistanceProjectionNoeud");
    param.projeteNoeuds1SurReseau2ImpassesSeulement = p.getBoolean("projeteNoeuds1SurReseau2ImpassesSeulement");
    param.projeteNoeuds2SurReseau1 = p.getBoolean("projeteNoeuds2SurReseau1");
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = p.getDouble("projeteNoeuds2SurReseau1DistanceNoeudArc");
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = p.getDouble("projeteNoeuds2SurReseau1DistanceProjectionNoeud");
    param.projeteNoeuds2SurReseau1ImpassesSeulement = p.getBoolean("projeteNoeuds2SurReseau1ImpassesSeulement");
    
    param.varianteForceAppariementSimple = p.getBoolean("varianteForceAppariementSimple");
    param.varianteRedecoupageArcsNonApparies = p.getBoolean("varianteRedecoupageArcsNonApparies");
    param.varianteRedecoupageNoeudsNonApparies = p.getBoolean("varianteRedecoupageNoeudsNonApparies");
    param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = p.getDouble("varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc");
    param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = p.getDouble("varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud");
    param.varianteFiltrageImpassesParasites = p.getBoolean("varianteFiltrageImpassesParasites");
    param.varianteChercheRondsPoints = p.getBoolean("varianteChercheRondsPoints");
    
    return param;
  }
  
}
