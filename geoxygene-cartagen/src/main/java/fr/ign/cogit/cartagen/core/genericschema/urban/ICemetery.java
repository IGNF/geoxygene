package fr.ign.cogit.cartagen.core.genericschema.urban;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

public interface ICemetery extends IGeneObjSurf, IUrbanElement {

  public static final String FEAT_TYPE_NAME = "Cemetery"; //$NON-NLS-1$

  public CemeteryType getType();

  /**
   * Get the type as a String value for symbolisation purposes
   * @return
   */
  public String getTypeSymbol();

  public enum CemeteryType {
    MILITARY, CHRISTIAN, MUSLIM, JEWISH, UNKNOWN
  }
}
