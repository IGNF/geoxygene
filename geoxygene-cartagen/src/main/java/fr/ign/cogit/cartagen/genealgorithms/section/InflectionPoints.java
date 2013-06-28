/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.genealgorithms.section;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * 
 * @author K. Jaara 18 Avril 2011 des portions de code sont écrits par J. Girras
 * 
 */

public class InflectionPoints {
  private static Logger logger = Logger.getLogger(InflectionPoints.class
      .getName());

  /**
   * Linear section on which gaussian smoothing is performed
   */
  private final ILineString geom;
  // DirectPositionList dplPtsInflexionVirages=new DirectPositionList();
  private DirectPositionList jddPtsInflexion = new DirectPositionList();

  // DirectPositionList jddPtsInflexionLsInitiale = new DirectPositionList();

  /**
   * 
   * Sigma
   * 
   */

  /**
   * Data resolution: defined in the generalisation parameters
   */

  /**
   * Constructor
   * @param geoObj
   */

  public InflectionPoints(ILineString geom) {
    this.geom = geom;
  }

  public DirectPositionList getPtsInflexion() {
    return this.jddPtsInflexion;
  }

  public void setJddPtsInflexion(DirectPositionList jddPtsInflexion) {
    this.jddPtsInflexion = jddPtsInflexion;
  }

  /**
   * Computation of the gaussian smoothing on geoObj
   * @param isPersistantObject : determines if the geographic object is
   *          persistent in the database or just exists to apply the algorithm
   */

  public void compute2() {

    ILineString lsInitiale = this.geom;
    ILineString lsLisse = Operateurs.resampling(lsInitiale, 1);
    // ILineString lsLisse = GaussianFilter.gaussianFilter(lsInitiale, 10, 1);
    // ILineString
    // lsLisse=LissageGaussian.AppliquerLissageGaussien((GM_LineString)
    // lsInitiale, 10, 1, false);

    logger.debug("Gaussian Smoothing of : " + lsInitiale);

    // On determine les séquences de virages
    List<Integer> listSequence = determineSequences(lsLisse);

    // On applique le filtre gaussien

    // On crée une collection de points qui servira à découper tous les
    // virages

    if (listSequence.size() > 0) {
      List<Integer> listSequenceFiltre = filtrageSequences(listSequence, 1);
      DirectPositionList dplPointsInflexionLsLissee = determinePointsInflexion(
          lsInitiale, listSequenceFiltre);

      for (IDirectPosition directPosition : dplPointsInflexionLsLissee) {
        this.jddPtsInflexion.add(directPosition.toGM_Point().getPosition());
        // dplPtsInflexionVirages.add(directPosition);
      }

      // jddPtsInflexion.addAll(jddPtsInflexionLsInitiale);

    }
    // dplPtsInflexionVirages.add(lsInitiale.coord().get(lsInitiale.coord().size()-1));

  }

  /**
   * Computation of the gaussian smoothing on geoObj
   * @param isPersistantObject : determines if the geographic object is
   *          persistent in the database or just exists to apply the algorithm
   */

  public void compute() {

    this.jddPtsInflexion.clear();
    ILineString lsInitialee = this.geom;

    ILineString lsInitiale = Operateurs.resampling(lsInitialee, 1);
    ILineString lsLisse = GaussianFilter.gaussianFilter(lsInitiale, 5, 1);

    double sumOfDistances = 0;
    double length = 70;// 200 m max
    double sumOfAngels = 0;
    // ArrayList<Double> angels=new ArrayList<Double>();
    ArrayList<Double> differences = new ArrayList<Double>();

    ArrayList<Double> distances = new ArrayList<Double>();

    IDirectPosition p1 = lsInitiale.coord().get(0);
    IDirectPosition p2 = lsInitiale.coord().get(1);

    distances.add(p1.distance2D(p2));
    sumOfDistances += distances.get(distances.size() - 1);

    double angel = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX());

    angel = (angel + Math.PI) * 180 / Math.PI;

    // angels.add(angel);

    for (int i = 1; i < lsInitiale.coord().size() - 1; i++) {

      p1 = lsInitiale.coord().get(i);
      p2 = lsInitiale.coord().get(i + 1);

      distances.add(p1.distance2D(p2));
      sumOfDistances += distances.get(distances.size() - 1);

      double newAngel = Math
          .atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX());

      // give the value that varie from 0 to 360-epsilon
      newAngel = (newAngel + Math.PI) * 180 / Math.PI;

      // angels.add(newAngel);

      while (sumOfDistances > length && differences.size() > 0) {
        sumOfDistances -= distances.get(0);

        sumOfAngels -= differences.get(0);

        distances.remove(0);
        // System.out.println("olddiff=" + differences.get(0));
        differences.remove(0);

      }

      double diff = newAngel - angel;

      if (diff > 180)
        diff = -360 + diff;// for the case of angel=10 and newAngel=350;
      else if (diff < -180)
        diff = 360 + diff;

      differences.add(diff);
      sumOfAngels += diff;
      angel = newAngel;
      // System.out.println("sumOfAngels=" + sumOfAngels);
      // System.out.println("angel=" + newAngel);
      // System.out.println("diff=" + diff);

      /*
       * for(int k=0;k<angels.size();k++){ double diff2=newAngel-angels.get(k));
       * if(diff2>180)diff2=360-diff2;// for the case of angel=10 and
       * newAngel=350; else if(diff2<-180)diff2=-360-diff2;
       * if(Math.abs(newAngel->200) {
       * 
       * }
       * 
       * 
       * }
       */

      if (Math.abs(sumOfAngels) > 100) {

        double maxOfDiff = 0;
        int indexOfMaxOfDiff = -1;
        for (int k = 0; k < differences.size(); k++) {
          if (differences.get(k) > maxOfDiff) {
            maxOfDiff = differences.get(k);
            indexOfMaxOfDiff = k;

          }

        }
        double maxDistance = -1;
        int maxDistancePointIndex = -1;
        // if(i+differences.size()-indexOfMaxOfDiff-1>=jddPtsInflexion.size())
        for (int jj = 0; jj < differences.size(); jj++) {
          // jddPtsInflexion.add(lsInitiale.coord().get(i+differences.size()-indexOfMaxOfDiff-2));
          if (i + jj - indexOfMaxOfDiff >= 0
              && i + jj - indexOfMaxOfDiff < lsInitiale.coord().size()) {
            int currIndex = i + jj - indexOfMaxOfDiff;
            double distance = lsInitiale.coord().get(currIndex).distance2D(
                lsLisse.coord().get(currIndex));
            if (distance > maxDistance) {
              maxDistance = distance;
              maxDistancePointIndex = currIndex;
            }

          }

        }

        if (maxDistancePointIndex >= 0)
          this.jddPtsInflexion.add(lsInitiale.coord()
              .get(maxDistancePointIndex));

        differences.clear();
        sumOfDistances = distances.get(distances.size() - 1);
        distances.clear();
        sumOfAngels = 0;
        i++;

      }

    }

  }

  /**
   * Static computation of the curvature smoothing on a simple LineString
   * @param ls : the line to be treated
   * @param sigma : sigma
   * @param resolution : data resolution
   * @return the smoothed simple line
   */

  static double getNormalDistribution(double x) {
    return (1 / Math.sqrt(2 * Math.PI)) * Math.exp(-1 * x * x / 2.0);
  }

  public static List<Integer> determineSequences(ILineString lineString) {

    int sommesDesSommes = 0;
    List<Integer> listeSequences = new ArrayList<Integer>();
    DirectPositionList listePoints = (DirectPositionList) lineString
        .getControlPoint();
    // Angle angleTrigo;
    // String stAngleCourant;
    // String stAnglePrecedent = null;

    // Si la polyligne est inférieure à trois points, il n'y a pas d'angle
    // (pardi!!)
    if (listePoints.size() < 3) {
      logger.info("LineString inférieure à trois points");
    }

    // Sinon on identifie tous les changements de direction (sur la polyligne
    // déjà lissée c'est mieux !!!)
    else {
      int nbSommets = 2;
      for (int i = 0; i < listePoints.size() - 3; i++) {

        IDirectPosition p1 = listePoints.get(i);
        IDirectPosition p2 = listePoints.get(i + 1);
        IDirectPosition p3 = listePoints.get(i + 2);
        IDirectPosition p4 = listePoints.get(i + 3);

        double sign1 = p1.getY() - p2.getY()
            - ((p2.getY() - p3.getY()) / (p2.getX() - p3.getX()))
            * (p1.getX() - p2.getX());
        double sign2 = p4.getY() - p2.getY()
            - ((p2.getY() - p3.getY()) / (p2.getX() - p3.getX()))
            * (p4.getX() - p2.getX());

        // angleTrigo =
        // Angle.angleTroisPoints(listePoints.get(i),listePoints.get(i+1),listePoints.get(i+2));
        // Determine la direction de l'angle
        // if (angleTrigo.getValeur() > Math.PI){
        if (sign1 * sign2 >= 0)
          nbSommets = nbSommets + 1;
        else {
          listeSequences.add(nbSommets);
          sommesDesSommes += nbSommets;
          nbSommets = 1;
        }
      }
      /*
       * stAngleCourant = "Tourne à Gauche";
       * 
       * else { stAngleCourant = "Tourne à Droite"; } if (i>0){ //Identifie un
       * changement de direction if
       * (!(stAngleCourant.equals(stAnglePrecedent))){
       * listeSequences.add(nbSommets); sommesDesSommes+=nbSommets; nbSommets =
       * 1; }
       * 
       * //Pas de changement de direction if
       * (stAngleCourant.equals(stAnglePrecedent)){ nbSommets = nbSommets + 1; }
       * } stAnglePrecedent = stAngleCourant; }
       */
    }
    return listeSequences;
  }

  public static List<Integer> filtrageSequences(List<Integer> listeSequence,
      int filtrage) {

    List<Integer> listeSequenceFiltre = new ArrayList<Integer>();
    // Filtrage des micros inflexions (1 par défaud)
    listeSequenceFiltre = listeSequence;
    if (listeSequenceFiltre.size() == 1) {
      logger.info("LineString à une seule inflexion");
    }
    if (listeSequenceFiltre.size() == 2) {
      if (listeSequenceFiltre.get(0) <= filtrage) {
        int nbSommetsFiltre = listeSequenceFiltre.get(1) + 1;
        listeSequenceFiltre.clear();
        listeSequenceFiltre.add(nbSommetsFiltre);
      } else if (listeSequenceFiltre.get(1) <= filtrage) {
        int nbSommetsFiltre = listeSequenceFiltre.get(0) + 1;
        listeSequenceFiltre.clear();
        listeSequenceFiltre.add(nbSommetsFiltre);
      }
    } else if (listeSequenceFiltre.size() > 2) {
      for (int i = 0; i < listeSequenceFiltre.size(); i++) {
        if (listeSequenceFiltre.get(i) <= filtrage) {
          // cas où il faut virer une micro-inflexion en début de liste
          if (i == 0) {
            int nbSommetsFiltre = listeSequenceFiltre.get(1) + 1;
            listeSequenceFiltre.remove(0);
            listeSequenceFiltre.remove(0);
            listeSequenceFiltre.set(0, nbSommetsFiltre);
            i = -1;
          }
          // cas où il faut virer une micro-inflexion en fin de liste
          else if (i == listeSequenceFiltre.size() - 1) {
            int nbSommetsFiltre = listeSequenceFiltre.get(listeSequenceFiltre
                .size() - 2) + 1;
            listeSequenceFiltre.remove(i);
            listeSequenceFiltre.remove(listeSequenceFiltre.size() - 1);
            listeSequenceFiltre.add(nbSommetsFiltre);
          }
          // pour tous les autres cas
          else {
            int nbSommetsFiltre = listeSequenceFiltre.get(i - 1)
                + listeSequenceFiltre.get(i + 1) + 1;
            listeSequenceFiltre.remove(i - 1);
            // listNbInflexionsCorrect.remove(i+1);
            listeSequenceFiltre.remove(i);
            listeSequenceFiltre.set(i - 1, nbSommetsFiltre);
            i = -1;
          }
        }
      }
    }
    return listeSequenceFiltre;
  }

  /**
   * Détermine les points d'inflexions à partir de la lineString en entrée et de
   * la liste de séquence de sommets consécutifs dans la même direction
   * @param lineString
   * @param listeSequence
   * @return
   */

  public static DirectPositionList determinePointsInflexion(
      ILineString lineString, List<Integer> listeSequence) {
    DirectPositionList dplPointsInflexion = new DirectPositionList();
    DirectPositionList listePoints = (DirectPositionList) lineString
        .getControlPoint();

    // Détermine les points d'inflexion sur la polyligne originale
    dplPointsInflexion.add(listePoints.get(0));
    int positionPoint = 0;

    for (int i = 0; i < listeSequence.size(); i++) {
      positionPoint = positionPoint + listeSequence.get(i);
      dplPointsInflexion.add(listePoints.get(positionPoint));
    }
    return dplPointsInflexion;
  }
}
