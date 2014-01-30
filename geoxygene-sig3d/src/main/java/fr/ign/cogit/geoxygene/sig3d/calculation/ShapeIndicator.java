package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.io.FileWriter;

import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.distribution.EquiSurfaceDistribution;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * 
 *          Classe permettant de calculer une distance qui permettra d'établir
 *          La ressemblance entre 2 objets selon la méthode Shape Distributions
 *          Osada and al[2002] Shape Distributions ROBERT OSADA, THOMAS
 *          FUNKHOUSER, BERNARD CHAZELLE, and DAVID DOBKIN Princeton University
 *          Class wich calculate the dissimilarity measure between 2 solids
 *          
 * 
 */
public class ShapeIndicator {

  private final static Logger logger = Logger.getLogger(ShapeIndicator.class
      .getName());

  public final static int TYPE_LENGTH = 1;
  public final static int TYPE_AREA = 2;
  public final static int TYPE_ANGLE = 3;

  // Il s'agit des valeurs extrêmes et médiane (longueur, surface ou volume en
  // fonction de la mesure choisie)
  private double valMin = Double.POSITIVE_INFINITY;
  private double valMax = 0.0;
  private double valMoy = 0.0;

  // Il s'agit du nombre d'échantillons et de classes permettant d'afficher la
  // mesure finale
  public final int nbSamples = 256 * 256;// 512 * 512;
  public final int nbClasses = 128;// 512;
  public final int nbVertices = 16;// 32;

  // Il s'agit du tableau contenant les différents échantillons
  private double[] lSamples = new double[this.nbSamples];

  // il s'agit de la largeur de valeurs d'une classe
  private double stepSize;

  // Il s'agit des valeurs des classes
  private double[] classValue = new double[this.nbClasses];

  // Il s'agit de spoints que l'on gardera au final
  private IDirectPositionList finalPoints;

  private EquiSurfaceDistribution eq;

  public ShapeIndicator(IFeature feat) {
    this(feat.getGeom());
  }

  public ShapeIndicator(IGeometry geom) {

    eq = new EquiSurfaceDistribution(geom); 
    finalPoints = new DirectPositionList();

  }

  /**
   * @return il s'agit des points utilisés dans le diagram représentant la trace
   *         de l'objet
   */
  public IDirectPositionList getFinalPoints() {
    return this.finalPoints;
  }

  /**
   * Permet de déterminer le nombre d'éléments par classe
   */
  private void computeStatistics() {
    // Le pas de classe permet de déterminer dans quelle classe se trouve
    // quelle valeur
    this.stepSize = (this.valMax - this.valMin) / this.nbClasses;

    for (int i = 0; i < this.nbSamples; i++) {

      double valActu = this.lSamples[i];
      int classeVal = (int) ((valActu - this.valMin) / this.stepSize); 

      // cas de la valeur max 
      if (classeVal == this.nbClasses) {

        classeVal = this.nbClasses - 1;
      }

      this.classValue[classeVal] = this.classValue[classeVal] + 1;

    }

    // Les histogrammes sont prêts, il faut maintenant calculer les points de
    // la fonction

    int nbClasseParSommet = this.nbClasses / this.nbVertices;

    double[] tempValPoints = new double[this.nbVertices];

    for (int i = 0; i < this.nbClasses; i++) {
      tempValPoints[i / nbClasseParSommet] = tempValPoints[i
          / nbClasseParSommet]
          + this.classValue[i];

    }

    for (int i = 0; i < this.nbVertices; i++) {
      this.finalPoints.add(new DirectPosition(i * this.stepSize
          * nbClasseParSommet, tempValPoints[i]));

    }

  }

  /**
   * Détermine les différentes valeurs pour la distance appelée D2 (considéré
   * comme la meilleure par l'auteur)
   */
  public void computeLengthIndicator() {

    // Remplissage de la tablea d'échantillons
    for (int i = 0; i < this.nbSamples; i++) {

      IDirectPositionList lPointsAlea = new DirectPositionList();

      // Détermination de 2 points aléatoirement tirés sur la surface du
      // volume
      for (int j = 0; j < 2; j++) {

        lPointsAlea.add(eq.sample());

      }

      // Nous avons 2 points à tester, nous pouvons calculer la longeur
      double d = lPointsAlea.get(0).distance(lPointsAlea.get(1));

      if (d < this.valMin) {

        this.valMin = d;
      }

      if (d > this.valMax) {

        this.valMax = d;
      }

      this.valMoy = this.valMoy + d / this.nbSamples;

      this.lSamples[i] = d;

    }

    // Calcul des statistics
    this.computeStatistics();

  }

  /**
   * Détermine les différentes valeurs pour la distance appelée D3
   */
  public void computeAreaIndicator() {

    // Remplissage de la tablea d'échantillons
    for (int i = 0; i < this.nbSamples; i++) {

      DirectPositionList lPointsAlea = new DirectPositionList();

      // On détermine 3 points aléatoires sur la surface du volume
      for (int j = 0; j < 3; j++) {

        lPointsAlea.add(eq.sample());

      }

      // On calcule l'aire du triangle formé par ces 3 points
      Vecteur v1 = new Vecteur(lPointsAlea.get(0), lPointsAlea.get(1));
      Vecteur v2 = new Vecteur(lPointsAlea.get(0), lPointsAlea.get(2));

      Vecteur v3 = v1.prodVectoriel(v2);

      double aire = v3.norme() * 0.5;

      // Nous avons 2 points à tester, nous pouvons calculer la longeur

      if (aire < this.valMin) {

        this.valMin = aire;
      }

      if (aire > this.valMax) {

        this.valMax = aire;
      }

      this.valMoy = this.valMoy + aire / this.nbSamples;

      this.lSamples[i] = aire;

    }

    // On calcul les statistics
    this.computeStatistics();

  }

  /**
   * Détermine les différentes valeurs pour la distance appelée D4
   */
  public void computeVolumeIndicator() {

    // On remplit la table d'échantillons
    for (int i = 0; i < this.nbSamples; i++) {

      IDirectPositionList lPointsAlea = new DirectPositionList();

      // 4 points de la surface sont tirés aléatoirement
      // Pour former un tétraèdre
      for (int j = 0; j < 4; j++) {

        lPointsAlea.add(eq.sample());

      }

      // L'aire du-dit tétraèdre est calculée

      IDirectPosition dp1 = lPointsAlea.get(0);
      IDirectPosition dp2 = lPointsAlea.get(1);
      IDirectPosition dp3 = lPointsAlea.get(2);
      IDirectPosition dp4 = lPointsAlea.get(3);

      Matrix matVol = new Matrix(3, 3);

      matVol.set(0, 0, dp1.getX() - dp2.getX());
      matVol.set(1, 0, dp2.getX() - dp3.getX());
      matVol.set(2, 0, dp3.getX() - dp4.getX());
      matVol.set(0, 1, dp1.getY() - dp2.getY());
      matVol.set(1, 1, dp2.getY() - dp3.getY());
      matVol.set(2, 1, dp3.getY() - dp4.getY());
      matVol.set(0, 2, dp1.getZ() - dp2.getZ());
      matVol.set(1, 2, dp2.getZ() - dp3.getZ());
      matVol.set(2, 2, dp3.getZ() - dp4.getZ());

      double volume = Math.abs(matVol.det()) / 6.;

      // Nous avons 2 points à tester, nous pouvons calculer la longeur

      if (volume < this.valMin) {

        this.valMin = volume;
      }

      if (volume > this.valMax) {

        this.valMax = volume;
      }

      this.valMoy = this.valMoy + volume / this.nbSamples;

      this.lSamples[i] = volume;

    }
    // On calcule les statistiques
    this.computeStatistics();

  }

  /**
   * Compute indicator according to a given type TYPE_LENGTH TYPE_AREA
   * TYPE_ANGLE
   * @param type
   */
  public void computeType(int type) {

    switch (type) {
      case TYPE_LENGTH:
        this.computeLengthIndicator();
        break;
      case TYPE_AREA:
        this.computeAreaIndicator();
        break;
      case TYPE_ANGLE:
        this.computeVolumeIndicator();
        break;
      default:
        logger.error("Type does not exist");

    }

  }

  /**
   * @param filename Fichier permettant d'exporter des statistiques en filenam
   */
  public void exportStats(String filename) {

    try {

      FileWriter data = new FileWriter(filename);

      // Nous indiquons les caractéristiques de pas et les différentes
      // valeurs remarquables
      data.write("*****************************************************\n");
      data.write("*****************************************************\n");
      data.write("*Export de statistique pour la ressemblance d'objets*\n");
      data.write("*****************************************************\n");
      data.write("*****************************************************\n");
      data.write("Valeur minimale :" + this.valMin + "\n");
      data.write("Valeur maximale :" + this.valMax + "\n");
      data.write("Valeur moyenne :" + this.valMoy + "\n");
      data.write("Pas entre 2 classes :" + this.stepSize + "\n");

      // On affiche le nombre de valeurs par classes

      data.write("*****************************************************\n");
      data.write("*******************Histogrammes**********************\n");
      data.write("*****************************************************\n");

      for (int i = 0; i < this.nbClasses; i++) {

        data.write((this.stepSize * i) + " " + this.classValue[i] + "\n");
      }

      // On affiche les points formant la "signature" de l'objet

      data.write("*****************************************************\n");
      data.write("*******************Points approx*********************\n");
      data.write("*****************************************************\n");

      int nbElemt = this.finalPoints.size();

      for (int i = 0; i < nbElemt; i++) {

        data.write(this.finalPoints.get(i).getX() + " "
            + this.finalPoints.get(i).getY() + "\n");
      }

      data.close();

      ShapeIndicator.logger.info("Fichier export sauvegardé");
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  /**
   * 
   * @return valeur minimale des points du diagramme
   */
  public double getValMin() {
    return this.valMin;
  }

  /**
   * 
   * @return valeur maximale des points du diagramme
   */
  public double getValMax() {
    return this.valMax;
  }

  /**
   * 
   * @return pas des classes du diagramme
   */
  public double getStepSize() {
    return this.stepSize;
  }

  /**
   * @return valeur moyenne du diagramme
   */
  public double getValMoy() {
    return this.valMoy;
  }

  /**
   * Effectue la comparaison entre 2 indicateurs de forme Les 2 doivent être
   * effectués dans les mêmes conditions pour que cela ait un sens
   * 
   * 
   * @param sh
   * @return
   */
  public double compareWith(ShapeIndicator sh) {
    double diff = 0;

    IDirectPositionList dpl1 = this.getFinalPoints();
    IDirectPositionList dpl2 = sh.getFinalPoints();

    int nbPoints = dpl1.size();

    for (int i = 0; i < nbPoints; i++) {

      diff = diff + Math.abs(dpl1.get(i).getY() - dpl2.get(i).getY());

    }

    diff = diff / this.nbSamples;
    return diff;
  }

}
