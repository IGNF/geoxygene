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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.api.SpatialRelation;

public class ParallelSection implements RelationProperty {

  private ILineString member1Section, member2Section;
  private SpatialRelation relation;

  public ParallelSection(ILineString member1Section,
      ILineString member2Section, SpatialRelation relation) {
    super();
    this.member1Section = member1Section;
    this.member2Section = member2Section;
    this.relation = relation;
  }

  @Override
  public Object getValue() {
    ILineString[] value = new ILineString[2];
    value[0] = member1Section;
    value[1] = member2Section;
    return value;
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

}
