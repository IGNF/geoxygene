package fr.ign.cogit.convert.transform;




import junit.framework.TestCase;

import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;




public class TestExtrusion2DObject extends TestCase {

	// ----------------------------------- METHODS ------------------------------------

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to extrude 2D object
	// --------------------------------------------------------------------------------
	public void testConvertFromGeometry1() {

		// Creating square
		GM_Polygon square = Utils.createSquarePolygon(1, 0, 0);

		// Operating extrusion
		GM_Solid cube = (GM_Solid) Extrusion2DObject.convertFromGeometry(square, 0, 1);

		// Processing expected result
		GM_Solid cubeExp = Utils.createCube(0,0,0,1);

		// Comparison
		assertTrue("Extruded geometry is incorrect", cube.equals(cubeExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to extrude 2D object (null object)
	// --------------------------------------------------------------------------------
	public void testConvertFromGeometry2() {

		// Operating extrusion
		GM_Solid cube = (GM_Solid) Extrusion2DObject.convertFromGeometry(null, 0, 1);

		// Comparison
		assertNull("Extruded null geometry is incorrect", cube);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to extrude 2D polygon 
	// --------------------------------------------------------------------------------
	public void testConvertFromGeometry3() {

		// Creating square
		GM_Polygon square = Utils.createSquarePolygon(1, 0, 0);

		// Operating extrusion
		GM_Solid cube = (GM_Solid) Extrusion2DObject.convertFromPolygon(square, 0, 1);

		// Processing expected result
		GM_Solid cubeExp = Utils.createCube(0,0,0,1);

		// Comparison
		assertTrue("Extruded geometry is incorrect", cube.equals(cubeExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to extrude 2D line
	// --------------------------------------------------------------------------------
	public void testConvertFromGeometry4() {

		// Creating 1D line string
		GM_LineString line = new GM_LineString(new DirectPosition(0,0), new DirectPosition(1,0));
		
		// Operating line extrusion
		GM_MultiSurface<?> surface = (GM_MultiSurface<?>) Extrusion2DObject.convertFromLine(line, 0, 1);
		
		// Processing expected result
		String sExp = "MULTIPOLYGON (((1.0 0.0 0.0, 0.0 0.0 0.0, 0.0 0.0 1.0, 1.0 0.0 1.0, 1.0 0.0 0.0)))";
		
		// Comparison
		assertTrue("Extruded line is incorrect", surface.toString().equals(sExp));

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for method to extrude 2D point
	// --------------------------------------------------------------------------------
	public void testConvertFromGeometry5() {
		
		// Creating point
		GM_Point p = new GM_Point(new DirectPosition(0,0)); 
		
		// Operating extrusion
		GM_LineString line = (GM_LineString) Extrusion2DObject.convertFromPoint(p, 10, 15);
		
		// Processing expected result
		GM_LineString lineExpected = new GM_LineString(new DirectPosition(0,0,10),new DirectPosition(0,0,15));
		
		// Comparison
		assertTrue("Extruded point is incorrect", line.toString().equals(lineExpected.toString()));
		
	}

}
