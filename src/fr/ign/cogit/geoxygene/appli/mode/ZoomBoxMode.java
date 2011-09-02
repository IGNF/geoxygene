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

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
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
  // private Point previousPointView = null;

  // private DirectPosition initialPoint = null;
  // private DirectPosition lastPoint = null;
  private Color color = Color.RED;

  // private Color xorColor = new Color(4, 52, 87);
  // private Graphics2D graphics2D = null;
  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/zoomBox.png"))); //$NON-NLS-1$
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    ProjectFrame projectFrame = this.mainFrame.getSelectedProjectFrame();
    projectFrame.getLayerViewPanel().addPaintListener(this);

    /*
     * ProjectFrame frame = this.mainFrame.getSelectedProjectFrame(); graphics2D
     * = (Graphics2D) frame.getLayerViewPanel() .getGraphics();
     * graphics2D.setColor(color); graphics2D.setXORMode(xorColor);
     */
    if ((SwingUtilities.isLeftMouseButton(e))) {
      // try {
      // DirectPosition p = frame.getLayerViewPanel().getViewport()
      // .toModelDirectPosition(e.getPoint());
      // this.initialPoint = p;
      this.initialPointView = e.getPoint();
      // } catch (NoninvertibleTransformException e1) {
      // e1.printStackTrace();
      // }
    }
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    // long s = System.currentTimeMillis();
    // ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      // try {
      Point currentPoint = e.getPoint();
      // DirectPosition p = frame.getLayerViewPanel().getViewport()
      // .toModelDirectPosition(e.getPoint());
      // GM_Envelope envelope = this.getEnvelope(this.initialPoint, p);
      // GM_Polygon rect = new GM_Polygon(envelope);
      // graphics2D.setStroke(new BasicStroke(2));
      if (this.currentPointView != null) {
        // this.previousPointView = this.currentPointView;
        // GM_Envelope lastEnvelope = this.getEnvelope(this.initialPoint,
        // this.lastPoint);
        // long f = System.currentTimeMillis();
        // GM_Polygon lastRect = new GM_Polygon(lastEnvelope);
        // RenderUtil.draw(lastRect, frame.getLayerViewPanel().getViewport(),
        // graphics2D);
        /*
         * graphics2D.drawRect( Math.min(this.initialPointView.x,
         * this.currentPointView.x), Math.min(this.initialPointView.y,
         * this.currentPointView.y), Math.abs(this.initialPointView.x -
         * this.currentPointView.x), Math.abs(this.initialPointView.y -
         * this.currentPointView.y)); long f = System.currentTimeMillis();
         * System.out.println("draw previous= " + (f - s)); s = f;
         */
      }
      // RenderUtil.draw(rect, frame.getLayerViewPanel().getViewport(),
      // graphics2D);
      /*
       * graphics2D.drawRect( Math.min(this.initialPointView.x, currentPoint.x),
       * Math.min(this.initialPointView.y, currentPoint.y),
       * Math.abs(this.initialPointView.x - currentPoint.x),
       * Math.abs(this.initialPointView.y - currentPoint.y)); long f =
       * System.currentTimeMillis(); System.out.println("draw = " + (f - s));
       */
      // s = f;
      // this.lastPoint = p;
      // this.previousPointView = this.currentPointView;
      this.currentPointView = currentPoint;
      // } catch (NoninvertibleTransformException e1) {
      // e1.printStackTrace();
      // }
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
        // this.previousPointView = null;
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
    // graphics.setXORMode(Color.GRAY);
    /*
     * if (this.initialPointView != null && this.previousPointView != null) {
     * graphics.drawRect( Math.min(this.initialPointView.x,
     * this.previousPointView.x), Math.min(this.initialPointView.y,
     * this.previousPointView.y), Math.abs(this.initialPointView.x -
     * this.previousPointView.x), Math.abs(this.initialPointView.y -
     * this.previousPointView.y)); }
     */
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
