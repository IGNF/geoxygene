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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import org.geotools.data.simple.SimpleFeatureCollection;

/**
 * Network data matching results.<ul>
 *   <li>- Network matched</li>
 *   <li>- Number of arcs of the less detailled network</li>
 *   <li>- Number of arcs of the comparison network</li>
 *   <li>- ...</li>
 *   </ul>
 * 
 * @author M.-D. Van Damme
 * @version 1.6
 */
public class ResultatAppariement {
  
  /** Network matched. */
  SimpleFeatureCollection networkMatched;
  
  /** Number of arcs of the less detailled network. */
  int nbArcRef;
  
  /** Number of arcs of the comparison network. */
  int nbArcComp;
    
  /**
   * Constructor.
   * @param sfc
   */
  public ResultatAppariement(SimpleFeatureCollection sfc) {
    networkMatched = sfc;
    nbArcRef = 0;
    nbArcComp = 0;
  }
  
  /**
   * Return number of arcs of the less detailled network.
   * @return int
   */
  public int getNbArcRef() {
    return nbArcRef;
  }
  
  /**
   * @param n
   *          Number of arcs of the less detailled network to set.
   */
  public void setNbArcRef(int n) {
    nbArcRef = n;
  }
  
  /**
   * Return number of arcs of the comparison network.
   * @return int
   */
  public int getNbArcComp() {
    return nbArcComp;
  }
  
  /**
   * @param n
   *          Number of arcs of the comparison network to set.
   */
  public void setNbArcComp(int n) {
    nbArcComp = n;
  }
  
  /**
   * Return Network matched.
   * @return SimpleFeatureCollection
   */
  public SimpleFeatureCollection getNetworkMatched() {
    return networkMatched;
  }

}
