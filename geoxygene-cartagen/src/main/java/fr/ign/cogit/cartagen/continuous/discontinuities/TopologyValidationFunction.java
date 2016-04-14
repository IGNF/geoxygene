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

import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * This legibility functions checks that the geometry is valid and value
 * increases when more vertices are involved in topology problems.
 * @author gtouya
 * 
 */
public class TopologyValidationFunction extends ELECTRECriterion implements
    LegibilityFunction {

  public TopologyValidationFunction() {
    super("Topology validity");
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getValue(IGeometry geom, double scale) {

    if (geom.isValid())
      return 0;
    ILineString line = null;
    if (geom instanceof ILineString)
      line = (ILineString) geom;
    else if (geom instanceof IPolygon)
      line = ((IPolygon) geom).exteriorLineString();
    else
      return 0;
    int nbTopoVert = CommonAlgorithmsFromCartAGen.getSelfIntersections(line)
        .size();

    return nbTopoVert;
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
