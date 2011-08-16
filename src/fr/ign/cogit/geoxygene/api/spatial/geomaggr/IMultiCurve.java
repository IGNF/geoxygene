package fr.ign.cogit.geoxygene.api.spatial.geomaggr;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;

public interface IMultiCurve<CurveType extends IOrientableCurve> extends
    IMultiPrimitive<CurveType> {
  /** Périmètre totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length et non double
  public abstract double perimeter();

  public abstract boolean isMultiCurve();
  /** Longueur totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length.
  // code dans GM_Object
  /*
   * public double length() { return SpatialQuery.length(this); }
   */
}
