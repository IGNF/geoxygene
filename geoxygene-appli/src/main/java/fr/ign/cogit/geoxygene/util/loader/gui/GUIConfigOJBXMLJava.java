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
 * Choix des parametres pour la generation de fichiers de mapping OJB. Ces
 * parametres sont : repertoires d'accueil (XML et Java), nom du package, nom de
 * la classe mere, nom des fichiers de mapping.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

/*
 * Le pasage des parametres se fait par un tableau de String
 */

public class GUIConfigOJBXMLJava extends JFrame {

  /**
	 * 
	 */
  private static final long serialVersionUID = -3331447101762617637L;

  private static final String FRAME_TITLE = "GeOxygene Configuration mapping OJB / Java ";

  String dataDirectory;
  String mappingDirectory;
  String packageName;
  String motherClass;
  String mappingFileName;
  String motherClasseMappingFileName;

  private static String packageNameText = "Nom du package : ";
  private static String dataDirectoryText = "Repertoire d'accueil des packages : ";
  private static String mappingDirectoryText = "Repertoire d'accueil des fichiers de mapping : ";
  private static String motherClassText = "Nom de la classe mere : ";
  private static String mappingFileNameText = "Nom du fichier de mapping : ";
  private static String motherClasseMappingFileNameText = "Nom du fichier de mapping de la classe mere(*) :";

  private static String explicationText = "(*) : \nNe rien mettre si vous ne souhaitez pas faire apparaitre la classe mere dans le mapping"
      + "\nOn peut aussi indiquer le meme fichier que le fichier de mapping";

  String[] selectedValues = new String[6];

  public GUIConfigOJBXMLJava(String PackageName, String DataDirectory,
      String MappingDirectory, String MotherClass, String MappingFileName,
      String MotherClasseMappingFileName) {
    this.packageName = PackageName;
    this.dataDirectory = DataDirectory;
    this.mappingDirectory = MappingDirectory;
    this.motherClass = MotherClass;
    this.mappingFileName = MappingFileName;
    this.motherClasseMappingFileName = MotherClasseMappingFileName;
  }

  public String[] showDialog() {
    final JDialog dialog = this.createDialog(this);
    // dialog.show();
    dialog.setVisible(true);
    dialog.dispose();
    return this.selectedValues;
  }

  void getSelectedValues() {
    this.selectedValues[0] = this.dataDirectory;
    this.selectedValues[1] = this.mappingDirectory;
    this.selectedValues[2] = this.packageName;
    this.selectedValues[3] = this.motherClass;
    this.selectedValues[4] = this.mappingFileName;
    this.selectedValues[5] = this.motherClasseMappingFileName;
    if (this.selectedValues[5].trim().equals("")) {
      this.selectedValues[5] = null;
    }
  }

  private JDialog createDialog(Frame parent) {

    String title = GUIConfigOJBXMLJava.FRAME_TITLE;
    final JDialog dialog = new JDialog(parent, title, true);

    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new GridLayout(7, 2));

    JLabel dataDirectoryLabel = new JLabel(
        GUIConfigOJBXMLJava.dataDirectoryText);
    JPanel dataDirectoryPanel = new JPanel(new FlowLayout());
    final JTextField dataDirectoryTextField = new JTextField(this.dataDirectory);
    JButton dataDirectoryChoiceButton = new JButton("..."); //$NON-NLS-1$
    dataDirectoryChoiceButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dataDirectoryTextField.setText(GUIConfigOJBXMLJava.selectRepertoire());
      }
    });
    dataDirectoryPanel.add(dataDirectoryTextField);
    dataDirectoryPanel.add(dataDirectoryChoiceButton);
    contentPane.add(dataDirectoryLabel);
    contentPane.add(dataDirectoryPanel);

    JLabel mappingDirectoryLabel = new JLabel(
        GUIConfigOJBXMLJava.mappingDirectoryText);
    JPanel mappingDirectoryPanel = new JPanel(new FlowLayout());
    final JTextField mappingDirectoryTextField = new JTextField(
        this.mappingDirectory);
    JButton mappingDirectoryChoiceButton = new JButton("..."); //$NON-NLS-1$
    mappingDirectoryChoiceButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        mappingDirectoryTextField.setText(GUIConfigOJBXMLJava
            .selectRepertoire());
      }
    });
    mappingDirectoryPanel.add(mappingDirectoryTextField);
    mappingDirectoryPanel.add(mappingDirectoryChoiceButton);
    contentPane.add(mappingDirectoryLabel);
    contentPane.add(mappingDirectoryPanel);

    JLabel packageNameLabel = new JLabel(GUIConfigOJBXMLJava.packageNameText);
    final JTextField packageNameTextField = new JTextField(this.packageName);
    contentPane.add(packageNameLabel);
    contentPane.add(packageNameTextField);

    JLabel motherClassLabel = new JLabel(GUIConfigOJBXMLJava.motherClassText);
    final JTextField motherClassTextField = new JTextField(this.motherClass);
    contentPane.add(motherClassLabel);
    contentPane.add(motherClassTextField);

    JLabel mappingFileNameLabel = new JLabel(
        GUIConfigOJBXMLJava.mappingFileNameText);
    final JTextField mappingFileNameTextField = new JTextField(
        this.mappingFileName);
    contentPane.add(mappingFileNameLabel);
    contentPane.add(mappingFileNameTextField);

    JLabel motherClasseMappingFileNameLabel = new JLabel(
        GUIConfigOJBXMLJava.motherClasseMappingFileNameText);
    final JTextField motherClasseMappingFileNameTextField = new JTextField(
        this.motherClasseMappingFileName);
    contentPane.add(motherClasseMappingFileNameLabel);
    contentPane.add(motherClasseMappingFileNameTextField);

    JTextArea expl = new JTextArea(GUIConfigOJBXMLJava.explicationText);
    contentPane.add(expl);

    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    JButton okButton = new JButton("Ok");
    okButton.setActionCommand("Ok");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIConfigOJBXMLJava.this.packageName = packageNameTextField.getText();
        GUIConfigOJBXMLJava.this.dataDirectory = dataDirectoryTextField
            .getText();
        GUIConfigOJBXMLJava.this.mappingDirectory = mappingDirectoryTextField
            .getText();
        GUIConfigOJBXMLJava.this.motherClass = motherClassTextField.getText();
        GUIConfigOJBXMLJava.this.mappingFileName = mappingFileNameTextField
            .getText();
        GUIConfigOJBXMLJava.this.motherClasseMappingFileName = motherClasseMappingFileNameTextField
            .getText();
        GUIConfigOJBXMLJava.this.getSelectedValues();
        dialog.dispose();
      }
    });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIConfigOJBXMLJava.this.selectedValues[2] = null; // package name
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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  static String selectRepertoire() {
    JFileChooser choixRep = new JFileChooser();
    choixRep.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int result = choixRep.showSaveDialog(null);

    if (result == JFileChooser.CANCEL_OPTION) {
      return ""; //$NON-NLS-1$
    }

    String rep = choixRep.getSelectedFile().getPath();

    if (rep == null || rep.equals("")) { //$NON-NLS-1$
      JOptionPane.showMessageDialog(null, "nom de fichier incorrect",
          "nom de fichier incorrect", JOptionPane.ERROR_MESSAGE);
      return ""; //$NON-NLS-1$
    }
    return rep;

  }

}
