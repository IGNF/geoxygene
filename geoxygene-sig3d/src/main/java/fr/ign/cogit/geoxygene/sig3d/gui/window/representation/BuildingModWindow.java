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
import fr.ign.cogit.geoxygene.sig3d.representation.sample.BuildingTexture;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
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
public class BuildingModWindow extends JDialog implements ActionListener {

  // Nom de la couche
  JTextField jTFName;

  // List des textures pour les murs
  GenericTexturesList lGenericTextureWall;

  // Largeur et hauteur de l'image en m pour les murs
  JTextField jTFLengthWall;
  JTextField jTFHeigthWall;

  // List des textures pour les toits
  GenericTexturesList lGenericTextureRoof;

  // Largeur et hauteur de l'image en m pour les oits
  JTextField jTFLengthRoof;
  JTextField jTFHeigthRoof;

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
   * @param featColl
   * @param iMap3D
   */
  public BuildingModWindow(IFeatureCollection<IFeature> featColl,
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

    this.jTFName = new JTextField(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    this.jTFName.setBounds(160, 10, 100, 20);
    this.jTFName.setVisible(true);
    this.add(this.jTFName);

    // //////////////////////
    // //Partie gérant la texture des murs
    // ///////////////////////

    JLabel labelChoix = new JLabel();
    labelChoix.setBounds(10, 85, 150, 20);
    labelChoix.setText(Messages.getString("FenetreTexture.TextureMurale")); //$NON-NLS-1$
    this.add(labelChoix);

    JPanel pan = new JPanel();
    pan.setBounds(160, 50, 80, 80);
    this.lGenericTextureWall = new GenericTexturesList();
    pan.add(this.lGenericTextureWall);
    this.add(pan);

    int nbElem = this.lGenericTextureWall.getModelList().getSize();
    this.lGenericTextureWall.getModelList().setSelectedItem(
        this.lGenericTextureWall.getModelList().getElementAt(
            (int) (Math.random() * nbElem)));

    // Formulaire longueur
    JLabel labelLongueur = new JLabel();
    labelLongueur.setBounds(10, 145, 150, 20);
    labelLongueur.setText(Messages.getString("FenetreTexture.LongueurImage")); //$NON-NLS-1$
    this.add(labelLongueur);

    this.jTFLengthWall = new JTextField("10"); //$NON-NLS-1$
    this.jTFLengthWall.setBounds(160, 145, 100, 20);
    this.jTFLengthWall.setVisible(true);
    this.add(this.jTFLengthWall);

    // Formulaire largeur
    JLabel labelLargeur = new JLabel();
    labelLargeur.setBounds(10, 175, 150, 20);
    labelLargeur.setText(Messages.getString("FenetreTexture.HauteurImage")); //$NON-NLS-1$
    this.add(labelLargeur);

    this.jTFHeigthWall = new JTextField("10"); //$NON-NLS-1$
    this.jTFHeigthWall.setBounds(160, 175, 100, 20);
    this.jTFHeigthWall.setVisible(true);
    this.add(this.jTFHeigthWall);

    // //////////////////////
    // //Partie gérant la texture des toits
    // ///////////////////////

    JLabel labelChoixToit = new JLabel();
    labelChoixToit.setBounds(10, 230, 150, 20);
    labelChoixToit.setText(Messages.getString("FenetreTexture.TextureToit")); //$NON-NLS-1$
    this.add(labelChoixToit);

    JPanel panToit = new JPanel();
    panToit.setBounds(160, 205, 80, 80);
    this.lGenericTextureRoof = new GenericTexturesList();
    panToit.add(this.lGenericTextureRoof);
    this.add(panToit);

    // On sélectionne aléatoirement des textures
    this.lGenericTextureRoof.getModelList().setSelectedItem(
        this.lGenericTextureRoof.getModelList().getElementAt(
            (int) (Math.random() * nbElem)));

    // Formulaire longueur
    JLabel labelLongueurToit = new JLabel();
    labelLongueurToit.setBounds(10, 300, 150, 20);
    labelLongueurToit.setText(Messages
        .getString("FenetreTexture.LongueurImage")); //$NON-NLS-1$
    this.add(labelLongueurToit);

    this.jTFLengthRoof = new JTextField("10"); //$NON-NLS-1$
    this.jTFLengthRoof.setBounds(160, 300, 100, 20);
    this.jTFLengthRoof.setVisible(true);
    this.add(this.jTFLengthRoof);

    // Formulaire largeur
    JLabel labelLargeurToit = new JLabel();
    labelLargeurToit.setBounds(10, 330, 150, 20);
    labelLargeurToit.setText(Messages.getString("FenetreTexture.HauteurImage")); //$NON-NLS-1$
    this.add(labelLargeurToit);

    this.jTFHeigthRoof = new JTextField("10"); //$NON-NLS-1$
    this.jTFHeigthRoof.setBounds(160, 330, 100, 20);
    this.jTFHeigthRoof.setVisible(true);
    this.add(this.jTFHeigthRoof);

    JLabel labelAjouT = new JLabel();
    labelAjouT.setBounds(10, 370, 150, 20);
    labelAjouT.setText(Messages.getString("FenetreTexture.NouvelleTexture")); //$NON-NLS-1$
    this.add(labelAjouT);

    this.addTexture = new JButton(Messages.getString("3DGIS.Browse"));
    this.addTexture.setBounds(160, 370, 30, 30);
    this.addTexture.addActionListener(this);
    this.add(this.addTexture);

    this.ok = new JButton(Messages.getString("3DGIS.Ok"));
    this.ok.setBounds(50, 440, 100, 30);
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel = new JButton(Messages.getString("3DGIS.Cancel"));
    this.cancel.setBounds(150, 440, 100, 30);
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(300, 545);

  }

  /**
   * Initialise une fenetre permettant de modifier le style d'une couche
   * vectorielle
   * 
   * @param vectorialLayer
   */
  public BuildingModWindow(VectorLayer vectorialLayer) {
    this(vectorialLayer, null);

    this.modify = true;
    this.jTFName.setText(vectorialLayer.getLayerName());

    Representation rep = vectorialLayer.get(0).getRepresentation();

    if (rep instanceof BuildingTexture) {
      BuildingTexture oST = (BuildingTexture) rep;

      String texMur = TextureManager.getTexturePath(oST.getWallTexture());

      if (texMur != null) {
        this.lGenericTextureWall.getModelList().setSelectedItem(texMur);

      }

      String texToit = TextureManager.getTexturePath(oST.getRoofTexture());

      if (texToit != null) {
        this.lGenericTextureRoof.getModelList().setSelectedItem(texToit);

      }

      this.jTFLengthWall.setText("" + oST.getTextureWallLength());
      this.jTFHeigthWall.setText("" + oST.getTextureWallHeigth());
      this.jTFLengthRoof.setText("" + oST.getTextureRoofLength());
      this.jTFHeigthRoof.setText("" + oST.getTextureRoofHeigth());

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

      GenericTexturesListModel mod = this.lGenericTextureWall.getModelList();
      mod.setSelectedItem(nomfichier);
      mod.fireContentsChanged(mod, 0, mod.getSize() - 1);

      return;

    }

    if (o.equals(this.cancel)) {
      this.dispose();
      return;
    }

    if (o.equals(this.ok)) {
      int nbFeat = this.featColl.size();
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

      // //////////////////////
      // /Gestion des textures mur et toit
      // /////////////////////

      Texture2D texMur = TextureManager.textureLoading(this.lGenericTextureWall
          .getModelList().getSelectedItem().toString());

      if (texMur == null) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreTexture.NoSelection"),
            Messages.getString("FenetreAjoutCouche.Titre"),
            JOptionPane.WARNING_MESSAGE);
        return;

      }

      Texture2D texToit = TextureManager
          .textureLoading(this.lGenericTextureRoof.getModelList()
              .getSelectedItem().toString());

      if (texToit == null) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreTexture.NoSelection"),
            Messages.getString("FenetreAjoutCouche.Titre"),
            JOptionPane.WARNING_MESSAGE);
        return;

      }

      // //////////////////////
      // /Gestion des paramètres mur et toit
      // /////////////////////

      String strHauteurMur = this.jTFHeigthWall.getText();
      String strLongMur = this.jTFLengthWall.getText();
      // On récupère et vérifie les informations de hauteur
      double hauteurMur;
      try {
        hauteurMur = Double.parseDouble(strHauteurMur);

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (hauteurMur < 0) {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }
      // On récupère et vérifie les informations de longueur
      double longueurMur;
      try {
        longueurMur = Double.parseDouble(strLongMur);

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (longueurMur < 0) {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }

      String strHauteurToit = this.jTFHeigthRoof.getText();
      String strLongToit = this.jTFLengthRoof.getText();
      // On récupère et vérifie les informations de hauteur
      double hauteurToit;
      try {
        hauteurToit = Double.parseDouble(strHauteurToit);

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (hauteurToit < 0) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }
      // On récupère et vérifie les informations de longueur
      double longueurToit;
      try {
        longueurToit = Double.parseDouble(strLongToit);

      } catch (NumberFormatException nfe) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;

      }

      if (longueurToit < 0) {
        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
                Messages.getString("FenetreShapeFile.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        return;
      }

      for (int i = 0; i < nbFeat; i++) {
        IFeature feat = this.featColl.get(i);

        if (feat.getGeom().dimension() > 1) {
          // On associe la texture à l'objet
          feat.setRepresentation(new BuildingTexture(feat, texMur, longueurMur,
              hauteurMur, texToit, longueurToit, hauteurToit));
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
