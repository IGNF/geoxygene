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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.NetworkDataMatchingPlugin;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;


/**
 * GUI to load data and parameters for launching network data matching.
 * 
 * 
 */
public class EditParamPanel extends JDialog implements ActionListener {

  /** Serial version UID. */
  private static final long serialVersionUID = 1L;

  /** A classic logger. */
  private Logger logger = Logger.getLogger(EditParamPanel.class.getName());

  /** Origin Frame. */
  private NetworkDataMatchingPlugin networkDataMatchingPlugin;
  private String action;

  /** 4 buttons : launch, cancel, export and import XML parameters files. */
  private JButton launchButton = null;
  private JButton cancelButton = null;
  private JButton exportXML = null;

  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  EditParamDatasetPanel datasetPanel = null;
  EditParamDirectionPanel directionPanel = null;
  EditParamDistancePanel distancePanel = null;
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
    setIconImage(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/wrench.png")).getImage());
    
    initButtonPanel();
    datasetPanel = new EditParamDatasetPanel();
    directionPanel = new EditParamDirectionPanel();
    distancePanel = new EditParamDistancePanel();
    topoTreatmentPanel = new JPanel();
    
    // Init tabbed panel
    JTabbedPane tabbedPane = new JTabbedPane();
    
    tabbedPane.addTab("Données", datasetPanel);
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    tabbedPane.addTab("Direction", directionPanel);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
    
    tabbedPane.addTab("Ecarts de distance", distancePanel);
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
    
    tabbedPane.addTab("Traitements topologiques", topoTreatmentPanel);
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

    // Init param panel
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(500, 250);
    setVisible(true);
  }
  
  /**
   * 
   */
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    
    exportXML = new JButton("export XML");
    exportXML.setToolTipText("Export parameters in XML files");
    launchButton = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
    cancelButton = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
    
    launchButton.addActionListener(this);
    cancelButton.addActionListener(this);
    exportXML.addActionListener(this);
    
    buttonPanel.setLayout(new FlowLayout (FlowLayout.CENTER)); 
    buttonPanel.add(exportXML);
    buttonPanel.add(cancelButton);
    buttonPanel.add(launchButton);
    
  }
  
  /**
   * Actions : launch and cancel.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    
    Object source = evt.getSource();
    
    if (source == launchButton) {
      
      // Init action
      action = "LAUNCH";
      
      // Initialize all parameters
      // First, set default value to parameters
      // ParamPluginNetworkDataMatching paramPlugin = new ParamPluginNetworkDataMatching();
      
      // ParamNetworkDataMatching param = new ParamNetworkDataMatching();
      
      // Dataset
      // paramPlugin.setParamDataset(datasetPanel.valideField());
      
      // Direction
      // param.setParamDirectionNetwork1(directionPanel.valideField());
      
      // Ecart de distance
      // param.setParamDistance(distancePanel.valideField());
      
      // Topo treatment
      
      // Set parameters values to plugin
      // networkDataMatchingPlugin.setParamPlugin(paramPlugin);
      
      dispose();
    
    } else if (source == cancelButton) {
      // Init action
      action = "CANCEL";
      // do nothing
      dispose();
    } else if (source == exportXML) {
      exportXML();
    }
  
  }

  public String getAction() {
    return action;
  }
  
  /**
   * Display parameters in XML format for export.
   */
  private void exportXML() {
    try {
      
      ExportXMLWindow w = new ExportXMLWindow(networkDataMatchingPlugin.getParamPlugin());
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
