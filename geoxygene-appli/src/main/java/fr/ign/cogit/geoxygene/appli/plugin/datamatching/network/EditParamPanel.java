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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.NetworkDataMatchingPlugin;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;

/**
 * GUI to load data and parameters for launching network data matching.
 * 
 * @author M.-D. Van Damme
 */
public class EditParamPanel extends JDialog implements ActionListener {

  /** Serial version UID. */
  private static final long serialVersionUID = 1L;

  /** A classic logger. */
  private Logger logger = Logger.getLogger(EditParamPanel.class.getName());

  /** Origin Frame. */
  NetworkDataMatchingPlugin networkDataMatchingPlugin;

  /** 2 buttons : launch and cancel. */
  JButton launchButton = null;
  JButton cancelButton = null;

  /** FileUploads Field for uploading Reference shape . */
  JButton buttonRefShape = null;
  JTextField filenameRefShape = null;

  /** FileUploads Field for uploading Comparative shape . */
  JButton buttonCompShape = null;
  JTextField filenameCompShape = null;
  
  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  JPanel datasetPanel = null;
  EditParamDirectionPanel directionPanel = null;
  JPanel ecartDistancePanel = null;
  JPanel topoTreatmentPanel = null;

  /**
   * Constructor. Initialize the JDialog.
   * @param dmp
   */
  public EditParamPanel(NetworkDataMatchingPlugin dmp) {

    logger.info("ParamDataMatchingNetwork Constructor");
    networkDataMatchingPlugin = dmp;
    
    setModal(true);
    setTitle(I18N.getString("DataMatchingPlugin.InputDialogTitle"));
    
    initButtonPanel();
    initDatasetPanel();
    directionPanel = new EditParamDirectionPanel();
    initAutrePanel();
    
    // Init tabbed panel
    JTabbedPane tabbedPane = new JTabbedPane();
    
    tabbedPane.addTab("Données", datasetPanel);
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    tabbedPane.addTab("Direction", directionPanel);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
    
    tabbedPane.addTab("Ecarts de distance", ecartDistancePanel);
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
    
    tabbedPane.addTab("Traitements topologiques", topoTreatmentPanel);
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
    

    // Init param panel
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(200, 200);
    // setSize(500, 300);
    setVisible(true);
  }
  
  /**
   * 
   */
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    
    launchButton = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
    cancelButton = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
    
    launchButton.addActionListener(this);
    cancelButton.addActionListener(this);
    
    buttonPanel.setLayout(new FlowLayout (FlowLayout.CENTER)); 
    buttonPanel.add(launchButton); 
    buttonPanel.add(cancelButton);  
  }
  
  /**
   * 
   */
  private void initDatasetPanel() {
    
    datasetPanel = new JPanel();
    
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
    
    JLabel labelRef = new JLabel(
        I18N.getString("DataMatchingPlugin.NDM.LabelImportReferenceNetwork"));
    JLabel labelComp = new JLabel(
        I18N.getString("DataMatchingPlugin.NDM.LabelImportComparativeNetwork"));
    JLabel labelShpExt1 = new JLabel("(.shp)");
    JLabel labelShpExt2 = new JLabel("(.shp)");
    
    // fill:pref:grow
    FormLayout layout = new FormLayout(
        "20dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu",
        "20dlu, pref, pref, 40dlu");
    CellConstraints cc = new CellConstraints();
    datasetPanel.setLayout(layout);
    
    // First Line : filenameRefShape
    datasetPanel.add(labelRef, cc.xy(2, 2));
    datasetPanel.add(filenameRefShape, cc.xy(4, 2));
    datasetPanel.add(buttonRefShape, cc.xy(6, 2));
    datasetPanel.add(labelShpExt1, cc.xy(8, 2));

    // Second Line
    datasetPanel.add(labelComp, cc.xy(2, 3));
    datasetPanel.add(filenameCompShape, cc.xy(4, 3));
    datasetPanel.add(buttonCompShape, cc.xy(6, 3));
    datasetPanel.add(labelShpExt2, cc.xy(8, 3));
    
  }
  

  
  /**
   * 
   */
  private void initAutrePanel() {
    ecartDistancePanel = new JPanel();
    topoTreatmentPanel = new JPanel();
  }

  /**
   * Actions : launch and cancel.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    
    Object source = evt.getSource();
    
    if (source == launchButton) {
      
      // Set 2 filenames to dataMatching plugins
      networkDataMatchingPlugin.setRefShapeFilename(filenameRefShape.getText());
      networkDataMatchingPlugin.setCompShapeFilename(filenameCompShape.getText());
      
      ParamNetworkDataMatching param = new ParamNetworkDataMatching();
      ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
      
      if (directionPanel.getRbEdgeTwoWayRef().isSelected()) {
        paramDirection.setPopulationsArcsAvecOrientationDouble(true);
      } else {
        paramDirection.setPopulationsArcsAvecOrientationDouble(false);
        if (directionPanel.getRbEdgeOneWayRef().isSelected()) {
          paramDirection.setAttributOrientation1(null);
        } else if (directionPanel.getRbEdgeDefineWayRef().isSelected()) {
          paramDirection.setAttributOrientation1(directionPanel.getFieldAttributeRef().getText());
          Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
          orientationMap1.put(directionPanel.getFieldValueDirect(), 1);
          orientationMap1.put(directionPanel.getFieldValueInverse(), -1);
          orientationMap1.put(directionPanel.getFieldValueDoubleSens(), 2);
          paramDirection.setOrientationMap1(orientationMap1);
        }
      }
      param.setParamDirection(paramDirection);
      networkDataMatchingPlugin.setParam(param);
      
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
        // "D:\\Data\\Appariement\\MesTests\\T3"));
        "D:\\Data\\Appariement"));

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
  
  
}
