package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine.BridgeType;

public interface IBridgePoint extends IGeneObjPoint {

  public BridgeType getType();

  public INetworkSection getCrossedNetwork();

  public IRoadLine getRoad();

  public static final String FEAT_TYPE_NAME = "BridgePoint"; //$NON-NLS-1$
}
