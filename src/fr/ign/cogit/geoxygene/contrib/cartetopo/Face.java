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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe des faces de la carte topo.
 * Les arcs ont pour géométrie un GM_Polygon.
 * @author  Mustière/Bonin
 * @version 1.0
 */

public class Face  extends ElementCarteTopo   {

    public Face() {
    }
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                                      Géométrie
/////////////////////////////////////////////////////////////////////////////////////////////////
    
	/** Renvoie le GM_Polygon qui définit la géométrie de self */
    public GM_Polygon getGeometrie() {return (GM_Polygon)this.geom;}
	/** Définit le GM_Polygon qui définit la géométrie de self */
    public void setGeometrie(GM_Polygon geometrie) {this.setGeom(geometrie);}
	/** Renvoie la liste de DirectPosition qui définit les coordonnées de self */
    public DirectPositionList getCoord() {return this.getGeometrie().exteriorCoord();}
    // On suppose que exteriorCoordList() donne deux fois le point de départ
	/** Définit la liste de DirectPosition qui définit les coordonnées de self */
    public void setCoord(DirectPositionList dpl) {geom = new GM_Polygon(new GM_LineString(dpl));}
    
                                            
                                            
/////////////////////////////////////////////////////////////////////////////////////////////////
//                              Gestion des groupes
/////////////////////////////////////////////////////////////////////////////////////////////////
    
    /* Groupes auquels self appartient */
    private Collection listeGroupes = new ArrayList();
	/** Renvoie la liste des groupes de self*/
    public Collection getListeGroupes() {return this.listeGroupes;}
	/** Définit la liste des groupes de self*/
    public void setListeGroupes(Collection liste) {this.listeGroupes = liste;}
	/** Ajoute un groupe à self*/ 
    public void addGroupe(Groupe groupe) {
    	if (groupe != null && !listeGroupes.contains(groupe)) {
            this.listeGroupes.add(groupe);	
            if (!groupe.getListeFaces().contains(this))
                    groupe.addFace(this);
    	}
    }

    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                         Gestion de la topologie arcs / faces
/////////////////////////////////////////////////////////////////////////////////////////////////
                                        
    private List arcsDirects = new ArrayList();
	/** Renvoie la liste des arcs directs de self */
    public List getArcsDirects() { return arcsDirects; }
	/** Ajoute un arc direct de self */
    public void addArcDirect (Arc arc) {
        if (arc != null && !arcsDirects.contains(arc)) {
            arcsDirects.add(arc);
            if (arc.getFaceGauche() != this)
                arc.setFaceGauche(this);
        }
    }
	/** Enlève un arc direct de self */
	public void enleveArcDirect (Arc arc) {
		if (arc == null) return;
		if (!arcsDirects.contains(arc)) return;
		arcsDirects.remove(arc);
		arc.setFaceGauche(null);
	}

    private List arcsIndirects = new ArrayList();
	/** Renvoie la liste des arcs indirects de self */
    public List getArcsIndirects() { return arcsIndirects; }
	/** Ajoute un arc indirect de self */
    public void addArcIndirect (Arc arc) {
        if (arc != null && !arcsIndirects.contains(arc)) {
            arcsIndirects.add(arc);
            if (arc.getFaceDroite() != this)
                arc.setFaceDroite(this);
        }
    }
	/** Enlève un arc indirect de self */
	public void enleveArcIndirect (Arc arc) {
		if (arc == null) return;
		if (!arcsIndirects.contains(arc)) return;
		arcsIndirects.remove(arc);
		arc.setFaceDroite(null);
	}

      /** Renvoie la liste (non classée) des arcs entourant self.
       *  NB: cette liste est la concaténation des listes des arcs directs et indirects. 
       *  Ce sont ces listes qui doivent être manipulées pour la modification/l'instanciation
       *  des relations topologiques sur les faces.
       *  NB2 codeur : A faire : coder une méthode qui renvoie ces arcs dans le bon ordre de parcours
       */                
    public List arcs() {
        List Arcs = new ArrayList();
        Arcs.addAll(this.getArcsDirects());
        Arcs.addAll(this.getArcsIndirects());
        return Arcs;
    }

	/** Liste de liste représentant les arcs incidents à une face 
	 * (i.e. les arcs des noeuds de la face, sauf les arcs de la face eux-mêmes).
	 * Dans l'esprit de la méthode arcsOrientés d'un noeud, les arcs sont
	 * classés en tournant autour de la face dans l'ordre trigonométrique,
	 * et qualifiés d'entrants ou sortants.
	 * 
	 *
	 * ATTENTION : renvoie une liste de liste:
	 *      Liste.get(0) = liste des arcs (de la classe 'Arc')
	 *      Liste.get(1) = liste des orientations de type Boolean (classe Boolean et non type boolean), 
	 *                    true = entrant, false = sortant)
	 * 
	 * NB : Le classement est recalculé en fonction de la géométrie à chaque appel de la méthode.
	 */
	public List arcsExterieursClasses() {
		List arcsDeLaFace = this.arcs();
		Iterator itNoeudsDeLaFace = this.noeudsTrigo().iterator();
		List arcsDuNoeud;
		List resultat = new ArrayList();
		List arcsExterieurs = new ArrayList();
		List orientations = new ArrayList();
		Iterator itArcs, itOrientations;
		Noeud noeud;
		Arc arc;
		Boolean orientation;
		
		while(itNoeudsDeLaFace.hasNext()){
			noeud = (Noeud)itNoeudsDeLaFace.next();
			arcsDuNoeud = noeud.arcsClasses();
			itArcs = ((List)arcsDuNoeud.get(0)).iterator();
			itOrientations = ((List)arcsDuNoeud.get(1)).iterator();;
			while(itArcs.hasNext()){
				arc = (Arc)itArcs.next();
				orientation = (Boolean)itOrientations.next();
				if ( arcsDeLaFace.contains(arc)) continue;
				arcsExterieurs.add(arc);
				orientations.add(orientation);
			}
		}
		resultat.add(arcsExterieurs);
		resultat.add(orientations);
		return resultat;
	}
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                              Topologie faces / noeuds
/////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Renvoie la liste des noeuds entourant self.
     *  NB: cette liste n'est pas modifiable directement. En effet, la topologie face/noeuds n'est pas gérée 
     *  directement, elle est déduite par calcul des topologies face/arcs et arcs/noeuds
     */
    public List noeuds() {
        List arcs = this.arcs();
        HashSet noeuds = new HashSet();
        Arc arc;
        Iterator iterarcs = arcs.iterator();
        while (iterarcs.hasNext()) {
            arc =(Arc) iterarcs.next();
            noeuds.addAll(arc.noeuds());
        }
        return new ArrayList(noeuds);
    }   
    
	/**Renvoie la liste des noeuds entourant self en parcourant la face 
	 * dans le sens trigonométrique. Le noeud de départ est choisi au hasard.
	 * NB : La topologie arcs/noeuds ET faces doit avoir été instanciée.
	 * NB : On ne boucle pas : le premier noeud n'est pas égal au dernier noeud 
	 * (contrairement aux géométries de polygone).
	 */
	public List noeudsTrigo() {
		List arcs;
		List cycle = new ArrayList();
		List noeuds = new ArrayList();
		Arc arc0, arc;
		Noeud noeud;
		boolean renverser = true, orientation;
		Iterator itArcsEntourants, itOrientations; 
		
		arcs = new ArrayList(this.arcs());
		if ( arcs.size() == 0 ) {
			System.out.println("Problème : gestion d'une face avec zéro arc entourant: ");
			System.out.println("           la topolgie de face a bien été instanciée?"); 
			return null;
		}
		
		arc0 = (Arc)arcs.get(0);
		if ( arc0.getFaceDroite() == this ) {
			cycle = arc0.cycleADroite();
			renverser = true;
		} 
		else if ( arc0.getFaceGauche() == this ) {
			cycle = arc0.cycleAGauche();
			renverser = false;
		} 
		else System.out.println("Problème : incohérence dans la topologie arcs / faces"); 
		
		itArcsEntourants = ((List)cycle.get(0)).iterator(); 
		itOrientations = ((List)cycle.get(1)).iterator();
		while (itArcsEntourants.hasNext() ) {
			arc = (Arc)itArcsEntourants.next();
			orientation = ((Boolean)itOrientations.next()).booleanValue();
			if (orientation) noeud = arc.getNoeudIni();
			else  noeud = arc.getNoeudFin();
			if (renverser) noeuds.add(0, noeud);
			else noeuds.add(noeud); 
		} 
		return noeuds;
	}

    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                         Topologie faces / faces
/////////////////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie la liste des faces voisines de self.
     *  NB: ceci est calculé en passant par la topologie faces/arcs qui doit être instanciée.
     */
    public List voisins() {
        List arcs = this.arcs();
        HashSet voisins = new HashSet();
        Arc arc;
        Iterator iterarcs = arcs.iterator();
        while (iterarcs.hasNext()) {
            arc =(Arc) iterarcs.next();
            voisins.addAll(arc.faces());
        }
        voisins.remove(this);
        return new ArrayList(voisins);
    }       

    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                      Opérateurs de calcul sur les faces 
/////////////////////////////////////////////////////////////////////////////////////////////////
    /**Surface d'un polygone. */
	// Le calcul est effectué dans un repère local centré sur le premier point 
	// de la surface, ce qui est utile pour minimiser les erreurs de calcul 
	// si on manipule de grandes coordonnées). 
    public double surface(){
    	return Operateurs.surface(this.getGeometrie());
	}
    
}