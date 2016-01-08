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

import fr.ign.cogit.geoxygene.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.SpatialRelation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class Distance2PolygonsOp implements RelationOperation {

  private BinarySpatialRelation relation;

  public Distance2PolygonsOp(BinarySpatialRelation relation) {
    super();
    this.relation = relation;
  }

  @Override
  public Object compute() {
    GeometryProximity proxi = new GeometryProximity(relation.getMember1()
        .getGeom(), relation.getMember2().getGeom());
    double distMin = proxi.getDistance();
    if (relation.getMember1().getGeom()
        .intersects(relation.getMember2().getGeom()))
      distMin = -proxi.toOverlapSegment().length();
    return distMin;
  }

  @Override
  public SpatialRelation getRelation() {
    return relation;
  }

}
