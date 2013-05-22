package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.List;

import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.Tetraedrisation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
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
 *          Classe permettant d'effectuer des calculs géométriques de base sur
 *          des solides - calcul d'aire des surfaces - calcul de barycentre -
 *          calcul d'envelope convexe - calcul des coordonnées inférieures et
 *          supérieures d'un objet Basic calculations on solids
 */
public class Calculation3D {

  private final static Logger logger = Logger.getLogger(Calculation3D.class
      .getName());

  /**
   * Calcul l'envelope convexe d'un solide
   * 
   * @param sol le solide dont on calcule l'envelope
   * @return l'envelope convexe du solide
   */
  public static GM_Solid convexHull(GM_Solid sol) {

    // Tétraèdrisation non contrainte
    Tetraedrisation tet = new Tetraedrisation(sol);

    try {

      tet.tetraedriseWithNoConstraint(true);

      GM_Solid s = new GM_Solid(tet.getTriangles());

      return s;

    } catch (Exception e) {
      e.printStackTrace();

    }

    return null;

  }

  /**
   * Calcul le volume à l'aide de la somme des volumes des tétraèdres issus de
   * la tétraèdrisation d'un solide. Un solide mathématiquement juste doit être
   * utilisé
   * 
   * @param sol le solide dont on calcule le volume
   * @return le volume du solide paramètre en m^3
   */

  public static double volume(GM_Solid sol) {

    // Calcul de la tétraèdrisation
    Tetraedrisation tet = new Tetraedrisation(sol);

    try {

      tet.tetraedriseWithConstraint(false);

      List<GM_Solid> lf = tet.getTetraedres();

      int nbTetraedres = lf.size();

      double volumTotal = 0.0;

      for (int i = 0; i < nbTetraedres; i++) {

        List<IOrientableSurface> listSurdf = lf.get(i).getFacesList();

        // Par définition dans tetraèdrisation, il y a un roulement sur
        // les points initiaux
        IDirectPosition dp1 = listSurdf.get(0).coord().get(0);
        IDirectPosition dp2 = listSurdf.get(1).coord().get(0);
        IDirectPosition dp3 = listSurdf.get(2).coord().get(0);
        IDirectPosition dp4 = listSurdf.get(3).coord().get(0);

        Matrix matVol = new Matrix(3, 3);
        matVol.set(0, 0, dp1.getX() - dp2.getX());
        matVol.set(1, 0, dp2.getX() - dp3.getX());
        matVol.set(2, 0, dp3.getX() - dp4.getX());
        matVol.set(0, 1, dp1.getY() - dp2.getY());
        matVol.set(1, 1, dp2.getY() - dp3.getY());
        matVol.set(2, 1, dp3.getY() - dp4.getY());
        matVol.set(0, 2, dp1.getZ() - dp2.getZ());
        matVol.set(1, 2, dp2.getZ() - dp3.getZ());
        matVol.set(2, 2, dp3.getZ() - dp4.getZ());

        volumTotal = volumTotal + Math.abs(matVol.det()) / 6;

      }

      return volumTotal;

    } catch (Exception e) {
      Calculation3D.logger.error(Messages
          .getString("Erreur lors de la triangulation"));
      e.printStackTrace();
    }
    return Double.NaN;

  }

  /**
   * Calcul de l'aire des surfaces du tétraèdre par contribution des différentes
   * triangles. Un solide mathématiquement juste doit être utilisé
   * 
   * @param sol le solide dont on calcule l'aire des surfaces
   * @return l'aire des surfaces en m²
   */
  public static double area(GM_Solid sol) {

    // On triangule les différente surface
    Tetraedrisation tet = new Tetraedrisation(sol);

    try {

      tet.tetraedriseWithConstraint(true);

      List<IOrientableSurface> lf = tet.getTriangles();

      int nbTriangle = lf.size();

      double aire = 0;

      for (int i = 0; i < nbTriangle; i++) {
        // On calcule les différentes contribution d'aire
        IOrientableSurface triangle = lf.get(i);

        IDirectPositionList lpoints = triangle.coord();

        Vecteur v1 = new Vecteur(lpoints.get(0), lpoints.get(1));
        Vecteur v2 = new Vecteur(lpoints.get(0), lpoints.get(2));

        Vecteur v3 = v1.prodVectoriel(v2);

        aire = aire + v3.norme() * 0.5;
      }

      return aire;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return Double.NaN;
  }

  public static double area(List<ITriangle> lTri) {
    double aireTot = 0;

    for (ITriangle tri : lTri) {
      IDirectPositionList lpoints = tri.coord();

      Vecteur v1 = new Vecteur(lpoints.get(0), lpoints.get(1));
      Vecteur v2 = new Vecteur(lpoints.get(0), lpoints.get(2));

      Vecteur v3 = v1.prodVectoriel(v2);

      aireTot = aireTot + v3.norme() * 0.5;

    }

    return aireTot;
  }

  /**
   * Renvoie le barycentre 3D d'une surface
   * 
   * @param surf
   * @return renvoie un objet DirectPosition correspondant au centre de gravité
   *         d'une surface
   */
  public static IDirectPosition centerOfGravity(IOrientableSurface surf) {
    double x = 0;
    double y = 0;
    double z = 0;

    IDirectPositionList dpl = surf.coord();
    int nbPoints = dpl.size();

    // On enlève le dernier point car une surface est fermée
    for (int i = 0; i < nbPoints - 1; i++) {
      IDirectPosition dpTemp = dpl.get(i);

      x = x + dpTemp.getX();
      y = y + dpTemp.getY();
      z = z + dpTemp.getZ();

    }

    return new DirectPosition(x / (nbPoints - 1), y / (nbPoints - 1), z
        / (nbPoints - 1));

  }

  /**
   * Calcul du barycentre d'un corps par contribution du centre de gravité des
   * différents triangles composant la surface Un solide mathématiquement juste
   * doit être utilisé
   * 
   * @param sol le solide dont on veut déterminer le barycentre
   * @return le barycentre du solide
   */
  public static IDirectPosition centerOfGravity(GM_Solid sol) {

    Tetraedrisation tet = new Tetraedrisation(sol);

    try {

      tet.tetraedriseWithConstraint(true);

      List<IOrientableSurface> lf = tet.getTriangles();

      int nbTriangle = lf.size();

      double aireTotale = 0;

      double XTemp = 0.0;
      double YTemp = 0.0;
      double ZTemp = 0.0;

      // On calcule le volume en changeant d'origine pour àviter les
      // erreurs nummériques
      IDirectPosition pOrigin = lf.get(0).coord().get(0);

      for (int i = 0; i < nbTriangle; i++) {

        IOrientableSurface triangle = lf.get(i);

        IDirectPositionList lpoints = triangle.coord();

        IDirectPosition p1 = new DirectPosition(

        // Changement d'origine pour des calculs plus précis
            lpoints.get(0).getX() - pOrigin.getX(), lpoints.get(0).getY()
                - pOrigin.getY(), lpoints.get(0).getZ() - pOrigin.getZ()

        );

        IDirectPosition p2 = new DirectPosition(lpoints.get(1).getX()
            - pOrigin.getX(), lpoints.get(1).getY() - pOrigin.getY(), lpoints
            .get(1).getZ() - pOrigin.getZ());

        IDirectPosition p3 = new DirectPosition(lpoints.get(2).getX()
            - pOrigin.getX(), lpoints.get(2).getY() - pOrigin.getY(), lpoints
            .get(2).getZ() - pOrigin.getZ());

        Vecteur v1 = new Vecteur(p1, p2);
        Vecteur v2 = new Vecteur(p1, p3);

        double XCentre = (p1.getX() + p2.getX() + p3.getX()) / 3;
        double YCentre = (p1.getY() + p2.getY() + p3.getY()) / 3;
        double ZCentre = (p1.getZ() + p2.getZ() + p3.getZ()) / 3;

        Vecteur v3 = v1.prodVectoriel(v2);

        double aire = v3.norme() * 0.5;

        // On calcule la contribuation nouvelle
        XTemp = XTemp + XCentre * aire;
        YTemp = YTemp + YCentre * aire;
        ZTemp = ZTemp + ZCentre * aire;

        aireTotale = aireTotale + aire;

      }

      // On calcule le résultat final
      XTemp = XTemp / aireTotale;
      YTemp = YTemp / aireTotale;
      ZTemp = ZTemp / aireTotale;

      XTemp = XTemp + pOrigin.getX();
      YTemp = YTemp + pOrigin.getY();
      ZTemp = ZTemp + pOrigin.getZ();

      return new DirectPosition(XTemp, YTemp, ZTemp);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return new DirectPosition(Double.NaN, Double.NaN, Double.NaN);

  }

  /**
   * Calcule le point inférieur de la boite englobant d'une géométrie 3D
   * 
   * @param geom la géométrie sur laquelle on effectue le calcul
   * @return le point inférieur de la boite de la géoémtrie
   */
  public static IDirectPosition pointMin(IGeometry geom) {

    IDirectPositionList dpl = geom.coord();
    if (dpl.size() == 0) {

      return null;
    }

    return Calculation3D.pointMin(dpl);

  }

  /**
   * Calcule le point supérieur de la boite englobant d'une géométrie 3D
   * 
   * @param geom la géométrie sur laquelle on effectue le calcul
   * @return le point supérieur de la boite de la géoémtrie
   */
  public static IDirectPosition pointMax(IGeometry geom) {
    IDirectPositionList dpl = geom.coord();
    if (dpl.size() == 0) {

      return null;
    }

    return Calculation3D.pointMax(dpl);

  }

  /**
   * Calcule le point inférieur de la boite englobant d'une liste de points
   * 
   * @param dpl une liste de points dont on calcule le point inférieur
   * @return le point inférieur de la boite
   */
  public static IDirectPosition pointMin(IDirectPositionList dpl) {

    double xmin = Double.POSITIVE_INFINITY;
    double ymin = Double.POSITIVE_INFINITY;
    double zmin = Double.POSITIVE_INFINITY;

    if (dpl == null) {
      return new DirectPosition(xmin, ymin, zmin);
    }
    int nbPoints = dpl.size();

    for (int i = 0; i < nbPoints; i++) {
      IDirectPosition dpTemp = dpl.get(i);

      double xtemp = dpTemp.getX();
      double ytemp = dpTemp.getY();
      double ztemp = dpTemp.getZ();

      xmin = Math.min(xmin, xtemp);
      ymin = Math.min(ymin, ytemp);
      zmin = Math.min(zmin, ztemp);

    }
    return new DirectPosition(xmin, ymin, zmin);

  }

  /**
   * Calcule le point supérieur de la boite englobant d'une liste de points
   * 
   * @param dpl une liste de points dont on calcule le point supérieur
   * @return le point supérieur de la boite
   */
  public static IDirectPosition pointMax(IDirectPositionList dpl) {

    double xmax = Double.NEGATIVE_INFINITY;
    double ymax = Double.NEGATIVE_INFINITY;
    double zmax = Double.NEGATIVE_INFINITY;

    if (dpl == null) {
      return new DirectPosition(xmax, ymax, zmax);
    }

    int nbPoints = dpl.size();

    for (int i = 0; i < nbPoints; i++) {
      IDirectPosition dpTemp = dpl.get(i);

      double xtemp = dpTemp.getX();
      double ytemp = dpTemp.getY();
      double ztemp = dpTemp.getZ();

      xmax = Math.max(xmax, xtemp);
      ymax = Math.max(ymax, ytemp);
      zmax = Math.max(zmax, ztemp);

    }

    return new DirectPosition(xmax, ymax, zmax);

  }

  /**
   * Translate un objet d'un vecteur dp
   * 
   * @param obj l'objet que l'on translate
   * @param dp le vecteur dont on veut translater l'objet
   */
  public static void translate(IGeometry obj, IDirectPosition dp) {
    Calculation3D.translate(obj, dp.getX(), dp.getY(), dp.getZ());

  }

  /**
   * Translate un objet le long d'un vecteur (x,y,z)
   * 
   * @param obj l'objet que l'on translate
   * @param x x du vecteur de translation
   * @param y y du vecteur de translation
   * @param z z du vecteur de translation
   */
  public static void translate(IGeometry obj, double x, double y, double z) {

    IDirectPositionList dpl = obj.coord();

    IDirectPositionList dplUnique = new DirectPositionList();

    int nbPoints = dpl.size();

    bouclei: for (int i = 0; i < nbPoints; i++) {
      IDirectPosition dp = dpl.get(i);

      int nbUnique = dplUnique.size();

      for (int j = 0; j < nbUnique; j++) {
        IDirectPosition dp2 = dplUnique.get(j);

        if (dp == dp2) {

          continue bouclei;
        }
      }

      dplUnique.add(dp);

    }

    int nbPointsUniques = dplUnique.size();

    for (int i = 0; i < nbPointsUniques; i++) {

      IDirectPosition dpTemp = dplUnique.get(i);

      dpTemp.setX(((double) Math.round(100 * dpTemp.getX())) / 100);
      dpTemp.setY(((double) Math.round(100 * dpTemp.getY())) / 100);
      dpTemp.setZ(((double) Math.round(100 * dpTemp.getZ())) / 100);

      dplUnique.get(i).move(x, y, z);

    }

  }

}
