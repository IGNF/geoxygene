package fr.ign.cogit.cartagen.spatialrelation.relations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.cartagen.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.cartagen.spatialrelation.api.PredicateSpatialRelation;
import fr.ign.cogit.cartagen.spatialrelation.api.RelationExpression;
import fr.ign.cogit.cartagen.spatialrelation.api.RelationProperty;
import fr.ign.cogit.cartagen.spatialrelation.expressions.ThresholdExpression;
import fr.ign.cogit.cartagen.spatialrelation.operations.PartialParallelismAchievement;
import fr.ign.cogit.cartagen.spatialrelation.properties.ConvergingPoint;
import fr.ign.cogit.cartagen.spatialrelation.properties.DistanceInParallelPortions;
import fr.ign.cogit.cartagen.spatialrelation.properties.ParallelSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class PartialParallelism2Lines implements BinarySpatialRelation,
		PredicateSpatialRelation {

	private IFeature member1, member2;
	private List<ConvergingPoint> convergingPts;
	private List<ParallelSection> parallelSections;
	private DistanceInParallelPortions distance;
	private PartialParallelismAchievement achievementOp;
	private ThresholdExpression expression;

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
