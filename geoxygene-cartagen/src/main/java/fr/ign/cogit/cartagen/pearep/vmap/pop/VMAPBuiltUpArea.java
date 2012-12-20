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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPLandUseType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPBuiltUpArea extends GeneObjDefault implements
    ISimpleLandUseArea, VMAPFeature {

  // VMAP attributes
  private String fCode, name, nfi, nfn;
  private int bac, cod, dmr, dms, exs, fuc, pht, ppt, smc, use, tileId;
  private long facId;
  private double area;

  public VMAPBuiltUpArea(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());
  }

  public VMAPBuiltUpArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    this(poly);

    this.fCode = (String) attributes.get("f_code");
    this.name = (String) attributes.get("nam");

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.bac = (Integer) attributes.get("bac");
      this.cod = (Integer) attributes.get("cod");
      this.dmr = (Integer) attributes.get("dmr");
      this.dms = (Integer) attributes.get("dms");
      this.exs = (Integer) attributes.get("exs");
      this.fuc = (Integer) attributes.get("fuc");
      this.nfi = (String) attributes.get("nfi");
      this.nfn = (String) attributes.get("nfn");
      this.pht = (Integer) attributes.get("pht");
      this.ppt = (Integer) attributes.get("ppt");
      this.smc = (Integer) attributes.get("smc");
      this.use = (Integer) attributes.get("use");
    }
    if (type.equals(PeaRepDbType.VMAP1)) {
      this.exs = (Integer) attributes.get("exs");
      this.use = (Integer) attributes.get("use");
      this.tileId = (Integer) attributes.get("tile_id");
      this.facId = (Long) attributes.get("fac_id");
    }
    if (type.equals(PeaRepDbType.VMAP0)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.facId = (Long) attributes.get("fac_id");
    }
  }

  @Override
  public int getType() {
    return VMAPLandUseType.BUILT_UP.ordinal();
  }

  @Override
  public void setType(int type) {
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getBac() {
    return bac;
  }

  public void setBac(int bac) {
    this.bac = bac;
  }

  public int getDmr() {
    return dmr;
  }

  public void setDmr(int dmr) {
    this.dmr = dmr;
  }

  public int getDms() {
    return dms;
  }

  public void setDms(int dms) {
    this.dms = dms;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getFuc() {
    return fuc;
  }

  public void setFuc(int fuc) {
    this.fuc = fuc;
  }

  public int getPht() {
    return pht;
  }

  public void setPht(int pht) {
    this.pht = pht;
  }

  public int getPpt() {
    return ppt;
  }

  public void setPpt(int ppt) {
    this.ppt = ppt;
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

  public int getUse() {
    return use;
  }

  public void setUse(int use) {
    this.use = use;
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

  public long getFacId() {
    return facId;
  }

  public void setFacId(long facId) {
    this.facId = facId;
  }

  public double getArea() {
    return area;
  }

  public void setArea(double area) {
    this.area = area;
  }

}
