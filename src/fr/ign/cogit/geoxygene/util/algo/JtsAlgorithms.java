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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
  * Appel des methodes JTS sur des GM_Object.
  * cf. http://www.vividsolutions.com/jts/jtshome.htm
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */

public class JtsAlgorithms implements GeomAlgorithms {    
    
    
    public JtsAlgorithms() {
    }


    public GM_Object centroid(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            Geometry jtsCentroid=jtsGeom.getCentroid();
            return (JtsGeOxygene.makeGeOxygeneGeom(jtsCentroid));
        } catch (Exception e) {
            System.out.println("## CALCUL DE CENTROIDE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }
    }

    public GM_Object convexHull(GM_Object geom)  {  
        try { 
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);           
            Geometry jtsHull=jtsGeom.convexHull();               
            GM_Object result=JtsGeOxygene.makeGeOxygeneGeom(jtsHull);
            return result;
        } catch (Exception e) {
            System.out.println("## CALCUL D'ENVELOPPE CONVEXE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }
    }
    
     public GM_Object buffer (GM_Object geom, double distance) {
         try {            
             Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);             
             Geometry jtsBuffer=jtsGeom.buffer(distance);
             return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
         } catch (Exception e) {
             System.out.println("## CALCUL DE BUFFER AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
             e.printStackTrace();
             return null;
         }
     }
     
	public GM_Object buffer (GM_Object geom, double distance, int nSegments) {
		try {            
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);             
			Geometry jtsBuffer=jtsGeom.buffer(distance,nSegments);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
		} catch (Exception e) {
			System.out.println("## CALCUL DE BUFFER AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			e.printStackTrace();
			return null;
		}
	}
          
    public GM_Object buffer10 (GM_Object geom) {
        return buffer(geom,10);
    }
 
     public GM_Object boundary(GM_Object geom) {
         try {
             Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(geom);             
             Geometry jtsResult=jtsGeom1.getBoundary();
             return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);       
         } catch (Exception e) {
             System.out.println("## CALCUL DE FRONTIERE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
             e.printStackTrace();
             return null;
         }             
     }

    public GM_Object union(GM_Object g1, GM_Object g2)  {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            Geometry jtsUnion=jtsGeom1.union(jtsGeom2);
            return JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
        } catch (Exception e) {
            System.out.println("## CALCUL D'UNION AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }                        
    }   
    
    public GM_Object intersection(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            Geometry jtsInter=jtsGeom1.intersection(jtsGeom2);
            return JtsGeOxygene.makeGeOxygeneGeom(jtsInter);
        } catch (Exception e) {
            System.out.println("## CALCUL D'INTERSECTION AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }            
    }    

    public GM_Object difference(GM_Object g1, GM_Object g2)   {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            Geometry jtsResult=jtsGeom1.difference(jtsGeom2);       
            return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
        } catch (Exception e) {
            System.out.println("## CALCUL DE DIFFERENCE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }            
    }    

    public GM_Object symDifference(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            Geometry jtsSymDiff=jtsGeom1.symDifference(jtsGeom2);
            return JtsGeOxygene.makeGeOxygeneGeom(jtsSymDiff);
        } catch (Exception e) {
            System.out.println("## CALCUL DE DIFFERENCE SYMETRIQUE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }                        
    }    
    
    public boolean equals(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.equals(jtsGeom2);    
        } catch (Exception e) {
            System.out.println("## PREDICAT EQUALS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                       
    }    

    public boolean equalsExact(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.equalsExact(jtsGeom2); 
        } catch (Exception e) {
            System.out.println("## PREDICAT EQUALSEXACT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                    
    }    
    
	public boolean equalsExact(GM_Object g1, GM_Object g2, double tol) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
			return jtsGeom1.equalsExact(jtsGeom2,tol); 
		} catch (Exception e) {
			System.out.println("## PREDICAT EQUALSEXACT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			e.printStackTrace();
			return false;
		}                                                    
	}        

    public boolean contains(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.contains(jtsGeom2);       
        } catch (Exception e) {
            System.out.println("## PREDICAT CONTAINS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean crosses(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.crosses(jtsGeom2);       
        } catch (Exception e) {
            System.out.println("## PREDICAT CROSSES AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean disjoint(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.disjoint(jtsGeom2);    
        } catch (Exception e) {
            System.out.println("## PREDICAT DISJOINT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                      
    }    

    public boolean within(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            return jtsGeom1.within(jtsGeom2);
        } catch (Exception e) {
            System.out.println("## PREDICAT WITHIN AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }
	
	public boolean isWithinDistance (GM_Object g1, GM_Object g2, double dist) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.isWithinDistance(jtsGeom2,dist);
		} catch (Exception e) {
			System.out.println("## PREDICAT iisWithinDistance AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			e.printStackTrace();
			return false;
		}                                                   
	}
	
    public boolean intersects(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            return jtsGeom1.intersects(jtsGeom2);
        } catch (Exception e) {
            System.out.println("## PREDICAT INTERSECTS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean overlaps(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            return jtsGeom1.overlaps(jtsGeom2);
        } catch (Exception e) {
            System.out.println("## PREDICAT OVERLAPS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean touches(GM_Object g1, GM_Object g2) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
            return jtsGeom1.touches(jtsGeom2);
        } catch (Exception e) {
            System.out.println("## PREDICAT TOUCHES AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean isEmpty(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.isEmpty();
        } catch (Exception e) {
            System.out.println("## ISEMPTY() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                   
    }

    public boolean isSimple(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.isSimple();
        } catch (Exception e) {
            System.out.println("## ISSIMPLE() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                               
    }

    public boolean isValid(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.isValid();
        } catch (Exception e) {
            System.out.println("## ISVALID() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
            e.printStackTrace();
            return false;
        }                                                               
    }

    public double distance(GM_Object g1, GM_Object g2)  {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);             
            Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);             
            return jtsGeom1.distance(jtsGeom2);   
        } catch (Exception e) {
            System.out.println("## DISTANCE() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
            e.printStackTrace();
            return 0.0;
        }                                                                               
    }    

    public double area(GM_Object geom) {
        try {
            Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(geom);             
            return jtsGeom1.getArea();     
        } catch (Exception e) {
            System.out.println("## AREA() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
            e.printStackTrace();
            return 0.0;
        }                                                                               
    }    

    public double length(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.getLength();
        } catch (Exception e) {
            System.out.println("## LENGTH() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
            e.printStackTrace();
            return 0.0;
        }                                                                                           
    }

    public int dimension(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.getDimension();
        } catch (Exception e) {
            System.out.println("## DIMENSION() AVEC JTS : PROBLEME (le resultat renvoie 0) ##");
            e.printStackTrace();
            return 0;
        }                                                                                                       
    }

    public int numPoints(GM_Object geom) {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            return jtsGeom.getNumPoints();
        } catch (Exception e) {
            System.out.println("## NUMPOINTS() AVEC JTS : PROBLEME (le resultat renvoie 0) ##");
            e.printStackTrace();
            return 0;
        }                                                                                                                   
    }    

    public GM_Object translate(GM_Object geom, final double tx, final double ty, final double tz)  {
        try {
            Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
            CoordinateFilter translateCoord=new CoordinateFilter() {
                public void filter(Coordinate coord) {
                    coord.x+=tx;
                    coord.y+=ty;
                    coord.z+=tz;
                }
            };
            jtsGeom.apply(translateCoord);
            GM_Object result=JtsGeOxygene.makeGeOxygeneGeom(jtsGeom);
            return result;
        } catch (Exception e) {
            System.out.println("## TRANSLATE() AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
            e.printStackTrace();
            return null;
        }                                                                                                                   
    }
    
	public String relate(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.relate(jtsGeom2).toString();
		} catch (Exception e) {
			System.out.println("## RELATE AVEC JTS : PROBLEME ##");
			e.printStackTrace();
			return " RELATE AVEC JTS : PROBLEME ";
		}                                                   
	}


} // class
