package fr.ign.cogit.geoxygene.api.feature;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public interface IExtraction {
  /**
   * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que
   * pour les objets persistants
   */
  public abstract int getId();

  /**
   * Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela
   * est automatique)
   */
  public abstract void setId(int Id);

  /** Renvoie une geometrie. */
  public abstract IPolygon getGeom();

  /** Affecte une geometrie. */
  public abstract void setGeom(IPolygon g);

  public abstract String getNom();

  public abstract void setNom(String S);

  /** Ne pas utiliser, necessaire au mapping OJB */
  public abstract void setDataSetID(int I);

  /** Ne pas utiliser, necessaire au mapping OJB */
  public abstract int getDataSetID();
}
