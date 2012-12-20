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

public interface IEmptySpace extends IGeneObjSurf {
  public static final String FEAT_TYPE_NAME = "EmptySpace"; //$NON-NLS-1$

  public IUrbanBlock getBlock();
}
