package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.style.Layer;

public class DoublePierceLens extends AbstractLens {

  public static double FOCUS_RADIUS = 50.0;
  public static String FOCUS_LAYER_NAME = "";

  public static double TRANSITION_RADIUS = 100.0;
  public static String TRANSITION_LAYER_NAME = "";

  private GeOxygeneApplication appli;

  public DoublePierceLens(GeOxygeneApplication application, double focusRadius,
      double transitionRadius) {
    super();
    this.appli = application;
    this.setFocusRegion(new FishEyeLensZoneShape(1, 1, focusRadius));
    this.setTransitionRegion(new FishEyeLensZoneShape(1, 1, transitionRadius));

  }

  public DoublePierceLens(GeOxygeneApplication application) {
    super();
    this.appli = application;
    this.setFocusRegion(new FishEyeLensZoneShape(1, 1, FOCUS_RADIUS));
    this.setTransitionRegion(new FishEyeLensZoneShape(1, 1, TRANSITION_RADIUS));
  }

  @Override
  protected void changeShapes(int x, int y) {
    this.getFocusRegion().setCenter(x, y);
    this.getTransitionRegion().setCenter(x, y);
  }

  @Override
  public void apply(Graphics2D g2d, BufferedImage offscreen) {

    Point point;
    point = MouseInfo.getPointerInfo().getLocation();
    SwingUtilities.convertPointFromScreen(point, getVisuPanel());
    if (point.x < 0 || point.x >= offscreen.getWidth() || point.y < 0
        || point.y >= offscreen.getHeight()) {
      return;
    }
    this.changeShapes(point.x, point.y);
    // Get the region to modify
    Rectangle rectangle;
    rectangle = getTransitionRegion().getBounds();
    if (rectangle.x < 0) {
      rectangle.x = 0;
    }
    if (rectangle.x + rectangle.width >= offscreen.getWidth()) {
      rectangle.width = offscreen.getWidth() - rectangle.x;
    }
    if (rectangle.y < 0) {
      rectangle.y = 0;
    }
    if (rectangle.y + rectangle.height >= offscreen.getHeight()) {
      rectangle.height = offscreen.getHeight() - rectangle.y;
    }

    BufferedImage image;
    // logger.debug("Get image at " + rectangle);
    // logger.debug("Get offscreen at " + offscreen);
    image = offscreen.getSubimage(rectangle.x, rectangle.y, rectangle.width,
        rectangle.height);
    BufferedImage imageToDraw = new BufferedImage(image.getWidth(),
        image.getHeight(), image.getType());

    // get the layer from layer name
    Layer focuslayer = appli.getMainFrame().getSelectedProjectFrame()
        .getLayer(FOCUS_LAYER_NAME);
    GridCoverage2D focuscoverage = null;
    if (focuslayer != null) {
      IFeature feat = focuslayer.getFeatureCollection().iterator().next();
      if (feat instanceof FT_Coverage)
        focuscoverage = ((FT_Coverage) feat).coverage();
    }

    Layer transitionlayer = appli.getMainFrame().getSelectedProjectFrame()
        .getLayer(TRANSITION_LAYER_NAME);
    GridCoverage2D transitioncoverage = null;
    if (transitionlayer != null) {
      IFeature feat = transitionlayer.getFeatureCollection().iterator().next();
      if (feat instanceof FT_Coverage)
        transitioncoverage = ((FT_Coverage) feat).coverage();
    }

    // create a new picture for the focus zone
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (focuscoverage == null && transitioncoverage == null) {
          imageToDraw.setRGB(x, y, image.getRGB(x, y));
          continue;
        }

        if (getFocusRegion().contains(x + rectangle.x, y + rectangle.y)) {
          // Apply modification on the focus region
          int[] colorCoords = new int[4];
          IDirectPosition pointCoord;
          try {
            pointCoord = appli
                .getMainFrame()
                .getSelectedProjectFrame()
                .getLayerViewPanel()
                .getViewport()
                .toModelDirectPosition(
                    new Point2D.Double(x + rectangle.x, y + rectangle.y));

            DirectPosition pointGeotools = new DirectPosition2D(
                pointCoord.getX(), pointCoord.getY());
            focuscoverage.evaluate(pointGeotools, colorCoords);
            imageToDraw.setRGB(x, y, new Color(colorCoords[0], colorCoords[1],
                colorCoords[2]).getRGB());
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          } catch (PointOutsideCoverageException e) {
            imageToDraw.setRGB(x, y, image.getRGB(x, y));
          }
        } else if (getTransitionRegion().contains(x + rectangle.x,
            y + rectangle.y)) {
          // Apply modification on the focus region
          int[] colorCoords = new int[4];
          IDirectPosition pointCoord;
          try {
            pointCoord = appli
                .getMainFrame()
                .getSelectedProjectFrame()
                .getLayerViewPanel()
                .getViewport()
                .toModelDirectPosition(
                    new Point2D.Double(x + rectangle.x, y + rectangle.y));

            DirectPosition pointGeotools = new DirectPosition2D(
                pointCoord.getX(), pointCoord.getY());
            transitioncoverage.evaluate(pointGeotools, colorCoords);
            imageToDraw.setRGB(x, y, new Color(colorCoords[0], colorCoords[1],
                colorCoords[2]).getRGB());
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          } catch (PointOutsideCoverageException e) {
            imageToDraw.setRGB(x, y, image.getRGB(x, y));
          }
        } else {
          imageToDraw.setRGB(x, y, image.getRGB(x, y));
        }
      }
    }
    g2d.drawImage(imageToDraw, rectangle.x, rectangle.y, getVisuPanel());

  }
}
