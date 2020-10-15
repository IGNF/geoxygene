package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.union.CascadedPolygonUnion;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

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
import fr.ign.cogit.geoxygene.util.FeaturePolygonizer;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Re-implementation of block decomposition into parcels with flag shape. The algorithm is an adaptation from :
 * 
 * Vanegas, C. A., Kelly, T., Weber, B., Halatsch, J., Aliaga, D. G., Müller, P., May 2012. Procedural generation of parcels in urban modeling. Comp. Graph. Forum 31 (2pt3).
 * 
 * As input a polygon that represents the zone to decompose. For each step the decomposition is processed according to the OBBBlockDecomposition algorithm If one of the parcels do
 * not have access to the road, a L parcel is created. A road is added on the other parcel according to 1/ the shortest path to the public road 2/ if this shortest path does not
 * intersect an existing building. The width of the road is parametrable in the attributes : roadWidth
 * 
 * It is a recursive method, the decomposition is stop when a stop criteria is reached either the area or roadwidthaccess is below a given threshold
 * 
 * @author Mickael Brasebin
 *
 */
public class FlagParcelDecomposition {

	// We remove some parts that may have a too small area < 25
	public static double TOO_SMALL_PARCEL_AREA = 25;

	public static void main(String[] args) throws Exception {
		// Precision si set
		DirectPosition.PRECISION = 3;

		// Input 1/ the input parcelles to split
		String inputShapeFile = "/home/julien/devel/ParcelManager/src/main/resources/testData/parcelle.shp";
		// Input 2 : the buildings that mustnt intersects the allowed roads (facultatif)
		String inputBuildingFile = "/home/julien/devel/ParcelManager/src/main/resources/testData/building.shp";

		// Input 3 (facultative) : the exterior of the urban block (it serves to determiner the multicurve)
		String inputUrbanBlock = "/home/julien/devel/ParcelManager/src/main/resources/testData/ilot.shp";
		IFeatureCollection<IFeature> featC = ShapefileReader.read(inputUrbanBlock);
		
		String folderOut = "data/";

		// The output file that will contain all the decompositions
		String shapeFileOut = folderOut + "outflag.shp";

		(new File(folderOut)).mkdirs();

		// Reading collection
		IFeatureCollection<IFeature> featColl = ShapefileReader.read(inputShapeFile);
		IFeatureCollection<IFeature> featCollBuildings = ShapefileReader.read(inputBuildingFile);		
		
		List<IOrientableCurve> lOC = featC.select(featColl.envelope()).parallelStream().map(x ->  FromGeomToLineString.convert(x.getGeom())).collect(ArrayList::new, List::addAll, List::addAll);

		IMultiCurve<IOrientableCurve> iMultiCurve = new GM_MultiCurve<>(lOC);

		// Maxmimal area for a parcel
		double maximalArea = 800;
		// MAximal with to the road
		double maximalWidth = 15;
		// Do we want noisy results
		double noise = 0;
		// The with of the road that is created
		double roadWidth = 3;

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		int count = featColl.size();

		// For each shape
		for (int i = 0; i < count; i++) { // count
			IFeature feat = featColl.get(i);
			System.out.println(i + " / " + featColl.size());
      if (feat.getAttribute("NUMERO").toString().equalsIgnoreCase("0024")
          && feat.getAttribute("FEUILLE").toString().equalsIgnoreCase("2")
          && feat.getAttribute("SECTION").toString().equalsIgnoreCase("0A")) {

			IGeometry geom = feat.getGeom();
			IDirectPosition dp = new DirectPosition(0, 0, 0); // geom.centroid();
			geom = geom.translate(-dp.getX(), -dp.getY(), 0);

			List<IOrientableSurface> surfaces = FromGeomToSurface.convertGeom(geom);

			if (surfaces.size() != 1) {
				System.out.println("Not simple geometry : " + feat.toString());
				continue;
			}

			// We run the algorithm of decomposition
			FlagParcelDecomposition ffd = new FlagParcelDecomposition((IPolygon) surfaces.get(0), featCollBuildings, maximalArea, maximalWidth, roadWidth, iMultiCurve);
			IFeatureCollection<IFeature> results = ffd.decompParcel(noise);

			final int intCurrentCount = i;
			results.stream().forEach(x -> AttributeManager.addAttribute(x, "ID", intCurrentCount, "Integer"));
			results.stream().forEach(x -> x.setGeom(x.getGeom().translate(dp.getX(), dp.getY(), 0)));
			// Get the results
			featCollOut.addAll(results);
      }
		}

		ShapefileWriter.write(featCollOut, shapeFileOut, CRS.decode("EPSG:2154"));

	}

	private double maximalArea, maximalWidth, roadWidth;
	IPolygon polygonInit;
	IFeatureCollection<IFeature> buildings;

	/**
	 * Flag decomposition algorithm
	 * 
	 * @param p
	 *            the initial polygon to decompose
	 * @param buildings
	 *            the buildings that will constraint the possibility of adding a road
	 * @param maximalArea
	 *            the maximalArea for a parcel
	 * @param maximalWidth
	 *            the maximal width
	 * @param roadWidth
	 *            the road width
	 */
	public FlagParcelDecomposition(IPolygon p, IFeatureCollection<IFeature> buildings, double maximalArea, double maximalWidth, double roadWidth) {
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
	 * Flag decomposition algorithm
	 * 
	 * @param p
	 *            the initial polygon to decompose
	 * @param buildings
	 *            the buildings that will constraint the possibility of adding a road
	 * @param maximalArea
	 *            the maximalArea for a parcel
	 * @param maximalWidth
	 *            the maximal width
	 * @param roadWidth
	 *            the road width
	 * @param isLandExterior
	 *            the exterior of this island to assess road access
	 */
	public FlagParcelDecomposition(IPolygon p, IFeatureCollection<IFeature> buildings, double maximalArea, double maximalWidth, double roadWidth,
			IMultiCurve<IOrientableCurve> islandExterior) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.polygonInit = p;
		this.buildings = buildings;
		this.roadWidth = roadWidth;
		this.setExt(islandExterior);

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

		// Determination of splitting polygon (it is a splitting line in the article)
		List<IPolygon> splittingPolygon = computeSplittingPolygon(p, true, noise);

		// Split into polygon
		List<IPolygon> splittedPolygon = split(p, splittingPolygon);

		long nbNoRoadAccess = splittedPolygon.stream().filter(x -> !hasRoadAccess(x)).count();

		// If a parcel has no road access, there is a probability to make a
		// perpendicular split
		if (nbNoRoadAccess != 0) {

			List<List<IPolygon>> polGeneratedParcel = generateFlagParcel(splittedPolygon);

			splittedPolygon = polGeneratedParcel.get(0);

			for (IPolygon currentPoly : polGeneratedParcel.get(1)) {

				featCollOut.add(new DefaultFeature(currentPoly));

			}
		}

		// All splitted polygones are splitted and results added to the output
		for (IPolygon pol : splittedPolygon) {

			// System.out.println("---" + pol.area());
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

	private IGeometry makePolygonValid(IGeometry pol) {

		if (pol != null && !pol.isValid()) {
			pol = pol.buffer(0);
			if (!pol.isValid()) {
				pol = pol.buffer(0.5);
				if (!pol.isValid()) {
					System.out.println("Still not valid");
				}
			}
		}

		return pol;
	}

	/**
	 * The output is a list of two elements : 1/ the first one contains parcel with road access initially 2/ the second contains parcel with added road access
	 * 
	 * @param splittedPolygon
	 * @return
	 */
	private List<List<IPolygon>> generateFlagParcel(List<IPolygon> splittedPolygon) {
System.out.println("generateFlagParcel");
splittedPolygon.stream().forEach(p->System.out.println(p));
		// The output polygon
		List<List<IPolygon>> polygonesOut = new ArrayList<>();
		polygonesOut.add(new ArrayList<>());
		polygonesOut.add(new ArrayList<>());

		// We get the two geometries with and without road access
		List<IPolygon> lPolygonWithRoadAccess = splittedPolygon.stream().filter(x -> hasRoadAccess(x)).collect(Collectors.toList());
		List<IPolygon> lPolygonWithNoRoadAccess = splittedPolygon.stream().filter(x -> !hasRoadAccess(x)).collect(Collectors.toList());

		bouclepoly: for (IPolygon currentPoly : lPolygonWithNoRoadAccess) {

			List<Pair<IMultiCurve<IOrientableCurve>, IPolygon>> listMap = generateCandidateForCreatingRoad(currentPoly, lPolygonWithRoadAccess);

			// We order the proposition according to the length (we will try at first to
			// build the road on the shortest side
			listMap.sort(new Comparator<Pair<IMultiCurve<IOrientableCurve>, IPolygon>>() {

				@Override
				public int compare(Pair<IMultiCurve<IOrientableCurve>, IPolygon> o1, Pair<IMultiCurve<IOrientableCurve>, IPolygon> o2) {

					return Double.compare(o1.getKey().length(), o2.getKey().length());
				}
			});

			boucleside: for (Pair<IMultiCurve<IOrientableCurve>, IPolygon> side : listMap) {
				// The geometry road
				IGeometry road = side.getKey().buffer(this.roadWidth);
System.out.println("ROAD");
System.out.println(road);
				// The road intersects a building, we do not keep it
				if (!this.buildings.select(road).isEmpty()) {
					 System.out.println("Building case : " + this.polygonInit);
					continue;

				}

				// The first geometry is the polygon with road access and a remove of the
				// geometry
//				IGeometry geomPol1 = side.getValue().difference(road);
//
//				if (geomPol1 == null) {
//					geomPol1 = side.getValue().difference(road.buffer(0.5));
//				}

				IGeometry geomPol1 = null;
				IGeometry roadToAdd = null;
        try {
          IGeometry[] intersectionDifference = getIntersectionDifference(side.getValue(), road);
          roadToAdd = intersectionDifference[0];
          geomPol1 = intersectionDifference[1];
//          System.out.println("A");
//          System.out.println(side.getValue());
//          System.out.println("B");
//          System.out.println(road);
//          System.out.println("Difference");
//          System.out.println(geomPol1);
//          System.out.println("Should be");
//          System.out.println(side.getValue().difference(road));
//          System.out.println("Intersection");
//          System.out.println(roadToAdd);
        } catch (Exception e) {
          e.printStackTrace();
        }

				geomPol1 = makePolygonValid(geomPol1);
				// The second geometry is the union between the road (intersection between road
				// and existing parcel) and the original of the geomtry of the parcel with no
				// road acess

//				IGeometry roadToAdd = road.intersection(side.getValue());
//
//				if (roadToAdd == null) {
//					roadToAdd = road.buffer(0.5).intersection(side.getValue());
//				}

				roadToAdd = makePolygonValid(roadToAdd);

//				IGeometry geomPol2 = currentPoly.union(roadToAdd.buffer(0.01)).buffer(0.0);
				IGeometry geomPol2;
        try {
          geomPol2 = getUnion(currentPoly,roadToAdd);
        } catch (Exception e) {
          e.printStackTrace();
          geomPol2 = currentPoly.union(roadToAdd.buffer(0.01)).buffer(0.0);
        }
				geomPol2 = makePolygonValid(geomPol2);

				// It might be a multi polygon so we remove the small area <
				List<IPolygon> lPolygonsOut1 = FromGeomToSurface.convertGeom(geomPol1).stream().map(x -> (IPolygon) x).collect(Collectors.toList());
				lPolygonsOut1 = lPolygonsOut1.stream().filter(x -> x.area() > TOO_SMALL_PARCEL_AREA).collect(Collectors.toList());

				List<IPolygon> lPolygonsOut2 = FromGeomToSurface.convertGeom(geomPol2).stream().map(x -> (IPolygon) x).collect(Collectors.toList());
				lPolygonsOut2 = lPolygonsOut2.stream().filter(x -> x.area() > TOO_SMALL_PARCEL_AREA).collect(Collectors.toList());

				System.out.println("lPolygonsOut1");
				lPolygonsOut1.stream().forEach(p->System.out.println(p));
        System.out.println("lPolygonsOut2");
        lPolygonsOut2.stream().forEach(p->System.out.println(p));
				// We check if there is a road acces for all, if not we abort
				for (IPolygon pol : lPolygonsOut1) {
					if (!hasRoadAccess(pol)) {
						 System.out.println("Road access is missing ; polyinit : " +
						 this.polygonInit);
						 System.out.println("Current polyg : " + pol);
						continue boucleside;
					}
				}
				for (IPolygon pol : lPolygonsOut2) {
					if (!hasRoadAccess(pol)) {
						 System.out.println("Road access is missing ; polyinit : " +
						 this.polygonInit);
						 System.out.println("Current polyg : " + pol);
						continue boucleside;
					}
				}

				// We directly add the result from polygon 2 to the results
				polygonesOut.get(1).addAll(lPolygonsOut2);

				// We update the geometry of the first polygon
				lPolygonWithRoadAccess.remove(side.getValue());
				lPolygonWithRoadAccess.addAll(lPolygonsOut1);

				// We go to the next polygon
				continue bouclepoly;
			}

			/*
			 * System.out.println("I am empty"); generateFlagParcel(splittedPolygon);
			 */

			// We have added nothing if we are here, we kept the initial polygon
			polygonesOut.get(1).add(currentPoly);
		}

		// We add the polygon with road access
		polygonesOut.get(0).addAll(lPolygonWithRoadAccess);

		return polygonesOut;
	}
	private IGeometry[] getIntersectionDifference(IGeometry a, IGeometry b) throws Exception {
	  GeometryFactory fact = new GeometryFactory();
	  Geometry jtsGeomA = AdapterFactory.toGeometry(fact, a, true);
	  Geometry jtsGeomB = AdapterFactory.toGeometry(fact, b, true);
	  try {
	  Geometry[] result = FeaturePolygonizer.getIntersectionDifference(new ArrayList<Geometry>(Arrays.asList(jtsGeomA)), new ArrayList<Geometry>(Arrays.asList(jtsGeomB)));
	  return new IGeometry[] {JtsGeOxygene.makeGeOxygeneGeom(result[0]), JtsGeOxygene.makeGeOxygeneGeom(result[1])};
	  } catch (Exception e) {
	    System.out.println("GeomA");
	    System.out.println(jtsGeomA);
      System.out.println("GeomB");
      System.out.println(jtsGeomB);
      System.out.println("Polygons");
      for (Polygon p: FeaturePolygonizer.getPolygons(new ArrayList<Geometry>(Arrays.asList(jtsGeomA, jtsGeomB)))) {
        System.out.println(p);
      }
	    throw e;
	  }
	}
  private IGeometry getUnion(IGeometry a, IGeometry b) throws Exception {
    GeometryFactory fact = new GeometryFactory();
    PrecisionModel pm = new PrecisionModel(100);
    Geometry jtsGeomA = GeometryPrecisionReducer.reduce(AdapterFactory.toGeometry(fact, a, true), pm);
    Geometry jtsGeomB = GeometryPrecisionReducer.reduce(AdapterFactory.toGeometry(fact, b, true), pm);
    try {
      return JtsGeOxygene.makeGeOxygeneGeom(new CascadedPolygonUnion(new ArrayList<Geometry>(Arrays.asList(jtsGeomA, jtsGeomB))).union());
    } catch (Exception e) {
      return JtsGeOxygene.makeGeOxygeneGeom(FeaturePolygonizer.getUnion(new ArrayList<Geometry>(Arrays.asList(jtsGeomA, jtsGeomB))));
    }
  }
  private IGeometry getIntersection(IGeometry a, IGeometry b) throws Exception {
    try {
      return a.intersection(b);
    } catch (Exception e) {
      GeometryFactory fact = new GeometryFactory();
      PrecisionModel pm = new PrecisionModel(100);
      Geometry jtsGeomA = GeometryPrecisionReducer.reduce(AdapterFactory.toGeometry(fact, a, true), pm);
      Geometry jtsGeomB = GeometryPrecisionReducer.reduce(AdapterFactory.toGeometry(fact, b, true), pm);
      return JtsGeOxygene.makeGeOxygeneGeom(FeaturePolygonizer.getIntersection(new ArrayList<Geometry>(Arrays.asList(jtsGeomA, jtsGeomB))));
    }
  }
	/**
	 * Generate a list of candidate for creating roads. The pair is composed of a linestring that may be used to generate the road and the parcel on which it may be built
	 * 
	 * @param currentPoly
	 * @param lPolygonWithRoadAcces
	 * @return
	 */
	private List<Pair<IMultiCurve<IOrientableCurve>, IPolygon>> generateCandidateForCreatingRoad(IPolygon currentPoly, List<IPolygon> lPolygonWithRoadAcces) {
		// A buffer to get the sides of the polygon with no road access
		IGeometry buffer = currentPoly.buffer(0.1);

		// A map to know to which polygon belongs a potential road
		List<Pair<IMultiCurve<IOrientableCurve>, IPolygon>> listMap = new ArrayList<>();

		for (IPolygon polyWithRoadAcces : lPolygonWithRoadAcces) {
			if (!polyWithRoadAcces.intersects(buffer)) {
				continue;
			}

			// We list the segments of the polygon with road access
			List<IOrientableCurve> lExterior = FromGeomToLineString.convertInSegment(polyWithRoadAcces);

			// We keep the ones that does not intersect the buffer
			List<IOrientableCurve> lExteriorToKeep = lExterior.stream().filter(x -> (!buffer.contains(x))).filter(x -> !this.getExt().buffer(0.1).contains(x))
					.collect(Collectors.toList());

			// We regroup the lines according to their connectivity
			List<IMultiCurve<IOrientableCurve>> sides = this.regroupLineStrings(lExteriorToKeep);
			// We add elements to list the correspondance between pears
			sides.stream().forEach(x -> listMap.add(new Pair<>(x, polyWithRoadAcces)));
		}

		return listMap;
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

	// This line represents the exterior of an urban island (it serves to determine
	// if a parcel has road access)
	private IMultiCurve<IOrientableCurve> ext = null;

	/**
	 * Determine the width of the parcel on road
	 * 
	 * @param p
	 * @return
	 */
	private double frontSideWidth(IPolygon p) {

		IGeometry geom = p.buffer(1).intersection(this.getExt());

		if (geom == null) {
			geom = p.buffer(5).intersection(this.getExt());
		}

		if (geom == null) {
			System.out.println("Cannot process to intersection between");
			System.out.println(p.toString());
			System.out.println(this.getExt().toString());
			return 0;
		}
		return geom.length();
	}

	/**
	 * Computed the splitting polygons composed by two boxes determined from the oriented bounding boxes splited from a line at its middle
	 * 
	 * @param pol
	 *            : the input polygon
	 * @param shortDirectionSplit
	 *            : it is splitted by the short edges or by the long edge.
	 * @return
	 * @throws Exception
	 */
	public static List<IPolygon> computeSplittingPolygon(IGeometry pol, boolean shortDirectionSplit, double noise) throws Exception {

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
		// chosen
		// direction
		// This points will be used for splitting
		IDirectPositionList intersectedPoints = determineIntersectedPoints(new LineEquation(translateCentroid, splitDirection),
				(shortDirectionSplit) ? oBB.getLongestEdges() : oBB.getShortestEdges());

		// Construction of the two splitting polygons by using the OBB edges and
		// the
		// intersection points
		IPolygon pol1 = determinePolygon(intersectedPoints, (shortDirectionSplit) ? oBB.getShortestEdges().get(0) : oBB.getLongestEdges().get(0));
		IPolygon pol2 = determinePolygon(intersectedPoints, (shortDirectionSplit) ? oBB.getShortestEdges().get(1) : oBB.getLongestEdges().get(1));

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

		IDirectPosition dp1 = eq.intersectionLineLine(new LineEquation(ls.get(0).coord().get(0), ls.get(0).coord().get(1)));
		IDirectPosition dp2 = eq.intersectionLineLine(new LineEquation(ls.get(1).coord().get(0), ls.get(1).coord().get(1)));

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

		IGeometry polyGeom = poly;
		if (!polyGeom.isValid()) {
			polyGeom = polyGeom.buffer(0);
		}

		IGeometry geom = null;
		IGeometry geom2 = null;
		
		try {
      geom = getIntersection(polygones.get(0), polyGeom);
      geom2 = getIntersection(polygones.get(1),polyGeom);
    } catch (Exception e) {
      e.printStackTrace();
    }

		if (geom == null) {
			geom = polygones.get(0).intersection(polyGeom.buffer(0.1));
		}

		if (geom2 == null) {
			geom2 = polygones.get(1).intersection(polyGeom.buffer(0.1));
		}

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
	public boolean hasRoadAccess(IPolygon poly) {

		if (poly.intersects(this.getExt().buffer(0.5))) {
			return true;
		}

		return false;
	}

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
