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

package fr.ign.cogit.geoxygene.datatools.oracle;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.GeomAlgorithms;

/** 
  * Appel des methodes Oracle sur des GM_Object.
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.1  
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
       return OracleSpatialQuery.bufferAgregat(data,tolerance,radius,geom);
   }        
   
   public GM_Object buffer (GM_Object geom, double radius) {
     return OracleSpatialQuery.buffer(data,tolerance,radius,geom);
   }
           
   public GM_Object buffer10(GM_Object geOxyGeom)  {
       return OracleSpatialQuery.bufferAgregat(data,tolerance,10,geOxyGeom);
   }           
           
   public GM_Object convexHull (GM_Object geom)  {
       return OracleSpatialQuery.convexHull(data,tolerance,geom);
   }
   
   public GM_Object centroid (GM_Object geom)  {
       return new GM_Point( OracleSpatialQuery.centroid(data,tolerance,geom) );
   }

   public DirectPosition representativePoint (GM_Object geom) {
       return OracleSpatialQuery.representativePoint(data,tolerance,geom);
   }
   
   public GM_Envelope envelope (GM_Object geom) {
       return OracleSpatialQuery.mbr(data,geom);
   }   
    
   public GM_Object difference (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.difference(data,tolerance,g1,g2);
   }  
       
   public GM_Object intersection (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.intersection(data,tolerance,g1,g2);
   }
   
   public GM_Object union (GM_Object g1, GM_Object g2) {
       return OracleSpatialQuery.union(data,tolerance,g1,g2);
   }   
       
   public GM_Object symDifference (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.symmetricDifference(data,tolerance,g1,g2);
   }       
       
   public boolean contains (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.contains(data,tolerance,g1,g2);
   }
       
   public boolean contains (GM_Object g, DirectPosition P)  {
       return OracleSpatialQuery.contains(data,tolerance,g,P);
   }

   public boolean intersects (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.intersects(data,tolerance,g1,g2);
   }
     
   public boolean equals (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.equals(data,tolerance,g1,g2);
   }
       
   public boolean isSimple(GM_Object geom)  {
       return OracleSpatialQuery.isSimple(geom);   
   }
   
   public double area(GM_Object geom)  {
       return OracleSpatialQuery.area(geom);   
   }
   
   public double length( GM_Object geom)  {
       return OracleSpatialQuery.length(geom);   
   }   
    
   public double distance (GM_Object g1, GM_Object g2)  {
       return OracleSpatialQuery.distance(data,tolerance,g1,g2);
   }

 
} // class
