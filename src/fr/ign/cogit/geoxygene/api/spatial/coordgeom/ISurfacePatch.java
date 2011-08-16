package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;

public interface ISurfacePatch extends ISurface {
  /** Renvoie l'attribut interpolation. */
  public abstract String getInterpolation();

  /** Renvoie l'attribut numDerivativesOnBoundary. */
  public abstract int getNumDerivativesOnBoundary();

  // ////////////////////////////////////////////////////////////////////////////////
  // Méthodes (abstaites, implémentée dans les
  // sous-classes)////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie un GM_SurfacePatch de sens opposé. Méthode abstraite implémentée
   * dans les sous-classes
   */
  abstract public ISurfacePatch reverse();

  /**
   * Renvoie le patch de rang i. Passer nécessairement 0 en paramètre car un
   * GM_SurfacePatch ne contient qu'un patch qui est lui-meme.
   */
  public abstract ISurfacePatch getPatch(int i);

  /**
   * Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est
   * lui-meme.
   */
  public abstract void setPatch(int i, ISurfacePatch value);

  /**
   * Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est
   * lui-meme.
   */
  public abstract void addPatch(ISurfacePatch value);

  /**
   * Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est
   * lui-meme.
   */
  public abstract void addPatch(int i, ISurfacePatch value);

  /**
   * Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est
   * lui-meme.
   */
  public abstract void removePatch(ISurfacePatch value);

  /**
   * Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est
   * lui-meme.
   */
  public abstract void removePatch(int i);
}
