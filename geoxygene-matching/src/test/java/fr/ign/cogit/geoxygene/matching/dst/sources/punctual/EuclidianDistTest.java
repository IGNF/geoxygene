package fr.ign.cogit.geoxygene.matching.dst.sources.punctual;

import org.junit.Test;

import fr.ign.cogit.geoxygene.function.LinearFunction;


public class EuclidianDistTest {
  
  @Test
  public void testEvaluate() {
    
    EuclidianDist source = new EuclidianDist();

    // F1
    LinearFunction f11 = new LinearFunction(75, 0);
    f11.setDomainOfFunction(0., 50., true, false);
    
    
    //Function f12x = new Arithmetic('+', new Constant(75), new Arithmetic('*', new Constant(25), new T()));
    //Function f11y = new Arithmetic('-', new Constant(1), new Arithmetic('*', new Constant(0.9), new T()));
    //Function f12y = new Constant(0.1);
    // source.setF1x(new Function[] { f11x, f12x });
    // source.setF1y(new Function[] { f11y, f12y });
    
    // F2
    
    
    // F3
    
    // double m1 = source.evaluate(ref, candidate);
    
  }

}
