package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Component;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.swing.JLabel;

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
 * Classe abstraite implémentant couche proposant quelques méthodes pour
 * d'autres implémentations de l'interfaceCouche : - Gestion de la visibilité -
 * gestion de la sélectabilité - les accesseurs - un construceteur donnant les
 * permissions nécessaires au BranchGroup associé à la couche Abstract class
 * providing some functionnalities for implementing the class Couche
 */
public abstract class DefaultLayer implements Layer {

  private BranchGroup parent = null;
  protected boolean visible = true;
  protected boolean selectable = true;

  protected String layerName = "";

  protected BranchGroup bgLayer = null;

  /**
   * Constructeur standard permettant notamment de gérer les persmissions
   * nécessaires au BranchGroup (Detach, Children read, write, extend & Pickable
   * read, write)
   */
  public DefaultLayer() {
    this.bgLayer = new BranchGroup();
    this.bgLayer.setCapability(BranchGroup.ALLOW_DETACH);

    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_READ);
    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.bgLayer.setCapability(Node.ALLOW_PICKABLE_READ);
    this.bgLayer.setCapability(Node.ALLOW_PICKABLE_WRITE);
  }

  @Override
  public BranchGroup getBranchGroup() {
    return this.bgLayer;
  }

  @Override
  public String getLayerName() {
    return this.layerName;
  }

  @Override
  public boolean isSelectable() {
    return this.selectable;
  }

  @Override
  public void setLayerName(String nom) {
    this.layerName = nom;
  }

  /**
   * Gestion standard de la sélectabilité
   */
  @Override
  public void setSelectable(boolean selectable) {

    if (selectable) {
      this.getBranchGroup().setPickable(true);

    } else {
      this.getBranchGroup().setPickable(false);
    }

    this.selectable = selectable;

  }

  @Override
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * Implémentation standard de gestion de la visibilité
   */
  @Override
  public void setVisible(boolean visible) {
    BranchGroup branch = this.getBranchGroup();
    if (branch == null) {
      return;
    }

    if (visible) {

      if (this.parent == null) {
        return;
      }

      if (this.getBranchGroup().getParent() != null) {
        return;
      }

      this.parent.addChild(this.getBranchGroup());
    } else {

      Node node = branch.getParent();

      if (node != null) {

        this.parent = (BranchGroup) node;
      }
      this.getBranchGroup().detach();
    }

    this.visible = visible;
  }

  @Override
  public Component getRepresentationComponent() {
    JLabel lab = new JLabel(this.getClass().getSimpleName());
    return lab;
  }
}
