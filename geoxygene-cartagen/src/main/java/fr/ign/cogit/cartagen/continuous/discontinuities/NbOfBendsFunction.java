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

import java.util.Map;

import fr.ign.cogit.cartagen.spatialanalysis.measures.section.BendSeries;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This legibility functions counts the number of bends in the continuous
 * current state for a given smoothing sigma. A good continuous generalization
 * should monotonously reduce the number of bends.
 * @author GTouya
 * 
 */
public class NbOfBendsFunction extends ELECTRECriterion implements
    LegibilityFunction {

  /*** The sigma of the gaussian smoothing applied to compute inflexion points ***/
  private double sigmaSmoothing;

  public NbOfBendsFunction(double sigmaSmoothing) {
    super("NbOfBends");
    this.sigmaSmoothing = sigmaSmoothing;
  }

  @Override
  public double getValue(IGeometry feature, double scale) {
    ILineString line = null;
    if (feature instanceof ILineString)
      line = (ILineString) feature;
    else
      return 0;

    BendSeries bendSeries = new BendSeries(line, sigmaSmoothing);

    return bendSeries.getBends().size();
  }

  @Override
  public String getName() {
    return super.getName();
  }

  @Override
  public double value(Map<String, Object> param) {
    // TODO Auto-generated method stub
    return 0;
  }

}
