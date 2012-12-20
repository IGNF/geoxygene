/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.admin;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminCapital;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.schemageo.impl.administratif.ChefLieuImpl;

/*
 * ###### IGN / CartAGen ###### Title: SimpleAdminUnit Description: Unités
 * administratives élémentaires Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class AdminCapital extends GeneObjPointDefault implements IAdminCapital {

  /**
   * Associated Geoxygene schema object
   */
  private ChefLieu geoxObj;
  private String name;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public AdminCapital() {
    super();
  }

  /**
   * Constructor
   */
  public AdminCapital(ChefLieu geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.name = geoxObj.getNom();
  }

  public AdminCapital(IPoint point) {
    super();
    this.geoxObj = new ChefLieuImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  /**
   * @return The name of the Capital
   */
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
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

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new ChefLieuImpl();
    this.geoxObj.setNom(this.name);
    this.geoxObj.setGeom(this.getGeom());
  }

}
