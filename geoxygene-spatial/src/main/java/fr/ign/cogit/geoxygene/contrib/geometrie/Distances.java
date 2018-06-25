/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.util.Resampler;

/**
 * Méthodes statiques de calcul de distance.
 * <p>
 * English: Computation of distances (static methods).
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 * @version 1.0
 */

public abstract class Distances {
  public static Logger logger = Logger.getLogger(Distances.class.getName());

  // Organisation du code:
  // - Distances entre points
  // - Distances entre un point et un autre type de géométrie
  // - Distances entre lignes
  // - Distances entre surfaces

  // ////////////////////////////////////////////////////////////
  // //
  // Distances entre points //
  // //
  // ////////////////////////////////////////////////////////////
  @Deprecated
  /** Distance euclidienne entre 2 points (en 2D ou 3D si les points ont un Z).
   *  @Deprecated La méthode .distance de DirectPosition fait la même chose
   * */
  public static double distance(IDirectPosition dp1, IDirectPosition dp2) {
    if (!Double.isNaN(dp1.getZ()) && !Double.isNaN(dp2.getZ())) {
      return Math.sqrt(Math.pow(dp1.getX() - dp2.getX(), 2)
          + Math.pow(dp1.getY() - dp2.getY(), 2)
          + Math.pow(dp1.getZ() - dp2.getZ(), 2));
    }
    return Math.sqrt(Math.pow(dp1.getX() - dp2.getX(), 2)
        + Math.pow(dp1.getY() - dp2.getY(), 2));
  }

  /**
   * Distance euclidienne calculée en 2 dimensions XY, même sur des objets 3D.
   * @deprecated La méthode .distance de DirectPosition fait la même chose
   * @return the 2d distance between 2 points
   */
  @Deprecated
  public static double distance2D(IDirectPosition dp1, IDirectPosition dp2) {
    return Math.sqrt(Math.pow(dp1.getX() - dp2.getX(), 2)
        + Math.pow(dp1.getY() - dp2.getY(), 2));
  }

  /**
   * Est-ce que les deux points sont distants de moins du seuil passé en
   * paramètre ? Méthode optimisée pour accélérer les requêtes spatiales.
   */
  public static boolean proche(IDirectPosition dp1, IDirectPosition dp2,
      double distance) {
    return Math.abs(dp1.getX() - dp2.getX()) <= distance
        && Math.abs(dp1.getY() - dp2.getY()) <= distance
        && dp1.distance2D(dp2) <= distance;
  }

  // ////////////////////////////////////////////////////////////
  // //
  // Distances entre un point et un autre type de géométrie //
  // //
  // ////////////////////////////////////////////////////////////

  /** Distance euclidienne du point M au segment [A,B] */
  public static double distancePointSegment(IDirectPosition M,
      IDirectPosition A, IDirectPosition B) {
    return M.distance(Operateurs.projection(M, A, B));
  }

  /**
   * Distance euclidienne d'un point P à une ligne.
   * @param point point
   * @param line ligne
   * @return Distance euclidienne d'un point P à une ligne
   */
  public static double distance(IDirectPosition point, ILineString line) {
    return Distances.distance(point, line.coord());
  }

  /**
   * Distance euclidienne d'un point P à un anneau.
   * @param point point
   * @param ring un anneau
   * @return distance euclidienne
   */
  public static double distance(IDirectPosition point, IRing ring) {
    return Distances.distance(point, ring.coord());
  }

  /**
   * @param point
   * @param surface
   * @return the distance between a point and a surface
   */
  public static double distance(IDirectPosition point,
      IOrientableSurface surface) {
    return Distances.distance(point, surface.coord());
  }

  /**
   * Distance euclidienne d'un point P à une liste de points.
   * @param point point
   * @param pointList une liste de points
   * @return distance euclidienne
   */
  public static double distance(IDirectPosition point,
      IDirectPositionList pointList) {
    double distmin = pointList.get(0).distance(point);
    for (int i = 0; i < pointList.size() - 1; i++) {
      double dist = Distances.distancePointSegment(point, pointList.get(i),
          pointList.get(i + 1));
      distmin = Math.min(dist, distmin);
    }
    return distmin;
  }

  // /////////////////////////////////////////////////////////
  // //
  // Distances entre lignes //
  // //
  // /////////////////////////////////////////////////////////

  /**
   * Approximation de la première composante de Hausdorff d'une ligne vers une
   * autre. Elle est calculee comme le maximum des distances des points
   * intermédiaires de la première ligne L1 à l'autre ligne L2.
   */
  public static double premiereComposanteHausdorff(ILineString l1,
      ILineString l2) {
    double result = 0;
    for (IDirectPosition p : l1.coord()) {
      double dist = Distances.distance(p, l2);
      // double dist = l2.distance(p.toGM_Point());
      result = Math.max(dist, result);
    }
    return result;
  }

  /**
   * Approximation (très proche) de la distance de Hausdorff entre deux lignes.
   * Elle est calculee comme le maximum des distances d'un point intermediaire
   * d'une des lignes a l'autre ligne. Dans certains cas cette definition
   * diffère de la définition theorique pure car la distance de Hausdorff ne se
   * realise pas necessairement sur un point intermediaire. Mais cela est rare
   * sur des données réelles. Cette implementation est un bon compromis entre
   * simplicité et précision.
   */
  public static double hausdorff(ILineString L1, ILineString L2) {
    return Math.max(Distances.premiereComposanteHausdorff(L1, L2),
        Distances.premiereComposanteHausdorff(L2, L1));
  }

  /**
   * Distance de Hausdorff entre un point P et une ligne L. C'est-à-dire
   * distance au point P du point intermédiaire de la ligne L le plus éloigné du
   * point P.
   */
  public static double hausdorff(ILineString l, IPoint p) {
    Iterator<IDirectPosition> itPts = l.coord().getList().iterator();
    IDirectPosition point;
    double distmax = 0, dist;

    while (itPts.hasNext()) {
      point = itPts.next();
      dist = point.distance(p.getPosition());
      if (dist > distmax) {
        distmax = dist;
      }
    }
    return distmax;
  }

  /**
   * Distance moyenne entre deux polylignes, définie comme le rapport de l'aire
   * séparant deux polylignes sur la moyenne de leurs longueurs. IMPORTANT: la
   * méthode suppose que les lignes sont orientées globalement dans le même
   * sens.
   */
  public static double distanceMoyenne(ILineString L1, ILineString L2) {
    Iterator<IDirectPosition> itPts;

    // fabrication de la surface delimitée par les lignes
    List<IDirectPosition> points = new ArrayList<IDirectPosition>();
    itPts = L1.coord().getList().iterator();
    while (itPts.hasNext()) {
      IDirectPosition pt = itPts.next();
      points.add(0, pt);
    }
    itPts = L2.coord().getList().iterator();
    while (itPts.hasNext()) {
      IDirectPosition pt = itPts.next();
      points.add(0, pt);
    }
    points.add(L1.endPoint());
    GM_Polygon poly = new GM_Polygon(new GM_LineString(points));

    return 2 * poly.area() / (L1.length() + L2.length());
  }

  /**
   * Median of the distances between each vertex of the lines and their closest
   * point on the other line. It gives a median distance between two lines.
   */
  public static double lineMedianDistance(ILineString l1, ILineString l2) {
    List<Double> distances = new ArrayList<>();

    for (IDirectPosition pt : l1.coord()) {
      distances.add(distance(pt, l2));
    }
    for (IDirectPosition pt : l2.coord()) {
      distances.add(distance(pt, l1));
    }

    Collections.sort(distances);
    int nb = distances.size();
    if ((nb % 2) == 0) {
      return distances.get(nb / 2);
    } else {
      int round = Double.valueOf(Math.ceil(nb / 2)).intValue();
      return (distances.get(round) + distances.get(round - 1)) / 2;
    }
  }

  /**
   * Similar to lineMedianDistance, but the distances are only computed from the
   * smallest line points to the longest line, to avoid biases due to a line
   * much longer than the other.
   */
  public static double lineMedianDistance2(ILineString l1, ILineString l2) {
    List<Double> distances = new ArrayList<>();
    ILineString longest = l1, other = l2;
    if (l2.length() > l1.length()) {
      longest = l2;
      other = l1;
    }

    for (IDirectPosition pt : longest.coord()) {
      distances.add(distance(pt, other));
    }

    Collections.sort(distances);
    int nb = distances.size();
    if ((nb % 2) == 0) {
      return distances.get(nb / 2);
    } else {
      int round = Double.valueOf(Math.ceil(nb / 2)).intValue();
      return (distances.get(round) + distances.get(round - 1)) / 2;
    }
  }

  /**
   * Mesure d'écart entre deux polylignes, défini comme une approximation de la
   * surface séparant les polylignes. Plus précisément, cet écart est égal à la
   * somme, pour chaque point P de L1, de (distance de P à L2) * (moyenne des
   * longueurs des segments autour de P).
   * <p>
   * NB: Ce n'est pas une distance au sens mathématique du terme, et en
   * particulier cet écart n'est pas symétrique: ecart(L1,L2) != ecart(L2,L1).
   * <p>
   * NB2: Comme cet écart dépend beaucoup de l'échantillonage de la ligne 1,
   * elle est échantillonée par défaut à 1.0m.
   * @see #ecartSurface(ILineString, ILineString, double)
   */
  public static double ecartSurface(ILineString l1, ILineString l2) {
    return Distances.ecartSurface(l1, l2, 1.0);
  }

  /**
   * Mesure d'écart entre deux polylignes, défini comme une approximation de la
   * surface séparant les polylignes. Plus précisément, cet écart est égal à la
   * somme, pour chaque point P de L1, de (distance de P à L2) * (moyenne des
   * longueurs des segments autour de P).
   * <p>
   * NB: Ce n'est pas une distance au sens mathématique du terme, et en
   * particulier cet écart n'est pas symétrique: ecart(L1,L2) != ecart(L2,L1)
   * @param l1 line
   * @param l2 line
   * @param threshold threshold used to subsample l1
   */
  public static double ecartSurface(ILineString l1, ILineString l2,
      double threshold) {
    double ecartTotal = 0;
    ILineString l = Resampler.resample(l1, threshold);
    IDirectPositionList pts = l.getControlPoint();
    for (int i = 0; i < pts.size(); i++) {
      double distPt = l2.distance(pts.get(i).toGM_Point());
      double long1 = 0;
      if (i != 0) {
        long1 = pts.get(i).distance2D(pts.get(i - 1));
      }
      double long2 = 0;
      if (i != pts.size() - 1) {
        long2 = pts.get(i).distance2D(pts.get(i + 1));
      }
      ecartTotal += distPt * (long1 + long2) / 2;
    }
    return ecartTotal;
  }

  // //////////////////////////////////////////////////////////
  // //
  // Distances entre surfaces //
  // //
  // //////////////////////////////////////////////////////////
  @SuppressWarnings("unchecked")
  private static IMultiSurface<IOrientableSurface> toMultiSurface(IGeometry geom) {
    if (geom instanceof IMultiSurface<?>) {
      return (IMultiSurface<IOrientableSurface>) geom;
    }
    List<IOrientableSurface> list = new ArrayList<IOrientableSurface>();
    list.add((IOrientableSurface) geom);
    IMultiSurface<IOrientableSurface> multiSurface = new GM_MultiSurface<IOrientableSurface>(
        (ICompositeSurface) list);
    return multiSurface;
  }

  /**
   * Distance surfacique entre deux IGeometry.
   */
  @SuppressWarnings("unchecked")
  public static double distanceSurfacique(IGeometry geom, IGeometry geom2) {
    if (geom instanceof IMultiSurface<?> || geom2 instanceof IMultiSurface<?>) {
      return Distances.distanceSurfacique(
          (IMultiSurface<IOrientableSurface>) geom,
          (IMultiSurface<IOrientableSurface>) geom2);
    }
    return Distances.distanceSurfacique((IPolygon) geom, (IPolygon) geom2);
  }

  /**
   * Distance surfacique entre deux GM_Polygon.
   * <p>
   * Définition : 1 - surface(intersection)/surface(union) Ref [Vauglin 97]
   * <p>
   * NB: renvoie 2 en cas de problème lors du calcul d'intersection avec JTS
   * (bug en particulier si les surfaces sont dégénérées ou trop complexes).
   */
  public static double distanceSurfacique(IPolygon A, IPolygon B) {
    IGeometry inter = A.intersection(B);
    if (inter == null) {
      return 2;
    }
    IGeometry union = A.union(B);
    if (union == null) {
      return 1;
    }
    return 1 - inter.area() / union.area();
  }

  /**
   * Distance surfacique entre deux IMultiSurface. Définition : 1 -
   * surface(intersection)/surface(union) Ref [Vauglin 97] NB: renvoie 2 en cas
   * de problème lors du calcul d'intersection avec JTS (bug en particulier si
   * les surfaces sont dégénérées ou trop complexes).
   */
  public static double distanceSurfacique(IMultiSurface<IOrientableSurface> A,
      IMultiSurface<IOrientableSurface> B) {
    IGeometry inter = A.intersection(B);
    // en cas de problème d'intersection avec JTS, la méthode retourne 2
    if (inter == null) {
      return 2;
    }
    IGeometry union = A.union(B);
    if (union == null) {
      return 1;
    }
    return 1 - inter.area() / union.area();
  }

  /**
   * Distance surfacique "robuste" entre deux polygones.
   * <p>
   * Il s'agit ici d'une pure bidouille pour contourner certains bugs de JTS: Si
   * JTS plante au calcul d'intersection, on filtre les surfaces avec Douglas et
   * Peucker, progressivement avec 10 seuils entre min et max. Min et Max
   * doivent être fixer donc de l'ordre de grandeur de la précision des données
   * sinon le calcul risque d'être trop faussé.
   * <p>
   * Définition : 1 - surface(intersection)/surface(union) Ref [Vauglin 97]
   * <p>
   * NB: renvoie 2 en cas de problème lors du calcul d'intersection avec JTS
   * (bug en particulier si les surfaces sont dégénérées ou trop complexes).
   */
  public static double distanceSurfaciqueRobuste(GM_Polygon A, GM_Polygon B,
      double min, double max) {
    IGeometry inter = Operateurs.intersectionRobuste(A, B, min, max);
    // en cas de problème d'intersection avec JTS, la méthode retourne 2
    if (inter == null) {
      return 2;
    }
    IGeometry union = A.union(B);
    if (union == null) {
      return 1;
    }
    return 1 - inter.area() / union.area();
  }

  /**
   * même chose que distanceSurfaciqueRobuste mais pour des IPolygon.
   */
  public static double distanceSurfaciqueRobuste(IPolygon A, IPolygon B,
      double min, double max) {
    IGeometry inter = Operateurs.intersectionRobuste(A, B, min, max);
    // en cas de problème d'intersection avec JTS, la méthode retourne 2
    if (inter == null) {
      inter = Operateurs.intersectionRobuste(A.buffer(0.1), B.buffer(0.1), min,
          max);
      if (inter == null)
        return 2;
    }
    IGeometry union = A.union(B);
    if (union == null) {
      union = A.buffer(0.1).union(B.buffer(0.1));
      if (union == null)
        return 1;
    }
    return 1 - inter.area() / union.area();
  }

  /**
   * Distance surfacique entre deux IMultiSurface. Cette méthode contourne des
   * bugs de JTS, qui sont trop nombreux sur les agrégats. En contrepartie,
   * cette méthode n'est valable que si les IPolygon composant A [resp. B] ne
   * s'intersectent pas entre elles.
   * <p>
   * Définition : 1 - surface(intersection)/surface(union)
   * <p>
   * Ref [Vauglin 97]
   * <p>
   * NB: renvoie 2 en cas de problème résiduer lors du calcul d'intersection
   * avec JTS (bug en particulier si les surfaces sont dégénérées ou trop
   * complexes).
   */
  public static double distanceSurfaciqueRobuste(
      IMultiSurface<IOrientableSurface> A, IMultiSurface<IOrientableSurface> B) {
    double inter = Distances.surfaceIntersection(A, B);
    if (inter == -1) {
      Distances.logger
          .error("Plantage JTS, renvoi 2 à la distance surfacique de deux multi_surfaces");
      return 2;
    }
    return 1 - inter / (A.area() + B.area() - inter);
  }

  /**
   * Surface de l'intersection. Cette méthode contourne des bugs de JTS, qui
   * sont trop nombreux sur les agrégats. En contrepartie, cette méthode n'est
   * valable que si les GM_Polygon composant A [resp. B] ne s'intersectent pas
   * entre elles. NB: renvoie -1 en cas de problème résiduer lors du calcul
   * d'intersection avec JTS (bug en particulier si les surfaces sont dégénérées
   * ou trop complexes).
   */
  public static double surfaceIntersection(IMultiSurface<IOrientableSurface> A,
      IMultiSurface<IOrientableSurface> B) {
    double inter = 0;
    for (IOrientableSurface surfA : A) {
      for (IOrientableSurface surfB : B) {
        if (surfB.intersection(surfA) == null) {
          Distances.logger
              .error("Plantage JTS, renvoi -1 à l'intersection de deux multi_surfaces");
          return -1;
        }
        inter = inter + surfB.intersection(surfA).area();
      }
    }
    return inter;
  }

  /**
   * Surface de l'union. Cette méthode contourne des bugs de JTS, qui sont trop
   * nombreux sur les agrégats. En contrepartie, cette méthode n'est valable que
   * si les GM_Polygon composant A [resp. B] ne s'intersectent pas entre elles.
   * NB: renvoie -1 en cas de problème résiduer lors du calcul d'intersection
   * avec JTS (bug en particulier si les surfaces sont dégénérées ou trop
   * complexes).
   */
  public static double surfaceUnion(IMultiSurface<IOrientableSurface> A,
      IMultiSurface<IOrientableSurface> B) {
    double inter = Distances.surfaceIntersection(A, B);
    if (inter == -1) {
      Distances.logger
          .error("Plantage JTS, renvoi -1 à l'union de deux 2 multi_surfaces");
      return -1;
    }
    return A.area() + B.area() - inter;
  }

  /**
   * Mesure dite "Exactitude" entre 2 surfaces. Ref : [Bel Hadj Ali 2001]
   * <p>
   * Définition : Surface(A inter B) / Surface(A)
   */
  public static double exactitude(IPolygon A, IPolygon B) {
    IGeometry inter = A.intersection(B);
    if (inter == null) {
      return 0;
    }
    return inter.area() / A.area();
  }

  /**
   * Mesure dite "Complétude" entre 2 surfaces. Ref : [Bel Hadj Ali 2001]
   * <p>
   * Définition : Surface(A inter B) / Surface(B)
   */
  public static double completude(IPolygon A, IPolygon B) {
    return Distances.exactitude(B, A);
  }

  /**
   * Mesure dite "Exactitude" entre 2 IMultiSurface. Ref : [Bel Hadj Ali 2001].
   * <p>
   * Définition : Surface(A inter B) / Surface(A)
   */
  public static double exactitude(IMultiSurface<IOrientableSurface> A,
      IMultiSurface<IOrientableSurface> B) {
    IGeometry inter = A.intersection(B);
    if (inter == null) {
      return 0;
    }
    return inter.area() / A.area();
  }

  /**
   * Mesure dite "Complétude" entre 2 IMultiSurface.
   * <p>
   * Ref : [Bel Hadj Ali 2001] Définition : Surface(A inter B) / Surface(B)
   */
  public static double completude(IMultiSurface<IOrientableSurface> A,
      IMultiSurface<IOrientableSurface> B) {
    return Distances.exactitude(B, A);
  }

  /**
   * Mesure d'association entre deux surfaces (cf. [Bel Hadj Ali 2001]). <BR>
   * <STRONG> Definition : </STRONG> associationSurfaces(A,B) = vrai si
   * <UL>
   * <LI>Surface(intersection) > min (min etant la resolution minimum des deux
   * bases)</LI>
   * <LI>ET (Surface(intersection) > surface(A) * coeff</LI>
   * <LI>OU Surface(intersection) > surface(B) * coeff )</LI>
   * </UL>
   * <BR>
   * associationSurfaces(A,B) = faux sinon.
   */
  public static boolean associationSurfaces(IGeometry A, IGeometry B,
      double min, double coeff) {
    IGeometry inter = A.intersection(B);
    if (inter == null) {
      return false;
    }
    double interArea = inter.area();
    if (interArea < min) {
      return false;
    }
    if (interArea > A.area() * coeff) {
      return true;
    }
    if (interArea > B.area() * coeff) {
      return true;
    }
    return false;
  }

  /**
   * Test d'association "robuste" entre deux surfaces (cf. [Bel Hadj Ali 2001]).
   * Il s'agit ici d'une pure bidouille pour contourner certains bugs de JTS: Si
   * JTS plante au calcul , on filtre les surfaces avec Douglas et Peucker,
   * progressivement avec 10 seuils entre min et max. Min et Max doivent être
   * fixer donc de l'ordre de grandeur de la précision des données sinon le
   * calcul risque d'être trop faussé. <BR>
   * <STRONG> Definition : </STRONG> associationSurfaces(A,B) = vrai si
   * <UL>
   * <LI>Surface(intersection) > min (min etant la resolution minimum des deux
   * bases)</LI>
   * <LI>ET (Surface(intersection) > surface(A) * coeff</LI>
   * <LI>OU Surface(intersection) > surface(B) * coeff )</LI>
   * </UL>
   * <BR>
   * associationSurfaces(A,B) = faux sinon.
   */
  public static boolean associationSurfacesRobuste(IGeometry A, IGeometry B,
      double min, double coeff, double minDouglas, double maxDouglas) {
    IGeometry inter = Operateurs.intersectionRobuste(A, B, minDouglas,
        maxDouglas);
    if (inter == null) {
      return false;
    }
    double interArea = inter.area();
    if (interArea < min) {
      return false;
    }
    if (interArea > A.area() * coeff) {
      return true;
    }
    if (interArea > B.area() * coeff) {
      return true;
    }
    return false;
  }
}
