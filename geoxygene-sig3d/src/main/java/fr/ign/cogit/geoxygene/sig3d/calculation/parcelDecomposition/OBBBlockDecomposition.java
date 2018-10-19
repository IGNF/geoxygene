package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
=======
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

>>>>>>> 8664bae0666183b485afb7d0ee298683fd721c1f
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
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

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
		
		DirectPosition.PRECISION = 5;
		
		String strMuliPol = "POLYGON ((930504.39 6676077.2 0.0, 930503.2 6676071.07 0.0, 930485.03 6676097.67 0.0, 930481.32 6676103.72 0.0, 930462.54 6676132.06 0.0, 930462.63 6676145.02 0.0, 930462.79 6676160.69 0.0, 930459.84 6676170.01 0.0, 930444.52 6676187.57 0.0, 930440.01 6676199.36 0.0, 930439.81 6676199.93 0.0, 930436.99 6676204.35 0.0, 930426.35 6676221.02 0.0, 930425.36 6676236.96 0.0, 930420.83 6676241.63 0.0, 930404.88 6676255.86 0.0, 930406.14 6676257.45 0.0, 930394.8 6676266.84 0.0, 930394.22 6676267.19 0.0, 930398.09 6676276.79 0.0, 930399.56 6676279.36 0.0, 930403.84 6676284.62 0.0, 930407.5 6676287.92 0.0, 930411.9 6676291.22 0.0, 930415.44 6676293.44 0.0, 930419.13 6676295.19 0.0, 930421.32 6676295.78 0.0, 930427.86 6676296.75 0.0, 930430.31 6676296.72 0.0, 930432.45 6676296.1 0.0, 930437.31 6676292.66 0.0, 930434.51 6676294.18 0.0, 930431.87 6676294.79 0.0, 930429.37 6676294.73 0.0, 930427.65 6676294.39 0.0, 930410.69 6676281.4 0.0, 930450.57 6676247.86 0.0, 930448.48 6676245.5 0.0, 930428.87 6676232.34 0.0, 930428.38 6676230.74 0.0, 930429.18 6676221.98 0.0, 930429.85 6676220.53 0.0, 930441.14 6676201.64 0.0, 930447.02 6676189.03 0.0, 930468.95 6676163.75 0.0, 930469.99 6676168.82 0.0, 930477.41 6676196.71 0.0, 930483.7 6676222.35 0.0, 930484.44 6676225.73 0.0, 930496.78 6676276.86 0.0, 930505.27 6676278.61 0.0, 930504.84 6676276.93 0.0, 930503.29 6676275.93 0.0, 930496.08 6676246.39 0.0, 930488.4 6676214.93 0.0, 930487.78 6676212.38 0.0, 930488.11 6676212.27 0.0, 930481.71 6676187.51 0.0, 930476.29 6676166.56 0.0, 930475.36 6676158.67 0.0, 930473.02 6676131.37 0.0, 930491.7 6676099.55 0.0, 930504.39 6676077.2 0.0))";

        double roadEpsilon = 0;
        double noise = 0;
        double maximalArea = 1200;
        double maximalWidth = 50;

        
		
		IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(WktGeOxygene.makeGeOxygene(strMuliPol)).get(0);
		System.out.println(pol.toString());
		
		OBBBlockDecomposition decomposition = new OBBBlockDecomposition(pol, maximalArea, maximalWidth, new MersenneTwister() , roadEpsilon);
		IFeatureCollection<IFeature> featColl = decomposition.decompParcel(noise);
		
	
		
		ShapefileWriter.write(featColl, "/tmp/tmp.shp");
	}

	private double maximalArea, maximalWidth;
	private double epsilon;
	IPolygon polygonInit;

	/**
	 * 
	 * @param p            : the polygon block that is decomposed
	 * @param maximalArea  : maximal area of splitted parcel
	 * @param maximalWidth : maximal road access of splitter parcel
	 * @param epsilon      : the likeness to garuantee road access to parcels
	 */
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, double epsilon) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.epsilon = epsilon;
		this.polygonInit = p;
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
		
		if( ! p .isValid()){
			p= (IPolygon) p.buffer(0);
			
			if(! p.isValid()){
				p= (IPolygon) p.buffer(0.001);
			}else{
				if(! p.isValid()){
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
		List<IPolygon> splittingPolygon = computeSplittingPolygon(p, true, noise);

		// Split into polygon
		List<IPolygon> splittedPolygon = split(p, splittingPolygon);

		// If a parcel has no road access, there is a probability to make a
		// perpendicular split
		if (!hasRoadAccess(splittedPolygon.get(0)) || !hasRoadAccess(splittedPolygon.get(1))) {
			// Probability to make a perpendicular split
			if (Math.random() < epsilon) {
				// Same steps but with different splitting geometries
				splittingPolygon = computeSplittingPolygon(p, false, noise);

				splittedPolygon = split(p, splittingPolygon);
			}

		}

		// All splitted polygones are splitted and results added to the output
		for (IPolygon pol : splittedPolygon) {
			featCollOut.addAll(decompParcel(pol, noise));
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


		// Determine the points that intersect the line and the OBB according to
		// chosen direction
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
