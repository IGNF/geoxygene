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
public class PointPlacement implements Placement {

  private AnchorPoint anchorPoint;

  /**
   * Renvoie la valeur de l'attribut anchorPoint.
   * @return la valeur de l'attribut anchorPoint
   */
  public AnchorPoint getAnchorPoint() {
    return this.anchorPoint;
  }

  /**
   * Affecte la valeur de l'attribut anchorPoint.
   * @param anchorPoint l'attribut anchorPoint à affecter
   */
  public void setAnchorPoint(AnchorPoint anchorPoint) {
    this.anchorPoint = anchorPoint;
  }

  private Displacement displacement;

  /**
   * Renvoie la valeur de l'attribut displacement.
   * @return la valeur de l'attribut displacement
   */
  public Displacement getDisplacement() {
    return this.displacement;
  }

  /**
   * Affecte la valeur de l'attribut displacement.
   * @param displacement l'attribut displacement à affecter
   */
  public void setDisplacement(Displacement displacement) {
    this.displacement = displacement;
  }

  private float rotation = 0.0f;

  /**
   * Renvoie la valeur de l'attribut rotation.
   * @return la valeur de l'attribut rotation
   */
  public float getRotation() {
    return this.rotation;
  }

  /**
   * Affecte la valeur de l'attribut rotation.
   * @param rotation l'attribut rotation à affecter
   */
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }
}
