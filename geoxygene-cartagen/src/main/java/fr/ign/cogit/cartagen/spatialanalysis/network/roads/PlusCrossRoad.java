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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class PlusCrossRoad extends SimpleCrossRoad {

  private HashMap<TronconDeRoute[], Double> mapPairs;
  private double squareAngle;

  public static boolean isCrossNode(NoeudRoutier node, double flatAngle,
      double squareAngle) {

    int degree = node.getArcsEntrants().size() + node.getArcsSortants().size();
    // test the node degree
    if (degree != 4) {
      return false;
    }

    // now, check if the shape is really a star
    // the road orientation is used to determine the star shape
    // first, exclude cycle roads
    HashSet<ArcReseau> roads = new HashSet<ArcReseau>();
    roads.addAll(node.getArcsEntrants());
    roads.addAll(node.getArcsSortants());
    if (roads.size() != degree) {
      return false;
    }

    HashMap<ArcReseau, Double> mapOrient = new HashMap<ArcReseau, Double>();
    for (ArcReseau road : roads) {
      // compute road absolute orientation between 0 and 2Pi
      double orientation = CommonAlgorithmsFromCartAGen
          .lineAbsoluteOrientation((GM_LineString) road.getGeom(),
              node.getGeom().getPosition()).getValeur();
      // star shaped crossroads have central symmetry so we put angles in [0,Pi]
      // orientations so that two symmetric roads might have the same
      // orientation.
      if (orientation > Math.PI) {
        orientation = 2.0 * Math.PI - orientation;
      }
      mapOrient.put(road, new Double(orientation));
    }
    // now, the roads with same orientation have to be paired. If a road does
    // not match
    // with another, it is not a star shaped crossroad.

    // a map to store road pairs and their orientation
    HashMap<ArcReseau[], Double> mapPairs = new HashMap<ArcReseau[], Double>();

    // loop on the map of oriented roads
    HashSet<ArcReseau> treated = new HashSet<ArcReseau>();
    for (ArcReseau road : mapOrient.keySet()) {
      // get the road orientation
      double orientRoad = mapOrient.get(road).doubleValue();
      // count road as treated
      treated.add(road);

      // loop once more on the roads
      HashSet<ArcReseau> roadsCopy = new HashSet<ArcReseau>();
      roadsCopy.addAll(mapOrient.keySet());
      for (ArcReseau roadOther : roadsCopy) {
        if (treated.contains(roadOther)) {
          continue;
        }
        // get the other orientation
        double orientOther = mapOrient.get(roadOther).doubleValue();
        if (Math.abs(orientRoad - orientOther) < flatAngle) {
          // then the two roads are symmetrical
          ArcReseau[] pair = new ArcReseau[2];
          pair[0] = road;
          pair[1] = roadOther;
          mapPairs.put(pair, (new Double((orientRoad + orientOther) / 2.0)));
          treated.add(roadOther);
          break;
        }
      }
    }

    // test the number of pairs
    if (mapPairs.keySet().size() != 2) {
      return false;
    }
    // test if the two pairs are square
    Iterator<ArcReseau[]> i = mapPairs.keySet().iterator();
    ArcReseau[] pair1 = i.next();
    ArcReseau[] pair2 = i.next();
    double orient1 = mapPairs.get(pair1).doubleValue();
    double orient2 = mapPairs.get(pair2).doubleValue();
    if (Math.abs(Math.PI / 2 - Math.abs(orient1 - orient2)) > squareAngle) {
      return false;
    }

    return true;
  }

  public PlusCrossRoad(NoeudRoutier node, double symmCrossAngle) {
    super(node.getGeom());
    this.setNode(node);
    this.setDegree(node.getArcsEntrants().size()
        + node.getArcsSortants().size());
    this.setRoads(new HashSet<TronconDeRoute>());
    // list the roads connected to the node
    HashSet<ArcReseau> roads = new HashSet<ArcReseau>();
    for (ArcReseau road : node.getArcsEntrants()) {
      roads.add(road);
      this.getRoads().add((TronconDeRoute) road);
    }
    for (ArcReseau road : node.getArcsSortants()) {
      roads.add(road);
      this.getRoads().add((TronconDeRoute) road);
    }
    this.setCoord(node.getGeom().getPosition());

    HashMap<ArcReseau, Double> mapOrient = new HashMap<ArcReseau, Double>();
    for (ArcReseau road : roads) {
      // compute road absolute orientation between 0 and 2Pi
      double orientation = CommonAlgorithmsFromCartAGen
          .lineAbsoluteOrientation((GM_LineString) road.getGeom(),
              node.getGeom().getPosition()).getValeur();
      // star shaped crossroads have central symmetry so we put angles in [0,Pi]
      // orientations so that two symmetric roads might have the same
      // orientation.
      if (orientation > Math.PI) {
        orientation = 2.0 * Math.PI - orientation;
      }
      mapOrient.put(road, new Double(orientation));
    }
    // now, the roads with same orientation have to be paired. If a road does
    // not match
    // with another, it is not a star shaped crossroad.

    // a map to store road pairs and their orientation
    this.mapPairs = new HashMap<TronconDeRoute[], Double>();

    // loop on the map of oriented roads
    HashSet<ArcReseau> treated = new HashSet<ArcReseau>();
    for (ArcReseau road : mapOrient.keySet()) {
      // get the road orientation
      double orientRoad = mapOrient.get(road).doubleValue();
      // count road as treated
      treated.add(road);

      // loop once more on the roads
      HashSet<ArcReseau> roadsCopy = new HashSet<ArcReseau>();
      roadsCopy.addAll(mapOrient.keySet());
      for (ArcReseau roadOther : roadsCopy) {
        if (treated.contains(roadOther)) {
          continue;
        }
        // get the other orientation
        double orientOther = mapOrient.get(roadOther).doubleValue();
        if (Math.abs(orientRoad - orientOther) < symmCrossAngle) {
          // then the two roads are symmetrical
          TronconDeRoute[] pair = new TronconDeRoute[2];
          pair[0] = (TronconDeRoute) road;
          pair[1] = (TronconDeRoute) roadOther;
          this.mapPairs.put(pair, new Double((orientRoad + orientOther) / 2.0));
          treated.add(roadOther);
          break;
        }
      }
    }

    // test if the two pairs are square
    Iterator<TronconDeRoute[]> i = this.mapPairs.keySet().iterator();
    TronconDeRoute[] pair1 = i.next();
    TronconDeRoute[] pair2 = i.next();
    double orient1 = this.mapPairs.get(pair1).doubleValue();
    double orient2 = this.mapPairs.get(pair2).doubleValue();
    this.setSquareAngle(Math.abs(orient1 - orient2));
  }

  public void setSquareAngle(double squareAngle) {
    this.squareAngle = squareAngle;
  }

  public double getSquareAngle() {
    return this.squareAngle;
  }

}
