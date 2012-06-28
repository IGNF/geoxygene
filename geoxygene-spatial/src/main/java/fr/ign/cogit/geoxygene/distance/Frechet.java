package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * A class to compute the Fréchet distance.
 * @author Julien Perret
 */
public class Frechet {
  /**
   * Internal method for computing the discrete Fréchet distance using a dynamic programming approach.
   * @param p linestring
   * @param q linestring
   * @param i index on p
   * @param j index on q
   * @param ca matrix containing the computed distances between the points from p and q. Its size is p*q.
   * @return the coupling measure between points i of p and j of q
   * @see #discreteFrechet(ILineString, ILineString)
   */
  private static double discreteFrechetCouplingMeasure(ILineString p, ILineString q, int i, int j, double[][] ca) {
    if (ca[i][j] > -1) {
      return ca[i][j];
    }
    double d = p.getControlPoint(i).distance(q.getControlPoint(j));
    if (i == 0 && j == 0) {
      return ca[i][j] = d;
    }
    if (i > 0 && j == 0) {
      return ca[i][j] = Math.max(discreteFrechetCouplingMeasure(p, q, i - 1, j, ca), d);
    }
    if (i == 0 && j > 0) {
      return ca[i][j] = Math.max(discreteFrechetCouplingMeasure(p, q, i, j - 1, ca), d);
    }
    if (i > 0 && j > 0) {
      return ca[i][j] = Math.max(
          Math.min(discreteFrechetCouplingMeasure(p, q, i - 1, j, ca),
              Math.min(discreteFrechetCouplingMeasure(p, q, i - 1, j - 1, ca),
                  discreteFrechetCouplingMeasure(p, q, i, j - 1, ca))), d);
    }
    return ca[i][j] = Double.POSITIVE_INFINITY;
  }

  /**
   * Discrete Fréchet distance.
   * <p>
   * Complexity O(pq).
   * <p>
   * Eiter, Thomas; Mannila, Heikki (1994), Computing discrete Fréchet distance, Tech. Report CD-TR
   * 94/64, Christian Doppler Laboratory for Expert Systems, TU Vienna, Austria.
   * @param p linestring
   * @param q linestring
   * @return the discrete Fréchet distance between the 2 input polygonal curves
   */
  public static double discreteFrechet(ILineString p, ILineString q) {
    System.out.println("FRECHET P = " + p);
    System.out.println("FRECHET Q = " + q);
    int sizeP = p.sizeControlPoint();
    int sizeQ = q.sizeControlPoint();
    double[][] ca = new double[sizeP][sizeQ];
    for (int i = 0; i < sizeP; i++) {
      for (int j = 0; j < sizeQ; j++) {
        ca[i][j] = -1.0;
      }
    }
    return discreteFrechetCouplingMeasure(p, q, sizeP - 1, sizeQ - 1, ca);
  }

  /**
   * Discrete Fréchet distance. This method reproject the points from each curve to the other (only
   * to the closest projection). This still has to be tested.
   * <p>
   * Complexity O(pq).
   * <p>
   * Eiter, Thomas; Mannila, Heikki (1994), Computing discrete Fréchet distance, Tech. Report CD-TR
   * 94/64, Christian Doppler Laboratory for Expert Systems, TU Vienna, Austria.
   * @param p
   *        linestring
   * @param q
   *        linestring
   * @return the discrete Fréchet distance between the 2 input polygonal curves
   */
  public static double discreteFrechetWithProjection(ILineString p, ILineString q) {
    List<IDirectPosition> pPoints = new ArrayList<IDirectPosition>(p.coord());
    List<IDirectPosition> qPoints = new ArrayList<IDirectPosition>(q.coord());
    for (IDirectPosition point : p.getControlPoint()) {
      Operateurs.projectAndInsert(point, qPoints);
    }
    for (IDirectPosition point : q.getControlPoint()) {
      Operateurs.projectAndInsert(point, pPoints);
    }
    return discreteFrechet(new GM_LineString(pPoints), new GM_LineString(qPoints));
  }
}
