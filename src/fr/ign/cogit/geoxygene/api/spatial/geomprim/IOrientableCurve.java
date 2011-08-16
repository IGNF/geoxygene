package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

public interface IOrientableCurve extends IOrientablePrimitive {
  /** Renvoie la primitive de self */
  public abstract ICurve getPrimitive();

  /** Renvoie la primitive orientée positivement correspondant à self. */
  public abstract IOrientableCurve getPositive();

  /** Renvoie la primitive orientée négativement correspondant à self. */
  public abstract IOrientableCurve getNegative();

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une
   * GM_CurveBoundary, c'est-à-dire deux GM_Point.
   */
  public abstract ICurveBoundary boundary();

  /** Renvoie les coordonnees de la primitive. */
  public abstract IDirectPositionList coord();
}
