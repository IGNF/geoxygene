package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.representation.sample.ObjectCartoon;
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
 * Fenetre permettant la gestion d'une représentation de type Cartoon avec des
 * bords épais pour chaque face des objets Cartoon style representation by
 * applying a black border on objects
 */
public class CartoonModWindow extends JDialog implements ActionListener {

  // Ce boolean passe à true quand il s'agit de la mise a jour (graphique
  // d'une couche)

  // Nom de la couche
  JTextField jTFName;
  JTextField jTFEdgeWidth;

  // Choix de la couleur
  JButton jBColor;

  JButton jBEdgeColor;

  // Ascenseur de l'opacité
  JSlider jSOpacity;

  JButton ok = new JButton();
  JButton cancel = new JButton();

  VectorLayer vl;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;

  // Les objets qui feront partie de la future couche
  IFeatureCollection<IFeature> featColl;

  // Indique si l'on est en mode modification
  boolean modify;

  private static final long serialVersionUID = 1L;

  /**
   * Fenetre de base permettant de définir la sémiologie à appliquer à une liste
   * d'objets
   * 
   * @param featColl la collection à laquelle sera appliquée le style
   * @param iMap3D la carte dans laquelle sera affichée la collection
   */
  public CartoonModWindow(IFeatureCollection<IFeature> featColl,
      InterfaceMap3D iMap3D) {
    super();

    this.modify = false;

    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.iMap3D = iMap3D;
    this.featColl = featColl;

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
        String texte = CartoonModWindow.this.jTFName.getText();
        if (texte.equalsIgnoreCase(Messages.getString("3DGIS.LayerName"))) { //$NON-NLS-1$

          CartoonModWindow.this.jTFName.setText(""); //$NON-NLS-1$
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

    this.jBColor = new JButton();
    this.jBColor.setBounds(200, 50, 40, 20);

    this.jBColor.addActionListener(this);
    this.jBColor.setBackground(ColorRandom.getRandomColor());
    this.add(this.jBColor);

    // Formulaire de la bordure des couleurs
    JLabel labelCB = new JLabel();
    labelCB.setBounds(10, 90, 150, 20);
    labelCB.setText(Messages.getString("FenetreBordsEpais.ColorBord")); //$NON-NLS-1$
    labelCB.setVisible(true);
    this.add(labelCB);

    this.jBEdgeColor = new JButton();
    this.jBEdgeColor.setBounds(200, 90, 40, 20);

    this.jBEdgeColor.addActionListener(this);
    this.jBEdgeColor.setBackground(Color.BLACK);
    this.add(this.jBEdgeColor);

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

    // Formulaire pour la largeur
    JLabel largeurBord = new JLabel();
    largeurBord.setBounds(10, 170, 150, 20);
    largeurBord.setText(Messages.getString("FenetreBordsEpais.LargeurBord")); //$NON-NLS-1$
    largeurBord.setVisible(true);
    this.add(largeurBord);

    this.jTFEdgeWidth = new JTextField("1"); //$NON-NLS-1$
    this.jTFEdgeWidth.setBounds(160, 170, 200, 20);
    this.jTFEdgeWidth.setVisible(true);
    this.add(this.jTFEdgeWidth);

    // Boutons de validations
    this.ok.setBounds(100, 210, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 210, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setBackground(Color.white);
    this.setSize(400, 310);

  }

  /**
   * Fenêtre modifiant le style d'une couche déjà existante
   * 
   * @param cv la couche dont on souhaite modifier le style
   */
  public CartoonModWindow(VectorLayer cv) {
    // TODO Auto-generated constructor stub
    this(cv, null);
    this.modify = true;
    this.jTFName.setText(cv.getLayerName());

    if (cv.get(0).getRepresentation() instanceof ObjectCartoon) {

      ObjectCartoon o = (ObjectCartoon) cv.get(0).getRepresentation();

      this.jTFEdgeWidth.setText("" + o.getWidthEdge());
      this.jBColor.setBackground(o.getColor());
      this.jBEdgeColor.setBackground(o.getEdgeColor());
      this.jSOpacity.setValue((int) (o.getCoefOpacity() * 100));

    }

  }

  /**
   * Gestion des actions
   */
  @Override
  public void actionPerformed(ActionEvent actionevent) {
    Object source = actionevent.getSource();

    // Affiche la fenetre de choix de couleur
    // si on clique sur le bouton
    if (source == this.jBColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null); //$NON-NLS-1$

      if (couleur == null) {
        return;
      }

      this.jBColor.setBackground(couleur);

      return;
    }

    // Affiche la fenetre de choix de couleur
    // si on clique sur le bouton
    if (source == this.jBEdgeColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null); //$NON-NLS-1$

      if (couleur == null) {
        return;
      }

      this.jBEdgeColor.setBackground(couleur);

      return;
    }

    // bouton de validation
    if (source == this.ok) {
      // On calcule les paramètres

      String nomCouche = this.jTFName.getText();

      if (this.modify
          || this.iMap3D.getCurrent3DMap().getLayer(nomCouche) == null) {

        Color couleur = this.jBColor.getBackground();
        Color couleurBordure = this.jBEdgeColor.getBackground();

        double opacite = this.jSOpacity.getValue() / 100.0;

        // On récupère et vérifie les informations de longueur
        int largeurBordure;
        try {
          largeurBordure = Integer.parseInt(this.jTFEdgeWidth.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.ValueIsNotNumber") + " >0", //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        if (largeurBordure <= 0) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreShapeFile.ValueIsNotNumber") + " >0", //$NON-NLS-1$
                  Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        int nbFeat = this.featColl.size();

        // On associe la représentation en fonction des paramètres à
        // chaque objet
        for (int i = 0; i < nbFeat; i++) {
          IFeature feat = this.featColl.get(i);

          if (feat.getGeom().dimension() > 1) {
            // On associe la texture à l'objet
            feat.setRepresentation(new ObjectCartoon(feat, couleur,
                couleurBordure, largeurBordure, opacite));
          }

        }

        if (this.modify) {
          /*
           * Mode modification on rafraichit la represenatation de la couche
           */
          ((VectorLayer) this.featColl)
              .setLayerName("" + this.jTFName.getText()); //$NON-NLS-1$
          ((VectorLayer) this.featColl).refresh();

        } else {
          VectorLayer cv = new VectorLayer(this.featColl, nomCouche);
          if (this.iMap3D.getCurrent3DMap().addLayer(cv)) {
            JOptionPane
                .showMessageDialog(
                    this,
                    Messages.getString("3DGIS.LayerAdded"), //$NON-NLS-1$
                    Messages.getString("FenetreAjoutCouche.Titre"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
          }

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
    }

  }

}
