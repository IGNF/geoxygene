/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.hydro;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.PointDEauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Points d'eau
 * Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class WaterPoint extends GeneObjPointDefault implements IWaterPoint {

  /**
   * Associated Geoxygene schema object
   */
  private PointDEau geoxObj;
  private String name = "";

  /**
   * Constructor
   */
  public WaterPoint(PointDEau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.name = geoxObj.getNom();
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public WaterPoint(IPoint point) {
    super();
    this.geoxObj = new PointDEauImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void restoreGeoxObjects() {
    IPoint geom = this.getGeom();
    this.geoxObj = new PointDEauImpl();
    this.geoxObj.setGeom(geom);
    this.geoxObj.setNom(this.name);
  }

}
