package fr.ign.cogit.evidence.massvalues;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.evidence.configuration.Configuration;
import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.massvalues.MassPotential;
import fr.ign.cogit.evidence.variable.Variable;
import fr.ign.cogit.evidence.variable.VariableFactory;
import fr.ign.cogit.evidence.variable.VariableSet;

public class SimpleEvidenceTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    System.out.println("TEST EVIDENZ");
    long start = System.currentTimeMillis();
    VariableFactory<String> vf = new VariableFactory<String>();
    Variable<String> murderer = vf.newVariable();
    murderer.add("Colonel Mustard");
    murderer.add("Miss Scarlett");
    murderer.add("Mrs Peacock");
    System.out.println("Variable murderer: " + murderer);
    Variable<String> room = vf.newVariable();
    room.add("Dining room");
    room.add("Kitchen");
    System.out.println("Variable room: " + room);
    Variable<String> weapon = vf.newVariable();
    weapon.add("Dagger");
    weapon.add("Candlestick");
    System.out.println("Variable weapon: " + weapon);
    VariableSet<String> d = new VariableSet<String>(vf);
    d.add(murderer);
    d.add(room);
    d.add(weapon);
    // first player
    VariableSet<String> d1 = new VariableSet<String>(vf);
    d1.add(murderer);
    d1.add(room);
    List<String> l11 = new ArrayList<String>();
    l11.add("Colonel Mustard");
    l11.add("Kitchen");
    Configuration<String> config11 = new Configuration<String>(d1, l11);
    ConfigurationSet<String> f11 = new ConfigurationSet<String>(d1);
    f11.add(config11);
    ConfigurationSet<String> f12 = new ConfigurationSet<String>(d1);
    f12.addAllConfigurations();
    MassPotential<String> player1 = new MassPotential<String>(d1);
    double m1 = 0.8;
    player1.add(f11, m1);
    player1.add(f12, 1 - m1);
    player1.check();
    System.out.println("Player 1:\n" + player1);
    // second player
    VariableSet<String> d2 = new VariableSet<String>(vf);
    d2.add(murderer);
    d2.add(weapon);
    List<String> l21 = new ArrayList<String>();
    l21.add("Colonel Mustard");
    l21.add("Candlestick");
    Configuration<String> config21 = new Configuration<String>(d2, l21);
    ConfigurationSet<String> f21 = new ConfigurationSet<String>(d2);
    f21.add(config21);
    ConfigurationSet<String> f22 = new ConfigurationSet<String>(d2);
    f22.addAllConfigurations();
    MassPotential<String> player2 = new MassPotential<String>(d2);
    double m2 = 0.8;
    player2.add(f21, m2);
    player2.add(f22, 1 - m2);
    player2.check();
    System.out.println("Player 2:\n" + player2);
    // combination
    MassPotential<String> finalPotential = player1.combination(player2);
    System.out.println("finalPotential:\n" + finalPotential);

    List<String> lfinal = new ArrayList<String>();
    lfinal.add("Colonel Mustard");
    lfinal.add("Kitchen");
    lfinal.add("Candlestick");
    Configuration<String> configFinal = new Configuration<String>(d, lfinal);
    ConfigurationSet<String> ffinal = new ConfigurationSet<String>(d);
    ffinal.add(configFinal);
    System.out.println("Player 1 + Player 2:\n" + finalPotential);
    double belief = finalPotential.bel(ffinal);
    System.out.println("Bel = " + belief);
    Assert.assertEquals(0.64, belief, 0.001);
    double plausibility = finalPotential.pls(ffinal);
    System.out.println("Pls = " + plausibility);
    Assert.assertEquals(1.0, plausibility, 0.001);
    double doubt = finalPotential.dou(ffinal);
    System.out.println("Dou = " + doubt);
    Assert.assertEquals(0.0, doubt, 0.001);
    double communality = finalPotential.com(ffinal);
    System.out.println("Com = " + communality);
    Assert.assertEquals(1.0, communality, 0.001);
    double ignorance = finalPotential.ign(ffinal);
    System.out.println("Ign = " + ignorance); 
    Assert.assertEquals(0.36, ignorance, 0.001);
    long end = System.currentTimeMillis();
    System.out.println("Test took " + (end - start) + " ms");
    System.out.println();
  }
  @Test
  public void testSmets() {
    System.out.println("TEST SMETS");
    long start = System.currentTimeMillis();
    VariableFactory<String> vf = new VariableFactory<String>();
    Variable<String> var = vf.newVariable();
    var.add("A");
    var.add("B");
    var.add("C");
    System.out.println("Variable: " + var);
    VariableSet<String> d = new VariableSet<String>(vf);
    d.add(var);
    System.out.println("nb of configurations = " + d.getNumberOfConfigurations());
    // first source
    List<String> l11 = new ArrayList<String>();
    l11.add("A");
    Configuration<String> config11 = new Configuration<String>(d, l11);
    ConfigurationSet<String> f11 = new ConfigurationSet<String>(d);
    f11.add(config11);
    List<String> l12 = new ArrayList<String>();
    l12.add("B");
    Configuration<String> config12 = new Configuration<String>(d, l12);
    ConfigurationSet<String> f12 = new ConfigurationSet<String>(d);
    f12.add(config12);
    List<String> l13A = new ArrayList<String>();
    l13A.add("A");
    Configuration<String> config13A = new Configuration<String>(d, l13A);
    List<String> l13B = new ArrayList<String>();
    l13B.add("B");
    Configuration<String> config13B = new Configuration<String>(d, l13B);
    ConfigurationSet<String> f13 = new ConfigurationSet<String>(d);
    f13.add(config13A);
    f13.add(config13B);
    MassPotential<String> mp1 = new MassPotential<String>(d);
    mp1.add(f11, 0.6);
    mp1.add(f12, 0.1);
    mp1.add(f13, 0.3);
    mp1.check();
    System.out.println("Source 1:\n" + mp1);
    // second source
    List<String> l21 = new ArrayList<String>();
    l21.add("B");
    Configuration<String> config21 = new Configuration<String>(d, l21);
    ConfigurationSet<String> f21 = new ConfigurationSet<String>(d);
    f21.add(config21);
    List<String> l22 = new ArrayList<String>();
    l22.add("C");
    Configuration<String> config22 = new Configuration<String>(d, l22);
    ConfigurationSet<String> f22 = new ConfigurationSet<String>(d);
    f22.add(config22);
    List<String> l23A = new ArrayList<String>();
    l23A.add("B");
    Configuration<String> config23A = new Configuration<String>(d, l23A);
    List<String> l23B = new ArrayList<String>();
    l23B.add("C");
    Configuration<String> config23B = new Configuration<String>(d, l23B);
    ConfigurationSet<String> f23 = new ConfigurationSet<String>(d);
    f23.add(config23A);
    f23.add(config23B);
    MassPotential<String> mp2 = new MassPotential<String>(d);
    mp2.add(f21, 0.7);
    mp2.add(f22, 0.2);
    mp2.add(f23, 0.1);
    mp2.check();
    System.out.println("Source 2:\n" + mp2);
    // combination
    MassPotential<String> finalPotential = mp1.combinationForPatiallyOverlappingFrames(mp2, false);
    System.out.println("MP 1 + MP 2:\n" + finalPotential);
    List<String> list = new ArrayList<String>();
    list.add("A");
    Configuration<String> config = new Configuration<String>(d, list);
    double pignistic = finalPotential.pignistic(config);
    System.out.println("pignistic A = " + pignistic);
    Assert.assertEquals(0.455, pignistic, 0.0001);
    list.clear();
    list.add("B");
    config = new Configuration<String>(d, list);
    pignistic = finalPotential.pignistic(config);
    System.out.println("pignistic B = " + pignistic); 
    Assert.assertEquals(0.19, pignistic, 0.0001);
    list.clear();
    list.add("C");
    config = new Configuration<String>(d, list);
    pignistic = finalPotential.pignistic(config);
    System.out.println("pignistic C = " + pignistic); 
    Assert.assertEquals(0.355, pignistic, 0.001);
    long end = System.currentTimeMillis();
    System.out.println("Test took " + (end - start) + " ms");
    System.out.println();
  }

  @Test
  public void testOlteanu() {
    System.out.println("TEST OLTEANU");
    long start = System.currentTimeMillis();
    VariableFactory<String> vf = new VariableFactory<String>();
    Variable<String> var = vf.newVariable();
    var.add("C1");
    var.add("C2");
    var.add("C3");
    System.out.println("Variable: " + var);
    VariableSet<String> d = new VariableSet<String>(vf);
    d.add(var);
    // first source
    List<String> l11 = new ArrayList<String>();
    l11.add("C1");
    Configuration<String> config11 = new Configuration<String>(d, l11);
    ConfigurationSet<String> f11 = new ConfigurationSet<String>(d);
    f11.add(config11);
    List<String> l12 = new ArrayList<String>();
    l12.add("C2");
    Configuration<String> config12 = new Configuration<String>(d, l12);
    ConfigurationSet<String> f12 = new ConfigurationSet<String>(d);
    f12.add(config12);
    List<String> l13 = new ArrayList<String>();
    l13.add("C3");
    Configuration<String> config13 = new Configuration<String>(d, l13);
    ConfigurationSet<String> f13 = new ConfigurationSet<String>(d);
    f13.add(config13);
    MassPotential<String> mp1 = new MassPotential<String>(d);
    mp1.add(f11, 0.6);
    mp1.add(f12, 0.2);
    mp1.add(f13, 0.2);
    mp1.check();
    System.out.println("Source 1:\n" + mp1);
    // second source
    List<String> l21 = new ArrayList<String>();
    l21.add("C1");
    Configuration<String> config21 = new Configuration<String>(d, l21);
    ConfigurationSet<String> f21 = new ConfigurationSet<String>(d);
    f21.add(config21);
    List<String> l22 = new ArrayList<String>();
    l22.add("C2");
    Configuration<String> config22 = new Configuration<String>(d, l22);
    ConfigurationSet<String> f22 = new ConfigurationSet<String>(d);
    f22.add(config22);
    List<String> l23 = new ArrayList<String>();
    l23.add("C3");
    Configuration<String> config23 = new Configuration<String>(d, l23);
    ConfigurationSet<String> f23 = new ConfigurationSet<String>(d);
    f23.add(config23);
    MassPotential<String> mp2 = new MassPotential<String>(d);
    mp2.add(f21, 0.9);
    mp2.add(f22, 0.1);
    mp2.add(f23, 0.);
    mp2.check();
    System.out.println("Source 2:\n" + mp2);
    // combination
    MassPotential<String> finalPotential = mp1.combination(mp2);
    System.out.println("MP 1 + MP 2:\n" + finalPotential);
    ConfigurationSet<String> ffinal = finalPotential.decide(true);
    System.out.println("ffinal2:\n" + ffinal);
    double belief = finalPotential.bel(ffinal);
    System.out.println("Bel = " + belief);
    Assert.assertEquals(0.96, belief, 0.005);
    double plausibility = finalPotential.pls(ffinal);
    System.out.println("Pls = " + plausibility);
    Assert.assertEquals(0.96, plausibility, 0.005);
    double doubt = finalPotential.dou(ffinal);
    System.out.println("Dou = " + doubt);
    Assert.assertEquals(0.04, doubt, 0.005);
    double communality = finalPotential.com(ffinal);
    System.out.println("Com = " + communality);
    Assert.assertEquals(0.96, communality, 0.005);
    double ignorance = finalPotential.ign(ffinal);
    System.out.println("Ign = " + ignorance); 
    Assert.assertEquals(0.0, ignorance, 0.005);
    double pignistic = finalPotential.pignistic(ffinal);
    System.out.println("pignistic = " + pignistic); 
    Assert.assertEquals(0.96, pignistic, 0.005);
    long end = System.currentTimeMillis();
    System.out.println("Test took " + (end - start) + " ms");
  }
  @Test
  public void testOlteanuSpecialized() {
    System.out.println("TEST OLTEANU SPECIALIZED SOURCES");//p110
    long start = System.currentTimeMillis();
    VariableFactory<String> vf = new VariableFactory<String>();
    Variable<String> var = vf.newVariable();
    var.add("C1");
    var.add("C2");
    var.add("C3");
    System.out.println("Variable: " + var);
    VariableSet<String> d = new VariableSet<String>(vf);
    d.add(var);
    // first source
    List<String> l11 = new ArrayList<String>();
    l11.add("C1");
    Configuration<String> config11 = new Configuration<String>(d, l11);
    ConfigurationSet<String> f11 = new ConfigurationSet<String>(d);
    f11.add(config11);
    ConfigurationSet<String> f12 = new ConfigurationSet<String>(d);
    f12.addAllConfigurations();
    f12.remove(config11);
    ConfigurationSet<String> f13 = new ConfigurationSet<String>(d);
    System.out.println("addAllConfigurations");
    f13.addAllConfigurations();
    System.out.println("addAllConfigurations");
    MassPotential<String> mp1 = new MassPotential<String>(d);
    mp1.add(f11, 0.4);
    mp1.add(f12, 0.0);
    mp1.add(f13, 0.6);
    mp1.check();
    System.out.println("Source 1:\n" + mp1);
    // second source
    List<String> l21 = new ArrayList<String>();
    l21.add("C2");
    Configuration<String> config21 = new Configuration<String>(d, l21);
    ConfigurationSet<String> f21 = new ConfigurationSet<String>(d);
    f21.add(config21);
    ConfigurationSet<String> f22 = new ConfigurationSet<String>(d);
    f22.addAllConfigurations();
    f22.remove(config21);
    ConfigurationSet<String> f23 = new ConfigurationSet<String>(d);
    f23.addAllConfigurations();
    MassPotential<String> mp2 = new MassPotential<String>(d);
    mp2.add(f21, 0.1);
    mp2.add(f22, 0.9);
    mp2.add(f23, 0.);
    mp2.check();
    System.out.println("Source 2:\n" + mp2);
    // third source
    List<String> l31 = new ArrayList<String>();
    l31.add("C3");
    Configuration<String> config31 = new Configuration<String>(d, l31);
    ConfigurationSet<String> f31 = new ConfigurationSet<String>(d);
    f31.add(config31);
    ConfigurationSet<String> f32 = new ConfigurationSet<String>(d);
    f32.addAllConfigurations();
    f32.remove(config31);
    ConfigurationSet<String> f33 = new ConfigurationSet<String>(d);
    f33.addAllConfigurations();
    MassPotential<String> mp3 = new MassPotential<String>(d);
    mp3.add(f31, 0.35);
    mp3.add(f32, 0.0);
    mp3.add(f33, 0.65);
    mp3.check();
    System.out.println("Source 3:\n" + mp3);
    // fourth source
    MassPotential<String> mp4 = new MassPotential<String>(d);
    mp4.add(f11, 0.3);
    mp4.add(f12, 0.0);
    mp4.add(f13, 0.7);
    mp4.check();
    System.out.println("Source 4:\n" + mp4);
    // fifth source
    MassPotential<String> mp5 = new MassPotential<String>(d);
    mp5.add(f21, 0.0);
    mp5.add(f22, 1.0);
    mp5.add(f23, 0.0);
    mp5.check();
    System.out.println("Source 5:\n" + mp5);
    // sixth source
    MassPotential<String> mp6 = new MassPotential<String>(d);
    mp6.add(f31, 0.3);
    mp6.add(f32, 0.0);
    mp6.add(f33, 0.7);
    mp6.check();
    System.out.println("Source 6:\n" + mp6);
    // combination
    Set<MassPotential<String>> potentialSet = new HashSet<MassPotential<String>>();
    potentialSet.add(mp1);
    potentialSet.add(mp2);
    potentialSet.add(mp3);
    potentialSet.add(mp4);
    potentialSet.add(mp5);
    potentialSet.add(mp6);
    MassPotential<String> finalPotential = MassPotential.combination(potentialSet, false);
    System.out.println("Final MP:\n" + finalPotential);
    double massC1 = finalPotential.mass(f11);
    System.out.println("MASS C1 "+ massC1);
    Assert.assertEquals(0.24, massC1, 0.01);
    double massC3 = finalPotential.mass(f31);
    System.out.println("MASS C3 "+ massC3);
    Assert.assertEquals(0.2, massC3, 0.01);
    double massC13 = finalPotential.mass(f22);
    System.out.println("MASS C1+C3 "+ massC13);
    Assert.assertEquals(0.17, massC13, 0.01);
    double massEmpty = finalPotential.mass(new ConfigurationSet<String>(d));
    System.out.println("MASS EMPTY "+ massEmpty);
    Assert.assertEquals(0.39, massEmpty, 0.01);
    
    System.out.println("NORMALIZING");
    finalPotential = finalPotential.norm();
    System.out.println("Final NORMALIZED MP:\n" + finalPotential);
    massC1 = finalPotential.mass(f11);
    System.out.println("MASS C1 "+ massC1);
    Assert.assertEquals(0.4, massC1, 0.02);
    massC3 = finalPotential.mass(f31);
    System.out.println("MASS C3 "+ massC3);
    Assert.assertEquals(0.34, massC3, 0.01);
    massC13 = finalPotential.mass(f22);
    System.out.println("MASS C1+C3 "+ massC13);
    Assert.assertEquals(0.288, massC13, 0.01);
    massEmpty = finalPotential.mass(new ConfigurationSet<String>(d));
    System.out.println("MASS EMPTY "+ massEmpty);
    Assert.assertEquals(0., massEmpty, 0.01);
    
    ConfigurationSet<String> ffinal = finalPotential.decide(true);
    System.out.println("Final decision:\n" + ffinal);
    double pignistic = finalPotential.pignistic(ffinal);
    System.out.println("pignistic = " + pignistic); 
    Assert.assertEquals(0.5, pignistic, 0.03);
    
    long end = System.currentTimeMillis();
    System.out.println("Test took " + (end - start) + " ms");
    System.out.println();
  }
}
