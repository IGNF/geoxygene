package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.io.vector.PostgisManager;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Cette fenêtre permet l'affichage des differents types de donnees chargeables
 * Windows of this class manage the loadinf of different kinds of available data
 */
public class PostGISStoringWindow extends JDialog implements ActionListener {

  JTextField jTFHost;
  JTextField jTFBDD;
  JTextField jTFPort;
  JTextField jTFUserName;
  JTextField jTFMDP;
  JTextField jTFLayerName;
  JTextField jtfSchema;
  
  JCheckBox jcbErase;

  JButton browse = new JButton();

  JButton ok = new JButton();
  JButton cancel = new JButton();
  JButton test = new JButton();

  // Fenetre dans laquelle s'affiche l'objet

  FT_FeatureCollection<IFeature> featColl;

  
  
  public static void main(String[] args){
	  (new PostGISStoringWindow(null)).setVisible(true);
  }
  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Initialisation de la fenetre à partir d'une liste d'entités que l'on
   * souhaite sauvegarder
   * 
   * @param featColl
   */
  public PostGISStoringWindow(FT_FeatureCollection<IFeature> featColl) {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.featColl = featColl;
    // Titre
    this.setTitle(Messages.getString("FenetreSauvegarde.Titre")); //$NON-NLS-1$
    this.setLayout(null);

    // Formulaire du type
    JLabel labelHote = new JLabel();
    labelHote.setBounds(10, 10, 100, 20);
    labelHote.setText("Host"); //$NON-NLS-1$
    this.add(labelHote);

    this.jTFHost = new JTextField("");
    this.jTFHost.setBounds(160, 10, 200, 20);
    this.jTFHost.setVisible(true);
    this.jTFHost.addActionListener(this);
    this.add(this.jTFHost);

    // Formulaire du type
    JLabel labelPort = new JLabel();
    labelPort.setBounds(10, 50, 100, 20);
    labelPort.setText("Port"); //$NON-NLS-1$
    this.add(labelPort);

    this.jTFPort = new JTextField("");
    this.jTFPort.setBounds(160, 50, 200, 20);
    this.jTFPort.setVisible(true);
    this.jTFPort.addActionListener(this);
    this.add(this.jTFPort);

    // Formulaire du type
    JLabel labelBDD = new JLabel();
    labelBDD.setBounds(10, 90, 170, 20);
    labelBDD.setText("Database"); //$NON-NLS-1$
    this.add(labelBDD);

    this.jTFBDD = new JTextField("");
    this.jTFBDD.setBounds(160, 90, 200, 20);
    this.jTFBDD.setVisible(true);
    this.jTFBDD.addActionListener(this);
    this.add(this.jTFBDD);
    
    JLabel labelSchema = new JLabel();
    labelSchema.setBounds(10, 130, 100, 20);
    labelSchema.setText("Schema"); //$NON-NLS-1$
    this.add(labelSchema);

    this.jtfSchema = new JTextField("public");
    this.jtfSchema.setBounds(160, 130, 200, 20);
    this.jtfSchema.setVisible(true);
    this.jtfSchema.addActionListener(this);
    this.add(this.jtfSchema);


    // Formulaire du type
    JLabel labelNomUser = new JLabel();
    labelNomUser.setBounds(10, 170, 100, 20);
    labelNomUser.setText("User"); //$NON-NLS-1$
    this.add(labelNomUser);

    this.jTFUserName = new JTextField("");
    this.jTFUserName.setBounds(160, 170, 200, 20);
    this.jTFUserName.setVisible(true);
    this.jTFUserName.addActionListener(this);
    this.add(this.jTFUserName);

    // Formulaire du type
    JLabel labelMDP = new JLabel();
    labelMDP.setBounds(10, 210, 100, 20);
    labelMDP.setText("Password"); //$NON-NLS-1$
    this.add(labelMDP);

    this.jTFMDP = new JPasswordField("");
    this.jTFMDP.setBounds(160, 210, 200, 20);
    this.jTFMDP.setVisible(true);
    this.jTFMDP.addActionListener(this);
    this.add(this.jTFMDP);

    // Formulaire du type
    JLabel labelNCouche = new JLabel();
    labelNCouche.setBounds(10, 250, 100, 20);
    labelNCouche.setText("Table"); //$NON-NLS-1$
    this.add(labelNCouche);

    this.jTFLayerName = new JTextField("");
    this.jTFLayerName.setBounds(160, 250, 200, 20);
    this.jTFLayerName.setVisible(true);
    this.jTFLayerName.addActionListener(this);
    this.add(this.jTFLayerName);

    this.jcbErase = new JCheckBox(
        Messages.getString("FenetreSauvegerde.SuppTable"));
    this.jcbErase.setBounds(10, 290, 200, 20);
    this.jcbErase.setVisible(true);
    this.add(this.jcbErase);

    // Boutons de validations
    this.ok.setBounds(50, 330, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(150, 330, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.test.setBounds(250, 330, 100, 20);
    this.test.setText("Test"); //$NON-NLS-1$
    this.test.addActionListener(this);
    this.add(this.test);

    this.setSize(400, 360);

  }

  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // Changement de valeur on efface le texte

    // bouton d'annulation
    if (source.equals(this.cancel)) {
      this.dispose();
    }
    // On récupère les informations
    // On envoie de serreurs
    String host = this.jTFHost.getText();
    String database = this.jTFBDD.getText();
    String port = this.jTFPort.getText();
    String user = this.jTFUserName.getText();
    String schema = this.jtfSchema.getText();
    String pw = this.jTFMDP.getText();
    String table = this.jTFLayerName.getText();

    if (host.isEmpty() || database.isEmpty() || port.isEmpty()
        || user.isEmpty() || pw.isEmpty() || table.isEmpty()) {

      JOptionPane
          .showMessageDialog(
              null,
              Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
              Messages.getString("FenetreSauvegarde.Titre"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
    }
    // Bouton de validation
    if (source.equals(this.ok)) {

      try {

        PostgisManager.saveGeometricTable(host, port, database, schema, table,  user, pw,
            this.featColl, this.jcbErase.isSelected());
      } catch (Exception e) {
        // Erreur de connexion
        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("FenetreSauvegarde.Fail") + " : \n" + e.getLocalizedMessage(), //$NON-NLS-1$
                Messages.getString("FenetreSauvegarde.Titre"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        e.printStackTrace();

        return;
      }

      JOptionPane
          .showMessageDialog(
              null,
              Messages.getString("FenetreSauvegarde.Success"), //$NON-NLS-1$
              Messages.getString("FenetreSauvegarde.Titre"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$

      return;
    }

    if (source.equals(this.test)) {
      try {
        PostgisManager.tableWithGeom(host, port, database, schema, user, pw);

        // Avec couche géométrique
        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("PostGIS.Success"), //$NON-NLS-1$
                Messages.getString("FenetreSauvegarde.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

      } catch (Exception e) {
        // Erreur de connexion
        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("PostGIS.Fail") + " : \n" + e.getLocalizedMessage(), //$NON-NLS-1$
                Messages.getString("FenetreSauvegarde.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        e.printStackTrace();
      }
      return;

    }

  }

}
