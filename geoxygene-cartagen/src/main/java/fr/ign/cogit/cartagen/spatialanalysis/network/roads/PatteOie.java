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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.CarrefourComplexeImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class PatteOie extends CarrefourComplexeImpl {

  private HashSet<ArcReseau> mainRoadIntern;
  private ArcReseau minorRoadExtern;
  private RondPoint roundAbout;

  public HashSet<ArcReseau> getMainRoadIntern() {
    return this.mainRoadIntern;
  }

  public void setMainRoadIntern(HashSet<ArcReseau> mainRoadIntern) {
    this.mainRoadIntern = mainRoadIntern;
  }

  public ArcReseau getMinorRoadExtern() {
    return this.minorRoadExtern;
  }

  public void setMinorRoadExtern(ArcReseau minorRoadExtern) {
    this.minorRoadExtern = minorRoadExtern;
  }

  public RondPoint getRoundAbout() {
    return this.roundAbout;
  }

  public void setRoundAbout(RondPoint roundAbout) {
    this.roundAbout = roundAbout;
  }

  /**
   * <p>
   * Determine if a road block object is a branching crossroad or not. Uses
   * graph measures (3 nodes with degree 3) and shape measures. A branching
   * crossroad should be triangle shaped. This version of the method does not
   * use the Gothic spatial queries but requires the cartagen objects as
   * entries.
   * 
   * @param block : the tested road block
   * @param sizeThreshold : the maximum area of a branching crossroad (2500 m²
   *          advised)
   * @param surfDistThreshold : the maximum surfacic distance between the block
   *          and the triangle formed by the three nodes of the block. (0.5
   *          advised)
   * @param simples : the simple crossroads of the network
   * @param roundabouts : the roundabouts of the network
   * @return true if the block is a branching crossroad.
   * 
   */
  public static boolean isBranchingCrossRoad(Ilot block, double sizeThreshold,
      double surfDistThreshold, IFeatureCollection<NoeudRoutier> simples,
      IFeatureCollection<RondPoint> roundabouts) {

    // get the block geometry
    IPolygon geom = (GM_Polygon) block.getGeom();
    // get the area of the geometry
    double area = geom.area();

    // if area is > to threshold, return false
    if (area > sizeThreshold) {
      return false;
    }

    // now check if the block has 3 degree 3 nodes
    // get the crossroads with gothic spatial index
    Collection<NoeudRoutier> crossroads = simples.select(geom);

    // particular case with a 4 node branching crossroad
    if (crossroads.size() == 4) {
      // if the fourth node is not degree 3, return false
      // loop on the crossroads to check their degree
      for (NoeudRoutier cross : crossroads) {
        if (cross.getDegre() != 3) {
          return false;
          // now check that three of the nodes are aligned
        }
      }

      ArrayList<NoeudRoutier> list = new ArrayList<NoeudRoutier>(crossroads);
      // sort the list so as to have consecutive nodes
      // check if 1 is connected to 0
      Set<TronconDeRoute> connected = list.get(1).getRoutes();
      connected.retainAll(list.get(0).getRoutes());
      if (connected.size() == 0) {
        // swap with number 2
        NoeudRoutier simple = list.get(1);
        list.remove(simple);
        list.add(2, simple);
      } else {
        // check if 2 is connected to 1
        connected.clear();
        connected = list.get(1).getRoutes();
        connected.retainAll(list.get(2).getRoutes());
        if (connected.size() == 0) {
          // swap with number 3
          NoeudRoutier simple = list.get(2);
          list.remove(simple);
          list.add(3, simple);
        }
      } // Now the list is sorted: 0 connected to 1 connected to 2 connected to
      // 3
      // Add all elements of the list at the end of the list(=> 01230123) to be
      // able
      // to go through all elements once as if it was 'circular'
      list.addAll(list);
      // Find the 'middle node' (if any) = the one that is flat => in the
      // 'middle'
      // of a side of the triangle (provided we have a branching crossroad)
      NoeudRoutier middle = null;
      for (int i = 0; i < crossroads.size(); i++) {
        IDirectPosition pt1 = list.get(i).getGeom().getPosition();
        IDirectPosition pt2 = list.get(i + 1).getGeom().getPosition();
        IDirectPosition pt3 = list.get(i + 2).getGeom().getPosition();
        // compute the angle between the 3 points
        double angle = Angle.angleTroisPoints(pt1, pt2, pt3).getValeur();
        // check if the angle is flat
        if (Math.abs(Math.PI - angle) < Math.PI / 8) {
          middle = list.get(i + 1);
        }
      }
      // If no 'middle' (no flat angle) then it is not a branching crossroad.
      if (middle == null) {
        return false;
      }

      // now check that the form is triangular
      // first, build the triangle: all nodes except the 'middle'
      DirectPositionList points = new DirectPositionList();
      for (NoeudRoutier cross : crossroads) {
        if (cross.equals(middle)) {
          continue;
        }
        points.add(cross.getGeom().centroid());
      }
      points.add(points.get(0));
      ILineString ring = new GM_LineString(points);
      GM_Polygon triangle = new GM_Polygon(ring);

      // then, compare the triangle to the block geometry
      double surfDist = Distances.distanceSurfacique(triangle, geom);
      if (surfDist > surfDistThreshold) {
        return false;
      }

      // arrived here, it is a branching crossroad
      return true;
    } // End particular case with a 4 node branching crossroad

    // if there is no 3 crossroads, it is not a branching crossroad
    if (crossroads.size() != 3) {
      return false;
    }

    // Case where the number of crossroads is 3
    // Test on the degrees of the nodes. In a branching crossroad, all nodes
    // should be of degree 3 or 4. Not more than 1node should be of degree 4.
    int nbDeg4 = 0;
    // loop on the crossroads to check their degree
    for (NoeudRoutier cross : crossroads) {
      // if the degree is not 3 or 4, it is not a branching crossroad
      if (cross.getDegre() != 3 && (cross.getDegre() != 4)) {
        return false;
      }
      // only one degree 4 node is accepted
      if (cross.getDegre() == 4) {
        nbDeg4 += 1;
        if (nbDeg4 > 1) {
          return false;
        }
      }
    }// end test on the degrees

    // check if the block is connected to a roundabout
    Collection<RondPoint> rounds = roundabouts.select(geom);

    if (rounds.size() > 0) {
      // get the area of the roundabout
      RondPoint round = rounds.iterator().next();
      double areaRound = round.getArea();
      // if the block area is twice bigger than the roundabout, it is not a
      // branching crossroad
      if (area > 2 * areaRound) {
        return false;
      }

      // there, the block is a branching crossroad

    } else {
      // here, test the shape of the block by comparing it to a triangle
      // first, build the triangle
      DirectPositionList points = new DirectPositionList();
      for (NoeudRoutier cross : crossroads) {
        points.add(cross.getGeom().centroid());
      }
      points.add(crossroads.iterator().next().getGeom().centroid());
      ILineString ring = new GM_LineString(points);
      GM_Polygon triangle = new GM_Polygon(ring);

      // then, compare the triangle to the block geometry
      double surfDist = Distances.distanceSurfacique(triangle, geom);

      if (surfDist > surfDistThreshold) {
        return false;
      }

      // there, the block is a branching crossroad
    }

    return true;
  }

  /**
   * Constructor from the complete components of a characterised branching
   * crossroad.
   * 
   * @param geom
   * @param simples
   * @param internalRoads
   * @param externalRoads
   * @param mainRoadIntern
   * @param minorRoadExtern
   * @param roundAbout
   * @author GTouya
   */
  public PatteOie(IPolygon geom, Collection<NoeudReseau> simples,
      HashSet<TronconDeRoute> internalRoads,
      HashSet<TronconDeRoute> externalRoads, HashSet<ArcReseau> mainRoadIntern,
      ArcReseau minorRoadExtern, RondPoint roundAbout) {
    this.setGeom(geom);
    this.setRoutesExternes(externalRoads);
    this.setRoutesInternes(internalRoads);
    this.setNoeuds(simples);
    this.mainRoadIntern = mainRoadIntern;
    this.minorRoadExtern = minorRoadExtern;
    this.roundAbout = roundAbout;
  }

  /**
   * Constructor from a block that represents a branching crossroad
   * (isBranchingCrossRoad returns true). The characterisation of the branching
   * crossroad is made based on the block geometry and topology.
   * 
   * @param block the block that is a branching crossroad
   * @param roads the roads from the network used here
   * @param crossRoads the crossroads from the network used here
   * @author GTouya
   */
  public PatteOie(Ilot block, IFeatureCollection<TronconDeRoute> roads,
      IFeatureCollection<NoeudRoutier> crossRoads) {
    this.setGeom(block.getGeom());
    this.setRoutesExternes(new HashSet<TronconDeRoute>());
    this.setRoutesInternes(new HashSet<TronconDeRoute>());
    this.setNoeuds(new HashSet<NoeudReseau>());
    this.mainRoadIntern = new HashSet<ArcReseau>();
    // characterise the new branching crossroad as a complex crossroad
    IGeometry blockGeom = block.getGeom();
    this.getNoeuds().addAll(crossRoads.select(blockGeom));
    Collection<TronconDeRoute> roundRoads = roads.select(blockGeom);
    // loop on the roundRoads to find out if they are internal or external to
    // the roundabout
    for (TronconDeRoute r : roundRoads) {
      if (block.getGeom().buffer(0.5).contains(r.getGeom())) {
        this.getRoutesInternes().add(r);
      } else {
        this.getRoutesExternes().add(r);
      }
    }
  }

  /**
   * find the characteristic roads of the branching crossroad. The link with the
   * roundabouts has to be instanciated before to optimise the characterisation
   * 
   * @author GTouya
   */
  public void characteriseBranching() {
    this.findMainRoadIntern();
    this.findMinorRoadExtern();
  }

  private void findMainRoadIntern() {
    // first, case with 4 nodes
    if (this.getNoeuds().size() == 4) {
      // get the middle node
      ArrayList<NoeudRoutier> list = new ArrayList<NoeudRoutier>();
      for (NoeudReseau noeudReseau : this.getNoeuds())
        list.add((NoeudRoutier) noeudReseau);
      // sort the list so as to have consecutive nodes
      // check if 1 is connected to 0
      Set<TronconDeRoute> connected = list.get(1).getRoutes();
      connected.retainAll(list.get(0).getRoutes());
      if (connected.size() == 0) {
        // swap with number 2
        NoeudRoutier simple = list.get(1);
        list.remove(simple);
        list.add(2, simple);
      } else {
        // check if 2 is connected to 1
        connected.clear();
        connected = list.get(1).getRoutes();
        connected.retainAll(list.get(2).getRoutes());
        if (connected.size() == 0) {
          // swap with number 3
          NoeudRoutier simple = list.get(2);
          list.remove(simple);
          list.add(3, simple);
        }
      }
      list.addAll(list);
      NoeudRoutier middle = null;
      for (int i = 0; i < 4; i++) {
        IDirectPosition pt1 = list.get(i).getGeom().getPosition();
        IDirectPosition pt2 = list.get(i + 1).getGeom().getPosition();
        IDirectPosition pt3 = list.get(i + 2).getGeom().getPosition();
        // compute the angle between the 3 points
        double angle = Angle.angleTroisPoints(pt1, pt2, pt3).getValeur();
        // check if the angle is flat
        if (Math.abs(Math.PI - angle) < Math.PI / 8) {
          middle = list.get(i + 1);
        }
      }
      // get the internal roads connected to middle
      if (middle != null) {
        Set<TronconDeRoute> mainRoads = middle.getRoutes();
        mainRoads.retainAll(this.getRoutesInternes());
        this.mainRoadIntern.addAll(mainRoads);
      }
    }

    // then, easy case with a roundabout
    if (this.roundAbout != null) {
      HashSet<TronconDeRoute> commons = new HashSet<TronconDeRoute>(this
          .getRoutesInternes());
      commons.retainAll(this.roundAbout.getRoutesInternes());
      this.mainRoadIntern.add(commons.iterator().next());
      return;
    }

    // regular case, it's the road having a flat angle with two of the external
    // roads
    double totalMax = Math.PI;
    for (TronconDeRoute i : this.getRoutesInternes()) {
      int nbFlatAngles = 0;
      double totalAngle = 0.0;
      IGeometry geomI = i.getGeom();
      // loop on the external roads
      for (TronconDeRoute e : this.getRoutesExternes()) {
        IGeometry geomE = e.getGeom();
        // if the geometries do not intersect, continue
        if (!geomI.intersects(geomE)) {
          continue;
        }
        // compute the angle between the two lines
        double angle = CommonAlgorithmsFromCartAGen.angleBetween2Lines(
            (ILineString) geomI, (ILineString) geomE);
        // put the angle in [0,Pi/2]
        if (angle > Math.PI / 2) {
          angle = Math.PI - angle;
        }
        // test if the angle is flat
        if (angle < Math.PI / 8) {
          nbFlatAngles += 1;
          totalAngle += angle;
        }
      }
      // now test if the road is the main road
      if (nbFlatAngles == 2 && totalAngle < totalMax) {
        this.mainRoadIntern.add(i);
        totalMax = totalAngle;
      }
    }
  }

  /**
   * Among the roads forming the branching crossroad, find the external road
   * that represents the secondary road of the crossroad, the one perpendicular
   * to the major internal road (representing the main road).
   * 
   * @author GTouya
   */
  private void findMinorRoadExtern() {
    // first, case with 4 nodes
    if (this.getNoeuds().size() == 4) {
      Iterator<ArcReseau> i = this.mainRoadIntern.iterator();
      IGeometry geom1 = i.next().getGeom();
      IGeometry geom2 = i.next().getGeom();
      // it's the one connected to none of the main roads
      for (TronconDeRoute r : this.getRoutesExternes()) {
        IGeometry geomExt = r.getGeom();
        if (!geom1.intersects(geomExt) && !geom2.intersects(geomExt)) {
          this.minorRoadExtern = r;
          break;
        }
      }
      return;
    }

    // the minorRoadExtern is the external road that is not connected to
    // mainRoadIntern
    IGeometry geom1 = this.mainRoadIntern.iterator().next().getGeom();
    for (TronconDeRoute r : this.getRoutesExternes()) {
      IGeometry geomExt = r.getGeom();
      if (!geom1.intersects(geomExt)) {
        this.minorRoadExtern = r;
        break;
      }
    }
  }

  public static PatteOie getBranchingCrossRoad(Ilot block,
      Collection<PatteOie> branchs) {
    for (PatteOie b : branchs) {
      if (block.getGeom().equals(b.getGeom())) {
        return b;
      }
    }
    return null;
  }
}
