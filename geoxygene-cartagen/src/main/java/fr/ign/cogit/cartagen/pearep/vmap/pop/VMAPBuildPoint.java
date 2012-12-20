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

public class VMAPBuildPoint extends GeneObjDefault implements IBuildPoint,
    VMAPFeature {

  private AutreConstruction geoxObj;

  // VMAP attributes
  private String fCode, name, nfi, nfn, voi;
  private int acc, aoo, bfc, coe, exs, hgt, hwt, len, smc, ssr, use, wid, z,
      tileId;
  private long endId;

  public VMAPBuildPoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);

    this.fCode = (String) attributes.get("f_code");
    this.aoo = (Integer) attributes.get("aoo");
    this.bfc = (Integer) attributes.get("bfc");
    this.exs = (Integer) attributes.get("exs");
    this.hgt = (Integer) attributes.get("hgt");
    this.hwt = (Integer) attributes.get("hwt");
    this.name = (String) attributes.get("nam");
    this.wid = (Integer) attributes.get("wid");

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.acc = (Integer) attributes.get("acc");
      this.coe = (Integer) attributes.get("coe");
      this.len = (Integer) attributes.get("len");
      this.nfi = (String) attributes.get("nfi");
      this.nfn = (String) attributes.get("nfn");
      this.smc = (Integer) attributes.get("smc");
      this.ssr = (Integer) attributes.get("ssr");
      this.z = (Integer) attributes.get("zv2");
      this.use = (Integer) attributes.get("use");
      this.voi = (String) attributes.get("voi");

    }
    if (type.equals(PeaRepDbType.VMAP1)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.endId = (Long) attributes.get("end_id");
    }

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

  public int getBfc() {
    return bfc;
  }

  public void setBfc(int bfc) {
    this.bfc = bfc;
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

  public int getHwt() {
    return hwt;
  }

  public void setHwt(int hwt) {
    this.hwt = hwt;
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

  public int getSsr() {
    return ssr;
  }

  public void setSsr(int ssr) {
    this.ssr = ssr;
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

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

  public int getTileId() {
    return tileId;
  }

  public void setTileId(int tileId) {
    this.tileId = tileId;
  }

  public long getEndId() {
    return endId;
  }

  public void setEndId(long endId) {
    this.endId = endId;
  }

}
