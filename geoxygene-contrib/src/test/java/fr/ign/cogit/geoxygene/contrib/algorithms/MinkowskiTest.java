package fr.ign.cogit.geoxygene.contrib.algorithms;

import org.junit.Test;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import junit.framework.Assert;

public class MinkowskiTest {

	@Test
	public void testSoustraction1(){

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
	public void testSoustraction2(){
		
		DirectPositionList points = new DirectPositionList();
		DirectPositionList points2 = new DirectPositionList();

		// -------------------------------------------------
		// Cas de non connexité de A - B
		// -------------------------------------------------
		
		
		points.add(new DirectPosition(10,10));
		points.add(new DirectPosition(15,10));
		points.add(new DirectPosition(18,10));
		points.add(new DirectPosition(19,13));
		points.add(new DirectPosition(17,15));
		points.add(new DirectPosition(14,12));
		points.add(new DirectPosition(15,15));
		points.add(new DirectPosition(10,15));
		points.add(new DirectPosition(10,10)); 

		points2.add(new DirectPosition(5,11));
		points2.add(new DirectPosition(8,11));
		points2.add(new DirectPosition(6,14));
		points2.add(new DirectPosition(5,11));
		
		
		// -------------------------------------------------
		// Création des polygones
		// -------------------------------------------------


		GM_LineString lines = new GM_LineString(points);

		GM_Polygon p1 = new GM_Polygon(lines);


		GM_LineString lines2 = new GM_LineString(points2);

		GM_Polygon p2 = new GM_Polygon(lines2);

		// -------------------------------------------------
		// Centre de masse du polygone B
		// -------------------------------------------------

		@SuppressWarnings("unused")
		DirectPosition center = (DirectPosition) p2.centroid();

		// -------------------------------------------------
		// Soustraction de Minkowski S = A ⊖ B
		// -------------------------------------------------
		
		@SuppressWarnings({ "rawtypes", "unused" })
		GM_MultiSurface innerfit = (GM_MultiSurface) Minkowski.substractionOfMinkowskiFromCenter(p1, p2);

		Assert.assertTrue(true);
		
	}
	
	@Test
	public void testSoustraction3(){
		
		DirectPositionList points = new DirectPositionList();
		DirectPositionList points2 = new DirectPositionList();

		// -------------------------------------------------
		// Cas de "forte" concavité de B
		// -------------------------------------------------

		points.add(new DirectPosition(10,10));
		points.add(new DirectPosition(15,10));
		points.add(new DirectPosition(17,12));
		points.add(new DirectPosition(13,12));
		points.add(new DirectPosition(13,15));
		points.add(new DirectPosition(10,15));
		points.add(new DirectPosition(10,10));

		points2.add(new DirectPosition(5,11));
		points2.add(new DirectPosition(9,11));
		points2.add(new DirectPosition(9,11.3));
		points2.add(new DirectPosition(5.3,11.3));
		points2.add(new DirectPosition(5.3,14));
		points2.add(new DirectPosition(5,14));
		points2.add(new DirectPosition(5,11));

		
		// -------------------------------------------------
		// Création des polygones
		// -------------------------------------------------


		GM_LineString lines = new GM_LineString(points);

		GM_Polygon p1 = new GM_Polygon(lines);


		GM_LineString lines2 = new GM_LineString(points2);

		GM_Polygon p2 = new GM_Polygon(lines2);

		// -------------------------------------------------
		// Centre de masse du polygone B
		// -------------------------------------------------

		@SuppressWarnings("unused")
		DirectPosition center = (DirectPosition) p2.centroid();

		// -------------------------------------------------
		// Soustraction de Minkowski S = A ⊖ B
		// -------------------------------------------------

		@SuppressWarnings("unused")
		GM_Polygon innerfit = (GM_Polygon) Minkowski.substractionOfMinkowskiFromCenter(p1, p2);

		Assert.assertTrue(true);

	}
	
	@Test
	public void testSoustraction4(){
		
		DirectPositionList points = new DirectPositionList();
		DirectPositionList points2 = new DirectPositionList();

		points.add(new DirectPosition(10,10));
		points.add(new DirectPosition(15,10));
		points.add(new DirectPosition(17,12));
		points.add(new DirectPosition(14,12));
		points.add(new DirectPosition(15,15));
		points.add(new DirectPosition(10,15));
		points.add(new DirectPosition(10,10)); 

		points2.add(new DirectPosition(5,11));
		points2.add(new DirectPosition(8,11));
		points2.add(new DirectPosition(6,14));
		points2.add(new DirectPosition(5,11));

		
		// -------------------------------------------------
		// Création des polygones
		// -------------------------------------------------


		GM_LineString lines = new GM_LineString(points);

		GM_Polygon p1 = new GM_Polygon(lines);


		GM_LineString lines2 = new GM_LineString(points2);

		GM_Polygon p2 = new GM_Polygon(lines2);

		// -------------------------------------------------
		// Centre de masse du polygone B
		// -------------------------------------------------

		@SuppressWarnings("unused")
		DirectPosition center = (DirectPosition) p2.centroid();

		// -------------------------------------------------
		// Soustraction de Minkowski S = A ⊖ B
		// -------------------------------------------------

		@SuppressWarnings("unused")
		GM_Polygon innerfit = (GM_Polygon) Minkowski.substractionOfMinkowskiFromCenter(p1, p2);

		
		Assert.assertTrue(true);

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
