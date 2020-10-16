package fr.ign.cogit.geoxygene.sig3d.representation.sample;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.Util;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * Classe permettant de représenter des solides en 3D. En appliquant un type de
 * texture prédéfini pour le toit et un type de texture prédéfini pour les murs.
 * Le distingo mur/toit se fait en regardant l'angle des normales des faces par
 * rapport à l'horizontale Basic class for rendering 3D objects
 * 
 */
public class BuildingTexture extends Default3DRep {

  private Texture2D wallTexture;
  private Texture2D roofTexture;

  private double textureRoofLength;
  private double textureRoofHeigth;

  private double textureWallLength;
  private double textureWallHeigth;
  private final static Logger logger = LogManager.getLogger(BuildingTexture.class
      .getName());

  /**
   * Constructeur de la représentation à partir d'une entité. Celle-ci doit
   * avoir une géométrie de type GM_Solid ou GM_MultiSolid
   * 
   * @param feat l'entité à laquelle on applique la texture
   * @param textureWall la texture que l'on appliquera aux faces verticles
   * @param textureRoof la texture que l'on applique aux faces non verticles
   */
  public BuildingTexture(IFeature feat, Texture2D textureWall,
      double textureWallLength, double textureWallHeigth,
      Texture2D textureRoof, double textureRoofLength, double textureRoofHeigth) {
    super();
    this.feat = feat;
    this.wallTexture = textureWall;
    this.roofTexture = textureRoof;

    this.textureRoofLength = textureRoofLength;
    this.textureRoofHeigth = textureRoofHeigth;

    this.textureWallLength = textureWallLength;
    this.textureWallHeigth = textureWallHeigth;

    // On décompose la géoémtrie sous forme d'un liste de facettes
    IGeometry geom = this.feat.getGeom();
    ArrayList<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();

    if (geom instanceof ISolid) {
      ISolid corps = (ISolid) geom;
      lFacettes.addAll(corps.getFacesList());

    } else if (geom instanceof IMultiSolid<?>) {

      GM_MultiSolid<?> multiCorps = (GM_MultiSolid<?>) geom;

      List<? extends ISolid> lOS = multiCorps.getList();

      int nbElements = lOS.size();

      for (int i = 0; i < nbElements; i++) {

        List<IOrientableSurface> lSurf = (lOS.get(i)).getFacesList();

        lFacettes.addAll(lSurf);

      }

    } else if (geom instanceof ICompositeSolid) {

      GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;

      List<? extends ISolid> lOS = compSolid.getGenerator();

      int nbElements = lOS.size();

      for (int i = 0; i < nbElements; i++) {
        ISolid s = lOS.get(i);
        lFacettes.addAll(s.getFacesList());
      }

    } else if (geom instanceof GM_MultiSurface<?>) {
      lFacettes.addAll(((GM_MultiSurface<?>) geom).getList());

    } else if (geom instanceof GM_OrientableSurface) {
      lFacettes.add((GM_OrientableSurface) geom);

    } else {

      BuildingTexture.logger.warn(Messages.getString("Representation.GeomUnk")
          + " : " + geom.getClass());
      return;
    }

    int nbFacet = lFacettes.size();
    // Une liste pour les facettes de Murs et une autre pour les facettes de
    // toits
    ArrayList<IOrientableSurface> lMurs = new ArrayList<IOrientableSurface>();
    ArrayList<IOrientableSurface> lToits = new ArrayList<IOrientableSurface>();

    // Initialisation des tailles de tableaux
    for (int i = 0; i < nbFacet; i++) {

      IOrientableSurface os = lFacettes.get(i);

      ApproximatedPlanEquation eq = new ApproximatedPlanEquation(os);
      Vecteur vect = eq.getNormale();

      if (Math.abs(vect.prodScalaire(MathConstant.vectZ)) < 0.01) {

        lMurs.add(os);

      } else {
        lToits.add(os);

      }

    }

    // préparation de la géométrie Java3D
    GeometryInfo geometryInfoMurs = Util.geometryWithTexture(
        new GM_Solid(lMurs), textureWallLength, textureWallHeigth);
    GeometryInfo geometryInfoToits = Util.geometryWithTexture(new GM_Solid(
        lToits), textureRoofLength, textureRoofHeigth);

    // préparation de l'apparence
    Appearance apparenceMur = this.generateAppearance(false);
    Appearance apparenceToit = this.generateAppearance(true);

    if (geometryInfoMurs != null) {
      // Calcul de l'objet Shape3D
      Shape3D shapepleineMur = new Shape3D(geometryInfoMurs.getGeometryArray(),
          apparenceMur);
      // Autorisations sur la Shape3D
      shapepleineMur.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shapepleineMur.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shapepleineMur.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shapepleineMur.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      shapepleineMur.setCapability(Node.ALLOW_LOCALE_READ);

      this.bGRep.addChild(shapepleineMur);

    } else {

      BuildingTexture.logger
          .warn(Messages.getString("Representation.RepNulle"));
    }

    if (geometryInfoToits != null) {
      Shape3D shapepleineToit = new Shape3D(
          geometryInfoToits.getGeometryArray(), apparenceToit);

      shapepleineToit.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shapepleineToit.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shapepleineToit.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shapepleineToit.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      shapepleineToit.setCapability(Node.ALLOW_LOCALE_READ);

      this.bGRep.addChild(shapepleineToit);

    } else {

      BuildingTexture.logger
          .warn(Messages.getString("Representation.RepNulle"));
    }

    // Optimisation
    this.bGRep.compile();

  }

  /**
   * Génère une apparence et gère le style en fonction du fait que c'est un mur
   * ou pas
   * 
   * @param isRoof indique si c'est un toit
   * @return
   */
  private Appearance generateAppearance(boolean isRoof) {

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
    // Le toit influe sur la texture
    if (isRoof) {

      apparenceFinale.setTexture(this.roofTexture);
    } else {

      apparenceFinale.setTexture(this.wallTexture);
    }

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

  @Override
  public Component getRepresentationComponent() {
    JPanel pan = new JPanel();
    pan.setLayout(new GridLayout(1, 2));

    pan.add(TextureManager.componentFromTexture(this.getWallTexture()));
    pan.add(TextureManager.componentFromTexture(this.getRoofTexture()));

    return pan;
  }

  /**
   * 
   * @return la texture affectée au mur
   */
  public Texture2D getWallTexture() {
    return this.wallTexture;
  }

  /**
   * 
   * @return la texture affectée au toit
   */
  public Texture2D getRoofTexture() {
    return this.roofTexture;
  }

  /**
   * 
   * @return la longueur en m (monde réel) de la texture affectée aux toits
   */
  public double getTextureRoofLength() {
    return this.textureRoofLength;
  }

  /**
   * 
   * @return la hauteur en m (monde réel) de la texture affectée aux toits
   */
  public double getTextureRoofHeigth() {
    return this.textureRoofHeigth;
  }

  /**
   * 
   * @return la longueur en m (monde réel) de la texture affectée aux murs
   */
  public double getTextureWallLength() {
    return this.textureWallLength;
  }

  /**
   * 
   * @return la hauteur en m (monde réel) de la texture affectée aux murs
   */
  public double getTextureWallHeigth() {
    return this.textureWallHeigth;
  }

}
