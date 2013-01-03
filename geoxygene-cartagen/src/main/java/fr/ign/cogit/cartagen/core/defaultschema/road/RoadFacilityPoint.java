/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadFacilityPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.routier.EquipementRoutier;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.EquipementRoutierImpl;

/*
 * ###### IGN / CartAGen ###### Title: RoadFacilityPoint Description:
 * Equipements routiers Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class RoadFacilityPoint extends GeneObjPointDefault implements
    IRoadFacilityPoint {

  /**
   * Associated Geoxygene schema object
   */
  private EquipementRoutier geoxObj;

  /**
   * Constructor
   */
  public RoadFacilityPoint(EquipementRoutier geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public RoadFacilityPoint(IPoint point) {
    super();
    this.geoxObj = new EquipementRoutierImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public RoadFacilityPoint() {
    super();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return super.getGeom();
  }

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
