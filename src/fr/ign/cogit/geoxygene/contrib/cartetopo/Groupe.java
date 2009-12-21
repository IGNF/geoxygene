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

import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.Population;


/**
 * Classe des groupes de la carte topo.
 * Un groupe est une composition de noeuds, d'arcs et de faces.
 * 
 * English: a group is a set of nodes/arcs/faces of a topological map
 * @author  Mustière/Bonin
 * @version 1.0
 */

public class Groupe  extends ElementCarteTopo   {

	public Groupe() {}


	///////////////////////////////////////////////////
	// Pour les relations de composition :
	// - un groupe contient PLUSIEURS noeuds, arcs et faces
	// - un groupe appartient à UNE carte topo
	///////////////////////////////////////////////////

	/* Noeuds composants du groupe */
	private List<Noeud> listeNoeuds = new ArrayList<Noeud>();
	/** Renvoie la liste des noeuds de self*/
	public List<Noeud> getListeNoeuds() {return this.listeNoeuds;}
	/** définit la liste des noeuds de self*/
	public void setListeNoeuds(List<Noeud> liste) {this.listeNoeuds = liste;}
	/** Ajoute un noeud à self*/
	public void addNoeud(Noeud noeud) {
		if (noeud != null && !this.listeNoeuds.contains(noeud)) {
			this.listeNoeuds.add(noeud);
			if (!noeud.getListeGroupes().contains(this))
				noeud.addGroupe(this);
		}
	}
	/** Ajoute une liste de noeuds à self**/
	public void addAllNoeuds(List<Noeud> liste) {
		Iterator<Noeud> itObj = liste.iterator();
		while (itObj.hasNext()) {
			Noeud objet = itObj.next();
			this.addNoeud(objet);
		}
	}

	/* Arcs composants du groupe */
	private List<Arc> listeArcs = new ArrayList<Arc>();
	/** Renvoie la liste des arcs de self*/
	public List<Arc> getListeArcs() {return this.listeArcs;}
	/** définit la liste des arcs de self*/
	public void setListeArcs(List<Arc> liste) {this.listeArcs = liste;}
	/** Ajoute un arc de self*/
	public void addArc(Arc arc) {
		if (arc != null && !this.listeArcs.contains(arc)) {
			this.listeArcs.add(arc);
			if (!arc.getListeGroupes().contains(this))
				arc.addGroupe(this);
		}
	}
	/** Ajoute une liste d'arcs à self**/
	public void addAllArcs(List<Arc> liste) {
		for(Arc arc:liste)this.addArc(arc);
	}

	/* Faces composants du groupe */
	private List<Face> listeFaces = new ArrayList<Face>();
	/** Renvoie la liste des faces de self*/
	public List<Face> getListeFaces() {return this.listeFaces;}
	/** définit la liste des faces de self*/
	public void setListeFaces(List<Face> liste) {this.listeFaces = liste;}
	/** Ajoute une face à self*/
	public void addFace(Face face) {
		if (face != null && !this.listeFaces.contains(face)) {
			this.listeFaces.add(face);
			if (!face.getListeGroupes().contains(this))
				face.addGroupe(this);
		}
	}
	/** Ajoute une liste de faces à self**/
	public void addAllFaces(List<Face> liste) {
		Iterator<Face> itObj = liste.iterator();
		while (itObj.hasNext()) {
			Face objet = itObj.next();
			this.addFace(objet);
		}
	}

	///////////////////////////////////////////////////
	// Pour les relations topologiques dans une vision Groupe = Hyper Noeud
	///////////////////////////////////////////////////

	/** Arcs entrants dans le groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
	public List<Arc> getEntrants() {
		List<Arc> arcs = new ArrayList<Arc>();
		List<Arc> arcsDuNoeud = new ArrayList<Arc>();
		int i, j;

		for (i=0; i<this.getListeNoeuds().size();i++) {
			arcsDuNoeud = (this.getListeNoeuds().get(i)).getEntrants();
			for (j=0; j<arcsDuNoeud.size(); j++) {
				if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
			}
		}
		return arcs;
	}

	/** Arcs sortants du groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
	public List<Arc> getSortants() {
		List<Arc> arcs = new ArrayList<Arc>();
		List<Arc> arcsDuNoeud = new ArrayList<Arc>();
		int i, j;

		for (i=0; i<this.getListeNoeuds().size();i++) {
			arcsDuNoeud = (this.getListeNoeuds().get(i)).getSortants();
			for (j=0; j<arcsDuNoeud.size(); j++) {
				if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
			}
		}
		return arcs;
	}

	/** Arcs adjacents (entrants et sortants) de self (vision groupe = hyper-noeud).
	 * NB : si un arc est à la fois entrant et sortant (boucle), il est 2 fois dans la liste
	 */
	public List<Arc> getAdjacents() {
		List<Arc> arcs = new ArrayList<Arc>();
		arcs.addAll(this.getSortants());
		arcs.addAll(this.getEntrants());
		return arcs;
	}

	///////////////////////////////////////////////////
	// Pour les relations topologiques dans une vision Groupe = Hyper Noeud,
	// en tenant compte du sens de circulation
	///////////////////////////////////////////////////
	/** Arcs entrants dans le groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
	public List<Arc> entrantsOrientes() {
		List<Arc> arcs = new ArrayList<Arc>();
		List<Arc> arcsDuNoeud = new ArrayList<Arc>();
		int i, j;

		for (i=0; i<this.getListeNoeuds().size();i++) {
			arcsDuNoeud = (this.getListeNoeuds().get(i)).entrantsOrientes();
			for (j=0; j<arcsDuNoeud.size(); j++) {
				if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
			}
		}
		return arcs;
	}

	/** Arcs sortants du groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
	public List<Arc> sortantsOrientes() {
		List<Arc> arcs = new ArrayList<Arc>();
		List<Arc> arcsDuNoeud = new ArrayList<Arc>();
		int i, j;

		for (i=0; i<this.getListeNoeuds().size();i++) {
			arcsDuNoeud = (this.getListeNoeuds().get(i)).sortantsOrientes();
			for (j=0; j<arcsDuNoeud.size(); j++) {
				if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
			}
		}
		return arcs;
	}

	/** Arcs incidents à un noeuds, classés en tournant autour du noeud dans l'ordre trigonométrique,
	 *  et qualifiés d'entrants ou sortants, au sens de la géoémtrie 
	 *  (utile particulièrement à la gestion des boucles).
	 *
	 *  NB : renvoie une liste de liste:
	 *      Liste.get(0) = liste des arcs (de la classe 'Arc')
	 *      Liste.get(1) = liste des orientations de type Boolean,
	 *                    true = entrant, false = sortant)
	 *  NB : Classement effectué sur la direction donnée par le premier point de l'arc après le noeud.
	 *  NB : Le premier arc est celui dont la direction est la plus proche de l'axe des X, en tournant dans le sens trigo.
	 *  NB : Ce classement est recalculé en fonction de la géométrie à chaque appel de la méthode.
	 */
	public List<Object> arcsClasses() {
		List<Arc> arcsClasses = new ArrayList<Arc>();
		List<Boolean> arcsClassesOrientation = new ArrayList<Boolean>();
		List<Arc> arcsEntrants = new ArrayList<Arc>(this.getEntrants());
		List<Arc> arcsSortants = new ArrayList<Arc>(this.getSortants());
		List<Arc> arcs = new ArrayList<Arc>();
		List<Angle> angles = new ArrayList<Angle>();
		List<Boolean> orientations = new ArrayList<Boolean>();
		List<Object> resultat = new ArrayList<Object>();
		Arc arc;
		Angle angle;
		double angleMin, angleCourant;
		int imin;
		Iterator<Arc> itArcs;
		int i;

		// recherche de l'angle de départ de chaque arc sortant
		itArcs = arcsSortants.iterator();
		while ( itArcs.hasNext() ) {
			arc = itArcs.next();
			angle = new Angle(arc.getCoord().get(0),arc.getCoord().get(1));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(false));
		}
		// recherche de l'angle de départ de chaque arc entrant
		itArcs = arcsEntrants.iterator();
		while ( itArcs.hasNext() ) {
			arc = itArcs.next();
			angle = new Angle(arc.getCoord().get(arc.getCoord().size()-1),
					arc.getCoord().get(arc.getCoord().size()-2));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(true));
		}
		// classement des arcs
		while ( !(arcs.isEmpty()) ) {
			angleMin = (angles.get(0)).getValeur();
			imin = 0;
			for(i=1;i<arcs.size() ;i++) {
				angleCourant = (angles.get(i)).getValeur();
				if ( angleCourant < angleMin ) {
					angleMin = angleCourant;
					imin = i;
				}
			}
			arcsClasses.add(arcs.get(imin));
			arcsClassesOrientation.add(orientations.get(imin));
			arcs.remove(imin);
			angles.remove(imin);
			orientations.remove(imin);
		}
		//retour du résultat
		resultat.add(arcsClasses);
		resultat.add(arcsClassesOrientation);
		return resultat;
	}

	///////////////////////////////////////////////////
	// méthodes de base pour manipuler un groupe
	///////////////////////////////////////////////////
	/** Pour vider un groupe, et mettre à jour les liens des objets simples vers ce groupe.
	 * Vide mais ne détruit pas le groupe: i.e. ne l'enlève pas de la carte topo.
	 */
	public void vide() {
		for(Arc arc:this.getListeArcs()) arc.getListeGroupes().remove(this);
		Iterator<Noeud> itNoeuds = this.getListeNoeuds().iterator();
		while (itNoeuds.hasNext()) {
			Noeud noeud= itNoeuds.next();
			noeud.getListeGroupes().remove(this);
		}
		this.getListeArcs().clear();
		this.getListeNoeuds().clear();
	}

	/** Pour vider un groupe, mettre à jour les liens des objets simples vers ce groupe,
	 *  et l'enlever des populations auxquelles il appartient.
	 * NB: ce groupe n'est pas vraiment detruit, il n'est pas rendu null ;
	 * NB: rien n'est géré au niveau de la persistance eventuelle.
	 */
	public void videEtDetache() {
		vide();
		Population<?> groupes = this.getPopulation();
		if (groupes != null) groupes.remove(this);
	}

	/** Pour copier un groupe.
	 * NB 1 : on crée un nouveau groupe pointant
	 * vers les mêmes objets composants.
	 * NB 2 : ce groupe n'est PAS ajouté à la carteTopo
	 */
	public Groupe copie() {
		//Groupe copie = new Groupe();
		Groupe  copie = (Groupe)this.getPopulation().nouvelElement();
		copie.addAllArcs(this.getListeArcs());
		copie.addAllNoeuds(this.getListeNoeuds());
		copie.addAllFaces(this.getListeFaces());
		//copie.setPopulation(this.getPopulation());
		return copie;
	}

	///////////////////////////////////////////////////
	///////////////////////////////////////////////////
	// opérateurs de calculs sur les groupes
	///////////////////////////////////////////////////
	///////////////////////////////////////////////////

	/** Decompose un groupe en plusieurs groupes connexes, et vide le groupe self.
	 * La liste en sortie contient des Groupes.
	 * ATTENTION : LE GROUPE EN ENTREE EST VIDE AU COURS DE LA METHODE PUIS ENLEVE DE LA CARTE TOPO.
	 */
	public List<Groupe> decomposeConnexes() {
		List<Groupe> groupesConnexes = new ArrayList<Groupe>();
		Groupe groupeConnexe;
		Noeud amorce;
		Arc arc;
		int i;

		try {
			if ( this.getPopulation() == null ) {
				System.out.println("ATTENTION : le groupe "+this+" n'a pas de population associée");
				System.out.println("            Impossible de le décomposer en groupes connexes");
				return null;
			}
			if ( this.getCarteTopo() == null ) {
				System.out.println("ATTENTION : le groupe "+this+" ne fait pas partie d'une carte topo");
				System.out.println("            Impossible de le décomposer en groupes connexes");
				return null;
			}
			if ( this.getCarteTopo().getPopArcs() == null ) {
				System.out.println("ATTENTION : le groupe "+this+" fait partie d'une carte topo sans population d'arcs");
				System.out.println("            Impossible de le décomposer en groupes connexes");
				return null;
			}
			if ( this.getCarteTopo().getPopNoeuds() == null ) {
				System.out.println("ATTENTION : le groupe "+this+" fait partie d'une carte topo sans population de noeuds");
				System.out.println("            Impossible de le décomposer en groupes connexes");
				return null;
			}

			while (this.getListeNoeuds().size() != 0) {
				groupeConnexe = (Groupe)this.getPopulation().nouvelElement();
				groupesConnexes.add(groupeConnexe);
				// le premier noeud de la liste des noeuds, vidée au fur et à mesure, est l'amorce d'un nouveau groupe connexe
				amorce = this.getListeNoeuds().get(0);
				groupeConnexe.ajouteVoisins(amorce, this);  //nb: méthode récursive
				groupeConnexe.arcsDansGroupe(this); // recherche des arcs du groupe, situés entre 2 noeuds du goupe connexe
			}
			// vidage des arcs du groupe, pour faire propre (on a déjà vidé les noeuds au fur et à mesure)
			for (i=0; i<this.getListeArcs().size(); i++) {
				arc = this.getListeArcs().get(i);
				arc.getListeGroupes().remove(this);
			}
			this.getListeArcs().clear();
			this.getPopulation().enleveElement(this);

			return groupesConnexes;
		} catch (Exception e) {System.out.println("----- ERREUR dans décomposition en groupes connxes: ");
		System.out.println("Source possible : Nom de la classe des groupes pas ou mal renseigné dans la carte topo");
		return null;}
	}

	// Methode nécessaire à DecomposeConnexe
	// ajoute le noeud au groupe connexe, cherche ses voisins, puis l'enlève du goupe total
	private void ajouteVoisins(Noeud noeud, Groupe groupeTotal) {
		List<Noeud> noeudsVoisins = new ArrayList<Noeud>();
		int i;

		if ( this.getListeNoeuds().contains(noeud) ) return;
		this.addNoeud(noeud);
		noeud.addGroupe(this);
		noeudsVoisins = noeud.voisins(groupeTotal);
		groupeTotal.getListeNoeuds().remove(noeud);
		noeud.getListeGroupes().remove(groupeTotal);
		for (i=0; i<noeudsVoisins.size(); i++) {
			this.ajouteVoisins(noeudsVoisins.get(i), groupeTotal);
		}
		return;
	}

	// Methode nécessaire à DecomposeConnexe
	// Recherche les arcs de groupeTotal ayant pour extrémité des noeuds de this.
	private void arcsDansGroupe(Groupe groupeTotal) {
		int i;
		Arc arc;
		for (i=0; i<groupeTotal.getListeArcs().size(); i++) {
			arc = groupeTotal.getListeArcs().get(i);
			if ( this.getListeNoeuds().contains(arc.getNoeudIni()) || this.getListeNoeuds().contains(arc.getNoeudIni()) )  {
				this.addArc(arc);
				arc.addGroupe(this);
			}
		}
	}

	/** somme des longueurs des arcs du groupe. */
	public double longueur() {
		int i;
		double longueur = 0;
		for(i=0;i<this.getListeArcs().size() ;i++) {
			longueur = longueur + (this.getListeArcs().get(i)).longueur();
		}
		return longueur;
	}


	/** Teste si le groupe contient exactement les mêmes arcs qu'un autre groupe.
	 *  NB: si des arcs sont en double dans un des groupes et pas dans l'autre, renvoie true quand même
	 */
	public boolean contientMemesArcs(Groupe groupe) {
		if (!groupe.getListeArcs().containsAll(this.getListeArcs()) ) return false;
		if (!this.getListeArcs().containsAll(groupe.getListeArcs()) ) return false;
		return true;
	}

	/** Pour un groupe dont on ne connait que les arcs :
	 * ajoute les noeuds ini et fin de ses arcs dans le groupe.
	 * La topologie doit avoir été instanciée.
	 */
	public void ajouteNoeuds() {
		int i;
		Noeud ini, fin;
		Arc arc;

		for (i=0; i<this.getListeArcs().size(); i++) {
			arc = this.getListeArcs().get(i);
			ini = arc.getNoeudIni();
			fin = arc.getNoeudFin();
			if ( ini != null ) {
				if ( !this.getListeNoeuds().contains(ini) ) {
					this.addNoeud(ini);
					ini.addGroupe(this);
				}
			}
			if ( fin != null ) {
				if ( !this.getListeNoeuds().contains(fin) ) {
					this.addNoeud(fin);
					fin.addGroupe(this);
				}
			}
		}
	}

}