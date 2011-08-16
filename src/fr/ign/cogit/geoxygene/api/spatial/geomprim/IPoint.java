package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

public interface IPoint extends IPrimitive {
  /** Renvoie le DirectPosition du point. */
  public abstract IDirectPosition getPosition();

  /**
   * Affecte un DirectPosition au point. Le DirectPosition et le GM_Point
   * doivent avoir la même dimension.
   * @param pos DirectPosition : coordonnées du point
   */
  public abstract void setPosition(IDirectPosition pos);

  /**
   * Renvoie la liste des coordonnées, qui est constituée d'un seul
   * DirectPosition.
   */
  public abstract IDirectPositionList coord();

  public abstract Object clone();

  public abstract boolean isPoint();
}
