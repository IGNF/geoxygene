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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

/**
 * @author G. Touya
 * 
 */
public class LSCoalescenceConstraint extends LSExternalConstraint {

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

  /**
   * @param pt
   * @param obj1
   * @param obj2
   * @param scheduler
   */
  public LSCoalescenceConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler) {
    super(pt, obj1, obj2, scheduler);

  }

  public String getNom() {
    return "CEMC_Coalescence";
  }

  double tolerance = 0.2;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.gothic.cogit.guillaume.moindresCarres.ContrainteExterneMC#
   * calculeSystemeEquations(gothic.main.GothicObject, gothic.main.GothicObject,
   * fr.ign.gothic.cogit.guillaume.moindresCarres.MCPoint)
   */
  @Override
  public EquationsSystem calculeSystemeEquations() {
    double distance = this.seuilSep * this.sched.getMapspec().getEchelle()
        / 1000.0 + 2 * this.getPoint().getSymbolWidth();
    Set<LSPoint> verticesConflits = this.rechercheConflitsCoal(distance,
        this.sched);

    // on calcule le nouveau système d'équation
    EquationsSystem nouveau = null;
    boolean first = true;
    Iterator<LSPoint> iter = verticesConflits.iterator();
    while (iter.hasNext()) {
      LSPoint point2 = iter.next();
      EquationsSystem temp = this.calculePointToPoint(point2);
      if (first) {
        first = false;
        nouveau = temp;
        continue;
      }
      // nouveau.print("nouveau");
      // temp.print("temp");
      nouveau.assemble(temp);
    }

    return nouveau;
  }

  private EquationsSystem calculePointToPoint(LSPoint point2) {
    EquationsSystem systeme = this.sched.initSystemeLocal();

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    if (!this.getPoint().isFixed()) {
      systeme.getUnknowns().addElement(this.getPoint());
      systeme.getUnknowns().addElement(this.getPoint());
    }
    if (!point2.isFixed()) {
      systeme.getUnknowns().addElement(point2);
      systeme.getUnknowns().addElement(point2);
    }
    if (systeme.getUnknowns().size() == 0)
      return null;

    // construction de la matrice des observations
    // c'est une matrice (1,1)
    systeme.initObservations(1);

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    systeme.getConstraints().add(this);

    // on calcule la norme du vecteur w
    double normeW = Math.sqrt((this.getPoint().getIniPt().getX() - point2
        .getIniPt().getX())
        * (this.getPoint().getIniPt().getX() - point2.getIniPt().getX())
        + (this.getPoint().getIniPt().getY() - point2.getIniPt().getY())
        * (this.getPoint().getIniPt().getY() - point2.getIniPt().getY()));
    // on calcule dist_min, la constante de l'équation
    double dist_min = 0.0;
    if (normeW < (this.seuilSep * this.sched.getMapspec().getEchelle() / 1000.0
        + this.getPoint().getSymbolWidth() + point2.getSymbolWidth())) {
      dist_min = this.seuilSep * this.sched.getMapspec().getEchelle() / 1000.0
          + this.getPoint().getSymbolWidth() + point2.getSymbolWidth() - normeW;
    }
    systeme.setObs(0, dist_min);

    // calcul des facteurs de l'équation sur les angles
    double a = 0.0, b = 0.0, c = 0.0, d = 0.0;
    a = (this.getPoint().getIniPt().getX() - point2.getIniPt().getX()) / normeW;
    b = (this.getPoint().getIniPt().getY() - point2.getIniPt().getY()) / normeW;
    c = (point2.getIniPt().getX() - this.getPoint().getIniPt().getX()) / normeW;
    d = (point2.getIniPt().getY() - this.getPoint().getIniPt().getY()) / normeW;

    // construction de la matrice A
    if (this.getPoint().isFixed()) {
      systeme.initMatriceA(1, 2);
      systeme.setA(0, 0, c);
      systeme.setA(0, 1, d);
      systeme.setNonNullValues(2);
      systeme.setObs(0, dist_min - a * this.getPoint().getDeltaX() - b
          * this.getPoint().getDeltaY());
    } else if (point2.isFixed()) {
      systeme.initMatriceA(1, 2);
      systeme.setA(0, 0, a);
      systeme.setA(0, 1, b);
      systeme.setNonNullValues(2);
      systeme.setObs(0,
          dist_min - c * point2.getDeltaX() - d * point2.getDeltaY());
    }
    systeme.initMatriceA(1, 4);
    systeme.setA(0, 0, a);
    systeme.setA(0, 1, b);
    systeme.setA(0, 2, c);
    systeme.setA(0, 3, d);
    systeme.setNonNullValues(4);

    return systeme;
  }

  /**
   * <p>
   * Recherche les conflits potentiels de coalescence. Ce sont les vertices qui
   * sont à plus de 2 fois la distance selon la géométrie mais à moins de 1,5
   * fois la distance "à vol d'oiseau".
   * 
   */
  public Set<LSPoint> rechercheConflitsCoal(double distance, LSScheduler sched) {
    // on commence par cr�er le set vide
    Set<LSPoint> verticesConflits = new HashSet<LSPoint>();

    // on passe par la classe gothic des points
    Collection<IFeature> ptsProches = sched.getPoints().select(
        this.getPoint().getIniPt(), 1.5 * distance);
    if (ptsProches == null) {
      return verticesConflits;
    }

    IGeometry geom = (ILineString) this.getObj().getGeom();
    if (geom.coord().size() < this.sched.getMapObjPts().get(getObj()).size())
      geom = LineDensification.densification2(geom, 50.0);

    // on filtre pour ne garder que ceux liés à obj
    for (IFeature vertex : ptsProches) {

      Set<IFeature> objets = ((LSPoint) vertex).getObjs();

      if (!objets.contains(this.getObj())) {
        continue;
      }

      // on cherche maintenant si les points sont proches en parcourant la
      // géométrie
      // pour cela, on marque la géométrie aux deux points
      int index1 = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionFromPoint(geom, this.getPoint().getIniPt());
      int index2 = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionFromPoint(geom,
              ((LSPoint) vertex).getIniPt());
      double dist1 = CommonAlgorithmsFromCartAGen.getLineDistanceToIndex(
          (ILineString) geom, index1);
      double dist2 = CommonAlgorithmsFromCartAGen.getLineDistanceToIndex(
          (ILineString) geom, index2);
      double dist = Math.abs(dist1 - dist2);
      if (dist > 2 * distance) {
        // on ajoute le LSPoint correspondant à vertex au set en sortie
        verticesConflits.add((LSPoint) vertex);
      }// if(dist<2*distance)
    }// while boucle sur ptsProches

    return verticesConflits;
  }// rechercheConflitsCoal(double distance,MCScheduler sched)

}
