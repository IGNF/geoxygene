package fr.ign.cogit.geoxygene.sig3d.representation.symbol;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Node;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;

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
 * Représentation d'un objet ponctuel avec un carré coloré ou sur lequel une
 * image est texturée faisant face à la caméra Representation of a ponctual
 * object with a colored square or a square with an image mapped on it
 */
public class Symbol2D extends Default3DRep {

  private double width, length;
  private Texture2D text = null;
  private Color color = null;
  
  
  /**
   * 
   * @param feat
   * @param width
   * @param text
   */
  public Symbol2D(IFeature feat, double width, Texture2D text) {

    this(feat, width, width, text);
  }

  /**
   * Create square centered on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param length largeur du rectangle
   * @param text la texture que l'on souhaite appliquer aux faces
   */
  public Symbol2D(IFeature feat, double width, double length, Texture2D text) {
    super();
    this.feat = feat;
    this.width = width;
    this.length = length;
    this.text = text;

    List<BranchGroup> lShapes = this.generateAllCube();

    int nbShapes = lShapes.size();

    for (int i = 0; i < nbShapes; i++) {

      this.bGRep.addChild(lShapes.get(i));

    }

    lShapes.clear();

  }

  /**
   * Create squares centered on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param pathText le chemin de la texture que l'on souhaite appliquer aux
   *          faces
   */
  public Symbol2D(IFeature feat, double width, String pathText) {

    this(feat, width, width, TextureManager.textureLoading(pathText));

  }
  
  
  /**
   * Create squares centered on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param length longueur du cube
   * @param pathText le chemin de la texture que l'on souhaite appliquer aux
   *          faces
   */
  public Symbol2D(IFeature feat, double width, double length, String pathText) {

    this(feat, width, width, TextureManager.textureLoading(pathText));

  }
  
  /**
   * 
   * @param feat
   * @param width
   * @param color
   */
  public Symbol2D(FT_Feature feat, double width , Color color) {
    this(feat, width, width,  color);

  }

  /**
   * Create squares centered on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param length longueur du cube
   * @param color la couleur que l'on souhaite appliquer au cube
   */
  public Symbol2D(FT_Feature feat, double width, double length, Color color) {
    super(feat);
    this.width = width;
    this.width = length;
    this.color = color;

    List<BranchGroup> lShapes = this.generateAllCube();

    int nbShapes = lShapes.size();

    for (int i = 0; i < nbShapes; i++) {

      this.bGRep.addChild(lShapes.get(i));

    }
  }

  /**
   * Génère les cubes en fonction des attributs renseignés
   * 
   * @return
   */
  private List<BranchGroup> generateAllCube() {
    IDirectPositionList dpl = this.feat.getGeom().coord();

    int nbPoints = dpl.size();

    List<BranchGroup> lFinaleShape = new ArrayList<BranchGroup>();

    for (int i = 0; i < nbPoints; i++) {

      lFinaleShape.add(this.generateSquare(dpl.get(i)));
    }

    return lFinaleShape;
  }

  /**
   * Génère un cube portant les spécifications indiquées dans le constructeurs
   * aux coordonnées dp
   */
  private BranchGroup generateSquare(IDirectPosition dp) {

    double x = dp.getX();

    double y = dp.getY();

    double z = dp.getZ();

    Point3d p1 = new Point3d(-this.width / 2, 0, -this.length / 2);
    Point3d p2 = new Point3d(this.width / 2, 0, -this.length / 2);
    Point3d p3 = new Point3d(this.width / 2, 0, this.length / 2);
    Point3d p4 = new Point3d(-this.width / 2, 0, this.length / 2);

    // Construction de l'objet geometrique QuadArray constitue de 16
    // points
    QuadArray quadArray;

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    Point3d[] ptTab = new Point3d[] { p1, p2, p3, p4, p4, p3, p2, p1,

    };

    if (this.text == null) {
      // On applique une couleur unique au cube.
      quadArray = new QuadArray(8, GeometryArray.COORDINATES
          | GeometryArray.COLOR_3);

      Color3f color3f = new Color3f(this.color);

      // Tableau des points constituant les faces
      quadArray.setCoordinates(0, ptTab);

      // Tableau des couleurs des 4 sommets de chaque face
      quadArray.setColors(0, new Color3f[] { color3f, color3f, color3f,
          color3f, color3f, color3f, color3f, color3f });

    } else {

      TexCoord2f t0 = new TexCoord2f(0f, 0f);
      TexCoord2f t1 = new TexCoord2f(1f, 0f);
      TexCoord2f t2 = new TexCoord2f(1f, 1f);
      TexCoord2f t3 = new TexCoord2f(0f, 1f);

      // On applique les textures
      TexCoord2f[] texCoord = new TexCoord2f[] { t0, t1, t2, t3, t0, t1, t2,
          t3, };

      // On applique une couleur unique au cube.
      quadArray = new QuadArray(8, GeometryArray.COORDINATES
          | GeometryArray.TEXTURE_COORDINATE_2);

      // Tableau des points constituant les faces
      quadArray.setCoordinates(0, ptTab);

      quadArray.setTextureCoordinates(0, 0, texCoord);

      // Autorisations pour l'apparence
      apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
      apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

      // Autorisations pour le material

      apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
      apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

      apparenceFinale.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
      apparenceFinale.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);

      apparenceFinale.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

      apparenceFinale.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

      apparenceFinale.setTexture(this.text);
      apparenceFinale.setTextureAttributes(new TextureAttributes());

    }

    Shape3D s = new Shape3D();
    s.setGeometry(quadArray);
    s.setAppearance(apparenceFinale);

    // Autorisations sur la Shape3D
    s.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    s.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    s.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    s.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
    s.setCapability(Node.ALLOW_LOCALE_READ);

    // Create the transformgroup used for the billboard
    TransformGroup billBoardGroup = new TransformGroup();
    // Set the access rights to the group
    billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    AxisAngle4d rotateAxisAngle = new AxisAngle4d(1f, 0f, 0f, Math.PI / 2.0);

    Transform3D rotX = new Transform3D();
    rotX.set(rotateAxisAngle);

    TransformGroup tgRotX = new TransformGroup(rotX);
    tgRotX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    tgRotX.addChild(s);

    // Add the cube to the group
    billBoardGroup.addChild(tgRotX);

    Billboard myBillboard = new Billboard(billBoardGroup,

    Billboard.ROTATE_ABOUT_POINT, new Point3f());

    myBillboard.setSchedulingBounds(new BoundingSphere(new Point3d(),
        Double.POSITIVE_INFINITY));

    // On place le centre aux bonnes coordonnées
    Transform3D translate = new Transform3D();
    translate.set(new Vector3f((float) x, (float) y, (float) z));
    TransformGroup transform = new TransformGroup(translate);
    transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    transform.addChild(billBoardGroup);
    transform.addChild(myBillboard);

    BranchGroup bg = new BranchGroup();
    bg.addChild(transform);

    return bg;

  }

  @Override
  public Component getRepresentationComponent() {
    return TextureManager.componentFromTexture(this.text);
  }
}
