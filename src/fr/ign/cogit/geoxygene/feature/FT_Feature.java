/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.Population;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;


/** 
 * Classe mère pour toute classe d'éléments ayant une réalité géographique.
 * Par défaut, porte une géométrie et une topologie, qui peuvent être nulles.
 * 
 * <P> A REVOIR plus tard : ne plus porter de geometrie ni de topologie par defaut,
 *  et permettre le choix du nom de l'attribut portant geometrie et topologie.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public abstract class FT_Feature implements Cloneable {
	
	
	protected int id;
	/** Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que pour les objets persistants */
	public int getId() {return id;}
	/** Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela est automatique) */
	public void setId (int Id) {id = Id;}
	
	
	protected GM_Object geom = null;
	/** Renvoie une geometrie. */
	public GM_Object getGeom() {return geom;}
	/** Affecte une geometrie (et met à jour les éventuels index concernés. */
	public void setGeom (GM_Object g) {
		boolean geomAvant = false;
		if ( geom != null ) geomAvant = true;
		geom = g;
		Iterator itFC = this.getFeatureCollections().iterator();
		while (itFC.hasNext()) {
			FT_FeatureCollection fc = (FT_FeatureCollection)itFC.next();
			if (fc.hasSpatialIndex()) {
				if (fc.getSpatialIndex().hasAutomaticUpdate()) {
					if ( geomAvant ) fc.getSpatialIndex().update(this, 0);
					else fc.getSpatialIndex().update(this, 1);
				}
			}
		}
	}
	
	/** Renvoie true si une geometrie existe, false sinon. */
	public boolean hasGeom() {if (geom == null) return false; else return true;}
	
	
	protected TP_Object topo = null;
	/** Renvoie une topologie. */
	public TP_Object getTopo() {return topo;}
	/** Affecte une topologie. */	
	public void setTopo (TP_Object t) {topo = t;}
	/** Renvoie true si une topologie existe, false sinon. */	
	public boolean hasTopo() {if (topo == null) return false; else return true;}
	   
	   
	/** Clonage avec clonage de la geometrie. */
	 public FT_Feature cloneGeom() {
		FT_Feature result = (FT_Feature)this.clone();
		result.setGeom ( (GM_Object) this.getGeom().clone());
		return result;
	 } 
	 
	 /** Clonage sans clonage de la geometrie. */
	 public Object clone() {
	 	try {
	 		return super.clone();
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 		return null;
	 	}
	 }
	    
	    
	/** Lien n-m bidirectionnel vers FT_FeatureCollection. */	    	    
	private List featurecollections = new ArrayList();	
	/** Renvoie toutes les FT_FeatureCollection auquelles appartient this. */
	public List getFeatureCollections() {return featurecollections;}
	/** Renvoie la i-eme FT_FeatureCollection a laquelle appartient this. */
	public FT_FeatureCollection getFeatureCollection(int i) {return (FT_FeatureCollection) featurecollections.get(i);	}
	

	/** Population a laquelle appartient this. 
	 *  Renvoie null si this n'appartient a aucune population. 
         *  NB : normalement, this appartient à une seule collection. 
         *  Si ce n'est pas le cas, une seule des collections est renvoyée au hasard (la première de la liste).
         */	    	    
        public Population getPopulation() {
            Iterator it = featurecollections.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof Population) return (Population)o;
            }
            return null;
        }

        /** Définit la population en relation, et met à jour la relation inverse. 
         *  ATTENTION : presistance du FT_Feature non gérée dans cette méthode.
         */
/*        public void setPopulation(Population O) {
            Population old = this.getPopulation();
            if ( old  != null ) old.remove(this);
            if ( O != null ) O.add(this);
        }
*/
    
    /** Lien bidirectionnel n-m des éléments vers eux-mêmes. 
     *  Les méthodes get (sans indice) et set sont nécessaires au mapping. 
     *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */    
    private List correspondants = new ArrayList();
	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
    public List getCorrespondants() {return correspondants ; }   
	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */ 
    public void setCorrespondants (List L) {
        List old = new ArrayList(correspondants);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            FT_Feature O = (FT_Feature)it1.next();
            correspondants.remove(O);
            O.getCorrespondants().remove(this);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            FT_Feature O = (FT_Feature)it2.next();
            correspondants.add(O);
            O.getCorrespondants().add(this);
        }
    }
	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
    public FT_Feature getCorrespondant(int i) {
    	if ( correspondants.size() == 0 ) return null;
    	return (FT_Feature)correspondants.get(i) ; 
    }
	/** Lien bidirectionnel n-m des éléments vers eux même.  */    
    public void addCorrespondant(FT_Feature O) {
        if ( O == null ) return;
        correspondants.add(O) ;
        O.getCorrespondants().add(this);
    }
	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
    public void removeCorrespondant(FT_Feature O) {
        if ( O == null ) return;
        correspondants.remove(O) ; 
        O.getCorrespondants().remove(this);
    }
	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
    public void clearCorrespondants() {
        Iterator it = correspondants.iterator(); 
        while ( it.hasNext() ) {
            FT_Feature O = (FT_Feature)it.next();
            O.getCorrespondants().remove(this);
        }
        correspondants.clear();
    }
	/** Renvoie les correspondants appartenant a la FT_FeatureCollection passee en parametre. */ 
    public List getCorrespondants(FT_FeatureCollection pop) {
        List elementsPop = pop.getElements();
        List resultats = new ArrayList(this.getCorrespondants());
        resultats.retainAll(elementsPop);
        return resultats;
    }    
    
}
