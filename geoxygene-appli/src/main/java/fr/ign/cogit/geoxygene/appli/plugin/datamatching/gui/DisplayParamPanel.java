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
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;

/**
 * 
 * //formPanel.setLayout(new FlowLayout (FlowLayout.LEFT));
 * //formPanel.add(new JLabel("<html>" + paramNetworkDataMatching.toString() + "</html>"));
 *
 */
public class DisplayParamPanel extends JInternalFrame {
  
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  JTabbedPane tabbedPane;
  
  /** Parameter values. */
  private ParamPluginNetworkDataMatching paramPlugin = null;
  private ParamNetworkDataMatching paramNetworkDataMatching = null; 
  // private ParamDirectionNetworkDataMatching paramDirection = null;
  // private ParamTopologyTreatmentNetwork paramTopo = null;
  
  /**
   * Constructor.
   * @param frame
   */
  public DisplayParamPanel(ParamPluginNetworkDataMatching paramPlugin) {
      
      super("Paramètres.", true, true, true, true);
      
      setToolTipText(this.getTitle());
      getDesktopIcon().setToolTipText(this.getTitle());
      setLocation(0, 0);
      setSize(400, 400);
      setFrameIcon(new ImageIcon(
              DisplayParamPanel.class.getResource("/images/icons/wrench.png")));
      
      getContentPane().setLayout(new BorderLayout());
    
      /** Initialize all parameters objects. */
      this.paramPlugin = paramPlugin;
      paramNetworkDataMatching = paramPlugin.getParamNetworkDataMatching();
      // paramDirection = this.paramNetworkDataMatching.getParamDirectionNetwork1();
      // paramTopo = this.paramNetworkDataMatching.getParamTopoTreatment();
    
      /** Initialize and configure panel. */
      // setOrientation(1);
      // setPreferredSize(new Dimension(1800, 350));
      // setMaximumSize(getPreferredSize());
      // setLayout(new FlowLayout(FlowLayout.LEFT));
    
      /** Diplay panel. */
      displayParam();
    
      getContentPane().add(tabbedPane, BorderLayout.CENTER);
    
      pack();
      setVisible(true);
    
  }
  
  /**
   * Display parameter.
   */
  private void displayParam() {
    
    // Init tabbed panel
    tabbedPane = new JTabbedPane();
    
    tabbedPane.addTab("Données", getDatasetParamTab());
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    tabbedPane.addTab("Direction", getDirectionParamTab());
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
    
    tabbedPane.addTab("Ecarts de distance", getDistanceParamTab());
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
    
    tabbedPane.addTab("Traitements topologiques", getTraitementTopoParamTab());
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
    
    tabbedPane.addTab("Surdécoupage", getSurdecoupageParamTab());
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
    
    
    this.add(tabbedPane);
    
  }
  
  
  /**
   * 
   * @return
   */
  private JPanel getDatasetParamTab() {
    
    FormLayout layout = new FormLayout(
            "20dlu, pref, 5dlu, pref, pref, 20dlu, pref, pref, 20dlu", // colonnes
            "10dlu, pref, 10dlu, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
              
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
        
    formPanel.add(new JLabel("Reseau 1"), cc.xy(5, 2));
    formPanel.add(new JLabel("Reseau 2"), cc.xy(8, 2));
        
    formPanel.add(new JLabel("Arc(s) : "), cc.xy(2, 6));
    
    String edgeNetwork1 = "<ul>";
    for (int i = 0; i < paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().size(); i++) {
        edgeNetwork1 = edgeNetwork1 + "<li>"
                + paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().get(i)
                + "</li>";
    }
    edgeNetwork1 = edgeNetwork1 + "</ul>";
    formPanel.add(new JLabel("<html>" + edgeNetwork1 + "</html>"), cc.xy(5,  6));
    
    String edgeNetwork2 = "<ul>";
    for (int i = 0; i < paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().size(); i++) {
        edgeNetwork2 = edgeNetwork2 + "<li>"
                + paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().get(i)
                + "</li>";
    }
    edgeNetwork2 = edgeNetwork2 + "</ul>";
    formPanel.add(new JLabel("<html>" + edgeNetwork2 + "</html>"), cc.xy(8,  6));
    
    
    formPanel.add(new JLabel("Noeud(s) : "), cc.xy(2, 8));
    
    return formPanel;
    
  }
  
  /**
   * Direction Panel
   * @return
   */
  private JPanel getDirectionParamTab() {
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, 5dlu, pref, pref, 20dlu, pref, pref, 20dlu", // colonnes
        "10dlu, pref, 10dlu, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
          
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    formPanel.add(new JLabel("Reseau 1"), cc.xy(5, 2));
    formPanel.add(new JLabel("Reseau 2"), cc.xy(8, 2));
    
    formPanel.add(new JLabel("Nom de l'attribut : "), cc.xy(2, 6));
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(2, 7));
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(2, 8));
    formPanel.add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(2, 9));
    
    // Line 2 : double sens
    if (paramNetworkDataMatching.getParamDirectionNetwork1().getOrientationDouble()) {
        formPanel.add(new JLabel("Tous les axes sont en double sens"), cc.xyw(4, 4, 3));
    } else {
        formPanel.add(new JLabel("Définir le sens de l'axe suivant la valeur d'un attribut"), cc.xyw(4, 5, 3));
        
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork1().getAttributOrientation()), cc.xy(5, 6));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork1()
                .getOrientationMap().get(OrientationInterface.SENS_DIRECT)), cc.xy(5, 7));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork1()
                .getOrientationMap().get(OrientationInterface.SENS_INVERSE)), cc.xy(5, 8));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork1()
                .getOrientationMap().get(OrientationInterface.DOUBLE_SENS)), cc.xy(5, 9));
    }
    if (paramNetworkDataMatching.getParamDirectionNetwork2().getOrientationDouble()) {
        formPanel.add(new JLabel("Tous les axes sont en double sens"), cc.xyw(7, 4, 3));
    } else {
        formPanel.add(new JLabel("Définir le sens de l'axe suivant la valeur d'un attribut"), cc.xyw(7, 5, 3));
        
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork2().getAttributOrientation()), cc.xy(8, 6));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork2()
                .getOrientationMap().get(OrientationInterface.SENS_DIRECT)), cc.xy(8, 7));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork2()
                .getOrientationMap().get(OrientationInterface.SENS_INVERSE)), cc.xy(8, 8));
        formPanel.add(new JLabel(paramNetworkDataMatching.getParamDirectionNetwork2()
                .getOrientationMap().get(OrientationInterface.DOUBLE_SENS)), cc.xy(8, 9));
    }
    
    return formPanel;
  }
  
  /**
   * 
   * @return
   */
  private JPanel getDistanceParamTab() {
    
      FormLayout layout = new FormLayout(
              "40dlu, pref, 5dlu, pref, pref, pref, 40dlu",  // colonnes
              "20dlu, pref, 10dlu, pref, 10dlu, pref, 10dlu, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Ligne 1
    formPanel.add(new JLabel("Distance maximale entre deux noeuds : "), cc.xy(2, 2));
    formPanel.add(new JLabel(Float.toString(paramNetworkDataMatching.getParamDistance().getDistanceNoeudsMax())), cc.xy(5, 2));
    formPanel.add(new JLabel(" m"), cc.xy(6, 2));
    
    // Ligne 2
    formPanel.add(new JLabel("Distance maximum autorisée entre les arcs des deux réseaux : "), cc.xy(2, 4));
    formPanel.add(new JLabel(Float.toString(paramNetworkDataMatching.getParamDistance().getDistanceArcsMax())), cc.xy(5, 4));
    formPanel.add(new JLabel(" m"), cc.xy(6, 4));
    
    // Ligne 3
    formPanel.add(new JLabel("<html>Distance minimum sous laquelle l'écart de distance<br/>pour divers arcs du "
        + " réseaux 2 (distance vers les arcs du réseau 1)<br/>n'a plus aucun sens : "), cc.xy(2, 6));
    formPanel.add(new JLabel(Float.toString(paramNetworkDataMatching.getParamDistance().getDistanceArcsMin())), cc.xy(5, 6));
    formPanel.add(new JLabel(" m"), cc.xy(6, 6));
    
    // Ligne 4
    formPanel.add(new JLabel("<html>Distance maximale autorisée entre deux noeuds appariés,<br/>quand le noeud du "
        + " réseau 1 est une impasse uniquement :<br/>"
        + "Ce paramètre peut ne pas être pris en compte.</html>"), cc.xy(2, 8));
    // add(impasseButton, cc.xy(4, 8));
    formPanel.add(new JLabel(Float.toString(paramNetworkDataMatching.getParamDistance().getDistanceNoeudsImpassesMax())), cc.xy(5, 8));
    formPanel.add(new JLabel(" m"), cc.xy(6, 8));
    
    return formPanel;
  }
  
   /**
    * TRAITEMENTS TOPOLOGIQUES A L'IMPORT.
    * 
    * @return
    */
    private JPanel getTraitementTopoParamTab() {
    
        FormLayout layout = new FormLayout(
              "40dlu, pref, 20dlu, pref, pref, pref, pref, 20dlu, pref, pref, pref, pref, 40dlu",  // colonnes
              "20dlu, pref, 10dlu, pref, pref, pref, pref, 40dlu");   // lignes
        CellConstraints cc = new CellConstraints();
        JPanel formPanel = new JPanel();
        formPanel.setLayout(layout);
      
        // Line 1 : titles
        formPanel.add(new JLabel("Reseau 1"), cc.xyw(5, 2, 3));
        formPanel.add(new JLabel("Reseau 2"), cc.xyw(10, 2, 3));
          
        // -------------------------------------------------------------------------
          
        // Ligne 2
        formPanel.add(new JLabel("Seuil fusion des noeuds : "), cc.xy(2, 4));
        formPanel.add(new JLabel (Double.toString(paramNetworkDataMatching.getParamTopoNetwork1().getSeuilFusionNoeuds())), cc.xyw(4, 4, 4));
        formPanel.add(new JLabel (Double.toString(paramNetworkDataMatching.getParamTopoNetwork2().getSeuilFusionNoeuds())), cc.xyw(9, 4, 4));
          
        // Ligne 3
        formPanel.add(new JLabel("Graphe planaire : "), cc.xy(2, 5));
        if (paramNetworkDataMatching.getParamTopoNetwork1().getGraphePlanaire()) {
            formPanel.add(new JLabel("oui"), cc.xy(5,  5));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(7,  5));
        }
        if (paramNetworkDataMatching.getParamTopoNetwork2().getGraphePlanaire()) {
            formPanel.add(new JLabel("oui"), cc.xy(10,  5));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(12,  5));
        }
        
        // Ligne 4
        formPanel.add(new JLabel("Éliminer les noeuds qui n'ont que 2 arcs incidents et fusionner ces arcs ?"), cc.xy(2, 6));
        if (paramNetworkDataMatching.getParamTopoNetwork1().getElimineNoeudsAvecDeuxArcs()) {
            formPanel.add(new JLabel("oui"), cc.xy(5,  6));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(7,  6));
        }
        if (paramNetworkDataMatching.getParamTopoNetwork2().getElimineNoeudsAvecDeuxArcs()) {
            formPanel.add(new JLabel("oui"), cc.xy(10,  6));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(12,  6));
        }
        
        // Ligne 5
        formPanel.add(new JLabel("Fusion des arcs doubles : "), cc.xy(2, 7));
        if (paramNetworkDataMatching.getParamTopoNetwork1().getFusionArcsDoubles()) {
            formPanel.add(new JLabel("oui"), cc.xy(5,  7));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(7,  7));
        }
        if (paramNetworkDataMatching.getParamTopoNetwork2().getFusionArcsDoubles()) {
            formPanel.add(new JLabel("oui"), cc.xy(10,  7));
        } else {
            formPanel.add(new JLabel("non"), cc.xy(12,  7));
        }
      
        return formPanel;
    }
  
    private JPanel getSurdecoupageParamTab() {
        
        FormLayout layout = new FormLayout(
                "20dlu, 40dlu, pref, 10dlu, pref, pref, pref, pref, 20dlu, pref, pref, pref, pref, 20dlu", // colonnes
                "20dlu, pref, 10dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 40dlu");  // lignes
        CellConstraints cc = new CellConstraints();
          
        JPanel formPanel = new JPanel();
        formPanel.setLayout(layout);
        
        // Line 1 : titles
        formPanel.add(new JLabel("Reseau 1"), cc.xyw(6, 2, 3));
        formPanel.add(new JLabel("Reseau 2"), cc.xyw(11, 2, 3));
        
        // Line 2
        formPanel.add(new JLabel("Projeter les noeuds du réseau 1 sur le réseau 2 pour découper ce dernier : "), cc.xyw(2, 4, 2));
        // formPanel.add(jcYesProjection1, cc.xy(5, 4));
        formPanel.add(new JLabel("oui"), cc.xy(6, 4));
        // formPanel.add(jcNoProjection1, cc.xy(7, 4));
        formPanel.add(new JLabel("non"), cc.xy(8, 4));
        
        // formPanel.add(jcYesProjection2, cc.xy(10, 4));
        formPanel.add(new JLabel("oui"), cc.xy(11, 4));
        // formPanel.add(jcNoProjection2, cc.xy(12, 4));
        formPanel.add(new JLabel("non"), cc.xy(13, 4));
        
        // Line 3
        formPanel.add(new JLabel("Distance de la projection des noeuds 2 sur le réseau 1 : "), cc.xy(3, 6));
        // formPanel.add(distanceNoeudArc1, cc.xyw(5, 6, 4));
        // formPanel.add(distanceNoeudArc2, cc.xyw(10, 6, 4));
        
        // Line 4
        formPanel.add(new JLabel("Distance min entre la projection d'un noeud sur un arc : "), cc.xy(3, 8));
        // formPanel.add(distanceProjectionNoeud1, cc.xyw(5, 8, 4));
        // formPanel.add(distanceProjectionNoeud2, cc.xyw(10, 8, 4));
        
        // Line 5
        formPanel.add(new JLabel("On ne projete que les impasses du réseau 1 sur le réseau 2 : "), cc.xy(3, 10));
        // formPanel.add(jcYesImpasseSeulement1, cc.xy(5, 10));
        formPanel.add(new JLabel("oui"), cc.xy(6, 10));
        // formPanel.add(jcNoImpasseSeulement1, cc.xy(7, 10));
        formPanel.add(new JLabel("non"), cc.xy(8, 10));
        
        // formPanel.add(jcYesImpasseSeulement2, cc.xy(10, 10));
        formPanel.add(new JLabel("oui"), cc.xy(11, 10));
        // formPanel.add(jcNoImpasseSeulement2, cc.xy(12, 10));
        formPanel.add(new JLabel("non"), cc.xy(13, 10));
        
        return formPanel;
    }

}
