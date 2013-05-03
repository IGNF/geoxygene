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

public class MGCPCropLandArea extends MGCPFeature implements ISimpleLandUseArea {

  // MGCP attributes
  private String ace, ale, cpyrt_note, date_bdi, dmt, nam, nfi, nfn, pfh,
      src_date, src_info, valid_date, valid_info, tier_note, txt, uid,
      upd_date, upd_info;
  private long acc, ace_eval, ale_eval, csp, csp2, fcsubtype, ffp, fmm, irg,
      smc, src_name, tre, upd_name, valid_stat, veg, zval_type, originform,
      targetscal;
  private double area;

  public MGCPCropLandArea(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());
  }

  /**
   * @param type
   */
  public MGCPCropLandArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    this(poly);
    this.setAttributeMap(attributes);//

    this.acc = getLongAttribute("acc");
    this.ace = (String) attributes.get("ace");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale = (String) attributes.get("ale");
    this.ale_eval = getLongAttribute("ale_eval");
    this.nfi = (String) attributes.get("nfi");
    this.date_bdi = (String) attributes.get("date_bdi");

    this.dmt = (String) attributes.get("dmt");
    this.pfh = (String) attributes.get("pfh");

    this.valid_date = (String) attributes.get("valid_date");
    this.valid_info = (String) attributes.get("valid_info");

    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.nfn = (String) attributes.get("nfn");
    this.nam = (String) attributes.get("nam");
    this.src_date = (String) attributes.get("src_date");
    this.src_info = (String) attributes.get("src_info");
    this.src_name = getLongAttribute("src_name");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");
    this.txt = (String) attributes.get("txt");
    this.tier_note = (String) attributes.get("tier_note");
    this.uid = (String) attributes.get("uid");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_name = getLongAttribute("upd_name");
    this.upd_info = (String) attributes.get("upd_info");
    this.zval_type = getLongAttribute("zval_type");
    this.smc = getLongAttribute("smc");

    this.csp = getLongAttribute("csp");
    this.csp2 = getLongAttribute("csp2");
    this.ffp = getLongAttribute("ffp");
    this.fmm = getLongAttribute("fmm");
    this.irg = getLongAttribute("irg");
    this.tre = getLongAttribute("tre");
    this.veg = getLongAttribute("veg");

    this.valid_stat = getLongAttribute("valid_stat");

    this.setAttributeMap(null);//

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

  public String getNam() {
    return this.nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
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

  public Long getAcc() {
    return this.acc;
  }

  public void setAcc(Long acc) {
    this.acc = acc;
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

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
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

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getDate_bdi() {
    return date_bdi;
  }

  public void setDate_bdi(String date_bdi) {
    this.date_bdi = date_bdi;
  }

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
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

  public long getValid_stat() {
    return valid_stat;
  }

  public void setValid_stat(long valid_stat) {
    this.valid_stat = valid_stat;
  }

  public String getDmt() {
    return dmt;
  }

  public void setDmt(String dmt) {
    this.dmt = dmt;
  }

  public String getPfh() {
    return pfh;
  }

  public void setPfh(String pfh) {
    this.pfh = pfh;
  }

  public long getCsp() {
    return csp;
  }

  public void setCsp(long csp) {
    this.csp = csp;
  }

  public long getCsp2() {
    return csp2;
  }

  public void setCsp2(long csp2) {
    this.csp2 = csp2;
  }

  public long getFfp() {
    return ffp;
  }

  public void setFfp(long ffp) {
    this.ffp = ffp;
  }

  public long getFmm() {
    return fmm;
  }

  public void setFmm(long fmm) {
    this.fmm = fmm;
  }

  public long getIrg() {
    return irg;
  }

  public void setIrg(long irg) {
    this.irg = irg;
  }

  public long getTre() {
    return tre;
  }

  public void setTre(long tre) {
    this.tre = tre;
  }

  public long getVeg() {
    return veg;
  }

  public void setVeg(long veg) {
    this.veg = veg;
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
