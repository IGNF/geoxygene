package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.List;

public interface ISolidBoundary extends IPrimitiveBoundary {
  /** Renvoie le shell extérieur. */
  public abstract IShell getExterior();

  /** Renvoie 1 si un shell extérieur a été affecté, 0 sinon. */
  public abstract int sizeExterior();

  /** Renvoie la liste des shells intérieurs */
  public abstract List<IShell> getInterior();

  /** Renvoie le shell intérieur de rang i. */
  public abstract IShell getInterior(int i);

  /** Renvoie le nombre de shells intérieurs. */
  public abstract int sizeInterior();
}
