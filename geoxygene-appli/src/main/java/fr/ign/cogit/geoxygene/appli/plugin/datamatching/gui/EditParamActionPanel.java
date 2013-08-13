package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

public class EditParamActionPanel extends JPanel implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** 1 button : import XML parameters files. */
  private JButton importXML = null;
  
  /** Actions */
  private JCheckBox cbRecalage;
  private JCheckBox cbExport;
  private JCheckBox cbTransfert;
  
  private JRadioButton rbExportGeometrieLiens2vers1;
  private JRadioButton rbExportGeometrieLiens1vers2;
  
  
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
    
    ButtonGroup groupe1 = new ButtonGroup();
    
    rbExportGeometrieLiens2vers1 = new JRadioButton("");
    rbExportGeometrieLiens2vers1.addActionListener(this);
    //if (paramDirection1.getOrientationDouble()) {
    rbExportGeometrieLiens2vers1.setSelected(true);
    //}
    groupe1.add(rbExportGeometrieLiens2vers1);
    
    rbExportGeometrieLiens1vers2 = new JRadioButton("");
    rbExportGeometrieLiens1vers2.addActionListener(this);
    //if (paramDirection1.getOrientationDouble()) {
     rbExportGeometrieLiens1vers2.setSelected(false);
    //}
    groupe1.add(rbExportGeometrieLiens1vers2);
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  
  /**
   * 
   */
  private void initPanel() {
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, 10dlu, pref, pref, 10dlu, pref, 40dlu",
        "20dlu, pref, pref, pref, pref, pref, 20dlu, pref, pref, 20dlu, pref, 40dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // Action recalage
    cbRecalage.setSelected(true);
    add(cbRecalage, cc.xy(2, 2));
    add(new JLabel("Ajouter le Recalage"), cc.xy(4, 2));
    
    // Action export liens
    cbExport.setSelected(false);
    add(cbExport, cc.xy(2, 3));
    add(new JLabel("Exporter les liens, la géométrie des liens est calculée : "), cc.xy(4, 3));
    
    // exportGeometrieLiens2vers1
    add(rbExportGeometrieLiens2vers1, cc.xy(5, 4));
    add(new JLabel("des objets 2 vers les objets 1"), cc.xy(7, 4));
    add(rbExportGeometrieLiens1vers2, cc.xy(5, 5));
    add(new JLabel("des objets 1 vers les objets 2"), cc.xy(7, 5));
    
    // Action transfert des attributs
    cbTransfert.setSelected(false);
    add(cbTransfert, cc.xy(2, 6));
    add(new JLabel("Transfert des attributs"), cc.xy(4, 6));
    
    // Import XML
    add(importXML, cc.xyw(5, 11, 3));
    
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
    Frame window=new Frame();
    window.setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/icons/16x16/page_white_add.png")).getImage());
    int returnVal = jFileChooser.showOpenDialog(window);
    // System.out.println("RETOUR = " + returnVal);
    
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
