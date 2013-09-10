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

  public long getLongAttribute(String attrName) {
    Object val = getAttributeMap().get(attrName);
    if (val == null)
      return 0;
    if (val instanceof Long)
      return (Long) val;
    if (val instanceof Integer)
      return new Long((Integer) val);
    if (!((String) val).equals(""))
      return new Long((String) val);
    return 0;
  }

  public double getDoubleAttribute(String attrName) {
    Object val = getAttributeMap().get(attrName);
    if (val == null)
      return 0;
    if (val instanceof Double)
      return (Double) val;
    if (val instanceof String) {
      if (!val.equals(""))
        return new Double((String) val);
    } else
      return new Double((Float) val);
    return 0.0;
  }

  public String getStringAttribute(String attrName) {
    Object val = getAttributeMap().get(attrName);
    if (val == null)
      return "";
    return val.toString();
  }
}
