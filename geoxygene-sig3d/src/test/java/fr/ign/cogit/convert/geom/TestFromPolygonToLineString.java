package fr.ign.cogit.convert.geom;




import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToLineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.tools.Utils;





public class TestFromPolygonToLineString extends TestCase {

	// ----------------------------------- METHODS ------------------------------------

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to convert polygon to line strings
	// --------------------------------------------------------------------------------
	public void testConvertListPolToLineStrings() {

		// Creating square polygon
		GM_Polygon square = Utils.createSquarePolygon(2, 0, 0);
		
		// Recovering line strings
		List<ILineString> LINES = FromPolygonToLineString.convertPolToLineStrings(square);
		
		assertEquals("Wrong number of line string in polygon decomposition", 4, LINES.size());
		
		
	}

	
}
