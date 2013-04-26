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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate the Visvalingam-Whyatt algorithm to be used inside a
 * ScaleMaster2.0
 * @author GTouya
 * 
 */
public class VisvalingamWhyattProcess extends ScaleMasterGeneProcess {

  private double areaTolerance;
  private static VisvalingamWhyattProcess instance = null;

  protected VisvalingamWhyattProcess() {
    // Exists only to defeat instantiation.
  }

  public static VisvalingamWhyattProcess getInstance() {
    if (instance == null) {
      instance = new VisvalingamWhyattProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      IGeometry geom = obj.getGeom();
      IGeometry newGeom = geom;
      VisvalingamWhyatt algo = new VisvalingamWhyatt(areaTolerance);
      try {
        if (newGeom instanceof IPolygon)
          newGeom = algo.simplify((IPolygon) geom);
        else if (newGeom instanceof ILineString)
          newGeom = algo.simplify((ILineString) geom);
      } catch (Exception e) {
        // let initial geom if algorithm fails
      }
      obj.setGeom(newGeom);
    }
  }

  @Override
  public String getProcessName() {
    return "Visvalingam-Whyatt";
  }

  @Override
  public void parameterise() {
    this.areaTolerance = (Double) getParamValueFromName("area_tolerance");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("area_tolerance", Double.class, 40.0));

    return params;
  }

}
