package fr.ign.cogit.geoxygene.sig3d.representation.toponym;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

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
 * Implémentation des paramètres de bases pour un toponyme (faire toujours face
 * à la caméra ou angle dans l'espage, couleur, taille police). Cette
 * représentation s'applique à des objets de dimension 0.
 * 
 * Implementation of standard toponym (billboard or fix position, color, size,
 * police). This style has to be applyed on a 0D object
 */
public class BasicToponym3D extends DefaultToponym3D {

  private final static Logger logger = Logger.getLogger(BasicToponym3D.class
      .getName());

  private boolean billboard;

  /**
   * Permet de créer un toponymes en prenant les différents attributs décrits
   * dans Default3DToponyme
   * 
   * @param feat l'entité qui permet de calculer la représentation
   * @param color la couleur appliquée
   * @param coefOpacity coefficient d'opacite
   * @param lambda angle de rotation suivant l'axe des X
   * @param teta angle de rotation suivant l'axe des Y
   * @param phi angle de rotation suivant l'axe des Z
   * @param text le texte que l'on affichera pour cette entité
   * @param police la police de caractère que l'on appliquera
   * @param size la taille (en m) du texte
   * @param billboard indique si l'on souhaite que le texte face face à la
   *          caméra
   */
  public BasicToponym3D(IFeature feat, Color color, double coefOpacity,
      double lambda, double teta, double phi, String text, String police,
      double size, boolean billboard) {

    super();

    this.feat = feat;
    this.color = color;

    this.opacity = coefOpacity;

    this.lambda = lambda;
    this.teta = teta;
    this.phi = phi;
    this.text = text;
    this.police = police;
    this.size = size;
    this.billboard = billboard;

    Appearance ap = this.genereApparence(color, coefOpacity);
    ArrayList<TransformGroup> lTG = this.geometryWithColor(ap);

    // Passer les informations de l'objet au BG
    int nbSphere = lTG.size();

    // On ajoute les objets à la Branch Group

    for (int i = 0; i < nbSphere; i++) {

      this.bGRep.addChild(lTG.get(i));

    }

    // Optimisation
    this.bGRep.compile();

  }

  /**
   * @return Indique si un billboard est rattaché à la représentation (faire
   *         toujours face à la caméra)
   * 
   */
  public boolean isBillboard() {
    return this.billboard;
  }

  private Shape3D s3D1 = null;

  /**
   * Renvoie la liste des shape3D définissant le toponyme, utile pour modifier
   * la représentation
   * 
   * @return toponyme en Shape3D
   */
  public ArrayList<Shape3D> getShapes() {
    ArrayList<Shape3D> lS = new ArrayList<Shape3D>();
    if (this.s3D1 != null) {
      lS.add(this.s3D1);
    }
    return lS;
  }

  /**
   * Permet de créer une géométrie Java3D à partir d'unee apparence
   * 
   * @param ap
   * @return
   */
  private ArrayList<TransformGroup> geometryWithColor(Appearance ap) {

    IGeometry geom = this.feat.getGeom();

    if (geom instanceof GM_MultiPoint) {

      // On peut àventuellement vouloir ajouter des traitements

    } else if (geom instanceof GM_Point) {

      // On peut àventuellement vouloir ajouter des traitements
    } else {
      BasicToponym3D.logger.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }

    IDirectPositionList lDP = geom.coord();

    int nbPoints = lDP.size();

    PointArray pA = new PointArray(nbPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3);

    ArrayList<TransformGroup> lTG = new ArrayList<TransformGroup>(nbPoints);

    pA.setCapability(GeometryArray.ALLOW_COLOR_READ);
    pA.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

    // Rotation en X, en Y et en Z
    Transform3D rotX = new Transform3D();
    rotX.rotX(this.lambda);// -Math.PI / 2.0);
    TransformGroup tgRotX = new TransformGroup(rotX);
    tgRotX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Transform3D rotY = new Transform3D();
    rotY.rotY(this.teta);
    TransformGroup tgRotY = new TransformGroup(rotY);
    tgRotY.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Transform3D rotZ = new Transform3D();
    rotZ.rotZ(this.phi);
    TransformGroup tgRotZ = new TransformGroup(rotZ);
    tgRotZ.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition pTemp = lDP.get(i);

      Font3D f3d = new Font3D(new Font(this.police, Font.PLAIN, 1),
          new FontExtrusion());
      Text3D text3D = new Text3D(f3d, this.text, new Point3f(0, 0, 0),
          Text3D.ALIGN_CENTER, Text3D.PATH_RIGHT);

      text3D.setCapability(Text3D.ALLOW_FONT3D_WRITE);
      text3D.setCapability(Geometry.ALLOW_INTERSECT);

      this.s3D1 = new Shape3D();
      this.s3D1.setGeometry(text3D);
      this.s3D1.setAppearance(ap);

      this.s3D1.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      this.s3D1.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

      // On place le centre de la sphère aux bonnes coordonnées
      Transform3D translate = new Transform3D();
      translate.set(new Vector3f((float) pTemp.getX(), (float) pTemp.getY(),
          (float) pTemp.getZ()));

      TransformGroup objScale = new TransformGroup();
      Transform3D t3d = new Transform3D();
      t3d.setScale(this.size);
      objScale.setTransform(t3d);
      objScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      TransformGroup tg1 = new TransformGroup(translate);

      if (this.billboard) {

        // Create the transformgroup used for the billboard
        TransformGroup billBoardGroup = new TransformGroup();
        // Set the access rights to the group
        billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        // Add the cube to the group
        billBoardGroup.addChild(this.s3D1);

        Billboard myBillboard = new Billboard(billBoardGroup,

        Billboard.ROTATE_ABOUT_POINT, new Vector3f());

        myBillboard.setSchedulingBounds(billBoardGroup.getBounds());

        objScale.addChild(billBoardGroup);
        objScale.addChild(myBillboard);
      } else {
        objScale.addChild(this.s3D1);
      }

      tgRotZ.addChild(objScale);
      tgRotY.addChild(tgRotZ);
      tgRotX.addChild(tgRotY);

      tg1.addChild(tgRotX);
      // Set the access rights to the group
      tg1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      lTG.add(tg1);

    }

    return lTG;

  }

  /**
   * Permet de créer l'apparence en fonction de paramètres Dans le cadre d'un
   * ponctuel, certains paramètres n'ont aucun sens
   * 
   * @param isColored
   * @param color
   * @param coefOpacity
   * @param isRepresentationSolid
   * @return l'apparence à appliquer
   */
  private Appearance genereApparence(Color color, double coefOpacity) {

    Color3f col3f = new Color3f(color);

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
    material.setDiffuseColor(col3f);
    material.setSpecularColor(col3f);
    material.setShininess(128);

    // et de material
    apparenceFinale.setMaterial(material);

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.BLENDED,
          (float) coefOpacity,

          TransparencyAttributes.BLEND_SRC_ALPHA,

          TransparencyAttributes.BLENDED);

      // et de transparence
      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    return apparenceFinale;

  }

  @Override
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;

    List<Shape3D> shapes = this.getShapes();
    int nbElem = shapes.size();
    // On récupère les différentes apparences et on affecte un facteur de
    // couleur
    for (int i = 0; i < nbElem; i++) {

      if (isSelected) {
        shapes.get(i).setAppearance(
            this.genereApparence(ConstantRepresentation.selectionColor, 0));

      } else {
        shapes.get(i).setAppearance(
            this.genereApparence(this.color, this.opacity));

      }

    }

  }

}
