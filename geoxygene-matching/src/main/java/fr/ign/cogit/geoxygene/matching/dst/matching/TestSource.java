package fr.ign.cogit.geoxygene.matching.dst.matching;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class TestSource implements Source<String, TestHypothesis> {
  
  @Override
  public List<Pair<byte[], Float>> evaluate(String reference, List<TestHypothesis> candidates,
      EvidenceCodec<TestHypothesis> codec) {
    // System.out.println("Evaluate TestSource");
    List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
    
    float sum = 0;
    for (TestHypothesis h : candidates) {
      // System.out.println("avant encoded = " + h);
      byte[] encoded = codec.encode(new TestHypothesis[] { h });
      
      // System.out.println("encoded h = " + h);
      // System.out.println("encoded = " + encoded);
      Pair<byte[], Float> zz = new Pair<byte[], Float>(encoded, (float) 1);
      // System.out.println("encoded = " + zz);
      weightedfocalset.add(zz);
      sum += 1;
    }
    
    for (Pair<byte[], Float> st : weightedfocalset) {
      st.setSecond(st.getSecond() / sum);
    }
    CombinationAlgos.sortKernel(weightedfocalset);
    return weightedfocalset;
  }
  
  @Override
  public double evaluate(String ref, TestHypothesis candidate) {
    // return compute(ref.getGeom(), candidate.getGeom());
    // System.out.println("Evaluate TestSource");
    return 0;
  }
  
  @Override
  public String getName() {
    return "Test";
  }

}
