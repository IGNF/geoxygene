package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public abstract class AbstractLens implements ILens {

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(AbstractLens.class.getName());

  private ILensZoneShape focusRegion;

  private ILensZoneShape transitionRegion;

  private int magni = 2;

  private JComponent visuPanel;

  @Override
  public void setVisuPanel(JComponent visuPanel) {
    this.visuPanel = visuPanel;
  }

  @Override
  public JComponent getVisuPanel() {
    return this.visuPanel;
  }

  @Override
  public void apply(Graphics2D g2d, BufferedImage offscreen) {

    Point point;
    point = MouseInfo.getPointerInfo().getLocation();
    SwingUtilities.convertPointFromScreen(point, visuPanel);
    if (point.x < 0 || point.x >= offscreen.getWidth() || point.y < 0
        || point.y >= offscreen.getHeight()) {
      return;
    }
    this.changeShapes(point.x, point.y);
    // Get the region to modify
    Rectangle rectangle;
    rectangle = transitionRegion.getBounds();
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

    // create a new picture for the focus zone
    // Graphics offgc;
    // BufferedImage scaled = null;
    // scaled = new BufferedImage(rectangle.width, rectangle.height,
    // BufferedImage.TYPE_INT_RGB);
    // offgc = scaled.getGraphics();

    // double pixelSizeSave = this.getVisuPanel().getPixelSize();
    // IDirectPosition geocenterSave = this.getVisuPanel().getGeoCenter();
    // this.getVisuPanel().setGeoCenter(
    // new DirectPosition(this.getVisuPanel().pixToCoordX(point.x), this
    // .getVisuPanel().pixToCoordY(point.y)));
    // this.getVisuPanel().setPixelSize(pixelSizeSave * 4);

    // this.getVisuPanel().paintGraphics(offgc);

    // this.getVisuPanel().setPixelSize(pixelSizeSave);
    // this.getVisuPanel().setGeoCenter(geocenterSave);

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (focusRegion.contains(x + rectangle.x, y + rectangle.y)) {
          // Apply modification on the focus region
          // logger.debug("Point " + x + ", " + y + " in Focus Region");
          imageToDraw.setRGB(
              x,
              y,
              image.getRGB(rectangle.width / 2 + (x - rectangle.width / 2)
                  / this.magni, rectangle.height / 2
                  + (y - rectangle.height / 2) / this.magni));
        } else if (transitionRegion.contains(x + rectangle.x, y + rectangle.y)) {
          // logger.debug("Point " + x + ", " + y + " in Transition Region");
          // logger.debug("X= " + x);
          // logger.debug("Y= " + y);
          int bf = 1;
          int bc = 1;
          double df = focusRegion.distance(x + rectangle.x, y + rectangle.y);
          double dc = transitionRegion.distance(x + rectangle.x, y
              + rectangle.y);

          double wf = dc != 0 ? 1 / Math.pow(df, bf) : 0;
          double wc = df != 0 ? 1 / Math.pow(dc, bc) : 0;

          int tX = (int) Math
              .round((wf
                  * (rectangle.width / 2 + (x - rectangle.width / 2)
                      / this.magni) + wc * x)
                  / (wf + wc));
          int tY = (int) Math
              .round((wf
                  * (rectangle.height / 2 + (y - rectangle.height / 2)
                      / this.magni) + wc * y)
                  / (wf + wc));
          if (tX >= image.getWidth() || tY >= image.getHeight()) {

          } else {

            imageToDraw.setRGB(x, y, image.getRGB(tX, tY));
          }

        } else {
          imageToDraw.setRGB(x, y, image.getRGB(x, y));
        }
      }
    }
    g2d.drawImage(imageToDraw, rectangle.x, rectangle.y, visuPanel);

    // visuPanel.repaint();

  }

  protected abstract void changeShapes(int x, int y);

  public ILensZoneShape getFocusRegion() {
    return focusRegion;
  }

  public void setFocusRegion(ILensZoneShape focusRegion) {
    this.focusRegion = focusRegion;
  }

  public ILensZoneShape getTransitionRegion() {
    return transitionRegion;
  }

  public void setTransitionRegion(ILensZoneShape transitionRegion) {
    this.transitionRegion = transitionRegion;
  }

}
