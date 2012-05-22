package fr.ign.cogit.geoxygene.api.spatial.geomprim;

public interface IOrientablePrimitive extends IPrimitive {
  /** Renvoie l'orientation de self */
  public abstract int getOrientation();
}
