package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.ModellingFilter;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.RepresentationModel;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

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
 * Interface permettant de paramètrer l'affichage des toponymes et ajoute le
 * resultat dans la carte de l'interface visible Une collection d'entités
 * ponctuels est demandée Le toponyme est placé en x,y,z (géométrie 3D requise)
 */
public class SymbolWindow extends JDialog implements ActionListener,
    ChangeListener {
  // Nom de la couche
  JTextField jTFName;

  // Il s'agit des paramètres utilisés si le fichier est le même pour chaque
  // entité
  private JTextField jTFPath;
  private JRadioButton jrbConstantPath;

  // Il s'agit des paramètres utilisés si le fichier de modélisation différe
  // selon l'entité
  private JRadioButton jrbAttributePath;
  private JComboBox jcbAttreibutePath;

  // Il s'agit de la valeur constante de rotation suivant X
  // au choix valeur constante ou valeur attributaire
  JRadioButton choiceAttValX;
  JRadioButton choiceConsValX;
  JComboBox jCBValChoiceAttValX;
  JTextField jTFConxValX;

  // Il s'agit de la valeur constante de rotation suivant Y
  // au choix valeur constante ou valeur attributaire
  JRadioButton choiceAttValY;
  JRadioButton choiceConsValY;
  JComboBox jCBValChoiceAttValY;
  JTextField jTFConxValY;

  // Il s'agit de la valeur constante de rotation suivant Z
  // au choix valeur constante ou valeur attributaire
  JRadioButton choixAttValZ;
  JRadioButton choixConsValZ;
  JComboBox jCBValChoiceAttValZ;
  JTextField jTFConxValZ;

  // Il s'agit du coefficient à appliquer à la forme
  JRadioButton choiceConsValHeight;
  JSlider jSTValAtt;

  // Il s'agit de l'attribut qui servira de coefficient
  JComboBox jCBValChoiceAttValHeight;
  JRadioButton choiceAttValHeight;

  JButton ok;
  JButton cance;

  IFeatureCollection<IFeature> featColl = null;
  InterfaceMap3D iMap3D = null;

  // Mode modification
  private boolean modify;
  // Couche qui sera modifée
  private VectorLayer vectorialLayer;

  /**
   * Permet de créer une fenêtre gérant la symbolisation
   * 
   * @param featColl la collection à représenter
   * @param iMap3D la fenêtre dans laquelle on ajoutera la collection
   */
  public SymbolWindow(IFeatureCollection<IFeature> featColl,
      InterfaceMap3D iMap3D) {

    super();

    this.modify = false;
    // On récupère les attributs entites
    List<GF_AttributeType> lAttributs = featColl.get(0).getFeatureType()
        .getFeatureAttributes();
    int nbElem = lAttributs.size();

    List<String> lNomAttributs = new ArrayList<String>(nbElem);
    List<String> lNomAttributsNumeriques = new ArrayList<String>(nbElem);

    for (int i = 0; i < nbElem; i++) {
      GF_AttributeType att = lAttributs.get(i);

      lNomAttributs.add(att.getMemberName());
      // On recupere les attributs de type numérique
      if (att.getValueType().equals("Double") //$NON-NLS-1$
          || att.getValueType().equals("Integer") //$NON-NLS-1$
          || att.getValueType().equals("Long")) { //$NON-NLS-1$
        lNomAttributsNumeriques.add(att.getMemberName());
      }

    }

    this.featColl = featColl;
    this.iMap3D = iMap3D;

    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    // Titre
    this.setTitle(Messages.getString("FenetreSymbol.Title")); //$NON-NLS-1$
    this.setLayout(null);

    // Formulaire du nom
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 10, 150, 20);
    labelNom.setText(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    this.add(labelNom);

    this.jTFName = new JTextField(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    this.jTFName.setBounds(200, 10, 220, 20);
    this.jTFName.setVisible(true);
    this.jTFName.addActionListener(this);

    this.jTFName.setSelectionStart(0);
    this.jTFName.setSelectionEnd(this.jTFName.getText().length());
    this.jTFName.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // TODO : effacer le texte
        String texte = SymbolWindow.this.jTFName.getText();
        if (texte.equalsIgnoreCase(Messages.getString("3DGIS.LayerName"))) { //$NON-NLS-1$

          SymbolWindow.this.jTFName.setText(""); //$NON-NLS-1$
        }

      }

    });
    this.add(this.jTFName);

    ButtonGroup gb = new ButtonGroup();

    JPanel jpan = new JPanel();
    jpan.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreSymbol.ModelingFile"))); //$NON-NLS-1$
    jpan.setLayout(new GridLayout(2, 3));
    jpan.setBounds(10, 40, 410, 90);

    this.jrbConstantPath = new JRadioButton(
        Messages.getString("FenetreSymbol.SameForAll")); //$NON-NLS-1$
    this.jrbConstantPath.setSelected(true);
    this.jrbConstantPath.addActionListener(this);
    jpan.add(this.jrbConstantPath);

    this.jTFPath = new JTextField();
    this.jTFPath.addActionListener(this);
    this.jTFPath.setEnabled(false);
    this.jTFPath.setText(""); //$NON-NLS-1$
    this.jTFPath.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // TODO : effacer le texte
        // JFileChooser homeChooser = new JFileChooser("Mes documents");
        JFileChooser homeChooser = new JFileChooser(Messages
            .getString("3DGIS.HomeDir")); //$NON-NLS-1$

        javax.swing.filechooser.FileFilter filtre = null;
        filtre = new ModellingFilter();

        homeChooser.setAcceptAllFileFilterUsed(false);

        // Un certain type de fichier est acceepté
        homeChooser.addChoosableFileFilter(filtre);

        homeChooser.showOpenDialog(null);

        File file = homeChooser.getSelectedFile();

        if (file == null) {
          return;
        }
        String nomfichier = file.getPath();

        SymbolWindow.this.jTFPath.setText(nomfichier);

      }

    });

    jpan.add(this.jTFPath);

    this.jrbAttributePath = new JRadioButton(
        Messages.getString("FenetreSymbol.ValueAttributedIndicated")); //$NON-NLS-1$
    this.jrbAttributePath.setSelected(false);
    this.jrbAttributePath.addActionListener(this);
    jpan.add(this.jrbAttributePath);

    this.jcbAttreibutePath = new JComboBox(lNomAttributs.toArray());
    this.jcbAttreibutePath.setEnabled(false);
    jpan.add(this.jcbAttreibutePath);

    gb.add(this.jrbConstantPath);
    gb.add(this.jrbAttributePath);

    this.add(jpan);

    JPanel jpan2 = new JPanel();
    jpan2.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreSymbol.LabellingRotation"))); //$NON-NLS-1$
    jpan2.setLayout(new GridLayout(6, 4));
    jpan2.setBounds(10, 160, 410, 200);

    JLabel valAngleX = new JLabel(Messages.getString("3DGIS.Axe") + " X"); //$NON-NLS-1$
    valAngleX.setSize(50, 30);
    jpan2.add(valAngleX);
    jpan2.add(new JLabel());
    jpan2.add(new JLabel());
    jpan2.add(new JLabel());

    this.choiceConsValX = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue")); //$NON-NLS-1$
    this.choiceConsValX.setSelected(true);
    this.choiceConsValX.addActionListener(this);
    jpan2.add(this.choiceConsValX);

    this.jTFConxValX = new JTextField("0"); //$NON-NLS-1$
    jpan2.add(this.jTFConxValX);

    this.choiceAttValX = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute")); //$NON-NLS-1$
    this.choiceAttValX.addActionListener(this);
    jpan2.add(this.choiceAttValX);

    this.jCBValChoiceAttValX = new JComboBox(lNomAttributsNumeriques.toArray());
    this.jCBValChoiceAttValX.setEnabled(false);
    jpan2.add(this.jCBValChoiceAttValX);

    ButtonGroup bgX = new ButtonGroup();
    bgX.add(this.choiceAttValX);
    bgX.add(this.choiceConsValX);

    JLabel valAngleY = new JLabel(Messages.getString("3DGIS.Axe") + " Y"); //$NON-NLS-1$
    jpan2.add(valAngleY);

    jpan2.add(new JLabel());
    jpan2.add(new JLabel());
    jpan2.add(new JLabel());

    this.choiceConsValY = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue")); //$NON-NLS-1$
    this.choiceConsValY.addActionListener(this);
    this.choiceConsValY.setSelected(true);
    jpan2.add(this.choiceConsValY);

    this.jTFConxValY = new JTextField("0"); //$NON-NLS-1$
    this.jTFConxValY.addActionListener(this);
    jpan2.add(this.jTFConxValY);

    this.choiceAttValY = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute")); //$NON-NLS-1$
    this.choiceAttValY.addActionListener(this);
    jpan2.add(this.choiceAttValY);

    this.jCBValChoiceAttValY = new JComboBox(lNomAttributsNumeriques.toArray());
    this.jCBValChoiceAttValY.setEnabled(false);
    jpan2.add(this.jCBValChoiceAttValY);

    ButtonGroup bgY = new ButtonGroup();
    bgY.add(this.choiceAttValY);
    bgY.add(this.choiceConsValY);

    JLabel valAngleZ = new JLabel(Messages.getString("3DGIS.Axe") + " Z"); //$NON-NLS-1$
    jpan2.add(valAngleZ);

    jpan2.add(new JLabel());
    jpan2.add(new JLabel());
    jpan2.add(new JLabel());

    this.choixConsValZ = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue")); //$NON-NLS-1$
    this.choixConsValZ.setSelected(true);
    this.choixConsValZ.addActionListener(this);
    jpan2.add(this.choixConsValZ);

    this.jTFConxValZ = new JTextField("0"); //$NON-NLS-1$
    jpan2.add(this.jTFConxValZ);

    this.choixAttValZ = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute")); //$NON-NLS-1$
    this.choixAttValZ.addActionListener(this);
    jpan2.add(this.choixAttValZ);

    this.jCBValChoiceAttValZ = new JComboBox(lNomAttributsNumeriques.toArray());
    this.jCBValChoiceAttValZ.setEnabled(false);
    jpan2.add(this.jCBValChoiceAttValZ);

    ButtonGroup bgZ = new ButtonGroup();
    bgZ.add(this.choixAttValZ);
    bgZ.add(this.choixConsValZ);

    JPanel jpan3 = new JPanel();
    jpan3.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreSymbol.ScaleFactor"))); //$NON-NLS-1$
    jpan3.setLayout(new GridLayout(2, 2));
    jpan3.setBounds(10, 380, 410, 80);

    this.choiceConsValHeight = new JRadioButton(
        Messages.getString("FenetreSymbol.Steady")); //$NON-NLS-1$
    this.choiceConsValHeight.setSelected(true);
    this.choiceConsValHeight.addActionListener(this);
    jpan3.add(this.choiceConsValHeight);

    this.jSTValAtt = new JSlider(0, 100, 1);
    this.jSTValAtt.addChangeListener(this);
    this.jSTValAtt.setVisible(true);
    jpan3.add(this.jSTValAtt);

    this.choiceAttValHeight = new JRadioButton(
        Messages.getString("FenetreSymbol.ValueAttributed")); //$NON-NLS-1$
    this.choiceAttValHeight.setSelected(false);
    this.choiceAttValHeight.addActionListener(this);
    jpan3.add(this.choiceAttValHeight);

    this.jCBValChoiceAttValHeight = new JComboBox(
        lNomAttributsNumeriques.toArray());
    this.jCBValChoiceAttValHeight.setEnabled(false);
    jpan3.add(this.jCBValChoiceAttValHeight);

    this.add(jpan3);

    ButtonGroup bg3 = new ButtonGroup();
    bg3.add(this.choiceAttValHeight);
    bg3.add(this.choiceConsValHeight);

    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(100, 480, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cance = new JButton();

    this.cance.setBounds(200, 480, 100, 20);
    this.cance.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cance.addActionListener(this);
    this.add(this.cance);

    this.setSize(440, 540);

    this.add(jpan2);

  }

  /**
   * Ouverture en mode modification. La représentation de la couche vecteur en
   * paramètre sera modifiée
   * 
   * @param vectorialLayer Couche dont la représentation sera modifiée
   */
  public SymbolWindow(VectorLayer vectorialLayer) {

    this(vectorialLayer, null);
    this.jTFName.setText(vectorialLayer.getLayerName());

    this.vectorialLayer = vectorialLayer;
    this.modify = true;

    Representation rep = vectorialLayer.get(0).getRepresentation();
    // On instancie les différents changemetns

    if (rep instanceof RepresentationModel) {
      RepresentationModel repM = (RepresentationModel) rep;

      this.jTFPath.setText(repM.getPath());
      this.jTFConxValX.setText(repM.getAngleRotX() + "");
      this.jTFConxValX.setText(repM.getAngleRotY() + "");
      this.jTFConxValX.setText(repM.getAngleRotZ() + "");
      this.jSTValAtt.setValue((int) repM.getScaleFactor());

    }

  }

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object source = e.getSource();

    if (source == this.cance) {
      this.dispose();
      return;
    }

    if (source == this.ok) {

      boolean bCheminConstant = true;
      String path = ""; //$NON-NLS-1$
      String attPath = ""; //$NON-NLS-1$

      // On regarde si le symbole dépend de l'entite
      if (this.jrbConstantPath.isSelected()) {

        path = "" + this.jTFPath.getText(); //$NON-NLS-1$
      } else {
        Object obj = this.jcbAttreibutePath.getSelectedItem();
        bCheminConstant = false;
        if (obj == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }
        attPath = "" + obj.toString(); //$NON-NLS-1$
      }

      double hauteur = 0;
      String attHauteur = ""; //$NON-NLS-1$
      boolean hasHauteurConst = this.choiceConsValHeight.isSelected();
      // On regarde si la hauteur du symbole est constante ou depend d'un
      // attribut
      if (hasHauteurConst) {

        hauteur = this.jSTValAtt.getValue();

      } else {

        if (this.jCBValChoiceAttValHeight.getSelectedItem() == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }
        attHauteur = this.jCBValChoiceAttValHeight.getSelectedItem().toString();
      }

      double RotX = 0;
      double RotY = 0;
      double RotZ = 0;

      boolean hasRotXConst = true;
      boolean hasRotYConst = true;
      boolean hasRotZConst = true;

      String attRotX = ""; //$NON-NLS-1$
      String attRotY = ""; //$NON-NLS-1$
      String attRotZ = ""; //$NON-NLS-1$

      // On regarde quels sont les parametres de rotations à appliquer
      if (this.choiceConsValX.isSelected()) {
        // Valeur constante de rotation autour de X
        try {
          RotX = Double.parseDouble(this.jTFConxValX.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.NonNumericValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

      } else {
        // Valeur attributaire de rotation autour de X
        hasRotXConst = false;

        if (this.jCBValChoiceAttValZ.getSelectedItem() == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        attRotX = this.jCBValChoiceAttValX.getSelectedItem().toString();

      }

      if (this.choiceConsValY.isSelected()) {
        // Valeur constante de rotation autour de Y
        try {
          RotY = Double.parseDouble(this.jTFConxValY.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.NonNumericValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

      } else {
        // Valeur attributaire de rotation autour de Y
        hasRotYConst = false;

        if (this.jCBValChoiceAttValY.getSelectedItem() == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }
        attRotY = this.jCBValChoiceAttValY.getSelectedItem().toString();

      }

      if (this.choixConsValZ.isSelected()) {
        // Valeur constante de rotation autour de Z
        try {
          RotZ = Double.parseDouble(this.jTFConxValZ.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.NonNumericValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

      } else {
        // Valeur attributaire de rotation autour de Z
        hasRotZConst = false;
        if (this.jCBValChoiceAttValZ.getSelectedItem() == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        attRotZ = this.jCBValChoiceAttValZ.getSelectedItem().toString();

      }

      int nbElem;
      // Si nous sommes en mode modify alors la representation de la
      // couche est modifiee
      if (this.modify) {

        nbElem = this.vectorialLayer.size();
      } else {

        nbElem = this.featColl.size();
      }

      if (bCheminConstant) {

        int index = path.lastIndexOf("."); //$NON-NLS-1$

        if (index == -1) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreSymbol.UncorrectValue"), //$NON-NLS-1$
                  Messages.getString("FenetreSymbol.ValidatorError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

      }

      for (int i = 0; i < nbElem; i++) {

        IFeature feat;

        if (this.modify) {
          feat = this.vectorialLayer.get(i);

        } else {

          feat = this.featColl.get(i);
        }
        // On charge l'objet en fonction du chemin
        // le manager evite de charger plusieurs fois le même
        if (!bCheminConstant) {

          Object obj = feat.getAttribute(attPath);

          if (obj != null) {
            path = obj.toString();

          }

        }

        // On recupere les parametres de hauteur et de rotation
        if (!hasHauteurConst) {

          Object obj = feat.getAttribute(attHauteur);

          if (obj == null) {
            return;
          }

          hauteur = Double.parseDouble(obj.toString());
        }

        double lambda = 0;
        double teta = 0;
        double phi = 0;

        if (hasRotXConst) {
          lambda = RotX;

        } else {
          lambda = Double.parseDouble(feat.getAttribute(attRotX).toString());

        }

        if (hasRotYConst) {
          teta = RotY;

        } else {
          teta = Double.parseDouble(feat.getAttribute(attRotY).toString());

        }

        if (hasRotZConst) {
          phi = RotZ;

        } else {
          phi = Double.parseDouble(feat.getAttribute(attRotZ).toString());

        }

        feat.setRepresentation(new RepresentationModel(feat, path, lambda
            * Math.PI / 180, teta * Math.PI / 180, phi * Math.PI / 180, hauteur));

      }

      if (this.modify) {
        /*
         * Mode modification on rafraichit la represenatation de la couche
         */
        this.vectorialLayer.setLayerName("" + this.jTFName.getText()); //$NON-NLS-1$
        this.vectorialLayer.refresh();
      } else {
        // Sinon on ajoute une nouvelle couche vectoriell
        VectorLayer cv = new VectorLayer(this.featColl, "" //$NON-NLS-1$
            + this.jTFName.getText());
        this.iMap3D.getCurrent3DMap().addLayer(cv);
      }

      this.dispose();
      return;
    }
    // Gestion de l'affichage en fonction du radiobutton selectionn
    if (source == this.choiceAttValX) {

      if (this.choiceAttValX.isSelected()) {
        this.jTFConxValX.setEnabled(false);
        this.jCBValChoiceAttValX.setEnabled(true);
        return;
      }
    }

    if (source == this.choiceConsValX) {

      if (this.choiceConsValX.isSelected()) {
        this.jTFConxValX.setEnabled(true);
        this.jCBValChoiceAttValX.setEnabled(false);
        return;
      }
    }

    if (source == this.choiceAttValX) {

      if (this.choiceAttValX.isSelected()) {
        this.jTFConxValX.setEnabled(false);
        this.jCBValChoiceAttValX.setEnabled(true);
        return;
      }
    }

    if (source == this.choiceConsValX) {

      if (this.choiceConsValX.isSelected()) {
        this.jTFConxValX.setEnabled(true);
        this.jCBValChoiceAttValX.setEnabled(false);
        return;
      }
    }

    if (source == this.choiceAttValY) {

      if (this.choiceAttValY.isSelected()) {
        this.jTFConxValY.setEnabled(false);
        this.jCBValChoiceAttValY.setEnabled(true);
        return;
      }
    }

    if (source == this.choiceConsValY) {

      if (this.choiceConsValY.isSelected()) {
        this.jTFConxValY.setEnabled(true);
        this.jCBValChoiceAttValY.setEnabled(false);
        return;
      }
    }

    if (source == this.choixAttValZ) {

      if (this.choixAttValZ.isSelected()) {
        this.jTFConxValZ.setEnabled(false);
        this.jCBValChoiceAttValZ.setEnabled(true);
        return;
      }
    }

    if (source == this.choixConsValZ) {

      if (this.choixConsValZ.isSelected()) {
        this.jTFConxValZ.setEnabled(true);
        this.jCBValChoiceAttValZ.setEnabled(false);
        return;
      }
    }

    if (source == this.jrbConstantPath) {

      this.jTFPath.setVisible(true);
      this.jcbAttreibutePath.setEnabled(false);

    }

    if (source == this.jrbAttributePath) {

      this.jTFPath.setVisible(false);
      this.jcbAttreibutePath.setEnabled(true);

    }

    if (source == this.choiceConsValHeight) {

      this.jCBValChoiceAttValHeight.setEnabled(false);
      this.jSTValAtt.setEnabled(true);

    }

    if (source == this.choiceAttValHeight) {
      this.jCBValChoiceAttValHeight.setEnabled(true);
      this.jSTValAtt.setEnabled(false);
    }

  }

  @Override
  public void stateChanged(ChangeEvent e) {
    // Modifie le texte en fonction de la position du slider
    JSlider source = (JSlider) e.getSource();
    int val = source.getValue();

    this.choiceConsValHeight.setText(Messages
        .getString("FenetreSymbol.Steady2") + val); //$NON-NLS-1$

  }

}
