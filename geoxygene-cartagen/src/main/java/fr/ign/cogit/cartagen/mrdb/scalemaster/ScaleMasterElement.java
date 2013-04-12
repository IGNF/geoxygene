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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.Interval;
import fr.ign.cogit.geoxygene.filter.Filter;

/**
 * A ScaleMasterUnit is the unit part of a scale master: it's what is inside a
 * scale interval, in a scaleline of the ScaleMaster, i.e. a class name from a
 * given DB and a generalisation process along with its parameters.
 * @author GTouya
 * 
 */
public class ScaleMasterElement {

  public enum ProcessPriority {
    MIN, LOW, MEDIUM, HIGH, URGENT
  }

  /**
   * The {@link ScaleLine} object {@code this} unit is part of.
   */
  private ScaleLine scaleLine;

  /**
   * The scale interval {@code this} unit is included in.
   */
  private Interval<Integer> interval;

  /**
   * The name of the CartAGenDB {@code this} unit takes its data from.
   */
  private String dbName;
  /**
   * The ordered list of generalisation processes to apply on selected features
   * for {@code this} scale master unit.
   */
  private List<String> processesToApply;
  /**
   * The ordered list of generalisation processes priorities. for {@code this}
   * scale master unit.
   */
  private List<ProcessPriority> processPriorities;
  /**
   * The ordered list of parameters for the generalisation processes with the
   * corresponding indices in the processesToApply list.
   */
  private List<Map<String, Object>> parameters;
  /**
   * The OGC Filter query to select the relevant features in the unit, using
   * only the features attributes.
   */
  private Filter ogcFilter;
  private ProcessPriority filterPriority;
  /**
   * The classes treated by this scale master element. These classes are a
   * subset of the scale line classes.
   */
  private Set<Class<? extends IGeneObj>> classes;
  /**
   * The enrichments required to trigger the processes and filter of this
   * {@link ScaleMasterElement} instance.
   */
  private List<ScaleMasterEnrichment> enrichments;

  public ScaleMasterElement(ScaleLine scaleLine, Interval<Integer> interval,
      String dbName) {
    this.scaleLine = scaleLine;
    this.interval = interval;
    this.dbName = dbName;
    this.processesToApply = new ArrayList<String>();
    this.parameters = new ArrayList<Map<String, Object>>();
    this.processPriorities = new ArrayList<ProcessPriority>();
    this.enrichments = new ArrayList<ScaleMasterEnrichment>();
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getDbName() {
    return dbName;
  }

  public void setInterval(Interval<Integer> interval) {
    this.interval = interval;
  }

  public Interval<Integer> getInterval() {
    return interval;
  }

  public void setScaleLine(ScaleLine scaleLine) {
    this.scaleLine = scaleLine;
  }

  public ScaleLine getScaleLine() {
    return scaleLine;
  }

  public void setProcessesToApply(List<String> processesToApply) {
    this.processesToApply = processesToApply;
  }

  public List<String> getProcessesToApply() {
    return processesToApply;
  }

  public void setOgcFilter(Filter ogcFilter) {
    this.ogcFilter = ogcFilter;
  }

  public Filter getOgcFilter() {
    return ogcFilter;
  }

  public void setClasses(Set<Class<? extends IGeneObj>> classes) {
    this.classes = classes;
  }

  public Set<Class<? extends IGeneObj>> getClasses() {
    return classes;
  }

  public void setParameters(List<Map<String, Object>> parameters) {
    this.parameters = parameters;
  }

  public List<Map<String, Object>> getParameters() {
    return parameters;
  }

  public ProcessPriority getFilterPriority() {
    return filterPriority;
  }

  public void setFilterPriority(ProcessPriority filterPriority) {
    this.filterPriority = filterPriority;
  }

  public List<ProcessPriority> getProcessPriorities() {
    return processPriorities;
  }

  public void setProcessPriorities(List<ProcessPriority> processPriorities) {
    this.processPriorities = processPriorities;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
    result = prime * result + ((interval == null) ? 0 : interval.hashCode());
    result = prime * result
        + ((processesToApply == null) ? 0 : processesToApply.hashCode());
    result = prime * result + ((scaleLine == null) ? 0 : scaleLine.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScaleMasterElement other = (ScaleMasterElement) obj;
    if (dbName == null) {
      if (other.dbName != null)
        return false;
    } else if (!dbName.equals(other.dbName))
      return false;
    if (interval == null) {
      if (other.interval != null)
        return false;
    } else if (!interval.equals(other.interval))
      return false;
    if (processesToApply == null) {
      if (other.processesToApply != null)
        return false;
    } else if (!processesToApply.equals(other.processesToApply))
      return false;
    if (scaleLine == null) {
      if (other.scaleLine != null)
        return false;
    } else if (!scaleLine.equals(other.scaleLine))
      return false;
    return true;
  }

  @Override
  public String toString() {
    String strOgcFilter = "";
    if (ogcFilter != null) {
      try {
        strOgcFilter = ogcFilter.toString();
      } catch (Exception e) {
        // display nothing
      }
    }
    if (processesToApply == null) {
      if (strOgcFilter.equals(""))
        strOgcFilter = "no process";
      return (strOgcFilter);
    } else if (processesToApply.size() == 1)
      return (processesToApply.get(0) + strOgcFilter);
    else {
      StringBuffer strBuff = new StringBuffer();
      for (String proc : this.processesToApply)
        strBuff.append(proc + " - ");
      return (strBuff.toString() + strOgcFilter);
    }
  }

  /**
   * Add a new process and its parameters to the processes to apply for this
   * element.
   * @param name
   * @param parameters
   */
  public void addProcess(String name, Map<String, Object> parameters,
      ProcessPriority priority) {
    this.processesToApply.add(name);
    this.parameters.add(parameters);
    this.processPriorities.add(priority);
  }

  public void setEnrichments(List<ScaleMasterEnrichment> enrichments) {
    this.enrichments = enrichments;
  }

  public List<ScaleMasterEnrichment> getEnrichments() {
    return enrichments;
  }
}
