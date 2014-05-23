/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;

/**
 * Encapsulate the Gaussian Filter algorithm to be used inside a ScaleMaster2.0
 * @author JFGirres
 * 
 */
public class GaussianFilteringProcess extends ScaleMasterGeneProcess {

  private double gThreshold;
  private double sigma;
  private static GaussianFilteringProcess instance = null;

  protected GaussianFilteringProcess() {
    // Exists only to defeat instantiation.
  }

  public static GaussianFilteringProcess getInstance() {
    if (instance == null) {
      instance = new GaussianFilteringProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      ILineString ls = (ILineString) obj.getGeom();
      IGeometry newGeom = null;
      try {
        newGeom = GaussianFilter.gaussianFilter(ls, sigma, gThreshold);
      } catch (Exception e) {
        // let initial geom if algorithm fails
      }
      obj.setGeom(newGeom);
    }
  }

  @Override
  public String getProcessName() {
    return "GaussianSmoothing";
  }

  @Override
  public void parameterise() {
    this.gThreshold = (Double) getParamValueFromName("gaussian_threshold");
    this.sigma = (Double) getParamValueFromName("gaussian_sigma");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("gaussian_threshold", Double.class, 1.0));
    params.add(new ProcessParameter("gaussian_sigma", Double.class, 0.0));
    return params;
  }

}
