package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;

public interface IRing extends ICompositeCurve {
  /**
   * Méthode pour vérifier qu'on a un chainage, et que le point initial est bien
   * égal au point final. Surcharge de la méthode validate sur
   * GM_CompositeCurve. Renvoie TRUE si c'est le cas, FALSE sinon.
   */
  public abstract boolean validate(double tolerance);

  public abstract Object clone();
}
