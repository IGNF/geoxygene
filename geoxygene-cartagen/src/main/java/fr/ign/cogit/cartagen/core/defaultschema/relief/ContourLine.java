/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.relief;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/*
 * ###### IGN / CartAGen ###### Title: ContourLine Description: Courbes de
 * niveau Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class ContourLine extends GeneObjLinDefault implements IContourLine {

  /**
   * Associated Geoxygene schema object
   */
  private CourbeDeNiveau geoxObj;

  /**
   * Constructor
   */
  public ContourLine(CourbeDeNiveau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public ContourLine(ILineString line, double value) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(), value, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
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

  private boolean isMaster = false;

  public void setMaster(boolean isMaster) {
    this.isMaster = isMaster;
  }

  @Override
  public boolean isMaster() {
    return this.isMaster;
  }

  @Override
  @Transient
  public double getWidth() {
    if (this.isMaster()) {
      return GeneralisationLegend.CN_LARGEUR_MAITRESSE;
    }
    return GeneralisationLegend.CN_LARGEUR_NORMALE;
  }

  @Override
  @Transient
  public IPolygon getSymbolExtent() {
    return (IPolygon) CommonAlgorithms.buffer(this.getGeom(), this.getWidth()
        * 0.5 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
  }

  @Override
  public double getAltitude() {
    return (this.geoxObj).getValeur();
  }

  @Override
  public void setAltitude(double z) {
    (this.geoxObj).setValeur(z);
  }

}
