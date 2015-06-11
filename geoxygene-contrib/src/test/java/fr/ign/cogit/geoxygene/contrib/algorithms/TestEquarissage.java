package fr.ign.cogit.geoxygene.contrib.algorithms;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class TestEquarissage {

	@Test
	public void test1() {

		// ---------------------------------------------------------------
		// Polygone quelconque
		// ---------------------------------------------------------------

		String wkt = "POLYGON(("
				+ "-70.3125 59.46294364366465,"
				+ "-71.015625 -10.971390694675893,"
				+ "8.4375 -10.971390694675893,"
				+ "87.1875 -1.5466849709194905,"
				+ "129.375 -10.28032720607631,"
				+ "129.375 46.21937355921822,"
				+ "88.2421875 36.48490899302895,"
				+ "88.2421875 62.365920983566596,"
				+ "130.4296875 62.202414403333876,"
				+ "130.78125 73.08407648774843,"
				+ "44.6484375 73.2874666745595,"
				+ "34.1015625 19.178378660628294,"
				+ "0.3515625 19.841128871326855,"
				+ "0.703125 73.58810386080637,"
				+ "-70.6640625 73.48848032020128,"
				+ "-79.8046875 73.38826897805087,"
				+ "-70.3125 59.46294364366465))";

		GM_Polygon poly = null;

		try {

			poly = (GM_Polygon) WktGeOxygene.makeGeOxygene(wkt);

		} catch (ParseException e) {

			e.printStackTrace();

		}

		// ---------------------------------------------------------------
		// Représentation Matlab
		// ---------------------------------------------------------------

		Matlab.clf();
		Matlab.plot(poly, "r");
		Matlab.holdon();


		// ---------------------------------------------------------------
		// Equarissage
		// ---------------------------------------------------------------

		Equarissage.setWeight(100);

		GM_Polygon poly_out = Equarissage.compute(poly);

		// ---------------------------------------------------------------
		// Représentation Matlab
		// ---------------------------------------------------------------

		Matlab.plot(poly_out, "g");
		Matlab.axisSquare();

		// ---------------------------------------------------------------
		// Test
		// ---------------------------------------------------------------
		Assert.assertTrue(true);

	}

}
