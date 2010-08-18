package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JButton;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

public class CreateRectangleMode extends AbstractGeometryEditMode {
    /**
     * @param theMainFrame the main frame
     * @param theModeSelector the mode selector
     */
    public CreateRectangleMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    @Override
    protected final JButton createButton() {
        return new JButton("Rectangle"); //$NON-NLS-1$
    }
    @Override
    public void leftMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
        try {
            DirectPosition p = frame.getLayerViewPanel().getViewport().
            toModelDirectPosition(e.getPoint());
            if(e.getClickCount() >= 2) {
                if (this.getPoints().size() >= 1) {
                    double temp;
                    double x = this.getPoints().get(0).getX();
                    double y = this.getPoints().get(0).getY();
                    double x2 = p.getX();
                    double y2 = p.getY();
                    if (x > x2) {
                        temp = x2;
                        x2 = x;
                        x = temp;
                    }
                    if (y > y2) {
                        temp = y2;
                        y2 = y;
                        y = temp;
                    }                   
                    GM_Envelope env = new GM_Envelope(x, x2, y, y2);
                    this.getGeometryToolBar().createPolygon(env.getGeom().coord());
                    this.getPoints().clear();
                }
            } else { this.getPoints().add(p); }
            frame.getLayerViewPanel().superRepaint();
        } catch (NoninvertibleTransformException e1) { e1.printStackTrace(); }
    }
    @Override
    public void mouseMoved(final MouseEvent e) {
        if (e.getSource() != this.mainFrame.getSelectedProjectFrame()
                .getLayerViewPanel()) { return; }
        try {
            DirectPosition p = this.mainFrame.getSelectedProjectFrame()
            .getLayerViewPanel().getViewport().
            toModelDirectPosition(e.getPoint());
            this.currentPoint = p;
            this.mainFrame.getSelectedProjectFrame()
            .getLayerViewPanel().superRepaint();
       } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }
    @Override
    protected String getToolTipText() {
        return I18N.getString("CreateRectangleMode.ToolTip"); //$NON-NLS-1$
    }
}
