package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
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
		
		String strMuliPol = "MULTIPOLYGON(((950028.07999999995809048 6686581.92999999970197678, 950041.53000000002793968 6686592.5400000000372529, 950053.77000000001862645 6686602.79999999981373549, 950057.31000000005587935 6686627.53000000026077032, 950062.91000000003259629 6686664.17999999970197678, 950067.21999999997206032 6686663.37000000011175871, 950059.32999999995809048 6686606.98000000044703484, 950088.76000000000931323 6686630.5400000000372529, 950098.48999999999068677 6686669.07000000029802322, 950106.16000000003259629 6686721.01999999955296516, 950100.30000000004656613 6686717.66000000014901161, 950073.34999999997671694 6686704.21999999973922968, 950070.88000000000465661 6686706.96999999973922968, 950103.72999999998137355 6686723.37000000011175871, 950084.96999999997206032 6686747.13999999966472387, 950067.56000000005587935 6686769.2099999999627471, 950049.85999999998603016 6686794.7900000000372529, 950034.55000000004656613 6686817.36000000033527613, 950019.56999999994877726 6686839.20000000018626451, 950012.2099999999627471 6686849.9599999999627471, 950010.15000000002328306 6686852.96999999973922968, 950010.53000000002793968 6686857.69000000040978193, 950013.41000000003259629 6686892.58000000007450581, 950016.68999999994412065 6686927.44000000040978193, 950017.0400000000372529 6686957.9599999999627471, 950042.96999999997206032 6686983.13999999966472387, 950087.75 6687026.67999999970197678, 950115.81000000005587935 6687053.90000000037252903, 950123.48999999999068677 6687114.80999999959021807, 950128.40000000002328306 6687119.23000000044703484, 950133.27000000001862645 6687087.17999999970197678, 950133.93999999994412065 6687082.73000000044703484, 950136.21999999997206032 6687067.75, 950163.78000000002793968 6687013.73000000044703484, 950160.93000000005122274 6687009.08999999985098839, 950220.35999999998603016 6686965.04999999981373549, 950240.85999999998603016 6686955.82000000029802322, 950362.9599999999627471 6686901.75, 950383.75 6686978.5, 950348.53000000002793968 6686993.76999999955296516, 950301.30000000004656613 6687045.15000000037252903, 950313.2099999999627471 6687085.4599999999627471, 950314.98999999999068677 6687092.44000000040978193, 950367.18000000005122274 6687211.07000000029802322, 950445.96999999997206032 6687248.91999999992549419, 950526.15000000002328306 6687292.53000000026077032, 950552.27000000001862645 6687296.61000000033527613, 950575.31999999994877726 6687254.71999999973922968, 950547.2900000000372529 6687232.5400000000372529, 950539.08999999996740371 6687212.12999999988824129, 950539.44999999995343387 6687207.28000000026077032, 950545.14000000001396984 6687201.50999999977648258, 950558.68999999994412065 6687193.54999999981373549, 950582.22999999998137355 6687218.40000000037252903, 950610.10999999998603016 6687242.95000000018626451, 950615.65000000002328306 6687247.9599999999627471, 950659.9599999999627471 6687303.20000000018626451, 950661.28000000002793968 6687306.05999999959021807, 950664.44999999995343387 6687315.03000000026077032, 950667.26000000000931323 6687320.05999999959021807, 950676.41000000003259629 6687330.05999999959021807, 950699.19999999995343387 6687355.34999999962747097, 950724.14000000001396984 6687391.03000000026077032, 950727.4599999999627471 6687395.9599999999627471, 950809.66000000003259629 6687443.26999999955296516, 950879.82999999995809048 6687490.7099999999627471, 950893.40000000002328306 6687504.65000000037252903, 950954.35999999998603016 6687537.66999999992549419, 951022.2900000000372529 6687553.42999999970197678, 951057.2900000000372529 6687557.20000000018626451, 951085.35999999998603016 6687561.36000000033527613, 951144.14000000001396984 6687565.66000000014901161, 951175.11999999999534339 6687580.4599999999627471, 951205.34999999997671694 6687584.36000000033527613, 951238.21999999997206032 6687594.2900000000372529, 951243.57999999995809048 6687594.90000000037252903, 951262.83999999996740371 6687596.99000000022351742, 951270.98999999999068677 6687597.41999999992549419, 951292.26000000000931323 6687591.49000000022351742, 951300.56999999994877726 6687586.53000000026077032, 951304.67000000004190952 6687583.36000000033527613, 951310.30000000004656613 6687578.54999999981373549, 951316.16000000003259629 6687572.49000000022351742, 951333.63000000000465661 6687558.33999999985098839, 951346.51000000000931323 6687556.91000000014901161, 951353.57999999995809048 6687555.13999999966472387, 951367.86999999999534339 6687548.24000000022351742, 951372.06000000005587935 6687545.54999999981373549, 951399.19999999995343387 6687549.16999999992549419, 951410.89000000001396984 6687549.86000000033527613, 951418.35999999998603016 6687549.55999999959021807, 951424.17000000004190952 6687548.54999999981373549, 951430.47999999998137355 6687543.86000000033527613, 951446.23999999999068677 6687529.34999999962747097, 951455.03000000002793968 6687518.75999999977648258, 951464.06999999994877726 6687509.19000000040978193, 951484.41000000003259629 6687486.29999999981373549, 951543.07999999995809048 6687419.29999999981373549, 951533.01000000000931323 6687368.19000000040978193, 951534.68000000005122274 6687364.5, 951550.13000000000465661 6687330.2099999999627471, 951599.52000000001862645 6687331.91999999992549419, 951637.32999999995809048 6687324.40000000037252903, 951664.51000000000931323 6687300.07000000029802322, 951658.73999999999068677 6687295.29999999981373549, 951564.16000000003259629 6687217.09999999962747097, 951513.80000000004656613 6687177.36000000033527613, 951481.81999999994877726 6687151.2099999999627471, 951379.47999999998137355 6687066.16000000014901161, 951341.31000000005587935 6687034.55999999959021807, 951307.02000000001862645 6686992.41000000014901161, 951292.01000000000931323 6686972.94000000040978193, 951248.44999999995343387 6686916.53000000026077032, 951201.69999999995343387 6686859.54999999981373549, 951183.05000000004656613 6686836.65000000037252903, 951143.30000000004656613 6686779, 951116.2099999999627471 6686739.86000000033527613, 951060.81000000005587935 6686659, 951035.03000000002793968 6686621.57000000029802322, 951008.78000000002793968 6686583.01999999955296516, 950968.58999999996740371 6686524.7099999999627471, 950896.10999999998603016 6686418.69000000040978193, 950855.64000000001396984 6686359.62000000011175871, 950850.21999999997206032 6686361.40000000037252903, 950850.42000000004190952 6686362.04999999981373549, 950823.82999999995809048 6686372.95000000018626451, 950798.16000000003259629 6686382.78000000026077032, 950749.35999999998603016 6686397.94000000040978193, 950746.30000000004656613 6686392.26999999955296516, 950729.39000000001396984 6686362.23000000044703484, 950733.5400000000372529 6686359.66000000014901161, 950730.59999999997671694 6686354.62999999988824129, 950755.08999999996740371 6686344.08000000007450581, 950774.02000000001862645 6686331.67999999970197678, 950827.09999999997671694 6686316.88999999966472387, 950750.57999999995809048 6686194.12999999988824129, 950702.2900000000372529 6686203.13999999966472387, 950671.68000000005122274 6686200.05999999959021807, 950652.28000000002793968 6686193.96999999973922968, 950575.36999999999534339 6686164.33999999985098839, 950531.67000000004190952 6686155.70000000018626451, 950509.2099999999627471 6686158.5400000000372529, 950521.77000000001862645 6686173.50999999977648258, 950532.72999999998137355 6686183.42999999970197678, 950556.43000000005122274 6686202.11000000033527613, 950568.38000000000465661 6686219.44000000040978193, 950587.43999999994412065 6686241.75, 950547.44999999995343387 6686265.23000000044703484, 950513.28000000002793968 6686276.79999999981373549, 950485.40000000002328306 6686254.83000000007450581, 950462.89000000001396984 6686221.91999999992549419, 950450.60999999998603016 6686248.76999999955296516, 950406.78000000002793968 6686346.83999999985098839, 950385.06000000005587935 6686421.75, 950372.4599999999627471 6686442.25, 950360.5400000000372529 6686455.88999999966472387, 950333.86999999999534339 6686471.54999999981373549, 950211.16000000003259629 6686542.41999999992549419, 950185.23999999999068677 6686555.01999999955296516, 950153.32999999995809048 6686561.2099999999627471, 950123.21999999997206032 6686561.83000000007450581, 950103.31000000005587935 6686544.5400000000372529, 950093.77000000001862645 6686533.50999999977648258, 950089.4599999999627471 6686537.59999999962747097, 950074.38000000000465661 6686534.42999999970197678, 950066.81999999994877726 6686533.63999999966472387, 950064.65000000002328306 6686533.40000000037252903, 950059.19999999995343387 6686537.33999999985098839, 950048.81999999994877726 6686553.12999999988824129, 950043.32999999995809048 6686561.44000000040978193, 950042.09999999997671694 6686563.12000000011175871, 950028.07999999995809048 6686581.92999999970197678)))";

        double roadEpsilon = 0;
        double noise = 0;
        double maximalArea = 1200;
        double maximalWidth = 50;

        
		
		IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(WktGeOxygene.makeGeOxygene(strMuliPol)).get(0);
		
		
		OBBBlockDecomposition decomposition = new OBBBlockDecomposition(pol, maximalArea, maximalWidth, new MersenneTwister() , roadEpsilon);
		IFeatureCollection<IFeature> featColl = decomposition.decompParcel(noise);
		
		ShapefileWriter.write(featColl, "/tmp/tmp.shp");
	}

	private double maximalArea, maximalWidth;
	private RandomGenerator rng;
	private double epsilon;
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
	public OBBBlockDecomposition(IPolygon p, double maximalArea, double maximalWidth, RandomGenerator rng,
			double epsilon) {
		super();

		this.maximalArea = maximalArea;
		this.maximalWidth = maximalWidth;
		this.rng = rng;
		this.epsilon = epsilon;
		this.p = p;
	}

	/**
	 * The decomposition method
	 * 
	 * @return List of parcels
	 * @throws Exception
	 */
	public IFeatureCollection<IFeature> decompParcel(double noise) throws Exception {
		return decompParcel(this.p, noise);
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
			if (rng.nextDouble() < epsilon) {
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

		// Construction of the two splitting polygons by using the OBB edges and
		// the intersection points
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

		ILineString ext = new GM_LineString(this.p.getExterior().coord());

		return poly.intersects(ext.buffer(0.5));
	}

}
