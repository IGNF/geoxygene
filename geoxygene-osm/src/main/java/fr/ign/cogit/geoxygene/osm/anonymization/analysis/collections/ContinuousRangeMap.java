package fr.ign.cogit.geoxygene.osm.anonymization.analysis.collections;

import java.io.Serializable;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Classe permettant d'enregistrer des valeurs sur 
 * des intervalles de nombres.
 * 
 * Pour cela on insert la valeur que l'on souhaite
 * avec son intervalle (début inclusif, fin exclusive).
 * Selectionner sur une valeur qui ne correspond à aucun
 * intervalle donne null.
 * 
 * Insérer sur des intervalles existant remplace la valeur 
 * retournée sur ces intervalles. 
 * 
 * Cette classe utilise des intervalles à valeurs continue 
 * enregistrées en tant que Double.
 * 
 * @author Matthieu Dufait
 * @param <V> type de la valeur
 */
public class ContinuousRangeMap<V> implements Serializable {
  private static final long serialVersionUID = -8885885335897478074L;
  
  private TreeMap<Double, V> tree;
  
  /**
   * Simple contructeur initialisant la table
   */
  public ContinuousRangeMap() {
    tree = new TreeMap<>();
    tree.put(Double.NEGATIVE_INFINITY, null);
  }
  
  public ContinuousRangeMap(ContinuousRangeMap<V> other) {
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
  public void insert(Double start, Double end, V value) {
    // récupération des entrées remplacées
    NavigableMap<Double, V> changedEntries = tree.subMap(start, true, end, true);
    
    if(!changedEntries.isEmpty()) {
      Double max = changedEntries.floorKey(Double.POSITIVE_INFINITY);
      V maxValue = tree.get(max);
      
      Set<Double> keyToRemove = new HashSet<>();

      // enregistrement des clé à supprimer
      for(Double key : changedEntries.keySet()) 
        keyToRemove.add(key);
      
      // suppersion 
      for(Double key : keyToRemove)
        tree.remove(key);
      // ajout de la dernière valeur conservée
      // d'où l'interet d'unclure end comme ça 
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
   * donnée en paramètre.
   * Selectionner sur une valeur qui ne correspond à aucun
   * intervalle donne null.
   * @param key valeur se trouvant sur l'intervalle
   * @return valeur retournée par l'intervalle
   */
  public V getValue(Double key) {
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
  public boolean setValue(Double key, V value) {
    Entry<Double, V> entry = tree.floorEntry(key);
    if(entry == null)
      throw new IllegalStateException("No entry should be null");
    if(entry.getValue() == null)
      return false;
    Entry<Double, V> entryUp = tree.ceilingEntry(key);
    this.insert(entry.getKey(), entryUp.getKey(), value);
    return true;    
  }
  
  /**
   * Retourne l'ensemble des clés 
   * de la table, permet d'itérer sur 
   * toutes les entrées.
   * @return ensemble des clés
   */
  public Set<Double> keySet() {
    return tree.keySet();
  }
  
  @Override
  public String toString() {
    return tree.toString();
  }
}
