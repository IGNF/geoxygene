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
 * 
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.impl.support.reseau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/**
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class NoeudReseauImpl extends ElementDuReseauImpl implements NoeudReseau {

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  /**
   * les arcs entrants du noeud
   */
  private Collection<ArcReseau> arcsEntrants = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcsEntrants() {
    return this.arcsEntrants;
  }

  /**
   * les arcs sortants du noeud
   */
  private Collection<ArcReseau> arcsSortants = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcsSortants() {
    return this.arcsSortants;
  }
  
  /**
   * @author JTeulade-Denantes
   * 
   * this function takes all the arcs related to the node in order to sort them in a clockwise direction
   * @return the list node arcs in a clockwise direction 
   */
  private List<ArcReseau> getClockwiseArcs() {
    
    IDirectPosition positionNode = this.getGeom().getPosition();
    //the position of each node arc
    IDirectPosition positionArc;
    List<Pair<ArcReseau, Double>> comparatorList = new ArrayList<Pair<ArcReseau,Double>>();
    
    for (ArcReseau arc : this.getArcsEntrants()) {
      //for arcs going in the node, the last point is the node itself, that's why we need the second last one
      positionArc = arc.getGeom().coord().get(arc.getGeom().coord().size()-2);
      //we add the orientation between the two positions to the comparatorList
      comparatorList.add(new Pair<ArcReseau, Double>(arc,positionArc.orientation(positionNode)));
    }
    
    for (ArcReseau arc : this.getArcsSortants()) {
      //for arcs going out the node, the first point is the node itself, that's why we need the second one
      positionArc = arc.getGeom().coord().get(1);
      comparatorList.add(new Pair<ArcReseau, Double>(arc,positionArc.orientation(positionNode)));
    }
    
    //we sort the list according to the angles
    Collections.sort(comparatorList, new Comparator<Pair<ArcReseau, Double>>(){
      public int compare(Pair<ArcReseau, Double> p1, Pair<ArcReseau, Double> p2) {
          return -p1.second().compareTo(p2.second());
      }
    });
    
    //we return only the arcs without the angle related
    List<ArcReseau> arcReseauList = new ArrayList<ArcReseau>();
    for (Pair<ArcReseau, Double> arcReseauPair : comparatorList) {
      arcReseauList.add(arcReseauPair.first());
    }
    return arcReseauList;
    
  }
  
  
  /**
   * @author JTeulade-Denantes
   * 
   * this function returns the arcs included between two arcs in a clockwise or counterclockwise direction
   * @param firstArc
   * @param secondArc
   * @param clockwise
   * @return the list of arcs
   */
  public List<ArcReseau> clockwiseSelectedArcs(ArcReseau firstArc, ArcReseau secondArc, boolean clockwise) {
    //we get back sorted list
    List<ArcReseau> clockwiseArcs = this.getClockwiseArcs();

    //we find the indexes of firstArc and secondArc in clockwiseArcs
    int firstArcIndex = clockwiseArcs.indexOf(firstArc);
    int secondArcIndex = clockwiseArcs.indexOf(secondArc);
   
    //we check whether firstArc and secondArc have been found in clockwiseArcs
    if (firstArcIndex==-1 || secondArcIndex==-1) {
      logger.info("error in clockwiseArcsCount function: one of the arcs doesn't belong to the current node");
      return null;
    }
    
    if (secondArcIndex > firstArcIndex) {
      if (clockwise) {
        clockwiseArcs = clockwiseArcs.subList(firstArcIndex+1, secondArcIndex);
      } else {
        clockwiseArcs.removeAll(clockwiseArcs.subList(firstArcIndex, secondArcIndex+1));
      }
    } else if (secondArcIndex < firstArcIndex) {
      if (clockwise) {
        clockwiseArcs.removeAll(clockwiseArcs.subList(secondArcIndex, firstArcIndex+1));
      } else {
        clockwiseArcs = clockwiseArcs.subList(secondArcIndex+1, firstArcIndex);
      }
    }
    return clockwiseArcs;
  }
  
  /**
   * 
   * @author JTeulade-Denantes
   *
   * @param <A>
   * @param <B>
   */
  private class Pair<A, B>
  {
      private A element1;

      private B element2;

      public Pair(){}

      public Pair(A element1, B element2)
      {
          this.element1 = element1;
          this.element2 = element2;
      }

      public A first()
      {
          return element1;
      }

      public B second()
      {
          return element2;
      }
      public String toString()
      {
          return "(" + element1 + "," + element2 + ")";
      }

      public void set1(A element1)
      {
          this.element1 = element1;
      }

      public void set2(B element2)
      {
          this.element2 = element2;
      }
  }
}
