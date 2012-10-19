package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.awt.GridLayout;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.conversion.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindowFactory;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
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
 * Fenetre permettant de charger des shapefiles et de les integrer en 3D suivant
 * differents mode : - Plaquage sur un MNT - Extrusion et definiont d'un Z
 * (attributaire ou constant)
 * 
 * Window used to load shapefiles and to transform them in 3D
 */
public class Feature2DLoadingWindow extends JDialog implements ActionListener {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;
  IFeatureCollection<IFeature> ftColl;

  // Premiere série de radiobouton
  // Choix dans l'altitude inférieur
  JRadioButton jrbDTMChoice;
  JRadioButton jrbZminChoice;
  JRadioButton jrbMinConstantChoice;

  JComboBox jcbDTMChoice;
  JCheckBox jcbSamplingChoice;
  JComboBox jcbZminChoice;
  JTextField jtfConstMinChoice;

  // Seconde série de radio button
  // permettant de déterminer l'altitude maximale
  JRadioButton jrbZMaxAttChoice;
  JRadioButton jrbZMaxHightChoice;
  JRadioButton jrbHightConstantChoice;

  JComboBox jcbZMaxAttChoice;
  JComboBox jcbZMaxHightChoice;
  JTextField jtfHightMaxChoice;

  // Bouton validation/annulation
  JButton ok = new JButton();
  JButton cancel = new JButton();

  private boolean isCanceled = false;

  /**
   * @param iMap3D la carte dans laquelle les données seront chargées
   * @param file le fichier de chargement (format file:///)
   * @throws Exception erreur de chargement
   */
  public Feature2DLoadingWindow(InterfaceMap3D iMap3D, String file)
      throws Exception {

    this(iMap3D, ShapefileReader.read(file));

  }

  /**
   * @param iMap3D la carte dans laquelle les données seront chargées
   * @param ftFeatureCol la collection que l'on souhaite charger
   * @throws Exception Exception erreur de chargement
   */
  public Feature2DLoadingWindow(InterfaceMap3D iMap3D,
     IFeatureCollection<IFeature> ftFeatureCol) throws Exception {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.setTitle(Messages.getString("FenetreShapeFile.Title")); //$NON-NLS-1$
    this.setLayout(null);

    this.iMap3D = iMap3D;
    this.ftColl = ftFeatureCol;

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

    String[] lAttNum = Feature2DLoadingWindow.getNumericAttributes(this.ftColl);

    // Ajout d'un JPanel pour le JTree
    JPanel jpan = new JPanel();
    jpan.setBounds(10, 10, 410, 150);
    jpan.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreShapeFile.MinimumHeigth"))); //$NON-NLS-1$
    jpan.setLayout(new GridLayout(4, 2));

    this.jrbDTMChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.DTMMapping")); //$NON-NLS-1$
    this.jrbDTMChoice.addActionListener(this);
    this.jrbDTMChoice.setSelected(true);
    jpan.add(this.jrbDTMChoice);

    this.jcbDTMChoice = new JComboBox(lNomMNT.toArray());
    this.jcbDTMChoice.addActionListener(this);
    jpan.add(this.jcbDTMChoice);

    jpan.add(new JLabel());

    this.jcbSamplingChoice = new JCheckBox(
        Messages.getString("FenetreShapeFile.GeometryAdaptation")); //$NON-NLS-1$
    jpan.add(this.jcbSamplingChoice);

    this.jrbZminChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.ZMinAttribute")); //$NON-NLS-1$
    this.jrbZminChoice.addActionListener(this);
    this.jrbZminChoice.setSelected(false);
    jpan.add(this.jrbZminChoice);

    this.jcbZminChoice = new JComboBox(lAttNum);
    this.jcbZminChoice.addActionListener(this);
    this.jcbZminChoice.setEnabled(false);
    jpan.add(this.jcbZminChoice);

    this.jrbMinConstantChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.SteadyHeigth")); //$NON-NLS-1$
    this.jrbMinConstantChoice.addActionListener(this);
    this.jrbMinConstantChoice.setSelected(false);
    jpan.add(this.jrbMinConstantChoice);

    this.jtfConstMinChoice = new JTextField("0"); //$NON-NLS-1$
    this.jtfConstMinChoice.setEnabled(false);
    jpan.add(this.jtfConstMinChoice);

    ButtonGroup bgr = new ButtonGroup();
    bgr.add(this.jrbDTMChoice);
    bgr.add(this.jrbZminChoice);
    bgr.add(this.jrbMinConstantChoice);

    this.add(jpan);

    // Ajout d'un JPanel pour le JTree
    JPanel jpan2 = new JPanel();
    jpan2.setBounds(10, 180, 410, 120);
    jpan2.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreShapeFile.SuperiorHeigth"))); //$NON-NLS-1$
    jpan2.setLayout(new GridLayout(3, 2));

    this.jrbZMaxAttChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.MaximumHeigth")); //$NON-NLS-1$
    this.jrbZMaxAttChoice.addActionListener(this);
    this.jrbZMaxAttChoice.setSelected(true);
    jpan2.add(this.jrbZMaxAttChoice);

    this.jcbZMaxAttChoice = new JComboBox(lAttNum);
    this.jcbZMaxAttChoice.addActionListener(this);
    jpan2.add(this.jcbZMaxAttChoice);

    this.jrbZMaxHightChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.ObjectHeigth")); //$NON-NLS-1$
    this.jrbZMaxHightChoice.addActionListener(this);
    this.jrbZMaxHightChoice.setSelected(false);
    jpan2.add(this.jrbZMaxHightChoice);

    this.jcbZMaxHightChoice = new JComboBox(lAttNum);
    this.jcbZMaxHightChoice.addActionListener(this);
    this.jcbZMaxHightChoice.setEnabled(false);
    jpan2.add(this.jcbZMaxHightChoice);

    this.jrbHightConstantChoice = new JRadioButton(
        Messages.getString("FenetreShapeFile.SteadyObjectHeigth")); //$NON-NLS-1$
    this.jrbHightConstantChoice.addActionListener(this);
    this.jrbHightConstantChoice.setSelected(false);
    jpan2.add(this.jrbHightConstantChoice);

    this.jtfHightMaxChoice = new JTextField("0"); //$NON-NLS-1$
    this.jtfHightMaxChoice.addActionListener(this);
    this.jtfHightMaxChoice.setEnabled(false);
    jpan2.add(this.jtfHightMaxChoice);

    this.add(jpan2);

    ButtonGroup bgr2 = new ButtonGroup();
    bgr2.add(this.jrbZMaxAttChoice);
    bgr2.add(this.jrbZMaxHightChoice);
    bgr2.add(this.jrbHightConstantChoice);

    // Boutons de validations
    this.ok.setBounds(100, 320, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 320, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(450, 380);

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    if (source.equals(this.ok)) {

      boolean zMaxISAtt = false;
      boolean zMaxISHaut = false;

      String valAttAltiMax = ""; //$NON-NLS-1$
      double hauteurConst = 0;

      FT_FeatureCollection<IFeature> featCollFinale = new FT_FeatureCollection<IFeature>();

      // On recupere le facteur de ZMax si il est defini
      if (this.jrbZMaxAttChoice.isSelected()) {
        zMaxISAtt = true;
        zMaxISHaut = false;

        Object selection = this.jcbZMaxAttChoice.getSelectedItem();

        if (selection == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ShapeLoading"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }

        valAttAltiMax = selection.toString();

      }

      // On recupere le facteur hauteur si il est defini
      if (this.jrbZMaxHightChoice.isSelected()) {
        zMaxISAtt = true;
        zMaxISHaut = true;

        Object selection = this.jcbZMaxHightChoice.getSelectedItem();

        if (selection == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ShapeLoading"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }

        valAttAltiMax = selection.toString();

      }

      // On recupere le facteur hauteur si il est constant
      if (this.jrbHightConstantChoice.isSelected()) {
        zMaxISAtt = false;
        zMaxISHaut = true;

        // On gere les mauvaises saisies
        try {
          hauteurConst = Double.parseDouble(this.jtfHightMaxChoice.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.ValueIsNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

      }

      // On recupere le MNT pour hauteur inferieure si il existe
      if (this.jrbDTMChoice.isSelected()) {
        Object obj = this.jcbDTMChoice.getSelectedItem();
        if (obj == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ShapeLoading"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        DTM mnt = (DTM) this.iMap3D.getCurrent3DMap().getLayer(obj.toString());

        int nbElem = this.ftColl.size();

        for (int i = 0; i < nbElem; i++) {
          IFeature feat = null;
          try {
            feat = this.ftColl.get(i).cloneGeom();
          } catch (CloneNotSupportedException e2) {
            e2.printStackTrace();
          }

          if (zMaxISAtt) {

            double zMax = Double.parseDouble(feat.getAttribute(valAttAltiMax)
                .toString());
            try {
              feat.setGeom(mnt.mapGeom(feat.getGeom(), zMax, zMaxISHaut,
                  this.jcbSamplingChoice.isSelected()));
            } catch (Exception e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }

          } else {
            try {
              feat.setGeom(mnt.mapGeom(feat.getGeom(), hauteurConst, true,
                  this.jcbSamplingChoice.isSelected()));
            } catch (Exception e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }

          featCollFinale.add(feat);

        }

      }

      // On recupere l'attribut Zmin si il existe
      if (this.jrbZminChoice.isSelected()) {

        Object selection = this.jcbZminChoice.getSelectedItem();

        if (selection == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.EmptyField"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ShapeLoading"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }

        String attZmin = selection.toString();

        int nbElem = this.ftColl.size();

        for (int i = 0; i < nbElem; i++) {
          IFeature feat = null;
          try {
            feat = (IFeature) this.ftColl.get(i).cloneGeom();
          } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
          }

          double zMin = Double.parseDouble(feat.getAttribute(attZmin)
              .toString());

          if (zMaxISAtt) {

            double zMax = 0;

            if (zMaxISHaut) {

              zMax = zMin
                  + Double.parseDouble(feat.getAttribute(valAttAltiMax)
                      .toString());
            } else {
              zMax = Double.parseDouble(feat.getAttribute(valAttAltiMax)
                  .toString());

            }

            feat.setGeom(Extrusion2DObject.convertFromGeometry(feat.getGeom(),
                zMin, zMax));

          } else {

            feat.setGeom(Extrusion2DObject.convertFromGeometry(feat.getGeom(),
                zMin, zMin + hauteurConst));

          }

          featCollFinale.add(feat);

        }

      }

      // On recupere le Zmin si il est constant
      if (this.jrbMinConstantChoice.isSelected()) {

        double zMin = 0;
        // On gere les mauvaises saisies
        try {
          zMin = Double.parseDouble(this.jtfConstMinChoice.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.ValueIsNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        int nbElem = this.ftColl.size();

        for (int i = 0; i < nbElem; i++) {
          IFeature feat = null;
          try {
            feat = this.ftColl.get(i).cloneGeom();
          } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
          }

          if (zMaxISAtt) {

            double zMax = 0;

            if (zMaxISHaut) {

              zMax = zMin
                  + Double.parseDouble(feat.getAttribute(valAttAltiMax)
                      .toString());
            } else {
              zMax = Double.parseDouble(feat.getAttribute(valAttAltiMax)
                  .toString());

            }

            feat.setGeom(Extrusion2DObject.convertFromGeometry(feat.getGeom(),
                zMin, zMax));

          } else {

            feat.setGeom(Extrusion2DObject.convertFromGeometry(feat.getGeom(),
                zMin, zMin + hauteurConst));

          }

          featCollFinale.add(feat);

        }

      }

      // On affiche un menu correspondant à la dimension
      RepresentationWindow repW = RepresentationWindowFactory.generateDialog(
          this.iMap3D, featCollFinale);

      ((JDialog) repW).setVisible(true);
      if (!repW.isCanceled()) {
        this.dispose();
        this.ftColl.clear();
      }

      return;

    }

    if (source.equals(this.cancel)) {
      this.dispose();
      this.isCanceled = true;

      return;
    }

    /*
     * On gere l'affichage des differents elements en fonction des radio boutons
     */

    if (source.equals(this.jrbDTMChoice)) {
      this.jcbDTMChoice.setEnabled(true);
      this.jcbZminChoice.setEnabled(false);
      this.jtfConstMinChoice.setEnabled(false);
      this.jcbSamplingChoice.setEnabled(true);
      return;

    }

    if (source.equals(this.jrbZminChoice)) {
      this.jcbDTMChoice.setEnabled(false);
      this.jcbZminChoice.setEnabled(true);
      this.jtfConstMinChoice.setEnabled(false);
      this.jcbSamplingChoice.setEnabled(false);
      return;
    }

    if (source.equals(this.jrbMinConstantChoice)) {
      this.jcbDTMChoice.setEnabled(false);
      this.jcbZminChoice.setEnabled(false);
      this.jtfConstMinChoice.setEnabled(true);
      this.jcbSamplingChoice.setEnabled(false);
      return;
    }

    if (source.equals(this.jrbZMaxAttChoice)) {
      this.jcbZMaxAttChoice.setEnabled(true);
      this.jcbZMaxHightChoice.setEnabled(false);
      this.jtfHightMaxChoice.setEnabled(false);
      return;

    }

    if (source.equals(this.jrbZMaxHightChoice)) {
      this.jcbZMaxAttChoice.setEnabled(false);
      this.jcbZMaxHightChoice.setEnabled(true);
      this.jtfHightMaxChoice.setEnabled(false);
      return;

    }

    if (source.equals(this.jrbHightConstantChoice)) {
      this.jcbZMaxAttChoice.setEnabled(false);
      this.jcbZMaxHightChoice.setEnabled(false);
      this.jtfHightMaxChoice.setEnabled(true);
      return;

    }

  }

  /**
   * Permet des retrouvers des attributs numériques
   * 
   * @param lFeatcollection
   * @return
   */
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

  public boolean isCanceled() {
    return this.isCanceled;
  }

}
