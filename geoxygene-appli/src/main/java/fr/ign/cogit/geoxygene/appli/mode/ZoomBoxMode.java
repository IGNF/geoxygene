package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * @author Charlotte Hoarau
 * 
 */
public class ZoomBoxMode extends AbstractMode implements PaintListener {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public ZoomBoxMode(final MainFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  private Point initialPointView = null;
  private Point currentPointView = null;
  private Color color = Color.RED;

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/zoomBox.png"))); //$NON-NLS-1$
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    ProjectFrame projectFrame = this.mainFrame.getSelectedProjectFrame();
    projectFrame.getLayerViewPanel().addPaintListener(this);

    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.initialPointView = e.getPoint();
    }
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      Point currentPoint = e.getPoint();
      this.currentPointView = currentPoint;
      ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
      frame.getLayerViewPanel().superRepaint();
    }
  }

  private GM_Envelope getEnvelope(DirectPosition p1, DirectPosition p2) {
    double xmin = Math.min(p1.getX(), p2.getX());
    double ymin = Math.min(p1.getY(), p2.getY());
    double xmax = Math.max(p1.getX(), p2.getX());
    double ymax = Math.max(p1.getY(), p2.getY());
    return new GM_Envelope(xmin, xmax, ymin, ymax);
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        Point currentPoint = e.getPoint();
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(currentPoint);
        GM_Envelope env = null;
        if (this.initialPointView != null && p != null) {
          DirectPosition initialPoint = frame.getLayerViewPanel().getViewport()
              .toModelDirectPosition(this.initialPointView);
          env = this.getEnvelope(initialPoint, p);
        }
        this.initialPointView = null;
        this.currentPointView = null;
        if (env != null && env.width() > 0 && env.length() > 0) {
          frame.getLayerViewPanel().getViewport().zoom(env);
          frame.getLayerViewPanel().superRepaint();
        }
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("ZoomBox.ToolTip"); //$NON-NLS-1$
  }

  @Override
  public Cursor getCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Cursor cursor = toolkit.createCustomCursor(
        toolkit.getImage(this.getClass()
            .getResource("/images/cursors/32x32/zoomCursor.gif").getFile()), //$NON-NLS-1$
        new Point(16, 16), "Zoom"); //$NON-NLS-1$
    return cursor;
  }

  @Override
  public void paint(LayerViewPanel layerViewPanel, Graphics graphics) {
    if (this.initialPointView != null && this.currentPointView != null) {
      Color c = graphics.getColor();
      graphics.setColor(this.color);
      graphics.drawRect(
          Math.min(this.initialPointView.x, this.currentPointView.x),
          Math.min(this.initialPointView.y, this.currentPointView.y),
          Math.abs(this.initialPointView.x - this.currentPointView.x),
          Math.abs(this.initialPointView.y - this.currentPointView.y));
      graphics.setColor(c);
    }
  }
}
