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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
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
	static Logger logger=Logger.getLogger(JtsAlgorithms.class.getName());

	public JtsAlgorithms() {}

	public DirectPosition centroid(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Point jtsCentroid=jtsGeom.getCentroid();
			return new DirectPosition(jtsCentroid.getX(),jtsCentroid.getY());
		} catch (Exception e) {
			logger.error("## CALCUL DE CENTROIDE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
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
			logger.error("## CALCUL D'ENVELOPPE CONVEXE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return null;
		}
	}

	public GM_Object buffer (GM_Object geom, double distance) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Geometry jtsBuffer=jtsGeom.buffer(distance);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
		} catch (Exception e) {
			logger.error("## CALCUL DE BUFFER AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) {
				logger.debug("Buffer de distance "+distance+" avec la géométrie :");
				logger.debug((geom!=null)?geom.toString():"null");
				logger.debug(e.getMessage());
			}
			return null;
		}
	}

	public GM_Object buffer (GM_Object geom, double distance, int nSegments) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Geometry jtsBuffer=jtsGeom.buffer(distance,nSegments);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
		} catch (Exception e) {
			logger.error("## CALCUL DE BUFFER AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) {
				logger.debug("Buffer de distance "+distance+" avec "+nSegments+" segments et la géométrie :");
				logger.debug((geom!=null)?geom.toString():"null");
				logger.debug(e.getMessage());
			}
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
			logger.error("## CALCUL DE FRONTIERE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
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
			logger.error("## CALCUL D'UNION AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
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
			logger.error("## CALCUL D'INTERSECTION AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return null;
		}
	}

	public GM_Object difference(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Geometry jtsResult=jtsGeom1.difference(jtsGeom2);
			//if (jtsResult.isEmpty()||jtsResult.getArea()==0.0) return null;
			return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
		} catch (Exception e) {
			logger.error("## CALCUL DE DIFFERENCE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
				logger.debug("Les géométries concernées sont :");
				logger.debug(g1);
				logger.debug(g2);
			}
			//e.printStackTrace();
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
			logger.error("## CALCUL DE DIFFERENCE SYMETRIQUE AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return null;
		}
	}

	public boolean equals(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.equals(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT EQUALS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean equalsExact(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.equalsExact(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT EQUALSEXACT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean equalsExact(GM_Object g1, GM_Object g2, double tol) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.equalsExact(jtsGeom2,tol);
		} catch (Exception e) {
			logger.error("## PREDICAT EQUALSEXACT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean contains(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.contains(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT CONTAINS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) {
				logger.debug("Les deux géométries concernées sont :");
				logger.debug((g1!=null)?g1.toString():"null");
				logger.debug((g2!=null)?g2.toString():"null");
				logger.debug(e.getMessage());
			}
			//e.printStackTrace();
			return false;
		}
	}

	public boolean crosses(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.crosses(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT CROSSES AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean disjoint(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.disjoint(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT DISJOINT AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean within(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.within(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT WITHIN AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean isWithinDistance (GM_Object g1, GM_Object g2, double dist) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.isWithinDistance(jtsGeom2,dist);
		} catch (Exception e) {
			logger.error("## PREDICAT iisWithinDistance AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean intersects(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.intersects(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT INTERSECTS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) {
				logger.debug("Les deux géométries concernées sont :");
				logger.debug((g1!=null)?g1.toString():"null");
				logger.debug((g2!=null)?g2.toString():"null");
				logger.debug(e.getMessage());
			}
			//e.printStackTrace();
			return false;
		}
	}

	public boolean overlaps(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.overlaps(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT OVERLAPS AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean touches(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.touches(jtsGeom2);
		} catch (Exception e) {
			logger.error("## PREDICAT TOUCHES AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean isEmpty(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isEmpty();
		} catch (Exception e) {
			//logger.error("## ISEMPTY() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			//if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return true;
		}
	}

	public boolean isSimple(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isSimple();
		} catch (Exception e) {
			logger.error("## ISSIMPLE() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public boolean isValid(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isValid();
		} catch (Exception e) {
			logger.error("## ISVALID() AVEC JTS : PROBLEME (le resultat renvoie FALSE) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return false;
		}
	}

	public double distance(GM_Object g1, GM_Object g2)  {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.distance(jtsGeom2);
		} catch (Exception e) {
			logger.error("## DISTANCE() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
				logger.debug("géométrie 1 = "+g1);
				logger.debug("géométrie 2 = "+g2);
			}
			//e.printStackTrace();
			return 0.0;
		}
	}

	public double area(GM_Object geom) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom1.getArea();
		} catch (Exception e) {
			logger.error("## AREA() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return 0.0;
		}
	}

	public double length(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getLength();
		} catch (Exception e) {
			logger.error("## LENGTH() AVEC JTS : PROBLEME (le resultat renvoie 0.0) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return 0.0;
		}
	}

	public int dimension(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getDimension();
		} catch (Exception e) {
			logger.error("## DIMENSION() AVEC JTS : PROBLEME (le resultat renvoie 0) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return 0;
		}
	}

	public int numPoints(GM_Object geom) {
		try {
			if (geom.isEmpty()) return 0;
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getNumPoints();
		} catch (Exception e) {
			logger.error("## NUMPOINTS() AVEC JTS : PROBLEME (le resultat renvoie 0) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
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
			logger.error("## TRANSLATE() AVEC JTS : PROBLEME (le resultat renvoie NULL) ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return null;
		}
	}

	public String relate(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.relate(jtsGeom2).toString();
		} catch (Exception e) {
			logger.error("## RELATE AVEC JTS : PROBLEME ##");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			//e.printStackTrace();
			return " RELATE AVEC JTS : PROBLEME ";
		}
	}
	
	/**
	 * Calcul de l'union d'une liste de géométries
	 * @param listeGeometries liste des géométries à unir
	 * @return union d'une liste de géométries
	 */
	public GM_Object union(List<GM_Object> listeGeometries) {
		List<Geometry> listeGeometriesJts = new ArrayList<Geometry>();
		for(GM_Object geom:listeGeometries) {
			try {listeGeometriesJts.add(JtsGeOxygene.makeJtsGeom(geom));}
			catch(Exception e){
				logger.error("Erreur dans la construction d'une géométrie JTS à partir d'un GM_Objet. L'objet est ignoré et le calcul de l'union continue sans cet objet");
				if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			}
		}
		Geometry union = union(listeGeometriesJts);
		try {return JtsGeOxygene.makeGeOxygeneGeom(union);}
		catch(Exception e) {
			logger.error("Erreur dans la construction d'unee géométrie GéOxygène à partir d'une géomtrie JTS. Le résultat renvoyé est null.");
			if (logger.isDebugEnabled()) logger.debug(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Détermine le point d'un polygone le plus loin d'un autre point.
	 * Le polygone doit être convexe et sans trou.
	 * Determine the farest point of a polygon to another given point.
	 * The polygon must be convex and without hole.
	 * 
	 * @param pt un point, a point
	 * @param poly un polygone convexe sans trou, a convex polygon without hole
	 */
	public static Point getPointLePlusLoin(Point pt, Polygon poly){
		Point pt_max=poly.getExteriorRing().getPointN(0);
		double dist_max=pt.distance(pt_max);
		for (int i=1; i<poly.getExteriorRing().getNumPoints(); i++){
			double dist=pt.distance(poly.getExteriorRing().getPointN(i));
			if (dist>dist_max) {
				pt_max=poly.getExteriorRing().getPointN(i);
				dist_max=dist;
			}
		}
		return pt_max;
	}

	/**
	 * Détermine le point d'un polygone le plus proche d'un autre point.
	 * Determine the closest point of a polygon to another given point.
	 * 
	 * @param pt un point, a point
	 * @param poly un polygone, a polygon
	 */
	public static DirectPosition getPointLePlusProche(DirectPosition pt, GM_Polygon poly){
		return getPointLePlusProche(pt, poly.exteriorLineString());
	}

	/**
	 * Détermine le point d'une ligne le plus proche d'un autre point.
	 * Determine the closest point of a line to another given point.
	 * 
	 * @param pt un point, a point
	 * @param l une ligne, a line
	 */
	public static DirectPosition getPointLePlusProche(DirectPosition pt, GM_LineString l) {
		Point point = new GeometryFactory().createPoint(AdapterFactory.toCoordinate(pt));
		LineString line;
		try {
			line = (LineString) AdapterFactory.toGeometry(new GeometryFactory(), l);
			Coordinate[] cp=(new DistanceOp(line, point)).nearestPoints();
			return AdapterFactory.toDirectPosition(line.getFactory().createPoint(cp[0]).getCoordinate());
		} catch (Exception e) {}
		return null;
	}
	/**
	 * Détermine le point d'une ligne le plus loin d'une ligne de base.
	 * Determine the closest point of a line to another given line.
	 * 
	 * @param base la ligne de comparaison, the base line
	 * @param l une ligne, a line
	 */
	public static DirectPosition getPointLePlusLoin(GM_LineString base, GM_LineString l) {
		try {
			LineString baseLine = (LineString) AdapterFactory.toGeometry(new GeometryFactory(), base);
			LineString line = (LineString) AdapterFactory.toGeometry(new GeometryFactory(), l);
			double distanceMax = Double.MIN_VALUE;
			Point pointLePlusLoin = null;
			for (int i=0 ; i<line.getNumPoints() ; i++) {
				Point p = line.getPointN(i);
				double distance = p.distance(baseLine);
				if (distance>distanceMax) {
					distanceMax=distance;
					pointLePlusLoin = p;
				}
			}
			if (pointLePlusLoin!=null) return AdapterFactory.toDirectPosition(pointLePlusLoin.getCoordinate());
			//return AdapterFactory.toDirectPosition(JtsUtil.getPointLePlusProche(point, line).getCoordinate());
		} catch (Exception e) {}
		return null;
	}

	static JtsAlgorithms singleton = new JtsAlgorithms();
	protected static EventListenerList listenerList = new EventListenerList();

	/**
	 * Adds an <code>ActionListener</code>.
	 * @param l the <code>ActionListener</code> to be added
	 */
	public static void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 * @see EventListenerList
	 */
	protected static void fireActionPerformed(ActionEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ActionListener.class) {
				// Lazily create the event:
				((ActionListener)listeners[i+1]).actionPerformed(event);
			}
		}
	}

	/**
	 * Union d'un tableau de Polygones
	 * @param geometryArray tableau de Polygones JTS
	 * @return union des Polygones
	 */
	public static Geometry union(Geometry[] geometryArray) {
		List<Geometry> liste = new ArrayList<Geometry>();
		for (int index=0; index <geometryArray.length;index++)
			liste.add(geometryArray[index]);
		return union(liste);
	}

	/**
	 * Union d'une collection de Polygones
	 * @param geometryCollection collection de Polygones JTS
	 * @return union des Polygones
	 */
	public static Geometry union(Collection<Geometry> geometryCollection) {
		final int cellSize = 1 + (int)Math.sqrt(geometryCollection.size());
		Comparator<Geometry> comparator =  new Comparator<Geometry>(){
			public int compare(Geometry o1, Geometry o2) {
				if (o1==null || o2==null) return 0;
				Envelope env1 = o1.getEnvelopeInternal();
				Envelope env2 = o2.getEnvelopeInternal();
				double indice1 = env1.getMinX()/cellSize + cellSize*((int)env1.getMinY()/cellSize);
				double indice2 = env2.getMinX()/cellSize + cellSize*((int)env2.getMinY()/cellSize);
				return indice1>=indice2?1:indice1<indice2?-1:0;
			}
			@Override
			public boolean equals(Object obj) {return this.equals(obj);}
		};
		int iteration = 1;
		int nbIteration = 1 + (int)(Math.log(geometryCollection.size())/Math.log(4));
		fireActionPerformed(new ActionEvent(singleton,0,"union",nbIteration));
		while (geometryCollection.size() > 1) {
			fireActionPerformed(new ActionEvent(singleton,1,"union-iteration",iteration++));
			if (logger.isTraceEnabled()) logger.trace("Union (" + iteration + "/" + nbIteration + ")");
			TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
			treeSet.addAll(geometryCollection);
			geometryCollection = union(treeSet, 4);
		}
		List<Polygon> polygons = new ArrayList<Polygon>();
		for (Geometry geom:geometryCollection) {
			if (geom instanceof Polygon) polygons.add((Polygon) geom);
			else if (geom instanceof MultiPolygon) {
				MultiPolygon multiPolygon = (MultiPolygon) geom;
				for (int index = 0;index < multiPolygon.getNumGeometries();index++)
					polygons.add((Polygon)multiPolygon.getGeometryN(index));
			} else
				logger.error("géométrie de type non géré "+geom.getGeometryType());
		}
		fireActionPerformed(new ActionEvent(singleton,4,"union-fin"));
		if (polygons.size()==1) return polygons.get(0);
		if (geometryCollection.isEmpty()) return new GeometryFactory().createGeometryCollection(new Geometry[0]);
		return geometryCollection.iterator().next().getFactory().createMultiPolygon(polygons.toArray(new Polygon[0]));
	}

	/**
	 * Union des éléments d'un ensemble de Polygones triés par groupes.
	 * Par exemple, si la taille des groupes vaut 4, on effectue l'union des Polygones 4 par 4.
	 * 
	 * @param treeSet ensemble de Polygones triés
	 * @param groupSize taille des groupes sur lesquels on effectue l'union
	 * @return liste des unions
	 */
	private static List<Geometry> union(TreeSet<Geometry> treeSet, int groupSize) {
		List<Geometry> unionGeometryList = new ArrayList<Geometry>();
		Geometry currUnion = null;
		int size = treeSet.size();
		int count = 0;
		fireActionPerformed(new ActionEvent(singleton,2,"union-detail",size));
		for (Geometry geom:treeSet) {
			if ((currUnion==null)||(count%groupSize==0)) currUnion = geom;
			else {
				currUnion = currUnion.union(geom);
				if (groupSize-count%groupSize==1) unionGeometryList.add(currUnion);
			}
			fireActionPerformed(new ActionEvent(singleton,3,"union-detail-iteration",++count));
			if (logger.isTraceEnabled()) logger.trace(" "+(count)+" - "+size+" features");
		}
		if (groupSize-count%groupSize!=0) {
			unionGeometryList.add(currUnion);
		}
		return unionGeometryList;
	}

	/**
	 * Union d'une collection de LineStrings.
	 * @param geometryCollection collection de LineStrings
	 * @return Union des LineString (une LineString)
	 */
	public static Geometry unionLineString(List<Geometry> geometryCollection) {
		final int cellSize = 1 + (int)Math.sqrt(geometryCollection.size());
		Comparator<Geometry> comparator =  new Comparator<Geometry>(){
			public int compare(Geometry o1, Geometry o2) {
				if (o1==null || o2==null) return 0;
				Envelope env1 = o1.getEnvelopeInternal();
				Envelope env2 = o2.getEnvelopeInternal();
				double indice1 = env1.getMinX()/cellSize + cellSize*((int)env1.getMinY()/cellSize);
				double indice2 = env2.getMinX()/cellSize + cellSize*((int)env2.getMinY()/cellSize);
				return indice1>=indice2?1:indice1<indice2?-1:0;
			}
			@Override
			public boolean equals(Object obj) {return this.equals(obj);}
		};
		int iteration = 1;
		int nbIteration = 1 + (int)(Math.log(geometryCollection.size())/Math.log(4));
		fireActionPerformed(new ActionEvent(singleton,0,"union",nbIteration));
		while (geometryCollection.size() > 1) {
			fireActionPerformed(new ActionEvent(singleton,1,"union-iteration",iteration++));
			if (logger.isTraceEnabled()) logger.trace("Union (" + iteration + "/" + nbIteration + ")");
			TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
			treeSet.addAll(geometryCollection);
			geometryCollection = unionLineString(treeSet, 4);
		}
		fireActionPerformed(new ActionEvent(singleton,4,"union-fin"));
		return geometryCollection.get(0);
	}
	/**
	 * Union des éléments d'un ensemble de LineStrings triées par groupes.
	 * Par exemple, si la taille des groupes vaut 4, on effectue l'union des LineStrings 4 par 4.
	 * 
	 * @param treeSet ensemble de LineStrings triées
	 * @param groupSize taille des groupes sur lesquels on effectue l'union
	 * @return liste des unions
	 */
	private static List<Geometry> unionLineString(TreeSet<Geometry> treeSet, int groupSize) {
		List<Geometry> unionGeometryList = new ArrayList<Geometry>();
		Geometry currUnion = null;
		int size = treeSet.size();
		int count = 0;
		fireActionPerformed(new ActionEvent(singleton,2,"union-detail",size));
		for (Geometry geom:treeSet) {
			if ((currUnion==null)||(count%groupSize==0)) currUnion = geom;
			else {
				currUnion = currUnion.union(geom);
				if (groupSize-count%groupSize==1) unionGeometryList.add(currUnion);
			}
			fireActionPerformed(new ActionEvent(singleton,3,"union-detail-iteration",++count));
			if (logger.isTraceEnabled()) logger.trace(" "+(count)+" - "+size+" features");
		}
		if (groupSize-count%groupSize!=0) {
			unionGeometryList.add(currUnion);
		}
		return unionGeometryList;
	}

	public static boolean isCCW(GM_LineString line) {
		Coordinate[] coords = AdapterFactory.toCoordinateSequence(new GeometryFactory(), line.coord()).toCoordinateArray();
		return CGAlgorithms.isCCW(coords);
	}
} // class
