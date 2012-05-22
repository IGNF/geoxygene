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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class UnaryLogicOpsType extends LogicOpsType {

  @XmlElements({
      @XmlElement(name = "PropertyIsEqualTo", type = PropertyIsEqualTo.class),
      @XmlElement(name = "PropertyIsGreaterThan", type = PropertyIsGreaterThan.class),
      @XmlElement(name = "PropertyIsGreaterThanOrEqualTo", type = PropertyIsGreaterThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsLessThan", type = PropertyIsLessThan.class),
      @XmlElement(name = "PropertyIsLessThanOrEqualTo", type = PropertyIsLessThanOrEqualTo.class),
      @XmlElement(name = "PropertyIsNotEqualTo", type = PropertyIsNotEqualTo.class),
      @XmlElement(name = "And", type = And.class),
      @XmlElement(name = "Or", type = Or.class),
      @XmlElement(name = "Not", type = Not.class) })
  Filter op = null;

  /**
   * Renvoie la valeur de l'attribut op.
   * @return la valeur de l'attribut op
   */
  public Filter getOp() {
    return this.op;
  }

  /**
   * Affecte la valeur de l'attribut op.
   * @param op l'attribut op à affecter
   */
  public void setOp(Filter op) {
    this.op = op;
  }
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " " + this.op.toString(); //$NON-NLS-1$
  }
  @Override
  public boolean equals(Object o) {
    if (! UnaryLogicOpsType.class.isAssignableFrom(o.getClass())) {
      return false;
    }
    UnaryLogicOpsType f = (UnaryLogicOpsType) o;
    return f.getClass().equals(this.getClass())
        && f.getOp().equals(this.getOp());
  }
}
