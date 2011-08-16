/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Cette classe permet l'affichage d'une fenêtre graphique contenant un message
 * d'avertissement lorsqu'un accès à une valeur <b>null</b> est tenté ou qu'une
 * telle valeur est renvoyée par une méthode déclenchée depuis le navigateur
 * d'objet graphique de GeOxygene.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowserNullPointerFrame extends JFrame {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  /** Localisation des fichiers d'internationalisation de l'interface. */
  private static final String I18N_LANGUAGE_FILE_LOCATION = "fr.ign.cogit.geoxygene.util.browser.ObjectBrowserLanguageFile";
  /** Locale courante. */
  private Locale currentLocale;
  /** RessourceBundle lié à la Locale et au fichier d'internationalisation. */
  private ResourceBundle i18nLanguageFile;

  /**
   * Constructeur prinicipal de ObjectBrowserNullPointerFrame.
   * 
   * @throws HeadlessException
   */
  public ObjectBrowserNullPointerFrame() throws HeadlessException {

    super();

    this.currentLocale = Locale.getDefault();
    this.i18nLanguageFile = ResourceBundle.getBundle(
        ObjectBrowserNullPointerFrame.I18N_LANGUAGE_FILE_LOCATION,
        this.currentLocale);
    /*
     * i18nLanguageFile =
     * ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,new Locale("en",
     * "US"));
     */

    this.setTitle(this.i18nLanguageFile
        .getString("NullPointerFrameDefaultTitle"));

    try {
      URL imageUrl = this.getClass().getResource("images/exclamation.gif");

      JLabel nullPointerLabel = new JLabel(this.i18nLanguageFile
          .getString("NullPointerFrameDefaultLabel"), new ImageIcon(imageUrl),
          SwingConstants.CENTER);

      this.getContentPane().add(nullPointerLabel, BorderLayout.CENTER);

      // Dimension frameSize = new Dimension(this.getPreferredSize());
      this.setSize(new Dimension(375, 80));
      this.setResizable(false);

      this.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          ObjectBrowserNullPointerFrame.this.dispose();
        }
      });

      this.setVisible(true);
    } catch (NullPointerException e) {
      // e.printStackTrace();
    }

  }

}
