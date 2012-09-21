package fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
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
 * dimension 1 Vide pour l'isntant, une seule représentation Window allowing to
 * choose the reprensetation of a 0D-object
 */
public class Representation1DWindow extends JDialog implements ActionListener,
    RepresentationWindow {

  private static final long serialVersionUID = 1029630136395331959L;
  VectorLayer vl = null;

  private boolean isCanceled;

  @Override
  public boolean isCanceled() {
    return this.isCanceled;
  }

  /**
   * Constructeur permettant d'ajouter des entités dans l'interface courante
   * 
   * @param iMap3D Interface servant d'appui à l'affichage de la fenêtre
   * @param featColl Collection qui sera symbolisée
   */
  public Representation1DWindow(InterfaceMap3D iMap3D,
      FT_FeatureCollection<FT_Feature> featColl) {
    super();

  }

  /**
   * Constructeur permettant de choisir la meilleure option en fonction d'une
   * couche vectorielle déjà chargée
   * 
   * @param vl
   */
  public Representation1DWindow(VectorLayer vl) {
    this(null, null);
    this.vl = vl;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

  }

}
