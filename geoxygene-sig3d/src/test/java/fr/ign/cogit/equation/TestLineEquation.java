package fr.ign.cogit.equation;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class TestLineEquation extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-1);
	
	private static Logger log = Logger.getLogger(TestLineEquation.class);

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
	// Test for method to build line equation from point and vector
	// --------------------------------------------------------------------------------
	public void testLineEquation1() {
		
		log.info("Test for method to build line equation from point and vector");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,1,2)).getNormalised();

		// Creating line equation
		LineEquation eq = new LineEquation(p, v); 

		// Comparisons
		assertEquals("Line equation a0 parameter is incorrect", p.getX(), eq.getA0(), epsilon);
		assertEquals("Line equation b0 parameter is incorrect", p.getY(), eq.getB0(), epsilon);
		assertEquals("Line equation c0 parameter is incorrect", p.getZ(), eq.getC0(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", v.getX(), eq.getA1(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", v.getY(), eq.getB1(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", v.getZ(), eq.getC1(), epsilon);	

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to build line equation from two points
	// --------------------------------------------------------------------------------
	public void testLineEquation2() {
		
		log.info("Test for method to build line equation from two points");

		// Creating points
		DirectPosition p1 = new DirectPosition(10,10,10);
		DirectPosition p2 = new DirectPosition(11,11,12);

		// Creating line equation
		LineEquation eq = new LineEquation(p1, p2); 

		// Comparisons
		assertEquals("Line equation a0 parameter is incorrect", p1.getX(), eq.getA0(), epsilon);
		assertEquals("Line equation b0 parameter is incorrect", p1.getY(), eq.getB0(), epsilon);
		assertEquals("Line equation c0 parameter is incorrect", p1.getZ(), eq.getC0(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", p2.getX()-p1.getX(), eq.getA1(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", p2.getY()-p1.getY(), eq.getB1(), epsilon);
		assertEquals("Line equation a1 parameter is incorrect", p2.getZ()-p1.getZ(), eq.getC1(), epsilon);	

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process value at t
	// --------------------------------------------------------------------------------
	public void testLineEquation3() {
		
		log.info("Test for method to process value at t");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,1,2)).getNormalised();

		// Creating line equation
		LineEquation eq = new LineEquation(p, v); 

		// Computing value at 0 and 2
		DirectPosition p0 = (DirectPosition) eq.valueAt(0);
		DirectPosition p2 = (DirectPosition) eq.valueAt(2);

		// Processing expected result

		double x = p.getX()+2*v.getX();
		double y = p.getY()+2*v.getY();
		double z = p.getZ()+2*v.getZ();

		DirectPosition p2Exp = new DirectPosition(x,y,z);

		// Comparisons
		assertTrue("Line equation value at 0 is incorrect", p0.equals(p));
		assertTrue("Line equation value at 2 is incorrect", p2.equals(p2Exp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to know if a point belongs to a line
	// --------------------------------------------------------------------------------
	public void testLineEquation4() {
		
		log.info("Test for method to know if a point belongs to a line");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,1,2)).getNormalised();

		// Creating line equation
		LineEquation eq = new LineEquation(p, v); 

		// Creating points
		DirectPosition p1 = new DirectPosition(9,9,8);
		DirectPosition p2 = new DirectPosition(9,9,7);

		// Processing method
		boolean b1 = eq.isPointOnLine(p1);
		boolean b2 = eq.isPointOnLine(p2);

		// Comparisons
		assertTrue("Point should satisfy line equation", b1);
		assertFalse("Point should not satisfy line equation", b2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection with a plane
	// --------------------------------------------------------------------------------
	public void testLineEquation5() {
		
		log.info("Test for method to process intersection with a plane");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,0,1)).getNormalised();

		// Creating line equation
		LineEquation eqL = new LineEquation(p, v); 

		// Creating plan
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eqP = new PlanEquation(p1,p2,p3);

		// Processing intersection
		DirectPosition pi = (DirectPosition) eqL.intersectionLinePlan(eqP);

		// Expected intersection
		DirectPosition piExp = new DirectPosition(0,10,0);

		// Comparison
		assertTrue("Intersection between line and point is incorrect", pi.equals(piExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection with a plane (parallel case)
	// --------------------------------------------------------------------------------
	public void testLineEquation6() {
		
		log.info("Test for method to process intersection with a plane (parallel case)");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,1,0)).getNormalised();

		// Creating line equation
		LineEquation eqL = new LineEquation(p, v); 

		// Creating plan
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eqP = new PlanEquation(p1,p2,p3);

		// Processing intersection
		DirectPosition pi = (DirectPosition) eqL.intersectionLinePlan(eqP);

		// Comparison
		assertNull("There should be no intersection in parallel case", pi);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process distance between a line and a point
	// --------------------------------------------------------------------------------
	public void testLineEquation7() {
		
		log.info("Test for method to process distance between a line and a point");

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);

		// Creating vector
		Vecteur v = (new Vecteur(1,1,0)).getNormalised();

		// Creating line equation
		LineEquation eq = new LineEquation(p, v); 

		// Creating points
		DirectPosition p1 = new DirectPosition(11,11,10);
		DirectPosition p2 = new DirectPosition(12,11,10);

		// Processing distances
		double d1 = eq.distance(p1);
		double d2 = eq.distance(p2);

		// Expected result
		double d2Exp = Math.sqrt(2)/2;

		// Comparison
		assertEquals("Distance between line and point is incorrect", 0, d1, epsilon);
		assertEquals("Distance between line and point is incorrect", d2Exp, d2, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between two lines
	// --------------------------------------------------------------------------------
	public void testLineEquation8() {
		
		log.info("Test for method to process intersection between two lines");

		// Creating random point

		double x = Math.random()*100;
		double y = Math.random()*100;
		double z = Math.random()*100;

		DirectPosition p = new DirectPosition(x,y,z);


		// Creating random vectors

		double x1 = Math.random()*100;
		double y1 = Math.random()*100;
		double z1 = Math.random()*100;

		double x2 = Math.random()*100;
		double y2 = Math.random()*100;
		double z2 = Math.random()*100;

		Vecteur v1 = (new Vecteur(x1,y1,z1)).getNormalised();
		Vecteur v2 = (new Vecteur(x2,y2,z2)).getNormalised();

		// Creating line equations
		LineEquation eq1 = new LineEquation(p, v1); 
		LineEquation eq2 = new LineEquation(p, v2); 

		// Processing intersection
		DirectPosition pi = (DirectPosition) eq1.intersectionLineLine(eq2);

		// Comparison
		assertTrue("Intersection between lines is incorrect", pi.equals(p));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between two lines (parallel case)
	// --------------------------------------------------------------------------------
	public void testLineEquation9() {
		
		log.info("Test for method to process intersection between two lines (parallel case)");

		// Creating random points

		double x = Math.random()*100;
		double y = Math.random()*100;
		double z = Math.random()*100;

		DirectPosition p1 = new DirectPosition(x,y,z);
		DirectPosition p2 = new DirectPosition(x+1,y+1,z+1);


		// Creating random vector

		double x1 = Math.random()*100;
		double y1 = Math.random()*100;
		double z1 = Math.random()*100;

		Vecteur v = (new Vecteur(x1,y1,z1)).getNormalised();
	
		// Creating line equations
		LineEquation eq1 = new LineEquation(p1, v); 
		LineEquation eq2 = new LineEquation(p2, v); 

		// Processing intersection
		DirectPosition pi = (DirectPosition) eq1.intersectionLineLine(eq2);
		
		// Comparison
		assertNull("Intersection between lines is incorrect", pi);

	}

}
