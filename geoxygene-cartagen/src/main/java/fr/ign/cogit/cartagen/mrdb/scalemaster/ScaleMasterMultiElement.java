package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.util.Set;

import fr.ign.cogit.cartagen.util.Interval;

public class ScaleMasterMultiElement implements
    Comparable<ScaleMasterMultiElement> {
  /**
   * The name of the CartAGenDB {@code this} takes its data from.
   */
  private String dbName;

  /**
   * The multi-theme process name for this line
   */
  private String processName;

  private Set<MultiThemeParameter> params;

  private Set<ScaleMasterTheme> themes;

  /**
   * The scale interval {@code this} unit is included in.
   */
  private Interval<Integer> interval;

  public ScaleMasterMultiElement(String dbName, String processName,
      Set<MultiThemeParameter> params, Interval<Integer> interval,
      Set<ScaleMasterTheme> themes) {
    super();
    this.dbName = dbName;
    this.processName = processName;
    this.params = params;
    this.interval = interval;
    this.setThemes(themes);
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public Set<MultiThemeParameter> getParams() {
    return params;
  }

  public void setParams(Set<MultiThemeParameter> params) {
    this.params = params;
  }

  public Interval<Integer> getInterval() {
    return interval;
  }

  public void setInterval(Interval<Integer> interval) {
    this.interval = interval;
  }

  public Set<ScaleMasterTheme> getThemes() {
    return themes;
  }

  public void setThemes(Set<ScaleMasterTheme> themes) {
    this.themes = themes;
  }

  @Override
  public int compareTo(ScaleMasterMultiElement o) {
    return this.getInterval().compareTo(o.getInterval());
  }

}
