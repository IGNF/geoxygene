package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

/**
 * Class to store and handle mathematical triangles
 * @author GTouya
 * 
 */
public class Circle {

  private double radius;
  private IDirectPosition center;

  /**
   * Builds a circle from its center and its radius.
   * @param radius
   * @param center
   */
  public Circle(double radius, IDirectPosition center) {
    super();
    this.radius = radius;
    this.center = center;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public IDirectPosition getCenter() {
    return center;
  }

  public void setCenter(IDirectPosition center) {
    this.center = center;
  }

  public double getDiameter() {
    return radius * 2;
  }

  /**
   * Builds a {@link IPolygon} object from the mathematical circle, given a
   * number of segments in the polygon.
   * @param nbSegments
   * @return
   */
  public IPolygon toPolygonGeometry(int nbSegments) {
    return GeometryFactory.buildCircle(getCenter(), getRadius(), nbSegments);
  }
}
