/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.DataMatchingPlugin;

/**
 * 
 * @author M.-D. Van Damme
 */
public class ParamDataMatchingNetwork extends JDialog implements ActionListener {

  /** . */
  private static final long serialVersionUID = 1L;

  /** A classic logger. */
  private Logger logger = Logger.getLogger(ParamDataMatchingNetwork.class
      .getName());

  /** Origin Frame. */
  DataMatchingPlugin dataMatchingPlugin;

  /** 2 buttons : launch and cancel. */
  JButton launchButton = new JButton(
      I18N.getString("DataMatchingPlugin.Launch"));
  JButton cancelButton = new JButton(
      I18N.getString("DataMatchingPlugin.Cancel"));

  /** FileUploads Field for uploading Reference shape . */
  JButton buttonRefShape = new JButton(
      I18N.getString("DataMatchingPlugin.Import"));
  JTextField filenameRefShape = new JTextField(50);

  /** FileUploads Field for uploading Comparative shape . */
  JButton buttonCompShape = new JButton(
      I18N.getString("DataMatchingPlugin.Import"));
  JTextField filenameCompShape = new JTextField(50);

  /** FileUploads Field for uploading Parameters data. */
  JButton buttonParamFile = new JButton(
      I18N.getString("DataMatchingPlugin.Import"));
  JTextField filenameParamFile = new JTextField(50);

  /**
   * Constructor. Initialize the JDialog.
   * @param dmp
   */
  public ParamDataMatchingNetwork(DataMatchingPlugin dmp) {

    logger.info("ParamDataMatchingNetwork Constructor");
    dataMatchingPlugin = dmp;

    setModal(true);
    setTitle(I18N.getString("DataMatchingPlugin.InputDialogTitle"));

    buttonRefShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonRefShape);
      }
    });
    buttonCompShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonCompShape);
      }
    });
    buttonParamFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonParamFile);
      }
    });

    JLabel labelRef = new JLabel(
        I18N.getString("DataMatchingPlugin.LabelImportReferenceNetwork"));
    JLabel labelComp = new JLabel(
        I18N.getString("DataMatchingPlugin.LabelImportComparativeNetwork"));
    JLabel labelParam = new JLabel(
        I18N.getString("DataMatchingPlugin.LabelImportParameter"));
    JLabel labelShpExt1 = new JLabel("(.shp)");
    JLabel labelShpExt2 = new JLabel("(.shp)");
    JLabel labelXmlExt = new JLabel("(.xml)");

    // fill:pref:grow
    FormLayout layout = new FormLayout(
        "20dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu",
        "20dlu, pref, pref, pref, 20dlu, pref, 20dlu");

    CellConstraints cc = new CellConstraints();
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);

    // First Line : filenameRefShape
    formPanel.add(labelRef, cc.xy(2, 2));
    formPanel.add(filenameRefShape, cc.xyw(4, 2, 3));
    formPanel.add(buttonRefShape, cc.xy(8, 2));
    formPanel.add(labelShpExt1, cc.xy(10, 2));

    // Second Line
    formPanel.add(labelComp, cc.xy(2, 3));
    formPanel.add(filenameCompShape, cc.xyw(4, 3, 3));
    formPanel.add(buttonCompShape, cc.xy(8, 3));
    formPanel.add(labelShpExt2, cc.xy(10, 3));

    // Third Line
    formPanel.add(labelParam, cc.xy(2, 4));
    formPanel.add(filenameParamFile, cc.xyw(4, 4, 3));
    formPanel.add(buttonParamFile, cc.xy(8, 4));
    formPanel.add(labelXmlExt, cc.xy(10, 4));

    // Fourth Line
    formPanel.add(launchButton, cc.xy(4, 6));
    formPanel.add(cancelButton, cc.xy(6, 6));

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(formPanel, BorderLayout.CENTER);

    launchButton.addActionListener(this);
    cancelButton.addActionListener(this);

    pack();
    setLocation(200, 200);
    setVisible(true);

  }

  /**
   * Actions : launch and cancel.
   */
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == launchButton) {
      // Set 3 filenames to dataMatching plugins
      dataMatchingPlugin.setRefShapeFilename(filenameRefShape.getText());
      dataMatchingPlugin.setCompShapeFilename(filenameCompShape.getText());
      dataMatchingPlugin.setParamFilename(filenameParamFile.getText());
      dispose();
    } else if (source == cancelButton) {
      // do nothing
      dispose();
    }
  }

  /**
   * Upload file. 
   * @param typeButton 
   */
  private void doUpload(JButton typeButton) {

    JFileChooser jFileChooser = new JFileChooser();
    // FIXME : utiliser le dernier répertoire ouvert par l'interface. 
    jFileChooser.setCurrentDirectory(new File(
        "D:\\Data\\Appariement\\MesTests\\T3"));

    // Crée un filtre qui n'accepte que les fichier shp ou les répertoires
    if (typeButton.equals(buttonRefShape) || typeButton.equals(buttonCompShape)) {
      jFileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return (f.isFile()
              && (f.getAbsolutePath().endsWith(".shp") || f.getAbsolutePath()
                  .endsWith(".SHP")) || f.isDirectory());
        }
        @Override
        public String getDescription() {
          return "ShapefileReader.ESRIShapefiles";
        }
      });
    } else if (typeButton.equals(buttonParamFile)) {
      // Crée un filtre qui n'accepte que les fichiers xml ou les répertoires
      jFileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return (f.isFile()
              && (f.getAbsolutePath().endsWith(".xml") || f.getAbsolutePath()
                  .endsWith(".XML")) || f.isDirectory());
        }
        @Override
        public String getDescription() {
          return "XMLFiles";
        }
      });
    }

    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jFileChooser.setMultiSelectionEnabled(false);

    // Show file dialog
    int returnVal = jFileChooser.showOpenDialog(this);
    
    // Initialize textField with the selectedFile
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      if (typeButton.equals(buttonRefShape)) {
        filenameRefShape.setText(jFileChooser.getSelectedFile()
            .getAbsolutePath());
      } else if (typeButton.equals(buttonCompShape)) {
        filenameCompShape.setText(jFileChooser.getSelectedFile()
            .getAbsolutePath());
      } else if (typeButton.equals(buttonParamFile)) {
        filenameParamFile.setText(jFileChooser.getSelectedFile()
            .getAbsolutePath());
      }
    }

  } // end doUpload method

}
