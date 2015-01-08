package fr.ign.cogit.geoxygene.contrib.algorithms;

import java.util.ArrayList;

import org.geotools.gml3.MultiSurface;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

// =================================================================================
// Classe d'utilitaires pour le calcul des sommes de Minkowski					   |
// ---------------------------------------------------------------------------------
// ALGORITHMES 																	   |
// ---------------------------------------------------------------------------------
// convexSumOfMinkowski(A,B) : somme entre 2 polygones convexes (avec ou sans ref) |
// ---------------------------------------------------------------------------------
// sumOfMinkowski(A,B) : somme entre 2 polygones quelconques (avec ou sans ref)    |
// ---------------------------------------------------------------------------------
// sumOfMinkowskiFromCenter(A,B) : somme avec référence au centroïd de B           |
// ---------------------------------------------------------------------------------
// inverseSumOfMinkowskiFromCenter(A,B) = sumOfMinkowskiFromCenter(A,-B)           |
// ---------------------------------------------------------------------------------
// substractionOfMinkowskiFromCenter(A,B) : soustraction de 2 polygones qcq        |
// ---------------------------------------------------------------------------------
// isConvex(A): true si le polygone A est convexe                                  |
// ---------------------------------------------------------------------------------
// Date : 17/07/2014                                                               |
// =================================================================================
public class Minkowski {


	// -----------------------------------------------------------------------------
	// Fonction de calcul de la somme de minkowski de deux polygones convexes
	// Entrée : polygone 1, polygone 2 (convexes)
	// Sortie : polygone somme
	// -----------------------------------------------------------------------------
	public static IGeometry convexSumOfMinkowski(IGeometry polygon1, IGeometry polygon2){

		IGeometry output;

		// Ensemble de points
		GM_MultiPoint points = new GM_MultiPoint();

		// Calcul des points sommes
		for (int i=0; i<polygon1.coord().size()-1; i++){

			for (int j=0; j<polygon2.coord().size()-1; j++){

				DirectPosition p1 = (DirectPosition) polygon1.coord().get(i);
				DirectPosition p2 = (DirectPosition) polygon2.coord().get(j);

				points.add(new GM_Point(new DirectPosition(p1.getX()+p2.getX(), p1.getY()+p2.getY())));

			}

		}

		// Calcul de l'enveloppe convexe

		output = points.convexHull();


		return output;

	}


	// -----------------------------------------------------------------------------
	// Fonction de calcul de la somme de minkowski de deux polygones convexes
	// Spécification d'un point de référence pour le polygone 2 (structurant)
	// Entrée : polygone 1, polygone 2 (convexes), point de référence
	// Sortie : polygone somme
	// -----------------------------------------------------------------------------
	public static IGeometry convexSumOfMinkowski(IGeometry polygon, IGeometry structure, IDirectPosition ref){

		// Référencement de l'élément structurant
		IGeometry polygon1 = (IGeometry) polygon.clone();
		IGeometry polygon2 = (IGeometry) structure.translate(-ref.getX(), -ref.getY(), 0).clone();

		return convexSumOfMinkowski(polygon1, polygon2);

	}

	// -----------------------------------------------------------------------------
	// Fonction booléenne de convexité d'un polygone
	// Entrée : géométrie
	// Sortie : booléen
	// -----------------------------------------------------------------------------
	public static boolean isConvex(IGeometry polygone){

		IGeometry convexHull = polygone.convexHull();

		return ((convexHull.area()-polygone.area())/polygone.area() < 0.001);

	}


	// -----------------------------------------------------------------------------
	// Fonction de calcul de la somme de minkowski de deux polygones quelconques
	// Entrée : polygone 1, polygone 2
	// Sortie : polygone
	// -----------------------------------------------------------------------------
	public static IGeometry sumOfMinkowski(IGeometry polygon1, IGeometry polygon2){

		// Sortie
		IGeometry output = null;

		// Décompositions convexes
		MultiSurface ms1 = (MultiSurface) PolygonMeshBuilder.triangulate(polygon1);
		MultiSurface ms2 = (MultiSurface) PolygonMeshBuilder.triangulate(polygon2);

		// Combinaisons de sommes de Minkowski

		ArrayList<IGeometry> COMBINAISONS = new ArrayList<IGeometry>();

		for (int i=0; i<ms1.getNumGeometries(); i++){

			for (int j=0; j<ms2.getNumGeometries(); j++){

				IGeometry geom1 = (IGeometry) ms1.getGeometryN(i);
				IGeometry geom2 = (IGeometry) ms2.getGeometryN(i);

				COMBINAISONS.add(sumOfMinkowski(geom1, geom2));

			}

		}

		// Recombinaisons par unions

		output = COMBINAISONS.get(0);

		for (int k=1; k<COMBINAISONS.size(); k++){

			output = output.union(COMBINAISONS.get(k));

		}


		return output;

	}


	// -----------------------------------------------------------------------------
	// Fonction de calcul de minkowski sur 2 polygones quelconques avec ref
	// Entrée : polygone 1, polygone 2, référence
	// Sortie : polygone
	// -----------------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public static IGeometry sumOfMinkowski(IGeometry polygon1, IGeometry polygon2, IDirectPosition ref){

		// Sortie
		IGeometry output = null;

		// Décompositions convexes
		GM_MultiSurface ms1 = (GM_MultiSurface) PolygonMeshBuilder.triangulate(polygon1);
		GM_MultiSurface ms2 = (GM_MultiSurface) PolygonMeshBuilder.triangulate(polygon2);

		// Combinaisons de sommes de Minkowski

		ArrayList<IGeometry> COMBINAISONS = new ArrayList<IGeometry>();

		for (int i=0; i<ms1.size(); i++){

			for (int j=0; j<ms2.size(); j++){

				IGeometry geom1 = (IGeometry) ms1.get(i);
				IGeometry geom2 = (IGeometry) ms2.get(j);

				COMBINAISONS.add(convexSumOfMinkowski(geom1, geom2, ref));

			}

		}

		// Recombinaisons par unions

		output = COMBINAISONS.get(0);

		for (int k=1; k<COMBINAISONS.size(); k++){

			output = output.union(COMBINAISONS.get(k));

		}


		return output;

	}

	// -----------------------------------------------------------------------------
	// Fonction de calcul de la somme Minkowski sur deux polygones quelconques
	// Référence au centre de masse du second polygone
	// Entrée : polygone 1, polygone 2
	// Sortie : polygone
	// -----------------------------------------------------------------------------
	public static IGeometry sumOfMinkowskiFromCenter(IGeometry polygon1, IGeometry polygon2){

		return sumOfMinkowski(polygon1, polygon2, polygon2.centroid());

	}

	// -----------------------------------------------------------------------------
	// Fonction de calcul de la somme de Minkowski par la transposée de l'éléments structurant
	// Référence au centre de masse du second polygone
	// Entrée : polygone 1, polygone 2
	// Sortie : polygone
	// -----------------------------------------------------------------------------
	public static IGeometry inverseSumOfMinkowskiFromCenter(IGeometry polygon1, IGeometry polygon2){

		// Renversement du polygone 2
		IGeometry polygon2bis = (IGeometry) polygon2.clone();

		for (int i=0; i<polygon2.coord().size(); i++){

			DirectPosition pos = (DirectPosition) polygon2bis.coord().get(i);

			polygon2bis.coord().get(i).setX(polygon2.centroid().getX()-pos.getX());
			polygon2bis.coord().get(i).setY(polygon2.centroid().getY()-pos.getY());

		}

		return sumOfMinkowski(polygon1, polygon2bis, polygon2bis.centroid());

	}

	// -----------------------------------------------------------------------------
	// Fonction de calcul de la soustraction de Minkowski
	// Référence au centre de masse du second polygone
	// A - B = comp(comp(A)+(-B)) avec :
	// (-) la soustraction de Minkowski
	// (+) la somme de Minkowski
	// (-B) le polygone transposée de B {-b tq b appartient à B}
	// Entrée : polygone 1, polygone 2
	// Sortie : polygone
	// -----------------------------------------------------------------------------
	public static IGeometry substractionOfMinkowskiFromCenter(IGeometry polygon1, IGeometry polygon2){
		
		// Création de l'univers Omega
		IGeometry box = polygon1.buffer(10).mbRegion();
		
		// Complémentaire de la parcelle dans Omega
		IGeometry parcelle = box.difference(polygon1);
		
		// Duplicata de l'élément structurant
		IGeometry batiment = (IGeometry) polygon2.clone();
		
		// Calcul de la somme inverse de Minkowski
		IGeometry temp = Minkowski.inverseSumOfMinkowskiFromCenter(parcelle, batiment);
		
		// On teste si l'espace résultant est vide
		if (((IPolygon)temp).getInterior().size() == 0){return null;}
		
		// Récupération du complémentaire dans Omega
		IGeometry tempclear = (IGeometry) temp.clone();
		((IPolygon)tempclear).getInterior().clear();
		IGeometry output = tempclear.difference(temp);
	
		return output;
		
	}


}

