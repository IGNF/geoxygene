package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class EditParamActionPanel extends JPanel implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** 2 buttons : export and import XML parameters files. */
  private JButton exportXML = null;
  private JButton importXML = null;
  
  private EditParamPanel paramPanel;
 
  /**
   * Constructor.
   * @param paramPanel
   */
  public EditParamActionPanel(EditParamPanel paramPanel) {
    
    this.paramPanel = paramPanel;
    
    exportXML = new JButton("export XML");
    exportXML.setToolTipText("Export parameters in XML files");
    exportXML.addActionListener(this);
    
    importXML = new JButton("import XML");
    importXML.setToolTipText("Import parameters in XML files");
    importXML.addActionListener(this);
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  
  /**
   * 
   */
  private void initPanel() {
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 40dlu",
        "20dlu, pref, pref, 20dlu, pref, pref, pref, 20dlu, pref, 40dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // Line 1 : titles
    add(exportXML, cc.xy(2, 2));
    add(importXML, cc.xy(2, 3));
    
    // Actions
    add(new JLabel("Ajouter le Recalage"), cc.xy(4, 5));
    add(new JLabel("Exporter les liens"), cc.xy(4, 6));
    add(new JLabel("Transfert des attributs"), cc.xy(4, 7));
    
    // Enregistrer
    add(new JLabel("Enregistrer les résultats"), cc.xy(4, 9));
    
  }
  
  /**
   * Actions : launch and cancel.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    
    if (source == exportXML) {
      paramPanel.setParameters();
      exportXML();
    }
  }
  
  /**
   * Display parameters in XML format for export.
   */
  private void exportXML() {
    try {
      
      ExportXMLWindow w = new ExportXMLWindow(paramPanel.getNetworkDataMatchingPlugin().getParamPlugin());
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
