package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

public interface IOrientableSurface extends IOrientablePrimitive {
  /** Renvoie la primitive de self */
  public abstract ISurface getPrimitive();

  /** Renvoie la primitive orientée positivement correspondant à self. */
  public abstract IOrientableSurface getPositive();

  /** Renvoie la primitive orientée négativement correspondant à self. */
  // on recalcule en dynamique la primitive de la primitive orientee
  // negativement, qui est "renversee"
  // par rapport a la primitive orientee positivement.
  public abstract IOrientableSurface getNegative();

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une
   * GM_SurfaceBoundary, c'est-à-dire un GM_Ring pour représenter l'extérieur,
   * et éventuellement des GM_Ring pour représenter les trous. ATTENTION ne
   * fonctionne que pour les surfaces composées d'un seul patch, qui est un
   * polygone.
   */
  @Override
  public abstract ISurfaceBoundary boundary();

  /** Renvoie les coordonnees de la primitive. */
  @Override
  public abstract IDirectPositionList coord();
}
