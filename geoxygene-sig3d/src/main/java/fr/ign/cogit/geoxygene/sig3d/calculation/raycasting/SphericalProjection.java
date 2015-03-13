/**
 * 
 */
package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Orientation;
import fr.ign.cogit.geoxygene.sig3d.calculation.OrientedBoundingBox;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.index.Tiling;


/**
 *         This software is released under the licence CeCILL
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
 * Cette classe permet d'effectuer des projections sphériques des entités se
 * trouvant autour d'un point de vue
 * 
 * 
 * 
 */
public class SphericalProjection {

  /**
   * EPSILON pour arrondir les zéro
   */
  public static double EPSILON = 0.001;

  /**
   * Vecteur désignant le nord (origine pour calculer l'angle alpha
   */
  public static Vecteur NORTH = new Vecteur(0, 1, 0);

  /**
   * Centre de vue
   */
  private IDirectPosition centre;

  /**
   * Distance de sélection des entités
   */
  public double distance = 1500;

  /**
   * Entités plaquées sur la sphère de centre centre et de rayon distance Cette
   * liste est utilisée pour générer les autres types de projection
   */
  private IFeatureCollection<IFeature> lFeatMapped = new FT_FeatureCollection<IFeature>();

  /**
   * Les entités utilisées pour effectuer la projection
   */
  private IFeatureCollection<IFeature> featsToProject = new FT_FeatureCollection<IFeature>();

  public SphericalProjection(IDirectPosition centre) {
    this.centre = centre;

  }

  /**
   * Permet d'initialiser la projection sphérique d'entité
   * @param lFeat les entités que l'on souhaite projeter (doit contenir des
   *          objets polygonaux)
   * @param centre le centre de la sphère
   * @param distance la distance de sélection des entités
   */
  public SphericalProjection(IFeatureCollection<IFeature> lFeat,
      IDirectPosition centre, double distance, boolean cut) {

    this(Visibility.returnVisible(lFeat, centre, distance), centre, distance,
        cut);

  }

  /**
   * Permet d'initialiser la projection sphérique d'entité
   * @param polyVisible listes de polygones visibles depuis le centre
   * @param centre le centre
   * @param distance la distance à laquelle doivent se trouver les objets
   */
  public SphericalProjection(List<IOrientableSurface> polyVisible,
      IDirectPosition centre, double distance, boolean cut) {
    this.centre = centre;
    this.distance = distance;
    int nbPoly = polyVisible.size();

    // Si on le souhaite, on peut découper les entité suivant l'angle 0
    if (cut) {

      polyVisible = this.cut(polyVisible);

    }

    int nbEl = polyVisible.size();

    for (int i = 0; i < nbEl; i++) {

      IFeature feat = new DefaultFeature(polyVisible.get(i));

      this.featsToProject.add(feat);
    }

    nbPoly = this.featsToProject.size();

    for (int i = 0; i < nbPoly; i++) {

      IPolygon poly = this.calculAngle((GM_Polygon) polyVisible.get(i), 2.0);

      if (!poly.isValid()) {

        poly = (IPolygon) poly.buffer(0.001);

      }

      ApproximatedPlanEquation eQ = new ApproximatedPlanEquation(poly);

      double z = eQ.getNormale().getZ();

      if (z < 0) {

        poly = (IPolygon) poly.reverse();

      }
      eQ = new ApproximatedPlanEquation(poly);

      z = eQ.getNormale().getZ();

      if (z < 0) {
        System.out.println("C'est quoi ce bazar ?");
      }

      if (!poly.isValid()) {
        System.out.println("Polygon valide " + poly.isValid());
      }

      if (poly != null) {

        this.lFeatMapped.add(new DefaultFeature(poly));
      }

    }

  }

  /**
   * Permet de découper des polygones suivant l'angle 0
   * @param polyVisible
   * @return
   */
  public List<IOrientableSurface> cut(List<IOrientableSurface> polyVisible) {

    int nbPoly = polyVisible.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < nbPoly; i++) {

      lOS.addAll(this.cut((GM_Polygon) polyVisible.get(i)));

    }

    return lOS;
  }

  /**
   * Permet de découper un polygone en 2 suivant l'axe 0
   * @param poly
   * @return
   */
  public List<GM_Polygon> cut(GM_Polygon poly) {
    // On traite les points de l'extérieur
    IDirectPositionList dplExteriori = poly.getExterior().coord();
    Box3D b = new Box3D(dplExteriori);

    List<GM_Polygon> lP = new ArrayList<GM_Polygon>(1);

    if (b.getLLDP().getX() > this.centre.getX()
        || b.getURDP().getX() < this.centre.getX()
        || b.getURDP().getY() < this.centre.getY()) {

      lP.add(poly);
      return lP;
    }

    // Il devrait y avoir une instersection avec l'axe des Y négatif

    DirectPositionList exteriorLeft = new DirectPositionList();
    DirectPositionList exteriorRight = new DirectPositionList();

    int nbEx = dplExteriori.size();
    boolean isOnRight = dplExteriori.get(0).getX() > this.centre.getX();

    // Pour chaque point on calcule l'extérieur
    for (int i = 0; i < nbEx; i++) {

      IDirectPosition dp = dplExteriori.get(i);

      boolean isOnRightTemp = (dp.getX() > this.centre.getX());

      // Toujours du même côté de la frontière
      if (isOnRight == isOnRightTemp) {

        if (isOnRight) {

          exteriorRight.add(dp);

        } else {
          exteriorLeft.add(dp);

        }
        continue;
      }

      // Changement de sens
      // On cherche le point de coordonnées x,y,z se situant sur la ligne
      // entre le point actuel et le point précédent
      // tel que Vpred . Vcherché (avec x = xcentre) = lambda * VSuiv

      IDirectPosition dpPred;

      if (i == 0) {
        System.out.println("J'y passe");

        dpPred = dplExteriori.get(dplExteriori.size() - 2);

      } else {

        dpPred = dplExteriori.get(i - 1);
      }

      double lambda = (dpPred.getX() - this.centre.getX())
          / (dp.getX() - this.centre.getX());
      double y = (dpPred.getY() - lambda * dp.getY()) / (1 - lambda);
      double z = (dpPred.getZ() - lambda * dp.getZ()) / (1 - lambda);

      if (Double.isNaN(y)) {
        System.out.println("hum");
      }

      DirectPosition dpInterG = new DirectPosition(this.centre.getX()
          - SphericalProjection.EPSILON, y, z);
      DirectPosition dpInterD = new DirectPosition(this.centre.getX()
          + SphericalProjection.EPSILON, y, z);

      if (isOnRightTemp) {
        exteriorRight.add(dpInterD);
        exteriorRight.add(dp);
        exteriorLeft.add(dpInterG);
      } else {
        exteriorRight.add(dpInterD);
        exteriorLeft.add(dpInterG);
        exteriorLeft.add(dp);

      }
      isOnRight = isOnRightTemp;

    }

    if (exteriorLeft.size() > 2) {

      if (!exteriorLeft.get(0)
          .equals(exteriorLeft.get(exteriorLeft.size() - 1))) {
        exteriorLeft.add(exteriorLeft.get(0));

      }

      lP.add(new GM_Polygon(new GM_LineString(exteriorLeft)));

    }

    if (exteriorRight.size() > 2) {

      if (!exteriorRight.get(0).equals(
          exteriorRight.get(exteriorRight.size() - 1))) {
        exteriorRight.add(exteriorRight.get(0));

      }

      lP.add(new GM_Polygon(new GM_LineString(exteriorRight)));

    }

    return lP;

  }

  /**
   * Renvoie les entités dont l'angle a été calculé sous la forme d'un disque
   * centré en zéro de rayon rayon
   * @param rayon le rayon du disque
   * @return
   */
  public IFeatureCollection<IFeature> getEquidistantProjection(double rayon) {

    int nbFeat = this.lFeatMapped.size();
    FT_FeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();
    for (int i = 0; i < nbFeat; i++) {

      featCollOut.add(new DefaultFeature(this.getEquidistantProjection(
          (GM_Polygon) this.lFeatMapped.get(i).getGeom(), rayon)));

    }

    return featCollOut;

  }

  /**
   * Transforme un polygone avec des coordonnées 2D sphérique en polygone
   * projetée dans un disque de rayon rayon
   * @param poly polygone 2D sphérique que l'on souhaite projeter
   * @param rayon le rayon de représentation en sortie
   * @return un poilygone projetée dans le disque de rayon, rayon
   */
  private IPolygon getEquidistantProjection(IPolygon poly, double rayon) {
    // On traite chaque point
    // On transforme chaque point suivant ses coordonnées angulaires
    IDirectPositionList dplExteriori = poly.getExterior().coord();
    int nbEx = dplExteriori.size();

    DirectPositionList dplExtAngle = new DirectPositionList();

    for (int i = 0; i < nbEx; i++) {

      IDirectPosition dp = dplExteriori.get(i);

      double x = rayon * (1 - dp.getY() / (Math.PI / 2)) * Math.sin(dp.getX());
      double y = rayon * (1 - dp.getY() / (Math.PI / 2)) * Math.cos(dp.getX());

      dplExtAngle.add(new DirectPosition(x, y));

    }

    GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtAngle));

    List<IRing> lRings = poly.getInterior();

    int nbInterior = lRings.size();

    for (int j = 0; j < nbInterior; j++) {

      IDirectPositionList dplInterior = lRings.get(j).coord();
      int nbPIn = dplInterior.size();

      DirectPositionList dplInteriorAngle = new DirectPositionList();

      for (int i = 0; i < nbPIn; i++) {

        IDirectPosition dp = dplInterior.get(i);

        double x = rayon * (1 - dp.getY() / (Math.PI / 2))
            * Math.sin(dp.getX());
        double y = rayon * (1 - dp.getY() / (Math.PI / 2))
            * Math.cos(dp.getX());

        dplInteriorAngle.add(new DirectPosition(x, y));

      }

      polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorAngle)));

    }

    return polyOut;

  }

  public IPolygon calculAngle(IPolygon poly) {

    return this.calculAngle(poly, 0.0);
  }

  /**
   * Créer un polygone 2D dont les coordonnées sont l'orientation et l'élévation
   * @param poly un polygone
   * @return polygone 2D
   */
  public IPolygon calculAngle(IPolygon poly, double threshold) {

    // On traite les points de l'extérieur
    IDirectPositionList dplExteriori = poly.getExterior().coord();
    int nbEx = dplExteriori.size();

    if (!dplExteriori.get(0).equals(dplExteriori.get(nbEx - 1))) {

      dplExteriori.add(dplExteriori.get(0));
      nbEx++;
    }

    DirectPositionList dplExtAngle = new DirectPositionList();

    // Pour chaque point on calcule l'extérieur
    for (int i = 0; i < nbEx; i++) {

      IDirectPosition dp = dplExteriori.get(i);
      Orientation or = this.calculAngle(dp);

      dplExtAngle.add(new DirectPosition(or.getAlpha(), or.getBeta(),
          this.centre.distance2D(dp)));

      if (threshold == 0) {

        continue;
      } else {

        if (i == nbEx - 1) {

          break;

        }

        IDirectPosition dpSuiv = dplExteriori.get(i + 1);

        Vecteur v = new Vecteur(dp, dpSuiv);

        int nbAdd = (int) (v.norme() / threshold);
        v.normalise();

        for (int j = 0; j < nbAdd; j++) {

          DirectPosition dpTemp = new DirectPosition(dp.getX() + (1 + j)
              * v.getX() * threshold, dp.getY() + (1 + j) * v.getY()
              * threshold, dp.getZ() + +(1 + j) * v.getZ() * threshold);

          Orientation orT = this.calculAngle(dpTemp);
          dplExtAngle.add(new DirectPosition(orT.getAlpha(), orT.getBeta(),
              this.centre.distance2D(dpTemp)));

        }

      }

    }

    // On initialise le polygone de sorite
    GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtAngle));

    List<IRing> lRings = poly.getInterior();

    int nbInterior = lRings.size();

    // On traite de la même manière chaque intérieur
    for (int j = 0; j < nbInterior; j++) {

      IDirectPositionList dplInterior = lRings.get(j).coord();
      int nbPIn = dplInterior.size();

      DirectPositionList dplInteriorAngle = new DirectPositionList();

      if (!dplInterior.get(0).equals(dplInterior.get(nbPIn - 1))) {

        dplInterior.add(dplExteriori.get(0));
      }

      for (int i = 0; i < nbPIn; i++) {

        IDirectPosition dp = dplInterior.get(i);
        Orientation or = this.calculAngle(dp);

        dplInteriorAngle.add(new DirectPosition(or.getAlpha(), or.getBeta(),
            this.centre.distance2D(dp)));

        if (threshold == 0) {

          continue;
        } else {

          if (i == nbPIn - 1) {

            break;

          }

          IDirectPosition dpSuiv = dplInterior.get(i + 1);

          Vecteur v = new Vecteur(dp, dpSuiv);

          int nbAdd = (int) (v.norme() / threshold);
          v.normalise();

          for (int k = 0; k < nbAdd; k++) {

            DirectPosition dpTemp = new DirectPosition(dp.getX() + (1 + k)
                * v.getX() * threshold, dp.getY() + (1 + k) * v.getY()
                * threshold, dp.getZ() + +(1 + k) * v.getZ() * threshold);

            Orientation orT = this.calculAngle(dpTemp);
            dplInteriorAngle.add(new DirectPosition(orT.getAlpha(), orT
                .getBeta(), this.centre.distance2D(dpTemp)));

          }

        }

      }
      // On ajoute les intérieur
      polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorAngle)));

    }

    return polyOut;

  }

  /**
   * Calcul l'orientation d'un point par rapport au centre L'angle horizontal
   * vaut zéro si le point se trouve en dessous du centre
   * @param dp le point dont on calcul l'orientation
   * @return
   */
  public Orientation calculAngle(IDirectPosition dp) {
    // On récupère la distance et la différence de hauteur
    double distance = dp.distance(this.centre);
    double hauteur = dp.getZ() - this.centre.getZ();

    // L'angle vertical est obtenu gâce à l'ArcSinus
    double beta = Math.asin(hauteur / distance);

    if (Double.isNaN(beta)) {

      System.out.println("SphericalProjection : Isnan");
    }

    Vecteur v = new Vecteur(this.centre, dp);
    v.setZ(0);

    v.normalise();

    // L'angle horizontal grâce à l'arsinus avec le nord
    double alpha = Math
        .abs(Math.acos(SphericalProjection.NORTH.prodScalaire(v)));

    // L'angle alpha est mis entre 0 et 2 * PI
    if (v.getX() < 0) {

      alpha = 2 * Math.PI - alpha;

    }

    return new Orientation(alpha, beta);

  }
  

  public IFeatureCollection<IFeature> raffinedOrthoProjection() {
    IFeatureCollection<IFeature> featOut = new FT_FeatureCollection<>();

    this.getLFeatMapped().initSpatialIndex(Tiling.class, false);

    for (IFeature feat : this.getLFeatMapped()) {

      Collection<IFeature> featSelect = this.getLFeatMapped().select(
          feat.getGeom());

      while (featSelect.contains(feat)) {
        featSelect.remove(feat);
      }

      List<IOrientableSurface> lPolOut = solveZBuffer(feat, featSelect);

      for (IOrientableSurface poly : lPolOut) {
        featOut.add(new DefaultFeature(poly));
      }

    }

    return featOut;
  }


  private List<IOrientableSurface> solveZBuffer(IFeature feat,
      Collection<IFeature> featSelect) {

    List<IOrientableSurface> lPolOut = new ArrayList<>();

    IGeometry geom = (IGeometry) feat.getGeom().clone();

    OrientedBoundingBox oBB = new OrientedBoundingBox(geom);

    for (IFeature featToTreat : featSelect) {

      OrientedBoundingBox oBBToTest = new OrientedBoundingBox(
          featToTreat.getGeom());

      if (   oBBToTest.getzMax() -  oBBToTest.getzMin() >=  oBB.getzMax() -  oBB.getzMin()) {
        continue;

      }

      // On considère qu'il est devant

      IGeometry geomTemp = geom.intersection(featToTreat.getGeom());

      if (geomTemp.area() < 0.001) {
        continue;
      }

      geom = geom.difference(featToTreat.getGeom());

      if (geom == null) {
        return lPolOut;
      }

      List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(geom);

      if (lOS == null || lOS.isEmpty()) {
        return lPolOut;
      }
      

      geom = new GM_MultiSurface<>(FromGeomToSurface.convertGeom(geom));

    }

    lPolOut.addAll(FromGeomToSurface.convertGeom(geom));

    int nbPol = lPolOut.size();

    for (int i = 0; i < nbPol; i++) {
      IPolygon polyI = (IPolygon) lPolOut.get(i);

      if (polyI.isEmpty()) {
        lPolOut.remove(i);
        i--;
        nbPol--;
        continue;

      }

      List<IRing> lR = new ArrayList<>();
      lR.add(polyI.getExterior());

      if (polyI.getInterior() != null) {
        lR.addAll(polyI.getInterior());

      }

      for (IRing r : lR) {

        if (!r.isEmpty() && r.coord().size() < 4) {

          lPolOut.remove(i);
          i--;
          nbPol--;

          break;
        }

      }

    }


    return lPolOut;
  }
  /**
   * 
   * @return les entités plaquées en 2D en coordonnées angulaires
   */
  public IFeatureCollection<IFeature> getLFeatMapped() {
    return this.lFeatMapped;
  }

  public FT_FeatureCollection<IFeature> getStereoProjection(double hautPlan,
      boolean inverse) {
    FT_FeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();

    int nbElem = this.featsToProject.size();

    IDirectPositionList dplPlan = new DirectPositionList();
    IDirectPosition poleSud;

    if (inverse) {
      dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() + hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX() + 100, this.centre
          .getY(), this.centre.getZ() + hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX(),
          this.centre.getY() + 100, this.centre.getZ() + hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() + hautPlan));

      poleSud = new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() - hautPlan);

    } else {

      dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() - hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX() + 100, this.centre
          .getY(), this.centre.getZ() - hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX(),
          this.centre.getY() + 100, this.centre.getZ() - hautPlan));
      dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() - hautPlan));

      poleSud = new DirectPosition(this.centre.getX(), this.centre.getY(),
          this.centre.getZ() + hautPlan);
    }

    ApproximatedPlanEquation pl = new ApproximatedPlanEquation(dplPlan);

    for (int i = 0; i < nbElem; i++) {

      IGeometry geom = this.featsToProject.get(i).getGeom();

      if (geom instanceof GM_Polygon) {
        GM_Polygon poly = (GM_Polygon) geom;

        IDirectPositionList dplExt = poly.getExterior().coord();
        int nbEx = dplExt.size();

        IDirectPositionList dplExtOut = new DirectPositionList();

        // Pour chaque point on calcule l'extérieur
        for (int j = 0; j < nbEx; j++) {

          IDirectPosition dp = dplExt.get(j);

          LineEquation lE = new LineEquation(poleSud, dp);

          IDirectPosition dpInter = lE.intersectionLinePlan(pl);
          dplExtOut.add(dpInter);

        }

        // On initialise le polygone de sorite
        GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtOut));

        List<IRing> lRings = poly.getInterior();

        int nbInterior = lRings.size();

        // On traite de la même manière chaque intérieur
        for (int j = 0; j < nbInterior; j++) {

          IDirectPositionList dplInterior = lRings.get(j).coord();
          int nbPIn = dplInterior.size();

          DirectPositionList dplInteriorOut = new DirectPositionList();

          for (int k = 0; k < nbPIn; k++) {

            IDirectPosition dp = dplInterior.get(k);

            LineEquation lE = new LineEquation(poleSud, dp);

            IDirectPosition dpInter = lE.intersectionLinePlan(pl);
            dplInteriorOut.add(dpInter);

          }
          // On ajoute les intérieur
          polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorOut)));

        }

        // if(polyOut.isValid()){
        // System.out.println("Polygon valid : " + polyOut);
        featCollOut.add(new DefaultFeature(polyOut));
        // }else{
        // System.out.println("Non valid");
        // }

      } else {
        System.out.println("Autre classe " + geom.getClass().toString()
            + " GM_Polygon attendu");
      }

    }

    return featCollOut;
  }

  public FT_FeatureCollection<IFeature> getGnomoniqueProjection(double hautPlan) {
    FT_FeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();

    int nbElem = this.featsToProject.size();

    IDirectPositionList dplPlan = new DirectPositionList();
    dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
        this.centre.getZ() + hautPlan));
    dplPlan.add(new DirectPosition(this.centre.getX() + 100,
        this.centre.getY(), this.centre.getZ() + hautPlan));
    dplPlan.add(new DirectPosition(this.centre.getX(),
        this.centre.getY() + 100, this.centre.getZ() + hautPlan));
    dplPlan.add(new DirectPosition(this.centre.getX(), this.centre.getY(),
        this.centre.getZ() + hautPlan));

    ApproximatedPlanEquation pl = new ApproximatedPlanEquation(dplPlan);

    for (int i = 0; i < nbElem; i++) {

      IGeometry geom = this.featsToProject.get(i).getGeom();

      if (geom instanceof GM_Polygon) {
        GM_Polygon poly = (GM_Polygon) geom;

        IDirectPositionList dplExt = poly.getExterior().coord();
        int nbEx = dplExt.size();

        DirectPositionList dplExtOut = new DirectPositionList();

        // Pour chaque point on calcule l'extérieur
        for (int j = 0; j < nbEx; j++) {

          IDirectPosition dp = dplExt.get(j);

          if (dp.getZ() <= this.centre.getZ()) {
            System.out
                .println("Error projection gnomonique ne supporte pas z < centre");
            return null;
          }

          LineEquation lE = new LineEquation(this.centre, dp);

          IDirectPosition dpInter = lE.intersectionLinePlan(pl);
          dplExtOut.add(dpInter);

        }

        // On initialise le polygone de sorite
        GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtOut));

        List<IRing> lRings = poly.getInterior();

        int nbInterior = lRings.size();

        // On traite de la même manière chaque intérieur
        for (int j = 0; j < nbInterior; j++) {

          IDirectPositionList dplInterior = lRings.get(j).coord();
          int nbPIn = dplInterior.size();

          DirectPositionList dplInteriorOut = new DirectPositionList();

          for (int k = 0; k < nbPIn; k++) {

            IDirectPosition dp = dplInterior.get(k);

            if (dp.getZ() <= this.centre.getZ()) {
              System.out
                  .println("Error projection gnomonique ne supporte pas z < centre");
              return null;
            }

            LineEquation lE = new LineEquation(this.centre, dp);

            IDirectPosition dpInter = lE.intersectionLinePlan(pl);
            dplInteriorOut.add(dpInter);

          }
          // On ajoute les intérieur
          polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorOut)));

        }

        if (polyOut.isValid()) {

          featCollOut.add(new DefaultFeature(polyOut));
        } else {
          System.out.println("Non valid");
        }

      } else {
        System.out.println("Autre classe " + geom.getClass().toString()
            + " GM_Polygon attendu");
      }

    }

    return featCollOut;

  }

  /**
   * Renvoie les entités dont l'angle a été calculé sous la forme d'un disque
   * centré en zéro de rayon rayon
   * @param rayon le rayon du disque
   * @return
   */
  public IFeatureCollection<IFeature> getOrthographicProjection(double rayon) {

    int nbFeat = this.lFeatMapped.size();
    FT_FeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();
    for (int i = 0; i < nbFeat; i++) {

      featCollOut.add(new DefaultFeature(this.getOrthographicProjection(
          (GM_Polygon) this.lFeatMapped.get(i).getGeom(), rayon)));

    }

    return featCollOut;

  }

  private IPolygon getOrthographicProjection(IPolygon poly, double rayon) {
    // On traite chaque point
    // On transforme chaque point suivant ses coordonnées angulaires
    IDirectPositionList dplExteriori = poly.getExterior().coord();
    int nbEx = dplExteriori.size();

    DirectPositionList dplExtAngle = new DirectPositionList();

    for (int i = 0; i < nbEx; i++) {

      IDirectPosition dp = dplExteriori.get(i);

      double x = rayon * (1 - Math.sin(dp.getY())) * Math.sin(dp.getX());
      double y = rayon * (1 - Math.sin(dp.getY())) * Math.cos(dp.getX());

      dplExtAngle.add(new DirectPosition(x, y));

    }

    GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtAngle));

    List<IRing> lRings = poly.getInterior();

    int nbInterior = lRings.size();

    for (int j = 0; j < nbInterior; j++) {

      IDirectPositionList dplInterior = lRings.get(j).coord();
      int nbPIn = dplInterior.size();

      DirectPositionList dplInteriorAngle = new DirectPositionList();

      for (int i = 0; i < nbPIn; i++) {

        IDirectPosition dp = dplInterior.get(i);

        double x = rayon * (1 - Math.sin(dp.getY())) * Math.sin(dp.getX());
        double y = rayon * (1 - Math.sin(dp.getY())) * Math.cos(dp.getX());

        dplInteriorAngle.add(new DirectPosition(x, y));

      }

      polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorAngle)));

    }

    return polyOut;

  }

  /**
   * Renvoie les entités dont l'angle a été calculé sous la forme d'un disque
   * centré en zéro de rayon rayon
   * @param rayon le rayon du disque
   * @return
   */
  public IFeatureCollection<IFeature> getLambertProjection(double rayon,
      double phiO, double lambdaO) {

    int nbFeat = this.lFeatMapped.size();
    FT_FeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();
    for (int i = 0; i < nbFeat; i++) {

      featCollOut
          .add(new DefaultFeature(this.getLambertProjection(
              (GM_Polygon) this.lFeatMapped.get(i).getGeom(), rayon, phiO,
              lambdaO)));

    }

    return featCollOut;

  }


  private IPolygon getLambertProjection(IPolygon poly, double rayon,
      double phiO, double lambdaO) {
    // On traite chaque point
    // On transforme chaque point suivant ses coordonnées angulaires
    IDirectPositionList dplExteriori = poly.getExterior().coord();
    int nbEx = dplExteriori.size();

    DirectPositionList dplExtAngle = new DirectPositionList();

    for (int i = 0; i < nbEx; i++) {

      IDirectPosition dp = dplExteriori.get(i);

      double phi = dp.getY();
      double lambda = dp.getX();

      double cosPhi = Math.cos(phi);

      double cosPhiO = Math.cos(phiO);
      double sinPhiO = Math.sin(phiO);

      double alphaPrime = Math.sqrt(2 / (1 + Math.cos(phi - phiO) + cosPhi
          * cosPhiO * (Math.cos(lambda - lambdaO) - 1)));

      double x = rayon * alphaPrime * cosPhi * Math.sin(lambda - lambdaO);
      double y = rayon
          * alphaPrime
          * (Math.sin(phi - phiO) - sinPhiO * cosPhi
              * (Math.cos(lambda - lambdaO) - 1));

      dplExtAngle.add(new DirectPosition(x, y));

    }

    GM_Polygon polyOut = new GM_Polygon(new GM_LineString(dplExtAngle));

    List<IRing> lRings = poly.getInterior();

    int nbInterior = lRings.size();

    for (int j = 0; j < nbInterior; j++) {

      IDirectPositionList dplInterior = lRings.get(j).coord();
      int nbPIn = dplInterior.size();

      DirectPositionList dplInteriorAngle = new DirectPositionList();

      for (int i = 0; i < nbPIn; i++) {

        IDirectPosition dp = dplInterior.get(i);

        double phi = dp.getY();
        double lambda = dp.getX();

        double cosPhi = Math.cos(phi);
        double cosPhiO = Math.cos(phiO);

        double sinPhiO = Math.sin(phiO);

        double alphaPrime = Math.sqrt(2 / (1 + Math.cos(phi - phiO) + cosPhi
            * cosPhiO * (Math.cos(lambda - lambdaO) - 1)));

        double x = rayon * alphaPrime * cosPhi * Math.sin(phi - phiO);
        double y = rayon
            * alphaPrime
            * (Math.sin(phi - phiO) - sinPhiO * cosPhi
                * (Math.cos(lambda - lambdaO) - 1));

        dplInteriorAngle.add(new DirectPosition(x, y));

      }

      polyOut.addInterior(new GM_Ring(new GM_LineString(dplInteriorAngle)));

    }

    return polyOut;

  }

  /**
   * 
   * @return the lFeatCut les entités découpées si un découpage est effectué
   */
  public IFeatureCollection<IFeature> getFeatToProject() {
    return this.featsToProject;
  }

}
