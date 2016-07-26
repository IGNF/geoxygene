package convert;



import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;



public class TestFromGeomToSurface extends TestCase {

	// ----------------------------------- METHODS ------------------------------------

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to convert solid to surfaces
	// --------------------------------------------------------------------------------
	public void testConvertGeom1() {

		// Creating solid
		GM_Solid cube = Utils.createCube(0, 0, 0, 10);

		// Converting into surfaces
		List<IOrientableSurface> AS = FromGeomToSurface.convertGeom(cube);

		String s1 = "POLYGON ((0.0 0.0 0.0, 10.0 0.0 0.0, 10.0 10.0 0.0, 0.0 10.0 0.0, 0.0 0.0 0.0))";
		String s2 = "POLYGON ((0.0 0.0 0.0, 10.0 0.0 0.0, 10.0 0.0 10.0, 0.0 0.0 10.0, 0.0 0.0 0.0))";
		String s3 = "POLYGON ((0.0 0.0 0.0, 0.0 10.0 0.0, 0.0 10.0 10.0, 0.0 0.0 10.0, 0.0 0.0 0.0))";
		String s4 = "POLYGON ((0.0 0.0 10.0, 10.0 0.0 10.0, 10.0 10.0 10.0, 0.0 10.0 10.0, 0.0 0.0 10.0))";
		String s5 = "POLYGON ((0.0 10.0 0.0, 10.0 10.0 0.0, 10.0 10.0 10.0, 0.0 10.0 10.0, 0.0 10.0 0.0))";
		String s6 = "POLYGON ((10.0 0.0 0.0, 10.0 10.0 0.0, 10.0 10.0 10.0, 10.0 0.0 10.0, 10.0 0.0 0.0))";

		String ps1 = ((GM_Polygon) AS.get(0)).toString();
		String ps2 = ((GM_Polygon) AS.get(1)).toString();
		String ps3 = ((GM_Polygon) AS.get(2)).toString();
		String ps4 = ((GM_Polygon) AS.get(3)).toString();
		String ps5 = ((GM_Polygon) AS.get(4)).toString();
		String ps6 = ((GM_Polygon) AS.get(5)).toString();

		boolean b1 = ((ps1.equals(s1))||(ps1.equals(s2))||(ps1.equals(s3))||(ps1.equals(s4))||(ps1.equals(s5))||(ps1.equals(s6)));
		boolean b2 = ((ps2.equals(s1))||(ps2.equals(s2))||(ps2.equals(s3))||(ps2.equals(s4))||(ps2.equals(s5))||(ps2.equals(s6)));
		boolean b3 = ((ps3.equals(s1))||(ps3.equals(s2))||(ps3.equals(s3))||(ps3.equals(s4))||(ps3.equals(s5))||(ps3.equals(s6)));
		boolean b4 = ((ps4.equals(s1))||(ps4.equals(s2))||(ps4.equals(s3))||(ps4.equals(s4))||(ps4.equals(s5))||(ps4.equals(s6)));
		boolean b5 = ((ps5.equals(s1))||(ps5.equals(s2))||(ps5.equals(s3))||(ps5.equals(s4))||(ps5.equals(s5))||(ps5.equals(s6)));
		boolean b6 = ((ps6.equals(s1))||(ps6.equals(s2))||(ps6.equals(s3))||(ps6.equals(s4))||(ps6.equals(s5))||(ps6.equals(s6)));

		// Comparison
		assertTrue("Wrong decomposition in surfaces",b1);
		assertTrue("Wrong decomposition in surfaces",b2);
		assertTrue("Wrong decomposition in surfaces",b3);
		assertTrue("Wrong decomposition in surfaces",b4);
		assertTrue("Wrong decomposition in surfaces",b5);
		assertTrue("Wrong decomposition in surfaces",b6);

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for method to convert solid into multi-surfaces
	// --------------------------------------------------------------------------------
	public void testConvertGeom2() {

		// Creating solid
		GM_Solid cube = Utils.createCube(0, 0, 0, 10);

		// Converting into surfaces
		GM_MultiSurface<IOrientableSurface> AS = (GM_MultiSurface<IOrientableSurface>) FromGeomToSurface.convertMSGeom(cube);

		String s1 = "POLYGON ((0.0 0.0 0.0, 10.0 0.0 0.0, 10.0 10.0 0.0, 0.0 10.0 0.0, 0.0 0.0 0.0))";
		String s2 = "POLYGON ((0.0 0.0 0.0, 10.0 0.0 0.0, 10.0 0.0 10.0, 0.0 0.0 10.0, 0.0 0.0 0.0))";
		String s3 = "POLYGON ((0.0 0.0 0.0, 0.0 10.0 0.0, 0.0 10.0 10.0, 0.0 0.0 10.0, 0.0 0.0 0.0))";
		String s4 = "POLYGON ((0.0 0.0 10.0, 10.0 0.0 10.0, 10.0 10.0 10.0, 0.0 10.0 10.0, 0.0 0.0 10.0))";
		String s5 = "POLYGON ((0.0 10.0 0.0, 10.0 10.0 0.0, 10.0 10.0 10.0, 0.0 10.0 10.0, 0.0 10.0 0.0))";
		String s6 = "POLYGON ((10.0 0.0 0.0, 10.0 10.0 0.0, 10.0 10.0 10.0, 10.0 0.0 10.0, 10.0 0.0 0.0))";

		String ps1 = ((GM_Polygon) AS.get(0)).toString();
		String ps2 = ((GM_Polygon) AS.get(1)).toString();
		String ps3 = ((GM_Polygon) AS.get(2)).toString();
		String ps4 = ((GM_Polygon) AS.get(3)).toString();
		String ps5 = ((GM_Polygon) AS.get(4)).toString();
		String ps6 = ((GM_Polygon) AS.get(5)).toString();

		boolean b1 = ((ps1.equals(s1))||(ps1.equals(s2))||(ps1.equals(s3))||(ps1.equals(s4))||(ps1.equals(s5))||(ps1.equals(s6)));
		boolean b2 = ((ps2.equals(s1))||(ps2.equals(s2))||(ps2.equals(s3))||(ps2.equals(s4))||(ps2.equals(s5))||(ps2.equals(s6)));
		boolean b3 = ((ps3.equals(s1))||(ps3.equals(s2))||(ps3.equals(s3))||(ps3.equals(s4))||(ps3.equals(s5))||(ps3.equals(s6)));
		boolean b4 = ((ps4.equals(s1))||(ps4.equals(s2))||(ps4.equals(s3))||(ps4.equals(s4))||(ps4.equals(s5))||(ps4.equals(s6)));
		boolean b5 = ((ps5.equals(s1))||(ps5.equals(s2))||(ps5.equals(s3))||(ps5.equals(s4))||(ps5.equals(s5))||(ps5.equals(s6)));
		boolean b6 = ((ps6.equals(s1))||(ps6.equals(s2))||(ps6.equals(s3))||(ps6.equals(s4))||(ps6.equals(s5))||(ps6.equals(s6)));

		// Comparison
		assertTrue("Wrong decomposition in surfaces",b1);
		assertTrue("Wrong decomposition in surfaces",b2);
		assertTrue("Wrong decomposition in surfaces",b3);
		assertTrue("Wrong decomposition in surfaces",b4);
		assertTrue("Wrong decomposition in surfaces",b5);
		assertTrue("Wrong decomposition in surfaces",b6);

	}

}
