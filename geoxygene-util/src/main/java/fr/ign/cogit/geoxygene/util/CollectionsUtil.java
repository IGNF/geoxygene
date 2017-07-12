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
}
