package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.sig3d.representation.toponym.BasicToponym3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.util.ColorRandom;

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
 * Interface permettant de paramètrer l'affichage des toponymes Une collection
 * d'entités ponctuels est demandée Le toponyme est placé en x,y,z (géométrie 3D
 * requise)
 * 
 * Window to parametrize toponyms rendering
 */
public class ToponymWindow extends JDialog implements ActionListener {
  // Nom de la couche
  JTextField jTFNom;

  // Ascenseur de l'opacite
  JSlider jSOpacite;

  // Il s'agit des options concernant le texte du toponyme
  // soit une valeur fixe jChoixValConst
  // soit une valeur issue des attributs jChoixValAtt
  JTextField jValTFNomConst;
  JComboBox<Object> jListAttrbiText;
  JRadioButton jChoixValAtt;
  JRadioButton jChoixValConst;

  // Il s'agit de la couleur à appliquer au toponyme
  JButton jBCouleur;

  // Il s'agit de la taille
  JTextField jTFTaille;

  // Il s'agit du choix pour police de caractère
  JComboBox<Object> jCBChoixPolice;

  // Il s'agit de choisir
  // choixBillboard le toponyme fera face à la caméra
  // choixRotation le toponyme sera fixe en fonction de valeurs constantes
  JRadioButton choixBillboard;
  JRadioButton choixRotation;

  // Il s'agit de la valeur constante de rotation suivant X
  // au choix valeur constante ou valeur attributaire
  JRadioButton choixAttValX;
  JRadioButton choixConsValX;
  JComboBox<Object> jCBValChoixAttValX;
  JTextField jTFConxValX;

  // Il s'agit de la valeur constante de rotation suivant Y
  // au choix valeur constante ou valeur attributaire
  JRadioButton choixAttValY;
  JRadioButton choixConsValY;
  JComboBox<Object> jCBValChoixAttValY;
  JTextField jTFConxValY;

  // Il s'agit de la valeur constante de rotation suivant Z
  // au choix valeur constante ou valeur attributaire
  JRadioButton choixAttValZ;
  JRadioButton choixConsValZ;
  JComboBox<Object> jCBValChoixAttValZ;
  JTextField jTFConxValZ;

  // Panneau contenant le formulaire concernant la rotation
  JPanel jpan2;

  JButton ok;
  JButton annul;

  IFeatureCollection<IFeature> fCollFeat = null;
  InterfaceMap3D iCarte3D = null;

  private boolean modify;
  private VectorLayer cv;

  /**
   * Constructeur à partir d'une interface dans laquelle le resultat sera ajoute
   * et d'une liste d'entite possedant des geometries ponctuelles sur lesquelles
   * seront appliquer l'affichage en mode toponyme
   * 
   * @param fCollFeat liste d'entites sur lesquels le style sera applique
   * @param iCarte3D l'interface de carte qui recevra le resultat
   */
  public ToponymWindow(IFeatureCollection<IFeature> fCollFeat,
      InterfaceMap3D iCarte3D) {

    super();

    this.modify = false;

    List<GF_AttributeType> lAttributs = fCollFeat.get(0).getFeatureType()
        .getFeatureAttributes();
    int nbElem = lAttributs.size();

    List<String> lNomAttributs = new ArrayList<String>(nbElem);
    List<String> lNomAttributsNumeriques = new ArrayList<String>(nbElem);

    for (int i = 0; i < nbElem; i++) {
      GF_AttributeType att = lAttributs.get(i);

      lNomAttributs.add(att.getMemberName());

      if (att.getValueType().equals("Double")
          || att.getValueType().equals("Integer")
          || att.getValueType().equals("Long")) {
        lNomAttributsNumeriques.add(att.getMemberName());
      }

    }

    this.fCollFeat = fCollFeat;
    this.iCarte3D = iCarte3D;

    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    // Titre
    this.setTitle(Messages.getString("FenetreAjoutCouche.Titre"));
    this.setLayout(null);

    // Formulaire du nom
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 10, 150, 20);
    labelNom.setText(Messages.getString("FenetreAjoutCouche.Titre"));
    this.add(labelNom);

    this.jTFNom = new JTextField(Messages.getString("3DGIS.LayerName")+ (++CountLayer.COUNT));
    this.jTFNom.setBounds(200, 10, 220, 20);
    this.jTFNom.setVisible(true);
    this.jTFNom.addActionListener(this);

    this.jTFNom.setSelectionStart(0);
    this.jTFNom.setSelectionEnd(this.jTFNom.getText().length());
    this.jTFNom.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // TODO : effacer le texte
        String texte = ToponymWindow.this.jTFNom.getText();
        if (texte.equalsIgnoreCase(Messages.getString("3DGIS.LayerName"))) {

          ToponymWindow.this.jTFNom.setText("");
        }

      }

    });
    this.add(this.jTFNom);

    // Ajout d'un JPanel pour le JTree
    JPanel jpan = new JPanel();
    jpan.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreToponyme.TexteAffiche")));
    jpan.setLayout(new GridLayout(2, 2, 2, 2));
    jpan.setBounds(10, 40, 410, 90);

    ButtonGroup rbg = new ButtonGroup();

    this.jChoixValAtt = new JRadioButton(
        Messages.getString("FenetreToponyme.UtilisationAttribut"));
    this.jChoixValAtt.setSelected(true);
    this.jChoixValAtt.addActionListener(this);
    rbg.add(this.jChoixValAtt);
    jpan.add(this.jChoixValAtt);

    this.jListAttrbiText = new JComboBox<Object>(lNomAttributs.toArray());
    jpan.add(this.jListAttrbiText);

    this.jChoixValConst = new JRadioButton(
        Messages.getString("FenetreToponyme.ValeurConstante"));
    this.jChoixValConst.setSelected(false);
    this.jChoixValConst.addActionListener(this);
    rbg.add(this.jChoixValConst);
    jpan.add(this.jChoixValConst);

    this.jValTFNomConst = new JTextField("X");
    this.jValTFNomConst.setEnabled(false);
    jpan.add(this.jValTFNomConst);

    this.add(jpan);

    JLabel labelCouleur = new JLabel(Messages.getString("3DGIS.Color"));
    labelCouleur.setBounds(10, 140, 180, 20);
    this.add(labelCouleur);

    this.jBCouleur = new JButton();
    this.jBCouleur.setBounds(200, 140, 40, 20);
    this.jBCouleur.addActionListener(this);
    this.jBCouleur.setBackground(ColorRandom.getRandomColor());
    this.add(this.jBCouleur);

    JLabel labelTaille = new JLabel(Messages.getString("3DGIS.Taille"));
    labelTaille.setBounds(10, 180, 200, 20);
    this.add(labelTaille);

    this.jTFTaille = new JTextField("50");
    this.jTFTaille.setBounds(200, 180, 220, 20);
    this.jTFTaille.addActionListener(this);
    this.add(this.jTFTaille);

    JLabel labelPolice = new JLabel(Messages.getString("3DGIS.Police"));
    labelPolice.setBounds(10, 220, 200, 20);
    this.add(labelPolice);

    GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontnames = e.getAvailableFontFamilyNames();

    this.jCBChoixPolice = new JComboBox<Object>(fontnames);
    this.jCBChoixPolice.setSelectedItem("Arial");
    this.jCBChoixPolice.setBounds(200, 220, 220, 20);
    this.add(this.jCBChoixPolice);

    // Formulaire pour l'opacité
    JLabel labelTransParence = new JLabel();
    labelTransParence.setBounds(10, 260, 150, 20);
    labelTransParence.setText(Messages.getString("3DGIS.Transparency"));
    labelTransParence.setVisible(true);
    this.add(labelTransParence);

    this.jSOpacite = new JSlider(0, 100, 100);
    this.jSOpacite.setBounds(160, 260, 200, 20);
    this.jSOpacite.setVisible(true);

    this.add(this.jSOpacite);

    this.choixBillboard = new JRadioButton(
        Messages.getString("FenetreToponyme.ToponymeFace"));
    this.choixBillboard.setSelected(true);
    this.choixBillboard.setBounds(10, 300, 300, 20);
    this.choixBillboard.addActionListener(this);
    this.add(this.choixBillboard);

    this.choixRotation = new JRadioButton(
        Messages.getString("FenetreToponyme.AngleTopoFixe"));
    this.choixRotation.setSelected(false);
    this.choixRotation.setBounds(10, 340, 300, 20);
    this.choixRotation.addActionListener(this);
    this.add(this.choixRotation);

    ButtonGroup rbg2 = new ButtonGroup();
    rbg2.add(this.choixBillboard);
    rbg2.add(this.choixRotation);

    this.jpan2 = new JPanel();
    this.jpan2.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreToponyme.RotTopo")));
    this.jpan2.setLayout(new GridLayout(6, 4));
    this.jpan2.setBounds(10, 380, 410, 200);
    this.jpan2.setVisible(false);

    JLabel valAngleX = new JLabel(Messages.getString("3DGIS.Axe") + " X");
    valAngleX.setSize(50, 30);
    this.jpan2.add(valAngleX);
    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());

    this.choixConsValX = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue"));
    this.choixConsValX.setSelected(true);
    this.choixConsValX.addActionListener(this);
    this.jpan2.add(this.choixConsValX);

    this.jTFConxValX = new JTextField("0");
    this.jpan2.add(this.jTFConxValX);

    this.choixAttValX = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute"));
    this.choixAttValX.addActionListener(this);
    this.jpan2.add(this.choixAttValX);

    this.jCBValChoixAttValX = new JComboBox<Object>(lNomAttributsNumeriques.toArray());
    this.jCBValChoixAttValX.setEnabled(false);
    this.jpan2.add(this.jCBValChoixAttValX);

    ButtonGroup bgX = new ButtonGroup();
    bgX.add(this.choixAttValX);
    bgX.add(this.choixConsValX);

    JLabel valAngleY = new JLabel(Messages.getString("3DGIS.Axe") + " Y");
    this.jpan2.add(valAngleY);

    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());

    this.choixConsValY = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue"));
    this.choixConsValY.addActionListener(this);
    this.choixConsValY.setSelected(true);
    this.jpan2.add(this.choixConsValY);

    this.jTFConxValY = new JTextField("0");
    this.jTFConxValY.addActionListener(this);
    this.jpan2.add(this.jTFConxValY);

    this.choixAttValY = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute"));
    this.choixAttValY.addActionListener(this);
    this.jpan2.add(this.choixAttValY);

    this.jCBValChoixAttValY = new JComboBox<Object>(lNomAttributsNumeriques.toArray());
    this.jCBValChoixAttValY.setEnabled(false);
    this.jpan2.add(this.jCBValChoixAttValY);

    ButtonGroup bgY = new ButtonGroup();
    bgY.add(this.choixAttValY);
    bgY.add(this.choixConsValY);

    JLabel valAngleZ = new JLabel("Axe Z");
    this.jpan2.add(valAngleZ);

    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());
    this.jpan2.add(new JLabel());

    this.choixConsValZ = new JRadioButton(
        Messages.getString("FenetreSymbol.DegreeValue"));
    this.choixConsValZ.setSelected(true);
    this.choixConsValZ.addActionListener(this);
    this.jpan2.add(this.choixConsValZ);

    this.jTFConxValZ = new JTextField("0");
    this.jpan2.add(this.jTFConxValZ);

    this.choixAttValZ = new JRadioButton(
        Messages.getString("FenetreSymbol.Attribute"));
    this.choixAttValZ.addActionListener(this);
    this.jpan2.add(this.choixAttValZ);

    this.jCBValChoixAttValZ = new JComboBox<Object>(lNomAttributsNumeriques.toArray());
    this.jCBValChoixAttValZ.setEnabled(false);
    this.jpan2.add(this.jCBValChoixAttValZ);

    ButtonGroup bgZ = new ButtonGroup();
    bgZ.add(this.choixAttValZ);
    bgZ.add(this.choixConsValZ);

    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(100, 600, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.annul = new JButton();

    this.annul.setBounds(200, 600, 100, 20);
    this.annul.setText(Messages.getString("3DGIS.Cancel"));
    this.annul.addActionListener(this);
    this.add(this.annul);

    this.setBackground(Color.white);
    this.setSize(440, 670);

    this.add(this.jpan2);

  }

  /**
   * crée la fenêtre à partir d'une Couche en prenant en compte les paramètres
   * de la couche
   * 
   * @param cv
   */
  public ToponymWindow(VectorLayer cv) {

    this(cv, null);
    this.cv = cv;
    this.modify = true;

    this.jTFNom.setText(cv.getLayerName());

    Representation rep = cv.get(0).getRepresentation();

    if (rep instanceof BasicToponym3D) {

      BasicToponym3D repToponyme3D = (BasicToponym3D) rep;

      this.jBCouleur.setBackground(repToponyme3D.getColor());
      this.jTFTaille.setText("" + repToponyme3D.getSize());
      this.jSOpacite.setValue((int) (100 * repToponyme3D.getOpacity()));

      if (repToponyme3D.isBillboard()) {
        this.jpan2.setVisible(false);
      } else {
        this.jTFConxValX.setText(repToponyme3D.getLambda() + "");
        this.jTFConxValY.setText(repToponyme3D.getTeta() + "");
        this.jTFConxValZ.setText(repToponyme3D.getPhi() + "");
      }
    }

  }

  /**
   * UID
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object source = e.getSource();

    if (source == this.annul) {
      this.dispose();
      return;
    }

    if (source == this.ok) {

      boolean textValConstante = true;
      String valText = "";
      String valAttText = "";

      if (this.jChoixValConst.isSelected()) {
        // On a décidé de mettre une valeur d'affichage constante
        valText = this.jValTFNomConst.getText();
        if (valText == null) {
          valText = "";
        }

      } else {
        // On va utiliser une valeur attributaire
        valAttText = this.jListAttrbiText.getSelectedItem().toString();
        textValConstante = false;

      }
      // On récupère la couleur à travers la couleur du bouton
      Color couleur = this.jBCouleur.getBackground();

      // On parse le champs taille
      double taille;
      try {
        taille = Double.parseDouble(this.jTFTaille.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreMNT.ValueNotNumber"),
            Messages.getString("FenetreMNT.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }
      // On récupère la police sélectionnée
      String police = this.jCBChoixPolice.getSelectedItem().toString();

      // On récupère l'opacité de l'objet
      double opacite = this.jSOpacite.getValue() / 100.0;

      boolean bFaceCamera = true;

      double RotX = 0;
      double RotY = 0;
      double RotZ = 0;

      boolean hasRotXConst = true;
      boolean hasRotYConst = true;
      boolean hasRotZConst = true;

      String attRotX = "";
      String attRotY = "";
      String attRotZ = "";

      if (this.choixRotation.isSelected()) {// Une rotation est définie pour
        // les toponymes
        bFaceCamera = false;

        if (this.choixConsValX.isSelected()) {
          // Valeur constante de rotation pour X
          try {
            RotX = Double.parseDouble(this.jTFConxValX.getText());

          } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreMNT.ValueNotNumber"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }

        } else {
          // Valeur en fonction d'un champ pour X
          hasRotXConst = false;

          if (this.jCBValChoixAttValZ.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreSymbol.UncorrectValue"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }

          attRotX = this.jCBValChoixAttValX.getSelectedItem().toString();

        }

        if (this.choixConsValY.isSelected()) {
          // Valeur constante de rotation pour Y
          try {
            RotY = Double.parseDouble(this.jTFConxValY.getText());

          } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreMNT.ValueNotNumber"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }

        } else {
          // Valeur en fonction d'un champ pour Y
          hasRotYConst = false;

          if (this.jCBValChoixAttValY.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreSymbol.UncorrectValue"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }
          attRotY = this.jCBValChoixAttValY.getSelectedItem().toString();

        }

        if (this.choixConsValZ.isSelected()) {
          // Valeur constante de rotation pour Z
          try {
            RotZ = Double.parseDouble(this.jTFConxValZ.getText());

          } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreMNT.ValueNotNumber"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }

        } else {
          // Valeur en fonction d'un champ pour Z
          hasRotZConst = false;

          if (this.jCBValChoixAttValZ.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                Messages.getString("FenetreSymbol.UncorrectValue"),
                Messages.getString("FenetreMNT.ValidationError"),
                JOptionPane.ERROR_MESSAGE);
            return;

          }

          attRotZ = this.jCBValChoixAttValZ.getSelectedItem().toString();

        }

      }

      int nbElem;
      /*
       * Si le mode modification est utilisé alors on change les représentation
       * de la couche déjà affichée
       */
      if (this.modify) {

        nbElem = this.cv.size();
      } else {

        nbElem = this.fCollFeat.size();
      }

      for (int i = 0; i < nbElem; i++) {

        IFeature feat;

        if (this.modify) {
          feat = this.cv.get(i);

        } else {

          feat = this.fCollFeat.get(i);
        }

        String textTemp = "";
        if (textValConstante) {
          textTemp = "" + valText;

        } else {

          textTemp = "" + feat.getAttribute(valAttText);
        }

        double lambda = 0;
        double teta = 0;
        double phi = 0;
        /*
         * On applique les coefficients de rotations en fonction du champs
         * sélectionné ou de la valeur ajoutée
         */
        if (!bFaceCamera) {

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

        }
        // On applique la représentation
        
        if(feat.getGeom()!=null&&!feat.getGeom().isEmpty()){
            feat.setRepresentation(new BasicToponym3D(feat, couleur, opacite,
                    Math.PI * lambda / 180, Math.PI * teta / 180, Math.PI * phi / 180,
                    textTemp, police, taille, bFaceCamera));
        }
   

      }

      if (this.modify) {
        // Si le mode modification est activé on applique un
        // rafraichissement de l'écran
        this.cv.setLayerName("" + this.jTFNom.getText());
        this.cv.refresh();
      } else {
        // Sinon on crée une nouvelle couche
        VectorLayer cv = new VectorLayer(this.fCollFeat, ""
            + this.jTFNom.getText());
        this.iCarte3D.getCurrent3DMap().addLayer(cv);
      }

      this.dispose();
      return;
    }

    // Affiche la fenetre de choix de couleur
    // si on clique sur le bouton
    if (source == this.jBCouleur) {

      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null);

      if (couleur == null) {
        return;
      }

      this.jBCouleur.setBackground(couleur);

      return;
    }

    /*
     * Gestion de l'affichage des différents éléments en fonction du bouton
     * radio sélectionné
     */

    if (source == this.jChoixValConst) {
      this.jListAttrbiText.setEnabled(false);
      this.jValTFNomConst.setEnabled(true);
      return;

    }

    if (source == this.jChoixValAtt) {

      this.jListAttrbiText.setEnabled(true);
      this.jValTFNomConst.setEnabled(false);
      return;

    }

    if (source == this.choixBillboard) {

      if (this.choixBillboard.isSelected()) {

        this.jpan2.setVisible(false);
        return;
      }
    }

    if (source == this.choixRotation) {

      if (this.choixRotation.isSelected()) {

        this.jpan2.setVisible(true);
        return;
      }
    }

    if (source == this.choixAttValX) {

      if (this.choixAttValX.isSelected()) {
        this.jTFConxValX.setEnabled(false);
        this.jCBValChoixAttValX.setEnabled(true);
        return;
      }
    }

    if (source == this.choixConsValX) {

      if (this.choixConsValX.isSelected()) {
        this.jTFConxValX.setEnabled(true);
        this.jCBValChoixAttValX.setEnabled(false);
        return;
      }
    }

    if (source == this.choixAttValX) {

      if (this.choixAttValX.isSelected()) {
        this.jTFConxValX.setEnabled(false);
        this.jCBValChoixAttValX.setEnabled(true);
        return;
      }
    }

    if (source == this.choixConsValX) {

      if (this.choixConsValX.isSelected()) {
        this.jTFConxValX.setEnabled(true);
        this.jCBValChoixAttValX.setEnabled(false);
        return;
      }
    }

    if (source == this.choixAttValY) {

      if (this.choixAttValY.isSelected()) {
        this.jTFConxValY.setEnabled(false);
        this.jCBValChoixAttValY.setEnabled(true);
        return;
      }
    }

    if (source == this.choixConsValY) {

      if (this.choixConsValY.isSelected()) {
        this.jTFConxValY.setEnabled(true);
        this.jCBValChoixAttValY.setEnabled(false);
        return;
      }
    }

    if (source == this.choixAttValZ) {

      if (this.choixAttValZ.isSelected()) {
        this.jTFConxValZ.setEnabled(false);
        this.jCBValChoixAttValZ.setEnabled(true);
        return;
      }
    }

    if (source == this.choixConsValZ) {

      if (this.choixConsValZ.isSelected()) {
        this.jTFConxValZ.setEnabled(true);
        this.jCBValChoixAttValZ.setEnabled(false);
        return;
      }
    }

  }
}
