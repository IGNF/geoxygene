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

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * @author Julien Perret
 * 
 */
public class IntegerSortedSetVarCharFieldConversion implements FieldConversion {
  /**
   * serial uid.
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unchecked")
  public Object javaToSql(Object source) throws ConversionException {
    if (source instanceof SortedSet) {
      String s = ""; //$NON-NLS-1$
      if (((List) source).isEmpty()) {
        return s;
      }
      SortedSet<Integer> dates = (SortedSet<Integer>) source;
      Iterator<Integer> iterator = dates.iterator();
      s += iterator.next();
      while (iterator.hasNext()) {
        s += "-" + iterator.next(); //$NON-NLS-1$
      }
      return s;
    }
    return null;
  }

  public Object sqlToJava(Object source) throws ConversionException {
    if (source instanceof String) {
      String s = (String) source;
      String[] values = s.split("-"); //$NON-NLS-1$
      SortedSet<Integer> result = new TreeSet<Integer>();
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
