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
package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ColorMap {
  @XmlElement(name = "PropertyName")
  String propertyName = null;
  @Transient
  public String getPropertyName() {
    return this.propertyName;
  }
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
  @XmlElement(name = "Interpolate")
  Interpolate interpolate = null;
  public Interpolate getInterpolate() {
    return this.interpolate;
  }
  public void setInterpolate(Interpolate interpolate) {
    this.interpolate = interpolate;
  }
  public int getColor(double value) {
    if (this.interpolate != null) {
      InterpolationPoint previous = null;
      for (InterpolationPoint point : this.interpolate.getInterpolationPoint()) {
        if (value <= point.getData()) {
          if (previous == null) {
            return point.getColor().getRGB();
          }
          return this.interpolateColor(value, previous.getData(),
              previous.getColor(), point.getData(), point.getColor()).getRGB();
        }
        previous = point;
      }
    }
    return 0;
  }

  private Color interpolateColor(double value, double data1, Color color1,
      double data2, Color color2) {
    double r1 = color1.getRed();
    double g1 = color1.getGreen();
    double b1 = color1.getBlue();
    double r2 = color2.getRed();
    double g2 = color2.getGreen();
    double b2 = color2.getBlue();
    return new Color(
        (float) this.interpolate(value, data1, r1, data2, r2) / 255f,
        (float) this.interpolate(value, data1, g1, data2, g2) / 255f,
        (float) this.interpolate(value, data1, b1, data2, b2) / 255f);
  }

  private double interpolate(double value, double data1, double value1,
      double data2, double value2) {
    return value1 + (value - data1) * (value2 - value1) / (data2 - data1);
  }
}
