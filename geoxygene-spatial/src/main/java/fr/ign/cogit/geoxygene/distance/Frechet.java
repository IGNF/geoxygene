package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Id;

import org.apache.xerces.impl.dtd.models.DFAContentModel;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * A class to compute the Fréchet distance.
 * @author Julien Perret
 */
public class Frechet {
  /**
   * Internal method for computing the discrete Fréchet distance using a dynamic
   * programming approach.
   * @param p linestring
   * @param q linestring
   * @param i index on p
   * @param j index on q
   * @param ca matrix containing the computed distances between the points from
   *          p and q. Its size is p*q.
   * @return the coupling measure between points i of p and j of q
   * @see #discreteFrechet(ILineString, ILineString)
   */
  private static double discreteFrechetCouplingMeasure(ILineString p,
      ILineString q, int i, int j, double[][] ca) {
    if (ca[i][j] > -1) {
      return ca[i][j];
    }
    double d = p.getControlPoint(i).distance(q.getControlPoint(j));
    if (i == 0 && j == 0) {
      return ca[i][j] = d;
    }
    if (i > 0 && j == 0) {
      return ca[i][j] = Math.max(
          discreteFrechetCouplingMeasure(p, q, i - 1, j, ca), d);
    }
    if (i == 0 && j > 0) {
      return ca[i][j] = Math.max(
          discreteFrechetCouplingMeasure(p, q, i, j - 1, ca), d);
    }
    if (i > 0 && j > 0) {
      return ca[i][j] = Math.max(Math.min(
          discreteFrechetCouplingMeasure(p, q, i - 1, j, ca), Math.min(
              discreteFrechetCouplingMeasure(p, q, i - 1, j - 1, ca),
              discreteFrechetCouplingMeasure(p, q, i, j - 1, ca))), d);
    }

    return ca[i][j] = Double.POSITIVE_INFINITY;
  }

  /**
   * Discrete Fréchet distance.
   * <p>
   * Complexity O(pq).
   * <p>
   * Eiter, Thomas; Mannila, Heikki (1994), Computing discrete Fréchet distance,
   * Tech. Report CD-TR 94/64, Christian Doppler Laboratory for Expert Systems,
   * TU Vienna, Austria.
   * @param p linestring
   * @param q linestring
   * @return the discrete Fréchet distance between the 2 input polygonal curves
   */
  public static double discreteFrechet(ILineString p, ILineString q) {
    // System.out.println("FRECHET P = " + p);
    // System.out.println("FRECHET Q = " + q);
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
   * Discrete Fréchet distance. This method reproject the points from each curve
   * to the other (only to the closest projection). This still has to be tested.
   * <p>
   * Complexity O(pq).
   * <p>
   * Eiter, Thomas; Mannila, Heikki (1994), Computing discrete Fréchet distance,
   * Tech. Report CD-TR 94/64, Christian Doppler Laboratory for Expert Systems,
   * TU Vienna, Austria.
   * @param p linestring
   * @param q linestring
   * @return the discrete Fréchet distance between the 2 input polygonal curves
   */
  public static double discreteFrechetWithProjection(ILineString p,
      ILineString q) {
    List<IDirectPosition> pPoints = new ArrayList<IDirectPosition>(p.coord());
    List<IDirectPosition> qPoints = new ArrayList<IDirectPosition>(q.coord());
    for (IDirectPosition point : p.getControlPoint()) {
      Operateurs.projectAndInsert(point, qPoints);
    }
    for (IDirectPosition point : q.getControlPoint()) {
      Operateurs.projectAndInsert(point, pPoints);
    }
    return discreteFrechet(new GM_LineString(pPoints), new GM_LineString(
        qPoints));
  }

  /**
   * <p>
   * Compute the discrete partial Frechet distance between two polygonal curves.
   * This algorithm is taken from (Devogele 2002), A New Merging Process for
   * Data Integration Based on the Discrete Fréchet Distance. <br>
   * The algorithm search for the distance between <b>a part of</b> p and <b>
   * the whole</b> line q. Thus, p must be shorter than q.
   * 
   * <br>
   * This is a non optimal algorithm running in O(n²).
   * <p>
   * @param p a linestring shorter than q
   * @param q a linestring
   * @return the partial discrete Frechet distance between the two curves.
   * **/
  public static double partialDiscreteFrechetDistance(ILineString p,
      ILineString q) {
    final List<IDirectPosition> pp = new ArrayList<IDirectPosition>(p.coord());
    final List<IDirectPosition> qp = new ArrayList<IDirectPosition>(q.coord());
    List<IDirectPosition> b = new ArrayList<IDirectPosition>(qp);
    List<IDirectPosition> e = new ArrayList<IDirectPosition>(qp);
    b.remove(b.size() - 1);
    Collections.reverse(e);
    e.remove(e.size() - 1);
    double dfdp = Double.POSITIVE_INFINITY;
    for (int j = 0; j < b.size(); j++) {
      if (pp.get(0).distance(b.get(j)) < dfdp) {
        for (int jj = 0; jj < e.size(); jj++) {
          if (j <= jj && pp.get(pp.size() - 1).distance(e.get(jj)) < dfdp) {
            try {
              int from = qp.indexOf(b.get(j));
              int to = qp.indexOf(e.get(jj));

              GM_LineString subcurve = from <= to ? new GM_LineString(
                  qp.subList(from, to)) : new GM_LineString(
                  qp.subList(to, from));
              double dfd = Frechet.discreteFrechet(p, subcurve);
              if (dfd < dfdp) {
                dfdp = dfd;
              }
            } catch (Exception e1) {
              System.out.println("error");
              e1.printStackTrace();
            }
          }
        }
      }
    }

    return dfdp;
  }

  static public double partialFrechet(final GM_LineString p,
      final GM_LineString q) {
    
    List<IDirectPosition> pp = new ArrayList<IDirectPosition>(p.coord());
    List<IDirectPosition> qp = new ArrayList<IDirectPosition>(q.coord());

    for (IDirectPosition point : p.getControlPoint()) {
      Operateurs.projectAndInsert(point, qp);
    }
    for (IDirectPosition point : q.getControlPoint()) {
      Operateurs.projectAndInsert(point, pp);
    }
    List<IDirectPosition> lcb = new ArrayList<IDirectPosition>(qp);
    List<IDirectPosition> lce = new ArrayList<IDirectPosition>(qp);

    // On ordonne les points
    Collections.sort(lcb, new Comparator<IDirectPosition>() {
      @Override
      public int compare(IDirectPosition o1, IDirectPosition o2) {
        double delta = o1.distance(p.coord().get(0))
            - o2.distance(p.coord().get(0));
        if (delta == 0.0)
          return 0;
        if (delta < 0.0)
          return 1;
        return -1;
      }
    });
    Collections.sort(lce, new Comparator<IDirectPosition>() {
      @Override
      public int compare(IDirectPosition o1, IDirectPosition o2) {
        double delta = o1.distance(p.coord().get(p.coord().size()-1))
            - o2.distance(p.coord().get(p.coord().size()-1));
        if (delta == 0.0)
          return 0;
        if (delta < 0.0)
          return 1;
        return -1;
      }
    });

    double pf = Double.POSITIVE_INFINITY;
    for (IDirectPosition l2j : lcb) {
      if (l2j.distance(pp.get(0)) < pf) {
        for (IDirectPosition l2jj : lce) {
          if (qp.indexOf(l2j) < qp.indexOf(l2jj)
              && pp.get(pp.size() - 1).distance(l2jj) < pf) {
            GM_LineString subcurve = new GM_LineString(l2j, l2jj);
            double df = Frechet.discreteFrechet(p, subcurve);
            if (df < pf) {
              pf = df;
            }
          }
        }
      }
    }
    return pf;
  }

  /**
   * 
   * Compute the free space cell as an ellipse intersecting the unit square.<br/>
   * Its inequation is giver by Ax² + Bxy + Cy² + Dx + Ey + F < epsilon²
   * Reference <a
   * href=" http://curve.carleton.ca/system/files/theses/27259.pdf">Frechet
   * Distance on Convex Polyhedron</a>
   * @param p1
   * @param p2
   * @param q1
   * @param q2
   * @return
   */
  public static double[] freeCellEllipse(IDirectPosition p1,
      IDirectPosition p2, IDirectPosition q1, IDirectPosition q2) {
    // Direct inequation of the ellipse in the unit square.
    double[] coef = new double[6];

    coef[0] = p1.distance(p2) * p1.distance(p2);
    coef[1] = q1.distance(q2) * q1.distance(q2);
    coef[2] = -2
        * ((p2.getX() - p1.getX()) * (q2.getX() - q1.getX()) + (p2.getY() - p1
            .getY()) * (q2.getY() - q1.getY()));
    coef[3] = 2 * ((p1.getX() - q1.getX()) * (p2.getX() - p1.getX()) + (p1
        .getY() - q1.getY()) * (p2.getY() - p1.getY()));
    coef[4] = 2 * ((p1.getX() - q1.getX()) * (q2.getX() - q1.getX()) + (p1
        .getY() - q1.getY()) * (q2.getY() - q1.getY()));
    coef[5] = ((p1.getX() - q1.getX()) * (p1.getX() - q1.getX()))
        + ((p1.getY() - q1.getY()) * (p1.getY() - q1.getY()));

    return coef;

  }

  public static IPolygon[][] freeSpaceDiagram(IDirectPositionList p,
      IDirectPositionList q, double sigma) {

    IPolygon[][] fsd = new GM_Polygon[q.size()][p.size()];
    double[][][][] localfsd = fsdCalculation(p, q, sigma);
    for (int i = 0; i < localfsd.length; i++) {
      for (int j = 0; j < localfsd[0].length; j++) {
        IDirectPositionList polygon = new DirectPositionList();

        for (int k = 0; k < localfsd[i][j][0].length; k++) {
          polygon.add(new DirectPosition(j + localfsd[i][j][0][k], q.size() - 1
              - i));
        }
        for (int k = localfsd[i][j][1].length - 1; k >= 0; k--) {
          polygon.add(new DirectPosition(j, q.size() - 1 - i
              + localfsd[i][j][1][k]));
        }
        for (int k = localfsd[i][j][2].length - 1; k >= 0; k--) {
          polygon
              .add(new DirectPosition(localfsd[i][j][2][k], q.size() - 1 - i));
        }
        for (int k = 0; k < localfsd[i][j][3].length; k++) {
          polygon.add(new DirectPosition(j - 1, q.size() - 1 - i
              + localfsd[i][j][3][k]));
        }
        polygon.add(polygon.get(0));
        fsd[i][j] = new GM_Polygon(new GM_LineString(polygon));

      }
    }
    return fsd;
  }

  public static double[][][][] fsdCalculation(IDirectPositionList p,
      IDirectPositionList q, double sigma) {
    double[][][][] fsd = new double[q.size()][p.size()][4][2];
    double[] top;
    double[] right;
    double[] bottom;
    double[] left;
    for (int j = q.size() - 1; j > 0; j--) {
      for (int i = p.size() - 1; i > 0; i--) {
        if (j == q.size() - 1) {
          top = boundary(q.get(j), p.get(i - 1), p.get(i), sigma);
        } else {
          top = fsd[j + 1][i][2];
        }
        if (i == p.size() - 1) {
          right = boundary(p.get(i), q.get(j - 1), q.get(j), sigma);
        } else {
          right = fsd[j][i + 1][3];
        }
        bottom = boundary(q.get(j - 1), p.get(i - 1), p.get(i), sigma);
        left = boundary(p.get(i - 1), q.get(j - 1), q.get(j), sigma);
        fsd[j][i][0] = top;
        fsd[j][i][1] = right;
        fsd[j][i][2] = bottom;
        fsd[j][i][3] = left;
      }
    }

    return fsd;
  }

  /**
   * Compute one side of a free space diagram cell. In other words, we compute
   * the intersection of the segment [q1;q2] with a circle of radius r and
   * centered in p;
   * @param p the center of the circle
   * @param q1 point of the segment
   * @param q2 point of the segment
   * @param r radius
   * @return
   */
  public static double[] boundary(IDirectPosition p, IDirectPosition q1,
      IDirectPosition q2, double r) {

    double[] result = new double[2];
    double l = q1.distance(q2);
    IDirectPosition[] intersection = Frechet.circleLineIntersect(q1.getX(),
        q1.getY(), q2.getX(), q2.getY(), p.getX(), p.getY(), r);

    // If something is reachable
    if (intersection != null) {
      if (intersection.length == 1) {
        result[0] = q1.distance(intersection[0]) / l;
      } else {
        // There is 2 intersection points.
        int pos1 = pointOnSegment(q1.getX(), q1.getY(), q2.getX(), q2.getY(),
            intersection[0].getX(), intersection[0].getY());
        int pos2 = pointOnSegment(q1.getX(), q1.getY(), q2.getX(), q2.getY(),
            intersection[1].getX(), intersection[1].getY());
        if (pos1 == pos2 && pos1 != 0) {
          return null;
        }
        if (pos1 < 0) {
          result[0] = 0.0;
        } else if (pos1 > 0) {
          result[0] = 1.0;
        } else {
          result[0] = q1.distance(intersection[0]) / l;
        }
        if (pos2 < 0) {
          result[1] = 0.0;
        } else if (pos2 > 0) {
          result[1] = 1.0;
        } else {
          result[1] = q1.distance(intersection[1]) / l;
        }
      }
    }
    return null;
  }

  /**
   * Points d'intersection entre une droite définie par 2 points (x1,y1);(x2,y2)
   * et un cercle de centre (cx,cy) et de rayon cr.
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   * @param cx
   * @param cy
   * @param cr
   * @return
   */
  public static DirectPosition[] circleLineIntersect(double x1, double y1,
      double x2, double y2, double cx, double cy, double cr) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double a = dx * dx + dy * dy;
    double b = 2 * (dx * (x1 - cx) + dy * (y1 - cy));
    double c = cx * cx + cy * cy;
    c += x1 * x1 + y1 * y1;
    c -= 2 * (cx * x1 + cy * y1);
    c -= cr * cr;
    double bb4ac = b * b - 4 * a * c;

    // Pas la peine d'aller plus loin, la ligne n'intersecte pas le cercle.
    if (bb4ac < 0) {
      return null;
    }
    double mu = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
    double ix1 = x1 + mu * (dx);
    double iy1 = y1 + mu * (dy);
    mu = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
    double ix2 = x1 + mu * (dx);
    double iy2 = y1 + mu * (dy);
    DirectPosition[] result = null;
    if (ix1 == ix2 && iy1 == iy2) {
      result = new DirectPosition[1];
      result[0] = new DirectPosition(ix1, iy1);
    } else {
      result = new DirectPosition[2];
      result[0] = new DirectPosition(ix1, iy1);
      result[1] = new DirectPosition(ix2, iy2);
    }
    return result;
  }

  public static int pointOnSegment(double x1, double y1, double x2, double y2,
      double x3, double y3) {
    double d = (distance(x1, y1, x3, y3) + distance(x3, y3, x2, y2) - distance(
        x1, y1, x2, y2));
    if (-0.0001d < d && d < 0.0001d) {
      return 0;
    }
    return ((distance(x1, y1, x3, y3) - distance(x2, y2, x3, y3)) > 0) ? -1 : 1;
  }

  public static double distance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
  }

}
