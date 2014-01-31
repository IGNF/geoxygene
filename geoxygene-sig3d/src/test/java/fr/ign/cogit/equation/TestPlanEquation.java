package fr.ign.cogit.equation;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

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

		// Creating random points
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



}
