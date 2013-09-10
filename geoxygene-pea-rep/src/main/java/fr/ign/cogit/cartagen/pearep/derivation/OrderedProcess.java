/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.geoxygene.filter.Filter;

public class OrderedProcess implements Comparable<OrderedProcess> {

  private ProcessPriority priority;
  private boolean filter;
  private Object process;

  public OrderedProcess(ProcessPriority priority, Object process) {
    super();
    this.priority = priority;
    this.process = process;
    this.filter = false;
    if (process instanceof Filter)
      this.filter = true;
  }

  public ProcessPriority getPriority() {
    return priority;
  }

  public void setPriority(ProcessPriority priority) {
    this.priority = priority;
  }

  public boolean isFilter() {
    return filter;
  }

  public void setFilter(boolean filter) {
    this.filter = filter;
  }

  public Object getProcess() {
    return process;
  }

  public void setProcess(Object process) {
    this.process = process;
  }

  @Override
  public int compareTo(OrderedProcess o) {
    if (this.priority.ordinal() > o.priority.ordinal())
      return (this.priority.ordinal() - o.priority.ordinal()) * 2;
    if (o.priority.ordinal() > this.priority.ordinal())
      return (this.priority.ordinal() - o.priority.ordinal()) * 2;
    if (this.priority.equals(o.priority) && this.filter)
      return 1;
    if (this.priority.equals(o.priority) && o.filter)
      return -1;
    return 0;
  }

}
