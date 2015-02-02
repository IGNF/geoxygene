package fr.ign.cogit.geoxygene.sig3d.calculation;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 **/
public class Orientation {

  private double alpha, beta;

  public Orientation(double alpha, double beta) {

    this.alpha = alpha;
    this.beta = beta;
  }

  /**
   * @return the alpha
   */
  public double getAlpha() {
    return this.alpha;
  }

  /**
   * @param alpha the alpha to set
   */
  public void setAlpha(double alpha) {
    this.alpha = alpha;
  }

  /**
   * @return the beta
   */
  public double getBeta() {
    return this.beta;
  }

  /**
   * @param beta the beta to set
   */
  public void setBeta(double beta) {
    this.beta = beta;
  }

}
