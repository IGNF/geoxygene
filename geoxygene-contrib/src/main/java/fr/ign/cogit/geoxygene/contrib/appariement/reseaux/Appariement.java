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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.ElementCarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.util.Resampler;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Appariement is the abstract class for data matching which carries the
 * algorithms for networks matching using topological maps ({@link ReseauApp}).
 * The method is based on [Devogele 97]. <b> For data matching of complete sets
 * of data, use {@link AppariementIO}. </b>
 * 
 * <p>
 * La classe Appariement supporte les methodes d'entrée pour executer
 * l'appariement de réseaux inspiré de la méthode de [Devogele 97].
 * 
 * <p>
 * <b>NB: Cette classe ne porte QUE les méthodes concernant l'appariement de
 * cartes topo ({@link ReseauApp}). Pour un appariement complet de jeux géo
 * (création carte topo, appariement, export), voir la classe
 * {@link AppariementIO}.</b>
 * 
 * @author Mustiere
 * @see AppariementIO
 * @see ReseauApp
 * @see CarteTopo
 */

// /////////////////////////////////////////////////////////////////////////////
// NOTE AUX CODEURS, dans le code parfois :
// reseau 1 = reseau ref, reseau 2 = reseau comp.
// /////////////////////////////////////////////////////////////////////////////

public abstract class Appariement {
  /**
   * Static logger for this class.
   */
  private static final Logger LOGGER = LogManager.getLogger(Appariement.class.getName());
  /**
   * Default tile size.
   */
  private static final int DEFAULT_TILE_SIZE = 20;
  /**
   * Constant holding the value 0.5.
   */
  private static final double ZERO_POINT_FIVE = 0.5;
  /**
   * Constant holding the value 100.
   */
  private static final double HUNDRED = 100;
  /**
   * Constant holding the value 1000.
   */
  private static final double THOUSAND = 1000;

  /**
   * Private constructor. Unused since this is a utilitary class.
   */
  private Appariement() {
  }

  /**
   * Matching between two networks represented by topological maps (
   * {@link ReseauApp}). <b>The networks given as arguments are modified during
   * this process: groups are created</b>
   * <p>
   * Appariement entre deux réseaux représentés par des carte topo. Processus
   * largement inspiré de celui défini dans la thèse de Thomas Devogèle (1997).
   * 
   * <b>Attention : les réseaux passés en entrée sont modifiés durant le
   * traitement : des groupes y sont ajoutés.</b>
   * 
   * @param reseau1 first network to be matched
   * @param reseau2 second network to be matched
   * @param param parameters of the data matching algorithm
   * @return a set of links between objects from both networks
   * 
   * @see ReseauApp
   * @see CarteTopo
   * @see EnsembleDeLiens
   * @see ReseauApp
   * @see ParametresApp
   */
  public static EnsembleDeLiens appariementReseaux(final ReseauApp reseau1,
      final ReseauApp reseau2, final ParametresApp param) {
    
    // build a spatial index (regular tiling with an average of 20 objets
    // per tile) if the network do not have one already.
    // Indexation spatiale si cela n'a pas déjà été fait :
    // dallage régulier avec en moyenne 20 objets par case.
    if (!reseau1.getPopArcs().hasSpatialIndex()) {
      int nb = Math.max(
          (int) Math.sqrt(reseau1.getPopArcs().size()
              / Appariement.DEFAULT_TILE_SIZE), 1);
      reseau1.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
    }
    int nb = Math.max(
        (int) Math.sqrt(reseau2.getPopNoeuds().size()
            / Appariement.DEFAULT_TILE_SIZE), 1);
    reseau2.getPopNoeuds().initSpatialIndex(Tiling.class, true, nb);
    nb = Math.max(
        (int) Math.sqrt(reseau1.getPopNoeuds().size()
            / Appariement.DEFAULT_TILE_SIZE), 1);
    reseau1.getPopNoeuds().initSpatialIndex(Tiling.class, true, nb);
    // /////////// APPARIEMENT
    // Pre-matching using nodes.
    // Préappariement de noeuds à noeuds.
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.NodePrematching") + //$NON-NLS-1$
          (new Time(System.currentTimeMillis())).toString());
    }
    EnsembleDeLiens liensPreAppNN = Appariement.preAppariementNoeudNoeud(
        reseau1, reseau2, param);
    // Pre-matching using edges.
    // Préappariement d'arcs à arcs.
    EnsembleDeLiens liensPreAppAA;
    if (liensPreAppNN.size() != 0) {
      // Nothing to do if there is no node.
      // Il est inutile de pre-appariéer les arcs si rien n'a été trouvé
      // liensPreAppAA sur les noeuds. Preappariement des arcs entre eux
      // (basé principalement sur Hausdorf).
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.EdgePrematching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensPreAppAA = Appariement.preAppariementArcArc(reseau1, reseau2, param);
    } else {
      liensPreAppAA = new EnsembleDeLiens(LienReseaux.class);
      liensPreAppAA.setNom(I18N.getString("Appariement.EdgePrematching")); //$NON-NLS-1$
    }
    // Matching each node of reseau1 independantly with nodes of reseau2
    // Appariement de chaque noeud de la BDref / reseau1
    // (indépendamment les uns des autres)
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.NodeMatching") + //$NON-NLS-1$
          (new Time(System.currentTimeMillis())).toString());
    }
    EnsembleDeLiens liensAppNoeuds = Appariement.appariementNoeuds(reseau1,
        reseau2, liensPreAppNN, liensPreAppAA, param);
    // cleaning-up
    Appariement.nettoyageLiens(reseau1, reseau2, liensPreAppNN);
    if (param.varianteRedecoupageNoeudsNonApparies) {
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N
            .getString("Appariement.ReprojectionOfNonMatchedNodes") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      Appariement.decoupeNoeudsNonApparies(reseau1, reseau2, liensAppNoeuds,
          param);
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.LinkCleanup") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      Appariement.nettoyageLiens(reseau1, reseau2);
      liensPreAppNN.setElements(new ArrayList<Lien>());
      liensPreAppAA.setElements(new ArrayList<Lien>());
      liensAppNoeuds.setElements(new ArrayList<Lien>());
      System.gc();
      // préappariement de noeuds à noeuds
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.NodePrematching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensPreAppNN = Appariement.preAppariementNoeudNoeud(reseau1, reseau2,
          param);
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N
            .getString("Appariement.NumberOfPrematchedNodes") + //$NON-NLS-1$
            liensPreAppNN.size());
      }
      // Preappariement des arcs entre eux
      // (basé principalement sur Hausdroff)
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.EdgePrematching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensPreAppAA = Appariement.preAppariementArcArc(reseau1, reseau2, param);
      // Appariement de chaque noeud de la BDref (indépendamment)
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.NodeMatching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensAppNoeuds = Appariement.appariementNoeuds(reseau1, reseau2,
          liensPreAppNN, liensPreAppAA, param);
      Appariement.nettoyageLiens(reseau1, reseau2, liensPreAppNN);
    }
    // Appariement de chaque arc du reseau 1
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.EdgeMatching") + //$NON-NLS-1$
          (new Time(System.currentTimeMillis())).toString());
    }
    EnsembleDeLiens liensAppArcs = Appariement.appariementArcs(reseau1,
        reseau2, liensPreAppAA, liensAppNoeuds, param);
    EnsembleDeLiens tousLiens = EnsembleDeLiens.compile(liensAppNoeuds,
        liensAppArcs);

    if (param.varianteRedecoupageArcsNonApparies) {
      // NB: pas optimal du tout, on recalcule tout après avoir redécoupé
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N
            .getString("Appariement.ResplittingNonMatchedEdges") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      Appariement.decoupeNonApparies(reseau1, reseau2, tousLiens, param);
      Appariement.nettoyageLiens(reseau1, reseau2);
      liensPreAppNN.setElements(new ArrayList<Lien>());
      liensPreAppAA.setElements(new ArrayList<Lien>());
      liensAppNoeuds.setElements(new ArrayList<Lien>());
      // préappariement de noeuds à noeuds
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.NodePrematching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensPreAppNN = Appariement.preAppariementNoeudNoeud(reseau1, reseau2,
          param);
      // Preappariement des arcs entre eux
      // (basé principalement sur Hausdroff)
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.EdgePrematching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensPreAppAA = Appariement.preAppariementArcArc(reseau1, reseau2, param);
      // Appariement de chaque noeud de la BDref (indépendamment)
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.NodeMatching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensAppNoeuds = Appariement.appariementNoeuds(reseau1, reseau2,
          liensPreAppNN, liensPreAppAA, param);
      Appariement.nettoyageLiens(reseau1, reseau2, liensPreAppNN);
      // Appariement de chaque arc de la BDref
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N.getString("Appariement.EdgeMatching") + //$NON-NLS-1$
            (new Time(System.currentTimeMillis())).toString());
      }
      liensAppArcs = Appariement.appariementArcs(reseau1, reseau2,
          liensPreAppAA, liensAppNoeuds, param);
      tousLiens = EnsembleDeLiens.compile(liensAppNoeuds, liensAppArcs);
    }
    Appariement.nettoyageLiens(reseau1, reseau2, liensPreAppAA);
    // Evaluation globale
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.GlobalControl") + //$NON-NLS-1$
          (new Time(System.currentTimeMillis())).toString());
    }
    
    // --------------------------------------------------------------------------------------
    Appariement.controleGlobal(reseau1, reseau2, tousLiens, param);

    return tousLiens;
  }

  /**
   * Remove all links from the input topological maps.
   * <p>
   * Enlève tous les liens "liens" des cartes topo en entrée, et vide les liens
   * "liens".
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param liens links to empty
   */
  public static void nettoyageLiens(final ReseauApp reseau1,
      final ReseauApp reseau2, final EnsembleDeLiens liens) {
    Appariement.nettoyageLiens(reseau1, liens);
    Appariement.nettoyageLiens(reseau2, liens);
    liens.setElements(new ArrayList<Lien>());
  }

  /**
   * Remove all links from the input topological map.
   * <p>
   * Enlève tous les liens "liens" des cartes topo en entrée.
   * @param res network
   * @param liens links to empty
   */
  private static void nettoyageLiens(final ReseauApp res, final EnsembleDeLiens liens) {
    for (Arc arc : res.getPopArcs()) {
      ((ArcApp) arc).getLiens().removeAll(liens.getElements());
    }
    for (Noeud node : res.getPopNoeuds()) {
      ((NoeudApp) node).getLiens().removeAll(liens.getElements());
    }
    for (Groupe group : res.getPopGroupes()) {
      ((GroupeApp) group).getLiens().removeAll(liens.getElements());
    }
  }

  /**
   * Remove all links from the input topological maps and destroy groups.
   * <p>
   * Enlève tous les liens des cartes topo en entrée, et détruit les groupes.
   * @param reseau1 network 1
   * @param reseau2 network 2
   */
  public static void nettoyageLiens(final ReseauApp reseau1,
      final ReseauApp reseau2) {
    Appariement.nettoyageLiens(reseau1);
    Appariement.nettoyageLiens(reseau2);
  }

  /**
   * Remove all links from the input topological map and destroy groups.
   * <p>
   * Enlève tous les liens de la carte topo en entrée, et détruit les groupes.
   * @param res network
   */
  public static void nettoyageLiens(final ReseauApp res) {
    for (Arc arc : res.getPopArcs()) {
      ((ArcApp) arc).setLiens(new ArrayList<LienReseaux>());
    }
    for (Noeud node : res.getPopNoeuds()) {
      ((NoeudApp) node).setLiens(new ArrayList<LienReseaux>());
    }
    res.getPopGroupes().setElements(new ArrayList<Groupe>());
  }

  /**
   * Nodes Prematching using euclidian distance as proposed in [Devogèle 97].
   * <p>
   * Préappariement entre noeuds uniquement sur un critère de distance
   * euclidienne, comme proposé dans [Devogèle 97].
   * 
   * On crée des liens 1-n (n candidats noeuds de BDcomp pour chaque noeud de
   * BDref).
   * 
   * Comme suggéré dans [Devogèle 97], la taille de la recherche peut varier
   * selon le type du noeud de la BD de reférence (rond point, changement
   * d'attribut...).
   * <p>
   * <ul>
   * <li>NB1: On préfére largement une taille de recherche sur-évaluée que
   * sous-évaluée.
   * <li>NB2: On ne traite pas les noeuds isolés.
   * </ul>
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param param matching paramters
   * @return resulting set of links between networks
   */
  public static EnsembleDeLiens preAppariementNoeudNoeud(
      final CarteTopo reseau1, final CarteTopo reseau2,
      final ParametresApp param) {
    int nbCandidats = 0, nbRef = 0;
    EnsembleDeLiens liens = new EnsembleDeLiens(LienReseaux.class);
    liens.setNom(I18N.getString("Appariement.NodePrematching")); //$NON-NLS-1$
    Iterator<Noeud> itNoeuds = reseau1.getListeNoeuds().iterator();
    while (itNoeuds.hasNext()) {
      NoeudApp noeudRef = (NoeudApp) itNoeuds.next();
      // On ne tient pas compte des noeuds isolés
      if (noeudRef.getEntrants().size() == 0
          && noeudRef.getSortants().size() == 0) {
        continue;
      }
      nbRef++;
      // Détermination des noeuds comp dans le rayon de recherche
      Collection<Noeud> candidats = reseau2.getPopNoeuds().select(
          noeudRef.getGeometrie(), noeudRef.getTaille());
      if (candidats.size() != 0) {
        LienReseaux lien = (LienReseaux) liens.nouvelElement();
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        nbCandidats = nbCandidats + candidats.size();
        Iterator<Noeud> itCandidats = candidats.iterator();
        while (itCandidats.hasNext()) {
          NoeudApp noeudComp = (NoeudApp) itCandidats.next();
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
        }
      }
    }
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.Assessment") //$NON-NLS-1$
          + nbCandidats + I18N.getString("Appariement.CandidateCompNodes") //$NON-NLS-1$
          + nbRef + I18N.getString("Appariement.RefNodesToProcess")); //$NON-NLS-1$
    }
    return liens;
  }

  // ////////////////////////////////////////////////////////////////////////
  /**
   * Edges prematching using Haussdorff half distance.
   * <p>
   * Préappariement entre arcs basé sur la "demi-distance de Hausdorff" (on ne
   * prend en compte que la composante de réseau 2 vers réseau 1).
   * 
   * Pour chaque arc du reseau 2, on garde les arcs du reseau 1 qui sont à la
   * fois : 1/ à moins distanceMax de l'arc comp 2/ à moins de D + distanceMin
   * de l'arc comp, D étant la distance entre l'arc ref le plus proche de arc
   * comp
   * 
   * NB: ce pré-appariement est différent de ce qui est proposé dans [Devogèle
   * 97], pour minimiser la sensibilité aux seuils.
   * 
   * On crée des liens 1-n: (1 arc de comp) - (n arcs de ref). Un arc de ref
   * peut être alors concerné par plusieurs liens différents. Au total on a donc
   * des relations n-m codées sous la forme de n relations 1-m.
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param param matching parameters
   * @return resulting set of links
   */
  public static EnsembleDeLiens preAppariementArcArc(final CarteTopo reseau1,
      final CarteTopo reseau2, final ParametresApp param) {
    int nbCandidats = 0;
    List<Double> distances = new ArrayList<Double>();
    EnsembleDeLiens liens = new EnsembleDeLiens(LienReseaux.class);
    liens.setNom(I18N.getString("Appariement.EdgePrematching")); //$NON-NLS-1$
    for (Arc edge : reseau2.getListeArcs()) {
      ArcApp arcComp = (ArcApp) edge;
      // On recherche les arcs dans l'entourage proche, grosso modo
      Collection<Arc> arcsProches = reseau1.getPopArcs().select(arcComp.getGeometrie(),
          param.distanceArcsMax);
      if (arcsProches.isEmpty()) {
        continue;
      }
      // On calcule leur distance à arccomp et
      // on recherche le plus proche
      distances.clear();
      Iterator<Arc> itArcsProches = arcsProches.iterator();
      ArcApp arcRef = (ArcApp) itArcsProches.next();
      ILineString resampled = Resampler.resample(arcComp.getGeometrie(), param.distanceArcsMax);
      double dmin = Distances.premiereComposanteHausdorff(resampled, arcRef.getGeometrie());
      if (dmin > param.distanceArcsMax) {
        dmin = Double.MAX_VALUE;
      }
//      double dmin = arcComp.premiereComposanteHausdorff(arcRef, param.distanceArcsMax);
      distances.add(new Double(dmin));
      while (itArcsProches.hasNext()) {
        arcRef = (ArcApp) itArcsProches.next();
//        double d = arcComp.premiereComposanteHausdorff(arcRef, param.distanceArcsMax);
        double d = Distances.premiereComposanteHausdorff(resampled, arcRef.getGeometrie());
        if (d > param.distanceArcsMax) {
          d = Double.MAX_VALUE;
        }
        distances.add(new Double(d));
        if (d < dmin) {
          dmin = d;
        }
      }
      // On garde tous ceux assez proches
      double dmax = Math.min(dmin + param.distanceArcsMin, param.distanceArcsMax);
      List<Arc> candidats = new ArrayList<Arc>();
      Iterator<Arc> itArc = arcsProches.iterator();
      Iterator<Double> itDistance = distances.iterator();
      while (itArc.hasNext() && itDistance.hasNext()) {
        Double distance = itDistance.next();
        Arc arc = itArc.next();
        if (distance < dmax) {
          candidats.add(arc);
        }
      }
      // Si pas de candidat pour l'arccomp, on s'arrête là
      if (candidats.isEmpty()) {
        continue;
      }
      // Si il y a des candidats: on construit le lien de pré-appariement
      LienReseaux lien = (LienReseaux) liens.nouvelElement();
      arcComp.addLiens(lien);
      lien.addArcs2(arcComp);
      nbCandidats = nbCandidats + candidats.size();
      Iterator<Arc> itCandidats = candidats.iterator();
      while (itCandidats.hasNext()) {
        arcRef = (ArcApp) itCandidats.next();
        lien.addArcs1(arcRef);
        arcRef.addLiens(lien);
      }
    }
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug("  Bilan : " //$NON-NLS-1$
          + nbCandidats
          + I18N.getString("Appariement.CandidateCompEdges") + //$NON-NLS-1$
          reseau1.getListeArcs().size()
          + I18N.getString("Appariement.RefEdgesToProcess")); //$NON-NLS-1$
    }
    return liens;
  }

  /**
   * Node matching of network 1 with network 2 as proposed in [Devogèle 97].
   * Matches are evaluated with a mark of (0, 0.5, or 1).
   * <p>
   * Appariement des Noeuds du reseau 1 avec les arcs et noeuds du reseau 1,
   * comme proposé dans [Devogèle 97] + modif au filtrage Seb On crée les liens
   * qui vont bien si le noeud est apparié. Une évaluation de l'appariement est
   * stockée sur les liens (note de 0, 0.5 ou 1). Une explication plus détaillée
   * du résultat est stockée sur les noeuds ref et comp.
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param liensPreAppNN node prematching link set
   * @param liensPreAppAA edge prematching link set
   * @param param matching parameters
   * @return resulting set of links
   */
  public static EnsembleDeLiens appariementNoeuds(final CarteTopo reseau1,
      final CarteTopo reseau2, final EnsembleDeLiens liensPreAppNN,
      final EnsembleDeLiens liensPreAppAA, final ParametresApp param) {
    
    IPopulation<Groupe> groupesComp = reseau2.getPopGroupes();
    int nbSansHomologue = 0, nbNonTraite = 0, nbPlusieursNoeudsComplets = 0, nbPlusieursGroupesComplets = 0, nbNoeudNoeud = 0, nbNoeudGroupe = 0, nbNoeudGroupeIncertain = 0, nbNoeudNoeudIncertain = 0;
    EnsembleDeLiens liens = new EnsembleDeLiens(LienReseaux.class);
    liens.setNom(I18N.getString("Appariement.NodeMatching")); //$NON-NLS-1$
    // On initialise le resultat à "non apparié" pour les noeuds comp
    Iterator<Noeud> itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
    while (itNoeuds.hasNext()) {
      NoeudApp noeudComp = (NoeudApp) itNoeuds.next();
      noeudComp.setResultatAppariement(I18N.getString("Appariement.Unmatched")); //$NON-NLS-1$
    }
    // On traite chaque noeud ref, un par un
    itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
    while (itNoeuds.hasNext()) {
      NoeudApp noeudRef = (NoeudApp) itNoeuds.next();
      // pour détecter les cas non traités
      noeudRef.setResultatAppariement(I18N.getString("Appariement.NA")); //$NON-NLS-1$
      // On ne traite pas les noeuds isolés
      if (noeudRef.arcs().size() == 0) {
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.IsolatedNode")); //$NON-NLS-1$
        nbNonTraite++;
        continue;
      }
      List<LienReseaux> liensDuNoeudRef = new ArrayList<LienReseaux>(
          noeudRef.getLiens(liensPreAppNN.getElements()));
      // Noeud ref qui n'a aucun noeud comp candidat
      // dans le pré-appariement
      if (liensDuNoeudRef.size() == 0) {
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.NoCandidateForMatching")); //$NON-NLS-1$
        nbSansHomologue++;
        continue;
      }
      // Noeud ref qui a un ou plusieurs candidats
      // (réunis dans un seul lien par construction).
      //
      // On qualifie des noeuds comp candidats en comparant
      // les entrants/sortants.
      // Un noeud comp est dit, par rapport au noeud ref, :
      // - complet si on trouve une correspondance entre tous
      // les incidents des noeuds ref et comp,
      // - incomplet si on trouve une correspondance entre certains
      // incidents
      // - impossible si on ne trouve aucune correspondance
      List<Noeud> noeudsCompCandidats = (liensDuNoeudRef.get(0)).getNoeuds2();
      List<ElementCarteTopo> complets = new ArrayList<ElementCarteTopo>();
      List<ElementCarteTopo> incomplets = new ArrayList<ElementCarteTopo>();
      Iterator<Noeud> itNoeudsComp = noeudsCompCandidats.iterator();
      while (itNoeudsComp.hasNext()) {
        NoeudApp noeudComp = (NoeudApp) itNoeudsComp.next();
        int correspondance = noeudRef.correspCommunicants(noeudComp, liensPreAppAA);
        if (correspondance == 1) {
          complets.add(noeudComp);
        }
        if (correspondance == 0) {
          incomplets.add(noeudComp);
        }
      }
      // Cas d'un appariement simple :
      // 1 noeud BDref correspond à un noeud BDComp
      // C'est un appariement que l'on juge sûr.
      if (complets.size() == 1) {
        NoeudApp noeudComp = (NoeudApp) complets.get(0);
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
        noeudComp.setResultatAppariement(I18N
            .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
        nbNoeudNoeud++;
        LienReseaux lien = (LienReseaux) liens.nouvelElement();
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.addNoeuds2(noeudComp);
        noeudComp.addLiens(lien);
        lien.setEvaluation(1);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      // Cas où plusieurs noeuds comp candidats sont complets.
      // Ce cas ne devrait en théorie pas arriver d'après Thomas,
      // mais si en pratique les seuils sont larges, c'est un cas courant
      //
      // Ajout Seb pour filtrer ce sur-appariement :
      // on choisit le plus proche (devrait être affiné)
      if (complets.size() > 1) {
        Appariement.filtrageNoeudsComplets(complets, noeudRef);
        if (complets.size() == 1) {
          noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainChoiceBetweenSeveralNodes")); //$NON-NLS-1$
          nbPlusieursNoeudsComplets++;
          LienReseaux lien = (LienReseaux) liens.nouvelElement();
          lien.addNoeuds1(noeudRef);
          noeudRef.addLiens(lien);
          NoeudApp noeudComp = (NoeudApp) complets.get(0);
          noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainChoiceBetweenSeveralNodes")); //$NON-NLS-1$
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          lien.setCommentaire(noeudRef.getResultatAppariement());
          continue;
        }
        // Si il reste plusieurs noeuds complets après le filtrage
        // NB : cas impossible dans l'état actuel du filtrage qui
        // ne garde qu'un noeud complet
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.UncertainMatchedWithSeveralNodes")); //$NON-NLS-1$
        nbPlusieursNoeudsComplets++;
        LienReseaux lien = (LienReseaux) liens.nouvelElement();
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        for (int j = 0; j < complets.size(); j++) {
          NoeudApp noeudComp = (NoeudApp) complets.get(j);
          noeudComp.setResultatAppariement(I18N
              .getString("Appariement.UncertainCompetingNodes")); //$NON-NLS-1$
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
        }
        lien.setEvaluation(0);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      // Cas où il n'y a pas de noeud comp complet, mais un seul
      // incomplet.
      // Appariement simple : 1 noeud ref correspond à un noeud comp,
      // mais c'est un appariement que l'on juge incertain
      if (incomplets.size() == 1) {
        NoeudApp noeudComp = (NoeudApp) incomplets.get(0);
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithIncompleteNode")); //$NON-NLS-1$
        noeudComp.setResultatAppariement(I18N
            .getString("Appariement.UncertainIncompleteNode")); //$NON-NLS-1$
        nbNoeudNoeudIncertain++;
        LienReseaux lien = (LienReseaux) liens.nouvelElement();
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.addNoeuds2(noeudComp);
        noeudComp.addLiens(lien);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      // Le noeud ref n'a que des noeuds comp candidats impossibles
      // i.e. ils n'ont aucun arc sortant apparié avec un des arcs
      // sortant du noeudref
      if (incomplets.size() == 0) {
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.UnmatchedNoPossibleCandidate")); //$NON-NLS-1$
        nbSansHomologue++;
        continue;
      }
      // Si il y a plusieurs noeuds incomplets,
      // MAIS on autorise uniquement les appariement 1-1 et 1-0
      // aux noeuds.
      // On choisit alors le noeud incomplet le plus proche.
      if (param.varianteForceAppariementSimple) {
        NoeudApp noeudComp = Appariement
            .noeudLePlusProche(incomplets, noeudRef);
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithIncompleteNode")); //$NON-NLS-1$
        noeudComp.setResultatAppariement(I18N
            .getString("Appariement.UncertainIncompleteNode")); //$NON-NLS-1$
        nbNoeudNoeudIncertain++;
        LienReseaux lien = (LienReseaux) liens.nouvelElement();
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.addNoeuds2(noeudComp);
        noeudComp.addLiens(lien);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      // DEBUT TRAITEMENT DES CARREFOURS COMPLEXES :
      // (quand un noeud ref correpsond à un ensemble de noeuds et
      // arcs dans BDcomp)
      // On crée un nouveau groupe contenant tous les noeuds incomplets
      // ainsi que tous les arcs ayant aux 2 extrémités un de ces noeuds
      GroupeApp groupeComp = (GroupeApp) groupesComp.nouvelElement();
      List<ElementCarteTopo> noeudsIncomplets = new ArrayList<ElementCarteTopo>(
          incomplets);
      // ajout pour mieux gérer le cas des groupes impossibles
      for (int j = 0; j < incomplets.size(); j++) {
        NoeudApp noeudComp = (NoeudApp) incomplets.get(j);
        groupeComp.addNoeud(noeudComp);
        // noeudcomp.addGroupe(groupecomp);
        List<Arc> entrants = noeudComp.getEntrants();
        for (int k = 0; k < entrants.size(); k++) {
          NoeudApp noeudDeb = (NoeudApp) ((ArcApp) entrants.get(k))
              .getNoeudIni();
          for (int l = 0; l < incomplets.size(); l++) {
            if (noeudDeb == incomplets.get(l)) {
              groupeComp.addArc(entrants.get(k));
              // ((Arc_App)entrants.get(k)).addGroupe(groupecomp);
              break;
            }
          }
        }
      }
      // decomposition du groupe créé en groupes connexes
      List<Groupe> groupesConnexes = groupeComp.decomposeConnexes();
      // Choix du groupe connexe à conserver :
      // Qualification des groupes candidats en comparant les
      // entrants/sortants.
      // Un groupe comp est dit, par rapport au noeud ref, :
      // - complet si on trouve une correspondance entre
      // tous les incidents des noeuds ref et comp,
      // - incomplet si on trouve une correspondance entre
      // certains incidents
      // - impossible si on ne trouve aucune correspondance
      // NB: methode strictement similaire à ce qui était réalisé
      // sur les noeuds
      complets = new ArrayList<ElementCarteTopo>();
      incomplets = new ArrayList<ElementCarteTopo>();
      for (int j = 0; j < groupesConnexes.size(); j++) {
        groupeComp = (GroupeApp) groupesConnexes.get(j);
        int correspondance = noeudRef.correspCommunicants(groupeComp,
            liensPreAppAA);
        if (correspondance == 1) {
          complets.add(groupeComp);
        }
        if (correspondance == 0) {
          incomplets.add(groupeComp);
        }
      }
      // On a trouvé un unique groupe complet
      // C'est un appariement que l'on juge sûr
      if (complets.size() == 1) {
        // vidage des groupes rejetés, pour faire propre
        for (int j = 0; j < incomplets.size(); j++) {
          ((GroupeApp) incomplets.get(j)).vide();
        }
        groupeComp = (GroupeApp) complets.get(0);
        // filtrage du groupe connexe choisi
        groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef,
            liensPreAppAA);
        if (groupeComp.getListeNoeuds().size() == 0) {
          noeudRef.setResultatAppariement(I18N
              .getString("Appariement.Unmatched" + //$NON-NLS-1$
                  "UniqueCompleteGroup")); //$NON-NLS-1$
          nbSansHomologue = nbSansHomologue + 1;
          continue;
        }
        if ((groupeComp.getListeNoeuds().size() == 1)
            && (groupeComp.getListeArcs().size() == 0)) {
          NoeudApp noeudComp = (NoeudApp) groupeComp.getListeNoeuds().get(0);
          noeudRef.setResultatAppariement(I18N
              .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
          noeudComp.setResultatAppariement(I18N
              .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
          groupeComp.vide();
          nbNoeudNoeud++;
          LienReseaux lien = new LienReseaux();
          liens.add(lien);
          lien.addNoeuds1(noeudRef);
          noeudRef.addLiens(lien);
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          lien.setCommentaire(noeudRef.getResultatAppariement());
          continue;
        }
        noeudRef.setResultatAppariement(I18N
            .getString("Appariement.MatchedWithAGroup")); //$NON-NLS-1$
        groupeComp.setResultatAppariement(I18N
            .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
        nbNoeudGroupe++;
        for (int j = 0; j < groupeComp.getListeArcs().size(); j++) {
          ((ArcApp) groupeComp.getListeArcs().get(j))
              .setResultatAppariement(I18N
                  .getString("Appariement.MatchedWithANodeInAGroup")); //$NON-NLS-1$
        }
        for (int j = 0; j < groupeComp.getListeNoeuds().size(); j++) {
          ((NoeudApp) groupeComp.getListeNoeuds().get(j))
              .setResultatAppariement(I18N
                  .getString("Appariement.MatchedWithANodeInAGroup")); //$NON-NLS-1$
        }
        LienReseaux lien = new LienReseaux();
        liens.add(lien);
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.addGroupes2(groupeComp);
        groupeComp.addLiens(lien);
        lien.setEvaluation(1);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      // plusieurs groupes sont bien appariés (complets) avec un
      // noeud ref.
      // On est sur qu'il y a sur-appariement
      // (à raffiner dans le futur si possible).
      // Cas qui ne devrait pas arriver d'après Thomas
      // (si les seuils sont bien choisis !).
      if (complets.size() > 1) {
        // vidage des groupes rejetés, pour faire propre
        for (int j = 0; j < incomplets.size(); j++) {
          ((GroupeApp) incomplets.get(j)).vide();
        }

        List<ElementCarteTopo> complets2 = new ArrayList<ElementCarteTopo>();
        for (int j = 0; j < complets.size(); j++) {
          groupeComp = (GroupeApp) complets.get(j);
          groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef,
              liensPreAppAA);
          if (groupeComp.getListeNoeuds().size() != 0) {
            complets2.add(groupeComp);
          }
        }
        complets = new ArrayList<ElementCarteTopo>(complets2);
        // aucun goupe complet restant après les filtrages
        if (complets.size() == 0) {
          noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UnmatchedEmptiedCompleteGroups")); //$NON-NLS-1$
          nbSansHomologue++;
          continue;
        }
        // un seul goupe complet restant après les filtrages
        if (complets.size() == 1) {
          LienReseaux lien = new LienReseaux();
          liens.add(lien);
          noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithCompleteGroup")); //$NON-NLS-1$
          nbNoeudGroupeIncertain++;
          groupeComp = (GroupeApp) complets.get(0);
          groupeComp.setResultatAppariement(I18N
              .getString("Appariement.UncertainOnlyCompleteGroup")); //$NON-NLS-1$
          for (int k = 0; k < groupeComp.getListeArcs().size(); k++) {
            ((ArcApp) groupeComp.getListeArcs().get(k))
                .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                    "UncertainInTheOnlyCompleteGroup")); //$NON-NLS-1$
          }
          for (int k = 0; k < groupeComp.getListeNoeuds().size(); k++) {
            ((NoeudApp) groupeComp.getListeNoeuds().get(k))
                .setResultatAppariement(I18N.getString("Appariement.Uncertain" + //$NON-NLS-1$
                    "InTheOnlyCompleteGroup")); //$NON-NLS-1$
          }
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          lien.setCommentaire(noeudRef.getResultatAppariement());
          lien.addGroupes2(groupeComp);
          groupeComp.addLiens(lien);
          lien.addNoeuds1(noeudRef);
          noeudRef.addLiens(lien);
          continue;
        }
        // plusieurs goupes complets restant après les filtrages
        LienReseaux lien = new LienReseaux();
        liens.add(lien);
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWith" + //$NON-NLS-1$
            "SeveralCompleteGroups")); //$NON-NLS-1$
        nbPlusieursGroupesComplets++;
        for (int j = 0; j < complets.size(); j++) {
          groupeComp = (GroupeApp) complets.get(j);
          groupeComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainOneOfSeveral" + //$NON-NLS-1$
              "CompetingCompleteGroups")); //$NON-NLS-1$
          for (int k = 0; k < groupeComp.getListeArcs().size(); k++) {
            ((ArcApp) groupeComp.getListeArcs().get(k))
                .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                    "UncertainInOneOfSeveral" + //$NON-NLS-1$
                    "CompetingCompleteGroups")); //$NON-NLS-1$
          }
          for (int k = 0; k < groupeComp.getListeNoeuds().size(); k++) {
            ((NoeudApp) groupeComp.getListeNoeuds().get(k))
                .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                    "UncertainInOneOfSeveral" + //$NON-NLS-1$
                    "CompetingCompleteGroups")); //$NON-NLS-1$
          }
          lien.addGroupes2(groupeComp);
          groupeComp.addLiens(lien);
        }
        lien.setEvaluation(0);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        continue;
      }
      // On a trouvé un unique groupe incomplet. C'est un appariement
      // que l'on accepte mais qu'on l'on juge incertain
      if (incomplets.size() == 1) {
        groupeComp = (GroupeApp) incomplets.get(0);
        groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef,
            liensPreAppAA);
        if (groupeComp.getListeNoeuds().size() == 0) {
          noeudRef.setResultatAppariement(I18N
              .getString("Appariement.UnmatchedOnly" + //$NON-NLS-1$
                  "EmptiedIncompleteGroup")); //$NON-NLS-1$
          nbSansHomologue++;
          continue;
        }
        if ((groupeComp.getListeNoeuds().size() == 1)
            && (groupeComp.getListeArcs().size() == 0)) {
          NoeudApp noeudComp = (NoeudApp) groupeComp.getListeNoeuds().get(0);
          noeudRef.setResultatAppariement(I18N
              .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
          noeudComp.setResultatAppariement(I18N
              .getString("Appariement.MatchedWithANode")); //$NON-NLS-1$
          groupeComp.vide();
          nbNoeudNoeudIncertain++;
          LienReseaux lien = new LienReseaux();
          liens.add(lien);
          lien.addNoeuds1(noeudRef);
          noeudRef.addLiens(lien);
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          lien.setCommentaire(noeudRef.getResultatAppariement());
          continue;
        }
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithAnIncompleteGroup")); //$NON-NLS-1$
        nbNoeudGroupeIncertain = nbNoeudGroupeIncertain + 1;
        groupeComp.setResultatAppariement(I18N
            .getString("Appariement.UncertainIncompleteGroup")); //$NON-NLS-1$
        for (int k = 0; k < groupeComp.getListeArcs().size(); k++) {
          ((ArcApp) groupeComp.getListeArcs().get(k))
              .setResultatAppariement(I18N
                  .getString("Appariement.UncertainInAnIncompleteGroup")); //$NON-NLS-1$
        }
        for (int k = 0; k < groupeComp.getListeNoeuds().size(); k++) {
          ((NoeudApp) groupeComp.getListeNoeuds().get(k))
              .setResultatAppariement(I18N
                  .getString("Appariement.UncertainInAnIncompleteGroup")); //$NON-NLS-1$
        }
        // param.db.create(groupeComp);
        LienReseaux lien = new LienReseaux();
        liens.add(lien);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.addGroupes2(groupeComp);
        groupeComp.addLiens(lien);
        continue;
      }
      // On a trouvé plusieurs groupes incomplets. C'est un appariement
      // que l'on accepte peut-être mais que l'on sait incohérent.
      if (incomplets.size() > 1) {
        List<ElementCarteTopo> incomplets2 = new ArrayList<ElementCarteTopo>();
        for (int j = 0; j < incomplets.size(); j++) {
          groupeComp = (GroupeApp) incomplets.get(j);
          groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef,
              liensPreAppAA);
          if (groupeComp.getListeNoeuds().size() != 0) {
            incomplets2.add(groupeComp);
          }
        }
        incomplets = new ArrayList<ElementCarteTopo>(incomplets2);
        // aucun goupe incomplet restant après le filtrage
        if (incomplets.size() == 0) {
          noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UnmatchedEmptiedIncompleteGroups")); //$NON-NLS-1$
          nbSansHomologue = nbSansHomologue + 1;
          continue;
        }
        // un seul goupe incomplet restant après le filtrage
        if (incomplets.size() == 1) {
          LienReseaux lien = new LienReseaux();
          liens.add(lien);
          groupeComp = (GroupeApp) incomplets.get(0);
          groupeComp.setResultatAppariement(I18N
              .getString("Appariement.UncertainInco" + //$NON-NLS-1$
                  "mpleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
          for (int k = 0; k < groupeComp.getListeArcs().size(); k++) {
            ((ArcApp) groupeComp.getListeArcs().get(k))
                .setResultatAppariement(I18N
                    .getString("Appariement.UncertainInInco" + //$NON-NLS-1$
                        "mpleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
          }
          for (int k = 0; k < groupeComp.getListeNoeuds().size(); k++) {
            ((NoeudApp) groupeComp.getListeNoeuds().get(k))
                .setResultatAppariement(I18N
                    .getString("Appariement.UncertainInIncomplete" + //$NON-NLS-1$
                        "GroupEmptiedIncompleteGroups")); //$NON-NLS-1$
          }
          lien.addGroupes2(groupeComp);
          groupeComp.addLiens(lien);
          noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithAnIncomplete" + //$NON-NLS-1$
              "GroupEmptiedIncompleteGroups")); //$NON-NLS-1$
          nbNoeudGroupeIncertain++;
          lien.addNoeuds1(noeudRef);
          noeudRef.addLiens(lien);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          lien.setCommentaire(noeudRef.getResultatAppariement());
          continue;
        }
        // plusieurs goupes incomplets restant après les filtrages
        LienReseaux lien = new LienReseaux();
        liens.add(lien);
        for (int j = 0; j < incomplets.size(); j++) {
          groupeComp = (GroupeApp) incomplets.get(j);
          groupeComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainAmongSeveralIncompleteGroups")); //$NON-NLS-1$
          for (int k = 0; k < groupeComp.getListeArcs().size(); k++) {
            ((ArcApp) groupeComp.getListeArcs().get(k))
                .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                    "UncertainInOneOf" + //$NON-NLS-1$
                    "SeveralIncompleteGroups")); //$NON-NLS-1$
          }
          for (int k = 0; k < groupeComp.getListeNoeuds().size(); k++) {
            ((NoeudApp) groupeComp.getListeNoeuds().get(k))
                .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                    "Uncertain" + //$NON-NLS-1$
                    "InOneOfSeveralIncompleteGroups")); //$NON-NLS-1$
          }
          lien.addGroupes2(groupeComp);
          groupeComp.addLiens(lien);
        }
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatched" + //$NON-NLS-1$
            "WithSeveralIncompleteGroups")); //$NON-NLS-1$
        nbPlusieursGroupesComplets++;
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.setEvaluation(0);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        continue;
      }
      if (incomplets.size() == 0) { // ajout Seb 31/05/05
        noeudRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainImpossibleCandidateGroups")); //$NON-NLS-1$
        nbNoeudNoeudIncertain++;
        LienReseaux lien = new LienReseaux();
        liens.add(lien);
        lien.addNoeuds1(noeudRef);
        noeudRef.addLiens(lien);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        lien.setCommentaire(noeudRef.getResultatAppariement());
        for (int j = 0; j < noeudsIncomplets.size(); j++) {
          NoeudApp noeudComp = (NoeudApp) noeudsIncomplets.get(j);
          noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "Uncertain" + //$NON-NLS-1$
              "ImpossibleCandidateGroups")); //$NON-NLS-1$
          lien.addNoeuds2(noeudComp);
          noeudComp.addLiens(lien);
        }
        continue;
      }
    }
    // pour le réseau routier uniquement: on traite le cas des rond-points
    // si un noeud est apparié avec une partie de rond point,
    // il devient apparié avec l'ensemble du rond point
    if (param.varianteChercheRondsPoints) {
      Appariement.rondsPoints(reseau1, reseau2, liens, liensPreAppAA, param);
    }
    // Fin, affichage du bilan
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.NodeAssessment")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.CorrectMatches")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbNoeudNoeud + I18N //$NON-NLS-1$
          .getString("Appariement." + //$NON-NLS-1$
              "NodesMatchedWithASingleNode")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbNoeudGroupe + I18N //$NON-NLS-1$
          .getString("Appariement." + //$NON-NLS-1$
              "NodesMatchedWithASingleGroup")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UncertainMatches")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbNoeudNoeudIncertain + I18N //$NON-NLS-1$
          .getString("Appariement." + //$NON-NLS-1$
              "NodesMatchedWithASingleIncompleteNode")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbPlusieursNoeudsComplets //$NON-NLS-1$
          + I18N.getString("Appariement." + //$NON-NLS-1$
              "NodesMatchedWithASeveralNodes")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbNoeudGroupeIncertain + I18N //$NON-NLS-1$
          .getString("Appariement." + //$NON-NLS-1$
              "NodesMatchedWithASingleIncompleteGroup")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.IncoherentMatches")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbSansHomologue + I18N //$NON-NLS-1$
          .getString("Appariement." + //$NON-NLS-1$
              "NodesWithoutMatchFound")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbPlusieursGroupesComplets //$NON-NLS-1$
          + I18N.getString("Appariement." + //$NON-NLS-1$
              "NodesWithSeveralHomologousGroups")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnprocessedNodes")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbNonTraite + I18N //$NON-NLS-1$
          .getString("Appariement.IsolatedNodes")); //$NON-NLS-1$
    }
    return liens;
  }

  /**
   * Minumum roundabout compacity.
   */
  private static final double MIN_COMPACITY = 0.95;
  /**
   * Maximum roundabout surface.
   */
  private static final double MAX_ROUNDABOUT_SURFACE = 8000;

  /**
   * Pour le réseau routier uniquement: on traite le cas des rond-points. Si un
   * noeud est apparié avec une partie de rond point, il devient apparié avec
   * l'ensemble du rond point.
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param liensNoeuds node linls
   * @param liensPreAppAA prematching edge links
   * @param param matching parameters
   */
  private static void rondsPoints(final CarteTopo reseau1,
      final CarteTopo reseau2, final EnsembleDeLiens liensNoeuds,
      final EnsembleDeLiens liensPreAppAA, final ParametresApp param) {
    Iterator<?> itFaces = reseau2.getPopFaces().getElements().iterator();
    while (itFaces.hasNext()) {
      Face face = (Face) itFaces.next();
      double compacite = IndicesForme.indiceCompacite(face.getGeometrie());
      if (compacite < Appariement.MIN_COMPACITY) {
        continue;
      }
      if (face.getGeometrie().area() > Appariement.MAX_ROUNDABOUT_SURFACE) {
        continue;
      }
      // rond point de diametre>100m
      List<Noeud> noeudsDuRondPoint = face.noeuds();
      Iterator<?> itNoeudsDuRondPoint = noeudsDuRondPoint.iterator();
      while (itNoeudsDuRondPoint.hasNext()) {
        NoeudApp noeud = (NoeudApp) itNoeudsDuRondPoint.next();
        List<LienReseaux> liensDuNoeudDuRondPoint = noeud.getLiens(liensNoeuds
            .getElements());
        Iterator<?> itLiensDuNoeudsDuRondPoint = liensDuNoeudDuRondPoint
            .iterator();
        // Pour un lien d'un noeud du rond point, 2 cas possibles:
        // 1/ avant ce lien ne pointait que vers ce noeud
        while (itLiensDuNoeudsDuRondPoint.hasNext()) {
          LienReseaux lien = (LienReseaux) itLiensDuNoeudsDuRondPoint.next();
          NoeudApp noeudRef = (NoeudApp) lien.getNoeuds1().get(0);
          if (lien.getNoeuds2().size() == 1) { // cas 1
            GroupeApp rondPoint = (GroupeApp) reseau2.getPopGroupes()
                .nouvelElement();
            rondPoint.addAllNoeuds(new ArrayList<Noeud>(noeudsDuRondPoint));
            rondPoint.addAllArcs(new ArrayList<Arc>(face.arcs()));
            lien.getNoeuds2().clear();
            noeud.getLiens().remove(lien);
            lien.addGroupes2(rondPoint);
            rondPoint.addLiens(lien);
            if (noeudRef.correspCommunicants(rondPoint, liensPreAppAA) == 1) {
              lien.setEvaluation(1);
              rondPoint.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                  "MatchedWithANode" + //$NON-NLS-1$
                  "-Roundabout")); //$NON-NLS-1$
              for (int k = 0; k < rondPoint.getListeArcs().size(); k++) {
                ((ArcApp) rondPoint.getListeArcs().get(k))
                    .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                        "MatchedWithANode" + //$NON-NLS-1$
                        "-InRoundabout")); //$NON-NLS-1$
              }
              for (int k = 0; k < rondPoint.getListeNoeuds().size(); k++) {
                ((NoeudApp) rondPoint.getListeNoeuds().get(k))
                    .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                        "MatchedWithANode" + //$NON-NLS-1$
                        "-InRoundabout")); //$NON-NLS-1$
              }
            } else {
              rondPoint.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                  "UncertainMatched" + //$NON-NLS-1$
                  "WithANode" + //$NON-NLS-1$
                  "-IncompleteRoundabout")); //$NON-NLS-1$
              for (int k = 0; k < rondPoint.getListeArcs().size(); k++) {
                ((ArcApp) rondPoint.getListeArcs().get(k))
                    .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                        "UncertainMatched" + //$NON-NLS-1$
                        "WithANode" + //$NON-NLS-1$
                        "-InIncompleteRoundabout")); //$NON-NLS-1$
              }
              for (int k = 0; k < rondPoint.getListeNoeuds().size(); k++) {
                ((NoeudApp) rondPoint.getListeNoeuds().get(k))
                    .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                        "UncertainMatched" + //$NON-NLS-1$
                        "WithANode" + //$NON-NLS-1$
                        "-InIncompleteRoundabout")); //$NON-NLS-1$
              }
            }
            continue;
          }
        }
        // 2/ avant ce lien pointait vers une partie du rond point
        Iterator<?> itGroupesDuNoeud = noeud.getListeGroupes().iterator();
        while (itGroupesDuNoeud.hasNext()) {
          GroupeApp groupe = (GroupeApp) itGroupesDuNoeud.next();
          List<?> liens = groupe.getLiens(liensNoeuds.getElements());
          if ((liens.size() == 0) || (groupe.getLiens().size() == 0)) {
            continue;
          }
          if (liensNoeuds.getElements().contains(groupe.getLiens().get(0))) {
            Iterator<?> itNoeudsDuRondPoint2 = noeudsDuRondPoint.iterator();
            while (itNoeudsDuRondPoint2.hasNext()) {
              NoeudApp noeud2 = (NoeudApp) itNoeudsDuRondPoint2.next();
              if (!groupe.getListeNoeuds().contains(noeud2)) {
                groupe.addNoeud(noeud2);
              }
            }
            Iterator<?> itArcsDuRondPoint = face.arcs().iterator();
            while (itArcsDuRondPoint.hasNext()) {
              ArcApp arc2 = (ArcApp) itArcsDuRondPoint.next();
              if (!groupe.getListeArcs().contains(arc2)) {
                groupe.addArc(arc2);
              }
            }
          }
          LienReseaux lien = (LienReseaux) liens.get(0);
          NoeudApp noeudRef = (NoeudApp) lien.getNoeuds1().get(0);
          if (noeudRef.correspCommunicants(groupe, liensPreAppAA) == 1) {
            lien.setEvaluation(1);
            groupe.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                "MatchedWithANode-Roundabout")); //$NON-NLS-1$
            for (int k = 0; k < groupe.getListeArcs().size(); k++) {
              ((ArcApp) groupe.getListeArcs().get(k))
                  .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                      "MatchedWithANode" + //$NON-NLS-1$
                      "-InRoundabout")); //$NON-NLS-1$
            }
            for (int k = 0; k < groupe.getListeNoeuds().size(); k++) {
              ((NoeudApp) groupe.getListeNoeuds().get(k))
                  .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                      "MatchedWithANode" + //$NON-NLS-1$
                      "-InRoundabout")); //$NON-NLS-1$
            }
          } else {
            groupe.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                "UncertainMatchedWithANode" + //$NON-NLS-1$
                "-InIncompleteRoundabout")); //$NON-NLS-1$
            for (int k = 0; k < groupe.getListeArcs().size(); k++) {
              ((ArcApp) groupe.getListeArcs().get(k))
                  .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                      "UncertainMatchedWithANode" + //$NON-NLS-1$
                      "-InIncompleteRoundabout")); //$NON-NLS-1$
            }
            for (int k = 0; k < groupe.getListeNoeuds().size(); k++) {
              ((NoeudApp) groupe.getListeNoeuds().get(k))
                  .setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
                      "UncertainMatchedWithANode" + //$NON-NLS-1$
                      "-InIncompleteRoundabout")); //$NON-NLS-1$
            }
          }

        }
      }
    }
  }

  /**
   * On ne garde que le noeud comp le plus proche.
   * @param complets list of objects
   * @param noeudRef reference node
   */
  private static void filtrageNoeudsComplets(
      final List<ElementCarteTopo> complets, final NoeudApp noeudRef) {
    if (complets.size() <= 1) {
      return;
    }
    NoeudApp noeudPlusProche = (NoeudApp) complets.get(0);
    double dmin = noeudPlusProche.distance(noeudRef);
    for (int i = 1; i < complets.size(); i++) {
      NoeudApp noeudComp = (NoeudApp) complets.get(i);
      double d = noeudComp.distance(noeudRef);
      if (d < dmin) {
        dmin = d;
        noeudPlusProche = noeudComp;
      }
    }
    complets.clear();
    complets.add(noeudPlusProche);
  }

  /**
   * Renvoie le noeud de 'noeuds' le plus proche de 'noeudRef'.
   * @param noeuds list of nodes
   * @param noeudRef reference node
   * @return the node is the list closest to the reference node
   */
  private static NoeudApp noeudLePlusProche(
      final List<ElementCarteTopo> noeuds, final NoeudApp noeudRef) {
    if (noeuds.size() < 1) {
      return null;
    }
    Iterator<ElementCarteTopo> itNoeuds = noeuds.iterator();
    NoeudApp plusProche = (NoeudApp) itNoeuds.next();
    double dmin = plusProche.distance(noeudRef);
    while (itNoeuds.hasNext()) {
      NoeudApp noeud = (NoeudApp) itNoeuds.next();
      double d = noeud.distance(noeudRef);
      if (d < dmin) {
        dmin = d;
        plusProche = noeud;
      }
    }
    return plusProche;
  }

  /**
   * Appariement des arcs, s'appuyant sur un appariement préalable des noeuds,
   * et sur un pré-appariement des arcs. S'appuie essentiellement sur la notion
   * de 'plus proche chemin d'un arc', défini comme le chemin minimisant la
   * surface entre le chemin et l'arc.
   * @param reseau1
   *        network 1
   * @param reseau2
   *        network 2
   * @param liensPreAppAA
   *        prematching edge links
   * @param liensAppNoeuds
   *        prematching node links
   * @param param
   *        matching parameters
   * @return resulting set of links
   */
  public static EnsembleDeLiens appariementArcs(final CarteTopo reseau1, final CarteTopo reseau2,
      final EnsembleDeLiens liensPreAppAA, final EnsembleDeLiens liensAppNoeuds,
      final ParametresApp param) {
    
    List<Arc> tousArcs = new ArrayList<Arc>();
    int nbSansHomologuePbNoeud = 0, nbSansHomologuePbPCC = 0, nbOkUneSerie = 0, nbOkPlusieursSeries = 0, nbDouteuxPbNoeud = 0, nbDouteuxPbSens = 0, nbTot = 0;
    double longSansHomologuePbNoeud = 0, longSansHomologuePbPCC = 0, longOkUneSerie = 0, longOkPlusieursSeries = 0, longDouteuxPbNoeud = 0, longDouteuxPbSens = 0, longTot = 0;
    
    EnsembleDeLiens liensArcsArcs = new EnsembleDeLiens(LienReseaux.class);
    liensArcsArcs.setNom(I18N.getString("Appariement.EdgeMatching")); //$NON-NLS-1$
    
    // on étudie tous les arc ref, un par un, indépendamment les uns des autres
    for (Arc edge : reseau1.getPopArcs()) {
      LOGGER.debug("appariementArcs : Edge " + edge);
      ArcApp arcRef = (ArcApp) edge;
      arcRef.setResultatAppariement(I18N.getString("Appariement.NA")); //$NON-NLS-1$
      // pour vérifier que tous les cas sont bien traités
      // /////// ETUDE DES EXTREMITES DE L'ARC /////////
      // problème de topologie ?
      if ((arcRef.getNoeudIni() == null) || (arcRef.getNoeudFin() == null)) {
        // Cas 1 : l'arc n'a pas de noeud à une de ses extrémités
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UnmatchedEdgeMissingANode")); //$NON-NLS-1$
        nbSansHomologuePbNoeud++;
        longSansHomologuePbNoeud = longSansHomologuePbNoeud + arcRef.longueur();
        continue;
      }
      // On ne sait pas traiter les boucles
      // (CODE A AFFINER POUR FAIRE CELA)
      if (arcRef.getNoeudIni() == arcRef.getNoeudFin()) {
        arcRef.setResultatAppariement(I18N
            .getString("Appariement.UnmatchedLoop")); //$NON-NLS-1$
        nbSansHomologuePbPCC++;
        longSansHomologuePbPCC = longSansHomologuePbPCC + arcRef.longueur();
        continue;
      }
      // Recherche des noeuds en correspondance avec les extrémités de
      // l'arc, que ce soit en entree ou en sortie pour l'arc
      // (au sens de la circulation)
      List<List<Noeud>> noeudsInOut = arcRef
          .noeudsEnCorrespondanceAuxExtremites(liensAppNoeuds, liensPreAppAA);
      List<Noeud> noeudsDebutIn = noeudsInOut.get(0);
      List<Noeud> noeudsDebutOut = noeudsInOut.get(1);
      List<Noeud> noeudsFinIn = noeudsInOut.get(2);
      List<Noeud> noeudsFinOut = noeudsInOut.get(3);
      if (((noeudsFinIn.isEmpty()) && (noeudsFinOut.isEmpty()))
          || ((noeudsDebutIn.isEmpty()) && (noeudsDebutOut.isEmpty()))) {
        // Cas 2 : un noeud extrémité n'est pas apparié
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UnmatchedNodeUnmatched")); //$NON-NLS-1$
        nbSansHomologuePbNoeud++;
        longSansHomologuePbNoeud = longSansHomologuePbNoeud + arcRef.longueur();
        continue;
      }
      // /////// CALCUL DES PLUS COURTS CHEMINS /////////
      // creation d'un groupe "tousCandidats" avec tous les arcs candidats issus du pré-appariement
      tousArcs = arcRef.arcsCompEnCorrespondance(liensPreAppAA);
      GroupeApp tousCandidats = (GroupeApp) reseau2.getPopGroupes().nouvelElement();
      tousCandidats.setListeArcs(new ArrayList<Arc>(tousArcs));
      for (Arc arcComp : tousCandidats.getListeArcs()) {
        arcComp.addGroupe(tousCandidats);
      }
      tousCandidats.ajouteNoeuds();
      // Pour éviter les débordements, on ne cherche que les pcc pas trop grands.
      // longMaxRecherche = arcRef.getGeometrie().length()*5* param.coefficentPoidsLongueurDistance;
      double longMaxRecherche = arcRef.getGeometrie().length() * param.distanceArcsMax;
      // calcul du poids des arcs
      Appariement.calculePoids(arcRef, tousCandidats, param);
      LOGGER.debug("Shortest Path between ");
      for (Noeud n : noeudsDebutOut) {
        LOGGER.debug("\tStart : " + n.getGeometrie());
      }
      for (Noeud n : noeudsFinIn) {
        LOGGER.debug("\tEnd : " + n.getGeometrie());
      }
      // Recherche du PCC dans un sens, et dans l'autre si l'arc est en double sens.
      GroupeApp pccMin1 = tousCandidats.plusCourtChemin(noeudsDebutOut, noeudsFinIn,
          longMaxRecherche);
      GroupeApp pccMin2 = tousCandidats.plusCourtChemin(noeudsFinOut, noeudsDebutIn,
          longMaxRecherche);
      tousCandidats.videEtDetache();
      // /////// ANALYSE DES PLUS COURTS CHEMINS /////////
      // cas 3 : on n'a trouvé aucun plus court chemin, dans aucun sens
      if ((pccMin1 == null) && (pccMin2 == null)) {
        LOGGER.debug("\t cas 3 : on n'a trouvé aucun plus court chemin, dans aucun sens");
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UnmatchedNoShortestPath")); //$NON-NLS-1$
        nbSansHomologuePbPCC++;
        longSansHomologuePbPCC = longSansHomologuePbPCC + arcRef.longueur();
        continue;
      }
      // cas 4 : on a trouvé un pcc dans un seul sens: celui direct
      if (pccMin1 == null) {
        LOGGER.debug("\t cas 4 : on a trouvé un pcc dans un seul sens: celui direct");
        LienReseaux lien = (LienReseaux) liensArcsArcs.nouvelElement();
        lien.addArcs1(arcRef);
        arcRef.addLiens(lien);
        if (pccMin2.getListeArcs().size() == 0) {
          // cas 4a : on a trouvé un pcc mais il est réduit à un point
          NoeudApp noeudComp = (NoeudApp) pccMin2.getListeNoeuds().get(0);
          lien.addNoeuds2(noeudComp);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          noeudComp.addLiens(lien);
          arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithACompNode")); //$NON-NLS-1$
          noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithAnEdge")); //$NON-NLS-1$
          pccMin2.videEtDetache();
          nbDouteuxPbNoeud++;
          longDouteuxPbNoeud = longDouteuxPbNoeud + arcRef.longueur();
          continue;
        }
        pccMin2.enleveExtremites();
        lien.addGroupes2(pccMin2);
        pccMin2.addLiens(lien);
        if (arcRef.getOrientation() == -1) {
          // cas 4b : on a trouvé un pcc dans un seul sens,
          // et c'est normal
          lien.setEvaluation(1);
          arcRef.setResultatAppariement("Matched with an edge or a succession of edges.");
          pccMin2.setResultatAppariementGlobal("Matched with a ref edge.");
          nbOkUneSerie++;
          longOkUneSerie = longOkUneSerie + arcRef.longueur();
        } else {
          // cas 4c : on a trouvé un pcc dans un seul sens,
          // mais ce n'est pas normal
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedOnlyOneWay" //$NON-NLS-1$
          ));
          pccMin2.setResultatAppariementGlobal(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedOnlyOneWay")); //$NON-NLS-1$
          nbDouteuxPbSens++;
          longDouteuxPbSens = longDouteuxPbSens + arcRef.longueur();
        }
        continue;
      }
      // cas 4 : on a trouvé un pcc dans un seul sens: celui indirect
      if (pccMin2 == null) {
        LOGGER.debug("\t cas 4 : on a trouvé un pcc dans un seul sens: celui indirect");
        LienReseaux lien = (LienReseaux) liensArcsArcs.nouvelElement();
        lien.addArcs1(arcRef);
        arcRef.addLiens(lien);
        if (pccMin1.getListeArcs().isEmpty()) {
          // cas 4a : on a trouvé un pcc
          // mais il est réduit à un point
          NoeudApp noeudComp = (NoeudApp) pccMin1.getListeNoeuds().get(0);
          lien.addNoeuds2(noeudComp);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          noeudComp.addLiens(lien);
          arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithACompNode")); //$NON-NLS-1$
          noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithAnEdge")); //$NON-NLS-1$
          pccMin1.videEtDetache();
          nbDouteuxPbNoeud++;
          longDouteuxPbNoeud = longDouteuxPbNoeud + arcRef.longueur();
          continue;
        }
        pccMin1.enleveExtremites();
        lien.addGroupes2(pccMin1);
        pccMin1.addLiens(lien);
        // pour noter le résultat sur les objets concernés
        if (arcRef.getOrientation() == 1) {
          // cas 4a : on a trouvé un pcc dans un seul sens,
          // et c'est normal
          lien.setEvaluation(1);
          arcRef.setResultatAppariement("Matched with an edge or a succession of edges");
          pccMin1.setResultatAppariementGlobal("Matched with a ref edge");
          nbOkUneSerie++;
          longOkUneSerie = longOkUneSerie + arcRef.longueur();
        } else {
          // cas 4b : on a trouvé un pcc dans un seul sens,
          // mais ce n'est pas normal
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedOnlyOneWay" //$NON-NLS-1$
          ));
          pccMin1.setResultatAppariementGlobal(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedOnlyOneWay" //$NON-NLS-1$
          ));
          nbDouteuxPbSens++;
          longDouteuxPbSens = longDouteuxPbSens + arcRef.longueur();
        }
        continue;
      }
      // cas 5 : on a trouvé un pcc dans les 2 sens, et c'est le même
      if (pccMin1.contientMemesArcs(pccMin2)) {
        LOGGER.debug("\t cas 5 : on a trouvé un pcc dans les 2 sens, et c'est le même");
        pccMin2.videEtDetache();
        LienReseaux lien = (LienReseaux) liensArcsArcs.nouvelElement();
        lien.addArcs1(arcRef);
        arcRef.addLiens(lien);
        if (pccMin1.getListeArcs().isEmpty()) {
          // cas 5a : on a trouvé un pcc mais il est réduit à un point
          LOGGER.debug("\t \t cas 5a : on a trouvé un pcc mais il est réduit à un point");
          NoeudApp noeudComp = (NoeudApp) pccMin1.getListeNoeuds().get(0);
          lien.addNoeuds2(noeudComp);
          lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
          noeudComp.addLiens(lien);
          arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "Uncertain" + //$NON-NLS-1$
              "MatchedWithACompNode")); //$NON-NLS-1$
          noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatchedWithAnEdge" //$NON-NLS-1$
          ));
          pccMin1.videEtDetache();
          nbDouteuxPbNoeud++;
          longDouteuxPbNoeud = longDouteuxPbNoeud + arcRef.longueur();
          continue;
        }
        pccMin1.enleveExtremites();
        // cas 5b : on a trouvé un pcc dans les 2 sens, non réduit à un point
        LOGGER.debug("\t \t cas 5b : on a trouvé un pcc dans les 2 sens, non réduit à un point");
        LOGGER.debug("\t \t Shortest Path");
        for (Arc a : pccMin1.getListeArcs()) {
          LOGGER.debug(a.getGeometrie());
        }
        lien.addGroupes2(pccMin1);
        lien.setEvaluation(1);
        pccMin1.addLiens(lien);
        arcRef.setResultatAppariement(I18N.getString("Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
        pccMin1.setResultatAppariementGlobal(I18N.getString("Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
        nbOkUneSerie++;
        longOkUneSerie = longOkUneSerie + arcRef.longueur();
        continue;
      }
      // cas 6 : on a trouvé un pcc dans les 2 sens, mais ce n'est pas le même.
      // cas d'arcs en parralèle
      LOGGER.debug("\t cas 6 : on a trouvé un pcc dans les 2 sens, mais ce n'est pas le même.");
      LienReseaux lien = (LienReseaux) liensArcsArcs.nouvelElement();
      lien.addArcs1(arcRef);
      arcRef.addLiens(lien);
      if (pccMin1.getListeArcs().isEmpty()) {
        // cas 6a : on a trouvé un pcc mais il est réduit à un point
        LOGGER.debug("\t \t cas 6a : on a trouvé un pcc mais il est réduit à un point.");
        NoeudApp noeudComp = (NoeudApp) pccMin1.getListeNoeuds().get(0);
        lien.addNoeuds2(noeudComp);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        noeudComp.addLiens(lien);
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithACompNode" //$NON-NLS-1$
        ));
        noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithAnEdge")); //$NON-NLS-1$
        pccMin1.videEtDetache();
      } else {
        // cas 6b : on a trouvé un pcc non réduit à un point
        LOGGER.debug("\t \t cas 6b : on a trouvé un pcc non réduit à un point.");
        pccMin1.enleveExtremites();
        lien.setEvaluation(1);
        lien.addGroupes2(pccMin1);
        pccMin1.addLiens(lien);
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "MatchedWithSeveralEdges")); //$NON-NLS-1$
        pccMin1.setResultatAppariementGlobal(I18N.getString("Appariement." + //$NON-NLS-1$
            "MatchedWithAnEdge")); //$NON-NLS-1$
      }
      if (pccMin2.getListeArcs().isEmpty()) {
        // cas 6a : on a trouvé un pcc mais il est réduit à un point
        LOGGER.debug("\t \t cas 6a : on a trouvé un pcc mais il est réduit à un point.");
        NoeudApp noeudComp = (NoeudApp) pccMin2.getListeNoeuds().get(0);
        lien.addNoeuds2(noeudComp);
        lien.setEvaluation(Appariement.ZERO_POINT_FIVE);
        noeudComp.addLiens(lien);
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithACompNode" //$NON-NLS-1$
        ));
        noeudComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatchedWithAnEdge")); //$NON-NLS-1$
        pccMin2.videEtDetache();
        nbDouteuxPbNoeud++;
        longDouteuxPbNoeud = longDouteuxPbNoeud + arcRef.longueur();
      } else {
        // cas 6b : on a trouvé un pcc non réduit à un point
        LOGGER.debug("\t \t cas 6b : on a trouvé un pcc non réduit à un point.");
        pccMin2.enleveExtremites();
        lien.addGroupes2(pccMin2);
        pccMin2.addLiens(lien);
        arcRef.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "MatchedWithSeveralEdges")); //$NON-NLS-1$
        pccMin2.setResultatAppariementGlobal(I18N.getString("Appariement." + //$NON-NLS-1$
            "MatchedWithAnEdge")); //$NON-NLS-1$
        if (lien.getEvaluation() == 1) {
          nbOkPlusieursSeries++;
          longOkPlusieursSeries = longOkPlusieursSeries + arcRef.longueur();
        } else {
          nbDouteuxPbNoeud++;
          longDouteuxPbNoeud = longDouteuxPbNoeud + arcRef.longueur();
        }
      }
    }
    // Fin, affichage du bilan
    longTot = longSansHomologuePbNoeud + longSansHomologuePbPCC
        + longOkUneSerie + longOkPlusieursSeries + longDouteuxPbNoeud
        + longDouteuxPbSens;
    nbTot = reseau1.getPopArcs().getElements().size();
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.EdgeAssessment")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.Network1TotalLength") //$NON-NLS-1$
          + Math.round(longTot / Appariement.THOUSAND)
          + I18N.getString("Appariement.KmIfMetric")); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.CorrectMatches")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbOkUneSerie + I18N //$NON-NLS-1$
              .getString("Appariement." + //$NON-NLS-1$
                  "EdgesMatches" + //$NON-NLS-1$
                  "WithOneOrSeveralCompEdges") //$NON-NLS-1$
          + nbOkUneSerie * Appariement.HUNDRED / nbTot
          + "%nb, " //$NON-NLS-1$
          + Math.round(longOkUneSerie * Appariement.HUNDRED / longTot)
          + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbOkPlusieursSeries + I18N //$NON-NLS-1$
              .getString("Appariement." + //$NON-NLS-1$
                  "EdgesMatchesWithTwoSetsOfCompEdges") //$NON-NLS-1$
          + nbOkPlusieursSeries * Appariement.HUNDRED / nbTot
          + "%nb, " + Math.round(longOkPlusieursSeries //$NON-NLS-1$
              * Appariement.HUNDRED / longTot) + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UncertainMatches")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbDouteuxPbSens + I18N //$NON-NLS-1$
              .getString("Appariement.EdgesMatchedOnlyOneWay") //$NON-NLS-1$
          + nbDouteuxPbSens * Appariement.HUNDRED / nbTot
          + "%nb, " //$NON-NLS-1$
          + Math.round(longDouteuxPbSens * Appariement.HUNDRED / longTot)
          + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbDouteuxPbNoeud + I18N //$NON-NLS-1$
              .getString("Appariement.EdgesMatchesWithANode") //$NON-NLS-1$
          + nbDouteuxPbNoeud * Appariement.HUNDRED / nbTot
          + "%nb, " + Math.round(longDouteuxPbNoeud //$NON-NLS-1$
              * Appariement.HUNDRED / longTot) + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedEdges")); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbSansHomologuePbNoeud + I18N //$NON-NLS-1$
              .getString("Appariement." + //$NON-NLS-1$
                  "EdgesWithoutHomologous-" + //$NON-NLS-1$
                  "UnmatchedNode") //$NON-NLS-1$
          + nbSansHomologuePbNoeud * Appariement.HUNDRED / nbTot + "%nb, " + //$NON-NLS-1$
          Math.round(longSansHomologuePbNoeud * Appariement.HUNDRED / longTot)
          + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug("      " + nbSansHomologuePbPCC + I18N //$NON-NLS-1$
              .getString("Appariement." + //$NON-NLS-1$
                  "EdgesWithoutHomologous-" + //$NON-NLS-1$
                  "NoShortestPath") //$NON-NLS-1$
          + nbSansHomologuePbPCC * Appariement.HUNDRED / nbTot
          + "%nb, " //$NON-NLS-1$
          + Math.round(longSansHomologuePbPCC * Appariement.HUNDRED / longTot)
          + "%long)"); //$NON-NLS-1$
    }
    return liensArcsArcs;
  }

  /**
   * Affectation des poids aux arcs pour le calcul de 'plus proche chemin'.
   * @param arcRef reference edge
   * @param tousCandidats candidates
   * @param param matching parameters
   */
  private static void calculePoids(final ArcApp arcRef, final GroupeApp tousCandidats,
      final ParametresApp param) {
    for (Arc arcComp : tousCandidats.getListeArcs()) {
      // poids = arcComp.longueur() + param.coefficentPoidsLongueurDistance*
      // Operateurs.premiere_composante_hausdorff(arcComp.getGeometrie(),arcRef.getGeometrie());
      // ancienne version
      double poids = Distances.ecartSurface(arcComp.getGeometrie(), arcRef.getGeometrie());
      arcComp.setPoids(poids);
      // LOGGER.debug("ecartSurface");
      // LOGGER.debug(arcComp.getGeometrie());
      // LOGGER.debug(arcRef.getGeometrie());
      // LOGGER.debug("Poids = " + poids);
    }
  }
  
  /**
   * Controle de l'enemble des appariements (et non plus un à un) : recherche
   * des arcs ou noeuds du réseau 2 appariés avec plusieurs objets du réseau 1.
   * @param reseau1 network 1
   * @param reseau2 network 2
   * @param liens links
   * @param param matching parameters
   * @return resultatStatAppariement, tableau de stats sur les résultats
   */
  private static void controleGlobal(final CarteTopo reseau1,
      final CarteTopo reseau2, final EnsembleDeLiens liens,
      final ParametresApp param) {
    
    // ///////////////////////////////////////////////////////
    // ////////// Controle global des arcs comp //////////////
    // on recherche les arcs comp appariés avec plusieurs objets ref
    // pour les marquer comme douteux
    int nbSansCorresp = 0, nbDouteux = 0, nbOK = 0;
    double longSansCorresp = 0, longDouteux = 0, longOK = 0;
    
    
    Iterator<?> itArcs = reseau2.getPopArcs().getElements().iterator();
    while (itArcs.hasNext()) {
      ArcApp arcComp = (ArcApp) itArcs.next();
      // On Récupère tous les liens concernés par l'arc
      List<LienReseaux> liensObjet = new ArrayList<LienReseaux>();
      liensObjet.addAll(arcComp.getLiens(liens.getElements()));
      Iterator<?> itGroupes = arcComp.getListeGroupes().iterator();
      while (itGroupes.hasNext()) {
        GroupeApp groupe = (GroupeApp) itGroupes.next();
        liensObjet.addAll(groupe.getLiens(liens.getElements()));
      }
      // cas où l'arc n'est concerné par aucun lien
      if (liensObjet.size() == 0) {
        arcComp.setResultatAppariement(I18N.getString("Appariement.Unmatched")); //$NON-NLS-1$
        nbSansCorresp++;
        longSansCorresp = longSansCorresp + arcComp.longueur();
        continue;
      }
      // cas où l'arc est concerné par un seul lien
      if (liensObjet.size() == 1) {
        if ((liensObjet.get(0)).getEvaluation() == 1) {
          nbOK++;
          longOK = longOK + arcComp.longueur();
        } else {
          nbDouteux++;
          longDouteux = longDouteux + arcComp.longueur();
        }
        continue;
      }
      // cas où l'arc est concerné par plusieurs liens
      if (param.varianteFiltrageImpassesParasites) {
        // on regarde si l'arc est une petite impasse dans le réseau
        // de comparaison apparié. Si oui, on l'enlève des
        // appariements, c'est sûrement un parasite;
        if (arcComp.longueur() < param.distanceNoeudsMax) {
          // est-il petit?
          // est-il une impasse au début?
          Noeud iniComp = arcComp.getNoeudIni();
          boolean impasse = true;
          for (Arc edge : iniComp.arcs()) {
            ArcApp arc = (ArcApp) edge;
            if (arc == arcComp) {
              continue;
            }
            if (arc.aUnCorrespondantGeneralise(liens)) {
              impasse = false;
              break;
            }
          }
          if (impasse) { // on nettoie
            arcComp.setResultatAppariement(I18N
                .getString("Appariement.UnmatchedDeadend")); //$NON-NLS-1$
            itGroupes = arcComp.getListeGroupes().iterator();
            while (itGroupes.hasNext()) {
              GroupeApp groupe = (GroupeApp) itGroupes.next();
              groupe.getListeArcs().remove(arcComp);
              groupe.getListeNoeuds().remove(arcComp.getNoeudIni());
              if (groupe.getListeArcs().isEmpty()) {
                liens.removeAll(groupe.getLiens(liens.getElements()));
              }
            }
            arcComp.getListeGroupes().clear();
            continue;
          }
          // est-il une impasse à la fin?
          Noeud finComp = arcComp.getNoeudFin();
          impasse = true;
          for (Arc edge : finComp.arcs()) {
            ArcApp arc = (ArcApp) edge;
            if (arc == arcComp) {
              continue;
            }
            if (arc.aUnCorrespondantGeneralise(liens)) {
              impasse = false;
              break;
            }
          }
          if (impasse) { // on nettoie
            arcComp.setResultatAppariement(I18N
                .getString("Appariement.UnmatchedDeadend")); //$NON-NLS-1$
            itGroupes = arcComp.getListeGroupes().iterator();
            while (itGroupes.hasNext()) {
              GroupeApp groupe = (GroupeApp) itGroupes.next();
              groupe.getListeArcs().remove(arcComp);
              groupe.getListeNoeuds().remove(arcComp.getNoeudFin());
              if (groupe.getListeArcs().isEmpty()) {
                liens.removeAll(groupe.getLiens(liens.getElements()));
              }
            }
            arcComp.getListeGroupes().clear();
            continue;
          }
        }
      }
      // il faut le marquer comme incertain
      nbDouteux++;
      longDouteux = longDouteux + arcComp.longueur();
      Iterator<?> itLiens = liensObjet.iterator();
      while (itLiens.hasNext()) {
        LienReseaux lien = (LienReseaux) itLiens.next();
        lien.affecteEvaluationAuxObjetsLies(Appariement.ZERO_POINT_FIVE,
            I18N.getString("Appariement." + //$NON-NLS-1$
                "UncertainCompEdgeMatched" + //$NON-NLS-1$
                "WithSeveralRefObjects")); //$NON-NLS-1$
        arcComp.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
            "UncertainMatched" + //$NON-NLS-1$
            "WithSeveralRefObjects")); //$NON-NLS-1$
      }
      continue;
    }
    int nb = nbDouteux + nbOK + nbSansCorresp;
    double longTotal = longDouteux + longOK + longSansCorresp;
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.Network2Edges" //$NON-NLS-1$
          ) + nb + "):"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.CorrectMatchedEdges")//$NON-NLS-1$
          + nbOK + " (" + (nbOK * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " //$NON-NLS-1$
          + Math.round(longOK * Appariement.HUNDRED / longTotal) + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.UncertainMatchedEdges"//$NON-NLS-1$
          )
          + nbDouteux
          + " (" + (nbDouteux * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " //$NON-NLS-1$
          + Math.round(longDouteux * Appariement.HUNDRED / longTotal)
          + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedEdges") //$NON-NLS-1$
          + nbSansCorresp + " (" + (nbSansCorresp * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " //$NON-NLS-1$
          + Math.round(longSansCorresp * Appariement.HUNDRED / longTotal)
          + "%long)"); //$NON-NLS-1$
    }
    
    // ////////// Controle global des noeuds comp //////////////
    // on recherche les noeuds comp appariés avec plusieurs objets ref
    // pour les marquer comme douteux
    nbSansCorresp = 0;
    nbDouteux = 0;
    nbOK = 0;
    Iterator<?> itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
    while (itNoeuds.hasNext()) {
      NoeudApp noeud = (NoeudApp) itNoeuds.next();
      // on Récupère tous les liens concernés par le noeud
      List<LienReseaux> liensObjet = new ArrayList<LienReseaux>();
      liensObjet.addAll(noeud.getLiens());
      Iterator<?> itGroupes = noeud.getListeGroupes().iterator();
      while (itGroupes.hasNext()) {
        GroupeApp groupe = (GroupeApp) itGroupes.next();
        liensObjet.addAll(groupe.getLiens());
      }
      liensObjet.retainAll(liens.getElements());
      // cas où le noeud n'est concerné par aucun lien
      if (liensObjet.size() == 0) {
        noeud.setResultatAppariement(I18N.getString("Appariement.Unmatched")); //$NON-NLS-1$
        nbSansCorresp = nbSansCorresp + 1;
        continue;
      }
      // cas où le noeud est concerné par un seul lien
      if (liensObjet.size() == 1) {
        if ((liensObjet.get(0)).getEvaluation() == 1) {
          nbOK++;
        } else {
          nbDouteux++;
        }
        continue;
      }

      // cas où le noeud est concerné par plusieurs liens
      // il faut le marquer comme incertain si il est concerné par
      // au moins un un noeud ref
      // (et non par des arcRefs, car cela peut être normal dans le
      // cas des doubles voies par exemple).
      Iterator<?> itLiens = liensObjet.iterator();
      boolean ok = true;
      while (itLiens.hasNext()) {
        LienReseaux lien = (LienReseaux) itLiens.next();
        if (lien.getNoeuds1().size() != 0) {
          ok = false;
          break;
        }
      }
      if (ok) {
        nbOK++;
      } else {
        nbDouteux++;
        itLiens = liensObjet.iterator();
        while (itLiens.hasNext()) {
          LienReseaux lien = (LienReseaux) itLiens.next();
          lien.affecteEvaluationAuxObjetsLies(Appariement.ZERO_POINT_FIVE,
              I18N.getString("Appariement." + //$NON-NLS-1$
                  "UncertainCompNode" + //$NON-NLS-1$
                  "Matched" + //$NON-NLS-1$
                  "WithSeveralRefObjects")); //$NON-NLS-1$
          noeud.setResultatAppariement(I18N.getString("Appariement." + //$NON-NLS-1$
              "UncertainMatched" + //$NON-NLS-1$
              "WithSeveralRefObjects")); //$NON-NLS-1$
        }
      }
    }
    nb = nbDouteux + nbOK + nbSansCorresp;
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.Network2Nodes") //$NON-NLS-1$
          + nb + "):"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.CorrectMatchedNodes") //$NON-NLS-1$
          + nbOK + " (" + (nbOK * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.UncertainMatchedNodes")//$NON-NLS-1$
          + nbDouteux + " (" + (nbDouteux //$NON-NLS-1$
              * Appariement.HUNDRED / nb) + "%)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedNodes") //$NON-NLS-1$
          + nbSansCorresp + " (" + (nbSansCorresp * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%)"); //$NON-NLS-1$
    }
    
    // //////////////////////////////////////////////////////
    // ////////// Controle global des arcs ref //////////////
    // On ne fait que compter pour évaluer le résultat
    nbSansCorresp = 0;
    nbDouteux = 0;
    nbOK = 0;
    longSansCorresp = 0;
    longDouteux = 0;
    longOK = 0;
    itArcs = reseau1.getPopArcs().getElements().iterator();
    while (itArcs.hasNext()) {
      ArcApp arc = (ArcApp) itArcs.next();
      if (arc.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Unmatched"))) { //$NON-NLS-1$
        nbSansCorresp++;
        longSansCorresp = longSansCorresp + arc.longueur();
        continue;
      }
      if (arc.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Uncertain"))) { //$NON-NLS-1$
        nbDouteux++;
        longDouteux = longDouteux + arc.longueur();
        continue;
      }
      if (arc.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Matched"))) { //$NON-NLS-1$
        nbOK++;
        longOK = longOK + arc.longueur();
        continue;
      }
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N
            .getString("Appariement.UnexpectedResultOnEdge")//$NON-NLS-1$
            + arc.getResultatAppariement());
      }
    }

    nb = nbDouteux + nbOK + nbSansCorresp;
    longTotal = longDouteux + longOK + longSansCorresp;
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.Network1Edges") //$NON-NLS-1$
          + nb + "):"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.CorrectMatchedEdges") //$NON-NLS-1$
          + nbOK + " (" + (nbOK * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " + //$NON-NLS-1$
          Math.round(longOK * Appariement.HUNDRED / longTotal) + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.UncertainMatchedEdges" //$NON-NLS-1$
          ) + nbDouteux + " (" + (nbDouteux * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " + //$NON-NLS-1$
          Math.round(longDouteux * Appariement.HUNDRED / longTotal) + "%long)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedEdges") //$NON-NLS-1$
          + nbSansCorresp + " (" + (nbSansCorresp * Appariement.HUNDRED / nb) //$NON-NLS-1$
          + "%, " //$NON-NLS-1$
          + Math.round(longSansCorresp * Appariement.HUNDRED / longTotal)
          + "%long)"); //$NON-NLS-1$
    }
    
    // ///////////////////////////////////////////
    // ////////// cas des noeuds ref ////////////
    // On ne fait que compter pour évaluer le résultat
    nbSansCorresp = 0;
    nbDouteux = 0;
    nbOK = 0;
    itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
    while (itNoeuds.hasNext()) {
      NoeudApp noeud = (NoeudApp) itNoeuds.next();
      if (noeud.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Unmatched"))) { //$NON-NLS-1$
        nbSansCorresp++;
        continue;
      }
      if (noeud.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Uncertain"))) { //$NON-NLS-1$
        nbDouteux++;
        continue;
      }
      if (noeud.getResultatAppariement().startsWith(
          I18N.getString("Appariement.Matched"))) { //$NON-NLS-1$
        nbOK++;
        continue;
      }
      if (Appariement.LOGGER.isDebugEnabled()) {
        Appariement.LOGGER.debug(I18N
            .getString("Appariement.UnexpectedResultOnNode") //$NON-NLS-1$
            + noeud.getResultatAppariement());
      }
    }
    nb = nbDouteux + nbOK + nbSansCorresp;
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.Network1Nodes") //$NON-NLS-1$
          + nb + "):"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.CorrectMatchedNodes") //$NON-NLS-1$
          + nbOK + " (" //$NON-NLS-1$
          + (nbOK * Appariement.HUNDRED / nb) + "%)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N
          .getString("Appariement.UncertainMatchedNodes")//$NON-NLS-1$
          + nbDouteux + " (" + (nbDouteux //$NON-NLS-1$
              * Appariement.HUNDRED / nb) + "%)"); //$NON-NLS-1$
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedNodes") //$NON-NLS-1$
          + nbSansCorresp + " (" //$NON-NLS-1$
          + (nbSansCorresp * Appariement.HUNDRED / nb) + "%)"); //$NON-NLS-1$
    }
    
  }

  /**
   * Les noeuds de référence non appariés par les 'liens' sont projetés sur le
   * réseau comp de manière à introduire un noeud dans le réseau Comp aux
   * endroits qui pourraient correspondre à ces noeuds Ref non appariés.
   * @param ref reference network
   * @param comp comparison network
   * @param liens links
   * @param param matching parameters
   */
  public static void decoupeNoeudsNonApparies(final ReseauApp ref,
      final ReseauApp comp, final EnsembleDeLiens liens,
      final ParametresApp param) {
    List<IPoint> noeudsNonApparies = new ArrayList<IPoint>(0);
    for (Noeud node : ref.getPopNoeuds()) {
      NoeudApp noeud = (NoeudApp) node;
      if (noeud.getLiens(liens.getElements()).isEmpty()) {
        noeudsNonApparies.add(noeud.getGeometrie());
      }
    }
    if (Appariement.LOGGER.isDebugEnabled()) {
      Appariement.LOGGER.debug(I18N.getString("Appariement.UnmatchedNodes") + //$NON-NLS-1$
          noeudsNonApparies.size());
    }
    comp.projete(noeudsNonApparies,
        param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc,
        param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud);
  }

  /**
   * Découpe les arcs du reseau1 'reseauADecouper' non appariés par les 'liens'
   * de manière à introduire un noeud dans le reseauADecouper aux endroits où il
   * s'éloigne du réseau2 'reseauDecoupant'.
   * <p>
   * Remarque: utilisé pour les GR par exemple pour traiter le cas des GR hors sentier.
   * @param reseauADecouper
   *        network to split
   * @param reseauDecoupant
   *        splitting network
   * @param liens
   *        links
   * @param param
   *        matching parameters
   */
  public static void decoupeNonApparies(final ReseauApp reseauADecouper,
      final ReseauApp reseauDecoupant, final EnsembleDeLiens liens,
      final ParametresApp param) {
    double distanceMaxNoeudArc = param.projeteNoeuds2SurReseau1DistanceNoeudArc;
    // param.distanceArcsMax;
    List<IPoint> pointsDeDecoupage = new ArrayList<IPoint>();
    // recherche des points de découpage pour chaque arc à découper
    for (Arc edge : reseauADecouper.getPopArcs()) {
      ArcApp arcADecouper = (ArcApp) edge;
      // on ne traite que les arcs non appariés auparavant
      if (!arcADecouper.getLiens(liens.getElements()).isEmpty()) {
        continue;
      }
      // on découpe chaque arc non apparié en fonction des arcs de
      // l'autre réseau assez proches
      // (seuil choisi = diantanceMaxNoeudArc)
      Collection<? extends Arc> arcsDecoupantsRes2 = reseauDecoupant.getPopArcs().select(
          arcADecouper.getGeometrie(), distanceMaxNoeudArc);
      if (arcsDecoupantsRes2.isEmpty()) {
        continue;
      }
      // on traite chaque arc découpant à son tour
      for (Arc cuttingEdge : arcsDecoupantsRes2) {
        ArcApp arcDecoupant = (ArcApp) cuttingEdge;
        // ré échantillonage de la géométrie de l'arc découpant à environ 1m
        ILineString lineStringDecoupant = new GM_LineString(arcDecoupant.getGeometrie().getControlPoint(), false);
        for (IDirectPosition dp : arcADecouper.getGeometrie().coord().getList()) {
          lineStringDecoupant = Operateurs.projectionEtInsertion(dp, lineStringDecoupant);
        }
        // initalisation des compteurs
        int indiceDernierPtProche = 0;
        boolean proche = false;
        if (Distances.distance(lineStringDecoupant.getControlPoint(0),
            arcADecouper.getGeometrie()) <= distanceMaxNoeudArc) {
          proche = true;
        }
        // parcour des points de l'arc découpant
        for (int i = 1; i < lineStringDecoupant.getControlPoint().size(); i++) {
          IDirectPosition ptCourantArcDecoupant = lineStringDecoupant
              .getControlPoint(i);
          double distancePtCourantVersArcADecoupe = Distances.distance(
              ptCourantArcDecoupant, arcADecouper.getGeometrie());
          if (proche) {
            if (distancePtCourantVersArcADecoupe > distanceMaxNoeudArc) {
              // on était proche et on s'éloigne à partir de ce point
              // --> on rajoute deux points de découpage, entourant la partie proche,
              proche = false;
              // ptDecoupage = Operateurs.projection(
              // arcDecoupant.getGeometrie().getControlPoint(
              // indiceDernierPtProche),
              // arcADecouper.getGeometrie());
              // pointsDeDecoupage.add(new GM_Point(ptDecoupage));
              IDirectPosition ptDecoupage = Operateurs.projection(
                  lineStringDecoupant.getControlPoint(i - 1),
                  arcADecouper.getGeometrie());
              pointsDeDecoupage.add(new GM_Point(ptDecoupage));
              continue;
            }
            // on était proche et on le reste: on continue à
            // parcourir l'arc découpant
            continue;
          }
          if (distancePtCourantVersArcADecoupe <= distanceMaxNoeudArc) {
            // on était éloigné et on rentre dans une zone proche:
            // intialisation des compteurs
            proche = true;
            indiceDernierPtProche = i;
            IDirectPosition ptDecoupage = Operateurs.projection(
                lineStringDecoupant.getControlPoint(indiceDernierPtProche),
                arcADecouper.getGeometrie());
            pointsDeDecoupage.add(new GM_Point(ptDecoupage));
            continue;
          }
          // on était éloigné et on le reste:
          // on continue à parcourir l'arc découpant
          continue;
        }
      }
    }
    // découpage effectif du réseau à découper.
    reseauADecouper.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
    reseauADecouper.instancieAttributsNuls(param);
    reseauDecoupant.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
  }
}
