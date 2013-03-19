package fr.ign.cogit.geoxygene.appli.plugin.datamatching.network;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetwork;

/**
 * Button 1 : see parameters
 * Button 2 : see statistics results
 * Button 3 : detail of link ?? 
 * 
 */
public class DisplayToolBarNetworkDataMatching extends JToolBar implements ActionListener {

  private static final long serialVersionUID = 4791806011051504347L;

  /** Logger. */
  static Logger logger = Logger.getLogger(DisplayToolBarNetworkDataMatching.class.getName());

  private ProjectFrame projectFrame;
  private JToggleButton btnStats;
  private JToggleButton btnLinks;
  private JToggleButton btnParam;
  
  private DisplayStatResultPanel tableauResultat = null;
  private DisplayParamPanel parameterPanel = null;
  
  private ResultNetwork resultNetwork = null;
  private ParametresApp parameterApp = null;

  /**
   * Constructor.
   * 
   * @param projectFrame The ProjectFrame object which contains the Menu Bar.
   */
  public DisplayToolBarNetworkDataMatching(ProjectFrame projectFrame, ResultNetwork resultNetwork, ParametresApp parameterApp) {
    this.projectFrame = projectFrame;
    this.resultNetwork = resultNetwork;
    this.parameterApp = parameterApp;
    
    init();
  }
  
  /**
   * 
   */
  private void init() {
    
    btnParam = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/wrench.png")));
    btnParam.addActionListener(this);
    btnParam.setToolTipText("Afficher les paramètres");
    add(btnParam);
    
    btnStats = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/sum.png")));
    btnStats.addActionListener(this);
    btnStats.setToolTipText("Afficher les résultats");
    add(btnStats);
    
    btnLinks = new JToggleButton(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/link.png")));
    btnLinks.setToolTipText("Bientôt disponible");
    add(btnLinks);
    
    parameterPanel = new DisplayParamPanel(parameterApp);
    tableauResultat = new DisplayStatResultPanel(resultNetwork);
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    
    if (e.getSource() == btnStats) {
      if (!btnStats.isSelected()) {
          // Hide stat results
          setStatResultToolsVisible(false);
      } else {
          // Hide and unselected others
          setParamToolsVisible(false);
          btnParam.setSelected(false);
          
          // Display the stat results
          setStatResultToolsVisible(true);
      }
    } else if (e.getSource() == btnParam) {
      if (!btnParam.isSelected()) {
        // Hide param
        setParamToolsVisible(false);
      } else {
        // Hide and unselected others
        setStatResultToolsVisible(false);
        btnStats.setSelected(false);
        // Display param
        setParamToolsVisible(true);
      }
    }
  }
  
  /**
   * 
   * @param b
   */
  public void setStatResultToolsVisible(boolean b) {
    if (b) {
      projectFrame.getContentPane().add(tableauResultat, BorderLayout.SOUTH);
      tableauResultat.setVisible(true);
      projectFrame.validate();
    } else {
      // projectFrame.getContentPane().remove(tableauResultat);
      tableauResultat.setVisible(false);
      projectFrame.validate();
    }
  }
  
  /**
   * 
   * @param b
   */
  public void setParamToolsVisible(boolean b) {
    if (b) {
      projectFrame.getContentPane().add(parameterPanel, BorderLayout.SOUTH);
      parameterPanel.setVisible(true);
      projectFrame.validate();
    } else {
      // projectFrame.getContentPane().remove(parameterPanel);
      parameterPanel.setVisible(false);
      projectFrame.validate();
    }
  }

}
