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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 * Splash screen displayed when running the GeOxygene application.
 * 
 * @author Julien Perret
 */
public class SplashScreen extends JWindow {
  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The image label.
   */
  private JLabel imageLabel = new JLabel();
  /**
   * The caption label.
   */
  private JLabel captionLabel = new JLabel();
  /**
   * The progress bar.
   */
  private JProgressBar progressBar = new JProgressBar();
  /**
   * The default inset size.
   */
  private static final int DEFAULT_INSET_SIZE = 10;
  /**
   * The default font size.
   */
  private static final int DEFAULT_FONT_SIZE = 10;

  /**
   * Constructor.
   * @param image the icon image
   * @param caption the caption of the splash screen
   */
  public SplashScreen(final Icon image, final String caption) {
    JPanel container = new JPanel(new GridBagLayout());
    container.setBorder(BorderFactory.createLineBorder(Color.black));
    container.setBackground(Color.white);
    this.imageLabel.setIcon(image);
    this.captionLabel.setText(caption);
    this.captionLabel.setFont(new Font("Arial", //$NON-NLS-1$
        Font.PLAIN, SplashScreen.DEFAULT_FONT_SIZE));
    this.captionLabel.setBackground(Color.white);
    this.captionLabel.setOpaque(false);
    this.captionLabel.setForeground(Color.lightGray);
    this.progressBar.setOpaque(false);
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    container.add(this.imageLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
            SplashScreen.DEFAULT_INSET_SIZE, SplashScreen.DEFAULT_INSET_SIZE,
            SplashScreen.DEFAULT_INSET_SIZE, SplashScreen.DEFAULT_INSET_SIZE),
        0, 0));
    container.add(this.captionLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(0, SplashScreen.DEFAULT_INSET_SIZE, 0,
            SplashScreen.DEFAULT_INSET_SIZE), 0, 0));
    container.add(this.progressBar, new GridBagConstraints(0, 2, 1, 1, 0, 0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,
            SplashScreen.DEFAULT_INSET_SIZE, SplashScreen.DEFAULT_INSET_SIZE,
            SplashScreen.DEFAULT_INSET_SIZE), 0, 0));
    this.getContentPane().setBackground(Color.white);
    this.getContentPane().add(container);
    this.pack();
    this.setLocationRelativeTo(null);
  }
}
