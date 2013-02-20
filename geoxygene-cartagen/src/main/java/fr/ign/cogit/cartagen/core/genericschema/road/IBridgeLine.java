package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;

public interface IBridgeLine extends IGeneObjLin {

  public enum BridgeType {
    BRIDGE, FORD
  }

  public BridgeType getType();

  public IWaterArea getCrossedArea();

  public static final String FEAT_TYPE_NAME = "BridgeLine"; //$NON-NLS-1$
}
