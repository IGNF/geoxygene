/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.network;

public enum NetworkSectionType {
  NORMAL,
  DIRECT_DEAD_END,
  INDIRECT_DEAD_END,
  BRIDGE,
  ISOLATED,
  DOUBLE_DEAD_END,
  HYBRID,
  UNKNOWN;
}

