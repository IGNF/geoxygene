/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

/** NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait hériter GM_CurveSegment de GM_Curve.
 *
 * <P> Définition de la norme : les classes GM_Curve et GM_CurveSegment représentent toutes deux des géométries à une dimension, et partagent donc plusieurs signatures d'opération.
 * Celles-ci sont définies dans l'interface GM_GenericCurve.
 * La paramétrisation employée dans les méthodes se fait par la longueur de l'arc (absisse curviligne) ou par une autre paramétrisation.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


interface GM_GenericCurve {

	/**
	 * Retourne le DirectPosition du premier point. Différent de l'opérateur "boundary" car renvoie la valeur du point et non pas l'objet géométrique représentatif.
	 */
	DirectPosition startPoint();


	/**
	 * Retourne le DirectPosition du dernier point. Différent de l'opérateur "boundary" car renvoie la valeur du point et non pas l'objet géométrique représentatif.
	 */
	DirectPosition endPoint();


	/** NON IMPLEMENTE.
	 * Renvoie un point à l'abcsisse curviligne s.
	 */
	// NORME : le paramètre en entree est de type Distance.
	DirectPosition param(double s);


	/** NON IMPLEMENTE.
	 * Vecteur tangent a la courbe, à l'abscisse curviligne  passée en paramètre. Le vecteur résultat est normé.
	 */
	// NORME : le paramètre en entree est de type Distance.
	//     Vecteur tangent(double s);


	/**
	 * Renvoie O pour une GM_Curve.
	 * Pour un GM_CurveSegment, égal au endParam du précedent segment dans la segmentation (0 pour le premier segment).
	 */
	// NORME : le résultat est de type Distance.
	double startParam();


	/**
	 * Longueur de la courbe pour une GM_Curve. Pour un GM_CurveSegment, égale à startParam plus la longueur du segment.
	 */
	// NORME : le résultat est de type Distance.
	double endParam();


	/** NON IMPLEMENTE.
	 * Renvoie le paramètre au point P (le paramètre étant a priori la distance).
	 * Si P n'est pas sur la courbe, on cherche alors pour le calcul le point le plus proche de P sur la courbe
	 * (qui est aussi renvoyé en résultat).
	 * On renvoie en général une seule distance, sauf si la courbe n'est pas simple.
	 */
	// NORME : le résultat est de type Distance.
	List<?> paramForPoint(DirectPosition P);


	/** NON IMPLEMENTE.
	 * Représentation alternative d'une courbe comme l'image continue d'un intervalle de réels,
	 * sans imposer que cette paramétrisation représente la longueur de la courbe,
	 * et sans imposer de restrictions entre la courbe et ses segments.
	 * Utilité : pour les courbes paramétrées,  pour construire une surface paramétrée.
	 */
	DirectPosition constrParam(double cp);


	/** NON IMPLEMENTE.
	 * Paramètre au startPoint pour une courbe paramétrée, c'est-à-dire : constrParam(startConstrParam())=startPoint().
	 */
	double startConstrParam();


	/** NON IMPLEMENTE.
	 * Paramètre au endPoint pour une courbe paramétrée, c'est-à-dire : constrParam(endConstrParam())=endPoint().
	 */
	double endConstrParam();


	/** NON IMPLEMENTE.
	 * Longueur entre 2 points.
	 */
	// NORME : le résultat est de type Length.
	double length(GM_Position p1, GM_Position p2);


	/** NON IMPLEMENTE.
	 * Longueur d'une courbe paramétrée "entre 2 réels".
	 */
	// NORME : le résultat est de type Length.
	double length(double cparam1, double cparam2);


	/**
	 * Approximation linéaire d'une courbe avec les points de contrôle.
	 * Le  paramètre spacing indique la distance maximum entre 2 points de contrôle;
	 * le paramètre  offset indique la distance maximum entre la polyligne générée et la courbe originale.
	 * Si ces 2 paramètres sont à 0, alors aucune contrainte n'est imposée.
	 * Le paramètre tolérance permet d'éliminer les points consécutifs doublons qui peuvent apparaître quand la courbe est composée de plusieurs segments.
	 */
	// NORME : spacing et offset sont de type Distance. tolerance n'est pas en paramètre.
	GM_LineString asLineString (double spacing, double offset, double tolerance) ;
}
