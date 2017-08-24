package fr.ign.cogit.geoxygene.osm.anonymization.analysis.collections;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Classe permettant d'enregistrer des valeurs sur 
 * des intervalles de nombres.
 * 
 * Pour cela on insert la valeur que l'on souhaite
 * avec son intervalle (début inclusif, fin exclusive).
 * Selectionner sur une valeur qui ne correspond à aucun
 * intervalle donne null.
 * 
 * Insérer sur des intervalles existant remplace la valeur retournée
 * sur ces intervalles. 
 * 
 * Cette classe utilise des intervalles à valeurs discrètes enregistrées
 * en tant que Long.
 * 
 * @author Matthieu Dufait
 * @param <V> type de la valeur
 */
public class DiscreetRangeMap<V> implements Serializable {
  private static final long serialVersionUID = -7809573667753395475L;
  
  private TreeMap<Long, V> tree;
  
  /**
   * Simple contructeur intialisant la table
   */
  public DiscreetRangeMap() {
    tree = new TreeMap<>();
    tree.put(Long.MIN_VALUE, null);
  }
  
  public DiscreetRangeMap(DiscreetRangeMap<V> other) {
    tree = new TreeMap<>(other.tree);
  }
  
  /**
   * Insert une valeur sur un intervalle.
   * 
   * Replace les valeurs si on insert sur 
   * des intervalles existants.
   * 
   * @param start début de l'intervalle inclusif
   * @param end fin de l'intervalle exclusive
   * @param value valeur à placer sur cet intervalle
   */
  public void insert(Long start, Long end, V value) {
    // récupération des entrées remplacées
    NavigableMap<Long, V> changedEntries = tree.subMap(start, true, end, true);
    
    if(!changedEntries.isEmpty()) {
      Long max = changedEntries.floorKey(Long.MAX_VALUE);
      V maxValue = tree.get(max);
      
      Set<Long> keyToRemove = new HashSet<>();

      // enregistrement des clé à supprimer
      for(Long key : changedEntries.keySet()) 
        keyToRemove.add(key);
      
      // suppersion 
      for(Long key : keyToRemove)
        tree.remove(key);
      // ajout de la dernière valeur conservée
      // d'où l'interet d'inclure end comme ça 
      // si une entrée a la clé end, elle est 
      // remplacée directement par elle-même
      tree.put(end, maxValue);
    }    
    
    tree.put(start, value);
    if(tree.get(end) == null)
      tree.put(end, null);
    
  }
  
  /**
   * Retourne la valeur retournée par 
   * l'intervalle contenant la clé 
   * donnée en paramètre
   * Selectionner sur une valeur qui ne correspond à aucun
   * intervalle donne null.
   * @param key valeur se trouvant sur l'intervalle
   * @return valeur retournée par l'intervalle
   */
  public V getValue(Long key) {
    return tree.floorEntry(key).getValue();
  }
  
  /**
   * Modifie la valeur retournée par 
   * l'intervalle contenant la clé 
   * donnée en paramètre.
   * Ne fais rien si aucune valeur n'est
   * défini sur l'intervalle recherché.
   * Retourne un booléen indiquant si
   * l'opération a réussi
   * @param key valeur se trouvant sur l'intervalle
   * @param value nouvelle valeur retournée par l'intervalle
   * @return true si l'opération a réussi et la valeur est modifié
   *         false sinon.
   */
  public boolean setValue(Long key, V value) {
    Entry<Long, V> entry = tree.floorEntry(key);
    if(entry == null)
      throw new IllegalStateException("No entry should be null");
    if(entry.getValue() == null)
      return false;
    Entry<Long, V> entryUp = tree.ceilingEntry(key);
    this.insert(entry.getKey(), entryUp.getKey(), value);
    return true;
  }
  
  /**
   * Retourne l'ensemble des clés 
   * de la table, permet d'itérer sur 
   * toutes les entrées.
   * @return ensemble des clés
   */
  public Set<Long> keySet() {
    return tree.keySet();
  }
  
  public String toString() {
    return tree.toString();
  }
  
  public static void main(String[] args) {
    DiscreetRangeMap<String> list = new DiscreetRangeMap<>();
    
    /*list.insert(0.0, 0.5, "First Half");
    System.out.println("List 1");
    for(Double key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    list.insert(0.5, 1.0, "Second Half");
    System.out.println("List 2");
    for(Double key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    list.insert(0.25, 0.75, "Middle Half");
    System.out.println("List 3");
    for(Double key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    list.insert(0.75, 1.25, "End");
    System.out.println("List 4");
    for(Double key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));*/

    list.insert(5l, 6l, "Second");
    System.out.println("List 2");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    
    list.insert(0l, 1l, "First");
    System.out.println("List 1");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    
    list.insert(1l, 7l, "New");
    System.out.println("List 3");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    
    for(Long d = -10l; d < 10.5; d ++)
      System.out.println(d+" => "+list.getValue(d));
    list.insert(1l, 7l, "New");
    System.out.println("List 4");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    
    list.setValue(5l, "Test");
    System.out.println("List 5");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    list.setValue(30l, "LOL");
    System.out.println("List 6");
    for(Long key : list.keySet())
      System.out.println(key +" => "+list.getValue(key));
    
  }
}
