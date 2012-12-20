/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.railway;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.railway.ITriageArea;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.AireTriage;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.AireTriageImpl;

/*
 * ###### IGN / CartAGen ###### Title: TriageArea Description: Aires de triage
 * Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class TriageArea extends GeneObjSurfDefault implements ITriageArea {

  /**
   * Associated Geoxygene schema object
   */
  private AireTriage geoxObj;

  /**
   * Constructor
   */
  public TriageArea(AireTriage geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public TriageArea(IPolygon poly) {
    super();
    this.geoxObj = new AireTriageImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public TriageArea() {
    super();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
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
