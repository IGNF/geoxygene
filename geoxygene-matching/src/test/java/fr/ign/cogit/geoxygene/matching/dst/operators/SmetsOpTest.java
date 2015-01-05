package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class SmetsOpTest {
  
  @Test
  public void test1() {
    
    List<List<Pair<byte[], Float>>> beliefs = new ArrayList<List<Pair<byte[], Float>>>();
    
    List<Pair<byte[], Float>> ligne1 = new ArrayList<Pair<byte[], Float>>();
    byte[] t11 = new byte[] { 1, 0, 0, 0, 0, 0, 0 };
    byte[] t12 = new byte[] { 0, 1, 1, 1, 1, 1, 1 };
    byte[] t13 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne1.add(new Pair<byte[], Float>(t11, 0f));
    ligne1.add(new Pair<byte[], Float>(t12, 1f));
    ligne1.add(new Pair<byte[], Float>(t13, 0f));
    beliefs.add(ligne1);
    
    List<Pair<byte[], Float>> ligne2 = new ArrayList<Pair<byte[], Float>>();
    byte[] t21 = new byte[] { 0, 1, 0, 0, 0, 0, 0 };
    byte[] t22 = new byte[] { 1, 0, 1, 1, 1, 1, 1 };
    byte[] t23 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne2.add(new Pair<byte[], Float>(t21, 0f));
    ligne2.add(new Pair<byte[], Float>(t22, 1f));
    ligne2.add(new Pair<byte[], Float>(t23, 0f));
    beliefs.add(ligne2);
    
    List<Pair<byte[], Float>> ligne3 = new ArrayList<Pair<byte[], Float>>();
    byte[] t31 = new byte[] { 0, 0, 1, 0, 0, 0, 0 };
    byte[] t32 = new byte[] { 1, 1, 0, 1, 1, 1, 1 };
    byte[] t33 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne3.add(new Pair<byte[], Float>(t31, 0f));
    ligne3.add(new Pair<byte[], Float>(t32, 1f));
    ligne3.add(new Pair<byte[], Float>(t33, 0f));
    beliefs.add(ligne3);
    
    List<Pair<byte[], Float>> ligne4 = new ArrayList<Pair<byte[], Float>>();
    byte[] t41 = new byte[] { 0, 0, 0, 1, 0, 0, 0 };
    byte[] t42 = new byte[] { 1, 1, 1, 0, 1, 1, 1 };
    byte[] t43 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne4.add(new Pair<byte[], Float>(t41, 0f));
    ligne4.add(new Pair<byte[], Float>(t42, 1f));
    ligne4.add(new Pair<byte[], Float>(t43, 0f));
    beliefs.add(ligne4);
    
    List<Pair<byte[], Float>> ligne5 = new ArrayList<Pair<byte[], Float>>();
    byte[] t51 = new byte[] { 0, 0, 0, 0, 1, 0, 0 };
    byte[] t52 = new byte[] { 1, 1, 1, 1, 0, 1, 1 };
    byte[] t53 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne5.add(new Pair<byte[], Float>(t51, 0f));
    ligne5.add(new Pair<byte[], Float>(t52, 1f));
    ligne5.add(new Pair<byte[], Float>(t53, 0f));
    beliefs.add(ligne5);
    
    List<Pair<byte[], Float>> ligne6 = new ArrayList<Pair<byte[], Float>>();
    byte[] t61 = new byte[] { 0, 0, 0, 0, 0, 1, 0 };
    byte[] t62 = new byte[] { 1, 1, 1, 1, 1, 0, 1 };
    byte[] t63 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne6.add(new Pair<byte[], Float>(t61, 0f));
    ligne6.add(new Pair<byte[], Float>(t62, 1f));
    ligne6.add(new Pair<byte[], Float>(t63, 0f));
    beliefs.add(ligne6);
    
    List<Pair<byte[], Float>> ligne7 = new ArrayList<Pair<byte[], Float>>();
    byte[] t71 = new byte[] { 0, 0, 0, 0, 0, 0, 1 };
    byte[] t72 = new byte[] { 1, 1, 1, 1, 1, 1, 0 };
    byte[] t73 = new byte[] { 1, 1, 1, 1, 1, 1, 1 };
    ligne7.add(new Pair<byte[], Float>(t71, 0f));
    ligne7.add(new Pair<byte[], Float>(t72, 1f));
    ligne7.add(new Pair<byte[], Float>(t73, 0f));
    beliefs.add(ligne7);
    
    CombinationOp op = new SmetsOp(true);
    List<Pair<byte[], Float>> result = op.combine(beliefs);
    
    // System.out.println("Result");
    // for (Pair<byte[], Float> test : result) {
      // System.out.println(Arrays.toString(test.getFirst()) + ", " + test.getSecond());
    // }
    
  }

}
