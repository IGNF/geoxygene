package fr.ign.cogit.geoxygene.sig3d.representation.basic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.convert.java3d.ConversionJava3DGeOxygene;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
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
 * Classe permettant de représenter des solides en 3D. Basic class for rendering
 * 3D objects.
 */
public class Object3d extends BasicRep3D {
  /**
   * le logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(Object3d.class
      .getName());

  /**
   * créer la représentation d'un objet avec une géométrie de type solide. Les
   * couleurs des faces de l'objets seront aléatoires. créer le lien entre la
   * représentation et l'objet indispensable pour pouvoir effectuer des
   * sélections
   * 
   * @param feat l'entité dont la géométrie servira à créer la représentation et
   *          à laquelle sera attachée la représentation
   */
  public Object3d(IFeature feat) {
    this(feat, false, new Color(0f, 0f, 0f), 1, true);
  }

  /**
   * créer la représentation d'un objet avec une géométrie de type solide. Les
   * faces airont la couleur choisie. créer le lien entre la représentation et
   * l'objet indispensable pour pouvoir effectuer des sélections
   * 
   * @param feat l'entité dont la géométrie servira à créer la représentation et
   *          à laquelle sera attachée la représentation
   * @param color la couleur que l'on applique
   */
  public Object3d(IFeature feat, Color color) {
    this(feat, true, color, 1, true);
  }

  /**
   * créer la représentation d'un objet avec une géométrie de type solide. créer
   * le lien entre la représentation et l'objet indispensable pour pouvoir
   * effectuer des sélections
   * 
   * @param feat l'entité dont la géométrie servira à créer la représentation et
   *          à laquelle sera attachée la représentation
   * @param isClrd indique si une couleur unique sera appliquée ou non (si false
   *          une couleur différente par face)
   * @param color couleur appliquée si isClrd == true
   * @param coefOpacity coefficient d'opacité appliqué à l'objet
   * @param isSolid propose un mode de représentation filaire (false) ou
   *          surfacique (true)
   */
  public Object3d(IFeature feat, boolean isClrd, Color color,
      double coefOpacity, boolean isSolid) {
    // On crée le bg avec toutes les autorisations nécessaires
    super(feat, isClrd, color, coefOpacity, isSolid);

    // préparation de la géométrie Java3D
    GeometryInfo geometryInfo = null;
    if (isClrd) {
      geometryInfo = this.geometryWithColor();
    } else {
      geometryInfo = this.geometryWithOutColor();
    }

    if (geometryInfo == null) {
      Object3d.LOGGER.warn(Messages.getString("Representation.RepNulle"));
      return;
    }
    // préparation de l'apparence
    Appearance apparence = this.generateAppearance(isClrd, color, coefOpacity,
        isSolid);

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
   * Génère l'objet Java3D si on a choisi d'appliquer une unique couleur
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  private GeometryInfo geometryWithColor() {
    // On récupère sous forme d'une liste de faces la géométrie
    IGeometry geom = this.feat.getGeom();
    ArrayList<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();

    if (geom instanceof GM_Solid) {

      GM_Solid corps = (GM_Solid) geom;
      lFacettes.addAll(corps.getFacesList());

    } else if (geom instanceof GM_MultiSolid<?>) {

      GM_MultiSolid<ISolid> multiCorps = (GM_MultiSolid<ISolid>) geom;
      List<ISolid> lOS = multiCorps.getList();
      int nbElements = lOS.size();

      for (int i = 0; i < nbElements; i++) {
        List<IOrientableSurface> lSurf = (lOS.get(i)).getFacesList();
        lFacettes.addAll(lSurf);
      }

    } else if (geom instanceof GM_CompositeSolid) {

      GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;
      List<ISolid> lOS = compSolid.getGenerator();

      int nbElements = lOS.size();
      for (int i = 0; i < nbElements; i++) {
        ISolid s = lOS.get(i);
        lFacettes.addAll(s.getFacesList());
      }

    } else {
      Object3d.LOGGER.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }

    return ConversionJava3DGeOxygene.fromOrientableSToTriangleArray(lFacettes);

  }

  /**
   * Génèration de l'objet Java 3D dans le cadre d'un choix de couleur aléatoire
   * pour chaque face
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  private GeometryInfo geometryWithOutColor() {
    // On récupère la géométrie sous forme d'une liste de faces
    IGeometry geom = this.feat.getGeom();
    List<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();
    if (geom instanceof GM_Solid) {
      GM_Solid corps = (GM_Solid) geom;
      lFacettes = corps.getFacesList();

    } else if (geom instanceof GM_MultiSolid<?>) {

      GM_MultiSolid<ISolid> multiCorps = (GM_MultiSolid<ISolid>) geom;
      List<ISolid> lOS = multiCorps.getList();

      int nbElements = lOS.size();

      for (int i = 0; i < nbElements; i++) {

        lFacettes.addAll((lOS.get(i)).getFacesList());

      }

    } else if (geom instanceof GM_CompositeSolid) {

      GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;
      List<ISolid> lOS = compSolid.getGenerator();

      int nbElements = lOS.size();

      for (int i = 0; i < nbElements; i++) {
        ISolid s = lOS.get(i);
        lFacettes.addAll(s.getFacesList());
      }

    } else {
      Object3d.LOGGER.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }

    // géométrie de l'objet
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

    // Nombre de facettes
    int nbFacet = lFacettes.size();

    // On compte le nombres de points
    int npoints = 0;

    // On compte le nombre de polygones(trous inclus)
    int nStrip = 0;

    // Initialisation des tailles de tableaux
    for (int i = 0; i < nbFacet; i++) {
      IOrientableSurface os = lFacettes.get(i);

      npoints = npoints + os.coord().size() - ((GM_Polygon) os).getInterior().size() -1;
      nStrip = nStrip + 1 + ((GM_Polygon) os).getInterior().size();
    }

    // Nombre de points
    Point3f[] Tabpoints = new Point3f[npoints];
    Color4f[] couleurs = new Color4f[npoints];
    Vector3f[] normal = new Vector3f[npoints];
    // TexCoord2f[] TexCoord = new TexCoord2f[npoints];

    // Peut servir à detecter les trous
    int[] strip = new int[nStrip];
    int[] contours = new int[nbFacet];

    // compteurs pour remplir le tableau de points
    int elementajoute = 0;

    // Compteur pour remplir les polygones (trous inclus)
    int nbStrip = 0;

    // Pour chaque face
    for (int i = 0; i < nbFacet; i++) {

      GM_Polygon poly = (GM_Polygon) lFacettes.get(i);

      // Nombre de ring composant le polygone
      int nbContributions = 1 + poly.getInterior().size();

      // Liste de points utilisés pour définir les faces
      IDirectPositionList lPoints = null;

      // Pour chaque contribution (extérieurs puis intérieursr
      // Pour Java3D la première contribution en strip est le contour
      // Les autres sont des trous

      for (int k = 0; k < nbContributions; k++) {

        // Nombre de points de la contribution
        int nbPointsFace = 0;

        // Première contribution = extérieur

        if (k == 0) {

          lPoints = ((GM_Curve) poly.getExterior().getPositive()).coord();
        } else {

          // Contribution de type trou
          lPoints = ((GM_Curve) poly.getInterior(k - 1).getPositive()).coord();

        }

        // DirectPosition dpMin = lPoints.pointMin();

        // Nombres de points de la contribution
        int n = lPoints.size()-1;

        // Vectuer normal par défaut
        Vecteur vect = new Vecteur(0.0, 0.0, 1.0);

        if (n > 3) {
          // On calcul l'équation du plan
          PlanEquation eq = new PlanEquation(poly);
          vect = eq.getNormale();
        }

        // On récupère la normal ayant un z positif
        Vector3f vecteurActuel;
        if (vect.getZ() >= 0) {
          vecteurActuel = new Vector3f((float) vect.getX(),
              (float) vect.getY(), (float) vect.getZ());
        } else {
          vecteurActuel = new Vector3f((float) -vect.getX(),
              (float) -vect.getY(), (float) -vect.getZ());

        }

        vecteurActuel.normalize();

        vecteurActuel = new Vector3f(0, 0, 1);

        Color4f coultemp = new Color4f((float) Math.random(),
            (float) Math.random(), (float) Math.random(), 1.0f);

        for (int j = 0; j < n; j++) {

          // On complète le tableau de points
          IDirectPosition dp = lPoints.get(j);
          Point3f point = new Point3f((float) dp.getX(), (float) dp.getY(),
              (float) dp.getZ());

          Tabpoints[elementajoute] = point;
          normal[elementajoute] = new Vector3f(vecteurActuel);
          couleurs[elementajoute] = coultemp;

          // Un point en plus dans la liste de tous les points
          elementajoute++;

          // Un point en plus pour la contribution en cours
          nbPointsFace++;
        }

        // On indique le nombre de points relatif à la
        // contribution
        strip[nbStrip] = nbPointsFace;
        nbStrip++;
      }

      // Pour avoir des corps séparés, sinon il peut y avoir des trous
      contours[i] = nbContributions;

    }

    // On indique quels sont les points combien il y a de contours et de
    // polygons
    // geometryInfo.setTextureCoordinateParams(1, 2);
    geometryInfo.setCoordinates(Tabpoints);
    geometryInfo.setStripCounts(strip);
    geometryInfo.setContourCounts(contours);
    geometryInfo.setColors(couleurs);
    geometryInfo.setNormals(normal);
    geometryInfo.recomputeIndices();

    return geometryInfo;

  }

  /**
   * Génère l'apparence à appliquer à la géométrie
   * 
   * @param isClrd
   * @param color
   * @param coefTransp
   * @param isSolid
   * @return
   */
  private Appearance generateAppearance(boolean isClrd, Color color,
      double coefTransp, boolean isSolid) {

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

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    if (isClrd) {
      Color3f couleur3F = new Color3f(color);
      // Création du material (gestion des couleurs et de l'affichage)
      Material material = new Material();

      material.setDiffuseColor(couleur3F);
      material.setSpecularColor(new Color3f(color.brighter()));
      material.setAmbientColor(new Color3f(color.darker()));
      material.setShininess(128);
      
      apparenceFinale.setMaterial(material);

    }

    if (coefTransp != 1) {

      TransparencyAttributes t_attr = new TransparencyAttributes(
          TransparencyAttributes.BLENDED, (float) coefTransp,
          TransparencyAttributes.BLEND_SRC_ALPHA,
          TransparencyAttributes.BLENDED);
      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    return apparenceFinale;

  }

  /**
   * Change l'attirbut Couleur de manière définitive ainsi que la couleur de
   * l'objet
   * 
   * @param color
   */
  public void setColor(Color color) {
    this.color = color;
    this.getShapes()
        .get(0)
        .setAppearance(
            this.generateAppearance(true, color, this.opacity, this.isSolid));

  }

  /**
   * Affichage ou non des faces dont la normale ne fait pas face à la caméra
   * 
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

  @Override
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;

    List<Shape3D> shapesTemp = this.getShapes();
    int nbElem = shapesTemp.size();
    if (isSelected) {
      // Si l'objet est sélectionné on applique
      // la couleur de sélection
      for (int i = 0; i < nbElem; i++) {
        shapesTemp.get(i).setAppearance(
            this.generateAppearance(true,
                ConstantRepresentation.selectionColor, this.opacity,
                this.isSolid));
      }
    } else {
      // Sinon on applique la couleur initiale
      for (int i = 0; i < nbElem; i++) {
        shapesTemp.get(i).setAppearance(
            this.generateAppearance(this.isColored, this.color, this.opacity,
                this.isSolid));
      }
    }
  }

}
