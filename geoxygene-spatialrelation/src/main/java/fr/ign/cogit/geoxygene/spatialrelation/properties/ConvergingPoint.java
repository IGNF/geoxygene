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

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.api.SpatialRelation;

public class ConvergingPoint implements RelationProperty {

  private boolean converging = true;
  private IDirectPosition position;
  private SpatialRelation relation;

  public ConvergingPoint(boolean converging, IDirectPosition position,
      SpatialRelation relation) {
    super();
    this.converging = converging;
    this.position = position;
    this.relation = relation;
  }

  public boolean isConverging() {
    if (converging)
      return true;
    return false;
  }

  public boolean isDiverging() {
    if (!converging)
      return true;
    return false;
  }

  public IDirectPosition getPosition() {
    return position;
  }

  public void setPosition(IDirectPosition position) {
    this.position = position;
  }

  public SpatialRelation getRelation() {
    return relation;
  }

  public void setRelation(SpatialRelation relation) {
    this.relation = relation;
  }

  @Override
  public Object getValue() {
    Vector<Object> vect = new Vector<Object>(2);
    vect.add(converging);
    vect.add(position);
    return vect;
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
