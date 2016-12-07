/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * ###### IGN / Projet Nouvelle Carte de Base ###### Title: Squareness
 * Description: Classe pour la mesure concernant la rectangularit� des b�timents
 * Author: C. Greschner Version: 1.0 Changes: 0.1 (25/10/04) : creation 0.2
 * (28/10/04) : mise au propre des commentaires 0.3 (09/12/04) : remplacement de
 * la fonction qui calcul squareness par un constructeur, mise au propre des
 * commentaires 1.0 (12/05/05) : Tidy up by Jenny Trevisan
 */

package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;

/**
 * <p>
 * Measures a building's <b>squareness</b> and returns results to : measureAble,
 * currentSquared, currentNearlySquared, currentHoles. Uses the functions
 * <i>angle3Points</i> of LibGeom and <i>ramenerAngleModuloPi</i> of IGNMath to
 * calculate the angle of a corner.<br>
 * This class is based on the function
 * <i>COGITmeas_sa_calc_angle_min_diff_a_angle_droit</i> from the Lull file
 * <i>COGIT_lib_measure</i>.
 * </p>
 * 
 * @see <a href = "../../util/LibGeom.html">LibGeom
 * @see <a href = "../../util/IGNMath.html">IGNMath
 * @author Christina Greschner
 * @version 1.0
 */

public class Squareness {

  // l'attributs de la classe
  /**
   * used as a boolean value. true if it was actually possible to calculate an
   * angle. false if it was not possible to calculate an angle.
   */
  private boolean done = false;
  /**
   * percentage of the corners that have 90�, given as a value between 0 and 1.
   */
  private double squaredCorners = 0.0;
  /**
   * percentage of the corners that have <i>almost</i> 90�, given as a value
   * between 0 and 1.
   */
  private double nearlySquaredCorners = 0.0;
  /**
   * used as a boolean value to indicate if the geometry has holes.
   */
  private boolean hasHoles = false;

  /**
   * The list of deviations to perpendicular angles.
   */
  private List<Double> deviations = new ArrayList<>();

  /**
   * <p>
   * Initialises the variables and carries out the actual measure.
   * </p>
   * 
   * @param geom Polygon of the building
   * @param flexibility Flexibility of the angle (degrees)
   * @param tolerance Tolerance of the angle (degrees)
   * @throws Exception
   */
  public Squareness(IGeometry geom, double flexibility, double tolerance)
      throws Exception {
    // Initialisation
    double flex = flexibility * Math.PI / 180;
    double tol = tolerance * Math.PI / 180;
    int nbAngles = 0;
    int nbAnglesDroits = 0;
    int nbAnglesPresqueDroits = 0;
    double deltaMin = Math.PI; // On pourrait éventuellement sortir deltaMin
                               // en
    // attribut

    // on vérifie que la géometrie est bien une surface simple ou une ligne
    // simple
    if (!(geom instanceof ILineString || geom instanceof IPolygon)) {
      throw new Exception(
          "Wrong type of geometry, getSquareness only works on simple lines or simple areas.");
    }

    ILineString outerRing = null;
    if (geom instanceof IPolygon) {
      // on récupère le contour extérieur
      outerRing = ((IPolygon) geom).exteriorLineString();
      // on récupère les contours internes (trous)
      int nbInnerRing = ((IPolygon) geom).getInterior().size();
      if (nbInnerRing > 0) {
        this.setHasHoles(true);
      }
    } else {
      outerRing = (ILineString) geom;
    }

    // on récupère le nombre des vertices
    double angle, deltaPiSur2;
    int outerNbPts = outerRing.numPoints();
    if (outerNbPts > 2) {
      // dans le cas ou la surface a suffisamment de points pour
      // calculer un angle
      // on récupère et stoche les deux premiers points
      IDirectPosition xy1 = outerRing.coord().get(0);
      IDirectPosition xy2 = outerRing.coord().get(1);

      // passage en revue des coins
      for (int i = 2; i < outerNbPts; i++) {
        IDirectPosition xy3 = outerRing.coord().get(i);

        // calcul de l'angle
        angle = Angle.angleTroisPoints(xy1, xy2, xy3).angleAPiPres()
            .getValeur();
        nbAngles = nbAngles + 1;
        deltaPiSur2 = Math.abs(angle - Math.PI);
        deviations.add(deltaPiSur2);

        // l'angle est droit
        if (deltaPiSur2 <= flex) {
          nbAnglesDroits = nbAnglesDroits + 1;
        } else if (deltaPiSur2 <= tol) {
          nbAnglesPresqueDroits = nbAnglesPresqueDroits + 1;
        }

        if (deltaPiSur2 < deltaMin) {
          deltaMin = deltaPiSur2;
        }

        // changer l'assignation des variables
        xy1 = xy2;
        xy2 = xy3;

      }

      // RESULTATS
      // on élève la flexibilité du résultat
      deltaMin = deltaMin - flex;
      // si tous les angles sont droits, on renvoie 0

      if (nbAngles == nbAnglesDroits) {
        deltaMin = 0.0;
        this.setSquaredCorners(1.0);
        this.setNearlySquaredCorners(0.0);
      } else if (nbAngles != nbAnglesDroits) {
        // on d�duit les pourcentages
        this.setSquaredCorners((nbAnglesDroits + 0.0) / (nbAngles + 0.0));
        this.setNearlySquaredCorners(
            (nbAnglesPresqueDroits + 0.0) / (nbAngles + 0.0));

      }
      this.setDone(true);

    } else {
      this.setDone(false);
      deltaMin = 0.0;
      this.setSquaredCorners(0);
      this.setNearlySquaredCorners(0);
    }
  }

  private void setDone(boolean done) {
    this.done = done;
  }

  public boolean isDone() {
    return this.done;
  }

  private void setHasHoles(boolean hasHoles) {
    this.hasHoles = hasHoles;
  }

  public boolean isHasHoles() {
    return this.hasHoles;
  }

  private void setNearlySquaredCorners(double nearlySquaredCorners) {
    this.nearlySquaredCorners = nearlySquaredCorners;
  }

  public double getNearlySquaredCorners() {
    return this.nearlySquaredCorners;
  }

  private void setSquaredCorners(double squaredCorners) {
    this.squaredCorners = squaredCorners;
  }

  public double getSquaredCorners() {
    return this.squaredCorners;
  }

  public List<Double> getDeviations() {
    return deviations;
  }

  public void setDeviations(List<Double> deviations) {
    this.deviations = deviations;
  }
}
