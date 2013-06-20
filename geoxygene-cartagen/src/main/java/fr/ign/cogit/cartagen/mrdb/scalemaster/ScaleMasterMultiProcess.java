/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * ScaleMasterMultiProcess is a specific type of ScaleMaster process that is
 * applied on multiple themes and not only one like the other processes (except
 * the landuse process). It is triggered after all other processes and
 * parameters may be related to two themes (e.g. the inter-distance between
 * features of two themes).
 * @author GTouya
 * 
 */
public abstract class ScaleMasterMultiProcess implements
    Comparable<ScaleMasterMultiProcess> {

  private Set<MultiThemeParameter> parameters;
  private int scale;
  private ScaleMaster scaleMaster;

  protected ScaleMasterMultiProcess() {
    parameters = new HashSet<MultiThemeParameter>();
  }

  public Set<MultiThemeParameter> getParameters() {
    return parameters;
  }

  public void setParameters(Set<MultiThemeParameter> parameters) {
    this.parameters = parameters;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public int getScale() {
    return scale;
  }

  public ScaleMaster getScaleMaster() {
    return scaleMaster;
  }

  public void setScaleMaster(ScaleMaster scaleMaster) {
    this.scaleMaster = scaleMaster;
  }

  /**
   * Fills the process parameters from the generic set of parameters.
   */
  public abstract void parameterise();

  /**
   * Execute the process with the given parameters.
   */
  public abstract void execute(IFeatureCollection<? extends IGeneObj> features)
      throws Exception;

  public abstract String getProcessName();

  /**
   * Get all the parameters of a process with their default value.
   * @return
   */
  public abstract Set<MultiThemeParameter> getDefaultParameters();

  /**
   * Get the parameter value from its name.
   * @param paramName
   * @return
   */
  public Object getParamValueFromName(String paramName, String theme1,
      String theme2) {
    for (MultiThemeParameter param : getParameters()) {
      if (param.getName().equals(paramName))
        if ((theme1.equals(param.getTheme1()) || theme1.equals(param
            .getTheme2()))
            && (theme2.equals(param.getTheme1()) || theme2.equals(param
                .getTheme2())))
          return param.getValue();
    }
    return null;
  }

  public boolean hasParameter(String paramName) {
    for (ProcessParameter param : getParameters()) {
      if (param.getName().equals(paramName))
        return true;
    }
    return false;
  }

  public void addParameter(MultiThemeParameter parameter) {
    boolean contain = false;
    for (ProcessParameter param : this.parameters) {
      if (param.getName().equals(parameter.getName())) {
        param.setValue(parameter.getValue());
        contain = true;
      }
    }
    if (!contain)
      this.parameters.add(parameter);
  }

  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer(getProcessName());
    buff.append(" {");
    boolean first = true;
    for (ProcessParameter param : getParameters()) {
      if (!first)
        buff.append(", ");
      first = false;
      buff.append(param.getName());
      buff.append("(" + param.getValue() + ")");
    }
    buff.append("}");
    return buff.toString();
  }

  public Map<String, Object> getParametersMap() {
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    for (MultiThemeParameter param : parameters) {
      paramsMap.put(param.getName(), param.getValue());
    }
    return paramsMap;
  }

  @Override
  public int compareTo(ScaleMasterMultiProcess o) {
    return this.getProcessName().compareTo(o.getProcessName());
  }

}
