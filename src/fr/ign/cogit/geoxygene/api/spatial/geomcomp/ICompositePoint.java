package fr.ign.cogit.geoxygene.api.spatial.geomcomp;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public interface ICompositePoint extends IComposite, IPoint {
  /** Renvoie le GM_Point constituant self. */
  public abstract IPoint getGenerator();

  /** Affecte le GM_Point constituant self. */
  public abstract void setGenerator(IPoint value);

  /** Renvoie 1 si un GM_Point a été affecté, 0 sinon. */
  public abstract int sizeGenerator();

  /** Renvoie le DirectPosition du point. */
  @Override
  public abstract IDirectPosition getPosition();
}
