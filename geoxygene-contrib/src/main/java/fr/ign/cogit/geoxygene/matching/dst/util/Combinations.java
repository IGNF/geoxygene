/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Bertrand Dumenieu
 * 
 */
public final class Combinations {

  /**
   * Calcul de l'ensemble des combinaisons possibles au sein d'une liste
   * d'éléments s. Il s'agit de combinaisons sans répétitions, et l'ordre a une
   * importance.
   * @param s
   * @return
   */
  public final static <E> LinkedList<List<E>> enumerate(List<E> s) {
    LinkedList<List<E>> combinations = new LinkedList<List<E>>();
    for (int i = 1; i <= s.size(); i++) {
      combinations.addAll(Combinations.enumerate(i, s));
    }
    return combinations;
  }

  /**
   * Calcul des combinaisons de k parmi n dans une liste d'objets s. Il s'agit
   * de combinaisons sans répétitions, et l'ordre a une importance.
   */
  public final static <E> LinkedList<List<E>> enumerate(int k, List<E> s) {
    LinkedList<Integer[]> indexes = Combinations.enumerate(s.size(), k);
    LinkedList<List<E>> combinations = new LinkedList<List<E>>();
    Iterator<Integer[]> it = indexes.iterator();
    while (it.hasNext()) {
      Integer[] localidx = it.next();
      List<E> combinaison = new ArrayList<E>(localidx.length);
      for (int i = 0; i < localidx.length; i++) {
        combinaison.add(i, s.get(localidx[i] - 1));
      }
      combinations.add(combinaison);
    }
    return combinations;
  }

  public final static LinkedList<Integer[]> enumerate(int n) {
    LinkedList<Integer[]> l = new LinkedList<Integer[]>();
    for (int i = 1; i <= n; i++) {
      l.addAll(Combinations.enumerate(n, i));
    }
    return l;
  }

  /**
   * Calcul des éléments k parmi n.
   * @param n
   * @param k
   * @return
   */
  public final static LinkedList<Integer[]> enumerate(int n, int k) {
    int[] comb = new int[n * n];
    LinkedList<Integer[]> l = new LinkedList<Integer[]>();

    for (int i = 0; i < k; ++i)
      comb[i] = i;
    build(comb, k);
    l.add(build(comb, k));

    while (next_comb(comb, k, n)) {
      build(comb, k);
      l.add(build(comb, k));
    }
    return l;
  }

  private final static boolean next_comb(int comb[], int k, int n) {
    int i = k - 1;
    ++comb[i];
    while ((i > 0) && (comb[i] >= n - k + 1 + i)) {
      --i;
      ++comb[i];
    }

    if (comb[0] > n - k)
      return false;

    for (i = i + 1; i < k; ++i)
      comb[i] = comb[i - 1] + 1;

    return true;
  }

  private final static Integer[] build(int[] comb, int k) {
    Integer[] built = new Integer[k];
    for (int i = 0; i < k; ++i) {
      built[i] = comb[i] + 1;
    }
    return built;
  }

  public final static BigInteger combination(int p, int n) {
    BigInteger totot = fact(n).divide((fact(p).multiply(fact(n - p))));
    return totot;
  }

  public final static BigInteger combination(int n) {
    BigInteger res = BigInteger.valueOf(0l);
    for (int p = 1; p <= n; p++) {
      res = res.add(combination(p, n));
    }
    return res;
  }

  private final static BigInteger fact(int n) {
    BigInteger ret = BigInteger.ONE;
    for (int i = 1; i <= n; i++) {
      ret = ret.multiply(BigInteger.valueOf(i));
    }
    return ret;
  }

  private final static void Permutation(int k, int[] r) {
    int fact = 1;
    for (int i = 2; i < r.length + 1; i++) {
      fact = fact * (i - 1);
      int pos = i - ((k / fact) % i) - 1;
      swap(pos, i - 1, r);
    }
  }

  private final static void swap(int i, int j, int[] r) {
    int oi = r[i];
    int oj = r[j];
    r[j] = oi;
    r[i] = oj;
  }

  public final static LinkedList<int[]> permutation(int[] l) {
    LinkedList<int[]> result = new LinkedList<int[]>();
    int resFact = Combinations.fact(l.length).intValue();
    for (int i = 0; i < resFact; i++) {
      int[] r = l.clone();
      Permutation(i, r);
      result.add(r);
    }
    return result;
  }

  public final static <E> LinkedList<List<E>> permutation(List<E> c) {
    LinkedList<List<E>> combinations = new LinkedList<List<E>>();
    int[] indexes = new int[c.size()];
    for (int i = 0; i < c.size(); i++) {
      indexes[i] = i;
    }
    LinkedList<int[]> perms = permutation(indexes);
    Iterator<int[]> it = perms.iterator();
    while (it.hasNext()) {
      int[] comb = it.next();
      List<E> result = new ArrayList<E>();
      for (int i = 0; i < comb.length; i++) {
        result.add(c.get(comb[i]));
      }
      combinations.add(result);
    }
    return combinations;

  }
}
