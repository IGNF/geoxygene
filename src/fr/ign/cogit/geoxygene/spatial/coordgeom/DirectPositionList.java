/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

/**
 * Liste de DirectPosition. On reprend les méthodes standards de
 * <tt> java.util.List </tt> en les typant.
 * <p>
 * NB : Dans la norme ISO, cette classe s'appelle GM_PointArray
 * @author Thierry Badard
 * @author Arnaud Braun
 */
public class DirectPositionList implements IDirectPositionList {
  /** La liste des DirectPosition. */
  protected List<IDirectPosition> list = new ArrayList<IDirectPosition>(0);
  @Override
  public void setList(List<IDirectPosition> theList) {
    this.list = theList;
  }
  @Override
  public List<IDirectPosition> getList() {
    return this.list;
  }
  @Override
  public IDirectPosition get(int i) {
    return this.list.get(i);
  }
  @Override
  public void set(int i, IDirectPosition value) {
    this.list.set(i, value);
  }
  @Override
  public boolean add(IDirectPosition value) {
    return this.list.add(value);
  }
  @Override
  public void add(int i, IDirectPosition value) {
    this.list.add(i, value);
  }
  @Override
  public boolean addAll(IDirectPositionList theList) {
    return this.list.addAll(theList.getList());
  }
  @Override
  public void remove(IDirectPosition value) {
    this.list.remove(value);
  }
  @Override
  public void remove(int i) {
    this.list.remove(i);
  }
  @Override
  public void removeAll(IDirectPositionList theList) {
    this.list.removeAll(theList.getList());
  }
  @Override
  public void clear() {
    this.list.clear();
  }
  @Override
  public int size() {
    return this.list.size();
  }
  /** Constructeur par défaut. */
  public DirectPositionList() {
  }
  /**
   * Constructeur à partir d'une liste de DirectPosition. Attention, ne vérifie
   * pas que la liste passée en paramètre ne contient que des DirectPosition. Ne
   * clone pas la liste passée en paramètre mais fait une référence.
   */
  public DirectPositionList(List<IDirectPosition> theList) {
    this.setList(theList);
  }
  public DirectPositionList(IDirectPosition... list) {
    this(new ArrayList<IDirectPosition>(Arrays.asList(list)));
  }
  @Override
  public DirectPositionList clone() {
    DirectPositionList dpl = new DirectPositionList();
    for (IDirectPosition p : this.list) {
      dpl.add((IDirectPosition) p.clone());
    }
    return dpl;
  }
  @Override
  public double[] toArray2D() {
    double[] array = new double[this.list.size() * 2];
    int i = 0;
    for (IDirectPosition p : this) {
      array[i++] = p.getX();
      array[i++] = p.getY();
    }
    return array;
  }
  @Override
  public double[] toArray3D() {
    double[] array = new double[this.list.size() * 3];
    int i = 0;
    for (IDirectPosition p : this) {
      array[i++] = p.getX();
      array[i++] = p.getY();
      array[i++] = p.getZ();
    }
    return array;
  }
  @Override
  public double[] toArrayX() {
    double[] array = new double[this.list.size()];
    int i = 0;
    for (IDirectPosition p : this) {
      array[i++] = p.getX();
    }
    return array;
  }
  @Override
  public double[] toArrayY() {
    double[] array = new double[this.list.size()];
    int i = 0;
    for (IDirectPosition p : this) {
      array[i++] = p.getY();
    }
    return array;
  }
  @Override
  public double[] toArrayZ() {
    double[] array = new double[this.list.size()];
    int i = 0;
    for (IDirectPosition p : this) {
      array[i++] = p.getZ();
    }
    return array;
  }
  @Override
  public String toString() {
    String result = new String();
    if (this.size() == 0) {
      result = "DirectPositionList : liste vide"; //$NON-NLS-1$
      return result;
    }
    for (int i = 0; i < this.size(); i++) {
      result = result + this.get(i).toString() + "\n"; //$NON-NLS-1$
    }
    return result.substring(0, result.length() - 1); // without the last "\n"
  }
  @Override
  public Iterator<IDirectPosition> iterator() {
    return this.list.iterator();
  }
  @Override
  public ListIterator<IDirectPosition> listIterator() {
    return this.list.listIterator();
  }
  @Override
  public boolean addAll(Collection<? extends IDirectPosition> c) {
    return this.addAll((DirectPositionList) c);
  }
  @Override
  public boolean contains(Object o) {
    return this.list.contains(o);
  }
  @Override
  public boolean containsAll(Collection<?> c) {
    return this.list.containsAll(c);
  }
  @Override
  public boolean isEmpty() {
    return this.list.isEmpty();
  }
  @Override
  public boolean remove(Object o) {
    return this.list.remove(o);
  }
  @Override
  public boolean removeAll(Collection<?> c) {
    return this.list.removeAll(c);
  }
  @Override
  public boolean retainAll(Collection<?> c) {
    return this.list.retainAll(c);
  }
  @Override
  public Object[] toArray() {
    return this.list.toArray();
  }
  @Override
  public <T> T[] toArray(T[] a) {
    return this.list.toArray(a);
  }
  @Override
  public void permuter(int i, int j) {
    if (i == j) {
      return;
    }
    if (i >= this.size() || j >= this.size()) {
      System.out
          .println("Erreur dans permutation: index " + i + " ou " + j + " plus grand que " + this.size()); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
    }
    IDirectPosition dpi = this.get(i), dpj = this.get(j);
    this.remove(i);
    this.add(i, dpj);
    this.remove(j);
    this.add(j, dpi);
  }
  @Override
  public void inverseOrdre() {
    int nb = this.size();
    for (int i = 0; i < nb / 2; i++) {
      this.permuter(i, nb - 1 - i);
    }
  }
  @Override
  public IDirectPositionList reverse() {
    IDirectPositionList list = this.clone();
    Collections.reverse(list.getList());
    return list;
  }
}
