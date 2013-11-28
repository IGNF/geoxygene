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

import java.awt.BorderLayout;

import javax.swing.JToolBar;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;

public class CartAGenProjectPlugin implements ProjectFramePlugin {

  protected static CartAGenProjectPlugin instance = null;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de CartagenApplication.
   */
  public static CartAGenProjectPlugin getInstance() {
    return instance;
  }

  public CartAGenProjectPlugin() {
    super();
    instance = this;
  }

  @Override
  public void initialize(final ProjectFrame frame) {
    JToolBar toolbar = new BottomLegendToolbar(frame,
        frame.getLayerLegendPanel(), CartAGenPlugin.getInstance()
            .getApplication());
    frame.getLayerLegendPanel().add(toolbar, BorderLayout.PAGE_END);

    // ajout d'écouteurs
    frame.getLayerViewPanel().addMouseListener(
        CartAGenPlugin.getInstance().rightPanel);
    frame.getLayerViewPanel().addMouseMotionListener(
        CartAGenPlugin.getInstance().rightPanel);
  }

}
