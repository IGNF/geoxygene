/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.partition;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;

/*
 * ###### IGN / CartAGen ###### Title: RoadLine Description: Tronçons de route
 * Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class Mask extends NetworkSection implements IMask {

  /**
   * Associated Geoxygene schema object
   */
  private DefaultFeature geoxObj;

  /**
   * Constructor
   */
  public Mask(ILineString geoxObj) {
    super();
    this.geoxObj = new DefaultFeature(geoxObj);
    this.setInitialGeom(geoxObj);
    this.setEliminated(false);
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public Mask() {
    super();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public Direction getDirection() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @Transient
  public INetworkNode getFinalNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @Transient
  public INetworkNode getInitialNode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @Transient
  public double getInternWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  @Transient
  public double getWidth() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  @Transient
  public boolean isDeadEnd() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setDirection(Direction direction) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setFinalNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setInitialNode(INetworkNode node) {
    // TODO Auto-generated method stub

  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return super.getGeom();
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  @Column(name = "CartAGenDB_name")
  public String getDbName() {
    return super.getDbName();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

}
