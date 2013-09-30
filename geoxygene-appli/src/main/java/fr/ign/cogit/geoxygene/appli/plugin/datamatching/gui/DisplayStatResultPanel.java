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
import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultCarteTopoStatElementInterface;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElement;

/**
 * 
 * 
 *  
 */
public class DisplayStatResultPanel extends JInternalFrame  {
  
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  private JPanel formPanel;
  
  /** */
  private ResultNetworkDataMatching resultNetworkMatcher;
  private ResultNetworkStat resultNetwork;
  
  /** . */
  private final static Color network1Color = new Color(11, 73, 157);
  private final static Color network2Color = new Color(145, 100, 10);
  
  /**
   * Constructor.
   * @param res
   */
  public DisplayStatResultPanel(ResultNetworkDataMatching res) {
      
      super("Résultat.", true, true, true, true);
      
      setToolTipText(this.getTitle());
      getDesktopIcon().setToolTipText(this.getTitle());
      setFrameIcon(new ImageIcon(
              DisplayStatResultPanel.class.getResource("/images/icons/sum.png")));
      setLocation(400, 0);
      setSize(400, 400);
      
      getContentPane().setLayout(new BorderLayout());
    
      resultNetworkMatcher = res;
      resultNetwork = res.getResultStat();
      // setOrientation(1);
      // setPreferredSize(new Dimension(1800, 350));
      // setMaximumSize(getPreferredSize());
      // setLayout(new FlowLayout());
    
      displayResult();
      
      getContentPane().add(formPanel, BorderLayout.CENTER);
    
      pack();
      setVisible(true);
    
  }
  
  /**
   * Display stat result.
   */
  private void displayResult() {
    
    FormLayout layout = new FormLayout(
        "20dlu, 20dlu, pref, 5dlu, pref, 5dlu, 20dlu, 30dlu, 20dlu, 20dlu, pref, 5dlu, pref, 5dlu, 20dlu, 30dlu, 20dlu", // colonnes
        "20dlu, pref, 5dlu, pref, pref, pref, pref, 5dlu, pref, pref, pref, pref, 20dlu, pref, 10dlu"  // lignes 
        );
    CellConstraints cc = new CellConstraints();
    
    formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Element Network 
    ResultNetworkStatElement edgesRef = resultNetwork.getStatsEdgesOfNetwork1();
    ResultNetworkStatElement nodesRef = resultNetwork.getStatsNodesOfNetwork1();
    ResultNetworkStatElement edgesComp = resultNetwork.getStatsEdgesOfNetwork2();
    ResultNetworkStatElement nodesComp = resultNetwork.getStatsNodesOfNetwork2();
    
    // --------
    
    // Ligne 1 : titles
    JLabel network1Label = new JLabel("Network 1");
    network1Label.setForeground(network1Color);
    //network1Label.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 1));
    formPanel.add(network1Label, cc.xyw(2, 2, 4));
    JLabel network2Label = new JLabel("Network 2");
    //network2Label.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 1));
    network2Label.setForeground(network2Color);
    formPanel.add(network2Label, cc.xyw(10, 2, 4));
    
    // --------
    DecimalFormat df = new DecimalFormat("0");
    double res1TotalNb = edgesRef.getTotalNetworkElementNumber();
    double res1TotalLg = edgesRef.getTotalNetworkElementLength();
    double res1TotalNbOk = edgesRef.getCorrectMatchingNetworkElementNumber();
    double res1TotalLgOk = edgesRef.getCorrectedMatchingNetworkElementLength();
    double res1TotalNbDouteux = edgesRef.getDoubtfulNetworkElementNumber();
    double res1TotalLgDouteux = edgesRef.getDoubtfulNetworkElementLength();
    double res1TotalNbNull = edgesRef.getNoMatchingNetworkElementNumber();
    double res1TotalLgNull = edgesRef.getNoMatchingNetworkElementLength();
    
    double res2TotalNb = edgesComp.getTotalNetworkElementNumber();
    double res2TotalLg = edgesComp.getTotalNetworkElementLength();
    double res2TotalNbOk = edgesComp.getCorrectMatchingNetworkElementNumber();
    double res2TotalLgOk = edgesComp.getCorrectedMatchingNetworkElementLength();
    double res2TotalNbDouteux = edgesComp.getDoubtfulNetworkElementNumber();
    double res2TotalLgDouteux = edgesComp.getDoubtfulNetworkElementLength();
    double res2TotalNbNull = edgesComp.getNoMatchingNetworkElementNumber();
    double res2TotalLgNull = edgesComp.getNoMatchingNetworkElementLength();
    
    // Ligne 2 : totaux des arcs
    formPanel.add(new JLabel("Nombre d'arcs : "), cc.xyw(2, 4, 2));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getTotalNetworkElementNumber())), cc.xy(5, 4));
    formPanel.add(new JLabel("Nombre d'arcs : "), cc.xyw(10, 4, 2));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getTotalNetworkElementNumber())), cc.xy(13, 4));
    
    // Ligne 3 : arcs jugés OK
    formPanel.add(new JLabel("Arcs appariés et jugés OK : "), cc.xy(3, 5));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getCorrectMatchingNetworkElementNumber())), cc.xy(5, 5));
    double percentNbOk = (res1TotalNbOk * 100 / res1TotalNb);
    double percentLgOk = (res1TotalLgOk * 100 / res1TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbOk) + "%, "), cc.xy(7, 5));
    formPanel.add(new JLabel(df.format(percentLgOk) + "% long)"), cc.xy(8, 5));
    
    formPanel.add(new JLabel("Arcs1 appariés et jugés OK : "), cc.xy(11, 5));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getCorrectMatchingNetworkElementNumber())), cc.xy(13, 5));
    percentNbOk = (res2TotalNbOk * 100 / res2TotalNb);
    percentLgOk = (res2TotalLgOk * 100 / res2TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbOk) + "%, "), cc.xy(15, 5));
    formPanel.add(new JLabel(df.format(percentLgOk) + "% long)"), cc.xy(16, 5));
    
    // Ligne 4 : arcs douteux
    formPanel.add(new JLabel("Arcs appariés et jugés douteux : "), cc.xy(3, 6));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getDoubtfulNetworkElementNumber())), cc.xy(5, 6));
    double percentNbDouteux = (res1TotalNbDouteux * 100 / res1TotalNb);
    double percentLgDouteux = (res1TotalLgDouteux * 100 / res1TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbDouteux) + "%, "), cc.xy(7, 6));
    formPanel.add(new JLabel(df.format(percentLgDouteux) + "% long)"), cc.xy(8, 6));
    
    formPanel.add(new JLabel("Arcs appariés et jugés douteux : "), cc.xy(11, 6));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getDoubtfulNetworkElementNumber())), cc.xy(13, 6));
    percentNbDouteux = (res2TotalNbDouteux * 100 / res2TotalNb);
    percentLgDouteux = (res2TotalLgDouteux * 100 / res2TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbDouteux) + "%, "), cc.xy(15, 6));
    formPanel.add(new JLabel(df.format(percentLgDouteux) + "% long)"), cc.xy(16, 6));
    
    // Ligne 5 : arcs non appariés
    formPanel.add(new JLabel("Arcs non appariés : "), cc.xy(3, 7));
    formPanel.add(new JLabel(Integer.toString(edgesRef.getNoMatchingNetworkElementNumber())), cc.xy(5, 7));
    double percentNbNon = (res1TotalNbNull * 100 / res1TotalNb);
    double percentLgNon = (res1TotalLgNull * 100 / res1TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbNon) + "%, "), cc.xy(7, 7));
    formPanel.add(new JLabel(df.format(percentLgNon) + "% long)"), cc.xy(8, 7));
    
    formPanel.add(new JLabel("Arcs non appariés : "), cc.xy(11, 7));
    formPanel.add(new JLabel(Integer.toString(edgesComp.getNoMatchingNetworkElementNumber())), cc.xy(13, 7));
    percentNbNon = (res2TotalNbNull * 100 / res2TotalNb);
    percentLgNon = (res2TotalLgNull * 100 / res2TotalLg);
    formPanel.add(new JLabel("(" + df.format(percentNbNon) + "%, "), cc.xy(15, 7));
    formPanel.add(new JLabel(df.format(percentLgNon) + "% long)"), cc.xy(16, 7));
    
    // --------
    res1TotalNb = nodesRef.getTotalNetworkElementNumber();
    res1TotalNbOk = nodesRef.getCorrectMatchingNetworkElementNumber();
    res1TotalNbDouteux = nodesRef.getDoubtfulNetworkElementNumber();
    res1TotalNbNull = nodesRef.getNoMatchingNetworkElementNumber();
    
    res2TotalNb = nodesComp.getTotalNetworkElementNumber();
    res2TotalNbOk = nodesComp.getCorrectMatchingNetworkElementNumber();
    res2TotalNbDouteux = nodesComp.getDoubtfulNetworkElementNumber();
    res2TotalNbNull = nodesComp.getNoMatchingNetworkElementNumber();
    
    // Ligne 6 : totaux des noeuds
    formPanel.add(new JLabel("Nombre de noeuds : "), cc.xyw(2, 9, 2));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getTotalNetworkElementNumber())), cc.xy(5, 9));
    formPanel.add(new JLabel("Nombre de noeuds : "), cc.xyw(10, 9, 2));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getTotalNetworkElementNumber())), cc.xy(13, 9));
    
    // Ligne 7 : arcs jugés OK
    formPanel.add(new JLabel("Noeuds appariés et jugés OK : "), cc.xy(3, 10));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getCorrectMatchingNetworkElementNumber())), cc.xy(5, 10));
    percentNbOk = (res1TotalNbOk * 100 / res1TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbOk) + "%)"), cc.xy(7, 10));
    
    formPanel.add(new JLabel("Noeuds appariés et jugés OK : "), cc.xy(11, 10));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getCorrectMatchingNetworkElementNumber())), cc.xy(13, 10));
    percentNbOk = (res2TotalNbOk * 100 / res2TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbOk) + "%)"), cc.xy(15, 10));
    
    // Ligne 8 : arcs douteux
    formPanel.add(new JLabel("Noeuds appariés et jugés douteux : "), cc.xy(3, 11));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getDoubtfulNetworkElementNumber())), cc.xy(5, 11));
    percentNbDouteux = (res1TotalNbDouteux * 100 / res1TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbDouteux) + "%)"), cc.xy(7, 11));
    
    formPanel.add(new JLabel("Noeuds appariés et jugés douteux : "), cc.xy(11, 11));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getDoubtfulNetworkElementNumber())), cc.xy(13, 11));
    percentNbDouteux = (res2TotalNbDouteux * 100 / res2TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbDouteux) + "%)"), cc.xy(15, 11));
    
    // Ligne 9 : arcs non appariés
    formPanel.add(new JLabel("Noeuds non appariés : "), cc.xy(3, 12));
    formPanel.add(new JLabel(Integer.toString(nodesRef.getNoMatchingNetworkElementNumber())), cc.xy(5, 12));
    percentNbNon = (res1TotalNbNull * 100 / res1TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbNon) + "%)"), cc.xy(7, 12));
    
    formPanel.add(new JLabel("Noeuds non appariés : "), cc.xy(11, 12));
    formPanel.add(new JLabel(Integer.toString(nodesComp.getNoMatchingNetworkElementNumber())), cc.xy(13, 12));
    percentNbNon = (res2TotalNbNull * 100 / res2TotalNb);
    formPanel.add(new JLabel("(" + df.format(percentNbNon) + "%)"), cc.xy(15, 12));
    

    // Cartes TOPO
    formPanel.add(new JLabel("Nb d'arcs bruts : "), cc.xy(3, 14));
    formPanel.add(new JLabel(Integer.toString(resultNetworkMatcher.getReseauStat1()
            .getResultCarteTopoEdge().getNbElementForType(ResultCarteTopoStatElementInterface.NB_BRUTS))), cc.xy(5, 14));
    
    // Add panel
    this.add(formPanel);
  
  }
  
  
}
