/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.section;

import java.util.HashSet;

import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class CollapseRoundabout {

  /**
   * Under this diameter, the roundabout is collapsed to point. 100 m as default
   * value (the BD CARTO threshold).
   */
  private double diameterThreshold = 100.0;
  private IRoundAbout roundabout;

  public CollapseRoundabout(double diameterThreshold, IRoundAbout roundabout) {
    super();
    this.diameterThreshold = diameterThreshold;
    this.roundabout = roundabout;
  }

  /**
   * Collapse the roundabout to a point if diameter > threshold. The internal
   * roads are deleted while the external roads are extended.
   */
  public IRoadNode collapseToPoint() {
    // test if the diameter is less than threshold
    if (this.roundabout.getDiameter() > this.diameterThreshold) {
      return null;
    }

    HashSet<IRoadLine> externalRoads = new HashSet<IRoadLine>();
    externalRoads.addAll(this.roundabout.getExternalRoads());
    HashSet<IRoadLine> internalRoads = new HashSet<IRoadLine>();
    internalRoads.addAll(this.roundabout.getInternalRoads());
    // loop on the branching crossroads connected on the roundabout to add their
    // external roads
    for (IBranchingCrossroad branch : this.roundabout.getBranchings()) {
      externalRoads.addAll(branch.getExternalRoads());
      internalRoads.addAll(branch.getInternalRoads());
    }

    // get the roundabout centroid
    IDirectPosition centroid = this.roundabout.getGeom().centroid();

    // build the new road node
    IRoadNode node = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartAGenDB().getGeneObjImpl().getCreationFactory()
        .createRoadNode(new GM_Point(centroid));

    // loop on the external roads to extend them
    for (IRoadLine road : externalRoads) {
      // get its geometry
      ILineString geom = road.getGeom();
      // get line first and last point
      IDirectPosition firstPt = geom.coord().get(0);
      IDirectPosition lastPt = geom.coord().get(geom.coord().size() - 1);

      // compute the distances to the centroid
      double distFirst = centroid.distance2D(firstPt);
      double distLast = centroid.distance2D(lastPt);

      if (distFirst < distLast) {
        // the centroid is added at the beginning of the road geometry
        geom.addControlPoint(0, centroid);
        node.getOutSections().add(road);
        road.setInitialNode(node);

        // update the GeOx objects topology
        ((ArcReseau) road.getGeoxObj()).setNoeudInitial((NoeudReseau) node
            .getGeoxObj());
        ((NoeudReseau) node.getGeoxObj()).getArcsSortants().add(
            ((ArcReseau) road.getGeoxObj()));
      } else {
        // the centroid is added at the end of the road geometry
        geom.addControlPoint(centroid);
        node.getInSections().add(road);
        road.setFinalNode(node);
        // update the GeOx objects topology
        ((ArcReseau) road.getGeoxObj()).setNoeudFinal((NoeudReseau) node
            .getGeoxObj());
        ((NoeudReseau) node.getGeoxObj()).getArcsEntrants().add(
            ((ArcReseau) road.getGeoxObj()));
      }

      // update the road geometry
      road.setGeom(geom);

    }

    // delete the internal roads
    for (IRoadLine road : internalRoads) {
      road.setDeleted(true);
    }

    // added by Kusay to link the round about with the node which is resulted
    // from collapsing
    // TODO Declenche une exception dans certains cas A REVOIR !!!!
    try {
      if (!this.roundabout.getAntecedents().isEmpty())
        this.roundabout.getAntecedents().iterator().next()
            .addResultingObject(node);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return node;
  }
}
