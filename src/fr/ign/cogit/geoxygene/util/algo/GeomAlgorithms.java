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

package fr.ign.cogit.geoxygene.util.algo;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** 
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */

public interface GeomAlgorithms {
    
	public GM_Object centroid(GM_Object geom) ;
    
    public GM_Object convexHull(GM_Object geOxyGeom) ;

    public GM_Object buffer(GM_Object geOxyGeom, double distance) ;

    public GM_Object buffer10(GM_Object geOxyGeom) ;

    public GM_Object difference(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public GM_Object intersection(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public GM_Object union(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public GM_Object symDifference(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public boolean contains(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public boolean intersects(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public double distance(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

    public double length(GM_Object geOxyGeom1) ;

    public double area(GM_Object geOxyGeom1) ;
    
    public boolean equals(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;

//  public GM_Envelope envelope(GM_Object geOxyGeom) ;
//  public boolean isValid(GM_Object geOxyGeom) ;    
//  public GM_Object translate(GM_Object geom, double tx, double ty, double tz) ;
//  DirectPositionList coordinates(GM_Object geOxyGeom1) ;
//	GM_Object boundary(GM_Object geOxyGeom1) ;
//	boolean equals(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean equalsExact(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean crosses(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean disjoint(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean within(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean overlaps(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean touches(GM_Object geOxyGeom1, GM_Object geOxyGeom2) ;
//	boolean isEmpty(GM_Object geOxyGeom) ;
//	boolean isSimple(GM_Object geOxyGeom) ;
//	int dimension(GM_Object geOxyGeom) ;
//	int numPoints(GM_Object geOxyGeom) ;

}
