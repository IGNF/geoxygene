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
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class FilteringProcess extends ScaleMasterGeneProcess {

  private double dpThreshold;
  private static FilteringProcess instance = null;

  protected FilteringProcess() {
    // Exists only to defeat instantiation.
  }

  public static FilteringProcess getInstance() {
    if (instance == null) {
      instance = new FilteringProcess();
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
      try {
        if (newGeom instanceof IPolygon)
          newGeom = Filtering.DouglasPeuckerPoly((IPolygon) geom, dpThreshold);
        else if (newGeom instanceof ILineString)
          newGeom = Filtering.DouglasPeucker(geom, dpThreshold);
      } catch (Exception e) {
        // let initial geom if D&P fails
      }
      obj.setGeom(newGeom);
    }
  }

  @Override
  public String getProcessName() {
    return "Filtering";
  }

  @Override
  public void parameterise() {
    this.dpThreshold = (Double) getParamValueFromName("dp_filtering");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("dp_filtering", Double.class, 2.0));

    return params;
  }

}
