package fr.ign.cogit.equation;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.tools.Utils;

public class TestPlanEquation extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-10);

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
	// Test for method to compute plane equation from 3 points : Oxy plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation1() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Expected result
		double aExp = 0;
		double bExp = 0;
		double cExp = 1; 
		double dExp = 0; 

		// Normalizations
		double somme = aExp+bExp+cExp+dExp;
		aExp = aExp/somme;
		bExp = bExp/somme;
		cExp = cExp/somme;
		dExp = dExp/somme;

		// Comparsion
		assertEquals("a coefficient in plane equation is incorrect", aExp, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", bExp, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", cExp, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", dExp, d, epsilon);


	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 3 points : Oxz plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation2() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,0,1);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Expected result
		double aExp = 0;
		double bExp = 1;
		double cExp = 0; 
		double dExp = 0; 

		// Normalizations
		double somme = a+b+c+d;
		a = a/somme;
		b = b/somme;
		c = c/somme;
		d = d/somme;

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", aExp, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", bExp, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", cExp, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", dExp, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 3 points : Oyz plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation3() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(0,1,0);
		DirectPosition p3 = new DirectPosition(0,0,1);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Expected result
		double aExp = 1;
		double bExp = 0;
		double cExp = 0; 
		double dExp = 0; 

		// Normalizations
		double somme = a+b+c+d;
		a = a/somme;
		b = b/somme;
		c = c/somme;
		d = d/somme;

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", aExp, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", bExp, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", cExp, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", dExp, d, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 3 points : random plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation4() {

		// Creating random points
		DirectPosition p1 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p2 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p3 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Substitution
		double verification1 = a*p1.getX()+b*p1.getY()+c*p1.getZ()+d;
		double verification2 = a*p2.getX()+b*p2.getY()+c*p2.getZ()+d;
		double verification3 = a*p3.getX()+b*p3.getY()+c*p3.getZ()+d;

		// Comparison
		assertEquals("Randomly created plane equation is incorrect", 0, verification1, epsilon);
		assertEquals("Randomly created plane equation is incorrect", 0, verification2, epsilon);
		assertEquals("Randomly created plane equation is incorrect", 0, verification3, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 3 points : colinear case
	// --------------------------------------------------------------------------------
	public void testPlanEquation5() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0, 0, 0);
		DirectPosition p2 = new DirectPosition(1, 1, 1);
		DirectPosition p3 = new DirectPosition(2, 2, 2);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", 0, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", 0, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", 0, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", 0, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 1 point and 1 normal vector
	// --------------------------------------------------------------------------------
	public void testPlanEquation6() {

		// Creating random point
		DirectPosition p = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Creating random normal vector
		Vecteur n = new Vecteur(new DirectPosition(Math.random()*100, Math.random()*100, Math.random()*100));
		n.normalise();

		// Computing equation
		PlanEquation eq = new PlanEquation(n, p);

		// Recovering parameters
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// Expected parameters
		double aExp = n.getX();
		double bExp = n.getY();
		double cExp = n.getZ(); 
		double dExp = -(aExp*p.getX()+bExp*p.getY()+cExp*p.getZ()); 

		// Normalizations
		double somme1 = a+b+c+d;
		a = a/(somme1);
		b = b/(somme1);
		c = c/(somme1);
		d = d/(somme1);

		double somme2 = aExp+bExp+cExp+dExp;
		aExp = aExp/(somme2);
		bExp = bExp/(somme2);
		cExp = cExp/(somme2);
		dExp = dExp/(somme2);

		// Comparisons
		assertEquals("a coefficient in plane equation is incorrect", aExp, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", bExp, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", cExp, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", dExp, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from 1 point and 1 null vector
	// --------------------------------------------------------------------------------
	public void testPlanEquation7() {

		// Creating random point
		DirectPosition p = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Creating random normal vector
		Vecteur n = new Vecteur(0,0,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(n, p);

		// Recovering parameters
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd(); 

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", Double.NaN, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", Double.NaN, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", Double.NaN, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", Double.NaN, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from surface
	// --------------------------------------------------------------------------------
	public void testPlanEquation8() {

		// Creating surface
		GM_Surface surface = Utils.createSquarePolygon(2, 0, 0);

		// Computing equation
		PlanEquation eq = new PlanEquation(surface);

		// recovering parameters
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd(); 

		// Normalizations
		double somme = a+b+c+d;
		a = a/somme;
		b = b/somme;
		c = c/somme;
		d = d/somme;

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", 0, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", 0, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", 1, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", 0, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute plane equation from list of points
	// --------------------------------------------------------------------------------
	public void testPlanEquation9() {

		// Creating list of points
		DirectPositionList dpl = new DirectPositionList();

		// Filling list with random coordinates
		for (int i=0; i<10; i++){

			dpl.add(new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100));

		}

		// Computing equation
		PlanEquation eq = new PlanEquation(dpl);

		// recovering parameters
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd(); 

		// Expected results
		PlanEquation eq2 = new PlanEquation(dpl.get(0), dpl.get(1), dpl.get(2));
		double aExp = eq2.getCoeffa();
		double bExp = eq2.getCoeffb();
		double cExp = eq2.getCoeffc();
		double dExp = eq2.getCoeffd(); 

		// Normalizations
		double somme1 = a+b+c+d;
		a = a/somme1;
		b = b/somme1;
		c = c/somme1;
		d = d/somme1;

		double somme2 = aExp+bExp+cExp+dExp;
		aExp = aExp/somme2;
		bExp = bExp/somme2;
		cExp = cExp/somme2;
		dExp = dExp/somme2;

		// Comparison
		assertEquals("a coefficient in plane equation is incorrect", aExp, a, epsilon);
		assertEquals("b coefficient in plane equation is incorrect", bExp, b, epsilon);
		assertEquals("c coefficient in plane equation is incorrect", cExp, c, epsilon);
		assertEquals("d coefficient in plane equation is incorrect", dExp, d, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute equation from a plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation10() {

		// Creating random points
		DirectPosition p1 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p2 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p3 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Substitution
		double verification1 = eq.equationValue(p1);
		double verification2 = eq.equationValue(p2);
		double verification3 = eq.equationValue(p3);

		// Comparison
		assertEquals("Randomly created plane equation is incorrect", 0, verification1, epsilon);
		assertEquals("Randomly created plane equation is incorrect", 0, verification2, epsilon);
		assertEquals("Randomly created plane equation is incorrect", 0, verification3, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute sign from a plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation11() {

		// Creating random points
		DirectPosition p1 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p2 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);
		DirectPosition p3 = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering parameteres
		double a = eq.getCoeffa();
		double b = eq.getCoeffb();
		double c = eq.getCoeffc();
		double d = eq.getCoeffd();

		// New random point
		DirectPosition p = new DirectPosition(Math.random()*100,Math.random()*100,Math.random()*100);

		// Recovering result
		double s = eq.equationSignum(p);

		// Expected result
		double variable = a*p.getX()+b*p.getY()+c*p.getZ()+d;
		double sExp = (Double) ((variable != 0) ? (variable)/Math.abs(variable) : 0);

		// Comparison
		assertEquals("Randomly created plane equation is incorrect", sExp, s, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection on a plane (Oxy plane)
	// --------------------------------------------------------------------------------
	public void testPlanEquation12() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating line
		DirectPosition p4 = new DirectPosition(0,2,2);
		DirectPosition p5 = new DirectPosition(0,-2,-2);

		// Processing intersection
		DirectPosition p = (DirectPosition) eq.intersectionLinePlan(p4, p5);

		// Comparison
		assertEquals("Intersection coordinate X between line and plane is incorrect", 0, p.getX(), epsilon);
		assertEquals("Intersection coordinate Y between line and plane is incorrect", 0, p.getY(), epsilon);
		assertEquals("Intersection coordinate Z between line and plane is incorrect", 0, p.getZ(), epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection on a plane : case "parallel"
	// --------------------------------------------------------------------------------
	public void testPlanEquation13() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating line
		DirectPosition p4 = new DirectPosition(0,2,2);
		DirectPosition p5 = new DirectPosition(0,-2,2);

		// Processing intersection
		DirectPosition p = (DirectPosition) eq.intersectionLinePlan(p4, p5);

		// Comparison
		assertNull("Intersections should be null", p);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection between plane and half line
	// --------------------------------------------------------------------------------
	public void testPlanEquation14() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating line
		DirectPosition p4 = new DirectPosition(0,0,4);
		DirectPosition p5 = new DirectPosition(0,0,2);

		// Processing intersections
		boolean bool1 =  eq.intersecteHalfLinePlan(p4, p5, 4.1);
		boolean bool2 =  eq.intersecteHalfLinePlan(p4, p5, 3.9);

		// Comparison
		assertTrue("Half line and plane should intersect", bool1);
		assertFalse("Half line and plane should not intersect", bool2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection between plane and half line
	// --------------------------------------------------------------------------------
	public void testPlanEquation15() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Line origin
		DirectPosition p4 = new DirectPosition(0,0,4);

		// Line direction
		Vecteur direction = new Vecteur(0,0,-1);

		// Processing intersections
		boolean bool1 =  eq.intersectionLinePlan(p4, direction, 4.1);
		boolean bool2 =  eq.intersectionLinePlan(p4, direction, 3.9);

		// Comparison
		assertTrue("Half line and plane should intersect", bool1);
		assertFalse("Half line and plane should not intersect", bool2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection between plane and half line (parallel)
	// --------------------------------------------------------------------------------
	public void testPlanEquation16() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating lines
		DirectPosition p4 = new DirectPosition(0,2,2);
		DirectPosition p5 = new DirectPosition(0,4,2);
		DirectPosition p6 = new DirectPosition(0,2,0);
		DirectPosition p7 = new DirectPosition(0,4,0);


		// Processing intersections
		boolean bool1 =  eq.intersecteHalfLinePlan(p6, p7, 100);
		boolean bool2 =  eq.intersecteHalfLinePlan(p4, p5, 100);

		// Comparison
		assertTrue("Half line and plane should intersect", bool1);
		assertFalse("Half line and plane should not intersect", bool2);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection point between plane and half line
	// --------------------------------------------------------------------------------
	public void testPlanEquation17() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,2);
		DirectPosition p2 = new DirectPosition(1,0,2);
		DirectPosition p3 = new DirectPosition(0,1,2);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Line origin
		DirectPosition p4 = new DirectPosition(3,3,4);

		// Line direction
		Vecteur direction = new Vecteur(0,0,-1);

		// Processing intersections
		DirectPosition pi =  (DirectPosition) eq.intersectionHalfLinePlan(p4, direction);

		// Computing expected result
		DirectPosition piExp = new DirectPosition(3,3,2);

		// Comparison
		assertTrue("Intersection point coordinates are incorrect", pi.equals(piExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute intersection point between plane and line
	// --------------------------------------------------------------------------------
	public void testPlanEquation18() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,2);
		DirectPosition p2 = new DirectPosition(1,0,2);
		DirectPosition p3 = new DirectPosition(0,1,2);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Line origin
		DirectPosition p4 = new DirectPosition(3,3,4);

		// Line direction
		Vecteur direction = new Vecteur(0,1,0);

		// Processing intersections
		DirectPosition pi =  (DirectPosition) eq.intersectionHalfLinePlan(p4, direction);

		// Comparison
		assertNull("There should be no intesection", pi);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to project a 2D point on a plane
	// --------------------------------------------------------------------------------
	public void testPlanEquation19() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,3,2);
		DirectPosition p2 = new DirectPosition(1,0,2);
		DirectPosition p3 = new DirectPosition(12,1,2);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// New point
		DirectPosition p0 = new DirectPosition(3,3);

		// Projection
		double alti = eq.getZ(p0);

		// Projected point
		DirectPosition pp = new DirectPosition(p0.getX(), p0.getY(), alti);

		// Test
		double valTest = eq.equationValue(pp);

		// Comparsion
		assertEquals("Projected point should be located on plane", 0, valTest, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between plane and triangle
	// --------------------------------------------------------------------------------
	public void testPlanEquation20() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating triangle

		DirectPosition pt1 = new DirectPosition(0,0,10);
		DirectPosition pt2 = new DirectPosition(-5,-5,-10);
		DirectPosition pt3 = new DirectPosition(5,5,-10);

		GM_Triangle triangle = new GM_Triangle(pt1, pt2, pt3);

		// Processing intersection
		GM_LineString line = (GM_LineString) eq.triangleIntersection(triangle);

		// Expected result
		GM_LineString lineExp = new GM_LineString();
		lineExp.addControlPoint(new DirectPosition(-2.5,-2.5,0));
		lineExp.addControlPoint(new DirectPosition(2.5,2.5,0));

		// Comparison
		assertTrue("Triangle intersection with plane is incorrect", line.equals(lineExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between plane and triangle
	// --------------------------------------------------------------------------------
	public void testPlanEquation21() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating triangle

		DirectPosition pt1 = new DirectPosition(0,0,15);
		DirectPosition pt2 = new DirectPosition(-5,-5,10);
		DirectPosition pt3 = new DirectPosition(5,5,10);

		GM_Triangle triangle = new GM_Triangle(pt1, pt2, pt3);

		// Processing intersection
		GM_LineString line = (GM_LineString) eq.triangleIntersection(triangle);

		// Comparison
		assertNull("There should be no intersection", line);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process intersection between plane and triangle
	// --------------------------------------------------------------------------------
	public void testPlanEquation22() {

		// NEEDS FIXING METHOD triangleIntersection

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(1,0,0);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating triangle vertices

		DirectPosition pt1 = new DirectPosition(0,0,0);
		DirectPosition pt2 = new DirectPosition(1,1,0);
		DirectPosition pt3 = new DirectPosition(0,1,0);

		// Creating trinagle
		GM_Triangle triangle = new GM_Triangle(pt1, pt2, pt3);

		@SuppressWarnings("unused")
		GM_LineString line = (GM_LineString) eq.triangleIntersection(triangle);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process equation value : 
	// BE CAREFUL, REDUDANCY with equation Value Method !!!
	// --------------------------------------------------------------------------------
	public void testPlanEquation23() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(0,0,1);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation (x = 0)
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating random point
		double X = Math.random()*100;
		double Y = Math.random()*100;
		double Z = Math.random()*100;
		DirectPosition p = new DirectPosition(X,Y,Z);

		// Checking if point is satisfying equation
		double val = eq.positionPointInPlan(p);

		// Processing expected value
		double valExp = eq.getCoeffa()*X+eq.getCoeffb()*Y+eq.getCoeffc()*Z+eq.getCoeffd();

		// Comparison
		assertEquals("Equation value on point is incorrect", valExp, val, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to return plane normal vector
	// --------------------------------------------------------------------------------
	public void testPlanEquation24() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(0,0,1);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation (x = 0)
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Recovering normal vector
		Vecteur n = eq.getNormale();

		// Expected normal
		Vecteur nExp = new Vecteur(1,0,0);

		// Comparison
		assertEquals("Normal vector X coordinate is incorrect", Math.abs(n.getX()), Math.abs(nExp.getX()), epsilon);
		assertEquals("Normal vector Y coordinate is incorrect", Math.abs(n.getY()), Math.abs(nExp.getY()), epsilon);
		assertEquals("Normal vector Z coordinate is incorrect", Math.abs(n.getZ()), Math.abs(nExp.getZ()), epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to return orthogonal projection
	// --------------------------------------------------------------------------------
	public void testPlanEquation25() {

		// Creating points
		DirectPosition p1 = new DirectPosition(0,0,0);
		DirectPosition p2 = new DirectPosition(0,0,1);
		DirectPosition p3 = new DirectPosition(0,1,0);

		// Computing equation (x = 0)
		PlanEquation eq = new PlanEquation(p1,p2,p3);

		// Creating point
		DirectPosition p = new DirectPosition(10,10,10);
		
		// Processing projection
		DirectPosition pp = (DirectPosition) eq.normalCasting(p);
		
		// Expected result
		DirectPosition ppExp = new DirectPosition(0,10,10);
		
		// Comparison
		assertTrue("Orthogonal projection coordinates are incorrect", pp.equals(ppExp));
		
	}

}
