package fr.ign.cogit.geoxygene.sig3d.representation.citygml.relief;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangulatedSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe expérimentale pour représenter les TIN
 * 
 * Harmoniser avec les MNT ?
 * 
 * @author MBrasebin
 * 
 */
public class RepresentationTin extends Default3DRep {

  ITriangulatedSurface tin;
  private int nbClasses;
  private double zMin;
  private double zMax;
  private int method;
  private Color3f[] colorShade;

  public final static int METHOD_LINEAR = 1;

  public static Color[] DEFAULT_COLORSHADE = ColorShade.BLUE_PURPLE_WHITE;

  public RepresentationTin(ITriangulatedSurface tin) {
    this(tin, DEFAULT_COLORSHADE);

  }

  public RepresentationTin(ITriangulatedSurface tin, int method) {

    this(tin, DEFAULT_COLORSHADE, method);

  }

  public RepresentationTin(ITriangulatedSurface tin, Color[] colorShade) {

    this(tin, colorShade, METHOD_LINEAR);

  }

  public RepresentationTin(ITriangulatedSurface tin, Color[] colorShade,
      int method) {
    super();
    this.nbClasses = colorShade.length;
    Box3D b = new Box3D(tin.coord());
    this.zMin = b.getLLDP().getZ();
    this.zMax = b.getURDP().getZ();
    this.colorShade = toColor3f(colorShade);
    this.method = METHOD_LINEAR;
    this.tin = tin;
    GeometryInfo geoInfo = fromOrientableSToTriangleArray();

    Appearance app = new Appearance();

    ColoringAttributes at = new ColoringAttributes();
    at.setShadeModel(ColoringAttributes.NICEST);

    app.setColoringAttributes(at);

    this.bGRep.addChild(new Shape3D(geoInfo.getGeometryArray(), app));

  }

  private Color3f[] toColor3f(Color[] tab) {
    Color3f[] col3f = new Color3f[this.nbClasses];
    for (int i = 0; i < nbClasses; i++) {

      col3f[i] = new Color3f(tab[i]);
    }

    return col3f;
  }

  /**
   * Renvoie une géométrie Java 3D à partir d'une liste de facettes
   * 
   * @param this.tin une liste de faces GeOxygene
   * @return une géométrie Java3D
   */
  private GeometryInfo fromOrientableSToTriangleArray() {

    // géométrie de l'objet
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

    // Nombre de facettes
    int nbFacet = this.tin.getPatch().size();

    if (nbFacet == 0) {

      return geometryInfo;
    }

    // On compte le nombres de points
    int npoints = 0;

    // On compte le nombre de polygones(trous inclus)
    int nStrip = 0;

    // Initialisation des tailles de tableaux
    for (int i = 0; i < nbFacet; i++) {
      IOrientableSurface os = this.tin.getPatch().get(i);

      npoints = npoints + os.coord().size();
      nStrip = nStrip + 1 + ((GM_Polygon) os).getInterior().size();
    }

    // Nombre de points
    Point3d[] tabpoints = new Point3d[npoints];
    Vector3f[] normals = new Vector3f[npoints];
    Color3f[] couleurs = new Color3f[npoints];

    // Peut servir à detecter les trous
    int[] strip = new int[nStrip];
    int[] contours = new int[nbFacet];

    // compteurs pour remplir le tableau de points
    int elementajoute = 0;

    // Compteur pour remplir les polygones (trous inclus)
    int nbStrip = 0;

    // Pour chaque face
    for (int i = 0; i < nbFacet; i++) {
      GM_Polygon poly = (GM_Polygon) this.tin.getPatch().get(i);

      ApproximatedPlanEquation eq = new ApproximatedPlanEquation(poly);
      Vecteur vect = eq.getNormale();

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

          lPoints = poly.getExterior().coord();

        } else {

          // Contribution de type trou
          lPoints = poly.getInterior(k - 1).coord();

        }

        // Nombres de points de la contribution
        int n = lPoints.size();

        for (int j = 0; j < n; j++) {
          // On complète le tableau de points
          IDirectPosition dp = lPoints.get(j);
          Point3d point = new Point3d(dp.getX(), dp.getY(), dp.getZ());

          if (vect.getZ() < 0) {
            vect = vect.multConstante(-1);
          }

          tabpoints[elementajoute] = point;
          couleurs[elementajoute] = this.getColor(dp.getZ());

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

    geometryInfo.setCoordinates(tabpoints);
    geometryInfo.setStripCounts(strip);
    geometryInfo.setContourCounts(contours);
    geometryInfo.setNormals(normals);
    geometryInfo.setColors(couleurs);

    return geometryInfo;
  }

  /**
   * Renvoie la couleur pour un z donné en fonction de la méthode
   * @param z
   * @return
   */
  public Color3f getColor(double z) {

    switch (this.method) {
      case METHOD_LINEAR:
        return this.getColorWithLinearMethod(z);

      default:
        return this.getColorWithLinearMethod(z);

    }

  }

  private Color3f getColorWithLinearMethod(double z) {

    double prop = (z - this.zMin) / (this.zMax - this.zMin);
    int indexClass = 0;
    if (prop != 1.0) {
      indexClass = (int) (prop * nbClasses);
    } else {
      indexClass = this.nbClasses - 1;
    }

    return this.colorShade[indexClass];

  }

}
