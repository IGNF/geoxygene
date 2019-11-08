package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class MorphologyTransform {

    @SuppressWarnings("unused")
    private static Logger logger = Logger
            .getLogger(MorphologyTransform.class.getName());

    private double bufferSize;
    private int bufferStep;
    private int capForm = BufferParameters.CAP_ROUND;

    public MorphologyTransform(double bufferSize, int bufferStep) {
        super();
        this.bufferSize = bufferSize;
        this.bufferStep = bufferStep;
    }

    public MorphologyTransform() {
        super();
    }

    /**
     * Opening is an erosion then a dilatation of the polygon with the same
     * buffer. May return a null geometry or IMultiSurface.
     * 
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
        IGeometry dilatated = intBuffer.buffer(bufferSize, bufferStep, capForm,
                capForm);
        // finally, the geometry is slightly filtered
        IGeometry opened = Filtering.DouglasPeucker(dilatated, 1.0);

        return opened;
    }

    /**
     * Opening is an erosion then a dilatation of the polygon with the same
     * buffer. May return a null geometry or IMultiSurface.
     * 
     * @param polygon
     * @return
     */
    public IGeometry opening(IMultiSurface<IPolygon> geometry) {
        // compute the erosion
        IGeometry intBuffer = erosionMultiPolygon(geometry);

        // test if the geometry is null
        if (intBuffer == null) {
            return null;
        }

        // now the dilatation
        IGeometry dilatated = intBuffer.buffer(bufferSize, bufferStep, capForm,
                capForm);
        // finally, the geometry is slightly filtered
        IGeometry opened = Filtering.DouglasPeucker(dilatated, 1.0);

        return opened;
    }

    /**
     * Closing is the opposite of opening: a dilatation and than an erosion of
     * the polygon with the same buffer.
     * 
     * @param polygon
     * @return
     */
    public IPolygon closing(IPolygon polygon) {
        try {
            IPolygon buffer = (IPolygon) polygon.buffer(bufferSize, bufferStep,
                    capForm, capForm);
            // the buffered geometry is slightly filtered to avoid small
            // geometry
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
     * Closing method for a multi-polygon.
     * 
     * @param polygon
     * @return
     */
    @SuppressWarnings("unchecked")
    public IMultiSurface<IPolygon> closingMultiPolygon(
            IMultiSurface<IPolygon> geom) {
        try {
            IGeometry buffer = geom.buffer(bufferSize, bufferStep, capForm,
                    capForm);
            // the buffered geometry is slightly filtered to avoid small
            // geometry
            // problems
            IGeometry filtered = Filtering.DouglasPeucker(buffer, 1.0);
            if (filtered instanceof IMultiSurface<?>)
                return erosionMultiPolygon((IMultiSurface<IPolygon>) filtered);
            else if (filtered instanceof IPolygon) {
                IMultiSurface<IPolygon> multi = GeometryEngine.getFactory()
                        .createMultiPolygon();
                multi.add((IPolygon) filtered);
                return erosionMultiPolygon(multi);
            }
            return geom;
        } catch (Exception e) {
            e.printStackTrace();
            return geom;
        }
    }

    /**
     * Closing method for a multi-surface.
     * 
     * @param polygon
     * @return
     */
    @SuppressWarnings("unchecked")
    public IGeometry closing(IMultiSurface<IOrientableSurface> geom) {
        try {
            IMultiSurface<IOrientableSurface> buffer = (IMultiSurface<IOrientableSurface>) geom
                    .buffer(bufferSize, bufferStep, capForm, capForm);
            // the buffered geometry is slightly filtered to avoid small
            // geometry
            // problems
            IGeometry filtered = Filtering.DouglasPeucker(buffer, 1.0);
            IGeometry closed = erosion(
                    (IMultiSurface<IOrientableSurface>) filtered);
            return closed;
        } catch (Exception e) {
            return geom;
        }
    }

    /**
     * <p>
     * Apply erosion on a polygon with no hole.<br>
     * 
     * @param geom
     *            : the polygon without hole to erode.
     */
    public IGeometry erosionNoHole(IPolygon geom) {
        // get the exterior of the polygon
        IRing exterior = geom.getExterior();
        // buffer the exterior
        IPolygon buffer = (IPolygon) exterior.buffer(bufferSize, bufferStep,
                capForm, capForm);

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
     * @param geom
     *            : geometry to modifie.
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
            IGeometry buffer = polygon.buffer(bufferSize, bufferStep, capForm,
                    capForm);
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
     * @param geom
     *            : the geometry to erode.
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

    /**
     * <p>
     * Erosion for IMultiSurface<IPolygon> geometries. <br>
     * 
     * @param geom
     *            : the geometry to erode.
     */
    @SuppressWarnings("unchecked")
    public IMultiSurface<IPolygon> erosionMultiPolygon(
            IMultiSurface<IPolygon> geom) {
        List<IPolygon> lOS = new ArrayList<IPolygon>();

        for (IPolygon simple : geom.getList()) {
            IGeometry eroded = erosion(simple);
            if (eroded == null)
                continue;
            if (eroded instanceof IPolygon) {
                lOS.add((IPolygon) eroded);
            } else {
                for (IPolygon simpleEroded : ((IMultiSurface<IPolygon>) eroded)
                        .getList())
                    lOS.add(simpleEroded);
            }
        }

        if (lOS.size() == 0) {
            return null;
        }
        IMultiSurface<IPolygon> surfComp = new GM_MultiSurface<IPolygon>(lOS);
        return surfComp;
    }

    public IPolygon minkowskiSumWithCustomPolyCentr(IPolygon polygon,
            IPolygon polyToSum, IDirectPosition centroid) {
        if (polygon == null)
            return null;

        // easy case: no holes in polygon
        if (polygon.getInterior().size() == 0) {
            return minkowskiSumWithCustomPolyCentrNoHole(polygon, polyToSum,
                    centroid);
        }

        // case with holes in polygon: the final polygon should be the minkowski
        // sum
        // of the exterior and the minkowski difference for the inner rings.
        IPolygon polyNoHole = new GM_Polygon(polygon.getExterior());
        IPolygon extSum = minkowskiSumWithCustomPolyCentrNoHole(polyNoHole,
                polyToSum, centroid);
        // loop on the inner rings to reduce each one and add it as a hole for
        // extSum
        for (IRing inner : polygon.getInterior()) {
            IGeometry innerDiff = minkowskiDiffWithCustomPolyCentrNoHole(
                    new GM_Polygon(inner), polyToSum, centroid);
            if (innerDiff == null)
                continue;
            if (innerDiff instanceof IPolygon)
                extSum.addInterior(((IPolygon) innerDiff).getExterior());
            if (innerDiff instanceof IMultiSurface) {
                for (Object part : ((IMultiSurface<?>) innerDiff).getList()) {
                    IPolygon hole = (IPolygon) part;
                    extSum.addInterior(hole.getExterior());
                }
            }
        }

        return extSum;
    }

    public IGeometry minkowskiDiffWithCustomPolyCentr(IPolygon polygon,
            IPolygon polyToSum, IDirectPosition centroid) {
        // easy case: no holes in polygon
        if (polygon.getInterior().size() == 0) {
            return minkowskiDiffWithCustomPolyCentrNoHole(polygon, polyToSum,
                    centroid);
        }

        // case with holes in polygon: the final polygon should be the minkowski
        // difference of the exterior and the minkowski sum for the inner rings.
        IPolygon polyNoHole = new GM_Polygon(polygon.getExterior());
        IGeometry extSum = minkowskiDiffWithCustomPolyCentrNoHole(polyNoHole,
                polyToSum, centroid);
        // loop on the inner rings to reduce each one and add it as a hole for
        // extSum
        for (IRing inner : polygon.getInterior()) {
            IGeometry innerDiff = minkowskiDiffWithCustomPolyCentrNoHole(
                    new GM_Polygon(inner), polyToSum, centroid);
            if (innerDiff == null)
                continue;
            extSum.difference(innerDiff);
        }

        return extSum;
    }

    private IPolygon minkowskiSumWithCustomPolyCentrNoHole(IPolygon polygon,
            IPolygon polyToSum, IDirectPosition centroid) {
        // get the exterior LineString of polygon
        ILineString exterior = polygon.exteriorLineString();
        // reverse the line to make its left side the exterior of the polygon
        exterior = (ILineString) exterior.reverse();
        // compute the dilatation of the exterior with polyToSum
        IPolygon union = (IPolygon) lineLeftDilatationFromPolyCentr(exterior,
                polyToSum, centroid);
        // if union contains holes, they have to be removed
        return CommonAlgorithmsFromCartAGen.removeHoles(union);
    }

    private IGeometry minkowskiDiffWithCustomPolyCentrNoHole(IPolygon polygon,
            IPolygon polyToSum, IDirectPosition centroid) {
        // get the exterior LineString of polygon
        ILineString exterior = polygon.exteriorLineString();
        // compute the dilatation of the exterior with polyToSum
        IPolygon union = lineLeftDilatationFromPolyCentr(exterior, polyToSum,
                centroid);

        // the polygon to retrieve is the union of the inner rings of the union
        // geometry

        // special case, if union does not contain holes, polygon was two small
        // to
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
     * dilatation is guided by the parameter polygon. It's like a minkowski sum
     * of the line and the polygon but the line is only dilatated from the
     * distance to polygon centroid. It helps to build what C. Duchêne calls
     * reduced areas (PhD, 2004): if the centroid of the polygon is moved
     * outside the dilatated area, it won't intersect the line. Be careful, the
     * convex hull of the parameter polygon is rather used to simplify the
     * problem.
     * 
     * @param line
     * @return
     */
    private IPolygon lineLeftDilatationFromPolyCentr(ILineString line,
            IPolygon polyToSum, IDirectPosition centroid) {

        JtsAlgorithms algo = new JtsAlgorithms();

        try {
            LineString jtsLine = (LineString) JtsGeOxygene.makeJtsGeom(line);

            if (CGAlgorithms.isCCW(jtsLine.getCoordinates())) {
                line = (ILineString) line.getNegative();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // line = (ILineString) line.getNegative();
        IGeometry toReturn = null;

        // Use convex hull
        IPolygon polyConvex = (IPolygon) polyToSum.convexHull();

        // Test if the coordinates of the points of polyconvex are in direct
        // sense.
        IDirectPositionList coord = polyConvex.coord();
        IDirectPosition point0 = coord.get(0);
        IDirectPosition point1 = coord.get(1);
        IDirectPosition point2 = coord.get(2);

        double vp = (point1.getX() - point0.getX())
                * (point2.getY() - point0.getY())
                - (point2.getX() - point0.getX())
                        * (point1.getY() - point0.getY());

        if (vp < 0.0) {
            coord.inverseOrdre();
        }

        // for each vector, create an area representing the dilatation
        for (int i = 0; i < line.getControlPoint().size() - 1; i++) {

            // Create return area for each point of the line.
            IDirectPosition pointA = line.getControlPoint(i);
            IDirectPosition pointB = line.getControlPoint(i + 1);
            IDirectPosition pointC = line.getControlPoint(
                    i == line.getControlPoint().size() - 2 ? 1 : i + 2);
            // logger.debug("A: " + i + ", B: " + (i + 1) + ", C: "
            // + (i == line.getControlPoint().size() - 2 ? 1 : i + 2));
            //
            // logger.debug("A : " + pointA);
            // logger.debug("B : " + pointB);
            // logger.debug("C : " + pointC);

            double dX = pointB.getX() - pointA.getX();
            double dY = pointB.getY() - pointA.getY();

            double theta = Math.atan2(dY, dX) - Math.PI / 2;

            IDirectPosition positionAB = this.getFurthestPoint(coord, theta);
            // logger.debug("positionAB: " + positionAB);
            // Initialize for next turn.
            dX = pointC.getX() - pointB.getX();
            dY = pointC.getY() - pointB.getY();
            theta = Math.atan2(dY, dX) - Math.PI / 2;

            IDirectPosition positionBC = this.getFurthestPoint(coord, theta);
            // logger.debug("positionBC: " + positionBC);

            // Construct the area
            // Create polygon.
            List<IDirectPosition> tempList = new ArrayList<IDirectPosition>();

            // First segment
            tempList.add(pointA);
            tempList.add(pointB);
            // Analysis of the convexity of the angle ABC
            double vectorialProduct = (pointB.getX() - pointA.getX())
                    * (pointC.getY() - pointA.getY())
                    - (pointC.getX() - pointA.getX())
                            * (pointB.getY() - pointA.getY());

            // logger.debug("Vector product: " + vectorialProduct);

            // Dilatation of the second point if convexity at left.
            // We compute the translation vectors to apply to B to get the
            // dilatation
            // of the angle: these vectors are join centroid to each point of
            // polyConvex between positionAB and positionBC, in direct order.
            if (vectorialProduct < 0.0) {
                boolean afterBC = false;
                int j = 0;
                while (true) {
                    IDirectPosition tempPoint = coord.get(j);
                    j = (j < coord.size() - 2) ? j + 1 : 0;
                    // logger.debug("tempPoint: " + tempPoint);
                    if (afterBC || (tempPoint.equals(positionBC))) {
                        // logger.debug("tempPoint: afterBC");
                        afterBC = true;
                        IDirectPosition toAdd = new DirectPosition(
                                pointB.getX() + centroid.getX()
                                        - tempPoint.getX(),
                                pointB.getY() + centroid.getY()
                                        - tempPoint.getY());
                        tempList.add(toAdd);
                        if (tempPoint.equals(positionAB)) {
                            // logger.debug("tempPoint: break");
                            break;
                        }
                    }
                }
            }

            // Dilatation of the left side.
            // Vector joining positionAB to centroid
            tempList.add(new DirectPosition(
                    pointB.getX() + centroid.getX() - positionAB.getX(),
                    pointB.getY() + centroid.getY() - positionAB.getY()));

            tempList.add(new DirectPosition(
                    pointA.getX() + centroid.getX() - positionAB.getX(),
                    pointA.getY() + centroid.getY() - positionAB.getY()));

            tempList.add(tempList.get(0));

            // close line and return an area.

            ILineString tempLine = new GM_LineString(
                    (List<IDirectPosition>) tempList);
            IPolygon tempPolygon = new GM_Polygon(tempLine);
            // logger.debug("tempPolygon " + tempPolygon);
            if (toReturn == null) {
                toReturn = tempPolygon;
            } else {
                IGeometry temp = algo.union(toReturn, tempPolygon);
                if (temp != null) {
                    toReturn = temp;
                }
            }
            // logger.debug("toReturn " + toReturn);
        }

        if (toReturn.isMultiSurface()) {

            IMultiSurface<?> multiTemp = ((IMultiSurface<?>) toReturn);
            // logger.debug("size " + multiTemp.size());
            //
            // for (IGeometry toShow : multiTemp) {
            // logger.debug(toShow);
            // }

            if (multiTemp.size() >= 1) {
                // TODO use convex hull instead
                toReturn = (IPolygon) multiTemp.get(0);
            } else {

            }
        }

        return (IPolygon) toReturn;
    }

    /**
     * Return the farest point of the line in the direction given by theta
     * angle. If they are an egality, get the first in direct sense.
     * 
     * @param polygon
     * @param theta
     * @return
     */
    private IDirectPosition getFurthestPoint(IDirectPositionList coord,
            double theta) {
        // IPolygon rotation = CommonAlgorithms.rotation(polygon, - theta);
        IDirectPosition toReturn = null;
        double xMax = -Double.MAX_VALUE;
        boolean identFlag = false;
        for (IDirectPosition toCompare : coord) {
            // logger.debug("point: " + toCompare);

            double xRotation = toCompare.getX() * Math.cos(-theta)
                    - toCompare.getY() * Math.sin(-theta);

            // logger.debug("xRotation " + xRotation);
            // logger.debug("identFlag " + identFlag);

            if ((xMax < xRotation)) {
                toReturn = toCompare;
                xMax = xRotation;
                identFlag = true;
            } else if (xMax == xRotation) {
                if (!identFlag) {
                    toReturn = toCompare;
                    xMax = xRotation;
                    identFlag = true;
                }
            } else {
                identFlag = false;
            }
        }
        return toReturn;
    }

    public int getCapForm() {
        return capForm;
    }

    public void setCapForm(int capForm) {
        this.capForm = capForm;
    }
}
