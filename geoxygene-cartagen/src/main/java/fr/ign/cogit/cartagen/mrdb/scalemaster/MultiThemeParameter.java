package fr.ign.cogit.cartagen.mrdb.scalemaster;

public class MultiThemeParameter extends ProcessParameter {

  private String theme1, theme2;

  public MultiThemeParameter(String name, Class<?> type, Object value,
      String theme1, String theme2) {
    super(name, type, value);
    this.setTheme1(theme1);
    this.setTheme2(theme2);
  }

  public String getTheme1() {
    return theme1;
  }

  public void setTheme1(String theme1) {
    this.theme1 = theme1;
  }

  public String getTheme2() {
    return theme2;
  }

  public void setTheme2(String theme2) {
    this.theme2 = theme2;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
    MultiThemeParameter other = (MultiThemeParameter) obj;
    if (getName() == null) {
      if (other.getName() != null)
        return false;
    } else if (!getName().equals(other.getName()))
      return false;
    if (getTheme1() == null) {
      if (other.getTheme1() != null)
        return false;
    } else if (!getTheme1().equals(other.getTheme1())
        && !getTheme1().equals(other.getTheme2()))
      return false;
    if (getTheme2() == null) {
      if (other.getTheme2() != null)
        return false;
    } else if (!getTheme2().equals(other.getTheme2())
        && !getTheme2().equals(other.getTheme1()))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ProcessParameter [name=" + getName() + ", type=" + getType()
        + ", value=" + getValue() + " for " + getTheme1() + " and "
        + getTheme2() + "]";
  }

}
