package fr.ign.cogit.geoxygene.api.spatial.geomroot;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IBoundary;

public interface IGeometry {
  /** Renvoie l' identifiant du système de coordonnées de référence. */
  public abstract int getCRS();

  /** Affecte une valeur au système de coordonnées de référence. */
  public abstract void setCRS(int crs);

  /**
   * Collection de GM_Object représentant la frontière de self. Cette collection
   * d'objets a une structure de GM_Boundary, qui est un sous-type de
   * GM_Complex.
   */
  abstract public IBoundary boundary();

  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  // diverses methodes utiles
  // /////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie la liste des DirectPosition de l'objet. Méthode abstraite redéfinie
   * dans les sous-classes. Cette méthode se comporte différemment selon le type
   * d'objet géométrique.
   */
  abstract public IDirectPositionList coord();

  /** Clone l'objet. */
  public abstract Object clone();

  /** Ecrit la géométrie dans une chaine de caractere au format WKT. */
  @Override
  public abstract String toString();

  /**
   * Exporte la géométrie dans un fichier texte au format WKT. Si append =
   * false, un nouveau fichier est systematiquement cree. Si append = true, et
   * que le fichier existe deja, la geometrie est ajoutee a la fin du fichier;
   * si le fichier n'existe pas, il est cree.
   */
  public abstract void exportWkt(String path, boolean append);

  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  // methodes geometriques directement codees
  // /////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Rectangle englobant minimum de l'objet (en 2D) sous forme de GM_Envelope.
   */
  public abstract IEnvelope envelope();
  public abstract IEnvelope getEnvelope();

  /**
   * Rectangle englobant minimum de l'objet (en 2D) sous forme de GM_Polygon. Le
   * but est d'obtenir une region contenant l'objet. Tout autre implémentation
   * serait possible : le but serait de supporter des méthodes d'indexation qui
   * n'utilisent pas les rectangles minimaux englobants.
   * 
   * @return
   */
  public abstract IPolygon mbRegion();

  /**
   * Teste l'intersection stricte entre la géométrie manipulée et celle passée
   * en paramètre, i.e. l'intersection sans les cas où les géométries sont
   * simplement adjacentes (intersection = point ou ligne) ou sont contenues
   * l'une dans dans l'autre
   * 
   * @param geom GM_Object
   * @return boolean
   */
  public abstract boolean intersectsStrictement(IGeometry geom);

  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  // methodes geometriques et topologiques faisant appel a JTS
  // ////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Centre de gravité de l'objet (avec JTS). Le résultat n'est pas
   * nécessairement dans l'objet.
   */
  public abstract IDirectPosition centroid();

  /** Enveloppe convexe de l'objet (avec JTS). */
  public abstract IGeometry convexHull();

  /**
   * Calcule de buffer sur l'objet (avec JTS). Les distances negatives sont
   * acceptees (pour faire une érosion). Le nombre de segments utilisés pour
   * approximer les parties courbes du buffer est celui par défaut de JTS, i.e.
   * 8. La forme du "chapeau" (cap) utilsée est celle par défaut de JTS, i.e.
   * CAP_ROUND : une courbe.
   * 
   * @param distance distance utilisée pour le calcul du buffer
   * @return buffer sur l'objet
   * @see #buffer(double, int)
   */
  public abstract IGeometry buffer(double distance);

  /**
   * Calcule de buffer sur l'objet (avec JTS) en indiquant le nombre de segments
   * approximant la partie courbe. Les distances negatives sont acceptees (pour
   * faire une érosion). La forme du "chapeau" (cap) utilsée est celle par
   * défaut de JTS, i.e. CAP_ROUND : une courbe.
   * 
   * @param distance distance utilisée pour le calcul du buffer
   * @param nSegments nombre de segments utilisés pour approximer les parties
   *          courbes du buffer
   * @return buffer sur l'objet
   * @see #buffer(double)
   */
  public abstract IGeometry buffer(double distance, int nSegments);

  /**
   * Calcule de buffer sur l'objet (avec JTS) en indiquant le nombre de segments
   * approximant la partie courbe et les differents parametres usuels des
   * buffers.
   * 
   * @param distance
   * @param nSegments
   * @param endCapStyle
   * @param joinStyle
   * @return
   */
  public IGeometry buffer(double distance, int nSegments, int endCapStyle,
      int joinStyle);

  /**
   * Union avec l'objet passé en paramètre (avec JTS). Renvoie éventuellement un
   * aggrégat si les objets sont disjoints.
   */
  public abstract IGeometry union(IGeometry geom);

  /**
   * Intersection avec l'objet passé en paramètre (avec JTS). Renvoie un
   * GM_Aggregate vide si les objets sont disjoints.
   */
  public abstract IGeometry intersection(IGeometry geom);

  /**
   * Différence avec l'objet passé en paramètre (avec JTS). Returns a Geometry
   * representing the points making up this Geometry that do not make up "geom".
   */
  public abstract IGeometry difference(IGeometry geom);

  /**
   * Différence symétrique avec l'objet passé en paramètre (avec JTS). La
   * différence symétrique (opérateur booléan XOR) est la différence de l'union
   * avec l'intersection. Returns a set combining the points in this Geometry
   * not in other, and the points in other not in this Geometry.
   */
  public abstract IGeometry symmetricDifference(IGeometry geom);

  /**
   * Predicat topologique sur la relation d'egalite (!= equalsExact) (avec JTS).
   * Returns true if the DE-9IM intersection matrix for the two Geometrys is
   * T*F**FFF*.
   * 
   * @param geom géométrie à comparer à this
   * @return vrai si les deux géométries sont égales (if the DE-9IM intersection
   *         matrix for the two Geometrys is T*F**FFF*)
   * @see #equalsExact(IGeometry)
   * @see #equalsExact(IGeometry, double)
   */
  public abstract boolean equals(IGeometry geom);

  /**
   * This et l'objet passe en parametre appartiennent a la meme classe et ont
   * exactement les memes coordonnees dans le même ordre (avec JTS). Ce prédicat
   * est plus stricte que {@link #equals(IGeometry)}
   * 
   * @param geom géométrie à comparer à this
   * @return vrai si les deux géométries ont la même classe et sont strictement
   *         égales
   * @see #equals(IGeometry)
   * @see #equalsExact(IGeometry, double)
   */
  public abstract boolean equalsExact(IGeometry geom);

  /**
   * This et l'objet passe en parametre appartiennent a la meme classe et ont
   * exactement les memes coordonnees à une tolérance près (avec JTS) Ce
   * prédicat est plus stricte que {@link #equals(IGeometry)} et moins que
   * {@link #equalsExact(IGeometry)}
   * 
   * @param geom géométrie à comparer à this
   * @return vrai si les deux géométries ont la même classe et sont strictement
   *         égales à une tolérance près
   */
  public abstract boolean equalsExact(IGeometry geom, double tolerance);

  /**
   * Predicat topologique sur la relation de contenance (avec JTS). Returns true
   * if geom.within(this) returns true.
   */
  public abstract boolean contains(IGeometry geom);

  /**
   * Predicat topologique crosses (avec JTS). Returns true if the DE-9IM
   * intersection matrix for the two Geometrys is T*T****** (for a point and a
   * curve, a point and an area or a line and an area), 0******** (for two
   * curves) .
   */
  public abstract boolean crosses(IGeometry geom);

  /**
   * Predicat topologique sur la relation de disjonction (avec JTS). Returns
   * true if the DE-9IM intersection matrix for the two Geometrys is FF*FF****.
   */
  public abstract boolean disjoint(IGeometry geom);

  /**
   * Predicat topologique sur la relation d'interieur (avec JTS). Returns true
   * if the DE-9IM intersection matrix for the two Geometrys is T*F**F***.
   */
  public abstract boolean within(IGeometry geom);

  /**
   * Teste si la distance entre cette géométrie et geom est inférieure à la
   * distance passée en paramètre.
   */
  public abstract boolean isWithinDistance(IGeometry geom, double distance);

  /**
   * Predicat topologique sur la relation d'intersection (avec JTS). Returns
   * true if disjoint returns false.
   */
  public abstract boolean intersects(IGeometry geom);

  /**
   * Predicat topologique sur la relation de recouvrement (avec JTS). Returns
   * true if the DE-9IM intersection matrix for the two Geometrys is T*T***T**
   * (for two points or two surfaces), or 1*T***T** (for two curves) .
   */
  public abstract boolean overlaps(IGeometry geom);

  /**
   * Predicat topologique sur la relation de contact (avec JTS). Returns true if
   * the DE-9IM intersection matrix for the two Geometrys is FT*******,
   * F**T***** or F***T****.
   */
  public abstract boolean touches(IGeometry geom);

  /** Renvoie true si la geometrie est vide (avec JTS). */
  public abstract boolean isEmpty();

  /**
   * Renvoie TRUE si l'objet n'a pas de point d'auto-intersection ou
   * d'auto-tangence (avec JTS). Cette opération n'est pas applicable aux objets
   * fermés (ceux pour lesquels isCycle() = TRUE).
   */
  public abstract boolean isSimple();

  /**
   * Renvoie TRUE si la geometrie est valide au sens JTS. Utile pour debugger.
   */
  public abstract boolean isValid();

  /**
   * Distance entre this et l'objet passe en parametre (avec JTS). Returns the
   * minimum distance between this Geometry and the Geometry geom.
   */
  public abstract double distance(IGeometry geom);

  /** Aire de l'objet (avec JTS) */
  public abstract double area();

  /** Longueur de l'objet (avec JTS) */
  public abstract double length();

  /** Dimension maximale de l'objet (point 0, courbe 1, surface 2) (avec JTS). */
  public abstract int dimension();

  /** Nombre de points de l'objet (avec JTS). */
  public abstract int numPoints();

  /** Translation de l'objet (avec JTS). */
  public abstract IGeometry translate(final double tx, final double ty,
      final double tz);

  /** Returns the DE-9IM intersection matrix for the two Geometrys. */
  public abstract String relate(IGeometry geom);

  public abstract boolean isLineString();

  public abstract boolean isMultiCurve();

  public abstract boolean isPolygon();

  public abstract boolean isMultiSurface();

  public abstract boolean isPoint();
    
  public abstract int coordinateDimension();
}
