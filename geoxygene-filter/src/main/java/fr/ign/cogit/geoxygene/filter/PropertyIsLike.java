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

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Julien Perret
 * 
 */
public class PropertyIsLike extends BinaryComparisonOpsType {
  static Logger logger = LogManager.getLogger(PropertyIsLike.class.getName());
  @XmlAttribute(required = true)
  private String wildCard;
  @XmlAttribute(required = true)
  private String singleChar;
  @XmlAttribute(name = "escape", required = true)
  private String escapeChar;

  public String getWildCard() {
    return this.wildCard;
  }

  public void setWildCard(String wildCard) {
    this.wildCard = wildCard;
  }

  public String getSingleChar() {
    return this.singleChar;
  }

  public void setSingleChar(String singleChar) {
    this.singleChar = singleChar;
  }

  public String getEscapeChar() {
    return this.escapeChar;
  }

  public void setEscapeChar(String escapeChar) {
    this.escapeChar = escapeChar;
  }

  @Override
  public boolean evaluate(Object object) {
    Object property = this.getPropertyName().evaluate(object);
    if (property == null) {
      return false;
    }
    String regex = this.getLiteral().getValue();
    regex.replace(this.getSingleChar(), "."); //$NON-NLS-1$
    regex.replace(this.getWildCard(), ".*"); //$NON-NLS-1$
    regex.replace(this.getEscapeChar(), "\\"); //$NON-NLS-1$
    if (property instanceof String) {
      // FIXME voir influence sensibilité à la casse
      return ((String) property).matches(regex);
    }
    if (property instanceof Number) {
      return ((Number) property).toString().matches(regex);
    }
    return false;
  }

  @Override
  public String toString() {
    return this.getPropertyName() + " is like " + this.getLiteral().toString(); //$NON-NLS-1$
  }
}
