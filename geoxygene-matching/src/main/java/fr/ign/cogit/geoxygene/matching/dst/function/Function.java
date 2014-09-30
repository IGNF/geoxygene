package fr.ign.cogit.geoxygene.matching.dst.function;

/**
 *  
 *
 */
public abstract class Function {
  
  protected Function[] sources;
  
  public Function(Function f) {
    this(new Function[] { f });
  }
  
  public Function(Function[] sources) {
    this.sources = sources;
  }
  
  public abstract double f(double t);
  
  public String toString() {
    String name = this.getClass().toString();
    StringBuffer buf = new StringBuffer(name);
    if (sources.length > 0) {
      buf.append("(");
      for (int i = 0; i < sources.length; i++) {
        if (i > 0) {
          buf.append(", ");
        }
        buf.append(sources[i]);
      }
      buf.append(")");
    }
    return buf.toString();
  }

}
