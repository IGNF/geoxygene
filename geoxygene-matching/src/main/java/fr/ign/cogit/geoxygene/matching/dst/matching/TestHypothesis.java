package fr.ign.cogit.geoxygene.matching.dst.matching;

import fr.ign.cogit.geoxygene.matching.dst.evidence.Hypothesis;

/**
 * Example : AvionHypothesis, SymptomHypothesis.
 *
 */
public class TestHypothesis implements Hypothesis {
  
  private String chaine;
  
  public TestHypothesis() {
    this.chaine = "aa";
  }
  
  public TestHypothesis(String chaine) {
    this.chaine = chaine;
  }

  @Override
  public String toString() {
    return chaine;
  }
  
  @Override
  public boolean equals(Object o) {
    // System.out.println("TestHypothesis : equal entre " + o + " et " + chaine);
    // System.out.println(o.getClass()); // fr.ign.cogit.geoxygene.matching.dst.evidence.TestHypothesis !!!!!!
    // if (!String.class.isAssignableFrom(o.getClass())) {
    //   return false;
    // }
    return o.equals(chaine);
  }
}
