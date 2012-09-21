package fr.ign.cogit.geoxygene.sig3d.representation;

import java.awt.Component;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JLabel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

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
 * Cette classe astraitre contient un ensemble de méthodes communes à la
 * représentation de base proposée Sont implémentés : - Les getters et setters
 * décrits dans l'interface - Une méthodes permettant de renvoyer les Shapes
 * rattachés directement à un Bg - Un constructeur implémentant le BranchGroup
 * en donnant les autorisations nécessaires pour permettre un bon
 * affichage/sélection des différents éléments rattachés This class contain some
 * methods for basic-representation classes
 * 
 */
public abstract class Default3DRep implements I3DRepresentation {
  /**
   * Il s'agit de l'entité à laquelle est liée la représentation
   */
  protected IFeature feat = null;

  /**
   * Il s'agit du noeud attaché à la représentation
   */
  protected BranchGroup bGRep = null;

  /**
   * Constructeur par défaut permettant de créer les autorisations nécessaires à
   * un BranchGroup attribut (ici BgRep attribut "protected")
   */
  public Default3DRep() {
    // Association des BranchGroup des
    // faces
    this.bGRep = new BranchGroup();

    // Capacites du BranchGroup de l'objet geographique
    this.bGRep.setCapability(Node.ALLOW_PICKABLE_READ);
    this.bGRep.setCapability(Node.ALLOW_PICKABLE_WRITE);
    this.bGRep.setCapability(Node.ENABLE_PICK_REPORTING);

    this.bGRep.setCapability(Group.ALLOW_CHILDREN_READ);
    this.bGRep.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.bGRep.setCapability(Group.ALLOW_CHILDREN_EXTEND);

    this.bGRep.setCapability(BranchGroup.ALLOW_DETACH);
    this.bGRep.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);

    // Passer les informations de l'objet au BG
    this.bGRep.setUserData(this);
    this.bGRep.setPickable(true);
  }

  /**
   * @return renvoie le noeud associé à la représentation
   */
  @Override
  public BranchGroup getBGRep() {
    return this.bGRep;
  }

  /**
   * Il s'agit de l'objet géographique lié à la représentation (utile pour
   * obtenir l'objet à partir d'une sélection)
   * 
   * @return l'entité rattaché à la représentation
   */
  @Override
  public IFeature getFeature() {
    return this.feat;

  }

  protected boolean selected = false;

  /**
   * Indique si l'objet est sélectionné
   */
  @Override
  public boolean isSelected() {
    return this.selected;
  }

  /**
   * Opération déclenchée en cas de sélection de l'objet
   */
  // Branchgroup de la selection
  BranchGroup bgSel = null;

  @Override
  /**
   * Propose une implémentation de base pour la sélection des objets
   */
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;

    if (this.bgSel == null) {

      this.initBGSel();
    }

    if (isSelected) {
      // Si il n'est pas attaché on l'attache
      if (!this.bgSel.isLive()) {

        this.bGRep.addChild(this.bgSel);
      }

    } else {
      // Si il est attaché on le détache
      if (this.bgSel.isLive()) {
        this.bgSel.detach();
      }
    }

  }

  /**
   * Permet de créer un BranchGroup contenant une sphère Elle servira à indiquer
   * si un objet est selectionné
   */
  private void initBGSel() {
    // On initialise la selection
    // On récupère la sphère englobante de l'objet
    BoundingSphere bs = new BoundingSphere(this.bGRep.getBounds());
    Point3d pt = new Point3d();
    bs.getCenter(pt);
    // On place le sphère aux bonnes coordonnées
    Transform3D translate = new Transform3D();
    translate.set(new Vector3f((float) pt.getX(), (float) pt.getY(), (float) pt
        .getZ()));

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    // Autorisations pour l'apparence
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    // Indique que l'on est en mode surfacique
    pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

    pa.setCullFace(PolygonAttributes.CULL_BACK);

    pa.setBackFaceNormalFlip(false);

    Color3f couleur3F = new Color3f(ConstantRepresentation.selectionColor);
    // Création du material (gestion des couleurs et de l'affichage)
    Material material = new Material();
    material.setAmbientColor(couleur3F.x / 2, couleur3F.y / 2, couleur3F.z / 2);
    material.setDiffuseColor(couleur3F);
    material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
    material.setShininess(128);
    apparenceFinale.setMaterial(material);

    // On applique l'apparence à la sphère
    Sphere sphere = new Sphere((float) bs.getRadius(), apparenceFinale);

    TransparencyAttributes t_attr = new TransparencyAttributes(
        TransparencyAttributes.BLENDED, (float) 0.5,
        TransparencyAttributes.BLEND_SRC_ALPHA, TransparencyAttributes.BLENDED);
    apparenceFinale.setTransparencyAttributes(t_attr);

    TransformGroup TG1 = new TransformGroup(translate);
    TG1.addChild(sphere);

    this.bgSel = new BranchGroup();
    this.bgSel.setCapability(BranchGroup.ALLOW_DETACH);
    this.bgSel.addChild(TG1);

  }

  @Override
  public Component getRepresentationComponent() {
    JLabel lab = new JLabel(this.getClass().getSimpleName());
    return lab;
  }

}
