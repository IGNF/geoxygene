/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementPonctuelReseau;

/**
 * point d'eau, reservoir (?), point d'eau isole, toponyme de surface hydro (?)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface PointDEau extends ElementPonctuelReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return l'altitude maximale de l'objet
   */
  public double getZMax();

  public void setZMax(double zMax);

  /**
   * @return l'altitude minimale de l'objet
   */
  public double getZMin();

  public void setZMin(double zMin);

  /**
   * @return la hauteur de l'objet
   */
  public double getHauteur();

  public void setHauteur(double hauteur);

}
