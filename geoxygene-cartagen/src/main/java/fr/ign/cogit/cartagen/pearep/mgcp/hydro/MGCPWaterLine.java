/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp.hydro;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.TronconHydrographiqueImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterLine Description: Tronçons
 * hydrographiques Author: J. Renard Date: 18/09/2009
 */
public class MGCPWaterLine extends MGCPFeature implements IWaterLine {

  /**
   * Associated Geoxygene schema object
   */
  @Transient
  private TronconHydrographique geoxObj;
  @Transient
  private INetworkNode initialNode;
  @Transient
  private INetworkNode finalNode;
  private Direction direction;
  private boolean deadEnd = false;
  private int importance;
  private NetworkSectionType type;

  private long acc, ace_eval, ale_eval, cda, dir, fcsubtype, hyp, loc, prc,
      shl, shr, smc, src_name, tid, upd_name, valid_stat, wcc, wst, zval_type,
      scamax, scamin, originform, targetscal, fun, nvs, catcan;
  private String ace, ale, cpyrt_note, date_bdi, lbv, nam, nfi, nfn, rbv,
      src_date, src_info, tier_note, txt, uid, upd_date, upd_info, valid_date,
      valid_info, wid, status, hgt;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public MGCPWaterLine() {
    super();
  }

  public MGCPWaterLine(IGeometry geom) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false,
        (ICurve) geom);
    this.setInitialGeom(geom);
    this.setEliminated(false);
  }

  /**
   * The generic constructor used to correct road data.
   * @param line
   * @param attributes
   * @param type
   */
  public MGCPWaterLine(ILineString line) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(geom);
    this.setEliminated(false);
  }

  /**
   * @param type
   */
  public MGCPWaterLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    // attributes present in Mgcp++
    this.acc = getLongAttribute("acc");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    if (attributes.containsKey("cda"))
      this.cda = getLongAttribute("cda");
    this.dir = getLongAttribute("dir");
    this.fcsubtype = getLongAttribute("fcsubtype");
    if (attributes.containsKey("fun"))
      this.fun = getLongAttribute("fun");

    this.hyp = getLongAttribute("hyp");
    this.loc = getLongAttribute("loc");
    if (attributes.containsKey("nvs"))
      this.nvs = getLongAttribute("nvs");
    this.prc = getLongAttribute("prc");
    if (attributes.containsKey("shl"))
      this.shl = getLongAttribute("shl");
    if (attributes.containsKey("shr"))
      this.shr = getLongAttribute("shr");
    this.smc = getLongAttribute("smc");
    this.src_name = getLongAttribute("src_name");
    if (attributes.containsKey("tid"))
      this.tid = getLongAttribute("tid");
    this.upd_name = getLongAttribute("upd_name");
    this.valid_stat = getLongAttribute("valid_stat");
    if (attributes.containsKey("wcc"))
      this.wcc = getLongAttribute("wcc");
    if (attributes.containsKey("wst"))
      this.wst = getLongAttribute("wst");
    this.zval_type = getLongAttribute("zval_type");
    if (attributes.containsKey("catcan"))
      this.catcan = getLongAttribute("catcan");
    this.scamax = getLongAttribute("scamax");
    this.scamin = getLongAttribute("scamin");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");

    this.ace = getStringAttribute("ace");
    this.ale = getStringAttribute("ale");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.date_bdi = getStringAttribute("date_bdi");
    if (attributes.containsKey("hgt"))
      this.hgt = getStringAttribute("hgt");
    this.lbv = getStringAttribute("lbv");
    this.nam = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.rbv = getStringAttribute("rbv");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.tier_note = getStringAttribute("tier_note");
    this.txt = getStringAttribute("txt");
    this.uid = getStringAttribute("uid");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.wid = getStringAttribute("wid");
    this.status = getStringAttribute("status");

    this.setAttributeMap(null);
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public double getWidth() {
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  @Transient
  public double getInternWidth() {
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  @Transient
  public INetworkNode getFinalNode() {
    return this.finalNode;
  }

  @Override
  @Transient
  public INetworkNode getInitialNode() {
    return this.initialNode;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    this.finalNode = node;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = node;
  }

  @Override
  public boolean isDeadEnd() {
    return this.deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  @Column(name = "CartAGenDB_name")
  public String getDbName() {
    return super.getDbName();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  public String getNam() {
    return this.nam;
  }

  public void setNam(String name) {
    this.nam = name;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false,
        this.getGeom());
    this.geoxObj.setNom(this.nam);
  }

  @Override
  public int getImportance() {
    return this.importance;
  }

  @Override
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return this.type;
  }

  @Override
  public INetworkFace getRightFace() {
    return null;
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return this.importance == at.getImportance();
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.type = type;
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public long getDir() {
    return this.dir;
  }

  public void setDir(long dir) {
    this.dir = dir;
  }

  public long getWcc() {
    return this.wcc;
  }

  public void setWcc(long wcc) {
    this.wcc = wcc;
  }

  public long getHyp() {
    return this.hyp;
  }

  public void setHyp(long hyp) {
    this.hyp = hyp;
  }

  public long getLoc() {
    return this.loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
  }

  public long getPrc() {
    return this.prc;
  }

  public void setPrc(long prc) {
    this.prc = prc;
  }

  public long getSmc() {
    return this.smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public long getTid() {
    return this.tid;
  }

  public void setTid(long tid) {
    this.tid = tid;
  }

  public String getWid() {
    return this.wid;
  }

  public void setWid(String wid) {
    this.wid = wid;
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

  public NetworkSectionType getType() {
    return type;
  }

  public void setType(NetworkSectionType type) {
    this.type = type;
  }

  public String getLbv() {
    return lbv;
  }

  public void setLbv(String lbv) {
    this.lbv = lbv;
  }

  public String getRbv() {
    return rbv;
  }

  public void setRbv(String rbv) {
    this.rbv = rbv;
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

  public long getCda() {
    return cda;
  }

  public void setCda(long cda) {
    this.cda = cda;
  }

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
  }

  public long getShl() {
    return shl;
  }

  public void setShl(long shl) {
    this.shl = shl;
  }

  public long getShr() {
    return shr;
  }

  public void setShr(long shr) {
    this.shr = shr;
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

  public long getWst() {
    return wst;
  }

  public void setWst(long wst) {
    this.wst = wst;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getFun() {
    return fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public long getNvs() {
    return nvs;
  }

  public void setNvs(long nvs) {
    this.nvs = nvs;
  }

  public long getCatcan() {
    return catcan;
  }

  public void setCatcan(long catcan) {
    this.catcan = catcan;
  }

  public String getHgt() {
    return hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
  }

}
