package fr.ign.cogit.cartagen.util.comparators;

import java.util.Comparator;
import java.util.Map;

public class DoubleMapComparator<T> implements Comparator<T> {

  private Map<T, Double> map;

  public DoubleMapComparator(Map<T, Double> map) {
    super();
    this.map = map;
  }

  @Override
  public int compare(T o1, T o2) {
    return map.get(o1).compareTo(map.get(o2));
  }

}
