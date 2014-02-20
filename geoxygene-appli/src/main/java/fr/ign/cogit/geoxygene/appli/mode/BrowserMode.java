/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.panel.SimpleObjectBrowser;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Browser Mode. Allow the user to browse feature attributes.
 * 
 * @author Julien Perret
 * 
 */
public class BrowserMode extends AbstractMode {

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(BrowserMode.class
      .getName());

  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public BrowserMode(final MainFrame theMainFrame,
      final MainFrameToolBar theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/browse.png"))); //$NON-NLS-1$
  }

  /**
   * Selection radius. Modifications @author amaudet : A radius based on pixels
   * seems better than a radius based on map-metres.
   */
  // private final double selectionRadius = 10.0;
  private double getSelectionRadius() {
    double scale = this.mainFrame.getSelectedProjectFrame().getLayerViewPanel()
        .getViewport().getScale();
    return 10.0 / scale;
  }

  @Override
  public final void leftMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {

    LOGGER
        .debug("--------------------------------------------------------------------------------");
    LOGGER.debug("click event : select");

    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      List<IFeature> features = new ArrayList<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {
          for (IFeature feature : layer.getFeatureCollection().select(p,
              this.getSelectionRadius())) {
            if (feature.isDeleted()) {
              continue;
            }
            features.add(feature);
          }
        }
      }

      SimpleObjectBrowser browser = new SimpleObjectBrowser(e.getPoint(),
          features);
      browser.setVisible(true);

    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public final void rightMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {
    // Do nothing
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("SelectionMode.ToolTip"); //$NON-NLS-1$
  }
}
