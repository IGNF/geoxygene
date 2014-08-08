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
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.DeleteDoublePreProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.MakeNetworkPlanar;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRoadLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
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
  private boolean deleted = true;

  protected boolean isDeleted() {
    return deleted;
  }

  protected String getAttributeName() {
    return attributeName;
  }

  protected double getLengthThreshold() {
    return lengthThreshold;
  }

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
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    this.parameterise();

    // do the road enrichment
    Set<Class<? extends IGeneObj>> classes = new HashSet<Class<? extends IGeneObj>>();
    // remove double features in roads
    classes.clear();
    classes.add(MGCPRoadLine.class);
    DeleteDoublePreProcess processDblRoad = DeleteDoublePreProcess
        .getInstance();
    processDblRoad.setProcessedClasses(classes);
    try {
      processDblRoad.execute(CartAGenDocOld.getInstance().getCurrentDataset()
          .getCartAGenDB());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // make planar and enrich the road network
    classes.clear();
    classes.add(MGCPRoadLine.class);
    MakeNetworkPlanar process = MakeNetworkPlanar.getInstance();
    process.setProcessedClasses(classes);
    try {
      process.execute(CartAGenDocOld.getInstance().getCurrentDataset()
          .getCartAGenDB());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
        if (!obj.isDeleted()) {
          continue;
        }
        map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
      } else {
        if (obj.isDeleted()) {
          continue;
        }
        map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
      }
    }

    // then compute the strokes
    RoadStrokesNetwork network = new RoadStrokesNetwork(map.keySet());
    HashSet<String> attributeNames = new HashSet<String>();
    if (this.getAttributeName() != null) {
      if (this.getAttributeName() != "")
        attributeNames.add(this.getAttributeName());
    }
    network.buildStrokes(attributeNames, 112.5, 45.0, true);
    // select the strokes big enough
    for (Stroke stroke : network.getStrokes()) {
      if (stroke.getLength() > this.lengthThreshold) {
        if (deleted) {
          for (ArcReseau arc : stroke.getFeatures()) {
            IRoadLine road = map.get(arc);
            if (road != null)
              road.cancelElimination();
          }
        }
      } else {
        if (!deleted) {
          for (ArcReseau arc : stroke.getFeatures()) {
            IRoadLine road = map.get(arc);
            if (road != null)
              road.eliminateBatch();
          }
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
      attributeName = (String) this.getParamValueFromName("name_attribute");
    }
    if (this.hasParameter("deleted")) {
      this.deleted = (Boolean) this.getParamValueFromName("deleted");
    }
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("name_attribute", String.class, ""));
    params.add(new ProcessParameter("deleted", Boolean.class, true));
    params.add(new ProcessParameter("min_length", Double.class, 2000.0));
    return params;
  }

}
