package fr.ign.cogit.cartagen.pearep.mgcp.energy;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class MGCPPipeLine extends MGCPFeature implements IElectricityLine {

  private ArcReseau geoxObj;
  private boolean deadEnd;
  private INetworkNode initialNode;
  private INetworkNode finalNode;
  private Direction direction;
  private int importance;
  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;
  private String upd_date, upd_info, cpyrt_note, src_info, txt, nfi, nfn,
      tier_note, nam, voi, src_date, hgt, ace, ale;
  private long acc, ace_eval, ale_eval, uid, upd_name, src_name, zval_type,
      fun, owo, ppo, loc, rta;

  /**
   * @param type
   */
  public MGCPPipeLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setGeoxObj(new ArcReseauImpl(new ReseauImpl(), false, line));
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;

    // attributes present in Mgcp++
    this.acc = (Long) attributes.get("acc");
    this.ace_eval = (Long) attributes.get("ace_eval");
    this.ale_eval = (Long) attributes.get("ale_eval");
    if (attributes.containsKey("uid"))
      this.uid = (Long) attributes.get("uid");
    this.src_name = (Long) attributes.get("src_name");
    this.zval_type = (Long) attributes.get("zval_type");
    this.hgt = (String) attributes.get("hgt");
    this.ppo = (Long) attributes.get("ppo");
    this.voi = (String) attributes.get("voi");
    this.owo = (Long) attributes.get("owo");
    this.loc = (Long) attributes.get("loc");
    this.rta = (Long) attributes.get("rta");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_info = (String) attributes.get("upd_info");
    this.fun = (Long) attributes.get("fun");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.upd_name = (Long) attributes.get("upd_name");
    this.src_info = (String) attributes.get("src_info");
    this.txt = (String) attributes.get("txt");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.tier_note = (String) attributes.get("tier_note");
    this.nam = (String) attributes.get("nam");
    this.src_date = (String) attributes.get("src_date");
    this.ace = (String) attributes.get("ace");
    this.ale = (String) attributes.get("ale");
  }

  private void setGeoxObj(ArcReseau arcReseau) {
    this.geoxObj = arcReseau;
  }

  @Override
  public int getImportance() {
    return importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public boolean isDeadEnd() {
    return deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
  }

  @Override
  public INetworkNode getInitialNode() {
    return initialNode;
  }

  @Override
  public void setInitialNode(INetworkNode initialNode) {
    this.initialNode = initialNode;
  }

  @Override
  public INetworkNode getFinalNode() {
    return finalNode;
  }

  @Override
  public void setFinalNode(INetworkNode finalNode) {
    this.finalNode = finalNode;
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return networkSectionType;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType networkSectionType) {
    this.networkSectionType = networkSectionType;
  }

  @Override
  public double getWidth() {
    return GeneralisationLegend.RES_ELEC_LARGEUR;
  }

  @Override
  public double getInternWidth() {
    return 0;
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return false;
  }

  @Override
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public INetworkFace getRightFace() {
    return null;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  @Override
  public IFeature getGeoxObj() {
    return geoxObj;
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

  public String getVoi() {
    return voi;
  }

  public void setVoi(String voi) {
    this.voi = voi;
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getHgt() {
    return hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
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

  public long getFun() {
    return fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public long getOwo() {
    return owo;
  }

  public void setOwo(long owo) {
    this.owo = owo;
  }

  public long getPpo() {
    return ppo;
  }

  public void setPpo(long ppo) {
    this.ppo = ppo;
  }

  public long getLoc() {
    return loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
  }

  public long getRta() {
    return rta;
  }

  public void setRta(long rta) {
    this.rta = rta;
  }

}
