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

import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.Population;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;


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
    private List listenoeuds = new ArrayList();
	/** Renvoie la liste des noeuds de self*/
    public List getListenoeuds() {return this.listenoeuds;}
	/** Définit la liste des noeuds de self*/
    public void setListenoeuds(List liste) {this.listenoeuds = liste;}
	/** Ajoute un noeud à self*/ 
    public void addNoeud(Noeud noeud) {
    	if (noeud != null && !listenoeuds.contains(noeud)) {
            this.listenoeuds.add(noeud);	
            if (!noeud.getListegroupes().contains(this))
                    noeud.addGroupe(this);
    	}
    }
	public void addAllNoeuds(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Noeud objet = (Noeud) itObj.next();
			this.addNoeud(objet);
		}
	}
    
    /* Arcs composants du groupe */
    private List listearcs = new ArrayList();
	/** Renvoie la liste des arcs de self*/
    public List getListearcs() {return this.listearcs;}
	/** Définit la liste des arcs de self*/
    public void setListearcs(List liste) {this.listearcs = liste;}
	/** Ajoute un arc de self*/
    public void addArc(Arc arc) {
        if (arc != null && !listearcs.contains(arc)) {
            this.listearcs.add(arc); 	
            if (!arc.getListegroupes().contains(this))
                    arc.addGroupe(this);
    	}
    }
	public void addAllArcs(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Arc objet = (Arc) itObj.next();
			this.addArc(objet);
		}
	}

    /* Faces composants du groupe */
    private List listefaces = new ArrayList();
	/** Renvoie la liste des faces de self*/
    public List getListefaces() {return this.listefaces;}
	/** Définit la liste des faces de self*/
    public void setListefaces(List liste) {this.listefaces = liste;}
	/** Ajoute une face à self*/
    public void addFace(Face face) {
        if (face != null && !listefaces.contains(face)) {
            this.listefaces.add(face);	
            if (!face.getListegroupes().contains(this))
                    face.addGroupe(this);
        }
    }
	public void addAllFaces(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Face objet = (Face) itObj.next();
			this.addFace(objet);
		}
	}


    
    ///////////////////////////////////////////////////
    // Pour les relations topologiques dans une vision Groupe = Hyper Noeud
    ///////////////////////////////////////////////////

    /** Arcs entrants dans le groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
    public List getEntrants() {
        List arcs = new ArrayList();
        List arcs_du_noeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListenoeuds().size();i++) {
            arcs_du_noeud = ((Noeud)this.getListenoeuds().get(i)).getEntrants();
            for (j=0; j<arcs_du_noeud.size(); j++) {
                if (!this.getListearcs().contains(arcs_du_noeud.get(j))) arcs.add(arcs_du_noeud.get(j));
            }
        }
        return arcs;
    }

    /** Arcs sortants du groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
    public List getSortants() {
        List arcs = new ArrayList();
        List arcs_du_noeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListenoeuds().size();i++) {
            arcs_du_noeud = ((Noeud)this.getListenoeuds().get(i)).getSortants();
            for (j=0; j<arcs_du_noeud.size(); j++) {
                if (!this.getListearcs().contains(arcs_du_noeud.get(j))) arcs.add(arcs_du_noeud.get(j));
            }
        }
        return arcs;
    }
    
    /** Arcs adjacents (entrants et sortants) de self (vision groupe = hyper-noeud). 
     * NB : si un arc est à la fois entrant et sortant (boucle), il est 2 fois dans la liste
     */    
    public List getAdjacents() {
        List arcs = new ArrayList();
        arcs.addAll(this.getSortants());
        arcs.addAll(this.getEntrants());
        return arcs;        
    }

    ///////////////////////////////////////////////////
    // Pour les relations topologiques dans une vision Groupe = Hyper Noeud,
    // en tenant compte du sens de circulation
    ///////////////////////////////////////////////////
    /** Arcs entrants dans le groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
    public List entrantsOrientes() {
        List arcs = new ArrayList();
        List arcs_du_noeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListenoeuds().size();i++) {
            arcs_du_noeud = ((Noeud)this.getListenoeuds().get(i)).entrantsOrientes();
            for (j=0; j<arcs_du_noeud.size(); j++) {
                if (!this.getListearcs().contains(arcs_du_noeud.get(j))) arcs.add(arcs_du_noeud.get(j));
            }
        }
        return arcs;
    }

    /** Arcs sortants du groupe, au sens de la géométrie (vision groupe = hyper-noeud) */
    public List sortantsOrientes() {
        List arcs = new ArrayList();
        List arcs_du_noeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListenoeuds().size();i++) {
            arcs_du_noeud = ((Noeud)this.getListenoeuds().get(i)).sortantsOrientes();
            for (j=0; j<arcs_du_noeud.size(); j++) {
                if (!this.getListearcs().contains(arcs_du_noeud.get(j))) arcs.add(arcs_du_noeud.get(j));
            }
        }
        return arcs;
    }
    
	/** Arcs incidents à un noeuds, classés en tournant autour du noeud dans l'ordre trigonométrique,
	 *  et qualifiés d'entrants ou sortants, au sens de la géoémtrie (utile particulièrement à la gestion des boucles).
	 *
	 *  NB : renvoie une liste de liste:
	 *      Liste.get(0) = liste des arcs (de la classe 'Arc')
	 *      Liste.get(1) = liste des orientations de type Boolean, 
	 *                    true = entrant, false = sortant)
	 *  NB : Classement effectué sur la direction donnée par le premier point de l'arc après le noeud.
	 *  NB : Le premier arc est celui dont la direction est la plus proche de l'axe des X, en tournant dans le sens trigo.
	 *  NB : Ce classement est recalculé en fonction de la géométrie à chaque appel de la méthode.
	 */
	public List arcsClasses() {
		List arcsclasses = new ArrayList();
		List arcsclasses_orientation = new ArrayList();
		List arcs_entrants = new ArrayList(this.getEntrants());
		List arcs_sortants = new ArrayList(this.getSortants());
		List arcs = new ArrayList();
		List angles = new ArrayList();
		List orientations = new ArrayList();
		List resultat = new ArrayList();
		Arc arc;
		Angle angle;
		double anglemin, anglecourant;
		int imin;
		Iterator itArcs;
		int i;

		// recherche de l'angle de départ de chaque arc sortant
		itArcs = arcs_sortants.iterator();
		while ( itArcs.hasNext() ) {
			arc = (Arc)itArcs.next();
			angle = new Angle((DirectPosition)arc.getCoord().get(0),(DirectPosition)arc.getCoord().get(1));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(false));
		}
		// recherche de l'angle de départ de chaque arc entrant
		itArcs = arcs_entrants.iterator();
		while ( itArcs.hasNext() ) {
			arc = (Arc)itArcs.next();
			angle = new Angle((DirectPosition)arc.getCoord().get(arc.getCoord().size()-1),
							  (DirectPosition)arc.getCoord().get(arc.getCoord().size()-2));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(true));
		}
		// classement des arcs 
		while ( !(arcs.isEmpty()) ) {
			anglemin = ((Angle)angles.get(0)).getAngle();
			imin = 0;
			for(i=1;i<arcs.size() ;i++) {
				anglecourant = ((Angle)angles.get(i)).getAngle();
				if ( anglecourant < anglemin ) {
					anglemin = anglecourant;
					imin = i;
				}
			}
			arcsclasses.add(arcs.get(imin));
			arcsclasses_orientation.add(orientations.get(imin));
			arcs.remove(imin);
			angles.remove(imin);
			orientations.remove(imin);
		}
		//retour du résultat
		resultat.add(arcsclasses);
		resultat.add(arcsclasses_orientation);
		return resultat;
	}
    
    ///////////////////////////////////////////////////
    // Méthodes de base pour manipuler un groupe
    ///////////////////////////////////////////////////
    /** Pour vider un groupe, et mettre à jour les liens des objets simples vers ce groupe.
     * Vide mais ne détruit pas le groupe: i.e. ne l'enlève pas de la carte topo.
     */
    public void vide() {
		Iterator itArcs = this.getListearcs().iterator();
		while (itArcs.hasNext()) {
			Arc arc = (Arc) itArcs.next();
			arc.getListegroupes().remove(this);
		}  
		Iterator itNoeuds = this.getListenoeuds().iterator();
		while (itNoeuds.hasNext()) {
			Noeud noeud= (Noeud) itNoeuds.next();
			noeud.getListegroupes().remove(this);
		}  
        this.getListearcs().clear();
        this.getListenoeuds().clear();
    }

	/** Pour vider un groupe, mettre à jour les liens des objets simples vers ce groupe,
	 *  et l'enlever des populations auxquelles il appartient.
	 * NB: ce groupe n'est pas vraiment detruit, il n'est pas rendu null ;
	 * NB: rien n'est géré au niveau de la persistance eventuelle.
	 */
	public void videEtDetache() {
		vide();
		Population groupes = this.getPopulation();
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
		copie.addAllArcs(this.getListearcs());
		copie.addAllNoeuds(this.getListenoeuds());
		copie.addAllFaces(this.getListefaces());
		//copie.setPopulation(this.getPopulation());
		return copie;
	}
    
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    // Opérateurs de calculs sur les groupes
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////

    /** Decompose un groupe en plusieurs groupes connexes, et vide le groupe self. 
      * La liste en sortie contient des Groupes.
      * ATTENTION : LE GROUPE EN ENTREE EST VIDE AU COURS DE LA METHODE PUIS ENLEVE DE LA CARTE TOPO. 
      */
    public List decomposeConnexes() {
        List groupes_connexes = new ArrayList();
        Groupe groupe_connexe;
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
            
            while (this.getListenoeuds().size() != 0) {
                groupe_connexe = (Groupe)this.getPopulation().nouvelElement();
                groupes_connexes.add(groupe_connexe);
                // le premier noeud de la liste des noeuds, vidée au fur et à mesure, est l'amorce d'un nouveau groupe connexe
                amorce = (Noeud)this.getListenoeuds().get(0); 
                groupe_connexe.ajoute_voisins(amorce, this);  //nb: méthode récursive
                groupe_connexe.arcs_dans_groupe(this); // recherche des arcs du groupe, situés entre 2 noeuds du goupe connexe
            }
            // vidage des arcs du groupe, pour faire propre (on a déjà vidé les noeuds au fur et à mesure)
            for (i=0; i<this.getListearcs().size(); i++) {
                arc = (Arc)this.getListearcs().get(i);
                arc.getListegroupes().remove(this);
            }
            this.getListearcs().clear();
            this.getPopulation().enleveElement(this);

            return groupes_connexes;
        } catch (Exception e) {System.out.println("----- ERREUR dans décomposition en groupes connxes: ");
                               System.out.println("Source possible : Nom de la classe des groupes pas ou mal renseigné dans la carte topo");
                               return null;}
    }

        // Methode nécessaire à Decompose_Connexe 
        // ajoute le noeud au groupe connexe, cherche ses voisins, puis l'enlève du goupe total
        private void ajoute_voisins(Noeud noeud,
                                    Groupe groupe_total) {
            List noeuds_voisins = new ArrayList();
            int i;

            if ( this.getListenoeuds().contains(noeud) ) return;
            this.addNoeud(noeud);
            noeud.addGroupe(this);
            noeuds_voisins = noeud.voisins(groupe_total);
            groupe_total.getListenoeuds().remove(noeud);
            noeud.getListegroupes().remove(groupe_total);
            for (i=0; i<noeuds_voisins.size(); i++) {
                this.ajoute_voisins((Noeud)noeuds_voisins.get(i), groupe_total);
            }
            return;
        }

        // Methode nécessaire à Decompose_Connexe 
        // Recherche les arcs de groupe_total ayant pour extrémité des noeuds de this.
        private void arcs_dans_groupe(Groupe groupe_total) {
            int i;
            Arc arc;
            for (i=0; i<groupe_total.getListearcs().size(); i++) {
                arc = (Arc)groupe_total.getListearcs().get(i);
                if ( this.getListenoeuds().contains(arc.getNoeudini()) || this.getListenoeuds().contains(arc.getNoeudini()) )  {
                        this.addArc(arc);
                        arc.addGroupe(this);
                }
            }
        }

	/** somme des longueurs des arcs du groupe. */
	public double longueur() {
		int i;
		double longueur = 0;
		for(i=0;i<this.getListearcs().size() ;i++) {
			longueur = longueur + ((Arc)this.getListearcs().get(i)).longueur();
		}
		return longueur;
	}
    

	/** Teste si le groupe contient exactement les mêmes arcs qu'un autre groupe.
	 *  NB: si des arcs sont en double dans un des groupes et pas dans l'autre, renvoie true quand même
	 */
	public boolean contient_memes_arcs(Groupe groupe) {
		if (!groupe.getListearcs().containsAll(this.getListearcs()) ) return false;
		if (!this.getListearcs().containsAll(groupe.getListearcs()) ) return false;
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
        
		for (i=0; i<this.getListearcs().size(); i++) {
			arc = (Arc)this.getListearcs().get(i);
			ini = arc.getNoeudini();
			fin = arc.getNoeudfin();
			if ( ini != null ) {
				if ( !this.getListenoeuds().contains(ini) ) {
					this.addNoeud(ini);
					ini.addGroupe(this);
				}
			}
			if ( fin != null ) {
				if ( !this.getListenoeuds().contains(fin) ) {
					this.addNoeud(fin);
					fin.addGroupe(this);
				}
			}
		}
	}
    

	


}
