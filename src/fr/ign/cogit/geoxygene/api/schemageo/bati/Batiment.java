/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.bati;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Micro;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Batiment extends Micro {

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

  /**
   * @return la nature de l'objet
   */
  public String getNature();

  public void setNature(String nature);

}
