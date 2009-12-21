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

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * Classe des angles en radian. Supporte quelques opérations
 * de base.
 * La classe possede un attribut angle qui est la valeur de l'angle (en radians)
 * comprise entre 0 et 2*pi. La methode setAngle(a) ramene a entre en 0 et 2*pi.
 * 
 * English: Class for computations on angles (in radian). The attribute
 * angle is the value of the angle (in radian) between 0 and 2*pi.
 * 
 * @author  Musti�re/Bonin/Grosso
 * @version 1.0
 */

public class Angle
{
	// CONSTANTES
	/** angle de valeur nulle */
	public static final Angle angleNul = new Angle(0);
	/** angle de valeur PI */
	public static final Angle anglePlat = new Angle(Math.PI);
	/** angle de valeur PI/2 */
	public static final Angle angleDroit = new Angle(Math.PI/2);

	protected double valeur = 0;

	public double getValeur() {
		return this.valeur;
	}

	public void setValeur(double valeur) {
		if (valeur % (2 * Math.PI) >= 0) this.valeur = Math.abs(valeur % (2 * Math.PI));
		else this.valeur = (valeur % (2 * Math.PI) + 2 * Math.PI);
	}

	/* Constructeurs */
	public Angle() {
	}

	public Angle(double valeur) {
		this.setValeur(valeur);
	}

	/** Angle entre 2 points dans le plan X,Y (valeur comprise entre 0 et 2*pi) */
	public Angle(DirectPosition pt1, DirectPosition pt2) {
		double x = pt2.getX()-pt1.getX();
		double y = pt2.getY()-pt1.getY();
		this.setValeur(Math.atan2(y,x));
	}

	/** crée un angle à pi pr�s (entre O et pi),
	 * à partir d'un angle à 2pi pr�s (entre 0 et pi) */
	public static Angle angleAPiPres(Angle angle2pi) {
		if (angle2pi.valeur > Math.PI ) return new Angle(angle2pi.valeur-Math.PI);
		return new Angle(angle2pi.valeur);
	}

	/** crée un angle à pi pr�s (entre O et pi),
	 * à partir de this */
	public Angle angleAPiPres() {
		if (this.valeur > Math.PI ) return new Angle(this.valeur-Math.PI);
		return new Angle(this.valeur);
	}

	/** ajoute a à l'angle */
	public void ajoute(Angle a) {this.setValeur(this.valeur+a.valeur);}

	/** ajoute les angles a et b */
	public static Angle ajoute(Angle a, Angle b) {
		return new Angle(a.valeur+b.valeur);
	}

	/** Angle de la "bissectrice" des deux angles a et b.
	 * L'angle est au milieu entre a et b.
	 * NB: bissectrice(a,b) = bissectrice(b,a) + PI */
	public static Angle bissectrice(Angle a, Angle b) {
		return new Angle(a.valeur+ecarttrigo(a,b).valeur/2);
	}

	/** Moyenne de deux angles (défini à pi pr�s). */
	public static Angle moyenne(Angle a, Angle b) {
		return angleAPiPres(new Angle((a.valeur+b.valeur)/2));
	}

	/** Ecart de a vers b dans le sens trigonom�trique,
	 * ex : ecart(pi/4, 7pi/4) = 3pi/2 */
	public static Angle ecarttrigo(Angle a, Angle b) {
		return new Angle(b.valeur-a.valeur);
	}

	/** Ecart au plus court entre les deux angles,
	 * dans [0,pi], ex : ecart(pi/4, 7pi/4) = pi/2 */
	public static Angle ecart(Angle a, Angle b) {
		return new Angle(
				Math.min(ecarttrigo(a,b).getValeur(),ecarttrigo(b,a).getValeur()));
	}
	/** Angle entre 3 points; pt2 est le point central: angle(pt2pt1,pt2pt3). */
	public static Angle angleTroisPoints(DirectPosition pt1, DirectPosition pt2, DirectPosition pt3) {
		Angle angle1 = new Angle(pt2,pt1);
		Angle angle2 = new Angle(pt2,pt3);
		Angle angle = Angle.ecarttrigo(angle1,angle2);
		return angle;
	}
}