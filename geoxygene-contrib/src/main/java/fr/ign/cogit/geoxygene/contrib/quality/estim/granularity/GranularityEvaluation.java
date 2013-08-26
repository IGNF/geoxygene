package fr.ign.cogit.geoxygene.contrib.quality.estim.granularity;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class GranularityEvaluation {

  private IFeatureCollection<IFeature> jddIn;
  private double medianDistance;

  public GranularityEvaluation(IFeatureCollection<IFeature> ftColIn) {
    setJddIn(ftColIn);
  }

  /**
   * Compute the granularity of a feature collection by studying distances
   * between consecutive points
   * @author JFGirres
   */
  public void execute() {

    List<Double> listDistanceTotal = new ArrayList<Double>();

    for (IFeature feature : jddIn) {
      List<Double> listDistanceObjet = new ArrayList<Double>();
      if (feature.getGeom().isLineString()) {
        IDirectPositionList dplLs = new DirectPositionList();
        dplLs = feature.getGeom().coord();
        if (!(dplLs.size() < 2)) {
          for (int i = 1; i < dplLs.size(); i++) {
            IDirectPosition dp1 = dplLs.get(i);
            IDirectPosition dp2 = dplLs.get(i - 1);
            double distance = dp1.distance2D(dp2);
            if (!(distance < 1)) {
              listDistanceObjet.add(distance);
            }
          }
          listDistanceTotal.addAll(listDistanceObjet);
        }
      }
      if (feature.getGeom().isMultiCurve()) {
        @SuppressWarnings("unchecked")
        IMultiCurve<ILineString> multiLs = (IMultiCurve<ILineString>) feature
            .getGeom();
        for (ILineString ls : multiLs.getList()) {
          IDirectPositionList dplLs = new DirectPositionList();
          dplLs = ls.coord();
          if (!(dplLs.size() < 2)) {
            for (int i = 1; i < dplLs.size(); i++) {
              IDirectPosition dp1 = dplLs.get(i);
              IDirectPosition dp2 = dplLs.get(i - 1);
              double distance = dp1.distance2D(dp2);
              if (!(distance < 1)) {
                listDistanceObjet.add(distance);
              }
            }
            listDistanceTotal.addAll(listDistanceObjet);
          }
        }
      }

      if (feature.getGeom().isPolygon()) {
        IDirectPositionList dplPoly = new DirectPositionList();
        dplPoly = feature.getGeom().coord();
        if (!(dplPoly.size() < 2)) {
          for (int i = 1; i < dplPoly.size(); i++) {
            IDirectPosition dp1 = dplPoly.get(i);
            IDirectPosition dp2 = dplPoly.get(i - 1);
            double distance = dp1.distance2D(dp2);
            if (!(distance < 1)) {
              listDistanceObjet.add(distance);
            }
          }
          listDistanceTotal.addAll(listDistanceObjet);
        }
      }
      if (feature.getGeom().isMultiSurface()) {
        @SuppressWarnings("unchecked")
        IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) feature
            .getGeom();
        for (IPolygon poly : multiPoly.getList()) {
          IDirectPositionList dplPoly = new DirectPositionList();
          dplPoly = poly.coord();
          if (!(dplPoly.size() < 2)) {
            for (int i = 1; i < dplPoly.size(); i++) {
              IDirectPosition dp1 = dplPoly.get(i);
              IDirectPosition dp2 = dplPoly.get(i - 1);
              double distance = dp1.distance2D(dp2);
              if (!(distance < 1)) {
                listDistanceObjet.add(distance);
              }
            }
            listDistanceTotal.addAll(listDistanceObjet);
          }
        }
      }
    }
    setMedianDistance(MathUtil.mediane(listDistanceTotal));
  }

  public IFeatureCollection<IFeature> getJddIn() {
    return jddIn;
  }

  public void setJddIn(IFeatureCollection<IFeature> jddIn) {
    this.jddIn = jddIn;
  }

  public double getMedianDistance() {
    return medianDistance;
  }

  public void setMedianDistance(double medianDistance) {
    this.medianDistance = medianDistance;
  }

}
