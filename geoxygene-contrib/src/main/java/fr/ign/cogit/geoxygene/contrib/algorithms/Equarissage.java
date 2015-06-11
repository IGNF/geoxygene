package fr.ign.cogit.geoxygene.contrib.algorithms;

// ----------------------------------------------------------------------
// Classe d'équarissage de polygones 2D par moindres carrés
// Trois contraintes :
//       - Contrainte sur les angles droits
//       - Contrainte sur les angles plats
//       - Contraintes de conservation des positions des sommets
// Paramètres :
//		 - Weight : poids sur les contraintes de redressement 
// (arbitrairement : poids sur les contraintes de position = 1.0)
//		 - Threshold : seuil sur le cosinus de l'angle (entre 0 et 1). 
// Tous les angles à moins de t° d'un angle plat ou droit seront 
// contraints
// Date : 11/06/2015
// ----------------------------------------------------------------------


import fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric.Constraint;
import fr.ign.cogit.geoxygene.contrib.leastsquaresgeneric.Solver;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class Equarissage {


	private static double threshold = 0.4;
	private static double weight = 0.005;
	
	
	public static void setThreshold(double t){threshold = t;}
	public static void setWeight(double w){weight = w;}


	public static GM_Polygon compute(GM_Polygon polygon) {

		
		DirectPositionList POINTS = (DirectPositionList) polygon.coord();
		
		// ---------------------------------------------------------------
		// Mise sous contraintes du problème d'équarissage
		// ---------------------------------------------------------------

		Solver solver = new Solver();

		// Formalisation des contraintes

		for (int i=0; i<POINTS.size()-1; i++){

			solver.addConstraint(new Constraint("x"+i+" = "+POINTS.get(i).getX()));
			solver.addConstraint(new Constraint("y"+i+" = "+POINTS.get(i).getY()));

		}

		for (int j=1; j<POINTS.size(); j++){

			int i = j-1;
			int k = j+1;

			if (k==POINTS.size()-1){k = 0;}

			if (k==POINTS.size()){k = 1;}
			if (j==POINTS.size()-1){j = 0;}

			//  Test de contrainte

			double xi = POINTS.get(i).getX();  double yi = POINTS.get(i).getY();
			double xj = POINTS.get(j).getX();  double yj = POINTS.get(j).getY();
			double xk = POINTS.get(k).getX();  double yk = POINTS.get(k).getY();

			double norms = (Math.sqrt((xi-xj)*(xi-xj)+(yi-yj)*(yi-yj))*Math.sqrt((xk-xj)*(xk-xj)+(yk-yj)*(yk-yj)));
			double product = ((xi-xj)*(xk-xj)+(yi-yj)*(yk-yj))/norms;

			if (Math.abs(product) <= threshold){

				Constraint constraint = new Constraint("(x"+k+"-x"+j+")*(x"+i+"-x"+j+")+(y"+k+"-y"+j+")*(y"+i+"-y"+j+") = "+0);

				constraint.setWeight(weight);

				solver.addConstraint(constraint);

			}


			if (Math.abs(product) >= 1-threshold){

				Constraint constraint = new Constraint("(x"+j+"-x"+i+")*(y"+k+"-y"+j+")-(x"+k+"-x"+j+")*(y"+j+"-y"+i+") = 0");

				constraint.setWeight(weight);

				solver.addConstraint(constraint);

				System.out.println(constraint);

			}



			if (j == 0){break;}

		}

		// Déclarations des inconnues
		for (int i=0; i<POINTS.size()-1; i++){

			solver.addParameter("x"+i, POINTS.get(i).getX());
			solver.addParameter("y"+i, POINTS.get(i).getY());

		}

		// ---------------------------------------------------------------
		// Vérification de la formulation des contraintes
		// ---------------------------------------------------------------

		/*
			System.out.println("-----------------------------------------");
			System.out.println("Nombre de contraintes : "+solver.getConstraintsNumber());
			System.out.println("Nombre de paramètres : "+solver.getParametersNumber());
			System.out.println("-----------------------------------------");


			for (int i=0; i<solver.getConstraintsNumber(); i++){

				System.out.println(solver.getConstraint(i));

			}
		 */

		//	System.out.println("-----------------------------------------");

		// ---------------------------------------------------------------
		// Paramétrage
		// ---------------------------------------------------------------
		solver.setIterationsNumber(10);
		solver.setConvergenceCriteria(0.01);

		// ---------------------------------------------------------------
		// Résolution
		// ---------------------------------------------------------------
		solver.compute();

		// ---------------------------------------------------------------
		// Résultats numériques
		// ---------------------------------------------------------------
		//	solver.displayResults();

		// ---------------------------------------------------------------
		// Polygone de sortie
		// ---------------------------------------------------------------

		DirectPositionList OUT = new DirectPositionList();

		for (int i=0; i<solver.getParametersNumber(); i+=2){

			OUT.add(new DirectPosition(solver.getParameter(i), solver.getParameter(i+1)));

		}

		OUT.add(new DirectPosition(solver.getParameter(0), solver.getParameter(1)));

		return new GM_Polygon(new GM_LineString(OUT));

	}

}
