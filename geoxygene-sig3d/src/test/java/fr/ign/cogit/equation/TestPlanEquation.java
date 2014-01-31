package fr.ign.cogit.equation;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
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
		aExp = aExp/(aExp+bExp+cExp+dExp);
		bExp = bExp/(aExp+bExp+cExp+dExp);
		cExp = cExp/(aExp+bExp+cExp+dExp);
		dExp = dExp/(aExp+bExp+cExp+dExp);

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
		a = a/(a+b+c+d);
		b = b/(a+b+c+d);
		c = c/(a+b+c+d);
		d = d/(a+b+c+d);

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
		a = a/(a+b+c+d);
		b = b/(a+b+c+d);
		c = c/(a+b+c+d);
		d = d/(a+b+c+d);

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
		a = a/(a+b+c+d);
		b = b/(a+b+c+d);
		c = c/(a+b+c+d);
		d = d/(a+b+c+d);

		aExp = aExp/(aExp+bExp+cExp+dExp);
		bExp = bExp/(aExp+bExp+cExp+dExp);
		cExp = cExp/(aExp+bExp+cExp+dExp);
		dExp = dExp/(aExp+bExp+cExp+dExp);

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
		a = a/(a+b+c+d);
		b = b/(a+b+c+d);
		c = c/(a+b+c+d);
		d = d/(a+b+c+d);

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
		a = a/(a+b+c+d);
		b = b/(a+b+c+d);
		c = c/(a+b+c+d);
		d = d/(a+b+c+d);

		aExp = aExp/(aExp+bExp+cExp+dExp);
		bExp = bExp/(aExp+bExp+cExp+dExp);
		cExp = cExp/(aExp+bExp+cExp+dExp);
		dExp = dExp/(aExp+bExp+cExp+dExp);

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





}
