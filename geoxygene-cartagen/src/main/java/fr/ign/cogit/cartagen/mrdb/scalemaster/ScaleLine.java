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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.cartagen.util.Interval;

public class ScaleLine {

  /**
   * The data theme described by {@code this} scaleline of the ScaleMaster.
   */
  private ScaleMasterTheme theme;
  /**
   * The {@link ScaleMaster} object {@code this} scaleline is part of.
   */
  private ScaleMaster scaleMaster;
  /**
   * The line composed of intervals related to sets of ScaleMasterUnits
   */
  private Map<Interval<Integer>, List<ScaleMasterElement>> line;

  private int id;

  public void setTheme(ScaleMasterTheme theme) {
    this.theme = theme;
  }

  public ScaleMasterTheme getTheme() {
    return theme;
  }

  public void setScaleMaster(ScaleMaster scaleMaster) {
    this.scaleMaster = scaleMaster;
  }

  public ScaleMaster getScaleMaster() {
    return scaleMaster;
  }

  public void setLine(Map<Interval<Integer>, List<ScaleMasterElement>> line) {
    this.line = line;
  }

  public Map<Interval<Integer>, List<ScaleMasterElement>> getLine() {
    return line;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScaleLine other = (ScaleLine) obj;
    if (line == null) {
      if (other.line != null)
        return false;
    } else if (!line.equals(other.line))
      return false;
    if (scaleMaster == null) {
      if (other.scaleMaster != null)
        return false;
    } else if (!scaleMaster.equals(other.scaleMaster))
      return false;
    if (theme == null) {
      if (other.theme != null)
        return false;
    } else if (!theme.equals(other.theme))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return theme.toString() + " in " + scaleMaster.toString();
  }

  public ScaleLine(ScaleMasterTheme theme, ScaleMaster scaleMaster,
      Map<Interval<Integer>, List<ScaleMasterElement>> line) {
    super();
    this.theme = theme;
    this.scaleMaster = scaleMaster;
    this.line = new HashMap<Interval<Integer>, List<ScaleMasterElement>>();
    this.id = scaleMaster.newLineId();
    for (List<ScaleMasterElement> list : line.values()) {
      for (ScaleMasterElement elem : list) {
        elem.setScaleLine(this);
        this.addElement(elem);
      }
    }
  }

  public ScaleLine(ScaleMaster scaleMaster, ScaleMasterTheme theme) {
    this.theme = theme;
    this.scaleMaster = scaleMaster;
    this.line = new HashMap<Interval<Integer>, List<ScaleMasterElement>>();
    this.id = scaleMaster.newLineId();
    this.scaleMaster.getScaleLines().add(this);
  }

  /**
   * Add a {@link ScaleMasterElement} instance in {@code this} line. If no
   * interval yet correspond to the element's one, the interval is added in the
   * line.
   * @param element
   */
  public void addElement(ScaleMasterElement element) {
    Interval<Integer> interval = element.getInterval();
    List<ScaleMasterElement> list = this.getElementListFromInterval(interval);
    if (list == null)
      list = new ArrayList<ScaleMasterElement>();
    list.add(element);
    this.line.put(element.getInterval(), list);
    element.setScaleLine(this);
  }

  /**
   * Get the current list of elements related to an interval in {@code this}.
   * @param interval
   * @return
   */
  private List<ScaleMasterElement> getElementListFromInterval(
      Interval<Integer> interval) {
    for (Interval<Integer> interv : line.keySet()) {
      if (interv.equals(interval))
        return line.get(interv);
    }
    return null;
  }

  /**
   * Get the first {@link ScaleMasterElement} object that is eligible at the
   * given scale, in {@code this} line.
   * @param scale
   * @return
   */
  public ScaleMasterElement getElementFromScale(int scale) {
    for (Interval<Integer> interval : this.line.keySet()) {
      if (scale > interval.minimum() && scale <= interval.maximum())
        return this.line.get(interval).get(0);
    }
    return null;
  }
}
