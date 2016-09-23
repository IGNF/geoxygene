package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import org.citygml4j.model.citygml.appearance.AbstractSurfaceData;
import org.citygml4j.model.citygml.appearance.Appearance;
import org.citygml4j.model.citygml.appearance.AppearanceProperty;
import org.citygml4j.model.citygml.appearance.Color;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.citygml.appearance.SurfaceDataProperty;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureCoordinates;
import org.citygml4j.model.citygml.appearance.X3DMaterial;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

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
 * Classe permettant de générere une apparence type GeOxygene 3D à partir d'une
 * apparence de CityGML TODO : Attribut isSmooth et Target pour X3D non gérés.
 * Ambient intensity non usable en java Gérer les histoires de profils Class to
 * handle apperance of CityGML objects and to traduce them into GeOxygene types
 * 
 */
public class ConvertCityGMLAppearance extends Default3DRep {

  private boolean isRepresentationSet = true;

  public boolean isRepresentationSet() {
    return this.isRepresentationSet;
  }

  /**
   * Constructeur pour obtenir une géométrie à partir d'une liste d'apparence
   * CityGML et d'une liste d'identifiant On considère que le premier
   * identifiant s'applique à la première face de la géométrie de l'entité etc.
   * L'application fait le lien entre la géométrie et l'identifiant
   * 
   * @param feat l'entité qui se verra appliqué un style
   * @param lAppObjet liste d'apparence GeOyxgene
   * @param indRing list de Ring pouvant appartenir à l'objet
   */
  public ConvertCityGMLAppearance(IFeature feat,
      List<AppearanceProperty> lAppObjet, List<String> indRing) {

    super();

    GeometryInfo geomInfo = null;

    javax.media.j3d.Appearance appFinale = new javax.media.j3d.Appearance();

    // Récupération des différentes propriétés de représntations
    List<AppearanceProperty> lAppTemp = new ArrayList<AppearanceProperty>();

    if (lAppObjet != null) {

      lAppTemp.addAll(lAppObjet);
    }
    // On ajoute les propriétés globales au cas ou elles existent
    if (ParserCityGMLV2.LIST_APP_GEN != null) {

      lAppTemp.addAll(ParserCityGMLV2.LIST_APP_GEN);
    }

    this.feat = feat;
    IGeometry geom = feat.getGeom();
    // On récupère les facettes

    // On décompose en facettes (car on appliquera sur chaque face la
    // représentation ad hoc
    ArrayList<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();

    int nbAppP = lAppTemp.size();

    // Pas d'apparence, on met nulle par défaut
    if (nbAppP != 0) {

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

      } else if (geom instanceof IMultiSurface<?>) {

        lFacettes.addAll(((GM_MultiSurface<?>) geom).getList());

      } else if (geom instanceof IOrientableSurface) {
        lFacettes.add((GM_OrientableSurface) geom);

      } else {

        System.out.println("Type inconnue");

      }

      int nbFacettes = lFacettes.size();

      // On va créer pour chaque facette la géométrie associée
      for (int i = 0; i < nbFacettes; i++) {

        appFinale = new javax.media.j3d.Appearance();
        geomInfo = null;

        String indActu = indRing.get(i);
        // On ne texture d'une fois une facette

        boucleappearance: for (int j = 0; j < nbAppP; j++) {

          Appearance ap = lAppTemp.get(j).getAppearance();

          // On récupère les différentes propriétées de surfaces pour
          // chaque apparence
          List<SurfaceDataProperty> lsdTemp = ap.getSurfaceDataMember();

          int nbSurfaceDataProperty = lsdTemp.size();

          for (int k = 0; k < nbSurfaceDataProperty; k++) {

            AbstractSurfaceData sd = lsdTemp.get(k).getSurfaceData();
            // On regarde si c'est un objet de type Material
            if (sd instanceof X3DMaterial) {
              List<String> lTargets = ((X3DMaterial) sd).getTarget();
              int nbTarget = lTargets.size();
              for (int l = 0; l < nbTarget; l++) {
                if (lTargets.get(l).equals("#" + indActu)) {
                  ConvertCityGMLAppearance.generateAppearance((X3DMaterial) sd,
                      appFinale);
                  break boucleappearance;
                }
              }

              // Un objet de type Parametrized texture
            } else if (sd instanceof ParameterizedTexture) {

              ParameterizedTexture pt = (ParameterizedTexture) sd;
              // On regarde les associations pour détecter si il
              // existe celle correspondant à l'ID du polygone
              int nbAssociation = pt.getTarget().size();

              for (int l = 0; l < nbAssociation; l++) {

                if (pt.getTarget().get(l).getUri().equals("#" + indActu)) {
                  // C'est le cas on génère géométrie et
                  // représentations ad hoc
                  geomInfo = this.geometryWithParametrizedTexture(lFacettes
                      .get(i), ((TexCoordList) pt.getTarget().get(l)
                      .getTextureParameterization()).getTextureCoordinates()
                      .get(0));

                  this.addTextureInformation(pt, appFinale);

                  break boucleappearance;
                } else {

                  List<TextureCoordinates> tcl = ((TexCoordList) (pt
                      .getTarget().get(l).getTextureParameterization()))
                      .getTextureCoordinates();

                  int nbTCL = tcl.size();

                  for (int t = 0; t < nbTCL; t++) {
                    TextureCoordinates tclTemP = tcl.get(t);

                    if (tclTemP.getRing().equals("#" + indActu)) {
                      geomInfo = this.geometryWithParametrizedTexture(
                          lFacettes.get(i), tclTemP);

                      this.addTextureInformation(pt, appFinale);

                      break boucleappearance;
                    }

                  }
                }
              }

            }/*
              * else if (sd instanceof GeoreferencedTexture) { // Cas non traité
              * des Georeferenced texture GeoreferencedTexture gt =
              * (GeoreferencedTexture) sd; break boucleappearance; }
              */

          }

        }// fin de la boucle sur les apparences
        if (geomInfo == null) {

          this.isRepresentationSet = false;

        }

        if (geomInfo != null) {
          // On créer la géométrie
          // Calcul de l'objet Shape3D
          Shape3D shapepleine = new Shape3D(geomInfo.getGeometryArray(),
              appFinale);

          // Autorisations sur la Shape3D
          shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
          shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
          shapepleine.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
          shapepleine.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
          shapepleine.setCapability(Node.ALLOW_LOCALE_READ);

          // Ajout au BgClone les elements transformes
          this.bGRep.addChild(shapepleine);

        }

      }// fin de la boucle sur les facettes
       // Optimisation
      this.bGRep.compile();
    } else {

      this.isRepresentationSet = false;

    }

  }

  /**
   * Ajoute une information de texture
   * 
   * @param pt objet texture Java3D
   * @param apparenceFinale apparence qui se voit ajoutée les infos de texture
   */
  private void addTextureInformation(ParameterizedTexture pt,
      javax.media.j3d.Appearance apparenceFinale) {

    // Autorisations pour l'apparence
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material

    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);

    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_TEXTURE_WRITE);

    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    System.out.println(ParserCityGMLV2.PATH + pt.getImageURI());

    Texture2D t = TextureManager.textureLoading(ParserCityGMLV2.PATH
        + pt.getImageURI());

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

    pa.setBackFaceNormalFlip(true);
    apparenceFinale.setPolygonAttributes(pa);

    /*
     * if (pt.getWrapMode().equals(WrapModeType.NONE)) { } else if
     * (pt.getWrapMode().equals(WrapModeType.WRAP)) {
     * t.setBoundaryModeS(Texture.WRAP); t.setBoundaryModeT(Texture.WRAP); }
     * else if (pt.getWrapMode().equals(WrapModeType.MIRROR)) { } else if
     * (pt.getWrapMode().equals(WrapModeType.CLAMP)) {
     * t.setBoundaryModeS(Texture.CLAMP); t.setBoundaryModeT(Texture.CLAMP); }
     * else if (pt.getWrapMode().equals(WrapModeType.BORDER)) {
     * t.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
     * t.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY); } if (pt.getBorderColor()
     * != null) { t.setBoundaryColor(new Color4f(pt.getBorderColor().getRed()
     * .floatValue(), pt.getBorderColor().getBlue().floatValue(),
     * pt.getBorderColor().getGreen().floatValue(), 1f)); }
     */

    TextureAttributes ta = new TextureAttributes();

    apparenceFinale.setTextureAttributes(ta);
    apparenceFinale.setTexture(t);

  }

  /**
   * Génère une géométrie Java3D texturée à partir d'une géométrie de dimension
   * 2 ou + et d'un TextureCoordinates issus du parsing d'un fichier CityGML par
   * CityGML4j
   * 
   * @param geom géométrie que l'on souhaite texturer
   * @param tc les informations de texturées stockées dans CityGML
   * @return une géométrie Java3D avec les informatinos de textures renseignées
   */
  public GeometryInfo geometryWithParametrizedTexture(IGeometry geom,
      TextureCoordinates tc) {

    // On redivise en face au cas ou il y en ait plusieurs
    ArrayList<IOrientableSurface> lFacettes = new ArrayList<IOrientableSurface>();
    lFacettes.addAll(FromGeomToSurface.convertGeom(geom));
    /*
    if (geom instanceof ISolid) {
      ISolid corps = (ISolid) geom;
      lFacettes.addAll(corps.getFacesList());

    } else if (geom instanceof IMultiSolid<?>) {

      IMultiSolid<?> multiCorps = (IMultiSolid<?>) geom;

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

      System.out.println("Type inconnue");
      return null;
    }*/

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
      
      if(os instanceof GM_CompositeSurface || os.coord().isEmpty()){
          lFacettes.remove(i);
          i--;
          nbFacet--;
          
          continue;
      }

      npoints = npoints + os.coord().size();
      nStrip = nStrip + 1 + ((GM_Polygon) os).getInterior().size();
    }

    // Nombre de points
    Point3f[] tabpoints = new Point3f[npoints];
    Vector3f[] normals = new Vector3f[npoints];
    TexCoord2f[] texCoord = new TexCoord2f[npoints];

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

      ApproximatedPlanEquation eq = new ApproximatedPlanEquation(poly);
      Vecteur vect = eq.getNormale();

      // DirectPositionList lPoints = facet.coord();

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

        if (k == 0) {

          lPoints = poly.getExterior().coord();

        } else {

          // Contribution de type trou

          lPoints = poly.getInterior(k - 1).coord();

        }

        // Nombres de points de la contribution
        int n = lPoints.size();

        Vecteur axe = MathConstant.vectZ;
        Vecteur vectProject = axe.prodVectoriel(vect);

        for (int j = 0; j < n; j++) {
          // On complète le tableau de points
          IDirectPosition dp = lPoints.get(j);
          Point3f point = new Point3f((float) dp.getX(), (float) dp.getY(),
              (float) dp.getZ());

          tabpoints[elementajoute] = point;

          if (vectProject.norme() < 0.5) {

            vectProject = MathConstant.vectY.prodVectoriel(vect);
            axe = MathConstant.vectY;

          }
          try {
            texCoord[elementajoute] = new TexCoord2f(tc.getValue().get(2 * j)
                .floatValue(), tc.getValue().get(2 * j + 1).floatValue());

          } catch (Exception e) {
            e.printStackTrace();

          }

          normals[elementajoute] = new Vector3f((float) vect.getX(),
              (float) vect.getY(), (float) vect.getZ());
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

    geometryInfo.setTextureCoordinateParams(1, 2);

    geometryInfo.setCoordinates(tabpoints);
    geometryInfo.setStripCounts(strip);
    geometryInfo.setContourCounts(contours);
    geometryInfo.setNormals(normals);
    geometryInfo.setTextureCoordinates(0, texCoord);
    return geometryInfo;

  }

  /**
   * Permet de modifier l'apparence en paramètre pour coincider avec les
   * informations X3D
   * 
   * @param x3d
   * @param apparenceFinale
   */
  private static void generateAppearance(X3DMaterial x3d,
      javax.media.j3d.Appearance apparenceFinale) {

    // On récupère les paramètres relatifs à la représentation
    Double shininess = x3d.getShininess();
    Double transparency = x3d.getTransparency();
    // Double ambientIntensity = x3d.getAmbientIntensity();
    Color specularColor = x3d.getSpecularColor();
    Color diffuseColor = x3d.getDiffuseColor();
    Color emissiveColor = x3d.getEmissiveColor();
    // ??????????????????????????????????????
    // boolean isSmooth = x3d.getIsSmooth();

    // Autorisations pour l'apparence
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale
        .setCapability(javax.media.j3d.Appearance.ALLOW_MATERIAL_WRITE);

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

    pa.setBackFaceNormalFlip(true);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    Material material = new Material();

    if (specularColor != null) {

      material.setSpecularColor(new Color3f(
          specularColor.getRed().floatValue(), specularColor.getGreen()
              .floatValue(), specularColor.getBlue().floatValue()));
    }

    if (diffuseColor != null) {

      material.setDiffuseColor(new Color3f(diffuseColor.getRed().floatValue(),
          diffuseColor.getGreen().floatValue(), diffuseColor.getBlue()
              .floatValue()));
    }

    if (emissiveColor != null) {

      material.setEmissiveColor(new Color3f(
          emissiveColor.getRed().floatValue(), emissiveColor.getGreen()
              .floatValue(), emissiveColor.getBlue().floatValue()));
    }

    if (shininess != null) {

      int shininessJ3D = (int) (shininess * 255 + 1);
      material.setShininess(shininessJ3D);

    }

    apparenceFinale.setMaterial(material);

    if (transparency != null && transparency != 0) {

      TransparencyAttributes t_attr = new TransparencyAttributes(
          TransparencyAttributes.BLENDED, (float) transparency.doubleValue(),
          TransparencyAttributes.BLEND_SRC_ALPHA,
          TransparencyAttributes.BLENDED);
      apparenceFinale.setTransparencyAttributes(t_attr);
    }

  }

}
