package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * La classe Grid represente une grille a pas fixe et qui permet de determiner combien de points appartiennent a chaques mailles.
 * L'emprise est definie par le coin superieur droit et le coin inferieur gauche (upperCorner et lowerCorner).
 * La taille des mailles peut être definie en la specifiant directement (stepX, stepY) ou en specifiant un nombre de lignes et de colonnes.
 * Les mailles ne sont crées que si elles contiennent au moins un point.
 * 
 * @author Simon
 *
 */
public class Grid extends Vector<Maille> {
  
  private static final long serialVersionUID = 1L;
  
  private IDirectPosition upperCorner;
  private IDirectPosition lowerCorner;
  
  private int numRows;
  private int numCols;
  
  private double stepX;
  private double stepY;
  
  public static int nValid = 0;
  public static int nError = 0;
  
  /**
   * Crée la grille en specifiant le nombre de ligne et de colonne.
   * @param upperCorner - le coin superieur droit
   * @param lowerCorner - le coin superieur gauche
   * @param numRows - le nombre de ligne
   * @param numCols - le nomnbre de colonne
   */
  public Grid(IDirectPosition upperCorner, IDirectPosition lowerCorner,
      int numRows, int numCols) {
    super();
    this.upperCorner = upperCorner;
    this.lowerCorner = lowerCorner;
    this.numRows = numRows;
    this.numCols = numCols;
    this.stepX = (upperCorner.getX()-lowerCorner.getX())/numCols;
    this.stepY = (upperCorner.getY()-lowerCorner.getY())/numRows;
  }

  /**
   * Crée la grille en specifiant l'emprise des mailles.
   * @param upperCorner - le coin superieur droit
   * @param lowerCorner - le coin superieur gauche
   * @param stepX - emprise en easting de la maille
   * @param stepY - emprise en northing de la maille
   */
  public Grid(IDirectPosition upperCorner, IDirectPosition lowerCorner,
      double stepX, double stepY) {
    super();
    this.upperCorner = upperCorner;
    this.lowerCorner = lowerCorner;
    this.stepX = stepX;
    this.stepY = stepY;
    double eX = this.upperCorner.getX()-this.lowerCorner.getX();
    double eY = this.upperCorner.getY()-this.lowerCorner.getY();
    this.numRows = (int)eX/(int)stepX +1;
    this.numCols = (int)eY/(int)stepY +1;
  }
  
  /**
   * Ajoute le point dans la maille de la grille, et crée cette maille si elle n'existe pas.
   * @param p - le point à ajouter
   */
  public void addPoint(IDirectPosition p){
    if(p.getX()<lowerCorner.getX() || upperCorner.getX()<p.getX() ||
        p.getY()<lowerCorner.getY() || upperCorner.getY()<p.getY()){
      System.err.println("out of bound");
      nError++;
      return;
    }
    
    int col = (int)((p.getX()-lowerCorner.getX())/stepX);
    int row = (int)((p.getY()-lowerCorner.getY())/stepY);
    
    getMaille(row, col).add(p);
    nValid++;
  }
  
  /**
   * Retourne la maille corespondant à la ligne et la colonne spécifié en parametre.
   * @param row - le numero de la ligne
   * @param col - le numero de la colonne
   * @return la maille[row, col]
   */
  public Maille getMaille(int row, int col){
    if(row>numRows || col>numCols || row<0 || col<0)
      return null;
    for (Maille m : this) {
      if(m.getCol()==col && m.getRow()==row)
        return m;
    }
    Maille m = new Maille(row, col);
    add(m);
    return m;
  }
  
  /**
   * Retourne l'emprise en easting de la maille.
   * @return stepX
   */
  public double getStepX() {
    return stepX;
  }

  /**
   * Retourne l'emprise en northing de la maille.
   * @return stepY
   */
  public double getStepY() {
    return stepY;
  }

  /**
   * Retourne le point superieur droit de la grille.
   * @return le upper corner de la grille
   */
  public IDirectPosition getUpperCorner() {
    return upperCorner;
  }

  /**
   * Retourne le point inferieur gauche de la grille.
   * @return le lower corner de la grille
   */
  public IDirectPosition getLowerCorner() {
    return lowerCorner;
  }

  /**
   * Met à jour l'attribut nombre de lignes de la grille.
   * Reaffecte tous les points de la grille
   * @param numRows - la nouvelle valeur de l'attribut numRows
   */
  public void setNumRows(int numRows) {
    if(numRows>0){
      this.numRows = numRows;
      this.stepY = (upperCorner.getY()-lowerCorner.getY())/numRows;
      calcGrid();
    }
  }

  /**
   * Met à jour l'attribut nombre de colonnes de la grille.
   * Reaffecte tous les points de la grille
   * @param numCols - la nouvelle valeur de l'attribut numCols
   */
  public void setNumCols(int numCols) {
    if(numCols>0){
      this.numCols = numCols;
      this.stepX = (upperCorner.getX()-lowerCorner.getX())/numCols;
      calcGrid();
      }
  }

  /**
   * Met à jour l'attribut emprise en easting de la grille.
   * Reaffect tous les points de la grille
   * @param stepX - la nouvelle valeur de l'attribut stepX
   */
  public void setStepX(double stepX) {
    if(stepX>0){
      this.stepX = stepX;
      double eX = this.upperCorner.getX()-this.lowerCorner.getX();
      this.numCols = (int)eX/(int)stepX +1;
      calcGrid();
    }
  }

  /**
   * Met à jour l'attribut emprise en northing de la grille.
   * Reaffect tous les points de la grille
   * @param stepY - la nouvelle valeur de l'attribut stepY
   */
  public void setStepY(double stepY) {
    if(stepY>0){
      this.stepY = stepY;
      double eY = this.upperCorner.getY()-this.lowerCorner.getY();
      this.numRows = (int)eY/(int)stepY +1;
      calcGrid();
    }
  }
  
  /**
   * Retourne le nombre de ligne de la grille.
   * @return le nombre de ligne de la grille
   */
  public int getNumRows() {
    return numRows;
  }

  /**
   * Retourne le nombre de colonne de la grille.
   * @return le nombre de colonne de la grille
   */
  public int getNumCols() {
    return numCols;
  }

  /**
   * Recalcul de la grille
   */
  private void calcGrid() {
    Vector<IDirectPosition> v = new Vector<IDirectPosition>();
    for (Maille m : this) {
      for (IDirectPosition p : m) {
        v.add(p);
      }
    }
    this.removeAllElements();
    for (IDirectPosition p : v) {
      this.addPoint(p);
    }
  }
  
  /**
   * Retourne le nombre maximum de points contenu dans une maille
   * @return le nombre maximum de points contenu dans une maille
   */
  private int getMax(){
    int max = get(0).size();
    for ( Maille v : this) {
      max = Math.max(max, v.size());
    }
    return max;
  }
  
  /**
   * Retourne le nombre minimum de points contenu dans une maille
   * @return le nombre minimum de points contenu dans une maille
   */
  private int getMin(){
    int min = get(0).size();
    for ( Maille v : this) {
      min = Math.min(min, v.size());
    }
    return min;
  }
  
  /**
   * Retourne le numero de la classe de la cardinalité selon une classification en amplitude égale à n classes
   * @param val - la valeur à classer parmi les effectifs de la grille
   * @param n - le nombre de classe
   * @return le numéro de la classe
   */
  public int getClasse(int val, int n){
    double min = getMin();
    double max = getMax();
    int r = (int) Math.floor((val-min)/(max-min)*(n-1));
    return r;
  }
  
}
