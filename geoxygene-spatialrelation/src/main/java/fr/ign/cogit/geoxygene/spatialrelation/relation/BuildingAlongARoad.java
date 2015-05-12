package fr.ign.cogit.geoxygene.spatialrelation.relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.PredicateSpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationExpression;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.api.SimpleOperator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.DoubleComparator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.ThresholdExpression;
import fr.ign.cogit.geoxygene.spatialrelation.operations.BuildingAlongRoadAchievement;
import fr.ign.cogit.geoxygene.spatialrelation.properties.DistanceBuildingRoad;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

public class BuildingAlongARoad extends DefaultFeature implements
    BinarySpatialRelation, PredicateSpatialRelation {

  // ***************
  // PROPERTIES
  // ***************
  private static final double MIN_DIST = 15.0;
  private IFeature member1, member2;
  private DistanceBuildingRoad distance;
  private BuildingAlongRoadAchievement achievementOp;
  private ThresholdExpression expression;

  // ***************
  // CONSTRUCTOR
  // ***************
  public BuildingAlongARoad(IFeature member1, IFeature member2) {
    this.member1 = member1;
    this.member2 = member2;
    this.achievementOp = new BuildingAlongRoadAchievement(member1,
        member2.getFeatureCollection(0), this);
    this.distance = new DistanceBuildingRoad();
    this.expression = new ThresholdExpression(MIN_DIST, SimpleOperator.EQ_INF,
        new DoubleComparator());
    this.setGeom(createGeom(member1, member2));
  }

  // ***************
  // GETTERS
  // ***************
  @Override
  public List<IFeature> getMembers() {
    List<IFeature> members = new ArrayList<IFeature>();
    members.add(member1);
    members.add(member2);
    return members;
  }

  @Override
  public IFeature getMember1() {
    return member1;
  }

  @Override
  public IFeature getMember2() {
    return member1;
  }

  @Override
  public Set<RelationProperty> getProperties() {
    Set<RelationProperty> properties = new HashSet<RelationProperty>();
    properties.add(distance);
    return properties;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  public DistanceBuildingRoad getDistance() {
    return distance;
  }

  @Override
  public RelationExpression getConditionOfRelevance() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ThresholdExpression getConditionOfAchievement() {
    return expression;
  }

  @Override
  public AchievementMeasureOperation achievementAssessedBy() {
    return achievementOp;
  }

  // ***************
  // SETTERS
  // ***************

  public void setDistance(DistanceBuildingRoad distance) {
    this.distance = distance;
  }

  public void setMember2(IFeature road) {
    this.member2 = road;
  }

  // ***************
  // METHODS
  // ***************

  public ILineString createGeom(IFeature member1, IFeature member2) {
    List<IDirectPosition> pts = new ArrayList<IDirectPosition>();
    IDirectPosition buildingCentroid = member1.getGeom().centroid();
    pts.add(buildingCentroid);
    ILineString roadGeom = (ILineString) member2.getGeom();
    IDirectPosition closestRoadPoint = JtsAlgorithms.getClosestPoint(
        buildingCentroid, roadGeom);
    pts.add(closestRoadPoint);
    // TODO Create geom from pts
    return null;
  }
}
