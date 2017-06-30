package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

public class TreeAlongRoad extends LoDSpatialRelationDetection {

  private double minDist, treeWidth, roadWidth;
  private Set<LoDSpatialRelation> rightInstances, leftInstances;

  public TreeAlongRoad(IFeatureCollection<IFeature> features1,
      IFeatureCollection<IFeature> features2, int lodDiffThreshold,
      double minDist, double treeWidth, double roadWidth) {
    super(features1, features2, lodDiffThreshold);
    this.minDist = minDist;
    this.treeWidth = treeWidth;
    this.roadWidth = roadWidth;
    rightInstances = new HashSet<LoDSpatialRelation>();
    leftInstances = new HashSet<LoDSpatialRelation>();
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // initialisation
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    RobustELECTRETRIMethod electrePt = LoDMultiCriteria
        .buildELECTRETRIMethodForPts();
    ConclusionIntervals conclusion = LoDMultiCriteria
        .initConclusion(electre.getCriteria());
    ConclusionIntervals conclusionPt = LoDMultiCriteria
        .initConclusion(electrePt.getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();

    for (IFeature road : getFeatures1()) {

      // get the LoD category for the road
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria
            .initParameters((OSMFeature) road, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(
          electre.decision(electre.getCriteria(), valeursCourantes, conclusion)
              .getCategory());

      // widthBuffer is the minDist plus the theoretical width of a tree
      // if minDist is smaller than road symbol width, symbol width is used
      double widthBuffer = roadWidth + treeWidth;
      // create a half buffer of the road on its right side with widthBuffer
      IPolygon rightBuffer = BufferComputing.buildHalfOffsetBuffer(Side.RIGHT,
          (ILineString) road.getGeom(), widthBuffer);
      // create a half buffer of the road on its left side with widthBuffer
      IPolygon leftBuffer = BufferComputing.buildHalfOffsetBuffer(Side.LEFT,
          (ILineString) road.getGeom(), widthBuffer);

      for (IFeature tree : getFeatures2()) {
        // search for trees close to the buffer
        if (rightBuffer != null
            && rightBuffer.intersects(GeometryFactory.buildCircle(
                ((IPoint) tree.getGeom()).getPosition(), treeWidth, 12))) {

          // get the LoD category for the tree
          Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
          for (Criterion crit : electrePt.getCriteria()) {
            Map<String, Object> param = LoDMultiCriteria
                .initParameters((OSMFeature) tree, crit);
            valeursCourantes2.put(crit.getName(),
                new Double(crit.value(param)));
          }
          LoDCategory category2 = LoDCategory
              .valueOf(electrePt.decision(electrePt.getCriteria(),
                  valeursCourantes2, conclusionPt).getCategory());

          // analyse LoD category difference
          if (Math.abs(category1.ordinal() - category2.ordinal()) < this
              .getLodDiffThreshold())
            continue;

          // add the new instance
          rightInstances.add(new LoDSpatialRelation(road, tree, category1,
              category2, getName()));
        }

        // search for trees close to the buffer
        if (leftBuffer != null
            && leftBuffer.intersects(GeometryFactory.buildCircle(
                ((IPoint) tree.getGeom()).getPosition(), treeWidth, 12))) {

          // get the LoD category for the tree
          Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
          for (Criterion crit : electre.getCriteria()) {
            Map<String, Object> param = LoDMultiCriteria
                .initParameters((OSMFeature) tree, crit);
            valeursCourantes2.put(crit.getName(),
                new Double(crit.value(param)));
          }
          LoDCategory category2 = LoDCategory.valueOf(electre
              .decision(electre.getCriteria(), valeursCourantes2, conclusion)
              .getCategory());

          // analyse LoD category difference
          if (Math.abs(category1.ordinal() - category2.ordinal()) < this
              .getLodDiffThreshold())
            continue;

          // add the new instance
          leftInstances.add(new LoDSpatialRelation(road, tree, category1,
              category2, getName()));
        }
      }
    }

    instances.addAll(rightInstances);
    instances.addAll(leftInstances);
    return instances;
  }

  @Override
  public String getName() {
    return TreeAlongRoad.class.getSimpleName();
  }

  public double getMinDist() {
    return minDist;
  }

  public void setMinDist(double minDist) {
    this.minDist = minDist;
  }

  public Set<LoDSpatialRelation> getRightInstances() {
    return rightInstances;
  }

  public void setRightInstances(Set<LoDSpatialRelation> rightInstances) {
    this.rightInstances = rightInstances;
  }

  public Set<LoDSpatialRelation> getLeftInstances() {
    return leftInstances;
  }

  public void setLeftInstances(Set<LoDSpatialRelation> leftInstances) {
    this.leftInstances = leftInstances;
  }

}
