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

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;


/**
 * Zoom mode. Allows the user to zoom.
 * @author Julien Perret
 *
 */
public class ZoomMode extends AbstractMode {
    /**
     * @param theMainFrame the main frame
     * @param theModeSelector the mode selector
     */
    public ZoomMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    @Override
    protected final JButton createButton() {
        return new JButton(new ImageIcon(this.getClass().
                getResource("/icons/16x16/zoom.png"))); //$NON-NLS-1$
    }

    @Override
    public final void leftMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
        try {
            frame.getLayerViewPanel().getViewport().zoomInTo(e.getPoint());
        } catch (NoninvertibleTransformException e1) { e1.printStackTrace(); }
    }
    @Override
    public final void rightMouseButtonClicked(final MouseEvent e,
            final ProjectFrame frame) {
        try {
            frame.getLayerViewPanel().getViewport().zoomOutTo(e.getPoint());
        } catch (NoninvertibleTransformException e1) { e1.printStackTrace(); }
    }
}
