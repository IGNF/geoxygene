package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopologyTreatmentNetwork;

public class EditParamTopoPanel extends JPanel {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  private ParamTopologyTreatmentNetwork paramTopoNetwork1;
  private ParamTopologyTreatmentNetwork paramTopoNetwork2;
  
  private JTextField jtSeuilFusionNoeuds1;
  private JRadioButton jcYesGraphePlanaire1;
  private JRadioButton jcNoGraphePlanaire1;
  private JRadioButton jcYesElimineNoeudsAvec2Arcs1;
  private JRadioButton jcNoElimineNoeudsAvec2Arcs1;
  private JRadioButton jcYesFusionArcsDoubles1;
  private JRadioButton jcNoFusionArcsDoubles1;
  
  private JTextField jtSeuilFusionNoeuds2;
  private JRadioButton jcYesGraphePlanaire2;
  private JRadioButton jcNoGraphePlanaire2;
  private JRadioButton jcYesElimineNoeudsAvec2Arcs2;
  private JRadioButton jcNoElimineNoeudsAvec2Arcs2;
  private JRadioButton jcYesFusionArcsDoubles2;
  private JRadioButton jcNoFusionArcsDoubles2;
  
  /**
   * Constructor.
   * @param pn1
   * @param pn2
   */
  public EditParamTopoPanel(ParamTopologyTreatmentNetwork pn1, ParamTopologyTreatmentNetwork pn2) {
    
    paramTopoNetwork1 = pn1;
    paramTopoNetwork2 = pn2;
    
    // Initialize fields
    initField();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  public void initField() {
    
    // Reseau n°1
    jtSeuilFusionNoeuds1 = new JTextField(20);
    jtSeuilFusionNoeuds1.setText(Double.toString(paramTopoNetwork1.getSeuilFusionNoeuds()));
    
    ButtonGroup gpGraphePlanaire1 = new ButtonGroup();
    jcYesGraphePlanaire1 = new JRadioButton();
    gpGraphePlanaire1.add(jcYesGraphePlanaire1);
    jcNoGraphePlanaire1 = new JRadioButton();
    gpGraphePlanaire1.add(jcNoGraphePlanaire1);
    if (paramTopoNetwork1.getGraphePlanaire()) {
      jcYesGraphePlanaire1.setSelected(true);
    } else {
      jcNoGraphePlanaire1.setSelected(true);
    }
    
    ButtonGroup gpElimineNoeudsAvec2Arcs1 = new ButtonGroup();
    jcYesElimineNoeudsAvec2Arcs1 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs1.add(jcYesElimineNoeudsAvec2Arcs1);
    jcNoElimineNoeudsAvec2Arcs1 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs1.add(jcNoElimineNoeudsAvec2Arcs1);
    if (paramTopoNetwork1.getElimineNoeudsAvecDeuxArcs()) {
      jcYesElimineNoeudsAvec2Arcs1.setSelected(true);
    } else {
      jcNoElimineNoeudsAvec2Arcs1.setSelected(true);
    }
    
    ButtonGroup gpFusionArcsDoubles1 = new ButtonGroup();
    jcYesFusionArcsDoubles1 = new JRadioButton();
    gpFusionArcsDoubles1.add(jcYesFusionArcsDoubles1);
    jcNoFusionArcsDoubles1 = new JRadioButton();
    gpFusionArcsDoubles1.add(jcNoFusionArcsDoubles1);
    if (paramTopoNetwork1.getFusionArcsDoubles()) {
      jcYesFusionArcsDoubles1.setSelected(true);
    } else {
      jcNoFusionArcsDoubles1.setSelected(true);
    }
    
    // Reseau n°2
    jtSeuilFusionNoeuds2 = new JTextField(20);
    jtSeuilFusionNoeuds2.setText(Double.toString(paramTopoNetwork2.getSeuilFusionNoeuds()));
    
    ButtonGroup gpGraphePlanaire2 = new ButtonGroup();
    jcYesGraphePlanaire2 = new JRadioButton();
    gpGraphePlanaire2.add(jcYesGraphePlanaire2);
    jcNoGraphePlanaire2 = new JRadioButton();
    gpGraphePlanaire2.add(jcNoGraphePlanaire2);
    if (paramTopoNetwork2.getGraphePlanaire()) {
      jcYesGraphePlanaire2.setSelected(true);
    } else {
      jcNoGraphePlanaire2.setSelected(true);
    }
    
    ButtonGroup gpElimineNoeudsAvec2Arcs2 = new ButtonGroup();
    jcYesElimineNoeudsAvec2Arcs2 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs2.add(jcYesElimineNoeudsAvec2Arcs2);
    jcNoElimineNoeudsAvec2Arcs2 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs2.add(jcNoElimineNoeudsAvec2Arcs2);
    if (paramTopoNetwork2.getElimineNoeudsAvecDeuxArcs()) {
      jcYesElimineNoeudsAvec2Arcs2.setSelected(true);
    } else {
      jcNoElimineNoeudsAvec2Arcs2.setSelected(true);
    }
    
    ButtonGroup gpFusionArcsDoubles2 = new ButtonGroup();
    jcYesFusionArcsDoubles2 = new JRadioButton();
    gpFusionArcsDoubles2.add(jcYesFusionArcsDoubles2);
    jcNoFusionArcsDoubles2 = new JRadioButton();
    gpFusionArcsDoubles2.add(jcNoFusionArcsDoubles2);
    if (paramTopoNetwork2.getFusionArcsDoubles()) {
      jcYesFusionArcsDoubles2.setSelected(true);
    } else {
      jcNoFusionArcsDoubles2.setSelected(true);
    }
    
  }
  
  /**
   * 
   */
  private void initPanel() {
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, 20dlu, pref, pref, pref, pref, 20dlu, pref, pref, pref, pref, 40dlu",  // colonnes
        "20dlu, pref, 10dlu, pref, pref, pref, pref, 40dlu");   // lignes
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // Line 1 : titles
    add(new JLabel("Reseau 1"), cc.xyw(5, 2, 3));
    add(new JLabel("Reseau 2"), cc.xyw(10, 2, 3));
    
    // -------------------------------------------------------------------------
    
    // Ligne 2
    add(new JLabel("Seuil fusion des noeuds : "), cc.xy(2, 4));
    add(jtSeuilFusionNoeuds1, cc.xyw(4, 4, 4));
    add(jtSeuilFusionNoeuds2, cc.xyw(9, 4, 4));
    
    // Ligne 3
    add(new JLabel("Graphe planaire : "), cc.xy(2, 5));
    add(jcYesGraphePlanaire1, cc.xy(4,  5));
    add(new JLabel("oui"), cc.xy(5,  5));
    add(jcNoGraphePlanaire1, cc.xy(6,  5));
    add(new JLabel("non"), cc.xy(7,  5));
    add(jcYesGraphePlanaire2, cc.xy(9,  5));
    add(new JLabel("oui"), cc.xy(10,  5));
    add(jcNoGraphePlanaire2, cc.xy(11,  5));
    add(new JLabel("non"), cc.xy(12,  5));
    
    // Ligne 4
    add(new JLabel("Éliminer les noeuds qui n'ont que 2 arcs incidents et fusionner ces arcs ?"), cc.xy(2, 6));
    add(jcYesElimineNoeudsAvec2Arcs1, cc.xy(4,  6));
    add(new JLabel("oui"), cc.xy(5,  6));
    add(jcNoElimineNoeudsAvec2Arcs1, cc.xy(6,  6));
    add(new JLabel("non"), cc.xy(7,  6));
    add(jcYesElimineNoeudsAvec2Arcs2, cc.xy(9,  6));
    add(new JLabel("oui"), cc.xy(10,  6));
    add(jcNoElimineNoeudsAvec2Arcs2, cc.xy(11,  6));
    add(new JLabel("non"), cc.xy(12,  6));
    
    // Ligne 5
    add(new JLabel("Fusion des arcs doubles : "), cc.xy(2, 7));
    add(jcYesFusionArcsDoubles1, cc.xy(4,  7));
    add(new JLabel("oui"), cc.xy(5,  7));
    add(jcNoFusionArcsDoubles1, cc.xy(6,  7));
    add(new JLabel("non"), cc.xy(7,  7));
    add(jcYesFusionArcsDoubles2, cc.xy(9,  7));
    add(new JLabel("oui"), cc.xy(10,  7));
    add(jcNoFusionArcsDoubles2, cc.xy(11,  7));
    add(new JLabel("non"), cc.xy(12,  7));
    
  }
  
  /**
   * 
   * @return
   */
  public ParamTopologyTreatmentNetwork[] valideField() {
    
    ParamTopologyTreatmentNetwork[] tabParamTopology = new ParamTopologyTreatmentNetwork[2];
    
    // param topology 1
    ParamTopologyTreatmentNetwork paramTopo1 = new ParamTopologyTreatmentNetwork();
    paramTopo1.setSeuilFusionNoeuds(Double.parseDouble(jtSeuilFusionNoeuds1.getText()));
    if (jcYesGraphePlanaire1.isSelected()) {
      paramTopo1.setGraphePlanaire(true);
    } else {
      paramTopo1.setGraphePlanaire(false);
    }
    if (jcYesElimineNoeudsAvec2Arcs1.isSelected()) {
      paramTopo1.setElimineNoeudsAvecDeuxArcs(true);
    } else {
      paramTopo1.setElimineNoeudsAvecDeuxArcs(false);
    }
    if (jcYesFusionArcsDoubles1.isSelected()) {
      paramTopo1.setFusionArcsDoubles(true);
    } else {
      paramTopo1.setFusionArcsDoubles(false);
    }
    tabParamTopology[0] = paramTopo1;
    
    // param topology 2
    ParamTopologyTreatmentNetwork paramTopo2 = new ParamTopologyTreatmentNetwork();
    paramTopo2.setSeuilFusionNoeuds(Double.parseDouble(jtSeuilFusionNoeuds2.getText()));
    if (jcYesGraphePlanaire2.isSelected()) {
      paramTopo2.setGraphePlanaire(true);
    } else {
      paramTopo2.setGraphePlanaire(false);
    }
    if (jcYesElimineNoeudsAvec2Arcs2.isSelected()) {
      paramTopo2.setElimineNoeudsAvecDeuxArcs(true);
    } else {
      paramTopo2.setElimineNoeudsAvecDeuxArcs(false);
    }
    if (jcYesFusionArcsDoubles2.isSelected()) {
      paramTopo2.setFusionArcsDoubles(true);
    } else {
      paramTopo2.setFusionArcsDoubles(false);
    }
    tabParamTopology[1] = paramTopo2;
    
    // 
    return tabParamTopology;
  }

}
