package fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.TopologieReseauPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.data.ParamDoingTopologicalStructure;
import fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.data.ParamFilenamePopulationEdgesNetwork;

/**
 * 
 * Instanciation de la topologie du reseau (rendu planaire, fusion des noeuds, arcs, ...)
 *
 */
public class DialogTopoStructurePanel extends JDialog implements ActionListener {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 4791806011051504347L;
    
    /** 2 buttons : launch, cancel. */
    private String action;
    private JButton launchButton = null;
    private JButton cancelButton = null;
    
    /** FileUploads Field for uploading Reference shape . */
    private ParamFilenamePopulationEdgesNetwork paramFilename;
    JButton buttonEdgesShape = null;
    JComboBox listShapefileEdgesNetwork = null;
    
    /** Actions */
    private JCheckBox cbRenduPlanaire;
    private JCheckBox cbFusionNoeudProche;
    private JCheckBox cbSuppNoeudIsole;
    private JCheckBox cbFiltreNoeudSimple;
    private JCheckBox cbFusionArcDouble;
    private JCheckBox cbCreationTopologieFace;
    
    private JTextField tolerance = null;
    private JTextField seuilFusion = null;
    
    private TopologieReseauPlugin topoPlugin;
    
    /**
     * Default constructor.
     */
    public DialogTopoStructurePanel(ParamFilenamePopulationEdgesNetwork f, TopologieReseauPlugin tplugin) {
        
        setModal(true);
        setTitle(I18N.getString("CarteTopoPlugin.DoingTopologicalStructure"));
        setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/icons/vector.png")).getImage());
        
        paramFilename = f;
        topoPlugin = tplugin;
        
        // Initialize components
        initComponents();
        
        // Initialize panel
        initPanel();
        
        pack();
        setLocation(500, 250);
        setVisible(true);
    }
    
    /**
     * Initialize all the components of the panel
     */
    private void initComponents() {
      
      // Init ref shape button import files
      buttonEdgesShape = new JButton(I18N.getString("DataMatchingPlugin.Import"));
      buttonEdgesShape.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          doUpload(buttonEdgesShape);
        }
      });
      listShapefileEdgesNetwork = new JComboBox();
      listShapefileEdgesNetwork.setSize(getWidth()*2, getHeight());
      for (int i = 0; i < paramFilename.getListNomFichiersPopArcs().size(); i++) {
        listShapefileEdgesNetwork.addItem(paramFilename.getListNomFichiersPopArcs().get(i));
      }
      
      cbRenduPlanaire = new JCheckBox();
      cbRenduPlanaire.setSelected(true);
      cbFusionNoeudProche = new JCheckBox();
      cbFusionNoeudProche.setSelected(true);
      cbSuppNoeudIsole = new JCheckBox();
      cbSuppNoeudIsole.setSelected(true);
      cbFiltreNoeudSimple = new JCheckBox();
      cbFiltreNoeudSimple.setSelected(true);
      cbFusionArcDouble = new JCheckBox();
      cbFusionArcDouble.setSelected(true);
      cbCreationTopologieFace = new JCheckBox();
      cbCreationTopologieFace.setSelected(true);
      
      tolerance = new JTextField(7);
      tolerance.setText("0.1");
      
      seuilFusion = new JTextField(7);
      seuilFusion.setText("0.1");
      
      launchButton = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
      cancelButton = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
      
      launchButton.addActionListener(this);
      cancelButton.addActionListener(this);
      
    }
    
    /**
     * Initialize panel
     */
    private void initPanel() {
      
      // "Définir le sens de l'axe suivant la valeur d'un attribut"
      // "Tous les axes sont en double sens"
      
      FormLayout layout = new FormLayout(
          "20dlu, right:pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu", // colonnes
          "20dlu, pref, 10dlu, pref, pref, pref, pref, pref, pref, 30dlu, pref, 20dlu");  // lignes
      CellConstraints cc = new CellConstraints();
      setLayout(layout);
      
      // Reseaux
      add(new JLabel("Réseau(x) : "), cc.xy(2, 2));
      add(listShapefileEdgesNetwork, cc.xy(4, 2));
      add(buttonEdgesShape, cc.xy(6, 2));
      add(new JLabel("(.shp)"), cc.xy(8, 2));
      
      // Création de la topologie arc-noeud
      add(cbRenduPlanaire, cc.xy(2, 4));
      add(new JLabel("Création de la topologie arc-noeud"), cc.xy(4, 4));
      add(tolerance, cc.xy(6, 4));
      add(new JLabel("tolérance (m)"), cc.xy(8, 4));
      
      add(cbFusionNoeudProche, cc.xy(2, 5));
      add(new JLabel("Fusion des noeuds proches"), cc.xy(4, 5));
      add(seuilFusion, cc.xy(6, 5));
      add(new JLabel("seuil (m)"), cc.xy(8, 5));
      
      add(cbSuppNoeudIsole, cc.xy(2, 6));
      add(new JLabel("Suppression des noeuds isolés"), cc.xy(4, 6));
      
      add(cbFiltreNoeudSimple, cc.xy(2, 7));
      add(new JLabel("Filtrage des noeuds simples (2 arcs incidents)"), cc.xy(4, 7));
      
      add(cbFusionArcDouble, cc.xy(2, 8));
      add(new JLabel("Fusion des arcs en double"), cc.xy(4, 8));
      
      add(cbCreationTopologieFace, cc.xy(2, 9));
      add(new JLabel("Création de la topologie des arcs"), cc.xy(4, 9));
      
      // 
      add(cancelButton, cc.xy(6, 11));
      add(launchButton, cc.xy(8, 11));
      
    }
    
    /**
     * Actions : launch and cancel.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (source == launchButton) {
        // Init action
        action = "LAUNCH";
      
        // check and init
        setParameters();
      
        dispose();
      } else if (source == cancelButton) {
        // Init action
        action = "CANCEL";
        // do nothing
        dispose();
      }
    }
    
    private void setParameters() {
      
      ParamDoingTopologicalStructure p = new ParamDoingTopologicalStructure();
      
      // Networks
      ParamFilenamePopulationEdgesNetwork paramDataset = new ParamFilenamePopulationEdgesNetwork();
      int count = listShapefileEdgesNetwork.getItemCount();
      for (int i = 0; i < count; i++) {
        String f = listShapefileEdgesNetwork.getItemAt(i).toString();
        paramDataset.addFilename(f);
      }
      p.paramDataset = paramDataset;
      
      p.tolerance = Double.parseDouble(tolerance.getText());
      p.seuilFusion = Double.parseDouble(seuilFusion.getText());
      
      if (cbRenduPlanaire.isSelected()) {
        p.doRenduPlanaire = true;
      } else {
        p.doRenduPlanaire = false;
      }
      
      if (cbFusionNoeudProche.isSelected()) {
        p.doFusionNoeudProche = true;
      } else {
        p.doFusionNoeudProche = false;
      }
      
      if (cbSuppNoeudIsole.isSelected()) {
        p.doSuppNoeudIsole = true;
      } else {
        p.doSuppNoeudIsole = false;
      }
      
      if (cbFiltreNoeudSimple.isSelected()) {
        p.doFiltreNoeudSimple = true;
      } else {
        p.doFiltreNoeudSimple = false;
      }
      
      if (cbFusionArcDouble.isSelected()) {
        p.doFusionArcDouble = true;
      } else {
        p.doFusionArcDouble = false;
      }
      
      if (cbCreationTopologieFace.isSelected()) {
        p.doCreationTopologieFace = true;
      } else {
        p.doCreationTopologieFace = false;
      }
      
      // 
      topoPlugin.setParamDoingTopologicalStructure(p);
    }
    
    /**
     * Upload file. 
     * @param typeButton 
     */
    private void doUpload(JButton typeButton) {

      JFileChooser jFileChooser = new JFileChooser();
      // TODO : utiliser le dernier répertoire ouvert par l'interface. 
      jFileChooser.setCurrentDirectory(new File(paramFilename.getListNomFichiersPopArcs().get(0)));
      
      // Crée un filtre qui n'accepte que les fichier shp ou les répertoires
      if (typeButton.equals(buttonEdgesShape)) {
        jFileChooser.setFileFilter(new FileFilter() {
          @Override
          public boolean accept(File f) {
            return (f.isFile()
                && (f.getAbsolutePath().endsWith(".shp") || f.getAbsolutePath()
                    .endsWith(".SHP")) || f.isDirectory());
          }
          @Override
          public String getDescription() {
            return "ShapefileReader.ESRIShapefiles";
          }
        });
      } 
      jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      
      // Multiple selection
      jFileChooser.setMultiSelectionEnabled(true);

      // Show file dialog
      // int returnVal = jFileChooser.showOpenDialog(this);
      Frame window=new Frame();
      window.setIconImage(new ImageIcon(
              GeOxygeneApplication.class.getResource("/images/icons/16x16/page_white_add.png")).getImage());
      int returnVal = jFileChooser.showOpenDialog(window);
      
      // Initialize textField with the selectedFile
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        
        if (typeButton.equals(buttonEdgesShape)) {
          
          listShapefileEdgesNetwork.removeAllItems();
          for (int i = 0; i < jFileChooser.getSelectedFiles().length; i++) {
            File f = jFileChooser.getSelectedFiles()[i];
            listShapefileEdgesNetwork.addItem(f.getAbsolutePath());
          }
          
        } 
      }

    } // end doUpload method
    
    

    /**
     * @return action of the panel.
     */
    public String getAction() {
      return action;
    }
}
