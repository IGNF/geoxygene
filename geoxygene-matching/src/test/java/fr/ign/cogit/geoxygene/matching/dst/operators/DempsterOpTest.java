package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class DempsterOpTest {

  /**
   * Tests from Don Koks and Subhash Challa (2003),
   * "An Introduction to Bayesian and Dempster-Shafer Data Fusion", Defence Science and Tech Org.
   */
  @Test
  public void testCombine() {
    DempsterOp op = new DempsterOp(true);
    List<List<Pair<byte[], Float>>> masspotentials = new ArrayList<List<Pair<byte[], Float>>>();
    byte[] f111 = new byte[] { 1, 0, 0 };
    byte[] fa18 = new byte[] { 0, 1, 0 };
    byte[] p3c = new byte[] { 0, 0, 1 };
    byte[] fast = new byte[] { 1, 1, 0 };
    byte[] unknown = new byte[] { 1, 1, 1 };
    List<Pair<byte[], Float>> source1 = new ArrayList<Pair<byte[], Float>>();
    List<Pair<byte[], Float>> source2 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source1);
    masspotentials.add(source2);
    // Table 3: Mass assignments for the various aircraft
    source1.add(new Pair<byte[], Float>(f111, 0.3f));
    source1.add(new Pair<byte[], Float>(fa18, 0.15f));
    source1.add(new Pair<byte[], Float>(p3c, 0.03f));
    source1.add(new Pair<byte[], Float>(fast, 0.42f));
    source1.add(new Pair<byte[], Float>(unknown, 0.1f));
    source2.add(new Pair<byte[], Float>(f111, 0.4f));
    source2.add(new Pair<byte[], Float>(fa18, 0.1f));
    source2.add(new Pair<byte[], Float>(p3c, 0.02f));
    source2.add(new Pair<byte[], Float>(fast, 0.45f));
    source2.add(new Pair<byte[], Float>(unknown, 0.03f));
    List<Pair<byte[], Float>> result = op.combine(masspotentials);
    List<Pair<byte[], Float>> expected = new ArrayList<Pair<byte[], Float>>();
    expected.add(new Pair<byte[], Float>(f111, 0.55f));
    expected.add(new Pair<byte[], Float>(fa18, 0.16f));
    expected.add(new Pair<byte[], Float>(p3c, 0.004f));
    expected.add(new Pair<byte[], Float>(fast, 0.29f));
    expected.add(new Pair<byte[], Float>(unknown, 0.003f));
    assertEquals(expected, result);
    // Table 4: A new set of mass assignments, to highlight the "fast" subset anomaly in Table 3
    source2.clear();
    source2.add(new Pair<byte[], Float>(f111, 0.5f));
    source2.add(new Pair<byte[], Float>(fa18, 0.3f));
    source2.add(new Pair<byte[], Float>(p3c, 0.17f));
    source2.add(new Pair<byte[], Float>(unknown, 0.03f));
    result = op.combine(masspotentials);
    expected.clear();
    expected.add(new Pair<byte[], Float>(f111, 0.63f));
    expected.add(new Pair<byte[], Float>(fa18, 0.31f));
    expected.add(new Pair<byte[], Float>(p3c, 0.035f));
    expected.add(new Pair<byte[], Float>(fast, 0.02f));
    expected.add(new Pair<byte[], Float>(unknown, 0.005f));
    assertEquals(expected, result);

    // Table 5: Allocating masses when the target is a decoy, but with no "Decoy" state speciﬁed
    source2.clear();
    source2.add(new Pair<byte[], Float>(f111, 0.3f));
    source2.add(new Pair<byte[], Float>(fa18, 0.3f));
    source2.add(new Pair<byte[], Float>(p3c, 0.3f));
    source2.add(new Pair<byte[], Float>(fast, 0f));
    source2.add(new Pair<byte[], Float>(unknown, 0.1f));
    result = op.combine(masspotentials);
    expected.clear();
    expected.add(new Pair<byte[], Float>(f111, 0.47f));
    expected.add(new Pair<byte[], Float>(fa18, 0.37f));
    expected.add(new Pair<byte[], Float>(p3c, 0.07f));
    expected.add(new Pair<byte[], Float>(fast, 0.07f));
    expected.add(new Pair<byte[], Float>(unknown, 0.02f));
    assertEquals(expected, result);

    // Table 6: Allocating masses when the target is a decoy, still with no "Decoy" state specified;
    // but now sensor 2 realises there is a problem in its measurements
    source2.clear();
    source2.add(new Pair<byte[], Float>(f111, 0.1f));
    source2.add(new Pair<byte[], Float>(fa18, 0.1f));
    source2.add(new Pair<byte[], Float>(p3c, 0.1f));
    source2.add(new Pair<byte[], Float>(fast, 0f));
    source2.add(new Pair<byte[], Float>(unknown, 0.7f));
    result = op.combine(masspotentials);
    expected.clear();
    expected.add(new Pair<byte[], Float>(f111, 0.34f));
    expected.add(new Pair<byte[], Float>(fa18, 0.20f));
    expected.add(new Pair<byte[], Float>(p3c, 0.04f));
    expected.add(new Pair<byte[], Float>(fast, 0.34f));
    expected.add(new Pair<byte[], Float>(unknown, 0.08f));
    assertEquals(expected, result);

    // Table 7: Now introducing a "Decoy" state
    f111 = new byte[] { 1, 0, 0, 0 };
    fa18 = new byte[] { 0, 1, 0, 0 };
    p3c = new byte[] { 0, 0, 1, 0 };
    fast = new byte[] { 1, 1, 0, 0 };
    byte[] decoy = new byte[] { 0, 0, 0, 1 };
    unknown = new byte[] { 1, 1, 1, 1 };
    source1.clear();
    source1.add(new Pair<byte[], Float>(f111, 0.3f));
    source1.add(new Pair<byte[], Float>(fa18, 0.15f));
    source1.add(new Pair<byte[], Float>(p3c, 0.03f));
    source1.add(new Pair<byte[], Float>(fast, 0.22f));
    source1.add(new Pair<byte[], Float>(decoy, 0.20f));
    source1.add(new Pair<byte[], Float>(unknown, 0.1f));
    source2.clear();
    source2.add(new Pair<byte[], Float>(f111, 0.1f));
    source2.add(new Pair<byte[], Float>(fa18, 0.1f));
    source2.add(new Pair<byte[], Float>(p3c, 0.1f));
    source2.add(new Pair<byte[], Float>(fast, 0.0f));
    source2.add(new Pair<byte[], Float>(decoy, 0.6f));
    source2.add(new Pair<byte[], Float>(unknown, 0.1f));
    result = op.combine(masspotentials);
    expected.clear();
    expected.add(new Pair<byte[], Float>(f111, 0.23f));
    expected.add(new Pair<byte[], Float>(fa18, 0.15f));
    expected.add(new Pair<byte[], Float>(p3c, 0.04f));
    expected.add(new Pair<byte[], Float>(fast, 0.06f));
    expected.add(new Pair<byte[], Float>(decoy, 0.5f));
    expected.add(new Pair<byte[], Float>(unknown, 0.02f));
    assertEquals(expected, result);
  }

  private void assertEquals(List<Pair<byte[], Float>> expected, List<Pair<byte[], Float>> result) {
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

  private boolean equals(byte[] array1, byte[] array2) {
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

  private String toString(byte[] array) {
    String result = "";
    for (byte b : array) {
      result += b;
    }
    return result;
  }

}
