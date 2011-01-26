/**
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
 * 
 */

package fr.ign.cogit.geoxygene.style;

/**
 * @author Julien Perret
 * 
 */
public class Displacement {
  private float x = 0.0f;

  /**
   * Renvoie la valeur de l'attribut x.
   * @return la valeur de l'attribut x
   */
  public float getDisplacementX() {
    return this.x;
  }

  /**
   * Affecte la valeur de l'attribut x.
   * @param x l'attribut x à affecter
   */
  public void setDisplacementX(float x) {
    this.x = x;
  }

  private float y = 0.0f;

  /**
   * Renvoie la valeur de l'attribut y.
   * @return la valeur de l'attribut y
   */
  public float getDisplacementY() {
    return this.y;
  }

  /**
   * Affecte la valeur de l'attribut y.
   * @param y l'attribut y à affecter
   */
  public void setDisplacementY(float y) {
    this.y = y;
  }
}
