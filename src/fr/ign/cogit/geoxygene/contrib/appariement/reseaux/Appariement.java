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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
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
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Appariement is the abstract class for data matching which carrie the algorithms for networks matching 
 * using topological maps ({@link ReseauApp}). 
 * The method is based on [Devogele 97]. 
 * <b>For data matching of complete sets of data, use {@link AppariementIO}.</b>
 * 
 * <p>
 * La classe Appariement  supporte les methodes d'entrée pour executer l'appariement
 * de réseaux inspiré de la méthode de [Devogele 97].
 * 
 * <p>
 * <b>NB: Cette classe ne porte QUE les méthodes concernant l'appariement de cartes topo ({@link ReseauApp}).
 * Pour un appariement complet de jeux géo (création carte topo, appariement, export),
 * voir la classe {@link AppariementIO}.</b>
 * 
 * @author Mustiere
 * @see AppariementIO
 * @see ReseauApp
 * @see CarteTopo
 */

/////////////////////////////////////////////////////////////////////////////////////////////////
//NOTE AUX CODEURS, dans le code parfois : reseau 1 = reseau ref, reseau 2 = reseau comp.
/////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class Appariement {
	private final static Logger logger=Logger.getLogger(Appariement.class.getName());

	/**
	 * Matching between two networks represented by topological maps ({@link ReseauApp}).
	 * <b>The networks given as arguments are modified during this process: groups are created</b>
	 * 
	 * <p>
	 * Appariement entre deux réseaux représentés par des carte topo.
	 * Processus largement inspiré de celui défini dans la thèse de Thomas Devogèle (1997).
	 * 
	 * <b>Attention : les réseaux passés en entrée sont modifiés durant le traitement : des groupes y sont ajoutés.</b>
	 * 
	 * @param reseau1 first network to be matched 
	 * @param reseau2 second network to be matched 
	 * @param param parameters of the data matching algorithm 
	 * @return 	a set of links between objects from both networks
	 * 
	 * @see ReseauApp
	 * @see CarteTopo
	 * @see EnsembleDeLiens
	 * @see ReseauApp
	 * @see ParametresApp
	 */
	public static EnsembleDeLiens appariementReseaux(ReseauApp reseau1, ReseauApp reseau2, ParametresApp param) {
		EnsembleDeLiens liensPreAppNN ;
		EnsembleDeLiens liensPreAppAA ;
		EnsembleDeLiens liensAppArcs ;
		EnsembleDeLiens liensAppNoeuds ;
		EnsembleDeLiens tousLiens ;

		// build a spatial index (regular tiling with an average of 20 objets per tile) if the network do not have one already
		// Indexation spatiale si cela n'a pas déjà été fait : dallage régulier avec en moyenne 20 objets par case
		if ( !reseau1.getPopArcs().hasSpatialIndex()) {
			int nb = Math.max((int)Math.sqrt(reseau1.getPopArcs().size()/20),1);
			reseau1.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
		}
		if ( !reseau2.getPopNoeuds().hasSpatialIndex()) {
			int nb = Math.max((int)Math.sqrt(reseau2.getPopNoeuds().size()/20),1);
			reseau2.getPopNoeuds().initSpatialIndex(Tiling.class, true, nb);
		}

		///////////// APPARIEMENT
		// Pre-matching using nodes
		// Préappariement de noeuds à noeuds
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
		liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);

		// Pre-matching using edges
		// Préappariement d'arcs à arcs
		if (liensPreAppNN.size() != 0 ) {// nothing to do if there is no node. Il est inutile de pre-appariéer les arcs si rien n'a été trouvé sur les noeuds
			// Preappariement des arcs entre eux (basé principalement sur Hausdorf)
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
		}
		else {
			liensPreAppAA = new EnsembleDeLiens(LienReseaux.class);
			liensPreAppAA.setNom(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgePrematching")); //$NON-NLS-1$
		}

		// Matching each node of reseau1 independantly with nodes of reseau2
		// Appariement de chaque noeud de la BDref / reseau1 (indépendamment les uns des autres)
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodeMatching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
		liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);

		// cleaning-up
		nettoyageLiens(reseau1, reseau2, liensPreAppNN);

		if (param.varianteRedecoupageNoeudsNonApparies) {
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.ReprojectionOfNonMatchedNodes")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			decoupeNoeudsNonApparies(reseau1,reseau2,liensAppNoeuds,param);
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.LinkCleanup")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			nettoyageLiens(reseau1, reseau2);
			liensPreAppNN.setElements(new ArrayList<Lien>());
			liensPreAppAA.setElements(new ArrayList<Lien>());
			liensAppNoeuds.setElements(new ArrayList<Lien>());
			System.gc();
			// Préappariement de noeuds à noeuds
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NumberOfPrematchedNodes")+liensPreAppNN.size()); //$NON-NLS-1$
			// Preappariement des arcs entre eux (basé principalement sur Hausdroff)
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
			// Appariement de chaque noeud de la BDref (indépendamment)
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodeMatching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);
			nettoyageLiens(reseau1, reseau2, liensPreAppNN);
		}

		// Appariement de chaque arc du reseau 1
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgeMatching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
		liensAppArcs = appariementArcs(reseau1, reseau2, liensPreAppAA, liensAppNoeuds, param);
		tousLiens = EnsembleDeLiens.compile(liensAppNoeuds, liensAppArcs);


		if (param.varianteRedecoupageArcsNonApparies) {
			// NB: pas optimal du tout, on recalcule tout après avoir redécoupé.
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.ResplittingNonMatchedEdges")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			decoupeNonApparies(reseau1,reseau2,tousLiens,param);
			nettoyageLiens(reseau1, reseau2);
			liensPreAppNN.setElements(new ArrayList<Lien>());
			liensPreAppAA.setElements(new ArrayList<Lien>());
			liensAppNoeuds.setElements(new ArrayList<Lien>());
			// Préappariement de noeuds à noeuds
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);
			// Preappariement des arcs entre eux (basé principalement sur Hausdroff)
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgePrematching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
			// Appariement de chaque noeud de la BDref (indépendamment)
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodeMatching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);
			nettoyageLiens(reseau1, reseau2, liensPreAppNN);
			// Appariement de chaque arc de la BDref
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgeMatching")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
			liensAppArcs = appariementArcs(reseau1, reseau2, liensPreAppAA, liensAppNoeuds, param);
			tousLiens = EnsembleDeLiens.compile(liensAppNoeuds, liensAppArcs);
		}

		nettoyageLiens(reseau1, reseau2, liensPreAppAA);

		// Evaluation globale
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.GlobalControl")+(new Time(System.currentTimeMillis())).toString()); //$NON-NLS-1$
		controleGlobal(reseau1, reseau2, tousLiens, param);

		//return liens_AppArcs;
		return tousLiens;
	}

	/** Enlève tous les liens "liens" des cartes topo en entrée, et vide les liens "liens" */
	public static void nettoyageLiens(ReseauApp reseau1, ReseauApp reseau2, EnsembleDeLiens liens) {
		nettoyageLiens(reseau1, liens);
		nettoyageLiens(reseau2, liens);
		liens.setElements(new ArrayList<Lien>());
	}

	/** Enlève tous les liens "liens" des cartes topo en entrée */
	private static void nettoyageLiens(ReseauApp res, EnsembleDeLiens liens) {
		for(Arc arc:res.getPopArcs()) ((ArcApp)arc).getLiens().removeAll(liens.getElements());
		for(Noeud node:res.getPopNoeuds()) ((NoeudApp)node).getLiens().removeAll(liens.getElements());
		for(Groupe group:res.getPopGroupes()) ((GroupeApp)group).getLiens().removeAll(liens.getElements());
	}

	/** Enlève tous les liens des cartes topo en entrée, et détruit les groupes */
	public static void nettoyageLiens(ReseauApp reseau1, ReseauApp reseau2) {
		nettoyageLiens(reseau1);
		nettoyageLiens(reseau2);
	}

	/** Enlève tous les liens de la carte topo en entrée, et détruit les groupes */
	public static void nettoyageLiens(ReseauApp res) {
		for(Arc arc:res.getPopArcs()) ((ArcApp)arc).setLiens(new ArrayList<LienReseaux>());
		for(Noeud node:res.getPopNoeuds()) ((NoeudApp)node).setLiens(new ArrayList<LienReseaux>());
		res.getPopGroupes().setElements(new ArrayList<Groupe>());
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Préappariement entre noeuds uniquement sur un critère de distance euclidienne,
	 *  comme proposé dans [Devogèle 97].
	 *
	 *  On crée des liens 1-n (n candidats noeuds de BDcomp pour chaque noeud de BDref).
	 * 
	 *  Comme suggéré dans [Devogèle 97], la taille de la recherche peut varier selon
	 *  le type du noeud de la BD de reférence (rond point, changement d'attribut...).
	 *  NB1: On préfère largement une taille de recherche sur-évaluée que sous-évaluée.
	 *  NB2: On ne traite pas les noeuds isolés.
	 * @param param 
	 */
	public static EnsembleDeLiens preAppariementNoeudNoeud(CarteTopo reseau1, CarteTopo reseau2, ParametresApp param) {
		int nbCandidats = 0, nbRef=0;
		NoeudApp noeudRef, noeudComp;
		List<Noeud> candidats;
		EnsembleDeLiens liens;
		Iterator<Noeud> itCandidats;
		LienReseaux lien;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodePrematching")); //$NON-NLS-1$
		Iterator<Noeud> itNoeuds = reseau1.getListeNoeuds().iterator();
		while (itNoeuds.hasNext()) {
			noeudRef = (NoeudApp)itNoeuds.next();
			// On ne tient pas compte des noeuds isolés
			if ( noeudRef.getEntrants().size() == 0 && noeudRef.getSortants().size() == 0 ) continue;
			nbRef++;
			// Détermination des noeuds comp dans le rayon de recherche
			candidats = reseau2.getPopNoeuds().select(noeudRef.getGeometrie(), noeudRef.getTaille()).getElements();
			if (candidats.size() != 0) {
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				nbCandidats = nbCandidats + candidats.size();
				itCandidats = candidats.iterator();
				while(itCandidats.hasNext()) {
					noeudComp = (NoeudApp)itCandidats.next();
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
			}
		}
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Assessment")+nbCandidats+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CandidateCompNodes")+nbRef+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.RefNodesToProcess")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return liens;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Préappariement entre arcs basé sur la "demi-distance de Hausdorff"
	 * (on ne prend en compte que la composante de réseau 2 vers réseau 1).
	 * 
	 * Pour chaque arc du reseau 2, on garde les arcs du reseau 1 qui sont à la fois
	 * 1/ à moins distanceMax de l'arc comp
	 * 2/ à moins de D + distanceMin de l'arc comp,
	 * 		D étant la distance entre l'arc ref le plus proche de arc comp
	 * 
	 * NB: ce pré-appariement est différent de ce qui est proposé dans [Devogèle 97],
	 * pour minimiser la sensibilité aux seuils.
	 * 
	 * On crée des liens 1-n: (1 arc de comp) - (n arcs de ref).
	 * Un arc de ref peut être alors concerné par plusieurs liens différents.
	 * Au total on a donc des relations n-m codées sous la forme de n relations 1-m
	 */
	public static EnsembleDeLiens preAppariementArcArc(CarteTopo reseau1, CarteTopo reseau2, ParametresApp param) {
		int nbCandidats = 0;
		ArcApp arcComp, arcRef;
		List<Arc> arcsProches, candidats;
		List<Double> distances = new ArrayList<Double>();
		LienReseaux lien;
		EnsembleDeLiens liens;
		Iterator<Arc> itArcs, itArcsProches, itCandidats;
		double dmin, dmax, d;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom("Préappariement des arcs"); //$NON-NLS-1$
		itArcs = reseau2.getListeArcs().iterator();
		while (itArcs.hasNext()) {
			arcComp = (ArcApp)itArcs.next();
			// On recherche les arcs dans l'entourage proche, grosso modo
			arcsProches = reseau1.getPopArcs().select(arcComp.getGeometrie(), param.distanceArcsMax).getElements();
			if (arcsProches.size() == 0) continue;
			// On calcule leur distance à arccomp et on recherche le plus proche
			distances.clear();
			itArcsProches = arcsProches.iterator();
			arcRef = (ArcApp) itArcsProches.next();
			dmin = arcComp.premiereComposanteHausdorff(arcRef,param.distanceArcsMax);
			distances.add(new Double(dmin));
			while (itArcsProches.hasNext()) {
				arcRef = (ArcApp) itArcsProches.next();
				d = arcComp.premiereComposanteHausdorff(arcRef,param.distanceArcsMax);
				distances.add(new Double(d));
				if (d<dmin) dmin=d;
			}
			// On garde tous ceux assez proches
			dmax = Math.min(dmin+param.distanceArcsMin, param.distanceArcsMax);
			candidats = new ArrayList<Arc>();
			for (int i=0;i<arcsProches.size();i++) {
				d = distances.get(i).doubleValue();
				if ( d<dmax ) candidats.add(arcsProches.get(i));
			}

			// Si pas de candidat pour l'arccomp, on s'arrête là
			if (candidats.size()==0) continue;
			// Si il y a des candidats: on construit le lien de pré-appariement
			lien = (LienReseaux)liens.nouvelElement();
			arcComp.addLiens(lien);
			lien.addArcs2(arcComp);
			nbCandidats = nbCandidats + candidats.size();
			itCandidats = candidats.iterator();
			while (itCandidats.hasNext()) {
				arcRef = (ArcApp) itCandidats.next();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
			}
		}
		if (logger.isDebugEnabled()) logger.debug("  Bilan : "+nbCandidats+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CandidateCompEdges")+reseau1.getListeArcs().size()+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.RefEdgesToProcess")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return liens;
	}


	/** Appariement des Noeuds du reseau 1 avec les arcs et noeuds du reseau 1,
	 *  comme proposé dans [Devogèle 97] + modif au filtrage Seb
	 *  On crée les liens qui vont bien si le noeud est apparié.
	 *  Une évaluation de l'appariement est stockée sur les liens (note de 0, 0.5 ou 1)
	 *  Une explication plus détaillée du résultat est stockée sur les noeuds ref et comp.
	 */
	@SuppressWarnings("unchecked")
	public static EnsembleDeLiens appariementNoeuds(
			CarteTopo reseau1, CarteTopo reseau2,
			EnsembleDeLiens liensPreAppNN, EnsembleDeLiens liensPreAppAA,
			ParametresApp param) {
		int j, k, l;
		Population groupesComp = reseau2.getPopGroupes();
		List<ElementCarteTopo> complets, complets2, incomplets, incomplets2,
		noeudsIncomplets;
		List<Groupe> groupesConnexes;
		List<Arc> entrants;
		List<LienReseaux> liensDuNoeudRef;
		List<Noeud> noeudsCompCandidats;
		NoeudApp noeudRef, noeudComp, noeudDeb;
		GroupeApp groupeComp;
		int nbSansHomologue = 0, nbNonTraite = 0, nbPlusieursNoeudsComplets = 0,
		nbPlusieursGroupesComplets = 0, nbNoeudNoeud = 0, nbNoeudGroupe = 0,
		nbNoeudGroupeIncertain = 0, nbNoeudNoeudIncertain = 0;
		int correspondance;
		Iterator itNoeuds;
		LienReseaux lien;
		EnsembleDeLiens liens;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodeMatching")); //$NON-NLS-1$
		// On initialise le resultat à "non apparié" pour les noeuds comp
		itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeudComp = (NoeudApp)itNoeuds.next();
			noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched")); //$NON-NLS-1$
		}

		// On traite chaque noeud ref, un par un
		itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeudRef = (NoeudApp)itNoeuds.next();
			noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NA")); // pour détecter les cas non traités //$NON-NLS-1$
			// On ne traite pas les noeuds isolés
			if ( noeudRef.arcs().size() == 0 ) {
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.IsolatedNode")); //$NON-NLS-1$
				nbNonTraite ++;
				continue;
			}

			liensDuNoeudRef = new ArrayList(noeudRef.getLiens(liensPreAppNN.getElements()));

			//////////////////////////////////////////////////////////////////
			// Noeud ref qui n'a aucun noeud comp candidat dans le pré-appariement
			if (liensDuNoeudRef.size() == 0) {
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NoCandidateForMatching")); //$NON-NLS-1$
				nbSansHomologue++;
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Noeud ref qui a un ou plusieurs candidats (réunis dans un seul lien par construction)
			//
			// On qualifie des noeuds comp candidats en comparant les entrants/sortants
			// Un noeud comp est dit, par rapport au noeud ref, :
			//   - complet si on trouve une correspondance entre tous les incidents des noeuds ref et comp,
			//   - incomplet si on trouve une correspondance entre certains incidents
			//   - impossible si on ne trouve aucune correspondance
			noeudsCompCandidats = (liensDuNoeudRef.get(0)).getNoeuds2();
			complets = new ArrayList<ElementCarteTopo>();
			incomplets = new ArrayList<ElementCarteTopo>();
			Iterator<Noeud> itNoeudsComp = noeudsCompCandidats.iterator();
			while (itNoeudsComp.hasNext()) {
				noeudComp = (NoeudApp)itNoeudsComp.next();
				correspondance = noeudRef.correspCommunicants(noeudComp, liensPreAppAA);
				if (  correspondance == 1 ) complets.add(noeudComp);
				if (  correspondance == 0 ) incomplets.add(noeudComp);
			}

			////////////////////////////////////////////////////////////////////
			// Cas d'un appariement simple : 1 noeud BDref correspond à un noeud BDComp
			// C'est un appariement que l'on juge sûr.
			if ( complets.size() == 1 ) {
				noeudComp = (NoeudApp)complets.get(0);
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
				noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
				nbNoeudNoeud ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(1);
				continue;
			}

			/////////////////////////////////////////////////////////////////////////
			// Cas où plusieurs noeuds comp candidats sont complets.
			// Ce cas ne devrait en théorie pas arriver d'après Thomas,
			// mais si en pratique les seuils sont larges, c'est un cas courant
			//
			// Ajout Seb pour filtrer ce sur-appariement : on choisit le plus proche (devrait être affiné)
			if ( complets.size() > 1 ) {
				filtrageNoeudsComplets(complets, noeudRef);
				if ( complets.size() == 1 ) {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainChoiceBetweenSeveralNodes")); //$NON-NLS-1$
					nbPlusieursNoeudsComplets++;
					lien = (LienReseaux)liens.nouvelElement();
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					noeudComp = (NoeudApp)complets.get(0);
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainChoiceBetweenSeveralNodes")); //$NON-NLS-1$
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				// Si il reste plusieurs noeuds complets après le filtrage
				// NB : cas impossible dans l'état actuel du filtrage qui ne garde qu'un noeud complet
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithSeveralNodes")); //$NON-NLS-1$
				nbPlusieursNoeudsComplets++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				for (j=0; j<complets.size(); j++) {
					noeudComp = (NoeudApp)complets.get(j);
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainCompetingNodes")); //$NON-NLS-1$
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
				lien.setEvaluation(0);
				continue;
			}

			///////////////////////////////////////////////////////////////////////////
			// Cas où il n'y a pas de noeud comp complet, mais un seul incomplet
			// Appariement simple : 1 noeud ref correspond à un noeud comp,
			// mais c'est un appariement que l'on juge incertain
			if ( incomplets.size() == 1 ) {
				noeudComp = (NoeudApp)incomplets.get(0);
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithIncompleteNode")); //$NON-NLS-1$
				noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainIncompleteNode")); //$NON-NLS-1$
				nbNoeudNoeudIncertain ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(0.5);
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Le noeud ref n'a que des noeuds comp candidats impossibles
			// i.e. ils n'ont aucun arc sortant apparié avec un des arcs sortant du noeudref
			if ( incomplets.size() == 0 ) {
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNoPossibleCandidate")); //$NON-NLS-1$
				nbSansHomologue++;
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Si il y a plusieurs noeuds incomplets,
			// MAIS on autorise uniquement les appariement 1-1 et 1-0 aux noeuds
			// On choisit alors le noeud incomplet le plus proche
			if (param.varianteForceAppariementSimple) {
				noeudComp = noeudLePlusProche(incomplets, noeudRef);
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithIncompleteNode")); //$NON-NLS-1$
				noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainIncompleteNode")); //$NON-NLS-1$
				nbNoeudNoeudIncertain ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(0.5);
				continue;
			}

			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			// DEBUT TRAITEMENT DES CARREFOURS COMPLEXES :
			// (quand un noeud ref correpsond à un ensemble de noeuds et arcs dans BDcomp)
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////

			// On crée un nouveau groupe contenant tous les noeuds incomplets ainsi que
			// tous les arcs ayant aux 2 extrémités un de ces noeuds
			groupeComp = (GroupeApp)groupesComp.nouvelElement();
			noeudsIncomplets = new ArrayList<ElementCarteTopo>(incomplets); //ajout pour mieux gérer le cas des groupes impossibles
			for (j=0;j<incomplets.size();j++) {
				noeudComp = (NoeudApp)incomplets.get(j);
				groupeComp.addNoeud(noeudComp);
				//noeudcomp.addGroupe(groupecomp);
				entrants = noeudComp.getEntrants();
				for (k=0;k<entrants.size();k++) {
					noeudDeb = (NoeudApp)((ArcApp)entrants.get(k)).getNoeudIni();
					for (l=0;l<incomplets.size();l++) {
						if ( noeudDeb == incomplets.get(l) ) {
							groupeComp.addArc(entrants.get(k));
							//((Arc_App)entrants.get(k)).addGroupe(groupecomp);
							break;
						}
					}
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// decomposition du groupe créé en groupes connexes
			groupesConnexes = groupeComp.decomposeConnexes();

			///////////////////////////////////////////////////////////////////////////////
			// Choix du groupe connexe à conserver :
			// Qualification des groupes candidats en comparant les entrants/sortants
			// Un groupe comp est dit, par rapport au noeud ref, :
			//   - complet si on trouve une correspondance entre tous les incidents des noeuds ref et comp,
			//   - incomplet si on trouve une correspondance entre certains incidents
			//   - impossible si on ne trouve aucune correspondance
			// NB: methode strictement similaire à ce qui était réalisé sur les noeuds
			complets = new ArrayList<ElementCarteTopo>();
			incomplets = new ArrayList<ElementCarteTopo>();

			for (j=0;j<groupesConnexes.size();j++) {
				groupeComp = (GroupeApp)groupesConnexes.get(j);
				correspondance = noeudRef.correspCommunicants(groupeComp, liensPreAppAA);
				if (  correspondance == 1 ) complets.add(groupeComp);
				if (  correspondance == 0 ) incomplets.add(groupeComp);
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouvé un unique groupe complet
			// C'est un appariement que l'on juge sûr
			if ( complets.size() == 1 ) {
				// vidage des groupes rejetés, pour faire propre
				for(j=0; j<incomplets.size();j++) {
					((GroupeApp)incomplets.get(j)).vide();
				}
				groupeComp = (GroupeApp)complets.get(0);
				// filtrage du groupe connexe choisi
				groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
				if ( groupeComp.getListeNoeuds().size() == 0 ) {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedUniqueCompleteGroup")); //$NON-NLS-1$
					nbSansHomologue = nbSansHomologue+1;
					continue;
				}
				if ( (groupeComp.getListeNoeuds().size() == 1) && (groupeComp.getListeArcs().size() == 0) ) {
					noeudComp = (NoeudApp)groupeComp.getListeNoeuds().get(0);
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
					groupeComp.vide();
					nbNoeudNoeud++;
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				else {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAGroup")); //$NON-NLS-1$
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
					nbNoeudGroupe++;
					for(j=0;j<groupeComp.getListeArcs().size() ;j++) {
						((ArcApp)groupeComp.getListeArcs().get(j)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANodeInAGroup")); //$NON-NLS-1$
					}
					for(j=0;j<groupeComp.getListeNoeuds().size() ;j++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(j)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANodeInAGroup")); //$NON-NLS-1$
					}
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					lien.setEvaluation(1);
					continue;
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// plusieurs groupes sont bien appariés (complets) avec un noeud ref
			// On est sur qu'il y a sur-appariement (à raffiner dans le futur si possible)
			// Cas qui ne devrait pas arriver d'après Thomas (si les seuils sont bien choisis !)
			if ( complets.size() > 1 ) {
				// vidage des groupes rejetés, pour faire propre
				for(j=0; j<incomplets.size();j++) {
					((GroupeApp)incomplets.get(j)).vide();
				}

				complets2 = new ArrayList<ElementCarteTopo>();
				for (j=0; j<complets.size(); j++) {
					groupeComp = (GroupeApp)complets.get(j);
					groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
					if ( groupeComp.getListeNoeuds().size() != 0 ) complets2.add(groupeComp);
				}
				complets = new ArrayList<ElementCarteTopo>(complets2);

				// aucun goupe complet restant après les filtrages
				if ( complets.size() == 0 ) {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEmptiedCompleteGroups")); //$NON-NLS-1$
					nbSansHomologue++;
					continue;
				}

				// un seul goupe complet restant après les filtrages
				if ( complets.size() == 1 ) {
					lien = new LienReseaux();
					liens.add(lien);
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithCompleteGroup")); //$NON-NLS-1$
					nbNoeudGroupeIncertain++;
					groupeComp = (GroupeApp)complets.get(0);
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainOnlyCompleteGroup")); //$NON-NLS-1$
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInTheOnlyCompleteGroup")); //$NON-NLS-1$
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInTheOnlyCompleteGroup")); //$NON-NLS-1$
					}
					lien.setEvaluation(0.5);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					continue;
				}

				// plusieurs goupes complets restant après les filtrages
				lien = new LienReseaux();
				liens.add(lien);
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithSeveralCompleteGroups")); //$NON-NLS-1$
				nbPlusieursGroupesComplets++;
				for (j=0; j<complets.size(); j++) {
					groupeComp = (GroupeApp)complets.get(j);
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainOneOfSeveralCompetingCompleteGroups")); //$NON-NLS-1$
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInOneOfSeveralCompetingCompleteGroups")); //$NON-NLS-1$
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInOneOfSeveralCompetingCompleteGroups")); //$NON-NLS-1$
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
				}
				lien.setEvaluation(0);
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				continue;
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouvé un unique groupe incomplet
			// C'est un appariement que l'on accepte mais qu'on l'on juge incertain
			if ( incomplets.size() == 1 ) {
				groupeComp = (GroupeApp)incomplets.get(0);
				groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
				if ( groupeComp.getListeNoeuds().size() == 0 ) {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedOnlyEmptiedIncompleteGroup")); //$NON-NLS-1$
					nbSansHomologue++;
					continue;
				}
				if ( (groupeComp.getListeNoeuds().size() == 1) && (groupeComp.getListeArcs().size() == 0) ) {
					noeudComp = (NoeudApp)groupeComp.getListeNoeuds().get(0);
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode")); //$NON-NLS-1$
					groupeComp.vide();
					nbNoeudNoeudIncertain++;
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				else {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnIncompleteGroup")); //$NON-NLS-1$
					nbNoeudGroupeIncertain = nbNoeudGroupeIncertain + 1;
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainIncompleteGroup")); //$NON-NLS-1$
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInAnIncompleteGroup")); //$NON-NLS-1$
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInAnIncompleteGroup")); //$NON-NLS-1$
					}
					//param.db.create(groupeComp);
					lien = new LienReseaux();
					liens.add(lien);
					lien.setEvaluation(0.5);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					continue;
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouvé plusieurs groupes incomplets
			// C'est un appariement que l'on accepte peut-être mais que l'on sait incohérent
			if ( incomplets.size() > 1 ) {
				incomplets2 = new ArrayList<ElementCarteTopo>();
				for (j=0; j<incomplets.size(); j++) {
					groupeComp = (GroupeApp)incomplets.get(j);
					groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
					if ( groupeComp.getListeNoeuds().size() != 0 ) incomplets2.add(groupeComp);
				}
				incomplets = new ArrayList<ElementCarteTopo>(incomplets2);

				// aucun goupe incomplet restant après le filtrage
				if ( incomplets.size() == 0 ) {
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEmptiedIncompleteGroups")); //$NON-NLS-1$
					nbSansHomologue = nbSansHomologue+1;
					continue;
				}

				// un seul goupe incomplet restant après le filtrage
				if ( incomplets.size() == 1 ) {
					lien = new LienReseaux();
					liens.add(lien);
					groupeComp = (GroupeApp)incomplets.get(0);
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainIncompleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInIncompleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInIncompleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnIncompleteGroupEmptiedIncompleteGroups")); //$NON-NLS-1$
					nbNoeudGroupeIncertain++;
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}

				// plusieurs goupes incomplets restant après les filtrages
				lien = new LienReseaux();
				liens.add(lien);
				for (j=0; j<incomplets.size(); j++) {
					groupeComp = (GroupeApp)incomplets.get(j);
					groupeComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainAmongSeveralIncompleteGroups")); //$NON-NLS-1$
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInOneOfSeveralIncompleteGroups")); //$NON-NLS-1$
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainInOneOfSeveralIncompleteGroups")); //$NON-NLS-1$
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
				}
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithSeveralIncompleteGroups")); //$NON-NLS-1$
				nbPlusieursGroupesComplets++;
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.setEvaluation(0);
				continue;
			}

			if ( incomplets.size() == 0 ) { // ajout Seb 31/05/05
				noeudRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainImpossibleCandidateGroups")); //$NON-NLS-1$
				nbNoeudNoeudIncertain++;
				lien = new LienReseaux();
				liens.add(lien);
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.setEvaluation(0.5);
				for (j=0; j<noeudsIncomplets.size(); j++) {
					noeudComp = (NoeudApp)noeudsIncomplets.get(j);
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainImpossibleCandidateGroups")); //$NON-NLS-1$
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
				continue;
			}
		}

		// pour le réseau routier uniquement: on traite le cas des rond-points
		// si un noeud est apparié avec une partie de rond point,
		// il devient apparié avec l'ensemble du rond point
		if ( param.varianteChercheRondsPoints) rondsPoints(reseau1, reseau2, liens, liensPreAppAA, param);


		// Fin, affichage du bilan
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodeAssessment")); //$NON-NLS-1$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatches")); //$NON-NLS-1$
			logger.debug("      "+nbNoeudNoeud+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesMatchedWithASingleNode")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("      "+nbNoeudGroupe+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesMatchedWithASingleGroup")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatches")); //$NON-NLS-1$
			logger.debug("      "+nbNoeudNoeudIncertain+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesMatchedWithASingleIncompleteNode")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("      "+nbPlusieursNoeudsComplets+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesMatchedWithASeveralNodes")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("      "+nbNoeudGroupeIncertain+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesMatchedWithASingleIncompleteGroup")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.IncoherentMatches")); //$NON-NLS-1$
			logger.debug("      "+nbSansHomologue+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesWithoutMatchFound")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("      "+nbPlusieursGroupesComplets+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NodesWithSeveralHomologousGroups")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnprocessedNodes")); //$NON-NLS-1$
			logger.debug("      "+nbNonTraite+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.IsolatedNodes")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return liens;
	}

	/**
	 * pour le réseau routier uniquement: on traite le cas des rond-points
	 * si un noeud est apparié avec une partie de rond point,
	 * il devient apparié avec l'ensemble du rond point
	 * @param reseau1
	 * @param reseau2
	 * @param liens_Noeuds
	 * @param liensPreAppAA
	 * @param param
	 */
	private static void rondsPoints(CarteTopo reseau1, CarteTopo reseau2, EnsembleDeLiens liens_Noeuds, EnsembleDeLiens liensPreAppAA, ParametresApp param) {
		double compacite;
		Iterator<?> itGroupesDuNoeud, itNoeudsDuRondPoint, itNoeudsDuRondPoint2, itArcsDuRondPoint, itLiensDuNoeudsDuRondPoint;
		List<Noeud> noeudsDuRondPoint;
		List <LienReseaux> liensDuNoeudDuRondPoint;
		LienReseaux lien;
		NoeudApp noeud, noeud2, noeudRef;
		GroupeApp rondPoint;
		ArcApp arc2;
		Iterator<?> itFaces = reseau2.getPopFaces().getElements().iterator();

		while (itFaces.hasNext()) {
			Face face = (Face) itFaces.next();
			compacite = IndicesForme.indiceCompacite(face.getGeometrie());
			if ( compacite < 0.95) continue;
			if (face.getGeometrie().area() > 8000 ) continue; //rond point de diametre>100m
			noeudsDuRondPoint = face.noeuds();
			itNoeudsDuRondPoint = noeudsDuRondPoint.iterator();
			while (itNoeudsDuRondPoint.hasNext()) {
				noeud = (NoeudApp) itNoeudsDuRondPoint.next();
				liensDuNoeudDuRondPoint = noeud.getLiens(liens_Noeuds.getElements());
				itLiensDuNoeudsDuRondPoint = liensDuNoeudDuRondPoint.iterator();
				// Pour un lien d'un noeud du rond point, 2 cas possibles:
				// 1/ avant ce lien ne pointait que vers ce noeud
				while (itLiensDuNoeudsDuRondPoint.hasNext()) {
					lien = (LienReseaux)itLiensDuNoeudsDuRondPoint.next();
					noeudRef = (NoeudApp)lien.getNoeuds1().get(0);
					if ( lien.getNoeuds2().size() == 1 ) { // cas 1
						rondPoint = (GroupeApp)reseau2.getPopGroupes().nouvelElement();
						rondPoint.addAllNoeuds(new ArrayList<Noeud>(noeudsDuRondPoint));
						rondPoint.addAllArcs(new ArrayList<Arc>(face.arcs()));
						lien.getNoeuds2().clear();
						noeud.getLiens().remove(lien);
						lien.addGroupes2(rondPoint);
						rondPoint.addLiens(lien);
						if (noeudRef.correspCommunicants(rondPoint, liensPreAppAA)==1) {
							lien.setEvaluation(1);
							rondPoint.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-Roundabout")); //$NON-NLS-1$
							for(int k=0;k<rondPoint.getListeArcs().size() ;k++) {
								((ArcApp)rondPoint.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-InRoundabout")); //$NON-NLS-1$
							}
							for(int k=0;k<rondPoint.getListeNoeuds().size() ;k++) {
								((NoeudApp)rondPoint.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-InRoundabout")); //$NON-NLS-1$
							}
						}
						else {
							rondPoint.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-IncompleteRoundabout")); //$NON-NLS-1$
							for(int k=0;k<rondPoint.getListeArcs().size() ;k++) {
								((ArcApp)rondPoint.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-InIncompleteRoundabout")); //$NON-NLS-1$
							}
							for(int k=0;k<rondPoint.getListeNoeuds().size() ;k++) {
								((NoeudApp)rondPoint.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-InIncompleteRoundabout")); //$NON-NLS-1$
							}
						}
						continue;
					}
				}
				// 2/ avant ce lien pointait vers une partie du rond point
				itGroupesDuNoeud = noeud.getListeGroupes().iterator();
				while (itGroupesDuNoeud.hasNext()) {
					GroupeApp groupe = (GroupeApp) itGroupesDuNoeud.next();
					List<?> liens = groupe.getLiens(liens_Noeuds.getElements());
					if (liens.size() == 0) continue;
					if ( groupe.getLiens().size() == 0 ) continue;
					if ( liens_Noeuds.getElements().contains(groupe.getLiens().get(0)) ) {
						itNoeudsDuRondPoint2 = noeudsDuRondPoint.iterator();
						while (itNoeudsDuRondPoint2.hasNext()) {
							noeud2 = (NoeudApp)itNoeudsDuRondPoint2.next();
							if ( !groupe.getListeNoeuds().contains(noeud2) ) groupe.addNoeud(noeud2);
						}
						itArcsDuRondPoint = face.arcs().iterator();
						while (itArcsDuRondPoint.hasNext()) {
							arc2 = (ArcApp)itArcsDuRondPoint.next();
							if ( !groupe.getListeArcs().contains(arc2) ) groupe.addArc(arc2);
						}
					}
					lien = (LienReseaux)liens.get(0);
					noeudRef = (NoeudApp)lien.getNoeuds1().get(0);
					if (noeudRef.correspCommunicants(groupe, liensPreAppAA)==1) {
						lien.setEvaluation(1);
						groupe.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-Roundabout")); //$NON-NLS-1$
						for(int k=0;k<groupe.getListeArcs().size() ;k++) {
							((ArcApp)groupe.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-InRoundabout")); //$NON-NLS-1$
						}
						for(int k=0;k<groupe.getListeNoeuds().size() ;k++) {
							((NoeudApp)groupe.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithANode-InRoundabout")); //$NON-NLS-1$
						}
					}
					else {
						groupe.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-InIncompleteRoundabout")); //$NON-NLS-1$
						for(int k=0;k<groupe.getListeArcs().size() ;k++) {
							((ArcApp)groupe.getListeArcs().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-InIncompleteRoundabout")); //$NON-NLS-1$
						}
						for(int k=0;k<groupe.getListeNoeuds().size() ;k++) {
							((NoeudApp)groupe.getListeNoeuds().get(k)).setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithANode-InIncompleteRoundabout")); //$NON-NLS-1$
						}
					}

				}
			}
		}
	}

	/** on ne garde que le noeud comp le plus proche */
	private static void filtrageNoeudsComplets(List<ElementCarteTopo> complets, NoeudApp noeudRef) {
		int i;
		NoeudApp noeudComp, noeudPlusProche;
		double d, dmin;

		if ( complets.size() <= 1 ) return;
		noeudPlusProche = (NoeudApp)complets.get(0);
		dmin = noeudPlusProche.distance(noeudRef);
		for(i=1;i<complets.size() ;i++) {
			noeudComp = (NoeudApp)complets.get(i);
			d = noeudComp.distance(noeudRef);
			if ( d < dmin) {
				dmin = d;
				noeudPlusProche = noeudComp;
			}
		}
		complets.clear();
		complets.add(noeudPlusProche);
	}

	/** renvoie le noeud de 'noeuds' le plus proche de 'noeudRef' */
	private static NoeudApp noeudLePlusProche(List<ElementCarteTopo> noeuds, NoeudApp noeudRef) {
		if ( noeuds.size() < 1 ) return null;
		Iterator<ElementCarteTopo> itNoeuds = noeuds.iterator();
		NoeudApp plusProche = (NoeudApp) itNoeuds.next();
		double dmin = plusProche.distance(noeudRef);
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			double d = noeud.distance(noeudRef);
			if ( d < dmin) {
				dmin = d;
				plusProche = noeud;
			}
		}
		return plusProche;
	}

	/** Appariement des arcs, s'appuyant sur un appariement préalable des noeuds,
	 *  et sur un pré-appariement des arcs.
	 *  S'appuie essentiellement sur la notion de 'plus proche chemin d'un arc', défini
	 *  comme le chemin minimisant la surface entre le chemin et l'arc.
	 */
	public static EnsembleDeLiens appariementArcs(CarteTopo reseau1,
			CarteTopo reseau2,
			EnsembleDeLiens liensPreAppAA,
			EnsembleDeLiens liensAppNoeuds,
			ParametresApp param) {

		GroupeApp tousCandidats, pccMin1, pccMin2;
		ArcApp arcRef, arcComp;
		NoeudApp noeudComp;
		List<Arc> tousArcs = new ArrayList<Arc>();
		List<Noeud> noeudsDebutIn, noeudsDebutOut, noeudsFinIn,noeudsFinOut;
		double longMaxRecherche ;
		int nbSansHomologuePbNoeud = 0, nbSansHomologuePbPCC = 0,
		nbOkUneSerie = 0, nbOkPlusieursSeries = 0,
		nbDouteuxPbNoeud = 0, nbDouteuxPbSens = 0, nbTot;
		double longSansHomologuePbNoeud = 0, longSansHomologuePbPCC = 0,
		longOkUneSerie = 0, longOkPlusieursSeries = 0,
		longDouteuxPbNoeud = 0, longDouteuxPbSens = 0, longTot;
		LienReseaux lien;
		EnsembleDeLiens liensArcsArcs = new EnsembleDeLiens(LienReseaux.class);
		Iterator<?> itArcs;
		Iterator<Arc> itTousArcsComp;

		liensArcsArcs.setNom(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgeMatching")); //$NON-NLS-1$

		// on étudie tous les arc ref, un par un, indépendamment les uns des autres
		itArcs = reseau1.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arcRef = (ArcApp)itArcs.next();
			arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.NA")); // pour vérifier que tous les cas sont bien traités //$NON-NLS-1$

			///////// ETUDE DES EXTREMITES DE L'ARC /////////

			// Problème de topologie ?
			if ( (arcRef.getNoeudIni() == null) || (arcRef.getNoeudFin() == null) ) {
				// Cas 1 : l'arc n'a pas de noeud à une de ses extrêmités
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEdgeMissingANode")); //$NON-NLS-1$
				nbSansHomologuePbNoeud++;
				longSansHomologuePbNoeud = longSansHomologuePbNoeud+arcRef.longueur();
				continue;
			}

			// On ne sait pas traiter les boucles (CODE A AFFINER POUR FAIRE CELA)
			if ( (arcRef.getNoeudIni() == arcRef.getNoeudFin() ) ) {
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedLoop")); //$NON-NLS-1$
				nbSansHomologuePbPCC++;
				longSansHomologuePbPCC = longSansHomologuePbPCC+arcRef.longueur();
				continue;
			}


			// Recherche des noeuds en correspondance avec les extrémités de l'arc,
			// que ce soit en entree ou en sortie pour l'arc (au sens de la circulation)
			List<List<Noeud>> noeudsInOut = arcRef.noeudsEnCorrespondanceAuxExtremites(liensAppNoeuds, liensPreAppAA);
			noeudsDebutIn = noeudsInOut.get(0);
			noeudsDebutOut = noeudsInOut.get(1);
			noeudsFinIn = noeudsInOut.get(2);
			noeudsFinOut = noeudsInOut.get(3);

			if ( ( (noeudsFinIn.size() == 0) && (noeudsFinOut.size() == 0))
					|| ( (noeudsDebutIn.size() == 0) && (noeudsDebutOut.size() == 0)) ) {
				// Cas 2 : un noeud extrémité n'est pas apparié
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNodeUnmatched")); //$NON-NLS-1$
				nbSansHomologuePbNoeud++;
				longSansHomologuePbNoeud = longSansHomologuePbNoeud+arcRef.longueur();
				continue;
			}


			///////// CALCUL DES PLUS COURTS CHEMINS /////////

			// creation d'un groupe "tousCandidats" avec tous les arcs candidats
			// issus du pré-appariement
			tousArcs = arcRef.arcsCompEnCorrespondance(liensPreAppAA);
			tousCandidats = (GroupeApp)reseau2.getPopGroupes().nouvelElement();
			tousCandidats.setListeArcs(new ArrayList<Arc> (tousArcs));
			itTousArcsComp = tousCandidats.getListeArcs().iterator();
			while (itTousArcsComp.hasNext()) {
				arcComp = (ArcApp) itTousArcsComp.next();
				arcComp.addGroupe(tousCandidats);
			}
			tousCandidats.ajouteNoeuds();
			// Pour éviter les débordements, on ne cherche que les pcc pas trop grands
			//longMaxRecherche = arcRef.getGeometrie().length()*5*param.coefficentPoidsLongueurDistance;
			longMaxRecherche = arcRef.getGeometrie().length()*param.distanceArcsMax;

			//calcul du poids des arcs
			calculePoids(arcRef, tousCandidats, param);

			// Recherche du PCC dans un sens, et dans l'autre si l'arc est en double sens
			pccMin1 = tousCandidats.plusCourtChemin(noeudsDebutOut, noeudsFinIn, longMaxRecherche);
			pccMin2 = tousCandidats.plusCourtChemin(noeudsFinOut, noeudsDebutIn, longMaxRecherche);
			tousCandidats.videEtDetache();

			///////// ANALYSE DES PLUS COURTS CHEMINS /////////

			// cas 3 : on n'a trouvé aucun plus court chemin, dans aucun sens
			if ( (pccMin1 == null) && (pccMin2 == null) ) {
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNoShortestPath")); //$NON-NLS-1$
				nbSansHomologuePbPCC++;
				longSansHomologuePbPCC = longSansHomologuePbPCC+arcRef.longueur();
				continue;
			}

			// cas 4 : on a trouvé un pcc dans un seul sens: celui direct
			if (pccMin1 == null) {
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin2.getListeArcs().size() == 0 ) {
					// cas 4a : on a trouvé un pcc mais il est réduit à un point
					noeudComp = (NoeudApp)pccMin2.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithACompNode")); //$NON-NLS-1$
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnEdge")); //$NON-NLS-1$
					pccMin2.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;
				}
				pccMin2.enleveExtremites();
				lien.addGroupes2(pccMin2);
				pccMin2.addLiens(lien);
				if ( arcRef.getOrientation() == -1 ) {
					// cas 4b : on a trouvé un pcc dans un seul sens, et c'est normal
					lien.setEvaluation(1);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdgeOrASuccessionOfEdges")); //$NON-NLS-1$
					pccMin2.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithARefEdge")); //$NON-NLS-1$
					nbOkUneSerie++;
					longOkUneSerie= longOkUneSerie+arcRef.longueur();
				}
				else {
					// cas 4c : on a trouvé un pcc dans un seul sens, mais ce n'est pas normal
					lien.setEvaluation(0.5);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedOnlyOneWay")); //$NON-NLS-1$
					pccMin2.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedOnlyOneWay")); //$NON-NLS-1$
					nbDouteuxPbSens++;
					longDouteuxPbSens= longDouteuxPbSens+arcRef.longueur();
				}
				continue;
			}

			// cas 4 : on a trouvé un pcc dans un seul sens: celui indirect
			if (pccMin2 == null) {
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin1.getListeArcs().size() == 0 ) {
					// cas 4a : on a trouvé un pcc mais il est réduit à un point
					noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithACompNode")); //$NON-NLS-1$
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnEdge")); //$NON-NLS-1$
					pccMin1.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;
				}
				pccMin1.enleveExtremites();
				lien.addGroupes2(pccMin1);
				pccMin1.addLiens(lien);
				// pour noter le résultat sur les objets concernés
				if ( arcRef.getOrientation() == 1 ) {
					// cas 4a : on a trouvé un pcc dans un seul sens, et c'est normal
					lien.setEvaluation(1);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdgeOrASuccessionOfEdges")); //$NON-NLS-1$
					pccMin1.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithARedEdge")); //$NON-NLS-1$
					nbOkUneSerie++;
					longOkUneSerie= longOkUneSerie+arcRef.longueur();
				}
				else {
					// cas 4b : on a trouvé un pcc dans un seul sens, mais ce n'est pas normal
					lien.setEvaluation(0.5);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedOnlyOneWay")); //$NON-NLS-1$
					pccMin1.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedOnlyOneWay")); //$NON-NLS-1$
					nbDouteuxPbSens++;
					longDouteuxPbSens= longDouteuxPbSens+arcRef.longueur();
				}
				continue;
			}

			// cas 5 : on a trouvé un pcc dans les 2 sens, et c'est le même
			if ( pccMin1.contientMemesArcs(pccMin2) ) {
				pccMin2.videEtDetache();
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin1.getListeArcs().size() == 0 ) {
					// cas 5a : on a trouvé un pcc mais il est réduit à un point
					noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithACompNode")); //$NON-NLS-1$
					noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnEdge")); //$NON-NLS-1$
					pccMin1.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;
				}
				pccMin1.enleveExtremites();
				// cas 5b : on a trouvé un pcc dans les 2 sens, non réduit à un point
				lien.addGroupes2(pccMin1);
				lien.setEvaluation(1);
				pccMin1.addLiens(lien);
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
				pccMin1.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
				nbOkUneSerie++;
				longOkUneSerie= longOkUneSerie+arcRef.longueur();
				continue;
			}

			// cas 6 : on a trouvé un pcc dans les 2 sens, mais ce n'est pas le même
			//			cas d'arcs en parralèle
			lien = (LienReseaux)liensArcsArcs.nouvelElement();
			lien.addArcs1(arcRef);
			arcRef.addLiens(lien);
			if ( pccMin1.getListeArcs().size() == 0 ) {
				// cas 6a : on a trouvé un pcc mais il est réduit à un point
				noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
				lien.addNoeuds2(noeudComp);
				lien.setEvaluation(0.5);
				noeudComp.addLiens(lien);
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithACompNode")); //$NON-NLS-1$
				noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnEdge")); //$NON-NLS-1$
				pccMin1.videEtDetache();
			}
			else {
				// cas 6b : on a trouvé un pcc non réduit à un point
				pccMin1.enleveExtremites();
				lien.setEvaluation(1);
				lien.addGroupes2(pccMin1);
				pccMin1.addLiens(lien);
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithSeveralEdges")); //$NON-NLS-1$
				pccMin1.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
			}
			if ( pccMin2.getListeArcs().size() == 0 ) {
				// cas 6a : on a trouvé un pcc mais il est réduit à un point
				noeudComp = (NoeudApp)pccMin2.getListeNoeuds().get(0);
				lien.addNoeuds2(noeudComp);
				lien.setEvaluation(0.5);
				noeudComp.addLiens(lien);
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithACompNode")); //$NON-NLS-1$
				noeudComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithAnEdge")); //$NON-NLS-1$
				pccMin2.videEtDetache();
				nbDouteuxPbNoeud++;
				longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
			}
			else {
				// cas 6b : on a trouvé un pcc non réduit à un point
				pccMin2.enleveExtremites();
				lien.addGroupes2(pccMin2);
				pccMin2.addLiens(lien);
				arcRef.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithSeveralEdges")); //$NON-NLS-1$
				pccMin2.setResultatAppariementGlobal(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.MatchedWithAnEdge")); //$NON-NLS-1$
				if (lien.getEvaluation() == 1 )	{
					nbOkPlusieursSeries++;
					longOkPlusieursSeries= longOkPlusieursSeries+arcRef.longueur();
				}
				else {
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
				}
			}
		}

		// Fin, affichage du bilan
		longTot = 	longSansHomologuePbNoeud + longSansHomologuePbPCC + longOkUneSerie +
		longOkPlusieursSeries + longDouteuxPbNoeud + longDouteuxPbSens ;
		nbTot = reseau1.getPopArcs().getElements().size();
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgeAssessment")); //$NON-NLS-1$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Network1TotalLength")+Math.round(longTot/1000)+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.KmIfMetric")); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatches")); //$NON-NLS-1$
			logger.debug("      "+nbOkUneSerie+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesMatchesWithOneOrSeveralCompEdges")+nbOkUneSerie*100/nbTot+"%nb, "+Math.round(longOkUneSerie*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug("      "+nbOkPlusieursSeries+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesMatchesWithTwoSetsOfCompEdges")+nbOkPlusieursSeries*100/nbTot+"%nb, "+Math.round(longOkPlusieursSeries*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatches")); //$NON-NLS-1$
			logger.debug("      "+nbDouteuxPbSens+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesMatchedOnlyOneWay")+nbDouteuxPbSens*100/nbTot+"%nb, "+Math.round(longDouteuxPbSens*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug("      "+nbDouteuxPbNoeud+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesMatchesWithANode")+nbDouteuxPbNoeud*100/nbTot+"%nb, "+Math.round(longDouteuxPbNoeud*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEdges")); //$NON-NLS-1$
			logger.debug("      "+nbSansHomologuePbNoeud+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesWithoutHomologous-UnmatchedNode")+nbSansHomologuePbNoeud*100/nbTot+"%nb, "+Math.round(longSansHomologuePbNoeud*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug("      "+nbSansHomologuePbPCC+I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.EdgesWithoutHomologous-NoShortestPath")+nbSansHomologuePbPCC*100/nbTot+"%nb, "+Math.round(longSansHomologuePbPCC*100/longTot)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return liensArcsArcs;
	}

	/** 
	 * Affectation des poids au arcs pour le calcul de 'plus proche chemin'
	 * @param arcRef
	 * @param tousCandidats
	 * @param param
	 */
	private static void	calculePoids(ArcApp arcRef, GroupeApp tousCandidats, ParametresApp param) {
		double poids;
		Iterator<Arc> itCandidats = tousCandidats.getListeArcs().iterator();
		while (itCandidats.hasNext()) {
			ArcApp arcComp = (ArcApp) itCandidats.next();
			//	poids = arcComp.longueur() + param.coefficentPoidsLongueurDistance*Operateurs.premiere_composante_hausdorff(arcComp.getGeometrie(),arcRef.getGeometrie()); ancienne version
			poids = Distances.ecartSurface(arcComp.getGeometrie(),arcRef.getGeometrie());
			arcComp.setPoids(poids);
		}
	}

	/** Controle de l'enemble des appariements (et non plus un à un) :
	 * recherche des arcs ou noeuds du réseau 2 appariés avec plusieurs objets du réseau 1
	 * */
	private static void controleGlobal(CarteTopo reseau1, CarteTopo reseau2, EnsembleDeLiens liens, ParametresApp param) {
		List<LienReseaux> liensObjet;
		Iterator<?> itGroupes, itArcs, itNoeuds, itLiens;
		GroupeApp groupe;
		int nb, nbSansCorresp, nbDouteux, nbOK ;
		double longTotal, longSansCorresp, longDouteux, longOK;
		ArcApp arcComp;

		////////////////////////////////////////////////////////////
		//////////// Controle global des arcs comp //////////////
		// on recherche les arcs comp appariés avec plusieurs objets ref
		// pour les marquer comme douteux

		nbSansCorresp = 0; nbDouteux = 0; nbOK = 0;
		longSansCorresp = 0; longDouteux = 0; longOK = 0;
		itArcs = reseau2.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arcComp = (ArcApp) itArcs.next();
			// On récupère tous les liens concernés par l'arc
			liensObjet = new ArrayList<LienReseaux>();
			liensObjet.addAll(arcComp.getLiens(liens.getElements()));
			itGroupes = arcComp.getListeGroupes().iterator();
			while (itGroupes.hasNext()) {
				groupe = (GroupeApp) itGroupes.next();
				liensObjet.addAll(groupe.getLiens(liens.getElements()));
			}
			// cas où l'arc n'est concerné par aucun lien
			if ( liensObjet.size() == 0) {
				arcComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched")); //$NON-NLS-1$
				nbSansCorresp++;
				longSansCorresp=longSansCorresp+arcComp.longueur();
				continue;
			}
			// cas où l'arc est concerné par un seul lien
			if ( liensObjet.size() == 1) {
				if ( (liensObjet.get(0)).getEvaluation()==1) {
					nbOK++;
					longOK=longOK+arcComp.longueur();
				}
				else {
					nbDouteux++;
					longDouteux=longDouteux+arcComp.longueur();
				}
				continue;
			}
			//cas où l'arc est concerné par plusieurs liens
			if ( param.varianteFiltrageImpassesParasites ) {
				// on regarde si l'arc est une petite impasse dans le réseau de comparaison apparié.
				// Si oui, on l'enlève des appariements, c'est sûrement un parasite;
				if (arcComp.longueur() < param.distanceNoeudsMax) { // est-il petit?
					// est-il une impasse au début?
					Noeud iniComp = arcComp.getNoeudIni();
					Iterator<?> itArcsIncidents = iniComp.arcs().iterator();
					boolean impasse = true;
					while (itArcsIncidents.hasNext()) {
						ArcApp arc = (ArcApp) itArcsIncidents.next();
						if (arc == arcComp) continue;
						if ( arc.aUnCorrespondantGeneralise(liens)) {
							impasse = false;
							break;
						}
					}
					if (impasse) { //on nettoie
						arcComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedDeadend")); //$NON-NLS-1$
						itGroupes = arcComp.getListeGroupes().iterator();
						while (itGroupes.hasNext()) {
							groupe = (GroupeApp) itGroupes.next();
							groupe.getListeArcs().remove(arcComp);
							groupe.getListeNoeuds().remove(arcComp.getNoeudIni());
							if (groupe.getListeArcs().size() == 0 ) liens.removeAll(groupe.getLiens(liens.getElements()));
						}
						arcComp.getListeGroupes().clear();
						continue;
					}

					// est-il une impasse à la fin?
					Noeud finComp = arcComp.getNoeudFin();
					itArcsIncidents = finComp.arcs().iterator();
					impasse = true;
					while (itArcsIncidents.hasNext()) {
						ArcApp arc = (ArcApp) itArcsIncidents.next();
						if (arc == arcComp) continue;
						if ( arc.aUnCorrespondantGeneralise(liens)) {
							impasse = false;
							break;
						}
					}
					if (impasse) { //on nettoie
						arcComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedDeadend")); //$NON-NLS-1$
						itGroupes = arcComp.getListeGroupes().iterator();
						while (itGroupes.hasNext()) {
							groupe = (GroupeApp) itGroupes.next();
							groupe.getListeArcs().remove(arcComp);
							groupe.getListeNoeuds().remove(arcComp.getNoeudFin());
							if (groupe.getListeArcs().size() == 0 ) liens.removeAll(groupe.getLiens(liens.getElements()));
						}
						arcComp.getListeGroupes().clear();
						continue;
					}
				}
			}

			// il faut le marquer comme incertain
			nbDouteux++;
			longDouteux=longDouteux+arcComp.longueur();
			itLiens = liensObjet.iterator();
			while (itLiens.hasNext()) {
				LienReseaux lien = (LienReseaux) itLiens.next();
				lien.affecteEvaluationAuxObjetsLies(0.5, I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainCompEdgeMatchedWithSeveralRefObjects")); //$NON-NLS-1$
				arcComp.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithSeveralRefObjects")); //$NON-NLS-1$
			}
			continue;
		}

		nb = nbDouteux+nbOK+nbSansCorresp;
		longTotal=longDouteux+longOK+longSansCorresp;
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Network2Edges")+nb+"):"); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatchedEdges")+nbOK+" ("+(nbOK*100/nb)+"%, "+Math.round(longOK*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedEdges")+nbDouteux+" ("+(nbDouteux*100/nb)+"%, "+Math.round(longDouteux*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEdges")+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%, "+Math.round(longSansCorresp*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		
		////////////////////////////////////////////////////////////
		//////////// Controle global des noeuds comp //////////////
		// on recherche les noeuds comp appariés avec plusieurs objets ref
		// pour les marquer comme douteux

		nbSansCorresp = 0;
		nbDouteux = 0;
		nbOK = 0;
		itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			//on récupère tous les liens concernés par le noeud
			liensObjet = new ArrayList<LienReseaux>();
			liensObjet.addAll(noeud.getLiens());
			itGroupes = noeud.getListeGroupes().iterator();
			while (itGroupes.hasNext()) {
				groupe = (GroupeApp) itGroupes.next();
				liensObjet.addAll(groupe.getLiens());
			}
			liensObjet.retainAll(liens.getElements());

			// cas où le noeud n'est concerné par aucun lien
			if ( liensObjet.size() == 0) {
				noeud.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched")); //$NON-NLS-1$
				nbSansCorresp = nbSansCorresp+1;
				continue;
			}

			// cas où le noeud est concerné par un seul lien
			if ( liensObjet.size() == 1) {
				if ( (liensObjet.get(0)).getEvaluation()==1) nbOK++;
				else nbDouteux++;
				continue;
			}

			//cas où le noeud est concerné par plusieurs liens
			// il faut le marquer comme incertain si il est concerné par au moins un un noeud ref
			// (et non par des arcRefs, car cela peut être normal dans le cas des doubles voies par exemple)
			itLiens = liensObjet.iterator();
			boolean OK = true;
			while (itLiens.hasNext()) {
				LienReseaux lien = (LienReseaux) itLiens.next();
				if (lien.getNoeuds1().size() != 0 ) {
					OK = false;
					break;
				}
			}
			if (OK) nbOK++;
			else {
				nbDouteux++;
				itLiens = liensObjet.iterator();
				while (itLiens.hasNext()) {
					LienReseaux lien = (LienReseaux) itLiens.next();
					lien.affecteEvaluationAuxObjetsLies(0.5, I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainCompNodeMatchedWithSeveralRefObjects")); //$NON-NLS-1$
					noeud.setResultatAppariement(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedWithSeveralRefObjects")); //$NON-NLS-1$
				}
			}
		}
		nb = nbDouteux+nbOK+nbSansCorresp;
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Network2Nodes")+nb+"):"); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatchedNodes")+nbOK+" ("+(nbOK*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedNodes")+nbDouteux+" ("+(nbDouteux*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNodes")+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		////////////////////////////////////////////////////////
		//////////// Controle global des arcs ref //////////////
		// On ne fait que compter pour évaluer le résultat
		nbSansCorresp = 0; nbDouteux = 0; nbOK = 0;
		longSansCorresp = 0; longDouteux = 0; longOK = 0;

		itArcs = reseau1.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			ArcApp arc = (ArcApp) itArcs.next();
			if (arc.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched"))) { //$NON-NLS-1$
				nbSansCorresp++;
				longSansCorresp = longSansCorresp+arc.longueur();
				continue;
			}
			if (arc.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Uncertain"))) { //$NON-NLS-1$
				nbDouteux++;
				longDouteux= longDouteux+arc.longueur();
				continue;
			}
			if (arc.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Matched"))) { //$NON-NLS-1$
				nbOK++;
				longOK= longOK+arc.longueur();
				continue;
			}
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnexpectedResultOnEdge")+arc.getResultatAppariement()); //$NON-NLS-1$
		}

		nb = nbDouteux+nbOK+nbSansCorresp;
		longTotal=longDouteux+longOK+longSansCorresp;
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Network1Edges")+nb+"):"); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatchedEdges")+nbOK+" ("+(nbOK*100/nb)+"%, "+Math.round(longOK*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedEdges")+nbDouteux+" ("+(nbDouteux*100/nb)+"%, "+Math.round(longDouteux*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedEdges")+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%, "+Math.round(longSansCorresp*100/longTotal)+"%long)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		/////////////////////////////////////////////
		//////////// cas des noeudss ref ////////////
		// On ne fait que compter pour évaluer le résultat
		nbSansCorresp = 0; 	nbDouteux = 0; 	nbOK = 0;
		itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			if (noeud.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched"))) { //$NON-NLS-1$
				nbSansCorresp++;
				continue;
			}
			if (noeud.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Uncertain"))) { //$NON-NLS-1$
				nbDouteux++;
				continue;
			}
			if (noeud.getResultatAppariement().startsWith(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Unmatched"))) { //$NON-NLS-1$
				nbOK++;
				continue;
			}
			if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnexpectedResultOnNode")+noeud.getResultatAppariement()); //$NON-NLS-1$
		}
		nb = nbDouteux+nbOK+nbSansCorresp;
		if (logger.isDebugEnabled()) {
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.Network1Nodes")+nb+"):"); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.CorrectMatchedNodes")+nbOK+" ("+(nbOK*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UncertainMatchedNodes")+nbDouteux+" ("+(nbDouteux*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNodes")+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/** Les noeuds de référence non appariés par les 'liens' sont projetés sur le réseau comp
	 * de manière à introduire un noeud dans le reséau Comp aux endroits qui pourraient correspondre
	 * à ces noeuds Ref non appariés.
	 */
	public static void decoupeNoeudsNonApparies(ReseauApp ref, ReseauApp comp, EnsembleDeLiens liens,  ParametresApp param) {
		List<GM_Point> noeudsNonApparies = new ArrayList<GM_Point>();
		Iterator<?> itNoeuds = ref.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			if ( noeud.getLiens(liens.getElements()).size() == 0) noeudsNonApparies.add(noeud.getGeometrie());
		}
		if (logger.isDebugEnabled()) logger.debug(I18N.getString("fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Appariement.UnmatchedNodes")+noeudsNonApparies.size()); //$NON-NLS-1$
		comp.projete(noeudsNonApparies, param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc, param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud);
	}

	/** Découpe les arcs du reseau1 'reseauADecouper' non appariés par les 'liens' de manière
	 *  à introduire un noeud dans le reséauADecouper aux endroits où il s'éloigne du réseau2 'reseauDecoupant'.
	 * 
	 *  Remarque: utilisé pour les GR par exemple pour traiter le cas des GR hors sentier.
	 *  
	 *  Dernière modif: 
	 *  08/04/2009, Seb, amélioration de la méthode pour autoriser les découpages multiples
	 */
	public static void decoupeNonApparies(ReseauApp reseauADecouper, ReseauApp reseauDecoupant, EnsembleDeLiens liens,  ParametresApp param) {

		double distanceMaxNoeudArc = param.projeteNoeud2surReseau1_DistanceNoeudArc; //param.distanceArcsMax;
		double distancePtCourantVersArcADecoupe ;
		ArcApp arcDecoupant, arcADecouper;
		Iterator<?> itArcsDecoupes,itArcsDecoupants;
		FT_FeatureCollection<?> arcsDecoupantsRes2 ;
		List<GM_Point> pointsDeDecoupage;
		DirectPosition ptCourantArcDecoupant, ptDecoupage;
		int indiceDernierPtProche ;
		boolean proche;

		itArcsDecoupes = reseauADecouper.getPopArcs().getElements().iterator();
		pointsDeDecoupage = new ArrayList<GM_Point>();
		// recherche des points de découpage pour chaque arc à découper
		while (itArcsDecoupes.hasNext()) {
			arcADecouper = (ArcApp) itArcsDecoupes.next();
			// on ne traite que les arcs non appariés auparavant
			if (arcADecouper.getLiens(liens.getElements()).size() != 0) continue;
			
			// on découpe chaque arc non apparié en fonction des arcs de l'autre réseau assez proches (seuil choisi = diantanceMaxNoeudArc)
			arcsDecoupantsRes2 = reseauDecoupant.getPopArcs().select(arcADecouper.getGeometrie(), distanceMaxNoeudArc);
			if (arcsDecoupantsRes2.size() == 0) continue;

			// on traite chaque arc découpant à son tour
			itArcsDecoupants = arcsDecoupantsRes2.getElements().iterator();
			while (itArcsDecoupants.hasNext()) {
				arcDecoupant = (ArcApp)itArcsDecoupants.next();
				// ré échantillonage de la géométrie de l'arc découpant à environ 1m
				
				GM_LineString lineStringDecoupant = arcDecoupant.getGeometrie();
				for(DirectPosition dp:arcADecouper.getGeometrie().coord().getList()) {
					lineStringDecoupant = Operateurs.projectionEtInsertion(dp, lineStringDecoupant);
				}
				
				// initalisation des compteurs
				indiceDernierPtProche= 0;
				if ( Distances.distance(lineStringDecoupant.getControlPoint(0), arcADecouper.getGeometrie() ) <= distanceMaxNoeudArc ) 
					proche = true;
				else
					proche = false;
				// parcour des points de l'arc découpant
				for(int i=1;i<lineStringDecoupant.getControlPoint().size();i++) {
					ptCourantArcDecoupant = lineStringDecoupant.getControlPoint(i);
					distancePtCourantVersArcADecoupe = Distances.distance(ptCourantArcDecoupant, arcADecouper.getGeometrie()) ;
					if (proche) {
						if ( distancePtCourantVersArcADecoupe >  distanceMaxNoeudArc) {
							// on était proche et on s'éloigne à partir de ce point
							// --> on rajoute deux points de découpage, entourant la partie proche,
							proche = false;
							//ptDecoupage = Operateurs.projection(arcDecoupant.getGeometrie().getControlPoint(indiceDernierPtProche), arcADecouper.getGeometrie());
							//pointsDeDecoupage.add(new GM_Point(ptDecoupage));
							ptDecoupage = Operateurs.projection(lineStringDecoupant.getControlPoint(i-1), arcADecouper.getGeometrie());
							pointsDeDecoupage.add(new GM_Point(ptDecoupage));
							continue;
						}
						else {
							// on était proche et on le reste: on continue à parcourir l'arc découpant
							continue;
						}
					}
					else {
						if ( distancePtCourantVersArcADecoupe <= distanceMaxNoeudArc) {
							// on était éloigné et on rentre dans une zone proche: intialisation des compteurs
							proche = true;
							indiceDernierPtProche = i;
							ptDecoupage = Operateurs.projection(lineStringDecoupant.getControlPoint(indiceDernierPtProche), arcADecouper.getGeometrie());
							pointsDeDecoupage.add(new GM_Point(ptDecoupage));
							continue;
						}
						else {
							// on était éloigné et on le reste: on continue à parcourir l'arc découpant
							continue;
						}
					}
				}
			}
		}
		
		// Découpage effectif du réseau à découper.
		reseauADecouper.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
		reseauADecouper.instancieAttributsNuls(param);
		reseauDecoupant.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
	}

	/* Ancienne version 08/04/2009, avant amélioration du redecoupage
	public static void decoupeNonApparies(ReseauApp ref, ReseauApp comp, EnsembleDeLiens liens,  ParametresApp param) {

		double distanceMaxNoeudArc = param.projeteNoeud2surReseau1_DistanceNoeudArc; //param.distanceArcsMax;
		double distanceMaxProjectionNoeud = param.projeteNoeud2surReseau1_DistanceProjectionNoeud;
		ArcApp arcComp, arcDecoupe;
		Iterator<?> itArcsDecoupes,itArcsDecoupants;
		FT_FeatureCollection<?> arcsCompProches ;
		List<GM_Point> pointsDeDecoupage;
		DirectPosition pt, ptDecoupage;
		int indiceDernierPtProche ;
		int i;
		double longProche;
		boolean proche;

		itArcsDecoupes = ref.getPopArcs().getElements().iterator();
		pointsDeDecoupage = new ArrayList<GM_Point>();
		while (itArcsDecoupes.hasNext()) {
			arcDecoupe = (ArcApp) itArcsDecoupes.next();
			if (arcDecoupe.getLiens(liens.getElements()).size() != 0) continue;

			arcsCompProches = comp.getPopArcs().select(arcDecoupe.getGeometrie(), distanceMaxNoeudArc);
			if (arcsCompProches.size() == 0) continue;

			itArcsDecoupants = arcsCompProches.getElements().iterator();
			while (itArcsDecoupants.hasNext()) {
				arcComp = (ArcApp)itArcsDecoupants.next();

				// recherche en partant du début
				indiceDernierPtProche= 0;
				longProche = 0;
				proche = false;
				for(i=0;i<arcComp.getGeometrie().getControlPoint().size();i++) {
					pt = arcComp.getGeometrie().getControlPoint(i);
					if ( Distances.distance(pt, arcDecoupe.getGeometrie() ) < distanceMaxNoeudArc ) {
						if (proche) longProche = longProche+Distances.distance(arcComp.getGeometrie().getControlPoint(i), arcComp.getGeometrie().getControlPoint(i-1));
						indiceDernierPtProche = i;
						proche = true;
					}
					else {
						if (proche) break;
					}
				}
				if ( indiceDernierPtProche!= 0 &&
						indiceDernierPtProche!=arcComp.getGeometrie().getControlPoint().size()-1 &&
						longProche > distanceMaxProjectionNoeud ) {
					ptDecoupage = Operateurs.projection(arcComp.getGeometrie().getControlPoint(indiceDernierPtProche), arcDecoupe.getGeometrie());
					pointsDeDecoupage.add(new GM_Point(ptDecoupage));
				}

				// recherche en partant de la fin
				indiceDernierPtProche= arcComp.getCoord().size()-1;
				longProche = 0;
				proche = false;
				for(i=arcComp.getGeometrie().getControlPoint().size()-1;i>=0;i--) {
					pt = arcComp.getGeometrie().getControlPoint(i);
					if ( Distances.distance(pt, arcDecoupe.getGeometrie() ) < distanceMaxNoeudArc ) {
						if (proche) longProche = longProche+Distances.distance(arcComp.getGeometrie().getControlPoint(i), arcComp.getGeometrie().getControlPoint(i+1));
						indiceDernierPtProche = i;
						proche = true;
					}
					else {
						if (proche) break;
					}
				}
				if ( indiceDernierPtProche!= 0 &&
						indiceDernierPtProche!=arcComp.getGeometrie().getControlPoint().size()-1 &&
						longProche > distanceMaxProjectionNoeud ) {
					ptDecoupage = Operateurs.projection(arcComp.getGeometrie().getControlPoint(indiceDernierPtProche), arcDecoupe.getGeometrie());
					pointsDeDecoupage.add(new GM_Point(ptDecoupage));
				}
			}
		}
		ref.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
		param.distanceNoeudsMax = 51;
		ref.instancieAttributsNuls(param);
		comp.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
	}
	*/
}