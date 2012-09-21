package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Component;

import javax.media.j3d.BranchGroup;

import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;

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
 * Cette classe contient les fonctions nécessaires que l'on doit définir pour
 * une couche. Les classes MNT et CoucheVecteur implémente cette interface. En
 * implémentant cette classe, vous pouvez définir de nouveaux types de couches
 * directement intégrables dans le viewer Il peut être nécessaire de modifier la
 * classe TableListCouchesListener et TableListCouchesRenderer pour permettre
 * d'ajuster la table de gestion de couches pour un meilleur confort de
 * navigation (ce n'est pas indispensable)
 * 
 * This class provides the functions needed to define a Layer. By implementing
 * this class, you can add new king of layers you can handle in the 3D viewer
 *
 */
public interface Layer {

  /**
   * Fournit l'emprise 3D de la couche (Pavé englobant parallèle aux axes)
   * 
   * @return pavé englobant
   */
  public Box3D get3DEnvelope();

  /**
   * Le BranchGroup associé à la couche.
   * 
   * @return BranchGroup
   */
  public BranchGroup getBranchGroup();

  /**
   * Le nom que porte la couche
   * 
   * @return le nom porté par la Couche
   */
  public String getLayerName();

  /**
   * Permet de modifier le nom de la couche
   * 
   * @param nom nouveau nom porté par la couche
   */
  public void setLayerName(String nom);

  /**
   * @return Indique si une couche est visible
   */
  public boolean isVisible();

  /**
   * Modifie la visibilité de la couche (cela revient à détacher le BranchGroup
   * de la couche)
   * 
   * @param visible indique si la couche devient visible ou non
   */
  public void setVisible(boolean visible);

  /**
   * 
   * 
   * @return Indique si l'on peut sélectionner une couche
   */
  public boolean isSelectable();

  /**
   * Rend une couche sélectionnable
   * 
   * @param selectable
   */
  public void setSelectable(boolean selectable);

  /**
   * Rafraichit l'affichage d'une couche. Régénère les nouvelles représentations
   */
  public void refresh();

  /**
   * 
   * @return Renvoie le composant représentant la couche dans la table des
   *         couches
   */
  public Component getRepresentationComponent();

}
