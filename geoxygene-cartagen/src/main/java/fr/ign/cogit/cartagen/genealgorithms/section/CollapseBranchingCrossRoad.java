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

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class CollapseBranchingCrossRoad {

  /**
   * Under this area, the branching crossroad is collapsed to a point. 800 m² as
   * default value.
   */
  private double minArea = 800.0;
  private IBranchingCrossroad branching;

  public CollapseBranchingCrossRoad(double minArea,
      IBranchingCrossroad branching) {
    super();
    this.minArea = minArea;
    this.branching = branching;
  }

  /**
   * Collapse the branching crossroad to a point if area < threshold. The
   * internal minor road sections are deleted while the external minor road
   * section is extended to the internal major road section.
   */
  public IRoadNode collapseToPoint() {
    // test if the diameter is less than threshold
    if (this.branching.getGeom().area() > this.minArea) {
      return null;
    }

    // test if connected to a roundabout
    if (this.branching.getRoundAbout() != null) {
      return null;
    }

    // test of twisted cases
    if (this.branching.getMinorRoadExtern() == null) {
      return null;
    }

    IRoadNode node = null;
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

    // characterise the corners of the branching crossroad
    IDirectPosition cornerMinor = null, cornerMain1 = null, cornerMain2 = null;
    for (INetworkNode internNode : branching.getSimples()) {
      if (branching.getMinorRoadExtern().getGeom()
          .intersects(internNode.getGeom()))
        cornerMinor = internNode.getPosition();
      else if (cornerMain1 == null)
        cornerMain1 = internNode.getPosition();
      else
        cornerMain2 = internNode.getPosition();
    }

    // now extend minor external road
    Vector2D direction = new Vector2D(branching.getMinorRoadExtern().getGeom()
        .coord().get(1), cornerMinor);
    if (!branching.getMinorRoadExtern().getGeom().coord().get(0)
        .equals(cornerMinor))
      direction = new Vector2D(branching.getMinorRoadExtern().getGeom().coord()
          .get(branching.getMinorRoadExtern().getGeom().coord().size() - 2),
          cornerMinor);
    Segment segment = new Segment(cornerMain1, cornerMain2)
        .extendAtExtremities(2.0);
    IDirectPosition proj = CommonAlgorithmsFromCartAGen.projection(cornerMinor,
        segment, direction);
    if (proj == null)
      proj = CommonAlgorithmsFromCartAGen.projection(cornerMinor,
          segment.extendAtExtremities(10.0), direction);
    if (proj == null) {
      // another twisted case: do nothing
      return null;
    }
    // check of the projected is on the outline or not
    if (!branching.getGeom().exteriorLineString().intersects(proj.toGM_Point())) {
      // it means that the main way is not right but curved. Then, look for
      // the nearest point on the outline.
      proj = CommonAlgorithms.getNearestPoint(branching.getGeom()
          .exteriorLineString(), proj.toGM_Point());
    }
    // build the new road node
    node = dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory()
        .createRoadNode(new GM_Point(proj));
    dataset.getRoadNodes().add(node);
    dataset.getRoadNetwork().addNode(node);

    if (!branching.getMinorRoadExtern().getGeom().coord().get(0)
        .equals(cornerMinor)) {
      IDirectPositionList points = new DirectPositionList();
      points.addAll(branching.getMinorRoadExtern().getGeom().coord());
      points.add(proj);
      ILineString newGeom = new GM_LineString(points);
      branching.getMinorRoadExtern().setGeom(newGeom);
      node.getOutSections().add(branching.getMinorRoadExtern());
    } else {
      IDirectPositionList points = new DirectPositionList();
      points.addAll(branching.getMinorRoadExtern().getGeom().coord());
      points.add(0, proj);
      ILineString newGeom = new GM_LineString(points);
      branching.getMinorRoadExtern().setGeom(newGeom);
      node.getInSections().add(branching.getMinorRoadExtern());
    }

    // delete the internal minor road sections and split the main intern road in
    // two.
    for (IRoadLine road : this.branching.getInternalRoads()) {
      if (branching.getMainRoadIntern().contains(road)) {
        if (road.getGeom().intersects(node.getGeom())) {
          // first copy the main internal road section
          IRoadLine newRoad = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createRoadLine(road.getGeom(), road.getImportance());
          dataset.getRoads().add(newRoad);
          dataset.getRoadNetwork().addSection(newRoad);
          // split the polyline in two
          ILineString[] lines = CommonAlgorithmsFromCartAGen.splitLine(
              road.getGeom(), proj, 0.001);
          road.setGeom(lines[0]);
          newRoad.setGeom(lines[1]);
          // then update the node-section link
          newRoad.setInitialNode(node);
          newRoad.setFinalNode(road.getFinalNode());
          road.setFinalNode(node);
        }
        continue;
      }
      road.eliminate();
    }

    return node;
  }
}
