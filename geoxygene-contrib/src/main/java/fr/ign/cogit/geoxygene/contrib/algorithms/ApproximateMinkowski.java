/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.geoxygene.contrib.algorithms;


import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;


// =================================================================================
// Classe d'utilitaires pour le calcul des soustractions de Minkowski approchées
// Date : 17/07/2014
// =================================================================================
public class ApproximateMinkowski {


	// Espace de recherche
	public static double MAX_DISTANCE = 15;

	// Distance interpoints
	public static double GRID_RESOLUTION = 1;

	// Seuil de simplification
	public static double TRESHOLD = 0.5;

	// -----------------------------------------------------------------------------
	// Fonction de calcul de la différence de Minkowski (polygones quelconques)
	// Entrée : polygone 1, polygone 2
	// Sortie : polygone de différence
	// -----------------------------------------------------------------------------
	public static IGeometry substractionOfMinkowskiFromCenter(IGeometry polygon1, IGeometry polygon2){

		// Polygone à translater
		IGeometry polytest;

		// Tests de translation

		ArrayList<IDirectPosition> validPoints = new ArrayList<IDirectPosition>();

		for (double dx=-MAX_DISTANCE; dx<=MAX_DISTANCE; dx+=GRID_RESOLUTION){

			for (double dy=-MAX_DISTANCE; dy<=MAX_DISTANCE; dy+=GRID_RESOLUTION){

				// Test de distance
				if (dx*dx+dy*dy > MAX_DISTANCE*MAX_DISTANCE){continue;}

				// Translation
				polytest = polygon2.translate(dx, dy, 0);

				// Test d'inclusion
				if (polygon1.contains(polytest)){

					validPoints.add(polytest.centroid());

				}
			}

		}

		// Alpha shape
		SwingingArmNonConvexHull alphaShapeBuilder = new SwingingArmNonConvexHull(validPoints, 1.42*GRID_RESOLUTION);
		IGeometry output = alphaShapeBuilder.compute();

		return output;

	}

}

