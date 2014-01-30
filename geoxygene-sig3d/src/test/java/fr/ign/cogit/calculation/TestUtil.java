package fr.ign.cogit.calculation;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;


public class TestUtil extends TestCase {


	// ---------------------------------- ATTRIBUTES ----------------------------------

	private double epsilon = Math.pow(10, -10);    // Scale error

	// ------------------------------------ TESTS -------------------------------------


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of a set of points
	// --------------------------------------------------------------------------------
	public void testCenterOf1() {

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
		assertTrue("Points set center of gravity is incorrect", gExp.equals(g));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of an empty set of points
	// --------------------------------------------------------------------------------
	public void testCenterOf2() {

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

		// Creating list of surfaces
		ArrayList<GM_Surface> AS = new ArrayList<GM_Surface>();

		// Detecting vertical surface
		GM_MultiSurface<IOrientableSurface> MS = (GM_MultiSurface<IOrientableSurface>) Util.detectRoof(AS, epsilon);

		// Comparison
		assertNull("Wrong number of roof surfaces has been detected", MS);

	}

}
