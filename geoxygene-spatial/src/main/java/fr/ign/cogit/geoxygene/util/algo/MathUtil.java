/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.algo;

import java.util.List;

/**
 * Fonctions mathématiques utilitaires
 * @author Julien Perret
 * @author Julien Gaffuri: ajout norme Np (24/06/2009)
 */
public class MathUtil {

  /**
   * Moyenne d'une liste
   * @param liste liste de doubles
   * @return moyenne des éléments d'une liste
   */
  public static double moyenne(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    double somme = 0.0;
    for (Double val : liste) {
      somme += val.doubleValue();
    }
    return somme / liste.size();
  }

  /**
   * Moyenne des carrés d'une liste
   * @param liste liste de doubles
   * @return moyenne des carrés des éléments d'une liste
   */
  public static double moyenneCarres(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    double somme = 0.0;
    for (Double val : liste) {
      somme += val.doubleValue() * val.doubleValue();
    }
    return somme / liste.size();
  }

  /**
   * Ecart type d'une liste. Cette fonction ne prends que la liste des éléments
   * comme paramètre. Si vous connaissez déjà la moyenne des éléments de la
   * liste, utilisez l'autre fonction et passez la en paramètre.
   * @param liste liste de doubles
   * @return écart type des éléments d'une liste
   */
  public static double ecartType(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    return MathUtil.ecartType(liste, MathUtil.moyenne(liste));
  }

  /**
   * Ecart type d'une liste. Cette fonction prend en paramètre la moyennes des
   * éléments de la liste. Elle est essentiellement utilitaire mais peux servir
   * si on connait déjà la moyenne des éléments de la liste passée en paramètre.
   * Sinon, passer par l'autre fonction.
   * @param liste liste de doubles
   * @param moyenne moyennes des éléments de la liste
   * @return écart type des éléments d'une liste
   */
  public static <E extends Number> double ecartType(List<E> liste,
      double moyenne) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    double somme = 0.0;
    for (Number d : liste) {
      double e = d.doubleValue() - moyenne;
      somme += e * e;
    }
    return Math.sqrt(somme / liste.size());
  }

  /**
   * Minimum d'une liste
   * @param liste liste de doubles
   * @return plus petite valeur des éléments d'une liste
   */
  public static double min(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    double min = Double.MAX_VALUE;
    for (Double val : liste) {
      min = Math.min(min, val.doubleValue());
    }
    return min;
  }

  /**
   * Maximum d'une liste
   * @param liste liste de doubles
   * @return plus grande valeur des éléments d'une liste
   */
  public static double max(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    double max = -Double.MAX_VALUE;
    for (Double val : liste) {
      max = Math.max(max, val.doubleValue());
    }
    return max;
  }

  /**
   * La valeur médiane d'une liste n'est définie que si la liste contient au
   * moins un élément.
   * @param liste liste de doubles
   * @return Valeur médiane de la liste si elle n'est pas vide. 0 sinon.
   */
  public static double mediane(List<Double> liste) {
    if (liste.isEmpty()) {
      return 0.0;
    }
    Double[] listeTriee = liste.toArray(new Double[0]);
    java.util.Arrays.sort(listeTriee);
    return listeTriee[liste.size() / 2].doubleValue();
  }

  /**
   * Calcul de norme Np
   * @param liste
   * @param p
   * @return
   */
  public static double normeP(List<Double> liste, double p) {
    double somme = 0;
    for (Double d : liste) {
      somme += Math.pow(d.doubleValue(), p);
    }
    return Math.pow(somme, 1 / p);
  }

  /**
   * Calcul de la moyenne p
   * @param liste
   * @param p
   * @return
   */
  public static double moyenneP(List<Double> liste, double p) {
    return MathUtil.normeP(liste, p) / Math.pow(liste.size(), 1 / p);
  }

  /**
   * Calcul de la distance euclidienne entre deux points. La distance
   * Euclidienne n'est calculée que si les 2 listes ont le même nombre de
   * coordonnées.
   * @param x1 Liste de coordonnées
   * @param x2 Liste de coordonnées
   * @return the euclidian distance between x1 and x2
   */
  public static float distEucl(float[] x1, float[] x2) {
    float distEucl = 0;

    for (int i = 0; i < x1.length; i++) {
      distEucl = distEucl + (float) Math.pow(x2[i] - x1[i], 2);
    }
    distEucl = (float) Math.pow(distEucl, 0.5);
    if (x1.length != x2.length) {
      return Float.NaN;
    } else {
      return distEucl;
    }
  }

  /**
   * Calcul de la distance de Manhattan entre deux points. La distance de
   * Manhattan n'est calculée que si les 2 listes ont le même nombre de
   * coordonnées.
   * @param x1 Liste de coordonnées
   * @param x2 Liste de coordonnées
   * @return the manhattan distance between x1 and x2
   */
  public static float distManhattan(List<Float> x1, List<Float> x2) {
    float distManhattan = 0;

    for (int i = 0; i < x1.size(); i++) {
      distManhattan = distManhattan + Math.abs(x2.get(i) - x1.get(i));
    }

    if (x1.size() != x2.size()) {
      return Float.NaN;
    } else {
      return distManhattan;
    }
  }

  /**
   * Interpolation linéaire entre deux points
   * @param x abscisse du point à interpoler
   * @param x1 abscisse du point 1
   * @param x2 abscisse du point 2
   * @param y1 ordonnée du point 1
   * @param y2 ordonnée du point 2
   * @return Résultat d'interpolation linéaire
   */
  public static float lin(int x, int x1, int x2, int y1, int y2) {
    float a = ((float) (y2 - y1)) / ((float) (x2 - x1));
    float b = ((float) (x2 * y1 - x1 * y2)) / ((float) (x2 - x1));
    return (float) a * x + b;
  }

}
