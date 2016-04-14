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
 * continuity, legibility functions compute a measure that state if the current
 * scale is legible or not (e.g. coalescence for linear features, topology
 * validity).
 * @author gtouya
 * 
 */
public interface LegibilityFunction {

  /**
   * Gives the value of the legibility function for a given feature and a scale
   * formatted as follows: 20000.0 means 1:20k scale.
   * @param geom
   * @param scale
   * @return
   */
  public double getValue(IGeometry geom, double scale);

  /**
   * Gives the function name
   * @return
   */
  public String getName();
}
