package fr.ign.cogit.geometry;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import junit.framework.TestCase;



public class TestTriangle extends TestCase {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-5);

	private static Logger log = Logger.getLogger(TestVertex.class);

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
	// Test for method to create triangle
	// --------------------------------------------------------------------------------
	public void testTriangle1() {

		log.info("Test for method to create triangle");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(10, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Tests
		assertTrue("Triangle vertex 1 coordinate X is incorrect", triangle.getCorners(0).getDirect().getX() == 0);
		assertTrue("Triangle vertex 1 coordinate Y is incorrect", triangle.getCorners(0).getDirect().getY() == 0);
		assertTrue("Triangle vertex 1 coordinate Z is incorrect", triangle.getCorners(0).getDirect().getZ() == 0);

		assertTrue("Triangle vertex 2 coordinate X is incorrect", triangle.getCorners(1).getDirect().getX() == 10);
		assertTrue("Triangle vertex 2 coordinate Y is incorrect", triangle.getCorners(1).getDirect().getY() == 0);
		assertTrue("Triangle vertex 2 coordinate Z is incorrect", triangle.getCorners(1).getDirect().getZ() == 0);

		assertTrue("Triangle vertex 3 coordinate X is incorrect", triangle.getCorners(2).getDirect().getX() == 10);
		assertTrue("Triangle vertex 3 coordinate Y is incorrect", triangle.getCorners(2).getDirect().getY() == 10);
		assertTrue("Triangle vertex 3 coordinate Z is incorrect", triangle.getCorners(2).getDirect().getZ() == 0);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process plane equation from triangle
	// --------------------------------------------------------------------------------
	public void testTriangle2() {

		log.info("Test for method to process plane equation from triangle");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(10, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing plane equation
		PlanEquation pEq = triangle.getPlanEquation();

		// Tests
		assertTrue("Vertex 1 should be included in plane", pEq.equationValue(VLIST[0]) == 0);
		assertTrue("Vertex 2 should be included in plane", pEq.equationValue(VLIST[1]) == 0);
		assertTrue("Vertex 3 should be included in plane", pEq.equationValue(VLIST[2]) == 0);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process triangle center
	// --------------------------------------------------------------------------------
	public void testTriangle3() {

		log.info("Test for method to process process triangle center");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing center
		DirectPosition dp = (DirectPosition) triangle.getCenter();

		// Tests
		assertEquals("Triangle center X coordinate is incorrect", dp.getX(), 5, epsilon);
		assertEquals("Triangle center Y coordinate is incorrect", dp.getY(), 10.0/3.0, epsilon);
		assertEquals("Triangle center Z coordinate is incorrect", dp.getZ(), 0, epsilon);

	}

	// --------------------------------------------------------------------------------
	// Test for method to process orientable surface
	// --------------------------------------------------------------------------------
	public void testTriangle4() {

		log.info("Test for method to process orientable surface");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing orientable surface
		GM_Surface surface = (GM_Surface) triangle.toGeoxygeneSurface();

		// Tests
		assertEquals("Created surface area is incorrect", 50, surface.area(), epsilon);
		assertTrue("Created surface centroid is incorrect", surface.centroid().equals(triangle.centroid()));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to translate triangle
	// --------------------------------------------------------------------------------
	public void testTriangle5() {

		log.info("Test for method to translate triangle");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Translating triangle
		triangle = triangle.translateTriangle(1, 1, 1);

		// Expected result
		DirectPosition pt1 = new DirectPosition(1, 1, 1);
		DirectPosition pt2 = new DirectPosition(11, 1, 1);
		DirectPosition pt3 = new DirectPosition(6, 11, 1);

		// Tests
		assertTrue("Translated triangle vertex 1 is incorrect", triangle.getCorners(0).getDirect().equals(pt1));
		assertTrue("Translated triangle vertex 2 is incorrect", triangle.getCorners(1).getDirect().equals(pt2));
		assertTrue("Translated triangle vertex 3 is incorrect", triangle.getCorners(2).getDirect().equals(pt3));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to translate triangle from vertex
	// --------------------------------------------------------------------------------
	public void testTriangle6() {

		log.info("Test for method to translate triangle from vertex");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Translating triangle
		triangle = triangle.translateTriangle(new Vertex(1, 1, 1));

		// Expected result
		DirectPosition pt1 = new DirectPosition(1, 1, 1);
		DirectPosition pt2 = new DirectPosition(11, 1, 1);
		DirectPosition pt3 = new DirectPosition(6, 11, 1);

		// Tests
		assertTrue("Translated triangle vertex 1 is incorrect", triangle.getCorners(0).getDirect().equals(pt1));
		assertTrue("Translated triangle vertex 2 is incorrect", triangle.getCorners(1).getDirect().equals(pt2));
		assertTrue("Translated triangle vertex 3 is incorrect", triangle.getCorners(2).getDirect().equals(pt3));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process triangle edges
	// --------------------------------------------------------------------------------
	public void testTriangle7() {

		log.info("Test for method to process triangle edges");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing edges
		ArrayList<Edge> EDGES = (ArrayList<Edge>) triangle.calculEdge();

		// Tests
		assertTrue("Wrong number of edges in triangle", EDGES.size() == 3);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to process triangle barycenter
	// --------------------------------------------------------------------------------
	public void testTriangle8() {

		log.info("Test for method to process process triangle barycenter");
		log.warn("Redundancy with 'center' method");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing center
		DirectPosition dp = (DirectPosition) triangle.Barycentre();

		// Tests
		assertEquals("Triangle center X coordinate is incorrect", dp.getX(), 5, epsilon);
		assertEquals("Triangle center Y coordinate is incorrect", dp.getY(), 10.0/3.0, epsilon);
		assertEquals("Triangle center Z coordinate is incorrect", dp.getZ(), 0, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to know if edges are included in triangles
	// --------------------------------------------------------------------------------
	public void testTriangle9() {

		log.info("Test for method to process triangle edges");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing edges
		ArrayList<Edge> EDGES = (ArrayList<Edge>) triangle.calculEdge();

		// Tests
		assertTrue("Edge 1 should be included in triangle", triangle.containEdge(EDGES.get(0)));
		assertTrue("Edge 2 should be included in triangle", triangle.containEdge(EDGES.get(1)));
		assertTrue("Edge 3 should be included in triangle", triangle.containEdge(EDGES.get(2)));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to know if edges are included in triangles
	// --------------------------------------------------------------------------------
	public void testTriangle10() {

		log.info("Test for method to process triangle edges");

		// Creating vertices
		Vertex[] VLIST = new Vertex[3];

		VLIST[0] = new Vertex(0, 0, 0);
		VLIST[1] = new Vertex(10, 0, 0);
		VLIST[2] = new Vertex(5, 10, 0);

		// Creating triangle
		Triangle triangle = new Triangle(VLIST);

		// Processing edges
		ArrayList<Edge> EDGES = (ArrayList<Edge>) triangle.calculEdge();

		// Tests
		assertTrue("Edge 1 should be included in triangle", triangle.contientEdge(EDGES.get(0)));
		assertTrue("Edge 2 should be included in triangle", triangle.contientEdge(EDGES.get(1)));
		assertTrue("Edge 3 should be included in triangle", triangle.contientEdge(EDGES.get(2)));

	}

}
