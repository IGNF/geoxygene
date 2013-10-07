package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class MorphologyTransform {

  private double bufferSize;
  private int bufferStep;

  public MorphologyTransform(double bufferSize, int bufferStep) {
    super();
    this.bufferSize = bufferSize;
    this.bufferStep = bufferStep;
  }

  /**
   * Opening is an erosion then a dilatation of the polygon with the same
   * buffer. May return a null geometry or IMultiSurface.
   * @param polygon
   * @return
   */
  public IGeometry opening(IPolygon polygon) {
    // compute the erosion
    IGeometry intBuffer = erosion(polygon);

    // test if the geometry is null
    if (intBuffer == null) {
      return null;
    }

    // now the dilatation
    IGeometry dilatated = intBuffer.buffer(bufferSize, bufferStep);
    // finally, the geometry is slightly filtered
    IGeometry opened = Filtering.DouglasPeucker(dilatated, 1.0);

    return opened;
  }

  /**
   * Closing is the opposite of opening: a dilatation and than an erosion of the
   * polygon with the same buffer.
   * @param polygon
   * @return
   */
  public IPolygon closing(IPolygon polygon) {
    try {
      IPolygon buffer = (IPolygon) polygon.buffer(bufferSize, bufferStep);
      // the buffered geometry is slightly filtered to avoid small geometry
      // problems
      IPolygon filtered = Filtering.DouglasPeuckerPoly(buffer, 1.0);
      IGeometry closed = erosion(filtered);
      if (closed instanceof IPolygon)
        return (IPolygon) closed;
      return polygon;
    } catch (Exception e) {
      return polygon;
    }
  }

  /**
   * <p>
   * Apply erosion on a polygon with no hole.<br>
   * 
   * @param geom : the polygon without hole to erode.
   */
  public IGeometry erosionNoHole(IPolygon geom) {
    // get the exterior of the polygon
    IRing exterior = geom.getExterior();
    // buffer the exterior
    IPolygon buffer = (IPolygon) exterior.buffer(bufferSize, bufferStep);

    // the geometrie(s) that we're interested in : the buffer holes.
    // if there is only one, it's simple, return it
    if (buffer.getInterior().size() == 1) {
      // we still have to check the hole is within the initial geometry
      IRing hole = buffer.getInterior(0);
      // take a random point on the ring
      IDirectPosition coord = hole.coord().get(0);
      if (geom.contains(new GM_Point(coord))) {
        // then return the hole as a polygon
        IPolygon surf = new GM_Polygon(hole);
        return surf;
      }
      // else return null
      return null;

      // if there is no hole, return null
    } else if (buffer.getInterior().size() == 0) {
      return null;

      // last case : multiple holes, return a multisurface.
    } else {
      List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
      for (IRing hole : buffer.getInterior()) {
        IPolygon surf = new GM_Polygon(hole);
        // on n'ajoute que les surfaces contenues dans geom
        if (surf.disjoint(geom)) {
          continue;
        }
        lOS.add(surf);
      }
      if (lOS.size() == 0) {
        return null;
      }
      IMultiSurface<IOrientableSurface> surfComp = new GM_MultiSurface<IOrientableSurface>(
          lOS);
      return surfComp;
    }
  }

  /**
   * <p>
   * Apply erosion to a polygon that can contain holes. <br>
   * 
   * @param geom : la géométrie à modifier.
   * @return IGeometry : the eroded geometry (a IPolygon or a IMultiSurface or
   *         null).
   */
  public IGeometry erosion(IPolygon geom) {
    // builds a no-hole polygon from the exterior
    IPolygon noHole = new GM_Polygon(geom.getExterior());

    // first erode the no-hole polygon
    IGeometry eroded = erosionNoHole(noHole);
    if (eroded == null) {
      return null;
    }

    // now take care of the holes
    for (IRing hole : geom.getInterior()) {
      // make this hole a polygon
      IPolygon polygon = new GM_Polygon(hole);
      // buffer this polygon
      IGeometry buffer = polygon.buffer(bufferSize, bufferStep);
      // remove the polygon from the eroded geometry
      eroded = eroded.difference(buffer);
    }
    if (eroded == null) {
      return null;
    }
    if (eroded.isEmpty())
      return null;
    return eroded;
  }

  /**
   * <p>
   * Erosion for IMultiSurface geometries. <br>
   * 
   * @param geom : the geometry to erode.
   */
  public IGeometry erosion(IMultiSurface<IOrientableSurface> geom) {
    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    for (IOrientableSurface simple : geom.getList()) {
      IGeometry eroded = erosion((IPolygon) simple);
      if (eroded == null)
        continue;
      lOS.add((IOrientableSurface) eroded);
    }

    if (lOS.size() == 0) {
      return null;
    }
    IMultiSurface<IOrientableSurface> surfComp = new GM_MultiSurface<IOrientableSurface>(
        lOS);
    return surfComp;
  }

  @SuppressWarnings("unchecked")
  public IPolygon minkowskiSumWithCustomPolyCentr(IPolygon polygon,
      IPolygon polyToSum) {
    // easy case: no holes in polygon
    if (polygon.getInterior().size() == 0) {
      return minkowskiSumWithCustomPolyCentrNoHole(polygon, polyToSum);
    }

    // case with holes in polygon: the final polygon should be the minkowski sum
    // of the exterior and the minkowski difference for the inner rings.
    IPolygon polyNoHole = new GM_Polygon(polygon.getExterior());
    IPolygon extSum = minkowskiSumWithCustomPolyCentrNoHole(polyNoHole,
        polyToSum);
    // loop on the inner rings to reduce each one and add it as a hole for
    // extSum
    for (IRing inner : polygon.getInterior()) {
      IGeometry innerDiff = minkowskiDiffWithCustomPolyCentrNoHole(
          new GM_Polygon(inner), polyToSum);
      if (innerDiff == null)
        continue;
      if (innerDiff instanceof IPolygon)
        extSum.addInterior(((IPolygon) innerDiff).getExterior());
      if (innerDiff instanceof IMultiSurface) {
        for (Object part : ((IMultiSurface) innerDiff).getList()) {
          IPolygon hole = (IPolygon) part;
          extSum.addInterior(hole.getExterior());
        }
      }
    }

    return extSum;
  }

  public IGeometry minkowskiDiffWithCustomPolyCentr(IPolygon polygon,
      IPolygon polyToSum) {
    // easy case: no holes in polygon
    if (polygon.getInterior().size() == 0) {
      return minkowskiDiffWithCustomPolyCentrNoHole(polygon, polyToSum);
    }

    // case with holes in polygon: the final polygon should be the minkowski
    // difference of the exterior and the minkowski sum for the inner rings.
    IPolygon polyNoHole = new GM_Polygon(polygon.getExterior());
    IGeometry extSum = minkowskiDiffWithCustomPolyCentrNoHole(polyNoHole,
        polyToSum);
    // loop on the inner rings to reduce each one and add it as a hole for
    // extSum
    for (IRing inner : polygon.getInterior()) {
      IGeometry innerDiff = minkowskiDiffWithCustomPolyCentrNoHole(
          new GM_Polygon(inner), polyToSum);
      if (innerDiff == null)
        continue;
      extSum.difference(innerDiff);
    }

    return extSum;
  }

  private IPolygon minkowskiSumWithCustomPolyCentrNoHole(IPolygon polygon,
      IPolygon polyToSum) {
    // get the exterior LineString of polygon
    ILineString exterior = polygon.exteriorLineString();
    // reverse the line to make its left side the exterior of the polygon
    exterior = (ILineString) exterior.reverse();
    // compute the dilatation of the exterior with polyToSum
    IPolygon union = (IPolygon) lineLeftDilatationFromPolyCentr(exterior,
        polyToSum);
    // if union contains holes, they have to be removed
    return CommonAlgorithmsFromCartAGen.removeHoles(union);
  }

  private IGeometry minkowskiDiffWithCustomPolyCentrNoHole(IPolygon polygon,
      IPolygon polyToSum) {
    // get the exterior LineString of polygon
    ILineString exterior = polygon.exteriorLineString();
    // compute the dilatation of the exterior with polyToSum
    IPolygon union = lineLeftDilatationFromPolyCentr(exterior, polyToSum);

    // the polygon to retrieve is the union of the inner rings of the union
    // geometry

    // special case, if union does not contain holes, polygon was two small to
    // be reduced by polyToSum and null is returned
    if (union.getInterior().size() == 0)
      return null;

    // special case with only one hole: it's the geometry to return
    if (union.getInterior().size() == 1)
      return new GM_Polygon(union.getInterior(0));

    // general case, several holes to merge into the returned geometry
    IGeometry finalGeom = null;
    for (IRing inner : union.getInterior()) {
      if (finalGeom == null)
        finalGeom = new GM_Polygon(inner);
      else {
        finalGeom = finalGeom.union(new GM_Polygon(inner));
      }
    }

    return finalGeom;
  }

  /**
   * Compute the union of the dilatation of each segment of the line. But, the
   * dilatation is guided by the parameter polygon. It's like a minkowski sum of
   * the line and the polygon but the line is only dilatated from the distance
   * to polygon centroid. It helps to build what C. Duchêne calls reduced areas
   * (PhD, 2004): if the centroid of the polygon is moved outside the dilatated
   * area, it won't intersect the line. Be careful, the convex hull of the
   * parameter polygon is rather used to simplify the problem.
   * @param line
   * @return
   */
  private IPolygon lineLeftDilatationFromPolyCentr(ILineString line,
      IPolygon polyToSum) {
    // Use convex hull
    polyToSum = (IPolygon) polyToSum.convexHull();

    // Create return area for each point of the line.
    IDirectPosition pointA = line.getControlPoint(0);
    IDirectPosition pointB = line.getControlPoint(1);
    IDirectPosition pointC = line.getControlPoint(2);

    double dX = pointB.getX() - pointA.getX();
    double dY = pointB.getY() - pointA.getY();

    double theta = Math.atan2(dY, dY) - Math.PI / 2;

    // for each vector, create an area representing the dillatation

    // get the constrained point of polyToSum

    // construct the area

    // the segment is

    return null;
  }

  private IDirectPosition getFarestPoint(IPolygon polygon, double angle) {
    IPolygon rotation = CommonAlgorithms.rotation(polygon, angle);
    IDirectPosition toReturn = null;
    for (IDirectPosition toCompare : rotation.coord()) {
      if (toReturn == null) {
        toReturn = toCompare;
      } else if (toReturn.getX() < toCompare.getX()) {
        toReturn = toCompare;
      }
      // TODO when egal

    }
    return toReturn;
  }
}
