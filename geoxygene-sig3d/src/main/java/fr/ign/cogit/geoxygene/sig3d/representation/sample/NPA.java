package fr.ign.cogit.geoxygene.sig3d.representation.sample;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.conversion.ConversionJava3DGeOxygene;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
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
 * Représentation non photoréaliste en appliquant une texture aux bordures des
 * bâtiments NPA representation
 * 
 */
public class NPA extends Default3DRep {

  private Texture2D texture;

  /**
   * Constructeur permettant de générer une représentation non photoréaliste à
   * un objet de type surfacique ou volumique. Une couleur est définie pour
   * l'objet ainsi
   * 
   * @param feat l'entité génératrice
   * @param color la couleur
   * @param texture la texture que l'on applique aux bordures
   * @param textureWidth la longueur en m de la texture sur le terrain
   * @param textureHeigth la demie hauteur de la texture sur le terrain
   */
  public NPA(IFeature feat, Color color, Texture2D texture,
      double textureWidth, double textureHeigth) {
    this(feat, color, true, 1, true, texture, textureWidth, textureHeigth);
  }

  /**
   * Constructeur permettant de générer une représentation non photoréaliste à
   * un objet de type surfacique ou volumique. Une couleur est définie pour
   * l'objet ainsi
   * 
   * @param feat l'entité génératrice
   * @param color la couleur
   * @param isColored indique si l'on appliquer une couleur aléatoire par face
   * @param coeffOpacity coefficient d'opacité
   * @param isSolid indique si l'on applique une représentation solide ou
   *          filaire
   * @param texture la texture que l'on applique
   * @param textureWidth la largeur de la texture en coordonnées terrain
   * @param textureHeigth la demie hauteur de la texture en coordonnées terrain
   */
  public NPA(IFeature feat, Color color, boolean isColored,
      double coeffOpacity, boolean isSolid, Texture2D texture,
      double textureWidth, double textureHeigth) {
    super();
    this.feat = feat;
    this.texture = texture;

    IGeometry geom = feat.getGeom();

    List<IOrientableSurface> lOS = NPA.generateLOSurface(geom);

    if (lOS == null || lOS.size() == 0) {

      this.bGRep = new BranchGroup();

      return;
    }

    List<IOrientableCurve> lCurve = NPA.recupLine(lOS);

    // On créer la représentation du solide
    BranchGroup bg = this.generateRepresentationSolid(lOS, isColored, color,
        coeffOpacity, isSolid);
    this.bGRep.addChild(bg);

    // On enlève les curves en double
    List<IOrientableCurve> lCurvePrep = NPA.prepareCurve(lCurve);

    // On génère l'apparence du contour
    bg = this.generateAppearanceLine(lCurvePrep, texture, textureWidth,
        textureHeigth);
    this.bGRep.addChild(bg);

    this.bGRep.compile();
  }

  /**
   * Récupère les surfaces d'une géométrie
   * 
   * @param geom
   * @return
   */
  private static List<IOrientableSurface> generateLOSurface(IGeometry geom) {

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    if (geom instanceof ISolid) {
      lOS.addAll(((GM_Solid) geom).getFacesList());
    } else if (geom instanceof IOrientableSurface) {
      lOS.add((GM_OrientableSurface) geom);
    } else if (geom instanceof IMultiSurface<?>) {
      lOS.addAll((GM_MultiSurface<?>) geom);

    } else if (geom instanceof IMultiSolid<?>) {

      GM_MultiSolid<?> multiCorps = (GM_MultiSolid<?>) geom;
      List<? extends ISolid> lCorps = multiCorps.getList();

      int nbElements = lCorps.size();

      for (int i = 0; i < nbElements; i++) {
        List<IOrientableSurface> lSurf = (lCorps.get(i)).getFacesList();
        lOS.addAll(lSurf);
      }

    } else if (geom instanceof GM_CompositeSolid) {

      GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;
      List<? extends ISolid> lCorps = compSolid.getGenerator();

      int nbElements = lCorps.size();

      for (int i = 0; i < nbElements; i++) {
        ISolid s = lCorps.get(i);
        lOS.addAll(s.getFacesList());
      }

    } else {

      return null;
    }
    return lOS;
  }

  /**
   * Récupère les lignes d'une liste de faces
   * 
   * @param lOS
   * @return
   */
  private static List<IOrientableCurve> recupLine(List<IOrientableSurface> lOS) {

    int nbSurf = lOS.size();
    // On récupère la liste des lignes
    List<IOrientableCurve> lCurve = new ArrayList<IOrientableCurve>();

    for (int i = 0; i < nbSurf; i++) {
      IRing r1 = ((GM_Polygon) lOS.get(i)).getExterior();

      lCurve.add(r1.getPrimitive());

      int nbInt = ((GM_Polygon) lOS.get(i)).getInterior().size();

      for (int j = 0; j < nbInt; j++) {
        lCurve.addAll(((GM_Polygon) lOS.get(i)).getInterior().get(j)
            .getGenerator());

      }

    }

    return lCurve;
  }

  /**
   * Génère la représentation du solide
   * 
   * @param lOS
   * @param isColored
   * @param color
   * @param coeffOpacity
   * @param isSolid
   * @return
   */
  private BranchGroup generateRepresentationSolid(List<IOrientableSurface> lOS,
      boolean isColored, Color color, double coeffOpacity, boolean isSolid) {

    // On génère le solide
    Shape3D s = new Shape3D(ConversionJava3DGeOxygene
        .fromOrientableSToTriangleArray(lOS).getGeometryArray(),
        this.generateAppearance(isColored, color, coeffOpacity, isSolid));
    BranchGroup bg = new BranchGroup();
    bg.addChild(s);

    return bg;
  }

  /**
   * Génère l'apparence à appliquer à la géométrie
   * 
   * @param isClrd
   * @param color
   * @param coefOpacity
   * @param isSolid
   * @return
   */
  private Appearance generateAppearance(boolean isClrd, Color color,
      double coefOpacity, boolean isSolid) {

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
      material.setAmbientColor(couleur3F);
      material.setDiffuseColor(couleur3F);
      material.setSpecularColor(couleur3F);
      material.setEmissiveColor(couleur3F);
      material.setShininess(1);
      apparenceFinale.setMaterial(material);

    }

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr = new TransparencyAttributes(
          TransparencyAttributes.BLENDED, (float) coefOpacity,
          TransparencyAttributes.BLEND_SRC_ALPHA,
          TransparencyAttributes.BLENDED);
      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    return apparenceFinale;

  }

  /**
   * Découpe les curves en une liste de segments uniques
   * 
   * @param lCurve
   * @return
   */
  private static List<IOrientableCurve> prepareCurve(
      List<IOrientableCurve> lCurve) {

    int nbCurve = lCurve.size();

    List<IOrientableCurve> lCurveTemp = new ArrayList<IOrientableCurve>();

    for (int i = 0; i < nbCurve; i++) {

      IOrientableCurve cTemp = lCurve.get(i);

      IDirectPositionList dpl = cTemp.coord();
      int nPoints = dpl.size();

      for (int j = 0; j < nPoints; j++) {

        IDirectPosition dp1 = (j == 0) ? dpl.get(nPoints - 1) : dpl.get(j - 1);
        IDirectPosition dp2 = dpl.get(j);

        if (!NPA.isSin(dp1, dp2, lCurveTemp)) {
          DirectPositionList dplTemp = new DirectPositionList();

          if (dp1.distance(dp2) < 0.5) {
            continue;
          }

          dplTemp.add(dp1);
          dplTemp.add(dp2);
          GM_OrientableCurve cTempAdd = new GM_LineString(dplTemp);
          lCurveTemp.add(cTempAdd);
        }
      }

    }

    return lCurveTemp;

  }

  /**
   * Regarde si un segment formé par 2 points est dans la liste de segments
   * 
   * @param dp1
   * @param dp2
   * @param lCurve
   * @return
   */
  private static boolean isSin(IDirectPosition dp1, IDirectPosition dp2,
      List<IOrientableCurve> lCurve) {
    int nbCurve = lCurve.size();

    for (int i = 0; i < nbCurve; i++) {
      IOrientableCurve cTemp = lCurve.get(i);

      IDirectPosition dp1Temp = cTemp.coord().get(0);
      IDirectPosition dp2Temp = cTemp.coord().get(1);

      if (dp1.equals(dp1Temp, 0.5) && dp2.equals(dp2Temp, 0.5)
          || dp2.equals(dp1Temp, 0.5) && dp1.equals(dp2Temp, 0.5)) {

        return true;

      }
    }

    return false;
  }

  /**
   * Génère la représentation de type linéaire en construisant des rectangles
   * ayant une texture
   * 
   * @param lCurve
   * @param texture
   * @param textureWidth
   * @param textureHigh
   * @return
   */
  private BranchGroup generateAppearanceLine(List<IOrientableCurve> lCurve,
      Texture2D texture, double textureWidth, double textureHigh) {
    // Le BranchGroup sur lequel seront attachés tous les objets
    BranchGroup bg = new BranchGroup();

    int nbCurves = lCurve.size();

    for (int i = 0; i < nbCurves; i++) {

      // géométrie de l'objet
      GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

      // On compte le nombres de points
      int npoints = 4;

      // On compte le nombre de polygones(trous inclus)
      int nStrip = 1;

      // Nombre de points
      Point3f[] Tabpoints = new Point3f[npoints];
      Vector3f[] normals = new Vector3f[npoints];
      TexCoord2f[] TexCoord = new TexCoord2f[npoints];

      // Peut servir à detecter les trous
      int[] strip = new int[nStrip];
      int[] contours = new int[1];

      IOrientableCurve c = lCurve.get(i);

      IDirectPosition dp1 = c.coord().get(0);
      IDirectPosition dp2 = c.coord().get(1);
      // Le vecteur suivant lequel la texture sera appliquée
      Vecteur vect = new Vecteur(dp1, dp2);

      // L'origine du repère locale
      double x0 = dp1.getX();
      double y0 = dp1.getY();
      double z0 = dp1.getZ();

      // On génère le vecteur directeur comme étant le produit vectoriel
      // du vecteur d'application de la texture
      Vecteur vectDir = MathConstant.vectX;

      if (Math.abs(vectDir.prodVectoriel(vect).norme() / vect.norme()) < 0.5) {

        vectDir = MathConstant.vectY;
        if (Math.abs(vectDir.prodVectoriel(vect).norme() / vect.norme()) < 0.5) {
          vectDir = MathConstant.vectZ;
        }
      }
      //
      vectDir = vect.prodVectoriel(vectDir);
      vectDir.normalise();
      // On oriente vers le haut le vecteur de Direction
      vectDir.multConstante(Math.signum(vectDir.getZ()));

      // On génère le rectangle d'un largeur textureHigh
      DirectPosition dpRec1 = new DirectPosition(dp1.getX() - textureHigh
          * vectDir.getX(), dp1.getY() - textureHigh * vectDir.getY(),
          dp1.getZ() - textureHigh * vectDir.getZ());
      DirectPosition dpRec2 = new DirectPosition(dp1.getX() + textureHigh
          * vectDir.getX(), dp1.getY() + textureHigh * vectDir.getY(),
          dp1.getZ() + textureHigh * vectDir.getZ());
      DirectPosition dpRec3 = new DirectPosition(dp2.getX() + textureHigh
          * vectDir.getX(), dp2.getY() + textureHigh * vectDir.getY(),
          dp2.getZ() + textureHigh * vectDir.getZ());
      DirectPosition dpRec4 = new DirectPosition(dp2.getX() - textureHigh
          * vectDir.getX(), dp2.getY() - textureHigh * vectDir.getY(),
          dp2.getZ() - textureHigh * vectDir.getZ());
      // On génère dans le repère local les points

      Point3f pointRec1 = new Point3f((float) (dpRec1.getX() - x0),
          (float) (dpRec1.getY() - y0), (float) (dpRec1.getZ() - z0));
      Point3f pointRec2 = new Point3f((float) (dpRec2.getX() - x0),
          (float) (dpRec2.getY() - y0), (float) (dpRec2.getZ() - z0));
      Point3f pointRec3 = new Point3f((float) (dpRec3.getX() - x0),
          (float) (dpRec3.getY() - y0), (float) (dpRec3.getZ() - z0));
      Point3f pointRec4 = new Point3f((float) (dpRec4.getX() - x0),
          (float) (dpRec4.getY() - y0), (float) (dpRec4.getZ() - z0));

      Tabpoints[0] = pointRec1;
      Tabpoints[1] = pointRec2;
      Tabpoints[2] = pointRec3;
      Tabpoints[3] = pointRec4;

      double offset = 0; // Math.random();

      // On calcule les coordonnées de texture, la longueur dépendant de
      // textureWidt
      TexCoord[0] = new TexCoord2f((float) offset, 0);
      TexCoord[1] = new TexCoord2f((float) offset, 1);
      TexCoord[2] = new TexCoord2f((float) (offset + vect.norme()
          / textureWidth), (float) 1.0);
      TexCoord[3] = new TexCoord2f((float) (offset + vect.norme()
          / textureWidth), 0);

      // Vector3f dir = new Vector3f((float) vect.getX(), (float) vect
      // .getY(), (float) vect.getZ());

      Vecteur normale = vect.prodVectoriel(vectDir);
      normale.normalise();
      Vector3f dir = new Vector3f((float) normale.getX(),
          (float) normale.getY(), (float) normale.getZ());

      normals[0] = dir;
      normals[1] = dir;
      normals[2] = dir;
      normals[3] = dir;

      // On indique le nombre de points relatif à la
      // contribution
      strip[0] = 4;

      // Pour avoir des corps séparés, sinon il peut y avoir des trous
      contours[0] = 1;

      // On indique quels sont les points combien il y a de contours et de
      // polygons

      geometryInfo.setTextureCoordinateParams(1, 2);

      geometryInfo.setCoordinates(Tabpoints);
      geometryInfo.setStripCounts(strip);
      geometryInfo.setContourCounts(contours);
      geometryInfo.setNormals(normals);

      geometryInfo.setTextureCoordinates(0, TexCoord);

      // préparation de l'apparence
      Appearance apparence = this.generateAppearance(texture);
      // Calcul de l'objet Shape3D
      Shape3D shapepleine = new Shape3D(geometryInfo.getGeometryArray(),
          apparence);

      // Autorisations sur la Shape3D
      shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shapepleine.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shapepleine.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      shapepleine.setCapability(Node.ALLOW_LOCALE_READ);

      // Create the transformgroup used for the billboard
      TransformGroup billBoardGroup = new TransformGroup();
      // Set the access rights to the group
      billBoardGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      // On place le centre de la sphère aux bonnes coordonnées
      Transform3D translate = new Transform3D();
      translate.set(new Vector3f((float) x0, (float) y0, (float) z0));

      AxisAngle4d rotateAxisAngle = new AxisAngle4d(vect.getX(), vect.getY(),
          vect.getZ(), Math.PI / 2.0);

      Transform3D rotX = new Transform3D();
      rotX.set(rotateAxisAngle);

      TransformGroup tgRotX = new TransformGroup(rotX);
      tgRotX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      tgRotX.addChild(shapepleine);

      // Add the cube to the group
      billBoardGroup.addChild(tgRotX);

      Billboard myBillboard = new Billboard(billBoardGroup,

      Billboard.ROTATE_ABOUT_AXIS, new Vector3f((float) vect.getX(),
          (float) vect.getY(), (float) vect.getZ()));

      myBillboard.setSchedulingBounds(new BoundingSphere(new Point3d(),
          Double.POSITIVE_INFINITY));

      TransformGroup transform = new TransformGroup(translate);
      transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      transform.addChild(billBoardGroup);
      transform.addChild(myBillboard);

      bg.addChild(transform);

    }
    return bg;
  }

  /**
   * Génère l'apparence de l'objet de type texture d'un objet
   * 
   * @return
   */
  private Appearance generateAppearance(Texture2D tex) {

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

    apparenceFinale.setTexture(tex);
    apparenceFinale.setTextureAttributes(new TextureAttributes());

    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    TransparencyAttributes t_attr = new TransparencyAttributes(
        TransparencyAttributes.NICEST, 0,
        TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA,
        TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
    apparenceFinale.setTransparencyAttributes(t_attr);

    return apparenceFinale;

  }

  @Override
  public Component getRepresentationComponent() {
    return TextureManager.componentFromTexture(this.texture);
  }

}
