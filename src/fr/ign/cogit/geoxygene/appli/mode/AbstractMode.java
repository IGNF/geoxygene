/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JButton;
import javax.swing.JComponent;

import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;

/**
 * @author Julien Perret
 */
public abstract class AbstractMode implements Mode {
    /**
     * MainFrame.
     */
    protected MainFrame mainFrame;
    /**
     * Mode Selector.
     */
    private ModeSelector modeSelector;
    /**
     * Button triggering the mode.
     */
    private JButton button;

    /**
     * Set the mode selector.
     * @param theModeSelector the new mode selector
     */
    public final void setModeSelector(final ModeSelector theModeSelector) {
        this.modeSelector = theModeSelector;
    }

    /**
     * @return the mode selector
     */
    public final ModeSelector getModeSelector() {
        return this.modeSelector;
    }

    /**
     * @return the button giving access to this mode
     */
    public final JButton getButton() {
        return this.button;
    }

    /**
     * Constructor.
     * @param theMainFrame the associated application
     * @param theModeSelector the mode selector
     */
    public AbstractMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        this.mainFrame = theMainFrame;
        this.setModeSelector(theModeSelector);
        final Mode currentMode = this;
        this.button = this.createButton();
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AbstractMode.this.getModeSelector().setCurrentMode(currentMode);
            }
        });
        this.getModeSelector().getToolBar().add(this.getButton());
    }

    /**
     * Create a button.
     * @return the created button
     */
    protected abstract JButton createButton();

    @Override
    public final void keyPressed(final KeyEvent e) {
        ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
        if (frame == null) {
            return;
        }
        switch (e.getKeyCode()) {
        case KeyEvent.VK_KP_UP:
        case KeyEvent.VK_UP:
            try {
                frame.getLayerViewPanel().getViewport().moveUp();
            } catch (NoninvertibleTransformException e2) {
                e2.printStackTrace();
            }
            break;
        case KeyEvent.VK_KP_DOWN:
        case KeyEvent.VK_DOWN:
            try {
                frame.getLayerViewPanel().getViewport().moveDown();
            } catch (NoninvertibleTransformException e2) {
                e2.printStackTrace();
            }
            break;
        case KeyEvent.VK_KP_RIGHT:
        case KeyEvent.VK_RIGHT:
            try {
                frame.getLayerViewPanel().getViewport().moveRight();
            } catch (NoninvertibleTransformException e2) {
                e2.printStackTrace();
            }
            break;
        case KeyEvent.VK_KP_LEFT:
        case KeyEvent.VK_LEFT:
            try {
                frame.getLayerViewPanel().getViewport().moveLeft();
            } catch (NoninvertibleTransformException e2) {
                e2.printStackTrace();
            }
            break;
        case KeyEvent.VK_PAGE_UP:
            try {
                frame.getLayerViewPanel().getViewport().zoomIn();
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
            break;
        case KeyEvent.VK_PAGE_DOWN:
            try {
                frame.getLayerViewPanel().getViewport().zoomOut();
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
            break;
        default:
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public final void mouseClicked(final MouseEvent e) {
        ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
        if ((frame == null) || (e.getSource() != frame.getLayerViewPanel())) {
            return;
        }
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            this.leftMouseButtonClicked(e, frame);
            break;
        case MouseEvent.BUTTON2:
            this.middleMouseButtonClicked(e, frame);
            break;
        case MouseEvent.BUTTON3:
            this.rightMouseButtonClicked(e, frame);
            break;
        default:
        }
    }

    /**
     * @param e mouse event
     * @param frame the project frame
     */
    public void leftMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
    }

    /**
     * @param e mouse event
     * @param frame the project frame
     */
    public final void middleMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
        try {
            frame.getLayerViewPanel().getViewport().moveTo(e.getPoint());
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * @param e mouse event
     * @param frame the project frame
     */
    public void rightMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (e.getSource() instanceof JComponent) {
            ((JComponent) e.getSource()).requestFocus();
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public final void mouseWheelMoved(final MouseWheelEvent e) {
        boolean zoomIn = e.getWheelRotation() < 0;
        ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
        if (frame == null) {
            return;
        }
        try {
            if (zoomIn) {
                frame.getLayerViewPanel().getViewport().zoomInTo(e.getPoint());
            } else {
                frame.getLayerViewPanel().getViewport().zoomOutTo(e.getPoint());
            }
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }
    @Override
    public void activated() {
    }
}
