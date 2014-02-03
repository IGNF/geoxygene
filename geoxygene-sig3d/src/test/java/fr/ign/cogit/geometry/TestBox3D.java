package fr.ign.cogit.geometry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;



public class TestBox3D extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	// private static double epsilon = Math.pow(10,-1);

	// ---------------------------------- PREPROCESS ----------------------------------

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// ------------------------------------ TESTS -------------------------------------


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to get box center from extremal coordinates
	// --------------------------------------------------------------------------------
	public void testBox3D1() {

		// Building box
		DirectPosition pmin = new DirectPosition(10,10,10);
		DirectPosition pmax = new DirectPosition(15,20,12);
		Box3D box = new Box3D(pmin, pmax);

		// Getting center
		DirectPosition pc = (DirectPosition) box.getCenter();

		// Expected result
		DirectPosition pcExp = new DirectPosition(12.5,15,11);

		// Comparison
		assertTrue("Box center coordinates are incorrect", pc.equals(pcExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process bounding box from geometry
	// --------------------------------------------------------------------------------
	public void testBox3D2() {

		// Creating cube
		GM_Solid cube = Utils.createCube(0, 0, 0, 10);

		// Computing bounding box
		Box3D box = new Box3D(cube);

		// Recovering extremal points
		DirectPosition pmin = (DirectPosition) box.getLLDP();
		DirectPosition pmax = (DirectPosition) box.getURDP();

		// Expected result
		DirectPosition pminExp = new DirectPosition(0,0,0);
		DirectPosition pmaxExp = new DirectPosition(10,10,10);

		// Comparison
		assertTrue("Bounding box is incorrect", pmin.equals(pminExp));
		assertTrue("Bounding box is incorrect", pmax.equals(pmaxExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process bounding box from list of points
	// --------------------------------------------------------------------------------
	public void testBox3D3() {

		// Creating list of random points
		DirectPositionList dpl = new DirectPositionList();

		for (int i=0; i<100; i++){

			dpl.add(new DirectPosition(Math.random()*100, Math.random()*100, Math.random()*100));

		}

		// Creating bounding box
		Box3D box = new Box3D(dpl);

		// Recovering parameters
		DirectPosition pmin = (DirectPosition) box.getLLDP();
		DirectPosition pmax = (DirectPosition) box.getURDP();

		// Processing expected parameters
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE; 
		double zmin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE; 
		double ymax = Double.MIN_VALUE; 
		double zmax = Double.MIN_VALUE;

		for (int i=0; i<dpl.size(); i++){

			xmin = Math.min(xmin, dpl.get(i).getX());
			ymin = Math.min(ymin, dpl.get(i).getY());
			zmin = Math.min(zmin, dpl.get(i).getZ());
			xmax = Math.max(xmax, dpl.get(i).getX());
			ymax = Math.max(ymax, dpl.get(i).getY());
			zmax = Math.max(zmax, dpl.get(i).getZ());

		}

		DirectPosition pminExp = new DirectPosition(xmin,ymin,zmin);
		DirectPosition pmaxExp = new DirectPosition(xmax,ymax,zmax);

		// Comparison
		assertTrue("Bounding box is incorrect", pmin.equals(pminExp));
		assertTrue("Bounding box is incorrect", pmax.equals(pmaxExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between boxes
	// --------------------------------------------------------------------------------
	public void testBox3D4() {
		
		// Creating boxes
		DirectPosition pmin1 = new DirectPosition(0,0,0);
		DirectPosition pmax1 = new DirectPosition(10,20,30);
		DirectPosition pmin2 = new DirectPosition(5,5,5);
		DirectPosition pmax2 = new DirectPosition(100,100,100);
		DirectPosition pmin3 = new DirectPosition(20,30,40);
		DirectPosition pmax3 = new DirectPosition(100,100,100);
		
		Box3D box1 = new Box3D(pmin1, pmax1);
		Box3D box2 = new Box3D(pmin2, pmax2);
		Box3D box3 = new Box3D(pmin3, pmax3);
		
		// Processing intersection
		boolean b1 = box1.intersect(box2);
		boolean b2 = box1.intersect(box3);
		
		// Comparison
		assertTrue("Intersection is not empty",b1);
		assertFalse("Intersection is empty",b2);
		
	}


}
