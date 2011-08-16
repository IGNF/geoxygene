/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.ferre.TronconCable;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ArcReseauImpl;

/**
 * telepherique, remontee mecanique, funiculaire, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class TronconCableImpl extends ArcReseauImpl implements TronconCable {

  /**
   * l'altitude maximale de l'objet
   */
  private double zMax;

  public double getZMax() {
    return this.zMax;
  }

  public void setZMax(double zMax) {
    this.zMax = zMax;
  }

  /**
   * l'altitude minimale de l'objet
   */
  private double zMin;

  public double getZMin() {
    return this.zMin;
  }

  public void setZMin(double zMin) {
    this.zMin = zMin;
  }

  private double zIni = 0;

  public double getZIni() {
    return this.zIni;
  }

  public void setZIni(double zIni) {
    this.zIni = zIni;
  }

  private double zFin = 0;

  public double getZFin() {
    return this.zFin;
  }

  public void setZFin(double zFin) {
    this.zFin = zFin;
  }

}
