/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Agrégation quelconque d'objets géométriques. Il n'y a aucune structure
 * interne. On aurait pu faire un set d'elements au lieu d'une liste ?
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public class GM_Aggregate<GeomType extends IGeometry> extends GM_Object
    implements Collection<GeomType>, IAggregate<GeomType> {
  /** Liste des éléments constituant self. */
  protected List<GeomType> element;

  @Override
  public List<GeomType> getList() {
    return this.element;
  }

  @Override
  public void setList(List<GeomType> L) {
    this.element = L;
  }

  @Override
  public GeomType get(int i) {
    return this.element.get(i);
  }

  @Override
  public GeomType set(int i, GeomType value) {
    return this.element.set(i, value);
  }

  @Override
  public boolean add(GeomType value) {
    return this.element.add(value);
  }

  @Override
  public boolean addAll(List<GeomType> theList) {
    return this.element.addAll(theList);
  }

  @Override
  public void add(int i, GeomType value) {
    this.element.add(i, value);
  }

  @Override
  public boolean remove(GeomType value) {
    return this.element.remove(value);
  }

  @Override
  public void remove(int i) {
    this.element.remove(i);
  }

  @Override
  public void clear() {
    this.element.clear();
  }

  @Override
  public int size() {
    return this.element.size();
  }

  @Override
  public IGeometry[] toArray() {
    IGeometry[] result = new IGeometry[this.size()];
    int i = 0;
    for (GeomType o : this) {
      result[i++] = o;
    }
    return result;
  }

  @Override
  public IBoundary boundary() {
    return null;
  }

  /** Constructeur par défaut. */
  public GM_Aggregate() {
    this.element = new ArrayList<GeomType>(0);
  }

  /** Constructeur à partir d'un GM_Object. */
  public GM_Aggregate(GeomType anObject) {
    this.element = new ArrayList<GeomType>(1);
    this.add(anObject);
  }

  /** Constructeur à partir d'une liste de GM_Object. */
  public GM_Aggregate(List<GeomType> fromSet) {
    int n = fromSet.size();
    this.element = new ArrayList<GeomType>(n);
    if (n > 0) {
      for (int i = 0; i < n; i++) {
        this.add(fromSet.get(i));
      }
    }
  }

  @Override
  public IDirectPositionList coord() {
    IDirectPositionList result = new DirectPositionList();
    if (this != null) {
      for (IGeometry o : this) {
        result.addAll(o.coord());
      }
    }
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
    return this.remove((GeomType) o);
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
    for (GeomType elt : this.element) {
      agg.add((GeomType) elt.clone());
    }
    agg.setCRS(this.getCRS());
    return agg;
  }
}
