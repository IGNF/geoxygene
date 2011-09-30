package fr.ign.cogit.appli.geopensim.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * Cette classe contient quelques Méthodes qui manipulent des listes.
 * @author Ana-Maria Raimond
 */
public class Mesures {
  /**
   * cette Méthode renvoie la différence entre le premier maximum et le deuxième
   * maximum d'une liste de n éléments.
   * @param liste une liste
   * @return la différence entre le premier maximum et le deuxième max
   */
  @SuppressWarnings("unchecked")
  public static double differenceMaxs(List<Double> liste) {
    double deuxiemeMax = 0;
    double valeurMax = 0;
    double diffMaxs = 0;
    // on calcule la valeur maximale de la liste
    for (Double valeur : liste) {
      if (valeur.doubleValue() > valeurMax) {
        valeurMax = valeur;
      }
    }
    List<Double> copieListe = (List<Double>) (((ArrayList<Double>) liste)
        .clone());
    // on enlève la valeur maximale de la liste
    copieListe.remove(valeurMax);
    // on calcule la valeur maximale de la nouvelle liste de laquelle on a
    // éliminé la valeur maximale
    // ce qui correspond au deuxième maximum
    for (Double valeur : copieListe) {
      if (valeur.doubleValue() > deuxiemeMax) {
        deuxiemeMax = valeur;
      }
    }
    // on calcule la différence entre les deux maximum
    diffMaxs = valeurMax - deuxiemeMax;
    return diffMaxs;
  }

  /**
   * Accepter deux décimales après la virgule.
   * @param valeurIni
   * @return
   */
  public static double arrondi2decimal(double valeurIni) {
    double arrondi;
    BigDecimal valeur = new BigDecimal(valeurIni);
    valeur = valeur.setScale(2, BigDecimal.ROUND_HALF_UP);
    arrondi = valeur.doubleValue();
    return arrondi;
  }

  public static double arrondi4decimal(double valeurIni) {
    double arrondi;
    BigDecimal valeur = new BigDecimal(valeurIni);
    valeur = valeur.round(MathContext.DECIMAL128);
    arrondi = valeur.doubleValue();
    return arrondi;
  }

  /**
   * Calcul de l'écart type.
   * @param list
   * @return
   */
  public static double standardDeviation(List<Double> list) {
    double meanValue = 0;
    for (Double value : list) {
      meanValue += value.doubleValue();
    }
    meanValue /= list.size();
    double stdDeviation = 0;
    for (Double value : list) {
      stdDeviation += Math.pow((value.doubleValue() - meanValue), 2);
    }
    stdDeviation /= list.size();
    return stdDeviation;
  }

  /**
   * Méthode qui renvoie true si une liste est contenue dans l'autre et false
   * dans le cas contraire.
   * 
   * @param l1 liste
   * @param l2 liste
   * @return true si la liste l1 contient tous les éléments de la liste l2
   */
  public static <T> boolean includes(List<T> l1, List<T> l2) {
    if (l1.isEmpty() || l2.isEmpty()) {
      return false;
    }
    return l1.containsAll(l2);
  }

  /**
   * Méthode qui renvoie true si l'intersection de deux listes est différente de
   * l'ensemble vide et false dans le cas contraire. Renvoie true si les 2
   * listes sont null.
   * 
   * @param l1 liste
   * @param l2 liste
   * @return true s'il existe au moins un élément qui appartient aux deux
   *         listes, faux sinon
   */
  @SuppressWarnings("unchecked")
  public static <T> boolean intersects(List<T> l1, List<T> l2) {
    if (l1 == null) {
      return l2 == null;
    }
    if (l2 == null) {
      return false;
    }
    ArrayList<T> copiel1 = (ArrayList<T>) (((ArrayList<T>) l1).clone());
    copiel1.retainAll(l2);
    return !copiel1.isEmpty();
  }

  // Méthode qui renvoi la cardinalité de l'intersection de deux liste
  // param : deux listes l1 et l2
  // return : la cardinalité de l'intersection de deux listes (un double)
  @SuppressWarnings("unchecked")
  public static <T> double cardIntersection(List<T> l1, List<T> l2) {
    double cardinalite = 0;
    if (intersects(l1, l2)) {
      ArrayList<T> copiel1 = (ArrayList<T>) (((ArrayList<T>) l1).clone());
      copiel1.retainAll(l2);
      cardinalite = copiel1.size();
    }
    return cardinalite;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // //////////Méthodes qui finalement n'ont pas été utilisées

  // methode de construction du cadre de discernement utilisant des binaires
  // NB: non-utilisé
  public void constructionReferentielDeDefinitionBinaire(
      IFeatureCollection<? extends IFeature> cadreDiscernement) {// 2
                                                                     // puissance
                                                                     // teta
    int sizeCadre = 0, sizeReferentielDefinition = 0;
    sizeCadre = cadreDiscernement.size() + 1;
    sizeReferentielDefinition = (int) Math.pow(2, sizeCadre);
    for (int i = 0; i < sizeReferentielDefinition; i++) {
      byte[] hypotheseBinaire = new byte[sizeReferentielDefinition];
      hypotheseBinaire[i] = (byte) (i);
      System.out.println(Integer.toBinaryString(hypotheseBinaire[i]));
    }
  }

  @SuppressWarnings("unchecked")
  public static List<ArrayList> intersectionsDesHypotheses(
      List<ArrayList> listeRefDeDef, List<ArrayList> cdl1, List<ArrayList> cdl2) {
    List<ArrayList> resultatIntersection = new ArrayList<ArrayList>();
    ArrayList listeObjetsCompRefDeDef = new ArrayList();
    ArrayList listeObjetsCompCdl1 = new ArrayList();
    ArrayList listeObjetsCompCdl2 = new ArrayList();

    Iterator<ArrayList> itRefDeDef = listeRefDeDef.iterator();
    while (itRefDeDef.hasNext()) {
      listeObjetsCompRefDeDef = itRefDeDef.next();
      Iterator<ArrayList> itCdl1 = cdl1.iterator();
      while (itCdl1.hasNext()) {
        listeObjetsCompCdl1 = itCdl1.next();
        Iterator<ArrayList> itCdl2 = cdl2.iterator();
        while (itCdl2.hasNext()) {
          listeObjetsCompCdl2 = itCdl2.next();
          ArrayList copieListeObjetsCompCdl1 = (ArrayList) listeObjetsCompCdl1
              .clone();
          copieListeObjetsCompCdl1.retainAll(listeObjetsCompCdl2);
          if (equals(listeObjetsCompRefDeDef, copieListeObjetsCompCdl1))
            resultatIntersection.add(listeObjetsCompRefDeDef);
          else
            continue;
        }
      }
    }
    return resultatIntersection;
  }

  public static <T> List<T> differrenceDeuxListes(List<T> l1, List<T> l2) {
    List<T> listDifferent = new ArrayList<T>();
    if (l1 == null) {
      return null;
    }
    if (l2 == null) {
      return l1;
    }
    for (int i = 0; i < l1.size(); i++) {
      for (int j = 0; j < l2.size(); j++) {
        if (l1.get(i) != l2.get(j)) {
          listDifferent.add(l1.get(i));
        } else {
          continue;
        }
      }
    }
    return listDifferent;
  }

  /**
   * @param <T> type of the list elements
   * @param l1 list 1
   * @param l2 list 2
   * @return true if the 2 lists contain the same elements
   */
  public static <T> boolean equals(List<T> l1, List<T> l2) {
    List<T> listEquals = new ArrayList<T>();
    if (l1 == null) {
      return l2 == null;
    }
    if (l2 == null) {
      return false;
    }
    if (l1.isEmpty() && l2.isEmpty()) {
      return true;
    }
    if (l1.size() != l2.size()) {
      return false;
    }
    for (int i = 0; i < l1.size(); i++) {
      for (int j = 0; j < l2.size(); j++) {
        if (l1.get(i) == l2.get(j)) {
          listEquals.add(l1.get(i));
        } else {
          continue;
        }
      }
    }
    return l1.equals(listEquals);
  }

  /**
   * @param value value to round
   * @param nbDigitsAfterDecimalPoint number of digits after the decimal point
   * @return the rounded value
   */
  public static double round(double value, int nbDigitsAfterDecimalPoint) {
    if (nbDigitsAfterDecimalPoint < 0) {
      return value;
    }
    double augmentation = Math.pow(10, nbDigitsAfterDecimalPoint);
    return Math.round(value * augmentation) / augmentation;
  }
}
