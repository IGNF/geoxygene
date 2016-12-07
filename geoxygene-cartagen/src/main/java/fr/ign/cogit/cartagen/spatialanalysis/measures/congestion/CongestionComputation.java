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
package fr.ign.cogit.cartagen.spatialanalysis.measures.congestion;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * @author JGaffuri
 * 
 */
public class CongestionComputation {
  // private static Logger logger =
  // Logger.getLogger(CalculEncombrement.class.getName());

  /**
   */
  private int nbSegmentsBatiBati = 0;

  /**
   * @return
   */
  public int getNbSegmentsBatiBati() {
    return this.nbSegmentsBatiBati;
  }

  /**
   */
  private double[] encombrements;

  /**
   * @return
   */
  public double[] getEncombrements() {
    return this.encombrements;
  }

  private static int NB_ORIENTATIONS_ENCOMBREMENT = 16;

  /**
   * suppose que la fonction de triangulation d'ilot ait ete executee
   * 
   * @param distanceMax
   */
  public void calculEncombrement(IGraphLinkableFeature feature,
      double distanceMax) {
    IGeneObj ag;
    IDirectPosition coord, coordAg;
    double encomb, orientationEncombrement, or, dOr, enc;

    // initialisations
    this.nbSegmentsBatiBati = 0;
    this.encombrements = new double[CongestionComputation.NB_ORIENTATIONS_ENCOMBREMENT];
    for (int i = 0; i < CongestionComputation.NB_ORIENTATIONS_ENCOMBREMENT; i++) {
      this.getEncombrements()[i] = 0.0;
    }
    double pas = 2 * Math.PI
        / CongestionComputation.NB_ORIENTATIONS_ENCOMBREMENT;

    // parcours des segments de proximite du batiment
    for (IEdge s : feature.getProximitySegments()) {
      if (s.getWeight() > distanceMax) {
        continue;
      }

      // recupere l'autre agent lie par le segment
      IGraphLinkableFeature feat = s.getInitialNode()
          .getGraphLinkableFeature() == feature
              ? s.getFinalNode().getGraphLinkableFeature()
              : s.getInitialNode().getGraphLinkableFeature();
      ag = feat.getFeature();

      // compte les segments bati-bati
      if (ag instanceof IUrbanElement) {
        this.nbSegmentsBatiBati++;
      }

      // calcul de l'encombrement concernant le segment
      encomb = (distanceMax - s.getWeight()) / distanceMax;

      // calcul de la direction de l'encombrement en radians, dans [0, 2PI[
      // (recupere les coordonnees des points les plus proches entre les deux
      // agents du segment)
      IDirectPositionList dl = CommonAlgorithms
          .getPointsLesPlusProches(feature.getSymbolGeom(), ag.getSymbolGeom());
      coord = dl.get(0);
      coordAg = dl.get(1);
      orientationEncombrement = Math.atan2(coordAg.getY() - coord.getY(),
          coordAg.getX() - coord.getX());
      if (orientationEncombrement < 0) {
        orientationEncombrement += 2 * Math.PI;
      }

      // ajoute la contribution du segment au tableau des encombrements
      for (int i = 0; i < CongestionComputation.NB_ORIENTATIONS_ENCOMBREMENT; i++) {
        // l'orientation
        or = i * pas;

        // calcul de l'ecart, dans ]0, Pi]
        dOr = Math.abs(orientationEncombrement - or);
        if (dOr > Math.PI) {
          dOr = 2 * Math.PI - dOr;
        }

        if (dOr > Math.PI / 4) {
          continue;
        }
        enc = encomb * (1 - 4 * dOr / Math.PI);
        if (enc > this.getEncombrements()[i]) {
          this.getEncombrements()[i] = enc;
        }
      }

    }
  }

}
