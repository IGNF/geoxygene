/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.properties;

import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.relation.PartialParallelism2Lines;

public class DistanceInParallelPortions implements RelationProperty {

  private double distance;
  private PartialParallelism2Lines relation;

  @Override
  public Object getValue() {
    return distance;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public RelationOperation getOperation() {
    // TODO Auto-generated method stub
    return null;
  }

  public PartialParallelism2Lines getRelation() {
    return relation;
  }

  public void setRelation(PartialParallelism2Lines relation) {
    this.relation = relation;
  }

}
