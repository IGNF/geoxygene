package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

public class PolygonTurningFunction {

  private IPolygon polygon;
  private IDirectPosition origin;
  private int originIndex;
  private List<Double> turningValues;
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
  public PolygonTurningFunction(IPolygon polygon) {
    this.polygon = polygon;
    this.setOrigin(polygon.coord().get(0));
    this.originIndex = 0;
    this.turningValues = new ArrayList<Double>();
    this.segmentLengths = new ArrayList<Double>();
    compute();
  }

  public PolygonTurningFunction(IPolygon polygon, IDirectPosition origin) {
    this.polygon = polygon;
    this.setOrigin(origin);
    this.turningValues = new ArrayList<Double>();
    this.segmentLengths = new ArrayList<Double>();
    for (int i = 0; i < polygon.coord().size(); i++) {
      if (polygon.coord().get(i).equals(origin)) {
        this.originIndex = i;
        break;
      }
    }
    compute();
  }

  private void compute() {
    // compute the polygon perimeter to normalise the segment lengths
    double perimeter = polygon.perimeter();
    // get the segment list in order to find each one's orientation
    List<Segment> segments = Segment.getSegmentList(polygon, origin);
    double angleSum = Angle.angleTroisPoints(getVertexBeforeOrigin(), origin,
        getVertexAfterOrigin()).getValeur();
    double lastAngle = 0.0;
    for (Segment segment : segments) {
      // increment segment lengths list
      segmentLengths.add(segment.length() / perimeter);
      // compute the segment orientation
      double angle = segment.orientation();
      // if the angle is bigger than last one, it's a turn to the left
      if (angle > lastAngle)
        angleSum += angle - lastAngle;
      else {
        // it's a turn to the right
        angleSum -= lastAngle - angle;
      }
      // increment turning angle
      lastAngle = angle;
      this.turningValues.add(angleSum);
    }
  }

  private IDirectPosition getVertexBeforeOrigin() {
    if (originIndex > 0)
      return polygon.coord().get(originIndex - 1);
    return polygon.coord().get(polygon.coord().size() - 2);
  }

  private IDirectPosition getVertexAfterOrigin() {
    return polygon.coord().get(originIndex + 1);
  }

  /**
   * If f is the turning function, this method returns f(s) for s between 0 & 1.
   * @param s
   * @return
   */
  public double getValue(double s) {
    int index = 0;
    double totalLength = 0.0;
    for (int i = 0; i < segmentLengths.size(); i++) {
      totalLength += segmentLengths.get(i);
      if (totalLength > s) {
        index = i;
        break;
      }
    }
    return turningValues.get(index);
  }

  /**
   * Compute the turning function distance between two polygons according to
   * Arkin's method (Arkin et al, 1991).
   * @param polygon2
   * @return
   */
  public double distanceArkin(IPolygon polygon2) {
    // initialisation of the minimum distance
    double minDistance = Double.MAX_VALUE;
    // loop on the polygon2 vertices to measure distance with the origin put on
    // each one
    for (IDirectPosition pt : polygon2.coord()) {
      PolygonTurningFunction func2 = new PolygonTurningFunction(polygon2, pt);
      double dist = distanceBetweenFunctions(func2);
      if (dist < minDistance)
        minDistance = dist;
    }
    return minDistance;
  }

  /**
   * It's the distance between two turning functions for t and theta parameters
   * constant. According to Arkin et al, the distance is the sum of the areas
   * between both curves inside a strip between two critical points. Critical
   * points are points between 0 and 1 where either this or func2 change turning
   * values.
   * @param func2
   * @return
   */
  private double distanceBetweenFunctions(PolygonTurningFunction func2) {
    List<Double> criticalPts = new ArrayList<Double>();
    double total = 0.0;
    for (double length : segmentLengths) {
      total += length;
      if (total < 1.0)
        criticalPts.add(total);
    }
    total = 0.0;
    for (double length : func2.segmentLengths) {
      total += length;
      if (total < 1.0)
        criticalPts.add(total);
    }
    criticalPts.add(1.0);
    // sort the critical points list in ascending order
    Collections.sort(criticalPts);

    // now sum the areas between the critical points
    double distance = 0.0;
    double previousPt = 0.0;
    for (double criticalPt : criticalPts) {
      // compute the area in the strip formed between previousPt and criticalPt
      double middle = (previousPt + criticalPt) / 2.0;
      double value1 = this.getValue(middle);
      double value2 = func2.getValue(middle);
      distance += (Math.max(value1, value2) - Math.min(value1, value2))
          * (criticalPt - previousPt);
      previousPt = criticalPt;
    }
    return distance;
  }

  /**
   * Print the graph of the turning function in a dialog.
   */
  public void print() {
    double[] x = new double[2 * turningValues.size()];
    double[] y = new double[2 * turningValues.size()];
    double totalLength = 0.0;
    for (int i = 0; i < turningValues.size(); i++) {
      // plot first point of the segment
      x[2 * i] = totalLength;
      y[2 * i] = turningValues.get(i);
      // plot second point of the segment
      totalLength += segmentLengths.get(i);
      x[2 * i + 1] = totalLength;
      y[2 * i + 1] = turningValues.get(i);
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
  public void print(PolygonTurningFunction func) {
    double[] x1 = new double[2 * this.turningValues.size()];
    double[] y1 = new double[2 * this.turningValues.size()];
    double[] x2 = new double[2 * func.turningValues.size()];
    double[] y2 = new double[2 * func.turningValues.size()];
    double totalLength = 0.0;
    for (int i = 0; i < this.turningValues.size(); i++) {
      // plot first point of the segment
      x1[2 * i] = totalLength;
      y1[2 * i] = this.turningValues.get(i);
      // plot second point of the segment
      totalLength += this.segmentLengths.get(i);
      x1[2 * i + 1] = totalLength;
      y1[2 * i + 1] = this.turningValues.get(i);
    }
    totalLength = 0.0;
    for (int i = 0; i < func.turningValues.size(); i++) {
      // plot first point of the segment
      x2[2 * i] = totalLength;
      y2[2 * i] = func.turningValues.get(i);
      // plot second point of the segment
      totalLength += func.segmentLengths.get(i);
      x2[2 * i + 1] = totalLength;
      y2[2 * i + 1] = func.turningValues.get(i);
    }
    // create your PlotPanel (you can use it as a JPanel)
    Plot2DPanel plot = new Plot2DPanel();

    // add a line plot to the PlotPanel
    plot.addLinePlot("turning function 1", x1, y1);
    plot.addLinePlot("turning function 2", x2, y2);
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
