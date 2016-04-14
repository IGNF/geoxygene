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

import fr.ign.cogit.cartagen.spatialanalysis.measures.LineCoalescence;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This legibility functions checks that the continuous current state does not
 * coalesce, and if it does, returns an amount of coalescence.
 * @author GTouya
 * 
 */
public class CoalescenceFunction extends ELECTRECriterion implements
    LegibilityFunction {

  /*** The line symbol width in mm on the map ***/
  private double symbolWidth = 0.1;

  public CoalescenceFunction() {
    super("Coalescence");
  }

  public CoalescenceFunction(double symbolWidth) {
    super("Coalescence");
    this.symbolWidth = symbolWidth;
  }

  @Override
  public double getValue(IGeometry feature, double scale) {
    ILineString line = null;
    if (feature instanceof ILineString)
      line = (ILineString) feature;
    else
      return 0;

    LineCoalescence measure = new LineCoalescence(line, scale, symbolWidth);
    measure.compute();
    double coalescenceAmount = 0.0;

    for (int i = 0; i < measure.getSections().size(); i++) {
      if (measure.getCoalescenceTypes().get(i) == LineCoalescence.NONE)
        continue;

      ILineString section = measure.getSections().get(i);
      if (measure.getCoalescenceTypes().get(i) == LineCoalescence.LEFT
          || measure.getCoalescenceTypes().get(i) == LineCoalescence.RIGHT)
        coalescenceAmount += section.length();
      else if (measure.getCoalescenceTypes().get(i) == LineCoalescence.BOTH
          || measure.getCoalescenceTypes().get(i) == LineCoalescence.HETEROG)
        coalescenceAmount += 2 * section.length();
    }

    // normalise the coalescence amount the line total length
    coalescenceAmount = coalescenceAmount / line.length();

    return coalescenceAmount;
  }

  public double getSymbolWidth() {
    return symbolWidth;
  }

  public void setSymbolWidth(double symbolWidth) {
    this.symbolWidth = symbolWidth;
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
