package fr.ign.cogit.geoxygene.sig3d.util.correction;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

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
public class NormalCorrectionNonTriangulated {

  public static IFeatureCollection<? extends IFeature> correct(
      IFeatureCollection<IFeature> featColl) throws Exception {

    IFeatureCollection<IFeature> featC = new FT_FeatureCollection<IFeature>();
    int nbElem = featColl.size();

    for (int i = 0; i < nbElem; i++) {

      try {
        IFeature feat = (IFeature) featColl.get(i).cloneGeom();

        correct(feat.getGeom());

        featC.add(feat);

      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return featC;
  }

  public static void correct(IGeometry geom) throws Exception {

    IMultiSurface<IOrientableSurface> iMS = FromGeomToSurface
        .convertMSGeom(geom);

    IMultiSurface<IOrientableSurface> iMSHor = Util.detectRoof(iMS.getList(),
        0.2);

    // On inverse si le le z de la normale est négative (pour les toits)

    for (IOrientableSurface os : iMSHor) {

      IPolygon p = (IPolygon) os;
      ApproximatedPlanEquation ap = new ApproximatedPlanEquation(p);

      Vecteur v = ap.getNormale();
      v.normalise();

      if (v.getZ() < 0) {

        reverse(p);

      }

    }

    IMultiSurface<IOrientableSurface> iMSVert = Util.detectVertical(iMS.getList(), 0.2);

    for (IOrientableSurface os : iMSVert) {

      IPolygon p = (IPolygon) os;
      ApproximatedPlanEquation ap = new ApproximatedPlanEquation(p);

      IDirectPosition dp = Util.centerOf(p.coord());

      Vecteur v = ap.getNormale();
      v.normalise();
      v = v.multConstante(0.1);

      boolean intersectsRoof = intersectRoof(iMSHor, dp, v);

      if (intersectsRoof) {

        reverse(p);
      }

    }
     

  }

  private static boolean intersectRoof(
      IMultiSurface<IOrientableSurface> iMSHor, IDirectPosition dp, Vecteur v) {

    IDirectPositionList dpl = new DirectPositionList();
    dpl.add(dp);
    dpl.add(v.translate(dp));

    ILineString ls = new GM_LineString(dpl);

    for (IOrientableSurface os : iMSHor) {

      if (ls.intersects(os.buffer(0.1))) {
        return true;
      }

    }
    
    

    return false;
  }

  private static void reverse(IPolygon p) throws Exception {

    IRing r = p.getExterior();
    p.setExterior(reverse(r));

    int nbInt = p.getInterior().size();

    for (int i = 0; i < nbInt; i++) {

      p.setInterior(i, reverse(p.getInterior(i)));
    }

  }

  private static IRing reverse(IRing r) throws Exception {

    IDirectPositionList dpl = r.coord();
    dpl = dpl.reverse();

    return new GM_Ring(new GM_LineString(dpl), 0);

  }

}
