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

package fr.ign.cogit.geoxygene.util.algo;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.oracle.SpatialQuery;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** 
  * Appel des methodes Oracle sur des GM_Object.
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */

public class OracleAlgorithms implements GeomAlgorithms {

    Geodatabase data;
    double tolerance;
 
 
    public OracleAlgorithms (Geodatabase db, double tol) {
        data = db;
        tolerance = tol;       
    }


   public  GM_Object bufferAgregat (GM_Object geom, double radius) {
       return SpatialQuery.bufferAgregat(data,tolerance,radius,geom);
   }        
   
   public GM_Object buffer (GM_Object geom, double radius) {
     return SpatialQuery.buffer(data,tolerance,radius,geom);
   }
           
   public GM_Object buffer10(GM_Object geOxyGeom)  {
       return SpatialQuery.bufferAgregat(data,tolerance,10,geOxyGeom);
   }           
           
   public GM_Object convexHull (GM_Object geom)  {
       return SpatialQuery.convexHull(data,tolerance,geom);
   }
   
   public GM_Object centroid (GM_Object geom)  {
       return new GM_Point( SpatialQuery.centroid(data,tolerance,geom) );
   }

   public DirectPosition representativePoint (GM_Object geom) {
       return SpatialQuery.representativePoint(data,tolerance,geom);
   }
   
   public GM_Envelope envelope (GM_Object geom) {
       return SpatialQuery.mbr(data,geom);
   }   
    
   public GM_Object difference (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.difference(data,tolerance,g1,g2);
   }  
       
   public GM_Object intersection (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.intersection(data,tolerance,g1,g2);
   }
   
   public GM_Object union (GM_Object g1, GM_Object g2) {
       return SpatialQuery.union(data,tolerance,g1,g2);
   }   
       
   public GM_Object symDifference (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.symmetricDifference(data,tolerance,g1,g2);
   }       
       
   public boolean contains (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.contains(data,tolerance,g1,g2);
   }
       
   public boolean contains (GM_Object g, DirectPosition P)  {
       return SpatialQuery.contains(data,tolerance,g,P);
   }

   public boolean intersects (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.intersects(data,tolerance,g1,g2);
   }
     
   public boolean equals (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.equals(data,tolerance,g1,g2);
   }
       
   public boolean isSimple(GM_Object geom)  {
       return SpatialQuery.isSimple(geom);   
   }
   
   public double area(GM_Object geom)  {
       return SpatialQuery.area(geom);   
   }
   
   public double length( GM_Object geom)  {
       return SpatialQuery.length(geom);   
   }   
    
   public double distance (GM_Object g1, GM_Object g2)  {
       return SpatialQuery.distance(data,tolerance,g1,g2);
   }

 
} // class
