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

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.graph.IGraphLinkableFeature;

/**
 * @author JGaffuri
 * 
 */
public class DeletionCost {
  private static Logger logger = Logger.getLogger(DeletionCost.class.getName());

  /**
   * mesure d'encombrement permettant de choisir le meilleur batiment d'un ilot
   * a supprimer plus ce nombre est grand, plus le batiment a de chance d'etre
   * supprimme. c'est la moyenne quadratique de 5 facteurs.
   * 
   * @param surfaceMaxBatimentIlot surface maximum des batiments de l'ilot
   *          auquel appartient le batiment
   * @param distanceMax la distance maximum entre deux objets juges proches
   * @return la valeur du cout, entre 0 (plutot a ne pas supprimer) et 1 (plutot
   *         a supprimer).
   */
  public static double getCoutSuppression(IGraphLinkableFeature feature,
      double surfaceMaxBatimentIlot, double distanceMax) {
    CongestionComputation encombrement = new CongestionComputation();
    encombrement.calculEncombrement(feature, distanceMax);
    int nb = encombrement.getEncombrements().length;

    // surface relative du batiment: plus un batiment est grand, moins il a de
    // chance d'etre supprime
    double surf = (surfaceMaxBatimentIlot - feature.getSymbolArea())
        / surfaceMaxBatimentIlot;

    // part des directions encombrees
    double tDirEnc = 0.0;
    for (int i = 0; i < nb; i++) {
      if (encombrement.getEncombrements()[i] > 0.05) {
        tDirEnc++;
      }
    }
    tDirEnc /= nb;

    // moyenne des encombrements
    double enc = 0.0;
    for (int i = 0; i < nb; i++) {
      enc += encombrement.getEncombrements()[i];
    }
    enc /= nb;

    // facteur concernant le type de liens: un batiment lie a des batiments a
    // plus de chance d'etre supprime
    double typeLiens;
    if (feature.getProximitySegments().size() == 0) {
      typeLiens = 0;
    } else {
      typeLiens = encombrement.getNbSegmentsBatiBati()
          / feature.getProximitySegments().size();
    }

    // appartenance à des alignements : un batiment dans un alignement aura
    // moins de chances d'être supprimé
    // double alignFactor = 1.0;
    // if (feature instanceof IBuilding) {
    // int nbAlign = ((IBuilding) feature).getAlignments().size();
    // if (nbAlign == 1) {
    // alignFactor = 0.5;
    // } else if (nbAlign > 1) {
    // alignFactor = 0.25;
    // }
    // }

    // cout global: moyenne quadratique des 5 facteurs
    double cout = Math.sqrt((Math.pow(surf, 0.5) + Math.pow(tDirEnc, 2.0)
        + Math.pow(enc, 2.0) + Math.pow(typeLiens, 2.0)) / 4);

    if (DeletionCost.logger.isInfoEnabled()) {
      DeletionCost.logger.info("facteurs de suppression de " + feature);
      DeletionCost.logger.info("   surf=" + surf);
      DeletionCost.logger.info("   tDirEnc=" + tDirEnc);
      DeletionCost.logger.info("   enc=" + enc);
      DeletionCost.logger.info("   typeLiens=" + typeLiens);
      DeletionCost.logger.info("   TOTAL=" + cout);
    }

    return cout;
  }

  /**
   * Measure algorithm to determine the cost for building deletion in an urban
   * block. The measure is based, like getCoutSuppression(...), on congestion.
   * But particular buildings are assigned a minimal cost to avoid the deletion.
   * The particularity concerns the area differences in the block.
   * 
   * @param feature
   * @param maxBuildingArea
   * @param medianBuildingArea
   * @param distanceMax the maximum distance for 2 objects to be considered as
   *          close
   * @return
   * @author GTouya
   */
  public static double getDeletionCost(IGraphLinkableFeature feature,
      double maxBuildingArea, double medianBuildingArea, double distanceMax) {
    CongestionComputation congestion = new CongestionComputation();
    congestion.calculEncombrement(feature, distanceMax);
    int nb = congestion.getEncombrements().length;

    // relative building area: the bigger the building is, the less it can be
    // deleted
    double area = (maxBuildingArea - feature.getSymbolArea()) / maxBuildingArea;

    // compute the congestion direction criterion
    double tDirEnc = 0.0;
    for (int i = 0; i < nb; i++) {
      if (congestion.getEncombrements()[i] > 0.05) {
        tDirEnc++;
      }
    }
    tDirEnc /= nb;

    // compute the congestion mean criterion
    double enc = 0.0;
    for (int i = 0; i < nb; i++) {
      enc += congestion.getEncombrements()[i];
    }
    enc /= nb;

    // compute the link type criterion: a building linked to other buildings is
    // more likely to be deleted
    double linksType;
    if (feature.getProximitySegments().size() == 0) {
      linksType = 0;
    } else {
      linksType = congestion.getNbSegmentsBatiBati()
          / feature.getProximitySegments().size();
    }

    // compute global cost: the quadratic mean of the criterion with weights
    double cost = Math.sqrt((Math.pow(area, 0.5) + Math.pow(tDirEnc, 2.0)
        + Math.pow(enc, 2.0) + Math.pow(linksType, 2.0)) / 4);

    // now check the differiancation criteria: a building very different to the
    // others should not be deleted and its cost is put to 0.0
    // first the area differenciation criterion
    if (feature.getSymbolArea() > 2.5 * medianBuildingArea) {
      cost = 0.0;
    }

    if (DeletionCost.logger.isInfoEnabled()) {
      DeletionCost.logger.info("facteurs de suppression de " + feature);
      DeletionCost.logger.info("   surf=" + area);
      DeletionCost.logger.info("   tDirEnc=" + tDirEnc);
      DeletionCost.logger.info("   enc=" + enc);
      DeletionCost.logger.info("   typeLiens=" + linksType);
      DeletionCost.logger.info("   TOTAL=" + cost);
    }

    return cost;
  }

}
