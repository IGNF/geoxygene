package fr.ign.cogit.calculation;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Proximity;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.tools.Utils;
import junit.framework.TestCase;


public class TestProximity extends TestCase {


	// ---------------------------------- ATTRIBUTES ----------------------------------

	private double epsilon = Math.pow(10, -10);    // Scale error

	private static Logger log = Logger.getLogger(TestProximity.class);

	// ------------------------------------ TESTS -------------------------------------


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between set of points processing
	// --------------------------------------------------------------------------------
	public void testNearest1() {

		log.info("Test for minimal distance between set of points processing");

		Proximity prox = new Proximity();

		// Creating point lists
		DirectPositionList POINTS1 = Utils.createPointsList(1, 0, 0, 1);
		DirectPositionList POINTS2 = Utils.createPointsList(1, 0.8, 0.8, 2);

		// Processing shortest distance
		prox.nearest(POINTS1, POINTS2);

		// Recovering closest couple of points
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;

		// Recovering shortest distance
		double d = prox.distance;

		// Setting expected result points
		DirectPosition p1Expected = new DirectPosition(1, 1, 1);
		DirectPosition p2Expected = new DirectPosition(0.8, 0.8, 2);

		// Processing expected distance
		double dExp = Math.sqrt(0.04+0.04+1);

		// Tests
		assertEquals("Closest point in list 1 is incorrect", p1Expected, p1);
		assertEquals("Closest point in list 2 is incorrect", p2Expected, p2);
		assertEquals("Shortest distance between set of points is incorrect", dExp, d, epsilon);	

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between single point and set of points processing
	// --------------------------------------------------------------------------------
	public void testNearest2() {

		log.info("Test for minimal distance between single point and set of points processing");

		Proximity prox = new Proximity();

		// Creating point lists
		DirectPositionList POINTS = new DirectPositionList();

		// Filling list
		POINTS.add(new DirectPosition(153, 127, 124));
		POINTS.add(new DirectPosition(375, 871, 212));
		POINTS.add(new DirectPosition(112, 155, 135));
		POINTS.add(new DirectPosition(159, 792, 336));

		// Creating single points
		DirectPosition point1 = new DirectPosition(110, 700, 300);
		DirectPosition point2 = new DirectPosition(153, 127, 124);

		// Processing case 1
		prox.nearest(point1, POINTS);

		// Expected results
		DirectPosition pExp = new DirectPosition(159, 792, 336);
		double dExp = Math.sqrt((159-110)*(159-110)+(792-700)*(792-700)+(336-300)*(336-300));

		// Actual results
		DirectPosition p = (DirectPosition) prox.nearest;  
		double d = prox.distance;  

		// Tests
		assertEquals("Closest point of set is incorrect", pExp, p);
		assertEquals("Shortest distance between point and set of points is incorrect", dExp, d, epsilon);


		// Processing case 2
		prox.nearest(point2, POINTS);

		// Expected results
		pExp = new DirectPosition(153, 127, 124);
		dExp = 0;

		// Actual results
		p = (DirectPosition) prox.nearest;
		d = prox.distance;

		// Tests
		assertEquals("Closest point of set is incorrect", pExp, p);
		assertEquals("Shortest distance between point and set of points is incorrect", dExp, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between single point and set of points processing
	// --------------------------------------------------------------------------------
	public void testNearest3() {

		log.info("Test for minimal distance between single point and set of points processing");

		Proximity prox = new Proximity();

		// Creating point lists
		DirectPositionList POINTS = new DirectPositionList();

		// Filling list with cube
		POINTS.add(new DirectPosition(0, 0, 0));
		POINTS.add(new DirectPosition(0, 1, 0));
		POINTS.add(new DirectPosition(1, 1, 0));
		POINTS.add(new DirectPosition(1, 0, 0));
		POINTS.add(new DirectPosition(0, 0, 1));
		POINTS.add(new DirectPosition(0, 1, 1));
		POINTS.add(new DirectPosition(1, 1, 1));
		POINTS.add(new DirectPosition(1, 0, 1));

		// Creating single points in cube centre
		DirectPosition point = new DirectPosition(0.5,0.5,0.5);

		// Processing
		prox.nearest(point, POINTS);

		// Comparison
		double dExp = Math.sqrt(0.5*0.5+0.5*0.5+0.5*0.5);
		double d = prox.distance;

		assertEquals("Shortest distance between point and set of points is incorrect", dExp, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between single point and single surface
	// --------------------------------------------------------------------------------
	public void testNearest4() {

		log.info("Test for minimal distance between single point and single surface");

		Proximity prox = new Proximity();

		// Creating surfaces list
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();
		SURFACES.add(Utils.createSquarePolygon(100, 0, 0));

		// Creating single points
		DirectPosition point1 = new DirectPosition(49,150,50);		// Case 1
		DirectPosition point2 = new DirectPosition(51,150,50);		// Case 2

		// Processing proximity functionnalities : case 1
		prox.nearest(point1, SURFACES);

		// Processing expected results
		DirectPosition p1Expected = new DirectPosition(49,150,50);
		DirectPosition p2Expected = new DirectPosition(0,100,0);
		GM_Surface surfaceExpected = (GM_Surface) SURFACES.get(0);
		double dExpected = Math.sqrt(49*49+50*50+50*50);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertEquals("Closest containing surface is incorrect", surfaceExpected, surface);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);

		// Processing proximity functionnalities : case 2
		prox.nearest(point2, SURFACES);

		// Processing expected results
		p1Expected = new DirectPosition(51,150,50);
		p2Expected = new DirectPosition(100,100,0);

		// Recovering results
		p1 = (DirectPosition) prox.nearest;
		p2 = (DirectPosition) prox.nearest2;
		surface = (GM_Surface) prox.containingFace;
		d = prox.distance;

		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertEquals("Closest containing surface is incorrect", surfaceExpected, surface);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between single point and surfaces list
	// --------------------------------------------------------------------------------
	public void testNearest5() {

		log.info("Test for minimal distance between single point and surfaces list");

		Proximity prox = new Proximity();

		// Creating surfaces
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();

		// Surfaces
		SURFACES.add(Utils.createSquarePolygon(1, 0, 0));
		SURFACES.add(Utils.createSquarePolygon(1, 1, 1));

		// Creating single points
		DirectPosition point1 = new DirectPosition(1.2, 0.2, 0);		// Case 1
		DirectPosition point2 = new DirectPosition(1.2, 0.8, 0);		// Case 2

		// Processing : case 1
		prox.nearest(point1, SURFACES);

		// Processing expected results
		DirectPosition p1Expected = new DirectPosition(1.2, 0.2, 0.0);
		DirectPosition p2Expected = new DirectPosition(1.0, 0.0, 0.0);
		GM_Surface surfaceExpected = (GM_Surface) SURFACES.get(0);
		double dExpected = Math.sqrt(0.2*0.2+0.2*0.2);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertEquals("Closest containing surface is incorrect", surfaceExpected, surface);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);


		// Processing : case 2
		prox.nearest(point2, SURFACES);

		// Processing expected results
		p1Expected = new DirectPosition(1.2, 0.8, 0.0);
		p2Expected = new DirectPosition(1.0, 1.0, 0.0);
		dExpected = Math.sqrt(0.2*0.2+0.2*0.2);

		// Recovering results
		p1 = (DirectPosition) prox.nearest;
		p2 = (DirectPosition) prox.nearest2;
		surface = (GM_Surface) prox.containingFace;
		d = prox.distance;

		// Special surface condition
		boolean b0 = surface.equals(SURFACES.get(0));
		boolean b1 = surface.equals(SURFACES.get(1));

		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertTrue("Closest containing surface is incorrect", b0 || b1);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between list of point and single surface
	// --------------------------------------------------------------------------------
	public void testNearest6() {

		log.info("Test for minimal distance between list of point and single surface");

		Proximity prox = new Proximity();

		// Creating surfaces
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();

		// Surface
		SURFACES.add(Utils.createSquarePolygon(1, 0, 0));

		// Creating list of points
		DirectPositionList POINTS = new DirectPositionList();

		POINTS.add(new DirectPosition(-10,12,3));
		POINTS.add(new DirectPosition(0.1,0.1,10));
		POINTS.add(new DirectPosition(1.3,0.4,0));

		// Processing case
		prox.nearest(POINTS, SURFACES);


		// Processing expected results
		DirectPosition p1Expected = new DirectPosition(1.3, 0.4, 0.0);
		DirectPosition p2Expected = new DirectPosition(1.0, 0.0, 0.0);
		GM_Surface surfaceExpected = (GM_Surface) SURFACES.get(0);
		double dExpected = Math.sqrt(0.3*0.3+0.4*0.4);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;


		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertEquals("Closest containing surface is incorrect", surfaceExpected, surface);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for minimal distance between list of point and list of surfaces
	// --------------------------------------------------------------------------------
	public void testNearest7() {

		log.info("Test for minimal distance between list of point and list of surfaces");

		Proximity prox = new Proximity();

		// Creating surfaces
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();

		// Surfaces
		SURFACES.add(Utils.createSquarePolygon(1, 0, 0));
		SURFACES.add(Utils.createSquarePolygon(1, 1, 1));

		// Creating list of points
		DirectPositionList POINTS = new DirectPositionList();

		POINTS.add(new DirectPosition(-10,12,3));
		POINTS.add(new DirectPosition(0.1,0.1,10));
		POINTS.add(new DirectPosition(1.3,0.4,0));

		// Processing case
		prox.nearest(POINTS, SURFACES);


		// Processing expected results
		DirectPosition p1Expected = new DirectPosition(1.3, 0.4, 0.0);
		DirectPosition p2Expected = new DirectPosition(1.0, 0.0, 0.0);
		GM_Surface surfaceExpected = (GM_Surface) SURFACES.get(0);
		double dExpected = Math.sqrt(0.3*0.3+0.4*0.4);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertEquals("Closest point is incorrect", p1Expected, p1);
		assertEquals("Closest point of surface is incorrect", p2Expected, p2);
		assertEquals("Closest containing surface is incorrect", surfaceExpected, surface);
		assertEquals("Shortest distance point-surfaces is incorrect", dExpected, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test when two points are same point
	// --------------------------------------------------------------------------------
	public void testNearest8() {

		log.info("Test when two points are same point");

		Proximity prox = new Proximity();

		// Creating point lists
		DirectPositionList POINTS1 = Utils.createPointsList(1, 0, 0, 1);
		DirectPositionList POINTS2 = Utils.createPointsList(1, 0, 0, 1);

		// Processing shortest distance
		prox.nearest(POINTS1, POINTS2);

		// Recovering closest couple of points
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;

		// Recovering shortest distance
		double d = prox.distance;

		// Processing expected distance
		double dExp = 0;

		boolean test1 =  false;
		for (IDirectPosition p : POINTS1){test1 = test1 || p1.equals(p);}

		boolean test2 =  false;
		for (IDirectPosition p : POINTS2){test2 = test2 || p2.equals(p);}


		// Tests
		assertTrue("Closest point in list 1 is incorrect", test1);
		assertTrue("Closest point in list 2 is incorrect", test2);
		assertEquals("Shortest distance between set of points is incorrect", dExp, d, epsilon);	

	}

	@Test
	// --------------------------------------------------------------------------------
	// Basic test case
	// --------------------------------------------------------------------------------
	public void testNearest9() {

		log.info("Basic test case");

		Proximity prox = new Proximity();

		// Creating surfaces
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();


		// Creating list of points
		DirectPositionList POINTS = new DirectPositionList();

		// Processing case
		prox.nearest(POINTS, SURFACES);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertNull("Point nearest should be null", p1);
		assertNull("Point nearest2 should be null", p2);
		assertNull("Surface containingFace should be null", surface);
		assertTrue("Distance should be null", Double.isNaN(d));


	}

	@Test
	// --------------------------------------------------------------------------------
	// Basic test case
	// --------------------------------------------------------------------------------
	public void testNearest10() {

		log.info("Basic test case");

		Proximity prox = new Proximity();

		// Creating surfaces
		ArrayList<IOrientableSurface> SURFACES = new ArrayList<IOrientableSurface>();


		// Creating list of points
		DirectPosition pt = new DirectPosition(10,10,10);

		// Processing case
		prox.nearest(pt, SURFACES);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertNull("Point nearest should be null", p1);
		assertNull("Point nearest2 should be null", p2);
		assertNull("Surface containingFace should be null", surface);
		assertTrue("Distance should be null", Double.isNaN(d));


	}

	@Test
	// --------------------------------------------------------------------------------
	// Basic test case
	// --------------------------------------------------------------------------------
	public void testNearest11() {

		log.info("Basic test case");

		Proximity prox = new Proximity();

		// Creating point lists
		DirectPositionList POINTS1 = new DirectPositionList();
		DirectPositionList POINTS2 = new DirectPositionList();

		// Processing shortest distance
		prox.nearest(POINTS1, POINTS2);

		// Tests
		assertTrue("", true);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Basic test case
	// --------------------------------------------------------------------------------
	public void testNearest12() {

		log.info("Basic test case");

		Proximity prox = new Proximity();

		// Creating surfaces
		DirectPositionList POINTS = new DirectPositionList();


		// Creating list of points
		DirectPosition pt = new DirectPosition(10,10,10);

		// Processing case
		prox.nearest(pt, POINTS);

		// Recovering results
		DirectPosition p1 = (DirectPosition) prox.nearest;
		DirectPosition p2 = (DirectPosition) prox.nearest2;
		GM_Surface surface = (GM_Surface) prox.containingFace;
		double d = prox.distance;

		// Comparison
		assertNull("Point nearest should be null", p1);
		assertNull("Point nearest2 should be null", p2);
		assertNull("Surface containingFace should be null", surface);
		assertTrue("Distance should be null", Double.isNaN(d));


	}


}
