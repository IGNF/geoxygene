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
import fr.ign.cogit.geoxygene.distance.Frechet;

/**
 * Distance Function dedicated to lines that uses the Frechet distance to
 * measure the distance between the lines.
 * @author GTouya
 * 
 */
public class FrechetDistanceFunction extends ELECTRECriterion implements
    DistanceFunction {

  public FrechetDistanceFunction(String nom) {
    super(nom);
    // TODO Auto-generated constructor stub
  }

  @Override
  public double getDistance(IGeometry previous, IGeometry current) {

    ILineString line1 = null, line2 = null;
    if (previous instanceof ILineString)
      line1 = (ILineString) previous;
    else if (previous instanceof IPolygon)
      line1 = ((IPolygon) previous).exteriorLineString();
    else
      return 0;
    if (current instanceof ILineString)
      line2 = (ILineString) current;
    else if (current instanceof IPolygon)
      line2 = ((IPolygon) current).exteriorLineString();
    else
      return 0;

    return Frechet.discreteFrechet(line1, line2);
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
