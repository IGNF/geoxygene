package fr.ign.cogit.geoxygene.contrib.conflation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Point;

import Jama.Matrix;

public class PolynomialTransformation {
  private Matrix transform;

  /**
   * @return the transformation matrix.
   */
  public Matrix getTranform() {
    return this.transform;
  }

  private int numberOfVectors;
  private int degree;
  private int np;

  /**
   * @param p1
   *        ground
   * @param p2
   *        image
   * @param degree
   *        degree of the polynomial
   */
  public PolynomialTransformation(Point[] p1, Point[] p2, int degree) {
    // TODO Check that n * 2 >= np
    assert (p1.length == p2.length);
    this.numberOfVectors = p1.length;
    this.degree = degree;
    this.np = (degree + 1) * (degree + 2);
    System.out.println("np = " + np);
    double[][] values = new double[2 * numberOfVectors][np];
    for (int i = 0; i < numberOfVectors; i++) {
      System.out.println("i = " + i);
      int index = 0;
      for (int j = 0; j <= degree; j++) {
        for (int k = 0; k <= degree; k++) {
          if (j + k <= degree) {
            System.out.println("j,k = " + j + "," + k);
            System.out.println("indices = " + (2 * i) + ", " + (j * (degree + 1) + k));
            System.out.println("indices = " + (2 * i + 1) + ", " + (np / 2 + j * (degree + 1) + k));
            values[2 * i + 1][np / 2 + index] = values[2 * i][index] = 1
                * Math.pow(p2[i].getX(), j) * Math.pow(p2[i].getY(), k);
            index++;
          }
        }
      }
    }
    Matrix matrixA = new Matrix(values);
    // for (int i = 0; i < matrixA.getRowDimension(); i++) {
    // String row = "";
    // for (int j = 0; j < matrixA.getColumnDimension(); j++) {
    // row += matrixA.get(i, j) + " ";
    // }
    // System.out.println(row);
    // }
    double[][] vals = new double[2 * numberOfVectors][1];
    for (int j = 0; j < numberOfVectors; j++) {
      vals[j * 2][0] = p1[j].getX();
      vals[j * 2 + 1][0] = p1[j].getY();
    }
    Matrix matrixB = new Matrix(vals);
    this.transform = matrixA.solve(matrixB);
    // Matrix Residual = matrixA.times(this.tranform).minus(matrixB);

  }

  /**
   * Apply the transformation to a point.
   * @param p
   *        a point
   * @return a new point after the application of the transformation
   */
  public Point transform(Point p) {
    return p.getFactory().createPoint(this.transform(p.getCoordinate()));
  }

  /**
   * Apply the transformation to a coordinate.
   * @param c
   *        a coordinate
   * @return the position of the coordinate after the application of the transformation
   */
  public Coordinate transform(Coordinate c) {
    double x = 0;
    double y = 0;
    int index = 0;
    for (int j = 0; j <= degree; j++) {
      for (int k = 0; k <= degree; k++) {
        if (j + k <= degree) {
          x += this.transform.get(index, 0) * Math.pow(c.x, j) * Math.pow(c.y, k);
          y += this.transform.get(np / 2 + index, 0) * Math.pow(c.x, j) * Math.pow(c.y, k);
          index++;
        }
      }
    }
    return new Coordinate(x, y);
  }

  /**
   * @return a coordinate filter that applies the transformation to all coordinates it is given.
   */
  public CoordinateFilter getCoordinateFilter() {
    return new CoordinateFilter() {
      @Override
      public void filter(Coordinate coord) {
        coord.setCoordinate(PolynomialTransformation.this.transform(coord));
      }
    };
  }
}
