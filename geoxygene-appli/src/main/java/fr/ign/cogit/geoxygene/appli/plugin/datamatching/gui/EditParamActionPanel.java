package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class EditParamActionPanel extends JPanel implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** 1 button : import XML parameters files. */
  private JButton importXML = null;
  
  /** Actions */
  private JCheckBox cbRecalage;
  private JCheckBox cbExport;
  private JCheckBox cbTransfert;
  
  private EditParamPanel paramPanel;
 
  /**
   * Constructor.
   * @param paramPanel
   */
  public EditParamActionPanel(EditParamPanel paramPanel) {
    
    this.paramPanel = paramPanel;
    
    importXML = new JButton("import XML");
    importXML.setToolTipText("Import parameters in XML files");
    importXML.addActionListener(this);
    
    cbRecalage = new JCheckBox();
    cbExport = new JCheckBox();
    cbTransfert = new JCheckBox();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  
  /**
   * 
   */
  private void initPanel() {
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, 10dlu, pref, pref, 40dlu",
        "20dlu, pref, pref, pref, 20dlu, pref, pref, 20dlu, pref, 40dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // Action recalage
    cbRecalage.setSelected(true);
    add(cbRecalage, cc.xy(2, 2));
    add(new JLabel("Ajouter le Recalage"), cc.xy(4, 2));
    
    // Action export liens
    cbExport.setSelected(false);
    add(cbExport, cc.xy(2, 3));
    add(new JLabel("Exporter les liens"), cc.xy(4, 3));
    
    // Action transfert des attributs
    cbTransfert.setSelected(false);
    add(cbTransfert, cc.xy(2, 4));
    add(new JLabel("Transfert des attributs"), cc.xy(4, 4));
    
    // Enregistrer
    add(new JLabel("Enregistrer les résultats"), cc.xy(4, 6));
    
    // import XML
    add(importXML, cc.xy(5, 9));
    
  }
  
  /**
   * Actions : launch and cancel.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    
    if (source == importXML) {
      doUpload(importXML);
    }
    
  }
  
  /**
   * Upload file. 
   * @param typeButton 
   */
  private void doUpload(JButton typeButton) {
    
    JFileChooser jFileChooser = new JFileChooser();
    
    jFileChooser.setCurrentDirectory(new File("D:\\Data\\Appariement\\Param"));
    
    // Crée un filtre qui n'accepte que les fichier XML ou les répertoires
    if (typeButton.equals(jFileChooser)) {
      jFileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return (f.isFile()
              && (f.getAbsolutePath().endsWith(".xml") || f.getAbsolutePath()
                  .endsWith(".XML")) || f.isDirectory());
        }
        @Override
        public String getDescription() {
          return "XMLfileReader";
        }
      });
    } 
    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    // Show file dialog
    int returnVal = jFileChooser.showOpenDialog(this);
    System.out.println("RETOUR = " + returnVal);
    
  }
  
  /**
   * 
   * @return
   */
  public Boolean[] valideField() {
    
    Boolean[] actions = new Boolean[3];
    
    Boolean recalage = cbRecalage.isSelected();
    actions[0] = recalage;
    
    Boolean export = cbExport.isSelected();
    actions[1] = export;
    
    Boolean transfert = cbTransfert.isSelected();
    actions[2] = transfert;
    
    return actions;
    
  }

}
