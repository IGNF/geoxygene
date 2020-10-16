package fr.ign.cogit.equation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.tools.Utils;
import junit.framework.TestCase;

public class TestApproximatedPlanEquation extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-1);
	
	private static Logger log = LogManager.getLogger(TestApproximatedPlanEquation.class);

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
	// Test for method to build approximated plan equation : 3 points
	// --------------------------------------------------------------------------------
	public void testApproximatedPlanEquation1() {
		
		log.info("Test for method to build approximated plan equation : 3 points");

		// Creating list of points
		DirectPositionList dpl = new DirectPositionList();
		dpl.add(new DirectPosition(0,0,0));
		dpl.add(new DirectPosition(1,1,0));
		dpl.add(new DirectPosition(1,2,0));

		// Creating plane
		ApproximatedPlanEquation apex = new ApproximatedPlanEquation(dpl);

		// Verifications
		double val1 = apex.equationValue(dpl.get(0));
		double val2 = apex.equationValue(dpl.get(1));
		double val3 = apex.equationValue(dpl.get(2));

		// Comparison
		assertEquals("Approximated plane equation does not fit definition points", 0, val1, epsilon);
		assertEquals("Approximated plane equation does not fit definition points", 0, val2, epsilon);
		assertEquals("Approximated plane equation does not fit definition points", 0, val3, epsilon);

	}





	@Test
	// --------------------------------------------------------------------------------
	// Test for method to build approximated plan equation : 1 million coplanar points
	// --------------------------------------------------------------------------------
	public void testApproximatedPlanEquation4() {
		
		log.info("Test for method to build approximated plan equation : 1 million coplanar points");

		// Number of points 
		int n = 1000000;

		// Creating list of points
		DirectPositionList dpl = new DirectPositionList();

		for (int i=0; i<n; i++){

			dpl.add(new DirectPosition(Math.random()*100,Math.random()*100,10));

		}

		// Creating plane
		ApproximatedPlanEquation apex = new ApproximatedPlanEquation(dpl);

		// Comparison

		double val;

		for (int i=0; i<n; i++){

			val = apex.equationValue(dpl.get(0));
			assertEquals("Approximated plane equation does not fit definition points", 0, val, epsilon);

		}

	}



	@Test
	// --------------------------------------------------------------------------------
	// Test for method to build approximated plan equation : surface
	// --------------------------------------------------------------------------------
	public void testApproximatedPlanEquation6() {
		
		log.info("Test for method to build approximated plan equation : surface");

		// Creating surface
		GM_Surface surface = Utils.createSquarePolygon(2, 0, 0);

		// Computing plane equation
		ApproximatedPlanEquation apex = new ApproximatedPlanEquation(surface);

		// Comparison
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffa(), epsilon);
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffb(), epsilon);
		assertEquals("Approximated plane equation is incorrect", 1, Math.abs(apex.getCoeffc()), epsilon);
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffd(), epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to build approximated plan equation : polygon
	// --------------------------------------------------------------------------------
	public void testApproximatedPlanEquation7() {
		
		log.info("Test for method to build approximated plan equation : polygon");

		// Creating surface
		GM_Polygon polygon = Utils.createSquarePolygon(2, 0, 0);

		// Computing plane equation
		ApproximatedPlanEquation apex = new ApproximatedPlanEquation(polygon);

		// Comparison
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffa(), epsilon);
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffb(), epsilon);
		assertEquals("Approximated plane equation is incorrect", 1, Math.abs(apex.getCoeffc()), epsilon);
		assertEquals("Approximated plane equation is incorrect", 0, apex.getCoeffd(), epsilon);

	}


}
