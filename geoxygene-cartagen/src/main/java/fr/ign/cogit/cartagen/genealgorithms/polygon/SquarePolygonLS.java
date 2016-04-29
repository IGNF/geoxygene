/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Implementation of an algorithm to try to square polygons i.e when angles are
 * quasi flat, or right, then try to make them really flat or right using least
 * squares adjustment.
 * @author ILokhat
 * 
 */
public class SquarePolygonLS {

  private static final Logger logger = Logger.getLogger(SquarePolygonLS.class);

  private IDirectPositionList points;
  private int nb_edges;
  private Vector2D[] vecs;
  private double rTol; // 90° angles tolerance
  private double flatTol; // flat angles tolerance
  private double hrTol1; // 45° angles tolerance
  private double hrTol2; // 135° angles tolerance
  private List<Integer> indicesRight;
  private List<Integer> indicesFlat;
  private List<Integer> indicesHrAig;
  private List<Integer> indicesHrObt;
  private final int MAX_ITER = 1000;
  private final double NORM_DIFF_TOL = 0.001;
  private Matrix y, p, xCurrent;
  private double poidsPtfFixe = 5;
  private double poids90 = 50;
  private double poids0 = 15;
  private double poids45 = 10;
  private int nbIters;
  IPolygon pOriginal;

  /**
   * Constructor expecting tolerance in degrees for right angles and 45 degrees
   * angles, and a tolerance for flat angles (in meters, 0 if completely flat)
   * @param rightTol
   * @param flatTol between 0.098 and 0.11 seems a reasonable choice
   * @param semiRightTol
   */
  public SquarePolygonLS(double rightTol, double flatTol, double semiRightTol) {
    this.rTol = Math.cos((Math.PI / 2) - rightTol * Math.PI / 180);
    this.flatTol = flatTol;
    this.hrTol1 = Math.cos((Math.PI / 4) - semiRightTol * Math.PI / 180);
    this.hrTol2 = Math.cos((Math.PI / 4) + semiRightTol * Math.PI / 180);
    GeometryEngine.init();
    /*
     * System.out.println("right_tol : " + rTol); System.out.println(
     * "flat_tol : " + flatTol); System.out.println("hright_tol1 : " + hrTol1);
     * System.out.println("hright_tol2 : " + hrTol2); System.out.println(
     * "MAX_ITER : " + MAX_ITER); System.out.println(
     * "Min delta between 2 Iterations : " + NORM_DIFF_TOL);
     */
  }

  /**
   * sets the polygon to square
   * @param p
   */
  public void setPolygon(IPolygon p) {
    this.pOriginal = p;
    this.points = new DirectPositionList();
    // "zeroing" the z
    for (IDirectPosition pt : p.exteriorCoord())
      points.add(new DirectPosition(pt.getX(), pt.getY(), 0));
    this.nb_edges = points.size() - 1;
    this.vecs = new Vector2D[nb_edges];
    // getting vectors from polygon vertices
    for (int i = 0; i < nb_edges; ++i) {
      vecs[i] = new Vector2D(points.get(i), points.get((i + 1) % nb_edges));
      vecs[i].normalise();
    }
    indicesRight = new ArrayList<>();
    indicesFlat = new ArrayList<>();
    indicesHrAig = new ArrayList<>();
    indicesHrObt = new ArrayList<>();
    for (int i = 0; i < vecs.length; ++i) {
      double dot = vecs[i].prodScalaire(vecs[(i + 1) % nb_edges]);
      double cross = vecs[i].prodVectoriel(vecs[(i + 1) % nb_edges]).norme();
      // getting list of vertices with quasi right angles
      if (Math.abs(dot) <= rTol) {
        indicesRight.add((i + 1) % nb_edges);

        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle droit : "
              + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
      // getting list of vertices with quasi flat angles
      else if (cross <= flatTol) {
        indicesFlat.add((i + 1) % nb_edges);

        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle plat: "
              + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
      // getting list of vertices with quasi semi-right angles
      else if (dot <= hrTol1 && dot >= hrTol2) {
        indicesHrAig.add((i + 1) % nb_edges);
        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle hr aig: "
              + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));

      } else if (dot >= -hrTol1 && dot <= -hrTol2) {
        indicesHrObt.add((i + 1) % nb_edges);
        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle hr obt: "
              + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
    }
    // observations matrix Y
    this.y = getY();
    // X0
    this.xCurrent = y.getMatrix(0, 2 * nb_edges - 1, 0, 0);
    // Weight matrix P
    this.p = getP();
    // this.p.print(5, 2);

    if (logger.isDebugEnabled()) {
      System.out.println("nombre angles :" + vecs.length);
      System.out.println(
          "nombre angles potentiellement droits :" + indicesRight.size());
      System.out.println(
          "nombre angles potentiellement plats :" + indicesFlat.size());
      System.out.println("nombre angles potentiellement  à 45 :"
          + (indicesHrAig.size() + indicesHrObt.size()));
    }
  }

  // return indices of points around pi
  private int[] getPointsAround(int i) {
    int p1 = i == 0 ? vecs.length - 1 : i - 1;
    int p2 = (i + 1) % nb_edges;
    return new int[] { p1, p2 };
  }

  // dot product of ab and bc
  private double dotProduct(int a, int b, int c) {
    Vector2D v1 = new Vector2D(points.get(a), points.get(b));
    Vector2D v2 = new Vector2D(points.get(b), points.get(c));
    v1.normalise();
    v2.normalise();
    return v1.prodScalaire(v2);
  }

  // cross product of ab and bc
  private double crossProduct(int a, int b, int c) {
    Vector2D v1 = new Vector2D(points.get(a), points.get(b));
    Vector2D v2 = new Vector2D(points.get(b), points.get(c));
    return v1.prodVectoriel(v2).getZ();
  }

  private Matrix getIdentityMatrix() {
    return Matrix.identity(nb_edges * 2, nb_edges * 2);
  }

  // creates the submatrix corresponding to the partial derivatives of the
  // scalar product
  private Matrix getDotProductSubMatrix(List<Integer> indices) {
    Matrix m = new Matrix(0, 0);
    if (indices.size() > 0) {
      m = new Matrix(indices.size(), 2 * nb_edges);
      for (int i = 0; i < m.getRowDimension(); ++i) {
        int indicePoint = indices.get(i);
        int[] pointsAround = getPointsAround(indicePoint);
        // df/xn = xn+1 - 2xn + xn-1
        double df = points.get(pointsAround[0]).getX()
            - 2 * points.get(indicePoint).getX()
            + points.get(pointsAround[1]).getX();
        m.set(i, indicePoint * 2, df);
        // df/yn = yn+1 - 2yn + yn-1
        df = points.get(pointsAround[0]).getY()
            - 2 * points.get(indicePoint).getY()
            + points.get(pointsAround[1]).getY();
        m.set(i, indicePoint * 2 + 1, df);
        // df/xn-1 = -xn+1 + xn
        df = points.get(indicePoint).getX()
            - points.get(pointsAround[1]).getX();
        m.set(i, pointsAround[0] * 2, df);
        // df/yn-1 = -yn+1 + yn
        df = points.get(indicePoint).getY()
            - points.get(pointsAround[1]).getY();
        m.set(i, pointsAround[0] * 2 + 1, df);
        // df/xn+1 = xn - xn-1
        df = points.get(indicePoint).getX()
            - points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[1] * 2, df);
        // df/yn+1 = yn - yn-1
        df = points.get(indicePoint).getY()
            - points.get(pointsAround[0]).getY();
        m.set(i, pointsAround[1] * 2 + 1, df);
      }
    }
    return m;
  }

  public Matrix getCrossProductSubMatrix() {
    Matrix m = new Matrix(0, 0);
    if (indicesFlat.size() > 0) {
      m = new Matrix(indicesFlat.size(), 2 * nb_edges);
      for (int i = 0; i < m.getRowDimension(); ++i) {
        int indicePoint = indicesFlat.get(i);
        int[] pointsAround = getPointsAround(indicePoint);
        // dg/xn
        double dg = points.get(pointsAround[1]).getY()
            - points.get(pointsAround[0]).getY();
        m.set(i, indicePoint * 2, dg);
        // dg/yn
        dg = points.get(pointsAround[0]).getX()
            - points.get(pointsAround[1]).getX();
        m.set(i, indicePoint * 2 + 1, dg);
        // dg/xn-1
        dg = points.get(indicePoint).getY()
            - points.get(pointsAround[1]).getY();
        m.set(i, pointsAround[0] * 2, dg);
        // dg/yn-1
        dg = points.get(pointsAround[1]).getX()
            - points.get(indicePoint).getX();
        m.set(i, pointsAround[0] * 2 + 1, dg);
        // dg/xn+1
        dg = points.get(pointsAround[0]).getY()
            - points.get(indicePoint).getY();
        m.set(i, pointsAround[1] * 2, dg);
        // dg/yn+1
        dg = points.get(indicePoint).getX()
            - points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[1] * 2 + 1, dg);
      }
    }
    return m;
  }

  // get the model Matrix
  public Matrix getA() {
    // re computing vectors
    for (int i = 0; i < nb_edges; ++i)
      vecs[i] = new Vector2D(points.get(i), points.get((i + 1) % nb_edges));
    // getting sub matrixes
    Matrix id = getIdentityMatrix();
    Matrix scal = getDotProductSubMatrix(indicesRight);
    Matrix cross = getCrossProductSubMatrix();
    Matrix scalHr = getDotProductSubMatrix(indicesHrAig);
    Matrix scalHr2 = getDotProductSubMatrix(indicesHrObt);
    int scalNbRows = scal.getRowDimension();
    int crosNbRows = cross.getRowDimension();
    int scHr1NbRows = scalHr.getRowDimension();
    int scHr2NbRows = scalHr2.getRowDimension();

    Matrix a = new Matrix(
        2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows,
        2 * nb_edges);
    a.setMatrix(0, 2 * nb_edges - 1, 0, 2 * nb_edges - 1, id);
    a.setMatrix(2 * nb_edges, 2 * nb_edges + scalNbRows - 1, 0,
        2 * nb_edges - 1, scal);
    a.setMatrix(2 * nb_edges + scalNbRows,
        2 * nb_edges + scalNbRows + crosNbRows - 1, 0, 2 * nb_edges - 1, cross);
    a.setMatrix(2 * nb_edges + scalNbRows + crosNbRows,
        2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows - 1, 0,
        2 * nb_edges - 1, scalHr);
    a.setMatrix(2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows,
        2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows - 1,
        0, 2 * nb_edges - 1, scalHr2);
    return a;
  }

  // Obs Matrix (xo yo..xn yn 0..0 cos(pi/4)..cos(pi/4) cos(3pi/4)..cos(3pi/4))
  private Matrix getY() {
    Matrix y = new Matrix(2 * nb_edges + indicesRight.size()
        + indicesFlat.size() + indicesHrAig.size() + indicesHrObt.size(), 1);
    for (int i = 0; i < nb_edges; ++i) {
      y.set(2 * i, 0, points.get(i).getX());
      y.set((2 * i) + 1, 0, points.get(i).getY());
    }
    for (int i = 0; i < indicesHrAig.size(); ++i)
      y.set(2 * nb_edges + indicesRight.size() + indicesFlat.size() + i, 0,
          Math.cos(Math.PI / 4));
    for (int i = 0; i < indicesHrObt.size(); ++i)
      y.set(2 * nb_edges + indicesRight.size() + indicesFlat.size()
          + indicesHrAig.size() + i, 0, Math.cos(Math.PI * 3 / 4));
    return y;
  }

  // B = Y - S(Xcourant)
  public Matrix getB() {
    Matrix s = new Matrix(2 * nb_edges + indicesRight.size()
        + indicesFlat.size() + indicesHrAig.size() + indicesHrObt.size(), 1);
    for (int i = 0; i < nb_edges; ++i) {
      s.set(2 * i, 0, points.get(i).getX());
      s.set((2 * i) + 1, 0, points.get(i).getY());
    }
    // F(i-1,i,i+1) (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indicesRight.size(); ++i) {
      int[] pointsAround = getPointsAround(indicesRight.get(i));
      s.set(2 * nb_edges + i, 0,
          dotProduct(pointsAround[0], indicesRight.get(i), pointsAround[1]));
    }
    // G(i-1,i,i+1) (Xn-1 Xn)^(Xn Xn+1)
    for (int i = 0; i < indicesFlat.size(); ++i) {
      int[] pointsAround = getPointsAround(indicesFlat.get(i));
      s.set(2 * nb_edges + indicesRight.size() + i, 0,
          crossProduct(pointsAround[0], indicesFlat.get(i), pointsAround[1]));
    }
    // angles pi/4 (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indicesHrAig.size(); ++i) {
      int[] pointsAround = getPointsAround(indicesHrAig.get(i));
      s.set(2 * nb_edges + indicesRight.size() + indicesFlat.size() + i, 0,
          dotProduct(pointsAround[0], indicesHrAig.get(i), pointsAround[1]));
    }
    // angles 3pi/4 (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indicesHrObt.size(); ++i) {
      int[] pointsAround = getPointsAround(indicesHrObt.get(i));
      s.set(
          2 * nb_edges + indicesRight.size() + indicesFlat.size()
              + indicesHrAig.size() + i,
          0, dotProduct(pointsAround[0], indicesHrObt.get(i), pointsAround[1]));
    }
    return y.minus(s);
  }

  // Weight Matrix
  public Matrix getP() {
    int n = 2 * nb_edges + indicesRight.size() + indicesFlat.size()
        + indicesHrAig.size() + indicesHrObt.size();
    Matrix p = new Matrix(n, n);
    for (int i = 0; i < 2 * nb_edges; ++i)
      p.set(i, i, poidsPtfFixe);
    for (int i = 2 * nb_edges; i < 2 * nb_edges + indicesRight.size(); ++i)
      p.set(i, i, poids90);
    for (int i = 2 * nb_edges + indicesRight.size(); i < 2 * nb_edges
        + indicesRight.size() + indicesFlat.size(); ++i)
      p.set(i, i, poids0);
    for (int i = 2 * nb_edges + indicesRight.size()
        + indicesFlat.size(); i < n; ++i)
      p.set(i, i, poids45);
    return p;
  }

  // compute Dx = (A'PA)^1.(A'PB)
  private Matrix computeDx() {
    Matrix a = getA();
    Matrix atp = a.transpose().times(p);
    Matrix dx = (atp).times(a).inverse().times(atp.times(getB()));

    return dx;
  }

  // update points with x
  private void setNewPoints(Matrix x) {
    for (int i = 0; i < x.getRowDimension(); i = i + 2)
      points.set(i / 2, new DirectPosition(x.get(i, 0), x.get(i + 1, 0)));
  }

  /**
   * returns an IPolygon with its angles squared or flattened using least
   * squares compensation
   * @return
   */
  public IPolygon square() {
    Matrix x = this.xCurrent;
    Matrix dx;
    int i = 0;
    long begin = System.nanoTime();
    do {
      // System.out.println("iter " + i);
      this.xCurrent = x.copy();
      dx = computeDx();
      x = x.plus(dx);
      setNewPoints(x);
      ++i;
      // System.out.println(i + " " + this.asWKT());
      // System.out.println("epsi " + x.minus(xCurrent).normF());
    } while (i < MAX_ITER && x.minus(xCurrent).normF() > NORM_DIFF_TOL);
    nbIters = i;
    points.set(points.size() - 1, points.get(0));
    IDirectPositionList pointsoriginal = pOriginal.exteriorCoord();
    for (int j = 0; j < pointsoriginal.size(); ++j)
      points.get(j).setZ(pointsoriginal.get(j).getZ());
    AbstractGeomFactory factory = AbstractGeometryEngine.getFactory();
    long end = System.nanoTime();
    System.out.println();
    System.out.println("computed in " + (end - begin) / 1000000 + " ms");
    System.out.println("nb iters : " + nbIters);
    return factory.createIPolygon(points);
  }

  /**
   * returns a wkt string of the polygon
   * @return
   */
  public String asWKT() {
    String s = "POLYGON((";
    for (int i = 0; i < nb_edges; ++i)
      s += points.get(i).getX() + " " + points.get(i).getY() + ", ";
    s += points.get(0).getX() + " " + points.get(0).getY() + "))";
    return s;
  }

  /**
   * set weights for constraints
   * @param poidsPtfFixe for non moving points
   * @param poids90 for right angles
   * @param poids0 for flat angles
   * @param poids45 for 45 degrees angles
   */
  public void setWeights(double poidsPtfFixe, double poids90, double poids0,
      double poids45) {
    this.poidsPtfFixe = poidsPtfFixe;
    this.poids90 = poids90;
    this.poids0 = poids0;
    this.poids45 = poids45;
  }

  public int getNbIters() {
    return nbIters;
  }

  public static void main(String[] args) throws ParseException {
    // IPolygon pol = (IPolygon) WktGeOxygene
    // .makeGeOxygene("POLYGON((9.404296875 46.12198680728803,0.439453125
    // 33.782799038378236,12.744140625 36.37175953328168,12.919921875
    // 22.496316413974803,27.333984375 22.982668749764606,25.927734375
    // 32.45786315594498,24.873046875 41.82372918453599,24.169921875
    // 47.088076832429145,9.404296875 46.12198680728803))");

    IPolygon pol = (IPolygon) WktGeOxygene.makeGeOxygene(
        "POLYGON((342763.66688345832517371 7685320.84001446887850761,342763.66688345832517371 7685308.73941662814468145,342772.06036172935273498 7685309.01919923722743988,342770.17182911833515391 7685320.42034055478870869,342763.66688345832517371 7685320.84001446887850761))");

    // IPolygon pol = (IPolygon) WktGeOxygene
    // .makeGeOxygene("POLYGON((638185.80000000004656613
    // 6871014.59999999962747097,638188.40000000002328306
    // 6871011.79999999981373549,638186 6871009.5,638186.80000000004656613
    // 6871008.59999999962747097,638185
    // 6871006.90000000037252903,638184.19999999995343387
    // 6871007.79999999981373549,638181.69999999995343387
    // 6871005.40000000037252903,638186.19999999995343387
    // 6871000.90000000037252903,638187.5
    // 6870999.59999999962747097,638197.30000000004656613
    // 6871009.09999999962747097,638190.80000000004656613
    // 6871015.59999999962747097,638188.80000000004656613
    // 6871017.70000000018626451,638186.69999999995343387
    // 6871015.40000000037252903,638185.80000000004656613
    // 6871014.59999999962747097))");

    long begin = System.nanoTime();
    SquarePolygonLS sq = new SquarePolygonLS(10, 0.11, 7);
    sq.setPolygon(pol);
    IPolygon pol2 = sq.square();
    long end = System.nanoTime();
    System.out.println();
    System.out.println("computed in " + (end - begin) / 1000000 + " ms");
    System.out.println("nb iters : " + sq.getNbIters());
    System.out.println(sq.asWKT());
    System.out.println("original pol area " + pol.area());
    System.out.println("squared pol area " + pol2.area());

  }
}
