package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Valeur contenue dans la liste de la classe ListeAHull
 * Cela represente les donnees issus d'analyse d'alpha hull
 * 
 * @see ListAHull
 * @author Simon
 *
 */
public class ValueTableAHull {
  
  private int alpha;
  private int nPtsHors;
  private IGeometry geometry;
  private double surfConvexHull;
  private int nbPointsTotal;
  private boolean valid;
  
  /**
   * construit l'objet en specifiant la valeur d'alpha
   * @param alpha
   */
  public ValueTableAHull(int alpha) {
    this.alpha = alpha;
  }

  /**
   * @return la valeur alpha
   */
  public int getAlpha() {
    return alpha;
  }

  /**
   * @return la valeur superficie
   */
  public double getSuperficie() {
    if(geometry==null)
      return -1;
    return geometry.area();
  }

  /**
   * @return le nombre de points hors de l'alpha hull
   */
  public int getnPtsHors() {
    return nPtsHors;
  }
  
  /**
   * Modifie la valeur du nombre de points en dehors de l'alpha hull
   * @param nPtsHors - le nombre de points hors de l'alpha hull
   */
  public void setnPtsHors(int nPtsHors) {
    this.nPtsHors = nPtsHors;
  }

  /**
   * @return le rapport entre la surface de l'alpha hull et celle de l'enveloppe convexe
   */
  public double getRapMCR() {
    return geometry.area()/surfConvexHull;
  }
  
  /**
   * Modifie la valeur de surface de l'enveloppe convexe
   * @param la nouvelle valeur de la surface
   */
  public void setSurfConvexHull(double surfConvexHull) {
    this.surfConvexHull = surfConvexHull;
  }

  /**
   * @return la geometrie de l'enveloppe
   */
  public IGeometry getGeometry() {
    return geometry;
  }

  /**
   * Remplace la geometrie de l'alpha hull
   * @param geometry - instance de la nouvelle geometrie
   */
  public void setGeometry(IGeometry geometry) {
    this.geometry = geometry;
  }

  /**
   * Modifie le nombre de points total de la population
   * @param nbPointsTotal - la nouvelle valeur
   */
  public void setNbPointsTotal(int nbPointsTotal) {
    this.nbPointsTotal = nbPointsTotal;
  }
  
  /**
   * @return le rapport entre le nombre de points hors de la surface et le nombre de points total
   */
  public double getRapNbPtHors(){
    return ((double)this.nPtsHors)/this.nbPointsTotal;
  }

  
  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }
  
}
