package fr.ign.parameters;

public class Direction {
  
  private boolean doubleSens = true;
  private String attribute = "";
  private String valueSensDirect = "";
  private String valueDoubleDens = "";
  
  public Direction() {
  }
  
  public void setDoubleSens(boolean doubleSens) {
    this.doubleSens = doubleSens;
  }
  
  public void setValueSensDirect(String valDirect) {
    this.valueSensDirect = valDirect;
  }
  
  public boolean getDoubleSens() {
    return doubleSens;
  }
  
  public String toString() {
    if (doubleSens) {
      return "DoubleSens";
    } else {
      return "SimpleSens ('" + valueSensDirect + "')";
    }
  }
}
