package fr.ign.cogit.convert.geom;




import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToLineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.tools.Utils;





public class TestFromPolygonToLineString extends TestCase {

	private static Logger log = Logger.getLogger(TestFromPolygonToLineString.class);

	// ----------------------------------- METHODS ------------------------------------

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to convert polygon to line strings
	// --------------------------------------------------------------------------------
	public void testConvertListPolToLineStrings1() {

		log.info("Test for method to convert polygon to line strings");

		// Creating square polygon
		GM_Polygon square = Utils.createSquarePolygon(2, 0, 0);

		// Recovering line strings
		List<ILineString> LINES = FromPolygonToLineString.convertPolToLineStrings(square);

		assertEquals("Wrong number of line string in polygon decomposition", 4, LINES.size());


	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to convert a list of polygons to line strings
	// --------------------------------------------------------------------------------
	public void testConvertListPolToLineStrings2() {

		log.info("Test for method to convert a list of polygons to line strings");

		// Creating square polygon
		GM_Polygon square1 = Utils.createSquarePolygon(2, 0, 0);
		GM_Polygon square2 = Utils.createSquarePolygon(2, 0, 0);
		
		// Creating list
		ArrayList<IOrientableSurface> LIST = new ArrayList<IOrientableSurface>();
		
		LIST.add(square1);
		LIST.add(square2);

		// Recovering line strings
		List<ILineString> LINES = FromPolygonToLineString.convertListPolToLineStrings(LIST);

		assertEquals("Wrong number of line string in polygon decomposition", 8, LINES.size());


	}


}
