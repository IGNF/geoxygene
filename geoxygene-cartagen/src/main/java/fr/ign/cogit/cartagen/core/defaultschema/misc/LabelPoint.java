/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.misc;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.activite.PointRepresentatifActiviteInteret;
import fr.ign.cogit.geoxygene.schemageo.impl.activite.PointRepresentatifActiviteInteretImpl;

/*
 * ###### IGN / CartAGen ###### Title: LabelPoint Description: Points
 * représentatifs d'activité et d'intérêt Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class LabelPoint extends GeneObjPointDefault implements ILabelPoint {

  /**
   * Associated Geoxygene schema object
   */
  private PointRepresentatifActiviteInteret geoxObj;

  /**
   * Constructor
   */
  public LabelPoint(PointRepresentatifActiviteInteret geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public LabelPoint(IPoint point) {
    super();
    this.geoxObj = new PointRepresentatifActiviteInteretImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public LabelPoint() {
    super();
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
