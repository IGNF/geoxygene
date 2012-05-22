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

package fr.ign.cogit.geoxygene.datatools.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * @author Julien Perret
 * 
 */
public class IntegerList2VarCharFieldConversion implements FieldConversion {
  /**
   * serial uid.
   */
  private static final long serialVersionUID = 1L;

  @Override
  @SuppressWarnings("unchecked")
  public Object javaToSql(Object source) throws ConversionException {
    if (source instanceof List) {
      String s = ""; //$NON-NLS-1$
      if (((List) source).isEmpty()) {
        return s;
      }
      List<Integer> dates = (List<Integer>) source;
      s += dates.get(0);
      for (int i = 1; i < dates.size(); i++) {
        s += "-" + dates.get(i); //$NON-NLS-1$
      }
      return s;
    }
    return null;
  }

  @Override
  public Object sqlToJava(Object source) throws ConversionException {
    if (source instanceof String) {
      String s = (String) source;
      String[] values = s.split("-"); //$NON-NLS-1$
      List<Integer> result = new ArrayList<Integer>();
      for (String val : values) {
        if (!val.isEmpty()) {
          result.add(new Integer(Integer.parseInt(val)));
        }
      }
      return result;
    }
    return null;
  }
}
