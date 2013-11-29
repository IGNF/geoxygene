package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.swing.JLabel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object0d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object1d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object2d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object3d;

/**
 * This software is released under the licence CeCILL
 * see LICENSE.TXT
 * see <http://www.cecill.info/ http://www.cecill.info/
 * @copyright IGN
 * @author Brasebin Mickaël
 * @version 0.1
 *          Il s'agit d'une couche de données au sens SIG Une couche de données ne
 *          possède pas forcément toutes les données d'une même dimension This is the
 *          class for layer. It is used to represents objects in 3D and to load objects
 *          from postgis
 */
public class VectorLayer extends FT_FeatureCollection<IFeature> implements Layer {
  // Il s'agit de la représentation de la couche
  // Les fils de ce Branch group sont les représentations de chacun des objets
  private Box3D envelope;
  protected boolean visible = true;
  protected boolean selectable = true;
  protected String layerName = "";
  private BranchGroup parent = null;
  protected BranchGroup bgLayer = null;

  /**
   * Permet d'initialiser les capacités nécessaires au branch Group
   */
  protected VectorLayer() {
    this.bgLayer = new BranchGroup();
    this.bgLayer.setCapability(BranchGroup.ALLOW_DETACH);
    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_READ);
    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.bgLayer.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.bgLayer.setCapability(Node.ALLOW_PICKABLE_READ);
    this.bgLayer.setCapability(Node.ALLOW_PICKABLE_WRITE);
  }

  /**
   * @param layerName
   */
  public VectorLayer(String layerName) {
    this();
    this.visible = true;
    this.selectable = true;
    this.layerName = layerName;
  }

  /**
   * Permet de créer une couche avec une représentation basique multicolor ou
   * sans représentation du tout
   * @param featColl
   *        les entités de la couche
   * @param layerName
   *        le nom de la couche
   */
  public VectorLayer(IFeatureCollection<? extends IFeature> featColl, String layerName) {
    this();
    this.visible = true;
    this.selectable = true;
    this.layerName = layerName;
    this.addAll(featColl);
    this.refresh();
  }

  /**
   * Permet de créer une couche vectorielle en appliquant une couleur
   * @param featColl
   *        les entités de la couche
   * @param layerName
   *        le nom de la couche
   * @param color
   *        la couleur appliquée
   */
  public VectorLayer(IFeatureCollection<? extends IFeature> featColl, String layerName, Color color) {
    this(featColl, layerName, true, color, 1, true);
  }

  /**
   * Permet de construire une couche vectorielle en appliquant un style aux
   * entités
   * @param featColl
   *        Les entites que l'on souhaite integrer dans la couche
   * @param layerName
   *        Le nom que portera la couche
   * @param isClrd
   *        Indique si la couche aura une couleur (sinon representation
   *        multi-coloree)
   * @param color
   *        Couleur a appliquer sur la couche
   * @param coefOpacity
   *        Coefficient d'opacite (entre 0 et 1)
   * @param isSolid
   *        Representation solide ou nom de la couche
   */
  public VectorLayer(IFeatureCollection<? extends IFeature> featColl, String layerName,
      boolean isClrd, Color color, double coefOpacity, boolean isSolid) {
    this();
    this.visible = true;
    this.selectable = true;
    this.layerName = layerName;
    this.addAll(featColl);
    int nbElement = this.size();
    // Pour chaque élément, nous implémentons la bonne classe
    // En fonction de leur dimension
    for (int i = 0; i < nbElement; i++) {
      IFeature obj = this.get(i);
      int dimension = obj.getGeom().dimension();
      I3DRepresentation representation = null;
      switch (dimension) {
        case 0:
          representation = new Object0d(obj, isClrd, color, coefOpacity, isSolid);
          break;
        case 1:
          representation = new Object1d(obj, isClrd, color, coefOpacity, isSolid);
          break;
        case 2:
          representation = new Object2d(obj, isClrd, color, coefOpacity, isSolid);
          break;
        case 3:
          representation = new Object3d(obj, isClrd, color, coefOpacity, isSolid);
          break;
        default:
          // TODO: Implement 'default' statement
          break;
      }
      obj.setRepresentation(representation);
      if (representation == null) {
        continue;
      }
      this.bgLayer.addChild(representation.getBGRep());
    }
  }

  /**
   * Recrée la visualisation 3D de la couche à partir des éléments contenus dans
   * la couche (Permet de gérer l'ajout d'entités ou la modification du style
   * d'une entité)
   */
  @Override
  public void refresh() {
    this.bgLayer.removeAllChildren();
    int nbElem = this.size();
    for (int i = 0; i < nbElem; i++) {
      Representation rep = this.get(i).getRepresentation();
      if (rep instanceof I3DRepresentation) {
        I3DRepresentation iRep3D = (I3DRepresentation) rep;
        this.bgLayer.addChild(iRep3D.getBGRep());
      }
    }
  }

  /**
   * Permet d'appliquer un style à une couche
   * @param hasColor
   *        indique si l'on a une couleur fixe ou aléatoire
   * @param couleur
   *        la couleur à appliquer (argument non utilisé si hasColor =
   *        false)
   * @param opacite
   *        le coefficient d'opacité entre 0 et 1 (0 invisible)
   * @param isSolid
   *        indique si l'objet est solide (par rapport à filaire)
   */
  public void updateStyle(boolean hasColor, Color couleur, double opacite, boolean isSolid) {
    int nbElement = this.size();
    // Pour chaque élément, nous implémentons la bonne classe
    // En fonction de leur dimension
    for (int i = 0; i < nbElement; i++) {
      IFeature obj = this.get(i);
      int dimension = obj.getGeom().dimension();
      I3DRepresentation representation = null;
      switch (dimension) {
        case 0:
          representation = new Object0d(obj, hasColor, couleur, opacite, isSolid);
          break;
        case 1:
          representation = new Object1d(obj, hasColor, couleur, opacite, isSolid);
          break;

        case 2:
          representation = new Object2d(obj, hasColor, couleur, opacite, isSolid);
          break;
        case 3:
          representation = new Object3d(obj, hasColor, couleur, opacite, isSolid);
          break;
        default:
          // TODO: Implement 'default' statement
          break;
      }
      obj.setRepresentation(representation);
      if (representation == null) {
        continue;
      }
    }
    this.refresh();
  }

  @Override
  public Box3D get3DEnvelope() {
    if (this.envelope != null) {
      System.out.println("envelope (not null) = " + this.envelope);
      return this.envelope;
    }
    // On va la calculer
    int nbElem = this.size();
    if (nbElem == 0) {
      System.out.println("envelope with no element");
      return null;
    }
    Box3D b = new Box3D(this.get(0).getGeom());
    for (int i = 1; i < nbElem; i++) {
      if (this.get(i).getGeom() == null) {
        continue;
      }
      b = b.union(new Box3D(this.get(i).getGeom()));
    }
    this.envelope = b;
    System.out.println("envelope = " + b);
    return b;
  }

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
  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
    if (selectable) {
      this.getBranchGroup().setPickable(true);
    } else {
      this.getBranchGroup().setPickable(false);
    }
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
  public void setLayerName(String nom) {
    this.layerName = nom;
  }

  @Override
  public boolean isVisible() {
    return this.visible;
  }

  @Override
  public boolean isSelectable() {
    return this.selectable;
  }

  @Override
  public Component getRepresentationComponent() {
    if (this.size() == 0) {
      return null;
    }
    IFeature feat = this.get(0);
    Representation rep = feat.getRepresentation();
    if (rep == null) {
      return null;
    }
    // Représentation type couleur unie
    if (rep instanceof I3DRepresentation) {
      return ((I3DRepresentation) rep).getRepresentationComponent();
    } else {
      JLabel lab = new JLabel(rep.getClass().getSimpleName());
      return lab;
    }
  }

  @Override
  public boolean add(IFeature feat) {
    super.add(feat);
    return this.attachFeature(feat);
  }

  @Override
  public void remove(int i) {
    IFeature feat = this.get(i);
    this.remove(feat);
  }

  @Override
  public boolean remove(IFeature feat) {
    super.remove(feat);
    return this.detachFeature(feat);
  }

  @Override
  public void setElements(Collection<? extends IFeature> liste) {
    super.setElements(liste);
    this.refresh();
  }

  /*
   * @Override public void addCollection(Collection<IFeature> value) {
   * super.addCollection(value); }
   * @Override public boolean removeAll(Collection<?> coll) { // TODO
   * Auto-generated method stub return super.removeAll(coll); }
   * @Override public void clear() { // TODO Auto-generated method stub
   * super.clear(); }
   * @Override public void addUnique(IFeature feature) { // TODO Auto-generated
   * method stub super.addUnique(feature); }
   * @Override public void removeCollection(IFeatureCollection<IFeature> value)
   * { // TODO Auto-generated method stub super.removeCollection(value); }
   * @Override public void addUniqueCollection( IFeatureCollection<? extends
   * IFeature> value) { // TODO Auto-generated method stub
   * super.addUniqueCollection(value); }
   * @Override public boolean remove(Object obj) { // TODO Auto-generated method
   * stub return super.remove(obj); }
   * @Override public boolean addAll(Collection<? extends IFeature> c) { // TODO
   * Auto-generated method stub return super.addAll(c); }
   */

  private boolean attachFeature(IFeature feat) {
    Representation rep = feat.getRepresentation();
    if (rep != null) {
      if (rep instanceof I3DRepresentation) {
        BranchGroup bg = ((I3DRepresentation) rep).getBGRep();
        if (!bg.isLive()) {
          this.bgLayer.addChild(bg);
          return true;
        }
      }
    }
    return false;
  }

  private boolean detachFeature(IFeature feat) {
    Representation rep = feat.getRepresentation();
    if (rep != null) {
      if (rep instanceof I3DRepresentation) {
        BranchGroup bg = ((I3DRepresentation) rep).getBGRep();
        if (bg.isLive()) {
          bg.detach();
          return true;
        }
      }
    }
    return false;
  }
}
