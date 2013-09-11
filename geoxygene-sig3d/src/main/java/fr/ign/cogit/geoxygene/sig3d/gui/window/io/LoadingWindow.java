package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.CITYGMLFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.DTMAscFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.SHPFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.DTMWindow;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

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
public class LoadingWindow extends JDialog implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 8769447350926959185L;
  
  private final static Logger logger = Logger.getLogger(LoadingWindow.class
      .getName());
  // Nom de la couche
  JComboBox jCBBFormat;

  JTextField jTFPath;

  JButton browse = new JButton();

  JButton ok = new JButton();
  JButton cancel = new JButton();

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;

  private static String[] availableFormats = {
      "CityGML", Messages.getString("3DGIS.ShapeFile"), Messages.getString("3DGIS.DTM") }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ 

  // Les controles

  /**
	 */


  /**
   * Initialisation de la fenetre
   * 
   * @param iMap3D
   */
  public LoadingWindow(InterfaceMap3D iMap3D) {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.iMap3D = iMap3D;

    // Titre
    this.setTitle(Messages.getString("3DGIS.Loading")); //$NON-NLS-1$
    this.setLayout(null);

    // Formulaire du type
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 10, 100, 20);
    labelNom.setText(Messages.getString("3DGIS.FileFormat")); //$NON-NLS-1$
    this.add(labelNom);

    this.jCBBFormat = new JComboBox(LoadingWindow.availableFormats);
    this.jCBBFormat.setBounds(160, 10, 200, 20);
    this.jCBBFormat.setVisible(true);
    this.jCBBFormat.addActionListener(this);
    this.add(this.jCBBFormat);

    this.jTFPath = new JTextField();
    this.jTFPath.setBounds(160, 50, 200, 20);
    this.jTFPath.setVisible(true);
    this.jTFPath.addActionListener(this);
    this.jTFPath.setEnabled(false);
    this.jTFPath.setText(""); //$NON-NLS-1$
    this.add(this.jTFPath);

    // Formulaire du chemin
    JLabel labelChemin = new JLabel();
    labelChemin.setBounds(10, 50, 100, 20);
    labelChemin.setText(Messages.getString("3DGIS.Path")); //$NON-NLS-1$
    this.add(labelChemin);

    this.browse.setBounds(360, 50, 20, 20);
    this.browse.setText(Messages.getString("3DGIS.Browse")); //$NON-NLS-1$
    this.browse.setVisible(true);
    this.browse.addActionListener(this);
    this.add(this.browse);

    // Boutons de validations
    this.ok.setBounds(100, 90, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 90, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(400, 150);

  }

  /**
   * Gestion des actions
   */
  @Override
  @SuppressWarnings("unchecked")
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // Changement de valeur on efface le texte
    if (source.equals(this.jCBBFormat)) {

      this.jTFPath.setText(""); //$NON-NLS-1$

    }

    // Navigateur pour sélectionné un fichier adapté au choix
    if (source.equals(this.browse)) {

      String formatChoisi = (String) this.jCBBFormat.getSelectedItem();

      // Ouvre une fenêtre permettant de choisir un fichier
      // JFileChooser homeChooser = new JFileChooser("Mes documents");
      JFileChooser homeChooser = new JFileChooser(
          Messages.getString("3DGIS.HomeDir")); //$NON-NLS-1$

      javax.swing.filechooser.FileFilter filtre = null;

      if (formatChoisi.equalsIgnoreCase(Messages
          .getString("FenetreChargement.CityGML"))) { //$NON-NLS-1$

        filtre = new CITYGMLFilter();
      } else if (formatChoisi.equalsIgnoreCase(Messages
          .getString("3DGIS.ShapeFile"))) { //$NON-NLS-1$
        filtre = new SHPFilter();

      } else if (formatChoisi.equalsIgnoreCase(Messages.getString("3DGIS.DTM"))) { //$NON-NLS-1$

        filtre = new DTMAscFilter();

      } else {

        LoadingWindow.logger.error(Messages
            .getString("FenetreChargement.UnknownFormat")); //$NON-NLS-1$
        return;

      }

      homeChooser.setAcceptAllFileFilterUsed(false);

      // Un certain type de fichier est acceepté
      homeChooser.addChoosableFileFilter(filtre);

      homeChooser.showOpenDialog(null);

      File file = homeChooser.getSelectedFile();

      if (file == null) {
        return;
      }
      String nomfichier = file.getPath();

      this.jTFPath.setText(nomfichier);

    }

    // bouton de validation
    if (source.equals(this.ok)) {

      // On calcule les paramètres

      String formatChoisi = (String) this.jCBBFormat.getSelectedItem();
      String chemin = this.jTFPath.getText();

      if (chemin.equals("")) { //$NON-NLS-1$
        return;
      }

      try {

        if (formatChoisi.equalsIgnoreCase(Messages
            .getString("FenetreChargement.CityGML"))) { //$NON-NLS-1$

          // Ouverture de la fenêtre de chargement du format CityGML
          (new CityGMLLoadingWindow(this.iMap3D, chemin)).setVisible(true);

        } else if (formatChoisi.equalsIgnoreCase(Messages
            .getString("3DGIS.ShapeFile"))) { //$NON-NLS-1$

          // On recupere le fichier
          FT_FeatureCollection<IFeature> ftColl = (FT_FeatureCollection<IFeature>) ShapefileReader
              .read(chemin);
          // On procède aux tests d'usage
          if (ftColl == null) {
            LoadingWindow.logger.error(Messages.getString("3DGIS.EmptyFile")); //$NON-NLS-1$
            JOptionPane
                .showMessageDialog(
                    this,
                    Messages.getString(Messages
                        .getString("FenetreChargement.26")), Messages.getString("3DGIS.Loading") + " : " + formatChoisi, //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    JOptionPane.ERROR_MESSAGE);
            return;
          }

          if (ftColl.size() == 0) {
            LoadingWindow.logger.error(Messages.getString("3DGIS.EmptyFile")); //$NON-NLS-1$
            JOptionPane
                .showMessageDialog(
                    this,
                    Messages.getString("3DGIS.EmptyFile"), Messages.getString("3DGIS.Loading") + " : " + formatChoisi, //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                    JOptionPane.ERROR_MESSAGE);
            return;
          }

          int dimension = ftColl.get(0).getGeom().coordinateDimension();
          // Indique si la fenêtre suivante est annulée ou non
          boolean isCanceled = false;

          if (dimension == 2) {
            // Géométrie 2D
            Feature2DLoadingWindow f2dlw = new Feature2DLoadingWindow(
                this.iMap3D, ftColl);
            f2dlw.setVisible(true);
            isCanceled = f2dlw.isCanceled();

          } else {

            // Géométrie 3D, l'utilisateur choisi

            int result = JOptionPane.showConfirmDialog(this,
                Messages.getString("FenetreChargement.Keep3DGeomQuestion"), //$NON-NLS-1$
                Messages.getString("3DGIS.Loading"), //$NON-NLS-1$
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
              ShapeFile3DWindow shap = new ShapeFile3DWindow(this.iMap3D,
                  ftColl);
              shap.setVisible(true);
              isCanceled = shap.isCanceled();

            } else if (result == JOptionPane.NO_OPTION) {
              Feature2DLoadingWindow f2dlw = new Feature2DLoadingWindow(
                  this.iMap3D, ftColl);
              f2dlw.setVisible(true);
              isCanceled = f2dlw.isCanceled();
            }
          }
          if (isCanceled) {

            return;
          }
          ftColl.clear();

        } else if (formatChoisi.equalsIgnoreCase(Messages
            .getString("3DGIS.DTM"))) { //$NON-NLS-1$

          DTMWindow fen = new DTMWindow(chemin, this.iMap3D);
          fen.setVisible(true);

        } else {

          LoadingWindow.logger.error(Messages
              .getString("FenetreChargement.UnknownFormat")); //$NON-NLS-1$
          return;

        }

        this.dispose();

      } catch (Exception e) {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreChargement.Error"), Messages.getString("3DGIS.Loading") + " : " + formatChoisi, //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
      }

    }

    // bouton d'annulation
    if (source.equals(this.cancel)) {
      this.dispose();
    }

  }
}
