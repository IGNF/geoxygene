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

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Layer;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.objectbrowsers.SimpleObjectBrowser;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Selection Mode. Allow the user to select features.
 * 
 * @author Julien Perret
 * 
 */
public class SelectionMode extends AbstractMode {

    private SimpleObjectBrowser browser = null;

    /**
     * @param theMainFrame
     *            the main frame
     * @param theModeSelector
     *            the mode selector
     */
    public SelectionMode(final GeoxygeneFrame theMainFrame, final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    @Override
    protected final JButton createButton() {
        return new JButton(new ImageIcon(this.getClass().getResource("/images/icons/16x16/selection.png"))); //$NON-NLS-1$
    }

    @Override
    public final void leftMouseButtonClicked(final MouseEvent e, final GeoxygeneFrame frame) {
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

        pv.getFrame().getRightPanel().lNbSelection.setText("Nb=" + pv.selectedObjects.size());

        if (!pv.automaticRefresh) {
            pv.repaint();
        }
    }

    @Override
    public final void rightMouseButtonClicked(final MouseEvent e, final GeoxygeneFrame frame) {
        VisuPanel pv = (VisuPanel) e.getSource();
        // position du clic
        double x = pv.pixToCoordX(e.getX()), y = pv.pixToCoordY(e.getY());
        GM_Point p = new GM_Point(new DirectPosition(x, y));
        List<IGeneObj> selectedObjs = new ArrayList<IGeneObj>();
        try {
            // get the selected objects
            for (Layer c : pv.getLayerManager().getLayers()) {
                if (c == null) {
                    continue;
                }
                if (c.isSelectable()) {
                    // parcours des objets de la liste
                    for (IFeature obj : c.getDisplayCache(pv)) {
                        if (obj.getGeom() == null || obj.getGeom().isEmpty()) {
                            continue;
                        }

                        // do not browse non IGeneObj features
                        if (!(obj instanceof IGeneObj)) {
                            continue;
                        }

                        // do not browse deleted features
                        if (obj.isDeleted()) {
                            continue;
                        }

                        // distance de la geometrie de l'objet au point du clic
                        double d = obj.getGeom().distance(p);
                        if (d <= pv.getSelectionDistance()) {
                            selectedObjs.add((IGeneObj) obj);
                        }
                    }
                }
            }
            this.browser = new SimpleObjectBrowser(e.getPoint(), selectedObjs);
            this.browser.setVisible(true);
        } catch (InterruptedException e1) {
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected String getToolTipText() {
        return "Selection mode"; //$NON-NLS-1$
    }
}
