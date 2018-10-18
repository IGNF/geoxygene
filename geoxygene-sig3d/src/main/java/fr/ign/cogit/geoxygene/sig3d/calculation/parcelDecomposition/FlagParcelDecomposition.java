package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import fr.ign.cogit.geoxygene.convert.FromGeomToLineString;
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
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Re-implementation of block decomposition into parcels with flag shape. The
 * algorithm is an adaptation from :
 * 
 * Vanegas, C. A., Kelly, T., Weber, B., Halatsch, J., Aliaga, D. G., Müller,
 * P., May 2012. Procedural generation of parcels in urban modeling. Comp.
 * Graph. Forum 31 (2pt3).
 * 
 * It is a recursive method, the decomposition is stop when a stop criteria is
 * reached either the area or roadwidthaccess is below a given threshold
 * 
 * @author Mickael Brasebin
 *
 */
public class FlagParcelDecomposition {

	public static void main(String[] args) throws Exception {
		DirectPosition.PRECISION = 4;
		
		
		String inputShapeFile = "/home/mbrasebin/Bureau/FolderTest/shapes.shp";
		String inputBuildingFile = "/home/mbrasebin/Bureau/FolderTest/batimentSys.shp";
		String shapeFileOut = "/home/mbrasebin/Bureau/FolderTest/out.shp";

		IFeatureCollection<IFeature> featColl = ShapefileReader.read(inputShapeFile);
		IFeatureCollection<IFeature> featCollBuildings = ShapefileReader.read(inputBuildingFile);

		double maximalArea = 500;
		double maximalWidth = 5;
		double noise = 0;
		double roadWidth = 3;

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		for (IFeature feat : featColl) {

			List<IOrientableSurface> surfaces = FromGeomToSurface.convertGeom(feat.getGeom());

			if (surfaces.size() != 1) {
				System.out.println("Not simple geometry : " + feat.toString());
				continue;
			}

			FlagParcelDecomposition ffd = new FlagParcelDecomposition((IPolygon) surfaces.get(0), featCollBuildings,
					maximalArea, maximalWidth, roadWidth);
			IFeatureCollection<IFeature> results = ffd.decompParcel(noise);

			featCollOut.addAll(results);

		}

		ShapefileWriter.write(featCollOut, shapeFileOut);

	}

	private double maximalArea, maximalWidth, roadWidth;
	IPolygon polygonInit;
	IFeatureCollection<IFeature> buildings;

	public FlagParcelDecomposition(IPolygon p, IFeatureCollection<IFeature> buildings, double maximalArea,
			double maximalWidth, double roadWidth) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.polygonInit = p;
		this.buildings = buildings;
		this.roadWidth = roadWidth;
		
		if (!buildings.hasSpatialIndex()) {
			buildings.initSpatialIndex(Tiling.class, true);
		}
	}

	/**
	 * The decomposition method
	 * 
	 * @return List of parcels
	 * @throws Exception
	 */
	public IFeatureCollection<IFeature> decompParcel(double noise) throws Exception {
		return decompParcel(this.polygonInit, noise);
	}

	/**
	 * The core algorithm
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	private IFeatureCollection<IFeature> decompParcel(IPolygon p, double noise) throws Exception {

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
		List<IPolygon> splittingPolygon = computeSplittingPolygon(p, true, noise);

		// Split into polygon
		List<IPolygon> splittedPolygon = split(p, splittingPolygon);

		// If a parcel has no road access, there is a probability to make a
		// perpendicular split
		if (!hasRoadAccess(splittedPolygon.get(0)) || !hasRoadAccess(splittedPolygon.get(1))) {
			// Probability to make a perpendicular split
			// Same steps but with different splitting geometries
			splittingPolygon = computeSplittingPolygon(p, false, noise);

			List<IPolygon> splittedPolygon2 = split(p, splittingPolygon);

			// If there is no road access to one of the parcel
			if (!hasRoadAccess(splittedPolygon2.get(0)) || !hasRoadAccess(splittedPolygon2.get(1))) {
				// We generate flags parcel to provide an access
				splittedPolygon = generateFlagParcel(splittedPolygon);

			} else {
				// If not we keep the new cut
				splittedPolygon = splittedPolygon2;
			}

		}

		// All splitted polygones are splitted and results added to the output
		for (IPolygon pol : splittedPolygon) {
			featCollOut.addAll(decompParcel(pol, noise));
		}

		return featCollOut;

	}

	private List<IMultiCurve<IOrientableCurve>> regroupLineStrings(List<IOrientableCurve> lineStrings) {

		List<IMultiCurve<IOrientableCurve>> curvesOutput = new ArrayList<>();

		while (!lineStrings.isEmpty()) {

			ILineString currentLineString = (ILineString) lineStrings.remove(0);
			IMultiCurve<IOrientableCurve> currentMultiCurve = new GM_MultiCurve<>();
			currentMultiCurve.add(currentLineString);

			IGeometry buffer = currentMultiCurve.buffer(0.1);

			for (int i = 0; i < lineStrings.size(); i++) {

				if (buffer.intersects(lineStrings.get(i))) {
					// Adding line in MultiCurve
					currentMultiCurve.add(lineStrings.remove(i));
					i = -1;
					// Updating the buffer
					buffer = currentMultiCurve.buffer(0.1);
				}

			}
			
			curvesOutput.add(currentMultiCurve);

		}

		return curvesOutput;
	}

	private List<IPolygon> generateFlagParcel(List<IPolygon> splittedPolygon) {

		IPolygon polyWithRoadAccess = hasRoadAccess(splittedPolygon.get(0)) ? splittedPolygon.get(0)
				: splittedPolygon.get(1);
		IPolygon polyWithNORoadAccess = (!hasRoadAccess(splittedPolygon.get(0))) ? splittedPolygon.get(0)
				: splittedPolygon.get(1);

		IGeometry buffer = polyWithNORoadAccess.buffer(0.1);

		List<IOrientableCurve> lExterior = FromGeomToLineString.convertInSegment(polyWithRoadAccess);

		List<IOrientableCurve> lExteriorToKeep = lExterior.stream().filter(x -> (!buffer.contains(x)))
				.filter(x -> !polygonInit.getExterior().buffer(0.1).contains(x)).collect(Collectors.toList());
		
		List<IMultiCurve<IOrientableCurve>> sides = this.regroupLineStrings(lExteriorToKeep);
		
		sides.sort(new Comparator<IMultiCurve<IOrientableCurve>>() {

			@Override
			public int compare(IMultiCurve<IOrientableCurve> o1, IMultiCurve<IOrientableCurve> o2) {
				return Double.compare(o1.length(), o2.length());
			}
		});
		
		if(sides.size() != 2) {
			System.out.println("Side != 2 for pol : " + this.polygonInit);
		}
		
		
		List<IPolygon> polygonesOut = new ArrayList<>();
		
		IGeometry road = sides.get(0).buffer(this.roadWidth);
		
		
		IGeometry geomPol1 =   polyWithRoadAccess.difference(road);
		IGeometry geomPol2 = polyWithNORoadAccess.union(road.intersection(polyWithRoadAccess));
		
	
		List<IPolygon> lPolygonsOut1 = FromGeomToSurface.convertGeom(geomPol1).stream().map(x -> (IPolygon) x ).collect(Collectors.toList());
		lPolygonsOut1 = lPolygonsOut1.stream().filter(x -> x.area() > 25).collect(Collectors.toList());
		
		List<IPolygon> lPolygonsOut2 = FromGeomToSurface.convertGeom(geomPol2).stream().map(x -> (IPolygon) x ).collect(Collectors.toList());
		lPolygonsOut2 = lPolygonsOut2.stream().filter(x -> x.area() > 25).collect(Collectors.toList());
		
		
		
		polygonesOut.addAll(lPolygonsOut1);
		polygonesOut.addAll(lPolygonsOut2);
		
		
 
		return polygonesOut;
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

		ILineString ext = new GM_LineString(this.polygonInit.getExterior().coord());

		return (p.buffer(0.1)).intersection(ext).length();
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
	public static List<IPolygon> computeSplittingPolygon(IGeometry pol, boolean shortDirectionSplit, double noise)
			throws Exception {

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

		// Determine the points that intersect the line and the OBB according to chosen
		// direction
		// This points will be used for splitting
		IDirectPositionList intersectedPoints = determineIntersectedPoints(
				new LineEquation(translateCentroid, splitDirection),
				(shortDirectionSplit) ? oBB.getLongestEdges() : oBB.getShortestEdges());

		// Construction of the two splitting polygons by using the OBB edges and the
		// intersection points
		IPolygon pol1 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(0) : oBB.getLongestEdges().get(0));
		IPolygon pol2 = determinePolygon(intersectedPoints,
				(shortDirectionSplit) ? oBB.getShortestEdges().get(1) : oBB.getLongestEdges().get(1));

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
	private static IPolygon determinePolygon(IDirectPositionList intersectedPoints, ILineString edge) {

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

		ILineString ext = new GM_LineString(this.polygonInit.getExterior().coord());

		return poly.intersects(ext.buffer(0.5));
	}

}
