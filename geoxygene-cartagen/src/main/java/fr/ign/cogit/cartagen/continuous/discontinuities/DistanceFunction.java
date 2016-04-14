/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.discontinuities;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * In order to identify discontinuities in continuous generalization, i.e.
 * scales to avoid that create piecewise continuity rather than strict
 * continuity, distance functions compute a similarity distance with a previous
 * scale (scale is discretized).
 * @author gtouya
 * 
 */
public interface DistanceFunction {

  /**
   * Get the distance value between a current geometry and the geometry at the
   * previous scale.
   * @param previous
   * @param current
   * @return
   */
  public double getDistance(IGeometry previous, IGeometry current);

  /**
   * Gives the function name
   * @return
   */
  public String getName();
}
