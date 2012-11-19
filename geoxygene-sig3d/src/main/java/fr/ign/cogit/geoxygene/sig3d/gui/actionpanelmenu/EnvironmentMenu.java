package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.appli.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object3d;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Fenetre gérant les paramètres de modification d'environnement. -
 *          Couleur de fond - Couleur de sélection - Cull mode - Gestion de
 *          l'affichage des objets proches ou éloignés
 * 
 *          Window for global representation
 */
public class EnvironmentMenu extends JPanel implements ActionListener {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  // Bouton représentant la couleur de fond
  JButton jBBackgroundColor;

  // Bouton représentant la couleur de sélection
  JButton jBSelectionColor;

  JButton ok;

  JCheckBox jCBCullMode;

  InterfaceMap3D iMap3D;

  // Nom de la couche
  JTextField jTFFrontClip;
  JTextField jTFBackClip;
  JTextField jTFZScale;

  public EnvironmentMenu(InterfaceMap3D iMap3D) {
    super();

    // Titre

    this.setLayout(null);
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreModificationEnvironnement.Title")));

    this.iMap3D = iMap3D;

    // Formulaire du couleur fond
    JLabel labelCouleurFond = new JLabel();
    labelCouleurFond.setBounds(10, 25, 150, 20);
    labelCouleurFond.setText(Messages.getString("3DGIS.Color"));
    this.add(labelCouleurFond);

    this.jBBackgroundColor = new JButton();
    this.jBBackgroundColor.setBounds(240, 25, 40, 20);

    this.jBBackgroundColor.addActionListener(this);
    Color3f couleur = new Color3f();

    iMap3D.getBackground3D().getColor(couleur);

    this.jBBackgroundColor.setBackground(new Color((int) (255 * couleur.x),
        (int) (255 * couleur.y), (int) (255 * couleur.z)));
    this.add(this.jBBackgroundColor);

    // Formulaire du couleur sélection
    JLabel labelCouleurSelection = new JLabel();
    labelCouleurSelection.setBounds(10, 75, 150, 20);
    labelCouleurSelection.setText("Couleur de sélection");
    this.add(labelCouleurSelection);

    this.jBSelectionColor = new JButton();
    this.jBSelectionColor.setBounds(240, 75, 40, 20);

    this.jBSelectionColor.addActionListener(this);

    iMap3D.getBackground3D().getColor(couleur);

    this.jBSelectionColor.setBackground(ConstantRepresentation.selectionColor);
    this.add(this.jBSelectionColor);

    // Formulaire du nom
    JLabel jLFrontClip = new JLabel();
    jLFrontClip.setBounds(10, 115, 150, 20);
    jLFrontClip.setText("Front Clip");
    this.add(jLFrontClip);

    this.jTFFrontClip = new JTextField(
        String.valueOf(ConstantRepresentation.frontClip));
    this.jTFFrontClip.setBounds(160, 115, 200, 20);
    this.jTFFrontClip.setVisible(true);
    this.jTFFrontClip.addActionListener(this);
    this.add(this.jTFFrontClip);

    // Formulaire du nom
    JLabel jJLBackClip = new JLabel();
    jJLBackClip.setBounds(10, 145, 150, 20);
    jJLBackClip.setText("Back Clip");
    this.add(jJLBackClip);

    this.jTFBackClip = new JTextField(
        String.valueOf(ConstantRepresentation.backClip));
    this.jTFBackClip.setBounds(160, 145, 200, 20);
    this.jTFBackClip.setVisible(true);
    this.jTFBackClip.addActionListener(this);
    this.add(this.jTFBackClip);

    this.jCBCullMode = new JCheckBox("Cull Mode",
        ConstantRepresentation.cullMode);
    this.jCBCullMode.setBounds(10, 185, 80, 20);
    this.jCBCullMode.setVisible(true);
    this.jCBCullMode.addActionListener(this);
    this.add(this.jCBCullMode);

    // Formulaire du nom
    JLabel jJLZEchelle = new JLabel();
    jJLZEchelle.setBounds(10, 225, 150, 20);
    jJLZEchelle.setText(Messages.getString("3DGIS.Scale") + " Z");
    this.add(jJLZEchelle);

    this.jTFZScale = new JTextField(
        String.valueOf(ConstantRepresentation.scaleFactorZ));
    this.jTFZScale.setBounds(160, 225, 200, 20);
    this.jTFZScale.setVisible(true);
    this.jTFZScale.addActionListener(this);
    this.add(this.jTFZScale);

    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(150, 275, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.Update"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.setSize(400, 320);
    this.setPreferredSize(new Dimension(400, 320));

    this.setVisible(true);

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    if (source == this.jBBackgroundColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this.iMap3D,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null);

      if (couleur == null) {
        return;
      }

      this.jBBackgroundColor.setBackground(couleur);

      return;
    }

    if (source == this.jBSelectionColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this.iMap3D,
          Messages.getString("FenetreAjoutCouche.ColorLayer"), null);

      if (couleur == null) {
        return;
      }

      this.jBSelectionColor.setBackground(couleur);

      return;
    }

    if (source == this.ok) {
      ConstantRepresentation.backGroundColor = this.jBBackgroundColor.getBackground();
      Color3f couleur3f = new Color3f(ConstantRepresentation.backGroundColor);
      this.iMap3D.getBackground3D().setColor(couleur3f);

      Color couleur = this.jBSelectionColor.getBackground();

      ConstantRepresentation.selectionColor = couleur;

      FT_FeatureCollection<IFeature> ftFeat = new FT_FeatureCollection<IFeature>();
      ftFeat.addAll(this.iMap3D.getSelection());
      this.iMap3D.setSelection(ftFeat);

      ftFeat.clear();

      double frontclip = 0;
      // On gère les mauvaises saisies
      try {
        frontclip = Double.parseDouble(this.jTFFrontClip.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }

      if (frontclip <= 0) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      double backclip;
      try {
        backclip = Double.parseDouble(this.jTFBackClip.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }

      if (backclip <= 0) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (backclip < frontclip) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreModificationEnvironnement.ErrorClip"),
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      ConstantRepresentation.frontClip = frontclip;
      ConstantRepresentation.backClip = backclip;

      this.iMap3D.getView().setFrontClipDistance(frontclip);
      this.iMap3D.getView().setBackClipDistance(backclip);

      boolean isSelected = this.jCBCullMode.isSelected();

      if (isSelected != ConstantRepresentation.cullMode) {

        ConstantRepresentation.cullMode = isSelected;
        this.refreshCull();

      }

      double zEchelle = Double.valueOf(this.jTFZScale.getText());

      if (zEchelle != ConstantRepresentation.scaleFactorZ) {

        this.iMap3D.setFacteurZ(zEchelle);

      }

    }

  }

  /**
   * Cette fonction régénère l'affichage de chaque couche
   */
  private void refreshCull() {
    List<Layer> alCouches = this.iMap3D.getCurrent3DMap().getLayerList();

    int nbCouches = alCouches.size();

    for (int i = 0; i < nbCouches; i++) {
      Layer coucheTemp = alCouches.get(i);

      if (!(coucheTemp instanceof VectorLayer)) {
        continue;
      }
      VectorLayer c = (VectorLayer) coucheTemp;

      int nbElement = c.size();

      for (int j = 0; j < nbElement; j++) {
        IFeature feat = c.get(j);

        Representation rep = feat.getRepresentation();

        if (!(rep instanceof Object3d)) {

          continue;
        }
        Object3d objet3D = (Object3d) rep;
        objet3D.setCullMode(ConstantRepresentation.cullMode);

      }

    }

  }

}
