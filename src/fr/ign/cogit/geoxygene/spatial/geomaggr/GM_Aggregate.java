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

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Agrégation quelconque d'objets géométriques. Il n'y a aucune structure interne.
 * On aurait pu faire un set d'elements au lieu d'une liste ?
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class GM_Aggregate<GeomType extends GM_Object> extends GM_Object implements Collection<GeomType>{

	/** Liste des éléments constituant self. */
	protected List<GeomType> element;

	/** Renvoie l'attribut element = liste de GM_Object. */
	public List<GeomType> getList () {return this.element;}

	/** Affecte une liste de GM_Object. */
	public void setList (List<GeomType> L) {element = L; }

	/** Renvoie l'élément de rang i */
	public GeomType get (int i) {return this.element.get(i);}

	/** Affecte un élément au i-ème rang de la liste */
	public GeomType set (int i, GeomType value) {return this.element.set(i, value);}

	/** Ajoute un élément en fin de liste */
	public boolean add (GeomType value) {return this.element.add(value);}

	/** Ajoute une liste d'éléments en fin de liste */
	public boolean addAll (List<GeomType> theList) {return this.element.addAll(theList);}

	/** Ajoute un élément au i-ème rang de la liste */
	public void add (int i, GeomType value) {this.element.add(i, value);}

	/** Efface de la liste le (ou les) éléments passé en paramètre */
	public boolean remove (GeomType value)  {return this.element.remove(value);}

	/** Efface le i-ème élément de la liste */
	public void remove (int i)  {this.element.remove(i);}

	/** Efface toute la liste */
	public void clear() {this.element.clear();}

	/** Renvoie le nombre de éléments */
	public int size () {return this.element.size();}

	/** Convertit l'agrégat en un tableau de GM_Obect. */
	public GM_Object[] toArray() {
		GM_Object[] result = new GM_Object[size()];
		int i=0;
		for(GeomType o:this) result[i++] = o;
		return result;
	}

	/** NON IMPLEMENTE (renvoie null).
	 * Collection de GM_Object représentant la frontière de self.
	 * Cette collection d'objets a une structure de GM_Boundary, qui est un sous-type de GM_Complex.
	 */
	/*public GM_Boundary boundary() {
        return null;
    }   */

	/** Constructeur par défaut. */
	public GM_Aggregate() {
		element = new ArrayList<GeomType>();
	}

	/** Constructeur à partir d'un GM_Object. */
	public GM_Aggregate (GeomType anObject) {
		element = new ArrayList<GeomType>();
		this.add(anObject);
	}

	/**Constructeur à partir d'une liste de GM_Object. */
	public GM_Aggregate (List<GeomType> fromSet) {
		element = new ArrayList<GeomType>();
		int n = fromSet.size();
		if (n > 0)
			for (int i=0; i<n; i++)
				this.add(fromSet.get(i));
	}


	/** Renvoie une DirectPositionList de tous les points de l'objet. */
	@Override
	public DirectPositionList coord() {
		DirectPositionList result = new DirectPositionList();
		for(GM_Object o:this) result.addAll(o.coord());
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends GeomType> c) {
		return this.element.addAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return this.element.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.element.containsAll(c);
	}

	@Override
	public Iterator<GeomType> iterator() {
		return this.element.iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		return this.remove((GeomType)o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.element.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.retainAll(c);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.element.toArray(a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		GM_Aggregate<GeomType> agg = new GM_Aggregate<GeomType>();
		for (GeomType elt : element)
			agg.add((GeomType) elt.clone());
		return agg;
	}

	/** Pour l'affichage des coordonnees. */
	/*public String toString() {
        String result = new String();
        initIterator();
        int i=0;
        while (hasNext()) {
            result = result+"\nGM_Aggregate - element "+i+"\n";
            result = result+next().toString();
        }
        return result;
    }*/

}
