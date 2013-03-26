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


import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElement;

/**
 * 
 * 
 *  
 */
public class DisplayStatResultPanel extends JToolBar  {
  
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** */
  private ResultNetworkStat resultNetwork;
  
  /**
   * Constructor.
   * @param res
   */
  public DisplayStatResultPanel(ResultNetworkStat res){
    
    resultNetwork = res;
    setOrientation(1);
    setPreferredSize(new Dimension(1800, 250));
    setMaximumSize(getPreferredSize());
    setLayout(new FlowLayout());
    
    displayResult();
    
  }
  
  /**
   * Display stat result.
   */
  private void displayResult() {
    
    // JLabel labelShpExt1 = new JLabel("Coucou : " + this.resultNetwork.getEdgesEvaluationComp().getTotalNetworkElementNumber());
    // this.add(labelShpExt1);
    
    FormLayout layout = new FormLayout(
        "20dlu, 20dlu, pref, 5dlu, pref, 40dlu, 20dlu, pref, 5dlu, pref, 20dlu", // colonnes
        "10dlu, pref, 5dlu, pref, pref, pref, pref, 5dlu, pref, pref, pref, pref, 10dlu" // lignes 
        );
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Element Network 
    ResultNetworkStatElement edgesRef = resultNetwork.getEdgesStatNetwork1();
    ResultNetworkStatElement nodesRef = resultNetwork.getNodesEvaluationRef();
    ResultNetworkStatElement edgesComp = resultNetwork.getNodesEvaluationComp();
    ResultNetworkStatElement nodesComp = resultNetwork.getNodesEvaluationComp();
    
    // --------
    
    // Ligne 1 : 2 titres
    formPanel.add(new JLabel("Réseau le moins détaillé"), cc.xyw(2, 2, 4));
    formPanel.add(new JLabel("Réseau de comparaison"), cc.xyw(7, 2, 4));
    
    // --------
    
    // Ligne 2 : totaux des arcs
    formPanel.add(new JLabel("Nombre d'arcs : "), cc.xyw(2, 4, 2));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getTotalNetworkElementNumber())), cc.xy(5, 4));
    formPanel.add(new JLabel("Nombre d'arcs : "), cc.xyw(7, 4, 2));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getTotalNetworkElementNumber())), cc.xy(10, 4));
    
    // Ligne 3 : arcs jugés OK
    formPanel.add(new JLabel("Arcs appariés et jugés OK : "), cc.xy(3, 5));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getCorrectMatchingNetworkElementNumber())), cc.xy(5, 5));
    formPanel.add(new JLabel("Arcs appariés et jugés OK : "), cc.xy(8, 5));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getCorrectMatchingNetworkElementNumber())), cc.xy(10, 5));
    
    // Ligne 4 : arcs douteux
    formPanel.add(new JLabel("Arcs appariés et jugés douteux : "), cc.xy(3, 6));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getDoubtfulNetworkElementNumber())), cc.xy(5, 6));
    formPanel.add(new JLabel("Arcs appariés et jugés douteux : "), cc.xy(8, 6));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getDoubtfulNetworkElementNumber())), cc.xy(10, 6));
    
    // Ligne 5 : arcs non appariés
    formPanel.add(new JLabel("Arcs non appariés : "), cc.xy(3, 7));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getNoMatchingNetworkElementNumber())), cc.xy(5, 7));
    formPanel.add(new JLabel("Arcs non appariés : "), cc.xy(8, 7));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getNoMatchingNetworkElementNumber())), cc.xy(10, 7));
    
    // --------
    
    // Ligne 6 : totaux des noeuds
    formPanel.add(new JLabel("Nombre de noeuds : "), cc.xyw(2, 9, 2));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getTotalNetworkElementNumber())), cc.xy(5, 9));
    formPanel.add(new JLabel("Nombre de noeuds : "), cc.xyw(7, 9, 2));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getTotalNetworkElementNumber())), cc.xy(10, 9));
    
    // Ligne 7 : arcs jugés OK
    formPanel.add(new JLabel("Noeuds appariés et jugés OK : "), cc.xy(3, 10));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getCorrectMatchingNetworkElementNumber())), cc.xy(5, 10));
    formPanel.add(new JLabel("Noeuds appariés et jugés OK : "), cc.xy(8, 10));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getCorrectMatchingNetworkElementNumber())), cc.xy(10, 10));
    
    // Ligne 8 : arcs douteux
    formPanel.add(new JLabel("Noeuds appariés et jugés douteux : "), cc.xy(3, 11));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getDoubtfulNetworkElementNumber())), cc.xy(5, 11));
    formPanel.add(new JLabel("Noeuds appariés et jugés douteux : "), cc.xy(8, 11));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getDoubtfulNetworkElementNumber())), cc.xy(10, 11));
    
    // Ligne 9 : arcs non appariés
    formPanel.add(new JLabel("Noeuds non appariés : "), cc.xy(3, 12));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getNoMatchingNetworkElementNumber())), cc.xy(5, 12));
    formPanel.add(new JLabel("Noeuds non appariés : "), cc.xy(8, 12));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getNoMatchingNetworkElementNumber())), cc.xy(10, 12));
    
    // Add panel
    this.add(formPanel);
  }
  
  
}
