package fr.ign.cogit.calculation;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.sig3d.calculation.BooleanOperators;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.geometry.Sphere;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;

public class TestBooleanOperators extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-1);

	private static Logger log = Logger.getLogger(TestBooleanOperators.class);

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
	// Test for solid cubes intersection
	// --------------------------------------------------------------------------------
	public void testCompute1() {

		log.info("Test for solid cube intersection");

		GM_Solid s = normalCase(BooleanOperators.INTERSECTION);

		// Processing expected result
		GM_Solid sExp= Utils.createTriangulatedCube(5, 5, 5, 5);

		// Comparison
		assertTrue("Output solid from boolean operation is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes intersection (same positions)
	// --------------------------------------------------------------------------------
	public void testCompute2() {

		log.info("Test for solid cubes intersection (same positions)");

		GM_Solid s = samePositions(BooleanOperators.INTERSECTION);

		// Processing expected result
		GM_Solid sExp = Utils.createTriangulatedCube(5, 5, 5, 10);

		// Comparison
		assertTrue("Output solid from boolean operation is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes intersection (empty intersection)
	// --------------------------------------------------------------------------------
	public void testCompute3() {

		log.info("Test for solid cubes intersection (empty intersection)");

		GM_Solid s = emptyIntersection(BooleanOperators.INTERSECTION);

		// Comparison
		assertNull("Output solid from boolean operation is incorrect", s);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes union
	// --------------------------------------------------------------------------------
	public void testCompute4() {

		log.info("Test for solid cubes union");

		DefaultFeature object1 = new DefaultFeature(Utils.createCube(2, 2, 2, 10));
		DefaultFeature object2 = new DefaultFeature(Utils.createCube(5, 5, 5, 1));

		// Processing expected result
		GM_Solid s =  BooleanOperators.compute(object1, object2, BooleanOperators.UNION);

		// Expected solid
		GM_Solid sExp = Utils.createTriangulatedCube(2, 2, 2, 10);

		// Comparison
		assertTrue("Output solid from boolean operation is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes union (same positions)
	// --------------------------------------------------------------------------------
	public void testCompute5() {

		log.info("Test for solid cubes union (same positions)");

		GM_Solid s = samePositions(BooleanOperators.UNION);

		// Processing expected result
		GM_Solid sExp = Utils.createTriangulatedCube(5, 5, 5, 10);

		// Comparison
		assertTrue("Output solid from boolean operation is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes union (empty intersection)
	// --------------------------------------------------------------------------------
	public void testCompute6() {

		log.info("Test for solid cubes union (empty intersection)");

		// Adding geometries
		DefaultFeature object1 = new DefaultFeature(Utils.createCube(0, 0, 0, 1));
		DefaultFeature object2 = new DefaultFeature(Utils.createCube(0, 1, 0, 1));
		DefaultFeature object3 = new DefaultFeature(Utils.createCube(1, 1, 0, 1));
		DefaultFeature object4 = new DefaultFeature(Utils.createCube(1, 0, 0, 1));
		DefaultFeature object5 = new DefaultFeature(Utils.createCube(0, 0, 1, 1));
		DefaultFeature object6 = new DefaultFeature(Utils.createCube(0, 1, 1, 1));
		DefaultFeature object7 = new DefaultFeature(Utils.createCube(1, 1, 1, 1));
		DefaultFeature object8 = new DefaultFeature(Utils.createCube(1, 0, 1, 1));

		// Creating a list
		ArrayList<DefaultFeature> ADF = new ArrayList<DefaultFeature>();
		ADF.add(object2);
		ADF.add(object3);
		ADF.add(object4);
		ADF.add(object5);
		ADF.add(object6);
		ADF.add(object7);
		ADF.add(object8);

		// Processing unions
		GM_Solid s =  BooleanOperators.compute(object1, ADF.get(1), BooleanOperators.UNION);

		for (DefaultFeature df : ADF){s = BooleanOperators.compute(new DefaultFeature(s), df, BooleanOperators.UNION);}

		GM_Solid sExp = Utils.createTriangulatedCube(0, 0, 0, 2);


		// Comparison
		assertTrue("Output solid from boolean operation is incorrect", s.equals(sExp));
	}

	@Test
	// --------------------------------------------------------------------------------
	// Test with Sphere differences
	// --------------------------------------------------------------------------------
	public void testCompute7() {

		log.info("Test with Sphere differences");

		// Creating spheres
		GM_Solid sphere1 = new Sphere(new DirectPosition(0,0,0),10,10);
		GM_Solid sphere2 = new Sphere(new DirectPosition(0,0,0),2,10);

		DefaultFeature df1 = new DefaultFeature(sphere1);
		DefaultFeature df2 = new DefaultFeature(sphere2);

		// Computing difference
		GM_Solid result = BooleanOperators.compute(df1, df2, BooleanOperators.DIFFERENCE);

		// Recovering volume
		double v = Util.volumeTriangulatedSolid(result);

		// Processing expected result
		double vExp =  Util.volumeTriangulatedSolid(sphere1)-Util.volumeTriangulatedSolid(sphere2);

		// Comparison
		assertEquals("Output solid volume from boolean operation is incorrect", vExp, v, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for particular cases
	// --------------------------------------------------------------------------------
	public void testCompute8() {

		log.info("Test for particular cases");

		// Creating spheres
		GM_Solid sphere1 = new Sphere(new DirectPosition(0,0,0),10,10);
		DefaultFeature df1 = new DefaultFeature(sphere1);

		// Computing case
		@SuppressWarnings("unused")
		GM_Solid result1 = BooleanOperators.compute(df1, null, BooleanOperators.DIFFERENCE);
		@SuppressWarnings("unused")
		GM_Solid result2 = BooleanOperators.compute(null, df1, BooleanOperators.DIFFERENCE);
		
		// Comparison
		assertTrue("", true);

	}

	// --------------------------------------------------------------------------------
	// Creating cube
	// --------------------------------------------------------------------------------
	public GM_Solid normalCase(int operation) {

		log.info("Test for solid cubes operation");

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = Utils.createCube(0, 0, 0, 10);
		GM_Solid cube2 = Utils.createCube(5, 5, 5, 10);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		return BooleanOperators.compute(object1, object2, operation);

	}

	// --------------------------------------------------------------------------------
	// Test for solid cubes operation (same positions)
	// --------------------------------------------------------------------------------
	public GM_Solid samePositions(int operation) {

		log.info("Test for solid cubes operation (same positions)");

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = Utils.createCube(5, 5, 5, 10);
		GM_Solid cube2 = Utils.createCube(5, 5, 5, 10);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		return BooleanOperators.compute(object1, object2, operation);

	}

	// --------------------------------------------------------------------------------
	// Test for solid cubes operation (empty intersection)
	// --------------------------------------------------------------------------------
	public GM_Solid emptyIntersection(int operation) {

		log.info("Test for solid cubes operation (empty intersection)");

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = Utils.createCube(5, 5, 5, 1);
		GM_Solid cube2 = Utils.createCube(7, 7, 7, 1);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		return BooleanOperators.compute(object1, object2, operation);

	}

}
