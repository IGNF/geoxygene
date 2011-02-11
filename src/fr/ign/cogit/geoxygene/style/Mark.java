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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.batik.ext.awt.geom.Polygon2D;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Mark {
  @XmlElement(name = "WellKnownName")
  private String wellKnownName = "square"; //$NON-NLS-1$

  /**
   * Renvoie la valeur de l'attribut wellKnownName.
   * @return la valeur de l'attribut wellKnownName
   */
  public String getWellKnownName() {
    return this.wellKnownName;
  }

  /**
   * Affecte la valeur de l'attribut wellKnownName.
   * @param wellKnownName l'attribut wellKnownName à affecter
   */
  public void setWellKnownName(String wellKnownName) {
    this.wellKnownName = wellKnownName;
  }

  @XmlElement(name = "Fill")
  private Fill fill = null;

  /**
   * Renvoie la valeur de l'attribut fill.
   * @return la valeur de l'attribut fill
   */
  public Fill getFill() {
    return this.fill;
  }

  /**
   * Affecte la valeur de l'attribut fill.
   * @param fill l'attribut fill à affecter
   */
  public void setFill(Fill fill) {
    this.fill = fill;
  }

  @XmlElement(name = "Stroke")
  private Stroke stroke = null;

  /**
   * Renvoie la valeur de l'attribut stroke.
   * @return la valeur de l'attribut stroke
   */
  public Stroke getStroke() {
    return this.stroke;
  }

  /**
   * Affecte la valeur de l'attribut stroke.
   * @param stroke l'attribut stroke à affecter
   */
  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
  }

  private static Shape square = new Rectangle2D.Float(-0.5f, -0.5f, 1.0f, 1.0f);
  private static Shape circle = new Ellipse2D.Float(-0.5f, -0.5f, 1.0f, 1.0f);
  private static Shape triangle = new Polygon2D(new float[] {
      -0.5f * (float) Math.cos(Math.PI / 6), 0.0f,
      0.5f * (float) Math.cos(Math.PI / 6) }, new float[] {
      -0.5f * (float) Math.sin(Math.PI / 6), 0.5f,
      -0.5f * (float) Math.sin(Math.PI / 6) }, 3);
  static float starInnerRatio = 0.4f;
  private static Shape star = new Polygon2D(new float[] { 0.0f,
      0.5f * (float) Math.cos(3.0 * Math.PI / 10.0) * Mark.starInnerRatio,
      0.5f * (float) Math.cos(Math.PI / 10.0),
      0.5f * (float) Math.cos(Math.PI / 10.0) * Mark.starInnerRatio,
      0.5f * (float) Math.cos(3.0 * Math.PI / 10.0), 0.0f,
      -0.5f * (float) Math.cos(3.0 * Math.PI / 10.0),
      -0.5f * (float) Math.cos(Math.PI / 10.0) * Mark.starInnerRatio,
      -0.5f * (float) Math.cos(Math.PI / 10.0),
      -0.5f * (float) Math.cos(3.0 * Math.PI / 10.0) * Mark.starInnerRatio,
      0.0f }, new float[] { -0.5f, -0.5f * (float) Math.sin(Math.PI / 10.0),
      -0.5f * (float) Math.sin(Math.PI / 10.0),
      0.5f * (float) Math.sin(Math.PI / 10.0) * Mark.starInnerRatio,
      0.5f * (float) Math.sin(3.0 * Math.PI / 10.0),
      0.5f * Mark.starInnerRatio,
      0.5f * (float) Math.sin(3.0 * Math.PI / 10.0),
      0.5f * (float) Math.sin(Math.PI / 10.0) * Mark.starInnerRatio,
      -0.5f * (float) Math.sin(Math.PI / 10.0),
      -0.5f * (float) Math.sin(Math.PI / 10.0), -0.5f }, 11);
  static float crossHalfWidth = 0.1f;
  private static Shape cross = new Polygon2D(new float[] { Mark.crossHalfWidth,
      Mark.crossHalfWidth, 0.5f, 0.5f, Mark.crossHalfWidth,
      Mark.crossHalfWidth, -Mark.crossHalfWidth, -Mark.crossHalfWidth, -0.5f,
      -0.5f, -Mark.crossHalfWidth, -Mark.crossHalfWidth, Mark.crossHalfWidth },
      new float[] { -0.5f, -Mark.crossHalfWidth, -Mark.crossHalfWidth,
          Mark.crossHalfWidth, Mark.crossHalfWidth, 0.5f, 0.5f,
          Mark.crossHalfWidth, Mark.crossHalfWidth, -Mark.crossHalfWidth,
          -Mark.crossHalfWidth, -0.5f, -0.5f }, 13);
  static float sqrt2over2 = 0.5f * (float) Math.sqrt(2);
  static float xShapeRadius = 0.5f;
  static float xShapeRatio = 0.25f;
  // static float xShapeP = sqrt2over2 * xShapeRadius * (1 - xShapeRatio);
  static float xShapeP = (float) (Mark.sqrt2over2 * (Math.sqrt(Math.pow(
      Mark.xShapeRadius, 2)
      - (Math.pow(Mark.xShapeRatio * Mark.xShapeRadius, 2) / 2)) - (Mark.xShapeRadius
      * Mark.xShapeRatio / Math.sqrt(2))));
  static float xShapeR = Mark.xShapeRadius * Mark.xShapeRatio;
  private static Shape xShape = new Polygon2D(new float[] { 0.0f, Mark.xShapeP,
      Mark.xShapeP + Mark.xShapeR, Mark.xShapeR, Mark.xShapeP + Mark.xShapeR,
      Mark.xShapeP, 0.0f, -Mark.xShapeP, -(Mark.xShapeP + Mark.xShapeR),
      -Mark.xShapeR, -(Mark.xShapeP + Mark.xShapeR), -Mark.xShapeP, 0.0f },
      new float[] { -Mark.xShapeR, -(Mark.xShapeP + Mark.xShapeR),
          -Mark.xShapeP, 0.0f, Mark.xShapeP, Mark.xShapeP + Mark.xShapeR,
          Mark.xShapeR, Mark.xShapeP + Mark.xShapeR, Mark.xShapeP, 0.0f,
          -Mark.xShapeP, -(Mark.xShapeP + Mark.xShapeR), -Mark.xShapeR }, 13);
  private static Shape hLine = new Polygon2D(new float[] { -0.5f, -0.5f, 0.5f,
      0.5f }, new float[] { 0.1f, -0.1f, -0.1f, 0.1f }, 4);
  private static Shape vLine = new Polygon2D(new float[] { 0.1f, -0.1f, -0.1f,
      0.1f }, new float[] { -0.5f, -0.5f, 0.5f, 0.5f }, 4);

  /**
   * @return the AWT shape used to draw this Mark
   */
  public Shape toShape() {
    if ((this.wellKnownName == null)
        || (this.wellKnownName.equalsIgnoreCase("square"))) {
      return Mark.square; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("circle")) {
      return Mark.circle; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("triangle")) {
      return Mark.triangle; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("star")) {
      return Mark.star; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("cross")) {
      return Mark.cross; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("x")) {
      return Mark.xShape; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("hLine")) {
      return Mark.hLine; //$NON-NLS-1$
    }
    if (this.wellKnownName.equalsIgnoreCase("vLine")) {
      return Mark.vLine; //$NON-NLS-1$
    }
    return null;
  }
}
