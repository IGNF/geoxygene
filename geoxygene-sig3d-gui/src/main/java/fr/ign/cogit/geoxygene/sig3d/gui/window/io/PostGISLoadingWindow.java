package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
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
public class PostGISLoadingWindow extends JDialog implements ActionListener {

  JTextField jTFHost;
  JTextField jTFBDD;
  JTextField jTFPort;
  JTextField jTFUser;
  JTextField jTFMDP;

  JButton browse = new JButton();

  JButton ok = new JButton();
  JButton cancel = new JButton();
  JButton test = new JButton();

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iCarte3D;

  // Les controles

  /**
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Initialisation de la fenetre - Elle est rattachée à une carte afin de
   * permettre l'affichage ultérieur des couches
   * 
   * @param iMap3D
   */
  public PostGISLoadingWindow(InterfaceMap3D iMap3D) {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.iCarte3D = iMap3D;

    // Titre
    this.setTitle(Messages.getString("FenetreChargement.PostGISTitle")); //$NON-NLS-1$
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
    labelBDD.setBounds(10, 90, 100, 20);
    labelBDD.setText("Database"); //$NON-NLS-1$
    this.add(labelBDD);

    this.jTFBDD = new JTextField("");
    this.jTFBDD.setBounds(160, 90, 200, 20);
    this.jTFBDD.setVisible(true);
    this.jTFBDD.addActionListener(this);
    this.add(this.jTFBDD);

    // Formulaire du type
    JLabel labelNomUser = new JLabel();
    labelNomUser.setBounds(10, 130, 100, 20);
    labelNomUser.setText("User"); //$NON-NLS-1$
    this.add(labelNomUser);

    this.jTFUser = new JTextField("");
    this.jTFUser.setBounds(160, 130, 200, 20);
    this.jTFUser.setVisible(true);
    this.jTFUser.addActionListener(this);
    this.add(this.jTFUser);

    // Formulaire du type
    JLabel labelMDP = new JLabel();
    labelMDP.setBounds(10, 170, 100, 20);
    labelMDP.setText("Password"); //$NON-NLS-1$
    this.add(labelMDP);

    this.jTFMDP = new JPasswordField("");
    this.jTFMDP.setBounds(160, 170, 200, 20);
    this.jTFMDP.setVisible(true);
    this.jTFMDP.addActionListener(this);
    this.add(this.jTFMDP);

    // Boutons de validations
    this.ok.setBounds(50, 210, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(150, 210, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.test.setBounds(250, 210, 100, 20);
    this.test.setText("Test"); //$NON-NLS-1$
    this.test.addActionListener(this);
    this.add(this.test);

    this.setSize(400, 280);
    this.setVisible(true);
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
    String user = this.jTFUser.getText();
    String pw = this.jTFMDP.getText();

    if (host.isEmpty() || database.isEmpty() || port.isEmpty()
        || user.isEmpty() || pw.isEmpty()) {

      JOptionPane
          .showMessageDialog(
              null,
              Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
              Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
    }
    // Bouton de validation
    if (source.equals(this.ok)) {

      try {
        List<String> lNoms = PostgisManager.tableWithGeom(host, port, database,
            user, pw);

        if (lNoms.size() == 0) {
          // Pas de couche géométrique
          JOptionPane
              .showMessageDialog(
                  null,
                  Messages.getString("FenetreChargement.PostGISNoGeomLayer"), //$NON-NLS-1$
                  Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

          return;
        } else {
          // Avec couche géométrique
          JOptionPane
              .showMessageDialog(
                  null,
                  Messages.getString("PostGIS.Success"), //$NON-NLS-1$
                  Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

          this.dispose();

          (new PostGISLayerChoiceWindow(this.iCarte3D, lNoms, host, database,
              port, user, pw)).setVisible(true);

        }

      } catch (Exception e) {
        // Erreur de connexion
        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("PostGIS.Fail") + " : \n" + e.getLocalizedMessage(), //$NON-NLS-1$
                Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        e.printStackTrace();

        return;
      }

      return;
    }

    if (source.equals(this.test)) {
      try {
        List<String> lNoms = PostgisManager.tableWithGeom(host, port, database,
            user, pw);

        if (lNoms.size() == 0) {
          // Pas de couche géométrique mais connecté
          JOptionPane
              .showMessageDialog(
                  null,
                  Messages.getString("FenetreChargement.PostGISNoGeomLayer"), //$NON-NLS-1$
                  Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        } else {
          // Avec couche géométrique
          JOptionPane
              .showMessageDialog(
                  null,
                  Messages.getString("PostGIS.Success"), //$NON-NLS-1$
                  Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

        }

      } catch (Exception e) {
        // Erreur de connexion
        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("PostGIS.Fail") + " : \n" + e.getLocalizedMessage(), //$NON-NLS-1$
                Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        e.printStackTrace();
      }
      return;

    }

  }

}
