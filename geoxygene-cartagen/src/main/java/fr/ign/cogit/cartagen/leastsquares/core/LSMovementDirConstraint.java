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
 *         Contrainte malléable pour les moindres carrés qui permet de contrôler
 *         la déformation d'un objet malléable : elle complète la contrainte de
 *         courbure.
 */
public class LSMovementDirConstraint extends LSInternalConstraint {

  private double weightFactor = 1.0;

  public static boolean appliesTo(LSPoint point) {
    if (point.isFixed())
      return false;
    if (!point.isPointIniFin()) {
      return true;
    }
    return false;
  }

  public LSMovementDirConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
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
    // test if densification is required
    if (geom.coord().size() < this.sched.getMapObjPts().get(obj).size()) {
      if (geom instanceof ILineString) {
        ligne = LineDensification.densification2((ILineString) geom, sched
            .getMapspec().getDensStep());
      } else {
        ligne = LineDensification.densification2(((IPolygon) geom)
            .exteriorLineString(), sched.getMapspec().getDensStep());
      }
    } else {
      if (geom instanceof ILineString) {
        ligne = (ILineString) geom;
      } else {
        ligne = ((IPolygon) geom).exteriorLineString();
      }
    }

    for (int i = 0; i < ligne.numPoints(); i++) {
      IDirectPosition coord = ligne.coord().get(i);
      if (!coord.equals2D(point.getIniPt(), 0.01)) {
        continue;
      }

      // si on est là, c'est le bon vertex
      // on marque le vertex précédent
      int prevIndex, nextIndex;
      if (i == 0) {
        prevIndex = ligne.numPoints() - 2;
      } else {
        prevIndex = i - 1;
      }
      // on marque le vertex suivant
      if (i + 1 == ligne.numPoints()) {
        nextIndex = 0;
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
      if (pt.getIniPt().equals2D(coordPrec, 0.01)) {
        pointPrec = pt;
      }
      if (pt.getIniPt().equals2D(coordSuiv, 0.01)) {
        pointSuiv = pt;
      }
    }// while boucle sur setPoints

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    if (!pointPrec.isFixed()) {
      systeme.getUnknowns().addElement(pointPrec);
      systeme.getUnknowns().addElement(pointPrec);
    }
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(point);
    if (!pointSuiv.isFixed()) {
      systeme.getUnknowns().addElement(pointSuiv);
      systeme.getUnknowns().addElement(pointSuiv);
    }

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    systeme.getConstraints().add(this);

    // construction de la matrice des observations
    // c'est une matrice (1,1) contenant un 0
    systeme.initObservations(1);

    // calcul des facteurs de l'équation sur les angles
    double a = 0.0, b = 0.0, c = 0.0, d = 0.0, e = 0.0, f = 0.0;
    a = (point.getIniPt().getY() - pointSuiv.getIniPt().getY()) / 2;
    b = (-point.getIniPt().getX() + pointSuiv.getIniPt().getX()) / 2;
    c = (-pointPrec.getIniPt().getY() + pointSuiv.getIniPt().getY()) / 2;
    d = (pointPrec.getIniPt().getX() - pointSuiv.getIniPt().getX()) / 2;
    e = (pointPrec.getIniPt().getY() - point.getIniPt().getY()) / 2;
    f = (-pointPrec.getIniPt().getX() + point.getIniPt().getX()) / 2;

    // construction de la matrice A

    if (pointPrec.isFixed() && pointSuiv.isFixed()) {
      systeme.initMatriceA(1, 2);
      systeme.setA(0, 0, c);
      systeme.setA(0, 1, d);
      systeme.setNonNullValues(2);
      systeme.setObs(0, -a * pointPrec.getDeltaX() - b * pointPrec.getDeltaY()
          - e * pointSuiv.getDeltaX() - f * pointSuiv.getDeltaY());
    } else if (pointPrec.isFixed()) {
      systeme.initMatriceA(1, 4);
      systeme.setA(0, 0, c);
      systeme.setA(0, 1, d);
      systeme.setA(0, 2, e);
      systeme.setA(0, 3, f);
      systeme.setNonNullValues(4);
      systeme.setObs(0, -a * pointPrec.getDeltaX() - b * pointPrec.getDeltaY());
    } else if (pointSuiv.isFixed()) {
      systeme.initMatriceA(1, 4);
      systeme.setA(0, 0, a);
      systeme.setA(0, 1, b);
      systeme.setA(0, 2, c);
      systeme.setA(0, 3, d);
      systeme.setNonNullValues(4);
      systeme.setObs(0, -e * pointSuiv.getDeltaX() - f * pointSuiv.getDeltaY());
    } else {
      systeme.initMatriceA(1, 6);
      systeme.setA(0, 0, a);
      systeme.setA(0, 1, b);
      systeme.setA(0, 2, c);
      systeme.setA(0, 3, d);
      systeme.setA(0, 4, e);
      systeme.setA(0, 5, f);
      systeme.setNonNullValues(6);
    }

    return systeme;
  }

  @Override
  public double getWeightFactor() {
    return weightFactor;
  }

}
