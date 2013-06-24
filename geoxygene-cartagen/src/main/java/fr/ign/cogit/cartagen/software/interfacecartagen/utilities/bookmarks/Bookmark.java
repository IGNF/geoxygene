/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.bookmarks;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;

/**
 * A bookmark stores how the CartAGen window is centred and zoomed on a
 * particular place of a Cartagen dataset.
 * 
 */
public class Bookmark {
  private String datasetName;
  private IEnvelope extent;
  private String name;

  /**
   * Constructor based on the three fields of the class
   * @param dataset
   * @param window
   * @param name
   */
  public Bookmark(String dataset, IEnvelope window, String name) {
    this.datasetName = dataset;
    this.extent = window;
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Bookmark)) {
      return false;
    }
    Bookmark other = (Bookmark) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    if (!this.datasetName.equals(other.datasetName)) {
      return false;
    }
    if (!this.extent.equals(other.extent)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public String getDatasetName() {
    return this.datasetName;
  }

  public void setDatasetName(String dataset) {
    this.datasetName = dataset;
  }

  public IEnvelope getExtent() {
    return this.extent;
  }

  public void setExtent(IEnvelope extent) {
    this.extent = extent;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the low x coordinate of the extent of the bookmark.
   * @return
   */
  public double xLo() {
    return this.extent.getLowerCorner().getX();
  }

  /**
   * Get the high x coordinate of the extent of the bookmark.
   * @return
   */
  public double xHi() {
    return this.extent.getUpperCorner().getX();
  }

  /**
   * Get the low y coordinate of the extent of the bookmark.
   * @return
   */
  public double yLo() {
    return this.extent.getLowerCorner().getY();
  }

  /**
   * Get the high y coordinate of the extent of the bookmark.
   * @return
   */
  public double yHi() {
    return this.extent.getUpperCorner().getY();
  }
}
