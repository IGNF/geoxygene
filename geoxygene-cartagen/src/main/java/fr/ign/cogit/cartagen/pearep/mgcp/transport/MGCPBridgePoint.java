package fr.ign.cogit.cartagen.pearep.mgcp.transport;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine.BridgeType;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public class MGCPBridgePoint extends MGCPFeature implements IBridgePoint {

  private BridgeType type;
  private INetworkSection crossedNetwork;
  private IRoadLine road;

  public MGCPBridgePoint(IPoint geom) {
    super();
    this.setGeom(geom);
    this.type = BridgeType.BRIDGE;
  }

  @Override
  public BridgeType getType() {
    return type;
  }

  @Override
  public INetworkSection getCrossedNetwork() {
    return crossedNetwork;
  }

  @Override
  public IRoadLine getRoad() {
    return road;
  }

  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  @Override
  public void setCrossedNetwork(INetworkSection section) {
    this.crossedNetwork = section;
  }

  @Override
  public void setRoad(IRoadLine road) {
    this.road = road;
  }
}
