package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamPluginNetworkDataMatching;
import fr.ign.parameters.Parameters;

/**
 * 
 * 
 *
 */
public class ExportXMLWindow extends JDialog {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  private ParamPluginNetworkDataMatching paramPlugin = null;
  
  protected JEditorPane editPaneHtml;
  protected JScrollPane jspHtml;
  
  protected JPanel panBoutons;
  protected JButton btFermer;
  private JButton btEnregistrer;
  
  /**
   * 
   * @param p
   */
  public ExportXMLWindow(ParamPluginNetworkDataMatching p) {
    
    // Set parameters object
    paramPlugin = p;
    
    // Initialize frame
    setModal(true);
    setTitle("Export des paramètres saisis au format XML");
    setIconImage(new ImageIcon(ExportXMLWindow.class.getResource("/images/icons/page_white_code.png")).getImage());
    setSize(800, 250);
    setLocation(400, 100);
    
    // initialize buttons panel
    initButtonPanel();
    
    // initialise editorPane
    afficherXML();
    
    // Add 2 panels to the frame
    getContentPane().add(jspHtml, BorderLayout.CENTER);
    getContentPane().add(panBoutons, BorderLayout.SOUTH);
    
    // Add events
    manageEvent();
    
    add(jspHtml);
    
    // Display panel
    pack();
    setVisible(true);
  }
  
  /**
   * Initialize buttons panel : record and close.
   */
  private void initButtonPanel() {
    
    panBoutons = new JPanel();
    panBoutons.setLayout(new FlowLayout (FlowLayout.CENTER));
    
    btFermer = new JButton();
    btFermer.setText("Fermer");
    btEnregistrer = new JButton("Enregistrer sous");
    
    panBoutons.add(btEnregistrer);
    panBoutons.add(btFermer);
    
  }
  
  /**
   * 
   */
  public void afficherXML() {
    
    editPaneHtml = new JEditorPane();
    editPaneHtml.setEditable(false);
    editPaneHtml.setSize(800, 500);
    jspHtml = new JScrollPane(editPaneHtml);
    
    Parameters p = paramPlugin.getParamNetworkDataMatching().convertParamAppToParameters();
    StringWriter sw = new StringWriter();
    p.marshall(sw);
    
    //
    editPaneHtml.setText(sw.toString());
    
  }
  
  /**
   * Manage events : close and record.
   */
  public void manageEvent() {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    btFermer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

}
