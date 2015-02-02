package fr.ign.cogit.geoxygene.sig3d.util.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.index.Tiling;

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
public class AggregationSurfaces {

  /**
   * Algorithme permettant de fusionner les objets le long d'une ligne
   * @param featCollIn
   * @param curve
   * @return
   */
  public static IFeatureCollection<IFeature> aggregateToLineString(
      IFeatureCollection<IFeature> featCollIn,
      IMultiCurve<IOrientableCurve> curve, boolean is3D) {

    // Le résultat que l'on renvoie

    // Nous avons besoin des toits pour l'indexation spatiale
    IFeatureCollection<IFeature> featsRoof = null;

    if (is3D) {

      featsRoof = RoofDetection.detectRoof(featCollIn, 0.20, true);

      for (int i = 0; i < featsRoof.size(); i++) {

        IGeometry geom = featsRoof.get(i).getGeom();

        // Pas de toit, on ignore
        if (geom == null || geom.isEmpty()) {
          System.out.println("Pas de toit");
          featsRoof.remove(i);
          featCollIn.remove(i);

          i--;
          continue;

        }

        featsRoof.get(i).setGeom((new GM_MultiPoint(geom.coord())).buffer(1));

      }

    } else {
      featsRoof = new FT_FeatureCollection<IFeature>();
      featsRoof.addAll(featCollIn);
    }

    featsRoof.initSpatialIndex(Tiling.class, false);

    // On récupère les éléments de toit ssur la ligne
    Collection<IFeature> featRoofConcerned = featsRoof
        .select(curve.buffer(0.5));// getIntersected(curve.buffer(0.7),featsRoof);

    // On ajoute les éléments pour former une collection
    IFeatureCollection<IFeature> featIntersected = new FT_FeatureCollection<IFeature>();

    IFeatureCollection<IFeature> featRoofIntersected = new FT_FeatureCollection<IFeature>();
    featRoofIntersected.addAll(featRoofConcerned);

    for (IFeature featToTreet : featRoofConcerned) {

      featIntersected.add(featCollIn.get(featsRoof.getElements().indexOf(
          featToTreet)));

    }

    for (IFeature featTemp : featIntersected) {

      featCollIn.remove(featTemp);

    }

    // On regarde quels éléments doivent fusionner entre eux
    boolean fus = true;

    boucleWhile: while (fus) {

      int nbConcerned = featRoofIntersected.size();

      for (int i = 0; i < nbConcerned; i++) {

        IFeature feat1 = featRoofIntersected.get(i);

        for (int j = i + 1; j < nbConcerned; j++) {
          IFeature feat2 = featRoofIntersected.get(j);

          if (feat1.getGeom().intersects(feat2.getGeom())) {

            IMultiSurface<IOrientableSurface> mS = tryFusion(featIntersected
                .get(i).getGeom(), featIntersected.get(j).getGeom());

            IFeature featI = featIntersected.get(i);

            featIntersected.remove(j);
            featIntersected.remove(i);

            featRoofIntersected.remove(j);
            featRoofIntersected.remove(i);
            featI.setGeom(mS);
            featIntersected.add(featI);
            featRoofIntersected.add(new DefaultFeature((new GM_MultiPoint(
                RoofDetection.detectRoof(mS, 0.2, true).coord())).buffer(0.5)));

            continue boucleWhile;

          }

        }

      }

      fus = false;
    }

    featCollIn.addAll(featIntersected);
    return featCollIn;

  }

  public static IFeatureCollection<IFeature> getIntersected(IGeometry geom,
      IFeatureCollection<IFeature> featToTreat) {

    IFeatureCollection<IFeature> featCollOutCollection = new FT_FeatureCollection<IFeature>();

    for (IFeature feat : featToTreat) {

      if (isIntersected(geom, feat)) {
        featCollOutCollection.add(feat);
      }

    }

    return featCollOutCollection;

  }

  public static boolean isIntersected(IGeometry geom, IFeature feat) {

    IGeometry geom2 = feat.getGeom();

    List<IOrientableSurface> iOS = new ArrayList<IOrientableSurface>();

    if (geom2 instanceof IMultiSurface<?>) {
      iOS.addAll((GM_MultiSurface<IOrientableSurface>) geom2);
    } else if (geom2 instanceof IOrientableSurface) {
      iOS.add((IOrientableSurface) geom2);
    }

    for (IOrientableSurface surf : iOS) {

      if (surf.intersects(geom)) {
        return true;
      }

    }

    return false;
  }

  public static IMultiSurface<IOrientableSurface> tryFusion(IGeometry geom1,
      IGeometry geom2) {
    IMultiSurface<IOrientableSurface> mS = new GM_MultiSurface<IOrientableSurface>();

    if (geom1 instanceof IMultiSurface<?>) {

      mS.addAll((IMultiSurface<IOrientableSurface>) geom1);

    } else if (geom1 instanceof IPolygon) {

      mS.add((IPolygon) geom1);

    } else {
      System.out.println("Problem : geom1");
    }

    if (geom2 instanceof IMultiSurface<?>) {

      mS.addAll((IMultiSurface<IOrientableSurface>) geom2);

    } else if (geom2 instanceof IPolygon) {

      mS.add((IPolygon) geom2);

    } else {
      System.out.println("Problem : geom2");
    }

    return mS;
  }

}
