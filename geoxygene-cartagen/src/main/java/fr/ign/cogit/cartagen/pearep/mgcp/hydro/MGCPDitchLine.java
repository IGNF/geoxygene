package fr.ign.cogit.cartagen.pearep.mgcp.hydro;

import java.util.HashMap;

import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IDitchLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ElementIsole;
import fr.ign.cogit.geoxygene.schemageo.impl.occSol.ElementIsoleImpl;

public class MGCPDitchLine extends MGCPFeature implements IDitchLine {

  private ElementIsole geoxObj;
  private String ace, ale, cpyrt_note, date_bdi, lbv, nam, nfi, nfn, rbv,
      src_date, src_info, txt, upd_date, upd_info, valid_date, valid_info,
      tier_note, wid;
  private long acc, ace_eval, ale_eval, cda, dir, fcsubtype, fun, hyp, loc,
      smc, src_name, uid, upd_name, valid_stat, zval_type, originform,
      targetscal;

  /**
   * @param type
   */
  public MGCPDitchLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setGeoxObj(new ElementIsoleImpl());
    this.getGeoxObj().setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    // attributes present in Mgcp++
    this.acc = getLongAttribute("acc");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    this.cda = getLongAttribute("cda");
    this.dir = getLongAttribute("dir");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.fun = getLongAttribute("fun");
    this.hyp = getLongAttribute("hyp");
    this.loc = getLongAttribute("loc");
    this.smc = getLongAttribute("smc");
    this.src_name = getLongAttribute("src_name");
    this.valid_stat = getLongAttribute("valid_stat");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");
    if (attributes.containsKey("uid"))
      this.uid = getLongAttribute("uid");
    this.zval_type = getLongAttribute("zval_type");
    this.upd_name = getLongAttribute("upd_name");

    this.ace = getStringAttribute("ace");
    this.ale = getStringAttribute("ale");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.date_bdi = getStringAttribute("date_bdi");
    this.nam = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.rbv = getStringAttribute("rbv");
    this.lbv = getStringAttribute("lbv");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.txt = getStringAttribute("txt");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.tier_note = getStringAttribute("tier_note");
    this.wid = getStringAttribute("wid");
    this.setAttributeMap(null);
  }

  private void setGeoxObj(ElementIsole elementIsole) {
    this.geoxObj = elementIsole;
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
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

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public String getSrc_info() {
    return src_info;
  }

  public void setSrc_info(String src_info) {
    this.src_info = src_info;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
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

  public String getTier_note() {
    return tier_note;
  }

  public void setTier_note(String tier_note) {
    this.tier_note = tier_note;
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

  public long getAcc() {
    return acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
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

  public long getUid() {
    return uid;
  }

  public void setUid(long uid) {
    this.uid = uid;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  public String getRbv() {
    return rbv;
  }

  public void setRbv(String rbv) {
    this.rbv = rbv;
  }

  public String getLbv() {
    return lbv;
  }

  public void setLbv(String lbv) {
    this.lbv = lbv;
  }

  public String getWid() {
    return wid;
  }

  public void setWid(String wid) {
    this.wid = wid;
  }

  public long getHyp() {
    return hyp;
  }

  public void setHyp(long hyp) {
    this.hyp = hyp;
  }

  public long getCda() {
    return cda;
  }

  public void setCda(long cda) {
    this.cda = cda;
  }

  /**
   * Get line length, useful to make OGC Filters on line length.
   * @return
   */
  public double getLength() {
    return this.getGeom().length();
  }

  public String getDate_bdi() {
    return date_bdi;
  }

  public void setDate_bdi(String date_bdi) {
    this.date_bdi = date_bdi;
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

  public long getDir() {
    return dir;
  }

  public void setDir(long dir) {
    this.dir = dir;
  }

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
  }

  public long getFun() {
    return fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public long getLoc() {
    return loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
  }

  public long getValid_stat() {
    return valid_stat;
  }

  public void setValid_stat(long valid_stat) {
    this.valid_stat = valid_stat;
  }

  public long getOriginform() {
    return originform;
  }

  public void setOriginform(long originform) {
    this.originform = originform;
  }

  public long getSmc() {
    return smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public long getTargetscal() {
    return targetscal;
  }

  public void setTargetscal(long targetscal) {
    this.targetscal = targetscal;
  }

}
