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
  private final double NORM_DIFF_TOL = 0.0001;
  private Matrix y, p, xCurrent;
  private double poidsPtfFixe = 5;
  private double poids90 = 100;
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
    System.out.println("right_tol : " + rTol);
    System.out.println("flat_tol : " + flatTol);
    System.out.println("hright_tol1 : " + hrTol1);
    System.out.println("hright_tol2 : " + hrTol2);
    System.out.println("MAX_ITER : " + MAX_ITER);
    System.out.println("Min delta between 2 Iterations : " + NORM_DIFF_TOL);
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
        System.out.println((i + 1) + " angle droit : " + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
      // getting list of vertices with quasi flat angles
      else if (cross <= flatTol) {
        indicesFlat.add((i + 1) % nb_edges);
        System.out.println((i + 1) + " angle plat: " + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
      // getting list of vertices with quasi semi-right angles
      else if (dot <= hrTol1 && dot >= hrTol2) {
        indicesHrAig.add((i + 1) % nb_edges);
        System.out.println((i + 1) + " angle hr aig: " + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      } else if (dot >= -hrTol1 && dot <= -hrTol2) {
        indicesHrObt.add((i + 1) % nb_edges);
        System.out.println((i + 1) + " angle hr obt: " + vecs[i].vectorAngle(vecs[(i + 1) % nb_edges]));
      }
    }
    // observations matrix Y
    this.y = getY();
    // X0
    this.xCurrent = y.getMatrix(0, 2 * nb_edges - 1, 0, 0);
    // Weight matrix P
    this.p = getP();
    // this.p.print(5, 2);
    System.out.println("nombre angles :" + vecs.length);
    System.out.println("nombre angles potentiellement droits :" + indicesRight.size());
    System.out.println("nombre angles potentiellement plats :" + indicesFlat.size());
    System.out.println("nombre angles potentiellement  à 45 :" + (indicesHrAig.size() + indicesHrObt.size()));
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
        double df = points.get(pointsAround[0]).getX() - 2 * points.get(indicePoint).getX()
            + points.get(pointsAround[1]).getX();
        m.set(i, indicePoint * 2, df);
        // df/yn = yn+1 - 2yn + yn-1
        df = points.get(pointsAround[0]).getY() - 2 * points.get(indicePoint).getY()
            + points.get(pointsAround[1]).getY();
        m.set(i, indicePoint * 2 + 1, df);
        // df/xn-1 = -xn+1 + xn
        df = points.get(indicePoint).getX() - points.get(pointsAround[1]).getX();
        m.set(i, pointsAround[0] * 2, df);
        // df/yn-1 = -yn+1 + yn
        df = points.get(indicePoint).getY() - points.get(pointsAround[1]).getY();
        m.set(i, pointsAround[0] * 2 + 1, df);
        // df/xn+1 = xn - xn-1
        df = points.get(indicePoint).getX() - points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[1] * 2, df);
        // df/yn+1 = yn - yn-1
        df = points.get(indicePoint).getY() - points.get(pointsAround[0]).getY();
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
        double dg = points.get(pointsAround[1]).getY() - points.get(pointsAround[0]).getY();
        m.set(i, indicePoint * 2, dg);
        // dg/yn
        dg = points.get(pointsAround[0]).getX() - points.get(pointsAround[1]).getX();
        m.set(i, indicePoint * 2 + 1, dg);
        // dg/xn-1
        dg = points.get(indicePoint).getY() - points.get(pointsAround[1]).getY();
        m.set(i, pointsAround[0] * 2, dg);
        // dg/yn-1
        dg = points.get(pointsAround[1]).getX() - points.get(indicePoint).getX();
        m.set(i, pointsAround[0] * 2 + 1, dg);
        // dg/xn+1
        dg = points.get(pointsAround[0]).getY() - points.get(indicePoint).getY();
        m.set(i, pointsAround[1] * 2, dg);
        // dg/yn+1
        dg = points.get(indicePoint).getX() - points.get(pointsAround[0]).getX();
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

    Matrix a = new Matrix(2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows, 2 * nb_edges);
    a.setMatrix(0, 2 * nb_edges - 1, 0, 2 * nb_edges - 1, id);
    a.setMatrix(2 * nb_edges, 2 * nb_edges + scalNbRows - 1, 0, 2 * nb_edges - 1, scal);
    a.setMatrix(2 * nb_edges + scalNbRows, 2 * nb_edges + scalNbRows + crosNbRows - 1, 0, 2 * nb_edges - 1, cross);
    a.setMatrix(2 * nb_edges + scalNbRows + crosNbRows, 2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows - 1, 0,
        2 * nb_edges - 1, scalHr);
    a.setMatrix(2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows,
        2 * nb_edges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows - 1, 0, 2 * nb_edges - 1, scalHr2);
    return a;
  }

  // Obs Matrix (xo yo..xn yn 0..0 cos(pi/4)..cos(pi/4) cos(3pi/4)..cos(3pi/4))
  private Matrix getY() {
    Matrix y = new Matrix(
        2 * nb_edges + indicesRight.size() + indicesFlat.size() + indicesHrAig.size() + indicesHrObt.size(), 1);
    for (int i = 0; i < nb_edges; ++i) {
      y.set(2 * i, 0, points.get(i).getX());
      y.set((2 * i) + 1, 0, points.get(i).getY());
    }
    for (int i = 0; i < indicesHrAig.size(); ++i)
      y.set(2 * nb_edges + indicesRight.size() + indicesFlat.size() + i, 0, Math.cos(Math.PI / 4));
    for (int i = 0; i < indicesHrObt.size(); ++i)
      y.set(2 * nb_edges + indicesRight.size() + indicesFlat.size() + indicesHrAig.size() + i, 0,
          Math.cos(Math.PI * 3 / 4));
    return y;
  }

  // B = Y - S(Xcourant)
  public Matrix getB() {
    Matrix s = new Matrix(
        2 * nb_edges + indicesRight.size() + indicesFlat.size() + indicesHrAig.size() + indicesHrObt.size(), 1);
    for (int i = 0; i < nb_edges; ++i) {
      s.set(2 * i, 0, points.get(i).getX());
      s.set((2 * i) + 1, 0, points.get(i).getY());
    }
    // F(i-1,i,i+1) (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indicesRight.size(); ++i) {
      int[] pointsAround = getPointsAround(indicesRight.get(i));
      s.set(2 * nb_edges + i, 0, dotProduct(pointsAround[0], indicesRight.get(i), pointsAround[1]));
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
      s.set(2 * nb_edges + indicesRight.size() + indicesFlat.size() + indicesHrAig.size() + i, 0,
          dotProduct(pointsAround[0], indicesHrObt.get(i), pointsAround[1]));
    }
    return y.minus(s);
  }

  // Weight Matrix
  public Matrix getP() {
    int n = 2 * nb_edges + indicesRight.size() + indicesFlat.size() + indicesHrAig.size() + indicesHrObt.size();
    Matrix p = new Matrix(n, n);
    for (int i = 0; i < 2 * nb_edges; ++i)
      p.set(i, i, poidsPtfFixe);
    for (int i = 2 * nb_edges; i < 2 * nb_edges + indicesRight.size(); ++i)
      p.set(i, i, poids90);
    for (int i = 2 * nb_edges + indicesRight.size(); i < 2 * nb_edges + indicesRight.size() + indicesFlat.size(); ++i)
      p.set(i, i, poids0);
    for (int i = 2 * nb_edges + indicesRight.size() + indicesFlat.size(); i < n; ++i)
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
  public void setWeights(double poidsPtfFixe, double poids90, double poids0, double poids45) {
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

    // IPolygon pol = (IPolygon) WktGeOxygene.makeGeOxygene(
    // "POLYGON((0.7470703125 48.94559401268371,0.9228515625
    // 37.328235417231944,12.7001953125 36.62610836350546,23.5986328125
    // 37.328235417231944,23.5107421875 44.615497679489586,22.5439453125
    // 49.80395904562219,3.5870361328125 48.947037410050974,0.7470703125
    // 48.94559401268371))");

    IPolygon pol = (IPolygon) WktGeOxygene.makeGeOxygene(
        "POLYGON((342763.9 7685274.5, 342763.9 7685274.1, 342767.0 7685273.9, 342767.1 7685274.4, 342770.9 7685274.1, 342770.8 7685272.4, 342772.1 7685272.3, 342772.0 7685269.0, 342774.9 7685268.9, 342775.1 7685272.1, 342776.3 7685272.1, 342776.3 7685273.9, 342779.6 7685273.9, 342779.7 7685273.5, 342782.7 7685273.4, 342782.8 7685273.8, 342785.9 7685273.7, 342785.9 7685271.9, 342787.3 7685271.8, 342787.2 7685268.4, 342790.2 7685268.2, 342790.3 7685271.7, 342791.4 7685271.7, 342791.5 7685273.4, 342794.7 7685273.4, 342794.7 7685272.8 , 342797.7 7685272.7 , 342797.8 7685273.1 , 342801.5 7685273.1 , 342801.7 7685271.4 , 342803.1 7685271.5 , 342803.6 7685268.2 , 342806.4 7685268.6 , 342805.9 7685272.0 , 342807.3 7685272.3 , 342806.9 7685273.9 , 342810.6 7685275.1 , 342810.8 7685274.8 , 342813.6 7685275.7 , 342813.5 7685276.1 , 342817.2 7685277.1 , 342817.9 7685275.8 , 342819.1 7685276.5 , 342820.7 7685273.6 , 342823.3 7685275.1 , 342821.6 7685278.0 , 342822.7 7685278.7 , 342821.7 7685280.1 , 342824.6 7685282.4 , 342825.0 7685282.0 , 342827.3 7685284.0 , 342827.0 7685284.5 , 342829.8 7685286.9 , 342831.2 7685285.7 , 342832.0 7685286.5 , 342834.6 7685284.5 , 342841.3 7685292.7 , 342821.1 7685309.4 , 342819.1 7685306.7 , 342817.6 7685307.8 , 342815.7 7685309.1 , 342815.1 7685308.3 , 342813.2 7685305.8 , 342814.6 7685304.5 , 342816.0 7685303.2 , 342818.3 7685301.4 , 342817.0 7685299.7 , 342821.7 7685295.3 , 342823.7 7685293.5 , 342823.5 7685293.3 , 342823.4 7685293.2 , 342823.3 7685293.1 , 342823.1 7685293.0 , 342822.9 7685292.8 , 342822.8 7685292.7 , 342822.7 7685292.6 , 342822.6 7685292.5 , 342822.5 7685292.4 , 342822.4 7685292.2 , 342822.2 7685292.1 , 342822.0 7685291.9 , 342821.9 7685291.8 , 342821.8 7685291.7 , 342821.6 7685291.5 , 342821.4 7685291.3 , 342821.3 7685291.2 , 342819.3 7685293.2 , 342817.2 7685291.6 , 342818.8 7685289.4 , 342818.5 7685289.2 , 342818.3 7685289.1 , 342818.1 7685288.9 , 342817.9 7685288.7 , 342817.7 7685288.6 , 342817.5 7685288.4 , 342817.3 7685288.3 , 342817.1 7685288.1 , 342816.9 7685288.0 , 342816.7 7685287.8 , 342816.5 7685287.7 , 342816.2 7685287.5 , 342814.8 7685289.8 , 342812.4 7685288.3 , 342813.5 7685286.0 , 342813.2 7685285.8 , 342813.0 7685285.7 , 342812.7 7685285.6 , 342812.5 7685285.5 , 342812.2 7685285.3 , 342811.8 7685285.1 , 342811.5 7685285.0 , 342811.0 7685284.8 , 342810.8 7685284.7 , 342810.5 7685284.5 , 342809.4 7685287.1 , 342806.6 7685286.2 , 342807.3 7685283.6 , 342807.1 7685283.6 , 342806.8 7685283.5 , 342806.5 7685283.5 , 342806.2 7685283.4 , 342806.0 7685283.3 , 342805.7 7685283.3 , 342805.4 7685283.2 , 342805.2 7685283.1 , 342804.9 7685283.1 , 342804.6 7685283.0 , 342804.4 7685283.0 , 342804.1 7685282.9 , 342803.6 7685285.6 , 342800.9 7685285.1 , 342801.2 7685282.6 , 342801.0 7685282.6 , 342800.7 7685282.6 , 342800.4 7685282.6 , 342800.2 7685282.5 , 342799.9 7685282.5 , 342799.7 7685282.5 , 342799.4 7685282.5 , 342799.1 7685282.4 , 342798.9 7685282.4 , 342798.6 7685282.4 , 342798.4 7685282.4 , 342798.1 7685282.3 , 342797.8 7685282.3 , 342797.8 7685284.8 , 342795.1 7685284.6 , 342794.9 7685282.4 , 342794.5 7685282.4 , 342794.3 7685282.4 , 342794.0 7685282.4 , 342793.7 7685282.4 , 342793.4 7685282.4 , 342793.1 7685282.4 , 342792.5 7685282.4 , 342791.9 7685282.4 , 342791.3 7685282.4 , 342791.0 7685282.4 , 342790.7 7685282.5 , 342790.5 7685282.5 , 342790.6 7685284.7 , 342790.6 7685285.0 , 342790.2 7685285.0 , 342789.8 7685285.1 , 342789.3 7685285.1 , 342788.9 7685285.1 , 342788.5 7685285.2 , 342788.0 7685285.2 , 342787.7 7685285.2 , 342787.7 7685284.7 , 342787.6 7685282.6 , 342787.4 7685282.6 , 342787.1 7685282.7 , 342786.8 7685282.7 , 342786.5 7685282.7 , 342786.2 7685282.7 , 342785.8 7685282.7 , 342785.5 7685282.8 , 342785.2 7685282.8 , 342784.9 7685282.8 , 342784.6 7685282.8 , 342783.0 7685282.8 , 342783.0 7685284.9 , 342783.0 7685285.4 , 342782.8 7685285.4 , 342782.6 7685285.4 , 342782.2 7685285.4 , 342781.9 7685285.4 , 342781.7 7685285.4 , 342781.5 7685285.5 , 342781.4 7685285.5 , 342781.2 7685285.5 , 342781.0 7685285.5 , 342780.8 7685285.5 , 342780.7 7685285.5 , 342780.5 7685285.5 , 342780.3 7685285.6 , 342780.0 7685285.6 , 342780.0 7685285.1 , 342779.9 7685282.9 , 342775.4 7685283.1 , 342775.4 7685285.2 , 342775.4 7685285.7 , 342772.4 7685285.8 , 342772.4 7685285.3 , 342772.4 7685283.2 , 342767.7 7685283.4 , 342767.7 7685285.6 , 342767.7 7685286.0 , 342764.9 7685286.2 , 342764.5 7685283.6 , 342761.5 7685284.0 , 342762.1 7685286.5 , 342759.2 7685287.3 , 342758.3 7685284.8 , 342758.1 7685284.9 , 342757.8 7685285.0 , 342757.5 7685285.1 , 342757.1 7685285.2 , 342756.8 7685285.4 , 342756.5 7685285.5 , 342756.2 7685285.7 , 342755.8 7685285.8 , 342755.5 7685286.0 , 342756.6 7685288.3 , 342756.4 7685288.4 , 342756.2 7685288.5 , 342756.1 7685288.6 , 342756.0 7685288.6 , 342755.8 7685288.7 , 342755.6 7685288.8 , 342755.4 7685288.9 , 342755.3 7685289.0 , 342755.1 7685289.1 , 342754.8 7685289.2 , 342754.7 7685289.3 , 342754.6 7685289.3 , 342754.4 7685289.4 , 342754.2 7685289.5 , 342754.0 7685289.6 , 342752.7 7685287.4 , 342752.4 7685287.6 , 342752.2 7685287.8 , 342751.9 7685287.9 , 342751.6 7685288.1 , 342751.3 7685288.3 , 342750.8 7685288.6 , 342750.6 7685288.8 , 342750.3 7685289.0 , 342750.0 7685289.2 , 342751.5 7685291.3 , 342749.2 7685293.1 , 342748.9 7685292.7 , 342747.5 7685291.2 , 342747.3 7685291.4 , 342747.0 7685291.6 , 342746.8 7685291.8 , 342746.3 7685292.2 , 342746.0 7685292.4 , 342745.8 7685292.6 , 342745.5 7685292.8 , 342745.3 7685293.0 , 342745.0 7685293.2 , 342744.7 7685293.4 , 342744.5 7685293.6 , 342744.2 7685293.8 , 342744.1 7685294.0 , 342745.4 7685295.6 , 342745.8 7685296.0 , 342743.5 7685297.9 , 342741.3 7685299.8 , 342741.0 7685299.5 , 342739.6 7685297.7 , 342736.1 7685300.6 , 342737.5 7685302.3 , 342737.8 7685302.7 , 342735.5 7685304.6 , 342735.2 7685304.3 , 342733.8 7685302.5 , 342730.2 7685305.5 , 342731.7 7685307.2 , 342731.9 7685307.5 , 342729.7 7685309.4 , 342729.4 7685309.0 , 342728.0 7685307.4 , 342724.5 7685310.3 , 342725.9 7685311.9 , 342726.2 7685312.3 , 342723.9 7685314.3 , 342723.5 7685313.8 , 342722.2 7685312.2 , 342718.6 7685315.2 , 342712.6 7685308.0 , 342715.2 7685305.8 , 342714.2 7685304.7 , 342715.3 7685303.8 , 342713.1 7685301.2 , 342715.4 7685299.3 , 342717.6 7685301.9 , 342718.5 7685301.1 , 342719.6 7685302.5 , 342722.3 7685300.4 , 342721.9 7685300.0 , 342724.3 7685298.0 , 342724.5 7685298.3 , 342727.0 7685296.3 , 342725.9 7685294.9 , 342726.8 7685294.1 , 342724.9 7685291.7 , 342727.2 7685289.8 , 342727.6 7685290.2 , 342729.2 7685292.1 , 342730.0 7685291.3 , 342731.2 7685292.7 , 342733.8 7685290.7 , 342733.5 7685290.3 , 342738.0 7685286.6 , 342738.2 7685287.0 , 342740.8 7685284.9 , 342739.5 7685283.1 , 342740.3 7685282.4 , 342738.6 7685280.4 , 342738.5 7685280.2 , 342740.7 7685278.3 , 342742.8 7685280.9 , 342744.0 7685279.9 , 342745.0 7685281.3 , 342748.7 7685279.5 , 342748.5 7685279.0 , 342751.1 7685277.7 , 342751.3 7685278.0 , 342755.1 7685276.0 , 342754.7 7685274.3 , 342755.9 7685273.9 , 342755.1 7685271.0 , 342757.9 7685270.2 , 342758.8 7685273.2 , 342760.1 7685273.0 , 342760.3 7685274.8 , 342763.9 7685274.5))");

    pol = (IPolygon) WktGeOxygene.makeGeOxygene(
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
