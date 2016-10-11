package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 **/
public class Cut3DGeomFrom2D {

  public static List<IOrientableSurface> cutCollectionFromPolygon(
      IFeatureCollection<IFeature> featColl, IPolygon polyCut) {

    List<IOrientableSurface> lOS = new ArrayList<>();

    for (IFeature feat : featColl) {

      lOS.addAll(cutFeatureFromPolygon(feat, polyCut));

    }

    return lOS;

  }

  public static List<IOrientableSurface> cutFeatureFromPolygon(IFeature feat,
      IPolygon polyCut) {
    List<IOrientableSurface> lS = new ArrayList<>();

    OrientedBoundingBox oBB = new OrientedBoundingBox(feat.getGeom());

    if (!oBB.getPoly().intersects(feat.getGeom())) {
      return lS;
    }

    return cutListSurfaceFromPolygon(
        FromGeomToSurface.convertGeom(feat.getGeom()), polyCut);

  }

  public static List<IOrientableSurface> cutListSurfaceFromPolygon(
      List<IOrientableSurface> lOS, IPolygon polyCut) {

    List<IOrientableSurface> lOSOut = new ArrayList<>();

    for (IOrientableSurface os : lOS) {

      lOSOut.addAll(cutSurfaceFromPolygon(os, polyCut));

    }

    return lOSOut;

  }

  public static List<IOrientableSurface> cutSurfaceFromPolygon(
      IOrientableSurface oS, IPolygon polyCut) {
    // System.out.println("oS : " + oS);
    List<ITriangle> lT = FromPolygonToTriangle.convertAndTriangle(oS);
    if (lT.get(0).isEmpty()){
      System.out.println("lT est vide");
    }

    return cutTriangleFromPolygon(lT, polyCut);

  }

  public static List<IOrientableSurface> cutTriangleFromPolygon(
      List<ITriangle> lT, IPolygon polyCut) {

    List<IOrientableSurface> lGeom = new ArrayList<>();

    List<ITriangle> verticalT = new ArrayList<>();
    List<ITriangle> horizontalT = new ArrayList<>();

    for (ITriangle t : lT) {

      if (isVertical(t)) {
        verticalT.add(t);
      } else {
        horizontalT.add(t);
      }

    }

    for (ITriangle t : horizontalT) {
      IOrientableSurface geomTemp = handleHorizontal(t, polyCut);

      if (geomTemp != null) {
        lGeom.add(geomTemp);
      }

    }

    List<ILineString> lSExtCut = new ArrayList<>();

    int nbSom = polyCut.getExterior().coord().size();

    for (int i = 0; i < nbSom - 1; i++) {

      IDirectPositionList dpl = new DirectPositionList();
      dpl.add(polyCut.getExterior().coord().get(i));
      dpl.add(polyCut.getExterior().coord().get(i + 1));

      lSExtCut.add(new GM_LineString(dpl));

    }

    for (ITriangle t : verticalT) {

      double distance1 = t.getCorners(0).getDirect()
          .distance2D(t.getCorners(1).getDirect());

      double distance2 = t.getCorners(2).getDirect()
          .distance2D(t.getCorners(1).getDirect());

      double distance3 = t.getCorners(2).getDirect()
          .distance2D(t.getCorners(0).getDirect());

      ILineString ls = null;

      if (distance1 > distance2 && distance1 > distance3) {

        IDirectPositionList dpl = new DirectPositionList();
        dpl.add(t.getCorners(0).getDirect());
        dpl.add(t.getCorners(1).getDirect());
        ls = new GM_LineString(dpl);
      }

      if (distance2 >= distance1 && distance2 > distance3) {
        IDirectPositionList dpl = new DirectPositionList();
        dpl.add(t.getCorners(2).getDirect());
        dpl.add(t.getCorners(1).getDirect());
        ls = new GM_LineString(dpl);
      }

      if (distance3 >= distance1 && distance3 >= distance2) {
        IDirectPositionList dpl = new DirectPositionList();
        dpl.add(t.getCorners(2).getDirect());
        dpl.add(t.getCorners(0).getDirect());
        ls = new GM_LineString(dpl);

      }

      if (ls == null) {
        System.out.println("ls null");
      }

      if (polyCut.contains(ls)) {
        lGeom.add(t);
        continue;
      }

      // on a un segment 2D représentatif

      for (ILineString lsTemp : lSExtCut) {
        if (!lsTemp.intersects(ls)) {
          continue;
        }

        IDirectPosition dp1 = lsTemp.coord().get(0);
        dp1.setZ(0);

        IDirectPosition dp2 = lsTemp.coord().get(1);
        dp2.setZ(0);

        IDirectPosition dp3 = (IDirectPosition) dp1.clone();
        dp3.setZ(1);

        PlanEquation ep = new PlanEquation(dp1, dp2, dp3);

        List<IGeometry> lOS = CutBuilding.cut(t, ep);

        for (IGeometry os : lOS) {
          if (os != null && !os.isEmpty()) {

            IDirectPositionList dpl = os.coord();

            dpl.add(t.getCorners(0).getDirect());
            dpl.add(t.getCorners(1).getDirect());
            dpl.add(t.getCorners(2).getDirect());

            int nbPos = dpl.size();

            for (int i = 0; i < nbPos; i++) {
              IPoint p = new GM_Point(dpl.get(i));

              if (!polyCut.buffer(0.2).contains(p)) {

                dpl.remove(i);
                i--;
                nbPos--;
              }

            }

            nbPos = dpl.size();

            if (nbPos < 2 || nbPos > 4) {
              System.out.println("What");
              continue;
            }

            if (nbPos == 3) {

              lGeom.add(new GM_Triangle(dpl.get(0), dpl.get(1), dpl.get(2)));
              continue;
            }

            if (nbPos == 4) {

              Vecteur v1 = new Vecteur(dpl.get(0), dpl.get(1));
              v1.normalise();
              Vecteur v2 = new Vecteur(dpl.get(1), dpl.get(2));
              v2.normalise();

              // if(v1.prodVectoriel(v2).norme() < 0.1){

              lGeom.add(new GM_Triangle(dpl.get(3), dpl.get(1), dpl.get(2)));
              lGeom.add(new GM_Triangle(dpl.get(3), dpl.get(0), dpl.get(2)));

              // }else{
              lGeom.add(new GM_Triangle(dpl.get(0), dpl.get(1), dpl.get(2)));
              lGeom.add(new GM_Triangle(dpl.get(3), dpl.get(2), dpl.get(0)));
              // }

              continue;

            }

            // lGeom.add(os);

          }
        }

      }

    }

    return lGeom;
  }

  private static IOrientableSurface handleHorizontal(ITriangle t, IPolygon poly) {

    IGeometry geom = t.intersection(poly);

    if (geom.isEmpty() || geom.area() == 0) {
      return null;
    }

    PlanEquation eQ = new PlanEquation(t);

    for (IDirectPosition dp : geom.coord()) {

      dp.setZ(eQ.getZ(dp));

    }

    return FromGeomToSurface.convertGeom(geom).get(0);

  }

  private static boolean isVertical(ITriangle t) {

    PlanEquation pE = new PlanEquation(t);

    Vecteur v = pE.getNormale().getNormalised();

    return Math.abs(v.getZ()) < 0.1;

  }

}
