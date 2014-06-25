package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Circle;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Triangle;

public class LineCurvature {

  /**
   * Compute the curvature of a line in a given point, using the approximation
   * of the radius of the circumscribed circle of the trinagle formed by the
   * consecutive points around the given point.
   * @param line
   * @param point
   * @return
   */
  public static double getCircumscribedCircleCurvature(ILineString line,
      IDirectPosition point) {
    IDirectPosition pt1 = null, pt2 = null, pt3 = null;
    if (point.equals(line.startPoint(), 0.01))
      return 0.0;
    if (point.equals(line.endPoint(), 0.01))
      return 0.0;
    for (int i = 1; i < line.numPoints() - 1; i++) {
      if (line.coord().get(i).equals(point, 0.01)) {
        pt1 = line.coord().get(i - 1);
        pt2 = line.coord().get(i);
        pt3 = line.coord().get(i + 1);
      }
    }
    return getCircumscribedCircleCurvature(line, pt2, pt1, pt3);
  }

  /**
   * Compute the curvature of a line in a given point (and its neighbours),
   * using the approximation of the radius of the circumscribed circle of the
   * trinagle formed by the given three consecutive points.
   * @param line
   * @param point
   * @param pointBefore
   * @param pointAfter
   * @return
   */
  public static double getCircumscribedCircleCurvature(ILineString line,
      IDirectPosition point, IDirectPosition pointBefore,
      IDirectPosition pointAfter) {

    Triangle triangle = new Triangle(pointBefore, point, pointAfter);
    Circle circle = triangle.getCircumscribedCircle();

    return 1 / circle.getRadius();
  }
}
