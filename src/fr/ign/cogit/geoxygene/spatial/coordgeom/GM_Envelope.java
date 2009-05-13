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

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Rectangle englobant minimum en 2D, ou pave englobant minimium en 3D.
 * Un GM_envelope est parallele aux axes.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Envelope {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Attributs et accesseurs /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/** Coin superieur : Xmax, Ymax, (Zmax).*/
	protected DirectPosition upperCorner;

	/** Affecte le coin superieur. */
	public void setUpperCorner (DirectPosition UpperCorner) {
		upperCorner = (DirectPosition)UpperCorner.clone();
	}

	/** Renvoie le coin superieur. */
	public DirectPosition getUpperCorner () {
		return this.upperCorner;
	}

	/** Coin inferieur : Xmin, Ymin, (Zmin). */
	protected DirectPosition lowerCorner;

	/** Affecte le coin inferieur. */
	public void setLowerCorner (DirectPosition LowerCorner) {
		lowerCorner = (DirectPosition)LowerCorner.clone();
	}

	/** Renvoie le coin inferieur. */
	public DirectPosition getLowerCorner () {
		return this.lowerCorner;
	}




	////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructeurs ///////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/** Constructeur par defaut (initialise des points 3D par defaut). */
	public GM_Envelope() {
		upperCorner = new DirectPosition();
		lowerCorner = new DirectPosition();
	}

	/** Constructeur a partir des 2 coins. Attention a l'ordre des points.*/
	public GM_Envelope (DirectPosition UpperCorner, DirectPosition LowerCorner) {
		upperCorner = UpperCorner;
		lowerCorner = LowerCorner;
	}

	/** Constructeur a partir de coordonnees (2D). */
	public GM_Envelope (double Xmin, double Xmax, double Ymin, double Ymax) {
		upperCorner = new DirectPosition(Xmax, Ymax);
		lowerCorner = new DirectPosition(Xmin, Ymin);
	}

	/** Constructeur a partir de coordonnees (3D). */
	public GM_Envelope (double Xmin, double Xmax, double Ymin, double Ymax, double Zmin, double Zmax) {
		upperCorner = new DirectPosition(Xmax, Ymax, Zmax);
		lowerCorner = new DirectPosition(Xmin, Ymin, Zmin);
	}

	/** Construit un carre dont P est le centre, de cote d. */
	public GM_Envelope (DirectPosition P, double d) {
		double c = d/2;
		upperCorner = new DirectPosition( P.getX()+c, P.getY()+c, P.getZ()+c );
		lowerCorner = new DirectPosition( P.getX()-c, P.getY()-c, P.getZ()-c );
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// Divers get //////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/** Renvoie la dimension (3D). */
	public int getDimension() {
		if (upperCorner.getDimension() == lowerCorner.getDimension()) return upperCorner.getDimension();
		System.out.println("GM_Enveloppe::getDimension() : Les points upperCorner et lowerCorner n'ont pas la même dimension.");
		return 0;
	}

	/** Renvoie la difference des X. */
	public double width() {
		return upperCorner.getX()-lowerCorner.getX();
	}

	/** Renvoie la difference des Y. */
	public double length() {
		return upperCorner.getY()-lowerCorner.getY();
	}

	/** Renvoie la difference des Z. */
	public double height() {
		return upperCorner.getZ()-lowerCorner.getZ();
	}

	/** Renvoie le X max. */
	public double maxX() {
		return upperCorner.getX();
	}

	/** Renvoie le X min. */
	public double minX() {
		return lowerCorner.getX();
	}

	/** Renvoie le Y max. */
	public double maxY() {
		return upperCorner.getY();
	}

	/** Renvoie le Y min. */
	public double minY() {
		return lowerCorner.getY();
	}

	/** Renvoie le Z max. */
	public double maxZ() {
		return upperCorner.getZ();
	}

	/** Renvoie le Z min. */
	public double minZ() {
		return lowerCorner.getZ();
	}

	/** Renvoie le centre de l'enveloppe. */
	public DirectPosition center() {
		int n = this.getDimension();
		DirectPosition result = new DirectPosition();
		for (int i=0; i<n; i++) {
			double theMin = lowerCorner.getCoordinate(i);
			double theMax = upperCorner.getCoordinate(i);
			result.setCoordinate(i,theMin+(theMax-theMin)/2);
		}
		return result;
	}




	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methodes contains ///////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Indique si self contient le point passe en parametre, fonctionne en 2D uniquement.
	 * @param dp le point
	 * @return
	 */
	public boolean contains (DirectPosition dp) {
		return contains(dp.getX(), dp.getY());
	}

	/**
	 * Indique si self contient le point passe en parametre, fonctionne en 2D uniquement.
	 * @param point
	 * @return
	 */
	public boolean contains (GM_Point point) {
		return contains(point.getPosition());
	}

	/**
	 * Indique si self contient le point de coordonnees x,y passees en parametre (2D).
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains (double x, double y) {
		if (x < lowerCorner.getX()) return false;
		else if (x > upperCorner.getX()) return false;
		else if (y < lowerCorner.getY()) return false;
		else if (y > upperCorner.getY()) return false;
		return true;
	}

	/** Indique si self contient le point de coordonnees x,y,z passees en parametre (3D). */
	public boolean contains (double x, double y, double z) {
		double Xmin = lowerCorner.getX();
		double Xmax = upperCorner.getX();
		double Ymin = lowerCorner.getY();
		double Ymax = upperCorner.getY();
		double Zmin = lowerCorner.getZ();
		double Zmax = upperCorner.getZ();
		if (x<Xmin) return false;
		if (x>Xmax) return false;
		if (y<Ymin) return false;
		if (y>Ymax) return false;
		if (z<Zmin) return false;
		if (z>Zmax) return false;
		return true;
	}

	/** Indique si self contient entierement l'enveloppe passee en parametre. */
	public boolean contains (GM_Envelope env) {
		if (!this.contains(env.getLowerCorner())) return false;
		if (!this.contains(env.getUpperCorner())) return false;
		return true;
	}

	/** Indique si self et l'enveloppe passee en parametre se recouvrent, en 2D. */
	public boolean overlaps (GM_Envelope env) {
		if (this.getUpperCorner().getX() < env.getLowerCorner().getX()) return false;
		if (this.getLowerCorner().getX() > env.getUpperCorner().getX()) return false;
		if (this.getUpperCorner().getY() < env.getLowerCorner().getY()) return false;
		if (this.getLowerCorner().getY() > env.getUpperCorner().getY()) return false;
		return true;
	}

	  public boolean intersects(GM_Envelope env) {
	    return !(env.minX() > maxX() || env.maxX() < minX() || env.minY() > maxY() || env.maxY() < minY());
	  }



	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methodes expand /////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/** Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja dans l'enveloppe, ne fait rien. */
	public void expand (DirectPosition thePoint) {
		if (!this.contains(thePoint)) {
			int n = this.getDimension();
			for (int i=0; i<n; i++) {
				double theCoord = thePoint.getCoordinate(i);
				double theMin = lowerCorner.getCoordinate(i);
				double theMax = upperCorner.getCoordinate(i);
				if (theCoord > theMax) upperCorner.setCoordinate(i,theCoord);
				else if (theCoord < theMin) lowerCorner.setCoordinate(i,theCoord);
			}
		}
	}

	/** Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja dans l'enveloppe, ne fait rien. */
	public void expand (double x, double y) {
		if (!this.contains(x,y)) {
			double Xmin = lowerCorner.getX();
			double Xmax = upperCorner.getX();
			double Ymin = lowerCorner.getY();
			double Ymax = upperCorner.getY();
			if (y > Ymax) upperCorner.setY(y);
			else if (y < Ymin) lowerCorner.setY(y);
			if (x > Xmax) upperCorner.setX(x);
			else if (x < Xmin) lowerCorner.setX(x);
		}
	}

	/** Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja dans l'enveloppe, ne fait rien. */
	public void expand (double x, double y, double z) {
		if (!this.contains(x,y,z)) {
			double Xmin = lowerCorner.getX();
			double Xmax = upperCorner.getX();
			double Ymin = lowerCorner.getY();
			double Ymax = upperCorner.getY();
			double Zmin = lowerCorner.getZ();
			double Zmax = upperCorner.getZ();
			if (z > Zmax) upperCorner.setZ(z);
			else if (z < Zmin) lowerCorner.setZ(z);
			if (y > Ymax) upperCorner.setY(y);
			else if (y < Ymin) lowerCorner.setY(y);
			if (x > Xmax) upperCorner.setX(x);
			else if (x < Xmin) lowerCorner.setX(x);
		}
	}

	/** Agrandit l'enveloppe pour contenir l'enveloppe en entree. Si elle est deja contenue, ne fait rien. */
	public void expand (GM_Envelope env) {
		if (!this.contains(env)) {
			this.expand(env.getUpperCorner());
			this.expand(env.getLowerCorner());
		}
	}

	/** Effectue une homothetie de facteur h sur l'enveloppe. */
	public void expandBy (double h) {
		DirectPosition theCenter = this.center();
		int n = this.getDimension();
		for (int i=0; i<n; i++) {
			double center = theCenter.getCoordinate(i);
			double delta = upperCorner.getCoordinate(i)-center;
			upperCorner.setCoordinate(i,center + h*delta);
			lowerCorner.setCoordinate(i,center - h*delta);
		}
	}

	/** Effectue une homothetie de w sur l'axe des X et de l sur l'axe des Y. */
	public void expandBy (double w, double l) {
		DirectPosition theCenter = this.center();
		double centerX = theCenter.getX();
		double deltaX = this.maxX() - centerX;
		upperCorner.setX(centerX + w*deltaX);
		lowerCorner.setX(centerX - w*deltaX);

		double centerY = theCenter.getY();
		double deltaY = this.maxY() - centerY;
		upperCorner.setY(centerY + l*deltaY);
		lowerCorner.setY(centerY - l*deltaY);
	}

	/** Effectue une homothetie de w sur l'axe des X, de l sur l'axe des Y, et de h sur l'axe des Z. */
	public void expandBy (double w, double l, double h) {
		DirectPosition theCenter = this.center();
		double centerX = theCenter.getX();
		double deltaX = this.maxX() - centerX;
		upperCorner.setX(centerX + w*deltaX);
		lowerCorner.setX(centerX - w*deltaX);

		double centerY = theCenter.getY();
		double deltaY = this.maxY() - centerY;
		upperCorner.setY(centerY + l*deltaY);
		lowerCorner.setY(centerY - l*deltaY);

		double centerZ = theCenter.getZ();
		double deltaZ = this.maxZ() - centerZ;
		upperCorner.setZ(centerZ + h*deltaZ);
		lowerCorner.setZ(centerZ - h*deltaZ);
	}




	////////////////////////////////////////////////////////////////////////////////////////////////
	// Divers //////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Construit un GM_object de l'enveloppe
	 * @return
	 */
	public GM_Object getGeom() {
		DirectPositionList coords = new DirectPositionList();
		coords.add(new DirectPosition(minX(), minY()));
		coords.add(new DirectPosition(minX(), maxY()));
		coords.add(new DirectPosition(maxX(), maxY()));
		coords.add(new DirectPosition(maxX(), minY()));
		coords.add(new DirectPosition(minX(), minY()));
		return new GM_Polygon( new GM_Ring( new GM_LineString(coords) ) );
	}

	
	
	/** Renvoie True si l'enveloppe est vide, c'est-a-dire : les coordonnees du lowerCorner
        sont plus grandes que celles du upperCorner. */
	public boolean isEmpty() {
		int n = this.getDimension();
		for (int i=0; i<n; i++) {
			double theMin = lowerCorner.getCoordinate(i);
			double theMax = upperCorner.getCoordinate(i);
			if (theMin > theMax) return true;
		}
		return false;
	}

	/** Clone l'enveloppe. */
	@Override
	public Object clone() {
		DirectPosition up = (DirectPosition)upperCorner.clone();
		DirectPosition low = (DirectPosition)lowerCorner.clone();
		return new GM_Envelope(up,low);
	}


	/** Affiche les coordonnees  */
	@Override
	public String toString() {
		return samplePoint().toString();
	}

	/** Renvoie la liste des DirectPosition de l'objet. */
	public DirectPositionList samplePoint() {
		DirectPositionList dpl = new DirectPositionList();
		DirectPosition dp;
		dpl.add(lowerCorner);
		if (!Double.isNaN(lowerCorner.getZ()))
			dp = new DirectPosition(upperCorner.getX(), lowerCorner.getY(), lowerCorner.getZ() ); // a revoir
			else
				dp = new DirectPosition(upperCorner.getX(), lowerCorner.getY());
		dpl.add(dp);
		dpl.add(upperCorner);
		if (!Double.isNaN(upperCorner.getZ()))
			dp = new DirectPosition(lowerCorner.getX(), upperCorner.getY(), upperCorner.getZ() ); // a revoir
		else
			dp = new DirectPosition(lowerCorner.getX(), upperCorner.getY());
		dpl.add(dp);

		return dpl;
	}

}
