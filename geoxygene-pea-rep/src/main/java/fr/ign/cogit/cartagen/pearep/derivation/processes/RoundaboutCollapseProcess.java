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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.genealgorithms.section.CollapseRoundabout;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.network.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * A selection process for roads based on strokes computation. The roads are
 * kept if they belong to a stroke longer than the threshold.
 * @author GTouya
 * 
 */
public class RoundaboutCollapseProcess extends ScaleMasterGeneProcess {

  private double diameter;
  private static RoundaboutCollapseProcess instance = null;
  private boolean deleted = true;

  protected RoundaboutCollapseProcess() {
    // Exists only to defeat instantiation.
  }

  public static RoundaboutCollapseProcess getInstance() {
    if (RoundaboutCollapseProcess.instance == null) {
      RoundaboutCollapseProcess.instance = new RoundaboutCollapseProcess();
    }
    return RoundaboutCollapseProcess.instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    this.parameterise();

    // enrich the road network
    NetworkEnrichment.enrichNetwork(CartAGenDocOld.getInstance()
        .getCurrentDataset(), CartAGenDocOld.getInstance().getCurrentDataset()
        .getRoadNetwork(), deleted);

    // get the eliminated features to compute strokes on
    HashMap<ArcReseau, IRoadLine> map = new HashMap<ArcReseau, IRoadLine>();
    // first get the road features not yet selected
    for (IGeneObj obj : CartAGenDocOld.getInstance().getCurrentDataset()
        .getRoadNetwork().getSections()) {
      if (((INetworkSection) obj).getInitialNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (((INetworkSection) obj).getFinalNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (deleted) {
        map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
      } else {
        if (obj.isDeleted()) {
          continue;
        }
        map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
      }
    }

    CrossRoadDetection algo = new CrossRoadDetection();
    algo.detectRoundaboutsAndBranchingCartagen(CartAGenDocOld.getInstance()
        .getCurrentDataset());

    for (IRoundAbout roundabout : CartAGenDocOld.getInstance()
        .getCurrentDataset().getRoundabouts()) {
      CollapseRoundabout collapse = new CollapseRoundabout(diameter, roundabout);
      collapse.collapseToPoint();
    }

  }

  @Override
  public String getProcessName() {
    return "RoundaboutCollapse";
  }

  @Override
  public void parameterise() {
    this.diameter = (Double) this.getParamValueFromName("diameter");
    if (this.hasParameter("deleted")) {
      this.deleted = (Boolean) this.getParamValueFromName("deleted");
    }
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("diameter", Double.class, 100.0));
    params.add(new ProcessParameter("deleted", Boolean.class, true));
    return params;
  }

}
