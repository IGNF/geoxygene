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

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Choix des parametres pour la generation de tables dans le SGBD et de fichiers de mapping OJB.
 * Ces parametres sont : repertoires d'accueil (XML), nom du package,nom des fichiers de mapping.
 *
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class GUIConfigSqlOJBXML extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6620516892426316181L;

	private static final String FRAME_TITLE = "GeOxygene Configuration SQL / mapping OJB";

	private String javaFilePath;
	private String tableName;
	private String mappingDirectory;
	private String mappingFileName;

	private static String javaFilePathText = "Classe Java à traduire : ";
	private static String tableNameText = "Nom de la table : ";
	private static String mappingDirectoryText = "Repertoire d'accueil du fichier de mapping : ";
	private static String mappingFileNameText = "Nom du fichier de mapping : ";

	private String[] selectedValues = new String[4];

	public  GUIConfigSqlOJBXML (String GeoxygeneDirectory, String MappingDirectory, String MappingFileName) {
		javaFilePath = GeoxygeneDirectory;
		mappingDirectory = MappingDirectory;
		mappingFileName = MappingFileName;
	}


	public String[] showDialog() {
		final  JDialog dialog = createDialog(this);
		dialog.setVisible(true);
		dialog.dispose();
		return selectedValues;
	}


	private void getSelectedValues() {
		selectedValues[0] = javaFilePath;
		selectedValues[1] = mappingDirectory;
		selectedValues[2] = tableName;
		selectedValues[3] = mappingFileName;
	}


	private JDialog createDialog (Frame parent) {

		String title = FRAME_TITLE;
		final JDialog dialog = new JDialog(parent, title, true);

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new GridLayout(5,2));


		JLabel javaFilePathLabel = new JLabel (javaFilePathText);
		JPanel javaFilePathPanel =	new JPanel(new FlowLayout());
		final JTextField javaFilePathTextField = new JTextField (javaFilePath);
		JButton javaFilePathChoiceButton = new JButton("...");
		javaFilePathChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				javaFilePathTextField.setText(GUIConfigSqlOJBXML.selectRepertoire(javaFilePath));
			}
		});
		javaFilePathPanel.add(javaFilePathTextField);
		javaFilePathPanel.add(javaFilePathChoiceButton);
		contentPane.add(javaFilePathLabel);
		contentPane.add(javaFilePathPanel);

		JLabel tableNameLabel = new JLabel (tableNameText);
		final JTextField tableNameTextField = new JTextField (tableName);
		contentPane.add(tableNameLabel);
		contentPane.add(tableNameTextField);


		JLabel mappingDirectoryLabel = new JLabel (mappingDirectoryText);
		JPanel mappingDirectoryPanel =	new JPanel(new FlowLayout());
		final JTextField mappingDirectoryTextField = new JTextField (mappingDirectory);
		JButton mappingDirectoryChoiceButton = new JButton("...");
		mappingDirectoryChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mappingDirectoryTextField.setText(GUIConfigSqlOJBXML.selectRepertoire(mappingDirectory));
			}
		});
		mappingDirectoryPanel.add(mappingDirectoryTextField);
		mappingDirectoryPanel.add(mappingDirectoryChoiceButton);
		contentPane.add(mappingDirectoryLabel);
		contentPane.add(mappingDirectoryPanel);

		JLabel mappingFileNameLabel = new JLabel (mappingFileNameText);
		final JTextField mappingFileNameTextField = new JTextField (mappingFileName);
		contentPane.add(mappingFileNameLabel);
		contentPane.add(mappingFileNameTextField);

		JPanel controlPanel =	new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

		JButton okButton = new JButton("Ok");
		okButton.setActionCommand("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableName = tableNameTextField.getText();
				javaFilePath = javaFilePathTextField.getText().replace('/','.');
				javaFilePath = javaFilePath.replace('\\','.');
				mappingDirectory = mappingDirectoryTextField.getText();
				mappingFileName = mappingFileNameTextField.getText();
				getSelectedValues();
				dialog.dispose();
			}
		});

		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedValues[0] = null; //file java path
				dialog.dispose();
			}
		});

		controlPanel.add(okButton);
		controlPanel.add(cancelButton);
		contentPane.add(controlPanel);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		return dialog;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String selectRepertoire(String javaFilePath){
		JFileChooser fileChooser = new JFileChooser(javaFilePath);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".java")||f.isDirectory();
			}
			@Override
			public String getDescription() {
				return "Java Files (*.java)";
			}
		});

		int result = fileChooser.showOpenDialog(null);
		String pathFileJava = "";


		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			String absolutePathFileJava = f.getPath();
			pathFileJava = absolutePathFileJava.substring(javaFilePath.toString().length()+1);
			System.out.println(pathFileJava);

		}

		if (pathFileJava == null || pathFileJava.equals("") || !pathFileJava.endsWith(".java")){
			JOptionPane.showMessageDialog(null,"Le type de votre fichier n'est pas valide (.java)","Fichier de type incorrect",
					JOptionPane.WARNING_MESSAGE);
			pathFileJava = "";
		}
		else {
			if (pathFileJava.startsWith("src")) pathFileJava = pathFileJava.substring(4,pathFileJava.length()-5);
			else if (pathFileJava.startsWith("data")) pathFileJava = pathFileJava.substring(5,pathFileJava.length()-5);
		}
		return pathFileJava;
	}
}