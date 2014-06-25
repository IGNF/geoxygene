package fr.ign.cogit.cartagen.util.comparators;

import java.util.Comparator;
import java.util.Map;

public class IntegerMapComparator<T> implements Comparator<T> {

  private Map<T, Integer> map;

  public IntegerMapComparator(Map<T, Integer> map) {
    super();
    this.map = map;
  }

  @Override
  public int compare(T o1, T o2) {
    return map.get(o1).compareTo(map.get(o2));
  }

}
