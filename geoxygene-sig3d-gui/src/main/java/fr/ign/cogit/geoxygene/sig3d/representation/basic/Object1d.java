package fr.ign.cogit.geoxygene.sig3d.representation.basic;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.representation.ConstantRepresentation;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;


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
 * Classe permettant de représenter des polylignes en 3D.
 * 
 * Basic class for rendering 3D polylines
 * 
 */
public class Object1d extends BasicRep3D {

  private final static Logger logger = LogManager
      .getLogger(Object1d.class
      .getName());

  public static float width = 4.0f;

  public Object1d(IFeature feat, Color color) {

    this(feat, true, color, 1, true);
  }

  /**
   * créer la représentation d'un objet avec une géométrie de type linéaire.
   * créer le lien entre la représentation et l'objet indispensable pour pouvoir
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
  public Object1d(IFeature feat, boolean isClrd, Color color,
      double coefOpacity, boolean isSolid) {
    super(feat, isClrd, color, coefOpacity, isSolid);
    // Génère en fonction du cas la géométrie Java3D
    LineStripArray geomInfo = null;
    if (isClrd) {
      geomInfo = this.geometryWithColor();
    } else {
      geomInfo = this.geometryWithOutColor();
    }

    if (geomInfo == null) {
      Object1d.logger.warn(Messages.getString("Representation.RepNulle"));
      return;
    }
    // Génère l'apparence et l'applique
    Shape3D shapepleine = new Shape3D(geomInfo, this.generateAppearance(isClrd,
        color, coefOpacity, isSolid));

    // Autorisations sur la Shape3D
    shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shapepleine.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shapepleine.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    shapepleine.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    shapepleine.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
    shapepleine.setCapability(Node.ALLOW_LOCALE_READ);
    shapepleine.setCapability(Node.ALLOW_PICKABLE_READ);
    shapepleine.setCapability(Node.ALLOW_PICKABLE_WRITE);

    this.bGRep.addChild(shapepleine);

    // Optimisation
    this.bGRep.compile();
  }

  /**
   * Génère une géométrie Java3D à partir d'une couleur indiquée
   * 
   * @return
   */
  private LineStripArray geometryWithColor() {
    // On créer un tableau contenant les lignes à représenter
    Color3f couleur3F = new Color3f(this.color);

    IGeometry objgeom = this.feat.getGeom();

    ArrayList<IGeometry> lCurves = new ArrayList<IGeometry>();

    if (objgeom instanceof GM_OrientableCurve) {

      GM_OrientableCurve curve = (GM_OrientableCurve) objgeom;

      lCurves.add(curve);

    } else if (objgeom instanceof GM_MultiCurve<?>) {
      GM_MultiCurve<?> multiCurve = (GM_MultiCurve<?>) objgeom;
      lCurves.addAll(multiCurve.getList());

    } else if (objgeom instanceof GM_CompositeCurve) {
      GM_CompositeCurve multiCurve = (GM_CompositeCurve) objgeom;
      lCurves.addAll(multiCurve.getGenerator());

    } else {
      Object1d.logger.warn(Messages.getString("Representation.RepNulle"));
      return null;
    }

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
   * Génère une géométrie Java3D avec des couleurs aléatoires
   * 
   * @return
   */
  private LineStripArray geometryWithOutColor() {
    // On créer un tableau contenant les lignes à représenter
    IGeometry objgeom = this.feat.getGeom();

    ArrayList<IGeometry> lCurves = new ArrayList<IGeometry>();

    if (objgeom instanceof GM_OrientableCurve) {

      GM_OrientableCurve curve = (GM_OrientableCurve) objgeom;

      lCurves.add(curve);

    } else if (objgeom instanceof GM_MultiCurve<?>) {
      GM_MultiCurve<?> multiCurve = (GM_MultiCurve<?>) objgeom;
      lCurves.addAll(multiCurve.getList());

    } else if (objgeom instanceof GM_CompositeCurve) {
      GM_CompositeCurve multiCurve = (GM_CompositeCurve) objgeom;
      lCurves.addAll(multiCurve.getGenerator());

    } else {

      return null;
    }

    // Effectue la conversion de la géométrie

    // on compte le nombre de poitns
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

    for (int i = 0; i < nbLignes; i++) {
      stripVertexCount[i] = lCurves.get(i).coord().size();

    }

    // On indique de combien de points sera formé chaque fragment de lignes
    LineStripArray geom = new LineStripArray(nPoints, GeometryArray.COORDINATES
        | GeometryArray.COLOR_3, stripVertexCount);

    geom.setCapability(GeometryArray.ALLOW_COLOR_READ);
    geom.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    geom.setCapability(Geometry.ALLOW_INTERSECT);

    // compteurs pour remplir le tableau de points
    int elementajoute = 0;

    for (int i = 0; i < nbLignes; i++) {

      IDirectPositionList lPoints = lCurves.get(i).coord();
      int nPointsTemp = lPoints.size();
      for (int j = 0; j < nPointsTemp; j++) {
        // On récupère les points de chaque ligne
        IDirectPosition dp = lPoints.get(j);
        Point3d point = new Point3d((float) dp.getX(), (float) dp.getY(),
            (float) dp.getZ());
        geom.setCoordinate(elementajoute, point);

        Color3f couleur3F = new Color3f((float) Math.random(),
            (float) Math.random(), (float) Math.random());

        geom.setColor(elementajoute, couleur3F);

        elementajoute++;

      }
    }

    return geom;

  }

  /**
   * Modifie la couleur de l'affichage mais pas l'attribut couleur (utile pour
   * des changements de couleur temporaires
   */
  private void changeColor(Color color) {

    LineStripArray geom = (LineStripArray) this.getShapes().get(0)
        .getGeometry();

    if (color == null) {
      // Si il n'y a pas de couleur on applique la coloration aléatoire
      this.getShapes().get(0).setGeometry(this.geometryWithOutColor(), 0);

      return;
    }
    // On applique la coloration choisie
    Color3f couleur3F = new Color3f(color);

    IDirectPositionList lPoints = this.getFeature().getGeom().coord();

    int n = lPoints.size();
    Color3f[] tab = new Color3f[n];

    for (int i = 0; i < n; i++) {
      tab[i] = couleur3F;

    }

    geom.setColors(0, tab);

  }

  /**
   * Change l'attribut Couleur de manière définitive ainsi que la couleur de
   * l'objet
   * 
   * @param col
   */
  public void setColor(Color col) {
    this.color = col;
    this.changeColor(col);
  }

  /**
   * Methode pour l'apparence des triangles pleins
   */
  private Appearance generateAppearance(boolean isClrd, Color color,
      double coefOpacite, boolean isSolid) {

    this.isColored = isClrd;
    this.opacity = coefOpacite;
    this.isSolid = isSolid;

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
    lp.setLineWidth(Object1d.width);
    if (isSolid) {
      lp.setLinePattern(LineAttributes.PATTERN_SOLID);

    } else {

      lp.setLinePattern(LineAttributes.PATTERN_DASH);

    }

    apparenceFinale.setLineAttributes(lp);

    if (isClrd) {

      // Création du material (gestion des couleurs et de l'affichage)
      Material material = new Material();

      material.setAmbientColor(0.2f, 0.2f, 0.2f);
      material.setDiffuseColor(new Color3f(color));
      material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
      material.setShininess(128);

      apparenceFinale.setMaterial(material);
    }

    if (coefOpacite != 1) {

      TransparencyAttributes t_attr =

      new TransparencyAttributes(TransparencyAttributes.BLENDED,
          (float) coefOpacite,

          TransparencyAttributes.BLEND_SRC_ALPHA,

          TransparencyAttributes.BLENDED);

      apparenceFinale.setTransparencyAttributes(t_attr);
    }

    // Association à l'apparence des attributs de géométrie et de material

    return apparenceFinale;
  }

  @Override
  public void setSelected(boolean isSelected) {
    this.selected = isSelected;
    if (isSelected) {
      this.changeColor(ConstantRepresentation.selectionColor);

    } else {

      this.changeColor(this.color);

    }

  }

}
