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

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;

public abstract class SimpleCrossRoad extends AbstractFeature {

  public SimpleCrossRoad(IPoint point) {
    super(point);
    this.id = SimpleCrossRoad.counter.getAndIncrement();
  }

  private HashSet<TronconDeRoute> roads;
  private IDirectPosition coord;
  private int degree;
  private int id;

  private static AtomicInteger counter = new AtomicInteger();

  public IDirectPosition getCoord() {
    return this.coord;
  }

  public void setCoord(IDirectPosition coord) {
    this.coord = coord;
  }

  public int getDegree() {
    return this.degree;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public static String[] getAllSubClasses() {
    String[] classes = new String[6];
    classes[0] = TCrossRoad.class.toString();
    classes[1] = YCrossRoad.class.toString();
    classes[2] = ForkCrossRoad.class.toString();
    classes[3] = PlusCrossRoad.class.toString();
    classes[4] = StarCrossRoad.class.toString();
    classes[5] = StandardCrossRoad.class.toString();
    return classes;
  }

  public static HashSet<SimpleCrossRoad> classifyCrossRoads(
      Collection<TronconDeRoute> roads, double flatAngle, double bisAngle,
      double yAngle, double symmAngle, double forkAngle, double squareAngle,
      double symmCrossAngle) {
    HashSet<SimpleCrossRoad> crossRoads = new HashSet<SimpleCrossRoad>();
    // get the nodes from the roads
    HashSet<NoeudRoutier> nodes = new HashSet<NoeudRoutier>();
    for (TronconDeRoute road : roads) {
      nodes.add((NoeudRoutier) road.getNoeudInitial());
      nodes.add((NoeudRoutier) road.getNoeudFinal());
    }
    // loop on the nodes to classify them
    for (NoeudRoutier node : nodes) {
      // **********
      // Y-Node CASE
      // **********
      if (YCrossRoad.isYNode(node, flatAngle / 2.0, yAngle)) {
        // build the new Y-node
        crossRoads.add(new YCrossRoad(node, flatAngle, yAngle));
        continue;
      }

      // **********
      // T-Node CASE
      // **********
      if (TCrossRoad.isTNode(node, flatAngle, bisAngle)) {
        // build the new T-Node
        crossRoads.add(new TCrossRoad(node, flatAngle, bisAngle));
        continue;
      }

      // **********
      // Fork-Node CASE
      // **********
      if (ForkCrossRoad.isForkNode(node, forkAngle, symmAngle)) {
        // build the new fork node
        crossRoads.add(new ForkCrossRoad(node, forkAngle, symmAngle));
        continue;
      }

      // now the node is either a star, a cross or a standard crossroad.

      // **************
      // Cross-Node CASE
      // **************
      if (PlusCrossRoad.isCrossNode(node, symmCrossAngle, squareAngle)) {
        PlusCrossRoad plusCross = new PlusCrossRoad(node, symmCrossAngle);
        plusCross.setSquareAngle(squareAngle);
        crossRoads.add(plusCross);
        continue;
      }

      // **************
      // Star-Node CASE
      // **************
      if (StarCrossRoad.isStarNode(node, flatAngle)) {
        crossRoads.add(new StarCrossRoad(node, flatAngle));
        continue;
      }

      // ******************
      // Standard CASE
      // ******************
      // arrived here, the node has no particular character and is
      // considered as standard
      crossRoads.add(new StandardCrossRoad(node));

    }

    return crossRoads;
  }

  public void setRoads(HashSet<TronconDeRoute> roads) {
    this.roads = roads;
  }

  public HashSet<TronconDeRoute> getRoads() {
    return roads;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }
}
