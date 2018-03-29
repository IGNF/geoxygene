package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.media.j3d.Texture2D;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.ImageFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.table.textures.GenericTexturesList;
import fr.ign.cogit.geoxygene.sig3d.gui.table.textures.GenericTexturesListModel;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TexturedSurface;
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
 * Cette fenêtre permet de paramètrer l'ajout de textures génériques sur les
 * objets 2D - 3D This window allow the management of generic texturation
 */
public class TexturationWindow extends JDialog implements ActionListener {

  // Nom de la couche
  JTextField jTFName;

  // List des textures
  GenericTexturesList lTG;

  // Largeur et hauteur de l'image en m
  JTextField jTFLength;
  JTextField jTFHeigth;

  // Bouton permettant l'ajout de texture
  JButton addTexture;

  JButton ok;
  JButton cancel;
  /**
   * UID
   */
  private static final long serialVersionUID = 7477578030268146809L;

  private IFeatureCollection<IFeature> featColl;

  private InterfaceMap3D iMap3D;

  boolean modify;

  /**
   * Constructeur principal à partir d'une collection que l'on veut afficher et
   * l'élément de carte que l'on souhaite renseigner
   * 
   * @param featColl la collection sur laquelle un style sera appliqué
   * @param iMap3D la carte relié à la collection
   */
  public TexturationWindow(IFeatureCollection<IFeature> featColl,
      InterfaceMap3D iMap3D) {

    super();
    this.modify = false;
    this.featColl = featColl;
    this.iMap3D = iMap3D;
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);
    this.setLayout(null);
    this.setTitle(Messages.getString("FenetreTexture.Titre"));

    // Formulaire du nom
    JLabel labelNom = new JLabel();
    labelNom.setBounds(10, 10, 150, 20);
    labelNom.setText(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    this.add(labelNom);

    this.jTFName = new JTextField(Messages.getString("3DGIS.LayerName")+ (++CountLayer.COUNT)); //$NON-NLS-1$
    this.jTFName.setBounds(160, 10, 100, 20);
    this.jTFName.setVisible(true);
    this.add(this.jTFName);

    JLabel labelChoix = new JLabel();
    labelChoix.setBounds(10, 85, 150, 20);
    labelChoix.setText(Messages.getString("FenetreTexture.TextureChoice")); //$NON-NLS-1$
    this.add(labelChoix);

    JPanel pan = new JPanel();
    pan.setBounds(160, 50, 80, 80);

    JLabel labelAjouT = new JLabel();
    labelAjouT.setBounds(10, 145, 150, 20);
    labelAjouT.setText(Messages.getString("FenetreTexture.NouvelleTexture")); //$NON-NLS-1$
    this.add(labelAjouT);

    this.addTexture = new JButton(Messages.getString("3DGIS.Browse"));
    this.addTexture.setBounds(160, 145, 30, 30);
    this.addTexture.addActionListener(this);
    this.add(this.addTexture);

    this.lTG = new GenericTexturesList();
    pan.add(this.lTG);

    // Formulaire longueur
    JLabel labelLongueur = new JLabel();
    labelLongueur.setBounds(10, 185, 150, 20);
    labelLongueur.setText(Messages.getString("FenetreTexture.LongueurImage")); //$NON-NLS-1$
    this.add(labelLongueur);

    this.jTFLength = new JTextField("10"); //$NON-NLS-1$
    this.jTFLength.setBounds(160, 185, 100, 20);
    this.jTFLength.setVisible(true);
    this.add(this.jTFLength);

    // Formulaire largeur
    JLabel labelLargeur = new JLabel();
    labelLargeur.setBounds(10, 215, 150, 20);
    labelLargeur.setText(Messages.getString("FenetreTexture.HauteurImage")); //$NON-NLS-1$
    this.add(labelLargeur);

    this.jTFHeigth = new JTextField("10"); //$NON-NLS-1$
    this.jTFHeigth.setBounds(160, 215, 100, 20);
    this.jTFHeigth.setVisible(true);
    this.add(this.jTFHeigth);

    this.ok = new JButton(Messages.getString("3DGIS.Ok"));
    this.ok.setBounds(50, 245, 100, 30);
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel = new JButton(Messages.getString("3DGIS.Cancel"));
    this.cancel.setBounds(150, 245, 100, 30);
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.add(pan);
    this.setSize(300, 360);

  }

  /**
   * Permet de créer une fenetre permettant de modifier le style d'une couche
   * vectorielle
   * 
   * @param vectorialLayer permet de générer une fenêtre modifiant le style d'un
   *          couche déjà affichée
   */
  public TexturationWindow(VectorLayer vectorialLayer) {
    this(vectorialLayer, null);

    this.modify = true;
    this.jTFName.setText(vectorialLayer.getLayerName());

    Representation rep = vectorialLayer.get(0).getRepresentation();

    if (rep instanceof TexturedSurface) {
      TexturedSurface oST = (TexturedSurface) rep;
      String tex = TextureManager.getTexturePath(oST.getTexture());

      if (tex != null) {
        this.lTG.getModelList().setSelectedItem(
            TextureManager.getTexturePath(oST.getTexture()));

      }

      this.jTFLength.setText("" + oST.getImageLength());
      this.jTFHeigth.setText("" + oST.getImageHeigth());

    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object o = e.getSource();
    if (o.equals(this.addTexture)) {
      // Partie permettant d'ajouter une texture dans le manager
      JFileChooser homeChooser = new JFileChooser(
          Messages.getString("3DGIS.HomeDir")); //$NON-NLS-1$

      javax.swing.filechooser.FileFilter filtre = null;
      filtre = new ImageFilter();

      homeChooser.setAcceptAllFileFilterUsed(false);

      // Un certain type de fichier est acceepté
      homeChooser.addChoosableFileFilter(filtre);

      homeChooser.showOpenDialog(null);

      File file = homeChooser.getSelectedFile();

      if (file == null) {
        return;
      }
      String nomfichier = file.getPath();
      TextureManager.textureLoading(nomfichier);

      GenericTexturesListModel mod = this.lTG.getModelList();
      mod.setSelectedItem(nomfichier);
      mod.fireContentsChanged(mod, 0, mod.getSize() - 1);

      return;

    }

    if (o.equals(this.cancel)) {
      this.dispose();
      return;
    }

    if (o.equals(this.ok)) {

      String nomCouche = this.jTFName.getText();
      // Partie s'occupant de récupérer la texture et de générer les indos

      if (!this.modify
          && this.iMap3D.getCurrent3DMap().getLayer(nomCouche) != null) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("3DGIS.LayerExist"), //$NON-NLS-1$
                Messages.getString("FenetreAjoutCouche.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        return;

      }
      Texture2D tex = TextureManager.textureLoading(this.lTG.getModelList()
          .getSelectedItem().toString());

      if (tex == null) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreTexture.NoSelection"),
            Messages.getString("FenetreAjoutCouche.Titre"),
            JOptionPane.WARNING_MESSAGE);
        return;

      }
      int nbFeat = this.featColl.size();

      String strHauteur = this.jTFHeigth.getText();
      String strLong = this.jTFLength.getText();
      // On récupère et vérifie les informations de hauteur
      double hauteur;
      try {
        hauteur = Double.parseDouble(strHauteur);

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (hauteur < 0) {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }
      // On récupère et vérifie les informations de longueur
      double longueur;
      try {
        longueur = Double.parseDouble(strLong);

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }

      if (longueur < 0) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      for (int i = 0; i < nbFeat; i++) {
        IFeature feat = this.featColl.get(i);

        if (feat!=null&&!feat.getGeom().isEmpty()&&feat.getGeom().dimension() > 1) {
          // On associe la texture à l'objet
          feat.setRepresentation(new TexturedSurface(feat, tex, longueur,
              hauteur));
        }

      }
      if (this.modify) {
        /*
         * Mode modification on rafraichit la represenatation de la couche
         */
        ((VectorLayer) this.featColl).setLayerName("" + this.jTFName.getText()); //$NON-NLS-1$
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
    }

  }

}
