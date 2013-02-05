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

import java.util.HashMap;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * A selection process for roads based on strokes computation. The roads are
 * kept if they belong to a stroke longer than the threshold.
 * @author GTouya
 * 
 */
public class StrokeSelectionProcess extends ScaleMasterGeneProcess {

  private double lengthThreshold;
  private String attributeName = null;
  private static StrokeSelectionProcess instance = null;
  @SuppressWarnings("unused")
  private boolean deleted = false;

  protected StrokeSelectionProcess() {
    // Exists only to defeat instantiation.
  }

  public static StrokeSelectionProcess getInstance() {
    if (StrokeSelectionProcess.instance == null) {
      StrokeSelectionProcess.instance = new StrokeSelectionProcess();
    }
    return StrokeSelectionProcess.instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    this.parameterise();
    // do the road enrichment
    NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
        .getCurrentDataset().getRoadNetwork(), true);
    // get the eliminated features to compute strokes on
    HashMap<ArcReseau, IRoadLine> map = new HashMap<ArcReseau, IRoadLine>();
    // first get the road features not yet selected
    for (IGeneObj obj : features) {
      // FIXME gérer les cas sans sélection attributaire préalable
      if (!obj.isDeleted()) {
        continue;
      }
      map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
    }
    // then compute the strokes
    RoadStrokesNetwork network = new RoadStrokesNetwork(map.keySet());
    HashSet<String> attributeNames = new HashSet<String>();
    if (this.attributeName != null) {
      attributeNames.add(this.attributeName);
    }
    network.buildStrokes(attributeNames, 112.5, 45.0, true);
    // select the strokes big enough
    for (Stroke stroke : network.getStrokes()) {
      if (stroke.getLength() > this.lengthThreshold) {
        for (ArcReseau arc : stroke.getFeatures()) {
          IRoadLine road = map.get(arc);
          road.cancelElimination();
        }
      }
    }
  }

  @Override
  public String getProcessName() {
    return "StrokeSelection";
  }

  @Override
  public void parameterise() {
    this.lengthThreshold = (Double) this.getParamValueFromName("min_length");
    if (this.hasParameter("name_attribute")) {
      this.attributeName = (String) this
          .getParamValueFromName("name_attribute");
    }
    if (this.hasParameter("deleted")) {
      this.deleted = (Boolean) this.getParamValueFromName("deleted");
    }
  }

}
