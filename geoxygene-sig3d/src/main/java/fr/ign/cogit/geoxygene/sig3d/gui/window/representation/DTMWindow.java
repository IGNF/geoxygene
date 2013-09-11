package fr.ign.cogit.geoxygene.sig3d.gui.window.representation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.ImageFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.renderer.ColorGradient;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

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
 * Fenêtre gérant la représentation et le chargement d'un MNT
 * 
 * Windows allowing the management and the loading of a DTM
 */
public class DTMWindow extends JDialog implements ActionListener,
    ChangeListener {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private String file;
  private InterfaceMap3D iMap3D;

  private JComboBox jCBoxModChoice;
  private JComboBox jCBoxShadeChoice;
  private JTextField jTFPath;

  private JCheckBox jCBiSFilled;
  private JCheckBox jCBiSShadeReversed;

  private static final String LOADING_SHADE = Messages
      .getString("FenetreMNT.Shade"); //$NON-NLS-1$
  private static final String LOADING_PICTURE = Messages
      .getString("FenetreMNT.PictureMapping"); //$NON-NLS-1$

  private static final String[] loadingChoice = { DTMWindow.LOADING_SHADE,
      DTMWindow.LOADING_PICTURE };
  private JButton ok = new JButton();
  private JButton cancel = new JButton();

  private JButton browse = new JButton();
  private JLabel pathLabel;

  private JLabel jLabelXMin;
  private JTextField jTFxMin;

  private JLabel jLabelXMax;
  private JTextField jTFxMax;

  private JLabel jLabelYMin;
  private JTextField jTFyMin;

  private JLabel jLabelYMax;
  private JTextField jTFyMax;

  private JTextField jTFLayerName;

  private JSlider jSlideExa;
  private JLabel jLabelExa;

  /**
   * Constructeur de la fenêtre
   * 
   * @param filePath le chemin du MNT
   * @param iMap3D la carte dans laquelle il sera affiché
   */
  public DTMWindow(String filePath, InterfaceMap3D iMap3D) {

    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.modify = false;

    this.file = filePath;
    this.iMap3D = iMap3D;

    // Titre
    this.setTitle(Messages.getString("FenetreMNT.Title")); //$NON-NLS-1$
    this.setLayout(null);

    JLabel jLNomCouche = new JLabel(Messages.getString("3DGIS.LayerName")); //$NON-NLS-1$
    jLNomCouche.setBounds(10, 10, 140, 20);
    this.add(jLNomCouche);

    this.jTFLayerName = new JTextField(Messages.getString("3DGIS.DTM")+ (++CountLayer.COUNT)); //$NON-NLS-1$
    this.jTFLayerName.setBounds(150, 10, 230, 20);
    this.add(this.jTFLayerName);

    this.jCBoxModChoice = new JComboBox(DTMWindow.loadingChoice);
    this.jCBoxModChoice.setBounds(10, 40, 370, 20);
    this.jCBoxModChoice.setVisible(true);
    this.jCBoxModChoice.addActionListener(this);
    this.add(this.jCBoxModChoice);

    this.jCBiSFilled = new JCheckBox(Messages.getString("3DGIS.IsSolid")); //$NON-NLS-1$
    this.jCBiSFilled.setBounds(10, 70, 300, 20);
    this.jCBiSFilled.setSelected(true);
    this.add(this.jCBiSFilled);

    // étiquette Opération
    JLabel jLabelExa = new JLabel();
    jLabelExa.setBounds(10, 100, 120, 20);
    jLabelExa.setText(Messages.getString("FenetreMNT.Exaggeration")); //$NON-NLS-1$
    this.add(jLabelExa);

    this.jSlideExa = new JSlider(0, 20, 1);
    this.jSlideExa.setBounds(140, 100, 160, 30);
    this.jSlideExa.setVisible(true);
    this.jSlideExa.addChangeListener(this);
    this.add(this.jSlideExa);

    this.jLabelExa = new JLabel("1"); //$NON-NLS-1$
    this.jLabelExa.setBounds(310, 100, 150, 20);
    this.jLabelExa.setVisible(true);
    this.add(this.jLabelExa);

    this.jCBoxShadeChoice = new JComboBox(ColorShade.getColorShadeList());
    this.jCBoxShadeChoice.setRenderer(new ColorGradient());
    this.jCBoxShadeChoice.setBounds(10, 180, 370, 20);
    this.jCBoxShadeChoice.addActionListener(this);
    this.add(this.jCBoxShadeChoice);

    this.jCBiSShadeReversed = new JCheckBox(
        Messages.getString("FenetreMNT.ReverseShade")); //$NON-NLS-1$
    this.jCBiSShadeReversed.setBounds(10, 205, 300, 20);
    this.jCBiSShadeReversed.setSelected(false);
    this.add(this.jCBiSShadeReversed);

    // Formulaire du chemin
    this.pathLabel = new JLabel();
    this.pathLabel.setBounds(10, 130, 100, 20);
    this.pathLabel.setText(Messages.getString("3DGIS.Path")); //$NON-NLS-1$
    this.pathLabel.setVisible(false);
    this.add(this.pathLabel);

    this.jTFPath = new JTextField();
    this.jTFPath.setBounds(160, 130, 200, 20);
    this.jTFPath.setVisible(false);
    this.jTFPath.addActionListener(this);
    this.jTFPath.setEnabled(false);
    this.jTFPath.setText(""); //$NON-NLS-1$
    this.add(this.jTFPath);

    this.browse.setBounds(360, 130, 20, 20);
    this.browse.setText(Messages.getString("3DGIS.Browse")); //$NON-NLS-1$
    this.browse.setVisible(false);
    this.browse.addActionListener(this);
    this.add(this.browse);

    this.jLabelYMax = new JLabel("Y max"); //$NON-NLS-1$
    this.jLabelYMax.setBounds(50, 160, 50, 20);
    this.jLabelYMax.setVisible(false);
    this.add(this.jLabelYMax);

    this.jTFyMax = new JTextField();
    this.jTFyMax.setBounds(100, 160, 150, 20);
    this.jTFyMax.setVisible(false);
    this.add(this.jTFyMax);

    this.jLabelXMin = new JLabel("X min"); //$NON-NLS-1$
    this.jLabelXMin.setBounds(0, 190, 50, 20);
    this.jLabelXMin.setVisible(false);
    this.add(this.jLabelXMin);

    this.jTFxMin = new JTextField();
    this.jTFxMin.setBounds(50, 190, 150, 20);
    this.jTFxMin.setVisible(false);
    this.add(this.jTFxMin);

    this.jLabelXMax = new JLabel("X max"); //$NON-NLS-1$
    this.jLabelXMax.setBounds(200, 190, 50, 20);
    this.jLabelXMax.setVisible(false);
    this.add(this.jLabelXMax);

    this.jTFxMax = new JTextField();
    this.jTFxMax.setBounds(250, 190, 150, 20);
    this.jTFxMax.setVisible(false);

    this.add(this.jTFxMax);

    this.jLabelYMin = new JLabel("Y min"); //$NON-NLS-1$
    this.jLabelYMin.setBounds(50, 220, 50, 20);
    this.jLabelYMin.setVisible(false);
    this.add(this.jLabelYMin);

    this.jTFyMin = new JTextField();
    this.jTFyMin.setBounds(100, 220, 150, 20);
    this.jTFyMin.setVisible(false);
    this.add(this.jTFyMin);

    // Boutons de validations
    this.ok.setBounds(100, 250, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel.setBounds(200, 250, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.NO")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.setSize(450, 330);

  }

  private DTM mnt = null;
  private boolean modify;

  /**
   * Fenêtre permettant de modifer un MNT déjà affiché
   * 
   * @param mnt le mnt dont on souhaite modifier le style
   */
  public DTMWindow(DTM mnt) {

    this("", null); //$NON-NLS-1$
    this.modify = true;
    // Elle est rendue modale

    this.mnt = mnt;

    boolean modeDegrade = (mnt.getColorShade() != null);

    this.jTFLayerName.setText(mnt.getLayerName());

    if (modeDegrade) {
      this.jCBoxModChoice.setSelectedIndex(0);

    } else {

      this.jCBoxModChoice.setSelectedIndex(1);

    }

    this.jCBiSFilled.setSelected(mnt.isFilled());

    this.jSlideExa.setValue(Math.min(20, Math.abs(mnt.getExageration())));

    if (modeDegrade) {
      this.jCBoxShadeChoice.addItem(mnt.getColorShade());
      this.jCBoxShadeChoice.setSelectedItem(mnt.getColorShade());
    }
    this.jCBoxShadeChoice.setVisible(modeDegrade);

    this.jTFPath.setText(mnt.getImagePath());

    this.browse.setVisible(!modeDegrade);

    this.jLabelYMax.setVisible(!modeDegrade);

    this.jTFyMax.setText("" + mnt.getPMaxImage().getY()); //$NON-NLS-1$
    this.jTFyMax.setVisible(!modeDegrade);

    this.jLabelXMin.setVisible(!modeDegrade);

    this.jTFxMin.setText(mnt.getPMinImage().getX() + ""); //$NON-NLS-1$
    this.jTFxMin.setVisible(!modeDegrade);

    this.jLabelXMax.setVisible(!modeDegrade);

    this.jTFxMax.setText(mnt.getPMaxImage().getX() + ""); //$NON-NLS-1$
    this.jTFxMax.setVisible(!modeDegrade);

    this.add(this.jTFxMax);

    this.jLabelYMin.setVisible(!modeDegrade);

    this.jTFyMin.setText(mnt.getPMinImage().getY() + "" + ""); //$NON-NLS-1$//$NON-NLS-2$
    this.jTFyMin.setVisible(!modeDegrade);

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object obj = e.getSource();

    if (obj.equals(this.jCBoxModChoice)) {

      if (this.jCBoxModChoice.getSelectedItem().toString()
          .equals(DTMWindow.LOADING_SHADE)) {
        this.jCBoxShadeChoice.setVisible(true);
        this.jCBiSShadeReversed.setVisible(true);

        this.pathLabel.setVisible(false);
        this.jTFPath.setVisible(false);
        this.browse.setVisible(false);

        this.jLabelXMin.setVisible(false);

        this.jTFxMin.setVisible(false);

        this.jLabelXMax.setVisible(false);

        this.jTFxMax.setVisible(false);

        this.jLabelYMin.setVisible(false);

        this.jTFyMin.setVisible(false);

        this.jLabelYMax.setVisible(false);

        this.jTFyMax.setVisible(false);

      } else if (this.jCBoxModChoice.getSelectedItem().toString()
          .equals(DTMWindow.LOADING_PICTURE)) {

        this.jCBoxShadeChoice.setVisible(false);
        this.jCBiSShadeReversed.setVisible(false);

        this.pathLabel.setVisible(true);
        this.jTFPath.setVisible(true);
        this.browse.setVisible(true);

        this.jLabelXMin.setVisible(true);

        this.jTFxMin.setVisible(true);

        this.jLabelXMax.setVisible(true);
        this.jTFxMax.setVisible(true);
        this.jLabelYMin.setVisible(true);
        this.jTFyMin.setVisible(true);
        this.jLabelYMax.setVisible(true);
        this.jTFyMax.setVisible(true);
      }

    }

    if (obj.equals(this.browse)) {

      // JFileChooser homeChooser = new JFileChooser("Mes documents");
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

      this.jTFPath.setText(nomfichier);
    }

    if (obj.equals(this.cancel)) {
      this.dispose();
    }

    if (obj.equals(this.ok)) {

      boolean fill = this.jCBiSFilled.isSelected();

      int exag = Integer.parseInt(this.jLabelExa.getText());

      String nomCouche = this.jTFLayerName.getText();

      if (nomCouche == null) {

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreMNT.EmptyFileName"), //$NON-NLS-1$
                Messages.getString("FenetreMNT.DTMAddition"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        return;
      }
      if (nomCouche.equals("")) { //$NON-NLS-1$

        JOptionPane
            .showMessageDialog(
                this,
                Messages.getString("FenetreMNT.EmptyFileName"), //$NON-NLS-1$
                Messages.getString("FenetreMNT.DTMAddition"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
        return;
      }

      if (this.jCBoxModChoice.getSelectedItem().toString()
          .equals(DTMWindow.LOADING_SHADE)) {

        if (this.modify) {
          if (!this.jCBiSShadeReversed.isSelected()) {
            this.mnt.refresh(this.mnt.getPath(), nomCouche, fill, exag,
                (Color[]) this.jCBoxShadeChoice.getSelectedItem());
          } else {
            Color[] colors = ColorShade.reverse((Color[]) this.jCBoxShadeChoice
                .getSelectedItem());
            this.mnt.refresh(this.mnt.getPath(), nomCouche, fill, exag, colors);
          }
        } else {

          if (this.iMap3D.getCurrent3DMap().getLayer(nomCouche) == null) {

            if (!this.jCBiSShadeReversed.isSelected()) {
              DTM mnt = new DTM(this.file, nomCouche, fill, exag,
                  (Color[]) this.jCBoxShadeChoice.getSelectedItem());
              this.iMap3D.getCurrent3DMap().addLayer(mnt);
            } else {
              Color[] colors = ColorShade
                  .reverse((Color[]) this.jCBoxShadeChoice.getSelectedItem());
              DTM mnt = new DTM(this.file, nomCouche, fill, exag, colors);
              this.iMap3D.getCurrent3DMap().addLayer(mnt);
            }

          } else {
            // La couche existe déjà
            JOptionPane
                .showMessageDialog(
                    this,
                    Messages.getString("3DGIS.LayerExist"), //$NON-NLS-1$
                    Messages.getString("FenetreAjoutCouche.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

            return;
          }

        }

      } else if (this.jCBoxModChoice.getSelectedItem().toString()
          .equals(DTMWindow.LOADING_PICTURE)) {
        String cheminImage = this.jTFPath.getText();

        if (cheminImage == null) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.EmptyPictureName"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.DTMAddition"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
          return;
        }

        if (cheminImage.equals("")) { //$NON-NLS-1$
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.EmptyPictureName"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.DTMAddition"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
          return;
        }

        if (!cheminImage.contains("file:///")) {
          cheminImage = "file:///" + cheminImage;
        }

        // On récupère les différents objets et on Génère la contrainte
        double xMin;

        // On gère les mauvaises saisies
        try {
          xMin = Double.parseDouble(this.jTFxMin.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ValueNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        // On récupère les différents objets et on Génère la contrainte
        double xMax;

        // On gère les mauvaises saisies
        try {
          xMax = Double.parseDouble(this.jTFxMax.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ValueNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        if (xMax < xMin) {

          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ErrorMinMax"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }

        // On récupère les différents objets et on Génère la contrainte
        double yMin;

        // On gère les mauvaises saisies
        try {
          yMin = Double.parseDouble(this.jTFyMin.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ValueNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        // On récupère les différents objets et on Génère la contrainte
        double yMax;

        // On gère les mauvaises saisies
        try {
          yMax = Double.parseDouble(this.jTFyMax.getText());

        } catch (NumberFormatException nfe) {
          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ValueNotNumber"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;

        }

        if (yMax < yMin) {

          JOptionPane
              .showMessageDialog(
                  this,
                  Messages.getString("FenetreMNT.ErrorMinMax"), //$NON-NLS-1$
                  Messages.getString("FenetreMNT.ValidationError"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
          return;
        }

        GM_Envelope env = new GM_Envelope(xMin, xMax, yMin, yMax);

        if (this.modify) {

          this.mnt.refresh(this.mnt.getPath(), nomCouche, fill, exag,
              cheminImage, env);
        } else {

          if (this.iMap3D.getCurrent3DMap().getLayer(nomCouche) == null) {

            DTM mnt = new DTM(this.file, nomCouche, fill, exag, cheminImage,
                env);

            this.iMap3D.getCurrent3DMap().addLayer(mnt);
          } else {
            // La couche existe déjà
            JOptionPane
                .showMessageDialog(
                    this,
                    Messages.getString("3DGIS.LayerExist"), //$NON-NLS-1$
                    Messages.getString("FenetreAjoutCouche.Titre"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$

            return;

          }

        }

      }

      this.dispose();
    }

  }

  @Override
  public void stateChanged(ChangeEvent e) {

    if (e.getSource().equals(this.jSlideExa)) {
      JSlider source = (JSlider) e.getSource();
      int val = source.getValue();

      this.jLabelExa.setText(val + ""); //$NON-NLS-1$
    }

  }

}
