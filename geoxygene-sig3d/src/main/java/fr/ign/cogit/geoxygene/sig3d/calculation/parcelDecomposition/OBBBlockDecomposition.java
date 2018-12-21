package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
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
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Re-implementation of block decomposition into parcels from :
 * 
 * Vanegas, C. A., Kelly, T., Weber, B., Halatsch, J., Aliaga, D. G., Müller,
 * P., May 2012. Procedural generation of parcels in urban modeling. Comp.
 * Graph. Forum 31 (2pt3).
 * 
 * It is a recursive method, the decomposition is stop when a stop criteria is
 * reached either the area or road width access is below a given threshold
 * 
 * @author Mickael Brasebin
 *
 */
public class OBBBlockDecomposition {

	public static void main(String[] args) throws Exception {

		// Precision si set
		DirectPosition.PRECISION = 4;

		// Input 1/ the input shapes to split
		String inputShapeFile = "/home/mbrasebin/Bureau/misc/tous_ilots.shp";

		// The output file that will contain all the decompositions
		String shapeFileOut = "/home/mbrasebin/Bureau/misc/outNoFlagAll.shp";

		// Reading collection
		IFeatureCollection<IFeature> featColl = ShapefileReader.read(inputShapeFile);

		// Maxmimal area for a parcel
		double maximalArea = 2500;
		// MAximal with to the road
		double maximalWidth = 20;
		// Do we want noisy results
		double noise = 0;
		// Probability to get a cut perpendicular to the OBB
		double epsilon = 0;
		// Exterior from the UrbanBlock if necessary or null
		IMultiCurve<IOrientableCurve> imC = null;
		// Roads are created for this number of decomposition level
		int decompositionLevelWithRoad = 2;
		// Road width
		double roadWidth = 5.0;
		// Boolean forceRoadaccess
		boolean forceRoadAccess = false;

		IFeatureCollection<IFeature> featCollOut = featColl.parallelStream()
				.map(x -> processAPolygon(x, maximalArea, maximalWidth, epsilon, noise, imC, decompositionLevelWithRoad,
						roadWidth, forceRoadAccess))
				.collect(FT_FeatureCollection::new, FT_FeatureCollection::addAll, FT_FeatureCollection::addAll);

		ShapefileWriter.write(featCollOut, shapeFileOut);

	}

	private static List<IFeature> processAPolygon(IFeature feat, double maximalArea, double maximalWidth,
			double epsilon, double noise, IMultiCurve<IOrientableCurve> imC, int decompositionLevelWithRoad,
			double roadWidth, boolean forceRoadAccess) {

		List<IOrientableSurface> surfaces = FromGeomToSurface.convertGeom(feat.getGeom());

		if (surfaces.size() != 1) {
			System.out.println("Not simple geometry : " + feat.toString());
			return new ArrayList<>();
		}

		// We run the algorithm of decomposition
		OBBBlockDecomposition ffd = new OBBBlockDecomposition((IPolygon) surfaces.get(0), maximalArea, maximalWidth,
				epsilon, null, decompositionLevelWithRoad, 5.0, forceRoadAccess);
		IFeatureCollection<IFeature> results;
		try {
			results = ffd.decompParcel(noise);
			return results.getElements();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private double maximalArea, maximalWidth;
	private double epsilon;
	IPolygon polygonInit;
	private int decompositionLevelWithRoad;
	double roadWidth;

	/**
	 * 
	 * @param p            : the polygon block that is decomposed
	 * @param maximalArea  : maximal area of splitted parcel
	 * @param maximalWidth : maximal road access of splitter parcel
	 * @param epsilon      : the likeness to garuantee road access to parcels
	 */
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, double epsilon,
			boolean forceRoadAccess) {
		this(p, maximalArea, maximalWidth, epsilon, null, 0, 0, forceRoadAccess);
	}

	/**
	 * The polygon p is the polygon of a parcel to subdivide and the exteriori of
	 * the urban block is extBlock
	 * 
	 * @param p            : the polygon block that is decomposed
	 * @param maximalArea  : maximal area of splitted parcel
	 * @param maximalWidth : maximal road access of splitter parcel
	 * @param epsilon      : the likeness to garuantee road access to parcels ;
	 *                     extBlock exterior of the urban block
	 */
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, double epsilon,
			IMultiCurve<IOrientableCurve> extBlock, boolean forceRoadAccess) {
		this(p, maximalArea, maximalWidth, epsilon, extBlock, 0, 0, forceRoadAccess);
	}

	/**
	 * The polygon p is the polygon of a parcel to subdivide and the exteriori of
	 * the urban block is extBlock
	 * 
	 * @param p                          : the polygon block that is decomposed
	 * @param maximalArea                : maximal area of splitted parcel
	 * @param maximalWidth               : maximal road access of splitter parcel
	 * @param epsilon                    : the likeness to garuantee road access to
	 *                                   parcels ; extBlock exterior of the urban
	 *                                   block
	 * @param decompositionLevelWithRoad : roads are created until this rank
	 * @param roadWidth                  : the road width when created
	 */
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, double epsilon,
			IMultiCurve<IOrientableCurve> extBlock, int decompositionLevelWithRoad, double roadWidth,
			boolean forceRoadAccess) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.epsilon = epsilon;
		this.polygonInit = p;
		this.ext = extBlock;
		this.decompositionLevelWithRoad = decompositionLevelWithRoad;
		this.roadWidth = Math.max(0, roadWidth); // RoadWitdh must be positive
		this.forceRoadAccess = forceRoadAccess;
	}

	private boolean forceRoadAccess;

	/**
	 * The decomposition method
	 * 
	 * @return List of parcels
	 * @throws Exception
	 */
	public IFeatureCollection<IFeature> decompParcel(double noise) throws Exception {
		return decompParcel(this.polygonInit, noise, 0, forceRoadAccess);
	}

	/**
	 * The core algorithm
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	private IFeatureCollection<IFeature> decompParcel(IPolygon p, double noise, int decompositionLevel,
			boolean forceRoadAccess) throws Exception {

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		if (!p.isValid()) {
			p = (IPolygon) p.buffer(0);

			if (!p.isValid()) {
				p = (IPolygon) p.buffer(0.001);
			} else {
				if (!p.isValid()) {
					System.out.println("Invalid polygon : " + p);
					System.out.println("Try maybe with less precision : DirectPosition.Precision = 4");
				}
			}

		}

		double area = p.area();
		double frontSideWidth = this.frontSideWidth(p);

		// End test condition
		if (this.endCondition(area, frontSideWidth)) {
			featCollOut.add(new DefaultFeature(p));
			return featCollOut;

		}

		// Determination of splitting polygon (it is a splitting line in the
		// article)
		List<IPolygon> splittingPolygon = computeSplittingPolygon(p, true, noise, decompositionLevel,
				decompositionLevelWithRoad, this.roadWidth);

		// Split into polygon
		List<IPolygon> splittedPolygon = split(p, splittingPolygon);

		// If a parcel has no road access, there is a probability to make a
		// perpendicular split

		// Probability to make a perpendicular split if no road access or a little
		// probabibility epsilon

		if ((forceRoadAccess && ((!hasRoadAccess(splittedPolygon.get(0)) || !hasRoadAccess(splittedPolygon.get(1)))))
				|| (Math.random() < epsilon)) {

			// Same steps but with different splitting geometries
			splittingPolygon = computeSplittingPolygon(p, false, noise, decompositionLevel, decompositionLevelWithRoad,
					this.roadWidth);

			splittedPolygon = split(p, splittingPolygon);

		}

		// All splitted polygones are splitted and results added to the output

		for (IPolygon pol : splittedPolygon) {
			featCollOut.addAll(decompParcel(pol, noise, decompositionLevel + 1, forceRoadAccess));
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
		return testArea && testWidth;

	}

	/**
	 * Determine the width of the parcel on road
	 * 
	 * @param p
	 * @return
	 */
	private double frontSideWidth(IPolygon p) {

		ILineString ext = new GM_LineString(this.getExt().coord());

		double value = 0.0;

		if (ext == null || ext.isEmpty()) {
			return value;
		}

		try {
			value = (p.buffer(0.2)).intersection(ext).length();
		} catch (Exception e) {
			try {
				value = (p.buffer(0.4)).intersection(ext).length();
			} catch (Exception e2) {
				return 0;
			}
		}

		return value;

	}

	/**
	 * Computed the splitting polygons composed by two boxes determined from the
	 * oriented bounding boxes splited from a line at its middle
	 * 
	 * @param pol                 : the input polygon
	 * @param shortDirectionSplit : it is splitted by the short edges or by the long
	 *                            edge.
	 * @return
	 * @throws Exception
	 */
	public static List<IPolygon> computeSplittingPolygon(IGeometry pol, boolean shortDirectionSplit, double noise,
			int decompositionLevel, int decompositionLevelWithRoad, double roadWidth) throws Exception {

		// Determination of the bounding box
		OrientedBoundingBox oBB = new OrientedBoundingBox(pol);

		// Detmermination of the split vector
		Vecteur splitDirection = (shortDirectionSplit) ? oBB.shortestDirection() : oBB.longestDirection();

		IDirectPosition centroid = oBB.getCentre();

		// The noise value is determined by noise parameters and parcel width
		// (to avoid lines that go out of parcel)
		double noiseTemp = Math.min(oBB.getWidth() / 3, noise);

		// X and Y move of the centroid
		double alphaX = (0.5 - Math.random()) * noiseTemp;
		double alphaY = (0.5 - Math.random()) * noiseTemp;
		IDirectPosition translateCentroid = new DirectPosition(centroid.getX() + alphaX, centroid.getY() + alphaY);

		// Determine the points that intersect the line and the OBB according to
		// chosen direction
		// This points will be used for splitting
		IDirectPositionList intersectedPoints = determineIntersectedPoints(
				new LineEquation(translateCentroid, splitDirection),
				(shortDirectionSplit) ? oBB.getLongestEdges() : oBB.getShortestEdges());

		// Construction of the two splitting polygons by using the OBB edges and the
		// intersection points
		IPolygon pol1 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(0) : oBB.getLongestEdges().get(0),
				decompositionLevel, decompositionLevelWithRoad, roadWidth);
		IPolygon pol2 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(1) : oBB.getLongestEdges().get(1),
				decompositionLevel, decompositionLevelWithRoad, roadWidth);

		// Generated polygons are added and returned
		List<IPolygon> outList = new ArrayList<>();
		outList.add(pol1);
		outList.add(pol2);

		return outList;
	}

	/**
	 * Build the output polygon from OBB edges and splitting points
	 * 
	 * @param intersectedPoints
	 * @param edge
	 * @return
	 */
	private static IPolygon determinePolygon(IDirectPositionList intersectedPoints, ILineString edge,
			int decompositionLevel, int decompositionLevelWithRoad, double roadWidth) {

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

		IPolygon pol = new GM_Polygon(new GM_LineString(dpl1));

		if (decompositionLevel < decompositionLevelWithRoad) {

			IDirectPositionList dpl = new DirectPositionList(dp1, dp2);

			ILineString directionOfCut = (new GM_LineString(dpl));

			IGeometry geom = pol.difference(directionOfCut.buffer(roadWidth));

			// To check the geometries
			// Decomment the follong lines

			// System.out.println(geom);
			// System.out.println(roadWidth);
			// System.out.println(directionOfCut);

			// We keep it if it is only a polygon
			// If it is not a polygon it means that the OBB is too small to support this
			// operation
			// So we do not create the road
			if (geom instanceof IPolygon) {
				pol = (IPolygon) geom;
			}
		}

		return pol;

	}

	/**
	 * Determine the splitting points from line equation and OBB edges
	 * 
	 * @param eq
	 * @param ls
	 * @return
	 */
	private static IDirectPositionList determineIntersectedPoints(LineEquation eq, List<ILineString> ls) {

		IDirectPosition dp1 = eq
				.intersectionLineLine(new LineEquation(ls.get(0).coord().get(0), ls.get(0).coord().get(1)));
		IDirectPosition dp2 = eq
				.intersectionLineLine(new LineEquation(ls.get(1).coord().get(0), ls.get(1).coord().get(1)));

		if (dp1 == null) {
			System.out.println("Null");
			dp1 = eq.intersectionLineLine(new LineEquation(ls.get(0).coord().get(0), ls.get(0).coord().get(1)));

		}
		IDirectPositionList dpl = new DirectPositionList();
		dpl.add(dp1);
		dpl.add(dp2);

		return dpl;

	}

	/**
	 * Split the input polygons by a list of polygons
	 * 
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
	 * 
	 * @param poly
	 * @return
	 */
	private boolean hasRoadAccess(IPolygon poly) {

		ILineString ext = new GM_LineString(this.getExt().coord());

		return poly.intersects(ext.buffer(0.5));
	}

	// This line represents the exterior of an urban island (it serves to determine
	// if a parcel has road access)
	private IMultiCurve<IOrientableCurve> ext = null;

	public IMultiCurve<IOrientableCurve> getExt() {
		if (ext == null) {
			generateExt();
		}
		return ext;
	}

	public void setExt(IMultiCurve<IOrientableCurve> ext) {
		this.ext = ext;
	}

	private void generateExt() {
		// We determine it
		ext = new GM_MultiCurve<>();
		ILineString ls = new GM_LineString(this.polygonInit.getExterior().coord());
		ext.add(ls);
	}

}
