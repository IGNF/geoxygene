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
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
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
	static Logger logger = Logger.getLogger(JtsAlgorithms.class.getName());

	public JtsAlgorithms() {}

	@Override
	public DirectPosition centroid(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Point jtsCentroid=jtsGeom.getCentroid();
			return new DirectPosition(jtsCentroid.getX(),jtsCentroid.getY());
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.CentroidError")); //$NON-NLS-1$
			logger.error(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
			if (logger.isDebugEnabled()) {
			    logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object convexHull(GM_Object geom)  {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Geometry jtsHull=jtsGeom.convexHull();
			GM_Object result=JtsGeOxygene.makeGeOxygeneGeom(jtsHull);
			return result;
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.ConvexHullError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
			    logger.debug(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object buffer (GM_Object geom, double distance) {
		if ((distance==0)&&(geom instanceof GM_Point)) return geom;
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			Geometry jtsBuffer=jtsGeom.buffer(distance);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.BufferDistance")+distance); //$NON-NLS-1$ 
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.BufferDistance")+distance); //$NON-NLS-1$ 
				logger.debug(I18N.getString("JtsAlgorithms.BufferSegments")+nSegments); //$NON-NLS-1$ 
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	public GM_Object buffer (GM_Object geom, double distance, int nSegments, int cap) {
	    try {
	        Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
	        Geometry jtsBuffer=jtsGeom.buffer(distance,nSegments,cap);
	        return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
	    } catch (Exception e) {
	        logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
	        if (logger.isDebugEnabled()) {
	            logger.debug(I18N.getString("JtsAlgorithms.BufferDistance")+distance); //$NON-NLS-1$ 
	            logger.debug(I18N.getString("JtsAlgorithms.BufferSegments")+nSegments); //$NON-NLS-1$ 
                logger.debug(I18N.getString("JtsAlgorithms.Cap")+cap); //$NON-NLS-1$ 
	            logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
	            logger.debug(e.getMessage());
	        }
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public GM_Object buffer10 (GM_Object geom) {
		return buffer(geom,10);
	}

	public GM_Object boundary(GM_Object geom) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(geom);
			Geometry jtsResult=jtsGeom1.getBoundary();
			return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.BoundaryError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object union(GM_Object g1, GM_Object g2)  {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Geometry jtsUnion=jtsGeom1.union(jtsGeom2);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.UnionError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object intersection(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Geometry jtsInter=jtsGeom1.intersection(jtsGeom2);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsInter);
		} catch (Exception e) {
		    logger.error(I18N.getString("JtsAlgorithms.IntersectionError")); //$NON-NLS-1$
		    if (logger.isDebugEnabled()) {
		        logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
		        logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
		        logger.debug(e.getMessage());
		    }
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object difference(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Geometry jtsResult=jtsGeom1.difference(jtsGeom2);
			//if (jtsResult.isEmpty()||jtsResult.getArea()==0.0) return null;
			return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.DifferenceError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public GM_Object symDifference(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Geometry jtsSymDiff=jtsGeom1.symDifference(jtsGeom2);
			return JtsGeOxygene.makeGeOxygeneGeom(jtsSymDiff);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.SymDifferenceError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.equals(jtsGeom2);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.EqualsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.EqualsExactError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.EqualsExactError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Tolerance")+tol); //$NON-NLS-1$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean contains(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.contains(jtsGeom2);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.ContainsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.CrossesError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.DisjointError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.WithinError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.IsWithinDistanceError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Distance")+dist); //$NON-NLS-1$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean intersects(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.intersects(jtsGeom2);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.IntersectsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.OverlapsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.TouchesError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return false;
		}
	}

	public boolean isEmpty(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isEmpty();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.IsEmptyError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return true;
		}
	}

	public boolean isSimple(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isSimple();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.IsSimpleError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return false;
		}
	}

	public boolean isValid(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.isValid();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.IsValidError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return false;
		}
	}

	@Override
	public double distance(GM_Object g1, GM_Object g2)  {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			return jtsGeom1.distance(jtsGeom2);
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.DistanceError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return 0.0;
		}
	}

	@Override
	public double area(GM_Object geom) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom1.getArea();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.AreaError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return 0.0;
		}
	}

	@Override
	public double length(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getLength();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.LengthError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return 0.0;
		}
	}

	public int dimension(GM_Object geom) {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getDimension();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.DimensionError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return 0;
		}
	}

	public int numPoints(GM_Object geom) {
		try {
			if (geom.isEmpty()) return 0;
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			return jtsGeom.getNumPoints();
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.NumPointsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return 0;
		}
	}

	public GM_Object translate(GM_Object geom, final double tx, final double ty, final double tz)  {
		try {
			Geometry jtsGeom=JtsGeOxygene.makeJtsGeom(geom);
			CoordinateFilter translateCoord=new CoordinateFilter() {
				@Override
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
			logger.error(I18N.getString("JtsAlgorithms.TranslateError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
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
			logger.error(I18N.getString("JtsAlgorithms.RelateError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry1")+((g1!=null)?g1.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(I18N.getString("JtsAlgorithms.Geometry2")+((g2!=null)?g2.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return "ERROR"; //$NON-NLS-1$
		}
	}
	
	/**
	 * Calcul de l'union d'une liste de géométries
	 * @param listeGeometries liste des géométries à unir
	 * @return union d'une liste de géométries
	 */
	public static GM_Object union(List<? extends GM_Object> listeGeometries) {
		List<Geometry> listeGeometriesJts = new ArrayList<Geometry>();
		for(GM_Object geom:listeGeometries) {
			try {listeGeometriesJts.add(JtsGeOxygene.makeJtsGeom(geom));}
			catch(Exception e){
				logger.error(I18N.getString("JtsAlgorithms.GeometryConversionError")); //$NON-NLS-1$
				if (logger.isDebugEnabled()) {
					logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((geom!=null)?geom.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
					logger.debug(e.getMessage());
				}
			}
		}
		Geometry union = union(listeGeometriesJts);
		try {return JtsGeOxygene.makeGeOxygeneGeom(union);}
		catch(Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.GeometryConversionError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
				logger.debug(I18N.getString("JtsAlgorithms.Geometry")+((union!=null)?union.toString():I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
				logger.debug(e.getMessage());
			}
            e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * détermine le point d'un polygone le plus loin d'un autre point.
	 * Le polygone doit être convexe et sans trou.
	 * Determine the farest point of a polygon to another given point.
	 * The polygon must be convex and without hole.
	 * 
	 * @param pt un point, a point
	 * @param poly un polygone convexe sans trou, a convex polygon without hole
	 */
	public static Point getFurthestPoint(Point pt, Polygon poly){
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
	 * détermine le point d'un polygone le plus proche d'un autre point.
	 * Determine the closest point of a polygon to another given point.
	 * 
	 * @param pt un point, a point
	 * @param poly un polygone, a polygon
	 */
	public static DirectPosition getClosestPoint(DirectPosition pt, GM_Polygon poly){
		return getClosestPoint(pt, poly.exteriorLineString());
	}

	/**
	 * détermine le point d'une ligne le plus proche d'un autre point.
	 * Determine the closest point of a line to another given point.
	 * 
	 * @param pt un point, a point
	 * @param l une ligne, a line
	 */
	public static DirectPosition getClosestPoint(DirectPosition pt, GM_LineString l) {
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
	 * détermine le point d'une ligne le plus loin d'une ligne de base.
	 * Determine the closest point of a line to another given line.
	 * 
	 * @param base la ligne de comparaison, the base line
	 * @param l une ligne, a line
	 */
	public static DirectPosition getFurthestPoint(GM_LineString base, GM_LineString l) {
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

	/**
	 * détermine les points les plus proches deux géométries. 
	 * Les points sont donnés dans le même ordre que les deux géométries d'entrée.
	 * Compute the nearest points of two geometries.
	 * The points are presented in the same order as the input Geometries. 
	 * 
	 * @param g1 une géométrie
	 * @param g2 une autre géométrie
	 * @return la liste des 2 points les plus proches
	 */
	public static DirectPositionList getClosestPoints(GM_Object g1, GM_Object g2) {
		try {
			Geometry jtsGeom1=JtsGeOxygene.makeJtsGeom(g1);
			Geometry jtsGeom2=JtsGeOxygene.makeJtsGeom(g2);
			Coordinate[] coord = DistanceOp.nearestPoints(jtsGeom1, jtsGeom2);
			DirectPosition dp1 = new DirectPosition(coord[0].x,coord[0].y);
			DirectPosition dp2 = new DirectPosition(coord[1].x,coord[1].y);
			DirectPositionList listePoints =new DirectPositionList();
			listePoints.add(dp1);
			listePoints.add(dp2);
			return listePoints;
		} catch (Exception e) {
			logger.error(I18N.getString("JtsAlgorithms.ClosestPointsError")); //$NON-NLS-1$
			if (logger.isDebugEnabled()) {
			    logger.debug(e.getMessage());
			}
		}
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
		Collection<Geometry> newGeometryCollection = geometryCollection;
		final int cellSize = 1 + (int)Math.sqrt(newGeometryCollection.size());
		Comparator<Geometry> comparator =  new Comparator<Geometry>(){
			@Override
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
		int nbIteration = 1 + (int)(Math.log(newGeometryCollection.size())/Math.log(4));
		fireActionPerformed(new ActionEvent(singleton,0,I18N.getString("JtsAlgorithms.UnionAction"),nbIteration)); //$NON-NLS-1$
		while (newGeometryCollection.size() > 1) {
			fireActionPerformed(new ActionEvent(singleton,1,I18N.getString("JtsAlgorithms.UnionIterationAction"),iteration++)); //$NON-NLS-1$
			if (logger.isTraceEnabled()) {
			    logger.trace("Union (" + iteration + "/" + nbIteration + ")");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			}
			TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
			treeSet.addAll(newGeometryCollection);
			newGeometryCollection = union(treeSet, 4);
		}
		List<Geometry> geometries = new ArrayList<Geometry>();
		for (Geometry geom : newGeometryCollection) {
			if (geom instanceof Polygon || geom instanceof LineString || geom instanceof Point) {
			    geometries.add(geom);
			} else {
			    if (geom instanceof MultiPolygon || geom instanceof MultiLineString || geom instanceof MultiPoint) {
			        GeometryCollection collection = (GeometryCollection) geom;
			        for (int index = 0;index < collection.getNumGeometries();index++)
			            geometries.add(collection.getGeometryN(index));
			    } else {
			        logger.error(I18N.getString("JtsAlgorithms.UnhandledGeometryType")+geom.getGeometryType()); //$NON-NLS-1$
			    }
			}
		}
		fireActionPerformed(new ActionEvent(singleton,4,I18N.getString("JtsAlgorithms.UnionFinishedAction"))); //$NON-NLS-1$
		if (geometries.size()==1) return geometries.get(0);
		if (geometries.isEmpty()) return new GeometryFactory().createGeometryCollection(new Geometry[0]);
		if (geometries.get(0) instanceof Polygon) {
		    return newGeometryCollection.iterator().next().getFactory().createMultiPolygon(geometries.toArray(new Polygon[0]));
		}
        if (geometries.get(0) instanceof LineString) {
            return newGeometryCollection.iterator().next().getFactory().createMultiLineString(geometries.toArray(new LineString[0]));
        }
        if (geometries.get(0) instanceof Point) {
            return newGeometryCollection.iterator().next().getFactory().createMultiPoint(geometries.toArray(new Point[0]));
        }
		return newGeometryCollection.iterator().next().getFactory().createGeometryCollection(geometries.toArray(new Geometry[0]));
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
		fireActionPerformed(new ActionEvent(singleton,2,I18N.getString("JtsAlgorithms.UnionDetailAction"),size)); //$NON-NLS-1$
		for (Geometry geom:treeSet) {
			if ((currUnion==null)||(count%groupSize==0)) currUnion = geom;
			else {
				currUnion = currUnion.union(geom);
				if (groupSize-count%groupSize==1) unionGeometryList.add(currUnion);
			}
			fireActionPerformed(new ActionEvent(singleton,3,I18N.getString("JtsAlgorithms.UnionDetailIterationAction"),++count)); //$NON-NLS-1$
			if (logger.isTraceEnabled()) {
			    logger.trace(" "+(count)+" - "+size+" features");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
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
		List<Geometry> newGeometryCollection = geometryCollection;
		final int cellSize = 1 + (int)Math.sqrt(newGeometryCollection.size());
		Comparator<Geometry> comparator =  new Comparator<Geometry>(){
			@Override
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
		int nbIteration = 1 + (int)(Math.log(newGeometryCollection.size())/Math.log(4));
		fireActionPerformed(new ActionEvent(singleton,0,I18N.getString("JtsAlgorithms.UnionAction"),nbIteration)); //$NON-NLS-1$
		while (newGeometryCollection.size() > 1) {
			fireActionPerformed(new ActionEvent(singleton,1,I18N.getString("JtsAlgorithms.UnionIterationAction"),iteration++)); //$NON-NLS-1$
			if (logger.isTraceEnabled()) {
			    logger.trace("Union (" + iteration + "/" + nbIteration + ")");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			}
			TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
			treeSet.addAll(newGeometryCollection);
			newGeometryCollection = unionLineString(treeSet, 4);
		}
		fireActionPerformed(new ActionEvent(singleton,4,I18N.getString("JtsAlgorithms.UnionFinishedAction"))); //$NON-NLS-1$
		return newGeometryCollection.get(0);
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
		fireActionPerformed(new ActionEvent(singleton,2,I18N.getString("JtsAlgorithms.UnionDetailAction"),size)); //$NON-NLS-1$
		for (Geometry geom:treeSet) {
			if ((currUnion==null)||(count%groupSize==0)) currUnion = geom;
			else {
				currUnion = currUnion.union(geom);
				if (groupSize-count%groupSize==1) unionGeometryList.add(currUnion);
			}
			fireActionPerformed(new ActionEvent(singleton,3,I18N.getString("JtsAlgorithms.UnionDetailIterationAction"),++count)); //$NON-NLS-1$
			if (logger.isTraceEnabled()) {
			    logger.trace(" "+(count)+" - "+size+" features");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			}
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

	/**
	 * tente d'appliquer filtre de douglas peucker a une geometrie.
	 * en cas d'echec, renvoie la geometrie initiale
	 * @param geom
	 * @param seuil
	 * @return the resulting Geometry after the application of the DouglasPeucker filter
	 */
	public static Geometry filtreDouglasPeucker(Geometry geom, double seuil){
		if (seuil == 0.0) return (Geometry)geom.clone();
		if (seuil <0.0) {
			logger.warn(I18N.getString("JtsAlgorithms.DouglasPeuckerWithNegativeThreshold")+seuil); //$NON-NLS-1$
			return geom;
		}

		Geometry g = DouglasPeuckerSimplifier.simplify(geom, seuil);

		if ((g==null)||g.isEmpty()||!g.isValid()) {
			logger.warn(I18N.getString("JtsAlgorithms.DouglasPeuckerError")); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.DouglasPeuckerThreshold")+seuil); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.Geometry")+geom); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.Result")+g); //$NON-NLS-1$ 
			return geom;
		}
		else if ( g.getGeometryType() != geom.getGeometryType()) {
			logger.warn(I18N.getString("JtsAlgorithms.DouglasPeuckerWithDifferentTypesError")); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.DouglasPeuckerThreshold")+seuil); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.Geometry")+geom); //$NON-NLS-1$
			logger.warn(I18N.getString("JtsAlgorithms.Result")+g); //$NON-NLS-1$
			return geom;
		}
		else return g;
	}

	/**
	 * calcule fermeture de geometrie (juste buffer externe, puis interne)
	 * @param geometry géométrie de départ
	 * @param distance distance utilisée pour le buffer positif puis pour le buffer négatif
	 * @param quadrantSegments nombre de segments utilisés pour la simplification par l'algorithme de Douglas-Peucker
	 * @param endCapStyle type d'approximation utilisée pour la simplification par l'algorithme de Douglas-Peucker
	 * @return la fermeture de la géométrie passée en paramètre
	 */
	public static Geometry fermeture(Geometry geometry, double distance, int quadrantSegments, int endCapStyle ) {
		Geometry geom = geometry.buffer(distance,quadrantSegments,endCapStyle);
		geom = geom.buffer(-distance,quadrantSegments,endCapStyle);
		return geom;
	}
	public static Geometry fermeture(Geometry geometry, double distance, int quadrantSegments) {
		return fermeture(geometry, distance, quadrantSegments, BufferParameters.CAP_ROUND);
	}

	/**
	 * Supprime les trous d'un polygone.
	 * Remove the holes from a polygon.
	 * 
	 * @param poly un polygone, a polygon
	 */
	public static Polygon supprimeTrous(Polygon poly){
		return new Polygon((LinearRing)poly.getExteriorRing(), null, poly.getFactory());
	}

    /**
     * Supprime les trous d'un multipolygone, i.e. supprime les trous de tous
     * les polygones d'un multipolygone.
     * Remove the holes from a multipolygon.
     * @see #supprimeTrous(Polygon)
     * @param mp un multipolyone, a multipolygon
     */
	public static MultiPolygon supprimeTrous(MultiPolygon mp){
		Polygon[] polys = new Polygon[mp.getNumGeometries()];
		for (int i = 0; i < mp.getNumGeometries(); i++) {
		    polys[i] = supprimeTrous((Polygon) mp.getGeometryN(i));
		}
		return (new GeometryFactory()).createMultiPolygon(polys);
	}

    /**
     * Builds on offset curve for the given linestring. A positive offset
     * builds an offset curve on the left-hand side of the reference
     * linestring. Negative means right.
     * @param line reference linestring
     * @param distance offset
     * @return a multi linestring at the given offset of the reference linestring
     */
    public static GM_MultiCurve<GM_LineString> offsetCurve(GM_LineString line,
            double distance) {
        //boolean left = (distance > 0);
        double d = Math.abs(distance);
        int orientationIndex = (int) (d / distance);
        try {
            LineString lineString = getLineStringWithoutDuplicates(
                    (LineString) JtsGeOxygene.makeJtsGeom(line));
            Geometry buffer = lineString.buffer(d, 4,
                    BufferParameters.CAP_FLAT);
            List<LineString> holes = new ArrayList<LineString>();
            if (buffer instanceof Polygon) {
                for (int i = 0; i < ((Polygon) buffer).getNumInteriorRing(); i++) {
                    holes.add(((Polygon) buffer).getInteriorRingN(i));
                }
                buffer = ((Polygon) buffer).getExteriorRing();
            } else {
                logger.error("Can't compute offsetcurve of " + buffer.getGeometryType());
            }
            GM_MultiCurve<GM_LineString> result = new GM_MultiCurve<GM_LineString>();
            List<Coordinate> coords = new ArrayList<Coordinate>();
            for (Coordinate c : buffer.getCoordinates()) {
                if (!lineString.isCoordinate(c)) {
                    coords.add(c);
                }
            }
            GM_LineString r = getOffsetCurveFromRing(coords, lineString, orientationIndex);
            if ((r != null) && !r.isEmpty()) { result.add(r); }
            for (LineString l : holes) {
                coords = new ArrayList<Coordinate>();
                for (Coordinate c : l.getCoordinates()) {
                    if (!lineString.isCoordinate(c)) {
                        coords.add(c);
                    }
                }
                r = getOffsetCurveFromRing(coords, lineString, orientationIndex);
                if ((r != null) && !r.isEmpty()) { result.add(r); }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Result (" + distance + " ) = " + result);
            }
            return result;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private static LineString getLineStringWithoutDuplicates(
            LineString lineString) {
        Coordinate[] coordinateArray = lineString.getCoordinates();
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        // build a list of linestring coordinates and remove duplicates
        Coordinate previous = coordinateArray[0];
        coordinates.add(previous);
        for (int i = 1; i < coordinateArray.length; i++) {
            if (!coordinateArray[i].equals2D(previous)) {
                coordinates.add(coordinateArray[i]);
                previous = coordinateArray[i];
            }
        }
        return lineString.getFactory().createLineString(
                coordinates.toArray(new Coordinate[0]));
    }

    /**
     * @param coords coordinates used to build the offsetcurve. These come
     * from a linear ring
     * @param line the reference linestring
     * @param orientationIndex orientation of the offset curve to build
     * @return the offsetcurve
     */
    private static GM_LineString getOffsetCurveFromRing(
            List<Coordinate> coords, LineString line,
            int orientationIndex) {
        int start = -1; int end = -1;
        // go through the coordinates of the buffer and select the range
        // of coordinates of the right side
        int previousOrientation = orientationIndex(coords.get(0),
                line);
        int lastNonNullOrientation = previousOrientation;
        for (int i = 1; i < coords.size(); i++) {
            int currentOrientation = orientationIndex(coords.get(i),
                    line);
            // if there is a change of side, set the start or end marker
            if (currentOrientation != previousOrientation) {
                if (currentOrientation == orientationIndex) {
                    start = i;
                    if (previousOrientation == 0) {
                        start -= 1;
                    }
                } else {
                    if (currentOrientation == -orientationIndex) {
                        end = i;
                    }
                }
            }
            previousOrientation = currentOrientation;
            if (currentOrientation != 0) {
                lastNonNullOrientation = currentOrientation;
            }
        }
        boolean cycle = true;
        // if we didn't find any change in direction, all points are on the
        // same side
        if ((start == -1 || end == -1) && (lastNonNullOrientation == orientationIndex)) {
            start = 0;
            end = coords.size();
            // we will not cycle through the coordinates
            cycle = false;
        }
        if (start == -1 || end == -1) {
            return null;
        }
        int numberOfCoordinates = end - start;
        if (numberOfCoordinates < 0) {
            numberOfCoordinates += coords.size();
        }
        // build the linestring using the determined range of coordinates
        List<Coordinate> offsetCoordinates = new ArrayList<Coordinate>();
        if (numberOfCoordinates > 1) {
            for (int i = start; i != end; i = cycle ? (i + 1) % coords.size() : i + 1) {
                offsetCoordinates.add(0, coords.get(i));
            }
        } else {
            return null;
        }
        // 2 coordinates are the same
        if (numberOfCoordinates == 2
                && offsetCoordinates.get(0).equals2D(offsetCoordinates.get(1))) {
            return null;
        }
        GM_LineString result = new GM_LineString(AdapterFactory
                .toDirectPositionList(offsetCoordinates
                        .toArray(new Coordinate[0])));
        return result;
    }

    /**
     * Determine the orientation of a coordinate to a linestring.
     * @param c coordinate
     * @param line the reference linestring
     * @return +1 if the coordinate is on the left, -1 if it is on the right,
     * 0 otherwise.
     */
    private static int orientationIndex(Coordinate c,
            LineString line) {
        double tolerance = 0.00000001;
        double distanceMin = Double.POSITIVE_INFINITY;
        // build a list of line segments
        List<LineSegment> closestLineSegments = new ArrayList<LineSegment>();
        for (int i = 0; i< line.getNumPoints()-1; i++) {
            Coordinate coordinate1  = line.getCoordinateN(i);
            Coordinate coordinate2  = line.getCoordinateN(i+1);
            LineSegment segment = new LineSegment(coordinate1, coordinate2);
            double d = segment.distance(c);
            if (d <= distanceMin + tolerance) {
                if (d < distanceMin - tolerance) {
                    distanceMin = d;
                    closestLineSegments.clear();
                }
                closestLineSegments.add(segment);
            }
        }
        int orientation = closestLineSegments.get(0).orientationIndex(c);
        for (int i = 1; i < closestLineSegments.size(); i++) {
            LineSegment segment = closestLineSegments.get(i);
            if (segment.orientationIndex(c) != orientation) {
                return 0;
            }
        }
        return orientation;
     }
} // class
