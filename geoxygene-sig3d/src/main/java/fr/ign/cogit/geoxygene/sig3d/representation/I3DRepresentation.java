package fr.ign.cogit.geoxygene.sig3d.representation;

import java.awt.Component;

import javax.media.j3d.BranchGroup;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.Representation;

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
 * Interface servant à la représentation des objets en 3D permet de faire le
 * lien entre la géométrie et la représentation dans la bibliohèque graphique
 * Toutes classes implémentant cette interface permettra de proposer de
 * nouvelles représentations qui seront affichables dans l'univers 3D
 * 
 * This class contain the necessary méthod to define a representation to apply
 * to objects
 */

public interface I3DRepresentation extends Representation {

  /**
   * 
   * 
   * @return Indique si un objet est selectionné
   */
  public boolean isSelected();

  /**
   * @return Renvoie le BranchGroup représentant l'entité
   */
  public BranchGroup getBGRep();

  /**
   * 
   * @return Il s'agit de l'objet géographique lié à la représentation (utile
   *         pour obtenir l'objet à partir d'une sélection)
   */
  public IFeature getFeature();

  /**
   * Action d'affichage à effectuer lors de la sélection d'un entité
   * 
   * @param selected indique si l'objet est sélectionné
   */
  public void setSelected(boolean selected);

  /**
   * 
   * @return Renvoie le composant représentant la couche dans la table des
   *         couches dans le cadre d'une VectorLayer - Par exemple : un bouton
   *         contenant la couleur d'un objet pour la classe Object3D ou la
   *         texture pour un objet de la classe TexturedSurface
   * 
   */
  public Component getRepresentationComponent();

}
