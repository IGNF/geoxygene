/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;

/**
 * telepherique, remontee mecanique, funiculaire, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface TronconCable extends ArcReseau {

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
   * @return l'altitude initiale de l'objet
   */
  public double getZIni();

  public void setZIni(double zIni);

  /**
   * @return l'altitude finale de l'objet
   */
  public double getZFin();

  public void setZFin(double zFin);

}
