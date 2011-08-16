package fr.ign.cogit.geoxygene.api.spatial.geomaggr;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;

public interface IMultiSurface<GeomType extends IOrientableSurface> extends
    IMultiPrimitive<GeomType> {
  /** Aire totale. */
  public abstract double area();

  /** Périmètre totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length et non double
  public abstract double perimeter();

  /** a expliquer **/
  public abstract IMultiSurface<GeomType> homogeneise();

  public abstract boolean isMultiSurface();
}
