package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public interface IDirectPosition {
  /** Renvoie l' identifiant du système de coordonnées de référence. */
  public abstract int getCRS();

  /** Affecte une valeur au système de coordonnées de référence. */
  public abstract void setCRS(int crs);

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Méthodes get
  // //////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /** Renvoie le tableau des coordonnées. */
  public abstract double[] getCoordinate();

  /** Renvoie la dimension (toujours 3). */
  public abstract int getDimension();

  /** Renvoie la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=2 pour Z). */
  public abstract double getCoordinate(int i);

  /** Renvoie X (1ère coordonnee du tableau, indice 0). */
  public abstract double getX();

  /** Renvoie Y (2ième coordonnée du tableau, indice 1). */
  public abstract double getY();

  /** Renvoie Z (3ième coordonnée du tableau, indice 2). */
  public abstract double getZ();

  /**
   * Affecte les coordonnées d'un tableau des coordonnées (2D ou 3D). Si le
   * tableau passé en paramètre est 2D, la 3ième coordonnée du DirectPosition
   * vaudra NaN. Le tableau est recopié et non passé en référence.
   */
  public abstract void setCoordinate(double[] coord);

  /**
   * Affecte la position d'un point géométrique. Le point passé en paramètre
   * doit avoir la même dimension que this.
   */
  public abstract void setCoordinate(IPoint thePoint);

  /**
   * Affecte une valeur à la i-ème coordonnées (i=0 pour X, i=1 pour Y, i=2 pour
   * Z.).
   */
  public abstract void setCoordinate(int i, double x);

  /** Affecte une valeur à X et Y. */
  public abstract void setCoordinate(double x, double y);

  /** Affecte une valeur à X, Y et Z. */
  public abstract void setCoordinate(double x, double y, double z);

  /** Affecte une valeur à X (1ère coordonnée du tableau). */
  public abstract void setX(double x);

  /** Affecte une valeur à Y (2ième coordonnée du tableau). */
  public abstract void setY(double y);

  /** Affecte une valeur à Z (3ième coordonnée du tableau). */
  public abstract void setZ(double z);

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Méthodes move
  // /////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Déplace le point suivant toutes les dimensions. Le point passé en paramètre
   * doit avoir la même dimension que this.
   */
  public abstract void move(IDirectPosition offsetPoint);

  /** Déplace le point suivant X et Y. */
  public abstract void move(double offsetX, double offsetY);

  /**
   * Déplace le point suivant X, Y et Z.
   */
  public abstract void move(double offsetX, double offsetY, double offsetZ);

  /**
   * the orientation betwwen -Pi and Pi toward an other point
   * @param dp
   */
  public abstract double orientation(IDirectPosition dp);

  public abstract double[] minus(DirectPosition p);

  public abstract double[] minus(IDirectPosition p2, double factor);

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Méthode equals
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public abstract boolean equals(Object o);

  /**
   * /** Indique si self et le point passé en paramètre sont égaux. Si les 2
   * points ont une troisième dimension affectée, on teste cette dimension.
   * @param pt un point
   * @return vrai si le point this est égal au point passé en paramètre
   * @see #equals(Object)
   * @see #equals(IDirectPosition, double)
   * @see #equals2D(IDirectPosition, double)
   */
  public abstract boolean equals(IDirectPosition pt);

  /**
   * Indique si self et le point passé en paramètre sont égaux, à une tolérance
   * près. Si les 2 points ont une troisième dimension affectée, on teste cette
   * dimension. Tolérance est un double qui doit être > 0.
   * @param pt un point
   * @param tolerance tolérance entre this et le point passé en paramètre
   * @return vrai si le point this est égal au point passé en paramètre à la
   *         tolérance près
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals2D(IDirectPosition, double)
   */
  public abstract boolean equals(IDirectPosition pt, double tolerance);

  /**
   * Indique si self et le point passé en paramètre sont égaux, à une tolérance
   * près. La comparaison est effectuée en 2D, i.e. la troisième dimension est
   * ignorée. Tolérance est un double qui doit être > 0.
   * @param pt un point
   * @param tolerance tolérance entre this et le point passé en paramètre
   * @return vrai si le point this est égal au point passé en paramètre à la
   *         tolérance près
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals(IDirectPosition, double)
   */
  public abstract boolean equals2D(IDirectPosition pt, double tolerance);

  /**
   * Indique si self et le point passé en paramètre sont égaux. La comparaison
   * est effectuée en 2D, i.e. la troisième dimension est ignorée.
   * @param pt un point
   * @return vrai si le point this est égal au point passé en paramètre
   * @see #equals(Object)
   * @see #equals(IDirectPosition)
   * @see #equals(IDirectPosition, double)
   * @see #equals2D(IDirectPosition, double)
   */
  public abstract boolean equals2D(IDirectPosition pt);

  /**
   * Calcul de la distance entre deux directPosition
   * @param d
   * @return
   */
  public abstract double distance(IDirectPosition d);

  /**
   * Calcul de la distance 2D entre deux directPosition
   * @param d a DirectPosition
   * @return the distance between this DirectPosition and <code>d</code>
   */
  public abstract double distance2D(IDirectPosition d);

  /** Clone le point. */
  public abstract Object clone();

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Méthode toGM_Point
  // ////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /** Créée un GM_Point à partir de this. */
  public abstract IPoint toGM_Point();

  // ////////////////////////////////////////////////////////////////////////////////////////
  // Méthode d'affichage
  // ///////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////
  /** Affiche les coordonnées du point (2D et 3D). */
  @Override
  public abstract String toString();
}
