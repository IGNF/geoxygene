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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public class ScaleMasterTheme {

  private String name;
  /**
   * The CartAGen Geo classes related to {@code this} scale master theme.
   */
  private Set<Class<? extends IGeneObj>> relatedClasses;

  private GeometryType geometryType;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Class<? extends IGeneObj>> getRelatedClasses() {
    return relatedClasses;
  }

  public void setRelatedClasses(Set<Class<? extends IGeneObj>> relatedClasses) {
    this.relatedClasses = relatedClasses;
  }

  public void setGeometryType(GeometryType geometryType) {
    this.geometryType = geometryType;
  }

  public GeometryType getGeometryType() {
    return geometryType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((relatedClasses == null) ? 0 : relatedClasses.hashCode());
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
    ScaleMasterTheme other = (ScaleMasterTheme) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (relatedClasses == null) {
      if (other.relatedClasses != null)
        return false;
    } else if (!relatedClasses.equals(other.relatedClasses))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return name;
  }

  public ScaleMasterTheme(String name,
      Set<Class<? extends IGeneObj>> relatedClasses, GeometryType geometryType) {
    super();
    this.name = name;
    this.relatedClasses = relatedClasses;
    this.setGeometryType(geometryType);
  }

  @SuppressWarnings("unchecked")
  public ScaleMasterTheme(String name, Object[] relatedClasses,
      GeometryType geometryType) {
    super();
    this.name = name;
    this.relatedClasses = new HashSet<Class<? extends IGeneObj>>();
    this.setGeometryType(geometryType);
    for (Object c : relatedClasses) {
      if (c instanceof Class)
        this.relatedClasses.add((Class<? extends IGeneObj>) c);
    }
  }

}
