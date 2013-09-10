package fr.ign.cogit.cartagen.pearep.gui;

import java.util.Comparator;

import fr.ign.cogit.cartagen.util.Interval;

public class IntervalComparator implements Comparator<Interval<Integer>> {

  @Override
  public int compare(Interval<Integer> o1, Interval<Integer> o2) {
    return o1.getMinimum() - o2.getMinimum();
  }

}
