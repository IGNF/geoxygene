package fr.ign.cogit.geoxygene.sig3d.representation.sample;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.convert.java3d.ConversionJava3DGeOxygene;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
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
 * Représentation en mode "cartoon" avec des bords noirs pour les différentes
 * faces d'un objet Cartoon representation by applying black border on the
 * different faces
 * 
 */
public class ObjectCartoon extends Default3DRep {

  // Couleur des faces
  private Color color;
  // Couleurs dse bords
  private Color edgeColor;
  // Largeur des bords
  private int widthEdge;
  // Opacité des bords
  private double coefOpacity;

  /**
   * Constructeur vide
   */
  public ObjectCartoon() {

  }

  /**
   * Constructeur à partir d'une entité. Utilise une bordure noir de largeur 3,
   * les faces sont opaques de couleur aléatoire
   * 
   * @param feat l'entité à laquelle on applique la représentation
   */
  public ObjectCartoon(IFeature feat) {
    this(feat, new Color((int) (Math.random() * 255),
        (int) (Math.random() * 255), (int) (Math.random() * 255)), Color.black,
        3, 1);

  }

  /**
   * Constructeur à partir d'une entité. Utilise une bordure noir de largeur 3,
   * les faces sont opaques de couleur définie
   * 
   * @param feat l'entité à laquelle on applique une représentation
   * @param color la couleur des faces
   */
  public ObjectCartoon(IFeature feat, Color color) {

    this(feat, color, Color.black, 3, 1);

  }

  /**
   * Constructeur à partir d'une entité. Utilise une bordure de couleur définie
   * de largeur 3, les faces sont opaques de couleur définie
   * 
   * @param feat l'entité à laquelle on applique une représentation
   * @param color la couleur des faces
   * @param edgeColor la couleur de la bordure
   */
  public ObjectCartoon(IFeature feat, Color color, Color edgeColor) {

    this(feat, color, edgeColor, 3, 1);

  }

  /**
   * Constructeur à partir d'une entité. Utilise une bordure de couleur définie
   * de largeur définie, les faces sont opaques de couleur définie
   * 
   * @param feat l'entité à laquelle on applique une représentation
   * @param color la couleur des faces
   * @param edgeColor la couleur de la bordure
   * @param largeurBordure la largeur de la bordure
   */
  public ObjectCartoon(IFeature feat, Color color, Color edgeColor,
      int largeurBordure) {

    this(feat, color, edgeColor, largeurBordure, 1);

  }

  /**
   * Constructeur à partir d'une entité. Utilise une bordure de couleur définie
   * de largeur définie, les faces sont d'une opacité définie de couleur définie
   * 
   * @param feat l'entité à laquelle on applique une représentation
   * @param color la couleur des faces
   * @param edgeColor la couleur de la bordure
   * @param edgeWiddth la largeur de la bordure
   * @param coefOpacity l'opacité des faces (1 = opaque)
   */
  @SuppressWarnings("unchecked")
  public ObjectCartoon(IFeature feat, Color color, Color edgeColor,
      int edgeWiddth, double coefOpacity) {
    super();
    this.feat = feat;
    this.color = color;
    this.edgeColor = edgeColor;
    this.widthEdge = edgeWiddth;
    this.coefOpacity = coefOpacity;

    IGeometry geom = feat.getGeom();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    if (geom instanceof GM_Solid) {
      lOS.addAll(((GM_Solid) geom).getFacesList());
    } else if (geom instanceof GM_OrientableSurface) {
      lOS.add((GM_OrientableSurface) geom);
    } else if (geom instanceof GM_MultiSurface<?>) {
      lOS.addAll((GM_MultiSurface<?>) geom);

    } else if (geom instanceof GM_MultiSolid<?>) {

      GM_MultiSolid<ISolid> multiCorps = (GM_MultiSolid<ISolid>) geom;
      List<ISolid> lCorps = multiCorps.getList();

      int nbElements = lCorps.size();

      for (int i = 0; i < nbElements; i++) {
        List<IOrientableSurface> lSurf = (lCorps.get(i)).getFacesList();
        lOS.addAll(lSurf);
      }

    } else if (geom instanceof GM_CompositeSolid) {

      GM_CompositeSolid compSolid = (GM_CompositeSolid) geom;
      List<ISolid> lCorps = compSolid.getGenerator();

      int nbElements = lCorps.size();

      for (int i = 0; i < nbElements; i++) {
        ISolid s = lCorps.get(i);
        lOS.addAll(s.getFacesList());
      }

    } else {

      this.bGRep = new BranchGroup();
      return;
    }

    if (lOS.size() == 0) {

      this.bGRep = new BranchGroup();

      return;
    }

    this.bGRep.addChild(this.traiteGeom(lOS));
    this.bGRep.compile();

  }

  public BranchGroup processGeom(List<IOrientableSurface> lOS, Color color,
      Color edgeColor, int widthEdge, double opacite) {
    this.color = color;
    this.edgeColor = edgeColor;
    this.widthEdge = widthEdge;
    this.coefOpacity = opacite;
    return this.traiteGeom(lOS);

  }

  private BranchGroup traiteGeom(List<IOrientableSurface> lOS) {
    int nbSurf = lOS.size();

    LineStripArray lStrips = null;
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

    lStrips = this.geometryWithColor(lCurve);

    Shape3D s = new Shape3D(ConversionJava3DGeOxygene
        .fromOrientableSToTriangleArray(lOS).getGeometryArray(),
        this.generateAppearance(true, this.color, this.coefOpacity, true));

    BranchGroup bg = new BranchGroup();

    bg.addChild(s);

  
      bg.addChild(new Shape3D(lStrips, this.generateAppearanceLine(true,
        this.edgeColor, 1, true)));

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
      if (ConstantRepresentation.cullMode) {
        pa.setCullFace(PolygonAttributes.CULL_BACK);

      }

    } else {
      // Indique que l'on est en mode filaire
      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);

    }
    pa.setCullFace(PolygonAttributes.CULL_NONE);
    pa.setBackFaceNormalFlip(false);

    // Association à l'apparence des attributs de géométrie et de material
    apparenceFinale.setPolygonAttributes(pa);

    if (isClrd) {
      Color3f couleur3F = new Color3f(color);
      // Création du material (gestion des couleurs et de l'affichage)

      Material material = new Material();

      material.setAmbientColor(couleur3F);
      material.setDiffuseColor(couleur3F);
      material.setEmissiveColor(couleur3F);
      material.setLightingEnable(true);
      material.setSpecularColor(couleur3F);
      material.setShininess(1);

      apparenceFinale.setMaterial(material);

    }

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.FASTEST,
          (float) (1 - coefOpacity));
      apparenceFinale.setTransparencyAttributes(t_attr);

    }
    return apparenceFinale;

  }

  /**
   * Génère une géométrie Java3D à partir d'une couleur indiquée
   * 
   * @return
   */
  private LineStripArray geometryWithColor(List<IOrientableCurve> lCurves) {
    // On créer un tableau contenant les lignes à représenter
    Color3f couleur3F = new Color3f(this.edgeColor);

    // Effectue la conversion de la géométrie

    // on compte le nombre de points
    int nPoints = 0;
    int nbLignes = lCurves.size();

    for (int i = 0; i < nbLignes; i++) {
      nPoints = nPoints + lCurves.get(i).coord().size();
    }

    // Problème de ligne vide
    if (nPoints < 2) {
      return null;
    }

    /*
     * Tableau permettant de définir le nombre de lignes représentées (cas des
     * multi-lignes Ici il y en a une contenant tous les points
     */
    int[] stripVertexCount = new int[nbLignes];

    // On indique de combien de points sera formé chaque fragment de lignes
    for (int i = 0; i < nbLignes; i++) {
      stripVertexCount[i] = lCurves.get(i).coord().size();


    }

    // On prépare la géométrie et ses autorisations
    LineStripArray geom = new LineStripArray(nPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3, stripVertexCount);

    geom.setCapability(GeometryArray.ALLOW_COLOR_READ);
    geom.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    geom.setCapability(Geometry.ALLOW_INTERSECT);

    // compteurs pour remplir le tableau de points
    int elementajoute = 0;

    for (int i = 0; i < nbLignes; i++) {
      // On récupère les points de chaque ligne
      IDirectPositionList lPoints = lCurves.get(i).coord();
      int nPointsTemp = lPoints.size();
      for (int j = 0; j < nPointsTemp; j++) {
        IDirectPosition dp = lPoints.get(j);
        Point3d point = new Point3d((float) dp.getX(), (float) dp.getY(),
            (float) dp.getZ());
        geom.setCoordinate(elementajoute, point);
        geom.setColor(elementajoute, couleur3F);

        elementajoute++;
      }
    }
    return geom;

  }

  /**
   * Methode pour l'apparence des lignes
   */
  private Appearance generateAppearanceLine(boolean isClrd, Color color,
      double coefOpacity, boolean isSolid) {

    // Création de l'apparence
    Appearance apparenceFinale = new Appearance();

    // Autorisations pour l'apparence
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    // Autorisations pour le material
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_READ);
    apparenceFinale.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    // Association à l'apparence des attributs de géométrie et de material

    // Création des attributs du polygone

    LineAttributes lp = new LineAttributes();

    lp.setLineAntialiasingEnable(true);
    lp.setLineWidth(this.widthEdge);
    if (isSolid) {
      lp.setLinePattern(LineAttributes.PATTERN_SOLID);

    } else {

      lp.setLinePattern(LineAttributes.PATTERN_DASH);

    }

    apparenceFinale.setLineAttributes(lp);

    if (isClrd) {

      // Création du material (gestion des couleurs et de l'affichage)
      Material material = new Material();

      material.setAmbientColor(new Color3f(color));
      material.setDiffuseColor(new Color3f(color));
      material.setEmissiveColor(new Color3f(color));
      material.setLightingEnable(true);
      material.setSpecularColor(new Color3f(color));
      material.setShininess(1);

      apparenceFinale.setMaterial(material);
    }

    if (coefOpacity != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.FASTEST,
          (float) (1 - coefOpacity));

      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    // Association à l'apparence des attributs de géométrie et de material

    return apparenceFinale;
  }

  public Color getColor() {
    return this.color;
  }

  public Color getEdgeColor() {
    return this.edgeColor;
  }

  public int getWidthEdge() {
    return this.widthEdge;
  }

  public double getCoefOpacity() {
    return this.coefOpacity;
  }

  @Override
  public Component getRepresentationComponent() {
    JButton jb = new JButton();
    jb.setBackground(this.getColor());
    jb.setBorder(BorderFactory.createLineBorder(this.getEdgeColor(),
        this.getWidthEdge()));
    jb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
    return jb;
  }
  


}
