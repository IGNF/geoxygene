package fr.ign.cogit.geoxygene.sig3d.representation.basic;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
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
 * @version 0.1
 * 
 * Classe permettant de représenter des points en 3D.
 * 
 * Basic class for rendering 3D points
 * 
 */
public class Object0d extends BasicRep3D {

  private final static Logger logger = LogManager.getLogger(Object0d.class
      .getName());
  /**
   * Liste des sphères représentant les ponctuels
   */
  private ArrayList<Sphere> lShape = new ArrayList<Sphere>();

  /**
   * Rayon des sphères
   */
  public static float RADIUS = 1.0f;

  /**
   * créer la représentation d'un objet avec une géométrie de type ponctuel.
   * créer le lien entre la représentation et l'objet indispensable pour pouvoir
   * effectuer des sélections
   * 
   * @param feat l'entite dont la geométrie servira pour faire la représentation
   * @param isClrd indique si l'on applique une couleur unique (true) ou une
   *          couleur aleatoire (false)
   * @param color couleur appliquée si isClrd == true
   * @param coefOpacity coefficient d'opacité applique sur l'objet
   * @param isSolid
   */
  public Object0d(IFeature feat, boolean isClrd, Color color,
      double coefOpacity, boolean isSolid) {

    super(feat, isClrd, color, coefOpacity, isSolid);

    ArrayList<TransformGroup> lTG = null;
    // On Génère la représentation Java3D en fonction de ses attributs
    if (isClrd) {
      Appearance ap = this.generateAppearance(isClrd, color, coefOpacity,
          isSolid);
      lTG = this.geometryWithColor(ap);
    } else {
      Appearance ap = this.generateAppearance(isClrd, color, coefOpacity,
          isSolid);
      lTG = this.geometryWithOutColor(ap);
    }
    // Passer les informations de l'objet au BG
    int nbSphere = this.lShape.size();

    // On ajoute les sphère à la Branch Group
    for (int i = 0; i < nbSphere; i++) {
      this.bGRep.addChild(lTG.get(i));
    }

    // Optimisation
    this.bGRep.compile();
  }

  /**
   * 
   * Permet de créer une géométrie Java3D à partir d'une apparence
   * 
   * @param ap
   * @return
   */
  private ArrayList<TransformGroup> geometryWithColor(Appearance ap) {
    // On récupère la géométrie sous forme de points
    IGeometry geom = this.feat.getGeom();

    if (geom instanceof GM_MultiPoint) {

      // On peut àventuellement vouloir ajouter des traitements

    } else if (geom instanceof GM_Point) {

      // On peut àventuellement vouloir ajouter des traitements
    } else {
      Object0d.logger.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }

    IDirectPositionList lDP = geom.coord();

    int nbPoints = lDP.size();

    PointArray pA = new PointArray(nbPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3);

    ArrayList<TransformGroup> lTG = new ArrayList<TransformGroup>(nbPoints);
    Object0d.logger.debug(nbPoints);
    pA.setCapability(GeometryArray.ALLOW_COLOR_READ);
    pA.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

    for (int i = 0; i < nbPoints; i++) {

      // On crée la sphère et on lui applique l'apparence choisie
      Sphere s = new Sphere(Object0d.RADIUS, ap);

      s.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
      s.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      s.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

      s.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      s.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

      IDirectPosition pTemp = lDP.get(i);

      // On place le centre de la sphère aux bonnes coordonnées
      Transform3D translate = new Transform3D();
      translate.set(new Vector3f((float) pTemp.getX(), (float) pTemp.getY(),
          (float) pTemp.getZ()));

      TransformGroup TG1 = new TransformGroup(translate);
      TG1.addChild(s);

      lTG.add(TG1);
      // On complète la liste des sphères
      this.lShape.add(s);

    }

    return lTG;

  }

  /**
   * Génère une représentation avec une couleur pour chaque sphère
   * 
   * @param ap
   * @return
   */
  private ArrayList<TransformGroup> geometryWithOutColor(Appearance ap) {
    // On décompose sous forme de points la géométrie
    IGeometry geom = this.feat.getGeom();
    if (geom instanceof GM_MultiPoint) {

      // On peut àventuellement vouloir ajouter des traitements

    } else if (geom instanceof GM_Point) {
      // On peut àventuellement vouloir ajouter des traitements

    } else {
      Object0d.logger.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }
    IDirectPositionList lDP = geom.coord();

    int nbPoints = lDP.size();

    PointArray pA = new PointArray(nbPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3);

    ArrayList<TransformGroup> lTG = new ArrayList<TransformGroup>(nbPoints);

    pA.setCapability(GeometryArray.ALLOW_COLOR_READ);
    pA.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

    for (int i = 0; i < nbPoints; i++) {

      Sphere s = new Sphere(Object0d.RADIUS, ap);

      // on ajoute une couleur aléatoire
      ColoringAttributes CA = new ColoringAttributes(new Color3f(
          (float) Math.random(), (float) Math.random(), (float) Math.random()),
          ColoringAttributes.SHADE_GOURAUD);
      ap.setColoringAttributes(CA);

      s.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
      s.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      s.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

      s.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      s.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

      IDirectPosition pTemp = lDP.get(i);

      // On place le sphère aux bonnes coordonnées
      Transform3D translate = new Transform3D();
      translate.set(new Vector3f((float) pTemp.getX(), (float) pTemp.getY(),
          (float) pTemp.getZ()));

      TransformGroup TG1 = new TransformGroup(translate);
      TG1.addChild(s);

      lTG.add(TG1);
      this.lShape.add(s);

    }

    return lTG;

  }

  /**
   * Change la couleur d'un objet
   * 
   * @param coul
   */
  public void setColor(Color coul) {

    this.color = coul;
    this.changeColor(this.color);
  }

  /**
   * Change la couleur sans changer l'apparence Utile pour les sélections
   */

  private void changeColor(Color color) {

    Appearance ap = this.generateAppearance(this.isColored, color,
        this.opacity, this.isSolid);
    ColoringAttributes CA = null;
    if (color == null) {

      CA = new ColoringAttributes(new Color3f((float) Math.random(),
          (float) Math.random(), (float) Math.random()),
          ColoringAttributes.SHADE_GOURAUD);

    } else {
      CA = new ColoringAttributes(new Color3f(color),
          ColoringAttributes.SHADE_GOURAUD);

    }

    ap.setColoringAttributes(CA);

    int nbSphere = this.lShape.size();

    for (int i = 0; i < nbSphere; i++) {

      this.lShape.get(i).setAppearance(ap);
    }

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
  private Appearance generateAppearance(boolean isClrd, Color color,
      double coeffOpacite, boolean isSolid) {

    this.isColored = isClrd;

    this.opacity = coeffOpacite;
    this.isSolid = isSolid;

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
      pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

    }

    if (isClrd) {

      // Création du material (gestion des couleurs et de l'affichage)
      Material material = new Material();

     
        
        
        material.setAmbientColor(new Color3f(color));
        material.setDiffuseColor(new Color3f(color));
        material.setSpecularColor(new Color3f(color));
        material.setEmissiveColor(new Color3f(color));
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

  @Override
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;

    if (isSelected) {

      this.changeColor(ConstantRepresentation.selectionColor);

    } else {
      this.changeColor(this.color);

    }

  }

}
