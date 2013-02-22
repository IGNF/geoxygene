/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

public class MGCPBuilding extends MGCPFeature implements IBuilding {

  private AutreConstruction geoxObj;

  // VMAP attributes
  private String ace, ale, aoo, cpyrt_note, hgt, nam, nfi, nfn, src_date,
      src_info, tier_note, txt, uid, upd_date, upd_info, voi, wid;
  private long acc, ace_eval, afc, ale_eval, caa, cef, cfc, cit, cus, ddc, ebt,
      fun, gfc, hwt, icf, mfc, paf, ppo, psf, res, rfc, sfy, smc, src_name,
      suc, tfc, upd_name, uuc, zval_type;

  /**
   * @param type
   */
  public MGCPBuilding(IPolygon polygon, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(polygon);
    this.setInitialGeom(polygon);
    this.setEliminated(false);

    this.acc = (Long) attributes.get("acc");
    this.ace_eval = (Long) attributes.get("ace_eval");
    this.afc = (Long) attributes.get("afc");
    this.ale_eval = (Long) attributes.get("ale_eval");
    this.caa = (Long) attributes.get("caa");
    this.cef = (Long) attributes.get("cef");
    this.cfc = (Long) attributes.get("cfc");
    this.cit = (Long) attributes.get("cit");
    this.cus = (Long) attributes.get("cus");
    this.ddc = (Long) attributes.get("ddc");
    this.ebt = (Long) attributes.get("ebt");
    this.fun = (Long) attributes.get("fun");
    this.gfc = (Long) attributes.get("gfc");
    this.hwt = (Long) attributes.get("hwt");
    this.icf = (Long) attributes.get("icf");
    this.mfc = (Long) attributes.get("mfc");
    this.paf = (Long) attributes.get("paf");
    this.ppo = (Long) attributes.get("ppo");
    this.psf = (Long) attributes.get("psf");
    this.res = (Long) attributes.get("res");
    this.rfc = (Long) attributes.get("rfc");
    this.sfy = (Long) attributes.get("sfy");
    this.smc = (Long) attributes.get("smc");
    this.src_name = (Long) attributes.get("src_name");
    this.suc = (Long) attributes.get("suc");
    this.tfc = (Long) attributes.get("tfc");
    this.upd_name = (Long) attributes.get("upd_name");
    this.uuc = (Long) attributes.get("uuc");
    this.zval_type = (Long) attributes.get("zval_type");

    this.ace = (String) attributes.get("ace");
    this.ale = (String) attributes.get("ale");
    this.aoo = (String) attributes.get("aoo");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.hgt = (String) attributes.get("hgt");
    this.nam = (String) attributes.get("nam");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.src_date = (String) attributes.get("src_date");
    this.src_info = (String) attributes.get("src_info");
    this.tier_note = (String) attributes.get("tier_note");
    this.txt = (String) attributes.get("txt");
    this.uid = (String) attributes.get("uid");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_info = (String) attributes.get("upd_info");
    this.voi = (String) attributes.get("voi");
    this.wid = (String) attributes.get("wid");

  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public String getNfi() {
    return this.nfi;
  }

  public void setNfi(String nfi) {
    this.nfi = nfi;
  }

  public String getNfn() {
    return this.nfn;
  }

  public void setNfn(String nfn) {
    this.nfn = nfn;
  }

  public String getVoi() {
    return this.voi;
  }

  public void setVoi(String voi) {
    this.voi = voi;
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public String getAoo() {
    return this.aoo;
  }

  public void setAoo(String aoo) {
    this.aoo = aoo;
  }

  public String getHgt() {
    return this.hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
  }

  public long getHwt() {
    return this.hwt;
  }

  public void setHwt(long hwt) {
    this.hwt = hwt;
  }

  public long getSmc() {
    return this.smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public String getWid() {
    return this.wid;
  }

  public void setWid(String wid) {
    this.wid = wid;
  }

  public long getAfc() {
    return this.afc;
  }

  public void setAfc(long afc) {
    this.afc = afc;
  }

  public long getCef() {
    return this.cef;
  }

  public void setCef(long cef) {
    this.cef = cef;
  }

  public long getCfc() {
    return this.cfc;
  }

  public void setCfc(long cfc) {
    this.cfc = cfc;
  }

  public long getCit() {
    return this.cit;
  }

  public void setCit(long cit) {
    this.cit = cit;
  }

  public long getCus() {
    return this.cus;
  }

  public void setCus(long cus) {
    this.cus = cus;
  }

  public long getDdc() {
    return this.ddc;
  }

  public void setDdc(long ddc) {
    this.ddc = ddc;
  }

  public long getEbt() {
    return this.ebt;
  }

  public void setEbt(long ebt) {
    this.ebt = ebt;
  }

  public long getGfc() {
    return this.gfc;
  }

  public void setGfc(long gfc) {
    this.gfc = gfc;
  }

  public long getIcf() {
    return this.icf;
  }

  public void setIcf(long icf) {
    this.icf = icf;
  }

  public long getMfc() {
    return this.mfc;
  }

  public void setMfc(long mfc) {
    this.mfc = mfc;
  }

  public long getPaf() {
    return this.paf;
  }

  public void setPaf(long paf) {
    this.paf = paf;
  }

  public long getPsf() {
    return this.psf;
  }

  public void setPsf(long psf) {
    this.psf = psf;
  }

  public long getRes() {
    return this.res;
  }

  public void setRes(long res) {
    this.res = res;
  }

  public long getRfc() {
    return this.rfc;
  }

  public void setRfc(long rfc) {
    this.rfc = rfc;
  }

  public long getSfy() {
    return this.sfy;
  }

  public void setSfy(long sfy) {
    this.sfy = sfy;
  }

  public long getSuc() {
    return this.suc;
  }

  public void setSuc(long suc) {
    this.suc = suc;
  }

  public long getTfc() {
    return this.tfc;
  }

  public void setTfc(long tfc) {
    this.tfc = tfc;
  }

  public long getUuc() {
    return this.uuc;
  }

  public void setUuc(long uuc) {
    this.uuc = uuc;
  }

  public long getFun() {
    return this.fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public long getCaa() {
    return this.caa;
  }

  public void setCaa(long caa) {
    this.caa = caa;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IUrbanBlock getBlock() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getNature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setNature(String nature) {
    // TODO Auto-generated method stub

  }

  public String getAce() {
    return ace;
  }

  public void setAce(String ace) {
    this.ace = ace;
  }

  public String getAle() {
    return ale;
  }

  public void setAle(String ale) {
    this.ale = ale;
  }

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public String getNam() {
    return nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getSrc_info() {
    return src_info;
  }

  public void setSrc_info(String src_info) {
    this.src_info = src_info;
  }

  public String getTier_note() {
    return tier_note;
  }

  public void setTier_note(String tier_note) {
    this.tier_note = tier_note;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public long getAce_eval() {
    return ace_eval;
  }

  public void setAce_eval(long ace_eval) {
    this.ace_eval = ace_eval;
  }

  public long getAle_eval() {
    return ale_eval;
  }

  public void setAle_eval(long ale_eval) {
    this.ale_eval = ale_eval;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  public long getPpo() {
    return ppo;
  }

  public void setPpo(long ppo) {
    this.ppo = ppo;
  }

}
