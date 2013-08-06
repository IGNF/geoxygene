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

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class MGCPBuiltUpArea extends MGCPFeature implements ISimpleLandUseArea {

  // VMAP attributes
  private String ace, ale, cpyrt_note, dmr, dms, hgt, nam, nfi, nfn, pfh,
      src_date, src_info, tier_note, txt, uid, upd_date, upd_info, valid_date,
      valid_info;
  private long acc, ace_eval, ale_eval, bac, fcsubtype, fuc, fun, ord, ppt,
      smc, src_name, upd_name, valid_stat, catbua, scamax, scamin, originform,
      targetscal, zval_type;
  private double area;

  public MGCPBuiltUpArea(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());

  }

  /**
   * @param type
   */
  public MGCPBuiltUpArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    this(poly);
    this.setAttributeMap(attributes);//

    this.acc = getLongAttribute("acc");
    this.ace = getStringAttribute("ace");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale = getStringAttribute("ale");
    this.ale_eval = getLongAttribute("ale_eval");

    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.nam = getStringAttribute("nam");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.src_name = getLongAttribute("src_name");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");
    this.txt = getStringAttribute("txt");
    this.tier_note = getStringAttribute("tier_note");
    this.uid = getStringAttribute("uid");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_name = getLongAttribute("upd_name");
    this.upd_info = getStringAttribute("upd_info");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.valid_stat = getLongAttribute("valid_stat");

    this.zval_type = getLongAttribute("zval_type");
    this.pfh = getStringAttribute("pfh");

    this.bac = getLongAttribute("bac");
    this.dmr = getStringAttribute("dmr");
    this.dms = getStringAttribute("dms");
    this.fun = getLongAttribute("fun");
    this.fuc = getLongAttribute("fuc");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.hgt = getStringAttribute("hgt");
    this.ppt = getLongAttribute("ppt");
    this.smc = getLongAttribute("smc");
    this.ord = getLongAttribute("ord");

    this.catbua = getLongAttribute("catbua");
    this.scamax = getLongAttribute("scamax");
    this.scamin = getLongAttribute("scamin");
    this.zval_type = getLongAttribute("zval_type");

    this.setAttributeMap(null);
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public void setGeom(IGeometry geom) {
    super.setGeom(geom);
    this.setArea(geom.area());
  }

  public Long getSmc() {
    return this.smc;
  }

  public void setSmc(Long smc) {
    this.smc = smc;
  }

  public Long getBac() {
    return this.bac;
  }

  public void setBac(Long bac) {
    this.bac = bac;
  }

  public String getDmr() {
    return this.dmr;
  }

  public void setDmr(String dmr) {
    this.dmr = dmr;
  }

  public String getDms() {
    return this.dms;
  }

  public void setDms(String dms) {
    this.dms = dms;
  }

  public Long getFuc() {
    return this.fuc;
  }

  public void setFuc(Long fuc) {
    this.fuc = fuc;
  }

  public Long getPpt() {
    return this.ppt;
  }

  public void setPpt(Long ppt) {
    this.ppt = ppt;
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

  public double getArea() {
    return this.area;
  }

  public void setArea(double area) {
    this.area = area;
  }

  public String getHgt() {
    return this.hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
  }

  public Long getAcc() {
    return this.acc;
  }

  public void setAcc(Long acc) {
    this.acc = acc;
  }

  public Long getFun() {
    return this.fun;
  }

  public void setFun(Long fun) {
    this.fun = fun;
  }

  public Long getOrd() {
    return this.ord;
  }

  public void setOrd(Long ord) {
    this.ord = ord;
  }

  public String getNam() {
    return nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
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

  public String getPfh() {
    return pfh;
  }

  public void setPfh(String pfh) {
    this.pfh = pfh;
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

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  public String getValid_date() {
    return valid_date;
  }

  public void setValid_date(String valid_date) {
    this.valid_date = valid_date;
  }

  public String getValid_info() {
    return valid_info;
  }

  public void setValid_info(String valid_info) {
    this.valid_info = valid_info;
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

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
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

  public long getValid_stat() {
    return valid_stat;
  }

  public void setValid_stat(long valid_stat) {
    this.valid_stat = valid_stat;
  }

  public long getCatbua() {
    return catbua;
  }

  public void setCatbua(long catbua) {
    this.catbua = catbua;
  }

  public long getScamax() {
    return scamax;
  }

  public void setScamax(long scamax) {
    this.scamax = scamax;
  }

  public long getScamin() {
    return scamin;
  }

  public void setScamin(long scamin) {
    this.scamin = scamin;
  }

  public long getOriginform() {
    return originform;
  }

  public void setOriginform(long originform) {
    this.originform = originform;
  }

  public long getTargetscal() {
    return targetscal;
  }

  public void setTargetscal(long targetscal) {
    this.targetscal = targetscal;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  @Override
  public int getType() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setType(int type) {
    // TODO Auto-generated method stub

  }

}
