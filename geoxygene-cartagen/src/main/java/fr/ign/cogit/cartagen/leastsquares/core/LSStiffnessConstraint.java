/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.leastsquares.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

/**
 * @author G. Touya
 * 
 *         Cette contrainte interne adaptée aux objets rigides comme les
 *         bâtiments cherche à conserver la longueur des segments en minimisant
 *         les différences de déplacement entre deux points consécutifs.
 */
public class LSStiffnessConstraint extends LSInternalConstraint {

  private Double minSegLength;
  /**
   * Below 1.5 m, the weight of the stiffness constraint is amplified to avoid
   * angle deformation of such small segments.
   */
  private double thresholdMinLength = 1.5;

  public LSStiffnessConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
  }

  /**
   * True if the constraint is applicable on point.
   * @param point
   * @return
   */
  public static boolean appliesTo(LSPoint point) {
    if (point.isFixed())
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.gothic.cogit.guillaume.moindresCarres.ContrainteInterneMC#
   * calculeSystemeEquations(gothic.main.GothicObject,
   * fr.ign.gothic.cogit.guillaume.moindresCarres.MCPoint)
   */
  @Override
  public EquationsSystem calculeSystemeEquations(IFeature obj, LSPoint point) {

    EquationsSystem systeme = this.sched.initSystemeLocal();
    // on commence par récupérer le point précédent et le suivant
    IDirectPosition coordPrec = null;
    IDirectPosition coordSuiv = null;
    // on commence par récupérer la géométrie
    IGeometry geom = obj.getGeom();
    ILineString ligne;
    boolean isLine = false;
    if (geom instanceof ILineString) {
      ligne = (ILineString) geom;
      isLine = true;
    } else {
      ligne = ((IPolygon) geom).exteriorLineString();
    }
    if (sched.getObjsMalleables().contains(obj)
        && ligne.coord().size() < this.sched.getMapObjPts().get(obj).size())
      ligne = LineDensification.densification2(ligne, sched.getMapspec()
          .getDensStep());
    boolean start = false;
    boolean end = false;
    for (int i = 0; i < ligne.numPoints(); i++) {
      IDirectPosition coord = ligne.coord().get(i);
      if (!coord.equals2D(point.getIniPt(), 0.001)) {
        continue;
      }

      // si on est là, c'est le bon vertex
      // on marque le vertex précédent
      int prevIndex, nextIndex;
      if (i == 0) {
        prevIndex = ligne.numPoints() - 2;
        start = true;
      } else {
        prevIndex = i - 1;
      }

      // on marque le vertex suivant
      if (i + 1 == ligne.numPoints()) {
        nextIndex = 0;
        end = true;
      } else {
        nextIndex = i + 1;
      }

      // on récupère les coordonnées précédentes
      coordPrec = ligne.coord().get(prevIndex);
      // on récupère les coordonnées suivantes
      coordSuiv = ligne.coord().get(nextIndex);
      break;
    }

    // on récupère maintenant les MCPoints correspondant à ces coordonnées
    ArrayList<LSPoint> listePoints = this.sched.getMapObjPts().get(obj);
    LSPoint pointPrec = null, pointSuiv = null;
    Iterator<LSPoint> iter = listePoints.iterator();
    while (iter.hasNext()) {
      LSPoint pt = iter.next();
      if (pt.getIniPt().equals2D(coordPrec, 0.001)) {
        pointPrec = pt;
      }
      if (pt.getIniPt().equals2D(coordSuiv, 0.001)) {
        pointSuiv = pt;
      }
    }// while boucle sur setPoints

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    if (((isLine == false) || (start == false)) && !pointPrec.isFixed()) {
      systeme.getUnknowns().addElement(pointPrec);
      systeme.getUnknowns().addElement(pointPrec);
      minSegLength = point.getIniPt().distance2D(coordPrec);
    }
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(point);
    if (((isLine == false) || (end == false)) && !pointSuiv.isFixed()) {
      systeme.getUnknowns().addElement(pointSuiv);
      systeme.getUnknowns().addElement(pointSuiv);
      if (minSegLength != null)
        minSegLength = Math.min(minSegLength,
            point.getIniPt().distance2D(coordSuiv));
      else
        minSegLength = point.getIniPt().distance2D(coordSuiv);
    }

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());

    // construction de la matrice des observations
    // c'est une matrice (4,1) contenant deux 0 dans le cas général
    // (2,1) dans le cas d'un bout d'une ligne
    systeme.initObservations(4);

    // construction de la matrice A
    if (isLine == false) {
      if (pointPrec.isFixed() && pointSuiv.isFixed()) {
        systeme.initMatriceA(4, 2);
        systeme.setA(0, 0, -1.0);
        systeme.setA(1, 1, -1.0);
        systeme.setA(2, 0, 1.0);
        systeme.setA(3, 1, 1.0);
        systeme.setNonNullValues(4);
        systeme.setObs(0, -pointPrec.getDeltaX());
        systeme.setObs(1, -pointPrec.getDeltaY());
        systeme.setObs(2, pointSuiv.getDeltaX());
        systeme.setObs(3, pointSuiv.getDeltaY());
      } else if (pointPrec.isFixed()) {
        systeme.initMatriceA(4, 4);
        systeme.setA(0, 0, -1.0);
        systeme.setA(1, 1, -1.0);
        systeme.setA(2, 0, 1.0);
        systeme.setA(2, 2, -1.0);
        systeme.setA(3, 1, 1.0);
        systeme.setA(3, 3, -1.0);
        systeme.setNonNullValues(6);
        systeme.setObs(0, -pointPrec.getDeltaX());
        systeme.setObs(1, -pointPrec.getDeltaY());
      } else if (pointSuiv.isFixed()) {
        systeme.initMatriceA(4, 4);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setA(2, 2, 1.0);
        systeme.setA(3, 3, 1.0);
        systeme.setNonNullValues(6);
        systeme.setObs(2, pointSuiv.getDeltaX());
        systeme.setObs(3, pointSuiv.getDeltaY());
      } else {
        systeme.initMatriceA(4, 6);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setA(2, 2, 1.0);
        systeme.setA(2, 4, -1.0);
        systeme.setA(3, 3, 1.0);
        systeme.setA(3, 5, -1.0);
        systeme.setNonNullValues(8);
      }
      for (int i = 0; i < 4; i++) {
        systeme.getConstraints().add(this);
      }
    } else if (start) {
      systeme.initObservations(2);
      if (pointSuiv.isFixed()) {
        systeme.initMatriceA(2, 2);
        systeme.setA(0, 0, -1.0);
        systeme.setA(1, 1, -1.0);
        systeme.setNonNullValues(2);
        systeme.setObs(0, -pointSuiv.getDeltaX());
        systeme.setObs(1, -pointSuiv.getDeltaY());
      } else {
        systeme.initMatriceA(2, 4);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setNonNullValues(4);
      }
      for (int i = 0; i < 2; i++) {
        systeme.getConstraints().add(this);
      }
    } else if (end) {
      systeme.initObservations(2);
      if (pointPrec.isFixed()) {
        systeme.initMatriceA(2, 2);
        systeme.setA(0, 0, 1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setNonNullValues(2);
        systeme.setObs(0, pointPrec.getDeltaX());
        systeme.setObs(1, pointPrec.getDeltaY());
      } else {
        systeme.initMatriceA(2, 4);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setNonNullValues(4);
      }
      for (int i = 0; i < 2; i++) {
        systeme.getConstraints().add(this);
      }
    } else {
      if (pointPrec.isFixed() && pointSuiv.isFixed()) {
        systeme.initMatriceA(4, 2);
        systeme.setA(0, 0, -1.0);
        systeme.setA(1, 1, -1.0);
        systeme.setA(2, 0, 1.0);
        systeme.setA(3, 1, 1.0);
        systeme.setNonNullValues(4);
        systeme.setObs(0, -pointPrec.getDeltaX());
        systeme.setObs(1, -pointPrec.getDeltaY());
        systeme.setObs(2, pointSuiv.getDeltaX());
        systeme.setObs(3, pointSuiv.getDeltaY());
      } else if (pointPrec.isFixed()) {
        systeme.initMatriceA(4, 4);
        systeme.setA(0, 0, -1.0);
        systeme.setA(1, 1, -1.0);
        systeme.setA(2, 0, 1.0);
        systeme.setA(2, 2, -1.0);
        systeme.setA(3, 1, 1.0);
        systeme.setA(3, 3, -1.0);
        systeme.setNonNullValues(6);
        systeme.setObs(0, -pointPrec.getDeltaX());
        systeme.setObs(1, -pointPrec.getDeltaY());
      } else if (pointSuiv.isFixed()) {
        systeme.initMatriceA(4, 4);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setA(2, 2, 1.0);
        systeme.setA(3, 3, 1.0);
        systeme.setNonNullValues(6);
        systeme.setObs(2, pointSuiv.getDeltaX());
        systeme.setObs(3, pointSuiv.getDeltaY());
      } else {
        systeme.initMatriceA(4, 6);
        systeme.setA(0, 0, 1.0);
        systeme.setA(0, 2, -1.0);
        systeme.setA(1, 1, 1.0);
        systeme.setA(1, 3, -1.0);
        systeme.setA(2, 2, 1.0);
        systeme.setA(2, 4, -1.0);
        systeme.setA(3, 3, 1.0);
        systeme.setA(3, 5, -1.0);
        systeme.setNonNullValues(8);
      }
      for (int i = 0; i < 4; i++) {
        systeme.getConstraints().add(this);
      }
    }

    return systeme;
  }

  @Override
  public double getWeightFactor() {
    if (minSegLength < thresholdMinLength)
      return super.getWeightFactor() * 5.0;
    return super.getWeightFactor();
  }

}
