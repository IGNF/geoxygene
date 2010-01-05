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

package fr.ign.cogit.geoxygene.util.console;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import fr.ign.cogit.geoxygene.I18N;

/**
 * GeOxygene Console.
 * @author Thierry Badard & Arnaud Braun
 */
public class GeOxygeneConsole {
    public static final int CASTOR = 1;
    public static final int OJB = 2;

    private static final String CONSOLE_TITLE
    = I18N.getString("GeOxygeneConsole.GeOxygeneConsole"); //$NON-NLS-1$

    public GeOxygeneConsoleInterface geOxygeneConsoleInterface;

    public GeOxygeneConsole () {
        this.geOxygeneConsoleInterface
        = new GeOxygeneConsoleInterface(CONSOLE_TITLE);
        this.geOxygeneConsoleInterface.pack();
        this.geOxygeneConsoleInterface.setSize(300, 350);
        //geOxygeneConsoleInterface.show();
        this.geOxygeneConsoleInterface.setVisible(true);
        this.geOxygeneConsoleInterface.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GeOxygeneConsole.this.geOxygeneConsoleInterface.dispose();
                System.exit(0);
            }
        });
    }
}
