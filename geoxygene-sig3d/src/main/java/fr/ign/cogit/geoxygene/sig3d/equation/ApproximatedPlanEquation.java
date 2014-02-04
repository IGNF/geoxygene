package fr.ign.cogit.geoxygene.sig3d.equation;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;

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
 * @version 0.1
 * 
 *
 * Classe permettant de définir des équations de plan de calculer l'equation
 * d'un plan ou la véritable équation pour un polygone. Il ne s'agit pas comme
 * dans la classe EquationPlan de déterminer une équation a partir des 3
 * premiers points
 * 
 * Cette équation donne le plan optimale les points ne sont pas coplanaires
 * Class to dertermine plan equation
 * 
 */
public class ApproximatedPlanEquation extends PlanEquation {

  private final static Logger logger = Logger
      .getLogger(ApproximatedPlanEquation.class.getName());

  /**
   * Construit l'équation à partir d'une liste de points. Le résultat sera l
   * 
   * @param dpl liste de points pour construire un plan
   */
  public ApproximatedPlanEquation(IDirectPositionList dpl) {
    super();
    if (dpl == null) {

      return;
    }

    if (dpl.size() < 3) {
    //  ApproximatedPlanEquation.logger.error(Messages.getString("PLAN.POINTS"));

      return;
    }

    int nbPoints = dpl.size();

    IDirectPosition dpAct = dpl.get(0);

    double xini = dpAct.getX();
    double yini = dpAct.getY();

    double zini;

    if (Double.isNaN(dpAct.getZ())) {
      zini = 0;
    } else {
      zini = dpAct.getZ();

    }

    this.coeffa = 0;
    this.coeffb = 0;
    this.coeffc = 0;

    double x0 = 0;
    double y0 = 0;
    double z0 = 0;

    // Il s'agit de calculer les produits vectoriels des différents
    // triangles formes par
    // dpl.get(0), dpAct, dpSuiv pour obtenir le vecteur normal moyen
    for (int i = 1; i < nbPoints; i++) {

      IDirectPosition dpSuiv = dpl.get(i);

      double zsuiv;
      if (Double.isNaN(dpSuiv.getZ())) {

        zsuiv = 0;
      } else {

        zsuiv = dpSuiv.getZ();

      }

      double zact;

      if (Double.isNaN(dpAct.getZ())) {

        zact = 0;
      } else {

        zact = dpAct.getZ();
      }

      x0 = x0 + dpSuiv.getX();
      y0 = y0 + dpSuiv.getY();
      z0 = z0 + zsuiv;

      this.coeffa = this.coeffa
          + ((dpAct.getY() - yini) - (dpSuiv.getY() - yini))
          * ((zact - zini) + (zsuiv - zini));
      this.coeffb = this.coeffb + ((zact - zini) - (zsuiv - zini))
          * ((dpAct.getX() - xini) + (dpSuiv.getX() - xini));
      this.coeffc = this.coeffc
          + ((dpAct.getX() - xini) - (dpSuiv.getX() - xini))
          * ((dpAct.getY() - yini) + (dpSuiv.getY() - yini));

      dpAct = dpSuiv;

    }

    Vecteur centre = new Vecteur(

    x0 / (nbPoints - 1),

    y0 / (nbPoints - 1), z0 / (nbPoints - 1));

    /*
     * if (Math.abs(coeffa) < 0.01) { coeffa = 0; } if (Math.abs(coeffb) < 0.01)
     * { coeffb = 0; } if (Math.abs(coeffc) < 0.01) { coeffc = 0; }
     */

    this.normaleToPlane = new Vecteur(this.coeffa, this.coeffb, this.coeffc);

    this.normaleToPlane.normalise();

    this.coeffa = this.normaleToPlane.getX();
    this.coeffb = this.normaleToPlane.getY();
    this.coeffc = this.normaleToPlane.getZ();

    this.coeffd = -centre.prodScalaire(this.normaleToPlane);

    // Affichage de l'équation implicite de l'équation
    this.equation = new String(this.coeffa + "x +" + this.coeffb + "y +"
        + this.coeffc + "z +" + this.coeffd + " = 0");
  }

  /**
   * Equation à partir d'une surface
   * 
   * @param oS la surface dont on veut calculer l'équation
   */
  public ApproximatedPlanEquation(IOrientableSurface oS) {
    this(oS.coord());
  }

  /**
   * Equation à partir d'une surface
   * 
   * @param oS la surface dont on veut calculer l'équation
   */
  public ApproximatedPlanEquation(IPolygon oS) {
    this(oS.getExterior().coord());
  }

}
