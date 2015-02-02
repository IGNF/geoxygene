package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
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
 * @version 1.7
 * 
 *
 * 
 * Cette classe contient des méthode static liées à la visibilités des objets
 * 
 * 
 */
public class Visibility {

  public static double EPSILON = 0.01;

  public static boolean WELL_ORIENTED_FACE = false;

  public static List<IOrientableSurface> returnVisible(IFeature feat,
      IDirectPosition centre, double distMax) {
    List<IOrientableSurface> lPoly = new ArrayList<IOrientableSurface>();
    IGeometry geom = feat.getGeom();

    Box3D b = new Box3D(geom);
    // On élimine par distance
    if (centre.distance(b.getCenter()) > 1.2 * distMax) {
      return lPoly;
    }

    if (geom instanceof GM_Solid) {

      GM_Solid sol = (GM_Solid) geom;

      List<IOrientableSurface> lOS = sol.getFacesList();

      int nbFaces = lOS.size();

      for (int j = 0; j < nbFaces; j++) {
        GM_Polygon poly = (GM_Polygon) lOS.get(j);

        Box3D b2 = new Box3D(poly);
        // On élimine par distance
        if (centre.distance(b2.getCenter()) > distMax) {
          continue;
        }

        // On vérifie si il est visible
        if (Visibility.isVisible(poly, centre)) {

          lPoly.add(poly);

        }
      }
    } else if (geom instanceof GM_MultiSurface<?>) {

      @SuppressWarnings("unchecked")
      GM_MultiSurface<IOrientableSurface> gms = (GM_MultiSurface<IOrientableSurface>) geom;

      int nbFaces = gms.size();

      for (int j = 0; j < nbFaces; j++) {
        IPolygon poly = (IPolygon) gms.get(j);
        // On vérifie si il est visible
        if (Visibility.isVisible(poly, centre)) {

          lPoly.add(poly);

        }

      }

    } else {
      System.out.println("Other type" + geom.getClass().toString()
          + " GM_Solid expected");
    }

    return lPoly;
  }

  /**
   * Renvoie les géométries surfaciques des objets se situant à une distance
   * distMax de centre et dont la normale est orientée vers le centre
   * 
   * @param lFeat entités (pour l'instant solide)
   * @param centre un centre
   * @param distMax une distance maximale
   * @return les géométries surfaciques de ces entités
   */
  public static List<IOrientableSurface> returnVisible(
      IFeatureCollection<IFeature> lFeat, IDirectPosition centre, double distMax) {

    int nbElem = lFeat.size();

    List<IOrientableSurface> lPoly = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < nbElem; i++) {

      lPoly.addAll(returnVisible(lFeat.get(i), centre, distMax));

    }

    return lPoly;

  }

  /**
   * Indique si un polygone est visible (normale orientée vers le centre)
   * 
   * @param poly
   * @return
   */
  public static boolean isVisible(IPolygon poly, IDirectPosition centre) {

    if (!WELL_ORIENTED_FACE) {
      return true;
    }

    ApproximatedPlanEquation ap = new ApproximatedPlanEquation(poly);
    
    
  //  System.out.println(poly +  " "   + ap.equationValue(centre));
    
    if (ap.equationValue(centre) > -Visibility.EPSILON) {

      return true;

    }

    return false;
  }

}
