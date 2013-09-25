package fr.ign.cogit.geoxygene.contrib.conflation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class RubberSheetingConflation {

  private IFeatureCollection<? extends IFeature> featsToConflate;
  private Set<ConflationVector> conflationVectors;
  private Map<IFeature, IGeometry> conflatedGeoms;
  private double distanceThreshold = 0.0005;

  public RubberSheetingConflation(
      IFeatureCollection<? extends IFeature> featsToConflate,
      Set<ConflationVector> conflationVectors) {
    super();
    this.featsToConflate = featsToConflate;
    this.conflationVectors = conflationVectors;
    this.conflatedGeoms = new HashMap<IFeature, IGeometry>();
  }

  public IFeatureCollection<? extends IFeature> getFeatsToConflate() {
    return featsToConflate;
  }

  public void setFeatsToConflate(
      IFeatureCollection<? extends IFeature> featsToConflate) {
    this.featsToConflate = featsToConflate;
  }

  public Set<ConflationVector> getConflationVectors() {
    return conflationVectors;
  }

  public void setConflationVectors(Set<ConflationVector> conflationVectors) {
    this.conflationVectors = conflationVectors;
  }

  public Map<IFeature, IGeometry> getConflatedGeoms() {
    return conflatedGeoms;
  }

  public void setConflatedGeoms(Map<IFeature, IGeometry> conflatedGeoms) {
    this.conflatedGeoms = conflatedGeoms;
  }

  public double getDistanceThreshold() {
    return distanceThreshold;
  }

  public void setDistanceThreshold(double distanceThreshold) {
    this.distanceThreshold = distanceThreshold;
  }

  /**
   * Trigger a rubber sheeting conflation on the features to conflate, according
   * to the conflation vectors.
   */
  public void conflation() {
    for (IFeature feat : this.featsToConflate) {
      IGeometry newGeom = null;
      if (feat.getGeom() instanceof IPoint)
        newGeom = conflatePoint((IPoint) feat.getGeom());
      else if (feat.getGeom() instanceof ILineString)
        newGeom = conflateLineString((ILineString) feat.getGeom());
      else if (feat.getGeom() instanceof IPolygon)
        newGeom = conflatePolygon((IPolygon) feat.getGeom());
      // fill the output map
      if (newGeom == null)
        this.conflatedGeoms.put(feat, feat.getGeom());
      else
        this.conflatedGeoms.put(feat, newGeom);
    }
  }

  private ILineString conflateLineString(ILineString geom) {
    IDirectPositionList newPtList = conflatePosList(geom.coord());
    return new GM_LineString(newPtList);
  }

  private IPoint conflatePoint(IPoint geom) {
    return new GM_Point(computeAggregatedVector(geom.getPosition()).translate(
        geom.getPosition()));
  }

  private IPolygon conflatePolygon(IPolygon geom) {
    // conflate the outer ring
    IDirectPositionList newOuterList = conflatePosList(geom.getExterior()
        .coord());
    IPolygon newPol = new GM_Polygon(new GM_LineString(newOuterList));

    // conflate inner rings
    for (IRing inner : geom.getInterior()) {
      IDirectPositionList newInnerList = conflatePosList(inner.coord());
      newPol.addInterior(new GM_Ring(new GM_LineString(newInnerList)));
    }
    return newPol;
  }

  /**
   * For a given list of coordinates from a geometry, the method derives a
   * conflated version of the point list.
   * @param ptList
   * @return
   */
  private IDirectPositionList conflatePosList(IDirectPositionList ptList) {
    IDirectPositionList newPtList = new DirectPositionList();
    for (IDirectPosition vertex : ptList) {
      Vector2D vect = computeAggregatedVector(vertex);
      if (vect == null)
        newPtList.add(vertex);
      else
        newPtList.add(vect.translate(vertex));
    }
    return newPtList;
  }

  /**
   * Compute the value of the rubber sheeting vector field at a given point.
   * @param point
   * @return
   */
  public Vector2D computeAggregatedVector(IDirectPosition point) {
    Vector2D vectFinal = null;
    double numerateurX = 0.0;
    double denominateur = 0.0;
    double numerateurY = 0.0;
    for (ConflationVector vect : conflationVectors) {

      // on calcule la distance entre le point et le vecteur de conflation
      // (ancré à un point).
      double dist = vect.getIniPos().distance2D(point);

      // on teste si le vecteur est assez près pour être pris en compte
      if (vect.getVector().norme() / (dist * dist) < distanceThreshold)
        continue;
      if (dist < 1.0)
        dist = 1.0;
      denominateur += 1 / (dist * dist);
      numerateurX += vect.getVector().getX() / (dist * dist);
      numerateurY += vect.getVector().getY() / (dist * dist);

    }
    // on rectifie la norme du vecteur agrégé
    if (denominateur == 0.0)
      return null;
    vectFinal = new Vector2D(numerateurX / denominateur, numerateurY
        / denominateur);
    return vectFinal;
  }
}
