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
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** méthodes statiques de calcul de distance.
 * 
 * English: Computation of distances (static methods)
 * 
 * @author  Mustière/Bonin
 * @version 1.0
 */

public abstract class Distances {

	// Organisation du code:
	// - Distances entre points
	// - Distances entre un point et un autre type de géométrie
	// - Distances entre lignes
	// - Distances entre surfaces



	//////////////////////////////////////////////////////////////
	//                                                          //
	//                Distances entre points                    //
	//                                    	                    //
	//////////////////////////////////////////////////////////////

	/** Distance euclidienne entre 2 points (en 2D ou 3D si les points ont un Z). */
	public static double distance (DirectPosition dp1, DirectPosition dp2)   {
		if (!Double.isNaN(dp1.getZ()) && !Double.isNaN(dp2.getZ())) {
			return Math.sqrt(Math.pow(dp1.getX()-dp2.getX(),2) +
					Math.pow(dp1.getY()-dp2.getY(),2) +
					Math.pow(dp1.getZ()-dp2.getZ(),2));
		}
		return Math.sqrt(Math.pow(dp1.getX()-dp2.getX(),2) +
				Math.pow(dp1.getY()-dp2.getY(),2));
	}

	/** Distance euclidienne calculée en 2 dimensions XY, même sur des objets 3D. */
	public static double distance2D (DirectPosition dp1, DirectPosition dp2)   {
		return Math.sqrt(Math.pow(dp1.getX()-dp2.getX(),2) +
				Math.pow(dp1.getY()-dp2.getY(),2));
	}

	/** Est-ce que les deux points sont distants de moins du seuil passé en paramètre ?
	 *  méthode optimisée pour accélérer les requêtes spatiales. */
	public static boolean proche(DirectPosition dp1, DirectPosition dp2, double distance) {
		if ( Math.abs(dp1.getX()-dp2.getX()) > distance ) return false;
		if ( Math.abs(dp1.getY()-dp2.getY()) > distance ) return false;
		if ( distance(dp1,dp2) > distance ) return false;
		return true;
	}



	//////////////////////////////////////////////////////////////
	//                                                          //
	//	Distances entre un point et un autre type de géométrie  //
	//                                                          //
	//////////////////////////////////////////////////////////////

	/** Distance euclidienne du point M au segment [A,B] */
	public static double distancePointSegment(DirectPosition M, DirectPosition A, DirectPosition B) {
		return distance(M,Operateurs.projection(M,A,B));
	}

	/** Distance euclidienne d'un point P à une ligne. */
	public static double distance(DirectPosition  M, GM_LineString L) {
		DirectPositionList listePoints = L.coord();
		double distmin, dist;

		distmin = distance(listePoints.get(0),M);
		for (int i=0;i<listePoints.size()-1;i++) {
			dist = distancePointSegment(M,listePoints.get(i),listePoints.get(i+1));
			if ( dist < distmin ) distmin = dist;
		}
		return(distmin);
	}



	///////////////////////////////////////////////////////////
	//                                                       //
	//	  Distances entre lignes                             //
	//                                                       //
	///////////////////////////////////////////////////////////

	/** Approximation de la Première composante de Hausdorff d'une ligne vers une autre.
	 * Elle est calculee comme le maximum des distances des points intermédiaires
	 * de la Première ligne L1 à l'autre ligne L2.
	 */
	public static double premiereComposanteHausdorff(GM_LineString L1, GM_LineString L2) {
		DirectPositionList listePoints = L1.coord();
		double dist , distmax = 0;

		for (int i=0;i<listePoints.size();i++) {
			dist = distance(listePoints.get(i), L2);
			if ( dist > distmax ) distmax = dist;
		}
		return distmax;
	}

	/** Approximation (très proche) de la distance de Hausdorff entre deux lignes.
	 * Elle est calculee comme le maximum des distances d'un point intermediaire
	 * d'une des lignes a l'autre ligne. Dans certains cas cette definition
	 * diffère de la définition theorique pure car la distance de Hausdorff ne se
	 * realise pas necessairement sur un point intermediaire. Mais cela est rare
	 * sur des données réelles. Cette implementation est un bon compromis entre
	 * simplicité et précision.
	 */
	public static double hausdorff(GM_LineString L1, GM_LineString L2)   {
		return Math.max(premiereComposanteHausdorff(L1,L2), premiereComposanteHausdorff(L2,L1));
	}

	/** Distance de Hausdorff entre un point P et une ligne L.
	 * c'est-à-dire distance au point P du point intermédiaire de
	 * la ligne L le plus éloigné du point P.
	 */
	public static double hausdorff(GM_LineString L, GM_Point P)   {
		Iterator<DirectPosition> itPts = L.coord().getList().iterator();
		DirectPosition point;
		double distmax = 0, dist;

		while (itPts.hasNext()) {
			point = itPts.next();
			dist = distance(point, P.getPosition());
			if ( dist > distmax ) distmax = dist;
		}
		return distmax;
	}

	/** Distance moyenne entre deux polylignes,
	 * définie comme le rapport de l'aire séparant deux polylignes
	 * sur la moyenne de leurs longueurs.
	 * 
	 * IMPORTANT: la méthode suppose que les lignes sont orientées globalement
	 * dans le même sens.
	 */
	public static double distanceMoyenne(GM_LineString L1, GM_LineString L2)   {
		GM_Polygon poly;
		GM_LineString perimetre;
		Iterator<DirectPosition> itPts;

		//fabrication de la surface delimitée par les lignes
		perimetre = new GM_LineString();
		itPts=L1.coord().getList().iterator();
		while (itPts.hasNext()) {
			DirectPosition pt = itPts.next();
			perimetre.addControlPoint(0,pt);
		}
		itPts=L2.coord().getList().iterator();
		while (itPts.hasNext()) {
			DirectPosition pt = itPts.next();
			perimetre.addControlPoint(0,pt);
		}
		perimetre.addControlPoint(L1.endPoint());
		poly = new GM_Polygon(perimetre);

		return 2*poly.area()/(L1.length()+L2.length()) ;
	}

	/** Mesure d'écart entre deux polylignes, défini comme une approximation de la
	 * surface séparant les polylignes.
	 * Plus précisément, cet écart est égal à la somme, pour chaque point P de L1,
	 * de (distance de P à L2) * (moyenne des longueurs des segments autour de P)
	 * 
	 * NB: Ce n'est pas une distance au sens mathématique du terme,
	 * et en particulier cet écart n'est pas symétrique: ecart(L1,L2) != ecart(L2,L1)
	 */
	public static double ecartSurface(GM_LineString L1, GM_LineString L2) {
		double ecartTotal=0, distPt, long1, long2;
		DirectPositionList pts = L1.coord();
		for(int i=0;i<pts.size();i++) {
			distPt = distance(pts.get(i), L2);
			if ( i==0 ) long1 = 0;
			else long1 = distance(pts.get(i),pts.get(i-1));
			if ( i==pts.size()-1 ) long2 = 0;
			else long2 = distance(pts.get(i),pts.get(i+1));
			ecartTotal = ecartTotal + distPt*(long1+long2)/2;
		}
		return ecartTotal;

	}



	////////////////////////////////////////////////////////////
	//                                                        //
	//          Distances entre surfaces                      //
	//                                                        //
	////////////////////////////////////////////////////////////

	/** Distance surfacique entre deux GM_Polygon.
	 * 
	 * définition : 1 - surface(intersection)/surface(union)
	 * Ref [Vauglin 97]
	 * 
	 * NB: renvoie 2 en cas de problème lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 */
	public static double distanceSurfacique(GM_Polygon A, GM_Polygon B)   {
		GM_Object inter = A.intersection(B);
		if ( inter == null ) return 2;
		GM_Object union = A.union(B);
		if ( union == null ) return 1;
		return 1 - inter.area()/union.area();
	}

	/** Distance surfacique entre deux GM_MultiSurface.
	 * 
	 * définition : 1 - surface(intersection)/surface(union)
	 * Ref [Vauglin 97]
	 * 
	 * NB: renvoie 2 en cas de problème lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 */
	public static double distanceSurfacique(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B)   {
		GM_Object inter = A.intersection(B);
		//en cas de problème d'intersection avec JTS, la méthode retourne 2
		if ( inter == null ) return 2;
		GM_Object union = A.union(B);
		if ( union == null ) return 1;
		return 1 - inter.area()/union.area();
	}

	/** Distance surfacique "robuste" entre deux polygones.
	 * 
	 * Il s'agit ici d'une pure bidouille pour contourner certains bugs de JTS:
	 * Si JTS plante au calcul d'intersection, on filtre les surfaces avec Douglas et Peucker,
	 * progressivement avec 10 seuils entre min et max. Min et Max doivent être fixer donc de
	 * l'ordre de grandeur de la précision des données sinon le calcul risque d'être trop faussé.
	 * 
	 * définition : 1 - surface(intersection)/surface(union)
	 * Ref [Vauglin 97]
	 * 
	 * NB: renvoie 2 en cas de problème lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 * */
	public static double distanceSurfaciqueRobuste(GM_Polygon A, GM_Polygon B, double min, double max)   {
		GM_Object inter = Operateurs.intersectionRobuste(A,B,min,max);
		//en cas de problème d'intersection avec JTS, la méthode retourne 2
		if ( inter == null ) return 2;
		GM_Object union = A.union(B);
		if ( union == null ) return 1;
		return 1 - inter.area()/union.area();
	}

	/** Distance surfacique entre deux GM_MultiSurface.
	 * 
	 * Cette méthode contourne des bugs de JTS, qui sont trop nombreux sur les agrégats.
	 * En contrepartie, cette méthode n'est valable que si les GM_Polygon composant A [resp. B]
	 * ne s'intersectent pas entre elles.
	 * 
	 * définition : 1 - surface(intersection)/surface(union)
	 * Ref [Vauglin 97]
	 * 
	 * NB: renvoie 2 en cas de problème résiduer lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 */
	public static double distanceSurfaciqueRobuste(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B)   {
		double inter = surfaceIntersection(A,B);
		if (inter == -1) {
			System.out.println("Plantage JTS, renvoi 2 à la distance surfacique de deux multi_surfaces");
			return 2;
		}
		return 1-inter/(A.area()+B.area()-inter);
	}

	/** Surface de l'intersection.
	 * 
	 * Cette méthode contourne des bugs de JTS, qui sont trop nombreux sur les agrégats.
	 * En contrepartie, cette méthode n'est valable que si les GM_Polygon composant A [resp. B]
	 * ne s'intersectent pas entre elles.
	 * 
	 * NB: renvoie -1 en cas de problème résiduer lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 */
	public static double surfaceIntersection(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B) {
		Iterator<GM_OrientableSurface> itA = A.getList().iterator();
		Iterator<GM_OrientableSurface> itB;
		double inter=0;

		while (itA.hasNext()) {
			GM_Surface surfA = (GM_Surface) itA.next();
			itB = B.getList().iterator();
			while (itB.hasNext()) {
				GM_Surface surfB = (GM_Surface) itB.next();
				if ( surfB.intersection(surfA)== null ) {
					System.out.println("Plantage JTS, renvoi -1 à l'intersection de deux multi_surfaces");
					return -1;
				}
				inter = inter+surfB.intersection(surfA).area();
			}
		}
		return inter;
	}

	/** Surface de l'union.
	 * 
	 * Cette méthode contourne des bugs de JTS, qui sont trop nombreux sur les agrégats.
	 * En contrepartie, cette méthode n'est valable que si les GM_Polygon composant A [resp. B]
	 * ne s'intersectent pas entre elles.
	 * 
	 * NB: renvoie -1 en cas de problème résiduer lors du calcul d'intersection avec JTS
	 *     (bug en particulier si les surfaces sont dégénérées ou trop complexes).
	 */
	public static double surfaceUnion(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B) {
		double inter = surfaceIntersection(A,B);
		if (inter == -1) {
			System.out.println("Plantage JTS, renvoi -1 à l'union de deux 2 multi_surfaces");
			return -1;
		}
		return A.area()+B.area()-inter;
	}

	/** Mesure dite "Exactitude" entre 2 surfaces.
	 * Ref : [Bel Hadj Ali 2001]
	 * 
	 * définition : Surface(A inter B) / Surface(A)
	 */
	public static double exactitude(GM_Polygon A, GM_Polygon B) {
		GM_Object inter = A.intersection(B);
		if ( inter == null ) return 0;
		return inter.area()/A.area();
	}

	/** Mesure dite "Complétude" entre 2 surfaces.
	 * Ref : [Bel Hadj Ali 2001]
	 * 
	 * définition : Surface(A inter B) / Surface(B)
	 */
	public static double completude(GM_Polygon A, GM_Polygon B) {
		return exactitude(B,A);
	}

	/** Mesure dite "Exactitude" entre 2 GM_MultiSurface.
	 * Ref : [Bel Hadj Ali 2001]
	 * définition : Surface(A inter B) / Surface(A)
	 */
	public static double exactitude(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B) {
		GM_Object inter = A.intersection(B);
		if ( inter == null ) return 0;
		return inter.area()/A.area();
	}

	/** Mesure dite "Complétude" entre 2 GM_MultiSurface.
	 * 
	 * Ref : [Bel Hadj Ali 2001]
	 * définition : Surface(A inter B) / Surface(B)
	 */
	public static double completude(GM_MultiSurface<GM_OrientableSurface> A, GM_MultiSurface<GM_OrientableSurface> B) {
		return exactitude(B,A);
	}

	/** Mesure d'association entre deux surfaces (cf. [Bel Hadj Ali 2001]).
	 *  <BR> <STRONG> Definition :  </STRONG> associationSurfaces(A,B) = vrai si
	 * <UL>
	 * <LI> Surface(intersection) > min  (min etant la resolution minimum des deux bases) </LI>
	 * <LI> ET (Surface(intersection) > surface(A) * coeff </LI>
	 * <LI> 	OU Surface(intersection) > surface(B) * coeff ) </LI>
	 * </UL>
	 * <BR> associationSurfaces(A,B) = faux sinon.
	 *
	 */
	public static boolean associationSurfaces (GM_Object A, GM_Object B, double min, double coeff) {
		GM_Object inter = A.intersection(B);
		if (inter == null) return false;
		double interArea = inter.area();
		if (interArea < min) return false;
		if (interArea > A.area()*coeff) return true;
		if (interArea > B.area()*coeff) return true;
		return false;
	}


	/** Test d'association "robuste" entre deux surfaces (cf. [Bel Hadj Ali 2001]).
	 * 
	 * Il s'agit ici d'une pure bidouille pour contourner certains bugs de JTS:
	 * Si JTS plante au calcul , on filtre les surfaces avec Douglas et Peucker,
	 * progressivement avec 10 seuils entre min et max. Min et Max doivent être fixer donc de
	 * l'ordre de grandeur de la précision des données sinon le calcul risque d'être trop faussé.
	 * 
	 *  <BR> <STRONG> Definition :  </STRONG> associationSurfaces(A,B) = vrai si
	 * <UL>
	 * <LI> Surface(intersection) > min  (min etant la resolution minimum des deux bases) </LI>
	 * <LI> ET (Surface(intersection) > surface(A) * coeff </LI>
	 * <LI> 	OU Surface(intersection) > surface(B) * coeff ) </LI>
	 * </UL>
	 * <BR> associationSurfaces(A,B) = faux sinon.
	 *
	 */
	public static boolean associationSurfacesRobuste(GM_Object A, GM_Object B, double min, double coeff, double minDouglas, double maxDouglas) {
		GM_Object inter = Operateurs.intersectionRobuste(A,B,minDouglas, maxDouglas);
		if (inter == null) return false;
		double interArea = inter.area();
		if (interArea < min) return false;
		if (interArea > A.area()*coeff) return true;
		if (interArea > B.area()*coeff) return true;
		return false;
	}

}