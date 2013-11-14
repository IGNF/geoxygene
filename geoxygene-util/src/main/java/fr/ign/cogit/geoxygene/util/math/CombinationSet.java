/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to handle mathematical combinations of sets.
 * @author GTouya
 * 
 */
public class CombinationSet {

  private Set<? extends Object> set;
  private long totalNb;

  private List<Object> internalList;

  public CombinationSet(Set<? extends Object> set) {
    this.set = set;
    this.internalList = new ArrayList<Object>(set);
    this.totalNb = 0;
    for (int i = 1; i <= set.size(); i++)
      totalNb += nbOfCombinations(i);
  }

  /**
   * Compute the nCk value for {@code this} combination object.
   * @param nbElements
   * @return
   */
  public int nbOfCombinations(int k) {
    if (k > set.size())
      return 0;
    if (k == set.size())
      return 1;
    long numerator = factorial(set.size());
    long den1 = factorial(k);
    long den2 = factorial(set.size() - k);
    return (int) (numerator / (den1 * den2));
  }

  /**
   * Sum the nCp value for {@code this} combination object, for each p value
   * above or equal to k.
   * @param nbElements
   * @return
   */
  public int nbOfCombinationsAbove(int k) {
    int nb = 0;
    for (int i = set.size(); i >= k; i--)
      nb += nbOfCombinations(i);
    return nb;
  }

  /**
   * Get all the combinations that have exactly n elements.
   * @param n
   * @return
   */
  public List<Combination> getAllCombinationsOfNElements(int n) {
    List<Combination> combinations = new ArrayList<Combination>();
    int nb = nbOfCombinations(n);
    for (Combination comb : getAllCombinations()) {
      if (combinations.size() == nb)
        break;
      if (comb.getSize() == n)
        combinations.add(comb);
    }
    return combinations;
  }

  /**
   * Get all the combinations that have n or more elements.
   * @param n
   * @return
   */
  public List<Combination> getAllCombinationsOfNOrMoreElements(int n) {
    List<Combination> combinations = new ArrayList<Combination>();
    int nb = nbOfCombinationsAbove(n);
    for (Combination comb : getAllCombinations()) {
      if (combinations.size() == nb)
        break;
      if (comb.getSize() >= n)
        combinations.add(comb);
    }
    return combinations;
  }

  /**
   * Get all the possible combinations for {@code this} combination set.
   * @return
   */
  public List<Combination> getAllCombinations() {
    List<Combination> combinations = new ArrayList<Combination>();
    for (long i = 1; i <= totalNb; i++) {
      Set<Object> subSet = new HashSet<Object>();
      BigInteger bigInt = BigInteger.valueOf(i);
      for (int j = 0; j < bigInt.bitLength(); j++) {
        if (bigInt.testBit(j))
          subSet.add(internalList.get(j));
      }
      Combination comb = new Combination(subSet, this);
      combinations.add(comb);
    }
    return combinations;
  }

  private long factorial(int n) {
    long factorial = 1;
    for (int i = n; i > 0; i--)
      factorial *= i;
    return factorial;
  }

  /**
   * Get the total number of possible combinations. If n is the set size, the
   * total number is nC1 + nC2 + ... + nC(n-1) + nCn. It is computed by the
   * {@code this} constructor.
   * @return
   */
  public long getTotalNb() {
    return totalNb;
  }

}
