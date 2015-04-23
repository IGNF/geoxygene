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

import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import Jama.Matrix;

/**
 * @author gtouya
 * 
 *         Implémentation Jama de {@link EquationsSystem}. Classe qui contient
 *         des systèmes d'équations linéaires représentées sous forme
 *         matricielle, en utilisant des matrices de la bibliothèque Jama.
 */
public class JamaEquationsSystem extends EquationsSystem {
  private static Logger logger = Logger.getLogger(JamaEquationsSystem.class
      .getName());

  Matrix matriceA;
  Matrix observations;

  /**
   * assemble deux systèmes d'équations en un nouveau. Au niveau des matrices,
   * cela se traduit par un changement de dimension (nouvelles inconnues et
   * nouvelles contraintes), un ajout potentiel d'inconnues dans le vecteur et
   * un agrandissement de la matrice (1,n) des observations.
   * 
   */
  @Override
  public EquationsSystem assemble(EquationsSystem systeme) {
    JamaEquationsSystem assemblage = new JamaEquationsSystem();

    // on assemble le nb de valeurs non nulles
    assemblage.setNonNullValues(this.getNonNullValues()
        + systeme.getNonNullValues());

    // on assemble les matrices d'observation
    assemblage.observations = new Matrix(this.getObsRowNumber()
        + systeme.getObsRowNumber(), 1);
    for (int i = 0; i < assemblage.observations.getRowDimension(); i++) {
      if (i < this.observations.getRowDimension()) {
        assemblage.observations.set(i, 0, this.observations.get(i, 0));
      } else {
        assemblage.observations.set(i, 0,
            systeme.getObs(i - this.getObsRowNumber()));
      }// else
    }// for i

    // on assemble le vecteur des contraintes :
    // on ajoute simplement les nouvelles contraintes après celles déjà
    // présentes
    assemblage.setConstraints(new Vector<LSConstraint>(this.getConstraints()));
    assemblage.getConstraints().addAll(systeme.getConstraints());

    // on détermine maintenant la dimension de la nouvelle matrice : on
    // additionne le nombre de lignes et celui des colonnes si les inconnues
    // du 2ème système ne sont pas dans le premier
    int nbLignes = this.observations.getRowDimension()
        + systeme.getObsRowNumber();
    // on construit un set des inconnues en double
    HashSet<LSPoint> setInconnues = new HashSet<LSPoint>();
    for (int i = 0; i < this.getUnknowns().size(); i = i + 2) {
      LSPoint point = this.getUnknowns().get(i);
      if (systeme.getUnknowns().contains(point)) {
        setInconnues.add(point);
      }
    }// for i

    int nbColonnes = this.getColumnNumber() + systeme.getColumnNumber() - 2
        * setInconnues.size();

    // il faut maintenant remplir le vecteur des inconnues
    assemblage.setUnknowns(new Vector<LSPoint>(this.getUnknowns()));
    for (int i = 0; i < systeme.getUnknowns().size(); i += 2) {
      LSPoint point = systeme.getUnknowns().get(i);
      if (setInconnues.contains(point)) {
        continue;
      }
      assemblage.getUnknowns().addElement(point);
      assemblage.getUnknowns().addElement(point);
    }// for i

    // enfin, on remplit la matrice assemblée
    assemblage.matriceA = new Matrix(nbLignes, nbColonnes);
    for (int i = 0; i < nbLignes; i++) {
      for (int j = 0; j < nbColonnes; j++) {
        // cas de lignes de this
        if (i < this.matriceA.getRowDimension()) {
          // on ne remplit que les colonnes initiales
          if (j < this.matriceA.getColumnDimension()) {
            assemblage.matriceA.set(i, j, this.matriceA.get(i, j));
          }
          // sinon, on laisse la valeur à 0.0
          // cas de lignes de systeme
        } else {
          // dans ce cas on teste si l'inconnue correspondant à la
          // colonne a déjà été traitée
          LSPoint inconnue = assemblage.getUnknowns().get(j);
          // on détermine si c'est la colonne x du point
          boolean estX = false;
          if (!(j + 1 >= assemblage.getUnknowns().size())) {
            if (inconnue.equals(assemblage.getUnknowns().get(j + 1))) {
              estX = true;
            }
          }
          // si j n'est pas une inconnue de systeme, on laisse 0
          if (systeme.getUnknowns().contains(inconnue) == false) {
            continue;
          }
          // on détermine la colonne correspondant dans systeme.matriceA
          int colonne = systeme.getUnknowns().indexOf(inconnue);
          if (estX == false) {
            colonne += 1;
          }

          // dans ce cas, on assigne la valeur de la 2ème matrice
          assemblage.matriceA.set(i, j,
              systeme.getA(i - this.matriceA.getRowDimension(), colonne));
        }// else de if(i<this.matriceA.getRowDimension())

      }// for j, boucle sur les colonnes de la matrice
    }// for i, boucle sur les lignes de la matrice

    return assemblage;
  }// assemble(SystemeEquations systeme)

  @Override
  public void print(String nom) {
    System.out.println("Description du systeme d equations " + nom + " :");
    System.out.println("matrice A");
    this.matriceA.print(2, 2);
    System.out.println("inconnues");
    System.out.println(this.getUnknowns());
    System.out.println("observations");
    this.observations.print(2, 2);
    System.out.println("contraintes");
    System.out.println(this.getConstraints());
    System.out.println("Fin de la description du systeme");
    System.out.println("");
  }

  @Override
  public JamaEquationsSystem copy() {
    JamaEquationsSystem systeme = new JamaEquationsSystem();
    if (this.estVide())
      return systeme;

    systeme.matriceA = this.matriceA.copy();
    systeme.observations = this.observations.copy();
    systeme.setUnknowns(new Vector<LSPoint>(this.getUnknowns()));
    systeme.setConstraints(new Vector<LSConstraint>(this.getConstraints()));

    return systeme;
  }

  public Matrix getMatriceA() {
    return this.matriceA;
  }

  public void setMatriceA(Matrix matriceA) {
    this.matriceA = matriceA;
  }

  public Matrix getObservations() {
    return this.observations;
  }

  public void setObservations(Matrix observations) {
    this.observations = observations;
  }

  public JamaEquationsSystem() {
    super();
  }

  @Override
  public boolean estVide() {
    if (this.matriceA == null || this.getUnknowns() == null
        || this.observations == null) {
      return true;
    }
    return false;
  }

  @Override
  public void clear() {
    this.matriceA = null;
    this.observations = null;
    this.getUnknowns().clear();
    this.getConstraints().clear();
    if (this.getResiduals() != null) {
      this.getResiduals().clear();
    }
    if (this.getSolutions() != null) {
      this.getSolutions().clear();
    }
  }

  @Override
  public void ajustementMoindresCarres(Map<String, Double> poids) {

    // on calcule la matrice des poids
    logger.fine("calcul de la matrice de poids");
    Matrix matricePoids = this.calculerMatricePoids(poids);

    // on commence par calculer la matrice à inverser
    logger.fine("calcul de la matrice");
    Matrix matriceAInverser = this.matriceA.transpose().times(
        matricePoids.times(this.matriceA));
    logger.finer(String.valueOf(matriceAInverser.getRowDimension()));
    logger.finer(String.valueOf(matriceAInverser.getColumnDimension()));

    // on termine l'ajustement
    logger.fine("inversion de la matrice");
    Matrix temp = this.matriceA.transpose().times(matricePoids);
    Matrix temp2 = temp.times(this.observations);
    Matrix solution = matriceAInverser.solve(temp2);
    // matricePoids.print(2,1);
    // systemeGlobal.observations.print(2,1);
    // solution.print(3,1);
    logger.fine("calcul des solutions");
    this.setSolutions(this.matriceToVector(solution));
    logger.fine(this.getSolutions().toString());

    // on calcule maintenant les résidus
    logger.fine("calcul des résidus");
    Matrix matriceResidus = this.matriceA.times(solution).minus(
        this.observations);
    this.setResiduals(this.matriceToVector(matriceResidus));
  }

  @Override
  public double getA(int i, int j) {
    return this.matriceA.get(i, j);
  }

  @Override
  public int getColumnNumber() {
    return this.matriceA.getColumnDimension();
  }

  @Override
  public double getObs(int i) {
    return this.observations.get(i, 0);
  }

  @Override
  public int getRowNumber() {
    return this.matriceA.getRowDimension();
  }

  /**
   * <p>
   * Construit la matrice complète des poids du systèmes en fonction des poids
   * attribués à chaque contrainte dans les mapspecs et du vecteur des
   * contraintes du système qui représente la correspondance entre la ligne de
   * la matrice et la contrainte qu'elle traduit. La matrice est diagonale
   * carrée de dimension le nb de ligne de la matrice Jacobienne A. Le poids est
   * placé dans la diagonale.
   * 
   */
  private Matrix calculerMatricePoids(Map<String, Double> poids) {
    int nb = this.getConstraints().size();
    Matrix matricePoids = new Matrix(nb, nb);
    for (int i = 0; i < nb; i++) {
      LSConstraint contrainte = this.getConstraints().get(i);
      Double poidsContr = poids.get(contrainte.getClass().getName());
      poidsContr *= contrainte.getWeightFactor();
      matricePoids.set(i, i, poidsContr.doubleValue());
    }// for i, boucle sur le vecteur des contraintes

    return matricePoids;
  }// calculerMatricePoids()

  /**
   * <p>
   * Transforme la première colonne d'une matrice en objet Vector.
   * 
   */
  private Vector<Double> matriceToVector(Matrix matrice) {
    Vector<Double> vecteur = new Vector<Double>(matrice.getRowDimension());

    for (int i = 0; i < matrice.getRowDimension(); i++) {
      vecteur.add(new Double(matrice.get(i, 0)));
    }

    return vecteur;
  }// matriceToVector(Matrix matrice)

  @Override
  public void initMatriceA(int rows, int columns) {
    this.matriceA = new Matrix(rows, columns);
  }

  @Override
  public void initObservations(int rows) {
    this.observations = new Matrix(rows, 1);
  }

  @Override
  public void setA(int i, int j, double value) {
    this.matriceA.set(i, j, value);
  }

  @Override
  public void setObs(int i, double value) {
    this.observations.set(i, 0, value);
  }

  @Override
  public int getObsRowNumber() {
    return this.observations.getRowDimension();
  }

}
