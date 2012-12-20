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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;

/**
 * @author Julien Perret
 */
public class ModeSelector implements ContainerListener, KeyListener,
    MouseListener, MouseWheelListener, MouseMotionListener {
  /**
   * Logger.
   */
  static final Logger LOGGER = Logger.getLogger(ModeSelector.class.getName());
  /**
   * List of modes.
   */
  private List<Mode> modes = new ArrayList<Mode>();
  /**
   * The current mode.
   */
  private Mode currentMode = null;

  public Mode getCurrentMode() {
    return this.currentMode;
  }

  /**
   * The toolbar.
   */
  private JToolBar toolBar = new JToolBar("Selection mode"); //$NON-NLS-1$

  /**
   * Get the toolbar.
   * @return the toolbar
   */
  public final JToolBar getToolBar() {
    return this.toolBar;
  }

  /**
   * Associated main frame.
   */
  private GeoxygeneFrame mainFrame;

  /**
   * @return the main frame
   */
  public final GeoxygeneFrame getMainFrame() {
    return this.mainFrame;
  }

  /**
   * Set the main frame.
   * @param frame the new main frame
   */
  public final void setMainFrame(final GeoxygeneFrame frame) {
    this.mainFrame = frame;
  }

  /**
   * Constructor.
   * @param theMainFrame associated main frame
   */
  public ModeSelector(final GeoxygeneFrame theMainFrame) {
    this.setMainFrame(theMainFrame);
    this.getMainFrame().getTopPanel().add(this.toolBar, BorderLayout.PAGE_END);

    this.addComponent(this.getMainFrame());

    this.modes.add(new ZoomMode(this.getMainFrame(), this));
    this.modes.add(new ZoomBoxMode(this.getMainFrame(), this));
    this.modes.add(new MoveMode(this.getMainFrame(), this));
    this.modes.add(new SelectionMode(this.getMainFrame(), this));

    this.toolBar.addSeparator();

    JButton zoomToFullExtentButton = new JButton(new ImageIcon(
        ModeSelector.class
            .getResource("/images/icons/16x16/zoomToFullExtent.png"))); //$NON-NLS-1$
    zoomToFullExtentButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (ModeSelector.this.mainFrame != null) {
          ModeSelector.this.mainFrame.getVisuPanel().zoomToFullExtent();
        }
      }
    });
    zoomToFullExtentButton.setToolTipText("Zoom to full extent"); //$NON-NLS-1$
    this.toolBar.add(zoomToFullExtentButton);

    this.toolBar.addSeparator();

    this.toolBar.addSeparator();

    this.setCurrentMode(this.modes.get(3));
  }

  @Override
  public final void keyPressed(final KeyEvent e) {
    this.currentMode.keyPressed(e);
  }

  @Override
  public final void keyReleased(final KeyEvent e) {
    this.currentMode.keyReleased(e);
  }

  @Override
  public final void keyTyped(final KeyEvent e) {
    this.currentMode.keyTyped(e);
  }

  @Override
  public final void mouseClicked(final MouseEvent e) {
    this.currentMode.mouseClicked(e);
  }

  @Override
  public final void mouseEntered(final MouseEvent e) {
    if (this.currentMode == null) {
      return;
    }
    this.currentMode.mouseEntered(e);
  }

  @Override
  public final void mouseExited(final MouseEvent e) {
    if (this.currentMode == null) {
      return;
    }
    this.currentMode.mouseExited(e);
  }

  @Override
  public final void mousePressed(final MouseEvent e) {
    this.currentMode.mousePressed(e);
  }

  @Override
  public final void mouseReleased(final MouseEvent e) {
    this.currentMode.mouseReleased(e);
  }

  @Override
  public final void mouseWheelMoved(final MouseWheelEvent e) {
    this.currentMode.mouseWheelMoved(e);
  }

  // private long s = System.currentTimeMillis();
  @Override
  public final void mouseDragged(final MouseEvent e) {
    // long f = System.currentTimeMillis();
    // System.out.println("mouse dragged " + (f - s) + " "+
    // e.getSource().getClass().getSimpleName() + " " + e.getPoint());
    // s = f;
    this.currentMode.mouseDragged(e);
  }

  @Override
  public final void mouseMoved(final MouseEvent e) {
    if (this.currentMode == null) {
      return;
    }
    this.currentMode.mouseMoved(e);
  }

  /**
   * Set the current mode.
   * @param mode the new current mode.
   */
  public final void setCurrentMode(final Mode mode) {
    if (this.currentMode != null) {
      this.currentMode.getButton().setEnabled(true);
    }
    this.currentMode = mode;
    this.currentMode.getButton().setEnabled(false);
    if (this.getMainFrame() != null) {
      VisuPanel layerViewPanel = this.getMainFrame().getVisuPanel();
      layerViewPanel.setCursor(this.currentMode.getCursor());
    }
    this.currentMode.activated();
  }

  @Override
  public final void componentAdded(final ContainerEvent e) {
    // System.out.println("added component " +
    // e.getChild().getClass().getSimpleName());
    this.addComponent(e.getChild());
  }

  /**
   * Add a component.
   * @param component the newly added component
   */
  private void addComponent(final Component component) {
    if (component instanceof AbstractButton) {
      return;
    }
    component.addKeyListener(this);
    component.addMouseWheelListener(this);
    component.addMouseListener(this);
    component.addMouseMotionListener(this);
    if (component instanceof Container) {
      Container container = (Container) component;
      container.addContainerListener(this);
      for (Component child : container.getComponents()) {
        this.addComponent(child);
      }
    }
  }

  @Override
  public final void componentRemoved(final ContainerEvent e) {
    // System.out.println("removed component " +
    // e.getChild().getClass().getSimpleName());
  }
}
