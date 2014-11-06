package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.AssertPair;
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
    
    byte[] B     = new byte[] { 1, 0, 0, 0, 0 };
    byte[] DF    = new byte[] { 0, 1, 0, 0, 0 };
    byte[] L     = new byte[] { 0, 0, 1, 0, 0 };
    byte[] M     = new byte[] { 0, 0, 0, 1, 0 };
    byte[] G1    = new byte[] { 1, 1, 0, 1, 1 };
    byte[] OMEGA = new byte[] { 1, 1, 1, 1, 1 };
    
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
    AssertPair.assertEquals(expectedR, result);
    
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
    AssertPair.assertEquals(expected, result);
    
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
    AssertPair.assertEquals(expected, result);

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
    AssertPair.assertEquals(expected, result);

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
    AssertPair.assertEquals(expected, result);

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
    AssertPair.assertEquals(expected, result);
  }

  
  /**
   *   Test :
   *   
   *   m21 <- mass(list("CS"=0.5753, "TP/GM"=0, "CS/TP/GM"=0.4247), stateSpace)
   *   m22 <- mass(list("CS"=1, "TP/GM"=0, "CS/TP/GM"=0), stateSpace)
   *   m2 <- dComb(m21, m22)
   *   
   *   m11 <- mass(list("TP"=0.723, "CS/GM"=0, "CS/TP/GM"=0.277), stateSpace)
   *   m12 <- mass(list("TP"=0.6975, "CS/GM"=0.1681, "CS/TP/GM"=0.1344), stateSpace)
   *   m1 <- dComb(m11, m12)
   *   
   *   m31 <- mass(list("GM"=0.4913, "CS/TP"=0.1044, "CS/TP/GM"=0.4043), stateSpace)
   *   m32 <- mass(list("GM"=0.6975, "CS/TP"=0.1681, "CS/TP/GM"=0.1344), stateSpace)
   *   m3 <- dComb(m31, m32)
   *
   *   m123 <- dComb(m1, m2, m3)
   */
  @Test
  public void testColSiberie() {
    
    DempsterOp op = new DempsterOp(false);
    
    // ----------------------------------------------------------------------------
  
    List<List<Pair<byte[], Float>>> m1 = new ArrayList<List<Pair<byte[], Float>>>();
    
    byte[] CS = new byte[] { 1, 0, 0 };
    byte[] TP_GM = new byte[] { 0, 1, 1 };
    byte[] TOUT = new byte[] { 1, 1, 1 };
    
    List<Pair<byte[], Float>> source11 = new ArrayList<Pair<byte[], Float>>();
    m1.add(source11);
    
    List<Pair<byte[], Float>> source12 = new ArrayList<Pair<byte[], Float>>();
    m1.add(source12);
    
    // 
    source11.add(new Pair<byte[], Float>(CS, 0.5753f));
    source11.add(new Pair<byte[], Float>(TP_GM, 0f));
    source11.add(new Pair<byte[], Float>(TOUT, 0.4247f));
    
    //
    source12.add(new Pair<byte[], Float>(CS, 1f));
    source12.add(new Pair<byte[], Float>(TP_GM, 0f));
    source12.add(new Pair<byte[], Float>(TOUT, 0f));
    
    List<Pair<byte[], Float>> result1 = op.combine(m1);
    
    List<Pair<byte[], Float>> expected1 = new ArrayList<Pair<byte[], Float>>();
    expected1.add(new Pair<byte[], Float>(CS, 1f));
    expected1.add(new Pair<byte[], Float>(TP_GM, 0f));
    expected1.add(new Pair<byte[], Float>(TOUT, 0f));
    
    AssertPair.assertEquals(expected1, result1);
    
    // ----------------------------------------------------------------------------
    
    List<List<Pair<byte[], Float>>> m2 = new ArrayList<List<Pair<byte[], Float>>>();
    
    byte[] TP = new byte[] { 0, 1, 0 };
    byte[] CS_GM = new byte[] { 1, 0, 1 };
    
    List<Pair<byte[], Float>> source21 = new ArrayList<Pair<byte[], Float>>();
    m2.add(source21);
    
    List<Pair<byte[], Float>> source22 = new ArrayList<Pair<byte[], Float>>();
    m2.add(source22);
    
    // 
    source21.add(new Pair<byte[], Float>(TP, 0.723f));
    source21.add(new Pair<byte[], Float>(CS_GM, 0f));
    source21.add(new Pair<byte[], Float>(TOUT, 0.277f));
    
    //
    source22.add(new Pair<byte[], Float>(TP, 0.6975f));
    source22.add(new Pair<byte[], Float>(CS_GM, 0.1681f));
    source22.add(new Pair<byte[], Float>(TOUT, 0.1344f));
    
    List<Pair<byte[], Float>> result2 = op.combine(m2);
    
    List<Pair<byte[], Float>> expected2 = new ArrayList<Pair<byte[], Float>>();
    expected2.add(new Pair<byte[], Float>(TP, 0.9046147f));
    expected2.add(new Pair<byte[], Float>(CS_GM, 0.05300583f));
    expected2.add(new Pair<byte[], Float>(TOUT, 0.04237944f));
    
    AssertPair.assertEquals(expected2, result2);
    
    // ----------------------------------------------------------------------------
    
    List<List<Pair<byte[], Float>>> m3 = new ArrayList<List<Pair<byte[], Float>>>();
    
    byte[] GM = new byte[] { 0, 0, 1 };
    byte[] CS_TP = new byte[] { 1, 1, 0 };
    
    List<Pair<byte[], Float>> source31 = new ArrayList<Pair<byte[], Float>>();
    m3.add(source31);
    
    List<Pair<byte[], Float>> source32 = new ArrayList<Pair<byte[], Float>>();
    m3.add(source32);
    
    // 
    source31.add(new Pair<byte[], Float>(GM, 0.4913f));
    source31.add(new Pair<byte[], Float>(CS_TP, 0.1044f));
    source31.add(new Pair<byte[], Float>(TOUT, 0.4043f));
    
    //
    source32.add(new Pair<byte[], Float>(GM, 0.6975f));
    source32.add(new Pair<byte[], Float>(CS_TP, 0.1681f));
    source32.add(new Pair<byte[], Float>(TOUT, 0.1344f));
    
    List<Pair<byte[], Float>> result3 = op.combine(m3);
    
    List<Pair<byte[], Float>> expected3 = new ArrayList<Pair<byte[], Float>>();
    expected3.add(new Pair<byte[], Float>(GM, 0.8178038f));
    expected3.add(new Pair<byte[], Float>(CS_TP, 0.1178601f));
    expected3.add(new Pair<byte[], Float>(TOUT, 0.06433618f));
    
    AssertPair.assertEquals(expected3, result3);
    
    // ----------------------------------------------------------------------------
    
    List<List<Pair<byte[], Float>>> m = new ArrayList<List<Pair<byte[], Float>>>();
    
    m.add(result1);
    m.add(result2);
    m.add(result3);
    
    List<Pair<byte[], Float>> result = op.combine(m);
    for (int i = 0; i < result.size(); i++) {
      LOGGER.trace(Arrays.toString(result.get(i).getFirst()) + " : " + result.get(i).getSecond());
    }
    List<Pair<byte[], Float>> expected = new ArrayList<Pair<byte[], Float>>();
    expected.add(new Pair<byte[], Float>(GM, 0f));
    expected.add(new Pair<byte[], Float>(CS, 1f));
    expected.add(new Pair<byte[], Float>(TP, 0f));
    expected.add(new Pair<byte[], Float>(CS_TP, 0f));
    expected.add(new Pair<byte[], Float>(CS_GM, 0f));
    expected.add(new Pair<byte[], Float>(TP_GM, 0f));
    expected.add(new Pair<byte[], Float>(TOUT, 0f));
    
    AssertPair.assertEquals(expected, result);
  }
}
