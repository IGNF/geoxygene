/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;

public class PolygonSimplification extends ScaleMasterGeneProcess {

  private double segLength;
  private static PolygonSimplification instance = null;

  public PolygonSimplification() {
    // Exists only to defeat instantiation.
  }

  public static PolygonSimplification getInstance() {
    if (instance == null) {
      instance = new PolygonSimplification();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    for (IGeneObj obj : features) {
      IPolygon poly = (IPolygon) obj.getGeom();
      IGeometry newGeom = SimplificationAlgorithm.simplification(poly,
          segLength);
      obj.setGeom(newGeom);
    }
  }

  @Override
  public String getProcessName() {
    return "Polygon Simplification";
  }

  @Override
  public void parameterise() {
    this.segLength = (Double) getParamValueFromName("segment_length");
  }

}
