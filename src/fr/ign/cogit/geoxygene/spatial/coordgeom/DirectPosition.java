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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Point connu par ses coordonnées.
 * <p>
 * Les coordonnées sont connues par un tableau, de longueur la dimension des
 * géométries (2D ou 3D). Dans cette version, tous les DirectPosition sont en
 * 3D. Si on est en 2D, la 3ieme coordonnée vaut NaN. TODO Ajouter la méthode
 * hashCode() FIXME import SRC.SC_CRS -> non implemente;
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */
public class DirectPosition implements IDirectPosition {
  static Logger logger = Logger.getLogger(DirectPosition.class.getName());

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Attribut CRS
  // //////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Identifiant du systeme de coordonnees de reference (CRS en anglais).
   * Lorsque les DirectPosition servent à definir un GM_Object, cet attribut
   * doit etre null. En effet, il est alors porte par le GM_Object.
   * <p>
   * FIXME Dans la norme ISO, cet attribut est une relation qui pointe vers la
   * classe SC_CRS (non implementee).
   */
  protected int CRS;

  @Override
  public int getCRS() {
    return this.CRS;
  }

  @Override
  public void setCRS(final int crs) {
    this.CRS = crs;
  }

  /** Tableau des coordonn�es du point. */
  protected double[] coordinate = new double[3];
  /** Dimension des coordonn�es (2D ou 3D) - dimension = coordinate.length */
  protected int dimension = 3;

  /**
   * Constructeur par d�faut (3D): cr�e un tableau de coordon�es � 3 dimensions,
   * vide.
   */
  public DirectPosition() {
    this.coordinate[0] = Double.NaN;
    this.coordinate[1] = Double.NaN;
    this.coordinate[2] = Double.NaN;
  }

  /**
   * Constructeur d'un DirectPosition � n dimensions : cr�e un tableau de
   * coordon�es � n dimensions, vide.
   */
  /*
   * public DirectPosition(int n) { coordinate = new double[n]; dimension = n; }
   */

  /**
   * Constructeur à partir d'un tableau de coordonnees. Si le tableau passe en
   * parametre est 2D, la 3ieme coordonnee du DirectPosition vaudra NaN. Le
   * tableau est recopie et non passe en reference.
   */
  public DirectPosition(final double[] coord) {
    this.setCoordinate(coord);
  }

  /** Constructeur à partir de 2 coordonn�es. */
  public DirectPosition(final double X, final double Y) {
    this.setCoordinate(X, Y);
  }

  /** Constructeur à partir de 3 coordonn�es. */
  public DirectPosition(final double X, final double Y, final double Z) {
    this.setCoordinate(X, Y, Z);
  }
  
  public DirectPosition(DirectPosition p) {
    this.setCoordinate(p.getCoordinate());
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methodes get
  // //////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////

  /** Renvoie le tableau des coordonnees. */
  @Override
  public double[] getCoordinate() {
    return this.coordinate;
  }

  /** Renvoie la dimension (toujours 3). */
  @Override
  public int getDimension() {
    return this.dimension;
  }

  /** Renvoie la i-eme coordonnees (i=0 pour X, i=1 pour Y, i=2 pour Z). */
  @Override
  public double getCoordinate(final int i) {
    return this.coordinate[i];
  }

  /** Renvoie X (1ere coordonnee du tableau, indice 0). */
  @Override
  public double getX() {
    return this.coordinate[0];
  }

  /** Renvoie Y (2ieme coordonnee du tableau, indice 1). */
  @Override
  public double getY() {
    return this.coordinate[1];
  }

  /** Renvoie Z (3ieme coordonnee du tableau, indice 2). */
  @Override
  public double getZ() {
    return this.coordinate[2];
  }

  /**
   * Affecte les coordonnees d'un tableau des coordonnees (2D ou 3D). Si le
   * tableau passe en parametre est 2D, la 3ieme coordonnee du DirectPosition
   * vaudra NaN. Le tableau est recopie et non passe en reference.
   */
  @Override
  public void setCoordinate(final double[] coord) {
    this.coordinate[0] = coord[0];
    this.coordinate[1] = coord[1];
    this.coordinate[2] = (coord.length == 3) ? coord[2] : Double.NaN;
  }

  /**
   * Affecte la position d'un point geometrique. Le point passe en parametre
   * doit avoir la meme dimension que this.
   */
  @Override
  public void setCoordinate(final IPoint thePoint) {
    final IDirectPosition pt = thePoint.getPosition();
    final double[] coord = pt.getCoordinate();
    this.setCoordinate(coord);
  }

  /**
   * Affecte une valeur à la i-eme coordonnees (i=0 pour X, i=1 pour Y, i=2 pour
   * Z.).
   */
  @Override
  public void setCoordinate(final int i, final double x) {
    this.coordinate[i] = x;
  }

  /** Affecte une valeur à X et Y. */
  @Override
  public void setCoordinate(final double x, final double y) {
    this.coordinate[0] = x;
    this.coordinate[1] = y;
    this.coordinate[2] = Double.NaN;
  }

  /** Affecte une valeur à X, Y et Z. */
  @Override
  public void setCoordinate(final double x, final double y, final double z) {
    this.coordinate[0] = x;
    this.coordinate[1] = y;
    this.coordinate[2] = z;
  }

  /** Affecte une valeur à X (1ere coordonnee du tableau). */
  @Override
  public void setX(final double x) {
    this.coordinate[0] = x;
  }

  /** Affecte une valeur à Y (2ieme coordonnee du tableau). */
  @Override
  public void setY(final double y) {
    this.coordinate[1] = y;
  }

  /** Affecte une valeur à Z (3ieme coordonnee du tableau). */
  @Override
  public void setZ(final double z) {
    this.coordinate[2] = z;
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methodes move
  // /////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Deplace le point suivant toutes les dimensions. Le point passe en parametre
   * doit avoir la meme dimension que this.
   */
  @Override
  public void move(final IDirectPosition offsetPoint) {
    if (this.dimension == offsetPoint.getDimension()) {
      for (int i = 0; i < this.dimension; i++) {
        this.coordinate[i] += offsetPoint.getCoordinate(i);
      }
    }
  }

  /** Deplace le point suivant X et Y. */
  @Override
  public void move(final double offsetX, final double offsetY) {
    this.coordinate[0] += offsetX;
    this.coordinate[1] += offsetY;
  }

  /**
   * Deplace le point suivant X, Y et Z.
   */
  @Override
  public void move(final double offsetX, final double offsetY,
      final double offsetZ) {
    this.coordinate[0] += offsetX;
    this.coordinate[1] += offsetY;
    if (this.coordinate.length == 3) {
      this.coordinate[2] += offsetZ;
    }
  }
  
  public void move(double[] v) {
    this.move(v, 1.0);
  }
  
  public void move(double[] v, double factor) {
    for (int i = 0; i < v.length && i < this.coordinate.length; i++) {
      this.coordinate[i] += factor * v[i];
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methodes equals
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof IDirectPosition) {
      return this.equals((IDirectPosition) o);
    }
    return false;
  }

  /**
   * /** Indique si self et le point passe en parametre sont egaux. Si les 2
   * points ont une troisieme dimension affectee, on teste cette dimension.
   * 
   * @param pt un point
   * @return vrai si le point this est egal au point passe en parametre
   * @see #equals(Object)
   * @see #equals(IDirectPosition, double)
   * @see #equals2D(IDirectPosition, double)
   */
  @Override
  public boolean equals(final IDirectPosition pt) {
    return this.equals(pt, 0);
  }

  /**
   * Indique si self et le point passe en parametre sont egaux, à une tolerance
   * pres. Si les 2 points ont une troisieme dimension affectee, on teste cette
   * dimension. Tolerance est un double qui doit etre > 0.
   * 
   * @param pt un point
   * @param tolerance tolerance entre this et le point passe en parametre
   * @return vrai si le point this est egal au point passe en parametre à la
   *         tolerance pres
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals2D(IDirectPosition, double)
   */
  @Override
  public boolean equals(final IDirectPosition pt, final double tolerance) {
    double x1, x2;
    for (int i = 0; i <= 1; i++) {
      x1 = this.coordinate[i];
      x2 = pt.getCoordinate(i);
      if ((x2 > x1 + tolerance) || (x2 < x1 - tolerance)) {
        return false;
      }
    }
    if (!Double.isNaN(this.getZ())) {
      if (!Double.isNaN(pt.getZ())) {
        x1 = this.coordinate[2];
        x2 = pt.getCoordinate(2);
        if ((x2 > x1 + tolerance) || (x2 < x1 - tolerance)) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Indique si self et le point passe en parametre sont egaux, à une tolerance
   * pres. La comparaison est effectuee en 2D, i.e. la troisieme dimension est
   * ignoree. Tolerance est un double qui doit etre > 0.
   * 
   * @param pt un point
   * @param tolerance tolerance entre this et le point passe en parametre
   * @return vrai si le point this est egal au point passe en parametre à la
   *         tolerance pres
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals(IDirectPosition, double)
   */
  @Override
  public boolean equals2D(final IDirectPosition pt, final double tolerance) {
    double x1, x2;
    for (int i = 0; i <= 1; i++) {
      x1 = this.coordinate[i];
      x2 = pt.getCoordinate(i);
      if ((x2 > x1 + tolerance) || (x2 < x1 - tolerance)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Indique si self et le point passe en parametre sont egaux. La comparaison
   * est effectuee en 2D, i.e. la troisieme dimension est ignoree.
   * 
   * @param pt un point
   * @return vrai si le point this est egal au point passe en parametre
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals(IDirectPosition, double)
   * @see #equals2D(IDirectPosition, double)
   */
  @Override
  public boolean equals2D(final IDirectPosition pt) {
    return this.equals2D(pt, 0);
  }

  /**
   * Calcul de la distance entre deux directPosition
   * 
   * @param d a IDirectPosition
   * @return the distance between this DirectPosition and <code>d</code>
   */
  @Override
  public double distance(final IDirectPosition d) {
    final double dx = this.getX() - d.getX();
    final double dy = this.getY() - d.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }
  
    /**
   * Calcul de la distance 2D entre deux directPosition
   * @param d a DirectPosition
   * @return the distance between this DirectPosition and <code>d</code>
   */
  public double distance2D(DirectPosition d) {
    double dx = this.getX() - d.getX();
    double dy = this.getY() - d.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }

  @Override
  public double orientation(IDirectPosition dp) {
    return Math.atan2(dp.getY() - this.getY(), dp.getX() - this.getX());
  }

  @Override
  public Object clone() {
    return new DirectPosition(this.getCoordinate().clone());
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methode toGM_Point
  // ////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////

  /** Creee un GM_Point à partir de this. */
  @Override
  public GM_Point toGM_Point() {
    return new GM_Point(this);
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methode d'affichage
  // ///////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    if (Double.isNaN(this.getZ())) {
      return new String("DirectPosition - X : " + this.getX() + "     Y : "
          + this.getY());
    } else {
      return new String("DirectPosition - X : " + this.getX() + "     Y : "
          + this.getY() + "     Z : " + this.getZ());
    }
  }

  @Override
  public int hashCode() {
    return (int) this.getX();
  }
  
    public double[] minus(DirectPosition p) {
    return this.minus(p, 1.0d);
  }
  public double[] minus(IDirectPosition p2, double factor) {
    double[] difference = new double[Math.min(this.coordinate.length, p2.getCoordinate().length)];
    for (int i = 0; i < difference.length; i++) {
      difference[i] = (this.coordinate[i] - p2.getCoordinate()[i]) * factor;
    }
    return difference;
  }
}
