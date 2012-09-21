package fr.ign.cogit.geoxygene.sig3d.representation.symbol;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
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
 * Représentation d'objet ponctuel par une cube coloré ou par un cube sur lequel
 * une image sera plaquée Representation of a ponctual object with a colored
 * cube or a cube with an image mapped on its faces
 */
public class Symbol3D extends Default3DRep {

  private double width;
  private Texture2D text = null;
  private Color color = null;

  /**
   * Create cubes center on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param text la texture que l'on souhaite appliquer aux faces
   */
  public Symbol3D(IFeature feat, double width, Texture2D text) {
    super();
    this.feat = feat;
    this.width = width;
    this.text = text;

    List<Shape3D> lShapes = this.generateAllCube();

    int nbShapes = lShapes.size();

    for (int i = 0; i < nbShapes; i++) {

      this.bGRep.addChild(lShapes.get(i));

    }

    lShapes.clear();

  }

  /**
   * Create cubes center on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param pathText le chemin de la texture que l'on souhaite appliquer aux
   *          faces
   */
  public Symbol3D(IFeature feat, double width, String pathText) {

    this(feat, width, TextureManager.textureLoading(pathText));

  }

  /**
   * Create cubes center on the different points of the feature
   * 
   * @param feat entité à laquelle sera associée la représentation
   * @param width largeur du cube
   * @param color la couleur que l'on souhaite appliquer au cube
   */
  public Symbol3D(IFeature feat, double width, Color color) {
    super();
    this.feat = feat;
    this.width = width;
    this.color = color;

    List<Shape3D> lShapes = this.generateAllCube();

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
  private List<Shape3D> generateAllCube() {
    IDirectPositionList dpl = this.feat.getGeom().coord();

    int nbPoints = dpl.size();

    List<Shape3D> lFinaleShape = new ArrayList<Shape3D>();

    for (int i = 0; i < nbPoints; i++) {

      lFinaleShape.add(this.generateCube(dpl.get(i)));
    }

    return lFinaleShape;
  }

  /**
   * Génère un cube portant les spécifications indiquées dans le constructeurs
   * aux coordonnées dp
   */
  private Shape3D generateCube(IDirectPosition dp) {

    double xMin = dp.getX() - this.width / 2;
    double xMax = dp.getX() + this.width / 2;

    double yMin = dp.getY() - this.width / 2;
    double yMax = dp.getY() + this.width / 2;

    double zMin = dp.getZ() - this.width
        / (ConstantRepresentation.scaleFactorZ * 2);
    double zMax = dp.getZ() + this.width
        / (ConstantRepresentation.scaleFactorZ * 2);

    Point3d p1 = new Point3d(xMin, yMin, zMin);
    Point3d p2 = new Point3d(xMin, yMax, zMin);
    Point3d p3 = new Point3d(xMax, yMax, zMin);
    Point3d p4 = new Point3d(xMax, yMin, zMin);

    Point3d p5 = new Point3d(xMin, yMin, zMax);
    Point3d p6 = new Point3d(xMin, yMax, zMax);
    Point3d p7 = new Point3d(xMax, yMax, zMax);
    Point3d p8 = new Point3d(xMax, yMin, zMax);

    // Construction de l'objet geometrique QuadArray constitue de 16
    // points
    QuadArray quadArray;

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    Point3d[] ptTab = new Point3d[] { p1, p2, p3, p4, p1, p4, p8, p5, p4, p3,
        p7, p8, p3, p2, p6, p7, p2, p1, p5, p6, p5, p8, p7, p6 };

    if (this.text == null) {
      // On applique une couleur unique au cube.
      quadArray = new QuadArray(24, GeometryArray.COORDINATES
          | GeometryArray.COLOR_3);

      Color3f color3f = new Color3f(this.color);

      // Tableau des points constituant les faces
      quadArray.setCoordinates(0, ptTab);

      // Tableau des couleurs des 4 sommets de chaque face
      quadArray.setColors(0, new Color3f[] { color3f, color3f, color3f,
          color3f, color3f, color3f, color3f, color3f, color3f, color3f,
          color3f, color3f, color3f, color3f, color3f, color3f, color3f,
          color3f, color3f, color3f, color3f, color3f, color3f, color3f });

    } else {

      TexCoord2f t0 = new TexCoord2f(0f, 0f);
      TexCoord2f t1 = new TexCoord2f(1f, 0f);
      TexCoord2f t2 = new TexCoord2f(1f, 1f);
      TexCoord2f t3 = new TexCoord2f(0f, 1f);

      // On applique les textures
      TexCoord2f[] texCoord = new TexCoord2f[] { t0, t1, t2, t3, t0, t1, t2,
          t3, t0, t1, t2, t3, t0, t1, t2, t3, t0, t1, t2, t3, t0, t1, t2, t3 };

      // On applique une couleur unique au cube.
      quadArray = new QuadArray(24, GeometryArray.COORDINATES
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

    return s;

  }

  @Override
  public Component getRepresentationComponent() {
    return TextureManager.componentFromTexture(this.text);
  }

}
