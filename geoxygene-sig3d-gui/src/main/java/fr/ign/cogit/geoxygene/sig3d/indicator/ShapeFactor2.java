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
 * @version 1.0
 **/
package fr.ign.cogit.geoxygene.sig3d.indicator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * 
 * @author MBrasebin
 *
 */
public class ShapeFactor2  {

  private double value;

  public ShapeFactor2(IFeature b) {
    
    double zMin = HauteurCalculation.calculateZBasPBB(b);
    double zMax = HauteurCalculation.calculateZHautPHF(b);

    double area = b.getGeom().area();
    
    
    
    value = Math.pow(zMax- zMin, 2) / area;

  }


  public Double getValue() {

    return value;
  }



}
