/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.ArrayList;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class YCrossRoad extends SimpleCrossRoad {

  private TronconDeRoute slipRoad;
  private double orientation;

  public static boolean isYNode(NoeudRoutier node, double flatAngle,
      double yAngle) {

    int degree = node.getArcsEntrants().size() + node.getArcsSortants().size();
    // test the node degree
    if (degree != 3) {
      return false;
    }

    // list the roads connected to the node
    ArrayList<ArcReseau> roads = new ArrayList<ArcReseau>();
    roads.addAll(node.getArcsEntrants());
    roads.addAll(node.getArcsSortants());
    // put all the roads one more time in the list
    roads.addAll(roads);

    // loop on the three roads to find out a flat angle
    for (int i = 0; i < 3; i++) { // (i) = the supposed minor road
      // get the three roads
      ArcReseau minorRoad = roads.get(i);
      ArcReseau majorRoad1 = roads.get(i + 1);
      ArcReseau majorRoad2 = roads.get(i + 2);

      // compute the possible flat angle
      double angle = Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          (GM_LineString) majorRoad1.getGeom(), (GM_LineString) majorRoad2
              .getGeom()));
      double angleDiff = Math.PI - angle;
      // test if the angle difference is greater than threshold
      if (angleDiff > flatAngle) {
        continue;
      }

      // compute the angle with the minor road
      double angleY = Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          (GM_LineString) majorRoad1.getGeom(), (GM_LineString) minorRoad
              .getGeom()));
      // test if the angle is less than threshold
      if (angleY > yAngle) {
        continue;
      }

      // if arrived here, node is a Y-Node
      return true;
    }
    // it is not a T-Node
    return false;
  }

  public YCrossRoad(NoeudRoutier node, double flatAngle, double yAngle) {
    super(node.getGeom());
    // set the degree
    this.setDegree(node.getArcsEntrants().size()
        + node.getArcsSortants().size());
    this.setRoads(new HashSet<TronconDeRoute>());
    // list the roads connected to the node
    ArrayList<ArcReseau> roads = new ArrayList<ArcReseau>();
    for (ArcReseau road : node.getArcsEntrants()) {
      roads.add(road);
      this.getRoads().add((TronconDeRoute) road);
    }
    for (ArcReseau road : node.getArcsSortants()) {
      roads.add(road);
      this.getRoads().add((TronconDeRoute) road);
    }
    // put all the roads one more time in the list
    roads.addAll(roads);

    // loop on the three roads to find out a flat angle
    for (int i = 0; i < 3; i++) { // (i) = the supposed minor road
      // get the three roads
      ArcReseau minorRoad = roads.get(i);
      ArcReseau majorRoad1 = roads.get(i + 1);
      ArcReseau majorRoad2 = roads.get(i + 2);

      // compute the possible flat angle
      double angle = Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          (GM_LineString) majorRoad1.getGeom(), (GM_LineString) majorRoad2
              .getGeom()));
      double angleDiff = Math.PI - angle;
      // test if the angle difference is greater than threshold
      if (angleDiff > flatAngle) {
        continue;
      }

      // compute the angle with the minor road
      double angleY = Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
          (GM_LineString) majorRoad1.getGeom(), (GM_LineString) minorRoad
              .getGeom()));
      // test if the angle is less than threshold
      if (angleY > yAngle) {
        continue;
      }

      // if arrived here, node is a Y-Node
      this.setSlipRoad((TronconDeRoute) minorRoad);
      this.setOrientation(CommonAlgorithmsFromCartAGen.lineAbsoluteOrientation(
          (GM_LineString) minorRoad.getGeom(), node.getGeom().getPosition())
          .getValeur());
      this.setCoord(node.getGeom().getPosition());
      break;
    }
  }

  public void setSlipRoad(TronconDeRoute slipRoad) {
    this.slipRoad = slipRoad;
  }

  public TronconDeRoute getSlipRoad() {
    return this.slipRoad;
  }

  public void setOrientation(double orientation) {
    this.orientation = orientation;
  }

  public double getOrientation() {
    return this.orientation;
  }
}
