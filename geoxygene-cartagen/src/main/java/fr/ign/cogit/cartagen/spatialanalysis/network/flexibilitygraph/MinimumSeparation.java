package fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;

public class MinimumSeparation {

  private Class<?> class1, class2;
  /**
   * The minimum separation threshold between features of class1 and class2 in
   * map mm.
   */
  private double minSep;

  public MinimumSeparation(Class<?> class1, Class<?> class2, double minSep) {
    super();
    this.class1 = class1;
    this.class2 = class2;
    this.minSep = minSep;
  }

  public Class<?> getClass1() {
    return class1;
  }

  public void setClass1(Class<?> class1) {
    this.class1 = class1;
  }

  public Class<?> getClass2() {
    return class2;
  }

  public void setClass2(Class<?> class2) {
    this.class2 = class2;
  }

  public double getMinSep() {
    return minSep;
  }

  public void setMinSep(double minSep) {
    this.minSep = minSep;
  }

  /**
   * Get the minimum separation value in meters.
   * @return
   */
  public double getMinSepMeters() {
    return minSep * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
  }

  @Override
  public int hashCode() {
    return class1.hashCode() * 100 + class2.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MinimumSeparation other = (MinimumSeparation) obj;
    if (class1 == null) {
      if (other.class1 != null)
        return false;
    } else if (!class1.getName().equals(other.class1.getName()))
      return false;
    if (class2 == null) {
      if (other.class2 != null)
        return false;
    } else if (!class2.getName().equals(other.class2.getName()))
      return false;
    return true;
  }

  public boolean appliesTo(INetworkSection section1, INetworkSection section2) {
    if (class1.isInstance(section1) && class2.isInstance(section2))
      return true;
    if (class1.isInstance(section2) && class2.isInstance(section1))
      return true;
    return false;
  }

}
