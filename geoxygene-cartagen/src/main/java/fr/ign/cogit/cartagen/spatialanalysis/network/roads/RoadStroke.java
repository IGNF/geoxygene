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

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class RoadStroke extends Stroke {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private int nbTNodes;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public RoadStroke(StrokesNetwork network, ArcReseau root) {
    super(network, root);
  }

  public RoadStroke(RoadStrokesNetwork network, ArrayList<ArcReseau> features,
      ILineString geomStroke) {
    super(network, features, geomStroke);
  }

  // Getters and setters //
  public void setNbTNodes(int nbTNodes) {
    this.nbTNodes = nbTNodes;
  }

  public int getNbTNodes() {
    return this.nbTNodes;
  }

  // Other public methods //
  public void computeNbTNodesInStroke() {
    // TODO
  }

  public void buildOneSideRoundabout(boolean side,
      HashSet<String> attributeNames, double deviatAngle, double deviatSum,
      boolean noStop, IFeatureCollection<RondPoint> roundabouts) {
    // get the following network segments of the root of this stroke
    NoeudReseau node = null;
    if (side) {
      node = this.getRoot().getNoeudInitial();
    } else {
      node = this.getRoot().getNoeudFinal();
    }
    HashSet<ArcReseau> followers = Stroke.getFollowing(this.getRoot(), node);
    if (followers.size() == 0) {
      return;
    }

    // test if the root of the strokes leads to a roundabout
    boolean round = false;
    RondPoint roundabout = null;
    for (RondPoint r : roundabouts) {
      if (r.getRoutesExternes().contains(this.getRoot())) {
        // test if the roundabout is in the side direction
        TronconDeRoute next = (TronconDeRoute) followers.iterator().next();
        if (r.getRoutesInternes().contains(next)) {
          // replace the followers by the external roads
          followers.clear();
          followers.addAll(r.getRoutesExternes());
          followers.remove(this.getRoot());
          round = true;
          roundabout = r;
        }
      }
    }

    // loop while there is a segment with a good continuity
    ArcReseau next = this.getRoot();
    NoeudReseau nextNode = next.getNoeudInitial();
    if (!side) {
      nextNode = next.getNoeudFinal();
    }
    boolean continuity = true;
    while (continuity) {
      // get the best candidate among the followers (the one with best
      // continuity)
      ArcReseau best = null;
      if (round) {
        best = this.chooseNextSegmentNoStopRoundabout(next, followers,
            attributeNames, deviatAngle, deviatSum, roundabout);
      } else if (noStop) {
        best = this.chooseNextSegmentNoStop(next, followers, attributeNames,
            deviatAngle, deviatSum);
      } else {
        best = this.chooseNextSegment(next, followers, attributeNames,
            deviatAngle, deviatSum);
      }

      // if best is null, break
      if (best == null) {
        break;
      }

      // if round is true, a fictive road line has to be built
      if (round && roundabout != null) {
        IDirectPositionList points = new DirectPositionList();
        IDirectPosition centre = roundabout.getGeom().centroid();
        // add nearest point of next geom to the list
        IDirectPosition first1 = next.getGeom().coord().get(0);
        IDirectPosition last1 = next.getGeom().coord().get(
            next.getGeom().coord().size() - 1);
        if (centre.distance(first1) < centre.distance(last1)) {
          points.add(first1);
        } else {
          points.add(last1);
        }
        // add centre point to the list
        points.add(centre);
        // add nearest point of best geom to the list
        IDirectPosition first2 = best.getGeom().coord().get(0);
        IDirectPosition last2 = best.getGeom().coord().get(
            best.getGeom().coord().size() - 1);
        if (centre.distance(first2) < centre.distance(last2)) {
          points.add(first2);
        } else {
          points.add(last2);
        }
        // build the new line geometry
        ILineString line = new GM_LineString(points);
        // build the fictive road going through the roundabout
        RoadLineImpl road = new RoadLineImpl(next.getReseau(), true, line);
        // update the network structure
        NoeudRoutier init = (NoeudRoutier) next.getNoeudFinal();
        if (!init.getGeom().coord().get(0).equals(points.get(0))) {
          init = (NoeudRoutier) next.getNoeudInitial();
        }
        NoeudRoutier fin = (NoeudRoutier) best.getNoeudInitial();
        if (!fin.getGeom().coord().get(0).equals(points.get(points.size() - 1))) {
          fin = (NoeudRoutier) best.getNoeudFinal();
        }
        init.getArcsSortants().add(road);
        fin.getArcsEntrants().add(road);
        // set the road attributes
        for (String name : attributeNames) {
          road.setAttribute(name, next.getAttribute(name));
        }
        if (side) {
          this.getFeatures().add(0, road);
        } else {
          this.getFeatures().add(road);
        }
        this.getNetwork().getGroupedFeatures().add(road);
        next = road;
        nextNode = fin;
      }

      // add this to the 2 sets (the network one and the stroke one)
      if (side) {
        this.getFeatures().add(0, best);
      } else {
        this.getFeatures().add(best);
      }
      this.getNetwork().getGroupedFeatures().add(best);

      // update the next node and then get 'best' followers
      if (nextNode.getGeom().coord().get(0).equals(
          best.getNoeudFinal().getGeom().coord().get(0)))
        nextNode = best.getNoeudInitial();
      else
        nextNode = best.getNoeudFinal();

      followers.clear();
      followers = Stroke.getFollowing(best, nextNode);
      // if there is no follower, break
      if (followers.size() == 0) {
        break;
      }

      // update the 'next' segment with 'best'
      next = best;

      // finally, update the round variable
      round = false;
      if (followers.size() == 0) {
        continue;
      }
      for (RondPoint r : roundabouts) {
        if (r.getRoutesExternes().contains(next)) {
          // test if the roundabout is in the side direction
          TronconDeRoute nextRoad = (TronconDeRoute) followers.iterator()
              .next();
          if (r.getRoutesInternes().contains(nextRoad)) {
            // replace the followers by the external roads
            followers.clear();
            followers.addAll(r.getRoutesExternes());
            followers.remove(next);
            round = true;
            roundabout = r;
          }
        }
      }
    }// while(continuity)

  }

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  private ArcReseau chooseNextSegmentNoStopRoundabout(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames,
      double deviatAngle, double deviatSum, RondPoint round) {
    // first, filter the followers
    this.filterFollowers(arc, followers);
    if (followers.size() == 0) {
      return null;
    }
    // then, filter the followers from the attributes
    this.filterByAttributeContinuity(arc, followers, attributeNames);
    if (followers.size() == 0) {
      return null;
    }

    boolean continuity = true;
    ArcReseau bestSegment = null;
    // if there is only one follower left, choose it as the best follower
    if (followers.size() == 1) {
      ArcReseau follower = followers.iterator().next();
      bestSegment = follower;
      continuity = true;
    } else {
      // get the initial geometry
      ILineString line = (ILineString) arc.getGeom();
      // extend the geometry to the roundabout centre
      IDirectPosition centre = round.getGeom().centroid();
      IDirectPositionList points1 = new DirectPositionList();
      points1.addAll(line.coord());
      IDirectPosition nearest = CommonAlgorithmsFromCartAGen
          .getNearestVertexFromPoint(line, centre);
      if (nearest.equals(points1.get(0))) {
        points1.add(0, centre);
      } else {
        points1.add(centre);
      }
      ILineString geom1 = new GM_LineString(points1);

      // Loop on the followers to choose the best continuity
      double minDiff = Math.PI;
      for (ArcReseau follower : followers) {
        // get the follower geometry
        ILineString geomFoll = (ILineString) follower.getGeom();
        // extend the geometry to the roundabout centre
        IDirectPositionList points2 = new DirectPositionList();
        points2.addAll(geomFoll.coord());
        nearest = CommonAlgorithmsFromCartAGen.getNearestVertexFromPoint(
            geomFoll, centre);
        if (nearest.equals(points2.get(0))) {
          points2.add(0, centre);
        } else {
          points2.add(centre);
        }
        ILineString geom2 = new GM_LineString(points2);
        // get the continuity difference with this follower
        double diffContinuity = this.goodContinuityDifference(geom1, geom2,
            deviatAngle, deviatSum);

        if (diffContinuity > -1.0) {
          // il y a un tronçon de bonne continuité
          continuity = true;
          if (diffContinuity < minDiff) {
            // this is the current best continuity
            // update the difference
            minDiff = diffContinuity;
            // change the bestSegment
            bestSegment = follower;
          }// if (diffContinuity < minDiff)
        }// if (diffContinuity > -1.0)
      }// loop on the followers
    }

    if ((continuity) && !this.getFeatures().contains(bestSegment)) {
      return bestSegment;
    }
    return null;
  }
}
