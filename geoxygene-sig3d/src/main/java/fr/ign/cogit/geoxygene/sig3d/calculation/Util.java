package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
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
 *
 * Classe contenant des fonctions utiles diverses contenant les géomérties en 3D
 * - Centre de gravité d'un jeu de points - Volume sous un triangle Class that
 * containts several 3D geometries functions - Center of gravity of a
 * DirectPositionList - Volume under a Triangle
 * 
 */
public abstract class Util {

  /**
   * Renvoie le centre de gravité d'une DirectPositionList
   * 
   * @param dpl la DirectPosition liste dont on calcule le centre de gravité
   * @return le centre de gravité
   */
  public static DirectPosition centerOf(IDirectPositionList dpl) {
    return Util.centerOf(dpl.toArray3D());
  }

  /**
   * Renvoie le centre de gravité d'un tableau de coordonnées (X Y Z, X Y Z,
   * etc.)
   * 
   * @param coord3d
   * @return le centre de gravité
   */
  public static DirectPosition centerOf(double[] coord3d) {
    double xIni = 0;
    double yIni = 0;
    double zIni = 0;

    double length = coord3d.length;

    for (int i = 0; i < length; i = i + 3) {
      xIni = xIni + coord3d[i];
      yIni = yIni + coord3d[i + 1];
      zIni = zIni + coord3d[i + 2];
    }

    double lengthPoint = length / 3;

    return new DirectPosition(xIni / lengthPoint, yIni / lengthPoint, zIni
        / lengthPoint);

  }

  /**
   * Calcul à partir de 3 points (x1,y1,z1), (x2,y2,z2) et (x3,y3,z3) le volume
   * se trouvant sous ce triangle (jusqu'à z=0). La formule utilisée est : 0.5 *
   * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) (z1 + z2 + z3) /  3
   * 
   * @param x1
   * @param y1
   * @param z1
   * @param x2
   * @param y2
   * @param z2
   * @param x3
   * @param y3
   * @param z3
   * @return le volume sous ce triangle
   */
  public static double volumeUnderTriangle(double x1, double y1, double z1,
      double x2, double y2, double z2, double x3, double y3, double z3

  ) {

    if (z1 + z2 + z3 == 0) {
      return 0;

    }

    return 0.5 * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2))
        * (z1 + z2 + z3) / 3;

  }

  /**
   * Calcule le volume se trouvant sous le triangle tri
   * 
   * @param tri
   * @return
   */
  public static double volumeUnderTriangle(ITriangle tri) {

    IDirectPositionList dpl = tri.coord();

    IDirectPosition dp1 = dpl.get(0);
    IDirectPosition dp2 = dpl.get(1);
    IDirectPosition dp3 = dpl.get(2);

    return Util.volumeUnderTriangle(dp1.getX(), dp1.getY(), dp1.getZ(),
        dp2.getX(), dp2.getY(), dp2.getZ(), dp3.getX(), dp3.getY(), dp3.getZ()

    );
  }

  /**
   * Détecte une liste de faces verticales à partir d'une liste de faces
   * 
   * @param lSurf liste des surfaces que l'on traite
   * @param tolerence tolérence en radian pour considérer si une face est
   *          verticale ou non
   * @return une liste de faces verticales
   */
  public static IMultiSurface<IOrientableSurface> detectVertical(
      List<? extends IOrientableSurface> lSurf, double tolerence) {

    List<IOrientableSurface> lFacesVerticales = new ArrayList<IOrientableSurface>();

    int nbFaces = lSurf.size(); 
   
    for (int i = 0; i < nbFaces; i++) {  
      IOrientableSurface surfTemp = lSurf.get(i);
      PlanEquation eqP = new PlanEquation(surfTemp);

      double prodscalaire = eqP.getNormale().prodScalaire(MathConstant.vectZ);   

      if (Math.abs(prodscalaire) < Math.sin(tolerence)
          * eqP.getNormale().norme()) {

        lFacesVerticales.add(surfTemp);

      }

    }

    return new GM_MultiSurface<IOrientableSurface>(lFacesVerticales);

  }

  /**
   * Détecte une liste de faces non verticales à partir d'une liste de faces
   * 
   * @param lSurf liste des surfaces que l'on traite
   * @param tolerence tolérence en radian pour considérer si une face est
   *          verticale ou non
   * @return une liste de faces non verticales
   */
  public static IMultiSurface<IOrientableSurface>  detectNonVertical(
     List<? extends IOrientableSurface> lSurf, double tolerence) {

    List<IOrientableSurface> lFacesVerticales = new ArrayList<IOrientableSurface>();

    int nbFaces = lSurf.size();

    for (int i = 0; i < nbFaces; i++) {
      IOrientableSurface surfTemp = lSurf.get(i);
      PlanEquation eqP = new PlanEquation(surfTemp);

      double prodscalaire = eqP.getNormale().prodScalaire(MathConstant.vectZ);

      if (!(Math.abs(prodscalaire) < Math.sin(tolerence)
          * eqP.getNormale().norme())) {

        lFacesVerticales.add(surfTemp);

      }

    }

    return new GM_MultiSurface<IOrientableSurface>(lFacesVerticales);

  }

  /**
   * Permet de détecter le toit, il s'agit des faces dont le produit scalaire
   * par rapport à l'axe z est supérieur à tolérance
   * @param lSurf
   * @param tolerence
   * @return
   */
  public static IMultiSurface<IOrientableSurface> detectRoof(
      List<? extends IOrientableSurface> lSurf, double tolerence) {

    List<IOrientableSurface> lFacesNVerticales = new ArrayList<IOrientableSurface>();

    int nbFaces = lSurf.size();

    for (int i = 0; i < nbFaces; i++) {

      IOrientableSurface surfTemp = lSurf.get(i);

      DirectPositionList dplTemp = (DirectPositionList) surfTemp.coord()
          .clone();
      // dplTemp.remove(0);

      ApproximatedPlanEquation eqP = new ApproximatedPlanEquation(dplTemp);

      Vecteur v = eqP.getNormale();
      v.normalise();

      if (Math.abs(v.getZ())> tolerence) {

        lFacesNVerticales.add((IPolygon) surfTemp);

      }

    }

    int nFacesNVerticales = lFacesNVerticales.size();

    if (nFacesNVerticales == 0) {
      return null;
    }

    return new GM_MultiSurface<IOrientableSurface>(lFacesNVerticales);

  }

  /**
   * Detect FloorTriangles
   * @param lSurf
   * @param tolerence
   * @return
   */
  public static GM_MultiSurface<ITriangle> detectFloorTriangles(
      List<ITriangle> lSurf, double tolerence) {

    List<IOrientableSurface> lFacesNVerticales = new ArrayList<IOrientableSurface>();

    int nbFaces = lSurf.size();

    for (int i = 0; i < nbFaces; i++) {

      IOrientableSurface surfTemp = lSurf.get(i);

      DirectPositionList dplTemp = (DirectPositionList) surfTemp.coord()
          .clone();
      // dplTemp.remove(0);

      ApproximatedPlanEquation eqP = new ApproximatedPlanEquation(dplTemp);

      Vecteur v = eqP.getNormale();
      v.normalise();

      if (v.getZ() < -tolerence) {

        lFacesNVerticales.add(new GM_Triangle(((GM_Polygon) surfTemp)
            .getExterior()));

      }

    }

    int nFacesNVerticales = lFacesNVerticales.size();

    if (nFacesNVerticales == 0) {
      return null;
    }

    return new GM_MultiSurface<ITriangle>(lFacesNVerticales);

  }

  /**
   * Detect Roof on triangulated objects
   * @param lSurf
   * @param tolerence
   * @return
   */
  public static GM_MultiSurface<ITriangle> detectRoofTriangles(
      List<? extends ITriangle> lSurf, double tolerence) {

    List<IOrientableSurface> lFacesNVerticales = new ArrayList<IOrientableSurface>();

    int nbFaces = lSurf.size();

    for (int i = 0; i < nbFaces; i++) {

      IOrientableSurface surfTemp = lSurf.get(i);

      DirectPositionList dplTemp = (DirectPositionList) surfTemp.coord()
          .clone();
      // dplTemp.remove(0);

      ApproximatedPlanEquation eqP = new ApproximatedPlanEquation(dplTemp);

      Vecteur v = eqP.getNormale();
      v.normalise(); 

      if (Math.abs(v.getZ()) > tolerence) { 

        lFacesNVerticales.add(new GM_Triangle(((GM_Polygon) surfTemp)
            .getExterior()));

      }

    }

    int nFacesNVerticales = lFacesNVerticales.size();

    if (nFacesNVerticales == 0) {
      return null;
    }

    return new GM_MultiSurface<ITriangle>(lFacesNVerticales);

  }
  
  /**
   * Indicate if a liste of surce is only composed by triangles
   * @param lS
   * @return
   */
  public static boolean containOnlyTriangleFaces(List<IOrientableSurface> lS) {

    int nbSurf = lS.size();

    for (int i = 0; i < nbSurf; i++) {
      IOrientableSurface surfTemp = lS.get(i);
      IDirectPositionList dpl = surfTemp.coord();

      if (dpl.size() > 4) {

        return false;
      }

      if (dpl.size() == 4) {

        if (!dpl.get(0).equals(dpl.get(dpl.size() - 1))) {
          return false;
        }
      }

    }

    return true;
  }

  /**
   * Assess volume in a triangulated solid
   * @param sol
   * @return
   */
  public static double volumeTriangulatedSolid(ISolid sol) {

    return Util.volumeUnderSurface(sol.getFacesList());

  }

  /**
   * Assess volume under a triangulated surface
   * @param lOS
   * @return
   */
  public static double volumeUnderSurface(List<IOrientableSurface> lOS) {

    int nbContrib = lOS.size();
    double volume1 = 0;

    for (int i = 0; i < nbContrib; i++) {

      IOrientableSurface gs = lOS.get(i);

      GM_Triangle tri = new GM_Triangle(gs.coord().get(0), gs.coord().get(1),
          gs.coord().get(2));
      volume1 = volume1 + Util.volumeUnderTriangle(tri);
    }

    return volume1;

  }

  /**
   * Asses area of a triangulated solide
   * @param sol
   * @return
   */
  public static double aireTriangulatedSolid(GM_Solid sol) {

    return Util.areaTriangulatedSurface(sol.getFacesList());

  }

  /**
   * Assess area of a triangulated surface
   * @param lOS
   * @return
   */
  public static double areaTriangulatedSurface(List<IOrientableSurface> lOS) {

    int nbContrib = lOS.size();
    double aire1 = 0;

    for (int i = 0; i < nbContrib; i++) {

      IOrientableSurface gs = lOS.get(i);

      GM_Triangle tri = new GM_Triangle(gs.coord().get(0), gs.coord().get(1),
          gs.coord().get(2));
      aire1 = aire1 + tri.area();
    }

    return aire1;

  }

  /**
   * Assess 3D area of a list of Triangles
   * @param triList
   * @return
   */
  public static double aireTriangles(List<ITriangle> triList) {

    int nbContrib = triList.size();
    double aire1 = 0;

    for (int i = 0; i < nbContrib; i++) {

      aire1 = aire1 + triList.get(i).area();
    }

    return aire1;
  }

}
