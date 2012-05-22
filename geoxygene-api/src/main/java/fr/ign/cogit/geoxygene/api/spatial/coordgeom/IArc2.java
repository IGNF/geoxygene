package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IArc2 extends IArcString {
  public enum ArcDirection {
      DIRECT, INDIRECT, NONE, UNKNOWN
  }

  public abstract IDirectPosition getCenter();

  public abstract void setCenter(IDirectPosition center);

  public abstract double getRadius();

  public abstract void setRadius(double radius);

  public abstract double getStartOfArc();

  public abstract void setStartOfArc(double startOfArc);

  public abstract double getEndOfArc();

  public abstract void setEndOfArc(double endOfArc);

  public abstract ArcDirection getDirection();

  public abstract void setEndOfArc(ArcDirection direction);

  /**
   * @return the angle, in radian, within ]-Pi, Pi], between the [O, x] axis and
   *         the line [O, midpoint]
   * 
   */
  public abstract double getMidOfArc();

  /**
   * @return the angle value of the arc, within ]-2Pi, 2Pi]
   */
  public abstract double getDelta();

  public abstract IPosition getStartPoint();

  public abstract IPosition getMidPoint();

  public abstract IPosition getEndPoint();

  @Override
  public abstract IGeometry intersection(IGeometry geom);

  @Override
  public abstract boolean contains(IGeometry geom);

  public abstract boolean contains2(IDirectPosition pt);

}
