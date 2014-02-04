package fr.ign.cogit.io.vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.vector.ShapeFileLoader;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;




public class TestShapeFileLoader extends TestCase {

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
	// Test for method to load shape file
	// --------------------------------------------------------------------------------
	public void testShapeFileLoader1() {

		loadAndTest("src\\test\\resources\\Bati.shp", 470, 91482);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to load shape file
	// --------------------------------------------------------------------------------
	public void testShapeFileLoader2() {

		loadAndTest("src\\test\\resources\\Parcelle.shp",137,2033);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to load shape file
	// --------------------------------------------------------------------------------
	public void testShapeFileLoader3() {

		loadAndTest("src\\test\\resources\\Route.shp",37,85);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to load shape file
	// --------------------------------------------------------------------------------
	public void testShapeFileLoader4() {

		loadAndTest("src\\test\\resources\\Ruerennes.shp",6,15);

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for method to load shape file
	// --------------------------------------------------------------------------------
	public void testShapeFileLoader5() {

		loadAndTest("src\\test\\resources\\projrenn.shp",19316,19316);

	}

	// --------------------------------------------------------------------------------
	// Method to load and test a shape file
	// --------------------------------------------------------------------------------
	public void loadAndTest(String path, int nbShapes, int nbPoints) {

		// Loading data
		FT_FeatureCollection<IFeature> data = ShapeFileLoader.loadingShapeFile(path,"0","10",true);

		// Counting number of points
		DirectPositionList dpl = new DirectPositionList();
		DirectPositionList dplTemp ;

		for (IFeature f : data){

			dplTemp = (DirectPositionList) f.getGeom().coord();

			for (int i=0; i<dplTemp.size(); i++){

				dpl.add(dplTemp.get(i));

			}

		}

		// Comparison
		assertEquals("Wrong number of loaded objects", nbShapes, data.size(), epsilon);
		assertEquals("Wrong number of loaded points", nbPoints, dpl.size());


	}


}

