package fr.ign.cogit.geoxygene.sig3d.util.aggregation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
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
 * 
 **/ 
public class AggregationLineString {

  public static double EPSILON = 0.1;

  public static List<IOrientableSurface> getPolygonOrNull(List<ICurve> lGeom) {

    List<IGeometry> lGeomOut = getAllGeom(lGeom);
    List<IOrientableSurface> lPolyOut = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < lGeomOut.size(); i++) {

      IGeometry geom = lGeomOut.get(i);

      if (geom instanceof IOrientableSurface) {
        IOrientableSurface iOS = (IOrientableSurface) geom;

        if (iOS.coord().get(0) != iOS.coord().get(iOS.coord().size() - 1)) {
          iOS.coord().add(iOS.coord().get(0));
        }

        if (iOS.coord().size() >= 4) {

          lPolyOut.add((IOrientableSurface) geom);
        }

      } else {

        return null;
      }

    }

    return lPolyOut;

  };

  public static List<IOrientableSurface> getPolygonOnly(List<ICurve> lGeom) {

    List<IGeometry> lGeomOut = getAllGeom(lGeom);
    List<IOrientableSurface> lPolyOut = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < lGeomOut.size(); i++) {

      IGeometry geom = lGeomOut.get(i);

      if (geom instanceof IOrientableSurface) {

        IOrientableSurface iOS = (IOrientableSurface) geom;

        if (iOS.coord().get(0) != iOS.coord().get(iOS.coord().size() - 1)) {
          iOS.coord().add(iOS.coord().get(0));
        }

        if (iOS.coord().size() >= 4) {

          lPolyOut.add((IOrientableSurface) geom);
        }

      }

    }

    return lPolyOut;

  }

  public static List<IGeometry> getAllGeom(List<ICurve> lGeom) {

    List<ICurve> lGeomIni = new ArrayList<ICurve>(lGeom);
    List<IGeometry> lGeomOut = new ArrayList<IGeometry>();

    bouclei: for (int i = 0; i < lGeomIni.size(); i++) {
      ILineString ls1 = (ILineString) lGeomIni.get(i);

      for (int j = i + 1; j < lGeomIni.size(); j++) {

        ILineString ls2 = (ILineString) lGeomIni.get(j);
        IGeometry geomAGG = aggregateLineString(ls1, ls2);

        if (geomAGG == null) {
          continue;
        }

        if (geomAGG instanceof IPolygon) {

          lGeomOut.add((IPolygon) geomAGG);
          lGeomIni.remove(j);
          lGeomIni.remove(i);
          i = -1;
          continue bouclei;
        }

        if (geomAGG instanceof ICurve) {

          lGeomIni.remove(j);
          lGeomIni.remove(i);
          lGeomIni.add((ICurve) geomAGG);
          i = -1;
          continue bouclei;
        }

      }

    }

    return lGeomOut;
  }

  public static IGeometry aggregateLineString(ILineString ls1, ILineString ls2) {

    IDirectPositionList dpl1 = (IDirectPositionList) ls1.coord().clone();
    IDirectPositionList dpl2 = (IDirectPositionList) ls2.coord().clone();

    IDirectPosition dp1 = dpl1.get(0);
    IDirectPosition dp2 = dpl1.get(dpl1.size() - 1);

    if (dp1.equals(dp2)) {
      return null;
    }

    IDirectPosition dp3 = dpl2.get(0);
    IDirectPosition dp4 = dpl2.get(dpl2.size() - 1);

    if (dp4.equals(dp3)) {
      return null;
    }

    if (dp2.equals(dp3, EPSILON) || dp2.equals(dp4, EPSILON)) {

      dpl1 = ((IDirectPositionList) dpl1.clone()).reverse();
      dp2 = dpl1.get(dpl1.size() - 1);
      dp1 = dpl1.get(0);

    }

    if (dp1.equals(dp3, EPSILON)) {

      if (dp2.equals(dp4, EPSILON)) {

        IDirectPositionList dpFinal = new DirectPositionList();

        IDirectPositionList dplTemp = (IDirectPositionList) dpl2.clone();
        dplTemp = dplTemp.reverse();
        // On vire le point 4 pour éviter les doublons
        dplTemp.remove(0);

        dpFinal.addAll(dpl1);
        dpFinal.addAll(dplTemp);

        if (!dpFinal.get(0).equals(dpFinal.get(dpFinal.size() - 1))) {

          dpFinal.add(dpFinal.get(0));

        }

        return new GM_Polygon(new GM_LineString(dpFinal));

      }

      IDirectPositionList dplTemp = (IDirectPositionList) dpl2.clone();

      // on vire l point 3
      dplTemp.remove(0);
      dplTemp = dplTemp.reverse();

      IDirectPositionList dpFinal = new DirectPositionList();
      dpFinal.addAll(dplTemp);
      dpFinal.addAll(dpl1);

      return new GM_LineString(dpFinal);
    }

    if (dp1.equals(dp4, EPSILON)) {

      if (dp2.equals(dp3, EPSILON)) {

        IDirectPositionList dpFinal = new DirectPositionList();

        IDirectPositionList dplTemp = (IDirectPositionList) dpl2.clone();
        // on vire le point 3
        dplTemp.remove(0);

        dpFinal.addAll(dpl1);
        dpFinal.addAll(dplTemp);

        if (!dpFinal.get(0).equals(dpFinal.get(dpFinal.size() - 1))) {

          dpFinal.add(dpFinal.get(0));

        }

        return new GM_Polygon(new GM_LineString(dpFinal));

      }

      IDirectPositionList dplTemp = (IDirectPositionList) dpl2.clone();

      // on vire l point 3
      dplTemp.remove(dplTemp.size() - 1);

      IDirectPositionList dpFinal = new DirectPositionList();
      dpFinal.addAll(dplTemp);
      dpFinal.addAll(dpl1);

      return new GM_LineString(dpFinal);
    }

    return null;
  }

}
