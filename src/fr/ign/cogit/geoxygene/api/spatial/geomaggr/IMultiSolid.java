package fr.ign.cogit.geoxygene.api.spatial.geomaggr;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;

public interface IMultiSolid<SolidType extends ISolid> extends
    IMultiPrimitive<SolidType> {
  /**
   * NON IMPLEMENTE (renvoie 0.0). Volume total.
   */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Volume et non double
  public abstract double volume();

  /**
   * NON IMPLEMENTE (renvoie 0.0). Aire totale.
   */
  public abstract double area();
}
