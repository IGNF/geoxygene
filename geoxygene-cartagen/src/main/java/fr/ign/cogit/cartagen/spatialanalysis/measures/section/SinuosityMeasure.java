/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Measure the sinuosity of a line string. Uses the measures and the thresholds
 * from S. Mustière's PhD. The sinuosity is characterised by textual classes.
 * @author GTouya
 * 
 */
public class SinuosityMeasure {

  private static Logger logger = Logger.getLogger(SinuosityMeasure.class
      .getName());

  private ILineString line;
  /**
   * the sigma parameter of the gaussian filter
   */
  private double sigma = 85.0;

  public enum SinuosityType {
    STRAIGHT, NOT_VERY_SINUOUS, VERY_SINUOUS, HETEROGENEOUS
  }

  public void setLine(ILineString line) {
    this.line = line;
  }

  public ILineString getLine() {
    return line;
  }

  public SinuosityMeasure(ILineString line, double sigma) {
    this.line = line;
    this.sigma = sigma;
  }

  public void setSigma(double sigma) {
    this.sigma = sigma;
  }

  public double getSigma() {
    return sigma;
  }

  public SinuosityType computeSinuosity() {
    // first compute the Gaussian Smoothing
    ILineString smoothLine = GaussianFilter.gaussianFilter(line, this
        .getSigma(), 1);

    // now compute the bend sequences of the smooth line
    List<Integer> bendSequence = computeBendSequences(smoothLine);

    // special case without inflexion point
    if (bendSequence.size() == 0)
      return SinuosityType.STRAIGHT;

    // filter the bend sequence
    List<Integer> filteredSequence = filterBendSequences(bendSequence, 1);
    // compute the inflexion points from the filtered bend sequence
    IDirectPositionList inflexionPts = computeInflexionPoints(line,
        filteredSequence);
    // finally compute the classification from the inflexion points.
    return classification(line, inflexionPts);
  }

  private SinuosityType classification(ILineString line,
      IDirectPositionList inflexionPts) {
    double baseLengthRatio = line.startPoint().distance2D(line.endPoint())
        / line.length();
    logger.fine("base/length ratio: " + baseLengthRatio);
    int bendNb = inflexionPts.size() - 1;
    logger.fine("bend number: " + bendNb);
    // rules base from S. Mustière
    if (baseLengthRatio <= 0.7) {
      if (bendNb >= 16) {
        logger.info("Sinuosity = Heterogeneous");
        return SinuosityType.HETEROGENEOUS;
      }
      if (bendNb < 16) {
        logger.info("Sinuosity = Hairpin Curves");
        return SinuosityType.VERY_SINUOUS;
      }
    }
    if (baseLengthRatio >= 0.96) {
      logger.info("Sinuosity = Null");
      return SinuosityType.STRAIGHT;
    }
    if (baseLengthRatio > 0.7 && baseLengthRatio < 0.96) {
      logger.info("Sinuosity = Low Sinuosity");
      return SinuosityType.NOT_VERY_SINUOUS;
    }
    return SinuosityType.HETEROGENEOUS;
  }

  /**
   * Méthode pour identifier les points d'inflexion dans une polyligne de
   * manière basique. Un point d'inflexion est identifié à chaque changement de
   * direction d'angles consécutifs. Renvoie une liste contenant le nombre de
   * sommet consécutifs (une séquence) avec le même sens d'angle.
   * @param smoothLine
   * @author JFGirres (refactoring GTouya)
   * @return
   */
  private List<Integer> computeBendSequences(ILineString smoothLine) {

    List<Integer> listeSequences = new ArrayList<Integer>();
    IDirectPositionList listePoints = smoothLine.getControlPoint();
    Angle angleTrigo;
    String stAngleCourant;
    String stAnglePrecedent = null;

    // Si la polyligne est inférieure à trois points, il n'y a pas d'angle
    // (pardi!!)
    if (listePoints.size() < 3) {
      logger.info("LineString inférieure à trois points");
    }

    // Sinon on identifie tous les changements de direction
    else {
      int nbSommets = 1;
      for (int i = 0; i < listePoints.size() - 2; i++) {
        angleTrigo = Angle.angleTroisPoints(listePoints.get(i), listePoints
            .get(i + 1), listePoints.get(i + 2));
        // Determine la direction de l'angle
        if (angleTrigo.getValeur() > Math.PI)
          stAngleCourant = "Tourne à Gauche";
        else
          stAngleCourant = "Tourne à Droite";
        if (i > 0) {
          // Identifie un changement de direction
          if (!(stAngleCourant.equals(stAnglePrecedent))) {
            listeSequences.add(nbSommets);
            nbSommets = 1;
          }

          // Pas de changement de direction
          if (stAngleCourant.equals(stAnglePrecedent)) {
            nbSommets = nbSommets + 1;
          }
        }
        stAnglePrecedent = stAngleCourant;
      }
    }
    logger.fine("Séquence Brute = " + listeSequences);
    return listeSequences;
  }

  /**
   * Méthode pour virer toutes les micros inflexions dans une polyligne à partir
   * d'une liste d'angles consécutifs
   * @param bendSequence
   * @param filtering
   * @return
   * @author JFGirres (refactoring GTouya)
   */
  private List<Integer> filterBendSequences(List<Integer> bendSequence,
      int filtering) {
    List<Integer> filteredSequence = new ArrayList<Integer>();
    // Filtrage des micros inflexions (1 par défaut)
    filteredSequence = bendSequence;
    if (filteredSequence.size() == 1) {
      logger.info("LineString à une seule inflexion");
    }
    if (filteredSequence.size() == 2) {
      if (filteredSequence.get(0) == filtering) {
        int filteredSummitNb = filteredSequence.get(1) + 1;
        filteredSequence.clear();
        filteredSequence.add(filteredSummitNb);
      } else if (filteredSequence.get(1) == filtering) {
        int nbSommetsFiltre = filteredSequence.get(0) + 1;
        filteredSequence.clear();
        filteredSequence.add(nbSommetsFiltre);
      }
    } else if (filteredSequence.size() > 2) {
      for (int i = 0; i < filteredSequence.size(); i++) {
        if (filteredSequence.get(i) == filtering) {
          // cas où il faut virer une micro-inflexion en début de liste
          if (i == 0) {
            int filteredSummitNb = filteredSequence.get(1) + 1;
            filteredSequence.remove(0);
            filteredSequence.set(0, filteredSummitNb);
            i = -1;
            logger.fine("Séquence Filtre = " + filteredSequence);
          }
          // cas où il faut virer une micro-inflexion en fin de liste
          else if (i == filteredSequence.size() - 1) {
            int filteredSummitNb = filteredSequence
                .get(filteredSequence.size() - 2) + 1;
            filteredSequence.remove(i);
            filteredSequence.remove(filteredSequence.size() - 1);
            filteredSequence.add(filteredSummitNb);
            logger.fine("Séquence Filtre = " + filteredSequence);
          }
          // pour tous les autres cas
          else {
            int nbSommetsFiltre = filteredSequence.get(i - 1)
                + filteredSequence.get(i + 1) + 1;
            filteredSequence.remove(i - 1);
            filteredSequence.remove(i);
            filteredSequence.set(i - 1, nbSommetsFiltre);
            i = -1;
            logger.fine("Séquence Filtre = " + filteredSequence);
          }
        }
      }
    }
    logger.fine("Séquence Filtre = " + filteredSequence);
    return filteredSequence;
  }

  /**
   * Détermine les points d'inflexions à partir de la lineString en entrée et de
   * la liste de séquence de sommets consécutifs dans la même direction.
   * @param line
   * @param filteredSequence
   * @return
   * @author JFGirres (refactoring GTouya)
   */
  private IDirectPositionList computeInflexionPoints(ILineString line,
      List<Integer> filteredSequence) {
    IDirectPositionList dplPointsInflexion = new DirectPositionList();
    IDirectPositionList listePoints = line.getControlPoint();

    // Détermine les points d'inflexion sur la polyligne originale
    dplPointsInflexion.add(listePoints.get(0));
    int positionPoint = 0;
    List<Integer> listPosition = new ArrayList<Integer>();
    listPosition.add(positionPoint);

    for (int i = 0; i < filteredSequence.size(); i++) {
      positionPoint = positionPoint + filteredSequence.get(i);
      dplPointsInflexion.add(listePoints.get(positionPoint));
      listPosition.add(positionPoint);
    }
    dplPointsInflexion.add(listePoints.get(listePoints.size() - 1));
    listPosition.add(listePoints.size() - 1);
    logger.fine("Liste Position = " + listPosition);
    return dplPointsInflexion;
  }
}
