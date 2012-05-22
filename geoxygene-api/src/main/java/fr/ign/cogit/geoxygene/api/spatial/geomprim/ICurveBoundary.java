package fr.ign.cogit.geoxygene.api.spatial.geomprim;

public interface ICurveBoundary extends IPrimitiveBoundary {
  /** Renvoie le point initial. */
  public abstract IPoint getStartPoint();

  /** Renvoie le point final. */
  public abstract IPoint getEndPoint();
}
