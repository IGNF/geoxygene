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

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;

/**
 * 
 * 
 *
 */
public class DisplayParamPanel extends JToolBar {
  
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** Parameter values. */
  private ParamNetworkDataMatching paramNetworkDataMatching = null;
  
  /**
   * Constructor.
   * @param frame
   */
  public DisplayParamPanel(ParamNetworkDataMatching paramNetworkDataMatching){
    
    this.paramNetworkDataMatching = paramNetworkDataMatching;
    setOrientation(1);
    setPreferredSize(new Dimension(1800, 250));
    setMaximumSize(getPreferredSize());
    setLayout(new FlowLayout());
    
    displayParam();
    
  }
  
  /**
   * Display parameter.
   */
  private void displayParam() {
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, 5dlu, pref, 20dlu",   // colonnes
        "10dlu, pref, pref, 10dlu"          // lignes 
        );
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Ligne 1 : Nom de l'attribut du reseau 1
    formPanel.add(new JLabel(" : "), cc.xy(2, 2));
    formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirection().getAttributOrientation1()), cc.xy(4, 2));
    
    // Ligne 2 : PopRef
    formPanel.add(new JLabel("Pop ref : "), cc.xy(2, 3));
    formPanel.add(new JLabel(paramNetworkDataMatching.getParamDataset().getPopulationsArcs1().size() + ""), cc.xy(4, 3));
    
    // Ligne x : distanceNoeudsMax
    // Distance maximale autorisée entre deux noeuds appariés
    
    
    // Add panel
    this.add(formPanel);
    
  }

}
