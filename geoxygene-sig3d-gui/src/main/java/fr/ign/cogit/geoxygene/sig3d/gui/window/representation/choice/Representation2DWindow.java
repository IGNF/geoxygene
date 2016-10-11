package fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.CartoonModWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.OneColoredLayerWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.TexturationWindow;
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
 * Fenetre permettant de choisir quelle representation appliquer à un objet de
 * dimension 2 Window allowing to choose the reprensetation of a 2D-object
 */
public class Representation2DWindow extends JDialog implements ActionListener,
    RepresentationWindow {

  private static final long serialVersionUID = 1029630136395331959L;

  private boolean isCanceled;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;

  // Collection si on crée une nouvelle couche
  IFeatureCollection<IFeature> ftColl;

  // Couche vectorielle si on modifie
  VectorLayer vl = null;
  // RadioButton
  JRadioButton jrbCoulorChoice;
  JRadioButton jrbGenericTextureChoice;
  JRadioButton jrbCartoonChoice;

  JButton ok;
  JButton cancel;

  /**
   * Constructeur permettant d'ajouter des entités dans l'interface courante
   * 
   * @param iMap3D Interface servant d'appui à l'affichage de la fenêtre
   * @param featColl Collection qui sera symbolisée
   */
  public Representation2DWindow(InterfaceMap3D iMap3D,
      IFeatureCollection<IFeature> featColl) {
    super();
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.setTitle(Messages.getString("FenetreChoix.Title"));
    this.setLayout(null);

    this.jrbCoulorChoice = new JRadioButton(
        Messages.getString("FenetreChoix.OneColor"));
    this.jrbCoulorChoice.setSelected(true);
    this.jrbCoulorChoice.setBounds(10, 10, 250, 30);

    this.add(this.jrbCoulorChoice);

    this.jrbGenericTextureChoice = new JRadioButton(
        Messages.getString("FenetreChoix.TexGen"));
    this.jrbGenericTextureChoice.setBounds(10, 40, 250, 30);
    this.jrbGenericTextureChoice.setSelected(false);
    this.add(this.jrbGenericTextureChoice);

    this.jrbCartoonChoice = new JRadioButton(
        Messages.getString("FenetreChoix.CartoonMode"));
    this.jrbCartoonChoice.setBounds(10, 70, 250, 30);
    this.jrbCartoonChoice.setSelected(false);
    this.add(this.jrbCartoonChoice);

    ButtonGroup bgr = new ButtonGroup();
    bgr.add(this.jrbCoulorChoice);
    bgr.add(this.jrbGenericTextureChoice);
    bgr.add(this.jrbCartoonChoice);

    // Boutons de validations
    this.ok = new JButton();
    this.ok.setBounds(50, 120, 100, 20);
    this.ok.setText(Messages.getString("3DGIS.OK")); //$NON-NLS-1$
    this.ok.addActionListener(this);
    this.add(this.ok);

    this.cancel = new JButton();
    this.cancel.setBounds(150, 120, 100, 20);
    this.cancel.setText(Messages.getString("3DGIS.Cancel")); //$NON-NLS-1$
    this.cancel.addActionListener(this);
    this.add(this.cancel);

    this.iMap3D = iMap3D;
    this.ftColl = featColl;

    this.setSize(300, 200);

  }

  /**
   * Permet de générer une fenêtre adaptée à l'application d'un nouveau style de
   * dimension 2 sur une couche
   * 
   * @param vl la couche vectorielle dont on modifie le style
   */
  public Representation2DWindow(VectorLayer vl) {
    this(null, null);
    this.vl = vl;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

    Object source = e.getSource();

    if (source == this.ok) {

      this.isCanceled = false;
      JDialog fen = null;
      // On choisit la manière dont on symbolisera le ponctuel
      if (this.jrbCoulorChoice.isSelected()) {
        // Représentation des points sous forme de sphères
        if (this.vl != null) {
          fen = new OneColoredLayerWindow(this.vl);
        } else {
          fen = new OneColoredLayerWindow(this.iMap3D, this.ftColl);
        }

      }

      if (this.jrbGenericTextureChoice.isSelected()) {
        // Repréesentation sous forme de textes
        if (this.vl != null) {
          fen = new TexturationWindow(this.vl);
        } else {
          fen = new TexturationWindow(this.ftColl, this.iMap3D);
        }
      }

      if (this.jrbCartoonChoice.isSelected()) {
        // Représentation sous forme de symboles
        if (this.vl != null) {
          fen = new CartoonModWindow(this.vl);
        } else {
          fen = new CartoonModWindow(this.ftColl, this.iMap3D);
        }

      }

      if (fen != null) {
        fen.setVisible(true);
      }
      this.dispose();
      return;
    }
    if (source == this.cancel) {

      this.isCanceled = true;
      this.dispose();
      return;
    }
  }

  @Override
  public boolean isCanceled() {
    return this.isCanceled;
  }

}
