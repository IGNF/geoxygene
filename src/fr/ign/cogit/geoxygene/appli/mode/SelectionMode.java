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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * @author Julien Perret
 */
public class SelectionMode extends AbstractMode {

  /**
   * @param theMainFrame
   * @param theModeSelector
   */
  public SelectionMode(MainFrame theMainFrame, ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/icons/16x16/selection.png")));
  }

  @Override
  public void leftMouseButtonClicked(MouseEvent e, ProjectFrame frame) {
    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      IFeatureCollection<IFeature> features = new FT_FeatureCollection<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {
          features.addAll(layer.getFeatureCollection().select(p, 10.0));
        }
      }
      frame.getLayerViewPanel().getSelectedFeatures().addAll(features);
      frame.getLayerViewPanel().repaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void rightMouseButtonClicked(MouseEvent e, ProjectFrame frame) {
    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      IFeatureCollection<IFeature> features = new FT_FeatureCollection<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {
          features.addAll(layer.getFeatureCollection().select(p, 10.0));
        }
      }
      if (features.isEmpty()) {
        frame.getLayerViewPanel().getSelectedFeatures().clear();
      } else {
        frame.getLayerViewPanel().getSelectedFeatures().removeAll(features);
      }
      frame.getLayerViewPanel().repaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public Cursor getCursor() {
    return null;
  }

  @Override
  protected String getToolTipText() {
    return null;
  }
}
