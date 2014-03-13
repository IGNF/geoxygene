/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.api;

public interface PredicateSpatialRelation extends SpatialRelation {

  /**
   * Assessing the achievement of a binary relation means assessing if it holds.
   * The condition that specifies if the relation holds between two features.
   * 
   * @return
   */
  public AchievementExpression getConditionOfAchievement();

  public AchievementMeasureOperation achievementAssessedBy();
}
