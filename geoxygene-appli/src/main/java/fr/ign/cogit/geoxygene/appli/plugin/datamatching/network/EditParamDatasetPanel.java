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
package fr.ign.cogit.geoxygene.appli.plugin.datamatching.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamFilenameNetworkDataMatching;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class EditParamDatasetPanel extends JPanel {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** FileUploads Field for uploading Reference shape . */
  JButton buttonRefShape = null;
  JTextField filenameRefShape = null;

  /** FileUploads Field for uploading Comparative shape . */
  JButton buttonCompShape = null;
  JTextField filenameCompShape = null;
  
  /**
   * Constructor.
   */
  public EditParamDatasetPanel() {
    
    // Initialize all fields
    initFields();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  /**
   * 
   */
  private void initFields() {
 
    // Init ref shape button import files
    buttonRefShape = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    buttonRefShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonRefShape);
      }
    });
    
    // Init comp shape button import files
    buttonCompShape = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    buttonCompShape.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonCompShape);
      }
    });
    
    filenameRefShape = new JTextField(30);
    filenameCompShape = new JTextField(30);
  }
  
  /**
   * 
   */
  private void initPanel() {
    
    JLabel labelRef = new JLabel(
        I18N.getString("DataMatchingPlugin.NDM.LabelImportReferenceNetwork"));
    JLabel labelComp = new JLabel(
        I18N.getString("DataMatchingPlugin.NDM.LabelImportComparativeNetwork"));
    JLabel labelShpExt1 = new JLabel("(.shp)");
    JLabel labelShpExt2 = new JLabel("(.shp)");
    
    // fill:pref:grow
    FormLayout layout = new FormLayout(
        "40dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 40dlu",
        "20dlu, pref, pref, 20dlu, pref, pref, 40dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // First Line : filenameRefShape
    add(labelRef, cc.xy(2, 2));
    add(filenameRefShape, cc.xy(4, 2));
    add(buttonRefShape, cc.xy(6, 2));
    add(labelShpExt1, cc.xy(8, 2));

    // Second Line
    add(labelComp, cc.xy(2, 3));
    add(filenameCompShape, cc.xy(4, 3));
    add(buttonCompShape, cc.xy(6, 3));
    add(labelShpExt2, cc.xy(8, 3));
    
    //
    add(new JLabel("Ajouter le Recalage"), cc.xy(4, 5));
    add(new JLabel("Enregistrer les résultats"), cc.xy(4, 6));
  }
  
  /**
   * Upload file. 
   * @param typeButton 
   */
  private void doUpload(JButton typeButton) {

    JFileChooser jFileChooser = new JFileChooser();
    // FIXME : utiliser le dernier répertoire ouvert par l'interface. 
    jFileChooser.setCurrentDirectory(new File("D:\\Data\\Appariement\\ESPON-DB"));

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
      } 
    }

  } // end doUpload method
  
  /**
   * Validation of all fields.
   * @return
   */
  public ParamFilenameNetworkDataMatching valideField() {
    
    // 
    ParamFilenameNetworkDataMatching paramDataset = new ParamFilenameNetworkDataMatching();
    
    /*if (filenameRefShape.getText() != null && !filenameRefShape.getText().equals("")) {
      IPopulation<IFeature> popArcs1 = ShapefileReader.read(filenameRefShape.getText());
      popArcs1.setNom("popArcs1");
      paramDataset.addPopulationsArcs1(popArcs1);
    }
    
    if (filenameCompShape.getText() != null && !filenameCompShape.getText().equals("")) {
      IPopulation<IFeature> popArcs2 = ShapefileReader.read(filenameCompShape.getText());
      popArcs2.setNom("popArcs2");
      paramDataset.addPopulationsArcs2(popArcs2);
    }*/
    
    return paramDataset;
  }

}
