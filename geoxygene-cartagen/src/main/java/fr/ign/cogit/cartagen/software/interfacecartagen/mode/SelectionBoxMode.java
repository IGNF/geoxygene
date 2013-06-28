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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.cartagen.software.interfacecartagen.event.PaintListener;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Layer;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Selection Mode. Allow the user to select features with a box.
 * @author Julien Perret
 * 
 */
public class SelectionBoxMode extends AbstractMode implements PaintListener {

  private Point initialPointView = null;
  private Point currentPointView = null;
  private Color color = Color.DARK_GRAY;

  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public SelectionBoxMode(final GeoxygeneFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/selection_box.png"))); //$NON-NLS-1$
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    this.mainFrame.getVisuPanel().addPaintListener(this);

    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.initialPointView = e.getPoint();
    }
  }

  @Override
  public final void leftMouseButtonClicked(final MouseEvent e,
      final GeoxygeneFrame frame) {
    VisuPanel pv = (VisuPanel) e.getSource();
    // position du clic
    double x = pv.pixToCoordX(e.getX()), y = pv.pixToCoordY(e.getY());
    GM_Point p = new GM_Point(new DirectPosition(x, y));

    try {
      // ajout des objets des couches selectionnables a la selection
      for (Layer c : pv.getLayerManager().getLayers()) {
        if (c == null) {
          continue;
        }
        if (c.isSelectable()) {
          pv.addToSelection(c.getDisplayCache(pv), p);
        }
      }
    } catch (InterruptedException e1) {
    }

    pv.getFrame().getRightPanel().lNbSelection.setText("Nb="
        + pv.selectedObjects.size());

    if (!pv.automaticRefresh) {
      pv.repaint();
    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      if (!(e.getSource() instanceof VisuPanel))
        return;
      VisuPanel pv = (VisuPanel) e.getSource();
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
        try {
          // ajout des objets des couches selectionnables a la selection
          for (Layer c : pv.getLayerManager().getLayers()) {
            if (c == null) {
              continue;
            }
            if (c.isSelectable()) {
              pv.addToSelection(c.getDisplayCache(pv), env);
            }
          }
        } catch (InterruptedException e1) {
        }

        pv.getFrame().getRightPanel().lNbSelection.setText("Nb="
            + pv.selectedObjects.size());

        if (!pv.automaticRefresh) {
          pv.repaint();
        }
      }
      this.mainFrame.getVisuPanel().clearPaintListeners();
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

  @Override
  protected String getToolTipText() {
    return "Selection box mode"; //$NON-NLS-1$
  }

  private GM_Envelope getEnvelope(IDirectPosition p1, IDirectPosition p2) {
    double xmin = Math.min(p1.getX(), p2.getX());
    double ymin = Math.min(p1.getY(), p2.getY());
    double xmax = Math.max(p1.getX(), p2.getX());
    double ymax = Math.max(p1.getY(), p2.getY());
    return new GM_Envelope(xmin, xmax, ymin, ymax);
  }

  @Override
  public void paint(VisuPanel layerViewPanel, Graphics graphics) {
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
