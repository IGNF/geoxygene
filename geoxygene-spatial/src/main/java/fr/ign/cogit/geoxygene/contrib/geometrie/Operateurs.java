/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

/**
 * En vrac un ensemble de méthodes statiques qui manipulent des géométries :
 * projections, abscisse curviligne, décalage, orientation...
 * <p>
 * CONTIENT des méthodes de : Projections d'un point Manipulation de l'abscisse
 * curviligne d'une ligne Mesures sur un polygone Offset d'une ligne (décalage)
 * Echantillonage d'une ligne Regression linéaire et beaucoup d'autres choses
 * très diverses
 * <p>
 * ATTENTION: certaines méthodes n'ont pas été conçues ni testées pour des
 * coordonnées 3D
 * <p>
 * English: Very very diverse set of methods on geometries
 * 
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Frédéric Rousseaux
 * @author Eric Grosso
 * @author Arnaud Lafragueta
 * @author Julien Perret
 */
public abstract class Operateurs {
	private static Logger logger = Logger.getLogger(Operateurs.class.getName());

	// ////////////////////////////////////////////////////////////////////
	// Projections d'un point
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Abscisse curviligne de M sur le segment [A,B].
	 * <p>
	 * English: Parameter of M on a [A,B].
	 */
	public static double paramForPoint(IDirectPosition m, IDirectPosition a, IDirectPosition b) {
		Vecteur ab = new Vecteur(a, b);
		boolean to2d = Double.isNaN(m.getZ()) || Double.isNaN(a.getZ()) || Double.isNaN(b.getZ());
		if (to2d) {
			ab.setZ(Double.NaN);
		}
		if (ab.norme() == 0) {
			return 0; // cas ou A et B sont confondus
		}
		Vecteur u_ab = ab.vectNorme();
		Vecteur am = new Vecteur(a, m);
		if (to2d) {
			am.setZ(Double.NaN);
		}
		double lambda = am.prodScalaire(u_ab);
		if (lambda <= 0) {
			return 0; // Cas ou M se projete en A sur le segment [AB]
		}
		double length = (to2d) ? a.distance2D(b) : a.distance(b);
		if (lambda >= length) {
			return length; // Cas ou M se projete en B sur le segment [AB]
		}
		// Cas ou M se projete entre A et B
		return lambda;
	}

	public static IDirectPosition param(double param, IDirectPosition a, IDirectPosition b) {
		if (param == 0) {
			return a;
		}
		double distance = a.distance(b);
		if (param >= distance) {
			return b;
		}
		double dx = b.getX() - a.getX();
		double dy = b.getY() - a.getY();
		double dz = b.getZ() - a.getZ();
		double ratio = param / distance;
		return new DirectPosition(a.getX() + ratio * dx, a.getY() + ratio * dy, a.getZ() + ratio * dz);
	}

	/**
	 * Projection de M sur le segment [A,B].
	 * <p>
	 * English: Projects M on a [A,B].
	 */
	public static IDirectPosition projection(IDirectPosition m, IDirectPosition a, IDirectPosition b) {
		Vecteur ab = new Vecteur(a, b);
		boolean to2d = Double.isNaN(m.getZ()) || Double.isNaN(a.getZ()) || Double.isNaN(b.getZ());
		if (to2d) {
			ab.setZ(Double.NaN);
		}
		if (ab.norme() == 0) {
			return a; // cas ou A et B sont confondus
		}
		Vecteur u_ab = ab.vectNorme();
		Vecteur am = new Vecteur(a, m);
		if (to2d) {
			am.setZ(Double.NaN);
		}
		double lambda = am.prodScalaire(u_ab);
		if (lambda <= 0) {
			return a; // Cas ou M se projete en A sur le segment [AB]
		}
		if (lambda >= (to2d ? a.distance2D(b) : a.distance(b))) {
			return b; // Cas ou M se projete en B sur le segment [AB]
		}
		// Cas ou M se projete entre A et B
		return Operateurs.translate(a, u_ab.multConstante(lambda));
	}

	/**
	 * Projection du point sur la polyligne. En théorie, il peut y avoir
	 * plusieurs points projetés, mais dans ce cas cette méthode n'en renvoie
	 * qu'un seul (le premier dans le sens de parcours de la ligne).
	 * <p>
	 * English: Projects M on thelineString.
	 */
	public static IDirectPosition projection(IDirectPosition dp, ILineString ls) {
		IDirectPositionList listePoints = ls.coord();
		double d, dmin;
		IDirectPosition pt, ptmin;
		if (listePoints.size() <= 1) {
			return listePoints.get(0);
		}
		ptmin = Operateurs.projection(dp, listePoints.get(0), listePoints.get(1));
		dmin = dp.distance(ptmin);
		for (int i = 0; i < listePoints.size() - 1; i++) {
			pt = Operateurs.projection(dp, listePoints.get(i), listePoints.get(i + 1));
			d = dp.distance(pt);
			if (d < dmin) {
				ptmin = pt;
				dmin = d;
			}
		}
		return ptmin;
	}

	/**
	 * Projection du point sur la polyligne et insertion du point projeté dans
	 * la ligne.
	 * <P>
	 * English: Projects M on the lineString and return the line with the
	 * projected point inserted.
	 */
	public static ILineString projectionEtInsertion(IDirectPosition point, ILineString line) {
		IDirectPositionList points = line.coord();
		Operateurs.projectAndInsert(point, points.getList());
		GM_LineString newLine = new GM_LineString(points, false);
		return newLine;
	}

	/**
	 * Projection du point sur la polyligne et insertion du point projeté dans
	 * la ligne.
	 * <p>
	 * English: Projects M on the lineString and return the line with the
	 * projected point inserted.
	 */
	public static void projectAndInsert(IDirectPosition point, List<IDirectPosition> points) {
		projectAndInsertWithPosition(point, points);
	}

	/**
	 * Projection du point sur la polyligne et insertion du point projeté dans
	 * la ligne, et retourne la position de l'ajout
	 * <p>
	 * English: Projects M on the lineString and return the line with the
	 * projected point inserted.
	 */
	public static int projectAndInsertWithPosition(IDirectPosition point, List<IDirectPosition> points) {
		if (points.size() < 2) {
			return -1;
		}
		IDirectPosition ptmin = Operateurs.projection(point, points.get(0), points.get(1));
		double dmin = point.distance(ptmin);
		int imin = 0;
		for (int i = 1; i < points.size() - 1; i++) {
			IDirectPosition pt = Operateurs.projection(point, points.get(i), points.get(i + 1));
			double d = point.distance(pt);
			if (d < dmin) {
				ptmin = pt;
				dmin = d;
				imin = i;
			}
		}
		points.add(imin + 1, ptmin);

		return imin + 1;
	}

	/**
	 * Projection du point sur la polyligne et insertion du point projeté dans
	 * la ligne.
	 * <p>
	 * English: Projects M on the lineString and return the line with the
	 * projected point inserted.
	 */
	public static void projectAndInsertAll(IDirectPosition point, List<IDirectPosition> points) {
		if (points.size() < 2) {
			return;
		}
		for (int i = 0; i < points.size() - 1; i++) {
			IDirectPosition a = points.get(i);
			IDirectPosition b = points.get(i + 1);
			IDirectPosition pt = Operateurs.projection(point, a, b);
			if (pt != a && pt != b) {
				points.add(i + 1, pt);
				i++;
			}
		}
	}

	/**
	 * Projection du point sur l'aggregat; ATTENTION: ne fonctionne que si
	 * l'aggregat ne contient que des GM_Point et GM_LineString.
	 * 
	 * <p>
	 * En théorie, il peut y avoir plusieurs points projetés, mais dans ce cas
	 * cette méthode n'en renvoie qu'un seul.
	 * <p>
	 * English: Projects M on the agregate.
	 */
	public static IDirectPosition projection(IDirectPosition dp, IAggregate<IGeometry> aggr) {
		Iterator<IGeometry> itComposants = aggr.getList().iterator();
		double d = 0, dmin = Double.POSITIVE_INFINITY;
		IDirectPosition pt = null, ptmin = null;
		boolean geomOK;
		while (itComposants.hasNext()) {
			IGeometry composant = itComposants.next();
			geomOK = false;
			if (composant instanceof IPoint) {
				pt = ((IPoint) composant).getPosition();
				d = pt.distance(dp);
				geomOK = true;
			}
			if (composant instanceof ILineString) {
				pt = Operateurs.projection(dp, (ILineString) composant);
				d = pt.distance(dp);
				geomOK = true;
			}
			if (!geomOK) {
				System.out.println("Projection - Type de géométrie non géré: " + composant.getClass());
				continue;
			}
			if (d < dmin) {
				ptmin = pt;
				dmin = d;
			}
		}
		return ptmin;
	}

	// ////////////////////////////////////////////////////////////////////
	// Manipulation de l'abscisse curviligne d'une ligne
	// ////////////////////////////////////////////////////////////////////

	/**
	 * Coordonnées du point situé sur la ligne à l'abscisse curviligne passée en
	 * paramètre. Renvoie Null si l'abscisse est négative ou plus grande que la
	 * longueur de la ligne.
	 * <p>
	 * English: Point located at the curvilinear abscisse.
	 */
	public static IDirectPosition pointEnAbscisseCurviligne(ILineString ls, double abscisse) {
		int i;
		double l = 0;
		double d;
		IDirectPosition pt1, pt2;
		Vecteur v1;

		if (abscisse > ls.length() || abscisse < 0) {
			return null;
		}
		pt1 = ls.coord().get(0);
		for (i = 1; i < ls.coord().size(); i++) {
			pt2 = ls.coord().get(i);
			d = pt1.distance(pt2);
			if (d != 0) {
				if (l + d > abscisse) {
					v1 = new Vecteur(pt1, pt2);
					v1 = v1.multConstante((abscisse - l) / d);
					return Operateurs.translate(pt1, v1);
				}
				l = l + d;
				pt1 = pt2;
			}
		}
		return ls.coord().get(ls.coord().size() - 1);
	}

	/**
	 * Coordonnées du point situé sur la ligne à l'abscisse curviligne passée en
	 * paramètre. Renvoie Null si l'abscisse est négative ou plus grande que la
	 * longueur de la ligne.
	 * <p>
	 * English: Point located at the curvilinear abscisse of a multi linear
	 * geometry.
	 */
	public static IDirectPosition pointEnAbscisseCurviligne(IMultiCurve<? extends ILineString> multiLs,
			double abscisse) {

		if (abscisse < 0 || abscisse > multiLs.length() ) {
			return null;
		}

		for (ILineString ls : multiLs) {
			double lengthTemp = ls.length();

			if (lengthTemp < abscisse) {
				abscisse = abscisse - lengthTemp;
				continue;
			}

			return pointEnAbscisseCurviligne(ls, abscisse);

		}

		return null;
	}

	/**
	 * Abscisse curviligne du ieme point de la ligne ls.
	 * <p>
	 * English: curvilinear abscisse of the ith point.
	 */
	public static double abscisseCurviligne(ILineString ls, int i) {
		double abs = 0;
		for (int j = 0; j < i; j++) {
			abs = abs + ls.getControlPoint(j).distance(ls.getControlPoint(j + 1));
		}
		return abs;
	}

	/**
	 * Coordonnées du point situé sur au milieu de la ligne.
	 * <p>
	 * English: Point in the middle of the line.
	 */
	public static IDirectPosition milieu(ILineString ls) {
		return Operateurs.pointEnAbscisseCurviligne(ls, ls.length() / 2);
	}

	/**
	 * Renvoie le milieu de [A,B].
	 * <p>
	 * English: Point in the middle of [A,B].
	 */
	public static IDirectPosition milieu(IDirectPosition A, IDirectPosition B) {
		IDirectPosition M;
		if (!Double.isNaN(A.getZ()) && !Double.isNaN(B.getZ())) {
			M = new DirectPosition((A.getX() + B.getX()) / 2, (A.getY() + B.getY()) / 2, (A.getZ() + B.getZ()) / 2);
		} else {
			M = new DirectPosition((A.getX() + B.getX()) / 2, (A.getY() + B.getY()) / 2, Double.NaN);
		}
		return M;
	}

	/**
	 * Premiers points intermédiaires de la ligne ls, situés à moins de la
	 * longueur curviligne passée en paramètre du point initial. Renvoie null si
	 * la longueur est négative. Renvoie le premier point si et seulement si la
	 * longueur est 0. Renvoie tous les points si la longueur est supérieure à
	 * la longueur de la ligne NB: les points sont renvoyés dans l'ordre en
	 * partant du premier point.
	 * <p>
	 * English: First points of the line.
	 */
	public static IDirectPositionList premiersPoints(ILineString ls, double longueur) {
		int i;
		double l = 0;
		DirectPositionList listePts = new DirectPositionList();

		if (longueur < 0) {
			return null;
		}
		listePts.add(ls.getControlPoint(0));
		for (i = 1; i < ls.coord().size(); i++) {
			l = l + ls.getControlPoint(i - 1).distance(ls.getControlPoint(i));
			if (l > longueur) {
				break;
			}
			listePts.add(ls.getControlPoint(i));
		}
		return listePts;
	}

	/**
	 * Derniers points intermédiaires de la ligne ls, situés à moins de la
	 * longueur curviligne passée en paramètre du point final. Renvoie null si
	 * la longueur est négative. Renvoie le dernier point seulement si la
	 * longueur est 0. Renvoie tous les points si la longueur est supérieure à
	 * la longueur de la ligne. NB: les points sont renvoyés dans l'ordre en
	 * partant du dernier point (ordre inverse par rapport à la géométrie
	 * initiale).
	 * <p>
	 * English: Last points of the line.
	 */
	public static IDirectPositionList derniersPoints(ILineString ls, double longueur) {
		int i;
		double l = 0;
		DirectPositionList listePts = new DirectPositionList();
		int nbPts = ls.coord().size();

		if (longueur < 0) {
			return null;
		}
		listePts.add(ls.getControlPoint(nbPts - 1));
		for (i = nbPts - 2; i >= 0; i--) {
			l = l + ls.getControlPoint(i).distance(ls.getControlPoint(i + 1));
			if (l > longueur) {
				break;
			}
			listePts.add(ls.getControlPoint(i));
		}
		return listePts;
	}

	// ////////////////////////////////////////////////////////////////////
	// Mesures sur un polygone
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Barycentre 2D (approximatif). Il est défini comme le barycentre des
	 * points intermédiaires du contour, ce qui est très approximatif
	 * <p>
	 * English: Center of the points of the polygon.
	 */
	public static IDirectPosition barycentre2D(IPolygon poly) {
		IDirectPositionList listePoints = poly.coord();
		IDirectPosition barycentre;
		double moyenneX = 0;
		double moyenneY = 0;
		double sommeX = 0;
		double sommeY = 0;

		for (int i = 0; i < listePoints.size() - 1; i++) {
			sommeX = sommeX + listePoints.get(i).getX();
			sommeY = sommeY + listePoints.get(i).getY();
		}
		moyenneX = sommeX / (listePoints.size() - 1);
		moyenneY = sommeY / (listePoints.size() - 1);

		barycentre = new DirectPosition(moyenneX, moyenneY);
		return (barycentre);
	}

	// ////////////////////////////////////////////////////////////////////
	// Offset d'une ligne (décalage)
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Calcul d'un offset direct (demi-buffer d'une ligne, ou décalage à
	 * gauche). Le paramètre offset est la taille du décalage.
	 * <p>
	 * English: shift of a line on the left
	 */
	public static ILineString directOffset(ILineString ls, double offset) {
		IDirectPositionList listePoints = ls.coord();
		IDirectPosition point, pointPrec, pointSuiv, pointRes;
		Vecteur u, v, n;
		List<IDirectPosition> points = new ArrayList<IDirectPosition>();
		u = new Vecteur(listePoints.get(0), listePoints.get(1));
		u.setZ(0.0);
		pointRes = new DirectPosition(listePoints.get(0).getX() + offset * u.vectNorme().getY(),
				listePoints.get(0).getY() - offset * u.vectNorme().getX(), listePoints.get(0).getZ());
		points.add(pointRes);
		for (int j = 1; j < listePoints.size() - 1; j++) {
			pointPrec = listePoints.get(j - 1);
			point = listePoints.get(j);
			pointSuiv = listePoints.get(j + 1);
			u = new Vecteur(pointPrec, point);
			u.setZ(0);
			v = new Vecteur(point, pointSuiv);
			v.setZ(0);
			n = u.vectNorme().soustrait(v.vectNorme());
			if (u.prodVectoriel(v).getZ() > 0) {
				pointRes = new DirectPosition(point.getX() + offset * n.vectNorme().getX(),
						point.getY() + offset * n.vectNorme().getY(), point.getZ());
				points.add(pointRes);
			} else {
				pointRes = new DirectPosition(point.getX() - offset * n.vectNorme().getX(),
						point.getY() - offset * n.vectNorme().getY(), point.getZ());
				points.add(pointRes);
			}
		}
		u = new Vecteur(listePoints.get(listePoints.size() - 2), listePoints.get(listePoints.size() - 1));
		u.setZ(0.0);
		pointRes = new DirectPosition(listePoints.get(listePoints.size() - 1).getX() + offset * u.vectNorme().getY(),
				listePoints.get(listePoints.size() - 1).getY() - offset * u.vectNorme().getX(),
				listePoints.get(listePoints.size() - 1).getZ());
		points.add(pointRes);
		return new GM_LineString(points);
	}

	/**
	 * Calcul d'un offset indirect (demi-buffer d'une ligne, ou décalage à
	 * droite). Le paramètre offset est la taille du décalage.
	 * <p>
	 * English: shift of a line on the right
	 */
	public static ILineString indirectOffset(ILineString ls, double offset) {
		IDirectPositionList listePoints = ls.coord();
		IDirectPosition point, pointPrec, pointSuiv, pointRes;
		Vecteur u, v, n;
		List<IDirectPosition> points = new ArrayList<IDirectPosition>();
		u = new Vecteur(listePoints.get(0), listePoints.get(1));
		u.setZ(0);
		pointRes = new DirectPosition(listePoints.get(0).getX() - offset * u.vectNorme().getY(),
				listePoints.get(0).getY() + offset * u.vectNorme().getX(), listePoints.get(0).getZ());
		points.add(pointRes);
		for (int j = 1; j < listePoints.size() - 1; j++) {
			pointPrec = listePoints.get(j - 1);
			point = listePoints.get(j);
			pointSuiv = listePoints.get(j + 1);
			u = new Vecteur(pointPrec, point);
			u.setZ(0);
			v = new Vecteur(point, pointSuiv);
			v.setZ(0);
			n = u.vectNorme().soustrait(v.vectNorme());
			if (u.prodVectoriel(v).getZ() < 0) {
				pointRes = new DirectPosition(point.getX() + offset * n.vectNorme().getX(),
						point.getY() + offset * n.vectNorme().getY(), point.getZ());
				points.add(pointRes);
			} else {
				pointRes = new DirectPosition(point.getX() - offset * n.vectNorme().getX(),
						point.getY() - offset * n.vectNorme().getY(), point.getZ());
				points.add(pointRes);
			}
		}
		u = new Vecteur(listePoints.get(listePoints.size() - 2), listePoints.get(listePoints.size() - 1));
		u.setZ(0);
		pointRes = new DirectPosition(listePoints.get(listePoints.size() - 1).getX() - offset * u.vectNorme().getY(),
				listePoints.get(listePoints.size() - 1).getY() + offset * u.vectNorme().getX(),
				listePoints.get(listePoints.size() - 1).getZ());
		points.add(pointRes);
		return new GM_LineString(points);
	}

	// ////////////////////////////////////////////////////////////////////
	// Echantillonage
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Méthode pour suréchantillonner une GM_LineString. Des points
	 * intermédiaires écartés du pas sont ajoutés sur chaque segment de la ligne
	 * ls, à partir du premier point de chaque segment. (voir aussi
	 * echantillonePasVariable pour une autre méthode )
	 * <p>
	 * English: Resampling of a line
	 */
	public static ILineString echantillone(ILineString ls, double pas) {
		IDirectPosition point1, point2, Xins;
		Vecteur u1;
		int nseg, i;
		double longTronc;
		Double fseg;
		ILineString routeSurech = (ILineString) ls.clone();
		IDirectPositionList listePoints = routeSurech.coord();
		DirectPositionList listePointsEchant = new DirectPositionList();
		for (int j = 1; j < listePoints.size(); j++) {
			point1 = listePoints.get(j - 1);
			listePointsEchant.add(point1);
			point2 = listePoints.get(j);
			longTronc = point1.distance(point2);
			fseg = new Double(longTronc / pas);
			nseg = fseg.intValue();
			u1 = new Vecteur(point1, point2);
			for (i = 0; i < nseg - 1; i++) {
				Xins = new DirectPosition(point1.getX() + (i + 1) * pas * u1.vectNorme().getX(),
						point1.getY() + (i + 1) * pas * u1.vectNorme().getY(),
						point1.getZ() + (i + 1) * pas * u1.vectNorme().getZ());
				listePointsEchant.add(Xins);
			}
		}
		listePointsEchant.add(listePoints.get(listePoints.size() - 1));
		routeSurech = new GM_LineString(listePointsEchant);
		return routeSurech;
	}

	/**
	 * Méthode pour suréchantillonner une GM_LineString. A l'inverse de la
	 * méthode "echantillone", le pas d'echantillonage diffère sur chaque
	 * segment de manière à ce que l'on échantillone chaque segment en
	 * différents mini-segments tous de même longueur. Le pas en entrée est le
	 * pas maximum autorisé.
	 * <p>
	 * English : Resampling of a line
	 */
	public static ILineString echantillonePasVariable(ILineString ls, double pas) {
		IDirectPosition point1, point2, Xins;
		Vecteur u1;
		int nseg, i;
		double longTronc;
		Double fseg;
		ILineString routeSurech = (ILineString) ls.clone();
		IDirectPositionList listePoints = routeSurech.coord();
		DirectPositionList listePointsEchant = new DirectPositionList();
		for (int j = 1; j < listePoints.size(); j++) {
			point1 = listePoints.get(j - 1);
			listePointsEchant.add(point1);
			point2 = listePoints.get(j);
			longTronc = point1.distance(point2);
			fseg = new Double(longTronc / pas);
			nseg = fseg.intValue();
			double epsilonPas = 0;
			if (nseg != 0) {
				epsilonPas = longTronc / nseg - pas;
			}
			double nouveauPas = pas - epsilonPas;
			u1 = new Vecteur(point1, point2);
			for (i = 0; i < nseg - 1; i++) {
				Xins = new DirectPosition(point1.getX() + (i + 1) * nouveauPas * u1.vectNorme().getX(),
						point1.getY() + (i + 1) * nouveauPas * u1.vectNorme().getY(),
						point1.getZ() + (i + 1) * nouveauPas * u1.vectNorme().getZ());
				listePointsEchant.add(Xins);
			}
		}
		listePointsEchant.add(listePoints.get(listePoints.size() - 1));
		routeSurech = new GM_LineString(listePointsEchant);
		return routeSurech;
	}

	/**
	 * Renvoie le point translaté de P avec le vecteur V; Contrairement au
	 * "move" de DirectPosition, on ne deplace pas le point P
	 * <p>
	 * English : Shift of a point
	 */
	public static IDirectPosition translate(IDirectPosition P, Vecteur V) {
		if (!Double.isNaN(P.getZ()) && !Double.isNaN(V.getZ())) {
			return new DirectPosition(P.getX() + V.getX(), P.getY() + V.getY(), P.getZ() + V.getZ());
		}
		return new DirectPosition(P.getX() + V.getX(), P.getY() + V.getY(), Double.NaN);
	}

	/**
	 * Mise bout à bout de plusieurs GM_LineString pour constituer une nouvelle
	 * GM_LineString La liste en entrée contient des GM_LineString. La polyligne
	 * créée commence sur l'extrémité libre de la première polyligne de la
	 * liste.
	 * <p>
	 * English: Combination of lines.
	 * 
	 * @param geometries
	 *            : Linestrings à fusionner
	 */
	public static ILineString compileArcs(List<ILineString> geometries) {
		if (Operateurs.logger.isDebugEnabled()) {
			Operateurs.logger.debug("compile geometries");
			for (ILineString l : geometries) {
				Operateurs.logger.debug("\t" + l);
			}
		}
		return Operateurs.compileArcs(geometries, 0d);
	}

	/**
	 * Mise bout à bout de plusieurs GM_LineString pour constituer une nouvelle
	 * GM_LineString La liste en entrée contient des GM_LineString. La polyligne
	 * créée commence sur l'extrémité libre de la première polyligne de la
	 * liste.
	 * <p>
	 * English: Combination of lines.
	 * 
	 * @param geometries
	 *            : Linestrings à fusionner
	 * @param tolerance
	 *            :distance minimale à laquelle on considère 2 points
	 *            superposés.
	 */
	public static ILineString compileArcs(List<ILineString> geometries, double tolerance) {
		Operateurs.logger.debug("compile geometries");
		for (ILineString l : geometries) {
			Operateurs.logger.debug("\t" + l);
		}
		IDirectPositionList finalPoints = new DirectPositionList();
		if (geometries.isEmpty()) {
			Operateurs.logger.error("ATTENTION. Erreur à la compilation de lignes : aucune ligne en entrée");
			return null;
		}
		ILineString currentLine = geometries.get(0);
		if (geometries.size() == 1) {
			return currentLine;
		}
		ILineString nextLine = geometries.get(1);
		IDirectPosition currentPoint = null;
		if (Distances.proche(currentLine.startPoint(), nextLine.startPoint(), tolerance)
				|| Distances.proche(currentLine.startPoint(), nextLine.endPoint(), tolerance)) {
			// premier point = point finale de la premiere ligne
			finalPoints.addAll(((ILineString) currentLine.reverse()).getControlPoint());
			currentPoint = currentLine.startPoint();
		} else if (Distances.proche(currentLine.endPoint(), nextLine.startPoint(), tolerance)
				|| Distances.proche(currentLine.endPoint(), nextLine.endPoint(), tolerance)) {
			// premier point = point initial de la premiere ligne
			finalPoints.addAll(currentLine.getControlPoint());
			currentPoint = currentLine.endPoint();
		} else {
			Operateurs.logger
					.error("ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
			for (ILineString l : geometries) {
				Operateurs.logger.error(l);
			}

			return null;
		}
		Operateurs.logger.debug("currentPoint = " + currentPoint.toGM_Point());
		for (int i = 1; i < geometries.size(); i++) {
			nextLine = geometries.get(i);
			Operateurs.logger.debug("copying " + nextLine.getControlPoint().size() + " = " + nextLine);
			ILineString lineCopy = new GM_LineString(nextLine.getControlPoint());
			if (Distances.proche(currentPoint, nextLine.startPoint(), tolerance)) {
				// LSSuivante dans le bon sens
				lineCopy.removeControlPoint(lineCopy.startPoint());
				finalPoints.addAll(lineCopy.getControlPoint());
				currentPoint = lineCopy.endPoint();
			} else if (Distances.proche(currentPoint, nextLine.endPoint(), tolerance)) {
				// LSSuivante dans le bon sens
				IDirectPosition toRemove = lineCopy.endPoint();
				ILineString reverse = (ILineString) lineCopy.reverse();
				reverse.removeControlPoint(toRemove);
				finalPoints.addAll(reverse.getControlPoint());
				currentPoint = lineCopy.startPoint();
			} else {
				Operateurs.logger.error(
						"ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
				for (ILineString l : geometries) {
					Operateurs.logger.error(l);
				}
				return null;
			}
		}
		Operateurs.logger.debug("new line with " + finalPoints.size());
		return new GM_LineString(finalPoints, false);
	}

	/**
	 * Compile connected lines into a single ILineString geometry given an
	 * unordered collection of lines.
	 * 
	 * @param geometries
	 * @param tolerance
	 * @return
	 */
	public static ILineString compileArcs(Collection<ILineString> geometries, double tolerance) {
		if (logger.isDebugEnabled()) {
			Operateurs.logger.debug("compile geometries");
			for (ILineString l : geometries) {
				Operateurs.logger.debug("\t" + l);
			}
		}

		IDirectPositionList finalPoints = new DirectPositionList();
		if (geometries.isEmpty()) {
			Operateurs.logger.error("ATTENTION. Erreur à la compilation de lignes : aucune ligne en entrée");
			return null;
		}

		Set<ILineString> remainingLines = new HashSet<>();
		remainingLines.addAll(geometries);
		ILineString first = geometries.iterator().next();
		remainingLines.remove(first);
		finalPoints.addAll(first.coord());
		// get the lines that connect to start node
		IDirectPosition startNode = first.startPoint();
		while (true) {
			boolean lineContinues = false;
			ILineString continuingLine = null;
			for (ILineString line : remainingLines) {
				if (line.startPoint().equals2D(startNode, tolerance)) {
					finalPoints.remove(0);
					for (IDirectPosition pt : line.coord())
						finalPoints.add(0, pt);
					startNode = line.endPoint();
					lineContinues = true;
					continuingLine = line;
					break;
				}
				if (line.endPoint().equals2D(startNode, tolerance)) {
					finalPoints.remove(0);
					for (IDirectPosition pt : line.coord().reverse())
						finalPoints.add(0, pt);
					startNode = line.startPoint();
					lineContinues = true;
					continuingLine = line;
					break;
				}
			}
			if (lineContinues) {
				remainingLines.remove(continuingLine);
				continue;
			}
			break;
		}

		// get the lines that connect to end node
		IDirectPosition endNode = first.endPoint();
		while (true) {
			boolean lineContinues = false;
			ILineString continuingLine = null;
			for (ILineString line : remainingLines) {
				if (line.startPoint().equals2D(endNode, tolerance)) {
					finalPoints.remove(finalPoints.size() - 1);
					for (IDirectPosition pt : line.coord())
						finalPoints.add(pt);
					endNode = line.endPoint();
					lineContinues = true;
					continuingLine = line;
					break;
				}
				if (line.endPoint().equals2D(endNode, tolerance)) {
					finalPoints.remove(finalPoints.size() - 1);
					for (IDirectPosition pt : line.coord().reverse())
						finalPoints.add(pt);
					endNode = line.startPoint();
					lineContinues = true;
					continuingLine = line;
					break;
				}
			}
			if (lineContinues) {
				remainingLines.remove(continuingLine);
				continue;
			}
			break;
		}
		if (logger.isDebugEnabled())
			Operateurs.logger.debug("new line with " + finalPoints.size());
		return new GM_LineString(finalPoints, false);
	}

	/**
	 * Version plus robuste mais aussi potentiellement faussée de
	 * l'intersection. Si JTS plante au calcul d'intersection, on filtre les
	 * surfaces avec Douglas et Peucker, progressivement avec 10 seuils entre
	 * min et max.
	 * <p>
	 * English: Robust intersection of objects (to bypass JTS bugs).
	 * 
	 * @param A
	 *            a geometry
	 * @param B
	 *            another geometry
	 * @param min
	 *            minimum threshold for Douglas-Peucker algorithm
	 * @param max
	 *            maximum threshold for Douglas-Peucker algorithm
	 * @return the intersection of geometries A and B
	 */
	public static IGeometry intersectionRobuste(IGeometry A, IGeometry B, double min, double max) {
		IGeometry intersection, Amodif, Bmodif;
		double seuilDouglas;
		intersection = A.intersection(B);
		if (intersection != null) {
			return intersection;
		}
		for (int i = 0; i < 10; i++) {
			seuilDouglas = min + i * (max - min) / 10;
			Amodif = Filtering.DouglasPeucker(A, seuilDouglas);
			Bmodif = Filtering.DouglasPeucker(B, seuilDouglas);
			intersection = Amodif.intersection(Bmodif);
			if (intersection != null) {
				if (Operateurs.logger.isDebugEnabled()) {
					Operateurs.logger.debug("Calcul d'intersection fait après filtrage avec Douglas Peucker à "
							+ seuilDouglas + "m, pour cause de plantage de JTS");
				}
				return intersection;
			}
		}
		Operateurs.logger.error(
				"ATTENTION : Plantage du calcul d'intersection, même après nettoyage de la géométrie avec Douglas Peucker");
		return null;
	}

	/**
	 * Version plus robuste mais aussi potentiellement faussée de l'union. Si
	 * JTS plante au calcul d'union, on filtre les surfaces avec Douglas et
	 * Peucker, progressivement avec 10 seuils entre min et max.
	 * <p>
	 * English: Robust union of objects (to bypass JTS bugs).
	 */
	public static IGeometry unionRobuste(IGeometry A, IGeometry B, double min, double max) {
		IGeometry union, Amodif, Bmodif;
		double seuilDouglas;
		union = A.union(B);
		if (union != null) {
			return union;
		}
		for (int i = 0; i < 10; i++) {
			seuilDouglas = min + i * (max - min) / 10;
			Amodif = Filtering.DouglasPeucker(A, seuilDouglas);
			Bmodif = Filtering.DouglasPeucker(B, seuilDouglas);
			union = Amodif.union(Bmodif);
			if (union != null) {
				System.out.println("Calcul d'union fait après filtrage avec Douglas Peucker à " + seuilDouglas
						+ "m, pour cause de plantage de JTS");
				return union;
			}
		}
		System.out.println(
				"ATTENTION : Plantage du calcul d'union, même après nettoyage de la géométrie avec Douglas Peucker");
		return null;
	}

	// ////////////////////////////////////////////////////////////////////
	// Regression linéaire
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Methode qui donne l'angle (radians) par rapport à l'axe des x de la
	 * droite passant au mieux au milieu d'un nuage de points (regression par
	 * moindres carrés). Cet angle (défini à pi près) est entre 0 et pi.
	 * English: Linear approximation.
	 */
	public static Angle directionPrincipale(IDirectPositionList listePts) {
		Angle ang = new Angle();
		double angle;
		double x0, y0, x, y, a;
		double moyenneX = 0, moyenneY = 0;
		Matrix Atrans, A, B, X;
		int i;

		// cas où la ligne n'a que 2 pts
		if ((listePts.size() == 2)) {
			ang = new Angle(listePts.get(0), listePts.get(1));
			angle = ang.getValeur();
			if (angle >= Math.PI) {
				return new Angle(angle - Math.PI);
			}
			return new Angle(angle);
		}

		// initialisation des matrices
		// On stocke les coordonnées, en se ramenant dans un repère local sur
		// (x0,y0)
		A = new Matrix(listePts.size(), 1); // X des points de la ligne
		B = new Matrix(listePts.size(), 1); // Y des points de la ligne
		x0 = listePts.get(0).getX();
		y0 = listePts.get(0).getY();
		for (i = 0; i < listePts.size(); i++) {
			x = listePts.get(i).getX() - x0;
			moyenneX = moyenneX + x;
			A.set(i, 0, x);
			y = listePts.get(i).getY() - y0;
			moyenneY = moyenneY + y;
			B.set(i, 0, y);
		}
		moyenneX = moyenneX / listePts.size();
		moyenneY = moyenneY / listePts.size();

		// cas où l'angle est vertical
		if (moyenneX == 0) {
			return new Angle(Math.PI / 2);
		}

		// cas général : on cherche l'angle par régression linéaire
		Atrans = A.transpose();
		X = (Atrans.times(A)).inverse().times(Atrans.times(B));
		a = X.get(0, 0);
		angle = Math.atan(a);
		// on obtient un angle entre -pi/2 et pi/2 ouvert
		// on replace cet angle dans 0 et pi
		if (angle < 0) {
			return new Angle(angle + Math.PI);
		}
		return new Angle(angle);
	}

	/**
	 * Methode qui donne l'angle dans [0,2*pi[ par rapport à l'axe des x, de la
	 * droite orientée passant au mieux au milieu d'un nuage de points ordonnés
	 * (regression par moindres carrés). L'ordre des points en entrée est
	 * important, c'est lui qui permet de donner l'angle à 2.pi près. Exemple:
	 * la liste des points peut correspondre à n points d'un arc, l'angle
	 * représente alors l'orientation générale de ces points, en prenant le
	 * premier pour point de départ.
	 * <p>
	 * English: Linear approximation.
	 */
	public static Angle directionPrincipaleOrientee(IDirectPositionList listePts) {
		double angle;
		double x0, y0, x, y, a;
		double moyenneX = 0, moyenneY = 0;
		Matrix Atrans, A, B, X;
		int i;

		// cas où la ligne n'a que 2 pts
		if ((listePts.size() == 2)) {
			return new Angle(listePts.get(0), listePts.get(1));
		}

		// initialisation des matrices
		// On stocke les coordonnées, en se ramenant dans un repère local sur
		// (x0,y0)
		A = new Matrix(listePts.size(), 1); // X des points de la ligne
		B = new Matrix(listePts.size(), 1); // Y des points de la ligne
		x0 = listePts.get(0).getX();
		y0 = listePts.get(0).getY();
		for (i = 0; i < listePts.size(); i++) {
			x = listePts.get(i).getX() - x0;
			moyenneX = moyenneX + x;
			A.set(i, 0, x);
			y = listePts.get(i).getY() - y0;
			moyenneY = moyenneY + y;
			B.set(i, 0, y);
		}
		moyenneX = moyenneX / listePts.size();
		moyenneY = moyenneY / listePts.size();

		// cas où l'angle est vertical
		if (moyenneX == 0) {
			if (moyenneY < 0) {
				return new Angle(3 * Math.PI / 2);
			}
			if (moyenneY > 0) {
				return new Angle(Math.PI / 2);
			}
		}

		// cas général : on cherche l'angle par régression linéaire
		Atrans = A.transpose();
		X = (Atrans.times(A)).inverse().times(Atrans.times(B));
		a = X.get(0, 0);
		// on obtient un angle entre -pi/2 et pi/2 ouvert
		angle = Math.atan(a);
		// on replace cet angle dans 0 et 2pi
		if (moyenneY < 0) {
			if (angle >= 0) {
				return new Angle(angle + Math.PI);
			}
			return new Angle(angle + 2 * Math.PI);
		}
		if (angle < 0) {
			return new Angle(angle + Math.PI);
		}
		return new Angle(angle);
	}

	// ////////////////////////////////////////////////////////////////////
	// Divers
	// ////////////////////////////////////////////////////////////////////
	/**
	 * Teste si 2 <code>DirectPosition</code>s ont les mêmes coordonnées. Le
	 * test est effectué en 3D :
	 * <ul>
	 * <li>si le premier point n'a pas de Z, le second ne doit pas en avoir pour
	 * être égal.
	 * <li>si le premier point possède un Z, sa valeur est comparée au Z du
	 * second.
	 * </ul>
	 * English: Tests the equality of geometries in 3D
	 * 
	 * @param pt1
	 *            une position
	 * @param pt2
	 *            une position
	 * @return vrai si les deux <code>DirectPosition</code>s ont les mêmes
	 *         coordonnées.
	 */
	public static boolean superposes(IDirectPosition pt1, IDirectPosition pt2) {
		return (pt1.getX() == pt2.getX()) && (pt1.getY() == pt2.getY())
				&& (Double.isNaN(pt1.getZ()) ? Double.isNaN(pt2.getZ()) : (pt1.getZ() == pt2.getZ()));
	}

	/**
	 * Teste si 2 <code>DirectPosition</code>s ont les mêmes coordonnées. Le
	 * test est effectué en 2D : aucun Z n'est considéré. English: Tests the
	 * equality of geometries in 2D
	 * 
	 * @param pt1
	 *            une position
	 * @param pt2
	 *            une position
	 * @return vrai si les deux <code>DirectPosition</code>s ont les mêmes
	 *         coordonnées en 2D.
	 */
	public static boolean superposes2D(IDirectPosition pt1, IDirectPosition pt2) {
		return (pt1.getX() == pt2.getX()) && (pt1.getY() == pt2.getY());
	}

	/**
	 * Teste si 2 <code>GM_Point</code>s ont les mêmes coordonnées.
	 * <p>
	 * English: Tests the equality of geometries
	 */
	public static boolean superposes(IPoint pt1, IPoint pt2) {
		return Operateurs.superposes(pt1.getPosition(), pt2.getPosition());
	}

	/**
	 * Teste la présence d'un DirectPosition (égalité 2D) dans une
	 * DirectPositionList. Renvoie -1 si le directPosition n'est pas dans la
	 * liste
	 * <p>
	 * English: tests if the line contains the point (in 2D)
	 */
	public static int indice2D(IDirectPositionList dpl, IDirectPosition dp) {
		int i;
		IDirectPosition dp1;
		for (i = 0; i < dpl.size(); i++) {
			dp1 = dpl.get(i);
			if ((dp1.getX() == dp.getX()) && (dp1.getY() == dp.getY())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Teste la présence d'un DirectPosition (égalité 3D) dans une
	 * DirectPositionList. Renvoie -1 si le directPosition n'est pas dans la
	 * liste
	 * <p>
	 * English: tests if the line contains the point (in 3D)
	 */
	public static int indice3D(IDirectPositionList dpl, IDirectPosition dp) {
		int i;
		IDirectPosition dp1;
		for (i = 0; i < dpl.size(); i++) {
			dp1 = dpl.get(i);
			if ((dp1.getX() == dp.getX()) && (dp1.getY() == dp.getY()) && (dp1.getZ() == dp.getZ())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Attribue par interpolation un Z aux points d'une ligne en connaissant le
	 * Z des extrémités.
	 * <p>
	 * English: Z interpolation.
	 */
	public static ILineString calculeZ(ILineString ligne) {

		IDirectPosition pointIni = ligne.startPoint();
		IDirectPosition pointFin = ligne.endPoint();
		double z_ini = pointIni.getZ();
		double z_fin = pointFin.getZ();
		IDirectPositionList listePoints = ligne.coord();
		double longueur = 0.0;
		double zCalc;
		IDirectPosition pointRoute, pointRoute1;
		for (int j = 1; j < listePoints.size() - 1; j++) {
			pointRoute = listePoints.get(j);
			pointRoute1 = listePoints.get(j - 1);
			longueur = longueur + pointRoute.distance(pointRoute1);
			zCalc = z_ini + (z_fin - z_ini) * longueur / ligne.length();
			pointRoute.setZ(zCalc);
			ligne.setControlPoint(j, pointRoute);
		}

		return ligne;
	}

	/**
	 * Fusionne les surfaces adjacentes d'une population. NB: quand X objets
	 * sont fusionés, un des objets (au hasard) est gardé avec ses attributs et
	 * sa géométrie est remplacée par celle fusionée.
	 * <p>
	 * English: aggregation of surfaces
	 */
	@SuppressWarnings("unchecked")
	public static void fusionneSurfaces(IPopulation<? extends IFeature> pop) {
		// if (!pop.hasSpatialIndex()) {
		// pop.initSpatialIndex(Tiling.class, true);
		// }
		// if (!pop.getSpatialIndex().hasAutomaticUpdate()) {
		// pop.getSpatialIndex().setAutomaticUpdate(true);
		// }
		List<IFeature> toRemove = new ArrayList<IFeature>();
		for (IFeature feature : pop) {
			// did we already deal with this feature?
			if (toRemove.contains(feature)) {
				continue;
			}
			if (Operateurs.logger.isDebugEnabled()) {
				Operateurs.logger.debug("dealing with feature " + feature.getId());
			}
			boolean changed = true;
			while (changed) {
				changed = false;
				// get the others intersecting features
				Collection<? extends IFeature> intersectingFeatures = pop.select(feature.getGeom());
				// remove the current feature
				intersectingFeatures.remove(feature);
				intersectingFeatures.removeAll(toRemove);
				if (Operateurs.logger.isDebugEnabled()) {
					Operateurs.logger.debug("intersercting " + intersectingFeatures.size() + " features");
				}
				// no intersecting feature
				if (intersectingFeatures.isEmpty()) {
					continue;
				}
				// we remove them from the features we have to deal with
				toRemove.addAll(intersectingFeatures);
				IGeometry union = feature.getGeom();
				IGeometry initialGeometry = feature.getGeom();
				for (IFeature objAfusionner : intersectingFeatures) {
					IGeometry surfaceToUnion = objAfusionner.getGeom();
					union = union.union(surfaceToUnion);
				}
				if (Operateurs.logger.isDebugEnabled()) {
					Operateurs.logger.debug("union = " + union);
				}
				if (!initialGeometry.equals(union)) {
					changed = true;
					if (union.isMultiSurface()) {
						if (Operateurs.logger.isDebugEnabled()) {
							Operateurs.logger.debug("multisurface = " + union);
						}
						union = ((GM_MultiSurface<GM_OrientableSurface>) union).get(0);
					}
					feature.setGeom(union);
				}
			}
		}
		pop.removeAll(toRemove);
	}

	/**
	 * Dilate les surfaces de la population.
	 * <p>
	 * English: dilates surfaces
	 */
	public static void bufferSurfaces(IPopulation<IFeature> popSurf, double tailleBuffer) {

		IFeature objSurf;
		Iterator<IFeature> itSurf = popSurf.getElements().iterator();

		while (itSurf.hasNext()) {
			objSurf = itSurf.next();
			objSurf.setGeom(objSurf.getGeom().buffer(tailleBuffer));
		}
	}

	/**
	 * Surface d'un polygone (trous non gérés). Utile pour pallier aux
	 * déficiences de JTS qui n'accèpte pas les géométries dégénérées.
	 * <p>
	 * Le calcul est effectué dans un repère local centré sur le premier point
	 * de la surface, ce qui est utile pour minimiser les erreurs de calcul si
	 * on manipule de grandes coordonnées).
	 * <p>
	 * English: surface of a polygon
	 */
	public static double surface(IPolygon poly) {
		IDirectPositionList pts = poly.exteriorCoord();
		IDirectPosition pt1, pt2;
		double ymin;
		double surf = 0;

		pt1 = pts.get(0);
		ymin = pt1.getY();
		for (int i = 1; i < pts.size(); i++) {
			pt2 = pts.get(i);
			surf = surf + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin)) / 2;
			pt1 = pt2;
		}
		return Math.abs(surf);
	}

	/**
	 * Surface d'un polygone (liste de points supposée fermée).
	 * <p>
	 * English: surface of a polygon
	 */
	public static double surface(IDirectPositionList pts) {
		IDirectPosition pt1, pt2;
		double ymin;
		double surf = 0;

		pt1 = pts.get(0);
		ymin = pt1.getY();
		for (int i = 1; i < pts.size(); i++) {
			pt2 = pts.get(i);
			surf = surf + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin)) / 2;
			pt1 = pt2;
		}
		return Math.abs(surf);
	}

	/**
	 * Compute the surface defined by 2 lineStrings.
	 * 
	 * @param lineString1
	 *            line1
	 * @param lineString2
	 *            line2
	 * @return a polygon
	 */
	public static IPolygon surfaceFromLineStrings(ILineString lineString1, ILineString lineString2) {
		// fabrication de la surface delimitée par les lignes
		List<IDirectPosition> points = new ArrayList<IDirectPosition>();
		// Initialisation de Distance1 = Somme des côtés à créer
		double sommeDistance1 = lineString1.startPoint().distance(lineString2.startPoint())
				+ lineString1.endPoint().distance(lineString2.endPoint());

		// Initialisation de Distance2 = Somme des diagonales à créer
		double sommeDistance2 = lineString1.startPoint().distance(lineString2.endPoint())
				+ lineString1.endPoint().distance(lineString2.startPoint());

		// Construction du périmètre sur le JDD de référence
		Iterator<IDirectPosition> itPoints = lineString1.coord().getList().iterator();
		while (itPoints.hasNext()) {
			IDirectPosition pt = itPoints.next();
			points.add(pt);
		}

		// Construction du périmètre sur le JDD à comparer en passant par les
		// côtés
		// On considère que la somme des côtés est inférieure à la somme des
		// diagonales
		itPoints = lineString2.coord().getList().iterator();
		while (itPoints.hasNext()) {
			IDirectPosition pt = itPoints.next();
			if (sommeDistance1 < sommeDistance2) {
				points.add(0, pt);
			} else {
				points.add(pt);
			}
		}

		// Bouclage du périmètre
		points.add(points.get(0));
		// Création d'un polygone à partir du périmètre
		IPolygon polygone = new GM_Polygon(new GM_LineString(points));
		return polygone;
	}

	/**
	 * Détermine si une liste de points tourne dans le sens direct ou non.
	 * <ul>
	 * <li>NB : La liste de points est supposée fermée (premier point = dernier
	 * point).
	 * <li>NB : renvoie true pour une surface dégénérée.
	 * </ul>
	 * <p>
	 * English : orientation of a polygon(direct rotation?).
	 */
	public static boolean sensDirect(IDirectPositionList pts) {
		Iterator<IDirectPosition> itPts = pts.getList().iterator();
		IDirectPosition pt1, pt2;
		double ymin;
		double surf = 0;
		pt1 = itPts.next();
		ymin = pt1.getY();
		while (itPts.hasNext()) {
			pt2 = itPts.next();
			surf = surf + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin));
			pt1 = pt2;
		}

		return (surf <= 0);
	}

	/**
	 * Fusionne bout à bout un ensemble de LineStrings en une seule.
	 * <p>
	 * Pour que l'algorithme fonctionne, il faut que, dans la liste, toutes les
	 * linestrings soient connexes. L'algorithme comment par remettre les
	 * linestrings dans l'ordre et dans le même sens.
	 * 
	 * @param linestringList
	 *            ensemble de LineStrings
	 * @return fusion de LineStrings
	 */
	public static ILineString union(List<ILineString> linestringList) {
		return Operateurs.union(linestringList, 0d);
	}

	/**
	 * Fusionne bout à bout un ensemble de LineStrings en une seule.
	 * <p>
	 * Pour que l'algorithme fonctionne, il faut que, dans la liste, toutes les
	 * linestrings soient connexes. L'algorithme comment par remettre les
	 * linestrings dans l'ordre et dans le même sens.
	 * 
	 * @param linestringList
	 *            ensemble de LineStrings
	 * @param tolerance
	 *            la distance à laquelle on considère que deux points sont
	 *            superposés.
	 * @return fusion de LineStrings
	 */
	public static ILineString union(List<ILineString> linestringList, double tolerance) {
		// Si il n'y a pas de polylignes dans la liste, arrêt de la procédure
		if (linestringList.isEmpty()) {
			return null;
		}
		ILineString lineStringCourante = null;
		// Si il n'y a qu'une seule polyligne dans la liste, alors on retourne
		// uniquement cette polyligne
		if (linestringList.size() == 1) {
			lineStringCourante = linestringList.get(0);
			return lineStringCourante;
		}
		// Sinon, il faud comparer toutes les polylignes de la liste (toutes les
		// combinaisons point de départ, point d'arrivée) et les fusionner au
		// fur et à mesure en les orientant.
		// Initialisation de la polyligne de référence.
		lineStringCourante = linestringList.remove(0);
		for (int i = 0; i < linestringList.size(); i++) {
			ILineString lineStringSuivante = linestringList.get(i);
			ILineString lineStringCopie = new GM_LineString(lineStringSuivante.getControlPoint());
			DirectPositionList pointsLiaison = new DirectPositionList();
			// Si le point de départ de la polyligne courante = point de départ
			// de la polyligne suivante
			if (lineStringCourante.startPoint().equals2D(lineStringSuivante.startPoint(), tolerance)) {
				pointsLiaison.addAll(((GM_LineString) lineStringCourante.reverse()).getControlPoint());
				lineStringCopie.removeControlPoint(lineStringCopie.startPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point d'arrivée de la polyligne courante = point d'arrivée
			// de la polyligne suivante
			else if (lineStringCourante.endPoint().equals2D(lineStringSuivante.endPoint(), tolerance)) {
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCopie.removeControlPoint(lineStringCopie.endPoint());
				List<IDirectPosition> list = new ArrayList<IDirectPosition>(
						lineStringCopie.getControlPoint().getList());
				Collections.reverse(list);
				pointsLiaison.addAll(new DirectPositionList(list));
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point d'arrivée de la polyligne courante = point de départ
			// de la polyligne suivante
			else if (lineStringCourante.endPoint().equals2D(lineStringSuivante.startPoint(), tolerance)) {
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCopie.removeControlPoint(lineStringCopie.startPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point de départ de la polyligne courant = point d'arrivée
			// de la polyligne suivante
			else if (lineStringCourante.startPoint().equals2D(lineStringSuivante.endPoint(), tolerance)) {
				lineStringCopie.removeControlPoint(lineStringCopie.endPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}
		}
		
		if(! linestringList.isEmpty()){
			Operateurs.logger.warn("WARNING. All input lines are not used to produce the output merged line (unused lines = " + linestringList.size()+")");
		
		}
		return lineStringCourante;
	}

	/**
	 */
	public static int insertionIndex(IDirectPosition point, List<IDirectPosition> points) {
		if (points.size() < 2) {
			return -1;
		}
		IDirectPosition ptmin = Operateurs.projection(point, points.get(0), points.get(1));
		double dmin = point.distance(ptmin);
		int imin = 0;
		for (int i = 1; i < points.size() - 1; i++) {
			IDirectPosition pt = Operateurs.projection(point, points.get(i), points.get(i + 1));
			double d = point.distance(pt);
			if (d < dmin) {
				ptmin = pt;
				dmin = d;
				imin = i;
			}
		}
		return imin + 1;
	}

	/**
	 * Méthode pour rééchantillonner une GM_LineString.
	 * <p>
	 * English: Resampling of a line.
	 * 
	 * @param ls
	 *            a linestring
	 * @param maxDistance
	 *            maximum distance between 2 consecutive points
	 * @return
	 */
	public static ILineString resampling(ILineString ls, double maxDistance) {
		IDirectPositionList list = ls.coord();
		return new GM_LineString(Operateurs.resampling(list, maxDistance), false);
	}

	/**
	 * Méthode pour rééchantillonner une {@code IDirectPositionList}.
	 * <p>
	 * English: Resampling of a line.
	 * 
	 * @param list
	 *            a IDirectPositionList
	 * @param maxDistance
	 *            maximum distance between 2 consecutive points
	 * @return a resampled IDirectPositionList
	 * @see IDirectPositionList
	 */
	public static IDirectPositionList resampling(IDirectPositionList list, double maxDistance) {
		IDirectPositionList resampledList = new DirectPositionList();
		IDirectPosition prevPoint = list.get(0);
		resampledList.add(prevPoint);
		for (int j = 1; j < list.size(); j++) {
			IDirectPosition nextPoint = list.get(j);
			double length = prevPoint.distance(nextPoint);
			Double fseg = new Double(length / maxDistance);
			int nseg = fseg.intValue();
			// make sure the distance between the resulting points is smaller
			// than
			// maxDistance
			if (fseg.doubleValue() > nseg) {
				nseg++;
			}
			// compute the actual distance between the resampled points
			double d = length / nseg;
			if (nseg >= 1) {
				Vecteur v = new Vecteur(prevPoint, nextPoint).vectNorme();
				for (int i = 0; i < nseg - 1; i++) {
					IDirectPosition curPoint = new DirectPosition(prevPoint.getX() + (i + 1) * d * v.getX(),
							prevPoint.getY() + (i + 1) * d * v.getY(), prevPoint.getZ() + (i + 1) * d * v.getZ());
					resampledList.add(curPoint);
				}
			}
			resampledList.add(nextPoint);
			prevPoint = nextPoint;
		}
		return resampledList;
	}
}
