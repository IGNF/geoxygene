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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SvgParameter implements StylingParameter {

  @SuppressWarnings("unused")
  static private Logger LOGGER = Logger.getLogger(SvgParameter.class.getName());

  @XmlAttribute
  private String name;

  /**
   * Renvoie la valeur de l'attribut name.
   * @return la valeur de l'attribut name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Affecte la valeur de l'attribut name.
   * @param name l'attribut name à affecter
   */
  public void setName(String name) {
    this.name = name;
  }

  @XmlMixed
  private List<String> values;

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  // private String value;

  /**
   * Renvoie la valeur de l'attribut value.
   * @return la valeur de l'attribut value
   */
  public String getValue() {
    if (values == null) {
      return null;
    }
    if (values.size() == 0) {
      return null;
    }
    return values.get(0);
  }

  /**
   * Affecte la valeur de l'attribut value.
   * @param value l'attribut value à affecter
   */
  public void setValue(String value) {
    this.values = new ArrayList<String>();
    this.values.add(value);
  }

  @XmlElement(name = "PropertyName")
  private PropertyName propertyName;

  public PropertyName getPropertyName() {
    return this.propertyName;
  }

  public void setPropertyName(PropertyName propertyName) {
    this.propertyName = propertyName;
  }
}
