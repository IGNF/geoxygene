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
import java.util.List;

/**
 * Classe supportant les opérations sur les ensembles
 * @author Mustiere
 * @version 1.0
 */
public class Ensemble {

  /**
   * Renvoie une liste de liste contenant l'ensemble des combinaisons des
   * éléments de la liste en entrée. Exemple : si la liste contient A, B, C en
   * entrée ça renvoie : [[], [A], [A, B], [A, B, C], [A, C], [B], [B, C], [C]]
   * @param <T>
   */
  public static <T> List<List<T>> combinaisons(final List<T> listeIN) {
    List<List<T>> combinaisons = new ArrayList<List<T>>();
    List<T> currentList = new ArrayList<T>();
    combinaisons.add(currentList);
    Ensemble.ajouteSuite(combinaisons, currentList, listeIN);
    return combinaisons;
  }

  private static <T> void ajouteSuite(List<List<T>> combinaisons,
      final List<T> currentList, final List<T> toBeAddedList) {
    List<T> copieAjout = new ArrayList<T>(toBeAddedList);
    for (T ajout : toBeAddedList) {
      List<T> nouvelleCombinaison = new ArrayList<T>(currentList);
      nouvelleCombinaison.add(ajout);
      combinaisons.add(nouvelleCombinaison);
      copieAjout.remove(ajout);
      Ensemble.ajouteSuite(combinaisons, nouvelleCombinaison, copieAjout);
    }
    copieAjout.clear();
  }
}
