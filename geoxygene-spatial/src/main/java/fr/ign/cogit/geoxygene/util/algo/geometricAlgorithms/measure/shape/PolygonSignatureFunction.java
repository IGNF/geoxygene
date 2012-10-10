package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

/**
 * Geometrical function to analyse the shape of a polygon (Bel Hadj Ali, 2001):
 * the shape is transformed into a function from [0,1] to R which represents the
 * radial distance from the centroid to each vertex. The signature function is
 * provided with a distance method inspired from the one in (Arkin et al 1991)
 * dedicated to the turning function.
 * @author GTouya
 * 
 */
public class PolygonSignatureFunction {

  private IPolygon polygon;
  private IDirectPosition origin;
  private int originIndex;
  private List<Double> radialDistances;
  /**
   * The segment lengths from origin to origin, normalised by polygon perimeter
   * to fit to [0,1] interval. The order corresponds to the one of the
   * turningValues list.
   */
  private List<Double> segmentLengths;

  /**
   * Constructor with the origin on the first vertex.
   * @param polygon
   */
  public PolygonSignatureFunction(IPolygon polygon) {
    this.polygon = polygon;
    this.setOrigin(polygon.coord().get(0));
    this.originIndex = 0;
    this.radialDistances = new ArrayList<Double>();
    this.segmentLengths = new ArrayList<Double>();
    compute();
  }

  /**
   * Constructor with the origin vertex as parameter.
   * @param polygon
   * @param origin
   */
  public PolygonSignatureFunction(IPolygon polygon, IDirectPosition origin) {
    this.polygon = polygon;
    this.setOrigin(origin);
    this.radialDistances = new ArrayList<Double>();
    this.segmentLengths = new ArrayList<Double>();
    for (int i = 0; i < polygon.coord().size(); i++) {
      if (polygon.coord().get(i).equals(origin)) {
        this.originIndex = i;
        break;
      }
    }
    compute();
  }

  /**
   * Compute the signature function according to the polygon geometry and the
   * origin vertex. The length of segments is normalised by polygon perimeter.
   */
  private void compute() {
    // compute the polygon perimeter to normalise the segment lengths
    double perimeter = polygon.perimeter();
    double absCurv = 0.0;
    IDirectPosition centroid = polygon.centroid();
    IDirectPosition previousVertex = null;
    for (int i = originIndex; i < polygon.coord().size(); i++) {
      IDirectPosition vertex = polygon.coord().get(i);
      if (previousVertex != null)
        absCurv += vertex.distance2D(previousVertex);
      // increment segment lengths list
      segmentLengths.add(absCurv / perimeter);
      // compute the segment orientation
      double radialDist = centroid.distance2D(vertex);
      // increment the radial distances
      this.radialDistances.add(radialDist);
      previousVertex = vertex;
    }
    for (int i = 0; i < originIndex; i++) {
      IDirectPosition vertex = polygon.coord().get(i);
      if (vertex.equals(previousVertex))
        continue;
      absCurv += vertex.distance2D(previousVertex);
      // increment segment lengths list
      segmentLengths.add(absCurv / perimeter);
      // compute the segment orientation
      double radialDist = centroid.distance2D(vertex);
      // increment the radial distances
      this.radialDistances.add(radialDist);
      previousVertex = vertex;
    }
  }

  /**
   * If f is the polygon signature function, this method returns f(s) for s
   * between 0 & 1.
   * @param s
   * @return
   */
  public double getValue(double s) {
    for (int i = 0; i < this.segmentLengths.size(); i++) {
      if (this.segmentLengths.get(i) == s)
        return this.radialDistances.get(i);
      if (this.segmentLengths.get(i) > s) {
        // compute the value by linear projection
        IDirectPosition pt1 = new DirectPosition(
            this.segmentLengths.get(i - 1), this.radialDistances.get(i - 1));
        IDirectPosition pt2 = new DirectPosition(this.segmentLengths.get(i),
            this.radialDistances.get(i));
        Segment seg = new Segment(pt1, pt2);
        return (-seg.getCoefA() * s - seg.getCoefC()) / seg.getCoefB();
      }
    }
    return 0.0;
  }

  /**
   * Compute the polygon signature function distance between two polygons
   * according to Arkin's method (Arkin et al, 1991) that was dedicated to
   * turning function.
   * @param polygon2
   * @return
   */
  public double distanceArkin(IPolygon polygon2) {
    if (polygon2.equals(this.polygon))
      return 0.0;
    // initialisation of the minimum distance
    double minDistance = Double.MAX_VALUE;
    // loop on the polygon2 vertices to measure distance with the origin put on
    // each one
    for (IDirectPosition pt : polygon2.coord()) {
      PolygonSignatureFunction func2 = new PolygonSignatureFunction(polygon2,
          pt);
      double dist = distanceBetweenFunctions(func2);
      if (dist < minDistance)
        minDistance = dist;
    }
    return minDistance;
  }

  /**
   * It's the distance between two functions for a given origin. It's the
   * integral of the difference of the two functions (i.e. the area between the
   * two curves.
   * @param func2
   * @return
   */
  private double distanceBetweenFunctions(PolygonSignatureFunction func2) {
    List<Double> criticalPts = new ArrayList<Double>();
    for (int i = 0; i < this.segmentLengths.size(); i++)
      criticalPts.add(this.segmentLengths.get(i));
    for (int i = 0; i < func2.segmentLengths.size(); i++) {
      if (!criticalPts.contains(func2.segmentLengths.get(i)))
        criticalPts.add(func2.segmentLengths.get(i));
    }
    // sort the critical points list in ascending order
    Collections.sort(criticalPts);
    // add critical points at intersections
    double previousPt = 0.0;
    for (double criticalPt : new ArrayList<Double>(criticalPts)) {
      if (criticalPt == previousPt)
        continue;
      Segment seg1 = new Segment(new DirectPosition(previousPt, this
          .getValue(previousPt)), new DirectPosition(criticalPt, this
          .getValue(criticalPt)));
      Segment seg2 = new Segment(new DirectPosition(previousPt, func2
          .getValue(previousPt)), new DirectPosition(criticalPt, func2
          .getValue(criticalPt)));
      if (seg1.intersects(seg2) && (!seg1.equals(seg2)))
        criticalPts.add(seg1.straightLineIntersection(seg2).getX());
      previousPt = criticalPt;
    }
    Collections.sort(criticalPts);

    // now sum the areas between the critical points
    double distance = 0.0;
    previousPt = 0.0;
    for (double criticalPt : criticalPts) {
      if (criticalPt == previousPt)
        continue;
      // compute the integral of both functions between previousPt and
      // criticalPt. The functions in such an interval are linear so the
      // integral is computed thanks to the trapezium area formula: on the
      // interval [a,b] Area = (b-a)/2*(f(a)+f(b)). Here, a=previousPt and
      // b=criticalPt.
      double integral1 = (this.getValue(previousPt) + this.getValue(criticalPt))
          * (criticalPt - previousPt) / 2.0;
      double integral2 = (func2.getValue(previousPt) + func2
          .getValue(criticalPt))
          * (criticalPt - previousPt) / 2.0;
      distance += Math.abs(integral1 - integral2);
      previousPt = criticalPt;
    }
    return distance;
  }

  /**
   * Print the graph of the turning function in a dialog.
   */
  public void print() {
    double[] x = new double[segmentLengths.size()];
    double[] y = new double[segmentLengths.size()];
    for (int i = 0; i < segmentLengths.size(); i++) {
      // plot the vertex
      x[i] = segmentLengths.get(i);
      y[i] = radialDistances.get(i);
    }
    // create your PlotPanel (you can use it as a JPanel)
    Plot2DPanel plot = new Plot2DPanel();

    // add a line plot to the PlotPanel
    plot.addLinePlot("turning function", x, y);
    // put the PlotPanel in a JFrame, as a JPanel
    JFrame frame = new JFrame("a plot panel");
    frame.setContentPane(plot);
    frame.setVisible(true);
  }

  /**
   * Plot the graph of both turning functions in a dialog.
   */
  public void print(PolygonSignatureFunction func) {
    double[] x1 = new double[this.segmentLengths.size()];
    double[] y1 = new double[this.segmentLengths.size()];
    double[] x2 = new double[func.segmentLengths.size()];
    double[] y2 = new double[func.segmentLengths.size()];
    for (int i = 0; i < this.segmentLengths.size(); i++) {
      // plot the vertex
      x1[i] = this.segmentLengths.get(i);
      y1[i] = this.radialDistances.get(i);
    }
    for (int i = 0; i < func.segmentLengths.size(); i++) {
      // plot the vertex
      x2[i] = func.segmentLengths.get(i);
      y2[i] = func.radialDistances.get(i);
    }
    // create your PlotPanel (you can use it as a JPanel)
    Plot2DPanel plot = new Plot2DPanel();

    // add a line plot to the PlotPanel
    plot.addLinePlot("polygon signature function 1", x1, y1);
    plot.addLinePlot("polygon signature function 2", x2, y2);
    // put the PlotPanel in a JFrame, as a JPanel
    JFrame frame = new JFrame("a plot panel");
    frame.setContentPane(plot);
    frame.setVisible(true);
  }

  public IDirectPosition getOrigin() {
    return origin;
  }

  public void setOrigin(IDirectPosition origin) {
    this.origin = origin;
  }

}
