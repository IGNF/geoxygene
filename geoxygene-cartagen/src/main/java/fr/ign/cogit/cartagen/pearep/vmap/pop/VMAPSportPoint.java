/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.pop;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

public class VMAPSportPoint extends GeneObjDefault implements IBuildPoint,
    VMAPFeature {

  private AutreConstruction geoxObj;

  // VMAP attributes
  private String fCode, name, nfi, nfn, voi;
  private int acc, aoo, coe, exs, hgt, len, smc, ssc, wid, z;
  private long ara;

  public VMAPSportPoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.acc = (Integer) attributes.get("acc");
    this.hgt = (Integer) attributes.get("hgt");
    this.aoo = (Integer) attributes.get("aoo");
    this.ara = (Long) attributes.get("ara");
    this.coe = (Integer) attributes.get("coe");
    this.exs = (Integer) attributes.get("exs");
    this.len = (Integer) attributes.get("len");
    this.smc = (Integer) attributes.get("smc");
    this.ssc = (Integer) attributes.get("ssc");
    this.wid = (Integer) attributes.get("wid");
    this.z = (Integer) attributes.get("zv2");
    this.fCode = (String) attributes.get("f_code");
    this.name = (String) attributes.get("nam");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.voi = (String) attributes.get("voi");
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
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

  public long getAra() {
    return ara;
  }

  public void setAra(int ara) {
    this.ara = ara;
  }

  public int getSsc() {
    return ssc;
  }

  public void setSsc(int ssc) {
    this.ssc = ssc;
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
