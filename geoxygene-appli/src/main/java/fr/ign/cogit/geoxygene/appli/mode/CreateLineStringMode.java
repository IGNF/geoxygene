package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JButton;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class CreateLineStringMode extends AbstractGeometryEditMode {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public CreateLineStringMode(final MainFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton("Line"); //$NON-NLS-1$
  }

  @Override
  public void leftMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {
    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      if (e.getClickCount() >= 2) {
        if (this.getPoints().size() >= 2) {
          this.getGeometryToolBar().createLineString(this.getPoints());
          this.getPoints().clear();
        }
      } else {
        this.getPoints().add(p);
      }
      frame.getLayerViewPanel().superRepaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    if (e.getSource() != this.mainFrame.getSelectedProjectFrame()
        .getLayerViewPanel()) {
      return;
    }
    try {
      DirectPosition p = this.mainFrame.getSelectedProjectFrame()
          .getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      this.currentPoint = p;
      this.mainFrame.getSelectedProjectFrame().getLayerViewPanel()
          .superRepaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("CreateLineStringMode.ToolTip"); //$NON-NLS-1$
  }
}
