package fr.ign.cogit.geoxygene.sig3d.representation.control;

import java.awt.Color;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnTransformChange;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;

import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;


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
 * Classe permettant de rajouter une boussole pour la navigation.... à ajouter
 * TODO : à finir
 */
@Deprecated
public class Compass extends Behavior {

  private WakeupOnTransformChange w;
  private Transform3D trans = new Transform3D();
  private TransformGroup initialTransform;

  public Compass(TransformGroup triggeredTransform,
      TransformGroup initialTransform) {

    this.w = new WakeupOnTransformChange(triggeredTransform);
    this.initialTransform = initialTransform;
    this.setBounds(new BoundingSphere(new Point3d(0, 0, 0),
        Double.POSITIVE_INFINITY));
    this.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0),
        Double.POSITIVE_INFINITY));
  }

  /**
   * Override Behavior's initialize method to setup wakeup criteria.
   */
  @Override
  public void initialize() {
    this.wakeupOn(this.w);
  }

  /**
   * Override Behavior's stimulus method to handle the event.
   */
  @Override
  public void processStimulus(@SuppressWarnings("rawtypes") Enumeration criteria) {
    // TODO Auto-generated method stub
    while (criteria.hasMoreElements()) {
      WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();

      if (wakeup instanceof WakeupOnTransformChange) {

        this.w.getTransformGroup().getTransform(this.trans);
        Vector3f v = new Vector3f();
        this.trans.get(v);

        Transform3D t = new Transform3D();
        this.initialTransform.getTransform(t);

        t.set(v);

        this.initialTransform.setTransform(t);

      }

    }

    this.wakeupOn(this.w);

  }

  public static Group createCompass() {
    Appearance ap = Compass.generateAppearance(Color.pink, 0.5, true);
    Cone c = new Cone(1f, 5f, ap);

    Transform3D tf = new Transform3D();
    tf.rotX(Math.PI / 2);

    Transform3D tf1 = new Transform3D();
    tf1.setTranslation(new Vector3d(10, 15, -40));

    TransformGroup tg = new TransformGroup(tf);
    tg.addChild(c);

    TransformGroup tg2 = new TransformGroup(tf1);
    tg2.addChild(tg);

    return tg2;
  }

  /**
   * Permet de créer l'apparence en fonction de paramètres Dans le cadre d'un
   * ponctuel, certains paramètres n'ont aucun sens
   * 
   * @param isColored
   * @param color
   * @param coefficientTransparence
   * @param isRepresentationSolid
   * @return
   */
  private static Appearance generateAppearance(Color color, double coefOpacity,
      boolean isSolid) {

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    // Autorisations pour l'apparence
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    // Création du material (gestion des couleurs et de l'affichage)
    Material material = new Material();

    material.setAmbientColor(0.2f, 0.2f, 0.2f);
    material.setDiffuseColor(new Color3f(color));
    material.setSpecularColor(new Color3f(color));
    material.setShininess(128);

    // et de material
    apparenceFinale.setMaterial(material);

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.BLENDED,
          (float) coefOpacity,

          TransparencyAttributes.BLEND_SRC_ALPHA,

          TransparencyAttributes.BLENDED);

      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    if (isSolid) {

      pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

      if (ConstantRepresentation.cullMode) {
        pa.setCullFace(PolygonAttributes.CULL_BACK);

      }

    } else {

      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);

    }

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    return apparenceFinale;

  }

}
