package fr.ign.cogit.geoxygene.spatialrelation.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
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
  private static final double MIN_DIST = 25.0;
  private IFeature member1, member2;
  private Double orientation = 0.0;
  private DistanceBuildingRoad distance;
  private BuildingAlongRoadAchievement achievementOp;

  private ThresholdExpression expression;

  // ***************
  // CONSTRUCTOR
  // ***************
  public BuildingAlongARoad(IFeature memberToSet1, IFeature memberToSet2) {
    // create the attribute schema

    SchemaDefaultFeature schemaBR = new SchemaDefaultFeature();
    Map<Integer, String[]> lookup = new HashMap<Integer, String[]>();
    String[] member1Attr = { "member1", "member1" };
    String[] member2Attr = { "member2", "member2" };
    String[] distAttr = { "distance", "distance" };
    String[] orientationAttr = { "orientation", "orientation" };
    lookup.put(0, member1Attr);
    lookup.put(1, member2Attr);
    lookup.put(2, distAttr);
    lookup.put(3, orientationAttr);

    schemaBR.setAttLookup(lookup);
    this.setSchema(schemaBR);

    this.member1 = memberToSet1;
    this.member2 = memberToSet2;
    this.distance = new DistanceBuildingRoad();
    this.expression = new ThresholdExpression(MIN_DIST, SimpleOperator.EQ_INF,
        new DoubleComparator());
    this.achievementOp = new BuildingAlongRoadAchievement(memberToSet1,
        memberToSet2.getFeatureCollection(0), this);
    this.setGeom(createGeom(memberToSet1, this.member2));
    // Double orient =
    // this.achievementOp.checkOrientation(this.member1,this.member2);
    this.setOrientation(0.0);
    Object[] attributes = { this.member1.getId(), this.member2.getId(),
        this.distance.getValue(), this.orientation };
    this.setAttributes(attributes);

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
    return member2;
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

  public void setOrientation(Double orientation) {
    this.orientation = orientation;
  }

  // ***************
  // METHODS
  // ***************

  public ILineString createGeom(IFeature member1, IFeature member2) {

    // Get the member geometry
    IGeometry buildingGeom = member1.getGeom();
    IGeometry roadGeom = member2.getGeom();

    // Get the closest points
    IDirectPositionList closestPoints = JtsAlgorithms.getClosestPoints(
        roadGeom, buildingGeom);

    // Create a geometry
    ILineString geomRelation = (ILineString) new GM_LineString(closestPoints);

    return geomRelation;
  }

}
