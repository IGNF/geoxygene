package fr.ign.cogit.geoxygene.sig3d.convert.java3d;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
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
 *
 * Classe permettant la conversion entre Java3D et les géométries GeOxygene
 * Conversion between Java3D geometries & GeOxygene geometries
 * 
 */
public class ConversionJava3DGeOxygene {

  /**
   * Permet de transformer un triangle Array (Java3D en une liste de géométries
   * ISO)
   * 
   * @param geometryArray Java3D GeometryArray en entrée
   * @return liste de triangles
   */
  public static List<IOrientableSurface> fromTriangleArrayToFacettes(
      TriangleArray geometryArray) {
    int count = geometryArray.getVertexCount();
    ArrayList<IOrientableSurface> lf = new ArrayList<IOrientableSurface>();

    double[][] debug = new double[count][9];

    double[] db = new double[3]; // 3*3 coordonnées

    int format = geometryArray.getVertexFormat();

    if ((format & GeometryArray.INTERLEAVED) != 0) {
      float[] dblT = geometryArray.getInterleavedVertices();

      // 3 pour les coordonnées
      int nbIndice = 3;

      if ((format & GeometryArray.TEXTURE_COORDINATE_2) != 0) {

        nbIndice = nbIndice + 2;
      } else if ((format & GeometryArray.TEXTURE_COORDINATE_3) != 0) {
        nbIndice = nbIndice + 3;

      } else if ((format & GeometryArray.TEXTURE_COORDINATE_4) != 0) {
        nbIndice = nbIndice + 4;

      }

      if ((format & GeometryArray.COLOR_3) != 0) {

        nbIndice = nbIndice + 3;
      } else if ((format & GeometryArray.COLOR_4) != 0) {
        nbIndice = nbIndice + 4;

      }

      if ((format & GeometryArray.NORMALS) != 0) {
        nbIndice = nbIndice + 3;

      }

      for (int i = 0; i <= count - 3; i = i + 3) {

        DirectPositionList lP = new DirectPositionList();

        DirectPosition p1 = new DirectPosition(
            10 * dblT[(i + 1) * nbIndice - 1],
            10 * dblT[(i + 1) * nbIndice - 3],
            10 * dblT[(i + 1) * nbIndice - 2]);
        DirectPosition p2 = new DirectPosition(
            10 * dblT[(i + 2) * nbIndice - 1],
            10 * dblT[(i + 2) * nbIndice - 3],
            10 * dblT[(i + 2) * nbIndice - 2]);
        DirectPosition p3 = new DirectPosition(
            10 * dblT[(i + 3) * nbIndice - 1],
            10 * dblT[(i + 3) * nbIndice - 3],
            10 * dblT[(i + 3) * nbIndice - 2]);

        lP.add(p1);
        lP.add(p2);
        lP.add(p3);
        lP.add(p1);

        GM_LineString ls = new GM_LineString(lP);

        GM_OrientableSurface oS = new GM_Triangle(ls);

        lf.add(oS);

      }

    } else {

      // geometryArray.
      for (int i = 0; i < count; i = i + 3) {
        // On récupère les points 3 par 3
        DirectPositionList lP = new DirectPositionList();

        geometryArray.getCoordinate(i, db);
        DirectPosition p1 = new DirectPosition(db[0], db[1], db[2]);

        debug[i][0] = db[0];
        debug[i][1] = db[1];
        debug[i][2] = db[2];

        geometryArray.getCoordinate(i + 1, db);

        DirectPosition p2 = new DirectPosition(db[0], db[1], db[2]);
        debug[i][3] = db[0];
        debug[i][4] = db[1];
        debug[i][5] = db[2];

        geometryArray.getCoordinate(i + 2, db);

        DirectPosition p3 = new DirectPosition(db[0], db[1], db[2]);

        debug[i][6] = db[0];
        debug[i][7] = db[1];
        debug[i][8] = db[2];

        lP.add(p1);
        lP.add(p2);
        lP.add(p3);
        lP.add(p1);

        GM_LineString ls = new GM_LineString(lP);

        GM_OrientableSurface oS = new GM_Triangle(ls);

        lf.add(oS);
      }

    }

    return lf;

  }

  /**
   * Permet de transformer un triangle Array (Java3D en une liste de géométries
   * ISO)
   * 
   * @param geometryArray un TriangleStripArray de Java3D
   * @return une liste de triangles 3D Geoxygene
   */
  public static List<IOrientableSurface> fromTriangleStripArrayToFacettes(
      TriangleStripArray geometryArray) {
    int count = geometryArray.getVertexCount();
    int nbStrip = geometryArray.getNumStrips();
    int[] stripVertexCount = new int[nbStrip];
      
     
    geometryArray.getStripVertexCounts(stripVertexCount);
    
    
    ArrayList<IOrientableSurface> lf = new ArrayList<IOrientableSurface>();

    double[][] debug = new double[count][9];

    double[] db = new double[3]; // 3*3 coordonnées
    int indBegin = 0;
    int indEnd = 0;
    
    for(int j=0;j<nbStrip;j++){
    
      indEnd = indBegin + stripVertexCount[j];
      
      
      if(indEnd <= indBegin+2){
        indEnd = indBegin;
        continue;
      }
      
      geometryArray.getCoordinate(indBegin, db);
      DirectPosition pPred1 = new DirectPosition(db[0], db[1], db[2]);


      geometryArray.getCoordinate(indBegin +1 , db);
      DirectPosition pPred2 = new DirectPosition(db[0], db[1], db[2]);
      
      boolean goodOrientation = true;
      
      
      for (int i = indBegin +2; i < indEnd; i ++) {
        // On récupère les points 3 par 3
        DirectPositionList lP = new DirectPositionList();

        geometryArray.getCoordinate(i , db);
        DirectPosition pTemp = new DirectPosition(db[0], db[1], db[2]);

        debug[i][0] = db[0];
        debug[i][1] = db[1];
        debug[i][2] = db[2];

        if(goodOrientation){
          
          
          lP.add(pPred1);
          lP.add(pPred2);
          lP.add(pTemp);
          lP.add(pPred1);
        }else{
          
          lP.add(pPred1);
          lP.add(pTemp);
          lP.add(pPred2);
          lP.add(pPred1);
        }
                
       

        GM_LineString ls = new GM_LineString(lP);

        GM_OrientableSurface oS = new GM_Triangle(ls);

        lf.add(oS);
        
        pPred1 = pPred2;
        pPred2 = pTemp;
        
        goodOrientation = !goodOrientation;


      }

      indBegin = indEnd;
      
    }
                               
    
    


    return lf;

  }

  /**
   * Renvoie une géométrie Java 3D à partir d'une liste de facettes
   * 
   * @param lFacettes une liste de faces GeOxygene
   * @return une géométrie Java3D
   */
  public static GeometryInfo fromOrientableSToTriangleArray(
      List<? extends IOrientableSurface> lFacettes) {

    // géométrie de l'objet
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

    // Nombre de facettes
    int nbFacet = lFacettes.size();

    if (nbFacet == 0) {

      return geometryInfo;
    }

    // On compte le nombres de points
    int npoints = 0;

    // On compte le nombre de polygones(trous inclus)
    int nStrip = 0;

    // Initialisation des tailles de tableaux
    for (int i = 0; i < nbFacet; i++) {
      IOrientableSurface os = lFacettes.get(i);

      npoints = npoints + (os.coord().size()) - ((GM_Polygon) os).getInterior().size() -1;
      nStrip = nStrip + 1 + ((GM_Polygon) os).getInterior().size();
    }

    // Nombre de points
    Point3d[] tabpoints = new Point3d[npoints];
    Vector3f[] normals = new Vector3f[npoints];

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
        int n = lPoints.size()-1;

        for (int j = 0; j < n; j++) {
          // On complète le tableau de points
          IDirectPosition dp = lPoints.get(j);
          Point3d point = new Point3d(dp.getX(), dp.getY(), dp.getZ());

          if (vect.getZ() < 0) {
            vect = vect.multConstante(-1);
          }

          tabpoints[elementajoute] = point;

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

    return geometryInfo;
  }
}
