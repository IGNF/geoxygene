package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class MoveFeatureMode extends AbstractGeometryEditMode {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public MoveFeatureMode(final MainFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton("Move feature"); //$NON-NLS-1$
  }

  private DirectPosition initialPoint = null;

  @Override
  public void mousePressed(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        this.initialPoint = p;
        this.currentPoint = p;
        this.dragCount = 0;
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        this.moveFeaturesToPoint(p);
        frame.getLayerViewPanel().repaint();
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  private void moveFeaturesToPoint(DirectPosition p) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    for (IFeature feature : frame.getLayerViewPanel().getSelectedFeatures()) {
      feature.setGeom(feature.getGeom().translate(
          p.getX() - this.initialPoint.getX(),
          p.getY() - this.initialPoint.getY(), 0));
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
  }

  int dragCount = 0;

  @Override
  public void mouseDragged(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    if ((SwingUtilities.isLeftMouseButton(e))) {
      try {
        Graphics2D graphics2D = (Graphics2D) frame.getLayerViewPanel()
            .getGraphics();
        graphics2D.setColor(Color.red);
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setXORMode(Color.white);
        DirectPosition p = frame.getLayerViewPanel().getViewport()
            .toModelDirectPosition(e.getPoint());
        if (this.dragCount > 0) {
          for (IFeature feature : frame.getLayerViewPanel()
              .getSelectedFeatures()) {
            RenderUtil.draw(
                feature.getGeom().translate(
                    this.currentPoint.getX() - this.initialPoint.getX(),
                    this.currentPoint.getY() - this.initialPoint.getY(), 0),
                frame.getLayerViewPanel().getViewport(), graphics2D);
          }
        }
        this.currentPoint = p;
        for (IFeature feature : frame.getLayerViewPanel().getSelectedFeatures()) {
          RenderUtil.draw(
              feature.getGeom().translate(
                  this.currentPoint.getX() - this.initialPoint.getX(),
                  this.currentPoint.getY() - this.initialPoint.getY(), 0),
              frame.getLayerViewPanel().getViewport(), graphics2D);
        }
        this.dragCount++;
      } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
      }
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("MoveFeatureMode.ToolTip"); //$NON-NLS-1$
  }
}
