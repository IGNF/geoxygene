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

  /** Tableau des coordonnées du point. */
  protected double[] coordinate = new double[3];
  /** Dimension des coordonnées (2D ou 3D) - dimension = coordinate.length. */
  protected int dimension = 3;

  /**
   * Constructeur par défaut (3D): crée un tableau de coordonées à 3 dimensions,
   * vide.
   */
  public DirectPosition() {
    this.coordinate[0] = Double.NaN;
    this.coordinate[1] = Double.NaN;
    this.coordinate[2] = Double.NaN;
  }

  /**
   * Constructeur d'un DirectPosition à n dimensions : cree un tableau de
   * coordonées à n dimensions, vide.
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

  /** Constructeur à partir de 2 coordonnées. */
  public DirectPosition(final double X, final double Y) {
    this.setCoordinate(X, Y);
  }

  /** Constructeur à partir de 3 coordonnées. */
  public DirectPosition(final double X, final double Y, final double Z) {
    this.setCoordinate(X, Y, Z);
  }

  public DirectPosition(DirectPosition p) {
    this.setCoordinate(p.getCoordinate());
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methodes get
  // ////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public double[] getCoordinate() {
    return this.coordinate;
  }

  @Override
  public int getDimension() {
    return this.dimension;
  }

  @Override
  public double getCoordinate(final int i) {
    return this.coordinate[i];
  }

  @Override
  public double getX() {
    return this.coordinate[0];
  }

  @Override
  public double getY() {
    return this.coordinate[1];
  }

  @Override
  public double getZ() {
    return this.coordinate[2];
  }

  @Override
  public void setCoordinate(final double[] coord) {
    this.coordinate[0] = coord[0];
    this.coordinate[1] = coord[1];
    this.coordinate[2] = (coord.length == 3) ? coord[2] : Double.NaN;
  }

  @Override
  public void setCoordinate(final IPoint thePoint) {
    final IDirectPosition pt = thePoint.getPosition();
    final double[] coord = pt.getCoordinate();
    this.setCoordinate(coord);
  }

  @Override
  public void setCoordinate(final int i, final double x) {
    this.coordinate[i] = x;
  }

  @Override
  public void setCoordinate(final double x, final double y) {
    this.coordinate[0] = x;
    this.coordinate[1] = y;
    this.coordinate[2] = Double.NaN;
  }

  @Override
  public void setCoordinate(final double x, final double y, final double z) {
    this.coordinate[0] = x;
    this.coordinate[1] = y;
    this.coordinate[2] = z;
  }

  @Override
  public void setX(final double x) {
    this.coordinate[0] = x;
  }

  @Override
  public void setY(final double y) {
    this.coordinate[1] = y;
  }

  @Override
  public void setZ(final double z) {
    this.coordinate[2] = z;
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methodes move
  // ////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public void move(final IDirectPosition offsetPoint) {
    if (this.dimension == offsetPoint.getDimension()) {
      for (int i = 0; i < this.dimension; i++) {
        this.coordinate[i] += offsetPoint.getCoordinate(i);
      }
    }
  }

  @Override
  public void move(final double offsetX, final double offsetY) {
    this.coordinate[0] += offsetX;
    this.coordinate[1] += offsetY;
  }

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
  // Méthodes equals
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

  @Override
  public boolean equals(final IDirectPosition pt) {
    return this.equals(pt, 0);
  }

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

  @Override
  public boolean equals2D(final IDirectPosition pt) {
    return this.equals2D(pt, 0);
  }

  @Override
  public double distance(final IDirectPosition d) {
    if (!Double.isNaN(this.getZ()) && !Double.isNaN(d.getZ()) && !Double.isInfinite(this.getZ()) && !Double.isInfinite(d.getZ())) {
      double dx = this.getX() - d.getX();
      double dy = this.getY() - d.getY();
      double dz = this.getZ() - d.getZ();
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    return this.distance2D(d);
  }

  @Override
  public double distance2D(IDirectPosition d) {
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
  // ////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public GM_Point toGM_Point() {
    return new GM_Point(this);
  }

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Methode d'affichage
  // ////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public String toString() {
    if (Double.isNaN(this.getZ())) {
      return "DirectPosition - X : " + this.getX() + "     Y : " + this.getY(); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return "DirectPosition - X : " + this.getX() + "     Y : " + this.getY() + "     Z : " + this.getZ(); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
  }

  @Override
  public double[] minus(IDirectPosition p) {
    return this.minus(p, 1.0d);
  }

  @Override
  public double[] minus(IDirectPosition p2, double factor) {
    double[] difference = new double[Math.min(this.coordinate.length,
        p2.getCoordinate().length)];
    for (int i = 0; i < difference.length; i++) {
      difference[i] = (this.coordinate[i] - p2.getCoordinate()[i]) * factor;
    }
    return difference;
  }

  @Override
  public int hashCode() {
    if(this.coordinate.length==2){
      return Double.valueOf(getX()).hashCode()^Double.valueOf(getY()).hashCode();
    }
    return Double.valueOf(getX()).hashCode()^Double.valueOf(getY()).hashCode()^Double.valueOf(getZ()).hashCode();
  }
}
