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
package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamFilenamePopulationEdgesNetwork;

/**
 * 
 * 
 *
 */
public class EditParamDatasetPanel extends JPanel {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  private ParamFilenamePopulationEdgesNetwork paramFilename1;
  private ParamFilenamePopulationEdgesNetwork paramFilename2;
  
  /** FileUploads Field for uploading Reference shape . */
  JButton buttonEdgesShape1 = null;
  JComboBox listShapefileEdgesNetwork1 = null;

  /** FileUploads Field for uploading Comparative shape . */
  JButton buttonEdgesShape2 = null;
  JComboBox listShapefileEdgesNetwork2 = null;
  
  /**
   * Constructor.
   */
  public EditParamDatasetPanel(ParamFilenamePopulationEdgesNetwork f1, ParamFilenamePopulationEdgesNetwork f2) {
    
    paramFilename1 = f1;
    paramFilename2 = f2;
    
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
    buttonEdgesShape1 = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    buttonEdgesShape1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonEdgesShape1);
      }
    });
    
    // Init comp shape button import files
    buttonEdgesShape2 = new JButton(I18N.getString("DataMatchingPlugin.Import"));
    buttonEdgesShape2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doUpload(buttonEdgesShape2);
      }
    });
    
    listShapefileEdgesNetwork1 = new JComboBox();
    for (int i = 0; i < paramFilename1.getListNomFichiersPopArcs().size(); i++) {
      listShapefileEdgesNetwork1.addItem(paramFilename1.getListNomFichiersPopArcs().get(i));
    }
    
    listShapefileEdgesNetwork2 = new JComboBox();
    for (int i = 0; i < paramFilename2.getListNomFichiersPopArcs().size(); i++) {
      listShapefileEdgesNetwork2.addItem(paramFilename2.getListNomFichiersPopArcs().get(i));
    }
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
        "20dlu, pref, 20dlu, pref, 20dlu, pref, pref, 40dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // First Line : filenameRefShape
    add(labelRef, cc.xy(2, 2));
    add(listShapefileEdgesNetwork1, cc.xy(4, 2));
    add(buttonEdgesShape1, cc.xy(6, 2));
    add(labelShpExt1, cc.xy(8, 2));

    // Second Line
    add(labelComp, cc.xy(2, 4));
    add(listShapefileEdgesNetwork2, cc.xy(4, 4));
    add(buttonEdgesShape2, cc.xy(6, 4));
    add(labelShpExt2, cc.xy(8, 4));
    
  }
  
  /**
   * Upload file. 
   * @param typeButton 
   */
  private void doUpload(JButton typeButton) {

    JFileChooser jFileChooser = new JFileChooser();
    // TODO : utiliser le dernier répertoire ouvert par l'interface. 
    jFileChooser.setCurrentDirectory(new File(paramFilename1.getListNomFichiersPopArcs().get(0)));
    
    // Crée un filtre qui n'accepte que les fichier shp ou les répertoires
    if (typeButton.equals(buttonEdgesShape1) || typeButton.equals(buttonEdgesShape2)) {
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
    
    // Multiple selection
    jFileChooser.setMultiSelectionEnabled(true);

    // Show file dialog
    // int returnVal = jFileChooser.showOpenDialog(this);
    Frame window=new Frame();
    window.setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/icons/16x16/page_white_add.png")).getImage());
    int returnVal = jFileChooser.showOpenDialog(window);
    
    // Initialize textField with the selectedFile
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      
      if (typeButton.equals(buttonEdgesShape1)) {
        
        listShapefileEdgesNetwork1.removeAllItems();
        for (int i = 0; i < jFileChooser.getSelectedFiles().length; i++) {
          File f = jFileChooser.getSelectedFiles()[i];
          listShapefileEdgesNetwork1.addItem(f.getAbsolutePath());
        }
        
      } else if (typeButton.equals(buttonEdgesShape2)) {
        
        listShapefileEdgesNetwork2.removeAllItems();
        for (int i = 0; i < jFileChooser.getSelectedFiles().length; i++) {
          File f = jFileChooser.getSelectedFiles()[i];
          listShapefileEdgesNetwork2.addItem(f.getAbsolutePath());
        }
        
      } 
    }

  } // end doUpload method
  
  /**
   * Validation of all fields.
   * @return
   */
  public ParamFilenamePopulationEdgesNetwork[] valideField() {
    
    // 
    ParamFilenamePopulationEdgesNetwork[] tabParamFilename = new ParamFilenamePopulationEdgesNetwork[2];
    ParamFilenamePopulationEdgesNetwork paramDataset = new ParamFilenamePopulationEdgesNetwork();

    int count = listShapefileEdgesNetwork1.getItemCount();
    for (int i = 0; i < count; i++) {
      String f = listShapefileEdgesNetwork1.getItemAt(i).toString();
      paramDataset.addFilename(f);
    }
    tabParamFilename[0] = paramDataset;
    
    paramDataset = new ParamFilenamePopulationEdgesNetwork();
    count = listShapefileEdgesNetwork2.getItemCount();
    for (int i = 0; i < count; i++) {
      String f = listShapefileEdgesNetwork2.getItemAt(i).toString();
      paramDataset.addFilename(f);
    }
    tabParamFilename[1] = paramDataset;
    
    return tabParamFilename;
  }

}
