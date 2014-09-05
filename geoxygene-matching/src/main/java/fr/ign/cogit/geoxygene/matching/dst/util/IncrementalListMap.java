/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Bertrand Dumenieu
 *
 */
public class IncrementalListMap<E> implements AutoIncrementalList<E>, Iterable<E>{
  


  private static final long serialVersionUID = 1L;
  private int counter = 0;
  private HashMap<Integer, E> imap;
  
  public IncrementalListMap(){
    this.imap = new HashMap<Integer, E>();
  };
  
  @SuppressWarnings("unchecked")
  public IncrementalListMap(IncrementalListMap<E> list){
    this.imap = (HashMap<Integer, E>) list.imap.clone();
    this.counter = list.counterValue();
  }
  
  public IncrementalListMap(List<E> list){
    this.imap = new HashMap<Integer, E>();
    this.addAll(list);
  }
  
  @Override
  public int size() {
    return this.imap.size();
  }
  
  @Override
  public boolean isEmpty() {
    return this.imap.isEmpty();
  }
  
  @Override
  public boolean contains(Object o) {
    return this.imap.values().contains(o);

  }
  
  @Override
  public Iterator<E> iterator() {
    return this.imap.values().iterator();

  }
  @Override
  public Iterator<Integer> idIterator() {
    return this.imap.keySet().iterator();

  }
  
  @Override
  public Object[] toArray() {
    return this.imap.values().toArray();
  }
  
  @Override
  public boolean add(E e) {
    return this.imap.put(++counter, e) != null;

  }
  
  @Override
  public boolean remove(Object o) {
    for(Entry<Integer, E> entry : this.imap.entrySet()){
      if(entry.getValue().equals(o) && entry.getKey() == this.counter){
        --this.counter;
        break;
      }
    }   
    return this.imap.remove(o) != null;
  }
  
  @Override
  public boolean containsAll(Collection<?> c) {
    return this.imap.values().containsAll(c);

  }
  
  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean b = false;
    for(E e : c){
      b = this.add(e);
    }
    return b;
  }
  
  @Override
  public boolean removeAll(Collection<?> c) {
    boolean b = false;
    for( Object e : c){
      b= this.remove(e);
    }
    return b;
  }
  
  @Override
  public boolean retainAll(Collection<?> c) {
    boolean b = false;
    for(E value : this.imap.values()){
      if(!c.contains(value)){
        b = this.remove(value);
      }
    }
    return b;
  }
  
  @Override
  public void clear() {
    this.counter = 0;
    this.imap.clear();    
  }
  
  @Override
  public E get(int identifier) {
    return this.imap.get(identifier);
  }
  
  @Override
  public E set(int identifier, E element) {
    if(identifier > this.counter)
      this.counter = identifier;
    return this.imap.put(identifier,element);
  }
  
  @Override
  public E remove(int identifier) {
    return this.imap.remove(identifier);
  }
  @Override
  public int getIdentifier(Object o) {
    for(Entry<Integer, E> entry : this.imap.entrySet()){
      if(entry.getValue().equals(o))
        return entry.getKey(); 
    }
    return -1;
  }
  @Override
  public int counterValue() {
    return this.counter;
  }

  @Override
  public List<E> asList() {
    return new ArrayList<E>(this.imap.values());
  }
  
  
  
  @Override
  public String toString() {
    return this.imap.toString();
  }
  
}