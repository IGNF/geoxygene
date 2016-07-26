package fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_ParameterizedTexture;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_TexCoordList;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_TextureCoordinates;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_X3DMaterial;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.GML_Polygon;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.GML_Ring;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class CG_StyleGenerator {

  public static List<CG_X3DMaterial> lMaterial = new ArrayList<CG_X3DMaterial>();
  public static List<Appearance> lAppearance = new ArrayList<Appearance>();

  Appearance apparenceFinale = new Appearance();
  GeometryInfo geometryInfo = null;
  Shape3D shape = null;

  private boolean isAppearanceSet = false;

  public CG_StyleGenerator(GML_Polygon poly, List<Object> lStyles) {

    int nbStylesToApply = lStyles.size();

    for (int i = 0; i < nbStylesToApply; i++) {

      Object o = lStyles.get(i);

      if (o instanceof CG_X3DMaterial) {

        CG_X3DMaterial mat = (CG_X3DMaterial) o;

        int index = CG_StyleGenerator.lMaterial.indexOf(mat);

        if (index != -1) {
          this.apparenceFinale = CG_StyleGenerator.lAppearance.get(index);
          continue;
        }

        this.generateMaterial((CG_X3DMaterial) o);

      } else if (o instanceof CG_ParameterizedTexture) {

        if (this.geometryInfo != null) {

          System.out.println("2 texturation sur la même face ?");
        }

        this.generateTexturedPolygon(poly, (CG_ParameterizedTexture) o);
      }

    }

    if (!this.isAppearanceSet) {

      this.generateDefaultApp();

    }

    if (this.geometryInfo == null) {

      this.generateEmptyGeomInfo(poly);

    }

    this.shape = new Shape3D(this.geometryInfo.getGeometryArray(),
        this.apparenceFinale);

  }

  private void generateTexturedPolygon(GML_Polygon poly,
      CG_ParameterizedTexture cgParamTexture) {
    // géométrie de l'objet
    this.geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

    // On compte le nombres de points
    int npoints = poly.coord().size();

    // On compte le nombre de polygones(trous inclus)
    int nStrip = poly.getInterior().size() + 1;

    // Nombre de points
    Point3f[] tabpoints = new Point3f[npoints];
    Vector3f[] normals = new Vector3f[npoints];
    TexCoord2f[] texCoord = new TexCoord2f[npoints];

    int[] strip = new int[nStrip];
    int[] contours = new int[1];

    // compteurs pour remplir le tableau de points
    int elementajoute = 0;

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

    List<GML_Ring> lRing = new ArrayList<GML_Ring>();
    lRing.add((GML_Ring) poly.getExterior());

    for (int i = 0; i < nbContributions - 1; i++) {
      lRing.add((GML_Ring) poly.getInterior().get(i));

    }

    CG_TexCoordList cgTCL = CG_StyleGenerator.retrieveTextureTexCoordList(
        poly.getID(), cgParamTexture);

    for (int k = 0; k < nbContributions; k++) {

      GML_Ring rActu = lRing.get(k);
      lPoints = rActu.coord();

      CG_TextureCoordinates cTexture = CG_StyleGenerator
          .retrieveTextureCoordinate(rActu.getID(), cgTCL);

      // Nombres de points de la contribution
      int n = lPoints.size();

      Vecteur axe = MathConstant.vectZ;
      Vecteur vectProject = axe.prodVectoriel(vect);

      if (n != (cTexture.getValue().size() / 2)) {

        System.out.println("Nombre points :" + n + "  points textures : "
            + cTexture.getValue().size() / 2);

      }

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

        texCoord[elementajoute] = new TexCoord2f(cTexture.getValue().get(2 * j)
            .floatValue(), cTexture.getValue().get(2 * j + 1).floatValue());

        normals[elementajoute] = new Vector3f((float) vect.getX(),
            (float) vect.getY(), (float) vect.getZ());

        elementajoute++;

      }

      // On indique le nombre de points relatif à la
      // contribution
      strip[k] = lPoints.size();

    }

    // Pour avoir des corps séparés, sinon il peut y avoir des trous
    contours[0] = nbContributions;

    // On indique quels sont les points combien il y a de contours et de
    // polygons

    this.geometryInfo.setTextureCoordinateParams(1, 2);

    this.geometryInfo.setCoordinates(tabpoints);
    this.geometryInfo.setStripCounts(strip);
    this.geometryInfo.setContourCounts(contours);
    this.geometryInfo.setNormals(normals);

    this.geometryInfo.setTextureCoordinates(0, texCoord);

    String path = Context.CITY_GML_CONTEXT + cgParamTexture.getImageURI();

    File f = new File(path);

    if (f.exists()) {
      this.apparenceFinale.setTexture(TextureManager.textureLoading(path));
    } else {
      System.out.println("Texture introuvable : " + path);
    }

  }

  private static CG_TexCoordList retrieveTextureTexCoordList(String IDPoly,
      CG_ParameterizedTexture pT) {

    int nbElem = pT.getTarget().size();

    for (int i = 0; i < nbElem; i++) {

      if (pT.getTextureAssociation().get(i).equals("#" + IDPoly)) {

        return (CG_TexCoordList) (pT.getTarget().get(i));
      }

    }

    return null;
  }

  private static CG_TextureCoordinates retrieveTextureCoordinate(String ID,
      CG_TexCoordList cgTCL) {
    int nbCoordinates = cgTCL.getTextureCoordinates().size();

    for (int i = 0; i < nbCoordinates; i++) {
      CG_TextureCoordinates cg = cgTCL.getTextureCoordinates().get(i);

      if (cg.getRing().equals("#" + ID) || cg.getRing().equals(ID)) {
        return cg;
      }
    }
    System.out.println("Not found" + ID);
    return null;
  }

  /**
   * Missing setSmoothness et ambientIntensity
   * 
   * @param X3Dmaterial
   * @return
   */
  private void generateMaterial(CG_X3DMaterial X3Dmaterial) {

    // Autorisations pour l'apparence
    this.apparenceFinale
        .setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    this.apparenceFinale
        .setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    this.apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    this.apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    this.apparenceFinale
        .setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
    this.apparenceFinale
        .setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);

    this.apparenceFinale.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    // Indique que l'on est en mode surfacique
    pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    this.apparenceFinale.setPolygonAttributes(pa);

    Material material = new Material();
    material.setShininess(X3Dmaterial.getShininess().floatValue());
    material.setSpecularColor(X3Dmaterial.getSpecularColor());
    material.setDiffuseColor(X3Dmaterial.getDiffuseColor());
    material.setEmissiveColor(X3Dmaterial.getEmissiveColor());
    this.apparenceFinale.setMaterial(material);

    if (X3Dmaterial.getTransparency() != 0) {

      TransparencyAttributes t_attr = new TransparencyAttributes(
          TransparencyAttributes.BLENDED, X3Dmaterial.getTransparency()
              .floatValue(), TransparencyAttributes.BLEND_SRC_ALPHA,
          TransparencyAttributes.BLENDED);
      this.apparenceFinale.setTransparencyAttributes(t_attr);
    }

    CG_StyleGenerator.lMaterial.add(X3Dmaterial);
    CG_StyleGenerator.lAppearance.add(this.apparenceFinale);

  }

  private void generateDefaultApp() {
    this.generateMaterial(new CG_X3DMaterial());
  }

  private GeometryInfo generateEmptyGeomInfo(GM_Polygon poly) {

    // géométrie de l'objet
    this.geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

    // Nombre de facettes
    int nbFacet = 1;

    // On compte le nombres de points
    int npoints = poly.coord().size();

    // On compte le nombre de polygones(trous inclus)
    int nStrip = poly.getInterior().size() + 1;

    // Nombre de points
    Point3d[] Tabpoints = new Point3d[npoints];

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

        lPoints = poly.getExterior().getPositive().coord();
      } else {

        // Contribution de type trou
        lPoints = poly.getInterior(k - 1).coord(); // Get negative ???

      }

      // DirectPosition dpMin = lPoints.pointMin();

      // Nombres de points de la contribution
      int n = lPoints.size();

      // Vectuer normal par défaut
      Vecteur vect = new Vecteur(0.0, 0.0, 1.0);

      // On calcul l'équation du plan
      ApproximatedPlanEquation eq = new ApproximatedPlanEquation(poly);
      vect = eq.getNormale();

      for (int j = 0; j < n; j++) {

        // On complète le tableau de points
        IDirectPosition dp = lPoints.get(j);
        Point3d point = new Point3d((float) dp.getX(), (float) dp.getY(),
            (float) dp.getZ());

        Tabpoints[elementajoute] = point;
        normal[elementajoute] = new Vector3f((float) vect.getX(),
            (float) vect.getY(), (float) vect.getZ());

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
    contours[0] = nbContributions;

    // On indique quels sont les points combien il y a de contours et de
    // polygons

    // geometryInfo.setTextureCoordinateParams(1, 2);

    this.geometryInfo.setCoordinates(Tabpoints);
    this.geometryInfo.setStripCounts(strip);
    this.geometryInfo.setContourCounts(contours);

    this.geometryInfo.setNormals(normal);
    this.geometryInfo.recomputeIndices();

    return this.geometryInfo;

  }

  public Shape3D getShape() {
    return this.shape;
  }

}
