package fr.ign.cogit.geoxygene.sig3d.analysis.roof;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
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
 * @version 1.7
 * 
 * */
public class RoofDetection {

  public static IFeatureCollection<IFeature> detectRoof(
      IFeatureCollection<IFeature> featColl, double threshold,
      boolean facesWellOriented) {
    int nbElem = featColl.size();

    IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < nbElem; i++) {
      IFeature feat = featColl.get(i);

      featCollOut.add(new DefaultFeature(detectRoof(feat, threshold,
          facesWellOriented)));

    }

    return featCollOut;

  }

  public static IMultiSurface<? extends IOrientableSurface> detectRoof(
      IFeature feat, double threshold, boolean facesWellOriented) {

    IGeometry geom = feat.getGeom();

    return detectRoof(geom, threshold, facesWellOriented);
  }

  @SuppressWarnings("unchecked")
  public static IMultiSurface<Triangle> detectRoofTriangle(List<Triangle> lTri,
      double threshold, boolean facesWellOriented) {

    return (IMultiSurface<Triangle>) detectRoof(new GM_MultiSurface<Triangle>(
        lTri), threshold, facesWellOriented);
  }

  public static IMultiSurface<? extends IOrientableSurface> detectRoof(
      IGeometry geom, double threshold, boolean facesWellOriented) {

    List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(geom);

    if (lOS.size() == 0) {
      return null;
    }

    IMultiSurface<IOrientableSurface> lOSOut = Util.detectRoof(lOS, threshold);

    if (facesWellOriented) {
      return lOSOut;
    }

    if (lOSOut == null) {
      return new GM_MultiSurface<>();
    }

    Box3D b = new Box3D(geom);

    double zmin = b.getLLDP().getZ();
    double zmax = b.getURDP().getZ();
    double zcut = (7.0 / 8.0) * zmin + (1.0 / 8.0) * zmax;
    // System.out.println("zmin : " + zmin);
    // System.out.println("zmax : " + zmax);
    // System.out.println("zcut : " + zcut);

    int nbl = lOSOut.size();

    for (int j = 0; j < nbl; j++) {
      IOrientableSurface os = lOSOut.get(j);

      Box3D bt = new Box3D(os);
      /*
       * if (zMoy < zcut) {
       * 
       * lOSOut.remove(j);
       * 
       * j--; nbl--; continue; }
       */

      if (bt.getLLDP().getZ() < zcut) {

        lOSOut.remove(j);

        j--;
        nbl--;
        continue;
      }

      if (!os.isValid()) {

        lOSOut.remove(j);

        j--;
        nbl--;
        continue;
      }
    }

    return lOSOut;
  }
}
