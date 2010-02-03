package fr.ign.cogit.geoxygene.appli.mode;

import javax.swing.JButton;

import fr.ign.cogit.geoxygene.appli.MainFrame;

public class CreateInteriorRingMode extends AbstractGeometryEditMode {
    /**
     * @param theMainFrame the main frame
     * @param theModeSelector the mode selector
     */
    public CreateInteriorRingMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    @Override
    protected final JButton createButton() {
        return new JButton("Ring"); //$NON-NLS-1$
    }
}
