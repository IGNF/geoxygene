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

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;

/**
 * @author Julien Perret
 * 
 */
public class GeometryEditingMode extends AbstractGeometryEditMode {

  /**
   * Constructor.
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public GeometryEditingMode(MainFrame theMainFrame,
      MainFrameToolBar theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/edit.png"))); //$NON-NLS-1$
  }

  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  @Override
  public void leftMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("GeometryEditingMode.ToolTip"); //$NON-NLS-1$
  }
}
