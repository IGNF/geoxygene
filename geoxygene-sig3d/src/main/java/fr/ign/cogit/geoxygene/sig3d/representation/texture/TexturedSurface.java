package fr.ign.cogit.geoxygene.sig3d.representation.texture;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Color3f;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;

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
 * Classe permettant de représenter des solides en 3D en associant une texture.
 * La texture est de type génèrique, c'est à dire qu'elle est répétée à l'infini
 * sur toute la surface de chaque facettes de l'objet
 * 
 * Basic class for rendering textured 3D objects
 * 
 */
public class TexturedSurface extends Default3DRep {

  // La texture que l'on souhaite afficher
  private Texture2D texture;

  // La taille en m représenté par l'image
  private double imageLength;
  private double imageHeigth;

  private final static Logger logger = Logger.getLogger(TexturedSurface.class
      .getName());

  /**
   * Permet d'appliquer une texture à une entité. Appel this(feat,tex,10,10)
   * 
   * @param feat L'entité utilisée pour Génèrer la représentation
   * @param tex La texture que l'on souhaite appliquer
   */
  public TexturedSurface(IFeature feat, Texture2D tex) {

    this(feat, tex, 10, 10);
  }

  /**
   * Permet d'appliquer une texture générique à une entité
   * 
   * @param feat L'entité utilisée pour Génèrer la représentation
   * @param tex La texture que l'on souhaite appliquer
   * @param imageLength la longueur que représente l'image dans le monde réel
   * @param imageHeigth la hauteur que représente l'image dans le monde réel
   */
  public TexturedSurface(IFeature feat, Texture2D tex, double imageLength,
      double imageHeigth) {

    // On créer le BranchGroup avec les autorisations ad hoc
    super();

    this.texture = tex;
    this.feat = feat;
    this.imageLength = imageLength;
    this.imageHeigth = imageHeigth;

    // préparation de la géométrie Java3D
    GeometryInfo geometryInfo = null;

    geometryInfo = Util.geometryWithTexture(feat.getGeom(), imageLength,
        imageHeigth);

    if (geometryInfo == null) {
      TexturedSurface.logger
          .warn(Messages.getString("Representation.RepNulle"));
      return;
    }

    // préparation de l'apparence
    Appearance apparence = this.generateAppearance();

    // Calcul de l'objet Shape3D
    Shape3D shapepleine = new Shape3D(geometryInfo.getGeometryArray(),
        apparence);

    // Autorisations sur la Shape3D
    shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shapepleine.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    shapepleine.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
    shapepleine.setCapability(Node.ALLOW_LOCALE_READ);

    // Ajout au BgClone les elements transformes
    this.bGRep.addChild(shapepleine);

    // Optimisation
    this.bGRep.compile();
  }

  /**
   * Génère l'apparence de l'objet
   * 
   * @return
   */
  private Appearance generateAppearance() {

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

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

    apparenceFinale.setTexture(this.texture);
    apparenceFinale.setTextureAttributes(new TextureAttributes());

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    return apparenceFinale;

  }

  /**
   * Renvoie les géométries générées pour un objet
   * 
   * @return
   */
  private ArrayList<Shape3D> getShapes() {

    ArrayList<Shape3D> shapes = new ArrayList<Shape3D>(1);
    Enumeration<?> enumGroup = this.bGRep.getAllChildren();

    while (enumGroup.hasMoreElements()) {

      Object objTemp = enumGroup.nextElement();
      // Théoriquement la géométrie Java3D n'est rattachée qu'au premier
      // noeud
      if (objTemp instanceof Shape3D) {
        shapes.add((Shape3D) objTemp);
      }
    }

    return shapes;
  }

  @Override
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;

    List<Shape3D> shapes = this.getShapes();
    int nbElem = shapes.size();

    for (int i = 0; i < nbElem; i++) {
      Appearance ap = shapes.get(i).getAppearance();

      if (isSelected) {

        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(ConstantRepresentation.selectionColor));
        ap.setColoringAttributes(ca);
        ap.setTexture(null);
        ap.setTextureAttributes(null);
      } else {

        ap.setTextureAttributes(new TextureAttributes());
        ap.setTexture(this.texture);

        shapes.get(i).getAppearance().setColoringAttributes(null);
        shapes.get(i).setAppearance(ap);
      }

    }

  }

  /**
   * @param cull
   */
  public void setCullMode(boolean cull) {

    PolygonAttributes pa = this.getShapes().get(0).getAppearance()
        .getPolygonAttributes();

    if (ConstantRepresentation.cullMode) {
      pa.setCullFace(PolygonAttributes.CULL_BACK);

    } else {
      pa.setCullFace(PolygonAttributes.CULL_NONE);

    }

    this.getShapes().get(0).getAppearance().setPolygonAttributes(pa);
  }

  /**
   * 
   * @return la texture affectée à l'objet
   */
  public Texture2D getTexture() {
    return this.texture;
  }

  /**
   * 
   * @return la longueur en m de la texture dans le monde réel
   */
  public double getImageLength() {
    return this.imageLength;
  }

  /**
   * 
   * @return la hateur en m de la texture dans le monde réel
   */
  public double getImageHeigth() {
    return this.imageHeigth;
  }

  @Override
  public Component getRepresentationComponent() {

    return TextureManager.componentFromTexture(this.getTexture());

  }

}
