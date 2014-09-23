package fr.ign.cogit.r;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;

import fr.ign.cogit.geoxygene.matching.dst.operators.DempsterOp;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class RDempsterOpCompareTest {

  
  @Test
  public void testInsectDiseaseDetection() {
    
    String[] args = new String[0];

    // Just making sure we have the right version of everything
    if (!Rengine.versionCheck()) {
      System.err
          .println("** Version mismatch - Java files don't match library version.");
      System.exit(1);
    }
    System.out.println("Creating Rengine (with no arguments)");
    // 1) we pass the arguments from the command line
    // 2) we won't use the main loop at first, we'll start it later
    // (that's the "false" as second argument)
    // 3) the callbacks are implemented by the TextConsole class above
    // Rengine re = new Rengine(args, false, new TextConsole());
    Rengine re = new Rengine(args, false, null);
    System.out.println("Rengine created, waiting for R");

    // the engine creates R is a new thread, so we should wait until it's ready
    if (!re.waitForR()) {
      System.out.println("Cannot load R");
      return;
    }

    // High-level API - do not use RNI methods unless there is no other way
    // to accomplish what you want
    try {

      // Declare a R object
      REXP m11;

      // System.out.println("Chargement de la librairie EvCombR");
      re.eval("library(EvCombR)");
      // Construct a state space
      re.eval("stateSpace <- c(\"B\", \"DF\", \"M\", \"WN\", \"L\")");
      // construct mass functions for Symptom 1
      re.eval("m1 <- mass(list(\"B/DF/M/WN\"=0.45, \"B/DF/M/WN/L\"=0.55), stateSpace)");
      // construct mass functions for Symptom 2
      re.eval("m2 <- mass(list(\"B\"=0.55, \"B/DF/M/WN/L\"=0.45), stateSpace)");
      // combine "Symptom 1 x Symptom 2" by using Dempster's combination
      re.eval("m3 <- dComb(m1, m2)");
      // Symptom 3 : Skin Rash
      re.eval("m4 <- mass(list(\"L\"=0.45, \"B/DF/M/WN/L\"=0.55), stateSpace)");
      // combine "Symptom 1 x Symptom 2 x Symptom 3" by using Dempster's combination
      re.eval("m5 <- dComb(m3, m4)");
      // Symptom 4 : Paralysis
      re.eval("m6 <- mass(list(\"L\"=0.45, \"B/DF/M/WN/L\"=0.55), stateSpace)");
      // combine "Symptom 1 x Symptom 2 x Symptom 3 x Symptom 4" by using Dempster's combination
      re.eval("m7 <- dComb(m5, m6)");
      // Symptom 5 : Headache
      re.eval("m8 <- mass(list(\"M\"=0.55, \"B/DF/M/WN/L\"=0.45), stateSpace)");
      // combine "Symptom 1 x Symptom 2 x Symptom 3 x Symptom 4 x Symptom 5" by using Dempster's combination
      re.eval("m9 <- dComb(m7, m8)");
      // Symptom 6 : Arthritis
      re.eval("m10 <- mass(list(\"DF\"=0.65, \"B/DF/M/WN/L\"=0.35), stateSpace)");

      // combine "Symptom 1 x Symptom 2 x Symptom 3 x Symptom 4 x Symptom 5 x Symptom 6"
      // by using Dempster's combination
      re.eval("m11 <- dComb(m9, m10)");
      
      // System.out.println(re.eval("m11@\"focal\""));
      RList v1 = re.eval("m11@\"focal\"").asList();
      
      for (int i=0; i < v1.keys().length; i++) { 
        System.out.println(v1.keys()[i] + " : " + v1.at(i).asDouble());
      }
      
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
      System.out.println(Arrays.toString(result.get(0).getFirst()) + ", " + result.get(0).getSecond());
      System.out.println(Arrays.toString(result.get(1).getFirst()) + ", " + result.get(1).getSecond());
      System.out.println(Arrays.toString(result.get(2).getFirst()) + ", " + result.get(2).getSecond());
      System.out.println(Arrays.toString(result.get(3).getFirst()) + ", " + result.get(3).getSecond());
      System.out.println(Arrays.toString(result.get(4).getFirst()) + ", " + result.get(4).getSecond());
      System.out.println(Arrays.toString(result.get(5).getFirst()) + ", " + result.get(5).getSecond());


    } catch (Exception e) {
      System.out.println("EX:" + e);
      e.printStackTrace();
    }

    if (re != null) {
      re.end();
      re.interrupt();
      System.out.println("REngine ended");
    }
    System.out.println("");
    
    Assert.assertTrue(true);
    
  }

  

}
