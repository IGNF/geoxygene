/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.ITriangleFace;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class TriangleFacesMeasures {

  /**
   * The mean X value of a triangle
   * @param tri
   * @return
   */
  public static double getX(ITriangleFace tri) {
    return (tri.getNode1().getPosition().getX()
        + tri.getNode2().getPosition().getX() + tri.getNode3().getPosition()
        .getX()) / 3.0;
  }

  /**
   * The mean Y value of a triangle
   * @param tri
   * @return
   */
  public static double getY(ITriangleFace tri) {
    return (tri.getNode1().getPosition().getY()
        + tri.getNode2().getPosition().getY() + tri.getNode3().getPosition()
        .getY()) / 3.0;
  }

  /**
   * @param pos
   * @return True if the input position is in the triangle, false otherwise
   */
  public static boolean contains(ITriangleFace tri, IDirectPosition pos) {
    if ((tri.getNode1().getPosition().getX() - pos.getX())
        * (tri.getNode2().getPosition().getY() - pos.getY())
        - (tri.getNode1().getPosition().getY() - pos.getY())
        * (tri.getNode2().getPosition().getX() - pos.getX()) < 0) {
      return false;
    } else if ((tri.getNode2().getPosition().getX() - pos.getX())
        * (tri.getNode3().getPosition().getY() - pos.getY())
        - (tri.getNode2().getPosition().getY() - pos.getY())
        * (tri.getNode3().getPosition().getX() - pos.getX()) < 0) {
      return false;
    } else if ((tri.getNode3().getPosition().getX() - pos.getX())
        * (tri.getNode1().getPosition().getY() - pos.getY())
        - (tri.getNode3().getPosition().getY() - pos.getY())
        * (tri.getNode1().getPosition().getX() - pos.getX()) < 0) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * @param pos
   * @return True if the input position is in the triangle in its initial state,
   *         false otherwise
   */
  public static boolean containsInitial(ITriangleFace tri, IDirectPosition pos) {
    if ((tri.getNode1().getPositionIni().getX() - pos.getX())
        * (tri.getNode2().getPositionIni().getY() - pos.getY())
        - (tri.getNode1().getPositionIni().getY() - pos.getY())
        * (tri.getNode2().getPositionIni().getX() - pos.getX()) < 0) {
      return false;
    } else if ((tri.getNode2().getPositionIni().getX() - pos.getX())
        * (tri.getNode3().getPositionIni().getY() - pos.getY())
        - (tri.getNode2().getPositionIni().getY() - pos.getY())
        * (tri.getNode3().getPositionIni().getY() - pos.getX()) < 0) {
      return false;
    } else if ((tri.getNode3().getPositionIni().getX() - pos.getX())
        * (tri.getNode1().getPositionIni().getY() - pos.getY())
        - (tri.getNode3().getPositionIni().getY() - pos.getY())
        * (tri.getNode1().getPositionIni().getX() - pos.getX()) < 0) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * @return The triangle area
   */
  public static double getArea(ITriangleFace tri) {
    double s = TriangleFacesMeasures.istReverted(tri) ? -1.0 : 1.0;
    return s
        * Math.abs(tri.getNode2().getPosition().getX()
            * tri.getNode1().getPosition().getY()
            - tri.getNode1().getPosition().getX()
            * tri.getNode2().getPosition().getY()
            + tri.getNode3().getPosition().getX()
            * tri.getNode2().getPosition().getY()
            - tri.getNode2().getPosition().getX()
            * tri.getNode3().getPosition().getY()
            + tri.getNode1().getPosition().getX()
            * tri.getNode3().getPosition().getY()
            - tri.getNode3().getPosition().getX()
            * tri.getNode1().getPosition().getY()) * 0.5;
  }

  /**
   * @return The triangle area in its initial state
   */
  public static double getInitialArea(ITriangleFace tri) {
    return Math.abs((tri.getNode2().getPositionIni().getX()
        * tri.getNode1().getPositionIni().getY()
        - tri.getNode1().getPositionIni().getX()
        * tri.getNode2().getPositionIni().getY()
        + tri.getNode3().getPositionIni().getX()
        * tri.getNode2().getPositionIni().getY()
        - tri.getNode2().getPositionIni().getX()
        * tri.getNode3().getPositionIni().getY()
        + tri.getNode1().getPositionIni().getX()
        * tri.getNode3().getPositionIni().getY() - tri.getNode3()
        .getPositionIni().getX()
        * tri.getNode1().getPositionIni().getY()) * 0.5);
  }

  /**
   * @return The triangle direction: "d" for direct, "i" for indirect, "n" if
   *         neither. (NB: in its initial state, the 3 points are ordered to be
   *         direct)
   */
  public static String getDirection(ITriangleFace tri) {
    double pv = (tri.getNode2().getPosition().getX() - tri.getNode1()
        .getPosition().getX())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        - (tri.getNode2().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX());
    if (pv > 0) {
      return "d";
    } else if (pv < 0) {
      return "i";
    } else {
      return "n";
    }
  }

  /**
   * @return Return if the triangle is reverted or not. (NB: in its initial
   *         state, the triangle is not reverted)
   */
  public static boolean istReverted(ITriangleFace tri) {
    return !TriangleFacesMeasures.getDirection(tri).equals("d");
  }

  /**
   * Return the Z value of the plan defined by the triangle
   * 
   * @param pos
   * @return
   */
  public static double getZ(ITriangleFace tri, IDirectPosition pos) {
    double ux = (tri.getNode2().getPosition().getY() - tri.getNode1()
        .getPosition().getY())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        - (tri.getNode2().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY());
    double uy = (tri.getNode2().getPosition().getZ() - tri.getNode1()
        .getPosition().getZ())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        - (tri.getNode2().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ());
    double uz = (tri.getNode2().getPosition().getX() - tri.getNode1()
        .getPosition().getX())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        - (tri.getNode2().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX());
    return tri.getNode1().getPosition().getZ()
        + (ux * (tri.getNode1().getPosition().getX() - pos.getX()) + uy
            * (tri.getNode1().getPosition().getY() - pos.getY())) / uz;
  }

  /**
   * Return the Z value of the plan defined by the triangle in its initial state
   * 
   * @param pos
   * @return
   */
  public static double getZInitial(ITriangleFace tri, IDirectPosition pos) {
    double ux = (tri.getNode2().getPosition().getY() - tri.getNode1()
        .getPosition().getY())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        - (tri.getNode2().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY());
    double uy = (tri.getNode2().getPosition().getZ() - tri.getNode1()
        .getPosition().getZ())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        - (tri.getNode2().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ());
    double uz = (tri.getNode2().getPosition().getX() - tri.getNode1()
        .getPosition().getX())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        - (tri.getNode2().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX());
    return tri.getNode1().getPosition().getZ()
        + (ux * (tri.getNode1().getPosition().getX() - pos.getX()) + uy
            * (tri.getNode1().getPosition().getY() - pos.getY())) / uz;
  }

  /**
   * @return The angle between the horizontal plan and the triangle, between 0
   *         and Pi, in radians. It is Pi/2 - zenital angle, or too the angle
   *         between the normal vector to the triangle and the vertical oriented
   *         up. 0 means the triangle is horizontal, Pi/2 it is vertical.
   *         Between Pi/2 and Pi, the triangle is reversed.
   */
  public static double getSlopeAngle(ITriangleFace tri) {
    double dx = (tri.getNode2().getPosition().getY() - tri.getNode1()
        .getPosition().getY())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        - (tri.getNode2().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY());
    double dy = (tri.getNode2().getPosition().getZ() - tri.getNode1()
        .getPosition().getZ())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        - (tri.getNode2().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ());
    double dz = (tri.getNode2().getPosition().getX() - tri.getNode1()
        .getPosition().getX())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        - (tri.getNode2().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX());
    return Math.abs(Math.atan2(Math.sqrt(dx * dx + dy * dy), dz));
  }

  /**
   * @return The normed vector product of the triangle. That is the vector
   *         normal to the oriented triangle.
   */
  public static double[] getSlopeVector(ITriangleFace tri) {
    // compute the vector product
    double[] slopeVector = new double[3];
    slopeVector[0] = (tri.getNode2().getPosition().getY() - tri.getNode1()
        .getPosition().getY())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        - (tri.getNode2().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY());
    slopeVector[1] = (tri.getNode2().getPosition().getZ() - tri.getNode1()
        .getPosition().getZ())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        - (tri.getNode2().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ());
    slopeVector[2] = (tri.getNode2().getPosition().getX() - tri.getNode1()
        .getPosition().getX())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        - (tri.getNode2().getPosition().getY() - tri.getNode1().getPosition()
            .getY())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX());

    // compute the norm
    double norm = Math.sqrt(slopeVector[0] * slopeVector[0] + slopeVector[1]
        * slopeVector[1] + slopeVector[2] * slopeVector[2]);

    // make the vector normed
    slopeVector[0] /= norm;
    slopeVector[1] /= norm;
    slopeVector[2] /= norm;

    return slopeVector;
  }

  /**
   * @return The azimutal orientation of the slope vector, between -Pi and Pi,
   *         in radian, from the (O,x) axis. Returns -999.9 if the slope is not
   *         defined (horizontal triangle)
   */
  public static double getSlopeAzimutalOrientation(ITriangleFace tri) {
    double dx = (tri.getNode2().getPosition().getY() - tri.getNode1()
        .getPosition().getY())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        - (tri.getNode2().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ())
        * (tri.getNode3().getPosition().getY() - tri.getNode1().getPosition()
            .getY());
    double dy = (tri.getNode2().getPosition().getZ() - tri.getNode1()
        .getPosition().getZ())
        * (tri.getNode3().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        - (tri.getNode2().getPosition().getX() - tri.getNode1().getPosition()
            .getX())
        * (tri.getNode3().getPosition().getZ() - tri.getNode1().getPosition()
            .getZ());
    if (dx == 0.0 && dy == 0.0) {
      return -999.9;
    }
    return Math.atan2(dy, dx);
  }

  /**
   * @return The azimutal orientation of the slope vector in the triangle
   *         initial state, between -Pi and Pi, in radian, from the (O,x) axis.
   *         Returns -999.9 if the slope is not defined (horizontal triangle)
   */
  public static double getInitialSlopeAzimutalOrientation(ITriangleFace tri) {
    double dx = (tri.getNode2().getPositionIni().getY() - tri.getNode1()
        .getPositionIni().getY())
        * (tri.getNode3().getPositionIni().getZ() - tri.getNode1()
            .getPositionIni().getZ())
        - (tri.getNode2().getPositionIni().getZ() - tri.getNode1()
            .getPositionIni().getZ())
        * (tri.getNode3().getPositionIni().getY() - tri.getNode1()
            .getPositionIni().getY());
    double dy = (tri.getNode2().getPositionIni().getZ() - tri.getNode1()
        .getPositionIni().getZ())
        * (tri.getNode3().getPositionIni().getX() - tri.getNode1()
            .getPositionIni().getX())
        - (tri.getNode2().getPositionIni().getX() - tri.getNode1()
            .getPositionIni().getX())
        * (tri.getNode3().getPositionIni().getZ() - tri.getNode1()
            .getPositionIni().getZ());
    if (dx == 0.0 && dy == 0.0) {
      return -999.9;
    }
    return Math.atan2(dy, dx);
  }

  /**
   * @param orientation
   * @return The difference between -Pi and Pi, in radian, between the slope
   *         azimutal orientation and a given one. Returns -999.9 if the slope
   *         is not defined (horizontal triangle)
   */
  public static double getSlopeAzimutalOrientationDifference(ITriangleFace tri,
      double orientation) {
    if (orientation == -999.9) {
      return -999.9;
    }

    // get the orientation
    double or = TriangleFacesMeasures.getSlopeAzimutalOrientation(tri);
    if (or == -999.9) {
      return -999.9;
    }

    // compute the difference
    double diff = or - orientation;

    // guarantee the value is between -Pi and Pi
    if (diff < -Math.PI) {
      return diff + 2.0 * Math.PI;
    } else if (diff > Math.PI) {
      return diff - 2.0 * Math.PI;
    } else {
      return diff;
    }
  }

  /**
   * @return The difference between -Pi and Pi, in radian, between the slope
   *         azimutal orientation and the one in its initial state. Returns
   *         -999.9 if the slope is not defined (horizontal triangle)
   */
  public static double getSlopeAzimutalOrientationDifference(ITriangleFace tri) {
    return TriangleFacesMeasures.getSlopeAzimutalOrientationDifference(tri,
        TriangleFacesMeasures.getInitialSlopeAzimutalOrientation(tri));
  }

  /**
   * @return True if a building is on the triangle, else false
   */
  public static boolean contientBatiment(ITriangleFace tri) {
    for (IBuilding bat : CartAGenDoc.getInstance().getCurrentDataset()
        .getBuildings()) {
      if (bat.getGeom() == null || bat.getGeom().isEmpty()) {
        return false;
      }
      if (TriangleFacesMeasures.contains(tri, bat.getGeom().centroid())) {
        return true;
      }
    }
    return false;
  }

}
