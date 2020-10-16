package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * Implementation of
 * "An Efficiently Computable Metric for Comparing Polygonal Shapes," by Arkin,
 * Chew, Huttenlocher, Kedem, and Mitchel (undated). This expands a little on
 * the cited reference to achieve O(n) space and O(mn log n) run time.
 * <p>
 * This could be improved to O(min m,n) and O(mn log min m,n) by selecting the
 * smallest of the 2 polys to create the initial event heap. See init_events().
 * <p>
 * Variable names match the article.
 * <p>
 * Implementation (c) Eugene K. Ressler 91, 92 This source may be freely
 * distributed and used for non-commercial purposes, so long as this comment is
 * attached to any code copied or derived from it.
 */
public class PolygonTurningSimilarity {
  /**
   * Compute floor(log_base2(x))
   */
  private static int ilog2(int x) {
    return Double.valueOf(Math.floor(Math.log10(x) / Math.log10(2.0))).intValue();
  }

  /**
   * Return angle a, adjusted by +-2kPI so that it is within [base-PI, base+PI).
   */
  private static double turn(double a, double base) {
    double result = a;
    while (result - base < -Math.PI) {
      result += 2 * Math.PI;
    }
    while (result - base >= Math.PI) {
      result -= 2 * Math.PI;
    }
    return result;
  }

  /**
   * Convert a polygon to a turning rep. This computes the absolute angle of
   * each leg wrt the x-axis, then adjusts this to within PI of the last leg to
   * form the turning angle. Finally, the total length of all legs is used to
   * compute the cumulative normalized arc length of each discontinuity, s.
   * @param p a polygon
   * @return a turning rep representing the given polygon
   */
  private static TurnRep poly_to_turn_rep(Polygon p) {
    TurnRep t = new TurnRep();

    LineString ring = p.getExteriorRing();
    if (!Orientation.isCCW(ring.getCoordinates())) {
      ring = (LineString) ring.reverse();
    }
    Coordinate[] points = ring.getCoordinates();
    int n = points.length;

    double theta0 = 0;
    double total_len = 0;
    for (int i0 = 0; i0 < n - 1; ++i0) {
      /*
       * Look one vertex ahead of i0 to compute the leg.
       */
      int i1 = i0 + 1;
      double dx = points[i1].x - points[i0].x;
      double dy = points[i1].y - points[i0].y;
      theta0 = turn(Math.atan2(dy, dx), theta0);
      Leg l = new Leg();
      t.leg.add(l);
      l.theta = theta0;
      total_len += l.len = Math.sqrt(dx * dx + dy * dy);
    }
    t.total_len = total_len;
    double len = 0;
    for (int i0 = 0; i0 < t.leg.size(); ++i0) {
      t.leg.get(i0).s = len / total_len;
      len += t.leg.get(i0).len;
    }
    return t;
  }

  /**
   * Fill in a turn rep with a rotated version of an original. Normalized arc
   * lengths start at 0 in the new representation.
   * @param t input turn rep
   * @param to rotation / start angle
   * @param r rotated turnrep
   */
  private static void rotate_turn_rep(TurnRep t, int to, TurnRep r) {
    int n = t.leg.size();
    double total_len = r.total_len = t.total_len;
    for (int ti = to, ri = 0; ri < n; ++ti, ++ri) {
      Leg l = new Leg();
      r.leg.add(l);
      l.theta = t.theta(ti);
      l.len = t.len(ti);
      l.s = t.s(ti);
    }
    double len = 0.0;
    for (int ri = 0; ri < n; ++ri) {
      r.leg.get(ri).s = len / total_len;
      len += r.leg.get(ri).len;
    }
  }

  /**
   * In one O(m + n) pass over the turning reps of the polygons to be matched,
   * this computes all the terms needed to incrementally compute the metric. See
   * the paper.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @return Initialized values
   */
  private static InitVals init_vals(TurnRep f, TurnRep g) {
    int i, n; /* loop params */
    int fi, gi; /* disconts that bound current strip */
    double ht0, slope; /* per paper */
    double a; /* alpha in the paper */
    double last_s = 0.0d; /* s at left edge of current strip */
    double ds; /* width of strip */
    double dtheta; /* height of strip */
    /*
     * First strip is between 0 and the earliest of 1'th f and g disconts.
     */
    gi = 1;
    fi = 1;
    /*
     * Zero accumulators and compute initial slope.
     */
    ht0 = a = 0.0d;
    slope = (g.s(1) < f.s(1)) ? 0 : -Math.pow(g.theta(0) - f.theta(0), 2);
    /*
     * Count all the strips
     */
    for (i = 0, n = f.leg.size() + g.leg.size() - 1; i < n; ++i) {
      /*
       * Compute height of current strip.
       */
      dtheta = g.theta(gi - 1) - f.theta(fi - 1);
      /*
       * Determine flavor of discontinuity on right.
       */
      if (f.s(fi) <= g.s(gi)) {
        /*
         * It's f. Compute width of current strip, then bump area accumulators.
         */
        ds = f.s(fi) - last_s;
        a += ds * dtheta;
        ht0 += ds * dtheta * dtheta;
        /*
         * Determine flavor of next strip. We know it's ff or fg. In latter
         * case, bump accumulator. Note we've skipped the first strip. It's
         * added as the "next" of the last strip.
         */
        if (f.s(fi + 1) > g.s(gi))
          slope += Math.pow(f.theta(fi) - g.theta(gi - 1), 2);
        /*
         * Go to next f discontinuity.
         */
        last_s = f.s(fi++);
      } else {
        /*
         * Else it's g ...
         */
        ds = g.s(gi) - last_s;
        a += ds * dtheta;
        ht0 += ds * dtheta * dtheta;
        /*
         * ... and next strip is gg or gf, and again we're interested in the
         * latter case.
         */
        if (g.s(gi + 1) >= f.s(fi))
          slope -= Math.pow(g.theta(gi) - f.theta(fi - 1), 2);
        /*
         * Go to next g discontinuity.
         */
        last_s = g.s(gi++);
      }
    }
    /*
     * Set up all return values.
     */
    return new InitVals(ht0, slope, a);
  }

  /**
   * Recompute ht0 and slope for the current event. Renormalize the turning reps
   * so that the event discontinuities are first in each. This keeps all s
   * values within [0,1) while recomputing so that all are represented with the
   * same precision. If we check that no other events are pending within machine
   * epsilon of t for (fi,gi) before calling this, numerical stability is
   * guaranteed (unlike the first two ways I tried).
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param fi rotation for the first polygon
   * @param gi rotation for the second polygon
   * @return Initialized values
   */
  private static InitVals reinit_vals(TurnRep f, TurnRep g, int fi, int gi) {
    TurnRep fr = new TurnRep(), gr = new TurnRep();
    rotate_turn_rep(f, fi, fr);
    rotate_turn_rep(g, gi, gr);
    return init_vals(fr, gr);
  }

  /**
   * Compute number of events between successive reinits that will not blow the
   * asymptotice complexity bound.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @return number of events
   */
  private static int reinit_interval(TurnRep f, TurnRep g) {
    return f.leg.size() * g.leg.size()
        / (Math.min(f.leg.size(), g.leg.size()) * ilog2(g.leg.size()));
  }

  /**
   * Following are routines to maintian the event heap. This is initialized with
   * one event per g discontinuity, namely, the one due to the f discontinuity
   * closest to the right. The sort key is the "f shift" parameter t. As the
   * algorithm runs, it draws an event (of min t) from the heap, handles it,
   * then inserts the event due to the *next* f discontinuity associated with
   * the same g discontinuity (unless this event would have t>1).
   */
  private Queue<Event> event = new PriorityQueue<>(1000, Comparator.comparingDouble(o -> o.t));

  /**
   * Insert a new event in the heap.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param fi see paper
   * @param gi see paper
   */
  private void add_event(TurnRep f, TurnRep g, int fi, int gi) {
    double t = f.s(fi) - g.s(gi);
    if (t > 1) {
      return;
    }
    this.event.add(new Event(t, fi, gi));
  }

  /**
   * Remove the event of min t from the heap and return it.
   * @return the event of min t from the heap
   */
  private Event next_event() {
    return this.event.poll();
  }

  /**
   * Peek at the next t to come off the heap without removing the element.
   * @return t of the next event from the heap
   */
  private double next_t() {
    return this.event.peek().t;
  }

  /**
   * Scan the turning reps to create the initial events in the heap as described
   * above.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   */
  private void init_events(TurnRep f, TurnRep g) {
    int fi, gi;
    this.event.clear();
    /*
     * Cycle through all g discontinuities, including the one at s = 1. This
     * takes care of s = 0.
     */
    for (fi = gi = 1; gi <= g.leg.size(); ++gi) {
      /*
       * Look for the first f discontinuity to the right of this g
       * discontinuity.
       */
      while (f.s(fi) <= g.s(gi)) {
        ++fi;
      }
      add_event(f, g, fi, gi);
    }
  }

  /**
   * The heart of the algorithm: Compute the minimum value of the integral term
   * of the metric by considering all critical events. This also returns the
   * theta* and event associated with the minimum.
   * @param f turning rep of the first polygon
   * @param g turning rep of the second polygon
   * @param hc0_init see paper
   * @param slope_init see paper
   * @param alpha see paper
   * @param d_update see paper
   * @return a set of result values including the minimum value of the integral
   *         term of the metric, the theta* and the event associated with the
   *         minimum
   */
  private Result h_t0min(TurnRep f, TurnRep g, double hc0_init, double slope_init,
                         double alpha, int d_update) {
    int left_to_update; /* # disconts left until update */
    double metric2, min_metric2; /* d^2 and d^2_min thus far */
    double theta_star, min_theta_star; /* theta* and theta*_min thus far */
    double last_t; /* t of last iteration */
    double hc0_err, slope_err; /* error mags discovered on update */
    double hc0 = hc0_init;
    double slope = slope_init;
    Event e; /* current event */
    Event min_e; /* event of d^2_min thus far */
    /*
     * At t = 0, theta_star is just alpha, and the min metric2 seen so far is
     * hc0 - min_theta_star^2. Also, no error has been seen.
     */
    min_theta_star = alpha;
    min_metric2 = hc0 - min_theta_star * min_theta_star;
    min_e = new Event(0.0, 0, 0);/* implicit first event */
    last_t = hc0_err = slope_err = 0.0d;
    /*
     * Compute successive hc_i0's by incremental update at critical events as
     * described in the paper.
     */
    left_to_update = d_update;
    while (!this.event.isEmpty()) {
      e = next_event();
      hc0 += (e.t - last_t) * slope;
      theta_star = alpha - 2 * Math.PI * e.t;
      metric2 = hc0 - theta_star * theta_star;
      if (metric2 < min_metric2) {
        min_metric2 = metric2;
        min_theta_star = theta_star;
        min_e = e;
      }
      /*
       * Update slope, last t, and put next event for this g discontinuity in
       * the heap.
       */
      slope += 2 * (f.theta(e.fi - 1) - f.theta(e.fi))
          * (g.theta(e.gi - 1) - g.theta(e.gi));
      last_t = e.t;
      add_event(f, g, e.fi + 1, e.gi);
      /*
       * Re-establish hc0 and slope now and then to reduce numerical error. If
       * d_update is 0, do nothing. Note we don't update if an event is close,
       * as this causes numerical ambiguity. The test number could be much
       * smaller, but why tempt Murphey? We force an update on last event so
       * there's always at least one.
       */
      if (d_update != 0
          && (this.event.isEmpty() || --left_to_update <= 0
              && e.t - last_t > 0.001 && next_t() - e.t > 0.001)) {
        InitVals newVals = reinit_vals(f, g, e.fi, e.gi);
        double rihc0 = newVals.ht0;
        double rislope = newVals.slope;
        double dhc0 = hc0 - rihc0;
        double dslope = slope - rislope;
        if (Math.abs(dhc0) > Math.abs(hc0_err))
          hc0_err = dhc0;
        if (Math.abs(dslope) > Math.abs(slope_err))
          slope_err = dslope;
        hc0 = rihc0;
        slope = rislope;
        left_to_update = d_update;
      }
    }
    /*
     * Set up return values.
     */
    return new Result(min_metric2, min_theta_star, min_e, hc0_err, slope_err);
  }

  public static double getTurningSimilarity(IPolygon p1, IPolygon p2) {
    try {
      return PolygonTurningSimilarity.getTurningSimilarity(
          (Polygon) JtsGeOxygene.makeJtsGeom(p1),
          (Polygon) JtsGeOxygene.makeJtsGeom(p2));
    } catch (Exception e) {
      e.printStackTrace();
      return Double.NEGATIVE_INFINITY;
    }
  }

  /**
   * Compute the minimum value of the integral term of the metric by considering
   * all critical events.
   * @param p1 first polygon
   * @param p2 second polygon
   * @return the minimum value of the integral term of the metric
   */
  public static double getTurningSimilarity(Polygon p1, Polygon p2) {
    PolygonTurningSimilarity pts = new PolygonTurningSimilarity();
    TurnRep turnRep1 = PolygonTurningSimilarity.poly_to_turn_rep(p1);
    TurnRep turnRep2 = PolygonTurningSimilarity.poly_to_turn_rep(p2);
    InitVals vals = PolygonTurningSimilarity.init_vals(turnRep1, turnRep2);
    pts.init_events(turnRep1, turnRep2);
    Result result = pts.h_t0min(turnRep1, turnRep2, vals.ht0, vals.slope,
        vals.alpha,
        PolygonTurningSimilarity.reinit_interval(turnRep1, turnRep2));
    double metric = result.getH_t0min();
    return metric < 0 ? 0 : Math.sqrt(metric);
  }

}

/**
 * Single leg of a turning rep polygon.
 */
class Leg {
  double theta; /* heading of the leg */
  double len; /* length in original coordinates */
  double s; /* cumulative arc length in [0,1] of start */

  @Override
  public String toString() {
    return "[" + this.theta + ", " + this.len + ", " + this.s + "]";
  }
}

/**
 * Polygon in turning rep.
 */
class TurnRep {
  double total_len;
  List<Leg> leg = new ArrayList<>();

  double len(int i) {
    return this.leg.get(i % this.leg.size()).len;
  }

  double s(int i) {
    return this.leg.get(i % this.leg.size()).s + i / this.leg.size();
  }

  double theta(int i) {
    return this.leg.get(i % this.leg.size()).theta + i / this.leg.size() * 2
        * Math.PI;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("TurnRep " + this.total_len + ", (");
    for (Leg l : this.leg) {
      result.append(l).append(", ");
    }
    return result.substring(0, result.length() - 2) + ")";
  }
}

/**
 * Critical event.
 */
class Event {
  double t; /* "f shift" parameter of the event */
  int fi, gi; /* pointers into turn reps f and g */

  Event(double t_, int fi_, int gi_) {
    this.t = t_;
    this.fi = fi_;
    this.gi = gi_;
  }

  @Override
  public String toString() {
    return "Event " + this.t + ", " + this.fi + ", " + this.gi;
  }
}

class InitVals {
  InitVals(double ht0_, double slope_, double alpha_) {
    this.ht0 = ht0_;
    this.slope = slope_;
    this.alpha = alpha_;
  }

  double ht0;
  double slope;
  double alpha;
}

class Result {
  public Result(double h_t0min1, double theta_star1, Event e1, double hc0_err1,
      double slope_err1) {
    this.h_t0min = h_t0min1;
    this.theta_star = theta_star1;
    this.e = e1;
    this.hc0_err = hc0_err1;
    this.slope_err = slope_err1;
  }

  public double getH_t0min() {
    return h_t0min;
  }

  private double h_t0min;

  public double getTheta_star() {
    return theta_star;
  }

  public Event getE() {
    return e;
  }

  public double getHc0_err() {
    return hc0_err;
  }

  public double getSlope_err() {
    return slope_err;
  }

  private double theta_star;
  private Event e;
  private double hc0_err;
  private double slope_err;
}
