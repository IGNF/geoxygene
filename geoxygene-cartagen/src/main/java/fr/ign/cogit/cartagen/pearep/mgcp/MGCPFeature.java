/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.Map;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;

public abstract class MGCPFeature extends GeneObjDefault {

  private Map<String, Object> attributeMap;

  public void setAttributeMap(Map<String, Object> attributeMap) {
    this.attributeMap = attributeMap;
  }

  public Map<String, Object> getAttributeMap() {
    return attributeMap;
  }
}
