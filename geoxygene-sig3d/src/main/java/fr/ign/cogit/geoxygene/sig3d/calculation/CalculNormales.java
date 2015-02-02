package fr.ign.cogit.geoxygene.sig3d.calculation;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
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
 * @author MBrasebin
 * 
 */
public class CalculNormales {

  public static IFeatureCollection<IFeature> getNormalFeature(
      IFeatureCollection<? extends IFeature> featColl, double size) {

    List<IOrientableCurve> lC = getNormal(featColl, size);
    int nbElem = lC.size();
    IFeatureCollection<IFeature> featC = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < nbElem; i++) {

      featC.add(new DefaultFeature(lC.get(i)));

    }

    return featC;

  }

  public static List<IOrientableCurve> getNormal(
      IFeatureCollection<? extends IFeature> featColl, double size) {
    int nbEl = featColl.size();

    List<IOrientableCurve> lLS = new ArrayList<IOrientableCurve>();

    for (int i = 0; i < nbEl; i++) {
      lLS.addAll(getNormal(featColl.get(i), size));

    }

    return lLS;
  }

  public static IFeature getNormalFeature(IFeature feat, double size) {

    IFeature featFin = new DefaultFeature(new GM_MultiCurve<IOrientableCurve>(
        getNormal(feat, size)));
    return featFin;

  }

  public static List<IOrientableCurve> getNormal(IFeature feat, double size) {
    List<IOrientableCurve> lLS = new ArrayList<IOrientableCurve>();

    IGeometry geom = feat.getGeom();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    if (geom instanceof GM_Solid) {

      lOS.addAll(((GM_Solid) geom).getFacesList());

    } else if (geom instanceof GM_OrientableSurface) {

      lOS.add((GM_OrientableSurface) geom);

    } else if (geom instanceof GM_MultiSurface<?>) {

      lOS.addAll((GM_MultiSurface<GM_OrientableSurface>) geom);

    } else {

      System.out.println("Autre classe : " + geom.getClass().getName());
    }

    int nbOs = lOS.size();

    for (int j = 0; j < nbOs; j++) {

      IOrientableSurface surf = lOS.get(j);
      Vecteur normal = new ApproximatedPlanEquation(surf).getNormale();

      if (surf.coord().size() < 4) {
        System.out.println("Problem");
      }

      DirectPositionList dpl = new DirectPositionList();
      IDirectPosition dpCentre = Util.centerOf(surf.coord());

      dpl.add(dpCentre);

      DirectPosition dpFin = new DirectPosition(dpCentre.getX() + normal.getX()
          * size, dpCentre.getY() + normal.getY() * size, dpCentre.getZ()
          + normal.getZ() * size

      );

      if (Double.isNaN(dpFin.getY())) {
        // System.out.println();
        continue;
      }
      dpl.add(dpFin);

      lLS.add(new GM_LineString(dpl));
    }

    return lLS;
  }

}
