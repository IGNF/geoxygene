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

package fr.ign.cogit.geoxygene.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( { PropertyIsEqualTo.class, PropertyIsGreaterThan.class,
    PropertyIsGreaterThanOrEqualTo.class, PropertyIsLessThan.class,
    PropertyIsLessThanOrEqualTo.class, PropertyIsNotEqualTo.class, And.class,
    Or.class, Not.class })
@XmlRootElement
public abstract class BinaryLogicOpsType extends LogicOpsType {

  @XmlElements( {
      @XmlElement(name = "PropertyIsEqualTo", type = PropertyIsEqualTo.class),
      @XmlElement(name = "PropertyIsGreaterThan", type = PropertyIsGreaterThan.class),
      @XmlElement(name = "PropertyIsGreaterThanOrEqualTo", type = PropertyIsGreaterThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsLessThan", type = PropertyIsLessThan.class),
      @XmlElement(name = "PropertyIsLessThanOrEqualTo", type = PropertyIsLessThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsNotEqualTo", type = PropertyIsNotEqualTo.class),
      @XmlElement(name = "And", type = And.class),
      @XmlElement(name = "Or", type = Or.class),
      @XmlElement(name = "Not", type = Not.class) })
  List<Filter> ops = new ArrayList<Filter>();

  /**
   * Renvoie la valeur de l'attribut ops.
   * @return la valeur de l'attribut ops
   */
  public List<Filter> getOps() {
    return this.ops;
  }

  /**
   * Affecte la valeur de l'attribut ops.
   * @param ops l'attribut ops à affecter
   */
  public void setOps(List<Filter> ops) {
    this.ops = ops;
  }

  @Override
  public String toString() {
    return this.ops.get(0).toString() + this.getClass().getSimpleName()
        + this.ops.get(1).toString();
  }
}
