package fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.gui;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;

/**
 * 
 *
 */
public class EditParamTopoPanel extends JPanel {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** Parameters. */
  private ParametresApp param = null;
  
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
  public EditParamTopoPanel(ParametresApp pn) {
    
    this.param = pn;
    
    // Initialize fields
    initField();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  public void initField() {
    
    // Reseau n°1
    jtSeuilFusionNoeuds1 = new JTextField(20);
    jtSeuilFusionNoeuds1.setText(Double.toString(param.topologieSeuilFusionNoeuds1));
    
    ButtonGroup gpGraphePlanaire1 = new ButtonGroup();
    jcYesGraphePlanaire1 = new JRadioButton();
    gpGraphePlanaire1.add(jcYesGraphePlanaire1);
    jcNoGraphePlanaire1 = new JRadioButton();
    gpGraphePlanaire1.add(jcNoGraphePlanaire1);
    if (param.topologieGraphePlanaire1) {
      jcYesGraphePlanaire1.setSelected(true);
    } else {
      jcNoGraphePlanaire1.setSelected(true);
    }
    
    ButtonGroup gpElimineNoeudsAvec2Arcs1 = new ButtonGroup();
    jcYesElimineNoeudsAvec2Arcs1 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs1.add(jcYesElimineNoeudsAvec2Arcs1);
    jcNoElimineNoeudsAvec2Arcs1 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs1.add(jcNoElimineNoeudsAvec2Arcs1);
    if (param.topologieElimineNoeudsAvecDeuxArcs1) {
      jcYesElimineNoeudsAvec2Arcs1.setSelected(true);
    } else {
      jcNoElimineNoeudsAvec2Arcs1.setSelected(true);
    }
    
    ButtonGroup gpFusionArcsDoubles1 = new ButtonGroup();
    jcYesFusionArcsDoubles1 = new JRadioButton();
    gpFusionArcsDoubles1.add(jcYesFusionArcsDoubles1);
    jcNoFusionArcsDoubles1 = new JRadioButton();
    gpFusionArcsDoubles1.add(jcNoFusionArcsDoubles1);
    if (param.topologieFusionArcsDoubles1) {
      jcYesFusionArcsDoubles1.setSelected(true);
    } else {
      jcNoFusionArcsDoubles1.setSelected(true);
    }
    
    // Reseau n°2
    jtSeuilFusionNoeuds2 = new JTextField(20);
    jtSeuilFusionNoeuds2.setText(Double.toString(param.topologieSeuilFusionNoeuds2));
    
    ButtonGroup gpGraphePlanaire2 = new ButtonGroup();
    jcYesGraphePlanaire2 = new JRadioButton();
    gpGraphePlanaire2.add(jcYesGraphePlanaire2);
    jcNoGraphePlanaire2 = new JRadioButton();
    gpGraphePlanaire2.add(jcNoGraphePlanaire2);
    if (param.topologieGraphePlanaire2) {
      jcYesGraphePlanaire2.setSelected(true);
    } else {
      jcNoGraphePlanaire2.setSelected(true);
    }
    
    ButtonGroup gpElimineNoeudsAvec2Arcs2 = new ButtonGroup();
    jcYesElimineNoeudsAvec2Arcs2 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs2.add(jcYesElimineNoeudsAvec2Arcs2);
    jcNoElimineNoeudsAvec2Arcs2 = new JRadioButton();
    gpElimineNoeudsAvec2Arcs2.add(jcNoElimineNoeudsAvec2Arcs2);
    if (param.topologieElimineNoeudsAvecDeuxArcs2) {
      jcYesElimineNoeudsAvec2Arcs2.setSelected(true);
    } else {
      jcNoElimineNoeudsAvec2Arcs2.setSelected(true);
    }
    
    ButtonGroup gpFusionArcsDoubles2 = new ButtonGroup();
    jcYesFusionArcsDoubles2 = new JRadioButton();
    gpFusionArcsDoubles2.add(jcYesFusionArcsDoubles2);
    jcNoFusionArcsDoubles2 = new JRadioButton();
    gpFusionArcsDoubles2.add(jcNoFusionArcsDoubles2);
    if (param.topologieFusionArcsDoubles2) {
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
  public ParametresApp valideField() {
    
    // param topology 1
    param.topologieSeuilFusionNoeuds1 = Double.parseDouble(jtSeuilFusionNoeuds1.getText());
    if (jcYesGraphePlanaire1.isSelected()) {
      param.topologieGraphePlanaire1 = true;
    } else {
      param.topologieGraphePlanaire1 = false;
    }
    if (jcYesElimineNoeudsAvec2Arcs1.isSelected()) {
      param.topologieElimineNoeudsAvecDeuxArcs1 = true;
    } else {
      param.topologieElimineNoeudsAvecDeuxArcs1 = false;
    }
    if (jcYesFusionArcsDoubles1.isSelected()) {
      param.topologieFusionArcsDoubles1 = true;
    } else {
      param.topologieFusionArcsDoubles1 = false;
    }
    
    // param topology 2
    param.topologieSeuilFusionNoeuds2 = Double.parseDouble(jtSeuilFusionNoeuds2.getText());
    if (jcYesGraphePlanaire2.isSelected()) {
      param.topologieGraphePlanaire2 = true;
    } else {
      param.topologieGraphePlanaire2 = false;
    }
    if (jcYesElimineNoeudsAvec2Arcs2.isSelected()) {
      param.topologieElimineNoeudsAvecDeuxArcs2 = true;
    } else {
      param.topologieElimineNoeudsAvecDeuxArcs2 = false;
    }
    if (jcYesFusionArcsDoubles2.isSelected()) {
      param.topologieFusionArcsDoubles2 = true;
    } else {
      param.topologieFusionArcsDoubles2 = false;
    }
    
    // 
    return param;
  }

}
