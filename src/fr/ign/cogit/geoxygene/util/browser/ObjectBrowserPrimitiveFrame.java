/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Cette classe rend possible l'affichage de l'argument de retour (pour l'instant de type primitif ou chaîne de caractère)
 * renvoyé par une méthode déclenchée depuis le navigateur d'objet graphique de GeOxygene.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowserPrimitiveFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Localisation des fichiers d'internationalisation de l'interface. */
	private static final String I18N_LANGUAGE_FILE_LOCATION = "fr.ign.cogit.geoxygene.util.browser.ObjectBrowserLanguageFile"; //$NON-NLS-1$
	/** Taille par défaut du champ texte affichant le résultat de la méthode.*/
	private static final int PRIMITIVE_FRAME_DEFAULT_TEXTFIELD_SIZE = 35;
	/** Locale courante. */
	private Locale currentLocale;
	/** RessourceBundle lié à la Locale et au fichier d'internationalisation. */
	private ResourceBundle i18nLanguageFile;

	/**
	 * Constructeur principal de ObjectBrowserPrimitiveFrame.
	 * 
	 * @param title titre de la fenêtre
	 * @param value valeur transtypée en chaîne de caractères de l'argument de retour de la méthode déclenchée.
	 * @throws HeadlessException
	 */
	public ObjectBrowserPrimitiveFrame(String title, String value)
	throws HeadlessException {
		super();

		this.currentLocale = Locale.getDefault();
		this.i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,this.currentLocale);
		//i18nLanguageFile = ResourceBundle.getBundle(I18N_LANGUAGE_FILE_LOCATION,new Locale("en", "US"));

		setTitle(this.i18nLanguageFile.getString("PrimitiveFrameDefaultTitle")+ title + "()");  //$NON-NLS-1$//$NON-NLS-2$

		JTextField returnedValue =
			new JTextField(value, PRIMITIVE_FRAME_DEFAULT_TEXTFIELD_SIZE);
		returnedValue.setSize(returnedValue.getPreferredSize());
		this.getContentPane().add(returnedValue, BorderLayout.CENTER);

		JLabel returnedValueLabel = new JLabel(this.i18nLanguageFile.getString("PrimitiveFrameDefaultLabel")); //$NON-NLS-1$
		returnedValueLabel.setSize(returnedValueLabel.getPreferredSize());
		this.getContentPane().add(returnedValueLabel, BorderLayout.WEST);
		this.getContentPane().setSize(this.getContentPane().getPreferredSize());

		Dimension frameSize = new Dimension(this.getPreferredSize());
		this.setSize(
				new Dimension(
						(int) frameSize.getWidth(),
						(int) frameSize.getHeight() + 30));

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		this.setVisible(true);
	}

}
