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

package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * méthodes statiques de calcul d'indices de forme (de lignes et de surfaces).
 * 
 * English : Measures of shapes
 * 
 * @author  Mustière/Sheeren/Grosso
 */

public abstract class IndicesForme {

	//////////////////////////////////////////
	// INDICES SUR DES SURFACES
	//////////////////////////////////////////

	/** Indice de circularité de Miller (pour des surfaces)
	 * Valeur entre 0 et 1.
	 * Vaut 1 si le polygone est un cercle, 0 si il est de surface nulle.
	 * définition = 4*pi*surface/perimetre^2
	 * Conseil : le seuil de 0.95 est adapté pour des ronds points dans un réseau routier.
	 */
	public static double indiceCompacite(GM_Polygon poly) {
		if (Operateurs.surface(poly) == 0) return 0;
		if (poly.coord().size() < 4) return 0;
		double perimetre = poly.perimeter();
		if ( perimetre == 0 ) return 0;
		double surface = poly.area();
		return 4*(Math.PI)*surface/Math.pow(perimetre,2);
	}

	/** Coefficient de compacité de Gravelius (pour des surfaces)
	 * Non borné : supérieur ou égal à 1 (cercle) .
	 * définition = perimetre/2*Racine(Pi*surface)
	 */
	public static double indiceCompaciteGravelius(GM_Polygon poly) {
		double perimetre = poly.length();
		double surface = poly.area();
		return perimetre/2*(Math.sqrt(Math.PI*surface));
	}

	/** Diamêtre d'une surface: plus grande distance entre 2 points de la
	 * frontière de la surface considérée.
	 * 
	 * English: diameter of a surface
	 * 
	 * @param A GM_Object
	 * @return -1 si A n'est pas une surface, le diamêtre sinon
	 */
	public static double diametreSurface(GM_Object A) {
		if (A.area()==0) return -1;

		DirectPositionList pts = A.coord();
		double dist, diametre = 0;

		Iterator<DirectPosition> itPts = pts.getList().iterator();
		while (itPts.hasNext()){
			DirectPosition dp = itPts.next();
			Iterator<DirectPosition> itPts2 = pts.getList().iterator();
			while (itPts2.hasNext()){
				DirectPosition dp2 = itPts2.next();
				dist = Distances.distance2D(dp,dp2);
				if ( dist > diametre ) diametre = dist;
			}
		}
		return diametre;
	}

	//////////////////////////////////////////
	// INDICES SUR DES LIGNES
	//////////////////////////////////////////
	/** méthode qui détermine si la liste de points passée en entrée est rectiligne.
	 *  Une ligne est considérée rectiligne si les angles entre les segments qui
	 *  la constitue ne sont pas trop forts (inférieur au seuil en paramètre en radians).
	 *  défaut : dépend de l'échantillonage des courbes, des critères de courbure
	 *  seraient plus stables.
	 * 
	 *  English: is the line straight?
	 */
	public static boolean rectiligne(GM_LineString ligne,double toleranceAngulaire){
		int i;
		Angle ecartTrigo;
		double angle;
		double angleMin = Math.PI - toleranceAngulaire;
		double angleMax = Math.PI + toleranceAngulaire;
		DirectPositionList listePts = ligne.getControlPoint();
		for (i=0;i<listePts.size()-2;i++){
			ecartTrigo = Angle.angleTroisPoints(listePts.get(i),listePts.get(i+1),listePts.get(i+2));
			angle = ecartTrigo.getValeur();
			if ((angle>angleMax) || (angle<angleMin)) return false ;
		}
		return true;
	}

}
