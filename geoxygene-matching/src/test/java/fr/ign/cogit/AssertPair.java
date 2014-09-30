/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit;

import java.util.List;

import org.junit.Assert;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * Assertion method for <byte[], Float> for writing tests. 
 * These methods can be used directly: AssertPair.assertEquals(...) 
 * 
 * @author Bertrand Dumenieu
 */
public class AssertPair extends Assert {
  
  public static void assertEquals(List<Pair<byte[], Float>> expected, List<Pair<byte[], Float>> result) {
    Assert.assertEquals(expected.size(), result.size());
    for (Pair<byte[], Float> expectedPair : expected) {
      Pair<byte[], Float> actualPair = null;
      for (Pair<byte[], Float> pair : result) {
        if (equals(expectedPair.getFirst(), pair.getFirst())) {
          actualPair = pair;
          break;
        }
      }
      if (actualPair == null) {
        Assert.fail("pair not found: " + toString(expectedPair.getFirst()));
      }
      Assert.assertEquals(expectedPair.getSecond(), actualPair.getSecond(), 0.006);
    }
  }

  private static boolean equals(byte[] array1, byte[] array2) {
    if (array1.length != array2.length) {
      return false;
    }
    for (int i = 0; i < array1.length; i++) {
      if (array1[i] != array2[i]) {
        return false;
      }
    }
    return true;
  }

  private static String toString(byte[] array) {
    String result = "";
    for (byte b : array) {
      result += b;
    }
    return result;
  }

}
