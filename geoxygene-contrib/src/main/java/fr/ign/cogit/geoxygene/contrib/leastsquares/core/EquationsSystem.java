/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.leastsquares.core;

import java.util.Map;
import java.util.Vector;

/**
 * @author gtouya
 * 
 *         Classe qui contient des systèmes d'équations linéaires représentées
 *         sous forme matricielle
 */
public abstract class EquationsSystem {

  private Vector<LSPoint> unknowns;
  private Vector<LSConstraint> constraints;
  // le vecteur des solutions des moindres carrés
  private Vector<Double> solutions;
  // le vecteur des résidus
  private Vector<Double> residuals;
  // le nombre de valeur non nulle dans A
  private int nonNullValues = 0;

  /**
   * assemble deux systèmes d'équations en un nouveau. Au niveau des matrices,
   * cela se traduit par un changement de dimension (nouvelles inconnues et
   * nouvelles contraintes), un ajout potentiel d'inconnues dans le vecteur et
   * un agrandissement de la matrice (1,n) des observations.
   * 
   */
  public abstract EquationsSystem assemble(EquationsSystem systeme);

  public abstract void print(String nom);

  public abstract EquationsSystem copy();

  public Vector<LSPoint> getUnknowns() {
    return unknowns;
  }

  public void setUnknowns(Vector<LSPoint> inconnues) {
    this.unknowns = inconnues;
  }

  public Vector<LSConstraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(Vector<LSConstraint> contraintes) {
    this.constraints = contraintes;
  }

  public void setSolutions(Vector<Double> solutions) {
    this.solutions = solutions;
  }

  public Vector<Double> getSolutions() {
    return solutions;
  }

  public void setResiduals(Vector<Double> residuals) {
    this.residuals = residuals;
  }

  public Vector<Double> getResiduals() {
    return residuals;
  }

  public EquationsSystem() {
    this.unknowns = new Vector<LSPoint>();
    this.constraints = new Vector<LSConstraint>();
  }

  public abstract void ajustementMoindresCarres(Map<String, Double> poids);

  public abstract boolean estVide();

  public abstract int getRowNumber();

  public abstract int getObsRowNumber();

  public abstract int getColumnNumber();

  /**
   * Récupère le ième élément de la matrice (1,n) des observations
   * @param i
   * @return
   */
  public abstract double getObs(int i);

  /**
   * Récupère l'élément (i,j) de la matrice A du système
   * @param i
   * @param j
   * @return
   */
  public abstract double getA(int i, int j);

  /**
   * Affecte le ième élément de la matrice (1,n) des observations
   * @param i the row number
   * @param value the double value to put in the matrix
   * @return
   */
  public abstract void setObs(int i, double value);

  /**
   * Affecte l'élément (i,j) de la matrice A du système
   * @param i the row number
   * @param j the column number
   * @param value the double value to put in the matrix
   * @return
   */
  public abstract void setA(int i, int j, double value);

  public abstract void initMatriceA(int rows, int columns);

  /**
   * Initialise the (n,1) observation vector (the right part of the equation)
   * with zeros.
   * @param rows
   */
  public abstract void initObservations(int rows);

  public abstract void clear();

  public void setNonNullValues(int nonNullValues) {
    this.nonNullValues = nonNullValues;
  }

  public int getNonNullValues() {
    return nonNullValues;
  }

  public Vector<Double> getObsVector() {
    Vector<Double> vect = new Vector<Double>();
    for (int i = 0; i < getRowNumber(); i++)
      vect.add(getObs(i));
    return vect;
  }

  @Override
  public String toString() {
    if (this.estVide())
      return "Système vide";
    StringBuffer buff = new StringBuffer("Système à " + getColumnNumber() / 2
        + " inconnues et " + getRowNumber() + " équations:\n");
    buff.append("inconnues: " + getUnknowns());
    buff.append("contraintes: " + getConstraints());
    return buff.toString();
  }
}
