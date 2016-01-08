/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.PredicateSpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationExpression;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.api.SimpleOperator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.DoubleComparator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.ThresholdExpression;
import fr.ign.cogit.geoxygene.spatialrelation.operations.Proximity2BuildingsAchievement;
import fr.ign.cogit.geoxygene.spatialrelation.properties.Distance2PolygonFeaturesProp;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class ProximityBetweenBuildings extends DefaultFeature implements
    BinarySpatialRelation, PredicateSpatialRelation {

  /**
   * The two network sections in relation
   */
  private IFeature member1, member2;
  private double minDist;
  private Proximity2BuildingsAchievement condOfAchievement;
  private Distance2PolygonFeaturesProp distProperty;

  private ThresholdExpression expression;

  public ProximityBetweenBuildings(IFeature member1, IFeature member2,
      double minDist) {
    super();
    this.member1 = member1;
    this.member2 = member2;
    this.minDist = minDist;
    this.expression = new ThresholdExpression(this.minDist,
        SimpleOperator.EQ_INF, new DoubleComparator());
    this.distProperty = new Distance2PolygonFeaturesProp(this);
    this.condOfAchievement = new Proximity2BuildingsAchievement(member1,
        member2, this);
    computeGeometry();
  }

  @Override
  public List<IFeature> getMembers() {
    List<IFeature> members = new ArrayList<IFeature>();
    members.add(member1);
    members.add(member2);
    return members;
  }

  @Override
  public Set<RelationProperty> getProperties() {
    Set<RelationProperty> properties = new HashSet<>();
    properties.add(distProperty);
    return properties;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public RelationExpression getConditionOfRelevance() {
    return null;
  }

  @Override
  public ThresholdExpression getConditionOfAchievement() {
    return expression;
  }

  @Override
  public AchievementMeasureOperation achievementAssessedBy() {
    return condOfAchievement;
  }

  @Override
  public IFeature getMember1() {
    return member1;
  }

  @Override
  public IFeature getMember2() {
    return member2;
  }

  public Distance2PolygonFeaturesProp getDistProperty() {
    return distProperty;
  }

  public void setDistProperty(Distance2PolygonFeaturesProp distProperty) {
    this.distProperty = distProperty;
  }

  private void computeGeometry() {
    GeometryProximity proxi = new GeometryProximity(
        this.getMember1().getGeom(), this.getMember2().getGeom());
    if (this.getMember1().getGeom().intersects(this.getMember2().getGeom()))
      this.setGeom(proxi.toOverlapSegment());
    else
      this.setGeom(proxi.toSegment());
  }
}
