/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.mode;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.cartagen.software.interfacecartagen.event.PaintListener;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * @author Charlotte Hoarau
 * @author Guillaume Touya
 * 
 */
public class ZoomBoxMode extends AbstractMode implements PaintListener {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public ZoomBoxMode(final GeoxygeneFrame theMainFrame,
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
    this.mainFrame.getVisuPanel().addPaintListener(this);

    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.initialPointView = e.getPoint();
    }
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      Point currentPoint = e.getPoint();
      this.currentPointView = currentPoint;
      this.paint(this.mainFrame.getVisuPanel(), this.mainFrame.getVisuPanel()
          .getGraphics());
    }
  }

  private GM_Envelope getEnvelope(IDirectPosition p1, IDirectPosition p2) {
    double xmin = Math.min(p1.getX(), p2.getX());
    double ymin = Math.min(p1.getY(), p2.getY());
    double xmax = Math.max(p1.getX(), p2.getX());
    double ymax = Math.max(p1.getY(), p2.getY());
    return new GM_Envelope(xmin, xmax, ymin, ymax);
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      Point currentPoint = e.getPoint();
      IDirectPosition p = this.mainFrame.getVisuPanel().toModelDirectPosition(
          currentPoint);
      GM_Envelope env = null;
      if (this.initialPointView != null && p != null) {
        IDirectPosition initialPoint = this.mainFrame.getVisuPanel()
            .toModelDirectPosition(this.initialPointView);
        env = this.getEnvelope(initialPoint, p);
      }
      this.initialPointView = null;
      this.currentPointView = null;
      if (env != null && env.width() > 0 && env.length() > 0) {
        this.mainFrame.getVisuPanel().centerAndZoom(env);
        this.mainFrame.getVisuPanel().repaint();
      }
      this.mainFrame.getVisuPanel().clearPaintListeners();
    }
  }

  @Override
  protected String getToolTipText() {
    return "Zoom box"; //$NON-NLS-1$
  }

  @Override
  public Cursor getCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Cursor cursor = toolkit
        .createCustomCursor(
            toolkit
                .getImage(this
                    .getClass()
                    .getResource("/images/cursors/32x32/zoomCursor.gif").getFile().replaceAll("%20", " ")), //$NON-NLS-1$
            new Point(16, 16), "Zoom"); //$NON-NLS-1$
    return cursor;
  }

  @Override
  public void paint(VisuPanel layerViewPanel, Graphics graphics) {
    if (this.initialPointView != null && this.currentPointView != null) {
      Color c = graphics.getColor();
      graphics.setColor(this.color);
      graphics.drawRect(Math.min(this.initialPointView.x,
          this.currentPointView.x), Math.min(this.initialPointView.y,
          this.currentPointView.y), Math.abs(this.initialPointView.x
          - this.currentPointView.x), Math.abs(this.initialPointView.y
          - this.currentPointView.y));
      graphics.setColor(c);
    }
  }
}
