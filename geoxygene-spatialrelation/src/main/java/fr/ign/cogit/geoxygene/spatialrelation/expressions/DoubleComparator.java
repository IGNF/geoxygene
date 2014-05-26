package fr.ign.cogit.geoxygene.spatialrelation.expressions;

import java.util.Comparator;

public class DoubleComparator implements Comparator<Object> {

  @Override
  public int compare(Object o1, Object o2) {
    if (o1 instanceof Double && o2 instanceof Double)
      return ((Double) o1).compareTo((Double) o2);
    return 0;
  }

}
