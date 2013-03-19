package fr.ign.cogit.geoxygene.appli.plugin.datamatching.network;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 */
public class EditParamDirectionPanel extends JPanel {

  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** */
  private JRadioButton rbEdgeTwoWayRef = null;
  private JRadioButton rbEdgeOneWayRef = null;
  private JRadioButton rbEdgeDefineWayRef = null;
  private JTextField fieldAttributeRef = null;
  private JTextField fieldValueDirect = null;
  private JTextField fieldValueInverse = null;
  private JTextField fieldValueDoubleSens = null;
  
  /**
   * Constructor.
   */
  public EditParamDirectionPanel() {
    
    // Initialize all fields
    initFields();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  /**
   * 
   */
  private void initFields() {
    
    ButtonGroup groupe = new ButtonGroup();
    
    rbEdgeTwoWayRef = new JRadioButton("Tous les axes sont en double sens");
    rbEdgeTwoWayRef.setSelected(true);
    groupe.add(rbEdgeTwoWayRef);
    
    rbEdgeOneWayRef = new JRadioButton("Tous les axes sont en simple sens");
    groupe.add(rbEdgeOneWayRef);
    
    rbEdgeDefineWayRef = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    groupe.add(rbEdgeDefineWayRef);
  }
  
  /**
   * 
   */
  private void initPanel() {
    FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, pref, pref, 20dlu",
        "10dlu, pref, pref, pref, pref, pref, pref, pref, 40dlu");
    CellConstraints cc = new CellConstraints();
    setLayout(layout);
    
    // JLabel labelParam1 = new JLabel("Avec orientation double");
    add(rbEdgeTwoWayRef, cc.xyw(2, 2, 4));
    add(rbEdgeOneWayRef, cc.xyw(2, 3, 4));
    add(rbEdgeDefineWayRef, cc.xyw(2, 4, 4));
    
    add(new JLabel("Nom de l'attribut : "), cc.xy(4, 5));
    fieldAttributeRef = new JTextField(40);
    fieldAttributeRef.setText("sens_de_circulation");
    add(fieldAttributeRef, cc.xy(5, 5));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(4, 6));
    fieldValueDirect = new JTextField(10);
    fieldValueDirect.setText("direct");
    add(fieldValueDirect, cc.xy(5, 6));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(4, 7));
    fieldValueInverse = new JTextField(10);
    fieldValueInverse.setText("inverse");
    add(fieldValueInverse, cc.xy(5, 7));
    
    add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(4, 8));
    fieldValueDoubleSens = new JTextField(10);
    fieldValueDoubleSens.setText("double");
    add(fieldValueDoubleSens, cc.xy(5, 8));
  }
  
  public JRadioButton getRbEdgeTwoWayRef() {
    return rbEdgeTwoWayRef;
  }
  public JRadioButton getRbEdgeOneWayRef() {
    return rbEdgeOneWayRef;
  }
  public JRadioButton getRbEdgeDefineWayRef() {
    return rbEdgeDefineWayRef;
  }
  public JTextField getFieldAttributeRef() {
    return fieldAttributeRef;
  }
  
  public JTextField getFieldValueDirect() {
    return fieldValueDirect;
  }
  public JTextField getFieldValueInverse() {
    return fieldValueInverse;
  }
  public JTextField getFieldValueDoubleSens() {
    return fieldValueDoubleSens;
  }
}
