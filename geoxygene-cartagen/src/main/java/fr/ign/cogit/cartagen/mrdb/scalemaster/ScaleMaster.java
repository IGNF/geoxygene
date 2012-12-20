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
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.mrdb.MRDBPointOfView;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.util.Interval;

/**
 * A ScaleMaster is a set of scale lines (timelines where time is replaced by
 * scale), inspired from the ScaleMaster of Brewer & Buttenfield (2007, 2009).
 * The difference is that, here, the scale lines also contain information on the
 * generalisation processes (with parameters) to apply on the layer in order to
 * make it legible. On the other hand, this class do not contain any display
 * information like symbol widths.
 * @author GTouya
 * 
 */
public class ScaleMaster {

  /**
   * The name of the Scale Master
   */
  private String name;

  /**
   * The scale lines of {@code this} {@link ScaleMaster}.
   */
  private Set<ScaleLine> scaleLines;

  /**
   * The point of view adopted for {@code this} {@link ScaleMaster}
   */
  private MRDBPointOfView pointOfView;

  /**
   * The global range of {@code this} {@link ScaleMaster}: all scale lines use
   * globalRange as the bounds for their own ranges.
   */
  private Interval<Integer> globalRange;

  /**
   * The databases used as initial data by {@code this} {@link ScaleMaster} i.e.
   * data in each {@link ScaleLine} interval comes from one of these databases.
   */
  private CartAGenDB databases;

  private AtomicInteger lineCounter = new AtomicInteger();

  /**
   * Default constructor.
   */
  public ScaleMaster() {
    this.scaleLines = new HashSet<ScaleLine>();
  }

  public Set<ScaleLine> getScaleLines() {
    return scaleLines;
  }

  public void setScaleLines(Set<ScaleLine> scaleLines) {
    this.scaleLines = scaleLines;
  }

  public MRDBPointOfView getPointOfView() {
    return pointOfView;
  }

  public void setPointOfView(MRDBPointOfView pointOfView) {
    this.pointOfView = pointOfView;
  }

  public Interval<Integer> getGlobalRange() {
    return globalRange;
  }

  public void setGlobalRange(Interval<Integer> globalRange) {
    this.globalRange = globalRange;
  }

  public CartAGenDB getDatabases() {
    return databases;
  }

  public void setDatabases(CartAGenDB databases) {
    this.databases = databases;
  }

  @Override
  public String toString() {
    return name + " for " + pointOfView.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((databases == null) ? 0 : databases.hashCode());
    result = prime * result
        + ((globalRange == null) ? 0 : globalRange.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((pointOfView == null) ? 0 : pointOfView.hashCode());
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
    ScaleMaster other = (ScaleMaster) obj;
    if (databases == null) {
      if (other.databases != null)
        return false;
    } else if (!databases.equals(other.databases))
      return false;
    if (globalRange == null) {
      if (other.globalRange != null)
        return false;
    } else if (!globalRange.equals(other.globalRange))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (pointOfView == null) {
      if (other.pointOfView != null)
        return false;
    } else if (!pointOfView.equals(other.pointOfView))
      return false;
    return true;
  }

  public int newLineId() {
    return lineCounter.getAndIncrement();
  }
}
