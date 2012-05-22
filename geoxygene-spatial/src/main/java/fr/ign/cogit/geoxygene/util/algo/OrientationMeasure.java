package fr.ign.cogit.geoxygene.util.algo;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Orientation measures For the sides orientation measure, see [Duchene et al
 * 2003]
 * 
 * @author JGaffuri
 * 
 */
public class OrientationMeasure {
  private static Logger logger = Logger.getLogger(OrientationMeasure.class
      .getName());

  // nombre d'orientations testees dans l'intervalle [0, Pi/2[ pour le calcul de
  // l'orientation moyenne des cotes
  private static int NB_ORIENTATIONS_TESTEES = 40;

  public static int getNB_ORIENTATIONS_TESTEES() {
    return OrientationMeasure.NB_ORIENTATIONS_TESTEES;
  }

  private Geometry geom = null;

  public OrientationMeasure(Geometry geom) {
    this.geom = geom;
  }

  public OrientationMeasure(IGeometry geomGeox) {
    try {
      this.geom = AdapterFactory.toGeometry(new GeometryFactory(), geomGeox);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private double[] contributionsCotesOrientation = null;

  public double[] getContributionsCotesOrientation() {
    if (this.contributionsCotesOrientation == null) {
      this.calculerContributionsCotesOrientation((Polygon) this.geom);
    }
    return this.contributionsCotesOrientation;
  }

  private double orientationCotes = -999.9;
  private double contributionMax = -999.9;

  /**
   * @return The sides orientation measure (see [Duchene et al 2003])
   */
  public double getSidesOrientation() {
    if (this.orientationCotes == -999.9) {
      this.calculerOrientationCote();
    }
    return this.orientationCotes;
  }

  private double indicateurOrientationCote = -999.9;

  public double getSidesOrientationIndicator() {
    if (this.indicateurOrientationCote == -999.9) {
      this.calculerIndicateurOrientationCote();
    }
    return this.indicateurOrientationCote;
  }

  // orientation d'une geometrie (en radian entre 0 et Pi, par rapport a l'axe
  // Ox)
  // c'est l'orientation du PPRE
  // renvoit 999.9 si le PPRE n'est pas defini, ou s'il est carre
  public double getGeneralOrientation() {

    // calcul du PPRE
    Polygon ppre = SmallestSurroundingRectangleComputation.getSSR(this.geom);

    if (ppre == null) {
      return 999.9;
    }

    // recupere le plus long cote
    Coordinate[] coords = ppre.getCoordinates();
    double lg1 = coords[0].distance(coords[1]);
    double lg2 = coords[1].distance(coords[2]);
    if (lg1 == lg2) {
      return 999.9;
    }

    // l'orientation est suivant c1,c2
    Coordinate c1, c2;
    if (lg1 > lg2) {
      c1 = coords[0];
      c2 = coords[1];
    } else {
      c1 = coords[1];
      c2 = coords[2];
    }

    // calcul de l'orientation du plus long cote
    double angle = Math.atan((c1.y - c2.y) / (c1.x - c2.x));
    if (angle < 0) {
      angle += Math.PI;
    }
    return angle;
  }

  // orientation des cotes d'un polygone (en radian, dans l'intervalle [0,
  // Pi/2[, par rapport a l'axe Ox)
  public void calculerOrientationCote() {

    // calcul des contributions des cotes
    if (this.contributionsCotesOrientation == null) {
      this.calculerContributionsCotesOrientation();
    }

    if (OrientationMeasure.logger.isTraceEnabled()) {
      OrientationMeasure.logger.trace("contributions:");
      String st = "";
      for (int i = 1; i < this.contributionsCotesOrientation.length; i++) {
        st += (int) this.contributionsCotesOrientation[i] + "  ";
      }
      OrientationMeasure.logger.trace(st);
    }

    // recupere l'index de la contribution maximale
    int iMax = 0;
    this.contributionMax = this.contributionsCotesOrientation[0];
    for (int i = 1; i < this.contributionsCotesOrientation.length; i++) {
      if (this.contributionsCotesOrientation[i] > this.contributionMax) {
        this.contributionMax = this.contributionsCotesOrientation[i];
        iMax = i;
      }
    }

    // renvoie l'angle correspondant a l'index max
    this.orientationCotes = 0.5 * Math.PI * iMax
        / OrientationMeasure.NB_ORIENTATIONS_TESTEES;
  }

  public void calculerContributionsCotesOrientation() {
    if (this.geom instanceof Polygon) {
      this.calculerContributionsCotesOrientation((Polygon) this.geom);
    } else if (this.geom instanceof LineString) {
      this.calculerContributionsCotesOrientation((LineString) this.geom);
    } else {
      OrientationMeasure.logger
          .warn("attention: calcul de l'orientation de cotes non permise pour geometrie "
              + this.geom);
      this.orientationCotes = -999.9;
      return;
    }
  }

  public void calculerContributionsCotesOrientation(Polygon poly) {
    if (OrientationMeasure.logger.isDebugEnabled()) {
      OrientationMeasure.logger
          .debug("calcul des contributions des cotes a l'orientation moyenne des cotes de "
              + poly);
    }

    // initialise la table des contributions
    this.contributionsCotesOrientation = new double[OrientationMeasure.NB_ORIENTATIONS_TESTEES];
    for (int i = 0; i < OrientationMeasure.NB_ORIENTATIONS_TESTEES; i++) {
      this.contributionsCotesOrientation[i] = 0.0;
    }

    // ajout des contributions des cotes de l'enveloppe exterieure
    this.ajouterContribution(poly.getExteriorRing());

    // ajout des contributions des cotes des trous
    for (int i = 0; i < poly.getNumInteriorRing(); i++) {
      this.ajouterContribution(poly.getInteriorRingN(i));
    }
  }

  public void calculerContributionsCotesOrientation(LineString ls) {
    if (OrientationMeasure.logger.isDebugEnabled()) {
      OrientationMeasure.logger
          .debug("calcul des contributions des cotes a l'orientation moyenne des cotes de "
              + ls);
    }

    // initialise la table des contributions
    this.contributionsCotesOrientation = new double[OrientationMeasure.NB_ORIENTATIONS_TESTEES];
    for (int i = 0; i < OrientationMeasure.NB_ORIENTATIONS_TESTEES; i++) {
      this.contributionsCotesOrientation[i] = 0.0;
    }

    // ajout des contributions des cotes de l'enveloppe exterieure
    this.ajouterContribution(ls);
  }

  // calcule les contributions de chaque mur a chaque orientation testee
  // chaque cote contribue proportionellement à sa longueur et à son écart à
  // l'orientation testés
  private void ajouterContribution(LineString ls) {
    double orientation, lg, delta;
    int index;

    // parcours des cotes pour calculer la contribution de chacun
    Coordinate[] coord = ls.getCoordinates();
    Coordinate c1 = coord[0], c2;
    double pasOrientation = Math.PI * 0.5
        / OrientationMeasure.NB_ORIENTATIONS_TESTEES;
    for (int i = 1; i < coord.length; i++) {
      c2 = coord[i];

      if (OrientationMeasure.logger.isTraceEnabled()) {
        OrientationMeasure.logger.trace("contribution de cote (" + c1 + ", "
            + c2 + ")");
      }

      // calcul de l'orientation à PI/2 pres entre c1 et c2
      if (c1.x == c2.x) {
        orientation = 0.0;
      } else {
        orientation = Math.atan(((c1.y - c2.y) / (c1.x - c2.x)));
      }
      if (orientation < 0) {
        orientation += 0.5 * Math.PI;
      }
      if (OrientationMeasure.logger.isTraceEnabled()) {
        OrientationMeasure.logger.trace("   orientation (en deg): "
            + orientation * 180 / Math.PI);
      }

      // calcul du plus petit index d'angle pour lesquels le cote contribue
      index = (int) (orientation / pasOrientation);
      if (OrientationMeasure.logger.isTraceEnabled()) {
        OrientationMeasure.logger.trace("   index: " + index);
      }

      // ajout des contributions
      lg = c1.distance(c2);
      delta = orientation / pasOrientation - index;
      this.contributionsCotesOrientation[index] += lg * (1 - delta);
      if (index + 1 == OrientationMeasure.NB_ORIENTATIONS_TESTEES) {
        this.contributionsCotesOrientation[0] += lg * delta;
      } else {
        this.contributionsCotesOrientation[index + 1] += lg * delta;
      }

      // et au suivant
      c1 = c2;
    }
  }

  private void calculerIndicateurOrientationCote() {
    if (this.orientationCotes == -999.9) {
      this.calculerOrientationCote();
    }

    // perimetre
    double perimetre = ((Polygon) this.geom).getExteriorRing().getLength();

    this.indicateurOrientationCote = this.contributionMax / perimetre;
  }

  /**
   * Computes the orientation of line geometry at a vertex of the line.
   * 
   * @param line the line on which orientation is computed
   * @param point thr point where orientation is computed
   * @return an Angle object corresponding to the absolute orientation [0,2Pi]
   * @author GTouya
   */
  public static Angle lineAbsoluteOrientation(ILineString line,
      IDirectPosition point) {

    // the first point of the angle is the central point staggered in the
    // abscise axis
    DirectPosition v1 = new DirectPosition(point.getX() + 20.0, point.getY());

    // then get the third point in the line geometry
    int nbVert = line.numPoints();
    DirectPosition coordIni = (DirectPosition) line.startPoint();
    DirectPosition v2 = null;
    // if nbVert > 2, get the second vertex in geometry
    if (nbVert > 2) {
      if (coordIni.equals(point)) {
        v2 = (DirectPosition) line.coord().get(2);
      } else {
        v2 = (DirectPosition) line.coord().get(nbVert - 3);
      }
    } else {
      // get the first vertex on geometry
      if (coordIni.equals(point)) {
        v2 = (DirectPosition) line.coord().get(1);
      } else {
        v2 = (DirectPosition) line.coord().get(nbVert - 2);
      }
    }

    // now, compute interAngle between geom and geomFoll
    return Angle.angleTroisPoints(v1, point, v2);
    // return Angle.angleTroisPoints(point,v1,v2); My Test
  }

}
