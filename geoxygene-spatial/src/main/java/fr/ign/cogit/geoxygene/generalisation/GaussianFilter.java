package fr.ign.cogit.geoxygene.generalisation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * A Class for the computation of Gaussian filtering.
 * @author Julien Perret
 */
public class GaussianFilter {
  /**
   * Compute the gaussian filtering of a set of input points.
   * @param line the input line
   * @param sigma the gaussian filter parameter
   * @param threshold the threshold used for subsampling
   * @return the result of a gaussian filter applied to the given set of points
   */
  public static ILineString gaussianFilter(ILineString line, double sigma,
      double threshold) {
    // remove duplicate points
    line = new GM_LineString(line.coord(), false);
    // subsample the line
    ILineString subsampledLine = Operateurs.resampling(line, threshold);
    // remove duplicate points
    subsampledLine = new GM_LineString(subsampledLine.coord(), false);
    int interval = (int) (4.0 * sigma / threshold);
    // if the interval is larger than the size of the line, modify reduce it
    if (interval > subsampledLine.sizeControlPoint() - 1) {
      interval = subsampledLine.sizeControlPoint() - 1;
      sigma = (interval * threshold) / 4.0;
    }
    // compute gaussian coefficients
    double c2 = -1.0 / (2.0 * sigma * sigma);
    double c1 = 1.0 / (sigma * Math.sqrt(2.0 * Math.PI));
    // compute gassian weights and their sum
    double[] weights = new double[interval + 1];
    double sum = 0;
    for (int k = 0; k <= interval; k++) {
      weights[k] = c1 * Math.exp(c2 * k * k);
      sum += weights[k];
      if (k > 0) {
        sum += weights[k];
      }
    }
    // extend the line at its first and last points with central inversion
    ILineString extendedLine = extend(subsampledLine, interval);
    // compute the actual filtering
    ILineString filteredLine = new GM_LineString();
    for (int index = 0; index < subsampledLine.sizeControlPoint(); index++) {
      double x = 0;
      double y = 0;
      for (int k = -interval; k <= interval; k++) {
        IDirectPosition p1 = extendedLine.getControlPoint(index - k + interval);
        x += weights[Math.abs(k)] * p1.getX() / sum;
        y += weights[Math.abs(k)] * p1.getY() / sum;
      }
      filteredLine.addControlPoint(new DirectPosition(x, y));
    }
    // only return the points matching the input points in the resulting
    // filtered line
    ILineString result = new GM_LineString();
    for (IDirectPosition p : line.getControlPoint()) {
      int index = subsampledLine.getControlPoint().getList().indexOf(p);
      result.addControlPoint(filteredLine.getControlPoint(index));
    }
    return result;
  }

  /**
   * Extend the given set of points at its fist and last points of k points
   * using central inversion.
   * @param line the input points
   * @param k the number of points to be added at the beginning and the end
   * @return an extended set of points
   */
  public static ILineString extend(ILineString line, int k) {
    IDirectPosition first = line.getControlPoint(0);
    IDirectPosition last = line.getControlPoint(line.sizeControlPoint() - 1);
    ILineString result = new GM_LineString();
    for (int index = 0; index < line.sizeControlPoint() + 2 * k; index++) {
      int position = index - k;
      if (index < k) {
        IDirectPosition p = line.getControlPoint(-position);
        result.addControlPoint(GaussianFilter.centralInversion(first, p));
      } else {
        if (position >= line.sizeControlPoint()) {
          int beyond = position - line.sizeControlPoint() + 1;
          IDirectPosition p = line.getControlPoint(line.sizeControlPoint() - 1
              - beyond);
          result.addControlPoint(GaussianFilter.centralInversion(last, p));
        } else {
          IDirectPosition p = line.getControlPoint(position);
          result.addControlPoint(p);
        }
      }
    }
    return result;
  }

  /**
   * Compute the central inversion of a position.
   * @param origin origin of symmetry
   * @param p a point
   * @return the result of the inversion through the given origin
   */
  private static IDirectPosition centralInversion(IDirectPosition origin,
      IDirectPosition p) {
   IDirectPosition result = new DirectPosition();
    result.setX(2 * origin.getX() - p.getX());
    result.setY(2 * origin.getY() - p.getY());
    return result;
  }
}
