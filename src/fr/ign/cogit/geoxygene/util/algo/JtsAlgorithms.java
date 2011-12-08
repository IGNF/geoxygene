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

package fr.ign.cogit.geoxygene.util.algo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * Appel des methodes JTS sur des GM_Object. cf.
 * http://www.vividsolutions.com/jts/jtshome.htm
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Christophe Pele
 * @author Jean-François Girres
 * @author Charlotte Hoarau
 * @author Mickaël Brasebin
 * @version 1.0
 * 
 */

public class JtsAlgorithms implements GeomAlgorithms {
  static Logger logger = Logger.getLogger(JtsAlgorithms.class.getName());

  public JtsAlgorithms() {
  }

  @Override
  public DirectPosition centroid(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      Point jtsCentroid = jtsGeom.getCentroid();
      return new DirectPosition(jtsCentroid.getX(), jtsCentroid.getY());
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.CentroidError")); //$NON-NLS-1$
      JtsAlgorithms.logger
          .error(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry convexHull(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      Geometry jtsHull = jtsGeom.convexHull();
      IGeometry result = JtsGeOxygene.makeGeOxygeneGeom(jtsHull);
      return result;
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.ConvexHullError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry buffer(IGeometry geom, double distance) {
    if (distance == 0) {
      return geom;
    }
    Geometry jtsGeom = null;
    try {
      jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      Geometry jtsBuffer = jtsGeom.buffer(distance);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferDistance") + distance); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
        JtsAlgorithms.logger.debug("Geometry JTS = " + jtsGeom); //$NON-NLS-1$
      }
      e.printStackTrace();
      return null;
    }
  }

  public IGeometry buffer(IGeometry geom, double distance, int nSegments) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      Geometry jtsBuffer = jtsGeom.buffer(distance, nSegments);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferDistance") + distance); //$NON-NLS-1$
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferSegments") + nSegments); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  public IGeometry buffer(IGeometry geom, double distance, int nSegments,
      int cap) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      Geometry jtsBuffer = jtsGeom.buffer(distance, nSegments, cap);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferDistance") + distance); //$NON-NLS-1$
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferSegments") + nSegments); //$NON-NLS-1$
        JtsAlgorithms.logger.debug(I18N.getString("JtsAlgorithms.Cap") + cap); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  public IGeometry buffer(IGeometry geom, double distance, int nSegments,
      int cap, int join) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      BufferParameters bufferParam = new BufferParameters(nSegments, cap, join,
          BufferParameters.DEFAULT_MITRE_LIMIT);
      Geometry jtsBuffer = BufferOp.bufferOp(jtsGeom, distance, bufferParam);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsBuffer);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.BufferError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferDistance") + distance); //$NON-NLS-1$
        JtsAlgorithms.logger.debug(I18N
            .getString("JtsAlgorithms.BufferSegments") + nSegments); //$NON-NLS-1$
        JtsAlgorithms.logger.debug(I18N.getString("JtsAlgorithms.Cap") + cap); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  public IGeometry boundary(IGeometry geom) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(geom);
      Geometry jtsResult = jtsGeom1.getBoundary();
      return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.BoundaryError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry union(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      Geometry jtsUnion = jtsGeom1.union(jtsGeom2);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.UnionError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry intersection(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      Geometry jtsInter = jtsGeom1.intersection(jtsGeom2);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsInter);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.IntersectionError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry difference(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      Geometry jtsResult = jtsGeom1.difference(jtsGeom2);
      // if (jtsResult.isEmpty()||jtsResult.getArea()==0.0) return null;
      return JtsGeOxygene.makeGeOxygeneGeom(jtsResult);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.DifferenceError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry symDifference(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      Geometry jtsSymDiff = jtsGeom1.symDifference(jtsGeom2);
      return JtsGeOxygene.makeGeOxygeneGeom(jtsSymDiff);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.SymDifferenceError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean equals(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.equals(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.EqualsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean equalsExact(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.equalsExact(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.EqualsExactError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean equalsExact(IGeometry g1, IGeometry g2, double tol) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.equalsExact(jtsGeom2, tol);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.EqualsExactError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Tolerance") + tol); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean contains(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.contains(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.ContainsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean crosses(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.crosses(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.CrossesError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean disjoint(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.disjoint(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.DisjointError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean within(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.within(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.WithinError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean isWithinDistance(IGeometry g1, IGeometry g2, double dist) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.isWithinDistance(jtsGeom2, dist);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.IsWithinDistanceError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Distance") + dist); //$NON-NLS-1$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean intersects(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.intersects(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.IntersectsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean overlaps(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.overlaps(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.OverlapsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean touches(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.touches(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.TouchesError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean isEmpty(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.isEmpty();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.IsEmptyError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return true;
    }
  }

  public boolean isSimple(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.isSimple();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.IsSimpleError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  public boolean isValid(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.isValid();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.IsValidError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public double distance(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.distance(jtsGeom2);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.DistanceError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return 0.0;
    }
  }

  @Override
  public double area(IGeometry geom) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom1.getArea();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.AreaError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return 0.0;
    }
  }

  @Override
  public double length(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.getLength();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.LengthError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return 0.0;
    }
  }

  public int dimension(IGeometry geom) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.getDimension();
    } catch (Exception e) {
      JtsAlgorithms.logger
          .error(I18N.getString("JtsAlgorithms.DimensionError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return 0;
    }
  }

  public int numPoints(IGeometry geom) {
    try {
      if (geom.isEmpty()) {
        return 0;
      }
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      return jtsGeom.getNumPoints();
    } catch (Exception e) {
      JtsAlgorithms.logger
          .error(I18N.getString("JtsAlgorithms.NumPointsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return 0;
    }
  }

  public IGeometry translate(IGeometry geom, final double tx, final double ty,
      final double tz) {
    try {
      Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
      CoordinateFilter translateCoord = new CoordinateFilter() {
        @Override
        public void filter(Coordinate coord) {
          coord.x += tx;
          coord.y += ty;
          coord.z += tz;
        }
      };
      jtsGeom.apply(translateCoord);
      IGeometry result = JtsGeOxygene.makeGeOxygeneGeom(jtsGeom);
      return result;
    } catch (Exception e) {
      JtsAlgorithms.logger
          .error(I18N.getString("JtsAlgorithms.TranslateError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  public String relate(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      return jtsGeom1.relate(jtsGeom2).toString();
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N.getString("JtsAlgorithms.RelateError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry1") + ((g1 != null) ? g1.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry2") + ((g2 != null) ? g2.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
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
  public static IGeometry union(List<? extends IGeometry> listeGeometries) {
    List<Geometry> listeGeometriesJts = new ArrayList<Geometry>(0);
    for (IGeometry geom : listeGeometries) {
      try {
        listeGeometriesJts.add(JtsGeOxygene.makeJtsGeom(geom));
      } catch (Exception e) {
        JtsAlgorithms.logger.error(I18N
            .getString("JtsAlgorithms.GeometryConversionError")); //$NON-NLS-1$
        if (JtsAlgorithms.logger.isDebugEnabled()) {
          JtsAlgorithms.logger
              .debug(I18N.getString("JtsAlgorithms.Geometry") + ((geom != null) ? geom.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
          JtsAlgorithms.logger.debug(e.getMessage());
        }
      }
    }
    Geometry union = JtsAlgorithms.union(listeGeometriesJts);
    try {
      return JtsGeOxygene.makeGeOxygeneGeom(union);
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.GeometryConversionError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger
            .debug(I18N.getString("JtsAlgorithms.Geometry") + ((union != null) ? union.toString() : I18N.getString("JtsAlgorithms.NullGeometry"))); //$NON-NLS-1$ //$NON-NLS-2$
        JtsAlgorithms.logger.debug(e.getMessage());
      }
      e.printStackTrace();
      return null;
    }
  }

  /**
   * détermine le point d'un polygone le plus loin d'un autre point. Le polygone
   * doit être convexe et sans trou. Determine the farest point of a polygon to
   * another given point. The polygon must be convex and without hole.
   * 
   * @param pt un point, a point
   * @param poly un polygone convexe sans trou, a convex polygon without hole
   */
  public static Point getFurthestPoint(Point pt, Polygon poly) {
    Point pt_max = poly.getExteriorRing().getPointN(0);
    double dist_max = pt.distance(pt_max);
    for (int i = 1; i < poly.getExteriorRing().getNumPoints(); i++) {
      double dist = pt.distance(poly.getExteriorRing().getPointN(i));
      if (dist > dist_max) {
        pt_max = poly.getExteriorRing().getPointN(i);
        dist_max = dist;
      }
    }
    return pt_max;
  }

  /**
   * détermine le point d'un polygone le plus proche d'un autre point. Determine
   * the closest point of a polygon to another given point.
   * 
   * @param pt un point, a point
   * @param poly un polygone, a polygon
   */
  public static IDirectPosition getClosestPoint(IDirectPosition pt,
      IPolygon poly) {
    return JtsAlgorithms.getClosestPoint(pt, poly.exteriorLineString());
  }

  /**
   * détermine le point d'une ligne le plus proche d'un autre point. Determine
   * the closest point of a line to another given point.
   * 
   * @param pt un point, a point
   * @param l une ligne, a line
   */
  public static IDirectPosition getClosestPoint(IDirectPosition pt,
      ILineString l) {
    Point point = new GeometryFactory().createPoint(AdapterFactory
        .toCoordinate(pt));
    LineString line;
    try {
      line = (LineString) AdapterFactory.toGeometry(new GeometryFactory(), l);
      Coordinate[] cp = (new DistanceOp(line, point)).nearestPoints();
      return AdapterFactory.toDirectPosition(line.getFactory()
          .createPoint(cp[0]).getCoordinate());
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * détermine le point d'une ligne le plus loin d'une ligne de base. Determine
   * the closest point of a line to another given line.
   * 
   * @param base la ligne de comparaison, the base line
   * @param l une ligne, a line
   */
  public static IDirectPosition getFurthestPoint(ILineString base, ILineString l) {
    try {
      LineString baseLine = (LineString) AdapterFactory.toGeometry(
          new GeometryFactory(), base);
      LineString line = (LineString) AdapterFactory.toGeometry(
          new GeometryFactory(), l);
      double distanceMax = Double.MIN_VALUE;
      Point pointLePlusLoin = null;
      for (int i = 0; i < line.getNumPoints(); i++) {
        Point p = line.getPointN(i);
        double distance = p.distance(baseLine);
        if (distance > distanceMax) {
          distanceMax = distance;
          pointLePlusLoin = p;
        }
      }
      if (pointLePlusLoin != null) {
        return AdapterFactory.toDirectPosition(pointLePlusLoin.getCoordinate());
        // return
        // AdapterFactory.toDirectPosition(JtsUtil.getPointLePlusProche(point,
        // line).getCoordinate());
      }
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * détermine les points les plus proches deux géométries. Les points sont
   * donnés dans le même ordre que les deux géométries d'entrée. Compute the
   * nearest points of two geometries. The points are presented in the same
   * order as the input Geometries.
   * 
   * @param g1 une géométrie
   * @param g2 une autre géométrie
   * @return la liste des 2 points les plus proches
   */
  public static IDirectPositionList getClosestPoints(IGeometry g1, IGeometry g2) {
    try {
      Geometry jtsGeom1 = JtsGeOxygene.makeJtsGeom(g1);
      Geometry jtsGeom2 = JtsGeOxygene.makeJtsGeom(g2);
      Coordinate[] coord = DistanceOp.nearestPoints(jtsGeom1, jtsGeom2);
      IDirectPosition dp1 = new DirectPosition(coord[0].x, coord[0].y);
      IDirectPosition dp2 = new DirectPosition(coord[1].x, coord[1].y);
      IDirectPositionList listePoints = new DirectPositionList();
      listePoints.add(dp1);
      listePoints.add(dp2);
      return listePoints;
    } catch (Exception e) {
      JtsAlgorithms.logger.error(I18N
          .getString("JtsAlgorithms.ClosestPointsError")); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isDebugEnabled()) {
        JtsAlgorithms.logger.debug(e.getMessage());
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
    JtsAlgorithms.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected static void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = JtsAlgorithms.listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        // Lazily create the event:
        ((ActionListener) listeners[i + 1]).actionPerformed(event);
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
    for (Geometry element : geometryArray) {
      liste.add(element);
    }
    return JtsAlgorithms.union(liste);
  }

  /**
   * Union d'une collection de Polygones
   * @param geometryCollection collection de Polygones JTS
   * @return union des Polygones
   */
  public static Geometry union(Collection<Geometry> geometryCollection) {
    Collection<Geometry> newGeometryCollection = geometryCollection;
    final int cellSize = 1 + (int) Math.sqrt(newGeometryCollection.size());
    Comparator<Geometry> comparator = new Comparator<Geometry>() {
      @Override
      public int compare(Geometry o1, Geometry o2) {
        if (o1 == null || o2 == null) {
          return 0;
        }
        Envelope env1 = o1.getEnvelopeInternal();
        Envelope env2 = o2.getEnvelopeInternal();
        double indice1 = env1.getMinX() / cellSize + cellSize
            * ((int) env1.getMinY() / cellSize);
        double indice2 = env2.getMinX() / cellSize + cellSize
            * ((int) env2.getMinY() / cellSize);
        return indice1 >= indice2 ? 1 : indice1 < indice2 ? -1 : 0;
      }

      @Override
      public boolean equals(Object obj) {
        return this.equals(obj);
      }
    };
    int iteration = 1;
    int nbIteration = 1 + (int) (Math.log(newGeometryCollection.size()) / Math
        .log(4));
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        0, I18N.getString("JtsAlgorithms.UnionAction"), nbIteration)); //$NON-NLS-1$
    while (newGeometryCollection.size() > 1) {
      JtsAlgorithms.fireActionPerformed(new ActionEvent(
          JtsAlgorithms.singleton, 1, I18N
              .getString("JtsAlgorithms.UnionIterationAction"), iteration++)); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isTraceEnabled()) {
        JtsAlgorithms.logger
            .trace("Union (" + iteration + "/" + nbIteration + ")"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
      }
      TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
      treeSet.addAll(newGeometryCollection);
      newGeometryCollection = JtsAlgorithms.union(treeSet, 4);
    }
    List<Geometry> geometries = new ArrayList<Geometry>();
    for (Geometry geom : newGeometryCollection) {
      if (geom instanceof Polygon || geom instanceof LineString
          || geom instanceof Point) {
        geometries.add(geom);
      } else {
        if (geom instanceof MultiPolygon || geom instanceof MultiLineString
            || geom instanceof MultiPoint) {
          GeometryCollection collection = (GeometryCollection) geom;
          for (int index = 0; index < collection.getNumGeometries(); index++) {
            geometries.add(collection.getGeometryN(index));
          }
        } else {
          JtsAlgorithms.logger
              .error(I18N.getString("JtsAlgorithms.UnhandledGeometryType") + geom.getGeometryType()); //$NON-NLS-1$
        }
      }
    }
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        4, I18N.getString("JtsAlgorithms.UnionFinishedAction"))); //$NON-NLS-1$
    if (geometries.size() == 1) {
      return geometries.get(0);
    }
    if (geometries.isEmpty()) {
      return new GeometryFactory().createGeometryCollection(new Geometry[0]);
    }
    if (geometries.get(0) instanceof Polygon) {
      return newGeometryCollection.iterator().next().getFactory()
          .createMultiPolygon(geometries.toArray(new Polygon[0]));
    }
    if (geometries.get(0) instanceof LineString) {
      return newGeometryCollection.iterator().next().getFactory()
          .createMultiLineString(geometries.toArray(new LineString[0]));
    }
    if (geometries.get(0) instanceof Point) {
      return newGeometryCollection.iterator().next().getFactory()
          .createMultiPoint(geometries.toArray(new Point[0]));
    }
    return newGeometryCollection.iterator().next().getFactory()
        .createGeometryCollection(geometries.toArray(new Geometry[0]));
  }

  /**
   * Union des éléments d'un ensemble de Polygones triés par groupes. Par
   * exemple, si la taille des groupes vaut 4, on effectue l'union des Polygones
   * 4 par 4.
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
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        2, I18N.getString("JtsAlgorithms.UnionDetailAction"), size)); //$NON-NLS-1$
    for (Geometry geom : treeSet) {
      if ((currUnion == null) || (count % groupSize == 0)) {
        currUnion = geom;
      } else {
        currUnion = currUnion.union(geom);
        if (groupSize - count % groupSize == 1) {
          unionGeometryList.add(currUnion);
        }
      }
      JtsAlgorithms.fireActionPerformed(new ActionEvent(
          JtsAlgorithms.singleton, 3, I18N
              .getString("JtsAlgorithms.UnionDetailIterationAction"), ++count)); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isTraceEnabled()) {
        JtsAlgorithms.logger.trace(" " + (count) + " - " + size + " features"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
      }
    }
    if (groupSize - count % groupSize != 0) {
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
    final int cellSize = 1 + (int) Math.sqrt(newGeometryCollection.size());
    Comparator<Geometry> comparator = new Comparator<Geometry>() {
      @Override
      public int compare(Geometry o1, Geometry o2) {
        if (o1 == null || o2 == null) {
          return 0;
        }
        Envelope env1 = o1.getEnvelopeInternal();
        Envelope env2 = o2.getEnvelopeInternal();
        double indice1 = env1.getMinX() / cellSize + cellSize
            * ((int) env1.getMinY() / cellSize);
        double indice2 = env2.getMinX() / cellSize + cellSize
            * ((int) env2.getMinY() / cellSize);
        return indice1 >= indice2 ? 1 : indice1 < indice2 ? -1 : 0;
      }

      @Override
      public boolean equals(Object obj) {
        return this.equals(obj);
      }
    };
    int iteration = 1;
    int nbIteration = 1 + (int) (Math.log(newGeometryCollection.size()) / Math
        .log(4));
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        0, I18N.getString("JtsAlgorithms.UnionAction"), nbIteration)); //$NON-NLS-1$
    while (newGeometryCollection.size() > 1) {
      JtsAlgorithms.fireActionPerformed(new ActionEvent(
          JtsAlgorithms.singleton, 1, I18N
              .getString("JtsAlgorithms.UnionIterationAction"), iteration++)); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isTraceEnabled()) {
        JtsAlgorithms.logger
            .trace("Union (" + iteration + "/" + nbIteration + ")"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
      }
      TreeSet<Geometry> treeSet = new TreeSet<Geometry>(comparator);
      treeSet.addAll(newGeometryCollection);
      newGeometryCollection = JtsAlgorithms.unionLineString(treeSet, 4);
    }
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        4, I18N.getString("JtsAlgorithms.UnionFinishedAction"))); //$NON-NLS-1$
    return newGeometryCollection.get(0);
  }

  /**
   * Union des éléments d'un ensemble de LineStrings triées par groupes. Par
   * exemple, si la taille des groupes vaut 4, on effectue l'union des
   * LineStrings 4 par 4.
   * 
   * @param treeSet ensemble de LineStrings triées
   * @param groupSize taille des groupes sur lesquels on effectue l'union
   * @return liste des unions
   */
  private static List<Geometry> unionLineString(TreeSet<Geometry> treeSet,
      int groupSize) {
    List<Geometry> unionGeometryList = new ArrayList<Geometry>();
    Geometry currUnion = null;
    int size = treeSet.size();
    int count = 0;
    JtsAlgorithms.fireActionPerformed(new ActionEvent(JtsAlgorithms.singleton,
        2, I18N.getString("JtsAlgorithms.UnionDetailAction"), size)); //$NON-NLS-1$
    for (Geometry geom : treeSet) {
      if ((currUnion == null) || (count % groupSize == 0)) {
        currUnion = geom;
      } else {
        currUnion = currUnion.union(geom);
        if (groupSize - count % groupSize == 1) {
          unionGeometryList.add(currUnion);
        }
      }
      JtsAlgorithms.fireActionPerformed(new ActionEvent(
          JtsAlgorithms.singleton, 3, I18N
              .getString("JtsAlgorithms.UnionDetailIterationAction"), ++count)); //$NON-NLS-1$
      if (JtsAlgorithms.logger.isTraceEnabled()) {
        JtsAlgorithms.logger.trace(" " + (count) + " - " + size + " features"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
      }
    }
    if (groupSize - count % groupSize != 0) {
      unionGeometryList.add(currUnion);
    }
    return unionGeometryList;
  }

  public static boolean isCCW(ILineString line) {
    Coordinate[] coords = AdapterFactory.toCoordinateSequence(
        new GeometryFactory(), line.coord()).toCoordinateArray();
    return CGAlgorithms.isCCW(coords);
  }

  /**
   * tente d'appliquer filtre de douglas peucker a une geometrie. en cas
   * d'echec, renvoie la geometrie initiale
   * @param geom
   * @param seuil
   * @return the resulting Geometry after the application of the DouglasPeucker
   *         filter
   */
  public static Geometry filtreDouglasPeucker(Geometry geom, double seuil) {
    if (seuil == 0.0) {
      return (Geometry) geom.clone();
    }
    if (seuil < 0.0) {
      JtsAlgorithms.logger
          .warn(I18N
              .getString("JtsAlgorithms.DouglasPeuckerWithNegativeThreshold") + seuil); //$NON-NLS-1$
      return geom;
    }

    Geometry g = DouglasPeuckerSimplifier.simplify(geom, seuil);

    if ((g == null) || g.isEmpty() || !g.isValid()) {
      JtsAlgorithms.logger.warn(I18N
          .getString("JtsAlgorithms.DouglasPeuckerError")); //$NON-NLS-1$
      JtsAlgorithms.logger.warn(I18N
          .getString("JtsAlgorithms.DouglasPeuckerThreshold") + seuil); //$NON-NLS-1$
      JtsAlgorithms.logger
          .warn(I18N.getString("JtsAlgorithms.Geometry") + geom); //$NON-NLS-1$
      JtsAlgorithms.logger.warn(I18N.getString("JtsAlgorithms.Result") + g); //$NON-NLS-1$
      return geom;
    } else if (g.getGeometryType() != geom.getGeometryType()) {
      JtsAlgorithms.logger.warn(I18N
          .getString("JtsAlgorithms.DouglasPeuckerWithDifferentTypesError")); //$NON-NLS-1$
      JtsAlgorithms.logger.warn(I18N
          .getString("JtsAlgorithms.DouglasPeuckerThreshold") + seuil); //$NON-NLS-1$
      JtsAlgorithms.logger
          .warn(I18N.getString("JtsAlgorithms.Geometry") + geom); //$NON-NLS-1$
      JtsAlgorithms.logger.warn(I18N.getString("JtsAlgorithms.Result") + g); //$NON-NLS-1$
      return geom;
    } else {
      return g;
    }
  }

  /**
   * calcule fermeture de geometrie (juste buffer externe, puis interne)
   * @param geometry géométrie de départ
   * @param distance distance utilisée pour le buffer positif puis pour le
   *          buffer négatif
   * @param quadrantSegments nombre de segments utilisés pour la simplification
   *          par l'algorithme de Douglas-Peucker
   * @param endCapStyle type d'approximation utilisée pour la simplification par
   *          l'algorithme de Douglas-Peucker
   * @return la fermeture de la géométrie passée en paramètre
   */
  public static Geometry fermeture(Geometry geometry, double distance,
      int quadrantSegments, int endCapStyle) {
    Geometry geom = geometry.buffer(distance, quadrantSegments, endCapStyle);
    geom = geom.buffer(-distance, quadrantSegments, endCapStyle);
    return geom;
  }

  public static Geometry fermeture(Geometry geometry, double distance,
      int quadrantSegments) {
    return JtsAlgorithms.fermeture(geometry, distance, quadrantSegments,
        BufferParameters.CAP_ROUND);
  }

  /**
   * Supprime les trous d'un polygone. Remove the holes from a polygon.
   * 
   * @param poly un polygone, a polygon
   */
  public static Polygon supprimeTrous(Polygon poly) {
    return new Polygon((LinearRing) poly.getExteriorRing(), null,
        poly.getFactory());
  }

  /**
   * Supprime les trous d'un multipolygone, i.e. supprime les trous de tous les
   * polygones d'un multipolygone. Remove the holes from a multipolygon.
   * @see #supprimeTrous(Polygon)
   * @param mp un multipolyone, a multipolygon
   */
  public static MultiPolygon supprimeTrous(MultiPolygon mp) {
    Polygon[] polys = new Polygon[mp.getNumGeometries()];
    for (int i = 0; i < mp.getNumGeometries(); i++) {
      polys[i] = JtsAlgorithms.supprimeTrous((Polygon) mp.getGeometryN(i));
    }
    return (new GeometryFactory()).createMultiPolygon(polys);
  }

  /**
   * Builds on offset curve for the given {@link ILineString}. A positive offset
   * builds an offset curve on the left-hand side of the reference
   * {@link ILineString}. Negative means right.
   * @param line reference {@link ILineString}
   * @param distance offset distance
   * @return a {@link IMultiCurve} at the given offset of the reference
   *         {@link ILineString}
   */
  public static IMultiCurve<ILineString> offsetCurve(ILineString line,
      double distance) {
    double d = Math.abs(distance);
    int orientationIndex = (int) (d / distance);
    try {
      // removing duplicate coordinates from the input linestring.
      LineString lineString = JtsAlgorithms
          .getLineStringWithoutDuplicates((LineString) JtsGeOxygene
              .makeJtsGeom(line));
      if (lineString != null) {
        Geometry buffer = lineString.buffer(d, 4, BufferParameters.CAP_ROUND);
        Polygon polygon = null;
        if (!(buffer instanceof Polygon)) {
          JtsAlgorithms.logger.error("Can't compute offsetcurve of " + //$NON-NLS-1$
              buffer.getGeometryType());
          return null;
        }
        polygon = (Polygon) buffer;
        IMultiCurve<ILineString> result = new GM_MultiCurve<ILineString>();
        // build the offset curve for the exterior ring
        ILineString r = JtsAlgorithms.getOffsetCurveFromRing(
            polygon.getExteriorRing(), lineString, orientationIndex, d);
        if ((r != null) && !r.isEmpty() && (r.coord().size() != 1)) {
          result.add(r);
        } // modif JFG
        // go through all interior rings
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
          LineString ring = polygon.getInteriorRingN(i);
          // build the offset curve for the interior ring
          r = JtsAlgorithms.getOffsetCurveFromRing(ring, lineString,
              orientationIndex, d);
          if ((r != null) && !r.isEmpty() && (r.coord().size() != 1)) {
            result.add(r);
          } // modif JFG
        }
        return result;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Remove duplicate coordinates from the input {@link GM_LineString}.
   * @param lineString input {@link GM_LineString}
   * @return a {@link GM_LineString} without duplicated coordinates
   */
  private static LineString getLineStringWithoutDuplicates(LineString lineString) {
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
    if (coordinates.size() >= 2) { // modif JFG
      return lineString.getFactory().createLineString(
          coordinates.toArray(new Coordinate[coordinates.size()]));
    } else {
      return null;
    }
  }

  /**
   * Measure the maximum error in the buffer computation. This error is measured
   * by computing the distance between all point in the buffer
   * {@link LineString} and the input {@link LineString} used to compute the
   * buffer.
   * @param bufferRing one of the buffer rings
   * @param line input {@link LineString} used to compute the buffer
   * @param distance distance used to compute the buffer
   * @return maximum error in the buffer computation
   */
  public static double bufferError(LineString bufferRing, LineString line,
      double distance) {
    double maxError = 0;
    for (Coordinate c : bufferRing.getCoordinates()) {
      double d = line.distance(line.getFactory().createPoint(c));
      maxError = Math.max(maxError, Math.abs(d - distance));
    }
    return maxError;
  }

  /**
   * Build an offset curve using the given {@link LineString} and the reference
   * {@link LineString}.
   * @param ring {@link LineString} used to build the offsetcurve. These come
   *          from a linear ring (exterior or interior)
   * @param line the reference {@link LineString}
   * @param orientationIndex orientation of the offset curve to build
   * @param distance offset distance
   * @param tolerance tolerance used to determine on which side of the
   *          linestring a point lies
   * @return the offsetcurve
   */
  private static ILineString getOffsetCurveFromRing(LineString ring,
      LineString line, int orientationIndex, double distance) {
    // go through the coordinates of the buffer and select the range
    // of coordinates of the right side
    List<Coordinate> coordinateBuffer = null;
    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    Coordinate previousCoordinate = ring.getCoordinateN(0);
    double tolerance = JtsAlgorithms.bufferError(ring, line, distance);
    if (tolerance > 0) {
      double e = Math.pow(10, Math.floor(Math.log10(tolerance)));
      tolerance = e * Math.ceil(tolerance / e);
    }
    int previousOrientation = JtsAlgorithms.orientationIndex(
        previousCoordinate, line, tolerance);
    if (previousOrientation == orientationIndex) {
      coordinateList.add(previousCoordinate);
    }
    int lastNonNullOrientation = previousOrientation;
    for (int i = 1; i < ring.getNumPoints(); i++) {
      Coordinate currentCoordinate = ring.getCoordinateN(i);
      int currentOrientation = JtsAlgorithms.orientationIndex(
          currentCoordinate, line, tolerance);
      if (currentOrientation == 0) {
        if (lastNonNullOrientation == orientationIndex) {
          // undetermied orientation but the last determined
          // orientation was the proper one
          coordinateList.add(currentCoordinate);
        }
      } else {
        if (currentOrientation == orientationIndex) {
          // the new orientation is the targetted one
          if (previousOrientation == 0
              && lastNonNullOrientation != orientationIndex) {
            // the previous coordinate was undetermined
            coordinateList.add(previousCoordinate);
          }
          coordinateList.add(currentCoordinate);
        } else {
          if (currentOrientation == -orientationIndex
              && !coordinateList.isEmpty()) {
            // we switched to the other side
            // we used a buffer in case we started in the
            // middle of the targeted offset curve
            coordinateBuffer = coordinateList;
            coordinateList = new ArrayList<Coordinate>();
          }
        }
      }
      previousOrientation = currentOrientation;
      previousCoordinate = currentCoordinate;
      if (currentOrientation != 0) {
        lastNonNullOrientation = currentOrientation;
      }
    }
    if (coordinateList.isEmpty() && coordinateBuffer == null) {
      return null;
    }
    if (coordinateBuffer != null && !coordinateBuffer.isEmpty()) {
      coordinateList.addAll(coordinateBuffer);
    }
    // build the linestring using the determined range of coordinates
    if (coordinateList.size() < 2) {
      return null;
    }
    // remove the points on the round caps
    boolean cap = true;
    while (cap) {
      if (coordinateList.size() < 2) {
        return null;
      }
      Coordinate c1 = coordinateList.get(0);
      Coordinate c2 = coordinateList.get(1);
      cap = ((c1.distance(line.getCoordinateN(0)) < distance + tolerance) && (c2
          .distance(line.getCoordinateN(0)) < distance + tolerance))
          || ((c1.distance(line.getCoordinateN(line.getNumPoints() - 1)) < distance
              + tolerance) && (c2.distance(line.getCoordinateN(line
              .getNumPoints() - 1)) < distance + tolerance));
      if (cap) {
        coordinateList.remove(0);
      }
    }
    cap = true;
    while (cap) {
      if (coordinateList.size() < 2) {
        return null;
      }
      Coordinate c1 = coordinateList.get(coordinateList.size() - 1);
      Coordinate c2 = coordinateList.get(coordinateList.size() - 2);
      cap = ((c1.distance(line.getCoordinateN(0)) < distance + tolerance) && (c2
          .distance(line.getCoordinateN(0)) < distance + tolerance))
          || ((c1.distance(line.getCoordinateN(line.getNumPoints() - 1)) < distance
              + tolerance) && (c2.distance(line.getCoordinateN(line
              .getNumPoints() - 1)) < distance + tolerance));
      if (cap) {
        coordinateList.remove(coordinateList.size() - 1);
      }
    }
    // 2 coordinates are the same
    if (coordinateList.size() == 2
        && coordinateList.get(0).equals2D(coordinateList.get(1))) {
      return null;
    }
    ILineString result = new GM_LineString(
        AdapterFactory.toDirectPositionList(coordinateList
            .toArray(new Coordinate[coordinateList.size()])));
    return result;
  }

  /**
   * Determine if the {@link Coordinate} is on the round cap of the buffer
   * computed from the input {@link LineString} and the input distance. To
   * determine that, it has to be at the given distance to the first or last
   * point of the input {@link LineString} but closer to the first or last line
   * segments.
   * @param c a {@link Coordinate}
   * @param line the input {@link LineString}
   * @param distance distance used to compute the buffer
   * @param tolerance tolerance used to compare distances
   * @param startCoordinate if true, used the first point of the input
   *          {@link LineString}
   * @return true if the input {@link Coordinate} is on the round cap
   */
  private static boolean isOnRoundCap(Coordinate c, LineString line,
      double distance, double tolerance, boolean startCoordinate) {
    Coordinate c1 = startCoordinate ? line.getCoordinateN(0) : line
        .getCoordinateN(line.getNumPoints() - 1);
    double d = c.distance(c1);
    if (d > distance - tolerance && d < distance + tolerance) {
      Coordinate c2 = startCoordinate ? line.getCoordinateN(1) : line
          .getCoordinateN(line.getNumPoints() - 2);
      LineSegment l = new LineSegment(c2, c1);
      d = l.distancePerpendicular(c);
      return (d < distance - tolerance);
    }
    return false;
  }

  /**
   * Determine the orientation of a {@link Coordinate} relative to a
   * {@link LineString}.
   * @param c coordinate
   * @param line the reference linestring
   * @param tolerance tolerance used to determine the closest line segments
   * @return +1 if the coordinate is on the left, -1 if it is on the right, 0
   *         otherwise.
   */
  public static int orientationIndex(Coordinate c, LineString line,
      double tolerance) {
    double distanceMin = line.distance(line.getFactory().createPoint(c));
    List<Integer> orientations = new ArrayList<Integer>();
    int previousOrientation = 0;
    Coordinate previousCoordinate = null;
    for (int i = 0; i < line.getNumPoints() - 1; i++) {
      Coordinate coordinate1 = line.getCoordinateN(i);
      Coordinate coordinate2 = line.getCoordinateN(i + 1);
      // test if the coordinate is duplicated in the linestring
      if (!coordinate1.equals2D(coordinate2)) {
        LineSegment segment = new LineSegment(coordinate1, coordinate2);
        Coordinate closestPoint = segment.closestPoint(c);
        double d = closestPoint.distance(c);
        if (d <= distanceMin + tolerance) {
          int orientation = segment.orientationIndex(c);
          if (line.isCoordinate(closestPoint)) {
            // the closest coordinate is on the line
            // if it is the second point (i+1), we will deal with
            // it next iteration
            if (closestPoint.equals2D(coordinate1)) {
              // if there was a line segment before and it had a different
              // orientation
              if (previousCoordinate != null
                  && orientation != previousOrientation) {
                orientations.add(new Integer(-CGAlgorithms.orientationIndex(
                    previousCoordinate, coordinate1, coordinate2)));
              } else {
                orientations.add(new Integer(orientation));
              }
            } else {
              // the closest point is the second point but there
              // is no next iteration
              if (closestPoint.equals2D(coordinate2)
                  && i == line.getNumPoints() - 2) {
                orientations.add(new Integer(orientation));
              }
            }
          } else {
            // the closest coordinate is not on the line
            orientations.add(new Integer(orientation));
          }
          previousCoordinate = coordinate1;
          previousOrientation = orientation;
        }
      }
    }
    if (orientations.isEmpty()) {
      // JtsAlgorithms.logger.info("orientations empty for "
      // + line.getFactory().createPoint(c));
      return 0;
    }
    Iterator<Integer> orientationIterator = orientations.iterator();
    int orientationIndex = orientationIterator.next().intValue();
    while (orientationIterator.hasNext()) {
      int orientation = orientationIterator.next().intValue();
      if (orientation != orientationIndex) {
        return 0;
      }
    }
    return orientationIndex;
  }

  public static ILineString cap(ILineString line, double distance, boolean start) {
    double d = Math.abs(distance);
    try {
      // removing duplicate coordinates from the input linestring.
      LineString lineString = JtsAlgorithms
          .getLineStringWithoutDuplicates((LineString) JtsGeOxygene
              .makeJtsGeom(line));
      Geometry buffer = lineString.buffer(d, 4, BufferParameters.CAP_ROUND);
      Polygon polygon = null;
      if (!(buffer instanceof Polygon)) {
        JtsAlgorithms.logger.error("Can't compute offsetcurve of " + //$NON-NLS-1$
            buffer.getGeometryType());
        return null;
      }
      polygon = (Polygon) buffer;
      // build the offset curve for the exterior ring
      GM_LineString r = JtsAlgorithms.getCapFromRing(polygon.getExteriorRing(),
          lineString, d, start);
      return r;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static GM_LineString getCapFromRing(LineString ring, LineString line,
      double distance, boolean start) {
    // go through the coordinates of the buffer and select the range
    // of coordinates of the right side
    List<Coordinate> coordinateList = new ArrayList<Coordinate>();
    Coordinate previousCoordinate = ring.getCoordinateN(0);
    double tolerance = JtsAlgorithms.bufferError(ring, line, distance);
    if (tolerance > 0) {
      double e = Math.pow(10, Math.floor(Math.log10(tolerance)));
      tolerance = e * Math.ceil(tolerance / e);
    }
    boolean previousRoundCap = JtsAlgorithms.isOnRoundCap(previousCoordinate,
        line, distance, tolerance, start);
    if (previousRoundCap) {
      coordinateList.add(previousCoordinate);
    }
    for (int i = 1; i < ring.getNumPoints(); i++) {
      Coordinate currentCoordinate = ring.getCoordinateN(i);
      boolean currentRoundCap = JtsAlgorithms.isOnRoundCap(currentCoordinate,
          line, distance, tolerance, start);
      if (previousRoundCap
          && currentCoordinate.distance(start ? line.getCoordinateN(0) : line
              .getCoordinateN(line.getNumPoints() - 1)) < distance + tolerance) {
        coordinateList.add(currentCoordinate);
      } else {
        if (currentRoundCap) {
          // the new orientation is the targetted one
          if (!previousRoundCap
              && previousCoordinate.distance(start ? line.getCoordinateN(0)
                  : line.getCoordinateN(line.getNumPoints() - 1)) < distance
                  + tolerance) {
            // the previous coordinate was undetermined
            coordinateList.add(previousCoordinate);
          }
          coordinateList.add(currentCoordinate);
        }
      }
      previousRoundCap = currentRoundCap;
      previousCoordinate = currentCoordinate;
    }
    if (coordinateList.isEmpty() || coordinateList.size() < 2) {
      return null;
    }
    // build the linestring using the determined range of coordinates
    // 2 coordinates are the same
    if (coordinateList.size() == 2
        && coordinateList.get(0).equals2D(coordinateList.get(1))) {
      return null;
    }
    GM_LineString result = new GM_LineString(
        AdapterFactory.toDirectPositionList(coordinateList
            .toArray(new Coordinate[coordinateList.size()])));
    return result;
  }

  /**
   * Plus Petit Rectangle Englobant d'une géométrie préservant son aire.
   * Smallest Enclosing Rectangle of a geometry preserving its area.
   * 
   * @param geom une géométrie, a geometry
   * @return le Plus Petit Rectangle Englobant, the Smallest Enclosing Rectangle
   */
  public static Polygon MBRAirePreservee(Geometry geom) {
    Polygon out = JtsAlgorithms.MBR(geom);
    return JtsAlgorithms.homothetie(out,
        (float) Math.sqrt(geom.getArea() / out.getArea()));
  }

  /**
   * Plus Petit Rectangle Englobant d'une géométrie respectant un aire donnée.
   * Smallest Enclosing Rectangle of a geometry with a given area.
   * 
   * @param geom une géométrie, a geometry
   * @param aireCible aire visée, target area
   * @return le Plus Petit Rectangle Englobant, the Smallest Enclosing Rectangle
   */
  public static Polygon MBRAireCible(Geometry geom, double aireCible) {
    Polygon out = JtsAlgorithms.MBR(geom);
    return JtsAlgorithms.homothetie(out,
        (float) Math.sqrt(aireCible / out.getArea()));
  }

  /**
   * Minimum Bounding Rectangle of a geometry. Plus Petit Rectangle Englobant
   * d'une géométrie.
   * 
   * 
   * @param geom a geometry, une géométrie.
   * @return the Minimum Bounding Rectangle, le Plus Petit Rectangle Englobant.
   */
  public static Polygon MBR(Geometry geom) {
    // recupere l'enveloppe convexe
    Geometry convexHull = geom.convexHull();
    // si ce n'est pas un polygone, le MBR n'est pas defini: on renvoit null
    if (!(convexHull instanceof Polygon)) {
      JtsAlgorithms.logger.error("Le PPRE calculé n'est pas un polygone. " + //$NON-NLS-1$
          "Son type est " + convexHull.getGeometryType()); //$NON-NLS-1$
      return null;
    }
    Polygon env = (Polygon) convexHull;
    // prend les coordonnees de l'enveloppe convexe
    Coordinate[] coord = env.getExteriorRing().getCoordinates();
    Coordinate centre = geom.getCentroid().getCoordinate();
    // parcours les segments
    double aire_min = Double.MAX_VALUE, angle_ = 0.0;
    Polygon ppre = null;
    for (int i = 0; i < coord.length - 1; i++) {
      // calcul de la rotation de l'enveloppe convexe
      double angle = Math.atan2(coord[i + 1].y - coord[i].y, coord[i + 1].x
          - coord[i].x);
      try {
        Polygon rot = (Polygon) JtsAlgorithms.rotation(env, centre,
            -1.0 * angle).getEnvelope();
        // calcul l'aire de l'enveloppe rectangulaire
        double aire = rot.getArea();
        // verifie si elle est minimum
        if (aire < aire_min) {
          aire_min = aire;
          ppre = rot;
          angle_ = angle;
        }
      } catch (ClassCastException e) {
        JtsAlgorithms.logger.error(geom);
        JtsAlgorithms.logger.error(env);
        JtsAlgorithms.logger.error(JtsAlgorithms.rotation(env, centre,
            -1.0 * angle).getEnvelope());
      }
    }
    return JtsAlgorithms.rotation(ppre, centre, angle_);
  }

  /**
   * Minimum bounding square of a rectangle envelope.
   * 
   * @param env the envelope to enclose by a square.
   * @return The minimum bounding square of a rectangle envelope
   */
  public static Polygon squareEnveloppe(Envelope env) {
    Polygon square = null;
    GeometryFactory factory = new GeometryFactory();

    double height = env.getHeight();
    double width = env.getWidth();

    if (height > width) {
      double diff = height - width;
      Coordinate[] coord = {
          new Coordinate(env.getMinX() - diff / 2, env.getMinY()),
          new Coordinate(env.getMaxX() + diff / 2, env.getMinY()),
          new Coordinate(env.getMaxX() + diff / 2, env.getMaxY()),
          new Coordinate(env.getMinX() - diff / 2, env.getMaxY()),
          new Coordinate(env.getMinX() - diff / 2, env.getMinY()) };

      LinearRing lr = factory.createLinearRing(coord);
      square = factory.createPolygon(lr, null);
    } else {
      double diff = width - height;
      Coordinate[] coord = {
          new Coordinate(env.getMinX(), env.getMinY() - diff / 2),
          new Coordinate(env.getMaxX(), env.getMinY() - diff / 2),
          new Coordinate(env.getMaxX(), env.getMaxY() + diff / 2),
          new Coordinate(env.getMinX(), env.getMaxY() + diff / 2),
          new Coordinate(env.getMinX(), env.getMinY() - diff / 2) };

      LinearRing lr = factory.createLinearRing(coord);
      square = factory.createPolygon(lr, null);
    }

    return square;
  }

  /**
   * Minimum Bounding Square of a geometry. Plus Petit Carré Englobant d'une
   * géométrie.
   * 
   * 
   * @param geom a geometry, une géométrie.
   * @return the Minimum Bounding Square, le Plus Petit Carré Englobant.
   */
  public static Polygon MBS(Geometry geom) {
    // recupere l'enveloppe convexe
    Geometry convexHull = geom.convexHull();
    // si ce n'est pas un polygone, le MBR n'est pas defini: on renvoit null
    if (!(convexHull instanceof Polygon)) {
      JtsAlgorithms.logger.error("Le PPRE calculé n'est pas un polygone. " + //$NON-NLS-1$
          "Son type est " + convexHull.getGeometryType()); //$NON-NLS-1$
      return null;
    }
    Polygon env = (Polygon) convexHull;
    // prend les coordonnees de l'enveloppe convexe
    Coordinate[] coord = env.getExteriorRing().getCoordinates();
    Coordinate centre = geom.getCentroid().getCoordinate();
    // parcours les segments
    double aire_min = Double.MAX_VALUE, angle_ = 0.0;
    Polygon ppre = null;
    for (int i = 0; i < coord.length - 1; i++) {
      // calcul de la rotation de l'enveloppe convexe
      double angle = Math.atan2(coord[i + 1].y - coord[i].y, coord[i + 1].x
          - coord[i].x);
      try {
        Polygon rot = JtsAlgorithms.squareEnveloppe(JtsAlgorithms.rotation(env,
            centre, -1.0 * angle).getEnvelopeInternal());
        // calcul l'aire de l'enveloppe carrée
        double aire = rot.getArea();
        // verifie si elle est minimum
        if (aire < aire_min) {
          aire_min = aire;
          ppre = rot;
          angle_ = angle;
        }
      } catch (ClassCastException e) {
        JtsAlgorithms.logger.error(geom);
        JtsAlgorithms.logger.error(env);
        JtsAlgorithms.logger.error(JtsAlgorithms.rotation(env, centre,
            -1.0 * angle).getEnvelope());
      }
    }
    return JtsAlgorithms.rotation(ppre, centre, angle_);
  }

  /**
   * Plus Petit Carré Englobant d'une géométrie préservant son aire. Smallest
   * Enclosing Suare of a geometry preserving its area.
   * 
   * @param geom une géométrie, a geometry
   * @return le Plus Petit Carré Englobant, the Smallest Enclosing Square
   */
  public static Polygon MBSAirePreservee(Geometry geom) {
    Polygon out = JtsAlgorithms.MBS(geom);
    return JtsAlgorithms.homothetie(out,
        (float) Math.sqrt(geom.getArea() / out.getArea()));
  }

  /**
   * Rotate a geometry. Effectue une rotation sur une géométrie.
   * 
   * 
   * @param geom une géométrie, a geometry
   * @param c centre de la rotation, center of the rotation
   * @param angle angle de rotation, angle of rotation
   * @return polygone résultant de la rotation, resulting polygon.
   */
  public static Polygon rotation(Polygon geom, Coordinate c, double angle) {
    double cos = Math.cos(angle), sin = Math.sin(angle);
    // rotation de l'enveloppe
    Coordinate[] coord = geom.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      double x = coord[i].x, y = coord[i].y;
      coord_[i] = new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y), c.y
          + sin * (x - c.x) + cos * (y - c.y));
    }
    LinearRing shell = geom.getFactory().createLinearRing(coord_);

    // rotation des trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      Coordinate[] coord2 = geom.getInteriorRingN(j).getCoordinates();
      Coordinate[] coord2_ = new Coordinate[coord2.length];
      for (int i = 0; i < coord2.length; i++) {
        double x = coord2[i].x, y = coord2[i].y;
        coord2_[i] = new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y),
            c.y + sin * (x - c.x) + cos * (y - c.y));
      }
      trous[j] = geom.getFactory().createLinearRing(coord2);
    }
    return geom.getFactory().createPolygon(shell, trous);
  }

  /**
   * Calcule l'homothétie d'une géométrie.
   * @param geom géométrie, geometry
   * @param x0 position en X du centre de l'homothétie, X position of the center
   *          of the operation
   * @param y0 position en Y du centre de l'homothétie, Y position of the center
   *          of the operation
   * @param scale facteur d'échelle, scale factor
   * @return polygon résultant de l'homothétie, resulting polygon
   */
  public static Polygon homothetie(Polygon geom, double x0, double y0,
      double scale) {
    // le contour externe
    Coordinate[] coord = geom.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(x0 + scale * (coord[i].x - x0), y0 + scale
          * (coord[i].y - y0));
    }
    LinearRing lr = geom.getFactory().createLinearRing(coord_);

    // les trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      Coordinate[] hole_coord = geom.getInteriorRingN(j).getCoordinates();
      Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
      for (int i = 0; i < hole_coord.length; i++) {
        hole_coord_[i] = new Coordinate(x0 + scale * (hole_coord[i].x - x0), y0
            + scale * (hole_coord[i].y - y0));
      }
      trous[j] = geom.getFactory().createLinearRing(hole_coord_);
    }
    return geom.getFactory().createPolygon(lr, trous);
  }

  /**
   * Calcule l'homothétie d'une géométrie.
   * @param geom géométrie, geometry
   * @param x0 position en X du centre de l'homothétie, X position of the center
   *          of the operation
   * @param y0 position en Y du centre de l'homothétie, Y position of the center
   *          of the operation
   * @param scaleX facteur d'échelle en X, X scale factor
   * @param scaleY facteur d'échelle en Y, Y scale factor
   * @return polygon résultant de l'homothétie, resulting polygon
   */
  public static Polygon homothetie(Polygon geom, double x0, double y0,
      double scaleX, double scaleY) {
    // le contour externe
    Coordinate[] coord = geom.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(x0 + scaleX * (coord[i].x - x0), y0 + scaleY
          * (coord[i].y - y0));
    }
    LinearRing lr = geom.getFactory().createLinearRing(coord_);

    // les trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      Coordinate[] hole_coord = geom.getInteriorRingN(j).getCoordinates();
      Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
      for (int i = 0; i < hole_coord.length; i++) {
        hole_coord_[i] = new Coordinate(x0 + scaleY * (hole_coord[i].x - x0),
            y0 + scaleY * (hole_coord[i].y - y0));
      }
      trous[j] = geom.getFactory().createLinearRing(hole_coord_);
    }
    return geom.getFactory().createPolygon(lr, trous);
  }

  /**
   * Calcule l'homothétie d'une géométrie.
   * @param geom géométrie, geometry
   * @param scaleX facteur d'échelle en X, X scale factor
   * @param scaleY facteur d'échelle en Y, Y scale factor
   * @return polygon résultant de l'homothétie, resulting polygon
   */
  public static Polygon homothetie(Polygon geom, double scaleX, double scaleY) {
    return JtsAlgorithms.homothetie(geom, geom.getCentroid().getX(), geom
        .getCentroid().getY(), scaleX, scaleY);
  }

  /**
   * Calcule l'homothétie d'une géométrie.
   * @param geom géométrie, geometry
   * @param scale facteur d'échelle, scale factor
   * @return polygon résultant de l'homothétie, resulting polygon
   */
  public static Polygon homothetie(Polygon geom, double scale) {
    return JtsAlgorithms.homothetie(geom, geom.getCentroid().getX(), geom
        .getCentroid().getY(), scale);
  }

} // class
