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

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/** 
  * Appel des methodes GEOS sur des GM_Object.
  * Non finalisé.
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */

public class GeosAlgorithms implements GeomAlgorithms {

    public GeosAlgorithms() {
    	System.loadLibrary("GeosAlgorithms");
    }
    
    private native String intersection(String wkt1, String wkt2);
    public GM_Object intersection(GM_Object geom1, GM_Object geom2)  {
        try {
            	String wkt1=WktGeOxygene.makeWkt(geom1);
            	String wkt2=WktGeOxygene.makeWkt(geom2);
            	String wktResult=intersection(wkt1,wkt2);
            	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private native String union(String wkt1, String wkt2);
    public GM_Object union(GM_Object geom1, GM_Object geom2) {
        try {
        	String wkt1=WktGeOxygene.makeWkt(geom1);
        	String wkt2=WktGeOxygene.makeWkt(geom2);
        	String wktResult=union(wkt1,wkt2);
        	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }            
    }
    
    private native String difference(String wkt1, String wkt2);
    public GM_Object difference(GM_Object geom1, GM_Object geom2) {
        try {
        	String wkt1=WktGeOxygene.makeWkt(geom1);
        	String wkt2=WktGeOxygene.makeWkt(geom2);
        	String wktResult=difference(wkt1,wkt2);
        	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }            
    }
    
    private native String symDifference(String wkt1, String wkt2);
    public GM_Object symDifference(GM_Object geom1, GM_Object geom2) {
        try {
        	String wkt1=WktGeOxygene.makeWkt(geom1);
        	String wkt2=WktGeOxygene.makeWkt(geom2);
        	String wktResult=symDifference(wkt1,wkt2);
        	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }            
    }
    
    private native String buffer(String wkt, double distance);
    public GM_Object buffer(GM_Object geom, double distance) {
        try {
        	String wkt=WktGeOxygene.makeWkt(geom);
        	String wktResult=buffer(wkt,distance);
        	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }            
    }
    
    public GM_Object buffer10(GM_Object geOxyGeom)  {
    	return buffer(geOxyGeom,10);
    }
    
    private native String convexHull(String wkt);
    public GM_Object convexHull(GM_Object geOxyGeom)  {
        try {
        	String wkt=WktGeOxygene.makeWkt(geOxyGeom);
        	String wktResult=convexHull(wkt);
        	return WktGeOxygene.makeGeOxygene(wktResult);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }            
    }
    
    private native boolean contains(String wkt1, String wkt2);
    public boolean contains(GM_Object geOxyGeom1, GM_Object geOxyGeom2)  {
    	String wkt1=WktGeOxygene.makeWkt(geOxyGeom1);
    	String wkt2=WktGeOxygene.makeWkt(geOxyGeom2);
    	boolean result=contains(wkt1,wkt2);
    	return result;
    }
    
    private native boolean intersects(String wkt1, String wkt2);
    public boolean intersects(GM_Object geOxyGeom1, GM_Object geOxyGeom2)  {
    	String wkt1=WktGeOxygene.makeWkt(geOxyGeom1);
    	String wkt2=WktGeOxygene.makeWkt(geOxyGeom2);
    	boolean result=intersects(wkt1,wkt2);
    	return result;
    }
    
    private native double distance(String wkt1, String wkt2);
    public double distance(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	String wkt1=WktGeOxygene.makeWkt(geOxyGeom1);
    	String wkt2=WktGeOxygene.makeWkt(geOxyGeom2);
    	double result=distance(wkt1,wkt2);
    	return result;
    }
    
    private native double area(String wkt);
    public double area(GM_Object geOxyGeom1) {
    	String wkt1=WktGeOxygene.makeWkt(geOxyGeom1);
    	double result=area(wkt1);
    	return result;
    }
    
    private native String boundary(String wkt);
    public GM_Object boundary(GM_Object geOxyGeom1) {
    	return null;
    }
    
    private native String coordinates(String wkt);
    public DirectPositionList coordinates(GM_Object geOxyGeom1) {
    	return null;
    }
    
    private native String envelope(String wkt);
    public GM_Envelope envelope(GM_Object geOxyGeom) {
    	return null;
    }
    
    private native boolean equals(String wkt1, String wkt2);
    public boolean equals(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean equalsExact(String wkt1, String wkt2);
    public boolean equalsExact(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean crosses(String wkt1, String wkt2);
    public boolean crosses(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean disjoint(String wkt1, String wkt2);
    public boolean disjoint(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean within(String wkt1, String wkt2);
    public boolean within(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean overlaps(String wkt1, String wkt2);
    public boolean overlaps(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean touches(String wkt1, String wkt2);
    public boolean touches(GM_Object geOxyGeom1, GM_Object geOxyGeom2) {
    	return false;
    }
    
    private native boolean isEmpty(String wkt);
    public boolean isEmpty(GM_Object geOxyGeom) {
    	return false;
    }
    
    private native boolean isSimple(String wkt);
    public boolean isSimple(GM_Object geOxyGeom) {
    	return false;
    }
    
    private native boolean isValid(String wkt);
    public boolean isValid(GM_Object geOxyGeom) {
    	return false;
    }
    
    public int dimension(GM_Object geOxyGeom) {
    	return 0;
    }
    
    public double length(GM_Object geOxyGeom) {
    	return 0;
    }
    
    public int numPoints(GM_Object geOxyGeom) {
    	return 0;
    }
    
    public GM_Object translate(GM_Object geom, double tx, double ty, double tz) {
    	return null;
    }
    
	public GM_Object centroid(GM_Object geom)  {
		return null;
	}

}
