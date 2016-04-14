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
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;

/**
 * Distance Function dedicated to lines that uses the Hausdorff distance to
 * measure the distance between the lines.
 * @author GTouya
 * 
 */
public class HausdorffDistanceFunction extends ELECTRECriterion implements
    DistanceFunction {

  public HausdorffDistanceFunction(String nom) {
    super(nom);
    this.setWeight(1.5);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.15);
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
    return Distances.hausdorff(line1, line2);
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
