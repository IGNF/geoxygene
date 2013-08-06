/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp.relief;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/*
 * ###### IGN / CartAGen ###### Title: ContourLine Description: Courbes de
 * niveau Author: J. Renard Date: 18/09/2009
 */

public class MGCPContourLine extends MGCPFeature implements IContourLine {

  /**
   * Associated Geoxygene schema object
   */
  private CourbeDeNiveau geoxObj;

  private long acc, ace_eval, ale_eval, clc, fcsubtype, hqc, smc, src_name,
      upd_name, valid_stat, zval_type, scamax, scamin, originform, targetscal;

  private String ace, ale, cpyrt_note, date_bdi, nam, nfi, nfn, src_date,
      src_info, tier_note, txt, uid, upd_date, upd_info, valid_date,
      valid_info, zv2;

  /**
   * Constructor
   */
  public MGCPContourLine(CourbeDeNiveau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  public MGCPContourLine(ILineString line, double value) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(), value, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }
  }

  /**
   * @param type
   */
  public MGCPContourLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new CourbeDeNiveauImpl(new ChampContinuImpl(),
        Integer.valueOf(getStringAttribute("zv2")), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//
    if ((int) (this.getAltitude() / GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE)
        * GeneralisationLegend.CN_EQUIDISTANCE_MAITRESSE == this.getAltitude()) {
      this.isMaster = true;
    }

    this.acc = getLongAttribute("acc");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    this.clc = getLongAttribute("clc");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.hqc = getLongAttribute("hqc");
    this.smc = getLongAttribute("smc");
    this.src_name = getLongAttribute("src_name");
    this.upd_name = getLongAttribute("upd_name");
    this.valid_stat = getLongAttribute("valid_stat");
    this.zval_type = getLongAttribute("zval_type");
    this.scamax = getLongAttribute("scamax");
    this.scamin = getLongAttribute("scamin");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");

    this.ace = getStringAttribute("ace");
    this.ale = getStringAttribute("ale");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.date_bdi = getStringAttribute("date_bdi");
    this.nam = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.tier_note = getStringAttribute("tier_note");
    this.txt = getStringAttribute("txt");
    this.uid = getStringAttribute("uid");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.zv2 = getStringAttribute("zv2");

    this.zv2 = getStringAttribute("zv2");
    this.setAttributeMap(null);

  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  private boolean isMaster = false;

  @Override
  public boolean isMaster() {
    return this.isMaster;
  }

  @Override
  public double getWidth() {
    if (this.isMaster()) {
      return GeneralisationLegend.CN_LARGEUR_MAITRESSE;
    }
    return GeneralisationLegend.CN_LARGEUR_NORMALE;
  }

  @Override
  public IPolygon getSymbolExtent() {
    return (IPolygon) CommonAlgorithms.buffer(this.getGeom(), this.getWidth()
        * 0.5 * Legend.getSYMBOLISATI0N_SCALE() / 1000.0);
  }

  @Override
  public double getAltitude() {
    return (this.geoxObj).getValeur();
  }

  @Override
  public void setAltitude(double z) {
    (this.geoxObj).setValeur(z);
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public long getHqc() {
    return this.hqc;
  }

  public void setHqc(long hqc) {
    this.hqc = hqc;
  }

  public void setZv2(String zv2) {
    this.zv2 = zv2;
  }

  public String getZv2() {
    return this.zv2;
  }

  public long getSmc() {
    return this.smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
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

  public long getClc() {
    return clc;
  }

  public void setClc(long clc) {
    this.clc = clc;
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

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
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

}
