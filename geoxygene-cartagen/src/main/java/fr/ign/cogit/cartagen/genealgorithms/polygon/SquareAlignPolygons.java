package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
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
 * squares adjustment. Here we added a constraint to try to maintain alignment
 * between polygons side by side in the list passed to the class
 * @author ILokhat
 * 
 */

public class SquareAlignPolygons {

  private static final Logger logger = Logger.getLogger(SquareAlignPolygons.class);

  private IDirectPositionList points; // P0..Pn Q0..Qm.. Z0..Zp
  private List<int[]> indicesRightAngles; // n-1, n, n+1
  private List<int[]> indicesFlatAngles; // n-1, n, n+1
  private List<int[]> indices45; // n-1, n, n+1
  private List<int[]> indices135; // n-1, n, n+1
  private List<int[]> indicesAligns; // n-1, n, q-1, q
  private List<IPolygon> originals;
  private double rTol; // 90° angles tolerance
  private double flatTol; // flat angles tolerance
  private double hrTol1; // 45° angles tolerance
  private double hrTol2; // 135° angles tolerance
  private double alignTol; // alignment tolerance
  private Matrix y, p, xCurrent, idMatrix;
  private double poidsPtfFixe = 5;
  private double poids90 = 100;
  private double poids0 = 15;
  private double poids45 = 10;
  private double poidsAlign = 15;
  private final int MAX_ITER = 1000;
  private final double NORM_DIFF_TOL = 0.0001;
  private int nbIters;

  /**
   * Constructor expecting tolerance in degrees for right angles and 45 degrees
   * angles, and a tolerance for flat angles (in meters, 0 if completely flat)
   * @param rightTol
   * @param flatTol between 0.098 and 0.11 seems a reasonable choice
   * @param semiRightTol
   */
  public SquareAlignPolygons(double rightTol, double flatTol, double semiRightTol) {
    this.rTol = Math.cos((Math.PI / 2) - rightTol * Math.PI / 180);
    this.flatTol = flatTol;
    this.alignTol = flatTol;
    this.hrTol1 = Math.cos((Math.PI / 4) - semiRightTol * Math.PI / 180);
    this.hrTol2 = Math.cos((Math.PI / 4) + semiRightTol * Math.PI / 180);
    GeometryEngine.init();
  }

  /**
   * Constructor expecting tolerance in degrees for right angles and 45 degrees
   * angles, and a tolerance for flat angles (in meters, 0 if completely flat)
   * @param rightTol
   * @param flatTol between 0.098 and 0.11 seems a reasonable choice
   * @param semiRightTol
   * @param alignTol similar to flatTol
   */
  public SquareAlignPolygons(double rightTol, double flatTol, double semiRightTol, double alignTol) {
    this.rTol = Math.cos((Math.PI / 2) - rightTol * Math.PI / 180);
    this.flatTol = flatTol;
    this.hrTol1 = Math.cos((Math.PI / 4) - semiRightTol * Math.PI / 180);
    this.hrTol2 = Math.cos((Math.PI / 4) + semiRightTol * Math.PI / 180);
    this.alignTol = alignTol;
    GeometryEngine.init();
  }

  public void setPolygons(List<IPolygon> polygons) {
    originals = polygons;
    points = new DirectPositionList();
    indicesRightAngles = new ArrayList<>();
    indicesFlatAngles = new ArrayList<>();
    indices45 = new ArrayList<>();
    indices135 = new ArrayList<>();
    indicesAligns = new ArrayList<>();
    int offset = 0;
    for (IPolygon p : polygons) {
      IDirectPositionList listePoints = p.exteriorCoord();
      // creating list of points with z = 0
      for (int i = 0; i < listePoints.size() - 1; ++i)
        points.add(new DirectPosition(listePoints.get(i).getX(), listePoints.get(i).getY(), 0));
      // setting point indices for angles constraints in each appropriate list
      setIndicesForAnglesConstraints(p, offset);
      offset += (listePoints.size() - 1);
    }
    // setting point indices for alignment constraint
    offset = 0;
    for (int i = 0; i < polygons.size() - 1; ++i) {
      int[] aligns = getIndicesNearestSides(polygons.get(i), polygons.get(i + 1), offset);
      // System.out.println(Arrays.toString(aligns));
      Vector2D vecP = new Vector2D(points.get(aligns[0]), points.get(aligns[1]));
      Vector2D vecQ = new Vector2D(points.get(aligns[2]), points.get(aligns[3]));
      double cross = vecP.getNormalised().prodVectoriel(vecQ.getNormalised()).norme();
      if (cross <= alignTol) {
        indicesAligns.add(aligns);
        if (logger.isDebugEnabled())
          System.out.println(Arrays.toString(aligns));
      }
      offset += (polygons.get(i).numPoints() - 1);
    }

    int nbEdges = points.size();
    this.y = getY();
    this.xCurrent = y.getMatrix(0, 2 * nbEdges - 1, 0, 0);
    this.p = getP();
    // identity Matrix
    this.idMatrix = Matrix.identity(nbEdges * 2, nbEdges * 2);
  }

  private void setIndicesForAnglesConstraints(IPolygon p, int offset) {
    IDirectPositionList listePoints = p.exteriorCoord();
    int nbEdges = listePoints.size() - 1;
    IDirectPositionList pointsP = new DirectPositionList();
    for (int i = 0; i < nbEdges; ++i)
      pointsP.add(new DirectPosition(listePoints.get(i).getX(), listePoints.get(i).getY(), 0));
    Vector2D[] vecs = new Vector2D[nbEdges];
    for (int i = 0; i < nbEdges; ++i) {
      vecs[i] = new Vector2D(pointsP.get(i), pointsP.get((i + 1) % nbEdges));
    }
    for (int i = 0; i < vecs.length; ++i) {
      double dot = (vecs[i]).getNormalised().prodScalaire((vecs[(i + 1) % nbEdges]).getNormalised());
      double cross = (vecs[i]).getNormalised().prodVectoriel((vecs[(i + 1) % nbEdges]).getNormalised()).norme();
      // getting list of vertices with quasi right angles
      if (Math.abs(dot) <= rTol) {
        int[] rights = getPointsAround(nbEdges, (i + 1) % nbEdges, offset);
        indicesRightAngles.add(rights);

        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle droit : " + vecs[i].vectorAngle(vecs[(i + 1) % nbEdges]));
      }
      // getting list of vertices with quasi flat angles
      else if (cross <= flatTol) {
        int[] flats = getPointsAround(nbEdges, (i + 1) % nbEdges, offset);
        indicesFlatAngles.add(flats);

        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle plat: " + vecs[i].vectorAngle(vecs[(i + 1) % nbEdges]));
      }
      // getting list of vertices with quasi semi-right angles
      else if (dot <= hrTol1 && dot >= hrTol2) {
        int[] angles45 = getPointsAround(nbEdges, (i + 1) % nbEdges, offset);
        indices45.add(angles45);
        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle hr aig: " + vecs[i].vectorAngle(vecs[(i + 1) % nbEdges]));

      } else if (dot >= -hrTol1 && dot <= -hrTol2) {
        int[] angles135 = getPointsAround(nbEdges, (i + 1) % nbEdges, offset);
        indices135.add(angles135);
        if (logger.isDebugEnabled())
          System.out.println((i + 1) + " angle hr obt: " + vecs[i].vectorAngle(vecs[(i + 1) % nbEdges]));
      }
    }

  }

  // return indices of points around i for a polygon with nbEdges edges
  private int[] getPointsAround(int nbEdges, int i, int offset) {
    int p1 = i == 0 ? nbEdges - 1 : i - 1;
    int p2 = (i + 1) % nbEdges;
    return new int[] { p1 + offset, i + offset, p2 + offset };
  }

  // segment mid closest to the next polygon
  private int[] getIndicesNearestSides(IPolygon p, IPolygon q, int offset) {
    int[] inds = { -1, -1, -1, -1 };// new int[4];
    AbstractGeomFactory factory = AbstractGeometryEngine.getFactory();
    IDirectPositionList ptsP = p.coord();
    IDirectPositionList ptsQ = q.coord();
    int nbEdges = ptsP.size() - 1;
    double minDist = Double.MAX_VALUE;
    for (int i = 0; i < nbEdges; ++i) {
      ILineSegment seg = factory.createLineSegment(ptsP.get(i), ptsP.get(i + 1));
      IPoint mil = factory.createPoint(seg.centroid());
      if (mil.distance(q) < minDist) {
        minDist = mil.distance(q);
        inds[0] = i;
        inds[1] = (i + 1) % nbEdges;
      }
    }
    // ILineSegment segP = factory.createLineSegment(ptsP.get(inds[0]),
    // ptsP.get(inds[1]));
    // System.out.println(segP);
    minDist = Double.MAX_VALUE;
    nbEdges = ptsQ.size() - 1;
    for (int i = 0; i < nbEdges; ++i) {
      ILineSegment seg = factory.createLineSegment(ptsQ.get(i), ptsQ.get(i + 1));
      IPoint mil = factory.createPoint(seg.centroid());
      if (mil.distance(p) < minDist) {
        minDist = mil.distance(p);
        inds[2] = i;
        inds[3] = (i + 1) % nbEdges;
      }
    }
    // System.out.println(factory.createLineSegment(ptsQ.get(inds[2]),
    // ptsQ.get(inds[3])));
    inds[0] += offset;
    inds[1] += offset;
    inds[2] += offset + ptsP.size() - 1;
    inds[3] += offset + ptsP.size() - 1;
    return inds;
  }

  // Obs Matrix (xo yo..xn yn 0..0 cos(pi/4)..cos(pi/4) cos(3pi/4)..cos(3pi/4))
  // 0..0
  private Matrix getY() {
    int nbEdges = points.size();
    Matrix y = new Matrix(2 * points.size() + indicesRightAngles.size() + indicesFlatAngles.size() + indices45.size()
        + indices135.size() + indicesAligns.size(), 1);
    for (int i = 0; i < nbEdges; ++i) {
      y.set(2 * i, 0, points.get(i).getX());
      y.set((2 * i) + 1, 0, points.get(i).getY());
    }
    for (int i = 0; i < indices45.size(); ++i) {
      int[] ind = indices45.get(i);
      Vector2D v0 = new Vector2D(points.get(ind[0]), points.get(ind[1]));
      Vector2D v1 = new Vector2D(points.get(ind[1]), points.get(ind[2]));
      double d = v0.norme() * v1.norme();
      y.set(2 * nbEdges + indicesRightAngles.size() + indicesFlatAngles.size() + i, 0, Math.cos(Math.PI / 4) * d);
    }
    for (int i = 0; i < indices135.size(); ++i) {
      int[] ind = indices45.get(i);
      Vector2D v0 = new Vector2D(points.get(ind[0]), points.get(ind[1]));
      Vector2D v1 = new Vector2D(points.get(ind[1]), points.get(ind[2]));
      double d = v0.norme() * v1.norme();
      y.set(2 * nbEdges + indicesRightAngles.size() + indicesFlatAngles.size() + indices45.size() + i, 0,
          Math.cos(Math.PI * 3 / 4) * d);
    }
    return y;
  }

  // Weight Matrix
  public Matrix getP() {
    int nbEdges = points.size();
    int nbRights = indicesRightAngles.size();
    int nbFlats = indicesFlatAngles.size();
    int nbHr = indices45.size() + indices135.size();
    int nbAligns = indicesAligns.size();
    int n = 2 * nbEdges + nbRights + nbFlats + nbHr + nbAligns;
    Matrix p = new Matrix(n, n);
    for (int i = 0; i < 2 * nbEdges; ++i)
      p.set(i, i, poidsPtfFixe);
    for (int i = 2 * nbEdges; i < 2 * nbEdges + nbRights; ++i)
      p.set(i, i, poids90);
    for (int i = 2 * nbEdges + nbRights; i < 2 * nbEdges + nbRights + nbFlats; ++i)
      p.set(i, i, poids0);
    for (int i = 2 * nbEdges + nbRights + nbFlats; i < 2 * nbEdges + nbRights + nbFlats + nbHr; ++i)
      p.set(i, i, poids45);
    for (int i = 2 * nbEdges + nbRights + nbFlats + nbHr; i < n; ++i)
      p.set(i, i, poidsAlign);
    return p;
  }

  // get the model Matrix
  public Matrix getA() {
    int nbEdges = points.size();
    // getting sub matrixes
    Matrix scal = getDotProductSubMatrix(indicesRightAngles);
    Matrix cross = getCrossProductSubMatrix();
    Matrix scalHr = getDotProductSubMatrix(indices45);
    Matrix scalHr2 = getDotProductSubMatrix(indices135);
    Matrix align = getAlignSubMatrix();
    int scalNbRows = scal.getRowDimension();
    int crosNbRows = cross.getRowDimension();
    int scHr1NbRows = scalHr.getRowDimension();
    int scHr2NbRows = scalHr2.getRowDimension();
    int alignNbRows = align.getRowDimension();

    Matrix a = new Matrix(2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows + alignNbRows, 2 * nbEdges);
    a.setMatrix(0, 2 * nbEdges - 1, 0, 2 * nbEdges - 1, idMatrix);
    a.setMatrix(2 * nbEdges, 2 * nbEdges + scalNbRows - 1, 0, 2 * nbEdges - 1, scal);
    a.setMatrix(2 * nbEdges + scalNbRows, 2 * nbEdges + scalNbRows + crosNbRows - 1, 0, 2 * nbEdges - 1, cross);
    a.setMatrix(2 * nbEdges + scalNbRows + crosNbRows, 2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows - 1, 0,
        2 * nbEdges - 1, scalHr);
    a.setMatrix(2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows,
        2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows - 1, 0, 2 * nbEdges - 1, scalHr2);
    a.setMatrix(2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows,
        2 * nbEdges + scalNbRows + crosNbRows + scHr1NbRows + scHr2NbRows + alignNbRows - 1, 0, 2 * nbEdges - 1, align);

    return a;
  }

  // creates the submatrix corresponding to the partial derivatives of the
  // scalar product
  private Matrix getDotProductSubMatrix(List<int[]> indices) {
    Matrix m = new Matrix(0, 0);
    int nbEdges = points.size();
    if (indices.size() > 0) {
      m = new Matrix(indices.size(), 2 * nbEdges);
      for (int i = 0; i < m.getRowDimension(); ++i) {
        int[] pointsAround = indices.get(i);
        double df = points.get(pointsAround[2]).getX() - 2 * points.get(pointsAround[1]).getX()
            + points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[1] * 2, df);
        // df/yn = yn+1 - 2yn + yn-1
        df = points.get(pointsAround[2]).getY() - 2 * points.get(pointsAround[1]).getY()
            + points.get(pointsAround[0]).getY();
        m.set(i, pointsAround[1] * 2 + 1, df);
        // df/xn-1 = -xn+1 + xn
        df = points.get(pointsAround[1]).getX() - points.get(pointsAround[2]).getX();
        m.set(i, pointsAround[0] * 2, df);
        // df/yn-1 = -yn+1 + yn
        df = points.get(pointsAround[1]).getY() - points.get(pointsAround[2]).getY();
        m.set(i, pointsAround[0] * 2 + 1, df);
        // df/xn+1 = xn - xn-1
        df = points.get(pointsAround[1]).getX() - points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[2] * 2, df);
        // df/yn+1 = yn - yn-1
        df = points.get(pointsAround[1]).getY() - points.get(pointsAround[0]).getY();
        m.set(i, pointsAround[2] * 2 + 1, df);
      }
    }
    return m;
  }

  private Matrix getCrossProductSubMatrix() {
    int nbEdges = points.size();
    Matrix m = new Matrix(0, 0);
    if (indicesFlatAngles.size() > 0) {
      m = new Matrix(indicesFlatAngles.size(), 2 * nbEdges);
      for (int i = 0; i < m.getRowDimension(); ++i) {
        int[] pointsAround = indicesFlatAngles.get(i);
        // dg/xn
        double dg = points.get(pointsAround[2]).getY() - points.get(pointsAround[0]).getY();
        m.set(i, pointsAround[1] * 2, dg);
        // dg/yn
        dg = points.get(pointsAround[0]).getX() - points.get(pointsAround[2]).getX();
        m.set(i, pointsAround[1] * 2 + 1, dg);
        // dg/xn-1
        dg = points.get(pointsAround[1]).getY() - points.get(pointsAround[2]).getY();
        m.set(i, pointsAround[0] * 2, dg);
        // dg/yn-1
        dg = points.get(pointsAround[2]).getX() - points.get(pointsAround[1]).getX();
        m.set(i, pointsAround[0] * 2 + 1, dg);
        // dg/xn+1
        dg = points.get(pointsAround[0]).getY() - points.get(pointsAround[1]).getY();
        m.set(i, pointsAround[2] * 2, dg);
        // dg/yn+1
        dg = points.get(pointsAround[1]).getX() - points.get(pointsAround[0]).getX();
        m.set(i, pointsAround[2] * 2 + 1, dg);
      }
    }
    return m;
  }

  private Matrix getAlignSubMatrix() {
    int nbEdges = points.size();
    Matrix m = new Matrix(0, 0);
    if (indicesAligns.size() > 0) {
      m = new Matrix(indicesAligns.size(), 2 * nbEdges);
    }
    for (int i = 0; i < m.getRowDimension(); ++i) {
      int[] pointsAround = indicesAligns.get(i);
      double dh = points.get(pointsAround[2]).getY() - points.get(pointsAround[3]).getY();
      m.set(i, pointsAround[0] * 2, dh);
      // dg/yn
      dh = points.get(pointsAround[3]).getX() - points.get(pointsAround[2]).getX();
      m.set(i, pointsAround[0] * 2 + 1, dh);
      // dg/xn+1
      dh = points.get(pointsAround[3]).getY() - points.get(pointsAround[2]).getY();
      m.set(i, pointsAround[1] * 2, dh);
      // dg/yn+1
      dh = points.get(pointsAround[2]).getX() - points.get(pointsAround[3]).getX();
      m.set(i, pointsAround[1] * 2 + 1, dh);
      // dh/xm
      dh = points.get(pointsAround[1]).getY() - points.get(pointsAround[0]).getY();
      m.set(i, pointsAround[2] * 2, dh);
      // dg/ym
      dh = points.get(pointsAround[0]).getX() - points.get(pointsAround[1]).getX();
      m.set(i, pointsAround[2] * 2 + 1, dh);
      // dg/xm+1
      dh = points.get(pointsAround[0]).getY() - points.get(pointsAround[1]).getY();
      m.set(i, pointsAround[3] * 2, dh);
      // dg/ym+1
      dh = points.get(pointsAround[1]).getX() - points.get(pointsAround[0]).getX();
      m.set(i, pointsAround[3] * 2 + 1, dh);
    }
    return m;
  }

  // B = Y - S(Xcourant)
  public Matrix getB() {
    int nbEdges = points.size();
    int nbRights = indicesRightAngles.size();
    int nbFlats = indicesFlatAngles.size();
    int nbHr = indices45.size() + indices135.size();
    int nbAligns = indicesAligns.size();
    Matrix s = new Matrix(2 * nbEdges + nbRights + nbFlats + nbHr + nbAligns, 1);
    for (int i = 0; i < nbEdges; ++i) {
      s.set(2 * i, 0, points.get(i).getX());
      s.set((2 * i) + 1, 0, points.get(i).getY());
    }
    // F(i-1,i,i+1) (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < nbRights; ++i) {
      int[] pointsAround = indicesRightAngles.get(i);
      s.set(2 * nbEdges + i, 0, dotProduct(pointsAround[0], pointsAround[1], pointsAround[2]));
    }
    // G(i-1,i,i+1) (Xn-1 Xn)^(Xn Xn+1)
    for (int i = 0; i < nbFlats; ++i) {
      int[] pointsAround = indicesFlatAngles.get(i);
      s.set(2 * nbEdges + nbRights + i, 0, crossProduct(pointsAround[0], pointsAround[1], pointsAround[2]));
    }
    // angles pi/4 (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indices45.size(); ++i) {
      int[] pointsAround = indices45.get(i);
      s.set(2 * nbEdges + nbRights + nbFlats + i, 0, dotProduct(pointsAround[0], pointsAround[1], pointsAround[2]));
    }
    // angles 3pi/4 (Xn-1 Xn).(Xn Xn+1)
    for (int i = 0; i < indices135.size(); ++i) {
      int[] pointsAround = indices135.get(i);
      s.set(2 * nbEdges + nbRights + nbFlats + indices45.size() + i, 0,
          dotProduct(pointsAround[0], pointsAround[1], pointsAround[2]));
    }
    // parallelism between 2 polygons, cross product (the z)
    for (int i = 0; i < nbAligns; ++i) {
      int[] pointsAround = indicesAligns.get(i);
      s.set(2 * nbEdges + nbRights + nbFlats + nbHr + i, 0,
          crossProduct(pointsAround[0], pointsAround[1], pointsAround[2], pointsAround[3]));
    }
    return y.minus(s);
  }

  // dot product of ab and bc
  private double dotProduct(int a, int b, int c) {
    Vector2D v1 = new Vector2D(points.get(a), points.get(b));
    Vector2D v2 = new Vector2D(points.get(b), points.get(c));
    return v1.prodScalaire(v2);
  }

  // cross product of ab and bc
  private double crossProduct(int a, int b, int c) {
    Vector2D v1 = new Vector2D(points.get(a), points.get(b));
    Vector2D v2 = new Vector2D(points.get(b), points.get(c));
    return v1.prodVectoriel(v2).getZ();
  }

  // cross product of ab and bc
  private double crossProduct(int a, int b, int c, int d) {
    Vector2D v1 = new Vector2D(points.get(a), points.get(b));
    Vector2D v2 = new Vector2D(points.get(c), points.get(d));
    return v1.prodVectoriel(v2).getZ();
  }

  // compute Dx = (A'PA)^1.(A'PB)
  private Matrix computeDx() {
    Matrix a = getA();
    Matrix atp = a.transpose().times(p);
    Matrix dx = atp.times(a).solve(atp.times(getB()));
    return dx;
  }

  // update points with x
  private void setNewPoints(Matrix x) {
    for (int i = 0; i < x.getRowDimension(); i = i + 2)
      points.set(i / 2, new DirectPosition(x.get(i, 0), x.get(i + 1, 0)));
  }

  public List<IPolygon> square() {
    Matrix x = this.xCurrent;
    Matrix dx;
    int i = 0;
    // long begin = System.nanoTime();
    do {
      // System.out.println("iter " + i);
      this.xCurrent = x.copy();
      dx = computeDx();
      x = x.plus(dx);
      setNewPoints(x);
      ++i;
      // v = getB().minus(getA().times(x));
      // vtpvCurrent = v.transpose().times(this.p).times(v);
      // res = Math.abs((vtpvCurrent.minus(vtpv)).get(0, 0));
      // System.out.println("vtpvCurr " + vtpvCurrent.get(0, 0));
      // System.out.println("vtpv " + vtpv.get(0, 0));
      // vtpv = vtpvCurrent;
      if (logger.isDebugEnabled()) {
        logger.debug("iter nb " + i);
        logger.debug("variation of dx norm2 with last iter " + dx.normInf());
      }
    } while (i < MAX_ITER && dx.normInf() > NORM_DIFF_TOL);
    nbIters = i;
    if (logger.isDebugEnabled())
      System.out.println("nb iters : " + nbIters);
    return rebuildPolygons();
  }

  private List<IPolygon> rebuildPolygons() {
    List<IPolygon> newPolygs = new ArrayList<>();
    AbstractGeomFactory factory = AbstractGeometryEngine.getFactory();
    int offset = 0;
    for (IPolygon p : originals) {
      IDirectPositionList newP = new DirectPositionList();
      int j = 0;
      IDirectPositionList pointsoriginal = p.exteriorCoord();
      for (j = 0; j < pointsoriginal.size() - 1; ++j) {
        points.get(offset + j).setZ(pointsoriginal.get(j).getZ());
        newP.add(points.get(offset + j));
      }
      newP.add(points.get(offset));
      offset += p.numPoints() - 1;
      newPolygs.add(factory.createIPolygon(newP));
    }
    return newPolygs;
  }

  public static void main(String[] args) throws ParseException {
    SquareAlignPolygons.logger.setLevel(Level.OFF);
    String poly = "POLYGON((339810.23176300001796335 7642898.97540399990975857,339825.85329399997135624 7642891.98611100018024445,339825.24993900000117719 7642890.42239999957382679,339826.96127000002888963 7642888.81250800006091595,339824.48684899997897446 7642882.04569400008767843,339822.63982699997723103 7642882.6473599998280406,339821.41962499998044223 7642880.1152379997074604,339805.85743199998978525 7642887.29528400022536516,339810.23176300001796335 7642898.97540399990975857))";
    IPolygon pol0 = (IPolygon) WktGeOxygene.makeGeOxygene(poly);
    poly = "POLYGON((339822.98888800002168864 7642872.4665329996496439,339819.41837899998063222 7642862.75993799977004528,339814.07412300002761185 7642864.54277199972420931,339811.56089000002248213 7642857.55325200036168098,339813.0901999999769032 7642857.02936599962413311,339809.87812700000358745 7642848.08210800029337406,339799.22233900002902374 7642851.85121299978345633,339802.0865639999974519 7642860.31517800036817789,339804.42946900002425537 7642867.23689999990165234,339800.57982099999208003 7642868.71306999959051609,339804.0712000000057742 7642879.3496679998934269,339822.98888800002168864 7642872.4665329996496439))";
    IPolygon pol1 = (IPolygon) WktGeOxygene.makeGeOxygene(poly);
    poly = "POLYGON((339808.80053499998757616 7642846.70012800022959709,339805.79903900000499561 7642837.09975000005215406,339795.30125999997835606 7642840.40031499974429607,339798.6991660000057891 7642850.09980599954724312,339808.80053499998757616 7642846.70012800022959709))";
    IPolygon pol2 = (IPolygon) WktGeOxygene.makeGeOxygene(poly);
    poly = "POLYGON((339806.20036199997412041 7642835.6994030000641942,339803.19966899999417365 7642825.50090499967336655,339792.70006399997510016 7642828.89930600021034479,339795.7999040000140667 7642838.80038600042462349,339806.20036199997412041 7642835.6994030000641942))";
    IPolygon pol3 = (IPolygon) WktGeOxygene.makeGeOxygene(poly);
    poly = "POLYGON((339800.79983400000492111 7642824.90031300019472837,339797.40008699998725206 7642814.49945299979299307,339786.70016000000759959 7642817.80049700010567904,339790.09991799999261275 7642828.39973600022494793,339800.79983400000492111 7642824.90031300019472837))";
    IPolygon pol4 = (IPolygon) WktGeOxygene.makeGeOxygene(poly);

    List<IPolygon> l = new ArrayList<>();
    l.add(pol0);
    l.add(pol1);
    l.add(pol2);
    l.add(pol3);
    l.add(pol4);
    SquareAlignPolygons sq = new SquareAlignPolygons(10, 0.11, 7);
    SquarePolygonLS sqo = new SquarePolygonLS(10, 0.11, 7);
    sq.setPolygons(l);
    List<IPolygon> result = new ArrayList<>();
    result = sq.square();
    System.out.println("************************** with alignment constraint ");
    for (IPolygon p : result) {
      System.out.println(p);
    }
    result.clear();
    for (IPolygon p : l) {
      sqo.setPolygon(p);
      result.add(sqo.square());
    }
    System.out.println();
    System.out.println("************************** without alignment constraint ");
    for (IPolygon p : result) {
      System.out.println(p);
    }

  }
}
