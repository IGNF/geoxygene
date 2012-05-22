/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Julien Perret
 * 
 */
public abstract class TimeUtil {
  public static String toTimeLength(long time) {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    c.setTimeInMillis(time);
    int days = c.get(Calendar.DAY_OF_YEAR) - 1; // first day of the year is 1
    int hours = c.get(Calendar.HOUR_OF_DAY);
    // + " h "
    int minutes = c.get(Calendar.MINUTE);
    // + " m "
    int seconds = c.get(Calendar.SECOND);
    // + " s "
    int milliseconds = c.get(Calendar.MILLISECOND);
    // + " ms");
    String result = "";
    if (days > 0) {
      result += days + " days ";
    }
    if (days > 0 || hours > 0) {
      result += hours + " h ";
    }
    if (days > 0 || hours > 0 || minutes > 0) {
      result += minutes + " m ";
    }
    if (days > 0 || hours > 0 || minutes > 0 || seconds > 0) {
      result += seconds + " s ";
    }
    return result + milliseconds + " ms";
  }

  public static void main(String[] args) {
    System.out.println(toTimeLength(0));
    System.out.println(toTimeLength(1000));
    System.out.println(toTimeLength(6505565));
  }
}
