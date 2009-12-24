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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Font;

import javax.swing.ImageIcon;

/**
 * Base class for GeOxygene applications.
 *
 * @author Julien Perret
 */
public class GeOxygeneApplication {
        /**
         * The splash Image used when running the application.
         */
        private static ImageIcon splashImage;

        /**
         * @return The splash image
         */
        public static synchronized ImageIcon splashImage() {
            if (splashImage == null) {
                synchronized (GeOxygeneApplication.class) {
                    splashImage = new ImageIcon(
                        GeOxygeneApplication.class
                            .getResource("/geoxygene-logo.png")); //$NON-NLS-1$
                }
            }
            return splashImage;
        }

        /**
         * The icon of the icon, i.e. the GeOxygene icon by default. Also used
         * by {@link LayerViewPanel}
         */
        private ImageIcon applicationIcon = new ImageIcon(
                        GeOxygeneApplication.class.
                                getResource("/icone.gif")); //$NON-NLS-1$

        /**
         *
         * @return the icon of the application
         * @see ImageIcon
         */
        public final ImageIcon getIcon() {
                return this.applicationIcon;
        }

        /**
         * Default font size.
         */
        private final int fontSize = 10;

        /**
         * The font used by the application.
         */
        private Font font = new Font(
                        "Arial", //$NON-NLS-1$
                        Font.PLAIN,
                        this.fontSize);

        /**
         * @return The font to be used for all menus, etc.
         */
        public final Font getFont() {
                return this.font;
        }

        /**
         * private main frame of the application.
         */
        private MainFrame frame;

        /**
         * @return The main frame of the application.
         */
        public final MainFrame getFrame() {
                return this.frame;
        }

        /**
         * Constructor.
         */
        public GeOxygeneApplication() {
                this.frame = new MainFrame("GeOxygene", this); //$NON-NLS-1$
                this.frame.setVisible(true);
        }

        /**
         * Constructor.
         * @param title title of the application
         * @param theApplicationIcon the application icon
         */
        public GeOxygeneApplication(
                        final String title,
                        final ImageIcon theApplicationIcon) {
                this.applicationIcon = theApplicationIcon;
                this.frame = new MainFrame(title, this);
                this.frame.setVisible(true);
        }

        /**
         * Exit the application.
         */
        public final void exit() {
                this.frame.setVisible(false);
                this.frame.dispose();
        }

        /**
         * Main GeOxygene Application.
         * @param args arguments of the application
         */
        public static void main(final String[] args) {
                SplashScreen splashScreen = new SplashScreen(splashImage(),
                                "GeOxygene"); //$NON-NLS-1$
                splashScreen.setVisible(true);
                GeOxygeneApplication application = new GeOxygeneApplication();
                application.getFrame().newProjectFrame();
                application.getFrame().setVisible(true);
                splashScreen.setVisible(false);
                splashScreen.dispose();
        }
}
