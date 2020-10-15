package fr.ign.cogit.geoxygene.matching.hmmm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 * 
 * Minimalistic implementation of "Hidden Markov Map Matching Through Noise and
 * Sparseness", Paul Newson and John Krumm, 17th ACM SIGSPATIAL International
 * Conference on Advances in Geographic Information Systems (ACM SIGSPATIAL GIS
 * 2009).
 * @see {@link http
 *      ://research.microsoft.com/en-us/um/people/jckrumm/MapMatchingData
 *      /data.htm}
 *      <p>
 *      In this implementation, we only use the proposed emission and transition
 *      probabilities but most of the breaks were ignored.
 
 * @author Julien Perret
 */
public abstract class HMMMapMatcher {
  /**
   * Static logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(HMMMapMatcher.class.getName());
  /**
   * Standard deviation of GPS measurements. {@latex[ \sigma _z }
   */
  private double sigmaZ = 0.0;
  /**
   * Internal use only: contains the value {@latex[ \sqrt 2\pi }\sigma _z }
   */
  private double emissionDenominator = 0.0;
  /**
   * The list of gps points to match.
   */
  private IFeatureCollection<? extends IFeature> points = null;

  /**
   * @return the list of gps points to match.
   */
  public IFeatureCollection<? extends IFeature> getPoints() {
    return points;
  }

  /**
   * Route Localization distance. This parameter is used in order to limit the
   * number of roads we try to match a gps point with.
   */
  private double selection = 0.0;
  /**
   * Beta describes the difference between route distances and great circle
   * distances.
   */
  private double beta = 0.0;
  /**
   * Distance above which a route distance is considered not reasonable. More
   * precisely, this parameter is used in addition to the circle distance
   * between 2 consecutive gps points. It is also used in the computation of the
   * shortest path to stop the computation when it becomes greater than the sum
   * of these 2 distances.
   */
  private double distanceLimit = 0.0;

  /**
   * @return the distance above which a route distance is considered not
   *         reasonable.
   */
  public double getDistanceLimit() {
    return distanceLimit;
  }

  /**
   * The topological map used to match the gps point to.
   */
  private CarteTopo networkMap = null;

  /**
   * @return The topological map used to match the gps point to.
   */
  public CarteTopo getNetworkMap() {
    return networkMap;
  }

  /**
   * @param gpsPop the list of gps points to match.
   * @param network The topological map used to match the gps point to.
   * @param sigmaZ Standard deviation of GPS measurements. {@latex[ \sigma _z }
   * @param selection Route Localization distance. This parameter is used in
   *          order to limit the number of roads we try to match a gps point
   *          with.
   * @param beta Beta describes the difference between route distances and great
   *          circle distances. It is used in the computation of transition
   *          probabilities. {@latex[ \beta }
   * @param distanceLimit the distance above which a route distance is
   *          considered not reasonable.
   * @see #importNetwork(FT_FeatureCollection)
   */
  public HMMMapMatcher(IFeatureCollection<? extends IFeature> gpsPop,
      IFeatureCollection<? extends IFeature> network, double sigmaZ,
      double selection, double beta, double distanceLimit) {
    
    this.sigmaZ = sigmaZ;
    this.emissionDenominator = Math.sqrt(2 * Math.PI) * this.sigmaZ;
    // this.network = network;
    this.points = gpsPop;
    this.selection = selection;
    this.beta = beta;
    this.distanceLimit = distanceLimit;
    this.networkMap = new CarteTopo("Network Map");
    this.importNetwork(network);
  }

  /**
   * Import the given features into the topological map to match the gps points
   * to. Subclasses have to implement this method in order to include the
   * specificities of the given feature schema.
   * @param network the network to import into the topological map to match the
   *          gps point to.
   */
  protected abstract void importNetwork(
      IFeatureCollection<? extends IFeature> network);

  /**
   * Optional method to filter out gps points too close to the previous one. The
   * points are considered too close is their distance is less than {@latex[
   * 2\sigma _z }.
   */
  public void preprocessPoints() {
    Iterator<? extends IFeature> iterator = this.points.iterator();
    IFeature previous = iterator.next();
    while (iterator.hasNext()) {
      IFeature current = iterator.next();
      double distance = current.getGeom().distance(previous.getGeom());
      // System.out.println("Distance = " + distance);
      if (distance <= 2 * this.sigmaZ) {
        iterator.remove();
      } else {
        previous = current;
      }
    }
  }

  /**
   * Compute the transitions and find the best match.
   * @return the {@link Node} containing the best match.
   */
  public Node computeTransitions() {
    Hashtable<Arc, Double> start_p = new Hashtable<Arc, Double>();
    Hashtable<Arc, Hashtable<GM_Point, Double>> emit_p = new Hashtable<Arc, Hashtable<GM_Point, Double>>();
    Hashtable<IFeature, Collection<Arc>> candidateEdges = new Hashtable<IFeature, Collection<Arc>>(
        this.points.size());
    for (IFeature f : this.points) {
      candidateEdges.put(f, new ArrayList<Arc>(0));
    }
    GM_Point p = (GM_Point) this.points.get(0).getGeom();
    Collection<Arc> candidates = this.networkMap.getPopArcs().select(p,
        this.selection);
    //LOGGER.info("start emission probabilities");
    for (Arc a : candidates) {
      double proba = emissionProbability(p, a);
      if (!Double.isInfinite(proba)) {
        start_p.put(a, new Double(proba));
        candidateEdges.get(this.points.get(0)).add(a);
      }
    }
    //LOGGER.info("emission probabilities");
    for (Arc arc : this.networkMap.getPopArcs()) {
      Collection<? extends IFeature> candidatePoints = this.points.select(arc
          .getGeom(), this.selection);
      Hashtable<GM_Point, Double> emit = new Hashtable<GM_Point, Double>(
          candidatePoints.size());
      for (IFeature f : candidatePoints) {
        GM_Point point = (GM_Point) f.getGeom();
        emit.put(point, new Double(emissionProbability(point, arc)));
      }
      emit_p.put(arc, emit);
    }
    //LOGGER.info("viterbi");
    Node result = forward_viterbi(start_p, emit_p);
    double total = result.getProb();
    List<Arc> path = result.getPath();
    double proba = result.getVProb();
   LOGGER.info("total length " + total);
   LOGGER.info("path=" + path);
   LOGGER.info("proba " + proba);
    return result;
  }

  /**
   * Compute the emission probability between the point p and the given edge
   * arc.
   * @param p a gps point
   * @param arc an edge
   * @return the emission probability
   */
  private double emissionProbability(GM_Point p, Arc arc) {
    double distance = arc.getGeom().distance(p);
    double x = (distance / this.sigmaZ);
    double a = 0.5 * x * x;
    double b = this.emissionDenominator;
    double proba = a + Math.log(b);
    return -proba;
  }

  /**
   * Compute the transition probability corresponding to the probability that p2
   * is matched to a2 given that p1 is matched to a1.
   * @param p1 a gps point
   * @param p2 another gps point
   * @param a1 the edge p1 is matched to
   * @param a2 the edge p2 is matched to
   * @return the transition probability corresponding to the probability that p2
   *         is matched to a2 given that p1 is matched to a1.
   */
  Transition transitionProbability(GM_Point p1, GM_Point p2, Arc a1, Arc a2) {
    IDirectPosition x1 = JtsAlgorithms.getClosestPoint(p1.getPosition(), a1
        .getGeometrie());
    IDirectPosition x2 = JtsAlgorithms.getClosestPoint(p2.getPosition(), a2
        .getGeometrie());
    double distance = p1.getPosition().distance(p2.getPosition());
    double distanceRoute = distance + this.distanceLimit;
    List<Arc> arcs = null;
    GM_LineString geom = null;
    if (a1 == a2) {
      distanceRoute = x1.distance(x2);
      arcs = new ArrayList<Arc>(1);
      arcs.add(a1);
      int index1 = Operateurs.insertionIndex(x1, a1.getGeometrie()
          .getControlPoint().getList());
      int index2 = Operateurs.insertionIndex(x2, a1.getGeometrie()
          .getControlPoint().getList());
      boolean reverse = false;
      if (index1 > index2) {
        reverse = true;
        int temp = index1;
        index1 = index2;
        index2 = temp;
      }
      IDirectPositionList l = new DirectPositionList(
          new ArrayList<IDirectPosition>(a1.getGeometrie().getControlPoint()
              .getList().subList(index1, index2)));
      if (reverse) {
        Collections.reverse(l.getList());
      }
      l.add(0, x1);
      l.add(x2);
      geom = new GM_LineString(l);
    } else {
      Groupe pcc = this.networkMap.shortestPath(x1, x2, a1, a2, distance
          + this.distanceLimit);
      if (pcc != null) {
        distanceRoute = pcc.getLength();
        arcs = new ArrayList<Arc>(pcc.getListeArcs());
        geom = (GM_LineString) pcc.getGeom();
        pcc.videEtDetache();
      }
    }
    if (distanceRoute >= distance + this.distanceLimit) {
      return new Transition(distance, distanceRoute, arcs,
          Double.NEGATIVE_INFINITY, geom);
    }
    double dt = Math.abs(distance - distanceRoute);
    double a = dt / this.beta;
    double proba = a + Math.log(this.beta);
    return new Transition(distance, distanceRoute, arcs, -proba, geom);
  }

  /**
   * Run the viterbi algorithm.
   * @param start_p start probabilities
   * @param emit_p emission probabilities
   * @return the best match
   */
  private Node forward_viterbi(Hashtable<Arc, Double> start_p,
      Hashtable<Arc, Hashtable<GM_Point, Double>> emit_p) {
    Hashtable<Arc, Node> T = new Hashtable<Arc, Node>(start_p.keySet().size());
    // initialize base cases
    for (Arc state : start_p.keySet()) {
      List<Arc> argmax = new ArrayList<Arc>(1);
      List<Arc> states = new ArrayList<Arc>(1);
      List<GM_LineString> geometries = new ArrayList<GM_LineString>(0);
      argmax.add(state);
      states.add(state);
      Double prob = start_p.get(state);
      if (prob == null) {
        prob = Double.NEGATIVE_INFINITY;
      } else {
       // LOGGER.info("initial probability = " + prob + " for " + state.getGeom());
        T.put(state, new Node(prob, argmax, states, prob, geometries));
      }
    }
    List<Integer> dropped = new ArrayList<Integer>(0);
    GM_Point previous = (GM_Point) this.points.get(0).getGeom();
    Collection<Arc> previousCandidates = start_p.keySet();
    // run viterbi for all observations
    for (int i = 1; i < this.points.size(); i++) {
      GM_Point current = (GM_Point) this.points.get(i).getGeom();
      //LOGGER.info("Point " + i + " = " + current);
      Collection<Arc> candidates = this.networkMap.getPopArcs().select(current,
          this.selection);
      Hashtable<Arc, Node> U = new Hashtable<Arc, Node>(candidates.size());
      // LOGGER.info("Candidates " + candidates.size());
      for (Arc nextState : candidates) {
        double total = 0;
        double valmax = Double.NEGATIVE_INFINITY;
        List<Arc> argmax = null;
        List<Arc> states = null;
        List<GM_LineString> geometries = null;
        double emit = Double.NEGATIVE_INFINITY;
        if (emit_p.get(nextState) != null
            && emit_p.get(nextState).get(current) != null) {
          emit = emit_p.get(nextState).get(current).doubleValue();
        }
        if (Double.isInfinite(emit)) {
          // LOGGER.debug("emit null for " + nextState);
          continue;
        }
        // LOGGER.info("Source Candidates " + previousCandidates.size());
        for (Arc sourceState : previousCandidates) {
          Node node = T.get(sourceState);
          if (node == null) {
            continue;
          }
          double prob = node.getProb();
          List<Arc> v_path = node.getPath();
          List<Arc> v_states = node.getStates();
          double v_prob = node.getVProb();
          List<GM_LineString> v_geometries = node.getGeometry();
          // LOGGER.debug("emit = " + emit);
          // LOGGER.info(" " + prob + " " + v_path + " " + v_prob);
          Transition transProb = transitionProbability(previous, current,
              sourceState, nextState);
          double trans = transProb.proba;
          if (Double.isInfinite(trans)) {
            // LOGGER.debug("trans null for " + previous + " to " + output);
            // LOGGER.debug("\t with" + source_state + " and " + next_state);
            continue;
          }
          // LOGGER.debug("trans = " + trans);
          double p = emit + trans;
          // LOGGER.debug("p = " + p);
          prob += p;
          v_prob += p;
          total += prob;
          // LOGGER.debug("v_prob = " + v_prob + " valmax = " + valmax);
          if (v_prob > valmax) {
            argmax = new ArrayList<Arc>(v_path);
            states = new ArrayList<Arc>(v_states);
            geometries = new ArrayList<GM_LineString>(v_geometries);
            for (Arc a : transProb.listeArcs) {
              // if (a != sourceState) {
              argmax.add(a);
              // }
            }
            states.add(nextState);
            geometries.add(transProb.geometry);
            valmax = v_prob;
          }
        }
        if (!Double.isInfinite(valmax) && argmax != null) {
          U.put(nextState, new Node(total, argmax, states, valmax, geometries));
        }
      }
      if (!U.isEmpty()) {
        // clean up
        for (Node p : T.values()) {
          if (p.getPath() != null) {
            p.getPath().clear();
          }
        }
        T.clear();
        T = U;
        previous = current;
        previousCandidates = candidates;
      } else {
       // LOGGER.info("dropping junk point " + current);
        dropped.add(i);
      }
    }
    double total = 0;
    List<Arc> argmax = null;
    List<Arc> states = null;
    List<GM_LineString> geometries = null;
    double valmax = Double.NEGATIVE_INFINITY;
    // find the best match
   // LOGGER.debug("find the best match");
    for (Arc state : T.keySet()) {
      LOGGER.debug("state " + state);
      Node objs = T.get(state);
      double prob = objs.getProb();
      List<Arc> v_path = objs.getPath();
      List<Arc> v_states = objs.getStates();
      List<GM_LineString> v_geometries = objs.getGeometry();
      double v_prob = objs.getVProb();
      total += prob;
    //  LOGGER.debug("total " + total);
      if (v_prob > valmax) {
        argmax = v_path;
        valmax = v_prob;
        states = v_states;
        geometries = v_geometries;
       // LOGGER.debug("best match " + v_path);
      }
    }
    // removing junk points
    Collections.reverse(dropped);
    for (Integer index : dropped) {
      this.points.remove(index.intValue());
    }
    return new Node(total, argmax, states, valmax, geometries);
  }

  /**
   * Nodes.
   */
  public class Node {
    
    private double prob;
    private double vProb;
    private List<Arc> path;
    private List<Arc> states;
    private List<GM_LineString> geometry;
    /**
     * @return
     */
    public double getProb() {
      return this.prob;
    }
    /**
     * @return
     */
    public List<Arc> getPath() {
      return this.path;
    }
    /**
     * @return
     */
    public List<Arc> getStates() {
      return this.states;
    }
    /**
     * @return
     */
    public double getVProb() {
      return this.vProb;
    }
    /**
     * @return
     */
    public List<GM_LineString> getGeometry() {
      return this.geometry;
    }
    /**
     * @param prob
     * @param path
     * @param states
     * @param vprob
     * @param geometries
     */
    public Node(double prob, List<Arc> path, List<Arc> states, double vprob,
        List<GM_LineString> geometries) {
      this.prob = prob;
      this.path = path;
      this.states = states;
      this.vProb = vprob;
      this.geometry = geometries;
    }
  }

  /**
   * Transitions.
   */
  class Transition {
    private GM_LineString geometry;
    private double distance;
    private double length;
    private List<Arc> listeArcs;
    private double proba;
    /**
     * @return
     */
    public GM_LineString getGeometry() {
      return this.geometry;
    }
    /**
     * @return
     */
    public double getDistance() {
      return this.distance;
    }
    /**
     * @return
     */
    public double getLength() {
      return this.length;
    }
    /**
     * @return
     */
    public List<Arc> getListeArcs() {
      return this.listeArcs;
    }
    /**
     * @return
     */
    public double getProba() {
      return this.proba;
    }
    /**
     * @param distance
     * @param length
     * @param listeArcs
     * @param proba
     * @param geom
     */
    public Transition(double distance, double length, List<Arc> listeArcs,
        double proba, GM_LineString geom) {
      this.distance = distance;
      this.length = length;
      this.listeArcs = listeArcs;
      this.proba = proba;
      this.geometry = geom;
    }
  }
}
