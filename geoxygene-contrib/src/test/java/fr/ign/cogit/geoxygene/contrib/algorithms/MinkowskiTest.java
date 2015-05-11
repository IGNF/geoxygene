package fr.ign.cogit.geoxygene.contrib.algorithms;

import junit.framework.Assert;

import org.junit.Test;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class MinkowskiTest {

	@Test
	public void testSoustraction(){

		// -------------------------------------------
		// Polygone de vérification :
		// -------------------------------------------
		DirectPositionList controlPts = new DirectPositionList();

		controlPts.add(new DirectPosition(11.166666666666666,13.166666666666666));
		controlPts.add(new DirectPosition(12.166666666666666,13.166666666666666));
		controlPts.add(new DirectPosition(12.166666666666666,12.166666666666666));
		controlPts.add(new DirectPosition(13.166666666666666,11.166666666666666));
		controlPts.add(new DirectPosition(11.166666666666666,11.166666666666666));
		controlPts.add(new DirectPosition(11.166666666666666,13.166666666666666));

		GM_LineString linesControl = new GM_LineString(controlPts);

		GM_Polygon control = new GM_Polygon(linesControl);

		// -------------------------------------------
		// Polygone A
		// -------------------------------------------

		DirectPositionList points = new DirectPositionList();

		points.add(new DirectPosition(10,10));
		points.add(new DirectPosition(15,10));
		points.add(new DirectPosition(17,12));
		points.add(new DirectPosition(13,13));
		points.add(new DirectPosition(15,15));
		points.add(new DirectPosition(10,15));
		points.add(new DirectPosition(10,10));

		GM_LineString lines = new GM_LineString(points);

		GM_Polygon p1 = new GM_Polygon(lines);

		// -------------------------------------------
		// Polygone B
		// -------------------------------------------

		DirectPositionList points2 = new DirectPositionList();

		points2.add(new DirectPosition(5,5));
		points2.add(new DirectPosition(7,5));
		points2.add(new DirectPosition(7,7));
		points2.add(new DirectPosition(6,8));
		points2.add(new DirectPosition(5,5));

		GM_LineString lines2 = new GM_LineString(points2);

		GM_Polygon p2 = new GM_Polygon(lines2);

		// -------------------------------------------
		// Polygone de différence A - B
		// -------------------------------------------

		GM_Polygon innerfit = (GM_Polygon) Minkowski.substractionOfMinkowskiFromCenter(p1, p2);

		// -------------------------------------------
		// Controle
		// -------------------------------------------

		double ratio = innerfit.intersection(control).area()/innerfit.union(control).area();

		Assert.assertEquals(ratio, 1.0, Math.pow(10, -6));

	}

	@Test
	public void testSomme(){


		// -------------------------------------------
		// Polygone de vérification :
		// -------------------------------------------
		DirectPositionList controlPts = new DirectPositionList();
		
		controlPts.add(new DirectPosition(17.833333333333332,12.833333333333332));
		controlPts.add(new DirectPosition(17.833333333333332,10.833333333333332));
		controlPts.add(new DirectPosition(15.833333333333332,8.833333333333332));
		controlPts.add(new DirectPosition(13.833333333333332,8.833333333333332));
		controlPts.add(new DirectPosition(10.833333333333332,8.833333333333332));
		controlPts.add(new DirectPosition(8.833333333333332,8.833333333333332));
		controlPts.add(new DirectPosition(8.833333333333332,13.833333333333332));
		controlPts.add(new DirectPosition(9.833333333333332,16.833333333333332));
		controlPts.add(new DirectPosition(14.833333333333332,16.833333333333332));
		controlPts.add(new DirectPosition(15.833333333333332,15.833333333333332));
		controlPts.add(new DirectPosition(15.833333333333332,14.083333333333332));
		controlPts.add(new DirectPosition(16.833333333333332,13.833333333333332));
		controlPts.add(new DirectPosition(17.833333333333332,12.833333333333332));

		GM_LineString linesControl = new GM_LineString(controlPts);

		GM_Polygon control = new GM_Polygon(linesControl);

		// -------------------------------------------
		// Polygone A
		// -------------------------------------------

		DirectPositionList points = new DirectPositionList();

		points.add(new DirectPosition(10,10));
		points.add(new DirectPosition(15,10));
		points.add(new DirectPosition(17,12));
		points.add(new DirectPosition(13,13));
		points.add(new DirectPosition(15,15));
		points.add(new DirectPosition(10,15));
		points.add(new DirectPosition(10,10));

		GM_LineString lines = new GM_LineString(points);

		GM_Polygon p1 = new GM_Polygon(lines);

		// -------------------------------------------
		// Polygone B
		// -------------------------------------------

		DirectPositionList points2 = new DirectPositionList();

		points2.add(new DirectPosition(5,5));
		points2.add(new DirectPosition(7,5));
		points2.add(new DirectPosition(7,7));
		points2.add(new DirectPosition(6,8));
		points2.add(new DirectPosition(5,5));

		GM_LineString lines2 = new GM_LineString(points2);

		GM_Polygon p2 = new GM_Polygon(lines2);

		// -------------------------------------------
		// Polygone de somme
		// -------------------------------------------

		GM_Polygon somme = (GM_Polygon) Minkowski.sumOfMinkowskiFromCenter(p1, p2);

		for (int i=0; i<somme.coord().size(); i++){

			System.out.println(somme.coord().get(i).getX()+","+somme.coord().get(i).getY());

		}

		// -------------------------------------------
		// Controle
		// -------------------------------------------

		double ratio = somme.intersection(control).area()/somme.union(control).area();

		Assert.assertEquals(ratio, 1.0, Math.pow(10, -6));

	}

}
