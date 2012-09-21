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
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.OneColoredLayerWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.SymbolWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.ToponymWindow;
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
 * dimension 0 Window allowing to choose the reprensetation of a 0D-object
 */
public class Representation0DWindow extends JDialog implements ActionListener,
    RepresentationWindow {

  private static final long serialVersionUID = 1029630136395331959L;

  // Fenetre dans laquelle s'affiche l'objet
  InterfaceMap3D iMap3D;
  IFeatureCollection<IFeature> ftColl;

  private boolean isCanceled;

  // RadioButton
  JRadioButton jrbSphereChoice;
  JRadioButton jrbToponymChoice;
  JRadioButton jrbModelChoice;

  JButton ok;
  JButton cancel;

  boolean modify;

  /**
   * Constructeur permettant d'ajouter des entités dans l'interface courante
   * 
   * @param iMap3D Interface servant d'appui à l'affichage de la fenêtre
   * @param featColl Collection qui sera symbolisée
   */
  public Representation0DWindow(InterfaceMap3D iMap3D,
      IFeatureCollection<IFeature> featColl) {
    super();
    this.modify = false;
    // Elle est rendue modale
    this.setFocusable(true);
    this.setModal(true);

    this.setTitle(Messages.getString("FenetreChoix.Title"));
    this.setLayout(null);

    this.jrbSphereChoice = new JRadioButton(
        Messages.getString("FenetreChoix.Sphere"));
    this.jrbSphereChoice.setSelected(true);
    this.jrbSphereChoice.setBounds(10, 10, 200, 30);

    this.add(this.jrbSphereChoice);

    this.jrbToponymChoice = new JRadioButton(
        Messages.getString("FenetreChoix.Text"));
    this.jrbToponymChoice.setBounds(10, 40, 200, 30);
    this.jrbToponymChoice.setSelected(false);
    this.add(this.jrbToponymChoice);

    this.jrbModelChoice = new JRadioButton(
        Messages.getString("FenetreChoix.Symbol"));
    this.jrbModelChoice.setBounds(10, 70, 200, 30);
    this.jrbModelChoice.setSelected(false);
    this.add(this.jrbModelChoice);

    ButtonGroup bgr = new ButtonGroup();
    bgr.add(this.jrbSphereChoice);
    bgr.add(this.jrbToponymChoice);
    bgr.add(this.jrbModelChoice);

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

  VectorLayer vl = null;

  /**
   * Fenetre permettant d'appliquer des modifications sur le style d'une couche
   * vectorielle
   * 
   * @param vl
   */
  public Representation0DWindow(VectorLayer vl) {
    this(null, null);
    this.vl = vl;

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

    Object source = e.getSource();
    // On affiche la fenetre adéquant
    if (source == this.ok) {

      this.isCanceled = false;

      JDialog fen = null;
      // On choisit la manière dont on symbolisera le ponctuel
      if (this.jrbSphereChoice.isSelected()) {
        // Représentation des points sous forme de sphères
        if (this.vl != null) {
          fen = new OneColoredLayerWindow(this.vl);
        } else {
          fen = new OneColoredLayerWindow(this.iMap3D, this.ftColl);
        }

      }

      if (this.jrbToponymChoice.isSelected()) {
        // Représentation sous forme de textes
        if (this.vl != null) {

          fen = new ToponymWindow(this.vl);
        } else {

          fen = new ToponymWindow(this.ftColl, this.iMap3D);
        }

      }

      if (this.jrbModelChoice.isSelected()) {
        // Représentation sous forme de symboles
        if (this.vl != null) {
          fen = new SymbolWindow(this.vl);
        } else {
          fen = new SymbolWindow(this.ftColl, this.iMap3D);
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
