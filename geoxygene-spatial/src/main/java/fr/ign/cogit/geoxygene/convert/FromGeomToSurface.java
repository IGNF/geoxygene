package fr.ign.cogit.geoxygene.convert;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

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
 * 
 */
public class FromGeomToSurface {

  public static List<IOrientableSurface> convertGeom(IGeometry geom) {

    return convertMSGeom(geom).getList();

  }

  @SuppressWarnings("unchecked")
  public static IMultiSurface<IOrientableSurface> convertMSGeom(IGeometry geom) {

    List<IOrientableSurface> lIOS = new ArrayList<IOrientableSurface>();

    if (geom instanceof ISolid) {

      lIOS.addAll(((ISolid) geom).getFacesList());

    } else if (geom instanceof IMultiSolid<?>) {
      for (ISolid sol : (IMultiSolid<ISolid>) geom) {
        lIOS.addAll((sol).getFacesList());
      }

    } else if (geom instanceof IOrientableSurface) {

      lIOS.add((IOrientableSurface) geom);

    } else if (geom instanceof IMultiSurface<?>) {

      lIOS.addAll((IMultiSurface<IOrientableSurface>) geom);
    } else if (geom instanceof IAggregate<?>) {
      IAggregate<?> agg = (IAggregate<?>) geom;

      for (IGeometry g : agg) {

        if (g instanceof IOrientableSurface) {
          lIOS.add((IOrientableSurface) g);
        }
      }

    }

    return new GM_MultiSurface<IOrientableSurface>(lIOS);

  }

}
