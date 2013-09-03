package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;



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
 * Exemple d'extension de la classe DTM destinée au calcul de surfaces projeteées sur un MNT
 * 
 * Class dedicated to measure surfaces projected on a DTM
 * 
 */
public class DTMArea extends DTM {
  
  
  private final static Logger logger = Logger.getLogger(DTMArea.class.getName());

   /**
    * 
    * Same constructor as DTM
    * 
    * @param file
    * @param layerName
    * @param fill
    * @param exager
    * @param colorGradation
    */
  public DTMArea(String file, String layerName, boolean fill, int exager,
      Color[] colorGradation) {
    super(file, layerName, fill, exager, colorGradation);
  }

  /**
   * Same constructor as DTM
   * 
   * @param file
   * @param layerName
   * @param fill
   * @param exager
   * @param imageFileName
   * @param imageEnvelope
   */
  public DTMArea(String file, String layerName, boolean fill, int exager,
      String imageFileName, GM_Envelope imageEnvelope) {
    super(file, layerName, fill, exager, imageFileName, imageEnvelope);
  }

  /**
   * 
   * Calculate 3D area of surfacic geometries of a feature
   * 
   * @param feat
   * @return
   * @throws Exception
   */
  public double calcul3DArea(IFeature feat) throws Exception {

    double area = 0;

    IGeometry geom = feat.getGeom();


    return calcul3DArea(geom);
  }
  
  
  
  public double calcul3DArea(IGeometry geom) throws Exception{
    double area = -1;
    if (geom instanceof GM_OrientableSurface
        || geom instanceof GM_MultiSurface<?>) {
      // On la convertit en JTS
      Geometry geomJTS = JtsGeOxygene.makeJtsGeom(geom);

      if (geomJTS instanceof Polygon) {
        // Polygon on applique tout de suite
        area = area + this.calcul3DArea((Polygon) geomJTS);

      } else if (geomJTS instanceof MultiPolygon) {
        // MultiPolygon on l'applique par morceaux
        MultiPolygon multiP = (MultiPolygon) geomJTS;
        int nGeom = multiP.getNumGeometries();

        for (int j = 0; j < nGeom; j++) {
          area = area + this.calcul3DArea((Polygon) multiP.getGeometryN(j));

        }

      } else {
        // Type de géométrie non reconnue
        logger.warn("Geomtrie non reconnue"
            + geomJTS.getClass().toString());
      }

    } else {
      // Type de géométrie non reconnue
      logger.warn("Geomtrie non reconnue" + geom.getClass().toString());
    }
    
    return area;
    
  }

  /**
   * Calculate 3D area of surfacic geometries from features
   * @param featColl
   * @return
   * @throws Exception
   */
  public double calcul3DArea(IFeatureCollection<? extends IFeature> featColl)
      throws Exception {
    // Le nombre d'éléments
    int nbelem = featColl.size();
    // L'aire totale
    double area = 0;

    for (int i = 0; i < nbelem; i++) {
      // On vérifie le type de la géométrie
      IFeature feat = featColl.get(i);
      area = this.calcul3DArea(feat) + area;

    }

    // On renvoie l'aire totale
    return area;
  }

  /**
   * Calculate 3D area from a polygon
   * 
   * @param poly 
   * @return 3D area
   * @throws Exception
   */
  public double calcul3DArea(Polygon poly) {
    GeometryFactory fac = new GeometryFactory();

    double area = 0;

    // On récupère les coordonnées extrêmes de l'enveloppes
    Coordinate[] coordEnv = poly.getEnvelope().getCoordinates();
    double xmin = coordEnv[0].x;
    double xmax = coordEnv[2].x;

    double ymin = coordEnv[0].y;
    double ymax = coordEnv[2].y;

    // On récupère dans quels triangles se trouvent dpMin et dpMax
    int posxMin = (int) (-1 + (xmin - this.xIni) / (this.stepX * this.sampling));
    int posyMin = (int) (-1 + (ymin - this.yIni) / (this.stepY * this.sampling));

    int posxMax = 1 + (int) ((xmax - this.xIni) / (this.stepX * this.sampling));
    int posyMax = 1 + (int) ((ymax - this.yIni) / (this.stepY * this.sampling));

    // On récupère les sommets extérieurs de ces triangles (ceux qui
    // permettent d'englober totalement le rectangle dpMin, dpMax
    Coordinate dpOrigin = new Coordinate(posxMin * this.stepX + this.xIni,
        posyMin * this.stepY + this.yIni);
    Coordinate dpFin = new Coordinate(posxMax * this.stepX + this.xIni, posyMax
        * this.stepY + this.yIni);

    // On évalue le nombre de mailles à couvrir
    int nbInterX = (int) ((dpFin.x - dpOrigin.x) / this.stepX);
    int nbInterY = (int) ((dpFin.y - dpOrigin.y) / this.stepY);

    // On crée une géométrie géoxygne pour chacune de ces mailles
    // (2 triangles par maille)
    for (int i = 0; i < nbInterX; i++) {
      for (int j = 0; j < nbInterY; j++) {

        Coordinate dp1 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + j * this.stepY);
        Coordinate dp2 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + j * this.stepY);
        Coordinate dp3 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + (j + 1) * this.stepY);

        Coordinate dp4 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + (j + 1) * this.stepY);

        Coordinate[] coord = new Coordinate[4];
        coord[0] = dp1;
        coord[1] = dp2;
        coord[2] = dp4;
        coord[3] = dp1;

        LinearRing l1 = fac.createLinearRing(coord);

        Coordinate[] coord2 = new Coordinate[4];
        coord2[0] = dp1;
        coord2[1] = dp4;
        coord2[2] = dp3;
        coord2[3] = dp1;

        LinearRing l2 = fac.createLinearRing(coord2);

        // Les 2 triangles du MNT que l'on étudie
        Polygon poly1 = fac.createPolygon(l1, null);
        Polygon poly2 = fac.createPolygon(l2, null);

        area = area + this.contributionForTriangle(poly, poly1)
            + this.contributionForTriangle(poly, poly2);

      }

    }

    return area;

  }

  /**
   * Calculate the contribution of a polyon for a given triangle
   * 
   * @param poly the polygon
   * @param triangle the considered triangle
   * @return
   */
  private double contributionForTriangle(Polygon poly, Polygon triangle) {

    // 1er cas : ils ne se touchent pas
    if (!poly.intersects(triangle)) {
      return 0;
    }

    // 2ieme cas : le triangle est complètement inclus dans le polygone
    if (poly.contains(triangle)) {

      // On passe notre triangle en 3D
      triangle = (Polygon) this.mapSurface(triangle, 0.0, true, false);

      // On renvoie l'aire 3D du triangle
      Coordinate[] coordT = triangle.getCoordinates();

      Coordinate cA = coordT[0];
      Coordinate cB = coordT[1];
      Coordinate cC = coordT[2];

      Vecteur v1 = new Vecteur(cB.x - cA.x, cB.y - cA.y, cB.z - cA.z);
      Vecteur v2 = new Vecteur(cC.x - cA.x, cC.y - cA.y, cC.z - cA.z);

      // L'aire c'est 1/2 (AB vectoriel AC)
      double area3DTriangle = 0.5 * v1.prodVectoriel(v2).norme();

      return area3DTriangle;

    }

    // On obtient la surface recouverte par le polygone sur le triangle
    Geometry geom = poly.intersection(triangle);

    // Si l'intersection n'existe pas, on continue
    if (geom == null || geom.isEmpty()) {

      logger.debug("Intersection between polygon and triangle is empty or null");
      return 0;
    }

    // On passe notre triangle en 3D
    triangle = (Polygon) this.mapSurface(triangle, 0.0, true, false);

    // On renvoie l'aire 3D du triangle
    Coordinate[] coordT = triangle.getCoordinates();

    Coordinate cA = coordT[0];
    Coordinate cB = coordT[1];
    Coordinate cC = coordT[2];

    Vecteur v1 = new Vecteur(cB.x - cA.x, cB.y - cA.y, cB.z - cA.z);
    Vecteur v2 = new Vecteur(cC.x - cA.x, cC.y - cA.y, cC.z - cA.z);

    // L'aire c'est 1/2 (AB vectoriel AC)
    double area3DTriangle = 0.5 * v1.prodVectoriel(v2).norme();

    // L'intersection n'est pas un triangle (a priori), on applique une
    // règle de 3
    // Aire 2D du triangle
    v1.setZ(0);
    v2.setZ(0);
    double area2DTriangle = 0.5 * v1.prodVectoriel(v2).norme();

    // Le %age de recouvrement entre le triangle et le polygone
    double coeffRecouvrement = geom.getArea() / area2DTriangle;

    if (coeffRecouvrement > 1.0) {
      logger.error("Coverage coeff higher than 1");
      return 0;
    }

    double aireFinale = coeffRecouvrement * area3DTriangle;

    return aireFinale;

  }

}
