package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

public interface ICurveSegment extends ICurve {
  /** Renvoie l'attribut interpolation. */
  public abstract String getInterpolation();

  /** Renvoie l'attribut numDerivativesAtStart. */
  public abstract int getNumDerivativesAtStart();

  /** Renvoie l'attribut numDerivativeAtEnd. */
  public abstract int getNumDerivativeAtEnd();

  /** Renvoie l'attribut numDerivativeInterior. */
  public abstract int getNumDerivativeInterior();

  // ////////////////////////////////////////////////////////////////////////////////
  // Méthodes (abstaites, implémentée dans les
  // sous-classes)////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie un GM_CurveSegment de sens opposé. Méthode abstraite implémentée
   * dans les sous-classes.
   */
  abstract public ICurveSegment reverse();
}
