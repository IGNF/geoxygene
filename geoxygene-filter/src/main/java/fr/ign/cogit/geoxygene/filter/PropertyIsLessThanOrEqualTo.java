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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Julien Perret
 */
public class PropertyIsLessThanOrEqualTo extends BinaryComparisonOpsType {
  static Logger logger = LogManager.getLogger(PropertyIsLessThanOrEqualTo.class
      .getName());

  @Override
  public boolean evaluate(Object object) {
    Object property = this.getPropertyName().evaluate(object);
    if (property == null) {
      return false;
    }
    if (property instanceof String) {
      if (!this.isMatchCase()) {
        return (String.CASE_INSENSITIVE_ORDER.compare(((String) property), this
            .getLiteral().getValue()) <= 0);
        // FIXME voir cas sensitif à la case
      }
    }
    if (property instanceof Number) {
      return (((Number) property).doubleValue() <= Double.parseDouble(this
          .getLiteral().getValue()));
    }
    return property.equals(this.getLiteral());
  }

  @Override
  public String toString() {
    return this.getPropertyName() + " <= " + this.getLiteral().toString(); //$NON-NLS-1$
  }
}
