/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.hydro;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.PointDEauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Points d'eau
 * Author: J. Renard Date: 18/09/2009
 */
@Access(AccessType.PROPERTY)
public class VMAPWaterPoint extends GeneObjDefault implements IWaterPoint,
    VMAPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private PointDEau geoxObj;
  private String name = "";

  // the VMAP attributes
  private String fCode, nfi, nfn;
  private Integer acc, hyc, scc, swt;

  /**
   * Constructor
   */
  public VMAPWaterPoint(PointDEau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.name = geoxObj.getNom();
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public VMAPWaterPoint(IPoint point) {
    super();
    this.geoxObj = new PointDEauImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  /**
   * Cosntructor from springp class from VMAP2i model
   */
  public VMAPWaterPoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new PointDEauImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.name = (String) attributes.get("nam");
    this.acc = (Integer) attributes.get("acc");
    this.hyc = (Integer) attributes.get("hyc");
    this.swt = (Integer) attributes.get("swt");
    this.scc = (Integer) attributes.get("scc");
    this.fCode = (String) attributes.get("f_code");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
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
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public String getNfi() {
    return nfi;
  }

  public void setNfi(String nfi) {
    this.nfi = nfi;
  }

  public String getNfn() {
    return nfn;
  }

  public void setNfn(String nfn) {
    this.nfn = nfn;
  }

  public Integer getAcc() {
    return acc;
  }

  public void setAcc(Integer acc) {
    this.acc = acc;
  }

  public Integer getHyc() {
    return hyc;
  }

  public void setHyc(Integer hyc) {
    this.hyc = hyc;
  }

  public Integer getScc() {
    return scc;
  }

  public void setScc(Integer scc) {
    this.scc = scc;
  }

  public Integer getSwt() {
    return swt;
  }

  public void setSwt(Integer swt) {
    this.swt = swt;
  }

  @Override
  public void restoreGeoxObjects() {
    IPoint geom = getGeom();
    this.geoxObj = new PointDEauImpl();
    this.geoxObj.setGeom(geom);
    this.geoxObj.setNom(name);
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
