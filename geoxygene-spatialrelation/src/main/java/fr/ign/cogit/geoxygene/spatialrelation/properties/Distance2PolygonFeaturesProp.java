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

import fr.ign.cogit.geoxygene.spatialrelation.api.BinarySpatialRelation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.operations.Distance2PolygonsOp;

/**
 * The distance between two polygon features, i.e. the minimum distance between
 * polygons if disjoint and the largest distance inside the intersection if
 * polygons intersect.
 * @author gtouya
 * 
 */
public class Distance2PolygonFeaturesProp implements RelationProperty {

  // ***************
  // PROPERTIES
  // ***************
  private double distance;
  private Distance2PolygonsOp operation;
  private BinarySpatialRelation relation;

  // ***************
  // CONSTRUCTOR
  // ***************
  public Distance2PolygonFeaturesProp(BinarySpatialRelation relation) {
    super();
    this.setRelation(relation);
    this.operation = new Distance2PolygonsOp(relation);
    this.getOperation().compute();
  }

  // ***************
  // GETTERS
  // ***************
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
    return operation;
  }

  // ***************
  // SETTERS
  // ***************

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public BinarySpatialRelation getRelation() {
    return relation;
  }

  public void setRelation(BinarySpatialRelation relation) {
    this.relation = relation;
  }

}
