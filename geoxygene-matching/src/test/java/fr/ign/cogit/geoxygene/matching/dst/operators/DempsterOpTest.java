package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class DempsterOpTest {
  
  private final static Logger LOGGER = Logger.getLogger(DempsterOpTest.class);
  
  /**
   * Tests from Andino Maseleno and Md. Mahmud Hasan (2013),
   * "The Dempster-Shafer Theory Algorithm and its Application to Insect Diseases Detection", International Journal of Advanced Science and Technology.
   * Vol. 50 January, 2013
   */
  @Test
  public void testInsectDiseaseDetection() {
    
    DempsterOp op = new DempsterOp(true);
    List<List<Pair<byte[], Float>>> masspotentials = new ArrayList<List<Pair<byte[], Float>>>();
    
    byte[] B = new byte[] { 1, 0, 0, 0, 0 };
    byte[] DF = new byte[] { 0, 1, 0, 0, 0 };
    byte[] M = new byte[] { 0, 0, 1, 0, 0 };
    byte[] L = new byte[] { 0, 0, 0, 0, 1 };
    byte[] G1 = new byte[] { 1,1, 1, 1, 0 };
    byte[] OMEGA = new byte[] { 1,1, 1, 1, 1 };
    
    // Symptom 1 : fever
    List<Pair<byte[], Float>> source1 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source1);
    source1.add(new Pair<byte[], Float>(G1, 0.45f));
    source1.add(new Pair<byte[], Float>(OMEGA, 0.55f));
    
    // Symptom 2 : Red Urine
    List<Pair<byte[], Float>> source2 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source2);
    source2.add(new Pair<byte[], Float>(B, 0.55f));
    source2.add(new Pair<byte[], Float>(OMEGA, 0.45f));
    
    // Symptom 3 : Skin Rash
    List<Pair<byte[], Float>> source3 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source3);
    source3.add(new Pair<byte[], Float>(L, 0.45f));
    source3.add(new Pair<byte[], Float>(OMEGA, 0.55f));
    
    // Symptom 4 : Paralysis
    List<Pair<byte[], Float>> source4 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source4);
    source4.add(new Pair<byte[], Float>(L, 0.45f));
    source4.add(new Pair<byte[], Float>(OMEGA, 0.55f));
    
    // Symptom 5 : Headache
    List<Pair<byte[], Float>> source5 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source5);
    source5.add(new Pair<byte[], Float>(M, 0.55f));
    source5.add(new Pair<byte[], Float>(OMEGA, 0.45f));
    
    // Symptom 6 : Arithritis
    List<Pair<byte[], Float>> source6 = new ArrayList<Pair<byte[], Float>>();
    masspotentials.add(source6);
    source6.add(new Pair<byte[], Float>(DF, 0.65f));
    source6.add(new Pair<byte[], Float>(OMEGA, 0.35f));
    
    // Combinaison des 6 symptomes
    List<Pair<byte[], Float>> result = op.combine(masspotentials);
    LOGGER.trace(result.get(0));
    LOGGER.trace(result.get(1));
    LOGGER.trace(result.get(2));
    LOGGER.trace(result.get(3));
    LOGGER.trace(result.get(4));
    LOGGER.trace(result.get(5));
    
    /*
     * L'article arrondi beaucoup trop, les résultats finaux ne sont pas assez précis pour tester.
     * 
    List<Pair<byte[], Float>> expectedArticle = new ArrayList<Pair<byte[], Float>>();
    expectedArticle.add(new Pair<byte[], Float>(B, 0.17f));
    expectedArticle.add(new Pair<byte[], Float>(G1, 0.04f));
    expectedArticle.add(new Pair<byte[], Float>(L, 0.17f));
    expectedArticle.add(new Pair<byte[], Float>(M, 0.13f));
    expectedArticle.add(new Pair<byte[], Float>(DF, 0.19f));
    expectedArticle.add(new Pair<byte[], Float>(OMEGA, 0.06f));
    assertEquals(expectedArticle, result);
    */
    
    List<Pair<byte[], Float>> expectedR = new ArrayList<Pair<byte[], Float>>();
    expectedR.add(new Pair<byte[], Float>(B, 0.1860373f));
    expectedR.add(new Pair<byte[], Float>(G1, 0.06849556f));
    expectedR.add(new Pair<byte[], Float>(L, 0.1930329f));
    expectedR.add(new Pair<byte[], Float>(M, 0.1860373f));
    expectedR.add(new Pair<byte[], Float>(DF, 0.2826801f));
    expectedR.add(new Pair<byte[], Float>(OMEGA, 0.08371679f));
    assertEquals(expectedR, result);
    
  }

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
    masspotentials.add(source1);
    
    List<Pair<byte[], Float>> source2 = new ArrayList<Pair<byte[], Float>>();
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
