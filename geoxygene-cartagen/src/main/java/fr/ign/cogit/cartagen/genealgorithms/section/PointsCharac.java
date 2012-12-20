/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.genealgorithms.section;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * 
 * @author K. Jaara 18 Avril 2011, des portions de code sont écrites par J.
 *         Girras une classe pour extraire les points characterestiques
 *         PointsCharac=without the window notion for describing the segment on
 *         which the point is located
 * 
 */

public class PointsCharac {
  // private static Logger logger = Logger.getLogger(PointsCharac.class
  // .getName());

  /**
   * Linear section on which gaussian smoothing is performed
   */
  private ILineString geneObj;
  // DirectPositionList dplPtsInflexionVirages=new DirectPositionList();
  private DirectPositionList PtsCharac = new DirectPositionList();

  // static ArrayList<Integer> pointIndexsToKeep = new ArrayList<Integer>();

  // DirectPositionList jddPtsInflexionLsInitiale = new DirectPositionList();

  /**
   * 
   * Sigma
   * 
   */

  /**
   * Data resolution: defined in the generalisation parameters
   */

  /**
   * Global constructor
   * @param geoObj
   */

  public DirectPositionList getPtsCharac() {
    return this.PtsCharac;
  }

  /**
   * Default constructor with empiric parameters
   * @param geoObj
   */

  public PointsCharac(ILineString geneObj) {
    this.geneObj = geneObj;

  }

  /**
   * Computation of the gaussian smoothing on geoObj
   * @param isPersistantObject : determines if the geographic object is
   *          persistent in the database or just exists to apply the algorithm
   */

  // extraction of characterestic points using the the reduction of points by
  // DouglasPeucker
  public DirectPositionList compute(double sigma, double Tolerance) {

    this.PtsCharac.clear();
    IDirectPositionList points = this.geneObj.coord();
    this.geneObj = GaussianFilter.gaussianFilter(this.geneObj, sigma, 1);
    // LissageGaussian.AppliquerLissageGaussien((GM_LineString) this.geneObj,
    // sigma, 1, false);

    IDirectPositionList pointsLisses = this.geneObj.coord();

    this.PtsCharac = (DirectPositionList) PointsCharac.DouglasPeuckerReduction(
        points, pointsLisses, Tolerance);

    return this.PtsCharac;

  }

  public static DirectPositionList getPointsCharac(ILineString geneobj,
      @SuppressWarnings("unused") double sigma, double Tolerance) {

    IDirectPositionList points = geneobj.coord();

    // LissageGaussian.AppliquerLissageGaussien((GM_LineString) geneobj, sigma,
    // 1, false);

    // IDirectPositionList pointsLisses = geneobj.coord();

    return (DirectPositionList) PointsCharac.DouglasPeuckerReduction(points,
        points, Tolerance);

  }

  // / <summary>
  // / Uses the Douglas Peucker algorithm to reduce the number of points.
  // / </summary>
  // / <param name="Points">The points.</param>
  // / <param name="Tolerance">The tolerance.</param>
  // / <returns></returns>
  public static IDirectPositionList DouglasPeuckerReduction(
      @SuppressWarnings("unused") IDirectPositionList points,
      IDirectPositionList pointsLisses, double Tolerance) {
    if (pointsLisses == null || pointsLisses.size() < 3) {
      return pointsLisses;
    }

    int firstPoint = 0;
    int lastPoint = pointsLisses.size() - 1;

    // Add the first and last index to the keepers
    /*
     * pointIndexsToKeep.add(firstPoint); pointIndexsToKeep.add(lastPoint);
     */

    // The first and the last point cannot be the same
    while (pointsLisses.get(firstPoint).equals(pointsLisses.get(lastPoint))) {
      lastPoint--;
    }

    PointsCharac.DouglasPeuckerReduction(pointsLisses, firstPoint, lastPoint,
        Tolerance);

    DirectPositionList returnPoints = new DirectPositionList();
    // sort(pointIndexsToKeep);
    /*
     * for(int index=0;index<pointIndexsToKeep.size();index++)
     * 
     * { returnPoints.add(points.get(pointIndexsToKeep.get(index))); }
     */

    return returnPoints;
  }

  // / <summary>
  // / Douglases the peucker reduction.
  // / </summary>
  // / <param name="points">The points.</param>
  // / <param name="firstPoint">The first point.</param>
  // / <param name="lastPoint">The last point.</param>
  // / <param name="tolerance">The tolerance.</param>
  // / <param name="pointIndexsToKeep">The point index to keep.</param>
  private static void DouglasPeuckerReduction(IDirectPositionList points,
      int firstPoint, int lastPoint, double tolerance) {
    double maxDistance = 0;
    int indexFarthest = 0;

    for (int index = firstPoint; index < lastPoint; index++) {
      double distance = PointsCharac.PerpendicularDistance(
          points.get(firstPoint), points.get(lastPoint), points.get(index));
      if (distance > maxDistance) {
        maxDistance = distance;
        indexFarthest = index;
      }
    }

    if (maxDistance > tolerance && indexFarthest != 0) {
      // Add the largest point that exceeds the tolerance
      // pointIndexsToKeep.add(indexFarthest);

      PointsCharac.DouglasPeuckerReduction(points, firstPoint, indexFarthest,
          tolerance);
      PointsCharac.DouglasPeuckerReduction(points, indexFarthest, lastPoint,
          tolerance);
    }
  }

  // / <summary>
  // / The distance of a point from a line made from point1 and point2.
  // / </summary>
  // / <param name="pt1">The PT1.</param>
  // / <param name="pt2">The PT2.</param>
  // / <param name="p">The p.</param>
  // / <returns></returns>
  public static double PerpendicularDistance(IDirectPosition Point1,
      IDirectPosition Point2, IDirectPosition Point) {
    // Area = |(1/2)(x1y2 + x2y3 + x3y1 - x2y1 - x3y2 - x1y3)| *Area of triangle
    // Base = v((x1-x2)²+(x1-x2)) *Base of Triangle*
    // Area = .5*Base*H *Solve for height
    // Height = Area/.5/Base

    double area = Math.abs(.5 * (Point1.getX() * Point2.getY() + Point2.getX()
        * Point.getY() + Point.getX() * Point1.getY() - Point2.getX()
        * Point1.getY() - Point.getX() * Point2.getY() - Point1.getX()
        * Point.getY()));
    double bottom = Math.sqrt(Math.pow(Point1.getX() - Point2.getX(), 2)
        + Math.pow(Point1.getY() - Point2.getY(), 2));
    double height = area / bottom * 2;

    return height;

  }

  /*
   * public static void main(String[] args) {
   * 
   * DirectPosition p1,p2,p3=new DirectPosition(); p1=new DirectPosition(-10,0);
   * p2=new DirectPosition(-10,10); p3=new DirectPosition(15,15);
   * 
   * 
   * System.out.println(PerpendicularDistance(p1,p2,p3));
   * 
   * }
   */

  static int[] sortedNumbers;

  @SuppressWarnings("null")
  public static void sort(ArrayList<Integer> values) {
    // Check for empty or null array

    PointsCharac.sortedNumbers = new int[values.size()];
    if (values == null || values.size() == 0) {
      return;
    }
    for (int i = 0; i < values.size(); i++) {
      PointsCharac.sortedNumbers[i] = values.get(i);
    }
    int n = values.size();
    PointsCharac.quicksort(0, n - 1);
  }

  private static void quicksort(int low, int high) {

    // @Lars Vogel

    int i = low, j = high;
    // Get the pivot element from the middle of the list
    int pivot = PointsCharac.sortedNumbers[low + (high - low) / 2];

    // Divide into two lists
    while (i <= j) {
      // If the current value from the left list is smaller then the pivot
      // element then get the next element from the left list
      while (PointsCharac.sortedNumbers[i] < pivot) {
        i++;
      }
      // If the current value from the right list is larger then the pivot
      // element then get the next element from the right list
      while (PointsCharac.sortedNumbers[j] > pivot) {
        j--;
      }

      // If we have found a values in the left list which is larger then
      // the pivot element and if we have found a value in the right list
      // which is smaller then the pivot element then we exchange the
      // values.
      // As we are done we can increase i and j
      if (i <= j) {
        PointsCharac.exchange(i, j);
        i++;
        j--;
      }
    }
    // Recursion
    if (low < j) {
      PointsCharac.quicksort(low, j);
    }
    if (i < high) {
      PointsCharac.quicksort(i, high);
    }
  }

  private static void exchange(int i, int j) {
    int temp = PointsCharac.sortedNumbers[i];
    PointsCharac.sortedNumbers[i] = PointsCharac.sortedNumbers[j];
    PointsCharac.sortedNumbers[j] = temp;
  }

  public void clear() {
    this.geneObj = null;
    this.PtsCharac.clear();
    // pointIndexsToKeep.cleafr.ign.cogit.cartagen.geneAlgorithms.sectionr();

  }
}
