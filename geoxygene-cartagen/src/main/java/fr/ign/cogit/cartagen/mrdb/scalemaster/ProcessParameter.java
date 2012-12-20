/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

/**
 * This class represents parameters for a generalisation process (algorithm or
 * more complex process). A parameter is characterised by its name, its type and
 * of course its value.
 * @author GTouya
 * 
 */
public class ProcessParameter {

  private String name;
  private Class<?> type;
  private Object value;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setType(Class<?> type) {
    this.type = type;
  }

  public Class<?> getType() {
    return type;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    ProcessParameter other = (ProcessParameter) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  public ProcessParameter(String name, Class<?> type, Object value) {
    super();
    this.name = name;
    this.type = type;
    this.value = value;
  }
}
