/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.expressions;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementExpression;
import fr.ign.cogit.geoxygene.spatialrelation.api.SimpleOperator;

public class ThresholdExpression implements AchievementExpression {

  private Object thresholdValue;
  private SimpleOperator operator;
  private Comparator<Object> comparator;

  public boolean validate(Object value) {
    int comp = comparator.compare(value, thresholdValue);
    if (comp == 0) {
      if (operator.equals(SimpleOperator.EQUAL)
          || operator.equals(SimpleOperator.EQ_INF)
          || operator.equals(SimpleOperator.EQ_SUP))
        return true;
      else
        return false;
    } else if (comp > 0) {
      if (operator.equals(SimpleOperator.SUP)
          || operator.equals(SimpleOperator.EQ_SUP))
        return true;
      else
        return false;
    } else {
      if (operator.equals(SimpleOperator.INF)
          || operator.equals(SimpleOperator.EQ_INF))
        return true;
      else
        return false;
    }
  }

  public boolean validateInv(Object value) {
    SimpleOperator op = this.operator.getReverse();
    int comp = comparator.compare(value, thresholdValue);
    if (comp == 0) {
      if (op.equals(SimpleOperator.EQUAL) || op.equals(SimpleOperator.EQ_INF)
          || op.equals(SimpleOperator.EQ_SUP))
        return true;
      else
        return false;
    } else if (comp > 0) {
      if (op.equals(SimpleOperator.SUP) || op.equals(SimpleOperator.EQ_SUP))
        return true;
      else
        return false;
    } else {
      if (op.equals(SimpleOperator.INF) || op.equals(SimpleOperator.EQ_INF))
        return true;
      else
        return false;
    }
  }

  public ThresholdExpression(Object thresholdValue, SimpleOperator operator,
      Comparator<Object> comparator) {
    super();
    this.thresholdValue = thresholdValue;
    this.operator = operator;
    this.comparator = comparator;
  }
}
