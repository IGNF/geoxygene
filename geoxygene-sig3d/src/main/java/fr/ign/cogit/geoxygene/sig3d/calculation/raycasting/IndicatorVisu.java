package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;


import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;


/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 *
 * Permet de calculer des indcateur de visu à partir d'un lancer de rayon
 * 
 */
public class IndicatorVisu {

  public static double EPSILON = 0.001;

  private RayCasting cast;

  private double minimalRadialDistance = Double.POSITIVE_INFINITY;
  private double maximalRadialDistance = Double.NEGATIVE_INFINITY;
  private double moyRadialDistance = Double.NaN;
  private double varianceRadialDistance = Double.NaN;

  private double minimalRadialDistance2D = Double.POSITIVE_INFINITY;
  private double maximalRadialDistance2D = Double.NEGATIVE_INFINITY;
  private double moyRadialDistance2D = Double.NaN;

  private double openess = Double.NaN;

  private double ratioSphere = Double.NaN;
  private double ratioSphere2 = Double.NaN;

  private double visibleSkySurface = Double.NaN;
  private double visibleVolume = Double.NaN;
  private double visibleVolumeRatio = Double.NaN;

  private double solidPerimeter = Double.NaN;

  // Calcul 2D
  private double aireIsovist = Double.NaN;
  private double permietreIsovist = Double.NaN;

  private int nbHit = -1;

  /**
   * Constructeur prenant en compte un lancer de rayon
   * 
   * @param cast
   */
  public IndicatorVisu(RayCasting cast) {
    this.cast = cast;

  }

  /**
   * Calcul min, moy et max distances radiales
   */
  private void calculateMinMoyMaxRadialDistance() {
    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();

    int nbP = dpl.size();

    double distCumul = 0;

    int count = 0;

    for (int i = 0; i < nbP; i++) {

      double d = centre.distance(dpl.get(i));

      if (d < this.cast.getRayon() - IndicatorVisu.EPSILON) {

        this.minimalRadialDistance = Math.min(this.minimalRadialDistance, d);
        this.maximalRadialDistance = Math.max(this.maximalRadialDistance, d);
        distCumul = distCumul + d;
        count++;
      }

    }

    if (count != 0) {

      this.moyRadialDistance = distCumul / count;
    }

  }

  /**
   * Calcul min, moy et max distances radiales 2D
   */
  private void calculateMinMoyMaxRadialDistance2D() {
    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();

    int nbP = dpl.size();

    double distCumul = 0;

    int count = 0;

    for (int i = 0; i < nbP; i++) {

      double d = centre.distance2D(dpl.get(i));

      if (d < this.cast.getRayon() - IndicatorVisu.EPSILON) {

        this.minimalRadialDistance2D = Math
            .min(this.minimalRadialDistance2D, d);
        this.maximalRadialDistance2D = Math
            .max(this.maximalRadialDistance2D, d);
        distCumul = distCumul + d;
        count++;

      }

    }

    if (count != 0) {
      this.moyRadialDistance2D = distCumul / count;

    }

  }

  /**
   * Calcule l'ouverture et la surface de ciel visible.
   * 
   * Le calcul doit être du type : TYPE_FIRST_POINT_AND_SPHERE
   */
  private void calculRatioSphere() {

    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();

    int nbP = dpl.size();

    double ratio = 0;

    for (int i = 0; i < nbP; i++) {

      if (centre.distance(dpl.get(i)) < this.cast.getRayon()
          - IndicatorVisu.EPSILON) {

        // Nous avons touché une cible
        ratio = ratio + 1;

      }
    }

    this.ratioSphere = 1 - ratio / nbP;

  }

  /**
   * Calcule l'ouverture et la surface de ciel visible.
   * 
   * Le calcul doit être du type : TYPE_FIRST_POINT_AND_SPHERE
   */
  private void calculRatioSphere2() {

    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();

    int nbP = dpl.size();

    double ratio = 0;

    int count = 0;

    for (int i = 0; i < nbP; i++) {

      if (centre.distance(dpl.get(i)) < this.cast.getRayon()
          - IndicatorVisu.EPSILON) {

        ratio = ratio + (centre.distance(dpl.get(i)) / this.cast.getRayon());
        count++;
      }

    }

    if (count == 0) {
      this.ratioSphere2 = -1;
    }

    this.ratioSphere2 = ratio / count;

  }

  public double getRatioSphere() {

    if (Double.isNaN(ratioSphere)) {
      this.calculRatioSphere();
    }
    return ratioSphere;
  }

  public double getRatioSphere2() {
    if (Double.isNaN(ratioSphere2)) {
      this.calculRatioSphere2();
    }
    return ratioSphere2;
  }

  /**
   * Calcule l'ouverture et la surface de ciel visible.
   * 
   * Le calcul doit être du type : TYPE_FIRST_POINT_AND_SPHERE
   */
  private void calculOpeness() {

    double openss = 0;

    if (this.cast.getLastTypeResult() != RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {

      return;
    }

    List<IOrientableSurface> lSol = this.cast.getGeneratedSolid()
        .getFacesList();
    int nbFaces = lSol.size();

    bouclei: for (int i = 0; i < nbFaces; i++) {
      IOrientableSurface faceActu = lSol.get(i);
      ITriangle tri = (ITriangle) faceActu;

      IDirectPositionList dplTemp = tri.coord();

      for (int j = 0; j < 3; j++) {

        if (this.cast.getCentre().distance(dplTemp.get(j)) < this.cast
            .getRayon() - IndicatorVisu.EPSILON) {

          continue bouclei;
        }

      }

      openss = openss + tri.area();

    }

    this.visibleSkySurface = openss;

    if (this.cast.isSphere()) {

      openss = openss / (4 * Math.PI * Math.pow(this.cast.getRayon(), 2));
    } else {

      openss = (2 * openss) / (4 * Math.PI * Math.pow(this.cast.getRayon(), 2));

    }

    this.openess = openss;

  }

  /**
   * 
   */
  public void calculSolidPerimeter() {

    if (this.cast.getLastTypeResult() != RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {
      System.out.println("Mauvais type de ray casting");
      return;
    }

    List<IOrientableSurface> lSol = this.cast.getGeneratedSolid()
        .getFacesList();
    int nbFaces = lSol.size();

    double surTot = 0;

    for (int i = 0; i < nbFaces; i++) {

      IOrientableSurface faceActu = lSol.get(i);
      ITriangle tri = (ITriangle) faceActu;

      surTot = surTot + tri.area();

    }

    this.solidPerimeter = surTot;

  }

  private void calculVisibleVolume() {

    if (this.cast.getLastTypeResult() != RayCasting.TYPE_FIRST_POINT_AND_SPHERE) {
      System.out.println("Mauvais type de ray casting");

      return;
    }

    List<IOrientableSurface> lSol = this.cast.getGeneratedSolid()
        .getFacesList();

    int nbFaces = lSol.size();

    double volTot = 0;

    for (int i = 0; i < nbFaces; i++) {

      IOrientableSurface faceActu = lSol.get(i);
      ITriangle tri = (ITriangle) faceActu;

      double volTemp = Math.abs(fr.ign.cogit.geoxygene.sig3d.calculation.Util
          .volumeUnderTriangle(tri));

      Vecteur v1 = new Vecteur(this.cast.getCentre(),
          fr.ign.cogit.geoxygene.sig3d.calculation.Util.centerOf(tri.coord()));

      Vecteur norm = (new ApproximatedPlanEquation(tri)).getNormale();

      if (v1.prodScalaire(norm) > 0) {

        volTot = volTot + volTemp;
      } else {

        volTot = volTot - volTemp;

      }

    }

    if (this.cast.isSphere()) {
      this.visibleVolumeRatio = volTot
          / ((4.0 / 3.0) * Math.PI * Math.pow(this.cast.getRayon(), 3));

    } else {

      this.visibleVolumeRatio = volTot
          / ((2.0 / 3.0) * Math.PI * Math.pow(this.cast.getRayon(), 3));
    }

    this.visibleVolume = volTot;

  }

  private void calculHit() {
    nbHit = 0;

    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();
    int nbP = dpl.size();

    for (int i = 0; i < nbP; i++) {

      double d = centre.distance(dpl.get(i));

      if (d < this.cast.getRayon() - IndicatorVisu.EPSILON) {

        nbHit++;
      }

    }
  }

  private double calculateVarianceDistance() {

    double moy = this.getMoyRadialDistance();

    IDirectPosition centre = this.cast.getCentre();
    IDirectPositionList dpl = this.cast.getDpGenerated();

    double variance = 0;
    int count = 0;

    int nbP = dpl.size();

    for (int i = 0; i < nbP; i++) {

      double d = centre.distance(dpl.get(i));

      if (d < this.cast.getRayon() - IndicatorVisu.EPSILON) {

        variance = Math.pow(d - moy, 2);

        count++;
      }

    }

    if (count == 0) {
      return -1;
    }

    return variance / count;

  }

  /**
   * 
   * @return
   */
  public double getMinimalRadialDistance() {

    if (this.minimalRadialDistance != Double.POSITIVE_INFINITY) {
      return this.minimalRadialDistance;
    }
    this.calculateMinMoyMaxRadialDistance();
    return this.minimalRadialDistance;
  }

  /**
   * 
   * @return
   */
  public double getMaximalRadialDistance() {

    if (this.maximalRadialDistance != Double.NEGATIVE_INFINITY) {
      return this.maximalRadialDistance;
    }
    this.calculateMinMoyMaxRadialDistance();
    return this.maximalRadialDistance;
  }

  /**
   * 
   * @return
   */
  public double getMoyRadialDistance() {

    if (!Double.isNaN(this.moyRadialDistance)) {
      return this.moyRadialDistance;
    }
    this.calculateMinMoyMaxRadialDistance();
    return this.moyRadialDistance;
  }

  /**
   * 
   * @return
   */
  public double getMinimalRadialDistance2D() {

    if (this.minimalRadialDistance2D != Double.POSITIVE_INFINITY) {
      return this.minimalRadialDistance2D;
    }
    this.calculateMinMoyMaxRadialDistance2D();
    return this.minimalRadialDistance2D;
  }

  /**
   * 
   * @return
   */
  public double getMaximalRadialDistance2D() {

    if (this.maximalRadialDistance2D != Double.NEGATIVE_INFINITY) {
      return this.maximalRadialDistance2D;
    }
    this.calculateMinMoyMaxRadialDistance();
    return this.maximalRadialDistance2D;
  }

  /**
   * 
   * @return
   */
  public double getMoyRadialDistance2D() {

    if (!Double.isNaN(this.moyRadialDistance2D)) {
      return this.moyRadialDistance2D;
    }
    this.calculateMinMoyMaxRadialDistance2D();
    return this.moyRadialDistance2D;
  }

  public double getOpeness() {
    if (!Double.isNaN(this.openess)) {

      return this.openess;
    }

    this.calculOpeness();
    return this.openess;

  }

  /**
   * 
   * @return
   */
  public double getVisibleSkySurface() {
    if (!Double.isNaN(this.visibleSkySurface)) {

      return this.visibleSkySurface;
    }

    this.calculOpeness();
    return this.visibleSkySurface;

  }

  public double getVisibleVolumeRatio() {

    if (!Double.isNaN(this.visibleVolumeRatio)) {
      return this.visibleVolumeRatio;
    }
    this.calculVisibleVolume();
    return this.visibleVolumeRatio;
  }

  /**
   * @return the aireIsovist
   */
  public double getAireIsovist() {

    if (Double.isNaN(this.aireIsovist)) {

      this.aireIsovist = this.cast.getGeneratedPolygon().area();
    }

    return this.aireIsovist;
  }

  public double getCompaciteIsovist() {

    if (Double.isNaN(this.aireIsovist)) {

      this.aireIsovist = this.cast.getGeneratedPolygon().area();
    }

    return this.aireIsovist;

  }

  /**
   * @return the permietreIsovist
   */
  public double getPermietreIsovist() {

    if (Double.isNaN(this.permietreIsovist)) {

      this.permietreIsovist = this.cast.getGeneratedPolygon().getExterior()
          .length();
    }

    return this.permietreIsovist;

  }

  public double getVisibleVolume() {

    if (!Double.isNaN(this.visibleVolume)) {
      return this.visibleVolume;
    }
    this.calculVisibleVolume();
    return this.visibleVolume;

  }

  /**
   * @return the solidPerimeter
   */
  public double getSolidPerimeter() {
    if (Double.isNaN(this.solidPerimeter)) {
      this.calculSolidPerimeter();
    }
    return this.solidPerimeter;
  }

  public double getSolidPerimeter2PerimeterRatio() {
    if (Double.isNaN(this.aireIsovist)) {

      this.aireIsovist = this.cast.getGeneratedPolygon().area();
    }

    if (Double.isNaN(this.permietreIsovist)) {

      this.permietreIsovist = this.cast.getGeneratedPolygon().getExterior()
          .length();
    }

    return this.aireIsovist / this.permietreIsovist;

  }

  public int getNbHit() {

    if (this.nbHit == -1) {
      calculHit();
    }
    return nbHit;
  }

  /**
   * @return the varianceRadialDistance
   */
  public double getVarianceRadialDistance() {

    if (Double.isNaN(this.varianceRadialDistance)) {

      this.varianceRadialDistance = this.calculateVarianceDistance();
    }
    return this.varianceRadialDistance;
  }

  public double getEcartType() {
    if (Double.isNaN(this.varianceRadialDistance)) {

      this.varianceRadialDistance = this.calculateVarianceDistance();
    }
    return Math.sqrt(this.varianceRadialDistance);
  }

  public RayCasting getCast() {
    return cast;
  }

}
