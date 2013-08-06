/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.ind;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.activite.PointRepresentatifActiviteInteret;
import fr.ign.cogit.geoxygene.schemageo.impl.activite.PointRepresentatifActiviteInteretImpl;

public class VMAPStoragePoint extends VMAPFeature implements IMiscPoint {

  private PointRepresentatifActiviteInteret geoxObj;

  // VMAP attributes
  private String fCode, voi;
  private int acc, aoo, coe, exs, hgt, len, loc, pro, smc, ssc, use, wid, z;

  /**
   * @param type
   */
  public VMAPStoragePoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new PointRepresentatifActiviteInteretImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.acc = (Integer) attributes.get("acc");
    this.aoo = (Integer) attributes.get("aoo");
    this.pro = (Integer) attributes.get("pro");
    this.smc = (Integer) attributes.get("smc");
    this.exs = (Integer) attributes.get("exs");
    this.hgt = (Integer) attributes.get("hgt");
    this.coe = (Integer) attributes.get("coe");
    this.len = (Integer) attributes.get("len");
    this.loc = (Integer) attributes.get("loc");
    this.ssc = (Integer) attributes.get("ssc");
    this.use = (Integer) attributes.get("use");
    this.wid = (Integer) attributes.get("wid");
    this.z = (Integer) attributes.get("zv2");
    this.fCode = getStringAttribute("f_code");
    this.voi = getStringAttribute("voi");
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public String getVoi() {
    return voi;
  }

  public void setVoi(String voi) {
    this.voi = voi;
  }

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getAoo() {
    return aoo;
  }

  public void setAoo(int aoo) {
    this.aoo = aoo;
  }

  public int getCoe() {
    return coe;
  }

  public void setCoe(int coe) {
    this.coe = coe;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getHgt() {
    return hgt;
  }

  public void setHgt(int hgt) {
    this.hgt = hgt;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }

  public int getLoc() {
    return loc;
  }

  public void setLoc(int loc) {
    this.loc = loc;
  }

  public int getPro() {
    return pro;
  }

  public void setPro(int pro) {
    this.pro = pro;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getSsc() {
    return ssc;
  }

  public void setSsc(int ssc) {
    this.ssc = ssc;
  }

  public int getUse() {
    return use;
  }

  public void setUse(int use) {
    this.use = use;
  }

  public int getWid() {
    return wid;
  }

  public void setWid(int wid) {
    this.wid = wid;
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

}
