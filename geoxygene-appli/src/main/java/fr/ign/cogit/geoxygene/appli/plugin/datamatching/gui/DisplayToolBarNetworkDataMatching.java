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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;

/**
 * Button 1 : see parameters
 * Button 2 : see statistics results
 * Button 3 : detail of link ?? 
 * 
 */
public class DisplayToolBarNetworkDataMatching extends JToolBar implements ActionListener {

  private static final long serialVersionUID = 4791806011051504347L;

  /** Logger. */
  static Logger logger = Logger.getLogger(DisplayToolBarNetworkDataMatching.class.getName());

  private ProjectFrame projectFrame;
  private JToggleButton btnStats;
  private JToggleButton btnLinks;
  private JToggleButton btnParam;
  
  private DisplayStatResultPanel tableauResultat = null;
  private DisplayParamPanel parameterPanel = null;
  
  private ResultNetworkDataMatching resultNetworkMatcher = null;
  private ResultNetworkStat resultNetwork = null;
  private ParamPluginNetworkDataMatching paramPlugin = null;
  private ParamNetworkDataMatching paramNetworkDataMatching = null;

  /**
   * Constructor.
   * 
   * @param projectFrame The ProjectFrame object which contains the Menu Bar.
   */
  public DisplayToolBarNetworkDataMatching(ProjectFrame projectFrame, ResultNetworkDataMatching result, 
          ParamPluginNetworkDataMatching param) {
    this.projectFrame = projectFrame;
    this.resultNetworkMatcher = result;
    this.resultNetwork = result.getResultStat();
    this.paramPlugin = param;
    
    this.paramNetworkDataMatching = paramPlugin.getParamNetworkDataMatching();
    
    init();
  }
  
  /**
   * 
   */
  private void init() {
    
    btnParam = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/wrench.png")));
    btnParam.addActionListener(this);
    btnParam.setToolTipText("Afficher les paramètres");
    add(btnParam);
    
    btnStats = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/sum.png")));
    btnStats.addActionListener(this);
    btnStats.setToolTipText("Afficher les résultats");
    add(btnStats);
    
    btnLinks = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/link.png")));
    btnLinks.setToolTipText("Bientôt disponible");
    add(btnLinks);
    
    // Enregistrer les résultats
    
    
    parameterPanel = new DisplayParamPanel(paramPlugin);
    tableauResultat = new DisplayStatResultPanel(resultNetworkMatcher);
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    if (e.getSource() == btnStats) {
      if (!btnStats.isSelected()) {
          // Hide stat results
          setStatResultToolsVisible(false);
      } else {
          // Hide and unselected others
          setParamToolsVisible(false);
          btnParam.setSelected(false);
          
          // Display the stat results
          setStatResultToolsVisible(true);
      }
    } else if (e.getSource() == btnParam) {
      if (!btnParam.isSelected()) {
        // Hide param
        setParamToolsVisible(false);
      } else {
        // Hide and unselected others
        setStatResultToolsVisible(false);
        btnStats.setSelected(false);
        // Display param
        setParamToolsVisible(true);
      }
    }
  }
  
  /**
   * 
   * @param b
   */
  public void setStatResultToolsVisible(boolean b) {
    if (b) {
      projectFrame.getContentPane().add(tableauResultat, BorderLayout.SOUTH);
      tableauResultat.setVisible(true);
      projectFrame.validate();
    } else {
      // projectFrame.getContentPane().remove(tableauResultat);
      tableauResultat.setVisible(false);
      projectFrame.validate();
    }
  }
  
  /**
   * 
   * @param b
   */
  public void setParamToolsVisible(boolean b) {
    if (b) {
      projectFrame.getContentPane().add(parameterPanel, BorderLayout.SOUTH);
      parameterPanel.setVisible(true);
      projectFrame.validate();
    } else {
      // projectFrame.getContentPane().remove(parameterPanel);
      parameterPanel.setVisible(false);
      projectFrame.validate();
    }
  }

}
