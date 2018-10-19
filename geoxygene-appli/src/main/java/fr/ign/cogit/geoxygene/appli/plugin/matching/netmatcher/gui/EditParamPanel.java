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

package fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.gui;

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
import fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.NetworkDataMatchingPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.data.ParamFilenamePopulationEdgesNetwork;
import fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;


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

  /** 2 buttons : launch, cancel. */
  private JButton launchButton = null;
  private JButton cancelButton = null;
  
  /** 1 button : export parameters in XML files. */
  private JButton exportXML = null;

  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  EditParamActionPanel actionPanel = null;
  EditParamDatasetPanel datasetPanel = null;
  EditParamDirectionPanel directionPanel = null;
  EditParamDistancePanel distancePanel = null;
  EditParamTopoPanel topoTreatmentPanel = null;
  EditParamProjectionPanel projectionPanel = null;
  EditParamVariantePanel variante = null;

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
    actionPanel = new EditParamActionPanel(this);
    
    datasetPanel = new EditParamDatasetPanel(networkDataMatchingPlugin.getParamPlugin().getParamFilenameNetwork1(),
        networkDataMatchingPlugin.getParamPlugin().getParamFilenameNetwork2());
    
    directionPanel = new EditParamDirectionPanel(networkDataMatchingPlugin.getParamPlugin().getParamNetworkDataMatching());
    
    distancePanel = new EditParamDistancePanel(networkDataMatchingPlugin.getParamPlugin().getParamNetworkDataMatching());
    
    topoTreatmentPanel = new EditParamTopoPanel(networkDataMatchingPlugin.getParamPlugin().getParamNetworkDataMatching());
    
    projectionPanel = new EditParamProjectionPanel(networkDataMatchingPlugin.getParamPlugin().getParamNetworkDataMatching());
    
    variante = new EditParamVariantePanel(networkDataMatchingPlugin.getParamPlugin().getParamNetworkDataMatching());
    
    // Init tabbed panel
    JTabbedPane tabbedPane = new JTabbedPane();
    
    tabbedPane.addTab("Appariement", actionPanel);
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    tabbedPane.addTab("Données", datasetPanel);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
    
    tabbedPane.addTab("Direction", directionPanel);
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
    
    tabbedPane.addTab("Ecarts de distance", distancePanel);
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
    
    tabbedPane.addTab("Traitements topologiques", topoTreatmentPanel);
    tabbedPane.setMnemonicAt(4, KeyEvent.VK_5);
    
    tabbedPane.addTab("Surdécoupage", projectionPanel);
    tabbedPane.setMnemonicAt(5, KeyEvent.VK_6);
    
    tabbedPane.addTab("Variantes", variante);
    tabbedPane.setMnemonicAt(6, KeyEvent.VK_7);

    // Init param panel
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(250, 250);
    setVisible(true);
  }
  
  /**
   * 
   */
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    
    launchButton = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
    cancelButton = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
    
    exportXML = new JButton("export XML");
    exportXML.setToolTipText("Export parameters in XML files");
    
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
      // Set parameters
      setParameters();
      dispose();
    } else if (source == cancelButton) {
      // Init action
      action = "CANCEL";
      // do nothing
      dispose();
    } else if (source == exportXML) {
      setParameters();
      exportXML();
    }
  
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
  
  /**
   * 
   */
  protected void setParameters() {
    
    // Initialize parameters
    ParamPluginNetworkDataMatching paramPlugin = new ParamPluginNetworkDataMatching();
    
    // ------------------------------------------------------------------------------------------
    
    // Dataset
    ParamFilenamePopulationEdgesNetwork[] tabParamFile = datasetPanel.valideField();
    paramPlugin.setParamFilenameNetwork1(tabParamFile[0]);
    paramPlugin.setParamFilenameNetwork2(tabParamFile[1]);
    
    // ------------------------------------------------------------------------------------------
    
    // Actions
    Boolean[] tabActions = actionPanel.valideField();
    paramPlugin.setDoRecalage(tabActions[0]);
    
    // ------------------------------------------------------------------------------------------

    ParametresApp paramtmp = new ParametresApp();
    ParametresApp param = new ParametresApp();
    
    // Direction
    paramtmp = directionPanel.valideField();
    param.populationsArcsAvecOrientationDouble1 = paramtmp.populationsArcsAvecOrientationDouble1;
    param.populationsArcsAvecOrientationDouble2 = paramtmp.populationsArcsAvecOrientationDouble2;
    param.attributOrientation1 = paramtmp.attributOrientation1;
    param.attributOrientation2 = paramtmp.attributOrientation2;
    param.orientationMap1 = paramtmp.orientationMap1;
    param.orientationMap2 = paramtmp.orientationMap2;
    
    // Ecart de distance
    paramtmp = distancePanel.valideField();
    param.distanceNoeudsMax = paramtmp.distanceNoeudsMax;
    param.distanceNoeudsImpassesMax = paramtmp.distanceNoeudsImpassesMax;
    param.distanceArcsMax = paramtmp.distanceArcsMax;
    param.distanceArcsMin = paramtmp.distanceArcsMin;
    
    // Topo treatment
    paramtmp = topoTreatmentPanel.valideField();
    param.topologieSeuilFusionNoeuds1 = paramtmp.topologieSeuilFusionNoeuds1;
    param.topologieSeuilFusionNoeuds2 = paramtmp.topologieSeuilFusionNoeuds2;
    param.topologieElimineNoeudsAvecDeuxArcs1 = paramtmp.topologieElimineNoeudsAvecDeuxArcs1;
    param.topologieElimineNoeudsAvecDeuxArcs2 = paramtmp.topologieElimineNoeudsAvecDeuxArcs2;
    param.topologieGraphePlanaire1 = paramtmp.topologieGraphePlanaire1;
    param.topologieGraphePlanaire2 = paramtmp.topologieGraphePlanaire2;
    param.topologieFusionArcsDoubles1 = paramtmp.topologieFusionArcsDoubles1;
    param.topologieFusionArcsDoubles2 = paramtmp.topologieFusionArcsDoubles2;
    
    // Projection
    paramtmp = projectionPanel.valideField();
    param.projeteNoeuds1SurReseau2 = paramtmp.projeteNoeuds1SurReseau2;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = paramtmp.projeteNoeuds1SurReseau2DistanceNoeudArc;
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = paramtmp.projeteNoeuds1SurReseau2DistanceProjectionNoeud;
    param.projeteNoeuds1SurReseau2ImpassesSeulement = paramtmp.projeteNoeuds1SurReseau2ImpassesSeulement;
    param.projeteNoeuds2SurReseau1 = paramtmp.projeteNoeuds2SurReseau1;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = paramtmp.projeteNoeuds2SurReseau1DistanceNoeudArc;
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = paramtmp.projeteNoeuds2SurReseau1DistanceProjectionNoeud;
    param.projeteNoeuds2SurReseau1ImpassesSeulement = paramtmp.projeteNoeuds2SurReseau1ImpassesSeulement;
    
    // Variante
    paramtmp = variante.valideField();
    param.varianteForceAppariementSimple = paramtmp.varianteForceAppariementSimple;
    param.varianteRedecoupageArcsNonApparies = paramtmp.varianteRedecoupageArcsNonApparies;
    param.varianteRedecoupageNoeudsNonApparies = paramtmp.varianteRedecoupageNoeudsNonApparies;
    param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = paramtmp.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc;
    param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = paramtmp.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud;
    param.varianteFiltrageImpassesParasites = paramtmp.varianteFiltrageImpassesParasites;
    param.varianteChercheRondsPoints = paramtmp.varianteChercheRondsPoints;
    
    //
    paramPlugin.setParamNetworkDataMatching(param);
    
    // ------------------------------------------------------------------------------------------
    
    // Set parameters values to plugin
    networkDataMatchingPlugin.setParamPlugin(paramPlugin);
    
  }

  public String getAction() {
    return action;
  }
  
  public NetworkDataMatchingPlugin getNetworkDataMatchingPlugin() {
    return networkDataMatchingPlugin;
  }
  
}
