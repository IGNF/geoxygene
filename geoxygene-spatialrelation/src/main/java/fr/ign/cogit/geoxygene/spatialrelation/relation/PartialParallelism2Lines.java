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
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.PredicateSpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationExpression;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.api.SimpleOperator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.DoubleComparator;
import fr.ign.cogit.geoxygene.spatialrelation.expressions.ThresholdExpression;
import fr.ign.cogit.geoxygene.spatialrelation.operations.PartialParallelismAchievement;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ConvergingPoint;
import fr.ign.cogit.geoxygene.spatialrelation.properties.DistanceInParallelPortions;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ParallelSection;

public class PartialParallelism2Lines implements BinarySpatialRelation,
    PredicateSpatialRelation {

  private static final double MIN_DIST = 15.0;
  private IFeature member1, member2;
  private List<ConvergingPoint> convergingPts;
  private List<ParallelSection> parallelSections;
  private DistanceInParallelPortions distance;
  private PartialParallelismAchievement achievementOp;
  private ThresholdExpression expression;

  public PartialParallelism2Lines(IFeature member1, IFeature member2) {
    this.member1 = member1;
    this.member2 = member2;
    this.achievementOp = new PartialParallelismAchievement(member1, member2,
        this);
    this.convergingPts = new ArrayList<ConvergingPoint>();
    this.parallelSections = new ArrayList<ParallelSection>();
    this.distance = new DistanceInParallelPortions();
    this.expression = new ThresholdExpression(MIN_DIST, SimpleOperator.EQ_INF,
        new DoubleComparator());
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
    Set<RelationProperty> properties = new HashSet<RelationProperty>();
    properties.addAll(convergingPts);
    properties.addAll(parallelSections);
    properties.add(distance);
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
  public IFeature getMember1() {
    return member1;
  }

  @Override
  public IFeature getMember2() {
    return member1;
  }

  public List<ConvergingPoint> getConvergencePts() {
    return convergingPts;
  }

  public void setConvergencePts(List<ConvergingPoint> convergencePts) {
    this.convergingPts = convergencePts;
  }

  public List<ParallelSection> getParallelSections() {
    return parallelSections;
  }

  public void setParallelSections(List<ParallelSection> parallelSections) {
    this.parallelSections = parallelSections;
  }

  public DistanceInParallelPortions getDistance() {
    return distance;
  }

  public void setDistance(DistanceInParallelPortions distance) {
    this.distance = distance;
  }

  @Override
  public ThresholdExpression getConditionOfAchievement() {
    return expression;
  }

  @Override
  public AchievementMeasureOperation achievementAssessedBy() {
    return achievementOp;
  }

}
