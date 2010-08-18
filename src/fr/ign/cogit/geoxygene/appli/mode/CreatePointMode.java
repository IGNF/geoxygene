package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JButton;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class CreatePointMode extends AbstractGeometryEditMode {
    /**
     * @param theMainFrame the main frame
     * @param theModeSelector the mode selector
     */
    public CreatePointMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    @Override
    protected final JButton createButton() {
        return new JButton("Point"); //$NON-NLS-1$
    }

    @Override
    public void leftMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
        try {
            DirectPosition p = frame.getLayerViewPanel().getViewport().
            toModelDirectPosition(e.getPoint());
            this.getGeometryToolBar().creerPoint(p);
        } catch (NoninvertibleTransformException e1) { e1.printStackTrace(); }
    }
    @Override
    protected String getToolTipText() {
        return I18N.getString("CreatePointMode.ToolTip"); //$NON-NLS-1$
    }
}
