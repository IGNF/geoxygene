package fr.ign.cogit.geoxygene.util.math;

import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CombinationSetTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetTotalNb() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    Assert.assertTrue(combiSet.getTotalNb() == 7);
  }

  @Test
  public void testNbOfCombinations() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    Assert.assertTrue(combiSet.nbOfCombinations(2) == 3);
  }

  @Test
  public void testNbOfCombinationsAbove() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    Assert.assertTrue(combiSet.nbOfCombinationsAbove(2) == 4);
  }

  @Test
  public void testGetAllCombinations() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    List<Combination> combis = combiSet.getAllCombinations();
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(1);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.add(2);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(2);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.add(3);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(3);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.add(1);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
  }

  @Test
  public void testGetAllCombinationsOfNElements() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    List<Combination> combis = combiSet.getAllCombinationsOfNElements(1);
    set.clear();
    set.add(1);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(2);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(3);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
  }

  @Test
  public void testGetAllCombinationsOfNOrMoreElements() {
    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(2);
    set.add(3);
    CombinationSet combiSet = new CombinationSet(set);
    List<Combination> combis = combiSet.getAllCombinationsOfNOrMoreElements(2);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(1);
    set.add(2);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(2);
    set.add(3);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
    set.clear();
    set.add(3);
    set.add(1);
    Assert.assertTrue(combis.contains(new Combination(set, combiSet)));
  }

}
