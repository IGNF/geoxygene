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
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * Arc d'un reseau à apparier.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0 
 * 
 */

public class ArcApp extends Arc {

    /** Evaluation du résultat de l'appariement sur l'arc. */
    private String resultatAppariement; 
    public String getResultatAppariement () {return resultatAppariement;}
    public void setResultatAppariement (String resultat) {resultatAppariement = resultat;}

    /** Liens qui référencent les objets auquel l'arc est apparié dans un autre réseau. */
    private List liens = new ArrayList();
    public List getLiens() {return liens;}
    public void setLiens(List liens) { this.liens = liens; }
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

	/** Recherche des noeuds en correspondance aux extrémités de l'arc,
	 * que ce soit en entree ou en sortie (au sens de la circulation, i.e. de l'attribut orientation)
     * 
     * Renvoie une liste de 4 listes:
     * 0: les noeuds en correspondance au début de l'arc, et en entree 
     * 1: les noeuds en correspondance au début de l'arc, et en sortie 
     * 2: les noeuds en correspondance à la fin de l'arc, et en entree 
     * 3: les noeuds en correspondance à la fin de l'arc, et en sortie 
     */
	public List noeudsEnCorrespondanceAuxExtremites(EnsembleDeLiens liensNoeuds, EnsembleDeLiens liensArcs) {
		List tousNoeuds;
		Iterator itTousGroupes;
		List noeudsDebutIn = new ArrayList(), noeudsDebutOut = new ArrayList();
		List noeudsFinIn = new ArrayList(), noeudsFinOut = new ArrayList();
		List resultat = new ArrayList();
		NoeudApp noeudRef;
		GroupeApp groupeComp;

		// traitement du noeud ini
		noeudRef = (NoeudApp)this.getNoeudIni();
		// noeud INI apparié avec un ou des noeuds
		tousNoeuds = noeudRef.noeudsCompEnCorrespondance(liensNoeuds);
		if ( this.getOrientation() != 1 ) noeudsDebutIn.addAll(tousNoeuds);
		if ( this.getOrientation() != -1 ) noeudsDebutOut.addAll(tousNoeuds);


		// noeud INI apparié avec un groupe
		itTousGroupes = noeudRef.groupesCompEnCorrespondance(liensNoeuds).iterator();
		while (itTousGroupes.hasNext()) {
			groupeComp = (GroupeApp) itTousGroupes.next();
			if ( this.getOrientation() != 1 ) noeudsDebutIn.addAll(groupeComp.noeudsEntree(noeudRef,liensArcs));
			if ( this.getOrientation() != -1 ) noeudsDebutOut.addAll(groupeComp.noeudsSortie(noeudRef,liensArcs));
		}

		// traitement du noeud fin
		noeudRef = (NoeudApp)this.getNoeudFin();
		// noeud FIN apparié avec un ou des noeuds
		tousNoeuds = noeudRef.noeudsCompEnCorrespondance(liensNoeuds);
		if ( this.getOrientation() != -1 ) noeudsFinIn.addAll(tousNoeuds);
		if ( this.getOrientation() != 1 ) noeudsFinOut.addAll(tousNoeuds);

		// noeud FIN apparié avec un groupe
		itTousGroupes = noeudRef.groupesCompEnCorrespondance(liensNoeuds).iterator();
		while (itTousGroupes.hasNext()) {
			groupeComp = (GroupeApp) itTousGroupes.next();
			if ( this.getOrientation() != -1 ) noeudsFinIn.addAll(groupeComp.noeudsEntree(noeudRef,liensArcs));
			if ( this.getOrientation() != 1 ) noeudsFinOut.addAll(groupeComp.noeudsSortie(noeudRef,liensArcs));
		}
		resultat.add(noeudsDebutIn);
		resultat.add(noeudsDebutOut);
		resultat.add(noeudsFinIn);
		resultat.add(noeudsFinOut);

		return resultat;
	}

    /** Arcs reliés à this par l'appariement passé en paramètre.
     * Cette liste ne contient pas de doublon.
     * La liste contient des Arc_Comp. */
    public List arcsCompEnCorrespondance(EnsembleDeLiens liens) {
        List liensOK;
        Collection arcs = new HashSet();
        LienReseaux lien;
        Iterator itLiensOK;
        
        liensOK = new ArrayList(this.getLiens());
        liensOK.retainAll(liens.getElements());
        itLiensOK = liensOK.iterator();
        while (itLiensOK.hasNext()) {
			lien = (LienReseaux) itLiensOK.next();
            arcs.addAll(lien.getArcs2());
        }
        return new ArrayList(arcs);
    }
    
    /** Arcs reliés à this par l'appariement passé en paramètre.
     * La liste contient des Arc_Ref. */
    public List arcsRefEnCorrespondance(EnsembleDeLiens liens) {
        List arcs = new ArrayList();
        List liensOK;
        LienReseaux lien;
        int i;
        
        liensOK = new ArrayList(this.getLiens());
        liensOK.retainAll(liens.getElements());
        for (i=0;i<liensOK.size();i++) {
            lien = (LienReseaux)liensOK.get(i); 
            arcs.addAll(lien.getArcs1());
        }
        return arcs;
    }

    /** Renvoie la liste des objets géo initaux reliés à un arc ref ou un noeud ref 
     * qui est  en correspondance avec this (un arc_comp) à travers liens,
     * soit directement, soit par l'intermédiaire d'un groupe.
     */
    public List objetsGeoRefEnCorrespondance(EnsembleDeLiens liens) {
        List objetsCtEnCorrespondance = new ArrayList();
        List objetsGeoEnCorrespondance = new ArrayList();
        List liensOK;
        LienReseaux lien;
        Iterator itGroupes, itLiens, itObjetsCT;
        
        // objets de reseauRef en correspondance directe avec this.
        liensOK = new ArrayList(this.getLiens());
        liensOK.retainAll(liens.getElements());
        itLiens = liensOK.iterator();
        while (itLiens.hasNext()) {
			lien = (LienReseaux) itLiens.next();
			objetsCtEnCorrespondance.addAll(lien.getArcs1());
			objetsCtEnCorrespondance.addAll(lien.getNoeuds1());
		}
        
        // objets de reseauRef en correspondance avec this à travers un groupe
        itGroupes = this.getListeGroupes().iterator();
        while (itGroupes.hasNext()) {
			GroupeApp groupe = (GroupeApp) itGroupes.next();
	        liensOK = new ArrayList(groupe.getLiens());
	        liensOK.retainAll(liens.getElements());
	        itLiens = liensOK.iterator();
	        while (itLiens.hasNext()) {
				lien = (LienReseaux) itLiens.next();
				objetsCtEnCorrespondance.addAll(lien.getArcs1());
				objetsCtEnCorrespondance.addAll(lien.getNoeuds1());
			}
		}

        // objets geo correspondants
        itObjetsCT = objetsCtEnCorrespondance.iterator();
        while (itObjetsCT.hasNext()) {
			FT_Feature objetCT = (FT_Feature) itObjetsCT.next();
			objetsGeoEnCorrespondance.addAll(objetCT.getCorrespondants());
		}
        
        return objetsGeoEnCorrespondance;
    }

    /** A un correspondant par l'appariement passé en paramètre ? */
    public boolean aUnCorrespondant(EnsembleDeLiens liens) {
        List liensOK = new ArrayList();
        liensOK = new ArrayList(this.getLiens());
        liensOK.retainAll(liens.getElements());
        if ( liensOK.size() != 0 ) return true;
        return false;
    }

    /** A un correspondant par l'appariement passé en paramètre, soit directement, 
     * soit par l'intermédiaire d'un groupe ? */
    public boolean aUnCorrespondantGeneralise(EnsembleDeLiens liens) {
    	if (this.aUnCorrespondant(liens)) return true;
    	Iterator itGroupes = this.getListeGroupes().iterator();
    	while (itGroupes.hasNext()) {
			GroupeApp groupe = (GroupeApp) itGroupes.next();
			List liensDuGroupe = groupe.getLiens(liens.getElements());
			if ( liensDuGroupe.size() != 0 ) return true;
		}
    	return false;
    }

    
	////////////////////////////////////////////////////
	// POUR MANIPULER LA TOPOLOGIE ET LA GEOMETRIE
	////////////////////////////////////////////////////

	/** L'arc est il une impasse ? */
    public boolean impasse() {
        return ( this.impasseDebut() || this.impasseFin() );
    }

    /** L'arc est il une impasse au début (noeud ini fond de l'impasse) ? */
    public boolean impasseDebut() {
        if ( (this.getNoeudIni().arcs().size()) == 1 ) return true;
        return false;
    }

    /** L'arc est il une impasse à la fin (noeud fin fond de l'impasse) ? */
    public boolean impasseFin() {
        if ( (this.getNoeudFin().arcs().size()) == 1 ) return true;
        return false;
    }
    
    /** L'arc est il une boucle (noeud ini = noeud fin) ? */
    public boolean boucle() {
        if ( this.getNoeudFin() == this.getNoeudIni() ) return true;
        return false;
    }

    /** L'arc est il une impasse au sein du groupe ? */
    public boolean impasse(GroupeApp groupe) {
        return ( this.impasseDebut(groupe) || this.impasseFin(groupe) );
    }
    
    /** L'arc est il une impasse au sein du groupe, au début (noeud ini fond de l'impasse) ? */
    public boolean impasseDebut(GroupeApp groupe) {
        List arcs = new ArrayList();
        arcs = new ArrayList(this.getNoeudIni().arcs());
        arcs.retainAll(groupe.getListeArcs());
        if ( arcs.size() == 1 ) return true;
        return false;
    }

    /** L'arc est il une impasse au sein du groupe, à la fin (noeud final au fond de l'impasse) ? */
    public boolean impasseFin(GroupeApp groupe) {
        List arcs = new ArrayList();
        arcs = new ArrayList(this.getNoeudFin().arcs());
        arcs.retainAll(groupe.getListeArcs());
        if ( arcs.size() == 1 ) return true;
        return false;
    }
    
    /** Première composante de la distance de Hausdorff de self vers l'arc.
	 * Version optimisée pour l'appariement: 
	 * si cette distance est supérieure à Dmax, alors renvoie Double.MAX_VALUE, 
	 * sans plus de précision.  
	 */
	public double premiereComposanteHausdorff (Arc arc, double dmax) {
		double dist , hausdorff = 0;
		Iterator itPts = this.getGeometrie().coord().getList().iterator();
		while (itPts.hasNext()) {
			DirectPosition pt = (DirectPosition)itPts.next();
			dist = Distances.distance(pt, arc.getGeometrie());
			if (dist > dmax) return Double.MAX_VALUE; 
			if (dist > hausdorff) hausdorff = dist;
		}
		return hausdorff;
	}

}