/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.urban;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public interface ISportsField extends IUrbanElement, IGeneObjSurf {

  public static final String FEAT_TYPE_NAME = "SportsField"; //$NON-NLS-1$

  public SportsFieldType getType();

  /**
   * Get the type as a String value for symbolisation purposes
   * @return
   */
  public String getTypeSymbol();

  public enum SportsFieldType {
    TENNIS, FOOTBALL, SWIMMINGPOOL, GYM, UNKNOWN
  }

  /**
   * The median line of a sports field, useful to draw the symbol for tennis
   * courts or football fields.
   * @return
   */
  public ILineString getMedianGeom();
}
