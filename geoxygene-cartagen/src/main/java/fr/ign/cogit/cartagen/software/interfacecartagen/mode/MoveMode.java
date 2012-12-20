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
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;

/**
 * @author Julien Perret
 * @author Charlotte Hoarau
 * 
 */
public class MoveMode extends AbstractMode {
  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(MoveMode.class.getName());

  private Point initialPointView = null;
  private Point currentPointView = null;
  private Cursor cursor, cursorClosed;

  /**
   * Constructor.
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public MoveMode(final GeoxygeneFrame theMainFrame,
      final ModeSelector theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/pan.png"))); //$NON-NLS-1$
  }

  @Override
  public final void leftMouseButtonClicked(final MouseEvent e,
      final GeoxygeneFrame frame) {
    frame.getVisuPanel().moveTo(e.getPoint());
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.initialPointView = e.getPoint();
      this.mainFrame.getVisuPanel().setCursor(this.cursorClosed);
    }
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.currentPointView = e.getPoint();

      int xMove = new Double(this.initialPointView.getX()
          - this.currentPointView.getX()).intValue();
      int yMove = new Double(this.currentPointView.getY()
          - this.initialPointView.getY()).intValue();

      this.mainFrame.getVisuPanel().panVector(xMove, yMove);

      this.initialPointView = this.currentPointView;
      this.currentPointView = null;
    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    if ((SwingUtilities.isLeftMouseButton(e))) {
      this.mainFrame.getVisuPanel().setCursor(this.cursor);
      this.currentPointView = e.getPoint();

      int xMove = new Double(this.initialPointView.getX()
          - this.currentPointView.getX()).intValue();
      int yMove = new Double(this.currentPointView.getY()
          - this.initialPointView.getY()).intValue();

      this.mainFrame.getVisuPanel().panVector(xMove, yMove);

      this.initialPointView = null;
      this.currentPointView = null;
    }
  }

  @Override
  public Cursor getCursor() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (this.cursor == null) {
      this.cursor = toolkit
          .createCustomCursor(
              toolkit
                  .getImage(this
                      .getClass()
                      .getResource("/images/cursors/32x32/panCursor.png").getFile().replaceAll("%20", " ")), //$NON-NLS-1$
              new Point(16, 16), "Pan"); //$NON-NLS-1$
      this.cursorClosed = toolkit
          .createCustomCursor(
              toolkit
                  .getImage(this
                      .getClass()
                      .getResource("/images/cursors/32x32/panClosedCursor.png").getFile().replaceAll("%20", " ")), //$NON-NLS-1$
              new Point(16, 16), "PanClosed"); //$NON-NLS-1$
    }
    return this.cursor;
  }

  @Override
  protected String getToolTipText() {
    return "Move mode"; //$NON-NLS-1$
  }
}
