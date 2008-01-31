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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

/** 
 * Noeud du reseau à apparier.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0 
 * 
 */

public class NoeudApp extends Noeud {
    
    /** Rayon maximal sur le tarrain de l'objet correpondant au noeud
     * (rayon de recherche pour l'appariement). */
    private double taille = 0.0;
    public double getTaille() {return taille;}
    public void setTaille(double taille) {this.taille = taille;}    

    /** Evaluation du résultat de l'appariement sur la face. */
    private String resultatAppariement; 
    public String getResultatAppariement () {return resultatAppariement;}
    public void setResultatAppariement (String resultat) {resultatAppariement = resultat;}

    /** Liens qui référencent les objets auquel l'arc est apparié dans un autre réseau. */
    private List liens = new ArrayList();
    public List getLiens() {return liens;}
    public void setLiens(List liens) { this.liens=liens; }
    public void addLiens(LienReseaux liens) { this.liens.add(liens); }
    

	////////////////////////////////////////////////////
	// POUR MANIPULER LES LIENS
	////////////////////////////////////////////////////

    /** Renvoie les liens de l'objet qui appartiennent à la liste liensPertinents */
    public List getLiens(List liensPertinents) {
        List listeTmp = new ArrayList(this.getLiens());
        listeTmp.retainAll(liensPertinents);
        return listeTmp;
    }
    
    /** Renvoie les liens concernant l'objet et portant le nom passé en paramètre.
     *  NB: renvoie une liste vide (et non "Null") si il n'y a pas de tels liens. */
    public List retrouveLiens(String nom) {
        List liens = new ArrayList();
        List tousLiens = new ArrayList();

        tousLiens = this.getLiens();
        Iterator it = tousLiens.iterator();
        while (it.hasNext()) {
            LienReseaux lien = (LienReseaux)it.next();
            if (lien.getNom().compareToIgnoreCase(nom)==0) liens.add(lien);
        }
        return liens;
    }
    
	/** Noeuds reliés à this par l'appariement passé en paramètre.
	 * La liste contient des NoeudComp. */
	public List noeudsCompEnCorrespondance(EnsembleDeLiens liens) {
		List noeuds = new ArrayList();
		List liensOK = new ArrayList();
		LienReseaux lien;
		int i;

		liensOK = new ArrayList(this.getLiens());
		liensOK.retainAll(liens.getElements());
		for(i=0;i<liensOK.size() ;i++) {
			lien = (LienReseaux)liensOK.get(i);
			noeuds.addAll(lien.getNoeuds2());
		}
		return noeuds;
	}
    
	/** Groupes reliés à this par l'appariement passé en paramètre
	 * La liste contient des GroupeComp. */
	public List groupesCompEnCorrespondance(EnsembleDeLiens liens) {
		List groupes = new ArrayList();
		List liensOK = new ArrayList();
		LienReseaux lien;
		int i; 
        
		liensOK = new ArrayList(this.getLiens());
		liensOK.retainAll(liens.getElements());
		for (i=0;i<liensOK.size();i++) {
			lien = (LienReseaux)liensOK.get(i); 
			groupes.addAll(lien.getGroupes2());
		}
		return groupes;
	}


	///////////////////////////////////////////////////
	// DIVERS
	///////////////////////////////////////////////////
    
	/** Noeud d'un groupe le plus proche d'un noeud donné */
	public NoeudApp noeudLePlusProche(Groupe groupe) {
		NoeudApp noeud, noeudLePlusProche;
		Iterator itNoeuds = groupe.getListeNoeuds().iterator();
		double dist, distmin;
		if ( groupe.getListeNoeuds().size() == 0 ) return null;
		noeudLePlusProche = (NoeudApp)itNoeuds.next();
		distmin = this.distance(noeudLePlusProche);
		while (itNoeuds.hasNext()) {
			noeud = (NoeudApp)itNoeuds.next();
			dist = this.distance(noeud);
			if ( distmin > dist ) {
				distmin = dist;
				noeudLePlusProche = noeud;
			}
		}
		return noeudLePlusProche;
	}

	////////////////////////////////////////////////////
	// POUR ETUDIER LES CORRESPONDANCES DES ARCS
	// COEUR DE L'APPARIEMENT DES NOEUDS
	////////////////////////////////////////////////////
    
	/** Teste la correspondance des arcs de self avec les arcs entrants et sortants des noeuds 
	 * 
	 * @return 
	 * 1 si ca correspond bien
	 * 0 si ca correspond en partie seulement
	 * -1 si rien ne correspond du tout
	 */
	public int correspCommunicants(NoeudApp noeudcomp, EnsembleDeLiens liensPreappArcs) {
		List inRef, inComp, outRef, outComp;
		List arcsRef, arcsComp, arcsRefClasses, arcsCompClasses;
		List arcsRefClassesArcs, arcsRefClassesOrientations;
		List arcsCompClassesArcs, arcsCompClassesOrientations;
		int nbCorresp,i;
		ArcApp arc;
		boolean entrantGeom, inOut = false;
		
		// 1ers tests sur le nombre
		arcsRef = this.arcs();
		arcsComp = noeudcomp.arcs();

		// 1: est-ce que chaque arc ref a au moins un correspondant autour du noeud comp ?
		nbCorresp = nbArcsRefAvecCorrespondant(arcsRef, arcsComp, liensPreappArcs);
		if (nbCorresp == 0) return -1; 
		if (nbCorresp != arcsRef.size()) return 0; 
		
		// 2: est-ce que chaque arc ref a un correspondant pour lui tout seul ?
		// NB: 1er filtrage pour gérer les cas faciles plus vite, 
		//     mais ne gère pas bien tous les cas
		Iterator itArcsRef = arcsRef.iterator();
		Collection arcsCompCandidats = new HashSet();
		while (itArcsRef.hasNext()) {
			arc = (ArcApp) itArcsRef.next();
			arcsCompCandidats.addAll(arc.arcsCompEnCorrespondance(liensPreappArcs));
		} 
		arcsCompCandidats.retainAll(arcsComp);
		if ( arcsCompCandidats.size() < arcsRef.size() ) return 0;  
		
		// 3 : plus fin : est-ce qu'on trouve bien des correspondances 1-1 ?
		
		// On crée les listes d'arcs in et out (au sens de la circulation),
		// en tournant autour des noeuds dans le bon sens.
		inRef = new ArrayList();
		inComp = new ArrayList();
		outRef = new ArrayList();
		outComp = new ArrayList();

		arcsRefClasses = this.arcsClasses();
		arcsRefClassesArcs = (List)arcsRefClasses.get(0);  
		arcsRefClassesOrientations = (List)arcsRefClasses.get(1);  

		for (i=0; i<arcsRefClassesArcs.size(); i++) {
			arc = (ArcApp)arcsRefClassesArcs.get(i);
			entrantGeom = ((Boolean)arcsRefClassesOrientations.get(i)).booleanValue();
			if (entrantGeom) {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) inRef.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) outRef.add(arc);
			}
			else {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) outRef.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) inRef.add(arc);
			}
		}
		arcsCompClasses = noeudcomp.arcsClasses();
		arcsCompClassesArcs = (List)arcsCompClasses.get(0);  
		arcsCompClassesOrientations = (List)arcsCompClasses.get(1);  

		for (i=0; i<arcsCompClassesArcs.size(); i++) {
			arc = (ArcApp)arcsCompClassesArcs.get(i);
			entrantGeom = ((Boolean)arcsCompClassesOrientations.get(i)).booleanValue();
			if (entrantGeom) {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) inComp.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) outComp.add(arc);
			}
			else {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) outComp.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) inComp.add(arc);
			}
		}
		
		// c'est la même chose en in et out ?
		if (inRef.size() == outRef.size() && inRef.size() == arcsRef.size() ) inOut = true;

//		// on double les liste pour pouvoir tourner comme on veut
//		incomp.addAll(incomp);
//		if ( incomp.size() != 0 ) incomp.remove(incomp.size()-1);
//		outcomp.addAll(outcomp);
//		if ( outcomp.size() != 0 ) outcomp.remove(outcomp.size()-1);


		// on teste si chaque arc entrant a au moins un correspondant, 
		// sans compter le même correspondant deux fois,
		// et en respectant le sens de rotation autour des noeuds
		if (inRef.size() != 0 ) {
			if (!correspondantsArcsClasses(inRef, inComp, 0, liensPreappArcs)) return 0; 
		}
		
		// si tous les arcs sont entrants et sortant, on ne refait pas 2 fois la même chose
		if (inOut ) return 1;
		
		//sinon, on refait la même chose sur les sortants
		if (outRef.size() != 0   ){
			if (!correspondantsArcsClasses(outRef, outComp, 0, liensPreappArcs)) return 0;
		}
		return 1;
	}        
    
	/** Teste la correspondance des arcs de self avec les arcs entrants et sortants des groupes
     * EQUIVALENT DE LA METHODE SUR LES NOEUDS, D'UN POINT DE VUE GROUPE = HYPER-NOEUD.
	 * 
	 * @return 
	 * 1 si ca correspond bien
	 * 0 si ca correspond en partie seulement
	 * -1 si rien ne correspond du tout
	 */
	public int correspCommunicants(GroupeApp groupecomp, EnsembleDeLiens liensPreappArcs) {
		List inRef, inComp, outRef, outComp;
		List arcsRef, arcsComp, arcsRefClasses, arcsCompClasses;
		List arcsRefClassesArcs, arcsRefClassesOrientations;
		List arcsCompClassesArcs, arcsCompClassesOrientations;
		int nbCorresp,i;
		Arc arc;
		boolean entrantGeom, inOut = false;
		
		// 1ers tests sur le nombre
		arcsRef = this.arcs();
		arcsComp = groupecomp.getAdjacents();
		nbCorresp = nbArcsRefAvecCorrespondant(arcsRef, arcsComp, liensPreappArcs);
		if (nbCorresp == 0) return -1;
		if (nbCorresp != arcsRef.size()) return 0;
		
		// On crée les listes d'arcs in et out (au sens de la circulation),
		// en tournant autour des noeuds dans le bon sens.
		inRef = new ArrayList();
		inComp = new ArrayList();
		outRef = new ArrayList();
		outComp = new ArrayList();

		arcsRefClasses = this.arcsClasses();
		arcsRefClassesArcs = (List)arcsRefClasses.get(0);  
		arcsRefClassesOrientations = (List)arcsRefClasses.get(1);  

		for (i=0; i<arcsRefClassesArcs.size(); i++) {
			arc = (Arc)arcsRefClassesArcs.get(i);
			entrantGeom = ((Boolean)arcsRefClassesOrientations.get(i)).booleanValue();
			if (entrantGeom) {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) inRef.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) outRef.add(arc);
			}
			else {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) outRef.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) inRef.add(arc);
			}
		}

		arcsCompClasses = groupecomp.arcsClasses();
		arcsCompClassesArcs = (List)arcsCompClasses.get(0);  
		arcsCompClassesOrientations = (List)arcsCompClasses.get(1);  

		for (i=0; i<arcsCompClassesArcs.size(); i++) {
			arc = (Arc)arcsCompClassesArcs.get(i);
			entrantGeom = ((Boolean)arcsCompClassesOrientations.get(i)).booleanValue();
			if (entrantGeom) {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) inComp.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) outComp.add(arc);
			}
			else {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) outComp.add(arc); 
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) inComp.add(arc);
			}
		}
		
		// c'est la même chose en in et out ?
		if (inRef.size() == outRef.size() && inRef.size() == arcsRef.size() ) inOut = true;

		// on teste si chaque arc entrant a au moins un correspondant, sans compter le même correspondant deux fois
		if (inRef.size() != 0 ) {
			if ( !correspondantsArcsClasses(inRef, inComp, 0, liensPreappArcs) ) return 0;
		}
		
		// si tous les arcs sont entrants et sortant, on ne refait pas 2 fois la même chose
		if (inOut ) return 1;
		
		//sinon, on refait la même chose sur les sortants
		if (outRef.size() != 0   ){
			if ( !correspondantsArcsClasses(outRef, outComp, 0, liensPreappArcs) ) return 0;
		}
		return 1;
	}        


    /** Methode utile à correspCommunicants (pour les noeuds et les groupes)
     * Renvoie le nb d'éléments de ref ayant au moins un correspondant dans comp par liens
     */
    private int nbArcsRefAvecCorrespondant(List ref, List comp, EnsembleDeLiens liens) {
        int nb = 0;
        List corresp;
        ArcApp arcRef;
		Iterator itRef = ref.iterator();
		while (itRef.hasNext()) {
			arcRef= (ArcApp) itRef.next();
            corresp = arcRef.arcsCompEnCorrespondance(liens);
            corresp.retainAll(comp);
            if ( corresp.size() != 0 ) nb = nb+1;
        }
        return nb;
    }
    
    
	/** Methode utile à correspCommunicants (pour les noeuds et les groupes)
	 * renvoie OK quand tout est bon
	 * @param ref : les arcs du noeud ref qui n'ont pas encore de correspondant
	 * @param comp : les arcs du noeud comp qui n'ont pas encore de correspondant
	 * @param rangRef : rang de l'arc ref en cours de traitement
	 */
	private boolean correspondantsArcsClasses(List ref, List comp, int rangRef, EnsembleDeLiens liens) {

		ArcApp arcRef, arcComp;
		List liensArcRef, arcsCompCandidats, compPourProchain;
		boolean OK;

		// si on n'a plus d'arc à traiter, c'est gagné
		if (rangRef == ref.size() ) return true;

		
		arcRef = (ArcApp)ref.get(rangRef); // arc en cours de traitement

		// on cherche les candidats à l'appariement de arcRef
		liensArcRef = new ArrayList(arcRef.getLiens(liens.getElements()));
		arcsCompCandidats = new ArrayList();
		for (int i=0; i<liensArcRef.size(); i++) arcsCompCandidats.addAll( ((LienReseaux)liensArcRef.get(i)).getArcs2() );
		arcsCompCandidats.retainAll(comp); 
		
		// si la liste des candidats est vide, c'est foutu, il faut revenir en arrière
		if ( arcsCompCandidats.size() == 0 ) return false; 
		
		// on teste toutes les combinaisons de correspondance possibles		
		for (int i=0; i<comp.size(); i++) {
			arcComp = (ArcApp)comp.get(i);
			if ( !arcsCompCandidats.contains(arcComp) )continue; // cet arc n'est pas candidat, on essaye avec le suivant
			
			//on a un candidat sous la main
			compPourProchain = new ArrayList();
			if (rangRef == 0) {
				for(int j=i+1;j<comp.size();j++) compPourProchain.add(comp.get(j)); 
				for(int j=0;j<i;j++) compPourProchain.add(comp.get(j)); 
			}
			else {
				for(int j=i+1;j<comp.size();j++) compPourProchain.add(comp.get(j));
			}
			if ( compPourProchain.size() < ref.size()-rangRef-1) continue;
			OK = correspondantsArcsClasses(ref, compPourProchain, rangRef+1, liens);
			if ( OK ) return true; // une correspondance possible : on continue
		}
		return false; // aucune correspondance possible : on remonte d'un cran
	}

}