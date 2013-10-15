package fr.ign.cogit.geoxygene.appli.event;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * Listener for displaying mouse coordinates when it is moving.
 * 
 * @author Charlotte Hoarau
 * 
 */
public class CoordPaintListener implements MouseMotionListener {

  @Override
  public void mouseMoved(MouseEvent e) {
    CoordPaintListener.displayCoord(e.getPoint(),
        (LayerViewPanel) e.getSource());
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  public static void displayCoord(Point pointMouse,
      LayerViewPanel layerViewPanel) {
    try {
      DirectPosition point = layerViewPanel.getViewport()
          .toModelDirectPosition(pointMouse);
      Graphics graphics = layerViewPanel.getGraphics();

      int shift = 10;
      int numericScaleLength = 80;
      int coordRectLength = 200;

      // Drawing the white background
      graphics.setColor(Color.WHITE);
      graphics.fillRect(layerViewPanel.getWidth() - numericScaleLength
          - coordRectLength, layerViewPanel.getHeight() - 3 * shift,
          coordRectLength, 3 * shift);
      // Drawing the black contour and the coordinates
      graphics.setColor(Color.BLACK);
      graphics.drawRect(layerViewPanel.getWidth() - numericScaleLength
          - coordRectLength, layerViewPanel.getHeight() - 3 * shift,
          coordRectLength, 3 * shift - 1);
      graphics.drawString("X : " + Math.round(point.getX()) + "     -     Y : " //$NON-NLS-1$ //$NON-NLS-2$
          + Math.round(point.getY()), layerViewPanel.getWidth()
          - numericScaleLength - coordRectLength + shift,
          layerViewPanel.getHeight() - shift);
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }
}
