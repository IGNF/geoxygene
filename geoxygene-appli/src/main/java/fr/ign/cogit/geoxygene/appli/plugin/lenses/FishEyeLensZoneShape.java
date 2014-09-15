package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class FishEyeLensZoneShape extends LensZoneShape {

  private Ellipse2D circle;

  private double x;

  private double y;

  private double radius;

  public FishEyeLensZoneShape(double x, double y, double radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.circle = new Ellipse2D.Double(x - radius, y - radius, 2 * radius,
        2 * radius);

  }

  @Override
  public void setCenter(double x, double y) {
    this.x = x;
    this.y = y;
    this.circle.setFrame(x - this.radius, y - this.radius, 2 * this.radius,
        2 * this.radius);
  }

  @Override
  public Rectangle getBounds() {
    return this.circle.getBounds();
  }

  @Override
  public boolean contains(double x, double y) {
    return this.circle.contains(x, y);
  }

  @Override
  public int distance(double x, double y) {
    double dist = Math.sqrt((this.y - y) * (this.y - y) + (this.x - x)
        * (this.x - x));

    return (int) Math.abs(radius - dist);
  }
}
