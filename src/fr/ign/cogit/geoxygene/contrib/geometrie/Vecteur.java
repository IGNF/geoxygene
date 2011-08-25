/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.geometrie;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Classe pour le calcul avec des vecteurs (2D ou 3D). Supporte la plupart des
 * opérations classiques. Les coordonnees des vecteurs sont stockees dans des
 * DirectPosition. Les vecteurs 2D ont NaN comme troisieme coordonnee.
 * 
 * English: Class for computations with vectors (either 2D or 3D). Coordinates
 * are stored into DirectPosition. 2D vectors are given Nan As third
 * coordinate).
 * 
 * @author Mustière/Bonin
 * @version 1.0
 */

public class Vecteur {
  protected IDirectPosition coord = null;

  public IDirectPosition getCoord() {
    return this.coord;
  }

  public void setCoord(IDirectPosition coord) {
    this.coord = coord;
  }

  /* Helpers pour la programmation : vecteurs nuls en 2D et 3D */
  public static final Vecteur vecteurNul2D = new Vecteur(new DirectPosition(0,
      0, Double.NaN));
  public static final Vecteur vecteurNul3D = new Vecteur(new DirectPosition(0,
      0, 0));

  public Vecteur() {
    this.coord = new DirectPosition();
  }

  /** Initialise le vecteur 0 -> dp1 (a dp1) */
  public Vecteur(IDirectPosition dp1) {
    this.coord = dp1;
  }

  /** Initialise le vecteur dp1 -> dp2 (a dp2-dp1) */
  public Vecteur(IDirectPosition dp1, IDirectPosition dp2) {
    if (!Double.isNaN(dp1.getZ()) && !Double.isNaN(dp2.getZ())) {
      this.coord = new DirectPosition(dp2.getX() - dp1.getX(), dp2.getY()
          - dp1.getY(), dp2.getZ() - dp1.getZ());
    } else {
      this.coord = new DirectPosition(dp2.getX() - dp1.getX(), dp2.getY()
          - dp1.getY(), Double.NaN);
    }
  }

  public Vecteur(double a, double b, double c) {
    if (Double.isNaN(c)) {
      this.coord = new DirectPosition(a, b, 0);
    } else {
      this.coord = new DirectPosition(a, b, c);
    }
  }

  public double getX() {
    return this.coord.getX();
  }

  public double getY() {
    return this.coord.getY();
  }

  public double getZ() {
    return this.coord.getZ();
  }

  public void setX(double X) {
    this.coord.setX(X);
  }

  public void setY(double Y) {
    this.coord.setY(Y);
  }

  public void setZ(double Z) {
    this.coord.setZ(Z);
  }

  /** Renvoie la norme de this */
  public double norme() {
    if (!Double.isNaN(this.getZ())) {
      return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)
          + Math.pow(this.getZ(), 2));
    }
    return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
  }

  /** Renvoie un NOUVEAU vecteur égal au vecteur normé porté par this */
  public Vecteur vectNorme() {
    double normev = this.norme();
    if (!Double.isNaN(this.getZ())) {
      return new Vecteur(new DirectPosition(this.getX() / normev, this.getY()
          / normev, this.getZ() / normev));
    }
    return new Vecteur(new DirectPosition(this.getX() / normev, this.getY()
        / normev, Double.NaN));
  }

  /** Norme le vecteur this (MODIFIE le this) */
  public void normalise() {
    double normev = this.norme();
    if (!Double.isNaN(this.getZ())) {
      this.setX((this.getX() / normev));
      this.setY((this.getY() / normev));
      this.setZ((this.getZ() / normev));
    } else {
      this.setX((this.getX() / normev));
      this.setY((this.getY() / normev));
    }
  }
  
  /**
   * Renvoie un vecteur normé et ne modifie pas le this
   * @return renvoie le vecteur normalisé
   */
  public Vecteur getNormalised(){
    
    double x,y,z;
    double normev = this.norme();
    x = (this.getX() / normev);
    y = (this.getY() / normev);
    if (!Double.isNaN(this.getZ())) {
      z = this.getZ() / normev;
      
      return new Vecteur(x,y,z);
    } 
    
    
    return new Vecteur(x,y,Double.NaN);
    
  }

  /** Renvoie un NOUVEAU vecteur égal à (this + v1) */
  public Vecteur ajoute(Vecteur v1) {
    if (!Double.isNaN(this.getZ()) && !Double.isNaN(v1.getZ())) {
      return new Vecteur(new DirectPosition(this.getX() + v1.getX(), this
          .getY()
          + v1.getY(), this.getZ() + v1.getZ()));
    }
    return new Vecteur(new DirectPosition(this.getX() + v1.getX(), this.getY()
        + v1.getY(), Double.NaN));
  }

  /** Renvoie un NOUVEAU vecteur égal à (this - v1) */
  public Vecteur soustrait(Vecteur v1) {
    if (!Double.isNaN(this.getZ()) && !Double.isNaN(v1.getZ())) {
      return new Vecteur(new DirectPosition(this.getX() - v1.getX(), this
          .getY()
          - v1.getY(), this.getZ() - v1.getZ()));
    }
    return new Vecteur(new DirectPosition(this.getX() - v1.getX(), this.getY()
        - v1.getY(), Double.NaN));
  }

  /** Renvoie un NOUVEAU vecteur égal à (lambda.this) */
  public Vecteur multConstante(double lambda) {
    if (!Double.isNaN(this.getZ())) {
      return new Vecteur(new DirectPosition(lambda * this.getX(), lambda
          * this.getY(), lambda * this.getZ()));
    }
    return new Vecteur(new DirectPosition(lambda * this.getX(), lambda
        * this.getY(), Double.NaN));
  }

  /** Renvoie le point translaté de P par le vecteur this */
  public IDirectPosition translate(IDirectPosition p) {
    DirectPosition p2 = new DirectPosition();
    p2.setX(p.getX() + this.getX());
    p2.setY(p.getY() + this.getY());
    if (Double.isNaN(this.getZ()) || Double.isNaN(p.getZ())) {
      p2.setZ(Double.NaN);
    } else {
      p2.setZ(p.getZ() + this.getZ());
    }
    return p2;
  }

  /** Renvoie la ligne translatée de L par le vecteur this */
  public ILineString translate(ILineString line) {
    GM_LineString L2 = new GM_LineString();
    for (int i = 0; i < line.sizeControlPoint(); i++) {
      IDirectPosition pt = line.getControlPoint(i);
      L2.addControlPoint(this.translate(pt));
    }
    return L2;
  }

  /**
   * Renvoie le produit vectoriel this^v1 ; NB: le produit vectoriel renvoie
   * toujours un vecteur 3D, même à partir de vecteurs 2D
   */
  public Vecteur prodVectoriel(Vecteur v1) {
    if (!Double.isNaN(this.getZ()) && !Double.isNaN(v1.getZ())) {
      return new Vecteur(new DirectPosition(this.getY() * v1.getZ()
          - this.getZ() * v1.getY(), this.getZ() * v1.getX() - this.getX()
          * v1.getZ(), this.getX() * v1.getY() - this.getY() * v1.getX()));
    }
    return new Vecteur(new DirectPosition(0, 0, this.getX() * v1.getY()
        - this.getY() * v1.getX()));
  }

  /** Renvoie le produit scalaire this.v1 */
  public double prodScalaire(Vecteur v1) {
    if (!Double.isNaN(this.getZ()) && !Double.isNaN(v1.getZ())) {
      return (this.getX() * v1.getX() + this.getY() * v1.getY() + this.getZ()
          * v1.getZ());

    }
    return (this.getX() * v1.getX() + this.getY() * v1.getY());
  }

  /** Angle entre l'axe des X et le vecteur this projeté sur le plan XY */
  public Angle direction() {
    return new Angle(Math.atan2(this.getY(), this.getX()));
  }

  /** Angle entre this et v1 */
  public Angle angleVecteur(Vecteur V1) {
    double angle = 0;
    angle = Math.acos(this.prodScalaire(V1) / (this.norme() * V1.norme()));
    if (Double.isNaN(angle)) {
      angle = 0;
    }
    return new Angle(angle);
  }
}
