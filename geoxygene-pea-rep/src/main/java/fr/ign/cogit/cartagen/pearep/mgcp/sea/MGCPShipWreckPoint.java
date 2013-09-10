package fr.ign.cogit.cartagen.pearep.mgcp.sea;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.PointDEauImpl;

public class MGCPShipWreckPoint extends MGCPFeature implements IWaterPoint {

  private PointDEau geoxObj;

  private String cpyrt_note, date_bdi, nam, nfi, nfn, src_date, src_info,
      tier_note, txt, uid_, upd_date, upd_info, valid_date, valid_info, status;
  private long objectid, acc, ace_eval, ale_eval, awp, src_name, smc, loc,
      upd_name, zval_type, fcsubtype, valid_stat, wle, catwrk, scamax, scamin,
      originform, targetscal;
  private double ale, ace, aoo, len_, wid;

  /**
   * @param type
   */
  public MGCPShipWreckPoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new PointDEauImpl();
    this.setInitialGeom(point);
    this.setGeom(point);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    this.objectid = getLongAttribute("objectid");
    this.acc = getLongAttribute("acc");
    this.setId((int) objectid);
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    this.aoo = getDoubleAttribute("aoo");
    this.src_name = getLongAttribute("src_name");
    this.loc = getLongAttribute("loc");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.upd_name = getLongAttribute("upd_name");
    this.zval_type = getLongAttribute("zval_type");
    this.wle = getLongAttribute("wle");
    this.catwrk = getLongAttribute("catwrk");
    this.scamin = getLongAttribute("scamin");
    this.scamax = getLongAttribute("scamax");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");

    this.ace = getDoubleAttribute("ace");
    this.ale = getDoubleAttribute("ale");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.date_bdi = getStringAttribute("date_bdi");
    this.len_ = getDoubleAttribute("len_");
    this.wid = getDoubleAttribute("wid");
    this.nam = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.tier_note = getStringAttribute("tier_note");
    this.txt = getStringAttribute("txt");
    this.uid_ = getStringAttribute("uid_");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.status = getStringAttribute("status");
    this.valid_stat = getLongAttribute("valid_stat");
    this.setAttributeMap(null);
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

  public double getAce() {
    return ace;
  }

  public void setAce(double ace) {
    this.ace = ace;
  }

  public double getAle() {
    return ale;
  }

  public void setAle(double ale) {
    this.ale = ale;
  }

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public String getDate_bdi() {
    return date_bdi;
  }

  public void setDate_bdi(String date_bdi) {
    this.date_bdi = date_bdi;
  }

  public String getNam() {
    return nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
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

  public String getUid_() {
    return uid_;
  }

  public void setUid_(String uid_) {
    this.uid_ = uid_;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getObjectid() {
    return objectid;
  }

  public void setObjectid(long objectid) {
    this.objectid = objectid;
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

  public long getAwp() {
    return awp;
  }

  public void setAwp(long awp) {
    this.awp = awp;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getSmc() {
    return smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public long getLoc() {
    return loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
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

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
  }

  public long getValid_stat() {
    return valid_stat;
  }

  public void setValid_stat(long valid_stat) {
    this.valid_stat = valid_stat;
  }

  public long getWle() {
    return wle;
  }

  public void setWle(long wle) {
    this.wle = wle;
  }

  public long getCatwrk() {
    return catwrk;
  }

  public void setCatwrk(long catwrk) {
    this.catwrk = catwrk;
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

  public double getAoo() {
    return aoo;
  }

  public void setAoo(double aoo) {
    this.aoo = aoo;
  }

  public double getLen_() {
    return len_;
  }

  public void setLen_(double len_) {
    this.len_ = len_;
  }

  public double getWid() {
    return wid;
  }

  public void setWid(double wid) {
    this.wid = wid;
  }

}
