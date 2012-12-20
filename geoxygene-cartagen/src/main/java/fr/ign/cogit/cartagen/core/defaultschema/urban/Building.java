/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.urban;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.Meso;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;

/*
 * ###### IGN / CartAGen ###### Title: Building Description: Batiments Author:
 * J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class Building extends UrbanElement implements IBuilding {

  /**
   * Associated Geoxygene schema object
   */
  private Batiment geoxObj;
  private String nature = "Indifferencie"; //$NON-NLS-1$

  /**
   * Constructor
   */
  public Building(Batiment geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom((IGeometry) geoxObj.getGeom().clone());
    this.setEliminated(false);
  }

  public Building(IPolygon poly) {
    super();
    this.geoxObj = new BatimentImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  public Building(IPolygon poly, String nature) {
    super();
    this.geoxObj = new BatimentImpl(poly);
    this.setNature(nature);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public Building() {
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
    return (IPolygon) super.getGeom();
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

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new BatimentImpl(this.getGeom());
  }

  @Override
  public void restoreGeoxRelations() {
    Batiment geox = (Batiment) this.getGeoxObj();
    if (this.getBlock() != null) {
      Meso meso = (Meso) this.getBlock().getGeoxObj();
      geox.setMeso(meso);
      meso.getComposants().add(geox);
    }
  }

  @Override
  public String getNature() {
    return nature;
  }

  @Override
  public void setNature(String nature) {
    this.nature = nature;
  }

}
