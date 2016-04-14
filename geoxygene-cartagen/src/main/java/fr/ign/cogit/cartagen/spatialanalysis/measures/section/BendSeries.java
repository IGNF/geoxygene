package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class BendSeries {

  private static Logger logger = Logger.getLogger(BendSeries.class.getName());

  private ILineString geom;
  private List<IDirectPosition> inflectionPts;
  private List<Bend> bends;
  private double sigmaSmoothing = 75.0;

  public BendSeries(ILineString geom) {
    super();
    this.geom = geom;
    ILineString smoothLine = GaussianFilter.gaussianFilter(geom,
        this.sigmaSmoothing, 1);
    List<Integer> sequences = this.computeBendSequences(smoothLine);
    List<Integer> filteredSequence = this.filterBendSequences(sequences, 1);
    this.computeInflectionPoints(filteredSequence);
    this.computeBendsFromInflectionPts();
  }

  public BendSeries(ILineString geom, double sigmaSmoothing) {
    super();
    this.geom = geom;
    this.sigmaSmoothing = sigmaSmoothing;
    ILineString smoothLine = GaussianFilter.gaussianFilter(geom,
        this.sigmaSmoothing, 1);
    List<Integer> sequences = this.computeBendSequences(smoothLine);
    List<Integer> filteredSequence = this.filterBendSequences(sequences, 1);
    this.computeInflectionPoints(filteredSequence);
    this.computeBendsFromInflectionPts();
  }

  public ILineString getGeom() {
    return geom;
  }

  public void setGeom(ILineString geom) {
    this.geom = geom;
  }

  public List<IDirectPosition> getInflectionPts() {
    return inflectionPts;
  }

  public void setInflectionPts(List<IDirectPosition> inflectionPts) {
    this.inflectionPts = inflectionPts;
  }

  public List<Bend> getBends() {
    return bends;
  }

  public void setBends(List<Bend> bends) {
    this.bends = bends;
  }

  /**
   * Get all summits of the bend series.
   * @return
   */
  public Set<IDirectPosition> getAllSummits() {
    Set<IDirectPosition> summits = new HashSet<IDirectPosition>();
    for (Bend bend : getBends()) {
      summits.add(bend.getBendSummit());
    }
    return summits;
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
        angleTrigo = Angle.angleTroisPoints(listePoints.get(i),
            listePoints.get(i + 1), listePoints.get(i + 2));
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
  private void computeInflectionPoints(List<Integer> filteredSequence) {
    this.inflectionPts = new ArrayList<IDirectPosition>();
    IDirectPositionList listePoints = geom.getControlPoint();

    // Détermine les points d'inflexion sur la polyligne originale
    inflectionPts.add(listePoints.get(0));
    int positionPoint = 0;
    List<Integer> listPosition = new ArrayList<Integer>();
    listPosition.add(positionPoint);

    for (int i = 0; i < filteredSequence.size(); i++) {
      positionPoint = positionPoint + filteredSequence.get(i);
      inflectionPts.add(listePoints.get(positionPoint));
      listPosition.add(positionPoint);
    }
    inflectionPts.add(listePoints.get(listePoints.size() - 1));
    listPosition.add(listePoints.size() - 1);
    logger.fine("Liste Position = " + listPosition);

    return;
  }

  /**
   * Compute the bends of the series from inflection points: a bend is the part
   * of line between two consecutive inflection points.
   */
  private void computeBendsFromInflectionPts() {
    // first, initialise the bend list
    this.bends = new ArrayList<Bend>();
    // then, compute bends from positions
    IDirectPosition pt1 = getGeom().startPoint();
    for (IDirectPosition pt : this.inflectionPts) {
      if (pt.equals2D(pt1, 0.01))
        continue;
      ILineString subLine = CommonAlgorithmsFromCartAGen.getSubLine(getGeom(),
          pt1, pt);
      this.bends.add(new Bend(subLine));
      pt1 = pt;
    }
  }
}
