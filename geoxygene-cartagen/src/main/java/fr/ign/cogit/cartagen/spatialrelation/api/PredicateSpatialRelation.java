package fr.ign.cogit.cartagen.spatialrelation.api;

public interface PredicateSpatialRelation extends SpatialRelation {

	/**
	 * Assessing the achievement of a binary relation means assessing if it
	 * holds. The condition that specifies if the relation holds between two
	 * features.
	 * 
	 * @return
	 */
	public AchievementExpression getConditionOfAchievement();

	public AchievementMeasureOperation achievementAssessedBy();
}
