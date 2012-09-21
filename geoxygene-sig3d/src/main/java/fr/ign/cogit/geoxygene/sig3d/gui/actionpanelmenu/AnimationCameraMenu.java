package fr.ign.cogit.geoxygene.sig3d.gui.actionpanelmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.navigation3D.CameraAnimation;

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
 * Fenetre permettant de créer une animation
 * 
 * Window to create an animation
 * 
 * 
 */
public class AnimationCameraMenu extends JPanel implements ActionListener {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  JButton ok;
  JButton stop;
  JRadioButton jRBAllerRetour;
  JRadioButton jRBSensUnique;

  JTextField jTFSpeed;
  JTextField jTFOffset;

  private InterfaceMap3D iMap3D;

  /**
   * 
   * Création de la fenetre permettant de créer des animations de caméra A
   * partir d'une sélection
   * 
   * @param iMap3D
   */
  public AnimationCameraMenu(InterfaceMap3D iMap3D) {

    super();

    this.iMap3D = iMap3D;
    // Titre

    this.setLayout(null);
    this.setBorder(new TitledBorder(new EtchedBorder(), Messages
        .getString("FenetreAnimationCamera.Titre")));

    ButtonGroup rbg = new ButtonGroup();

    this.jRBAllerRetour = new JRadioButton(
        Messages.getString("FenetreAnimationCamera.AR"));
    this.jRBAllerRetour.setBounds(10, 25, 200, 20);
    this.jRBAllerRetour.setSelected(true);
    this.jRBAllerRetour.addActionListener(this);
    rbg.add(this.jRBAllerRetour);
    this.add(this.jRBAllerRetour);

    this.jRBSensUnique = new JRadioButton(
        Messages.getString("FenetreAnimationCamera.OneWay"));
    this.jRBSensUnique.setBounds(10, 75, 200, 20);
    this.jRBSensUnique.setSelected(false);
    this.jRBSensUnique.addActionListener(this);
    rbg.add(this.jRBSensUnique);
    this.add(this.jRBSensUnique);

    // Formulaire du nom
    JLabel jJLBackClip = new JLabel();
    jJLBackClip.setBounds(10, 125, 400, 20);
    jJLBackClip
        .setText(Messages.getString("FenetreAnimationCamera.Distance1S"));
    this.add(jJLBackClip);

    this.jTFSpeed = new JTextField("1");
    this.jTFSpeed.setBounds(30, 175, 50, 20);
    this.jTFSpeed.setVisible(true);
    this.jTFSpeed.addActionListener(this);
    this.add(this.jTFSpeed);

    // Formulaire du nom
    JLabel jJLHauteur = new JLabel();
    jJLHauteur.setBounds(10, 225, 500, 20);
    jJLHauteur.setText(Messages
        .getString("FenetreAnimationCamera.HeigthToWaypoints"));
    this.add(jJLHauteur);

    this.jTFOffset = new JTextField("0");
    this.jTFOffset.setBounds(30, 275, 50, 20);
    this.jTFOffset.setVisible(true);
    this.jTFOffset.addActionListener(this);
    this.add(this.jTFOffset);
    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(100, 325, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.Ok"));
    this.ok.addActionListener(this);
    this.add(this.ok);

    // Boutons de validations
    this.stop = new JButton();
    this.stop.setBounds(200, 325, 100, 20);
    this.stop.setText(Messages.getString("FenetreAnimationCamera.Stop"));
    this.stop.addActionListener(this);
    this.add(this.stop);

    this.setSize(440, 670);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object source = e.getSource();

    if (source == this.ok) {

      FT_FeatureCollection<IFeature> lSelect = this.iMap3D.getSelection();

      if (lSelect.size() == 0) {

        JOptionPane.showMessageDialog(this,
            Messages.getString("3DGIS.NoSlection"),
            Messages.getString("FenetreAnimationCamera.Titre"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      double vitesse;
      try {
        vitesse = Double.parseDouble(this.jTFSpeed.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }

      double offset;
      try {
        offset = Double.parseDouble(this.jTFOffset.getText());

      } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this,
            Messages.getString("FenetreShapeFile.ValueIsNotNumber" + " >0"), //$NON-NLS-1$
            Messages.getString("FenetreShapeFile.ValidationError"),
            JOptionPane.ERROR_MESSAGE);
        return;

      }

      IDirectPositionList dpl = lSelect.get(0).getGeom().coord();

      int mode = 0;

      if (this.jRBSensUnique.isSelected()) {

        mode = CameraAnimation.ALLER_UNIQUEMENT;
      }

      CameraAnimation.animeCamera(this.iMap3D, dpl, mode, vitesse / 10, offset);

    }

    if (source == this.stop) {
      CameraAnimation.stopAnimation();
    }

  }

}
