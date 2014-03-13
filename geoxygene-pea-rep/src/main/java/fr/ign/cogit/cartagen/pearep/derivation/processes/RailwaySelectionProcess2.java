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
import fr.ign.cogit.cartagen.genealgorithms.rail.TypifySideTracks;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * A selection process for roads based on strokes computation. The roads are
 * kept if they belong to a stroke longer than the threshold.
 * @author GTouya
 * 
 */
public class RailwaySelectionProcess2 extends ScaleMasterGeneProcess {

  private double lengthThreshold;
  private String attributeName = null;
  private static RailwaySelectionProcess2 instance = null;

  protected RailwaySelectionProcess2() {
    // Exists only to defeat instantiation.
  }

  public static RailwaySelectionProcess2 getInstance() {
    if (RailwaySelectionProcess2.instance == null) {
      RailwaySelectionProcess2.instance = new RailwaySelectionProcess2();
    }
    return RailwaySelectionProcess2.instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    this.parameterise();
    TypifySideTracks algo = new TypifySideTracks(this.lengthThreshold,
        CartAGenDocOld.getInstance().getCurrentDataset());
    algo.typifySideTracks();
  }

  @Override
  public String getProcessName() {
    return "RailwaySelection2";
  }

  @Override
  public void parameterise() {
    this.lengthThreshold = (Double) this.getParamValueFromName("min_length");
    if (this.hasParameter("name_attribute")) {
      this.attributeName = (String) this
          .getParamValueFromName("name_attribute");
    }
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("name_attribute", String.class, ""));
    params.add(new ProcessParameter("min_length", Double.class, 2000.0));
    return params;
  }

}
