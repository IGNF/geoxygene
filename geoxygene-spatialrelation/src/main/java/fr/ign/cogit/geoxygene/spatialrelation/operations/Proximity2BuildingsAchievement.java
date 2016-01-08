/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.operations;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.relation.ProximityBetweenBuildings;

public class Proximity2BuildingsAchievement implements
    AchievementMeasureOperation {

  private IFeature feature1, feature2;
  private boolean achievement = false;
  private ProximityBetweenBuildings relation;

  public Proximity2BuildingsAchievement(IFeature feature1, IFeature feature2,
      ProximityBetweenBuildings relation) {
    super();
    this.feature1 = feature1;
    this.feature2 = feature2;
    this.relation = relation;
  }

  /**
   * Loop on the feature1 vertices to identify where parallelism between both
   * features starts and where it ends.
   */
  @Override
  public Object compute() {
    if (!(getFeature1().getGeom() instanceof IPolygon))
      return null;
    if (!(getFeature2().getGeom() instanceof IPolygon))
      return null;
    RelationProperty distance = relation.getProperties().iterator().next();
    double dist = (Double) distance.getValue();
    if (relation.getConditionOfAchievement().validate(dist))
      achievement = true;
    return dist;
  }

  @Override
  public boolean measureAchievement() {
    return achievement;
  }

  public IFeature getFeature1() {
    return feature1;
  }

  public void setFeature1(IFeature feature1) {
    this.feature1 = feature1;
  }

  public IFeature getFeature2() {
    return feature2;
  }

  public void setFeature2(IFeature feature2) {
    this.feature2 = feature2;
  }

  public ProximityBetweenBuildings getRelation() {
    return relation;
  }

  public void setRelation(ProximityBetweenBuildings relation) {
    this.relation = relation;
  }

}
