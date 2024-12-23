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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Choix de la table dans le SGBD et du fichier Shapefile pour l'export.
 * 
 * @author Julien Perret - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class GUIExportData extends JFrame {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private static final String FRAME_TITLE = "GeOxygene Export données ESRI Shapefile";

  String shapefilePath = ""; //$NON-NLS-1$
  String tableName;
  boolean validated = false;

  private static String shapefilePathText = "Fichier ESRI Shapefile à exporter : ";
  private static String tableNameText = "Nom de la table contenant les données à exporter : ";

  private static String[] shapefileTypeText = { "ESRI Shapefiles", "shp" };

  public GUIExportData() {
  }

  public boolean showDialog() {
    final JDialog dialog = this.createDialog(this);
    dialog.setVisible(true);
    dialog.dispose();
    return this.validated;
  }

  private JDialog createDialog(Frame parent) {

    String title = GUIExportData.FRAME_TITLE;
    final JDialog dialog = new JDialog(parent, title, true);

    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new GridLayout(6, 2));

    // table SQL
    JLabel tableNameLabel = new JLabel(GUIExportData.tableNameText);
    final JTextField tableNameTextField = new JTextField(this.tableName);
    contentPane.add(tableNameLabel);
    contentPane.add(tableNameTextField);

    // shapefile
    JLabel shapefilePathLabel = new JLabel(GUIExportData.shapefilePathText);
    JPanel shapefilePathPanel = new JPanel();
    shapefilePathPanel.setLayout(new BoxLayout(shapefilePathPanel,
        BoxLayout.X_AXIS));
    final JTextField shapefilePathTextField = new JTextField(this.shapefilePath);
    JButton shapefilePathChoiceButton = new JButton("..."); //$NON-NLS-1$
    shapefilePathChoiceButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        shapefilePathTextField.setText(GUIExportData.selectFichier(null));
      }
    });
    shapefilePathPanel.add(shapefilePathTextField);
    shapefilePathPanel.add(shapefilePathChoiceButton);
    contentPane.add(shapefilePathLabel);
    contentPane.add(shapefilePathPanel);

    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    // Ok
    JButton okButton = new JButton("Ok");
    okButton.setActionCommand("Ok");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GUIExportData.this.tableName = tableNameTextField.getText();
        GUIExportData.this.shapefilePath = shapefilePathTextField.getText();
        dialog.dispose();
        GUIExportData.this.validated = true;
      }
    });

    // Cancel
    JButton cancelButton = new JButton("Annuler");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
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
  /*
   * private static String selectRepertoire(String path){ JFileChooser
   * fileChooser = new JFileChooser(path);
   * fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   * 
   * int result = fileChooser.showDialog(null, "Ok");
   * 
   * if(result == JFileChooser.CANCEL_OPTION) return "";
   * 
   * String rep = fileChooser.getSelectedFile().getPath();
   * 
   * if(rep == null || rep.equals("")){ JOptionPane.showMessageDialog(null,
   * "nom de répertoire incorrect", "nom de répertoire incorrect",
   * JOptionPane.ERROR_MESSAGE); return ""; } return rep; }
   */

  static String selectFichier(String path) {
    JFileChooser fileChooser = new JFileChooser(path);
    String[] typeText = GUIExportData.shapefileTypeText;
    FileNameExtensionFilter filter = new FileNameExtensionFilter(typeText[0],
        typeText[1]);
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showDialog(null, "Ok");

    if (result == JFileChooser.CANCEL_OPTION) {
      return ""; //$NON-NLS-1$
    }

    String file = fileChooser.getSelectedFile().getPath();

    if (path != null) {
      file = file.substring(path.toString().length() + 1);
    }

    if (file == null || file.equals("")) { //$NON-NLS-1$
      JOptionPane.showMessageDialog(null, "nom de fichier incorrect",
          "nom de fichier incorrect", JOptionPane.ERROR_MESSAGE);
      return ""; //$NON-NLS-1$
    }

    return file;
  }

  /**
   * @return shapefilePath
   */
  public String getShapefilePath() {
    return this.shapefilePath;
  }

  /**
   * @return tableName
   */
  public String getTableName() {
    return this.tableName;
  }

}
