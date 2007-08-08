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

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.SpatialIndex;


/** 
 * Collection (liste) de FT_Feature. Peut porter un index spatial.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 * 19.02.2007 : ajout des méthodes contains, addUnique, remove, removeCollection,
 * addUniqueCollection, iterator
 * 
 */

public class FT_FeatureCollection {

	// constructeurs
	public FT_FeatureCollection() {}

	/** constructeur recopiant une autre collection.
	 * ATTENTION: ne recopie pas l'éventuel index spatial*/
	public FT_FeatureCollection(FT_FeatureCollection listeACopier) {
		this.setFlagGeom(listeACopier.getFlagGeom());
		this.setFlagTopo(listeACopier.flagTopo);
		this.getElements().addAll(listeACopier.getElements()); 
	}

	/** Constructeur à partir d'une collection de FT_Feature */
	public FT_FeatureCollection(Collection col) {
		this.getElements().addAll(col); 
	}

	// ---------------------------------------
	// --- Indicateurs de geometrie et topo --
	// ---------------------------------------

	/** Boolean indiquant si les FT_Feature portent une geometrie (true par defaut). */
	protected boolean flagGeom = true;


	/** Boolean indiquant si les FT_Feature portent une geometrie. */
	public boolean getFlagGeom() {
		return flagGeom; 
	}

	/** Boolean indiquant si les FT_Feature portent une geometrie. */
	public boolean hasGeom() {
		return flagGeom; 
	}

	/** Boolean indiquant si les FT_Feature portent une geometrie. */
	public void setFlagGeom(boolean Geom) {
		flagGeom = Geom; 
	}


	/** Boolean indiquant si les FT_Feature portent une topologie (false par defaut). */
	protected boolean flagTopo = false;

	/** Boolean indiquant si les FT_Feature portent une topologie. */
	public boolean hasTopo() {
		return flagTopo; 
	}

	/** Boolean indiquant si les FT_Feature portent une topologie. */
	public void setFlagTopo(boolean Topo) {
		flagTopo = Topo; 
	}



	// ---------------------------------------
	// --- Accesseurs ------------------------
	// ---------------------------------------

	/** La liste des FT_Feature composant this. */
	protected List elements = new ArrayList();

	/** Iterateur sur la liste des FT_Feature composant this. */
	private Iterator iElements;

	/** Renvoie la liste de FT_Feature composant this. */
	public List getElements () {
		return this.elements;
	}

	/** Affecte une liste de FT_Feature à this, et met à jour le lien inverse. 
	 * Attention detruit l'index spatial si celui existait. 
	 * Il faut donc le reinitialiser si on souhaite l'utiliser.*/
	public void setElements (List L) {
		List old = new ArrayList(elements);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			FT_Feature O = (FT_Feature)it1.next();
			elements.remove(O);
			O.getFeatureCollections().remove(this);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			FT_Feature O = (FT_Feature)it2.next();
			elements.add(O);
			if (!O.getFeatureCollections().contains(this))
				O.getFeatureCollections().add(this);
		}	
		if (isIndexed) removeSpatialIndex();
	}

	/** Renvoie le i-eme element de la liste des composants de this. */
	public FT_Feature get (int i) {
		return (FT_Feature) this.elements.get(i);
	}

	/** Ajoute un element a la liste des composants de this, et met à jour le lien inverse. */
	public void add (FT_Feature value) {
		if (value == null) return;
		this.elements.add(value);
		value.getFeatureCollections().add(this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate()) spatialindex.update(value,+1);
	}

	/** Ajoute les éléments d'une FT_FeatureCollection a la liste des composants de this, et met à jour le lien inverse. */
	public void addCollection (FT_FeatureCollection value) {
		FT_Feature elem;
		if (value == null) return;
		Iterator iter = value.elements.iterator();
		while(iter.hasNext()){
			elem = (FT_Feature) iter.next();
			add(elem);
		}
	}

	/** Efface de la liste l'element passe en parametre.
	 *  Attention, si l'élément est persistant, celui-ci n'est pas détruit, le faire après au besoin.
	 */
	public void remove (FT_Feature value)  {
		if (value==null) return;
		this.elements.remove(value);
		value.getFeatureCollections().remove(this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate()) spatialindex.update(value,-1);
	}    

	/** Efface de la liste tous les élements de la collection passée en paramètre.
	 *  Attention, si l'élément est persistant, celui-ci n'est pas détruit, le faire après au besoin.
	 */
	public void removeAll(Collection coll)  {
		if (coll==null) return;
		if (coll.size()==0) return;
		Iterator itColl = coll.iterator();
		while (itColl.hasNext()) {
			FT_Feature objet = (FT_Feature)itColl.next();
			this.remove(objet);
		}
	}    

	/** Efface toute la liste. 
	 * Detruit l'index spatial si celui existe. */
	public void clear() {   	
		Iterator it = elements.iterator(); 
		while ( it.hasNext() ) {
			FT_Feature O = (FT_Feature)it.next();
			O.getFeatureCollections().remove(this);
		}
		this.elements.clear();
		if (isIndexed) removeSpatialIndex();
	}

	/** Initialise l'iterateur de la liste. */
	public void initIterator() {
		iElements = this.elements.iterator();
	}

	/** Renvoie true s'il reste des elements avec l'iterateur, false sinon. */
	public boolean hasNext() {
		if (iElements.hasNext()) return true;
		else return false;
	}

	/** Renvoie le prochain element avec l'iterateur. */
	public FT_Feature next() {
		return (FT_Feature)iElements.next();
	}

	/** Renvoie le nombre de elements */
	public int size() {
		return this.elements.size();
	}



	// ---------------------------------------
	// --- Calcul de l'emprise ---------------
	// ---------------------------------------

	/** Calcul l'emprise rectangulaire des geometries de la collection. */
	public GM_Envelope envelope () {
		if (this.hasGeom())
			return this.getGeomAggregate().envelope();
		else {
			System.out.println("ATTENTION appel de envelope() sur une FT_FeatureCollection sans geometrie ! (renvoie null) ");
			return null;
		}
	}




	// ---------------------------------------
	// --- Utile ! ---------------------------
	// ---------------------------------------

	/** Renvoie toutes les geometries sous la forme d'un GM_Aggregate. */
	public GM_Aggregate getGeomAggregate() {
		if (this.hasGeom()) {
			GM_Aggregate aggr = new GM_Aggregate();
			initIterator();
			while (hasNext())
				aggr.add(next().getGeom());  
			return aggr;  
		} else {    
			System.out.println("ATTENTION appel de getGeom() sur une FT_FeatureCollection sans geometrie ! (renvoie null) ");
			return null;
		}
	}



	// ---------------------------------------
	// --- Index spatial ---------------------
	// ---------------------------------------

	/** Index spatial. */
	private SpatialIndex spatialindex;

	/** La collection possede-t-elle un index spatial ? */
	private boolean isIndexed = false;

	/** Index spatial. */	
	public SpatialIndex getSpatialIndex() {
		return spatialindex;
	}

	/** La collection possede-t-elle un index spatial ? */
	public boolean hasSpatialIndex() {
		return isIndexed;
	}

	/** Initialise un index spatial avec détermination automatique des paramètres. 
	 * Le boolean indique si on souhaite une mise a jour automatique de l'index. */
	public void initSpatialIndex (Class spatialIndexClass, boolean automaticUpdate) {	
		if (!this.hasGeom()) {
			System.out.println("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}
		try {
			spatialindex = (SpatialIndex) spatialIndexClass.
			getConstructor(new Class[] {FT_FeatureCollection.class, Boolean.class}).
			newInstance(new Object[] {this, new Boolean(automaticUpdate)} );
			isIndexed = true;						
		} catch (Exception e) {
			System.out.println("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}
	}

	/** Initialise un index spatial avec un parametre entier (utilise pour le dallage). 
	 * Le boolean indique si on souhaite une mise a jour automatique de l'index.*/
	public void initSpatialIndex (Class spatialIndexClass, boolean automaticUpdate, int i) {
		if (!this.hasGeom()) {
			System.out.println("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}		
		try {
			spatialindex = (SpatialIndex) spatialIndexClass.
			getConstructor(new Class[] {FT_FeatureCollection.class, Boolean.class, Integer.class}).
			newInstance(new Object[] {this, new Boolean(automaticUpdate), new Integer(i)} );
			isIndexed = true;												
		} catch (Exception e) {
			System.out.println("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}	
	}

	/** Initialise un index spatial d'une collection de FT_Feature, 
	 * en prenant pour paramètre les limites de la zone et un entier 
	 * (pour le dallage, cet entier est le nombre en X et Y de cases souhaitées sur la zone).
	 * 
	 * @param spatialIndexClass
	 * Nom de la classe d'index.
	 * 
	 * @param automaticUpdate
	 * Spéciifie si l'index doit être mis à jour automatiquement 
	 * quand on modifie les objets de fc.
	 * 
	 * @param enveloppe
	 * Enveloppe décrivant les limites de l'index spatial.
	 * NB: Tout objet hors de ces limites ne sera pas traité lors des requêtes spatiales !!!!!
	 * 
	 * @param i
	 * Nombre de dalles en X et en Y, du dallage.
	 */		
	public void initSpatialIndex (Class spatialIndexClass, boolean automaticUpdate, GM_Envelope enveloppe, int i) {
		if (!this.hasGeom()) {
			System.out.println("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}		
		try {
			spatialindex = (SpatialIndex) spatialIndexClass.
			getConstructor(new Class[] {FT_FeatureCollection.class, Boolean.class, GM_Envelope.class, Integer.class}).
			newInstance(new Object[] {this, new Boolean(automaticUpdate), enveloppe, new Integer(i)} );
			isIndexed = true;												
		} catch (Exception e) {
			System.out.println("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}	
	}

	/** Initialise un index spatial d'une collection de FT_Feature, 
	 * en prenant pour paramètre ceux d'un index existant.
	 */		
	public void initSpatialIndex (SpatialIndex spIdx) {
		//enlevé : Class spatialIndexClass,
		if (!this.hasGeom()) {
			System.out.println("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}		
		try {
//			spatialindex = (SpatialIndex) spatialIndexClass.
			spatialindex = (SpatialIndex) spIdx.getClass().
			getConstructor(new Class[] {FT_FeatureCollection.class, spIdx.getClass()}).
			newInstance(new Object[] {this, spIdx} );




			isIndexed = true;												
		} catch (Exception e) {
			System.out.println("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}	
	}




	/** Détruit l'index spatial. */
	public void removeSpatialIndex () {
		spatialindex = null;
		isIndexed = false;
	}	

	// ---------------------------------------
	// --- SELECTION AVEC L'Index spatial ----
	// ---------------------------------------

	/** Selection dans le carre dont P est le centre, de cote D. */
	public FT_FeatureCollection select (DirectPosition P, double D) {
		if (!isIndexed) {
			System.out.println("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(P,D);
	}

	/** Selection dans un rectangle. */	
	public FT_FeatureCollection select (GM_Envelope env) {
		if (!isIndexed) {
			System.out.println("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(env);
	}

	/** Selection des objets qui intersectent un objet geometrique quelconque. */
	public FT_FeatureCollection select (GM_Object geometry) {
		if (!isIndexed) {
			System.out.println("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry);
	}

	/** Selection des objets qui croisent ou intersectent un objet geometrique quelconque.
	 * 
	 * @param strictlyCrosses
	 * Si c'est TRUE : ne retient que les  objets qui croisent (CROSS au sens JTS)
	 * Si c'est FALSE : ne retient que les  objets qui intersectent (INTERSECT au sens JTS)
	 * Exemple : si 1 ligne touche "geometry" juste sur une extrémité, 
	 * alors avec TRUE cela ne renvoie pas la ligne, avec FALSE cela la renvoie
	 */
	public FT_FeatureCollection select(GM_Object geometry, boolean strictlyCrosses) {
		if (!isIndexed) {
			System.out.println("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry,strictlyCrosses);
	}


	/** Selection a l'aide d'un objet geometrique quelconque et d'une distance. */
	public FT_FeatureCollection select (GM_Object geometry, double distance) {
		if (!isIndexed) {
			System.out.println("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry, distance);

	}

	/** Encapsulation de la methode contains() avec typage */
	public boolean contains (FT_Feature value) {
		if (this.elements.contains(value)) return true;
		else
			return false;
	}


	/** Ajoute un element a la liste des composants de this s'il n'est pas déjà présent, et 
	 *  met à jour le lien inverse. */
	public void addUnique (FT_Feature value) {
		if (value == null) return;
		if (this.elements.contains(value)) return;
		this.elements.add(value);
		value.getFeatureCollections().add(this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate()) spatialindex.update(value,+1);
	}

	/** Efface de la liste l'element en position i.
	 *  Attention, si l'élément est persistant, celui-ci n'est pas détruit, le faire après au besoin.
	 */
	public void remove (int i)  {
		if (i>this.size()) return;
		FT_Feature value = this.get(i); 
		this.elements.remove(value);
		value.getFeatureCollections().remove(this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate()) spatialindex.update(value,-1);
	}    


	/** Efface de la liste la collection passée en parametre.
	 *  Attention, si l'élément est persistant, celui-ci n'est pas détruit, le faire après au besoin.
	 */
	public void removeCollection (FT_FeatureCollection value)  {
		FT_Feature elem;
		if (value==null) return;
		Iterator iter = value.elements.iterator();
		while(iter.hasNext()){
			elem = (FT_Feature) iter.next();
			remove(elem);
		}
	}    

	/** Ajoute les éléments d'une FT_FeatureCollection a la liste des composants de this, et met à jour le lien inverse. */
	public void addUniqueCollection (FT_FeatureCollection value) {
		FT_Feature elem;
		if (value == null) return;
		Iterator iter = value.elements.iterator();
		while(iter.hasNext()){
			elem = (FT_Feature) iter.next();
			this.addUnique(elem);
		}
	}
	/** Iterateur
	 */
	public Iterator iterator(){
		return this.elements.iterator();
	}

}