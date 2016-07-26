package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice.RepresentationWindow;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.BasicRep3D;
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
 * Fenetre gêrant l'ajout de couches ainsi que les différentes options
 * d'affichage Window which manages the addition of layers
 */
public class OneColoredLayerWindow extends JDialog implements ActionListener,
    RepresentationWindow {

  boolean isCanceled;
  // Ce boolean passe à true quand il s'agit de la mise a jour (graphique
  // d'une couche)
  boolean modify = false;
  // Nom de la couche
  JTextField jTFName;

  // Choix de la couleur
  JCheckBox jCBColor;
  JButton jBCoulor;

  // Choix de la solidité
  JCheckBox jCBSold;

  // Ascenseur de l'opacité
  JSlider jSOpacity;

  JButton ok = new JButton();
  JButton cancel = new JButton();

  VectorLayer vectorialLayer;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;

  // Les objets qui feront partie de la future couche
  IFeatureCollection<IFeature> featureCollection;

  // Les controles

  private static final long serialVersionUID = 1L;

  /**
   * Fenetre de base permettant de définir la sémiologie à appliquer à une liste
   * d'objets
   * 
   * @param iMap3D l'interface de carte à laquelle on souhaite rajouter la liste
   *          d'entités
   * @param ftFeatureCollection la liste d'entités que l'on souhaite ajouter
   */
  public OneColoredLayerWindow(InterfaceMap3D iMap3D,
      IFeatureCollection<IFeature> ftFeatureCollection) {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.modify = false;
    this.iMap3D = iMap3D;
    this.featureCollection = ftFeatureCollection;

    // Titre
    this.setTitle(Messages.getString("FenetreAjoutCouche.Titre")); //$NON-NLS-1$
    this.setLayout(null);

    // Formulaire du nom
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 10, 150, 20);
    labelNom.setText(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    this.add(labelNom);

    this.jTFName = new JTextField(Messages.getString("3DGIS.LayerName")+ (++CountLayer.COUNT)); //$NON-NLS-1$
    this.jTFName.setBounds(160, 10, 200, 20);
    this.jTFName.setVisible(true);
    this.jTFName.addActionListener(this);

    this.jTFName.setSelectionStart(0);
    this.jTFName.setSelectionEnd(this.jTFName.getText().length());
    this.jTFName.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        String texte = OneColoredLayerWindow.this.jTFName.getText();
        if (texte.equalsIgnoreCase(Messages.getString("3DGIS.LayerName"))) { //$NON-NLS-1$

          OneColoredLayerWindow.this.jTFName.setText(""); //$NON-NLS-1$
        }

      }

    });
    this.add(this.jTFName);

    // Formulaire de la couleur
    JLabel labelCouleur = new JLabel();
    labelCouleur.setBounds(10, 50, 150, 20);
    labelCouleur.setText(Messages.getString("3DGIS.Color")); //$NON-NLS-1$
    labelCouleur.setVisible(true);
    this.add(labelCouleur);

    this.jCBColor = new JCheckBox(Messages.getString("3DGIS.Activated"), true); //$NON-NLS-1$
    this.jCBColor.setBounds(150, 50, 80, 20);
    this.jCBColor.setVisible(true);
    this.jCBColor.addActionListener(this);
    this.add(this.jCBColor);

    this.jBCoulor = new JButton();
    this.jBCoulor.setBounds(240, 50, 40, 20);

    this.jBCoulor.addActionListener(this);
    this.jBCoulor.setBackground(ColorRandom.getRandomColor());
    this.add(this.jBCoulor);

    // Formulaire de la solidité
    JLabel labelSolide = new JLabel();
    labelSolide.setBounds(10, 90, 150, 20);
    labelSolide.setText(Messages.getString("3DGIS.IsSolid")); //$NON-NLS-1$
    labelSolide.setVisible(true);
    this.add(labelSolide);

    this.jCBSold = new JCheckBox("", true); //$NON-NLS-1$
    this.jCBSold.setBounds(150, 90, 40, 20);
    this.jCBSold.setVisible(true);
    this.jCBSold.addActionListener(this);
    this.add(this.jCBSold);

    // Formulaire pour l'opacité
    JLabel labelTransParence = new JLabel();
    labelTransParence.setBounds(10, 130, 150, 20);
    labelTransParence.setText(Messages.getString("3DGIS.Transparency")); //$NON-NLS-1$
    labelTransParence.setVisible(true);
    this.add(labelTransParence);

    this.jSOpacity = new JSlider(0, 100, 100);
    this.jSOpacity.setBounds(160, 130, 200, 20);
    this.jSOpacity.setVisible(true);

    this.add(this.jSOpacity);

    // Boutons de validations
    this.ok.setBounds(100, 200, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 200, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setBackground(Color.white);
    this.setSize(400, 270);

  }

  /**
   * Ouverture de la fenêtre en mode modification.
   * 
   * @param vectorialLayer la couche vectorielle dont on souhaite modifier les
   *          paramètres d'affichage
   */
  public OneColoredLayerWindow(VectorLayer vectorialLayer) {
    this(null, vectorialLayer);

    this.modify = true;

    this.vectorialLayer = vectorialLayer;

    if (this.vectorialLayer.size() == 0) {

      return;
    }

    Representation rep = this.vectorialLayer.get(0).getRepresentation();

    BasicRep3D abs = null;
    this.jTFName.setText(this.vectorialLayer.getLayerName());

    if (rep instanceof BasicRep3D) {

      abs = (BasicRep3D) rep;

      // Formulaire de la couleur
      JLabel labelCouleur = new JLabel();
      labelCouleur.setBounds(10, 50, 150, 20);
      labelCouleur.setText(Messages.getString("3DGIS.Color")); //$NON-NLS-1$
      labelCouleur.setVisible(true);
      this.add(labelCouleur);

      this.jCBColor.setSelected(abs.isColored());

      if (abs.isColored()) {

        this.jBCoulor.setEnabled(true);
        this.jBCoulor.setVisible(true);

        this.jBCoulor.setBackground(abs.getColor());
      } else {
        this.jBCoulor.setEnabled(false);
        this.jBCoulor.setVisible(false);

      }

      this.jCBSold.setSelected(abs.isSolid());

      this.jSOpacity.setValue((int) (abs.getOpacity() * 100));

    }

  }

  /**
   * Gestion des actions
   */
  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // Cache ou affiche le bouton de choix de couleur
    if (source == this.jCBColor) {

      if (this.jCBColor.isSelected()) {

        this.jBCoulor.setEnabled(true);
        this.jBCoulor.setVisible(true);

      } else {

        this.jBCoulor.setEnabled(false);
        this.jBCoulor.setVisible(false);
      }

      return;
    }

    // Affiche la fenetre de choix de couleur
    // si on clique sur le bouton
    if (source == this.jBCoulor) {
      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null);

      if (couleur == null) {
        return;
      }

      this.jBCoulor.setBackground(couleur);

      return;
    }

    // bouton de validation
    if (source == this.ok) {
      // On calcule les paramètres

      this.isCanceled = false;
      String nomCouche = this.jTFName.getText();

      if (this.modify
          || this.iMap3D.getCurrent3DMap().getLayer(nomCouche) == null) {

        boolean hasColor = this.jCBColor.isSelected();
        Color couleur = null;

        if (hasColor) {

          couleur = this.jBCoulor.getBackground();
        }

        boolean isSolid = this.jCBSold.isSelected();

        double opacite = this.jSOpacity.getValue() / 100.0;
        // Style

        // si c'est une modification, on fait un update
        // Sinon on ajoute
        if (this.modify) {
          this.vectorialLayer.setLayerName(nomCouche);
          // donnees.
          this.vectorialLayer.updateStyle(hasColor, couleur, opacite, isSolid);

        } else {

          this.iMap3D.getCurrent3DMap().addLayer(
              new VectorLayer(this.featureCollection, nomCouche, hasColor,
                  couleur, opacite, isSolid));

        }
        this.dispose();
      } else {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("3DGIS.LayerExist"), //$NON-NLS-1$
                Messages.getString("FenetreAjoutCouche.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
      }
    }

    // bouton d'annulation
    if (source == this.cancel) {
      this.dispose();
      this.isCanceled = true;
    }

  }

  @Override
  public boolean isCanceled() {
    // TODO Auto-generated method stub
    return this.isCanceled;
  }

}
