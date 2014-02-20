package fr.ign.cogit.geometry;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;



public class TestBox3D extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	// private static double epsilon = Math.pow(10,-1);
	
	private static Logger log = Logger.getLogger(TestBox3D.class);

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
		
		log.info("Test for method to get box center from extremal coordinates");

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
		
		log.info("Test for method to process bounding box from geometry");

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
		
		log.info("Test for method to process bounding box from list of points");

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

		log.info("Test for method to process intersection between boxes");
		
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

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between boxes
	// --------------------------------------------------------------------------------
	public void testBox3D5() {
		
		log.info("Test for method to process intersection between boxes");

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
		Box3D boxI1 = box1.intersection(box2);
		Box3D boxI2 = box1.intersection(box3);

		// Recovering results
		DirectPosition pminI1 = (DirectPosition) boxI1.getLLDP();
		DirectPosition pmaxI1 = (DirectPosition) boxI1.getURDP();

		// Expected results
		DirectPosition pminI1Exp = new DirectPosition(5,5,5);
		DirectPosition pmaxI1Exp = new DirectPosition(10,20,30);

		// Comparison
		assertTrue("Bounding boxes intersection is incorrect", pminI1.equals(pminI1Exp));
		assertTrue("Bounding boxes intersection is incorrect", pmaxI1.equals(pmaxI1Exp));
		assertNull("Bounding boxes intersection is incorrect", boxI2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process union between two boxes : standard case
	// --------------------------------------------------------------------------------
	public void testBox3D6() {
		
		log.info("Test for method to process union between two boxes : standard case");

		// Creating boxes
		DirectPosition pmin1 = new DirectPosition(0,0,0);
		DirectPosition pmax1 = new DirectPosition(10,20,30);
		DirectPosition pmin2 = new DirectPosition(5,5,5);
		DirectPosition pmax2 = new DirectPosition(100,100,100);

		Box3D box1 = new Box3D(pmin1, pmax1);
		Box3D box2 = new Box3D(pmin2, pmax2);

		// Processing union
		Box3D boxI1 = box1.union(box2);


		// Recovering results
		DirectPosition pminI1 = (DirectPosition) boxI1.getLLDP();
		DirectPosition pmaxI1 = (DirectPosition) boxI1.getURDP();

		// Expected results
		DirectPosition pminI1Exp = new DirectPosition(0,0,0);
		DirectPosition pmaxI1Exp = new DirectPosition(100,100,100);

		// Comparison
		assertTrue("Bounding boxes union is incorrect", pminI1.equals(pminI1Exp));
		assertTrue("Bounding boxes union is incorrect", pmaxI1.equals(pmaxI1Exp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process union between two boxes : no intersection case
	// --------------------------------------------------------------------------------
	public void testBox3D7() {
		
		log.info("Test for method to process union between two boxes : no intersection case");

		// Creating boxes
		DirectPosition pmin1 = new DirectPosition(0,0,0);
		DirectPosition pmax1 = new DirectPosition(10,20,30);
		DirectPosition pmin2 = new DirectPosition(50,50,50);
		DirectPosition pmax2 = new DirectPosition(100,100,100);

		Box3D box1 = new Box3D(pmin1, pmax1);
		Box3D box2 = new Box3D(pmin2, pmax2);

		// Processing union
		Box3D boxI1 = box1.union(box2);


		// Recovering results
		DirectPosition pminI1 = (DirectPosition) boxI1.getLLDP();
		DirectPosition pmaxI1 = (DirectPosition) boxI1.getURDP();

		// Expected results
		DirectPosition pminI1Exp = new DirectPosition(0,0,0);
		DirectPosition pmaxI1Exp = new DirectPosition(100,100,100);

		// Comparison
		assertTrue("Bounding boxes union is incorrect", pminI1.equals(pminI1Exp));
		assertTrue("Bounding boxes union is incorrect", pmaxI1.equals(pmaxI1Exp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process union between two boxes : same boxes case
	// --------------------------------------------------------------------------------
	public void testBox3D8() {
		
		log.info("Test for method to process union between two boxes : same boxes case");

		// Creating boxes
		DirectPosition pmin1 = new DirectPosition(0,0,0);
		DirectPosition pmax1 = new DirectPosition(10,20,30);

		Box3D box1 = new Box3D(pmin1, pmax1);

		// Processing union
		Box3D boxI1 = box1.union(box1);


		// Recovering results
		DirectPosition pminI1 = (DirectPosition) boxI1.getLLDP();
		DirectPosition pmaxI1 = (DirectPosition) boxI1.getURDP();

		// Expected results
		DirectPosition pminI1Exp = new DirectPosition(0,0,0);
		DirectPosition pmaxI1Exp = new DirectPosition(10,20,30);

		// Comparison
		assertTrue("Bounding boxes union is incorrect", pminI1.equals(pminI1Exp));
		assertTrue("Bounding boxes union is incorrect", pmaxI1.equals(pmaxI1Exp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process polygon footprint
	// --------------------------------------------------------------------------------
	public void testBox3D9() {
		
		log.info("Test for method to process polygon footprint");

		// Creating boxes
		DirectPosition pmin1 = new DirectPosition(0,0,0);
		DirectPosition pmax1 = new DirectPosition(10,20,30);

		Box3D box = new Box3D(pmin1, pmax1);

		// Processing footprint
		GM_Polygon footprint = (GM_Polygon) box.to_2D();
		
		// Expected result
		String s = "POLYGON ((0.0 0.0 0.0, 10.0 0.0 0.0, 10.0 20.0 0.0, 0.0 20.0 0.0, 0.0 0.0 0.0))";
		
		// Comparison
		assertEquals("Bounding box footprint is incorrect", s, footprint.toString());

	}

}
