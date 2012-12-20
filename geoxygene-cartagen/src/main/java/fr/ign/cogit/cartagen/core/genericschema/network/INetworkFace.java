/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.network;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

/*
 * ###### IGN / CartAGen ###### Title: Node Description: Faces de réseaux
 * Author: J. Renard Date: 01/07/2010
 */

public interface INetworkFace extends IGeneObjSurf {

  /**
   * @return les arcs bordant la face
   */
  public Collection<INetworkSection> getSections();

  public static final String FEAT_TYPE_NAME = "NetworkFace"; //$NON-NLS-1$
}
