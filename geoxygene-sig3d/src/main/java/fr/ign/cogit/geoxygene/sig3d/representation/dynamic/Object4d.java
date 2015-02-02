package fr.ign.cogit.geoxygene.sig3d.representation.dynamic;

import java.awt.Color;
import java.util.List;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.PositionPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.IManagerModel;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager.Manager3DS;
import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager.ManagerObj;
import fr.ign.cogit.geoxygene.sig3d.representation.symbol.Symbol2D;
import fr.ign.cogit.geoxygene.sig3d.representation.symbol.Symbol3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * @version 1.7
 * 
 * Classement permettantl a représentation 4D d'une trajectoire
 * 
 * 
 */
public class Object4d extends Default3DRep {

  // Temps minimum et maixmum pour l'objet
  private double tMin, tMax;

  // Les différentes constantes des modes de représentation
  public static int DEFAULT_REPRESENTATION = 0;
  public static int MODEL3D_REPRESENTATION = 1;
  public static int SQUARE_REPRESENTATION = 2;
  public static int CUBE_REPRESENTATION = 3;

  /**
   * Permet de faire de la représentaiton temporelle de trajectoire à partir
   * @param feat d'une entité portant une géométrie linéaire
   * @param time d'une liste de temps (ayant autant d'entrées que le linéaire de
   *          sommets)
   * @param tMinLayer le temps t0 de la couche
   * @param tMaxLayer le temps t1 de la couche
   * @param alpha fonction alpha qui permettra de faire s'écouler le temps
   * @param path chemin du fichier si il y a des objets à charger
   * @param size taille si nécessaire
   * @param representationMode mode de représentation
   */
  public Object4d(IFeature feat, List<Double> time, double tMinLayer,
      double tMaxLayer, Alpha alpha, String path, double size,
      int representationMode, double rotX, double rotY, double rotZ) {

    super();

    // La branche qui représentera l'objet
    BranchGroup b = new BranchGroup();

    if (representationMode == Object4d.DEFAULT_REPRESENTATION) {
      // Représentation par sphère
      b.addChild(new Sphere((int) size, Object4d.generateAppearance(true,
          Color.red, 1, true)));
    } else if (representationMode == Object4d.MODEL3D_REPRESENTATION) {
      // Représentation à l'aide d'un modèle 3D
      IManagerModel manager = null;

      if (path.toUpperCase().contains(".3DS")) { //$NON-NLS-1$
        manager = Manager3DS.getInstance();

      } else if (path.toUpperCase().contains(".OBJ")) { //$NON-NLS-1$
        manager = ManagerObj.getInstance();
      } else {

        System.out.println("Format non reconnu");
        return;
      }

      BranchGroup bIni = (BranchGroup) manager.loadingFile(path).cloneTree();

      // Rotation en X, en Y et en Z
      Transform3D trotX = new Transform3D();
      trotX.rotX(rotX);
      TransformGroup tgRotX = new TransformGroup(trotX);
      tgRotX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      Transform3D trotY = new Transform3D();
      trotY.rotY(rotY);
      TransformGroup tgRotY = new TransformGroup(trotY);
      tgRotY.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      Transform3D trotZ = new Transform3D();
      trotZ.rotZ(rotZ);
      TransformGroup tgRotZ = new TransformGroup(trotZ);
      tgRotZ.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      // Création d'une transformation pour permettre une mise à l'échelle
      Transform3D echelleZ = new Transform3D();
      echelleZ.setScale(new Vector3d(size, size, size));
      TransformGroup tgechelleZ = new TransformGroup(echelleZ);

      tgechelleZ.addChild(bIni);
      tgRotZ.addChild(tgechelleZ);
      tgRotY.addChild(tgRotZ);
      tgRotX.addChild(tgRotY);

      b.addChild(tgRotX);

    } else if (representationMode == Object4d.SQUARE_REPRESENTATION) {
      // Représentation par rectangle
      IFeature featureTemp = new DefaultFeature(new GM_Point(
          new DirectPosition(0.0, 0.0, 0.0)));
      Symbol2D s = new Symbol2D(featureTemp, size, path);
      b.addChild(s.getBGRep());

    } else if (representationMode == Object4d.CUBE_REPRESENTATION) {
      // Représentation par cube
      IFeature featureTemp = new DefaultFeature(new GM_Point(
          new DirectPosition(0.0, 0.0, 0.0)));
      Symbol3D s = new Symbol3D(featureTemp, size, path);
      b.addChild(s.getBGRep());
    }

    this.generation(feat, time, tMinLayer, tMaxLayer, alpha, b);

  }

  /**
   * Génère la représentation
   * @param feat
   * @param time
   * @param tMinLayer
   * @param tMaxLayer
   * @param alpha
   * @param b
   */
  private void generation(IFeature feat, List<Double> time, double tMinLayer,
      double tMaxLayer, Alpha alpha, BranchGroup b) {

    this.feat = feat;
    // On génère des informations relatives à l'objet tMin et tMax

    int nbT = time.size();
    this.tMin = Double.POSITIVE_INFINITY;
    this.tMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < nbT; i++) {
      if (time.get(i) > this.tMax) {
        this.tMax = time.get(i);
      }
      if (time.get(i) < this.tMin) {
        this.tMin = time.get(i);
      }

    }

    this.bGRep.addChild(this.generateBG(feat.getGeom().coord(), time,
        tMinLayer, tMaxLayer, alpha, b));

    this.bGRep.compile();

  }

  /**
   * Permet de générer la simulation
   * @param dpl les points à parcourir
   * @param time le tableau de temps
   * @param tMinLayer le tminimum de la couche
   * @param tMaxLayer le tmaximum de la couche
   * @param alpha la fonction alpha
   * @param b le branch group représentant l'objet en mouvement
   * @return
   */
  private BranchGroup generateBG(IDirectPositionList dpl, List<Double> time,
      double tMinLayer, double tMaxLayer, Alpha alpha, BranchGroup b) {

    BranchGroup bg = new BranchGroup();

    // on crée le BranchGroup de l'animation
    bg = new BranchGroup();
    bg.setCapability(Group.ALLOW_CHILDREN_READ);
    bg.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    bg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    bg.setCapability(BranchGroup.ALLOW_DETACH);

    // Il s'agit du TransformGroup qui àvoluera avec le temps
    TransformGroup objSpin = new TransformGroup();
    objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // On place la première et la dernière position à zéro
    if (((this.tMin - tMinLayer) / (tMaxLayer - tMinLayer)) != 0) {

      dpl.add(0, dpl.get(0));
      time.add(0, tMinLayer);

    }

    if (((this.tMax - tMinLayer) / (tMaxLayer - tMinLayer)) != 1) {

      dpl.add(dpl.size() - 1, dpl.get(dpl.size() - 1));
      time.add(time.size() - 1, tMaxLayer);
    }

    // Nombre de ponits parcourus
    int nbPoints = dpl.size();

    Point3f[] positions = new Point3f[nbPoints];
    float[] knots = new float[nbPoints];

    // Calcul de la distance cumulée à parcourir lors de l'animation
    // elle est utile pour règler les paramètres de vitesse

    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition dpTemp = dpl.get(i);

      // On ne garde que les modifications de déplacement relatif
      positions[i] = new Point3f((float) (dpTemp.getX()),
          (float) (dpTemp.getY()), (float) (dpTemp.getZ()));

      if (i == 0) {
        knots[i] = 0.0f;
        continue;
      }

      if (i == nbPoints - 1) {
        knots[i] = 1.0f;
        continue;
      }

      knots[i] = (float) ((time.get(i) - tMinLayer) / (tMaxLayer - tMinLayer));
    }

    System.out.println("Premier" + knots[0] + "last" + knots[knots.length - 1]);

    // On crée l'interpolateur ad hoc et on le lie au tg
    PositionPathInterpolator p = new PositionPathInterpolator(alpha, objSpin,
        new Transform3D(), knots, positions);
    BoundingSphere bounds2 = new BoundingSphere();
    p.setSchedulingBounds(bounds2);
    objSpin.addChild(p);

    // on crée le BranchGroup portant l'animation
    this.bGRep.addChild(objSpin);
    objSpin.addChild(b);

    return bg;
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
  private static Appearance generateAppearance(boolean isClrd, Color color,
      double coeffOpacite, boolean isSolid) {

    // Création de l'apparence
    Appearance appFin = new Appearance();

    // Autorisations pour l'apparence
    appFin.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    appFin.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    appFin.setCapability(Appearance.ALLOW_MATERIAL_READ);
    appFin.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
    if (isSolid) {
      // Indique que l'on est en mode surfacique
      pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

      // Indique que l'on n'affiche pas les faces cachées
      if (ConstantRepresentation.cullMode) {
        pa.setCullFace(PolygonAttributes.CULL_BACK);

      }

    } else {
      // Indique que l'on est en mode filaire
      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);

    }

    if (isClrd) {

      // Création du material (gestion des couleurs et de l'affichage)
      Material material = new Material();

      material.setAmbientColor(0.2f, 0.2f, 0.2f);
      material.setDiffuseColor(new Color3f(color));
      material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
      material.setShininess(128);

      // et de material
      appFin.setMaterial(material);
    }

    if (coeffOpacite != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.BLENDED,
          (float) coeffOpacite,

          TransparencyAttributes.BLEND_SRC_ALPHA,

          TransparencyAttributes.BLENDED);

      // et de transparence
      appFin.setTransparencyAttributes(t_attr);
    }

    appFin.setPolygonAttributes(pa);
    return appFin;

  }

}
