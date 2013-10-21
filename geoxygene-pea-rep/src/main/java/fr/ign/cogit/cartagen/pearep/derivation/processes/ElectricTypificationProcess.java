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
import fr.ign.cogit.cartagen.genealgorithms.energy.ElectricityLineTypification;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * 
 * @author GTouya
 * 
 */
public class ElectricTypificationProcess extends ScaleMasterGeneProcess {

  private static ElectricTypificationProcess instance = null;
  private double minLength = 500.0;
  private double parallelDist = 40.0;

  protected ElectricTypificationProcess() {
    // Exists only to defeat instantiation.
  }

  public static ElectricTypificationProcess getInstance() {
    if (instance == null) {
      instance = new ElectricTypificationProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    // perform typification
    ElectricityLineTypification algo = new ElectricityLineTypification(
        CartAGenDocOld.getInstance().getCurrentDataset().getElectricityNetwork(),
        parallelDist);
    for (ElectricityLineTypification.ParallelSectionsCluster cluster : algo
        .findClusters()) {
      cluster.collapse();
    }
    // now eliminate small features
    for (IGeneObj obj : features) {
      if (obj.isEliminated())
        continue;
      if (obj.getGeom().length() < minLength)
        obj.eliminateBatch();
    }
  }

  @Override
  public String getProcessName() {
    return "ElectricLineTypification";
  }

  @Override
  public void parameterise() {
    if (this.hasParameter("parallel_distance"))
      this.parallelDist = (Double) getParamValueFromName("parallel_distance");
    if (this.hasParameter("min_length"))
      this.minLength = (Double) getParamValueFromName("min_length");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("parallel_distance", Double.class, 40.0));
    params.add(new ProcessParameter("min_length", Double.class, 500.0));
    return params;
  }

}
