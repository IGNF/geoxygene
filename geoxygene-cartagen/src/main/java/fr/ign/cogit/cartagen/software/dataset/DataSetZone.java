/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class DataSetZone {

  private String name;
  private IPolygon extent;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public IPolygon getExtent() {
    return extent;
  }
  public void setExtent(IPolygon extent) {
    this.extent = extent;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((extent == null) ? 0 : extent.hashCode());
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
    DataSetZone other = (DataSetZone) obj;
    if (extent == null) {
      if (other.extent != null)
        return false;
    } else if (!extent.equals(other.extent))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
  
  public DataSetZone(String name, IPolygon extent) {
    super();
    this.name = name;
    this.extent = extent;
  }
  
  @Override
  public String toString(){return name;}
}
