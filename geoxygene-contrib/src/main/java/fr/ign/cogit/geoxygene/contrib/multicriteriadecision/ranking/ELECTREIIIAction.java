/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.multicriteriadecision.ranking;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ELECTREIIIAction {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static AtomicInteger counter = new AtomicInteger();
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private int id;
  private HashMap<String, Object> parameters;
  private Object obj;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ELECTREIIIAction(Object obj, HashMap<String, Object> parameters) {
    this.id = counter.getAndIncrement();
    this.parameters = new HashMap<>();
    this.parameters.putAll(parameters);
    this.obj = obj;
  }

  // Getters and setters //
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public HashMap<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(HashMap<String, Object> parameters) {
    this.parameters = parameters;
  }

  public Object getObj() {
    return obj;
  }

  public void setObj(Object obj) {
    this.obj = obj;
  }

  // Other public methods //
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    StringBuffer buff = new StringBuffer("action" + id + " (" + obj.toString());
    for (String param : parameters.keySet()) {
      buff.append(",[" + param + "," + parameters.get(param).toString() + "])");
    }
    return buff.toString();
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
