package fr.ign.cogit.geoxygene.appli.plugin.datamatching.network;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetwork;

public class DisplayParamPanel extends JToolBar {
  
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  private ParametresApp parameterApp = null;
  
  /**
   * Constructor.
   * @param frame
   */
  public DisplayParamPanel(ParametresApp parameterApp){
    
    this.parameterApp = parameterApp;
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
        "20dlu, pref, 5dlu, pref, 20dlu", // colonnes
        "10dlu, pref, 10dlu" // lignes 
        );
    CellConstraints cc = new CellConstraints();
    
    JPanel formPanel = new JPanel();
    formPanel.setLayout(layout);
    
    // Ligne 1 : distanceNoeudsMax
    formPanel.add(new JLabel("Distance maximale autorisée entre deux noeuds appariés : "), cc.xy(2, 2));
    formPanel.add(new JLabel(Float.toString(parameterApp.distanceNoeudsMax)), cc.xy(4, 2));
    
    
    
    // Add panel
    this.add(formPanel);
    
  }

}
