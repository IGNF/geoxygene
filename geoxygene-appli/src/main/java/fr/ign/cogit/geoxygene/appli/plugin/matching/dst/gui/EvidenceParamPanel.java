package fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.LinearFunction;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;


/**
 * 
 * 
 *
 */
public class EvidenceParamPanel extends JDialog implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 1L;

  /** A classic logger. */
  private final static Logger LOGGER = Logger.getLogger(EvidenceParamPanel.class.getName());
  
  private final static DecimalFormat DF = new DecimalFormat("0.00000");
  
  private String action;
  
  /** 2 buttons : launch, cancel. */
  private JButton recordButton = null;
  private JButton cancelButton = null;
  
  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  JPanel hypothesisPanel = null;
  
  /** Fields. */
  private JComboBox<String> comboListeCritere;
  private JComboBox<String> comboListeDistance;
  
  Source<IFeature, GeomHypothesis> source;
  
  // private final static JComboBox<String> comboListeFunction = new JComboBox<String>();
  
  
  /**
   * Constructor.
   * 
   * @param source
   */
  public EvidenceParamPanel(Source<IFeature, GeomHypothesis> source) {

    LOGGER.info("EvidenceParamPanel Constructor");
    
    this.source = source;
    
    setModal(true);
    setTitle("DataMatchingPlugin.InputDialogTitle");
    setIconImage(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/wrench.png")).getImage());
    
    // comboListeFunction.addItem("Linear Function (y=Ax+B)");
    // comboListeFunction.addItem("Constant Function (y=C)");
    
    initHypothesisPanel();
    initButtonPanel();
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(hypothesisPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(250, 50);
    setSize(new Dimension(800, 500));
    setPreferredSize(new Dimension(800, 500));
    setVisible(true);
  }
  
  /**
   * 
   */
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    
    recordButton = new JButton("Record");
    cancelButton = new JButton("Cancel");
    
    recordButton.addActionListener(this);
    cancelButton.addActionListener(this);
    
    buttonPanel.setLayout(new FlowLayout (FlowLayout.CENTER)); 
    buttonPanel.add(cancelButton);
    buttonPanel.add(recordButton);
    
  }
  
  
  private void initHypothesisPanel() {
    
    hypothesisPanel = new JPanel();
    
    FormLayout layout = new FormLayout(
        "10dlu, pref, 10dlu, pref, 10dlu, pref, pref, 10dlu",
        "10dlu, pref, 10dlu, pref, pref, pref, 10dlu");
    hypothesisPanel.setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    hypothesisPanel.setBorder(new TitledBorder("Edition critère"));
    
    comboListeCritere = new JComboBox<String>();
    comboListeCritere.addItem("Choisir un critère               ");
    comboListeCritere.addItem("Critères géométriques");
    comboListeCritere.addItem("Critères textes");
    comboListeCritere.addItem("Critères sémantiques");
    comboListeCritere.addActionListener(this);
    comboListeDistance = new JComboBox<String>();
    comboListeDistance.addItem("Choisir une distance            ");
    
    hypothesisPanel.add(comboListeCritere, cc.xy(4, 2));
    hypothesisPanel.add(comboListeDistance, cc.xy(6, 2));
    hypothesisPanel.add(new JLabel("                                        "), cc.xy(7, 2));
    
    hypothesisPanel.add(new JLabel("Croyance à l'appariement : "), cc.xy(2, 4));
    hypothesisPanel.add(new JLabel("Croyance au non appariement : "), cc.xy(2, 5));
    hypothesisPanel.add(new JLabel("Incertitude : "), cc.xy(2, 6));
    
    hypothesisPanel.add(displayFunctionPanel(source.getMasseAppCi()), cc.xyw(4, 4, 4));
    hypothesisPanel.add(displayFunctionPanel(source.getMasseAppPasCi()), cc.xyw(4, 5, 4));
    hypothesisPanel.add(displayFunctionPanel(source.getMasseIgnorance()), cc.xyw(4, 6, 4));
    
  }
  
  
  /**
   * Actions : launch and cancel.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    
    Object source = evt.getSource();
    
    if (source == recordButton) {
      // Init action
      action = "RECORD";
      
      // Set parameters
      // setParameters();
      dispose();
    } else if (source == cancelButton) {
      // Init action
      action = "CANCEL";
      
      // do nothing
      dispose();
    } else if (source == comboListeCritere) {
      
      int index = comboListeCritere.getSelectedIndex();
      comboListeDistance.removeAllItems();
      if (index == 0) {
        comboListeDistance.addItem("Choisir une distance");
      } else if (comboListeCritere.getSelectedIndex() == 1) {
        comboListeDistance.addItem("Distance euclidienne");
        comboListeDistance.addItem("Distance de Hausdorff");
        comboListeDistance.addItem("Distance partielle de Frechet");
      } else if (comboListeCritere.getSelectedIndex() == 2) {
        comboListeDistance.addItem("Distance Levenstein");
        comboListeDistance.addItem("Distance de Hamming");
      } else if (comboListeCritere.getSelectedIndex() == 3) {
        comboListeDistance.addItem("Distance Wu et Palmer");
      } 
    }
  
  }
  
  /**
   * 
   * @return
   */
  public String getAction() {
    return action;
  }
  
  
  /**
   * 
   * @param fs
   * @return
   */
  private JPanel displayFunctionPanel(Function1D[] fs) {
    
    JPanel fPanel = new JPanel();
    CellConstraints cc = new CellConstraints();
    String encodedRowSpecs = "10dlu";
    int nbLigne = fs.length;
    for (int i = 0; i < nbLigne; i++) {
      encodedRowSpecs += ", pref, pref";
    }
    encodedRowSpecs += ", 10dlu";
        
    fPanel.setBackground(Color.WHITE);
    FormLayout layoutFEAPanel = new FormLayout(
        "10dlu, pref, 10dlu, pref, pref, 10dlu, pref, pref, 10dlu, pref, 10dlu",
        encodedRowSpecs);
    fPanel.setLayout(layoutFEAPanel);
    
    for (int i = 0; i < nbLigne; i++) {
      if (fs[i] instanceof LinearFunction) {
        
        // comboListeFunction.setSelectedIndex(0);
        fPanel.add(new JLabel("Linear Function (y=Ax+B)"), cc.xy(2, 2 + i*2));
        
        fPanel.add(new JLabel("A : "), cc.xy(4, 2 + i*2));
        double a = ((LinearFunction) fs[i]).getA();
        JTextField fieldA = new JTextField(5);
        fieldA.setText(DF.format(a));
        fPanel.add(fieldA, cc.xy(5, 2 + i*2));
        
        fPanel.add(new JLabel("B :"), cc.xy(7, 2 + i*2));
        double b = ((LinearFunction) fs[i]).getB();
        JTextField fieldB = new JTextField(5);
        fieldB.setText(DF.format(b));
        fPanel.add(fieldB, cc.xy(8, 2 + i*2));
      
      } else if (fs[i] instanceof ConstantFunction) {
        
        // comboListeFunction.setSelectedIndex(1);
        fPanel.add(new JLabel("Constant Function (y=C)"), cc.xy(2, 2 + i*2));
        
        fPanel.add(new JLabel("C : "), cc.xy(4, 2 + i*2));
        double c = ((ConstantFunction) fs[i]).getShift();
        JTextField fieldC = new JTextField(5);
        fieldC.setText(DF.format(c));
        fPanel.add(fieldC, cc.xy(5, 2 + i*2));
      }
    }
    
    FunPanel f1 = new FunPanel(((GeoSource)source).getMasseAppCi());
    // f1.setBounds(100, 100, 100, 50);
    fPanel.add(f1, cc.xywh(10, 2, 1, nbLigne));
    
    return fPanel;
  }
  
}
