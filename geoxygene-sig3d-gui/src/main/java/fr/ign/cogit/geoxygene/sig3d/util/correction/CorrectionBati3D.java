package fr.ign.cogit.geoxygene.sig3d.util.correction;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.calculation.Proximity;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

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
public class CorrectionBati3D {

  /**
   * Pour l'instant se contente d'inverser les normales des toits (qui posent
   * problèmes)
   * 
   * @param feat
   */
  public static void correctionNormales(IFeatureCollection<IFeature> featColl) {

    int nbEl = featColl.size();

    for (int i = 0; i < nbEl; i++) {
      IGeometry geom = featColl.get(i).getGeom();

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
      Box3D b = new Box3D(geom);
      double zMin = b.getLLDP().getZ();
      double zMax = b.getURDP().getZ();

      for (int j = 0; j < nbOs; j++) {

        CorrectionBati3D.correctionNormales(lOS.get(j), zMin, zMax);

      }

    }

  }

  
  public static void correctionNormalesNoFloor(IFeatureCollection<IFeature> featColl) {

    int nbEl = featColl.size();

    for (int i = 0; i < nbEl; i++) {
      IGeometry geom = featColl.get(i).getGeom();

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

        CorrectionBati3D.correctionNormalesNoFloor(lOS.get(j));

      }

    }

  }
  
  
  public static IGeometry correctionNormalesNoFloor(IOrientableSurface geom) {

    Box3D b = new Box3D(geom);
    double zMin = b.getLLDP().getZ();
    double zMax = b.getURDP().getZ();

    Vecteur normal = new ApproximatedPlanEquation(geom).getNormale();

    normal.normalise();

    if (normal.getZ() < -0.1 && normal.getZ() > -0.2) {
      // Il faut inverser l'ordre des points

      GM_Polygon poly = (GM_Polygon) geom;
      Box3D bTemp = new Box3D(poly);

      double zMoy = bTemp.getCenter().getZ();

      if (Math.abs(zMoy - zMin) * 4 > Math.abs(zMoy - zMax)) {
        poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
            .inverseOrdrePoints(poly.getExterior().coord()))));
      }

      int nbInt = poly.getInterior().size();

      for (int k = 0; k < nbInt; k++) {

        poly.setInterior(
            k,
            new GM_Ring(new GM_LineString(CorrectionBati3D
                .inverseOrdrePoints(poly.getInterior(k).coord()))));
      }

    } else {

      // Il faut inverser l'ordre des points

      GM_Polygon poly = (GM_Polygon) geom;




      if ( normal.getZ() < 0) {

        poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
            .inverseOrdrePoints(poly.getExterior().coord()))));

        int nbInt = poly.getInterior().size();

        for (int k = 0; k < nbInt; k++) {

          poly.setInterior(
              k,
              new GM_Ring(new GM_LineString(CorrectionBati3D
                  .inverseOrdrePoints(poly.getInterior(k).coord()))));
        }

      }

    }

    return geom;

  }

  public static IGeometry correctionNormales(IOrientableSurface geom,
      double zMin, double zMax) {

    Vecteur normal = new ApproximatedPlanEquation(geom).getNormale();

    normal.normalise();

    if (normal.getZ() < -0.1) {
      // Il faut inverser l'ordre des points

      GM_Polygon poly = (GM_Polygon) geom;
      Box3D bTemp = new Box3D(poly);

      double zMoy = bTemp.getCenter().getZ();

      if (Math.abs(zMoy - zMin) * 4 > Math.abs(zMoy - zMax)) {
        poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
            .inverseOrdrePoints(poly.getExterior().coord()))));
      }

      int nbInt = poly.getInterior().size();

      for (int k = 0; k < nbInt; k++) {

        poly.setInterior(
            k,
            new GM_Ring(new GM_LineString(CorrectionBati3D
                .inverseOrdrePoints(poly.getInterior(k).coord()))));
      }

    } else if (normal.getZ() > 0.2) {

      // Il faut inverser l'ordre des points

      GM_Polygon poly = (GM_Polygon) geom;
      Box3D bTemp = new Box3D(poly);

      double zMoy = bTemp.getCenter().getZ();

      if (Math.abs(zMoy - zMin) < Math.abs(zMoy - zMax) * 4) {
        poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
            .inverseOrdrePoints(poly.getExterior().coord()))));
      }

      int nbInt = poly.getInterior().size();

      for (int k = 0; k < nbInt; k++) {

        poly.setInterior(
            k,
            new GM_Ring(new GM_LineString(CorrectionBati3D
                .inverseOrdrePoints(poly.getInterior(k).coord()))));
      }

    }

    return geom;

  }

  /**
   * 
   * @param gms
   * @return
   */
  public static IMultiSurface<IOrientableSurface> getHorizontalFace(
      IMultiSurface<IOrientableSurface> gms) {

    int nbSurf = gms.size();

    IMultiSurface<IOrientableSurface> finaleGOS = new GM_MultiSurface<IOrientableSurface>();

    for (int i = 0; i < nbSurf; i++) {

      IOrientableSurface gos = gms.get(i);

      ApproximatedPlanEquation eq = new ApproximatedPlanEquation(gos);

      Vecteur v = eq.getNormale();
      v.normalise();

      double d = Math.abs(v.prodScalaire(MathConstant.vectZ));

      if (d > 0.3) {

        finaleGOS.add(gos);

      }

    }

    return finaleGOS;

  }

  public static void close3DBatiCollWithDTM(
      IFeatureCollection<IFeature> featColl, DTM mnt) {

    int nbFeat = featColl.size();

    for (int i = 0; i < nbFeat; i++) {
      IFeature feat = featColl.get(i);
      IGeometry geom = feat.getGeom();

      if (geom instanceof GM_MultiSurface<?>) {

        IGeometry geeom = CorrectionBati3D.close3DBatiWithDTM(
            (IMultiSurface<IOrientableSurface>) geom, mnt);

        if (geeom == null) {

          i = i - 1;
          featColl.remove(i);
          nbFeat--;
          continue;
        }

        feat.setGeom(geeom);

      } else {

        System.out.println("Erreur");

      }

    }

  }

  public static IMultiSurface<IOrientableSurface> close3DBatiWithDTM(
      IMultiSurface<IOrientableSurface> gms, DTM mnt) {

    IMultiSurface<IOrientableSurface> horizontalFaces = CorrectionBati3D
        .getHorizontalFace(gms);

    int nbHorizon = horizontalFaces.size();

    if (nbHorizon == 0) {

      System.out.println("Je tiens un beau spécimen");

      return null;

    }

    for (int i = 0; i < nbHorizon; i++) {

      GM_Object geomIni = (GM_Object) horizontalFaces.get(i).clone();
      IDirectPositionList dpl = geomIni.coord();

      int nbP = dpl.size();

      IDirectPositionList dplTemporary = gms.coord();

      for (int j = 0; j < nbP; j++) {

        IDirectPosition dp = dpl.get(j);

        dp.setZ(0);// mnt.cast(dp).getZ()

        Proximity p = new Proximity();

        double z = p.nearest(dp, dplTemporary).getZ();

        if (z == 0) {

          double zMin = (new Box3D(dplTemporary)).getLLDP().getZ();
          System.out.println("zMinactu : " + zMin);

          dp.setZ(mnt.cast(dp).getZ());

        } else {

          dp.setZ(z);
        }

        if (geomIni instanceof GM_MultiSurface<?>) {

          GM_MultiSurface<GM_OrientableSurface> geomTemp = (GM_MultiSurface<GM_OrientableSurface>) geomIni;

          gms.addAll(geomTemp);
        } else if (geomIni instanceof GM_OrientableSurface) {

          gms.add((GM_OrientableSurface) geomIni);
        }

      }

    }

    // Test.featCollDEBUG.add(new DefaultFeature(geomIni));

    /*
     * for (int j = 0; j < nbTempo; j++) {
     * 
     * DirectPosition dpTemp = dplTemporary.get(j);
     * 
     * 
     * 
     * 
     * if (dpTemp.distance2D(dp) == 0 && dpTemp.distance(dp) < threshold) {
     * 
     * dp.setZ(dpTemp.getZ());
     * 
     * }
     * 
     * }
     * 
     * }
     */

    /*
     * if (geomIni instanceof GM_MultiSurface<?>) {
     * 
     * GM_MultiSurface<GM_OrientableSurface> geomTemp =
     * (GM_MultiSurface<GM_OrientableSurface>) geomIni;
     * 
     * int nbContrib = geomTemp.size();
     * 
     * for (int j = 0; j < nbContrib; j++) {
     * 
     * GM_OrientableSurface geo = geomTemp.get(j); GM_Polygon poly =
     * (GM_Polygon) geo; Vecteur v = (new
     * ApproximatedPlanEquation(poly)).getNormale();
     * 
     * if (v.prodScalaire(MathConstant.vectZ) < 0) {
     * 
     * poly.setExterior(new GM_Ring(new GM_LineString(
     * inverseOrdrePoints(poly.getExterior().coord()))));
     * 
     * int nbInt = poly.getInterior().size();
     * 
     * for (int k = 0; k < nbInt; k++) {
     * 
     * poly.setInterior(k, new GM_Ring(new GM_LineString(
     * inverseOrdrePoints(poly.getInterior(k).coord())))); }
     * 
     * }
     * 
     * geomTemp.add(poly);
     * 
     * }
     * 
     * gms.addAll(geomTemp);
     * 
     * } else if (geomIni instanceof GM_OrientableSurface) { GM_Polygon poly =
     * (GM_Polygon) geomIni; Vecteur v = (new
     * ApproximatedPlanEquation(poly)).getNormale();
     * 
     * if (v.prodScalaire(MathConstant.vectZ) < 0) {
     * 
     * poly.setExterior(new GM_Ring(new GM_LineString(inverseOrdrePoints(poly
     * .getExterior().coord()))));
     * 
     * int nbInt = poly.getInterior().size();
     * 
     * for (int k = 0; k < nbInt; k++) {
     * 
     * poly.setInterior(k, new GM_Ring(new GM_LineString(
     * inverseOrdrePoints(poly.getInterior(k).coord())))); }
     * 
     * }
     * 
     * gms.add(poly);
     * 
     * } else if (geomIni instanceof GM_Aggregate<?>) {
     * 
     * GM_Aggregate<?> geomTemp = (GM_Aggregate<?>) geomIni;
     * 
     * int nbContrib = geomTemp.size();
     * 
     * for (int j = 0; j < nbContrib; j++) {
     * 
     * GM_Object geomTest = geomTemp.get(j);
     * 
     * if (!(geomTest instanceof GM_OrientableSurface)) { continue; }
     * 
     * GM_OrientableSurface geo = (GM_OrientableSurface) geomTemp.get(j);
     * 
     * GM_Polygon poly = (GM_Polygon) geo; Vecteur v = (new
     * ApproximatedPlanEquation(poly)).getNormale();
     * 
     * if (v.prodScalaire(MathConstant.vectZ) < 0) {
     * 
     * poly.setExterior(new GM_Ring(new GM_LineString(
     * inverseOrdrePoints(poly.getExterior().coord()))));
     * 
     * int nbInt = poly.getInterior().size();
     * 
     * for (int k = 0; k < nbInt; k++) {
     * 
     * poly.setInterior(k, new GM_Ring(new GM_LineString(
     * inverseOrdrePoints(poly.getInterior(k).coord())))); }
     * 
     * }
     * 
     * gms.add(poly);
     * 
     * }
     * 
     * }else{
     * 
     * System.out.println("Classe inconuue"); }
     */

    return gms;

  }

  public static ISolid close3DBati(IMultiSurface<IOrientableSurface> gms) {

    IMultiSurface<IOrientableSurface> horizontalFaces = CorrectionBati3D
        .getHorizontalFace(gms);

    int nbHorizon = horizontalFaces.size();

    if (nbHorizon == 0) {

      return new GM_Solid(gms);

    }

    IGeometry geomIni = horizontalFaces.get(0);

    for (int i = 1; i < nbHorizon; i++) {

      geomIni = geomIni.union(horizontalFaces.get(i));

    }

    IDirectPositionList dpl = geomIni.coord();

    int nbP = dpl.size();

    Box3D b = new Box3D(gms);

    double zTemp = b.getLLDP().getZ();

    Proximity p = new Proximity();

    IDirectPositionList dplTemporary = gms.coord();
    for (int i = 0; i < nbP; i++) {

      IDirectPosition dp = dpl.get(i);

      dp.setZ(zTemp);

      double zMin = p.nearest(dp, dplTemporary).getZ();

      dp.setZ(zMin);
    }

    if (geomIni instanceof GM_MultiSurface<?>) {

      GM_MultiSurface<GM_OrientableSurface> geomTemp = (GM_MultiSurface<GM_OrientableSurface>) geomIni;

      int nbContrib = geomTemp.size();

      for (int j = 0; j < nbContrib; j++) {

        GM_OrientableSurface geo = geomTemp.get(j);
        GM_Polygon poly = (GM_Polygon) geo;
        Vecteur v = (new ApproximatedPlanEquation(poly)).getNormale();

        if (v.prodScalaire(MathConstant.vectZ) < 0) {

          poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
              .inverseOrdrePoints(poly.getExterior().coord()))));

          int nbInt = poly.getInterior().size();

          for (int k = 0; k < nbInt; k++) {

            poly.setInterior(
                k,
                new GM_Ring(new GM_LineString(CorrectionBati3D
                    .inverseOrdrePoints(poly.getInterior(k).coord()))));
          }

        }

        geomTemp.add(poly);

      }

      gms.addAll(geomTemp);

    } else if (geomIni instanceof GM_OrientableSurface) {
      GM_Polygon poly = (GM_Polygon) geomIni;
      Vecteur v = (new ApproximatedPlanEquation(poly)).getNormale();

      if (v.prodScalaire(MathConstant.vectZ) < 0) {

        poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
            .inverseOrdrePoints(poly.getExterior().coord()))));

        int nbInt = poly.getInterior().size();

        for (int k = 0; k < nbInt; k++) {

          poly.setInterior(
              k,
              new GM_Ring(new GM_LineString(CorrectionBati3D
                  .inverseOrdrePoints(poly.getInterior(k).coord()))));
        }

      }

      gms.add(poly);

    } else if (geomIni instanceof GM_Aggregate<?>) {

      GM_Aggregate<?> geomTemp = (GM_Aggregate<?>) geomIni;

      int nbContrib = geomTemp.size();

      for (int j = 0; j < nbContrib; j++) {

        IGeometry geomTest = geomTemp.get(j);

        if (!(geomTest instanceof GM_OrientableSurface)) {
          continue;
        }

        GM_OrientableSurface geo = (GM_OrientableSurface) geomTemp.get(j);

        GM_Polygon poly = (GM_Polygon) geo;
        Vecteur v = (new ApproximatedPlanEquation(poly)).getNormale();

        if (v.prodScalaire(MathConstant.vectZ) < 0) {

          poly.setExterior(new GM_Ring(new GM_LineString(CorrectionBati3D
              .inverseOrdrePoints(poly.getExterior().coord()))));

          int nbInt = poly.getInterior().size();

          for (int k = 0; k < nbInt; k++) {

            poly.setInterior(
                k,
                new GM_Ring(new GM_LineString(CorrectionBati3D
                    .inverseOrdrePoints(poly.getInterior(k).coord()))));
          }

        }

        gms.add(poly);

      }

    }

    return new GM_Solid(gms);

  }

  public static DirectPositionList inverseOrdrePoints(IDirectPositionList dpl) {

    int nbP = dpl.size();

    DirectPositionList dplOut = new DirectPositionList();

    for (int i = 0; i < nbP; i++) {
      dplOut.add(dpl.get(nbP - (i + 1)));

    }
    return dplOut;
  }

}
