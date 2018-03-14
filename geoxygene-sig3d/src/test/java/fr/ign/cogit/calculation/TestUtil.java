package fr.ign.cogit.calculation;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.tools.Utils;
import junit.framework.TestCase;


public class TestUtil extends TestCase {


	// ---------------------------------- ATTRIBUTES ----------------------------------

	private double epsilon = Math.pow(10, -4);    // Scale error
	
	private static Logger log = Logger.getLogger(TestUtil.class);

	// ------------------------------------ TESTS -------------------------------------


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of a set of points
	// --------------------------------------------------------------------------------
	public void testCenterOf1() {
		
		log.info("Test for method to compute center of gravity of a set of points");

		// Number of points
		int n = 100;

		// Mean coordinates
		double xMean = 0;
		double yMean = 0;
		double zMean = 0;

		// Generating set of points
		DirectPositionList dpl = new DirectPositionList();

		for (int i=0; i<n; i++){

			double x = Math.random()*100;
			double y = Math.random()*100;
			double z = Math.random()*100;

			dpl.add(new DirectPosition(x, y, z));

			xMean += x;
			yMean += y;
			zMean += z;

		}

		xMean /= n;
		yMean /= n;
		zMean /= n;

		// Processing center of gravity
		DirectPosition g = Util.centerOf(dpl);

		// Processing expected result
		DirectPosition gExp = new DirectPosition(xMean, yMean, zMean);

		// Comparison
		assertTrue("Points set center of gravity is incorrect", gExp.distance(g)<epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of an empty set of points
	// --------------------------------------------------------------------------------
	public void testCenterOf2() {
		
		log.info("Test for method to compute center of gravity of an empty set of points");

		// Generating empty set of points
		DirectPositionList dpl = new DirectPositionList();

		// Processing center of gravity
		DirectPosition g = Util.centerOf(dpl);

		// Processing expected result
		DirectPosition gExp = new DirectPosition(Double.NaN, Double.NaN, Double.NaN);

		// Comparison
		assertTrue("Points set center of gravity is incorrect", g.equals(gExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume under triangle
	// --------------------------------------------------------------------------------
	public void testCenterOf3() {
		
		log.info("Test for method to compute volume under triangle");

		// Generating triangle
		DirectPositionList dpl = new DirectPositionList();

		dpl.add(new DirectPosition(0, 0, 12));
		dpl.add(new DirectPosition(1, 0, 10));
		dpl.add(new DirectPosition(0, 1, 12));

		GM_Triangle triangle = new GM_Triangle(dpl);

		// Processing volume
		double v = Util.volumeUnderTriangle(triangle);

		// Processing expected result
		double vP = 1.0*1.0/2.0*10.0; 
		double vC = 1.0*2.0*1.0/3.0; 
		double vExp = vC + vP;

		// Comparison
		assertEquals("Volume under triangle is incorrect", vExp, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume under below Oxy triangle
	// --------------------------------------------------------------------------------
	public void testCenterOf4() {
		
		log.info("Test for method to compute volume under below Oxy triangle");

		// Generating triangle
		DirectPositionList dpl = new DirectPositionList();

		dpl.add(new DirectPosition(0, 0, 12));
		dpl.add(new DirectPosition(1, 0, 10));
		dpl.add(new DirectPosition(0, 1, 12));

		GM_Triangle triangle = new GM_Triangle(dpl);

		// Processing volume
		double v = Util.volumeUnderTriangle(triangle);

		// Processing expected result
		double vP = 1.0*1.0/2.0*10.0; 
		double vC = 1.0*2.0*1.0/3.0; 
		double vExp = vC + vP;

		// Comparison
		assertEquals("Volume under triangle is incorrect", vExp, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume "under" vertical triangle
	// --------------------------------------------------------------------------------
	public void testCenterOf5() {
		
		log.info("Test for method to compute volume 'under' vertical triangle");

		// Generating triangle
		DirectPositionList dpl = new DirectPositionList();

		dpl.add(new DirectPosition(0, 0, 12));
		dpl.add(new DirectPosition(0, 0, 10));
		dpl.add(new DirectPosition(0, 1, 10));

		GM_Triangle triangle = new GM_Triangle(dpl);

		// Processing volume
		double v = Util.volumeUnderTriangle(triangle);

		// Expected result
		double vExp = 0;

		// Comparison
		assertEquals("Volume under triangle is incorrect", vExp, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect vertical faces
	// --------------------------------------------------------------------------------
	public void testCenterOf6() {
		
		log.info("Test for method to detect vertical faces");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Surface 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,1,0));
		dpl1.add(new DirectPosition(1,1,0));
		dpl1.add(new DirectPosition(1,0,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Surface surface1 = new GM_Triangle(line1);

		// Surface 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		dpl2.add(new DirectPosition(1,0,0));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Surface surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectVertical(AS, epsilon);

		// Recovering vertical surface
		GM_Surface vertical = (GM_Surface) MS.get(0);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertTrue("Vertical surface has not been detected", vertical.toString().equals(surface2.toString()));
		assertEquals("Wrong number of vertical surface has been detected", 1, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect vertical faces with empty list
	// --------------------------------------------------------------------------------
	public void testCenterOf7() {
		
		log.info("Test for method to detect vertical faces with empty list");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectVertical(AS, epsilon);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Wrong number of vertical surface has been detected", 0, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect non-vertical faces
	// --------------------------------------------------------------------------------
	public void testCenterOf8() {
		
		log.info("Test for method to detect non-vertical faces");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Surface 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,2,0));
		dpl1.add(new DirectPosition(2,2,0));
		dpl1.add(new DirectPosition(2,0,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Surface surface1 = new GM_Triangle(line1);

		// Surface 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		dpl2.add(new DirectPosition(1,0,0));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Surface surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectNonVertical(AS, epsilon);

		// Recovering vertical surface
		GM_Surface noVertical = (GM_Surface) MS.get(0);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Non-vertical surface has not been detected", 8, noVertical.perimeter(), epsilon);
		assertEquals("Wrong number of non-vertical surface has been detected", 1, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect non-vertical faces with empty list
	// --------------------------------------------------------------------------------
	public void testCenterOf9() {
		
		log.info("Test for method to detect non-vertical faces with empty list");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectNonVertical(AS, epsilon);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Wrong number of non-vertical surface has been detected", 0, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect roof faces
	// --------------------------------------------------------------------------------
	public void testCenterOf10() {
		
		log.info("Test for method to detect roof faces");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Surface 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,2,0));
		dpl1.add(new DirectPosition(2,2,0));
		dpl1.add(new DirectPosition(2,0,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Surface surface1 = new GM_Triangle(line1);

		// Surface 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		dpl2.add(new DirectPosition(1,0,0));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Surface surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectRoof(AS, epsilon);

		// Recovering vertical surface
		GM_Surface noVertical = (GM_Surface) MS.get(0);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Roof has not been detected", 8, noVertical.perimeter(), epsilon);
		assertEquals("Wrong number of roof surfaces has been detected", 1, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect roof with empty list
	// --------------------------------------------------------------------------------
	public void testCenterOf11() {
		
		log.info("Test for method to detect roof with empty list");

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectRoof(AS, epsilon);

		// Comparison
		assertNull("Wrong number of roof surfaces has been detected", MS);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect roof in list of triangles
	// --------------------------------------------------------------------------------
	public void testCenterOf12() {
		
		log.info("Test for method to detect roof in list of triangles");

		// Creating list of surfaces
		ArrayList<ITriangle> AT = new ArrayList<ITriangle>();

		// Surface 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,2,0));
		dpl1.add(new DirectPosition(2,2,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AT.add(surface1);
		AT.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<ITriangle> MS = Util.detectRoofTriangles(AT, epsilon);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Wrong number of roof surfaces has been detected", 1, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect roof in list of triangles
	// --------------------------------------------------------------------------------
	public void testCenterOf13() {
		
		log.info("Test for method to detect roof in list of triangles");

		// Creating list of surfaces
		ArrayList<ITriangle> AT = new ArrayList<ITriangle>();


		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AT.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<ITriangle> MS = Util.detectRoofTriangles(AT, epsilon);

		// Comparison
		assertNull("Wrong number of roof surfaces has been detected", MS);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect roof in list of triangles
	// --------------------------------------------------------------------------------
	public void testCenterOf14() {
		
		log.info("Test for method to detect roof in list of triangles");

		// Creating list of surfaces
		ArrayList<ITriangle> AT = new ArrayList<ITriangle>();

		// Surface 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,2,0));
		dpl1.add(new DirectPosition(2,2,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AT.add(surface1);
		AT.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<ITriangle> MS = Util.detectRoofTriangles(AT, epsilon);

		// recovering number of detected surfaces
		int nDetect = MS.size();

		// Comparison
		assertEquals("Wrong number of floor surfaces has been detected", 1, nDetect);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to detect floor in list of triangles
	// --------------------------------------------------------------------------------
	public void testCenterOf15() {
		
		log.info("Test for method to detect floor in list of triangles");

		// Creating list of surfaces
		ArrayList<ITriangle> AT = new ArrayList<ITriangle>();


		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AT.add(surface2);

		// Detecting vertical surface
		GM_MultiSurface<ITriangle> MS = Util.detectFloorTriangles(AT, epsilon);

		// Comparison
		assertNull("Wrong number of floor surfaces has been detected", MS);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to know if a list contains only triangular surfaces
	// --------------------------------------------------------------------------------
	public void testCenterOf16() {
		
		log.info("Test for method to know if a list contains only triangular surfaces");

		// Creating list of surfaces
		ArrayList<IOrientableSurface> AS = new ArrayList<IOrientableSurface>();

		// Surface 1 : triangle
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,0));
		dpl1.add(new DirectPosition(0,2,0));
		dpl1.add(new DirectPosition(2,2,0));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2 :
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,0));
		dpl2.add(new DirectPosition(0,0,1));
		dpl2.add(new DirectPosition(1,0,1));
		dpl2.add(new DirectPosition(1,0,2));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Polygon surface2 = new GM_Polygon(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Recovering result
		boolean bf = Util.containOnlyTriangleFaces(AS);

		// Removing non triangle surface
		AS.remove(1);

		// Adding polygon triangle
		dpl2.remove(3);
		line2 = new GM_LineString(dpl2);
		surface2 = new GM_Polygon(line2);
		AS.add(surface2);

		// Recovering result
		boolean bt1 = Util.containOnlyTriangleFaces(AS);

		// Removing polygon triangle surface
		AS.remove(1);

		// Adding triangle
		surface2 = new GM_Triangle(line2);
		AS.add(surface2);

		// Recovering result
		boolean bt2 = Util.containOnlyTriangleFaces(AS);

		// Comparison
		assertFalse("List does not contain only triangular surfaces", bf);
		assertTrue("List contain only triangular surfaces", bt1);
		assertTrue("List contain only triangular surfaces", bt2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to know if an empty list contains only triangular surfaces
	// --------------------------------------------------------------------------------
	public void testCenterOf17() {
		
		log.info("Test for method to know if an empty list contains only triangular surfaces");

		// Creating list of surfaces
		ArrayList<IOrientableSurface> AS = new ArrayList<IOrientableSurface>();

		// Recovering result
		boolean bt = Util.containOnlyTriangleFaces(AS);

		// Comparison
		assertTrue("List does not contain non-triangular surfaces (empty list)", bt);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume under a surface
	// --------------------------------------------------------------------------------
	public void testCenterOf18() {
		
		log.info("Test for method to compute volume under a surface");

		// Creating list of surfaces
		ArrayList<IOrientableSurface> AS = new ArrayList<IOrientableSurface>();

		// Random parameters
		double triangleSize = Math.random()*100;
		double alti = Math.random()*100;
		
		
		triangleSize = (Math.round((triangleSize *1000 ) / 1000));
		alti = (Math.round((alti *1000 ) / 1000));

		// Surface 1 : triangle
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,alti));
		dpl1.add(new DirectPosition(triangleSize,triangleSize,alti));
		dpl1.add(new DirectPosition(0,triangleSize,alti));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2 : triangle
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,alti));
		dpl2.add(new DirectPosition(triangleSize,0,alti));
		dpl2.add(new DirectPosition(triangleSize,triangleSize,alti));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Processing volume under surface
		double v = Util.volumeUnderSurface(AS);

		// Processing expected result
		double vExp = triangleSize*triangleSize*alti;

		// Comparison
		assertEquals("Volume under surface is incorrect", vExp, v, Math.pow(10, 6)*epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to volume under an empty surface list
	// --------------------------------------------------------------------------------
	public void testCenterOf19() {
		
		log.info("Test for method to volume under an empty surface list");

		// Creating list of surfaces
		ArrayList<IOrientableSurface> AS = new ArrayList<IOrientableSurface>();

		// Processing volume under surface
		double v = Util.volumeUnderSurface(AS);

		// Comparison
		assertEquals("Volume under empty surface is incorrect", 0, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume under an negatively oriented surfaces
	// --------------------------------------------------------------------------------
	public void testCenterOf20() {
		
		log.info("Test for method to compute volume under an negatively oriented surfaces");

		// Creating list of surfaces
		ArrayList<IOrientableSurface> AS = new ArrayList<IOrientableSurface>();

		// Random parameters
		double triangleSize = Math.random()*100;
		double alti = Math.random()*100;

		// Surface 1 : triangle
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,alti));
		dpl1.add(new DirectPosition(triangleSize,triangleSize,alti));
		dpl1.add(new DirectPosition(0,triangleSize,alti));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2 : triangle
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,alti));
		dpl2.add(new DirectPosition(triangleSize,triangleSize,alti));
		dpl2.add(new DirectPosition(triangleSize,0,alti));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Processing volume under surface
		double v = Util.volumeUnderSurface(AS);

		// Comparison
		assertEquals("Volume under surface is incorrect", 0, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute volume in a triangulated surface (cube)
	// --------------------------------------------------------------------------------
	public void testCenterOf21() {
		
		log.info("Test for method to compute volume in a triangulated surface (cube)");

		// Random cube parameter
		double size = Math.random()*100.0; 

		// Creating triangulated cube
		GM_Solid cube = Utils.createTriangulatedCube(0, 0, 0, size);

		// Processing volume
		double v = Util.volumeTriangulatedSolid(cube);

		// Processing expected volume
		double vExp = Math.pow(size, 3);

		// Comparison
		assertEquals("Volume in triangulated solid is incorrect", vExp, v, Math.pow(10, 6)*epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute area of a triangulated surface (cube)
	// --------------------------------------------------------------------------------
	public void testCenterOf22() {
		
		log.info("Test for method to compute area of a triangulated surface (cube)");

		// Random cube parameter
		int size = (int) (Math.random()*100.0); 

		// Creating triangulated cube
		GM_Solid cube = Utils.createTriangulatedCube(0, 0, 0, size);

		// Processing area
		double v = Util.aireTriangulatedSolid(cube);

		// Processing expected volume
		double vExp = 6*size*size;

		// Comparison
		assertEquals("Volume in triangulated solid is incorrect", vExp, v, Math.pow(10, 6)*epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute area of a triangulated list of surfaces
	// --------------------------------------------------------------------------------
	public void testCenterOf23() {
		
		log.info("Test for method to compute area of a triangulated list of surfaces");

		// Creating list of surfaces
		ArrayList<ITriangle> AS = new ArrayList<ITriangle>();

		// Surface 1 : triangle
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(0,0,2));
		dpl1.add(new DirectPosition(1,1,2));
		dpl1.add(new DirectPosition(0,1,2));
		GM_LineString line1 = new GM_LineString(dpl1);
		GM_Triangle surface1 = new GM_Triangle(line1);

		// Surface 2 : triangle
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(0,0,2));
		dpl2.add(new DirectPosition(1,0,2));
		dpl2.add(new DirectPosition(1,1,2));
		GM_LineString line2 = new GM_LineString(dpl2);
		GM_Triangle surface2 = new GM_Triangle(line2);

		// Filling surface list
		AS.add(surface1);
		AS.add(surface2);

		// Processing volume under surface
		double v = Util.aireTriangles(AS);

		// Comparison
		assertEquals("Volume under surface is incorrect", 1, v, epsilon);

	}

	
}
