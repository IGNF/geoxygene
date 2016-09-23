package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoFactory;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;



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
 */
public class CutBuilding {

  public static double EPSILON = 0.1;

  public static List<IPolygon> cutAt(
      IFeatureCollection<? extends IFeature> featColl, double z) {

    List<IOrientableSurface> lOS = new ArrayList<>();

    for (IFeature feat : featColl) {

      lOS.addAll(FromGeomToSurface.convertGeom(feat.getGeom()));

    }

    return cutAt(lOS, z);

  }
  
  
  public static  List<IPolygon> cutAt(IGeometry geom, double z) {
    
    return cutAt(FromGeomToSurface.convertGeom(geom),z);
    
  }

  public static List<IPolygon> cutAt(List<IOrientableSurface> lOS, double z) {

    List<ITriangle> lT = FromPolygonToTriangle.convertAndTriangle(lOS);

    return cutFromTriAt(lT, z);
  }

  public static List<IPolygon> cutFromTriAt(List<ITriangle> lT, double z) {

    List<IPolygon> lPS = new ArrayList<>();

    PlanEquation eq = new PlanEquation(0, 0, 1, -z);

    List<IGeometry> lG = CutBuilding.cut(lT, eq);

    IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();
    for (IGeometry geom : lG) {

      if (geom.dimension() == 1) {
        featCollOut.add(new DefaultFeature(geom));
      }

    }

    
    if(featCollOut.size() < 3){
      return lPS;
    }
    
    
    CarteTopo map = CarteTopoFactory.newCarteTopo("", featCollOut, 0.5, true);

  
    
    if(map.getPopArcs().envelope() == null || map.getPopArcs().envelope().isEmpty()){
      return lPS;
    }





    for (Face f : map.getPopFaces()) {
      if (f.isInfinite()) {

        IPolygon pol = f.getGeometrie();

        for (IRing r : pol.getInterior()) {

          IDirectPositionList dpl = r.coord();
          dpl.inverseOrdre();

          for (IDirectPosition dp : dpl) {
            dp.setZ(z);
          }

          lPS.add(new GM_Polygon(new GM_LineString(dpl)));

        }

      }
    }

    return lPS;

  }

  /**
   * 
   * @param trianglesList
   * @param eq
   * @return
   */
  public static List<IGeometry> cut(ITriangle t, PlanEquation eq) {

    List<IGeometry> lGeom = new ArrayList<IGeometry>();

    IGeometry geom = eq.triangleIntersection(t);
    if (geom != null) {
      lGeom.add(geom);
    }

    return lGeom;
  }

  /**
   * 
   * @param trianglesList
   * @param eq
   * @return
   */
  public static List<IGeometry> cut(List<ITriangle> trianglesList,
      PlanEquation eq) {

    List<IGeometry> lGeom = new ArrayList<IGeometry>();

    for (ITriangle t : trianglesList) {
      lGeom.addAll(cut(t, eq));
    }

    return lGeom;
  }

}
