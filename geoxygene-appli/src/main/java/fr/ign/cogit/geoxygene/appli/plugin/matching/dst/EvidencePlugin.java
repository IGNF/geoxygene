package fr.ign.cogit.geoxygene.appli.plugin.matching.dst;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.FloatingProjectFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui.EvidenceGuiProcess;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;


/**
 * 
 *
 */
public class EvidencePlugin extends AbstractGeOxygeneApplicationPlugin {
  
  private FloatingProjectFrame projectFrame;
  
  private JButton evidenceButton;
  //private JButton configLayerButton;
  // private JButton runDSTButton;
  private JButton matriceConfusionButton;
  // private JButton jeuMasseButton;
  // private JButton razButton;
 
  
  
  /**
   * Initialize the plugin.
   * @param app the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
        
    this.application = application;
    projectFrame = (FloatingProjectFrame) this.application.getMainFrame().getSelectedProjectFrame();
    
    // Blanc
    projectFrame.getMainFrame().getMode().getToolBar().addSeparator();
        
    // Cube
    evidenceButton = new JButton();
    evidenceButton.setIcon(new ImageIcon(EvidencePlugin.class.getResource("/images/icons/application_form_add.png")));
    evidenceButton.setToolTipText("Nouveau Panel pour l'appariement avec fonctions de croyance.");
    evidenceButton.addActionListener(this);
    projectFrame.getMainFrame().getMode().getToolBar().add(evidenceButton);
    
    matriceConfusionButton = new JButton();
    matriceConfusionButton.setIcon(new ImageIcon( EvidenceGuiProcess.class.getResource("/images/icons/table.png")));
    matriceConfusionButton.setToolTipText("Afficher la matrice de confusion");
    matriceConfusionButton.setSelected(false);
    matriceConfusionButton.addActionListener(this);
    projectFrame.getMainFrame().getMode().getToolBar().add(matriceConfusionButton);
      
    /* configLayerButton = new JButton(); configLayerButton.setIcon(new
     * ImageIcon( EvidencePlugin.class
     * .getResource("/images/icons/layers.png")));
     * configLayerButton.setToolTipText("Configurer l'affichage des layers.");
     * configLayerButton.setSelected(false);
     * configLayerButton.addActionListener(this);
     * application.getMainFrame().getMode().getToolBar().add(configLayerButton);
     * 
     * jeuMasseButton = new JButton(); jeuMasseButton.setIcon(new ImageIcon(
     * EvidencePlugin.class .getResource("/images/icons/chart_bar.png")));
     * jeuMasseButton
     * .setToolTipText("Afficher le jeu de masse après fusion des candidats");
     * jeuMasseButton.setSelected(false);
     * jeuMasseButton.addActionListener(this);
     * application.getMainFrame().getMode().getToolBar().add(jeuMasseButton);
     * 
     * razButton = new JButton(); razButton.setIcon(new ImageIcon(
     * EvidencePlugin.class
     * .getResource("/images/icons/arrow_refresh_small.png")));
     * razButton.setToolTipText("Remise à zéro"); razButton.setSelected(false);
     * razButton.addActionListener(this);
     * application.getMainFrame().getMode().getToolBar().add(razButton);
     */
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource().equals(evidenceButton)) {
      
      this.loadData();
      
      @SuppressWarnings("unused")
      EvidenceGuiProcess evidenceGuiProcess = new EvidenceGuiProcess(projectFrame, 
          application.getMainFrame().getSize().getWidth(),
          application.getMainFrame().getSize().getHeight());
    }
  }
  
  public void loadData() {
    try {
      // Dataset 1
      URL jd1URL =  new URL("file", "", "./data/matching-test/col-siberie/jd1_col_siberie.shp");
      IPopulation<IFeature> jd1URLPop = ShapefileReader.read(jd1URL.getPath());
      projectFrame.addUserLayer(jd1URLPop, "Col Sibérie 1", null);
      
      // Dataset 2
      URL jd2URL = new URL("file", "", "./data/matching-test/col-siberie/jd2_col_siberie.shp");
      IPopulation<IFeature> jd2URLPop = ShapefileReader.read(jd2URL.getPath());
      projectFrame.addUserLayer(jd2URLPop, "Col Sibérie 2", null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  

}
