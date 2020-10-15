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

package fr.ign.cogit.geoxygene.contrib.operateurs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe supportant les opérations sur les ensembles
 * @author Mustiere
 * @version 1.0
 */
public class Ensemble {
  final static Logger logger = LogManager.getLogger(Ensemble.class.getName());

  /**
   * Renvoie une liste de liste contenant l'ensemble des combinaisons des
   * éléments de la liste en entrée. Exemple : si la liste contient A, B, C en
   * entrée ça renvoie : [[], [A], [A, B], [A, B, C], [A, C], [B], [B, C], [C]]
   * @param <T>
   */
  public static <T> List<List<T>> combinaisons(final List<T> listeIN) {
    // long bytes = Runtime.getRuntime().freeMemory();
    // logBytes(bytes);
    List<List<T>> combinaisons = new ArrayList<List<T>>(0);
    List<T> currentList = new ArrayList<T>(0);
    combinaisons.add(currentList);
    Ensemble.ajouteSuite(combinaisons, currentList, listeIN);
    // long bytesAfter = Runtime.getRuntime().freeMemory();
    // logBytes(bytesAfter);
    // long bytesDiff = Math.abs(bytes - bytesAfter);
    // logBytes(bytesDiff);
    return combinaisons;
  }

  private static void logBytes(long bytes) {
    long kb = bytes / 1024;
    bytes -= 1024 * kb;
    long mb = kb / 1024;
    kb -= 1024 * mb;
    Ensemble.logger.info("Memory size = " + mb + " M " + kb + " K " + bytes
        + " B");
  }

  private static <T> void ajouteSuite(List<List<T>> combinaisons,
      final List<T> currentList, final List<T> toBeAdded) {
    List<T> copieAjout = new ArrayList<T>(toBeAdded);
    for (T element : toBeAdded) {
      List<T> nouvelleCombinaison = new ArrayList<T>(currentList);
      nouvelleCombinaison.add(element);
      copieAjout.remove(element);
      Ensemble.ajouteSuite(combinaisons, nouvelleCombinaison, copieAjout);
      combinaisons.add(nouvelleCombinaison);
    }
    copieAjout.clear();
  }

  public static <T> List<List<T>> combinations(List<T> list) {
    List<List<T>> result = new ArrayList<List<T>>(0);
    Ensemble.combinations(result, new ArrayList<T>(0), list);
    return result;
  }

  private static <T> void combinations(List<List<T>> result, List<T> prefix,
      List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    List<T> subList = list.subList(1, list.size());
    Ensemble.combinations(result, prefix, subList);
    List<T> newPrefix = new ArrayList<T>(prefix);
    newPrefix.add(list.get(0));
    Ensemble.combinations(result, newPrefix, subList);
    result.add(newPrefix);
    /*
     * String s = ""; for (T e : newPrefix) { s += e + " "; } logger.info(s);
     */
  }

  public static <T> List<List<T>> combinations2(List<T> list) {
    List<List<T>> result = new ArrayList<List<T>>(0);
    Ensemble.combinations2(result, new ArrayList<T>(0), list);
    return result;
  }

  private static <T> void combinations2(List<List<T>> result, List<T> prefix,
      List<T> list) {
    if (!prefix.isEmpty()) {
      result.add(prefix);
      /*
       * String s = ""; for (T e : prefix) { s += e + " "; } logger.info(s);
       */
    }
    for (int i = 0; i < list.size(); i++) {
      List<T> newPrefix = new ArrayList<T>(prefix);
      newPrefix.add(list.get(i));
      List<T> subList = list.subList(i + 1, list.size());
      Ensemble.combinations2(result, newPrefix, subList);
    }
  }

  public static <T> List<List<T>> combinations3(List<T> list) {
    List<List<T>> result = new ArrayList<List<T>>(0);
    int size = list.size();
    for (long i = 1; i < Math.pow(2, size); i++) {
      List<T> comb = new ArrayList<T>(0);
      for (int j = 0; j < size; j++) {
        if ((i & (1L << j)) != 0) {
          comb.add(list.get(j));
        }
      }
      result.add(comb);
      /*
       * String s = ""; for (T e : comb) { s += e + " "; } logger.info(s);
       */
    }
    return result;
  }

  public static void main(String[] args) {
    List<Integer> list = new ArrayList<Integer>(0);
    for (int i = 0; i < 24; i++) {
      list.add(new Integer(i));
    }
    // logger.info(combinations(list).size());
    // logger.info(combinations2(list).size());
    Ensemble.logger.info(Ensemble.combinations3(list).size());
  }

  /**
   * Retourne l'intersection de deux ensembles, un ensemble vide si
   * l'intersection est vide.
   * 
   * @param <T>
   * @param E
   * @param F
   * @return
   */
  public static <T> Set<T> intersection(Set<T> E, Set<T> F) {
    Set<T> result = new HashSet<T>();
    for (T element : E) {
      if (F.contains(element)) {
        result.add(element);
      }
    }
    return result;
  }

}
