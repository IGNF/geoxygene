/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.ArrayList;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class ForkCrossRoad extends SimpleCrossRoad {

  private TronconDeRoute majorRoad, forkRoad1, forkRoad2;
  private double forkAngle, symmAngle, orientation;

  public static boolean isForkNode(NoeudRoutier node, double forkAngle,
      double symmAngle) {

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

    // loop on the 3 roads as we don't know where the fork is
    for (int i = 0; i < 3; i++) { // (i) = the supposed major road
      // get the three roads
      ArcReseau majorRoad = roads.get(i);
      ArcReseau forkRoad1 = roads.get(i + 1);
      ArcReseau forkRoad2 = roads.get(i + 2);

      // compute the fork angle
      double angleFork = Math.abs(CommonAlgorithmsFromCartAGen
          .angleBetween2Lines((GM_LineString) forkRoad1.getGeom(),
              (GM_LineString) forkRoad2.getGeom()));
      // test if the fork angle is greater than threshold
      if (angleFork > Math.PI) {
        angleFork = 2 * Math.PI - angleFork;
      }
      if (Math.abs(angleFork) > forkAngle) {
        continue;
      }

      // compute the two angles with the majorRoad
      double angleSym1 = Math.PI
          - Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
              (GM_LineString) forkRoad1.getGeom(),
              (GM_LineString) majorRoad.getGeom()));
      double angleSym2 = Math.PI
          - Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
              (GM_LineString) forkRoad2.getGeom(),
              (GM_LineString) majorRoad.getGeom()));

      // test if the difference between the 2 angles is bigger than threshold
      double diffAngles = Math.abs(angleSym1 - angleSym2);

      if (diffAngles > symmAngle) {
        continue;
      }

      // if arrived here, node is a Fork-Node
      return true;
    }
    // this is not a Fork-Node
    return false;
  }

  public ForkCrossRoad(NoeudRoutier node, double forkAngle, double symmAngle) {
    super(node.getGeom());
    this.setNode(node);
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
    // on fait une boucle sur les 3 arcs car on ne sait pas où se situe la
    // fourche
    for (int i = 0; i < 3; i++) { // (i) = la principale supposée
      // get the three roads
      this.majorRoad = (TronconDeRoute) roads.get(i);
      this.forkRoad1 = (TronconDeRoute) roads.get(i + 1);
      this.forkRoad2 = (TronconDeRoute) roads.get(i + 2);

      // compute the fork angle
      this.forkAngle = Math.abs(CommonAlgorithmsFromCartAGen
          .angleBetween2Lines((GM_LineString) this.forkRoad1.getGeom(),
              (GM_LineString) this.forkRoad2.getGeom()));
      // test if the fork angle is greater than threshold
      if (Math.abs(this.forkAngle) > forkAngle) {
        continue;
      }

      // compute the two angles with the majorRoad
      double angleSym1 = Math.PI
          - Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
              (GM_LineString) this.forkRoad1.getGeom(),
              (GM_LineString) this.majorRoad.getGeom()));
      double angleSym2 = Math.PI
          - Math.abs(CommonAlgorithmsFromCartAGen.angleBetween2Lines(
              (GM_LineString) this.forkRoad2.getGeom(),
              (GM_LineString) this.majorRoad.getGeom()));

      // test if the difference between the 2 angles is bigger than threshold
      this.symmAngle = Math.abs(angleSym1 - angleSym2);
      if (this.symmAngle > symmAngle) {
        continue;
      }

      break;
    }

    // if arrived here, the minor and major roads are correct
    this.orientation = CommonAlgorithmsFromCartAGen.lineAbsoluteOrientation(
        (GM_LineString) this.majorRoad.getGeom(), node.getGeom().getPosition())
        .getValeur();
    this.setCoord(node.getGeom().getPosition());
  }

  public TronconDeRoute getMajorRoad() {
    return this.majorRoad;
  }

  public void setMajorRoad(TronconDeRoute majorRoad) {
    this.majorRoad = majorRoad;
  }

  public TronconDeRoute getForkRoad1() {
    return this.forkRoad1;
  }

  public void setForkRoad1(TronconDeRoute forkRoad1) {
    this.forkRoad1 = forkRoad1;
  }

  public TronconDeRoute getForkRoad2() {
    return this.forkRoad2;
  }

  public void setForkRoad2(TronconDeRoute forkRoad2) {
    this.forkRoad2 = forkRoad2;
  }

  public double getForkAngle() {
    return this.forkAngle;
  }

  public void setForkAngle(double forkAngle) {
    this.forkAngle = forkAngle;
  }

  public double getSymmAngle() {
    return this.symmAngle;
  }

  public void setSymmAngle(double symmAngle) {
    this.symmAngle = symmAngle;
  }

  public double getOrientation() {
    return this.orientation;
  }

  public void setOrientation(double orientation) {
    this.orientation = orientation;
  }

}
