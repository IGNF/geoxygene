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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


/**
 * Point connu par ses coordonnées.
 * <p>
 * Les coordonnées sont connues par un tableau, de longueur la dimension des géométries (2D ou 3D).
 * Dans cette version, tous les DirectPosition sont en 3D.
 * Si on est en 2D, la 3ieme coordonnée vaut NaN.
 * TODO Ajouter la méthode hashCode()
 * FIXME import SRC.SC_CRS -> non implemente;
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */
public class DirectPosition {
	static Logger logger=Logger.getLogger(DirectPosition.class.getName());

	//////////////////////////////////////////////////////////////////////////////////////////
	// Attribut CRS //////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * Identifiant du système de coordonnées de référence (CRS en anglais).
	 * Lorsque les DirectPosition servent à définir un GM_Object, cet attribut doit être null.
	 * En effet, il est alors porté par le GM_Object.
	 * <p>
	 * FIXME Dans la norme ISO, cet attribut est une relation qui pointe vers la classe SC_CRS (non implémentée) 
	 */
	protected int CRS;
	/** Renvoie l' identifiant du système de coordonnées de référence. */
	public int getCRS() {return this.CRS;}
	/** Affecte une valeur au système de coordonnées de référence. */
	public void setCRS(int crs) {CRS = crs; }

	/** Tableau des coordonnées du point. */
	protected double[] coordinate = new double[3];
	/** Dimension des coordonnées (2D ou 3D) - dimension = coordinate.length  */
	protected int dimension = 3;
	/** Constructeur par défaut (3D): crée un tableau de coordonées à 3 dimensions, vide.*/
	public DirectPosition() {
		coordinate[0] = Double.NaN;
		coordinate[1] = Double.NaN;
		coordinate[2] = Double.NaN;
	}

	/** Constructeur d'un DirectPosition à n dimensions : crée un tableau de coordonées à n dimensions, vide.*/
	/*public DirectPosition(int n) {
        coordinate = new double[n];
        dimension = n;
    }*/

	/** Constructeur à partir d'un tableau de coordonnées.
	 * Si le tableau passé en paramètre est 2D, la 3ième coordonnée du DirectPosition vaudra NaN.
	 * Le tableau est recopié et non passé en référence. */
	public DirectPosition(double[] coord) {this.setCoordinate(coord);}
	/** Constructeur à partir de 2 coordonnées. */
	public DirectPosition(double X, double Y) {this.setCoordinate(X, Y);}
	/** Constructeur à partir de 3 coordonnées. */
	public DirectPosition(double X, double Y, double Z) {this.setCoordinate(X, Y, Z);}

	//////////////////////////////////////////////////////////////////////////////////////////
	// Méthodes get //////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le tableau des coordonnées. */
	public double[] getCoordinate() {return this.coordinate;}
	/** Renvoie la dimension (toujours 3). */
	public int getDimension () {return this.dimension;}
	/** Renvoie la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=2 pour Z). */
	public double getCoordinate(int i) {return this.coordinate[i];}
	/** Renvoie X (1ère coordonnee du tableau, indice 0). */
	public double getX() {return this.coordinate[0];}
	/** Renvoie Y (2ième coordonnée du tableau, indice 1). */
	public double getY() {return this.coordinate[1];}
	/** Renvoie Z (3ième coordonnée du tableau, indice 2). */
	public double getZ() {return this.coordinate[2];}
	/** 
	 * Affecte les coordonnées d'un tableau des coordonnées (2D ou 3D).
	 * Si le tableau passé en paramètre est 2D, la 3ième coordonnée du DirectPosition vaudra NaN.
	 * Le tableau est recopié et non passé en référence. */
	public void setCoordinate(double[] coord)  {
		coordinate[0] = coord[0];
		coordinate[1] = coord[1];
		coordinate[2] = (coord.length == 3)?coord[2]:Double.NaN;
	}
	/** Affecte la position d'un point géométrique. Le point passé en paramètre doit avoir la même dimension que this.*/
	public void setCoordinate(GM_Point thePoint) {
		DirectPosition pt = thePoint.getPosition();
		double[] coord = pt.getCoordinate();
		setCoordinate(coord);
	}
	/** Affecte une valeur à la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=2 pour Z.). */
	public void setCoordinate(int i, double x)  {coordinate[i] = x;}
	/** Affecte une valeur à X et Y. */
	public void setCoordinate(double x, double y) {
		coordinate[0] = x;
		coordinate[1] = y;
		coordinate[2] = Double.NaN;
	}
	/** Affecte une valeur à X, Y et Z. */
	public void setCoordinate(double x, double y, double z)  {
		coordinate[0] = x;
		coordinate[1] = y;
		coordinate[2] = z;
	}
	/** Affecte une valeur à X (1ère coordonnée du tableau). */
	public void setX(double x) {coordinate[0] = x;}
	/** Affecte une valeur à Y (2ième coordonnée du tableau). */
	public void setY(double y) {coordinate[1] = y;}
	/** Affecte une valeur à Z (3ième coordonnée du tableau). */
	public void setZ(double z)  {coordinate[2] = z;}

	//////////////////////////////////////////////////////////////////////////////////////////
	// Méthodes move /////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** Déplace le point suivant toutes les dimensions. Le point passé en paramètre doit avoir la même dimension que this.*/
	public void move(DirectPosition offsetPoint)  {
		if (dimension == offsetPoint.getDimension())
			for (int i=0; i<dimension; i++)
				coordinate[i] += offsetPoint.getCoordinate(i);
	}

	/** Déplace le point suivant X et Y. */
	public void move(double offsetX, double offsetY) {
		coordinate[0] += offsetX;
		coordinate[1] += offsetY;
	}

	/** 
	 * Déplace le point suivant X, Y et Z.
	 */
	public void move(double offsetX, double offsetY, double offsetZ) {
		coordinate[0] += offsetX;
		coordinate[1] += offsetY;
		if (coordinate.length==3) coordinate[2] += offsetZ;
	}


	//////////////////////////////////////////////////////////////////////////////////////////
	// Méthode equals ////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(Object o)  {
		if (this==o) return true;
		if (o instanceof DirectPosition) return this.equals((DirectPosition)o);
		return false;
	}
	/**
	/** Indique si self et le point passé en paramètre sont égaux.
	 * Si les 2 points ont une troisième dimension affectée, on teste cette dimension.
	 * @param pt un point
	 * @return vrai si le point this est égal au point passé en paramètre
	 * @see #equals(Object)
	 * @see #equals(DirectPosition, double)
	 * @see #equals2D(DirectPosition, double)
	 */
	public boolean equals(DirectPosition pt)  {return equals(pt, 0);}
	/** Indique si self et le point passé en paramètre sont égaux, à une tolérance près.
	 * Si les 2 points ont une troisième dimension affectée, on teste cette dimension.
	 * Tolérance est un double qui doit être > 0. 
	 * @param pt un point
	 * @param tolerance tolérance entre this et le point passé en paramètre
	 * @return vrai si le point this est égal au point passé en paramètre à la tolérance près
	 * @see #equals(Object)
	 * @see #equals(DirectPosition)
	 * @see #equals2D(DirectPosition, double)
	 */
	public boolean equals(DirectPosition pt, double tolerance)  {
		double x1, x2;
		for (int i=0; i<=1; i++) {
			x1 = coordinate[i];
			x2 = pt.getCoordinate(i);
			if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
		}
		if (!Double.isNaN(this.getZ()))
			if (!Double.isNaN(pt.getZ())) {
				x1 = coordinate[2];
				x2 = pt.getCoordinate(2);
				if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
			}
		return true;
	}

	/**
	 * Indique si self et le point passé en paramètre sont égaux, à une tolérance près.
	 * La comparaison est effectuée en 2D, i.e. la troisième dimension est ignorée.
	 * Tolérance est un double qui doit être > 0. 
	 * @param pt un point
	 * @param tolerance tolérance entre this et le point passé en paramètre
	 * @return vrai si le point this est égal au point passé en paramètre à la tolérance près
	 * @see #equals(Object)
	 * @see #equals(DirectPosition)
	 * @see #equals(DirectPosition, double)
	 */
	public boolean equals2D(DirectPosition pt, double tolerance)  {
		double x1, x2;
		for (int i=0; i<=1; i++) {
			x1 = coordinate[i];
			x2 = pt.getCoordinate(i);
			if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
		}
		return true;
	}
	
	/**
	 * Indique si self et le point passé en paramètre sont égaux.
	 * La comparaison est effectuée en 2D, i.e. la troisième dimension est ignorée.
	 * @param pt un point
	 * @return vrai si le point this est égal au point passé en paramètre
	 * @see #equals(Object)
	 * @see #equals(DirectPosition)
	 * @see #equals(DirectPosition, double)
	 * @see #equals2D(DirectPosition, double)
	 */
	public boolean equals2D(DirectPosition pt)  {return equals2D(pt, 0);}

	/**
	 * Calcul de la distance entre deux directPosition
	 * @param d
	 * @return
	 */
	public double distance(DirectPosition d) {
	    double dx = getX() - d.getX();
	    double dy = getY() - d.getY();
	    return Math.sqrt(dx * dx + dy * dy);
	}

	/** Clone le point. */
	@Override
	public Object clone() {return new DirectPosition(getCoordinate().clone());}

	//////////////////////////////////////////////////////////////////////////////////////////
	// Méthode toGM_Point ////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** Créée un GM_Point à partir de this.*/
	public GM_Point toGM_Point() {return new GM_Point(this);}

	//////////////////////////////////////////////////////////////////////////////////////////
	// Méthode d'affichage ///////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	/** Affiche les coordonnées du point (2D et 3D). */
	@Override
	public String toString () {
		if (Double.isNaN(this.getZ()))
			return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY());
		else
			return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY()+"     Z : "+this.getZ());
	}
}
