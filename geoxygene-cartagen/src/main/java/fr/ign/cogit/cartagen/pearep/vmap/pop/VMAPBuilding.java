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

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;

public class VMAPBuilding extends VMAPFeature implements IBuilding {

  /**
   * Associated Geoxygene schema object
   */
  private Batiment geoxObj;
  private String nature;

  // VMAP attributes
  private String fCode, name, nfi, nfn, voi;
  private int cod, bfc, coe, exs, hgt, hwt, len, smc, ssr, use, wid, z, tileId;

  private Long facId;

  public VMAPBuilding(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new BatimentImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);

    this.fCode = (String) attributes.get("f_code");
    this.bfc = (Integer) attributes.get("bfc");
    this.exs = (Integer) attributes.get("exs");
    this.setHgt((Integer) attributes.get("hgt"));
    this.hwt = (Integer) attributes.get("hwt");
    this.name = (String) attributes.get("nam");

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.cod = (Integer) attributes.get("cod");
      this.coe = (Integer) attributes.get("coe");
      this.len = (Integer) attributes.get("len");
      this.nfi = (String) attributes.get("nfi");
      this.nfn = (String) attributes.get("nfn");
      this.smc = (Integer) attributes.get("smc");
      this.ssr = (Integer) attributes.get("ssr");
      this.use = (Integer) attributes.get("use");
      this.voi = (String) attributes.get("voi");
      this.wid = (Integer) attributes.get("wid");
      this.z = (Integer) attributes.get("zv2");
    }

    if (type.equals(PeaRepDbType.VMAP1)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.facId = (Long) attributes.get("fac_id");
    }

  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IPolygon getSymbolGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IFeature getGeoxObj() {
    return geoxObj;
  }

  @Override
  public IUrbanBlock getBlock() {
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
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

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
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

  public int getHgt() {
    return hgt;
  }

  public void setHgt(int hgt) {
    this.hgt = hgt;
  }

  public int getTileId() {
    return tileId;
  }

  public void setTileId(int tileId) {
    this.tileId = tileId;
  }

  public Long getFacId() {
    return facId;
  }

  public void setFacId(Long facId) {
    this.facId = facId;
  }

  @Override
  public String getNature() {
    if (nature == null)
      computeNatureFromVMAP();
    return nature;
  }

  @Override
  public void setNature(String nature) {
    this.nature = nature;
  }

  private void computeNatureFromVMAP() {
    // TODO
  }
}
