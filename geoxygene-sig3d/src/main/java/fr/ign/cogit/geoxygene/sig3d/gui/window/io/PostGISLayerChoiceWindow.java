package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
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
 * Fenetre permettant de sélectionner des couches dans une BDD PostGIS Window to
 * select geometric table in PostGIS
 */
public class PostGISLayerChoiceWindow extends JDialog implements ActionListener {
  /**
     * 
     */
  private static final long serialVersionUID = -7947528275568905039L;
  // Paramètres de connexion
  private String host;
  private String database;
  private String port;
  private String user;
  private String pw;

  // La carte ou l'on ajoute les données
  private InterfaceMap3D iCarte3D;

  // La liste de checkbox pour ajouter des couches
  private List<JCheckBox> lCBLayers = new ArrayList<JCheckBox>();

  JButton ok = new JButton();
  JButton cancel = new JButton();

  /**
   * Initialise une fenêtre permettant d'ajouter une couche PostGIS à une carte
   * à partir d'informations nécessaires
   * 
   * @param iMap3D la carte qui se veerra ajoutée la couche
   * @param layerName la liste des noms de couche
   * @param host le nom de l'hote
   * @param database le nom de la base de données
   * @param port le port de la BDD
   * @param user le nom d'utilisateur
   * @param pw le mot de passe
   */
  public PostGISLayerChoiceWindow(InterfaceMap3D iMap3D,
      List<String> layerName, String host, String database, String port,
      String user, String pw) {

    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.host = host;
    this.database = database;
    this.port = port;
    this.user = user;
    this.pw = pw;
    this.iCarte3D = iMap3D;

    // Titre
    this.setTitle(Messages.getString("FenetreChargement.PostGISTitle")); //$NON-NLS-1$
    this.setLayout(null);
    int nbCouches = layerName.size();
    // Panneau mere contenant le panneau scrollable
    JPanel pMother = new JPanel(new GridLayout(1, 1));
    pMother.setSize(450, 700);

    JPanel pContent = new JPanel();

    pContent.setLayout(null);

    pContent.setVisible(true);

    pContent.setSize(350, nbCouches * 30 + 10);
    pContent.setPreferredSize(new Dimension(350, nbCouches * 30 + 10));
    // On créer les checkbox les unes en dessous des autres
    // Par un pas de 30 px
    for (int i = 0; i < nbCouches; i++) {

      JCheckBox jCBTemp = new JCheckBox(layerName.get(i));
      jCBTemp.setSelected(false);
      jCBTemp.setVisible(true);
      jCBTemp.setBounds(10, 10 + 30 * i, 400, 30);
      pContent.add(jCBTemp);
      this.lCBLayers.add(jCBTemp);

    }
    // On crée le scroll pan
    JScrollPane jscrollpan = new JScrollPane(pContent);
    jscrollpan
        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jscrollpan
        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    jscrollpan.setPreferredSize(new Dimension(700, 280));
    jscrollpan.setVisible(true);
    // On ajoute le tout
    pMother.add(jscrollpan);
    this.add(pMother);

    // Boutons de validations
    this.ok.setBounds(140, 750, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(240, 750, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(470, 850);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object o = e.getSource();

    if (o.equals(this.ok)) {
      // On récupère les jcheckbox sélectionnés
      List<String> lCouchesSelec = new ArrayList<String>();

      int nbCouches = this.lCBLayers.size();

      for (int i = 0; i < nbCouches; i++) {
        JCheckBox jCTemp = this.lCBLayers.get(i);
        if (jCTemp.isSelected()) {

          lCouchesSelec.add(jCTemp.getText());
        }
      }

      int nbCouchesSelectionnes = lCouchesSelec.size();

      if (nbCouchesSelectionnes == 0) {

        JOptionPane
            .showMessageDialog(
                null,
                Messages.getString("FenetreChargement.PostGISNoLayer"), //$NON-NLS-1$
                Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }
      // On traite chaque couche sélectionné
      for (int i = 0; i < nbCouchesSelectionnes; i++) {
        String nomActu = lCouchesSelec.get(i);
        IFeatureCollection<IFeature> featColl;
        try {
          // On récupère la collection depuis PG
          featColl = PostgisManager.loadGeometricTable(this.host, this.port,
              this.database, this.user, this.pw, nomActu);
        } catch (Exception ex) {
          JOptionPane
              .showMessageDialog(
                  null,
                  Messages.getString("FenetreChargement.Error") + nomActu + "\n" + ex.getLocalizedMessage(), //$NON-NLS-1$
                  Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          continue;
        }
        // On effectue le même traitement que les shapefiles suivant la
        // dimension
        int dimension = featColl.get(0).getGeom().dimension();

        if (dimension == 2) {

          try {
            (new Feature2DLoadingWindow(this.iCarte3D, featColl))
                .setVisible(true);
          } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            JOptionPane
                .showMessageDialog(
                    null,
                    Messages.getString("FenetreChargement.Error") + nomActu + "\n" + e1.getLocalizedMessage(), //$NON-NLS-1$
                    Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            continue;
          }
        } else {

          int result = JOptionPane.showConfirmDialog(this,
              Messages.getString("FenetreChargement.Keep3DGeomQuestion"), //$NON-NLS-1$
              Messages.getString("3DGIS.Loading"), //$NON-NLS-1$
              JOptionPane.YES_NO_OPTION);

          if (result == JOptionPane.YES_OPTION) {
            try {
              new ShapeFile3DWindow(this.iCarte3D, featColl);
            } catch (Exception e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();

              JOptionPane
                  .showMessageDialog(
                      null,
                      Messages.getString("FenetreChargement.Error") + nomActu + "\n" + e1.getLocalizedMessage(), //$NON-NLS-1$
                      Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
              continue;
            }
          } else if (result == JOptionPane.NO_OPTION) {
            try {
              (new Feature2DLoadingWindow(this.iCarte3D, featColl))
                  .setVisible(true);
            } catch (Exception e1) {

              e1.printStackTrace();

              JOptionPane
                  .showMessageDialog(
                      null,
                      Messages.getString("FenetreChargement.Error") + nomActu + "\n" + e1.getLocalizedMessage(), //$NON-NLS-1$
                      Messages.getString("FenetreChargement.PostGISTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
              continue;
            }
          }
          // On cache la fenetre
          this.dispose();
        }
      }

    }

    if (o.equals(this.cancel)) {
      // On cache la fenetre
      this.dispose();
    }

  }

}
