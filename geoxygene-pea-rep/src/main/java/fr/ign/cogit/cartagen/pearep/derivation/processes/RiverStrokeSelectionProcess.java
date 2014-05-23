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
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.rivers.RiverStrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * A selection process for roads based on strokes computation. The roads are
 * kept if they belong to a stroke longer than the threshold.
 * @author GTouya
 * 
 */
public class RiverStrokeSelectionProcess extends ScaleMasterGeneProcess {

  private double lengthThreshold;
  private int hortonOrder = 2;
  private static RiverStrokeSelectionProcess instance = null;
  private boolean braided = false;

  protected RiverStrokeSelectionProcess() {
    // Exists only to defeat instantiation.
  }

  public static RiverStrokeSelectionProcess getInstance() {
    if (RiverStrokeSelectionProcess.instance == null) {
      RiverStrokeSelectionProcess.instance = new RiverStrokeSelectionProcess();
    }
    return RiverStrokeSelectionProcess.instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    this.parameterise();

    HashMap<ArcReseau, IWaterLine> map = new HashMap<ArcReseau, IWaterLine>();
    HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
    for (IWaterLine feat : CartAGenDocOld.getInstance().getCurrentDataset()
        .getWaterLines()) {
      if (feat.isEliminated())
        continue;
      arcs.add((ArcReseau) feat.getGeoxObj());
      map.put((ArcReseau) feat.getGeoxObj(), feat);
    }
    RiverStrokesNetwork net = new RiverStrokesNetwork(arcs);
    net.findSourcesAndSinks();
    net.buildRiverStrokes();

    for (Stroke stroke : net.getStrokes()) {
      ((RiverStroke) stroke).computeHortonOrder();
      if (((RiverStroke) stroke).isBraided() && braided) {
        eliminateStroke((RiverStroke) stroke, map);
      } else if (((RiverStroke) stroke).getHortonOrder() < this.hortonOrder) {
        eliminateStroke((RiverStroke) stroke, map);
      } else if (stroke.getLength() < this.lengthThreshold) {
        eliminateStroke((RiverStroke) stroke, map);
      }
    }
  }

  private void eliminateStroke(RiverStroke stroke,
      HashMap<ArcReseau, IWaterLine> map) {
    for (ArcReseau arc : stroke.getFeatures()) {
      IWaterLine river = map.get(arc);
      river.eliminateBatch();
    }
  }

  @Override
  public String getProcessName() {
    return "RiverStrokeSelection";
  }

  @Override
  public void parameterise() {
    this.lengthThreshold = (Double) this.getParamValueFromName("min_length");
    if (this.hasParameter("horton_order")) {
      this.hortonOrder = (Integer) this.getParamValueFromName("horton_order");
    }
    if (this.hasParameter("braided")) {
      this.braided = (Boolean) this.getParamValueFromName("braided");
    }
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("horton_order", Integer.class, 2));
    params.add(new ProcessParameter("braided", Boolean.class, false));
    params.add(new ProcessParameter("min_length", Double.class, 500.0));
    return params;
  }

}
