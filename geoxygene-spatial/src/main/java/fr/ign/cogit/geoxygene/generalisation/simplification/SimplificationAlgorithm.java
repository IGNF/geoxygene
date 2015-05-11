/**
 * @author julien Gaffuri 29 sept. 2008
 */
package fr.ign.cogit.geoxygene.generalisation.simplification;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author julien Gaffuri 29 sept. 2008
 * 
 */
public class SimplificationAlgorithm {
  private final static Logger logger = Logger
      .getLogger(SimplificationAlgorithm.class.getName());

  private static double SEUIL_COTES_PARRALLELES = 20 * Math.PI / 180;
  private static double SEUIL_COTES_ORTHOGONAUX = 20 * Math.PI / 180;

  /**
   * simplifie un polygone en supprimant les cotes trop courts
   * 
   * @param poly le polygone
   * @param seuil la longueur seuil
   * @return le polygone simplifie
   */
  public static IGeometry simplification(IPolygon poly, double seuil) {
    IPolygon poly_ = (IPolygon) poly.clone();

    if (SimplificationAlgorithm.logger.isDebugEnabled()) {
      SimplificationAlgorithm.logger
          .debug("recupere les cotes plus petits que le seuil");
    }
    ArrayList<PolygonSegment> cps = PolygonSegment.getSmallest(poly_, seuil);
    if (SimplificationAlgorithm.logger.isDebugEnabled()) {
      SimplificationAlgorithm.logger.debug("nb=" + cps.size());
    }

    // essayer de supprimer des cotes trop courts tant qu'il y en a
    while (cps.size() > 0) {

      // recupere le cote le plus court de la liste des cotes trop court
      PolygonSegment cpPlusCourt = cps.get(0);
      for (PolygonSegment cPoly : cps) {
        if (cPoly.segment.length < cpPlusCourt.segment.length) {
          cpPlusCourt = cPoly;
        }
      }

      // enleve le cote de la liste
      cps.remove(cpPlusCourt);

      // tente suppression du cote
      resultatSuppressionCotePolygone res = SimplificationAlgorithm
          .suppressionCote(poly_, cpPlusCourt);

      // si la suppression a echoué, essayer avec un autre coté
      if (!res.ok) {
        continue;
      }

      // suppression a reussi: continuer avec le resultat obtenu
      poly_ = res.poly;
      cps = PolygonSegment.getSmallest(poly_, seuil);
    }

    return poly_;
  }

  /**
   * tente de supprimmer un cote d'un polygone
   * @param poly_
   * @param cpPlusCourt
   * @return
   */
  protected static resultatSuppressionCotePolygone suppressionCote(
      IPolygon poly, PolygonSegment cotePolygone) {

    // recupere l'anneau dont on souhaite supprimer un cote
    IRing anneau;
    if (cotePolygone.ringIndex == -1) {
      anneau = poly.getExterior();
    } else {
      anneau = poly.getInterior(cotePolygone.ringIndex);
    }

    // converti l'anneau en anneau JTS
    LinearRing anneauJTS = null;
    try {
      anneauJTS = (LinearRing) AdapterFactory.toGeometry(new GeometryFactory(),
          anneau);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // recupere resultat d'essai de suppression du cote de l'anneau
    resultatSuppressionCoteLigne res = SimplificationAlgorithm.suppressionCote(
        anneauJTS, cotePolygone.segment);

    // en cas d'echec, renvoyer un echec
    if (!res.ok) {
      return new resultatSuppressionCotePolygone(null, false);
    }

    // converti le resultat JTS en geoxygene
    IRing anneau_ = null;
    try {
      anneau_ = (IRing) AdapterFactory.toGM_Object(res.lr);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // construit le polygone resultat
    IPolygon poly_ = (IPolygon) poly.clone();
    if (cotePolygone.ringIndex == -1) {
      // remplacer l'anneau externe
      poly_.setExterior(anneau_);
    } else {
      // remplacer l'anneau interne
      poly_.setInterior(cotePolygone.ringIndex, anneau_);
    }

    // tester resultat
    if (!poly_.isValid() || poly_.isEmpty()) {
      return new resultatSuppressionCotePolygone(poly_, false);
    }

    // renvoie le resultat
    return new resultatSuppressionCotePolygone(poly_, true);
  }

  /**
   * simplifie une ligne fermee en supprimant les cotes trop courts
   * 
   * @param lr la ligne fermee
   * @param seuil la longueur seuil
   * @return la ligne fermee simplifiee FIXME à virer ? (aucun appel)
   */
  public static LineString simplification(LinearRing lr, double seuil) {

    if (SimplificationAlgorithm.logger.isDebugEnabled()) {
      SimplificationAlgorithm.logger.debug("recupere les cotes trop courts, < "
          + seuil);
    }
    LinearRing lr_ = (LinearRing) lr.clone();
    ArrayList<LineStringSegment> cotesTropCourts = LineStringSegment
        .getSmallest(lr_, seuil);
    if (SimplificationAlgorithm.logger.isDebugEnabled()) {
      SimplificationAlgorithm.logger.debug("  nb=" + cotesTropCourts.size());
    }

    // tant qu'il y a des cotes trop courts, tente de les supprimer
    while (cotesTropCourts.size() > 0) {
      if (SimplificationAlgorithm.logger.isDebugEnabled()) {
        SimplificationAlgorithm.logger
            .debug("tente suppression de l'un des cotes trop court");
      }

      boolean coteSupprime = false;
      for (LineStringSegment cls : cotesTropCourts) {
        resultatSuppressionCoteLigne res = SimplificationAlgorithm
            .suppressionCote(lr_, cls);
        if (res.ok) {
          if (SimplificationAlgorithm.logger.isDebugEnabled()) {
            SimplificationAlgorithm.logger.debug("   succes");
          }
          lr_ = res.lr;
          coteSupprime = true;
          cotesTropCourts = LineStringSegment.getSmallest(lr_, seuil);
          break;
        }
      }
      if (!coteSupprime) {
        if (SimplificationAlgorithm.logger.isDebugEnabled()) {
          SimplificationAlgorithm.logger.debug("   echec");
        }
        break;
      }
    }
    return lr_;
  }

  /**
   * tente de supprimer un cote de la ligne
   * @param lr la ligne
   * @param ppcls le cote a supprimer
   * @return la ligne avec le cote supprime
   */
  public static resultatSuppressionCoteLigne suppressionCote(LinearRing lr,
      LineStringSegment ppcls) {

    Coordinate[] coord = lr.getCoordinates();

    // s'il y a moins de 3 points, sortir
    if (coord.length <= 4) {
      SimplificationAlgorithm.logger.debug("simplification de " + lr
          + " impossible: moins de trois points");
      return new resultatSuppressionCoteLigne(null, false);
    }

    // deux points du segment le plus court
    Coordinate a = coord[ppcls.index], b = coord[ppcls.index + 1];
    // les deux points avant et apres
    Coordinate a_ = ppcls.index == 0 ? coord[coord.length - 2]
        : coord[ppcls.index - 1];
    Coordinate b_ = ppcls.index + 2 == coord.length ? coord[1]
        : coord[ppcls.index + 2];

    // calcul de l'angle entre (a, a_) et (b, b_) dans l'intervalle ]-pi, pi]
    double angle = Math.atan2(b_.y - b.y, b_.x - b.x)
        - Math.atan2(a_.y - a.y, a_.x - a.x);
    if (angle <= -Math.PI) {
      angle += 2 * Math.PI;
    } else if (angle > Math.PI) {
      angle -= 2 * Math.PI;
    }

    if (Math.abs(angle) <= Math.PI / 2
        + SimplificationAlgorithm.SEUIL_COTES_ORTHOGONAUX
        && Math.abs(angle) >= Math.PI / 2
            - SimplificationAlgorithm.SEUIL_COTES_ORTHOGONAUX) {
      // cotes presque orthogonaux: les prolonger
      if (SimplificationAlgorithm.logger.isDebugEnabled()) {
        SimplificationAlgorithm.logger.debug("cotes presque orthogonaux");
      }

      // calcul de l'intersection
      double xa = a_.x - a.x, ya = a_.y - a.y;
      double xb = b_.x - b.x, yb = b_.y - b.y;
      double t = (xb * (a.y - b.y) - yb * (a.x - b.x)) / (xa * yb - ya * xb);
      Coordinate c_ = new Coordinate(a.x + t * xa, a.y + t * ya);

      // construit la nouvelle sequence de coordonnees en supprimant les deux
      // coordonnees du segment supprime
      // et en ajoutant le point construit
      Coordinate[] coord_ = new Coordinate[coord.length - 1];

      if (ppcls.index == 0) {
        coord_[0] = c_;
        for (int i = 1; i < coord.length - 2; i++) {
          coord_[i] = coord[i - 1];
        }
        coord_[coord.length - 2] = c_;
      } else {
        for (int i = 0; i < ppcls.index; i++) {
          coord_[i] = coord[i];
        }
        coord_[ppcls.index] = c_;
        for (int i = ppcls.index + 1; i < coord.length - 1; i++) {
          coord_[i] = coord[i + 1];
        }
        if (ppcls.index == coord.length - 2) {
          coord_[0] = c_;
        }

      }
      if (coord_.length <= 3) {
        return new resultatSuppressionCoteLigne(null, false);
      } else if (coord_[0].x != coord_[coord_.length - 1].x
          || coord_[0].y != coord_[coord_.length - 1].y) {
        return new resultatSuppressionCoteLigne(null, false);
      } else {
        return new resultatSuppressionCoteLigne(
            new GeometryFactory().createLinearRing(coord_), true);
      }
    }

    else if (Math.abs(angle) <= SimplificationAlgorithm.SEUIL_COTES_PARRALLELES) {
      // cotes presque paralleles
      // on est en presence d'un petit depassement
      // tente de projeter a_ et b_ respectivement sur (b,b_) et (a,a_)
      if (SimplificationAlgorithm.logger.isDebugEnabled()) {
        SimplificationAlgorithm.logger
            .debug("cotes presque paralleles: petit depassement");
      }

      if (a_ == b_) {
        return new resultatSuppressionCoteLigne(null, false);
      }

      // calcul du projete de a_ sur (b,b_)
      double aux_a = ((b.x - b_.x) * (a_.x - b_.x) + (b.y - b_.y)
          * (a_.y - b_.y))
          / ((b.x - b_.x) * (b.x - b_.x) + (b.y - b_.y) * (b.y - b_.y));
      Coordinate ca = new Coordinate(b_.x + aux_a * (b.x - b_.x), b_.y + aux_a
          * (b.y - b_.y));
      boolean app_a = (b_.x - ca.x) * (b.x - ca.x) + (b_.y - ca.y)
          * (b.y - ca.y) < 0;

      // calcul du projete de b_ sur (a,a_)
      double aux_b = ((a.x - a_.x) * (b_.x - a_.x) + (a.y - a_.y)
          * (b_.y - a_.y))
          / ((a.x - a_.x) * (a.x - a_.x) + (a.y - a_.y) * (a.y - a_.y));
      Coordinate cb = new Coordinate(a_.x + aux_b * (a.x - a_.x), a_.y + aux_b
          * (a.y - a_.y));
      boolean app_b = (a_.x - cb.x) * (a.x - cb.x) + (a_.y - cb.y)
          * (a.y - cb.y) < 0;

      // determination des deux coordonnees a garder
      Coordinate c1, c2;

      if (!app_a && !app_b) {
        // aucun des deux projetes n'appartient au segment
        // cas tres peu probable
        return new resultatSuppressionCoteLigne(null, false);
      } else if (app_a && !app_b) {
        c1 = ca;
        c2 = b_;
      } else if (!app_a && app_b) {
        c1 = a_;
        c2 = cb;
      } else {
        double da = a_.distance(a), db = b_.distance(b);
        if (da < db) {
          c1 = ca;
          c2 = b_;
        } else {
          c1 = a_;
          c2 = cb;
        }
      }

      // construit la nouvelle sequence de coordonnees en supprimant les
      // coordonnees a_, a, b et b_
      // et en les remplacant par c1 et c2
      Coordinate[] coord_ = new Coordinate[coord.length - 2];
      coord_[0] = c1;
      coord_[1] = c2;
      if (ppcls.index == 0) {
        for (int i = 2; i < coord.length - 3; i++) {
          coord_[i] = coord[i + 1];
        }
        coord_[coord.length - 3] = c1;
      } else {
        for (int i = ppcls.index + 3; i < coord.length; i++) {
          coord_[i - ppcls.index - 1] = coord[i];
        }
        for (int i = 1; i < ppcls.index - 1; i++) {
          coord_[coord.length - ppcls.index - 2 + i] = coord[i];
        }
        coord_[coord.length - 3] = c1;
      }

      if (coord_.length <= 3) {
        return new resultatSuppressionCoteLigne(null, false);
      } else if (coord_[0].x != coord_[coord_.length - 1].x
          || coord_[0].y != coord_[coord_.length - 1].y) {
        return new resultatSuppressionCoteLigne(null, false);
      } else {
        return new resultatSuppressionCoteLigne(
            new GeometryFactory().createLinearRing(coord_), true);
      }

    }

    else if (Math.abs(angle) >= Math.PI
        - SimplificationAlgorithm.SEUIL_COTES_PARRALLELES) {
      // cotes presque paralleles, petit decrochement
      // supprime les trois cotes et les remplace par un nouveau qui moyenne les
      // 3 precedents
      if (SimplificationAlgorithm.logger.isDebugEnabled()) {
        SimplificationAlgorithm.logger
            .debug("cotes presque paralleles: petit decrochement");
      }

      // calcul de la direction du nouveau cote: (a_,a)+(b,b_)
      double dx = a.x - a_.x + b_.x - b.x, dy = a.y - a_.y + b_.y - b.y;
      double n = Math.sqrt(dx * dx + dy * dy);
      dx /= n;
      dy /= n;
      // calcul du point par lequel passe le nouveau cote: le milieu de (a_,b_)
      double xm = (a_.x + b_.x) * 0.5, ym = (a_.y + b_.y) * 0.5;

      // calcul de la premiere nouvelle coordonnee: projete de a_ sur le nouveau
      // cote
      double aux1 = (a_.x - xm) * dx + (a_.y - ym) * dy;
      Coordinate ca = new Coordinate(xm + aux1 * dx, ym + aux1 * dy);

      // calcul de la deuxieme nouvelle coordonnee: projete de b_ sur le nouveau
      // cote
      double aux2 = (b_.x - xm) * dx + (b_.y - ym) * dy;
      Coordinate cb = new Coordinate(xm + aux2 * dx, ym + aux2 * dy);

      // construit la nouvelle sequence de coordonnees en supprimant les deux
      // coordonnees du segment supprime
      // et en remplacant les coordonnees de a_ et b_ par ca et cb
      Coordinate[] coord_ = new Coordinate[coord.length - 2];
      coord_[0] = ca;
      coord_[1] = cb;
      if (ppcls.index == 0) {
        for (int i = 2; i < coord.length - 3; i++) {
          coord_[i] = coord[i + 1];
        }
        coord_[coord.length - 3] = ca;
      } else {
        for (int i = ppcls.index + 3; i < coord.length; i++) {
          coord_[i - ppcls.index - 1] = coord[i];
        }
        for (int i = 1; i < ppcls.index - 1; i++) {
          coord_[coord.length - ppcls.index - 2 + i] = coord[i];
        }
        coord_[coord.length - 3] = ca;
      }

      if (coord_.length <= 3) {
        return new resultatSuppressionCoteLigne(null, false);
      } else if (coord_[0].x != coord_[coord_.length - 1].x
          || coord_[0].y != coord_[coord_.length - 1].y) {
        return new resultatSuppressionCoteLigne(null, false);
      } else {
        return new resultatSuppressionCoteLigne(
            new GeometryFactory().createLinearRing(coord_), true);
      }

    }

    else {
      // aucune simplification possible
      if (SimplificationAlgorithm.logger.isDebugEnabled()) {
        SimplificationAlgorithm.logger.debug("pas de simplification");
      }
      return new resultatSuppressionCoteLigne(null, false);
    }
  }

  public static class resultatSuppressionCoteLigne {
    public LinearRing lr = null;
    public boolean ok = false;

    public resultatSuppressionCoteLigne(LinearRing lr, boolean ok) {
      this.lr = lr;
      this.ok = ok;
    }
  }

  /**
   * @author
   */
  public static class resultatSuppressionCotePolygone {
    /**
		 */
    public IPolygon poly = null;
    public boolean ok = false;

    public resultatSuppressionCotePolygone(IPolygon poly, boolean ok) {
      this.poly = poly;
      this.ok = ok;
    }
  }

}
