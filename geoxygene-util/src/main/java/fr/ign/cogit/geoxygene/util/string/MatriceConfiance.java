package fr.ign.cogit.geoxygene.util.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classe qui contient des Méthodes qui manipulent des matrices.
 */
public class MatriceConfiance {
  public double[][] values;
  public int nbRows;
  public int nbColumns;

  // définition des constructeurs
  public MatriceConfiance(double[][] matrix) {
    this.values = matrix;
    this.nbRows = matrix.length;
    this.nbColumns = matrix[0].length;
  }

  // matrice pour deux populations d'objets géo
  public MatriceConfiance(Collection<?> popRef, Collection<?> popComp) {
    this.nbRows = popRef.size() + 1;
    this.nbColumns = popComp.size() + 1;
    this.values = new double[this.nbRows][this.nbColumns];
  }

  // matrice pour deux strings
  public MatriceConfiance(String ch1, String ch2) {
    this.nbRows = TraitementChainesDeCaracteres.compteToken(ch1);
    this.nbColumns = TraitementChainesDeCaracteres.compteToken(ch2);
    this.values = new double[this.nbRows][this.nbColumns];
  }

  /**
   * Méthode qui calcule la somme des éléments d'une colonne et qui renvoie une
   * liste.
   * @return
   */
  private List<Double> columnSums() {
    List<Double> sumList = new ArrayList<Double>();
    for (int j = 0; j < this.nbColumns; j++) {
      double sum = 0;
      for (int i = 0; i < this.nbRows; i++) {
        sum += this.values[i][j];
      }
      sumList.add(new Double(sum));
    }
    return sumList;
  }

  /**
   * Méthode qui calcule la somme des éléments d'une ligne et qui renvoie une
   * liste.
   * @return
   */
  private List<Double> rowSums() {
    List<Double> sumList = new ArrayList<Double>();
    for (int i = 0; i < this.nbRows; i++) {
      double sum = 0;
      for (int j = 0; j < this.nbColumns; j++) {
        sum += this.values[i][j];
      }
      sumList.add(sum);
    }
    return sumList;
  }

  /**
   * Méthode qui divise une colonne d'une matrice par un double nC est le numéro
   * de la colonne à traiter.
   * @param sum
   * @param nC
   */
  private void divideColumn(double sum, int nC) {
    if (sum == 0) {
      return;
    }
    for (int i = 0; i < this.nbRows; i++) {
      this.values[i][nC] /= sum;
    }
  }

  /**
   * Méthode qui divise une ligne d'une matrice par un double nL est le numéro
   * de la ligne à traiter.
   * @param sum
   * @param nL
   */
  private void divideRow(double sum, int nL) {
    if (sum == 0) {
      return;
    }
    for (int j = 0; j < this.nbColumns; j++) {
      this.values[nL][j] /= sum;
    }
  }

  /**
   * Méthode qui réalise la normalisation d'une matrice; la matrice est
   * normalisée quand toutes les lignes et toutes les colonnes sont normalisées.
   * <p>
   * Une ligne(colonne)est dite normalisée si la somme de tous les éléments de la ligne(colonne) =
   * 1).
   * <p>
   * La normalisation d'une ligne(colonne) consiste à diviser chaque élément d'une ligne par la
   * somme des éléments de la ligne( colonne);
   */
  public void normalize() {
    int k = 0;
    while (true) {
      k++;
      System.out.println("itération" + k);
      List<Double> rowSums = this.rowSums();
      for (int i = 0; i < this.nbRows - 1; i++) {
        this.divideRow(rowSums.get(i).doubleValue(), i);
        // System.out.println("somme ligne : "+((Double)listeSommeLignes.get(i)));
      }
      List<Double> columnSums = this.columnSums();
      for (int j = 0; j < this.nbColumns - 1; j++) {
        this.divideColumn(columnSums.get(j).doubleValue(), j);
        // System.out.println("somme colonne : "+((Double)listeSommeColonnes.get(j)));
      }
      if (this.checkNormalizedMatrix()) {
        System.out.println("matrice normalisée");
        break;
      }
    }
  }

  /**
   * Méthode qui teste si la somme de toutes les lignes(colonnes) d'une matrice
   * vaut 1.
   */
  public boolean checkNormalizedMatrix() {
    double tolerance = 0.3;
    List<Double> rowSums = this.rowSums();
    List<Double> columnSums = this.columnSums();
    // version1 : on accepte une certaine erreur de +-0.1
    for (int i = 0; i < rowSums.size() - 1; i++) {
      double rowSum = rowSums.get(i).doubleValue();
      System.out.println("sommeLIGNE " + rowSum);
      if (!((rowSum >= 1 - tolerance && rowSum <= 1 + tolerance) || rowSum == 0)) {
        return false;
      }
    }
    for (int j = 0; j < columnSums.size() - 1; j++) {
      double columnSum = columnSums.get(j).doubleValue();
      System.out.println("sommeCOLONNE" + columnSum);
      if (!((columnSum >= 1 - tolerance && columnSum <= 1 + tolerance) || columnSum == 0)) {
        return false;
      }
    }
    return true;
    // version 2 : on n'accepte pas l'erreur; la ligne(colonne) est
    // normalisée si et seulement si la somme vaut 1)
    /*
     * for ( int i = 0 ; i < sommeLignes.size()-1 ; i++ ){
     * sommeListeLignes=sommeListeLignes +
     * ((Double)sommeLignes.get(i)).doubleValue(); } for ( int j = 0 ; j <
     * sommeColonnes .size()-1 ; j++ ){ sommeListeColonnes=sommeListeColonnes +
     * ((Double)sommeColonnes.get(j)).doubleValue();
     * System.out.println("val colonne : "+ ((Double)sommeColonnes
     * .get(j)).doubleValue()); } if(sommeListeLignes == sommeLignes .size()-1
     * && sommeListeColonnes == sommeColonnes.size()-1) return true; else return
     * false;
     */
  }

  /**
   * Méthode qui affiche une matrice
   */
  public void printMatrix() {
    String row = "";
    for (int i = 0; i < this.nbRows; i++) {
      for (int j = 0; j < this.nbColumns; j++) {
        row = row.concat(this.values[i][j] + " ");
      }
      System.out.println(row + ";");
      row = "";
    }
  }

  public static void printMatrix(double[][] matrix) {
    String row = "";
    for (double[] element : matrix) {
      for (int j = 0; j < matrix[0].length; j++) {
        row = row.concat(element[j] + " ");
      }
      System.out.println(row + ";");
      row = "";
    }
  }

  /**
   * Méthode qui calcule la somme des éléments d'une colonne et qui renvoie un
   * double.
   * @param matrix
   * @param nc
   *        nombre de colonnes.
   * @return
   */
  public static double columnSum(double[][] matrix, int nc) {
    double sum = 0;
    for (double[] element : matrix) {
      sum += element[nc];
    }
    return sum;
  }

  /**
   * Méthode qui calcule la valeur maximale d'une matrice.
   * @return
   */
  public double confidenceMaxsRows() {
    double confidence = 0;
    for (int i = 0; i < this.nbRows; i++) {
      double max = 0;
      for (int j = 0; j < this.nbColumns; j++) {
        if (this.values[i][j] >= max) {
          max = this.values[i][j];
        }
      }
      confidence += max;
    }
    double meanValue = (double) (this.nbRows + this.nbColumns) / 2;
    confidence /= meanValue;
    return confidence;
  }

  /**
   * Méthode qui calcule la valeur maximale d'une matrice.
   * @return
   */
  public List<Double> maxColumns() {
    List<Double> result = new ArrayList<Double>();
    for (int j = 0; j < this.nbColumns; j++) {
      double max = 0;
      for (int i = 0; i < this.nbRows; i++) {
        if (this.values[i][j] >= max) {
          max = this.values[i][j];
        }
      }
      result.add(max);
    }
    return result;
  }
}
