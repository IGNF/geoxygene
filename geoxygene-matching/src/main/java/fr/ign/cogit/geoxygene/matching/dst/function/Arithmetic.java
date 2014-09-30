package fr.ign.cogit.geoxygene.matching.dst.function;

/**
 * 
 *
 */
public class Arithmetic extends Function {

  protected char op;
  
  public Arithmetic(char op, Function f1, Function f2) {
    super(new Function[] { f1 , f2 });
    this.op = op;
  }
  
  public double f(double t) {
    switch (op) {
      case '+':
        return sources[0].f(t) + sources[1].f(t);
      case '-':
        return sources[0].f(t) - sources[1].f(t);
      case '*':
        return sources[0].f(t) * sources[1].f(t);
      case '/':
        return sources[0].f(t) / sources[1].f(t);
      default:
        return 0;
    }
  }
}
