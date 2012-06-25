/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class Utils {

  
  static Logger logger = Logger.getLogger(Utils.class);
  
  public static byte[] byteUnion(byte[] a, byte b[]){
    if (a.length != b.length){
      Utils.logger.info("Union of 2 byte arrays with different sizes");
    }
    int max = Math.max(a.length, b.length);
    byte[] newarray = new byte[max];
    Arrays.fill(newarray,(byte)0);
    for (int i = 0; i < max; i++) {
      byte aval = 0;
      byte bval = 0;
      if (i >= a.length) {
        aval = 0;
      } else {
        aval = a[i];
      }
      if (i >= b.length) {
        bval = 0;
      } else {
        bval = b[i];
      }
      if (aval == 1 && bval == 1) {
        aval = 0;
      }
      newarray[i] = (byte) (aval + bval);
    }
    return newarray;
  }
  
  
  public static byte[] byteIntersection(byte[] a, byte[] b) {
    int max = Math.max(a.length, b.length);
    byte[] newarray = new byte[max];
    Arrays.fill(newarray, (byte) 0);
    for (int i = 0; i < max; i++) {
      byte aval = 0;
      byte bval = 0;
      if (i >= a.length) {
        aval = 0;
      } else {
        aval = a[i];
      }
      if (i >= b.length) {
        bval = 0;
      } else {
        bval = b[i];
      }
      newarray[i] = (byte) (aval * bval);
    }
    return newarray;
  }
  
  public static List<byte[]> sortbyteArrays(List<byte[]> arrays){
    
    List<byte[]> newlist = new ArrayList<byte[]>(arrays);

    Collections.sort(arrays, Utils.byteArrayComparator());
    return newlist;
  }
  
  public static Comparator<byte[]> byteArrayComparator(){
    Comparator<byte[]> comparator = new Comparator<byte[]>() {
      public int compare(byte[] left, byte[] right) {
        for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
    }
    };
    return comparator;
  }


  /**
   * @param size
   * @return
   */
  public static boolean isEmpty(byte[] array) {
    byte[] empty = new byte[array.length];
    return Arrays.equals(array, empty);
  }
  
}
