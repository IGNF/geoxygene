/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.api;

public enum SimpleOperator {
  SUP, INF, EQ_SUP, EQ_INF, EQUAL, DIFF;

  public static SimpleOperator shortcut(String text) {
    SimpleOperator op = EQUAL;
    if (text.equals(">"))
      op = SUP;
    else if (text.equals("<"))
      op = INF;
    else if (text.equals(">="))
      op = EQ_SUP;
    else if (text.equals("<="))
      op = EQ_INF;
    else if (text.equals("<>") || text.equals("!="))
      op = DIFF;
    return op;
  }

  public String toShortcut() {
    String rac = "=";
    if (this.equals(SUP))
      rac = ">";
    else if (this.equals("INF"))
      rac = "<";
    else if (this.equals("EQ_SUP"))
      rac = ">=";
    else if (this.equals("EQ_INF"))
      rac = "<=";
    else if (this.equals("DIFF"))
      rac = "<>";
    return rac;
  }

  /**
   * Get the reverse of {@code this} operator, e.g. "<" is the reverse of ">=".
   * @return
   */
  public SimpleOperator getReverse() {
    if (this.equals(SUP))
      return EQ_INF;
    else if (this.equals("INF"))
      return EQ_SUP;
    else if (this.equals("EQ_SUP"))
      return INF;
    else if (this.equals("EQ_INF"))
      return SUP;
    else if (this.equals(EQUAL))
      return DIFF;
    else
      return EQUAL;
  }

  public boolean compare(int nb1, int nb2) {
    if (this.equals(EQUAL)) {
      if (nb1 == nb2)
        return true;
      return false;
    } else if (this.equals(SUP)) {
      if (nb1 > nb2)
        return true;
      return false;
    } else if (this.equals(INF)) {
      if (nb1 < nb2)
        return true;
      return false;
    } else if (this.equals(EQ_SUP)) {
      if (nb1 >= nb2)
        return true;
      return false;
    } else if (this.equals(DIFF)) {
      if (nb1 != nb2)
        return true;
      return false;
    } else {
      if (nb1 <= nb2)
        return true;
      return false;
    }
  }

  public boolean compare(double nb1, double nb2) {
    if (this.equals(EQUAL)) {
      if (nb1 == nb2)
        return true;
      return false;
    } else if (this.equals(SUP)) {
      if (nb1 > nb2)
        return true;
      return false;
    } else if (this.equals(INF)) {
      if (nb1 < nb2)
        return true;
      return false;
    } else if (this.equals(EQ_SUP)) {
      if (nb1 >= nb2)
        return true;
      return false;
    } else if (this.equals(DIFF)) {
      if (nb1 != nb2)
        return true;
      return false;
    } else {
      if (nb1 <= nb2)
        return true;
      return false;
    }
  }
}
