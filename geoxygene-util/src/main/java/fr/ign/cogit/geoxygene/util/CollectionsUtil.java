/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utilities methods to manipulate collections.
 * @author GTouya
 *
 */
public class CollectionsUtil {

  /**
   * Sort in ascending order a given map using the values to compare the keys.
   * @param map
   * @return
   */
  public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
      Map<K, V> map) {
    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
        map.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
      public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });

    LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
    for (Map.Entry<K, V> entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  /**
   * Sort in descending order a given map using the values to compare the keys.
   * @param map
   * @return
   */
  public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValueDescending(
      Map<K, V> map) {
    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
        map.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
      public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });
    Collections.reverse(list);
    LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
    for (Map.Entry<K, V> entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  /**
   * Compute the Hamming distance between two lists. If lists are not the same
   * size, returns -1.
   * @param list1
   * @param list2
   * @return
   */
  public static int getHammingDistance2Lists(List<? extends Object> list1,
      List<? extends Object> list2) {
    if (list1.size() != list2.size())
      return -1;
    int result = 0;
    for (int i = 0; i < list1.size(); i++) {
      if (!list1.get(i).equals(list2.get(i))) {
        result++;
      }
    }
    return result;
  }

  /**
   * Compute the Hamming distance between two lists. If lists are not the same
   * size, the method removes the last elements of the largest list.
   * @param list1
   * @param list2
   * @return
   */
  public static int getHammingDistance2DiffLists(List<? extends Object> list1,
      List<? extends Object> list2) {
    // first, cases with empty list(s)
    if (list1.isEmpty() && list2.isEmpty())
      return -1;
    else if (list1.isEmpty())
      return list2.size();
    else if (list2.isEmpty())
      return list1.size();

    // then, cases with lists with different sizes
    if (list1.size() > list2.size()) {
      int diff = list1.size() - list2.size();
      for (int i = 0; i < diff; i++) {
        list1.remove(list1.size() - 1);
      }
    } else if (list1.size() < list2.size()) {
      int diff = list2.size() - list1.size();
      for (int i = 0; i < diff; i++) {
        list2.remove(list2.size() - 1);
      }
    }

    // finally the regular case with lists of same size
    int result = 0;
    for (int i = 0; i < list1.size(); i++) {
      if (!list1.get(i).equals(list2.get(i))) {
        result++;
      }
    }
    return result;
  }

  /**
   * Compute the edit distance between two lists, using the Fischer-Wagner
   * algorithm.
   * @param list1
   * @param list2
   * @return
   */
  public static int getEditDistance2Lists(List<? extends Object> list1,
      List<? extends Object> list2) {
    // For all i and j, d[i][j] will hold the Levenshtein (edit) distance
    // between
    // the first i characters of s and the first j characters of t.
    // Note that d has (m+1) x (n+1) values.
    int[][] d = new int[list1.size() + 1][list2.size() + 1];

    for (int i = 0; i < list1.size() + 1; i++) {
      // the distance of any first string to an empty second string
      // (transforming the string of the first i characters of s into
      // the empty string requires i deletions)
      d[i][0] = i;
    }
    for (int i = 0; i < list2.size() + 1; i++) {
      // the distance of any second string to an empty first string
      d[0][i] = i;
    }
    for (int j = 1; j < list2.size() + 1; j++) {
      for (int i = 1; i < list1.size() + 1; i++) {
        if (list1.get(i - 1).equals(list2.get(j - 1))) {
          // no operation required
          d[i][j] = d[i - 1][j - 1];
        } else {
          // d[i][j] is the minimum between a deletion, an insertion, or a
          // substitution
          d[i][j] = Math.min(d[i - 1][j] + 1,
              Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + 1));
        }
      }
    }
    return d[list1.size()][list2.size()];
  }

  /**
   * Compute the "Jaro-Winkler" edit distance between two lists, that only
   * allows transpositions, rather than substitutions (like in Levenstein edit
   * distance).
   * @param list1
   * @param list2
   * @param scalingFactor used for how much the score is adjusted upwards for
   *          having common prefixes. default value is 0.1.
   * @return
   */
  public static double getJaroWinklerDistance2Lists(
      List<? extends Object> list1, List<? extends Object> list2,
      double scalingFactor) {
    if (list1 == null || list2 == null) {
      throw new IllegalArgumentException("Lists must not be null");
    }

    final int[] mtp = matches(list1, list2);
    final double m = mtp[0];
    if (m == 0) {
      return 1D;
    }
    final double j = ((m / list1.size() + m / list2.size() + (m - mtp[1]) / m))
        / 3;
    final double jw = j < 0.7D ? j
        : j + Math.min(scalingFactor, 1D / mtp[3]) * mtp[2] * (1D - j);
    return 1 - jw;
  }

  private static int[] matches(List<? extends Object> list1,
      List<? extends Object> list2) {
    List<? extends Object> max, min;
    if (list1.size() > list2.size()) {
      max = list1;
      min = list2;
    } else {
      max = list2;
      min = list1;
    }
    final int range = Math.max(max.size() / 2 - 1, 0);
    final int[] matchIndexes = new int[min.size()];
    Arrays.fill(matchIndexes, -1);
    final boolean[] matchFlags = new boolean[max.size()];
    int matches = 0;
    for (int mi = 0; mi < min.size(); mi++) {
      final Object c1 = min.get(mi);
      for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1,
          max.size()); xi < xn; xi++) {
        if (!matchFlags[xi] && c1 == max.get(xi)) {
          matchIndexes[mi] = xi;
          matchFlags[xi] = true;
          matches++;
          break;
        }
      }
    }
    final Object[] ms1 = new Object[matches];
    final Object[] ms2 = new Object[matches];
    for (int i = 0, si = 0; i < min.size(); i++) {
      if (matchIndexes[i] != -1) {
        ms1[si] = min.get(i);
        si++;
      }
    }
    for (int i = 0, si = 0; i < max.size(); i++) {
      if (matchFlags[i]) {
        ms2[si] = max.get(i);
        si++;
      }
    }
    int transpositions = 0;
    for (int mi = 0; mi < ms1.length; mi++) {
      if (ms1[mi] != ms2[mi]) {
        transpositions++;
      }
    }
    int prefix = 0;
    for (int mi = 0; mi < min.size(); mi++) {
      if (list1.get(mi).equals(list2.get(mi))) {
        prefix++;
      } else {
        break;
      }
    }
    return new int[] { matches, transpositions / 2, prefix, max.size() };
  }
}
