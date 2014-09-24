package fr.ign.cogit;

import java.util.List;

import org.junit.Assert;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * 
 * 
 *
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
