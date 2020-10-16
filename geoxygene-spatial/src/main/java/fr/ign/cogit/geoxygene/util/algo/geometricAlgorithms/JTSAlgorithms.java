package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class JTSAlgorithms {

  /**
   * Gets a random point that lies inside a polygon geometry.
   * 
   * @param surf the polygon the point has to inside
   * @return a point inside the polygon
   * @author GTouya
   */
  public static IDirectPosition getInteriorPoint(IPolygon surf) {
    // conversion en geometrie JTS
    Geometry geom = null;
    try {
      geom = AdapterFactory.toGeometry(new GeometryFactory(), surf);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (geom == null) {
      return null;
    }
    Point p = null;
    try {
      p = geom.getInteriorPoint();
    } catch (TopologyException e) {
      p = geom.getCentroid();
    }
    return new DirectPosition(p.getX(), p.getY());
  }

  /**
   * Test the covers predicate of JTS intersection model. Test if geom1 covers
   * geom2. The covers predicate is an extension of "contains" to the boundary:
   * a polygon does not contain its boundary but covers it.
   * 
   * @param geom1 the geometry that should cover the other
   * @param geom2 the geometry that should be covered
   * @return true if geom1 covers geom2
   * @author GTouya
   */
  public static boolean coversPredicate(IGeometry igeom1, IGeometry igeom2) {
    Geometry geom1 = null;
    try {
      geom1 = AdapterFactory.toGeometry(new GeometryFactory(), igeom1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (geom1 == null) {
      return false;
    }
    Geometry geom2 = null;
    try {
      geom2 = AdapterFactory.toGeometry(new GeometryFactory(), igeom2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (geom2 == null) {
      return false;
    }

    return geom1.covers(geom2);
  }

  @SuppressWarnings("unchecked")
  public static Collection<IPolygon> unionAdjacentPolygons(
      Collection<IGeometry> geoms) {
    List<Geometry> list = new ArrayList<Geometry>();
    Collection<IPolygon> outColn = new HashSet<IPolygon>();
    try {
      // Detection of invalid polygons
      for (IGeometry geom : geoms) {
        Geometry jtsGeom;
        jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
        if (jtsGeom instanceof MultiPolygon) {
          MultiPolygon mp = (MultiPolygon) jtsGeom;
          for (int i = 0; i < mp.getNumGeometries(); i++) {
            Polygon poly = (Polygon) mp.getGeometryN(i);
            if (poly.isValid() && poly.getArea() != 0) {
              list.add(poly);
            }
          }
        } else {
          if (jtsGeom instanceof Polygon) {
            Polygon poly = (Polygon) jtsGeom;
            if (poly.isValid() && poly.getArea() != 0) {
              list.add(poly);
            }
          } else {
            list.add(jtsGeom);
          }
        }
      }

      // Beastly union of the polygons
      Geometry jtsUnion = JtsAlgorithms.union(list);
      IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);

      // Split MultiPolygons and fill Polygons in the population
      if (union.isMultiSurface()) {
        IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) union;
        for (IPolygon polygon : multiPoly.getList()) {
          outColn.add(polygon);
        }
      } else if (union.isPolygon()) {
        outColn.add((IPolygon) union);
      }

      return outColn;

    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return outColn;
  }

  public static boolean isClockwise(ILineString line) {
    Geometry geom = null;
    try {
      geom = AdapterFactory.toGeometry(new GeometryFactory(), line);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (Orientation.isCCW(geom.getCoordinates()))
      return false;
    return true;
  }

  public static boolean isClockwise(IPolygon polygon) {
    Geometry geom = null;
    try {
      geom = AdapterFactory.toGeometry(new GeometryFactory(), polygon);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (Orientation.isCCW(geom.getCoordinates()))
      return false;
    return true;
  }
}
