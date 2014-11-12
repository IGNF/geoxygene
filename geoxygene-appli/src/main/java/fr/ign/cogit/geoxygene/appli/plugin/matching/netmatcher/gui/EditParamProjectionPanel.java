package fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class EditParamProjectionPanel extends JPanel implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** . */
  /** Parameters. */
  private ParametresApp param = null;
  
  // Projection du reseau 1
  private JRadioButton jcYesProjection1;
  private JRadioButton jcNoProjection1;
  private JTextField distanceNoeudArc1;
  private JTextField distanceProjectionNoeud1;
  private JRadioButton jcYesImpasseSeulement1;
  private JRadioButton jcNoImpasseSeulement1;
  
  // Projection du reseau 1
  private JRadioButton jcYesProjection2;
  private JRadioButton jcNoProjection2;
  private JTextField distanceNoeudArc2;
  private JTextField distanceProjectionNoeud2;
  private JRadioButton jcYesImpasseSeulement2;
  private JRadioButton jcNoImpasseSeulement2;
  
  
  /**
   * Constructor.
   */
  public EditParamProjectionPanel(ParametresApp param) {
    
    this.param = param;
    
    // Initialize all fields
    initFields();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  
  /**
   * 
   */
  private void initFields() {
    
    // Projection 1
    ButtonGroup gpProjection1 = new ButtonGroup();
    jcYesProjection1 = new JRadioButton();
    gpProjection1.add(jcYesProjection1);
    jcNoProjection1 = new JRadioButton();
    gpProjection1.add(jcNoProjection1);
    if (param.projeteNoeuds1SurReseau2) {
      jcYesProjection1.setSelected(true);
    } else {
      jcNoProjection1.setSelected(true);
    }
    jcYesProjection1.addActionListener(this);
    jcNoProjection1.addActionListener(this);
    
    distanceNoeudArc1 = new JTextField(10);
    distanceNoeudArc1.setText(Double.toString(param.projeteNoeuds1SurReseau2DistanceNoeudArc));
    
    distanceProjectionNoeud1 = new JTextField(10);
    distanceProjectionNoeud1.setText(Double.toString(param.projeteNoeuds1SurReseau2DistanceProjectionNoeud));
    
    ButtonGroup gpImpasseSeulement1 = new ButtonGroup();
    jcYesImpasseSeulement1 = new JRadioButton();
    gpImpasseSeulement1.add(jcYesImpasseSeulement1);
    jcNoImpasseSeulement1 = new JRadioButton();
    gpImpasseSeulement1.add(jcNoImpasseSeulement1);
    if (param.projeteNoeuds1SurReseau2) {
      jcYesImpasseSeulement1.setSelected(true);
    } else {
      jcNoImpasseSeulement1.setSelected(true);
    }
    
    // Projection 2
    ButtonGroup gpProjection2 = new ButtonGroup();
    jcYesProjection2 = new JRadioButton();
    gpProjection2.add(jcYesProjection2);
    jcNoProjection2 = new JRadioButton();
    gpProjection2.add(jcNoProjection2);
    if (param.projeteNoeuds2SurReseau1) {
      jcYesProjection2.setSelected(true);
    } else {
      jcNoProjection2.setSelected(true);
    }
    jcYesProjection2.addActionListener(this);
    jcNoProjection2.addActionListener(this);
    
    distanceNoeudArc2 = new JTextField(10);
    distanceNoeudArc2.setText(Double.toString(param.projeteNoeuds2SurReseau1DistanceNoeudArc));
    
    distanceProjectionNoeud2 = new JTextField(10);
    distanceProjectionNoeud2.setText(Double.toString(param.projeteNoeuds2SurReseau1DistanceProjectionNoeud));
    
    ButtonGroup gpImpasseSeulement2 = new ButtonGroup();
    jcYesImpasseSeulement2 = new JRadioButton();
    gpImpasseSeulement2.add(jcYesImpasseSeulement2);
    jcNoImpasseSeulement2 = new JRadioButton();
    gpImpasseSeulement2.add(jcNoImpasseSeulement2);
    if (param.projeteNoeuds2SurReseau1) {
      jcYesImpasseSeulement2.setSelected(true);
    } else {
      jcNoImpasseSeulement2.setSelected(true);
    }
    
  }
  
  /**
   * 
   */
  private void initPanel() {
    
    // "Définir le sens de l'axe suivant la valeur d'un attribut"
    // "Tous les axes sont en double sens"
    
    FormLayout layout = new FormLayout(
        "20dlu, 40dlu, pref, 10dlu, pref, pref, pref, pref, 20dlu, pref, pref, pref, pref, 20dlu", // colonnes
        "20dlu, pref, 10dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    setLayout(layout);
    
    // Line 1 : titles
    add(new JLabel("Reseau 1"), cc.xyw(6, 2, 3));
    add(new JLabel("Reseau 2"), cc.xyw(11, 2, 3));
    
    // Line 2
    add(new JLabel("Projeter les noeuds du réseau 1 sur le réseau 2 pour découper ce dernier : "), cc.xyw(2, 4, 2));
    add(jcYesProjection1, cc.xy(5, 4));
    add(new JLabel("oui"), cc.xy(6, 4));
    add(jcNoProjection1, cc.xy(7, 4));
    add(new JLabel("non"), cc.xy(8, 4));
    
    add(jcYesProjection2, cc.xy(10, 4));
    add(new JLabel("oui"), cc.xy(11, 4));
    add(jcNoProjection2, cc.xy(12, 4));
    add(new JLabel("non"), cc.xy(13, 4));
    
    // Line 3
    add(new JLabel("Distance de la projection des noeuds 2 sur le réseau 1 : "), cc.xy(3, 6));
    add(distanceNoeudArc1, cc.xyw(5, 6, 4));
    add(distanceNoeudArc2, cc.xyw(10, 6, 4));
    
    // Line 4
    add(new JLabel("Distance min entre la projection d'un noeud sur un arc : "), cc.xy(3, 8));
    add(distanceProjectionNoeud1, cc.xyw(5, 8, 4));
    add(distanceProjectionNoeud2, cc.xyw(10, 8, 4));
    
    // Line 5
    add(new JLabel("On ne projete que les impasses du réseau 1 sur le réseau 2 : "), cc.xy(3, 10));
    add(jcYesImpasseSeulement1, cc.xy(5, 10));
    add(new JLabel("oui"), cc.xy(6, 10));
    add(jcNoImpasseSeulement1, cc.xy(7, 10));
    add(new JLabel("non"), cc.xy(8, 10));
    
    add(jcYesImpasseSeulement2, cc.xy(10, 10));
    add(new JLabel("oui"), cc.xy(11, 10));
    add(jcNoImpasseSeulement2, cc.xy(12, 10));
    add(new JLabel("non"), cc.xy(13, 10));
  
  }
  
  /**
   * 
   * @return
   */
  public ParametresApp valideField() {
    
    // Reseau 1
    if (jcYesProjection1.isSelected()) {
      param.projeteNoeuds1SurReseau2 = true;
      param.projeteNoeuds1SurReseau2DistanceNoeudArc = Double.parseDouble(distanceNoeudArc1.getText());
      param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = Double.parseDouble(distanceProjectionNoeud1.getText());
      if (jcYesImpasseSeulement1.isSelected()) {
        param.projeteNoeuds1SurReseau2ImpassesSeulement = true;
      } else {
        param.projeteNoeuds1SurReseau2ImpassesSeulement = false;
      }
    } else {
      param.projeteNoeuds1SurReseau2 = false;
      param.projeteNoeuds1SurReseau2DistanceNoeudArc = 0;
      param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 0;
      param.projeteNoeuds1SurReseau2ImpassesSeulement = false;
    }
    
    // Reseau 2
    if (jcYesProjection2.isSelected()) {
      param.projeteNoeuds2SurReseau1 = true;
      param.projeteNoeuds2SurReseau1DistanceNoeudArc = Double.parseDouble(distanceNoeudArc2.getText());
      param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = Double.parseDouble(distanceProjectionNoeud2.getText());
      if (jcYesImpasseSeulement2.isSelected()) {
        param.projeteNoeuds2SurReseau1ImpassesSeulement = true;
      } else {
        param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
      }
    } else {
      param.projeteNoeuds2SurReseau1 = false;
      param.projeteNoeuds2SurReseau1DistanceNoeudArc = 0;
      param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 0;
      param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    }
    
    // return all parameters
    return param;
  
  }
  
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == jcYesProjection1) {
      distanceNoeudArc1.setEnabled(true);
      distanceProjectionNoeud1.setEnabled(true);
      jcYesImpasseSeulement1.setEnabled(true);
      jcNoImpasseSeulement1.setEnabled(true);
    } else if (source == jcNoProjection1) {
      distanceNoeudArc1.setEnabled(false);
      distanceProjectionNoeud1.setEnabled(false);
      jcYesImpasseSeulement1.setEnabled(false);
      jcNoImpasseSeulement1.setEnabled(false);
    } else if (source == jcYesProjection2) {
      distanceNoeudArc2.setEnabled(true);
      distanceProjectionNoeud2.setEnabled(true);
      jcYesImpasseSeulement2.setEnabled(true);
      jcNoImpasseSeulement2.setEnabled(true);
    } else if (source == jcNoProjection2) {
      distanceNoeudArc2.setEnabled(false);
      distanceProjectionNoeud2.setEnabled(false);
      jcYesImpasseSeulement2.setEnabled(false);
      jcNoImpasseSeulement2.setEnabled(false);
    }
  }

}
