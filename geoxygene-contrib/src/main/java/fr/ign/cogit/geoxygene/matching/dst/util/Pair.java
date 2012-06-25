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

import java.util.Arrays;

/**
 * @author Julien Perret
 *
 */
public class Pair<E ,V>{
  


  private E first;
  public V second;
  
  
  public Pair(E _first , V _second){
    this.first = _first;
    this.second = _second;
  }
  
  public E getFirst(){
    return this.first;
  }

  public V getSecond(){
    return this.second;
  }
  
  public void setFirst(E _first){
    this.first = _first;
  }
  
  public void setSecond(V _second){
    this.second = _second;
  }
  
  @Override
  public String toString() {
    String fstring = this.first.toString();
    if(first.getClass().isArray()){
      fstring = Arrays.toString(Arrays.asList(this.first).toArray());
    }
    String sstring = this.second.toString();
    if(second.getClass().isArray()){
      sstring = Arrays.toString(Arrays.asList(this.second).toArray());
    }
    return "{"+fstring+":"+sstring+"}";
  }
  
  


  
}
