package fr.ign.cogit;



import junit.framework.Assert;

import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.tools.Utils;

public class TestHeritage {
	
	/*@Test
	public void testPolygonFaux() {
		GM_Polygon polygon1 = Utils.createSquarePolygon(10, 0, 0);
		GM_Polygon polygon2 = Utils.createSquarePolygon(5, 10, 10);
		
		System.out.println("P1 = P2 : " + polygon1.equals(polygon2));
		//Assert.assertNotEquals("P1 = P2 : ", polygon1, polygon2);
	}*/
	
	@Test
	public void testPolygonVrai() {
		GM_Polygon polygon1 = Utils.createSquarePolygon(10, 0, 0);
		GM_Polygon polygon3 = Utils.createSquarePolygon(10, 0, 0);
		
		System.out.println("P1 = P3 : " + polygon1.equals(polygon3));
		// Assert.assertTrue("P1 = P3 avec equals : ", polygon1.equals(polygon3));
		Assert.assertEquals("P1 = P3 avec Assert.equals : ", polygon1, polygon3);
		
	}
	
	@Test
	public void testLineString() {
		
		DirectPositionList dpl = new DirectPositionList();
		dpl.add(new DirectPosition(0, 0, 0));
		dpl.add(new DirectPosition(0, -5, -15));
		dpl.add(new DirectPosition(0, +5, -15));
		GM_LineString ligne1 = new GM_LineString(dpl);
		
		DirectPositionList dp2 = new DirectPositionList();
		dp2.add(new DirectPosition(0, 0, 0));
		dp2.add(new DirectPosition(0, -5, -15));
		dp2.add(new DirectPosition(0, +5, -15));
		GM_LineString ligne2 = new GM_LineString(dp2);
		
		
		System.out.println("L1 = L2 : " + ligne1.equals(ligne2));
		// Assert.assertTrue("L1 = L2 avec equals : ", ligne1.equals(ligne2));
		Assert.assertEquals("L1 = L2 avec Assert.equals : ", ligne1, ligne2);
		
	}
	
	@Test
	public void testPoint() {

		// Creating basic cube
		GM_Solid cube = Utils.createCube(-5, -5, -5, 10);

		// Processing lower point
		DirectPosition pmin = (DirectPosition) Calculation3D.pointMin(cube);

		// Processing expected result
		DirectPosition pminExp = new DirectPosition(-5,-5,-5);

		// Comparison
		Assert.assertEquals("Lower point is incorrect", pmin, pminExp);

	}

}
