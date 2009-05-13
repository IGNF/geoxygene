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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Choix des parametres pour la generation de fichiers de mapping OJB.
 * Ces parametres sont : repertoires d'accueil (XML  et Java), nom du package,
 * nom de la classe mere, nom des fichiers de mapping.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

/* Le pasage des parametres se fait par un tableau de String
 */


public class GUIConfigOJBXMLJava extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3331447101762617637L;

	private static final String FRAME_TITLE = "GeOxygene Configuration mapping OJB / Java ";

	private String dataDirectory;
	private String mappingDirectory;
	private String packageName;
	private String motherClass;
	private String mappingFileName;
	private String motherClasseMappingFileName;

	private static String packageNameText = "Nom du package : ";
	private static String dataDirectoryText = "Repertoire d'accueil des packages : ";
	private static String mappingDirectoryText = "Repertoire d'accueil des fichiers de mapping : ";
	private static String motherClassText = "Nom de la classe mere : ";
	private static String mappingFileNameText = "Nom du fichier de mapping : ";
	private static String motherClasseMappingFileNameText = "Nom du fichier de mapping de la classe mere(*) :";

	private static String explicationText = "(*) : \nNe rien mettre si vous ne souhaitez pas faire apparaitre la classe mere dans le mapping"+
	"\nOn peut aussi indiquer le meme fichier que le fichier de mapping";

	private String[] selectedValues = new String[6];

	public  GUIConfigOJBXMLJava (String PackageName, String DataDirectory, String MappingDirectory,
			String MotherClass,	String MappingFileName, String MotherClasseMappingFileName) {
		packageName = PackageName;
		dataDirectory = DataDirectory;
		mappingDirectory = MappingDirectory;
		motherClass = MotherClass;
		mappingFileName = MappingFileName;
		motherClasseMappingFileName = MotherClasseMappingFileName;
	}


	public String[] showDialog() {
		final  JDialog dialog = createDialog(this);
		//dialog.show();
		dialog.setVisible(true);
		dialog.dispose();
		return selectedValues;
	}


	private void getSelectedValues() {
		selectedValues[0] = dataDirectory;
		selectedValues[1] = mappingDirectory;
		selectedValues[2] = packageName;
		selectedValues[3] = motherClass;
		selectedValues[4] = mappingFileName;
		selectedValues[5] = motherClasseMappingFileName;
		if (selectedValues[5].trim().equals(""))selectedValues[5] = null;
	}


	private JDialog createDialog (Frame parent) {

		String title = FRAME_TITLE;
		final JDialog dialog = new JDialog(parent, title, true);

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new GridLayout(7,2));


		JLabel dataDirectoryLabel = new JLabel (dataDirectoryText);
		JPanel dataDirectoryPanel =	new JPanel(new FlowLayout());
		final JTextField dataDirectoryTextField = new JTextField (dataDirectory);
		JButton dataDirectoryChoiceButton = new JButton("...");
		dataDirectoryChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataDirectoryTextField.setText(GUIConfigOJBXMLJava.selectRepertoire());
			}
		});
		dataDirectoryPanel.add(dataDirectoryTextField);
		dataDirectoryPanel.add(dataDirectoryChoiceButton);
		contentPane.add(dataDirectoryLabel);
		contentPane.add(dataDirectoryPanel);


		JLabel mappingDirectoryLabel = new JLabel (mappingDirectoryText);
		JPanel mappingDirectoryPanel =	new JPanel(new FlowLayout());
		final JTextField mappingDirectoryTextField = new JTextField (mappingDirectory);
		JButton mappingDirectoryChoiceButton = new JButton("...");
		mappingDirectoryChoiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mappingDirectoryTextField.setText(GUIConfigOJBXMLJava.selectRepertoire());
			}
		});
		mappingDirectoryPanel.add(mappingDirectoryTextField);
		mappingDirectoryPanel.add(mappingDirectoryChoiceButton);
		contentPane.add(mappingDirectoryLabel);
		contentPane.add(mappingDirectoryPanel);

		JLabel packageNameLabel = new JLabel (packageNameText);
		final JTextField packageNameTextField = new JTextField (packageName);
		contentPane.add(packageNameLabel);
		contentPane.add(packageNameTextField);


		JLabel motherClassLabel = new JLabel (motherClassText);
		final JTextField motherClassTextField = new JTextField (motherClass);
		contentPane.add(motherClassLabel);
		contentPane.add(motherClassTextField);

		JLabel mappingFileNameLabel = new JLabel (mappingFileNameText);
		final JTextField mappingFileNameTextField = new JTextField (mappingFileName);
		contentPane.add(mappingFileNameLabel);
		contentPane.add(mappingFileNameTextField);

		JLabel motherClasseMappingFileNameLabel = new JLabel (motherClasseMappingFileNameText);
		final JTextField motherClasseMappingFileNameTextField = new JTextField (motherClasseMappingFileName);
		contentPane.add(motherClasseMappingFileNameLabel);
		contentPane.add(motherClasseMappingFileNameTextField);

		JTextArea expl = new JTextArea(explicationText);
		contentPane.add(expl);


		JPanel controlPanel =	new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

		JButton okButton = new JButton("Ok");
		okButton.setActionCommand("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				packageName = packageNameTextField.getText();
				dataDirectory = dataDirectoryTextField.getText();
				mappingDirectory = mappingDirectoryTextField.getText();
				motherClass = motherClassTextField.getText();
				mappingFileName = mappingFileNameTextField.getText();
				motherClasseMappingFileName = motherClasseMappingFileNameTextField.getText();
				getSelectedValues();
				dialog.dispose();
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedValues[2] = null; //package name
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
	private static String selectRepertoire(){
		JFileChooser choixRep = new JFileChooser();
		choixRep.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = choixRep.showSaveDialog(null);

		if(result == JFileChooser.CANCEL_OPTION)
			return "";

		String rep = choixRep.getSelectedFile().getPath();

		if(rep == null || rep.equals("")){
			JOptionPane.showMessageDialog(null,
					"nom de fichier incorrect",
					"nom de fichier incorrect",
					JOptionPane.ERROR_MESSAGE);
			return "";
		}
		else
			return rep;

	}




}
