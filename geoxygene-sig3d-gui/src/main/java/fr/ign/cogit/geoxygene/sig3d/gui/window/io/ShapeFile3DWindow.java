package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion3DObject;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;

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
 * Fenetre permettant de charger des shapefiles et de les integrer en 3D suivant
 * differents mode : - Extrusion et definiont d'un Z (attributaire ou constant)
 * Window used to load 3D shapefile
 */
public class ShapeFile3DWindow extends JDialog implements ActionListener {

  private final static Logger logger = LogManager.getLogger(ShapeFile3DWindow.class
      .getName());
  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private boolean isCanceled = false;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;
  IFeatureCollection<IFeature> ftColl;

  JCheckBox jcbExtrudeChoice;
  JLabel jLExtrusion;

  JComboBox<String> jcbAttHightChoice;

  JRadioButton jrbExtruPos;
  JRadioButton jrbExtruNeg;

  // Bouton validation/annulation
  JButton ok = new JButton();
  JButton cancel = new JButton();

  /**
   * Permet de creer la fenetre permettant de charger un shapefile3D
   * 
   * @param iMap3D la carte dans laquelle on chargera le résultat
   * @param ftColl la collection préalablement chargée
   * @throws Exception
   */
  public ShapeFile3DWindow(InterfaceMap3D iMap3D,
      IFeatureCollection<IFeature> ftColl) throws Exception {
    super();
    ShapeFile3DWindow.logger.debug(Messages
        .getString("FenetreShapeFile3D.Opening")); //$NON-NLS-1$
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.setTitle(Messages.getString("FenetreShapeFile3D.Title")); //$NON-NLS-1$
    this.setLayout(null);

    this.iMap3D = iMap3D;

    // On récupère le fichier
    this.ftColl = ftColl;

    // Initialisation des listes des CheckBox
    // On liste les MNT pour pouvoir plaquer dessus la couche vectorielle
    ArrayList<String> lNomMNT = new ArrayList<String>();

    if (iMap3D != null) {
      List<Layer> lCouches = iMap3D.getCurrent3DMap().getLayerList();

      int nbElem = lCouches.size();

      for (int i = 0; i < nbElem; i++) {
        Layer c = lCouches.get(i);

        if (c instanceof DTM) {
          lNomMNT.add(c.getLayerName());

        }

      }

    }

    String[] lAttNum = ShapeFile3DWindow.getNumericAttributes(ftColl);

    this.jcbExtrudeChoice = new JCheckBox(
        Messages.getString("FenetreShapeFile3D.GeometryExtrusion")); //$NON-NLS-1$
    this.jcbExtrudeChoice.setBounds(15, 15, 200, 20);
    this.jcbExtrudeChoice.setSelected(false);
    this.jcbExtrudeChoice.addActionListener(this);
    this.add(this.jcbExtrudeChoice);

    this.jLExtrusion = new JLabel(
        Messages.getString("FenetreShapeFile3D.ObjectHeigth")); //$NON-NLS-1$
    this.jLExtrusion.setBounds(15, 55, 200, 20);
    this.jLExtrusion.setVisible(false);
    this.add(this.jLExtrusion);

    this.jcbAttHightChoice = new JComboBox<String>(lAttNum);
    this.jcbAttHightChoice.addActionListener(this);
    this.jcbAttHightChoice.setVisible(false);
    this.jcbAttHightChoice.setBounds(215, 55, 200, 20);
    this.add(this.jcbAttHightChoice);

    this.jrbExtruPos = new JRadioButton(
        Messages.getString("FenetreShapeFile3D.PositiveExtrusion")); //$NON-NLS-1$
    this.jrbExtruPos.setVisible(false);
    this.jrbExtruPos.setSelected(true);
    this.jrbExtruPos.setBounds(15, 95, 200, 20);
    this.add(this.jrbExtruPos);

    this.jrbExtruNeg = new JRadioButton(
        Messages.getString("FenetreShapeFile3D.NegativeExtrusion")); //$NON-NLS-1$
    this.jrbExtruNeg.setVisible(false);
    this.jrbExtruNeg.setBounds(15, 130, 200, 20);
    this.add(this.jrbExtruNeg);

    ButtonGroup bg = new ButtonGroup();
    bg.add(this.jrbExtruPos);
    bg.add(this.jrbExtruNeg);

    // Boutons de validations
    this.ok.setBounds(100, 200, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 200, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(450, 280);

  }

  /**
   * @return indique si le bouton "annuler" a été pressé
   */
  public boolean isCanceled() {
    return this.isCanceled;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();
    // Pour cacher/afficher certaines parties du formulaire au cas ou l'on
    // souhaite l'extrusion ou non
    if (source.equals(this.jcbExtrudeChoice)) {

      if (this.jcbExtrudeChoice.isSelected()) {

        this.jLExtrusion.setVisible(true);
        this.jcbAttHightChoice.setVisible(true);
        this.jrbExtruNeg.setVisible(true);
        this.jrbExtruPos.setVisible(true);
      } else {
        this.jLExtrusion.setVisible(false);
        this.jcbAttHightChoice.setVisible(false);
        this.jrbExtruNeg.setVisible(false);
        this.jrbExtruPos.setVisible(false);
      }
    }

    if (source.equals(this.ok)) {
      RepresentationWindow repW;
      FT_FeatureCollection<IFeature> featCollFinale = new FT_FeatureCollection<IFeature>();

      if (this.jcbExtrudeChoice.isSelected()) {
        // Cas ou une extrusion est souhaitee
        Object selection = this.jcbAttHightChoice.getSelectedItem();
        this.isCanceled = false;
        if (selection == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile3D.EmptyField"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile3D.ShapeLoading"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          ShapeFile3DWindow.logger.warn(Messages
              .getString("FenetreShapeFile3D.NoNumFieldSelected")); //$NON-NLS-1$
          return;
        }
        int nbElem = this.ftColl.size();
        String valAttHauteur = selection.toString();
        ShapeFile3DWindow.logger.info(Messages
            .getString("FenetreShapeFile3D.ExtrusionCase")); //$NON-NLS-1$
        ShapeFile3DWindow.logger.info(Messages
            .getString("FenetreShapeFile3D.FeatureConversion")); //$NON-NLS-1$
        for (int i = 0; i < nbElem; i++) {
          // On extrude chaque geometrie comme souhaitee
          IFeature feat = null;
          try {
            feat = this.ftColl.get(i).cloneGeom();
          } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
          }

          double hauteur = Double.parseDouble(feat.getAttribute(valAttHauteur)
              .toString());

          if (this.jrbExtruNeg.isSelected()) {

            hauteur = -hauteur;
          }

          feat.setGeom(Extrusion3DObject.conversionFromGeom(feat.getGeom(),
              hauteur));
          featCollFinale.add(feat);

        }
        repW = RepresentationWindowFactory.generateDialog(this.iMap3D,
            featCollFinale);

      } else {
        // Cas ou l'on conserve la geometrie de base
        // ... il n'y a rien a faire avec la version 3D du
        // ShapeFileReader
        ShapeFile3DWindow.logger.info(Messages
            .getString("FenetreShapeFile3D.NoExtrusion")); //$NON-NLS-1$
        repW = RepresentationWindowFactory.generateDialog(this.iMap3D,
            this.ftColl);

      }

      // On affiche un menu correspondant à la dimension

      ((JDialog) repW).setVisible(true);
      if (!repW.isCanceled()) {
        this.dispose();

      }

      return;

    }

    if (source.equals(this.cancel)) {
      this.dispose();
      this.isCanceled = true;
      return;
    }

  }

  private static String[] getNumericAttributes(
      IFeatureCollection<IFeature> lFeatcollection) {
    if (lFeatcollection == null) {
      return new String[] {};
    }

    if (lFeatcollection.size() == 0) {
      return new String[] {};
    }

    IFeature feat = lFeatcollection.get(0);

    GF_FeatureType featType = feat.getFeatureType();

    if (featType == null) {
      return new String[] {};
    }

    List<GF_AttributeType> listeAttributs = featType.getFeatureAttributes();

    int nbAttribut = listeAttributs.size();

    // On recupere les attributs susceptibles d'etres des hauteurs ou des
    // zmin

    int attCount = 0;
    for (int i = 0; i < nbAttribut; i++) {

      GF_AttributeType att = listeAttributs.get(i);

      if (att.getValueType().equalsIgnoreCase("DOUBLE") //$NON-NLS-1$
          || att.getValueType().equalsIgnoreCase("INTEGER") //$NON-NLS-1$
          || att.getValueType().equalsIgnoreCase("LONG")) { //$NON-NLS-1$
        attCount++;
        continue;
      }

    }

    String[] listeAttributsMenu = new String[attCount];

    int indcount = 0;

    for (int i = 0; i < nbAttribut; i++) {

      GF_AttributeType att = listeAttributs.get(i);
      if (att.getValueType().equalsIgnoreCase("DOUBLE") //$NON-NLS-1$
          || att.getValueType().equalsIgnoreCase("INTEGER") //$NON-NLS-1$
          || att.getValueType().equalsIgnoreCase("LONG")) { //$NON-NLS-1$

        listeAttributsMenu[indcount] = att.getMemberName();
        indcount++;

      }
      // Il n'y en a pas d'autres
      if (indcount == attCount) {

        break;
      }

    }

    return listeAttributsMenu;
  }

}
