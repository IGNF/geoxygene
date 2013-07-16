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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class BridgeCollapseProcess extends ScaleMasterGeneProcess {

  private static BridgeCollapseProcess instance = null;

  protected BridgeCollapseProcess() {
    // Exists only to defeat instantiation.
  }

  public static BridgeCollapseProcess getInstance() {
    if (instance == null) {
      instance = new BridgeCollapseProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      IBridgeLine bridge = (IBridgeLine) obj;

      // first the case where the containing network is deleted
      if (bridge.getContainingNetwork() != null
          && bridge.getContainingNetwork().isEliminated())
        bridge.eliminateBatch();

      // get the intersecting river area
      boolean collapse = false;
      if (bridge.getCrossedArea() == null)
        collapse = true;
      else if (bridge.getCrossedArea().isDeleted())
        collapse = true;
      if (!collapse)
        continue;

      // now, collapse the bridge to a point
      obj.eliminateBatch();
      IGeometry geom = obj.getGeom();
      INetworkSection crossedNetwork = bridge.getCrossedNetwork();
      if (crossedNetwork == null) {
        Collection<IWaterLine> rivers = CartAGenDoc.getInstance()
            .getCurrentDataset().getWaterLines().select(geom);
        if (rivers == null)
          continue;
        if (rivers.isEmpty())
          continue;
        crossedNetwork = rivers.iterator().next();
      }
      IDirectPosition centroid = null;
      if (geom.intersects(crossedNetwork.getGeom()))
        centroid = geom.intersection(crossedNetwork.getGeom()).coord().get(0);
      else {
        for (IRoadLine road : CartAGenDoc.getInstance().getCurrentDataset()
            .getRoads().select(geom)) {
          if (road.getGeom().intersectsStrictement(crossedNetwork.getGeom())) {
            centroid = road.getGeom().intersection(crossedNetwork.getGeom())
                .coord().get(0);
            break;
          }
        }
        if (centroid == null) {
          for (IRailwayLine rail : CartAGenDoc.getInstance()
              .getCurrentDataset().getRailwayLines().select(geom)) {
            if (rail.getGeom().intersectsStrictement(crossedNetwork.getGeom())) {
              centroid = rail.getGeom().intersection(crossedNetwork.getGeom())
                  .coord().get(0);
              break;
            }
          }
        }
      }
      if (centroid == null)
        continue;
      IBridgePoint bridgePt = CartagenApplication.getInstance()
          .getCreationFactory().createBridgePoint(centroid.toGM_Point());
      CartAGenDoc.getInstance().getCurrentDataset().getBridgePoints()
          .add(bridgePt);
      bridgePt.setCrossedNetwork(crossedNetwork);
    }
  }

  @Override
  public String getProcessName() {
    return "BridgeCollapse";
  }

  @Override
  public void parameterise() {
    // no parameter
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();

    return params;
  }

}
