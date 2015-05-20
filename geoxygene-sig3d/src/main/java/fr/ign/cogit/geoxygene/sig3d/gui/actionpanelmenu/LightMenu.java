package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.media.j3d.BoundingBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import fr.ign.cogit.geoxygene.appli.panel.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.CameraAnimation;
import fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable.LightsListTable;
import fr.ign.cogit.geoxygene.sig3d.gui.table.lightstable.LightsListTableModel;

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
 *          Fenetre permettant de gérer la position et la couleur de la lumière.
 *          Propose une interface sous forme de 9 boutons représentant la scène
 *          vue du dessus afin de choisir plus facilement les coordonnées de la
 *          lumière Window for the management of the position and color of light
 */
public class LightMenu extends JPanel implements ActionListener {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private static int index = 0;

  // Bouton représentant la couleur de sélection
  JButton jBCLightColor;

  JButton ok;
  JButton jbAdd;
  JButton jbSupp;

  InterfaceMap3D iMap3D;

  // Nom de la couche
  JTextField jTFX;
  JTextField jTFY;
  JTextField jTFZ;

  JTextField jTFLightPos;

  JButton jBPosition1;
  JButton jBPosition2;
  JButton jBPosition3;
  JButton jBPosition4;
  JButton jBPosition5;
  JButton jBPosition6;
  JButton jBPosition7;
  JButton jBPosition8;
  JButton jBPosition9;

  LightsListTable tbl;

  private final static Logger logger = Logger.getLogger(CameraAnimation.class
      .getName());

  /**
   * Permet d'ouvrir une fenêtre associé à un environnement 3D
   * 
   * @param iMap3D
   */
  public LightMenu(InterfaceMap3D iMap3D) {
    super();

    if (iMap3D.getTranslate() == null) {
      LightMenu.logger.info(Messages.getString("FenetreNiveau.Unavailable"));
      return;
    }
    // Elle est rendue modale
    this.setFocusable(true);

    // Titre
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreLumiere.Title")));

    this.setLayout(null);

    this.iMap3D = iMap3D;

    JLabel labelPosDefinies = new JLabel();
    labelPosDefinies.setBounds(10, 25, 150, 20);
    labelPosDefinies.setText(Messages
        .getString("FenetreLumiere.PredifinedPositions"));
    this.add(labelPosDefinies);

    this.jBPosition1 = new JButton("");
    this.jBPosition1.setBounds(10, 45, 20, 20);

    this.jBPosition1.addActionListener(this);
    this.add(this.jBPosition1);

    this.jBPosition2 = new JButton("");
    this.jBPosition2.setBounds(30, 45, 20, 20);

    this.jBPosition2.addActionListener(this);
    this.add(this.jBPosition2);

    this.jBPosition3 = new JButton("");
    this.jBPosition3.setBounds(50, 45, 20, 20);

    this.jBPosition3.addActionListener(this);
    this.add(this.jBPosition3);

    this.jBPosition4 = new JButton("");
    this.jBPosition4.setBounds(10, 65, 20, 20);

    this.jBPosition4.addActionListener(this);
    this.add(this.jBPosition4);

    this.jBPosition5 = new JButton("");
    this.jBPosition5.setBounds(30, 65, 20, 20);

    this.jBPosition5.addActionListener(this);
    this.add(this.jBPosition5);

    this.jBPosition6 = new JButton("");
    this.jBPosition6.setBounds(50, 65, 20, 20);

    this.jBPosition6.addActionListener(this);
    this.add(this.jBPosition6);

    this.jBPosition7 = new JButton("");
    this.jBPosition7.setBounds(10, 85, 20, 20);

    this.jBPosition7.addActionListener(this);
    this.add(this.jBPosition7);

    this.jBPosition8 = new JButton("");
    this.jBPosition8.setBounds(30, 85, 20, 20);

    this.jBPosition8.addActionListener(this);
    this.add(this.jBPosition8);

    this.jBPosition9 = new JButton("");
    this.jBPosition9.setBounds(50, 85, 20, 20);

    this.jBPosition9.addActionListener(this);
    this.add(this.jBPosition9);

    Point3f point = new Point3f();

    boolean hasLight = false;

    if (this.iMap3D.getLights().size() != 0) {
      this.iMap3D.getLights().get(0).getPosition(point);
      hasLight = true;
    }

    // Formulaire du couleur sélection
    JLabel labelX = new JLabel();
    labelX.setBounds(90, 45, 20, 20);
    labelX.setText("X");
    this.add(labelX);

    this.jTFX = new JTextField(String.valueOf(point.x
        - this.iMap3D.getTranslate().x));
    this.jTFX.setBounds(110, 45, 200, 20);
    this.jTFX.setVisible(true);
    this.jTFX.addActionListener(this);
    this.add(this.jTFX);

    // Formulaire du couleur sélection
    JLabel labelY = new JLabel();
    labelY.setBounds(90, 65, 20, 20);
    labelY.setText("Y");
    this.add(labelY);

    this.jTFY = new JTextField(String.valueOf(point.y
        - this.iMap3D.getTranslate().y));
    this.jTFY.setBounds(110, 65, 200, 20);
    this.jTFY.setVisible(true);
    this.jTFY.addActionListener(this);
    this.add(this.jTFY);

    // Formulaire du couleur sélection
    JLabel labelZ = new JLabel();
    labelZ.setBounds(90, 85, 20, 20);
    labelZ.setText("Z");
    this.add(labelZ);

    this.jTFZ = new JTextField(String.valueOf(point.z
        - this.iMap3D.getTranslate().z));
    this.jTFZ.setBounds(110, 85, 200, 20);
    this.jTFZ.setVisible(true);
    this.jTFZ.addActionListener(this);
    this.add(this.jTFZ);

    JLabel jLabelCouleur = new JLabel();
    jLabelCouleur.setBounds(10, 125, 50, 20);
    jLabelCouleur.setText("Couleur");
    this.add(jLabelCouleur);

    this.jBCLightColor = new JButton();
    this.jBCLightColor.setBounds(110, 125, 40, 20);

    this.jBCLightColor.addActionListener(this);

    Color3f color = new Color3f();

    if (hasLight) {
      this.iMap3D.getLights().get(0).getColor(color);
    }

    this.jBCLightColor.setBackground(new Color((int) (255 * color.x),
        (int) (255 * color.y), (int) (255 * color.z)));
    this.add(this.jBCLightColor);

    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(10, 165, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    // Boutons d'ajout
    this.jbAdd = new JButton();
    this.jbAdd.setBounds(110, 165, 100, 20);
    this.jbAdd.setText(Messages.getString("3DGIS.Add"));
    this.jbAdd.addActionListener(this);
    this.add(this.jbAdd);

    // Boutons de suppression
    this.jbSupp = new JButton();
    this.jbSupp.setBounds(210, 165, 100, 20);
    this.jbSupp.setText(Messages.getString("3DGIS.Delete"));
    this.jbSupp.addActionListener(this);
    this.add(this.jbSupp);

    this.tbl = new LightsListTable(this.iMap3D, this);

    JScrollPane jsp = new JScrollPane(this.tbl);
    jsp.setBounds(10, 205, 350, 500);
    this.add(jsp);
    this.setSize(400, 740);

    this.setVisible(true);

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object source = e.getSource();

    if (source == this.jBPosition1) {
      Point3d point = this.posToPoint3D(1);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition2) {
      Point3d point = this.posToPoint3D(2);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition3) {
      Point3d point = this.posToPoint3D(3);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition4) {
      Point3d point = this.posToPoint3D(4);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition5) {
      Point3d point = this.posToPoint3D(5);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition6) {
      Point3d point = this.posToPoint3D(6);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition7) {
      Point3d point = this.posToPoint3D(7);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition8) {
      Point3d point = this.posToPoint3D(8);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBPosition9) {
      Point3d point = this.posToPoint3D(9);
      this.jTFX.setText(point.x + "");
      this.jTFY.setText(point.y + "");
      this.jTFZ.setText(point.z + "");
      return;
    }

    if (source == this.jBCLightColor) {

      Color couleur = COGITColorChooserPanel.showDialog(this.iMap3D,
          Messages.getString("FenetreLumiere.LightColor"), null);

      if (couleur == null) {
        return;
      }

      this.jBCLightColor.setBackground(couleur);

      return;
    }

    if (source == this.ok) {

      double x = Double.valueOf(this.jTFX.getText());
      double y = Double.valueOf(this.jTFY.getText());
      double z = Double.valueOf(this.jTFZ.getText());

      this.iMap3D.moveLight(x, y, z, LightMenu.index);

      Color couleur = this.jBCLightColor.getBackground();

      if (this.iMap3D.getLights().size() <= LightMenu.index) {
        return;
      }

      this.iMap3D
          .getLights()
          .get(LightMenu.index)
          .setColor(
              new Color3f((float) couleur.getRed() / 255, (float) couleur
                  .getGreen() / 255, (float) couleur.getBlue() / 255));

      ((LightsListTableModel) this.tbl.getModel()).fireTableDataChanged();

    }

    if (source == this.jbAdd) {

      double x = Double.valueOf(this.jTFX.getText());
      double y = Double.valueOf(this.jTFY.getText());
      double z = Double.valueOf(this.jTFZ.getText());

      Color couleur = this.jBCLightColor.getBackground();

      this.iMap3D.addLight(couleur, (float) x, (float) y, (float) z);

      ((LightsListTableModel) this.tbl.getModel()).fireTableDataChanged();
    }

    if (source == this.jbSupp) {
      if (LightMenu.index > this.iMap3D.getLights().size() - 1) {
        return;
      }
      this.iMap3D.removeLight(LightMenu.index);

      LightMenu.index = 0;
      ((LightsListTableModel) this.tbl.getModel()).fireTableDataChanged();
    }

  }

  /**
   * Permet de bouger la derrière dans une position prédéfine Le carré
   * représente l'emprise de la carte et les numéros indiquent les positions 1 2
   * 3 ________ YMAX | | | | 4 | 5 | 6 | | |________| YMIN 7 8 9 XMIN XMAX
   * 
   * @param pos
   */
  public Point3d posToPoint3D(int pos) {

    if (pos < 1 || pos > 9) {
      return null;
    }

    BoundingBox b = new BoundingBox(this.iMap3D.getBgeneral().getBounds());
    Point3d pointLow = new Point3d();
    Point3d pointUpp = new Point3d();

    b.getLower(pointLow);
    b.getUpper(pointUpp);

    Point3d posFinal = new Point3d();

    posFinal.z = pointUpp.z;

    switch (pos) {
      case 1:
        posFinal.x = pointLow.x;
        posFinal.y = pointUpp.y;

        break;

      case 2:

        posFinal.x = 0.5 * (pointLow.x + pointUpp.x);
        posFinal.y = pointUpp.y;
        break;

      case 3:

        posFinal.x = pointUpp.x;
        posFinal.y = pointUpp.y;
        break;

      case 4:

        posFinal.x = pointLow.x;
        posFinal.y = 0.5 * (pointUpp.y + pointLow.y);
        break;

      case 5:

        posFinal.x = 0.5 * (pointLow.x + pointUpp.x);
        posFinal.y = 0.5 * (pointUpp.y + pointLow.y);
        break;

      case 6:

        posFinal.x = pointUpp.x;
        posFinal.y = 0.5 * (pointUpp.y + pointLow.y);
        break;
      case 7:
        posFinal.x = pointLow.x;
        posFinal.y = pointLow.y;
        break;

      case 8:
        posFinal.x = 0.5 * (pointLow.x + pointUpp.x);
        posFinal.y = pointLow.y;
        break;

      case 9:
        posFinal.x = pointUpp.x;
        posFinal.y = pointLow.y;
        break;

    }

    posFinal.x = posFinal.x - this.iMap3D.getTranslate().x;
    posFinal.y = posFinal.y - this.iMap3D.getTranslate().y;
    posFinal.z = posFinal.z - this.iMap3D.getTranslate().z;

    return posFinal;

  }

  /**
   * Met à jour dans le formulaire les informations concernant une nouvelle
   * sélection dans la table des lumières (déclenchée depuis
   * LightsListTableListener)
   * 
   * @param newIndex l'indice de l'objet sélectionné dans la liste des lumières
   *          rattachée à un objet InterfaceMap3D
   */
  public void setIndex(int newIndex) {

    int nbLight = this.iMap3D.getLights().size();

    if (newIndex > nbLight - 1 || newIndex < 0) {
      return;
    }
    LightMenu.index = newIndex;

    Point3f point = new Point3f();

    this.iMap3D.getLights().get(LightMenu.index).getPosition(point);

    this.jTFX.setText(""
        + this.tbl.getValueAt(LightMenu.index, LightsListTableModel.IND_X));
    this.jTFY.setText(""
        + this.tbl.getValueAt(LightMenu.index, LightsListTableModel.IND_Y));
    this.jTFZ.setText(""
        + this.tbl.getValueAt(LightMenu.index, LightsListTableModel.IND_Z));

    this.jBCLightColor.setBackground((Color) this.tbl.getValueAt(
        LightMenu.index, LightsListTableModel.IND_COL));
  }

}
