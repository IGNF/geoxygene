package fr.ign.cogit.evidence.delayedmassvaluation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.evidence.configuration.Configuration;
import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.delayedmassvaluation.MassComplement;
import fr.ign.cogit.evidence.delayedmassvaluation.MassFunctor;
import fr.ign.cogit.evidence.delayedmassvaluation.MassValue;
import fr.ign.cogit.evidence.delayedmassvaluation.MassPotential;
import fr.ign.cogit.evidence.variable.Variable;
import fr.ign.cogit.evidence.variable.VariableFactory;
import fr.ign.cogit.evidence.variable.VariableSet;

public class EvidenceFunctorTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    
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
    MassPotential<String, Object> player1 = new MassPotential<String, Object>(d1);
    MassFunctor<Object> m1 = new MassValue<Object>(0.8);
    player1.add(f11, m1);
    player1.add(f12, new MassComplement<Object>(m1));
    player1.check(null);
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
    MassPotential<String, Object> player2 = new MassPotential<String, Object>(d2);
    MassFunctor<Object> m2 = new MassValue<Object>(0.8);
    player2.add(f21, m2);
    player2.add(f22, new MassComplement<Object>(m2));
    player2.check(null);
    System.out.println("Player 2:\n" + player2);
    
    // combination
    MassPotential<String, Object> finalPotential = player1.combination(player2);
    List<String> lfinal = new ArrayList<String>();
    lfinal.add("Colonel Mustard");
    lfinal.add("Kitchen");
    lfinal.add("Candlestick");
    Configuration<String> configFinal = new Configuration<String>(d, lfinal);
    ConfigurationSet<String> ffinal = new ConfigurationSet<String>(d);
    ffinal.add(configFinal);
    System.out.println("Player 1 + Player 2:\n" + finalPotential);
    double belief = finalPotential.bel(ffinal, null);
    System.out.println("Bel = " + belief);
    Assert.assertEquals(0.64, belief, 0.001);
    double plausibility = finalPotential.pls(ffinal, null);
    System.out.println("Pls = " + plausibility);
    Assert.assertEquals(1.0, plausibility, 0.001);
    double doubt = finalPotential.dou(ffinal, null);
    System.out.println("Dou = " + doubt);
    Assert.assertEquals(0.0, doubt, 0.001);
    double communality = finalPotential.com(ffinal, null);
    System.out.println("Com = " + communality);
    Assert.assertEquals(1.0, communality, 0.001);
    double ignorance = finalPotential.ign(ffinal, null);
    System.out.println("Ign = " + ignorance);
    Assert.assertEquals(0.36, ignorance, 0.001);
    long end = System.currentTimeMillis();
    System.out.println("Test took " + (end - start) + " ms");
  }
}
