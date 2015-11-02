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
package fr.ign.cogit.geoxygene.style.thematic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.Fill;

@XmlAccessorType(XmlAccessType.FIELD)
public class ThematicClass {
  @XmlElement(required = false, name = "ClassLabel")
  private String classLabel = null;
  @XmlElements(@XmlElement(name = "PropertyName", type = PropertyName.class))
  @XmlElementWrapper(name = "ClassValue")
  private Expression[] classValue = null;
  
  @XmlElement(name = "Fill", type=Fill.class, required=true)
  private Fill fill = null;

  public String getClassLabel() {
    return this.classLabel;
  }

  public void setClassLabel(String classLabel) {
    this.classLabel = classLabel;
  }

  public Expression getClassValue() {
    if (this.classValue == null) {
      return null;
    }
    return this.classValue[0];
  }

  public void setClassValue(Expression classValue) {
    if (this.classValue == null) {
      this.classValue = new Expression[1];
    }
    this.classValue[0] = classValue;
  }

  public Fill getFill() {
    return this.fill;
  }

  public void setFill(Fill fill) {
    this.fill = fill;
  }
}
