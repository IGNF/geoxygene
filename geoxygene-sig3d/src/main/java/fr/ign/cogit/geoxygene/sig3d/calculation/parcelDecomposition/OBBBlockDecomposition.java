package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.OrientedBoundingBox;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Re-implementation of block decomposition into parcels from :
 * 
 * Vanegas, C. A., Kelly, T., Weber, B., Halatsch, J., Aliaga, D. G., Müller,
 * P., May 2012. Procedural generation of parcels in urban modeling. Comp.
 * Graph. Forum 31 (2pt3).
 * 
 * It is a recursive method, the decomposition is stop when a stop criteria is
 * reached either the area or roadwidthaccess is below a given threshol
 * 
 * @author Mickael Brasebin
 *
 */
public class OBBBlockDecomposition {

	private double maximalArea, maximalWidth;
	private RandomGenerator rng;
	private double epsilon;
	private double noise;
	IPolygon p;

	/**
	 * 
	 * @param p
	 *            : the polygon block that is decomposed
	 * @param maximalArea
	 *            : maximal area of splitted parcel
	 * @param maximalWidth
	 *            : maximal road access of splitter parcel
	 * @param rng
	 *            : the random generator
	 * @param epsilon
	 *            : the likeness to garuantee road access to parcels
	 * @param noise
	 *            : the variety of parcel decomposition
	 */
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, RandomGenerator rng, double epsilon,
			double noise) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.rng = rng;
		this.epsilon = epsilon;
		this.p = p;
		this.noise = noise;
	}

	/**
	 * The decomposition method
	 * 
	 * @return List of parcels
	 * @throws Exception
	 */
	public IFeatureCollection<IFeature> decompParcel() throws Exception {
		return decompParcel(this.p);
	}

	/**
	 * The core algorithm
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	private IFeatureCollection<IFeature> decompParcel(IPolygon p) throws Exception {

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		double area = p.area();
		double frontSideWidth = this.frontSideWidth(p);

		// End test condition
		if (this.endCondition(area, frontSideWidth)) {
			featCollOut.add(new DefaultFeature(p));
			return featCollOut;

		}

		// Determination of splitting polygon (it is a splitting line in the
		// article)
		List<IPolygon> splittingPolygon = computeSplittingPolygon(p, true);

		// Split into polygon
		List<IPolygon> splittedPolygon = split(p, splittingPolygon);

		// If a parcel has no road access, there is a probability to make a
		// perpendicular split
		if (!hasRoadAccess(splittedPolygon.get(0)) || !hasRoadAccess(splittedPolygon.get(1))) {
			// Probability to make a perpendicular split
			if (rng.nextDouble() < epsilon) {
				// Same steps but with different splitting geometries
				splittingPolygon = computeSplittingPolygon(p, false);

				splittedPolygon = split(p, splittingPolygon);
			}

		}

		// All splitted polygones are splitted and results added to the output
		for (IPolygon pol : splittedPolygon) {
			featCollOut.addAll(decompParcel(pol));
		}

		return featCollOut;

	}

	/**
	 * End condition : either the area is below a threshold or width to road
	 * 
	 * @param area
	 * @param frontSideWidth
	 * @return
	 */
	private boolean endCondition(double area, double frontSideWidth) {
		boolean testArea = (area <= this.maximalArea);
		boolean testWidth = (frontSideWidth <= this.maximalWidth);
		return testArea || testWidth;

	}

	/**
	 * Determine the width of the parcel on road
	 * 
	 * @param p
	 * @return
	 */
	private double frontSideWidth(IPolygon p) {

		ILineString ext = new GM_LineString(this.p.getExterior().coord());

		return (p.buffer(0.1)).intersection(ext).length();
	}

	/**
	 * Computed the splitting polygons composed by two boxes determined from the
	 * oriented bounding boxes splited from a line at its middle
	 * 
	 * @param pol
	 *            : the input polygon
	 * @param shortDirectionSplit
	 *            : it is splitted by the short edges or by the long edge.
	 * @return
	 * @throws Exception
	 */
	private List<IPolygon> computeSplittingPolygon(IPolygon pol, boolean shortDirectionSplit) throws Exception {

		// Determination of the bounding box
		OrientedBoundingBox oBB = new OrientedBoundingBox(pol);

		// Detmermination of the split vector
		Vecteur splitDirection = (shortDirectionSplit) ? oBB.shortestDirection() : oBB.longestDirection();

		IDirectPosition centroid = oBB.getCentre();

		// The noise value is determined by noise parameters and parcel width
		// (to avoid lines that go out of parcel)
		double noiseTemp = Math.min(oBB.getWidth() / 3, noise);

		//X and Y move of the centroid
		double alphaX = (0.5 - Math.random()) * noiseTemp;
		double alphaY = (0.5 - Math.random()) * noiseTemp;
		IDirectPosition translateCentroid = new DirectPosition(centroid.getX() + alphaX, centroid.getY() + alphaY);

		//Determine the points that intersect the line and the OBB according to chosen direction
		//This points will be used for splitting
		IDirectPositionList intersectedPoints = determineIntersectedPoints(
				new LineEquation(translateCentroid, splitDirection),
				(shortDirectionSplit) ? oBB.getLongestEdges() : oBB.getShortestEdges());

		//Construction of the two splitting polygons by using the OBB edges and the intersection points
		IPolygon pol1 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(0) : oBB.getLongestEdges().get(0));
		IPolygon pol2 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(1) : oBB.getLongestEdges().get(1));

		//Generated polygons are added and returned
		List<IPolygon> outList = new ArrayList<>();
		outList.add(pol1);
		outList.add(pol2);

		return outList;
	}

	
	/**
	 * Build the output polygon from OBB edges and splitting points
	 * @param intersectedPoints
	 * @param edge
	 * @return
	 */
	private IPolygon determinePolygon(IDirectPositionList intersectedPoints, ILineString edge) {

		IDirectPosition dp1 = intersectedPoints.get(0);
		IDirectPosition dp2 = intersectedPoints.get(1);

		Vecteur v = new Vecteur(dp1, dp2);

		Vecteur v1 = new Vecteur(edge.coord().get(0), edge.coord().get(1));

		IDirectPositionList dpl1 = new DirectPositionList();
		if (v.prodScalaire(v1) > 0) {

			dpl1.add(dp2);
			dpl1.add(dp1);
			dpl1.addAll(edge.coord());

			dpl1.add(dp2);

		} else {

			dpl1.add(dp1);
			dpl1.add(dp2);
			dpl1.addAll(edge.coord());

			dpl1.add(dp1);

		}

		return new GM_Polygon(new GM_LineString(dpl1));

	}

	/**
	 * Determine the splitting points from line equation and OBB edges
	 * @param eq
	 * @param ls
	 * @return
	 */
	private IDirectPositionList determineIntersectedPoints(LineEquation eq, List<ILineString> ls) {

		IDirectPosition dp1 = eq
				.intersectionLineLine(new LineEquation(ls.get(0).coord().get(0), ls.get(0).coord().get(1)));
		IDirectPosition dp2 = eq
				.intersectionLineLine(new LineEquation(ls.get(1).coord().get(0), ls.get(1).coord().get(1)));

		IDirectPositionList dpl = new DirectPositionList();
		dpl.add(dp1);
		dpl.add(dp2);

		return dpl;

	}

	/**
	 * Split the input polygons by a list of polygons
	 * @param poly
	 * @param polygones
	 * @return
	 */
	private List<IPolygon> split(IPolygon poly, List<IPolygon> polygones) {

		IGeometry geom = polygones.get(0).intersection(poly);
		IGeometry geom2 = polygones.get(1).intersection(poly);

		List<IOrientableSurface> iostemp = FromGeomToSurface.convertGeom(geom);
		List<IOrientableSurface> iostemp2 = FromGeomToSurface.convertGeom(geom2);

		List<IPolygon> listPoly = new ArrayList<>();

		for (IOrientableSurface ios : iostemp) {
			listPoly.add((IPolygon) ios);
		}

		for (IOrientableSurface ios : iostemp2) {
			listPoly.add((IPolygon) ios);
		}

		return listPoly;
	}

	/**
	 * Indicate if 
	 * @param poly
	 * @return
	 */
	private boolean hasRoadAccess(IPolygon poly) {

		ILineString ext = new GM_LineString(this.p.getExterior().coord());

		return poly.intersects(ext.buffer(0.5));
	}

}
