/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
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

package fr.ign.cogit.cartagen.software.interfacecartagen.mode;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;

/**
 * Zoom mode. Allows the user to zoom.
 * @author Julien Perret
 * @author Guillaume Touya
 * 
 */
public class ZoomMode extends AbstractMode {
  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public ZoomMode(final GeoxygeneFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/zoom.png"))); //$NON-NLS-1$
  }

  @Override
  public final void leftMouseButtonClicked(final MouseEvent e,
      final GeoxygeneFrame frame) {
    frame.getVisuPanel().zoomInTo(e.getPoint());
  }

  @Override
  public final void rightMouseButtonClicked(final MouseEvent e,
      final GeoxygeneFrame frame) {
    frame.getVisuPanel().zoomOutTo(e.getPoint());

  }

  @Override
  public Cursor getCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    // Modif Cecile create image à partir d'une URL pour que ca marche depuis un
    // jar
    Cursor cursor = toolkit.createCustomCursor(
        toolkit.createImage(this.getClass().getResource(
            "/images/cursors/32x32/zoomCursor.gif")), //$NON-NLS-1$
        new Point(16, 16), "Zoom"); //$NON-NLS-1$
    // Fin modif Cecile
    return cursor;
  }

  @Override
  protected String getToolTipText() {
    return "Zoom mode"; //$NON-NLS-1$
  }
}
