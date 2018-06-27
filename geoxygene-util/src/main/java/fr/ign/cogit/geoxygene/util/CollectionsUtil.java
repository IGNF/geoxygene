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
}
