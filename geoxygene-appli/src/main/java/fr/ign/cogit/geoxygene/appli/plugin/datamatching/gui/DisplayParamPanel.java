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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopoTreatmentNetworkDataMatching;

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
  private ParamDirectionNetworkDataMatching paramDirection = null;
  private ParamTopoTreatmentNetworkDataMatching paramTopo = null;
  
  /**
   * Constructor.
   * @param frame
   */
  public DisplayParamPanel(ParamNetworkDataMatching paramNetworkDataMatching){
    
    /** Initialize all parameters objects. */
    this.paramNetworkDataMatching = paramNetworkDataMatching;
    paramDirection = this.paramNetworkDataMatching.getParamDirectionNetwork1();
    // paramTopo = this.paramNetworkDataMatching.getParamTopoTreatment();
    
    /** Initialize and configure panel. */
    setOrientation(1);
    setPreferredSize(new Dimension(1800, 350));
    setMaximumSize(getPreferredSize());
    setLayout(new FlowLayout(FlowLayout.LEFT));
    
    /** Diplay panel. */
    displayParam();
    
  }
  
  /**
   * Display parameter.
   */
  private void displayParam() {
    
    // Init tabbed panel
    JTabbedPane tabbedPane = new JTabbedPane();
    
    tabbedPane.addTab("Données", getDatasetParamTab());
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    tabbedPane.addTab("Direction", getDirectionParamTab());
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
    
    tabbedPane.addTab("Ecarts de distance", getDistanceParamTab());
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
    
    //tabbedPane.addTab("Traitements topologiques", getTraitementTopoParamTab());
    //tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
    
    this.add(tabbedPane);
    
  }
  
  
  /**
   * 
   * @return
   */
  private JPanel getDatasetParamTab() {
    
    JPanel formPanel = new JPanel();
    
    formPanel.setLayout(new FlowLayout (FlowLayout.LEFT));
    formPanel.add(new JLabel("<html>" + paramNetworkDataMatching.toString() + "</html>"));
    
    return formPanel;
    
    /*FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu", // colonnes
        "10dlu, pref, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    
    // Ligne 2 : PopRef
    formPanel.add(new JLabel("Pop ref : "), cc.xy(2, 3));
    formPanel.add(new JLabel(paramNetworkDataMatching.getParamDataset().getPopulationsArcs1().size() + ""), cc.xy(4, 3));
    
    // Ligne x : distanceNoeudsMax
    // Distance maximale autorisée entre deux noeuds appariés
    
    
    return formPanel;*/
  }
  
  /**
   * Direction Panel
   * @return
   */
  private JPanel getDirectionParamTab() {
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu", // colonnes
        "10dlu, pref, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Ligne 1 
    /*if (paramDirection.getPopulationsArcsAvecOrientationDouble()) {
      // Double
      JLabel labelDoubleSens = new JLabel("Tous les axes sont en double sens");
      labelDoubleSens.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 1));
      labelDoubleSens.setForeground(Color.BLUE);
      formPanel.add(labelDoubleSens, cc.xyw(2, 2, 4));
      
      // Simple
      formPanel.add(new JLabel("Tous les axes sont en simple sens"), cc.xyw(2, 3, 4));
      
      // A la demande
      formPanel.add(new JLabel("Définir le sens de l'axe suivant la valeur d'un attribut"), cc.xyw(2, 4, 4));
   
    } else if (paramDirection.getAttributOrientation1().isEmpty()) {
      // Double
      formPanel.add(new JLabel("Tous les axes sont en double sens"), cc.xyw(2, 2, 4));
      
      // Simple
      JLabel labelSimpleSens = new JLabel("Tous les axes sont en simple sens");
      labelSimpleSens.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 1));
      labelSimpleSens.setForeground(Color.BLUE);
      formPanel.add(labelSimpleSens, cc.xyw(2, 3, 4));
      
      // A la demande
      formPanel.add(new JLabel("Définir le sens de l'axe suivant la valeur d'un attribut"), cc.xyw(2, 4, 4));
    
    } else {
      // Double
      formPanel.add(new JLabel("Tous les axes sont en double sens"), cc.xyw(2, 2, 4));
      
      // Simple
      formPanel.add(new JLabel("Tous les axes sont en simple sens"), cc.xyw(2, 3, 4));
      
      // A la demande
      JLabel labelDemande = new JLabel("Définir le sens de l'axe suivant la valeur d'un attribut");
      labelDemande.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 1));
      labelDemande.setForeground(Color.BLUE);
      formPanel.add(labelDemande, cc.xyw(2, 4, 4));
    }
    
    
    formPanel.add(new JLabel("Reseau 1"), cc.xy(6, 5));
    formPanel.add(new JLabel("Reseau 2"), cc.xy(8, 5));
    
    formPanel.add(new JLabel("Nom de l'attribut : "), cc.xy(4, 6));
    formPanel.add(new JLabel(paramDirection.getAttributOrientation1()), cc.xy(6, 6));
    formPanel.add(new JLabel(paramDirection.getAttributOrientation2()), cc.xy(8, 6));
    
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(4, 7));
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(4, 8));
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(4, 9));
    
    if (paramDirection.getOrientationMap1() != null) {
      formPanel.add(new JLabel(paramDirection.getOrientationMap1().get(ParamInterface.SENS_DIRECT).toString()), cc.xy(6, 7));
      formPanel.add(new JLabel(paramDirection.getOrientationMap1().get(ParamInterface.SENS_INVERSE).toString()), cc.xy(6, 8));
      formPanel.add(new JLabel(paramDirection.getOrientationMap1().get(ParamInterface.DOUBLE_SENS).toString()), cc.xy(6, 9));
    }
    if (paramDirection.getOrientationMap2() != null) {
      formPanel.add(new JLabel(paramDirection.getOrientationMap2().get(ParamInterface.SENS_DIRECT).toString()), cc.xy(8, 7));
      formPanel.add(new JLabel(paramDirection.getOrientationMap2().get(ParamInterface.SENS_INVERSE).toString()), cc.xy(8, 8));
      formPanel.add(new JLabel(paramDirection.getOrientationMap2().get(ParamInterface.DOUBLE_SENS).toString()), cc.xy(8, 9));
    }*/
    
    return formPanel;
  }
  
  /**
   * 
   * @return
   */
  private JPanel getDistanceParamTab() {
    FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu", // colonnes
        "10dlu, pref, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    return formPanel;
  }
  
  /**
   * TRAITEMENTS TOPOLOGIQUES A L'IMPORT.
   * 
   * @return
   */
  /*private JPanel getTraitementTopoParamTab() {
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, 20dlu, pref, 20dlu", // colonnes
        "20dlu, pref, 20dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Ligne Titre
    formPanel.add(new JLabel("Reseau 1"), cc.xy(4, 2));
    formPanel.add(new JLabel("Reseau 2"), cc.xy(6, 2));
    
    // Ligne 1
    formPanel.add(new JLabel("Les noeuds proches du réseau sont fusionnés en un seul noeud : "), cc.xy(2, 4));
    if (paramTopo.getTopologieSeuilFusionNoeuds1() < 0) {
      formPanel.add(new JLabel("Non"), cc.xy(4, 4));
    } else {
      formPanel.add(new JLabel(Double.toString(paramTopo.getTopologieSeuilFusionNoeuds1())), cc.xy(4, 4));
    }
    if (paramTopo.getTopologieSeuilFusionNoeuds2() < 0) {
      formPanel.add(new JLabel("Non"), cc.xy(6, 4));
    } else {
      formPanel.add(new JLabel(Double.toString(paramTopo.getTopologieSeuilFusionNoeuds2())), cc.xy(6, 4));
    }
    
    // Ligne 2
    formPanel.add(new JLabel("<html>Doit-on eliminer pour l'appariement, les noeuds du réseau qui n'ont que 2 "
        + "arcs incidents<br/>et fusionner ces arcs ? : </html>"), cc.xy(2, 6));
    if (paramTopo.getTopologieElimineNoeudsAvecDeuxArcs1()) {
      formPanel.add(new JLabel("Oui"), cc.xy(4, 6));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(4, 6));
    }
    if (paramTopo.getTopologieElimineNoeudsAvecDeuxArcs2()) {
      formPanel.add(new JLabel("Oui"), cc.xy(6, 6));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(6, 6));
    }
    
    // Ligne 3
    formPanel.add(new JLabel("Doit-on rendre le réseau planaire ? "), cc.xy(2, 8));
    if (paramTopo.getTopologieGraphePlanaire1()) {
      formPanel.add(new JLabel("Oui"), cc.xy(4, 8));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(4, 8));
    }
    if (paramTopo.getTopologieGraphePlanaire2()) {
      formPanel.add(new JLabel("Oui"), cc.xy(6, 8));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(6, 8));
    }
    
    // Ligne 4
    formPanel.add(new JLabel("Fusion des arcs doubles : "), cc.xy(2, 10));
    if (paramTopo.getTopologieFusionArcsDoubles1()) {
      formPanel.add(new JLabel("Oui"), cc.xy(4, 10));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(4, 10));
    }
    if (paramTopo.getTopologieFusionArcsDoubles2()) {
      formPanel.add(new JLabel("Oui"), cc.xy(6, 10));
    } else {
      formPanel.add(new JLabel("Non"), cc.xy(6, 10));
    }
    
    // Ligne 5
    formPanel.add(new JLabel("<html>Les noeuds du réseau contenus dans une même surface de la population en "
        + " paramètre<br/>seront fusionés en un seul noeud pour l'appariement</html>"), cc.xy(2, 12));
    if (paramTopo.getTopologieSurfacesFusionNoeuds1() != null) {
      formPanel.add(new JLabel("Oui"), cc.xy(4, 12));
    } else {
      formPanel.add(new JLabel("Aucune surface"), cc.xy(4, 12));
    }
    if (paramTopo.getTopologieSurfacesFusionNoeuds2() != null) {
      formPanel.add(new JLabel("Oui"), cc.xy(6, 12));
    } else {
      formPanel.add(new JLabel("Aucune surface"), cc.xy(6, 12));
    }
    
    return formPanel;
  }*/

}
