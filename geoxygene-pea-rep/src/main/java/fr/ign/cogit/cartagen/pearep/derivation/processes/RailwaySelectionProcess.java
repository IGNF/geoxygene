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
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * A selection process for roads based on strokes computation. The roads are
 * kept if they belong to a stroke longer than the threshold.
 * @author GTouya
 * 
 */
public class RailwaySelectionProcess extends ScaleMasterGeneProcess {

  private double lengthThreshold;
  private String attributeName = null;
  private static RailwaySelectionProcess instance = null;

  protected RailwaySelectionProcess() {
    // Exists only to defeat instantiation.
  }

  public static RailwaySelectionProcess getInstance() {
    if (RailwaySelectionProcess.instance == null) {
      RailwaySelectionProcess.instance = new RailwaySelectionProcess();
    }
    return RailwaySelectionProcess.instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    this.parameterise();

    // make planar and enrich the road network
    NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
        .getCurrentDataset(), CartAGenDoc.getInstance().getCurrentDataset()
        .getRailwayNetwork(), false);

    PropertyIsNotEqualTo filter = new PropertyIsNotEqualTo();
    filter.setLiteral(new Literal("0"));
    filter.setPropertyName(new PropertyName("loc"));

    // get the eliminated features to compute strokes on
    HashMap<ArcReseau, IRailwayLine> map = new HashMap<ArcReseau, IRailwayLine>();
    // first get the road features not yet selected
    for (IGeneObj obj : features) {
      if (((INetworkSection) obj).getInitialNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (((INetworkSection) obj).getFinalNode() == null) {
        obj.eliminateBatch();
        continue;
      }
      if (filter.evaluate(obj)) {
        continue;
      }
      map.put((ArcReseau) obj.getGeoxObj(), (IRailwayLine) obj);
    }
    // then compute the strokes
    StrokesNetwork network = new StrokesNetwork(map.keySet());
    HashSet<String> attributeNames = new HashSet<String>();
    if (this.attributeName != null) {
      attributeNames.add(this.attributeName);
    }
    network.buildStrokes(attributeNames, 112.5, 45.0, true);
    // select the strokes big enough
    for (Stroke stroke : network.getStrokes()) {
      if (stroke.getLength() < this.lengthThreshold) {
        for (ArcReseau arc : stroke.getFeatures()) {
          IRailwayLine rail = map.get(arc);
          if (rail != null) {
            rail.eliminateBatch();
          }
        }
      }
    }
  }

  @Override
  public String getProcessName() {
    return "RailwaySelection";
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
