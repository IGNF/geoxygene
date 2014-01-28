package fr.ign.cogit.calculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;

public class TestCalculation3D {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	private static double epsilon = Math.pow(10,-1);

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
	// Test for method to compute area of single random triangle
	// --------------------------------------------------------------------------------
	public void testArea1() {

		// Creating random triangle

		// Triangle coordinates
		Matrix[] C = new Matrix[3];

		for (int i=0; i<3; i++){C[i] = new Matrix(3,1);}

		//     Coord X			 Coord Y		   Coord Z

		C[0].set(0, 0, 0); C[0].set(1, 0, 0); C[0].set(2, 0, 0);	  // Point A
		C[1].set(0, 0, 1); C[1].set(1, 0, 0); C[1].set(2, 0, 0);	  // Point B
		C[2].set(0, 0, 1); C[2].set(1, 0, 1); C[2].set(2, 0, 0);	  // Point C


		// Rotation
		Matrix R = new Matrix(3,3);

		Matrix RX = new Matrix(3,3);   Matrix RY = new Matrix(3,3);   Matrix RZ = new Matrix(3,3);

		double thetaX = Math.random()*2*Math.PI;  double thetaY = Math.random()*2*Math.PI;  double thetaZ = Math.random()*2*Math.PI;
		double cX = Math.cos(thetaX);             double cY = Math.cos(thetaY);             double cZ = Math.cos(thetaZ);
		double sX = Math.sin(thetaX);             double sY = Math.sin(thetaY);             double sZ = Math.sin(thetaZ); 


		RX.set(0,0,1);  RX.set(0,1, 0);  RX.set(0, 2, 0);    RY.set(0,0,cY);  RY.set(0,1,0);  RY.set(0,2,-sY);   RZ.set(0,0,cZ);  RZ.set(0,1,-sZ);  RZ.set(0,2,0);
		RX.set(1,0,0);  RX.set(1,1,cX);  RX.set(1, 2,-sX);   RY.set(1,0, 0);  RY.set(1,1,1);  RY.set(1,2,  0);   RZ.set(1,0,sZ);  RZ.set(1,1, cZ);  RZ.set(1,2,0);
		RX.set(2,0,0);  RX.set(2,1,sX);  RX.set(2, 2, cX);   RY.set(2,0,sY);  RY.set(2,1,0);  RY.set(2,2, cY);   RZ.set(2,0, 0);  RZ.set(2,1,  0);  RZ.set(2,2,1);

		R = RX.times(RY).times(RZ);


		// Translation
		Matrix T = new Matrix(3,1);
		double dx = Math.random()*100; 		T.set(0, 0, dx);
		double dy = Math.random()*100;		T.set(1, 0, dy);
		double dz = Math.random()*100; 	    T.set(2, 0, dz);


		// Homothetic transformation
		Matrix K = new Matrix(3,3);

		double k = Math.random()*100;
		K.set(0, 0, k); K.set(0, 1, 0); K.set(0, 2, 0);
		K.set(1, 0, 0); K.set(1, 1, k); K.set(1, 2, 0);
		K.set(2, 0, 0); K.set(2, 1, 0); K.set(2, 2, k);


		// Operating transformation
		for (int i=0; i<3; i++){

			C[i] = K.times((R.times(C[i]).plus(T)));

		}

		DirectPositionList listeSommets = new DirectPositionList();
		listeSommets.add(new DirectPosition(C[0].get(0, 0), C[0].get(1, 0), C[0].get(2, 0)));
		listeSommets.add(new DirectPosition(C[1].get(0, 0), C[1].get(1, 0), C[1].get(2, 0)));
		listeSommets.add(new DirectPosition(C[2].get(0, 0), C[2].get(1, 0), C[2].get(2, 0)));

		GM_Triangle triangle = new GM_Triangle(listeSommets);
		ArrayList<ITriangle> LT = new ArrayList<ITriangle>();
		LT.add(triangle);

		// Processing expected value
		double aExp = 0.5*Math.pow(k, 2);

		// Recovering actual value
		double a = Calculation3D.area(LT);  

		// Comparison
		assertEquals("Triangle area is not correct", aExp, a, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute area of vertical triangle
	// --------------------------------------------------------------------------------
	public void testArea2() {

		DirectPositionList listeSommets = new DirectPositionList();
		listeSommets.add(new DirectPosition(0, 0, 0));
		listeSommets.add(new DirectPosition(2, 0, 0));
		listeSommets.add(new DirectPosition(2, 0, 2));

		GM_Triangle triangle = new GM_Triangle(listeSommets);
		ArrayList<ITriangle> LT = new ArrayList<ITriangle>();
		LT.add(triangle);

		// Processing area
		double a = Calculation3D.area(LT);  

		// Comparison
		assertEquals("Triangle area is not correct", 2, a, epsilon);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute area of a list of triangles
	// --------------------------------------------------------------------------------
	public void testArea3() {

		DirectPositionList listeSommets1 = new DirectPositionList();
		listeSommets1.add(new DirectPosition(0, 0, 0));
		listeSommets1.add(new DirectPosition(1, 0, 0));
		listeSommets1.add(new DirectPosition(1, 0, 1));

		DirectPositionList listeSommets2 = new DirectPositionList();
		listeSommets2.add(new DirectPosition(0, 0, 0));
		listeSommets2.add(new DirectPosition(2, 0, 0));
		listeSommets2.add(new DirectPosition(2, 0, 2));

		GM_Triangle triangle1 = new GM_Triangle(listeSommets1);
		GM_Triangle triangle2 = new GM_Triangle(listeSommets2);
		ArrayList<ITriangle> LT = new ArrayList<ITriangle>();
		LT.add(triangle1); LT.add(triangle2);

		// Processing area
		double a = Calculation3D.area(LT); 

		// Comparison
		assertEquals("Triangle area is not correct", 2.5, a, epsilon);


	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute area for empty list
	// --------------------------------------------------------------------------------
	public void testArea4() {

		ArrayList<ITriangle> LT = new ArrayList<ITriangle>();

		// Processing area
		double a = Calculation3D.area(LT);  

		// Comparison
		assertEquals("Triangle area is not correct", 0, a, epsilon);

	}


	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of a surface
	// --------------------------------------------------------------------------------
	public void testArea5() {

		GM_Polygon surface = Utils.createSquarePolygon(70, 10, 10);

		// Processing center of gravity
		DirectPosition G = (DirectPosition) Calculation3D.centerOfGravity(surface);

		// Expected result
		DirectPosition GExp = new DirectPosition(45,45,0);

		// Comparison
		assertTrue("Surface center of gravity is incorrect", G.equals(GExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of gravity of a vertical triangle
	// --------------------------------------------------------------------------------
	public void testArea6() {

		DirectPositionList dpl = new DirectPositionList();

		dpl.add(new DirectPosition(0, 0, 0));
		dpl.add(new DirectPosition(0, -5, -15));
		dpl.add(new DirectPosition(0, +5, -15));

		GM_Polygon surface = new GM_Polygon(new GM_LineString(dpl));

		// Processing center of gravity
		DirectPosition G = (DirectPosition) Calculation3D.centerOfGravity(surface);

		// Expected result
		DirectPosition GExp = new DirectPosition(0,0,-10);

		// Comparison
		assertEquals("Surface center of gravity is incorrect", GExp, G);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to compute center of complex form
	// --------------------------------------------------------------------------------
	public void testArea7() {

		DirectPosition center = new DirectPosition(12,12,0);


		DirectPositionList dpl = new DirectPositionList();

		// Builiding Pseudo-random points
		for (double theta = 0; theta < 2*Math.PI; theta += 0.0001){

			double radius = Math.random()*10;   
			
			double dx = radius*Math.cos(theta);
			double dy = radius*Math.sin(theta);

			dpl.add(new DirectPosition(center.getX() + dx, center.getY() + dy, 0));

		}

		// Building surfaces
		GM_Polygon surface = new GM_Polygon(new GM_LineString(dpl));

		// Processing center of gravity
		DirectPosition G = (DirectPosition) Calculation3D.centerOfGravity(surface);
		
		// Expected result
		DirectPosition GExp = new DirectPosition(center.getX(), center.getY(), 0);

		// Comparison
		assertEquals("Surface center of gravity coordinate X is incorrect", GExp.getX(), G.getX(), epsilon);
		assertEquals("Surface center of gravity coordinate Y is incorrect", GExp.getY(), G.getY(), epsilon);
		assertEquals("Surface center of gravity coordinate Z is incorrect", GExp.getZ(), G.getZ(), epsilon);

	}


}






