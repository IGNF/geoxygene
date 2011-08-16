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

import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ComparisonOpsType extends Filter {
  @XmlElement(name = "PropertyName")
  PropertyName propertyName;

  /**
   * Renvoie la valeur de l'attribut propertyName.
   * @return la valeur de l'attribut propertyName
   */
  public PropertyName getPropertyName() {
    return this.propertyName;
  }

  /**
   * Affecte la valeur de l'attribut propertyName.
   * @param propertyName l'attribut propertyName à affecter
   */
  public void setPropertyName(PropertyName propertyName) {
    this.propertyName = propertyName;
  }

  @XmlElement(name = "Literal")
  Literal literal;

  /**
   * Renvoie la valeur de l'attribut literal.
   * @return la valeur de l'attribut literal
   */
  public Literal getLiteral() {
    return this.literal;
  }

  /**
   * Affecte la valeur de l'attribut literal.
   * @param literal l'attribut literal à affecter
   */
  public void setLiteral(Literal literal) {
    this.literal = literal;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " " + this.getPropertyName() //$NON-NLS-1$
        + " " + this.getLiteral().toString(); //$NON-NLS-1$
  }
}
