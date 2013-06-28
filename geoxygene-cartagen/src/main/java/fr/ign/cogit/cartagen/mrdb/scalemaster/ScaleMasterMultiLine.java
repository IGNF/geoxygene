package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.cartagen.util.Interval;

public class ScaleMasterMultiLine {

  /**
   * The {@link ScaleMaster} object {@code this} scaleline is part of.
   */
  private ScaleMaster scaleMaster;
  /**
   * The line composed of process names related to list of
   * ScaleMasterMultiElements
   */
  private Map<String, List<ScaleMasterMultiElement>> line;

  public ScaleMasterMultiLine(ScaleMaster scaleMaster,
      Map<Interval<Integer>, List<ScaleMasterMultiElement>> line) {
    super();
    this.scaleMaster = scaleMaster;
    this.scaleMaster.setMultiLine(this);
    this.line = new HashMap<String, List<ScaleMasterMultiElement>>();
    for (List<ScaleMasterMultiElement> list : line.values()) {
      for (ScaleMasterMultiElement elem : list) {
        this.addElement(elem);
      }
    }
  }

  public ScaleMasterMultiLine(ScaleMaster scaleMaster) {
    this.scaleMaster = scaleMaster;
    this.line = new HashMap<String, List<ScaleMasterMultiElement>>();
    this.scaleMaster.setMultiLine(this);
  }

  /**
   * Add a {@link ScaleMasterMultiElement} instance in {@code this} line. If no
   * interval yet correspond to the element's one, the interval is added in the
   * line.
   * @param element
   */
  public void addElement(ScaleMasterMultiElement element) {
    Interval<Integer> interval = element.getInterval();
    List<ScaleMasterMultiElement> list = this
        .getElementListFromInterval(interval);
    if (list == null)
      list = new ArrayList<ScaleMasterMultiElement>();
    list.add(element);
    Collections.sort(list);
    this.line.put(element.getProcessName(), list);
  }

  /**
   * Get the {@link ScaleMasterMultiElement} objects that are eligible for the
   * given interval, in {@code this} line.
   * @param interval
   * @return
   */
  private List<ScaleMasterMultiElement> getElementListFromInterval(
      Interval<Integer> interval) {
    List<ScaleMasterMultiElement> elements = new ArrayList<ScaleMasterMultiElement>();
    for (String proc : line.keySet()) {
      for (ScaleMasterMultiElement elem : line.get(proc)) {
        if (elem.getInterval().contains(interval))
          elements.add(elem);
      }
    }
    return elements;
  }

  /**
   * Get the {@link ScaleMasterMultiElement} objects that are eligible at the
   * given scale, in {@code this} line.
   * @param scale
   * @return
   */
  public List<ScaleMasterMultiElement> getElementsFromScale(int scale) {
    List<ScaleMasterMultiElement> elements = new ArrayList<ScaleMasterMultiElement>();
    for (String proc : line.keySet()) {
      for (ScaleMasterMultiElement elem : line.get(proc)) {
        if (elem.getInterval().contains(scale))
          elements.add(elem);
      }
    }
    return elements;
  }

}
