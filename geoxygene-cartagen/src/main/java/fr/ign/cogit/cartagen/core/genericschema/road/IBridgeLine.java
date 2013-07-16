package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public interface IBridgeLine extends IGeneObjLin {

  public enum BridgeType {
    BRIDGE, FORD
  }

  public BridgeType getType();

  public void setType(BridgeType type);

  public IWaterArea getCrossedArea();

  public void setCrossedArea(IWaterArea area);

  /**
   * The crossed network section if the bridge line crosses a river or road or
   * railway line. May be null if the bridge is over an area feature (then
   * getCrossedArea() can't return a null value).
   * @return
   */
  public INetworkSection getCrossedNetwork();

  public void setCrossedNetwork(INetworkSection section);

  public INetworkSection getContainingNetwork();

  public void setContainingNetwork(INetworkSection section);

  public static final String FEAT_TYPE_NAME = "BridgeLine"; //$NON-NLS-1$
}
