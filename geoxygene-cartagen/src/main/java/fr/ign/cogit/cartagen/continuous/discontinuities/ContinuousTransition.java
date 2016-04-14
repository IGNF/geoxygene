/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.discontinuities;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Considering continuous or piecewise generalisation, a continuous transition
 * represents one step in the discretization of the continuous/piecewise
 * representations. A {@link ContinuousTransition} instance is made of a scale
 * interval and two geometries representing a feature at the end values of the
 * scale interval.
 * @author GTouya
 * 
 */
public class ContinuousTransition {

  /**
   * the endings of the scale interval, where 10000.0 stands for the 1:10k
   * scale, and the order starts from the largest to the smallest scale.
   */
  private double downScale, upScale;

  private IGeometry downGeom, upGeom;

  public ContinuousTransition(double downScale, double upScale,
      IGeometry downGeom, IGeometry upGeom) {
    super();
    this.downScale = downScale;
    this.upScale = upScale;
    this.downGeom = downGeom;
    this.upGeom = upGeom;
  }

  public double getDownScale() {
    return downScale;
  }

  public void setDownScale(double downScale) {
    this.downScale = downScale;
  }

  public double getUpScale() {
    return upScale;
  }

  public void setUpScale(double upScale) {
    this.upScale = upScale;
  }

  public IGeometry getDownGeom() {
    return downGeom;
  }

  public void setDownGeom(IGeometry downGeom) {
    this.downGeom = downGeom;
  }

  public IGeometry getUpGeom() {
    return upGeom;
  }

  public void setUpGeom(IGeometry upGeom) {
    this.upGeom = upGeom;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((downGeom == null) ? 0 : downGeom.hashCode());
    long temp;
    temp = Double.doubleToLongBits(downScale);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((upGeom == null) ? 0 : upGeom.hashCode());
    temp = Double.doubleToLongBits(upScale);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    ContinuousTransition other = (ContinuousTransition) obj;
    if (downGeom == null) {
      if (other.downGeom != null)
        return false;
    } else if (!downGeom.equals(other.downGeom))
      return false;
    if (Double.doubleToLongBits(downScale) != Double
        .doubleToLongBits(other.downScale))
      return false;
    if (upGeom == null) {
      if (other.upGeom != null)
        return false;
    } else if (!upGeom.equals(other.upGeom))
      return false;
    if (Double.doubleToLongBits(upScale) != Double
        .doubleToLongBits(other.upScale))
      return false;
    return true;
  }

}
