package fr.ign.cogit.geoxygene.contrib.algorithms;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

// ----------------------------------------------------------------------
// Classe d'impressions de lignes de command GNU Plot (Matlab)
// Date : 11/06/2015
//----------------------------------------------------------------------

public class Matlab {
	
	
	public static void clf(){

		System.out.println("clf;");

	}


	public static void holdon(){

		System.out.println("hold on;");

	}

	public static void axisSquare(){

		System.out.println("axis('square');");

	}

	public static void plot(GM_Polygon poly, String color){
		
		DirectPositionList POINTS = (DirectPositionList) poly.coord();

		System.out.println("M=[");

		for (int i=0; i<POINTS.size(); i++){

			System.out.println(POINTS.get(i).getX()+","+POINTS.get(i).getY());

		}

		System.out.println("];");

		System.out.println("plot(M(:,1),M(:,2),'"+color+"')");

	}
	
	
	public static void axis(double xmin, double xmax, double ymin, double ymax){

		System.out.println("axis(["+xmin+","+xmax+","+ymin+","+ymax+"]);");

	}
	


}
