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

package fr.ign.cogit.geoxygene.util.loader.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Envoie d'un message a l'utilisateur ...
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class GUICompileMessage extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5149838834707648811L;

	private static final String FRAME_TITLE = "GeOxygene Message";

	private static String message = "A faire toi meme ! : "+
	"\n## Compiler les classes generees"+
	"\n## Faire pointer le fichier racine repository.xml vers les fichiers de mapping generes"+
	"\n et apres ceci : creer une colonne \"COGITID\" dans vos tables " +
	"\n                  a l'aide du menu \"Manage Data\" (Generation COGITID)";


	public  GUICompileMessage () {
	}

	public void showDialog() {
		final  JDialog dialog = createDialog(this);
		//dialog.show();
		dialog.setVisible(true);
		dialog.dispose();
	}


	private JDialog createDialog (Frame parent) {

		String title = FRAME_TITLE ;
		final JDialog dialog = new JDialog(parent, title, true);
		Container contentPane = dialog.getContentPane();

		JTextArea text = new JTextArea(message);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		contentPane.add(text,BorderLayout.CENTER);
		contentPane.add(okButton, BorderLayout.SOUTH);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		return dialog;
	}



}
