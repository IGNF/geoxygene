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

package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import fr.ign.cogit.cartagen.core.carto.SLDUtil;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.LayerLegendPanel;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.ShowIniGeomAction;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class BottomLegendToolbar extends JToolBar {

  /****/
  private static final long serialVersionUID = 1L;
  private ProjectFrame frame;
  private LayerLegendPanel panel;
  private GeOxygeneApplication appli;
  private JToggleButton rawDisplayBtn;
  private JButton initGeomBtn;
  private StyledLayerDescriptor initialSld, rawSld;

  public BottomLegendToolbar(ProjectFrame frame, LayerLegendPanel panel,
      GeOxygeneApplication appli) {
    super();
    this.setFrame(frame);
    this.setPanel(panel);
    this.setAppli(appli);

    this.initGeomBtn = new JButton(new ShowIniGeomAction(appli));
    this.rawDisplayBtn = new JToggleButton(new RawDisplayAction());

    this.add(rawDisplayBtn);
    this.add(initGeomBtn);
    this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    this.setFloatable(false);
    this.setOrientation(HORIZONTAL);
  }

  public ProjectFrame getFrame() {
    return frame;
  }

  public void setFrame(ProjectFrame frame) {
    this.frame = frame;
  }

  public LayerLegendPanel getPanel() {
    return panel;
  }

  public void setPanel(LayerLegendPanel panel) {
    this.panel = panel;
  }

  public GeOxygeneApplication getAppli() {
    return appli;
  }

  public void setAppli(GeOxygeneApplication appli) {
    this.appli = appli;
  }

  public JToggleButton getRawDisplayBtn() {
    return rawDisplayBtn;
  }

  public void setRawDisplayBtn(JToggleButton rawDisplayBtn) {
    this.rawDisplayBtn = rawDisplayBtn;
  }

  public JButton getInitGeomBtn() {
    return initGeomBtn;
  }

  public void setInitGeomBtn(JButton initGeomBtn) {
    this.initGeomBtn = initGeomBtn;
  }

  class RawDisplayAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (rawDisplayBtn.isSelected()) {
        rawDisplayBtn.setText(I18N.getString("RawDisplay.selectedTitle"));
        // store the symbolised sld
        if (initialSld == null)
          initialSld = frame.getSld();
        if (rawSld == null) {
          // generate the raw sld from the symbolised sld
          rawSld = SLDUtil.computeRawSld(initialSld);
          frame.setSld(rawSld);
          for (Layer layer : rawSld.getLayers())
            frame.getLayerViewPanel().layerAdded(layer);
        } else
          frame.setSld(rawSld);
      } else {
        rawDisplayBtn.setText(I18N.getString("RawDisplay.unselectedTitle"));
        frame.setSld(initialSld);
      }
      frame.getLayerViewPanel().repaint();
    }

    public RawDisplayAction() {
      putValue(Action.NAME, I18N.getString("RawDisplay.unselectedTitle"));
      putValue(Action.SHORT_DESCRIPTION, I18N.getString("RawDisplay.descr"));
    }
  }

}
