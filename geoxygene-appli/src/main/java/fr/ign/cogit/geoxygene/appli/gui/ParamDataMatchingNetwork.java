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
import javax.swing.Box;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.DataMatchingPlugin;

/**
 * 
 * @author M.-D. Van Damme
 */
public class ParamDataMatchingNetwork extends JDialog implements ActionListener {

  /** A classic logger. */
  private Logger logger = Logger.getLogger(ParamDataMatchingNetwork.class
      .getName());

  /** Origin Frame. */
  DataMatchingPlugin dataMatchingPlugin;

  /** 2 buttons : launch and cancel. */
  JButton launch = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
  JButton cancel = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));

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

    dataMatchingPlugin = dmp;

    Box boite = Box.createVerticalBox();
    setModal(true);
    setTitle(I18N.getString("DataMatchingPlugin.InputDialogTitle"));

    // First Line
    JPanel line = new JPanel();
    buttonRefShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload("1");
      }
    });

    line = new JPanel();
    line.add(new JLabel("Réseau moins détaillé : "));
    line.add(filenameRefShape);
    line.add(buttonRefShape);
    line.add(new JLabel("(.shp)"));
    boite.add(line);

    // Second line
    line = new JPanel();
    buttonCompShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload("2");
      }
    });
    line.add(new JLabel("Réseau plus détaillé : "));
    line.add(filenameCompShape);
    line.add(buttonCompShape);
    line.add(new JLabel("(.shp)"));
    boite.add(line);

    // Third line
    line = new JPanel();
    buttonParamFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload("3");
      }
    });
    line.add(new JLabel("Paramètres de l'appariement : "));
    line.add(filenameParamFile);
    line.add(buttonParamFile);
    line.add(new JLabel("(.xml)"));
    boite.add(line);

    // Buttons line
    line = new JPanel();
    line.add(launch);
    line.add(cancel);
    boite.add(line);

    add(boite);

    launch.addActionListener(this);
    cancel.addActionListener(this);
    pack();
    setLocation(400, 200);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == launch) {
      // Set 3 filenames to dataMatching plugins
      dataMatchingPlugin.setRefShapeFilename(filenameRefShape.getText());
      dataMatchingPlugin.setCompShapeFilename(filenameCompShape.getText());
      dispose();
    } else if (source == cancel) {
      // do nothing
      dispose();
    }
  }

  private void doUpload(String type) {
    JFileChooser choixFichierRefShape = new JFileChooser();
    choixFichierRefShape.setCurrentDirectory(new File(
        "D:\\Data\\Appariement\\MesTests\\T3"));

    // Crée un filtre qui n'accepte que les fichier shp ou les répertoires
    choixFichierRefShape.setFileFilter(new FileFilter() {
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

    choixFichierRefShape.setFileSelectionMode(JFileChooser.FILES_ONLY);
    choixFichierRefShape.setMultiSelectionEnabled(false);

    int returnVal = choixFichierRefShape.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      if (type.equals("1")) {
        filenameRefShape.setText(choixFichierRefShape.getSelectedFile()
            .getAbsolutePath());
      } else if (type.equals("2")) {
        filenameCompShape.setText(choixFichierRefShape.getSelectedFile()
            .getAbsolutePath());
      } else if (type.equals("3")) {
        filenameParamFile.setText(choixFichierRefShape.getSelectedFile()
            .getAbsolutePath());
      }
    }

  }
}
