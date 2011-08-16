package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public interface IEnvelope {
  /** Affecte le coin superieur. */
  public abstract void setUpperCorner(IDirectPosition UpperCorner);

  /** Renvoie le coin superieur. */
  public abstract IDirectPosition getUpperCorner();

  /** Affecte le coin inferieur. */
  public abstract void setLowerCorner(IDirectPosition LowerCorner);

  /** Renvoie le coin inferieur. */
  public abstract IDirectPosition getLowerCorner();

  /** Renvoie la dimension (3D). */
  public abstract int getDimension();

  /** Renvoie la difference des X. */
  public abstract double width();

  /** Renvoie la difference des Y. */
  public abstract double length();

  /** Renvoie la difference des Z. */
  public abstract double height();

  /** Renvoie le X max. */
  public abstract double maxX();

  /** Renvoie le X min. */
  public abstract double minX();

  /** Renvoie le Y max. */
  public abstract double maxY();

  /** Renvoie le Y min. */
  public abstract double minY();

  /** Renvoie le Z max. */
  public abstract double maxZ();

  /** Renvoie le Z min. */
  public abstract double minZ();

  /** Renvoie le centre de l'enveloppe. */
  public abstract IDirectPosition center();

  /**
   * Indique si self contient le point passe en parametre, fonctionne en 2D
   * uniquement.
   * @param dp le point
   * @return
   */
  public abstract boolean contains(IDirectPosition dp);

  /**
   * Indique si self contient le point passe en parametre, fonctionne en 2D
   * uniquement.
   * @param point
   * @return
   */
  public abstract boolean contains(IPoint point);

  /**
   * Indique si self contient le point de coordonnees x,y passees en parametre
   * (2D).
   * @param x
   * @param y
   * @return
   */
  public abstract boolean contains(double x, double y);

  /**
   * Indique si self contient le point de coordonnees x,y,z passees en parametre
   * (3D).
   */
  public abstract boolean contains(double x, double y, double z);

  /** Indique si self contient entierement l'enveloppe passee en parametre. */
  public abstract boolean contains(IEnvelope env);

  /** Indique si self et l'enveloppe passee en parametre se recouvrent, en 2D. */
  public abstract boolean overlaps(IEnvelope env);

  public abstract boolean intersects(IEnvelope env);

  /**
   * Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja
   * dans l'enveloppe, ne fait rien.
   */
  public abstract void expand(IDirectPosition thePoint);

  /**
   * Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja
   * dans l'enveloppe, ne fait rien.
   */
  public abstract void expand(double x, double y);

  /**
   * Agrandit l'enveloppe pour contenir le point en entree. Si le point est deja
   * dans l'enveloppe, ne fait rien.
   */
  public abstract void expand(double x, double y, double z);

  /**
   * Agrandit l'enveloppe pour contenir l'enveloppe en entree. Si elle est deja
   * contenue, ne fait rien.
   */
  public abstract void expand(IEnvelope env);

  /** Effectue une homothetie de facteur h sur l'enveloppe. */
  public abstract void expandBy(double h);

  /** Effectue une homothetie de w sur l'axe des X et de l sur l'axe des Y. */
  public abstract void expandBy(double w, double l);

  /**
   * Effectue une homothetie de w sur l'axe des X, de l sur l'axe des Y, et de h
   * sur l'axe des Z.
   */
  public abstract void expandBy(double w, double l, double h);

  /**
   * Construit un GM_object de l'enveloppe
   * @return
   */
  public abstract IPolygon getGeom();

  /**
   * Renvoie True si l'enveloppe est vide, c'est-a-dire : les coordonnees du
   * lowerCorner sont plus grandes que celles du upperCorner.
   */
  public abstract boolean isEmpty();

  /** Clone l'enveloppe. */
  public abstract Object clone();

  /** Affiche les coordonnees */
  public abstract String toString();

  /** Renvoie la liste des DirectPosition de l'objet. */
  public abstract IDirectPositionList samplePoint();
}
