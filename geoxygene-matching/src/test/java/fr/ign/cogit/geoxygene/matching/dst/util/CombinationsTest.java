package fr.ign.cogit.geoxygene.matching.dst.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 */
public class CombinationsTest {
  
  private final static Logger LOGGER = Logger.getLogger(CombinationsTest.class);
  
  @Test
  public void testEnumerate() {
    
    List<String> candidates1 = new ArrayList<String>();
    candidates1.add("A");
    candidates1.add("B");
    LinkedList<List<String>> combinations = Combinations.enumerate(candidates1);
    
    Assert.assertEquals(3, combinations.size());
    
    // Compare LinkedList
    LinkedList<List<String>> expected = new LinkedList<List<String>>();
    List<String> A = new ArrayList<String>();
    A.add("A");
    expected.add(A);
    List<String> B = new ArrayList<String>();
    B.add("B");
    expected.add(B);
    List<String> AB = new ArrayList<String>();
    AB.add("A");
    AB.add("B");
    expected.add(AB);
    
    Assert.assertEquals(expected, combinations);
    for (List<String> l : combinations) {
      LOGGER.debug("Element de la combinaison : " + l.toString());
    }
    
  }

}
