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

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe racine de la carte topo. 
 * Une carte topo est une composition d'arcs, de noeuds, de faces et de groupes.
 * Une carte topo est vue comme un DataSet particulier. 
 * Elle a éventuellement une topologie (SPAGHETTI, NETWORK ou MAP).
 * 
 * English: a topological map is an oriented graph, with arcs sorted around the nodes;
 * @author  Mustière/Bonin
 * @version 1.0
 */


public class CarteTopo extends DataSet {


//	Accès aux composants de la carte topo

	/** Population des arcs de la carte topo. */
	public Population getPopArcs() {return this.getPopulation("Arc");}
	/** Population des noeuds de la carte topo. */
	public Population getPopNoeuds() {return this.getPopulation("Noeud");}
	/** Population des faces de la carte topo. */
	public Population getPopFaces() {return this.getPopulation("Face");}
	/** Population des groupes de la carte topo. */
	public Population getPopGroupes() {return this.getPopulation("Groupe");}

	/** Liste des noeuds de la carte topo. Surcharge de getPopNoeuds().getElements(). */
	public List getListeNoeuds() {return this.getPopNoeuds().getElements();}
	/** Liste des arcs de la carte topo. Surcharge de getPopArcs().getElements(). */
	public List getListeArcs() {return this.getPopArcs().getElements();}
	/** Liste des faces de la carte topo. Surcharge de getPopFaces().getElements(). */
	public List getListeFaces() {return this.getPopFaces().getElements();}
	/** Liste des groupes de la carte topo. Surcharge de getPopGroupes().getElements(). */
	public List getListeGroupes() {return this.getPopGroupes().getElements();}

	/** Ajoute un noeud à la population des noeuds de la carte topo. 
		Attention : même si la carte topo est persistante, le noeud n'est pas rendu persistant dans cette méthode */
	public void addNoeud(Noeud noeud) {this.getPopNoeuds().add(noeud);}
	/** Ajoute un arc à la population des arcs de la carte topo. 
		Attention : même si la carte topo est persistante, le noeud n'est pas rendu persistant dans cette méthode */
	public void addArc(Arc arc) {this.getPopArcs().add(arc);}
	/** Ajoute une face à la population des faces de la carte topo. 
		Attention : même si la carte topo est persistante, le noeud n'est pas rendu persistant dans cette méthode */
	public void addFace(Face face) {this.getPopFaces().add(face);}
	/** Ajoute un groupe à la population des groupes de la carte topo. 
		Attention : même si la carte topo est persistante, le noeud n'est pas rendu persistant dans cette méthode */
	public void addGroupe(Groupe groupe) {this.getPopGroupes().add(groupe);}


/////////////////////////////////////////////////////////////////////////////////////////////
//	Constructeurs
/////////////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur par défaut;
	 *  ATTENTION, constructeur à éviter car aucune population n'est créée:
	 * seule un objet carteTopo est créé */
	public CarteTopo() {}

	/** Constructeur d'une carte topo non persistante.
	 *  Le nom logique peut ête utilisé si la carte topo apparient à un DataSet, 
	 *  il peut être une chaîne vide sinon.
	 * 
	 *  Par ce constructeur, la carte topo contient des arcs/noeuds/faces/groupes  
	 *  des classes CarteTopo.Arc, CarteTopo.Noeud, CarteTopo.Face, CarteTopo.Groupe.
	 */
	public CarteTopo(String nomLogique) {
		this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
		this.setNom(nomLogique);
		this.setPersistant(false);
		Population arcs = new Population(false, "Arc", fr.ign.cogit.geoxygene.contrib.cartetopo.Arc.class,true);
		this.addPopulation(arcs);
		Population noeuds = new Population(false, "Noeud", fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud.class,true);
		this.addPopulation(noeuds);
		Population faces = new Population(false, "Face", fr.ign.cogit.geoxygene.contrib.cartetopo.Face.class,true);
		this.addPopulation(faces);
		Population groupes = new Population(false, "Groupe", fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe.class,false);
		this.addPopulation(groupes);
	}


/////////////////////////////////////////////////////////////////////////////////////////////
//	Attributs de la carte topo     
/////////////////////////////////////////////////////////////////////////////////////////////

	// Description de la structure topologique
	/** Spaghetti = pas de relation topologique entre arcs, noeuds et faces */
	public static final int SPAGHETTI = 0;
	/** Network = topologie arc / noeuds */
	public static final int NETWORK = 1;
	/** Network = topologie arc / noeuds / faces */
	public static final int MAP = 2;
	/** Niveau de topologie : 
	 * SPAGHETTI = pas de topologie ; NETWORK = topologie arcs/noeuds ; MAP (par défaut) = topologie arcs/noeuds/faces 
	 * NB : utiliser les constantes SPAGHETTI, NETWORK ou MAP pour remplir cet attribut.
	 * Remarque codeurs : Le code se sert très peu de l'attribut "type" pour l'instant. A revoir.
	 */
	private int type = MAP;
	public int getType() {return type;}
	public void setType(int i) {type = i;}



/////////////////////////////////////////////////////////////////////////////////////////////
//	COPIE et VIDAGE de la carte topo     
/////////////////////////////////////////////////////////////////////////////////////////////
	/** Copie d'une carte topologique avec toutes les relations topologiques.
	 * Les liens "correspondants" sont aussi dupliqués.
	 * 
	 * ATTENTION: ne fonctionne bien que pour une carteTopo non spécialisée 
	 * (i.e. avec carteTopo.Arc, carte.Noeud...).
	 * En effet, les objets copiés appartiendront au package cartetopo. 
	 */
	public CarteTopo copie(String nomLogique) {
		Noeud noeud, noeudCopie;
		Arc arc, arcCopie;
		Face face, faceCopie;
		Groupe groupe, groupeCopie;
		FT_Feature corresp;
		Iterator itNoeuds, itArcs, itCorresp, itArcsCopies,   
		itFaces, itGroupes, itGroupesCopies;
		ArrayList noeuds, noeudsCopies, arcs, arcsCopies, faces, facesCopies;

		// création d'une nouvelle carte        
		CarteTopo carte = new CarteTopo(nomLogique);

		// copie des objets, sans relation topologique        
		itNoeuds = this.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud) itNoeuds.next();
			noeudCopie = (Noeud)carte.getPopNoeuds().nouvelElement();
			noeudCopie.setGeometrie(noeud.getGeometrie());
			itCorresp = noeud.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				noeudCopie.addCorrespondant(corresp);
			}
		}
		itArcs = this.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arc = (Arc) itArcs.next();
			arcCopie = (Arc)carte.getPopArcs().nouvelElement();
			arcCopie.setGeometrie(arc.getGeometrie());
			itCorresp = arc.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				arcCopie.addCorrespondant(corresp);
			}
		}
		itFaces = this.getPopFaces().getElements().iterator();
		while (itFaces.hasNext()) {
			face = (Face) itFaces.next();
			faceCopie = (Face)carte.getPopFaces().nouvelElement();
			faceCopie.setGeometrie(face.getGeometrie());
			itCorresp = face.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				faceCopie.addCorrespondant(corresp);
			}
		}
		itGroupes = this.getPopGroupes().getElements().iterator();
		while (itGroupes.hasNext()) {
			groupe = (Groupe) itGroupes.next();
			groupeCopie = (Groupe)carte.getPopGroupes().nouvelElement();
			itCorresp = groupe.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				groupeCopie.addCorrespondant(corresp);
			}
		}

		if (type == SPAGHETTI) return carte;

		// copie des relations topologiques        
		noeuds = new ArrayList(this.getPopNoeuds().getElements());
		noeudsCopies = new ArrayList(carte.getPopNoeuds().getElements());
		arcs = new ArrayList(this.getPopArcs().getElements());
		arcsCopies = new ArrayList(carte.getPopArcs().getElements());
		faces = new ArrayList(this.getPopFaces().getElements());
		facesCopies = new ArrayList(carte.getPopArcs().getElements());

		itArcs = this.getPopArcs().getElements().iterator();
		itArcsCopies = carte.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arc = (Arc) itArcs.next();
			arcCopie = (Arc) itArcsCopies.next();
			arcCopie.setNoeudIni((Noeud) noeudsCopies.get(noeuds.indexOf(arc.getNoeudIni())));
			arcCopie.setNoeudFin((Noeud) noeudsCopies.get(noeuds.indexOf(arc.getNoeudFin())));
			if (arc.getFaceGauche() != null) {
				arcCopie.setFaceGauche((Face) facesCopies.get(faces.indexOf(arc.getFaceGauche())));
			}
			if (arc.getFaceDroite() != null) {
				arcCopie.setFaceDroite((Face) facesCopies.get(faces.indexOf(arc.getFaceDroite())));
			}
		}

		itGroupes = this.getPopGroupes().getElements().iterator();
		itGroupesCopies = carte.getPopGroupes().getElements().iterator();
		while (itGroupes.hasNext()) {
			groupe = (Groupe) itGroupes.next();
			groupeCopie = (Groupe) itGroupesCopies.next();
			itNoeuds = groupeCopie.getListeNoeuds().iterator();
			while (itNoeuds.hasNext()) {
				noeud = (Noeud) itNoeuds.next();
				groupeCopie.addNoeud( (Noeud) noeudsCopies.get(noeuds.indexOf(noeud)) );
			}
			itArcs = groupeCopie.getListeArcs().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				groupeCopie.addArc( (Arc) arcsCopies.get(arcs.indexOf(arc)) );
			}
			itFaces = groupeCopie.getListeFaces().iterator();
			while (itFaces.hasNext()) {
				face = (Face) itFaces.next();
				groupeCopie.addFace( (Face) facesCopies.get(faces.indexOf(face)) );
			}
		} 
		return carte;
	}


	/** Enlève des arcs de la carteTopo, en enlevant aussi les relations topologiques
	 * les concernant (avec les faces et noeuds).
	 */
	public void enleveArcs(List arcsAEnlever) {
		Iterator itArcs = arcsAEnlever.iterator();
		Arc arc;
		while (itArcs.hasNext()) {
			arc = (Arc) itArcs.next();
			this.getPopArcs().remove(arc);
			arc.setNoeudFin(null);
			arc.setNoeudIni(null);
			arc.setFaceDroite(null);
			arc.setFaceGauche(null);
		}
	}

	/** Enlève des noeuds de la carteTopo, en enlevant aussi les relations topologiques
	 * les concernant (avec les arcs et par conséquent avec les faces).
	 */
	public void enleveNoeuds(List noeudsAEnlever) {
		Iterator itNoeuds = noeudsAEnlever.iterator();
		Iterator itArcs;
		Noeud noeud;
		Arc arc;
		while (itNoeuds.hasNext()) {
			noeud = (Noeud) itNoeuds.next();
			this.getPopNoeuds().remove(noeud);
			itArcs = noeud.getEntrants().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				arc.setNoeudFin(null);
			}
			itArcs = noeud.getSortants().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				arc.setNoeudIni(null);
			}
		}
	}

	/** Enlève des faces de la carteTopo, en enlevant aussi les relations topologiques
	 * les concernant (avec les arcs et par conséquent avec les noeuds).
	 */
	public void enleveFaces(List facesAEnlever) {
		Iterator itFaces = facesAEnlever.iterator();
		Iterator itArcs;
		Face face;
		Arc arc;
		while (itFaces.hasNext()) {
			face = (Face) itFaces.next();
			this.getPopFaces().remove(face);
			itArcs = face.getArcsDirects().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				arc.setFaceGauche(null);
			}
			itArcs = face.getArcsIndirects().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				arc.setFaceDroite(null);
			}
		}
	}


/////////////////////////////////////////////////////////////////////////////////////////////
//	Instanciation ou nettoyage de la topologie de réseau 
/////////////////////////////////////////////////////////////////////////////////////////////

	/** Instancie la topologie de réseau d'une Carte Topo, 
	 *  en se basant sur la géométrie 2D des arcs et des noeuds.
	 *  Autrement dit: crée les relation "noeud initial" et "noeud final" d'un arc.
	 * 
	 *  ATTENTION: cette méthode ne rajoute pas de noeuds. Si un arc n'a pas de noeud
	 *  localisé à son extrémité, il n'aura pas de noeud initial (ou final).
	 *  DE PLUS si plusieurs noeuds sont trop proches (cf. param tolérance), 
	 *  alors un des noeuds est choisi au hasard pour la relation arc/noeud,
	 *  ce qui n'est pas correct.
	 *  IL EST DONC CONSEILLE DE FILTRER LES DOUBLONS AVANT SI NECESSAIRE.
	 * 
	 *  NB: si cela n'avait pas été fait avant, 
	 *  la population des noeuds est indexée dans cette méthode
	 *  (dallage, paramètre = 20).
	 * 
	 *  @param tolerance
	 *  Le paramètre "tolerance" spécifie la distance maximale acceptée entre
	 *  la position d'un noeud et la position d'une extrémité de ligne, 
	 *  pour considérer ce noeud comme extrémité (la tolérance peut être nulle).
	 * 
	 */
	public void creeTopologieArcsNoeuds(double tolerance) {
		Arc arc;
		Iterator itArcs;
		FT_FeatureCollection selection;

		// initialisation de l'index au besoin		
		if ( ! this.getPopNoeuds().hasSpatialIndex() )
			this.getPopNoeuds().initSpatialIndex(Tiling.class, true, 20 );

		itArcs = this.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arc = (Arc)itArcs.next();
			selection = this.getPopNoeuds().select(arc.getGeometrie().startPoint(), tolerance);
			if (selection.getElements().size() != 0) arc.setNoeudIni((Noeud)selection.getElements().get(0));
			selection = this.getPopNoeuds().select(arc.getGeometrie().endPoint(), tolerance);
			if (selection.getElements().size() != 0) arc.setNoeudFin((Noeud)selection.getElements().get(0));
		}

	}


	/**  Crée un nouveau noeud à l'extrémité de chaque arc si il n'y en a pas.
	 *   Les noeuds existants sont tous conservés.
	 * 
	 *  NB: la topologie arcs/noeuds est instanciée au passage.
	 * 
	 *  NB: si cela n'avait pas été fait avant, 
	 *  la population des noeuds est indexée dans cette méthode.
	 * 	Paramètres de l'index = le même que celui des arcs si il existe, 
	 *  sinon Dallage avec à peu près 50 noeuds par dalle.
	 * 
	 */
	public void creeNoeudsManquants(double tolerance) {
		Arc arc;
		Noeud noeud;
		Iterator itArcs; 
		FT_FeatureCollection selection;

		// initialisation de l'index au besoin
		// si on peut, on prend les mêmes paramètres que le dallage des arcs		
		if ( ! this.getPopNoeuds().hasSpatialIndex() ) {
			if ( this.getPopArcs().hasSpatialIndex()) {
				this.getPopNoeuds().initSpatialIndex(this.getPopArcs().getSpatialIndex() );
				this.getPopNoeuds().getSpatialIndex().setAutomaticUpdate(true);
			}
			else {
				GM_Envelope enveloppe = this.getPopArcs().envelope();
				int nb = (int)Math.sqrt(this.getPopArcs().size()/20);
				if (nb == 0) nb=1;
				this.getPopNoeuds().initSpatialIndex(Tiling.class, true, enveloppe, nb);
			}
		}

		itArcs = this.getPopArcs().getElements().iterator();;
		while (itArcs.hasNext()) {
			arc = (Arc)itArcs.next();
			//noeud initial
			selection = this.getPopNoeuds().select(arc.getGeometrie().startPoint(),tolerance);
			if (selection.getElements().size() == 0) {
				noeud = (Noeud)this.getPopNoeuds().nouvelElement(new GM_Point(arc.getGeometrie().startPoint()));
			}
			else noeud = (Noeud)selection.getElements().get(0);
			arc.setNoeudIni(noeud);

			//noeud final
			selection = this.getPopNoeuds().select(arc.getGeometrie().endPoint(),tolerance);
			if (selection.getElements().size() == 0) {
				noeud = (Noeud)this.getPopNoeuds().nouvelElement(new GM_Point(arc.getGeometrie().endPoint()));
			}
			else noeud = (Noeud)selection.getElements().get(0);
			arc.setNoeudFin(noeud);
		}
	}


	/** Filtrage des noeuds isolés (c'est-à-dire connectés à aucun arc).
	 *  Ceux-ci sont enlevés de la Carte Topo  
	 *  IMPORTANT : La topologie de réseau doit avoir été instanciée,
	 *  sinon tous les noeuds sont enlevés.
	 */
	public void filtreNoeudsIsoles() {
		Iterator itNoeuds;
		List aJeter = new ArrayList();
		Noeud noeud;

		itNoeuds = this.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud)itNoeuds.next();
			if ( noeud.arcs().size() == 0 ) aJeter.add(noeud);
		}
		itNoeuds = aJeter.iterator();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud)itNoeuds.next();
			this.getPopNoeuds().enleveElement(noeud);
		}
	}


	/** Filtrage des noeuds doublons (plusieurs noeuds localisés au même endroit).
	 * 
	 * NB: si cela n'avait pas été fait avant, 
	 * la population des noeuds est indexée dans cette méthode 
	 * (dallage, paramètre = 20).
	 * 
	 * Cette méthode gère les conséquences sur la topologie, 
	 * si celle-ci a été instanciée auparavant. 
	 * Cette méthode gère aussi les conséquences sur les correspondants (un
	 * noeud gardé a pour correspondants tous les correspondants des doublons).
	 * 
	 * @param tolerance 
	 * Le paramètre tolérance spécifie la distance maximale pour considérer deux
	 * noeuds positionnés au même endroit.
	 */
	public void filtreDoublons(double tolerance) {
		Iterator itNoeuds, itDoublons, itCorresp, itArcs;
		Noeud doublon, noeud;
		Arc arc;
		FT_Feature corresp;
		List aJeter = new ArrayList();
		FT_FeatureCollection selection;

		// initialisation de l'index au besoin		
		if ( ! this.getPopNoeuds().hasSpatialIndex() ) {
			this.getPopNoeuds().initSpatialIndex(Tiling.class, true, 20 );
		}
		itNoeuds = this.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud)itNoeuds.next();
			if ( aJeter.contains(noeud) ) continue;
			selection = this.getPopNoeuds().select(noeud.getCoord(),tolerance);
			itDoublons = selection.getElements().iterator();
			while (itDoublons.hasNext()) {
				doublon = (Noeud)itDoublons.next();
				if ( doublon == noeud ) continue;
				// on a trouvé un doublon à jeter
				// on gère les conséquences sur la topologie et les correspondants
				aJeter.add(doublon);
				itCorresp = doublon.getCorrespondants().iterator();
				while (itCorresp.hasNext() ) {
					corresp = (FT_Feature)itCorresp.next();
					noeud.addCorrespondant(corresp);
				}
				itArcs = doublon.getEntrants().iterator();
				while (itArcs.hasNext() ) {
					arc = (Arc)itArcs.next();
					noeud.addEntrant(arc);
				}
				itArcs = doublon.getSortants().iterator();
				while (itArcs.hasNext() ) {
					arc = (Arc)itArcs.next();
					noeud.addSortant(arc);
				}
			}
		}
		itNoeuds = aJeter.iterator();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud)itNoeuds.next();
			this.getPopNoeuds().enleveElement(noeud);
		}
	}


	/** Filtrage des noeuds "simples", c'est-à-dire avec seulement deux arcs incidents,
	 * si ils ont des orientations compatibles.
	 * Ces noeuds sont enlevés et un seul arc est créé à la place des deux arcs incidents.
	 *  
	 * Cette méthode gère les conséquences sur la topologie arcs/noeuds/faces.
	 * Cette méthode gère aussi les conséquences sur les correspondants.
	 * (un nouvel arc a pour correspondants tous les correspondants des deux
	 * arcs incidents). 
	 * Cette méthode gère les conséquences sur l'orientation
	 * 
	 * IMPORTANT: la topologie arcs/noeuds doit avoir été instanciée avant 
	 * de lancer cette méthode
	 */
	public void filtreNoeudsSimples() {
		Iterator itNoeuds, itCorresp;
		List geometries, arcsIncidents ;
		Noeud noeud, noeudIni1, noeudIni2, noeudFin1, noeudFin2;
		Arc arcTotal, arc1, arc2;
		Face faceDroite1, faceDroite2, faceGauche1, faceGauche2;  
		FT_Feature corresp;

		itNoeuds = this.getPopNoeuds().getElements().iterator();
		List noeudsElimines = new ArrayList();
		while (itNoeuds.hasNext()) {
			noeud = (Noeud)itNoeuds.next();
			arcsIncidents = noeud.arcs();
			if ( arcsIncidents.size() != 2 ) continue;
			if ( arcsIncidents.get(0) == arcsIncidents.get(1) ) continue; // gestion des boucles
			if ( noeud.entrantsOrientes().size() == 0 ) continue; // incompatibilité d'orientation
			if ( noeud.sortantsOrientes().size() == 0 ) continue; // incompatibilité d'orientation
			if ( (noeud.entrantsOrientes().size()+noeud.sortantsOrientes().size()) == 3 ) continue; // incompatibilité d'orientation

			arcTotal = (Arc)this.getPopArcs().nouvelElement();
			geometries= new ArrayList();
			arc1 = (Arc)arcsIncidents.get(0);
			arc2 = (Arc)arcsIncidents.get(1);
			geometries.add(arc1.getGeometrie());
			geometries.add(arc2.getGeometrie());

			//création de la nouvelle géométrie
			arcTotal.setGeometrie(Operateurs.compileArcs(geometries));

			//gestion des conséquences sur l'orientation et les correspondants
			arcTotal.setOrientation(arc1.getOrientation());
			itCorresp = arc1.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				if (!arcTotal.getCorrespondants().contains(corresp))
					arcTotal.addCorrespondant(corresp);
			}
			arc1.setCorrespondants(new ArrayList());

			itCorresp = arc2.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				arcTotal.addCorrespondant(corresp);
			}
			arc2.setCorrespondants(new ArrayList());

			itCorresp = noeud.getCorrespondants().iterator();
			while (itCorresp.hasNext() ) {
				corresp = (FT_Feature)itCorresp.next();
				if (!arcTotal.getCorrespondants().contains(corresp))
					arcTotal.addCorrespondant(corresp);
			}
			noeud.setCorrespondants(new ArrayList());

			//gestion des conséquences sur la topologie
			faceDroite1 = arc1.getFaceDroite();
			faceGauche1 = arc1.getFaceGauche();
			faceDroite2 = arc2.getFaceDroite();
			faceGauche2 = arc2.getFaceGauche();
			noeudIni1 = arc1.getNoeudIni();
			noeudFin1 = arc1.getNoeudFin();
			noeudIni2 = arc2.getNoeudIni();
			noeudFin2 = arc2.getNoeudFin();

			// conséquences sur le premier arc 			
			if ( noeudIni1 == noeud ) {
				noeudIni1.getSortants().remove(arc1);
				if ( noeudFin1 != null ) {
					noeudFin1.getEntrants().remove(arc1);
					noeudFin1.addSortant(arcTotal);

				}
				if ( faceDroite1 != null) {
					faceDroite1.getArcsIndirects().remove(arc1);
					arcTotal.setFaceGauche(faceDroite1);
				}
				if ( faceGauche1 != null) {
					faceGauche1.getArcsDirects().remove(arc1);
					arcTotal.setFaceDroite(faceGauche1);
				}
			}
			else {
				noeudFin1.getEntrants().remove(arc1);
				if (noeudIni1 != null ) {
					noeudIni1.getSortants().remove(arc1);
					noeudIni1.addSortant(arcTotal);
				}
				if ( faceDroite1 != null) {
					faceDroite1.getArcsIndirects().remove(arc1);
					arcTotal.setFaceDroite(faceDroite1);
				}
				if ( faceGauche1 != null) {
					faceGauche1.getArcsDirects().remove(arc1);
					arcTotal.setFaceGauche(faceGauche1);
				}
			}

			// conséquences sur le deuxième arc 			
			if ( noeudIni2 == noeud ) {
				noeudIni2.getSortants().remove(arc2);
				if ( noeudFin2 != null ) {
					noeudFin2.getEntrants().remove(arc2);
					noeudFin2.addEntrant(arcTotal);

				}
				if ( faceDroite2 != null) {
					faceDroite2.getArcsIndirects().remove(arc2);
				}
				if ( faceGauche2 != null) {
					faceGauche1.getArcsDirects().remove(arc2);
				}
			}
			else {
				noeudFin2.getEntrants().remove(arc2);
				if (noeudIni2 != null ) {
					noeudIni2.getSortants().remove(arc2);
					noeudIni2.addEntrant(arcTotal);
				}
				if ( faceDroite2 != null) {
					faceDroite2.getArcsIndirects().remove(arc2);
				}
				if ( faceGauche2 != null) {
					faceGauche2.getArcsDirects().remove(arc2);
				}
			}

			//Elimination des arcs et du noeud inutile
			this.getPopArcs().enleveElement(arc1);			
			this.getPopArcs().enleveElement(arc2);			
			noeudsElimines.add(noeud);
		}
		int i;
		for (i=0;i<noeudsElimines.size();i++){
			this.getPopNoeuds().enleveElement((Noeud)noeudsElimines.get(i));
		}
	}

	/** Filtre les arcs en double 
	 * (en double = même géométrie et même orientation).
	 * 
	 * Attention: les conséquences sur la topologie arcs/faces ne sont pas gérées.
	 */
	public void filtreArcsDoublons() {
		List arcs = this.getPopArcs().getElements();
		List arcsAEnlever = new ArrayList();
		for (int i=0; i<arcs.size();i++) {
			Arc arci = (Arc)arcs.get(i);
			if (arcsAEnlever.contains(arci)) continue;
			for (int j=i+1; j<arcs.size();j++) {
				Arc arcj = (Arc)arcs.get(j);
				if ( !arcj.getGeom().equals(arci.getGeom()) ) continue;
				if ( arcj.getOrientation() != arcj.getOrientation() ) continue;
				arcsAEnlever.add(arcj);
				Iterator itCor = arcj.getCorrespondants().iterator();
				while (itCor.hasNext()) {
					FT_Feature corresp = (FT_Feature) itCor.next();
					arci.addCorrespondant(corresp);
				}
				arcj.setCorrespondants(new ArrayList());
				arcj.setNoeudFin(null);
				arcj.setNoeudIni(null);
			}
		}
		this.getPopArcs().removeAll(arcsAEnlever); 
	}

	/** Transforme la carte topo pour la rendre planaire :
	 *  les arcs sont découpés à chaque intersection d'arcs, 
	 *  et un noeud est créé à chaque extrémité d'arc.
	 * 
	 *  NB: la topologie arcs/noeuds de la carte en sortie est instanciée.
	 * 
	 *  NB: les populations d'arcs et de noeuds sont indexées pendant la méthode,
	 *  si cela n'avait pas déjà été fait avant.
	 *  Les paramètres de ces index sont: 
	 *  20x20 cases pour les noeuds, ~50 arcs par case pour les arcs.
	 *  Si cela ne convient pas: instancier les topologies avant. 
	 * 
	 *  NB: les "correspondants" des arcs et noeuds suivent le découpage, 
	 *  de même que l'attribut orientation.
	 *  MAIS ATTENTION: 
	 *  - les liens vers des groupes ne suivent pas. 
	 *  - les attributs/liens particuliers (cas où les arcs proviennent 
	 *    d'une carteTopo spécialisée) ne suivent pas non plus
	 *  - la topologie des faces est détruite aussi 
	 * 
	 * @param tolerance
	 * Paramètre de tolérance sur la localisation des noeuds:
	 * deux extrémités d'arc à moins de cette distance sont considérées superposées
	 * (utilisé lors de la construction de la topologie arcs/noeuds).
	 * Ce paramètre peut être nul.
	 */ 
	public void rendPlanaire(double tolerance) {
		Arc arc, arcSel;
		FT_FeatureCollection selection;
		List listeInter;
		GM_Object nodedLineStrings, intersection  ;
		Iterator itSel, itDecoupes ;
		List dejaTraites = new ArrayList();
		List arcsEnleves = new ArrayList();
		GM_MultiPoint frontiereArc, frontiereArcSel  ;
		GM_Point ptArcIni, ptArcFin, ptArcSelIni, ptArcSelFin;

		if (this.getPopArcs().size() == 0) return;
		// initialisation de l'index des arcs au besoin		
		if ( ! this.getPopArcs().hasSpatialIndex() )
			this.getPopArcs().initSpatialIndex(Tiling.class, true);

		for(int i=0;i<this.getPopArcs().size();i++) {
			arc = (Arc)this.getPopArcs().get(i);
			if (arcsEnleves.contains(arc)) continue;
			if (dejaTraites.contains(arc)) continue;

			//les arcs qui croisent l'arc courant
			// Optimisation et blindage pour tous les cas non garanti (Seb)
			selection = this.getPopArcs().select(arc.getGeometrie()); 
			selection.remove(arc); 
			selection.getElements().removeAll(arcsEnleves);

			listeInter = new ArrayList();
			itSel = selection.getElements().iterator();
			ptArcIni = new GM_Point(arc.getGeometrie().startPoint());
			ptArcFin = new GM_Point(arc.getGeometrie().endPoint());
			frontiereArc = new GM_MultiPoint();
			frontiereArc.add(ptArcIni);
			frontiereArc.add(ptArcFin);
			while (itSel.hasNext()) {
				arcSel = (Arc) itSel.next();

//				if (arcSel == arc) continue;

				ptArcSelIni = new GM_Point(arcSel.getGeometrie().startPoint());
				ptArcSelFin = new GM_Point(arcSel.getGeometrie().endPoint());

				intersection = arcSel.getGeometrie().intersection(arc.getGeometrie());

				/* //modif Seb: tentative d'accélération : buggé mais je ne trouve pas pourquoi
				if (intersection instanceof GM_Point ) {
					if ( Operateurs.superposes(ptArcIni, (GM_Point)intersection) || 
						 Operateurs.superposes(ptArcFin, (GM_Point)intersection) ) { 		     
						if ( Operateurs.superposes(ptArcSelIni, (GM_Point)intersection) || 
							 Operateurs.superposes(ptArcSelFin, (GM_Point)intersection) )  		     
							continue;
					}
					listeInter.add(arcSel);
					continue;
				}
				 */				
				frontiereArcSel = new GM_MultiPoint();
				frontiereArcSel.add(ptArcSelIni);
				frontiereArcSel.add(ptArcSelFin);
				if ( frontiereArc.contains(intersection) ) { 
					if (frontiereArcSel.contains(intersection)) continue;
				}
				listeInter.add(arcSel); 
			}			

			if (listeInter.size() == 0) continue; //pas d'intersection avec cet arc

			//on découpe tout
			itSel = listeInter.iterator();
			nodedLineStrings = arc.getGeometrie();
			while (itSel.hasNext()) {
				arcSel = (Arc) itSel.next();
				nodedLineStrings = nodedLineStrings.union(arcSel.getGeometrie());
			}
			listeInter.add(arc); // on le rajoute pour la suite
			if (nodedLineStrings instanceof GM_LineString ) {
				System.out.println("Problème pour rendre le graphe planaire");
				System.out.println("  l'intersection de plusieurs arcs donne un seul arc (pb non résolu, pb JTS?)" );
				System.out.println("  pb sur l'arc "+arc.getGeom().coord() );
				continue;
			}
			if (nodedLineStrings instanceof GM_MultiCurve ) { //cas où il faut découper
				//1: on rajoute les morceaux d'arcs découpés
				itDecoupes = ((GM_MultiCurve)nodedLineStrings).getList().iterator();
				while (itDecoupes.hasNext()) {
					GM_LineString ligneDecoupe = (GM_LineString)itDecoupes.next();
					Arc arcNouveau = (Arc)this.getPopArcs().nouvelElement(ligneDecoupe);
					//on recherche à quel(s) arc(s) initial appartient chaque bout découpé
					itSel = listeInter.iterator();
					while (itSel.hasNext()) {
						arcSel = (Arc) itSel.next();
						// on devrait mettre ==0 ci-dessous, mais pour gérer les erreurs d'arrondi on met <0.01
						if ( Distances.premiereComposanteHausdorff(ligneDecoupe,arcSel.getGeometrie()) < 0.01 ) {
							//on appartient à lui
							arcNouveau.getCorrespondants().addAll(arcSel.getCorrespondants());
							arcNouveau.setOrientation(arcSel.getOrientation());
							//si on appartient à l'arc initial, pas la peine de revenir
							if (arcSel == arc) dejaTraites.add(arcNouveau);		
						}
					}
				}

				//2: on virera les arcs initiaux qui ont été découpés
				itSel = listeInter.iterator();
				while (itSel.hasNext()) {
					arcSel = (Arc) itSel.next();
					arcsEnleves.add(arcSel);
					arcSel.setCorrespondants(new ArrayList());
				}
				continue;
			}

			//cas imprévu: OUPS
			System.out.println("Problème pour rendre le graphe planaire");
			System.out.println("  bug non identifié : l'union donne une "+nodedLineStrings.getClass() );
			System.out.println("  pb sur l'arc "+arc.getGeom().coord() );
		}

		this.enleveArcs(arcsEnleves);
		//On construit les nouveaux noeuds éventuels et la topologie arcs/noeuds
		this.getPopNoeuds().setElements(new ArrayList());
		this.creeNoeudsManquants(tolerance);
	}


	/** Fusionne en un seul noeud, tous les noeuds proches de moins de "tolerance"
	 * Les correspondants suivent, la topologie arcs/noeuds aussi.
	 * NB: les petits arcs qui n'ont plus de sens sont aussi éliminés.
	 * Plus précisément ce sont ceux qui partent et arrivent sur un même
	 * nouveau noeud créé, et restant à moins de "tolerance" de ce nouveau noeud
	 * 
	 * Un index spatial (dallage) est créé si cela n'avait pas été fait avant, 
	 * mais il est toujours conseillé de le faire en dehors de cette méthode,
	 * pour controler la taille du dallage.
	 */
	public void fusionNoeuds(double tolerance) {
		Iterator itNoeudsProches, itArcs;
		Noeud noeud, nouveauNoeud, noeudProche;
		Arc arc;
		List aEnlever = new ArrayList();
		FT_FeatureCollection noeudsProches;
		List arcsModifies;

		if ( ! this.getPopArcs().hasSpatialIndex() )
			this.getPopArcs().initSpatialIndex(Tiling.class, true);

		for(int i=0;i<this.getPopNoeuds().size();i++) {
			noeud = (Noeud)this.getPopNoeuds().getElements().get(i);		
			//On cherche les noeuds voisins
			noeudsProches = this.getPopNoeuds().select(noeud.getGeometrie(), tolerance);
			noeudsProches.removeAll(aEnlever);
			if (noeudsProches.size() < 2) continue;

			//Si il y a des voisins, on crée un nouveau noeud
			GM_MultiPoint points = new GM_MultiPoint();
			itNoeudsProches = noeudsProches.getElements().iterator();
			while (itNoeudsProches.hasNext()) {
				noeudProche = (Noeud) itNoeudsProches.next();
				points.add(noeudProche.getGeometrie());				
			}
			GM_Point centroide = (GM_Point)points.centroid();
			nouveauNoeud = (Noeud)this.getPopNoeuds().nouvelElement();
			nouveauNoeud.setGeometrie(centroide);

			//On raccroche tous les arcs à ce nouveau noeud
			arcsModifies = new ArrayList();
			itNoeudsProches = noeudsProches.getElements().iterator();
			while (itNoeudsProches.hasNext()) {
				noeudProche = (Noeud) itNoeudsProches.next();
				nouveauNoeud.getCorrespondants().addAll(noeudProche.getCorrespondants());
				noeudProche.setCorrespondants(new ArrayList());
				aEnlever.add(noeudProche);	
				//modification de chaque arc du noeud proche à bouger
				itArcs = noeudProche.arcs().iterator();
				while (itArcs.hasNext()) {
					arc = (Arc)itArcs.next();
					arcsModifies.add(arc);
					if ( arc.getNoeudIni() == noeudProche ) {
						arc.setNoeudIni(nouveauNoeud);
						arc.getGeometrie().coord().set(0,nouveauNoeud.getGeometrie().getPosition());
					}
					if ( arc.getNoeudFin() == noeudProche ) {
						arc.setNoeudFin(nouveauNoeud);
						int fin = arc.getGeometrie().coord().size()-1;
						arc.getGeometrie().coord().set(fin,nouveauNoeud.getGeometrie().getPosition());
					}
				}
				//On enlève les arcs qui n'ont plus lieu d'être
				// (tout petit autour du nouveau noeud)
				itArcs = arcsModifies.iterator();
				while (itArcs.hasNext()) {
					arc = (Arc) itArcs.next();
					if ( arc.getNoeudIni() == nouveauNoeud && arc.getNoeudFin() == nouveauNoeud ) {
						if ( Distances.hausdorff(arc.getGeometrie(),noeudProche.getGeometrie()) <= tolerance ) {
							nouveauNoeud.getCorrespondants().addAll(arc.getCorrespondants());
							arc.setNoeudIni(null);
							arc.setNoeudFin(null);
							this.getPopArcs().remove(arc);
						}		
					}
				}
			}
		}
		//on enleve tous les anciens noeuds
		Iterator itAEnlever = aEnlever.iterator();
		while (itAEnlever.hasNext()) {
			noeud = (Noeud) itAEnlever.next();
			this.getPopNoeuds().remove(noeud);
		}
	}

	/** Fusionne en un seul noeud, tous les noeuds contenu dans une même
	 * surface de la population de surfaces passée en paramètre.
	 * 
	 * Les correspondants suivent, la topologie arcs/noeuds aussi.
	 * 
	 * NB: les petits arcs qui n'ont plus de sens sont aussi éliminés.
	 * Plus précisément ce sont ceux qui partent et arrivent sur un même
	 * nouveau noeud créé, et restant dans la surface de fusion.
	 * 
	 * Un index spatial (dallage) est créé si cela n'avait pas été fait avant, 
	 * mais il est toujours conseillé de le faire en dehors de cette méthode,
	 * pour controler la taille du dallage.
	 */
	public void fusionNoeuds(Population popSurfaces) {
		Iterator itNoeudsProches, itArcs;
		Noeud noeud, nouveauNoeud, noeudProche;
		Arc arc;
		List aEnlever = new ArrayList();
		FT_FeatureCollection noeudsProches;
		List arcsModifies;
		Iterator itSurf = popSurfaces.getElements().iterator();

		if ( ! this.getPopNoeuds().hasSpatialIndex() )
			this.getPopNoeuds().initSpatialIndex(Tiling.class, true);

		while (itSurf.hasNext()) {
			FT_Feature surf = (FT_Feature) itSurf.next();
			noeudsProches = this.getPopNoeuds().select(surf.getGeom());
			noeudsProches.removeAll(aEnlever);
			if (noeudsProches.size() < 2) continue;

			//Si il y a plusieurs noeuds dans la surface, on crée un nouveau noeud
			GM_MultiPoint points = new GM_MultiPoint();
			itNoeudsProches = noeudsProches.getElements().iterator();
			while (itNoeudsProches.hasNext()) {
				noeudProche = (Noeud) itNoeudsProches.next();
				points.add(noeudProche.getGeometrie());				
			}
			GM_Point centroide = (GM_Point)points.centroid();
			nouveauNoeud = (Noeud)this.getPopNoeuds().nouvelElement();
			nouveauNoeud.setGeometrie(centroide);

			//On raccroche tous les arcs à ce nouveau noeud
			arcsModifies = new ArrayList();
			itNoeudsProches = noeudsProches.getElements().iterator();
			while (itNoeudsProches.hasNext()) {
				noeudProche = (Noeud) itNoeudsProches.next();
				nouveauNoeud.getCorrespondants().addAll(noeudProche.getCorrespondants());
				noeudProche.setCorrespondants(new ArrayList());
				aEnlever.add(noeudProche);	
				//modification de chaque arc du noeud proche à bouger
				itArcs = noeudProche.arcs().iterator();
				while (itArcs.hasNext()) {
					arc = (Arc)itArcs.next();
					arcsModifies.add(arc);
					if ( arc.getNoeudIni() == noeudProche ) {
						arc.setNoeudIni(nouveauNoeud);
						arc.getGeometrie().coord().set(0,nouveauNoeud.getGeometrie().getPosition());
					}
					if ( arc.getNoeudFin() == noeudProche ) {
						arc.setNoeudFin(nouveauNoeud);
						int fin = arc.getGeometrie().coord().size()-1;
						arc.getGeometrie().coord().set(fin,nouveauNoeud.getGeometrie().getPosition());
					}
				}
				//On enlève les arcs qui n'ont plus lieu d'être
				// (tout petit autour du nouveau noeud)
				itArcs = arcsModifies.iterator();
				while (itArcs.hasNext()) {
					arc = (Arc) itArcs.next();
					if ( arc.getNoeudIni() == nouveauNoeud && arc.getNoeudFin() == nouveauNoeud ) {
						if ( surf.getGeom().contains(arc.getGeometrie()) ) {
							nouveauNoeud.getCorrespondants().addAll(arc.getCorrespondants());
							arc.setNoeudIni(null);
							arc.setNoeudFin(null);
							this.getPopArcs().remove(arc);
						}		
					}
				}
			}
		}
		//on enleve tous les anciens noeuds
		Iterator itAEnlever = aEnlever.iterator();
		while (itAEnlever.hasNext()) {
			noeud = (Noeud) itAEnlever.next();
			this.getPopNoeuds().remove(noeud);
		}
	}

	/** Découpe la carte topo this en fonction des noeuds d'une autre carte topo (ct).
	 * En détail: 
	 * Pour chaque noeud N de la carte topo en entrée, 
	 * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de distanceMaxNoeudArc). 
	 * Si aucune des extrémités de cet arc est à moins de distanceMaxProjectionNoeud du noeud N,
	 * alors on découpe l'arc en y projetant le noeud N.
	 * 
	 * Si impassesSeulement = true: seules les noeuds N extrémités d'impasse peuvent être projetées
	 * 
	 * La topologie arcs/noeuds, l'orientation et les correspondants suivent.  
	 * 
	 * Les arcs de this sont indexés au passage si cela n'avait pas été fait avant.
	 *
	 */
	public void projete(CarteTopo ct, double distanceMaxNoeudArc, double distanceMaxProjectionNoeud, boolean impassesSeulement) {
		Arc arc;
		Noeud noeud;
		Iterator itNoeuds = ct.getPopNoeuds().getElements().iterator();
		Iterator itArcs ;

		if ( !this.getPopArcs().hasSpatialIndex()) {
			int nb = (int)Math.sqrt(this.getPopArcs().size()/20);
			if (nb == 0) nb=1;
			this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
		}

		while (itNoeuds.hasNext()) {
			noeud = (Noeud) itNoeuds.next();
			if (impassesSeulement) {
				if ( noeud.arcs().size() != 1 ) continue;
			}
			itArcs = this.getPopArcs().select(noeud.getGeom(),distanceMaxNoeudArc).getElements().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				if ( Distances.distance(arc.getGeometrie().startPoint(), 
						noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud ) continue;
				if ( Distances.distance(arc.getGeometrie().endPoint(), 
						noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud ) continue;
				arc.projeteEtDecoupe(noeud.getGeometrie());						
			}
		}
	}

	/** Découpe la carte topo this en fonction de tous les points (noeuds et points intermediaires)
	 * d'une autre carte topo (ct).
	 * En détail: 
	 * Pour chaque point P de la carte topo en entrée, 
	 * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de distanceMaxNoeudArc). 
	 * Si aucune des extrémités de cet arc est à moins de distanceMaxProjectionNoeud du noeud P,
	 * alors on découpe l'arc en y projetant le noeud P.
	 * 
	 * La topologie arcs/noeuds, l'orientation et les correspondants suivent.  
	 * 
	 * Les arcs de this sont indexés au passage si cela n'avait pas été fait avant.
	 *
	 */
	public void projeteTousLesPoints(CarteTopo ct, double distanceMaxNoeudArc, double distanceMaxProjectionNoeud) {
		Arc arc, arcCT;
		Iterator itArcsCT = ct.getPopArcs().getElements().iterator();
		Iterator itArcs, itPointsCT ;

		if ( !this.getPopArcs().hasSpatialIndex()) {
			int nb = (int)Math.sqrt(this.getPopArcs().size()/20);
			if (nb == 0) nb=1;
			this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
		}

		while (itArcsCT.hasNext()) {
			arcCT = (Arc) itArcsCT.next();
			itPointsCT = arcCT.getGeometrie().coord().getList().iterator();
			while (itPointsCT.hasNext()) {
				DirectPosition dp = (DirectPosition) itPointsCT.next();
				if ( Distances.distance(arcCT.getGeometrie().startPoint(), dp) < distanceMaxProjectionNoeud ) continue;
				if ( Distances.distance(arcCT.getGeometrie().endPoint(), dp) < distanceMaxProjectionNoeud ) continue;
				itArcs = this.getPopArcs().select(dp,distanceMaxNoeudArc).getElements().iterator();
				while (itArcs.hasNext()) {
					arc = (Arc) itArcs.next();
					if ( Distances.distance(arc.getGeometrie().startPoint(), 
							dp) < distanceMaxProjectionNoeud ) continue;
					if ( Distances.distance(arc.getGeometrie().endPoint(), 
							dp) < distanceMaxProjectionNoeud ) continue;
					arc.projeteEtDecoupe(new GM_Point(dp));						
				}
			}
		}
	}

	/** Découpe la carte topo this en fonction d'un ensemble de points (GM_Point).
	 * En détail: 
	 * Pour chaque point en entrée, 
	 * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de distanceMaxNoeudArc). 
	 * Si aucune des extrémités de cet arc est à moins de distanceMaxProjectionNoeud du noeud N,
	 * alors on découpe l'arc en y projetant le noeud N.
	 * 
	 * La topologie arcs/noeuds, l'orientation et les correspondants suivent.  
	 *
	 */
	public void projete(List pts, double distanceMaxNoeudArc, double distanceMaxProjectionNoeud) {
		Arc arc;
		Iterator itPts = pts.iterator();
		Iterator itArcs ;
		while (itPts.hasNext()) {
			GM_Point point = (GM_Point) itPts.next();
			itArcs = this.getPopArcs().select(point,distanceMaxNoeudArc).getElements().iterator();
			while (itArcs.hasNext()) {
				arc = (Arc) itArcs.next();
				if ( Distances.distance(arc.getGeometrie().startPoint(), 
						point.getPosition()) < distanceMaxProjectionNoeud ) continue;
				if ( Distances.distance(arc.getGeometrie().endPoint(), 
						point.getPosition()) < distanceMaxProjectionNoeud ) continue;
				arc.projeteEtDecoupe(point);						
			}
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////
//	Instanciation de la topologie de faces
/////////////////////////////////////////////////////////////////////////////////////////////
	/** Crée les faces à partir d'un graphe planaire et instancie la topologie face / arcs. 
	 *  Une face est délimitée par un cycle minimal du graphe.
	 *
	 *  Le paramètre persistant spécifie si les faces créées, ainsi que la topologie, sont rendus persistants. 
	 *  Si oui, il faut appeler cette méthode dans une transaction ouverte.
	 *
	 *  NB1 : la topologie de réseau arcs/noeuds doit avoir été instanciée.
	 *  NB2 : une face "extérieure" est créée (sa géométrie entoure le "trou" de l'extérieur qu'est le réseau.
	 *        Donc, dans le cas d'une topologie complete arcs/faces, tous les arcs ont une face gauche 
	 *        et une face à droite.
	 *  NB3 : ATTENTION : en cas d'un réseau non connexe, une face extérieure différente est crée pour chaque 
	 *        partie connexe ! 
	 *  NB4 : Les culs de sac ont la même face à gauche et à droite, et la face "longe le cul de sac";
	 *        i.e. la géométrie de la face fait un aller/retour sur le cul-de-sac.
	 *  NB5 : Méthode en théorie conçue pour les graphes planaires uniquement (testée dans ce cadre uniquement).
	 *       La méthode est en théorie valable pour les graphes non planaires, mais les faces créées
	 *       seront étranges (on ne recrée pas les intersections manquantes, on les ignore).
	 *       Si il existe des arcs sans noeud initial ou final (topologie de réseau pas complete), 
	 *       alors ces arcs n'ont ni face à gauche, ni face à droite
	 */
	public void creeTopologieFaces() {
		List arcsDejaTraitesADroite = new ArrayList();
		List arcsDejaTraitesAGauche = new ArrayList();
		List cycle;
		List arcsDuCycle;
		List orientationsArcsDuCycle;
		Iterator itArcs, itArcsCycle, itOrientations;
		Arc arc, arcCycle;
		Face face;
		GM_Polygon geometrieDuCycle;
		boolean orientationOk = true;
		Population popFaces = this.getPopFaces();

		// Parcours de tous les arcs du graphe. Puis, pour chaque arc: 
		//    - recherche du cycle à droite et du cycle à gauche
		//    - creation des faces correspondantes
		//    - on note les arcs par lesquels on est déjà passé pour ne pas refaire le travail
		itArcs = this.getPopArcs().getElements().iterator();
		while(itArcs.hasNext()) {
			arc = (Arc)itArcs.next();
			// a droite
			if ( !arcsDejaTraitesADroite.contains(arc) ) {
				cycle = arc.cycleADroite();
				if ( cycle == null ) continue;
				arcsDuCycle = (List)cycle.get(0);
				orientationsArcsDuCycle = (List)cycle.get(1);
				geometrieDuCycle = (GM_Polygon)cycle.get(2);
				face = (Face)popFaces.nouvelElement();
				face.setGeometrie(geometrieDuCycle);
				//if ( persistant ) JeuDeDonnees.db.makePersistent(face);
				itArcsCycle = arcsDuCycle.iterator();
				itOrientations = orientationsArcsDuCycle.iterator();
				while (itArcsCycle.hasNext()) {
					arcCycle = (Arc)itArcsCycle.next();
					orientationOk = ((Boolean)itOrientations.next()).booleanValue();
					if ( orientationOk ) {
						arcCycle.setFaceDroite(face);
						arcsDejaTraitesADroite.add(arcCycle);
					}
					else {
						arcCycle.setFaceGauche(face);
						arcsDejaTraitesAGauche.add(arcCycle);
					}
				}
			}
			// a gauche
			if ( !arcsDejaTraitesAGauche.contains(arc) ) {
				cycle = arc.cycleAGauche();
				if ( cycle == null ) continue;
				arcsDuCycle = (List)cycle.get(0);
				orientationsArcsDuCycle = (List)cycle.get(1);
				geometrieDuCycle = (GM_Polygon)cycle.get(2);
				face = (Face)popFaces.nouvelElement();
				face.setGeometrie(geometrieDuCycle);
				//if ( persistant ) JeuDeDonnees.db.makePersistent(face);
				itArcsCycle = arcsDuCycle.iterator();
				itOrientations = orientationsArcsDuCycle.iterator();
				while (itArcsCycle.hasNext()) {
					arcCycle = (Arc)itArcsCycle.next();
					orientationOk = ((Boolean)itOrientations.next()).booleanValue();
					if ( orientationOk ) {
						arcCycle.setFaceGauche(face);
						arcsDejaTraitesAGauche.add(arcCycle);
					}
					else {
						arcCycle.setFaceDroite(face);
						arcsDejaTraitesADroite.add(arcCycle);
					}
				}
			}
		}
	}

	/** Détruit les relations topologique d'une face avec tous ses arcs entourants */
	public void videTopologieFace(Face face) {
		Iterator it = face.arcs().iterator();
		Arc arc;
		while ( it.hasNext() ) {
			arc = (Arc)it.next();
			arc.setFaceDroite(null);
			arc.setFaceGauche(null);
		}
	}

	/** Ajoute des arcs et des noeuds à la carteTopo this qui ne contient que des faces.
	 * Ces arcs sont les arcs entourant les faces.
	 * 
	 * Les relations topologiques arcs/noeuds/surfaces sont instanciées au passage.
	 * 
	 * Les trous sont gérés.
	 * Les faces en entrée peuvent avoir une orientation quelconque (direct), cela est géré.
	 * Par contre, on ne s'appuie que sur les points intermédiaires existants dans les polygones des faces :
	 * les relations topologiques sont donc bien gérés uniquement si les polygones ont des géométrie "compatibles".
	 * 
	 * @param filtrageNoeudsSimples
	 *   Si ce paramètre est égal à false, alors on crée un arc et deux noeuds 
	 *   pour chaque segment reliant des points intermédiares des surfaces.  
	 *   Si ce paramètre est égal à true, alors on fusionne les arcs et on ne retient  
	 *   que les noeuds qui ont 3 arcs incidents ou qui servent de point initial/final à une face. 
	 * 	
	 * @author  Mustière/Bonin
	 * 
	 * @date 09/05/2006
	 * 
	 */
	public void ajouteArcsEtNoeudsAuxFaces(boolean filtrageNoeudsSimples) {
		DirectPosition pt1, pt2;
		Iterator itPts ;
		boolean sensDirect;

		// On crée un arc pour chaque segment reliant deux points intermédiaires d'une surface
		// Pour deux faces adjacentes, on duplique ces arcs. On fait le ménage après.
		Iterator itFaces =  this.getPopFaces().getElements().iterator();
		while (itFaces.hasNext()) {
			Face face = (Face) itFaces.next();
			GM_Polygon geomFace = face.getGeometrie();
			//gestion du contour
			DirectPositionList ptsDeLaSurface = geomFace.exteriorCoord();
			sensDirect = Operateurs.sensDirect(ptsDeLaSurface);
			itPts = ptsDeLaSurface.getList().iterator();
			pt1 = (DirectPosition) itPts.next();
			while (itPts.hasNext()) {
				pt2 = (DirectPosition) itPts.next();
				Arc arc = (Arc)this.getPopArcs().nouvelElement();
				GM_LineString segment = new GM_LineString();
				segment.addControlPoint(pt1);
				segment.addControlPoint(pt2);
				arc.setGeom(segment);
				if (sensDirect) arc.setFaceGauche(face);
				else arc.setFaceDroite(face);
				pt1=pt2;
			}
			//gestion des trous
			Iterator itTrous = geomFace.getInterior().iterator();
			while (itTrous.hasNext()) {
				GM_Ring trou = (GM_Ring) itTrous.next();
				DirectPositionList geomTrou = trou.getPrimitive().coord();
				sensDirect = Operateurs.sensDirect(geomTrou);
				itPts = geomTrou.getList().iterator();
				pt1 = (DirectPosition) itPts.next();
				while (itPts.hasNext()) {
					pt2 = (DirectPosition) itPts.next();
					Arc arc = (Arc)this.getPopArcs().nouvelElement();
					GM_LineString segment = new GM_LineString();
					segment.addControlPoint(pt1);
					segment.addControlPoint(pt2);
					arc.setGeom(segment);
					if (sensDirect) arc.setFaceDroite(face);
					else arc.setFaceGauche(face);
					pt1=pt2;
				}
			}
		}

		//indexation spatiale des arcs crées
		//on crée un dallage avec en moyenne 20 objets par case
		FT_FeatureCollection arcsNonTraites = new FT_FeatureCollection(this.getPopArcs().getElements());
		int nb = (int)Math.sqrt(arcsNonTraites.size()/20);
		if (nb == 0) nb=1;
		arcsNonTraites.initSpatialIndex(Tiling.class, true, nb);

		// filtrage des arcs en double dus aux surfaces adjacentes
		List arcsAEnlever = new ArrayList();
		Iterator itArcs =  this.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			Arc arc = (Arc) itArcs.next();
			if ( !arcsNonTraites.contains(arc) ) continue;
			arcsNonTraites.remove(arc);
			FT_FeatureCollection arcsProches = arcsNonTraites.select(arc.getGeometrie().startPoint(),0);
			Iterator itArcsProches = arcsProches.getElements().iterator();
			while (itArcsProches.hasNext()) {
				Arc arc2 = (Arc) itArcsProches.next();
				if ( arc2.getGeometrie().startPoint().equals(arc.getGeometrie().startPoint(),0) 
						&& arc2.getGeometrie().endPoint().equals(arc.getGeometrie().endPoint(),0) ) {
					arcsAEnlever.add(arc2);
					arcsNonTraites.remove(arc2);
					if (arc2.getFaceDroite() != null) arc.setFaceDroite(arc2.getFaceDroite());
					if (arc2.getFaceGauche() != null) arc.setFaceGauche(arc2.getFaceGauche());
				}   
				if ( arc2.getGeometrie().startPoint().equals(arc.getGeometrie().endPoint(),0) 
						&& arc2.getGeometrie().endPoint().equals(arc.getGeometrie().startPoint(),0) ) {
					arcsAEnlever.add(arc2);
					arcsNonTraites.remove(arc2);
					if (arc2.getFaceDroite() != null) arc.setFaceGauche(arc2.getFaceDroite());
					if (arc2.getFaceGauche() != null) arc.setFaceDroite(arc2.getFaceGauche());
				}   
			}
		}
		this.getPopArcs().removeAll(arcsAEnlever);


		// ajout des noeuds et des relations topologiqes arc/noeud
		this.creeNoeudsManquants(0);

		// filtrage de tous les noeuds simples (degré=2)
		if (filtrageNoeudsSimples) this.filtreNoeudsSimples();
	}

/////////////////////////////////////////////////////////////////////////////////////////////
//	Pour les calculs de plus court chemin
/////////////////////////////////////////////////////////////////////////////////////////////

	/** Initialise le poids de chaque arc comme étant égal à sa longueur;
	 *  NB: utile uniquement aux plus courts chemins */
	public void initialisePoids() {
		Arc arc;
		Iterator itArcs = this.getPopArcs().getElements().iterator() ;
		while (itArcs.hasNext()) {
			arc = (Arc) itArcs.next();
			if ( arc.getGeometrie() == null) arc.setPoids(0);
			arc.setPoids(arc.longueur());
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////
//	IMPORT: remplissage de la carte topo à partir de Features
/////////////////////////////////////////////////////////////////////////////////////////////

	/** Charge en mémoire les élements de la classe 'nomClasseGeo'
	 * et remplit 'this' avec des correspondants de ces éléments.*/ 
	public void importClasseGeo(String nomClasseGeo) {
		Chargeur.importClasseGeo(nomClasseGeo, this);
	}   

	/** Remplit 'this' avec des correspondants des éléments de 'listeFeature'.*/ 
	public void importClasseGeo(FT_FeatureCollection listeFeatures) {
		Chargeur.importClasseGeo(listeFeatures, this);
	}
}