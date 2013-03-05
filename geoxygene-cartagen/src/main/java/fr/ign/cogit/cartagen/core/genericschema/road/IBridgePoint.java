package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;

public interface IBridgePoint extends IGeneObjPoint {

  public enum BridgeType {
    BRIDGE, FORD
  }

  public BridgeType getType();

  public IWaterLine getCrossedLine();

  public static final String FEAT_TYPE_NAME = "BridgePoint"; //$NON-NLS-1$
}
