package fr.ign.cogit.cartagen.core.defaultschema.road;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine.BridgeType;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.FranchissementImpl;

public class BridgePoint extends GeneObjPointDefault implements IBridgePoint {

  private BridgeType type;
  private Franchissement geoxObj;
  private INetworkSection crossedSection;
  private IRoadLine road;

  public BridgePoint(IPoint point) {
    super();
    this.geoxObj = new FranchissementImpl();
    this.geoxObj.setGeom(point);
    this.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.setType(BridgeType.BRIDGE);
    this.road = CartAGenDoc.getInstance().getCurrentDataset().getRoads()
        .select(point.buffer(0.5)).iterator().next();
  }

  @Override
  public BridgeType getType() {
    return this.type;
  }

  @Override
  public INetworkSection getCrossedNetwork() {
    return this.crossedSection;
  }

  @Override
  public IRoadLine getRoad() {
    return this.road;
  }

  @Override
  public void setCrossedNetwork(INetworkSection crossedSection) {
    this.crossedSection = crossedSection;
  }

  public void setType(BridgeType type) {
    this.type = type;
  }

  @Override
  public void setRoad(IRoadLine road) {
    this.road = road;
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
