package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.Layer;

public class SelectionBoxMode extends AbstractMode implements PaintListener {

  private Point initialPointView = null;
  private Point currentPointView = null;
  private Color color = Color.YELLOW;

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(SelectionMode.class
      .getName());

  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public SelectionBoxMode(final MainFrame theMainFrame,
      final MainFrameToolBar theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/selectionBox.png"))); //$NON-NLS-1$
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    ProjectFrame projectFrame = this.mainFrame.getSelectedProjectFrame();
    projectFrame.getLayerViewPanel().addPaintListener(this);

    if ((SwingUtilities.isLeftMouseButton(e))
        || (SwingUtilities.isRightMouseButton(e))) {
      this.initialPointView = e.getPoint();
    }
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))
        || (SwingUtilities.isRightMouseButton(e))) {
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
        Set<IFeature> features = new HashSet<IFeature>();
        for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
            .getLayers()) {
          if (layer.isVisible() && layer.isSelectable()) {
            for (IFeature feature : layer.getFeatureCollection().select(env)) {
              if (feature.isDeleted()) {
                continue;
              }
              features.add(feature);
            }
          }
        }
        LayerViewPanel lvPanel = frame.getLayerViewPanel();
        if ((SwingUtilities.isLeftMouseButton(e))) {
          lvPanel.getSelectedFeatures().addAll(features);
          LOGGER.debug("Number of selected features = " + features.size());
          for (IFeature feature : lvPanel.getSelectedFeatures()) {
            LOGGER.debug("\t" + feature);
          }
        } else if ((SwingUtilities.isRightMouseButton(e))) {
          if (features.isEmpty()) {
            lvPanel.getSelectedFeatures().clear();
          } else {
            lvPanel.getSelectedFeatures().removeAll(features);
          }
        }
        lvPanel.getRenderingManager().render(
            lvPanel.getRenderingManager().getSelectionRenderer());
        lvPanel.superRepaint();
      }
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public final void rightMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {
    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      Set<IFeature> features = new HashSet<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {

          IEnvelope env = new GM_Envelope(0.0, 0.0, 0.0, 0.0);
          features.addAll(layer.getFeatureCollection().select(env));
        }
      }
      LayerViewPanel lvPanel = frame.getLayerViewPanel();
      if (features.isEmpty()) {
        lvPanel.getSelectedFeatures().clear();
      } else {
        lvPanel.getSelectedFeatures().removeAll(features);
      }
      lvPanel.getRenderingManager().render(
          lvPanel.getRenderingManager().getSelectionRenderer());
      lvPanel.superRepaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("SelectionMode.ToolTip"); //$NON-NLS-1$
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
