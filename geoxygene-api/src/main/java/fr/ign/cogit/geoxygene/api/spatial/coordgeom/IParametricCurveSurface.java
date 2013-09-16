package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

public interface IParametricCurveSurface extends ISurfacePatch {
  public abstract String getHorizontalCurveType();

  public abstract String getVerticalCurveType();

  /**
   * @param t
   * @return ICurve
   */
  public abstract ICurve horizontalCurve(double t);

  /**
   * @param s
   * @return ICurve
   */
  public abstract ICurve verticalCurve(double s);

  /**
   * @param s
   * @param t
   * @return IDirectPosition
   */
  public abstract IDirectPosition surface(double s, double t);

  // Implémentation d'une méthode abstraite de GM_SurfacePatch.
  @Override
  public abstract ISurfacePatch reverse();
}
