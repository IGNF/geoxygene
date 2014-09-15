package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class JellyLensZoneShape extends LensZoneShape {

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(JellyLensZoneShape.class
      .getName());

  private double area;

  private IGeometry geometry;

  private double isovalue;

  private double x;

  private double y;

  private JComponent visuPanel;

  public JellyLensZoneShape(double x, double y, double area, IGeometry geometry) {
    this.x = x;
    this.y = y;
    this.setArea(area);
    this.geometry = geometry;
    this.isovalue = area;

  }

  public void setVisuPanel(JComponent visuPanel) {
    this.visuPanel = visuPanel;
  }

  private double distanceToOOI(double x, double y) {

    Point2D newPosition = null;
    try {
      newPosition = ((LayerViewPanel) this.visuPanel).getViewport()
          .toViewPoint(new DirectPosition(x, y));
    } catch (NoninvertibleTransformException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    double convX = newPosition.getX();
    double convY = newPosition.getY();
    // logger.debug("(x, y) : " + convX + ", " + convY);
    // logger.debug("Geometry " + this.geometry);

    return this.geometry
        .distance(new GM_Point(new DirectPosition(convX, convY)));
  }

  private double implicitFunction(double x, double y) {
    @SuppressWarnings("unused")
    double dist = Math.sqrt((this.y - y) * (this.y - y) + (this.x - x)
        * (this.x - x));
    //

    // }
    double r = this.distanceToOOI(x, y) / (750 * 2.0);
    // logger.debug("r : " + r);
    // logger.debug("beta : " + 750 * this.visuPanel.getPixelSize());
    double g = r < 1 ? 2 * Math.pow(r, 3) - 3 * r * r + 1 : 0;
    // logger.debug("g " + g);

    if ((Math.abs(this.y - y) < 15) && (Math.abs(this.x - x) < 15)) {
      // logger.debug("geometry " + geometry);
      // logger.debug("(x, y) : " + x + ", " + y + " lens = " + (1 / dist -
      // dist));
      // logger.debug("g " + g);
    }

    // return 1 / dist - dist + g * 50;
    return g;
  }

  @Override
  public int distance(double x, double y) {
    // logger.debug("distance");
    // logger.debug("distance : " + implicitFunction(x, y));
    return (int) (10000 * Math.abs(this.isovalue - implicitFunction(x, y)));
  }

  @Override
  public void setCenter(double x, double y) {
    // TODO Auto-generated method stub

  }

  @Override
  public Rectangle getBounds() {

    return new Rectangle((int) (this.x - 300), (int) (this.y - 300), 600, 600);

    //
    //
    // logger.debug("getBounds");
    // boolean control = false;
    // int minX = (int) this.x;
    // int minY = (int) this.y;
    // logger.debug("isovalue " + this.isovalue);
    // int step = 100;
    //
    // for (int x = (int) this.x; x > Integer.MIN_VALUE; x -= step) {
    // for (int y = (int) this.y; y > Integer.MIN_VALUE; y -= step) {
    // control = true;
    // logger.debug("x = " + x + " y = " + y);
    // logger.debug("implicit " + this.implicitFunction(x, y));
    // if (this.implicitFunction(x, y) > this.isovalue) {
    // control = false;
    // break;
    // }
    // }
    // if (control) {
    // minX = x;
    // break;
    // }
    // }
    //
    // for (int y = (int) this.y; y > Integer.MIN_VALUE; y -= step) {
    // for (int x = (int) this.x; x > Integer.MIN_VALUE; x -= step) {
    // control = true;
    //
    // logger.debug("implicit " + this.implicitFunction(x, y));
    // if (this.implicitFunction(x, y) > this.isovalue) {
    // control = false;
    // break;
    // }
    // }
    // if (control) {
    // minY = y;
    // break;
    // }
    // }
    //
    // logger.debug("exit for");
    //
    // control = false;
    // int maxX = (int) this.x;
    // int maxY = (int) this.y;
    //
    // for (int x = (int) this.x; x < Integer.MAX_VALUE; x += step) {
    // for (int y = (int) this.y; y < Integer.MAX_VALUE; y += step) {
    // control = true;
    // if (this.implicitFunction(x, y) > this.isovalue) {
    // control = false;
    // break;
    // }
    // }
    // if (control) {
    // maxX = x;
    // break;
    // }
    // }
    //
    // for (int y = (int) this.y; y < Integer.MAX_VALUE; y += step) {
    // for (int x = (int) this.x; x < Integer.MAX_VALUE; x += step) {
    // control = true;
    // if (this.implicitFunction(x, y) > this.isovalue) {
    // control = false;
    // break;
    // }
    // }
    // if (control) {
    // maxY = y;
    // break;
    // }
    // }

    // return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  @Override
  public boolean contains(double x, double y) {
    // logger.debug("contains");
    return this.implicitFunction(x, y) > this.isovalue;
  }

  public double getArea() {
    return area;
  }

  public void setArea(double area) {
    this.area = area;
  }

}
