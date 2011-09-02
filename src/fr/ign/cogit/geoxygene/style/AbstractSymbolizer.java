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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractSymbolizer implements Symbolizer {
  protected static Logger logger = Logger.getLogger(AbstractSymbolizer.class
      .getName());

  @XmlElement(name = "Stroke")
  private Stroke stroke = null;

  @Override
  public Stroke getStroke() {
    return this.stroke;
  }

  @Override
  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
  }

  @Override
  public boolean isLineSymbolizer() {
    return false;
  }

  @Override
  public boolean isPointSymbolizer() {
    return false;
  }

  @Override
  public boolean isPolygonSymbolizer() {
    return false;
  }

  @Override
  public boolean isRasterSymbolizer() {
    return false;
  }

  @Override
  public boolean isTextSymbolizer() {
    return false;
  }

  @XmlElement(name = "GeometryPropertyName")
  private String geometryPropertyName = "geom"; //$NON-NLS-1$

  @Override
  public String getGeometryPropertyName() {
    return this.geometryPropertyName;
  }

  @Override
  public void setGeometryPropertyName(String geometryPropertyName) {
    this.geometryPropertyName = geometryPropertyName;
  }

  @XmlAttribute(name = "uom")
  private String uom = Symbolizer.METRE;

  @Override
  public String getUnitOfMeasure() {
    return this.uom;
  }

  @Override
  public void setUnitOfMeasure(String uom) {
    this.uom = uom;
  }

  @Override
  public void setUnitOfMeasureMetre() {
    this.setUnitOfMeasure(Symbolizer.METRE);
  }

  @Override
  public void setUnitOfMeasureFoot() {
    this.setUnitOfMeasure(Symbolizer.FOOT);
  }

  @Override
  public void setUnitOfMeasurePixel() {
    this.setUnitOfMeasure(Symbolizer.PIXEL);
  }

  @XmlElement(name = "Shadow")
  private Shadow shadow = null;

  @Override
  public Shadow getShadow() {
    return this.shadow;
  }

  @Override
  public void setShadow(Shadow shadow) {
    this.shadow = shadow;
  }

  @Override
  public String toString() {
    String result = this.getClass().getSimpleName() + ":"; //$NON-NLS-1$
    result += this.getGeometryPropertyName() + " ("; //$NON-NLS-1$
    result += this.getUnitOfMeasure() + ")"; //$NON-NLS-1$
    return result;
  }
}
