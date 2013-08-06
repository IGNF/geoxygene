package fr.ign.cogit.cartagen.pearep.mgcp.transport;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.TronconFerre;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.TronconFerreImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class MGCPRailwayLine extends MGCPFeature implements IRailwayLine {
  /**
   * Associated Geoxygene schema object
   */
  private TronconFerre geoxObj;
  private boolean deadEnd;
  private boolean sideTrack;
  private INetworkNode initialNode;
  private INetworkNode finalNode;
  private Direction direction;
  private int importance;
  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;

  // attributes
  private long fun, loc, rir, src_name, uid, ale_eval, ace_eval, zval_type,
      rta, acc, rgc, rra, rrc, ctl, rsa;
  private String nam, nfi, nfn, cpyrt_note, ltn, tier_note, src_info, src_date,
      upd_date, txt, upd_info, ale, gaw, ace;

  /**
   * The generic constructor used to import VMAP data.
   * @param line
   * @param attributes
   * @param type
   */
  public MGCPRailwayLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconFerreImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setAttributeMap(attributes);//

    // attributes present in Mgcp++
    this.acc = getLongAttribute("acc");
    this.fun = getLongAttribute("fun");
    if (attributes.containsKey("loc"))
      this.loc = getLongAttribute("loc");
    this.ltn = getStringAttribute("ltn");
    if (attributes.containsKey("rir"))
      this.rir = getLongAttribute("rir");
    this.nam = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.tier_note = getStringAttribute("tier_note");
    this.src_info = getStringAttribute("src_info");
    this.src_date = getStringAttribute("src_date");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.txt = getStringAttribute("txt");
    this.src_name = getLongAttribute("src_name");
    if (attributes.containsKey("uid"))
      this.uid = getLongAttribute("uid");
    this.ale_eval = getLongAttribute("ale_eval");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale = getStringAttribute("ale");
    this.zval_type = getLongAttribute("zval_type");
    this.gaw = getStringAttribute("gaw");
    if (attributes.containsKey("rta"))
      this.rta = getLongAttribute("rta");
    this.ace = getStringAttribute("ace");
    this.rgc = getLongAttribute("rgc");
    this.rra = getLongAttribute("rra");
    if (attributes.containsKey("rrc"))
      this.rrc = getLongAttribute("rrc");
    if (attributes.containsKey("ctl"))
      this.ctl = getLongAttribute("ctl");
    if (attributes.containsKey("rsa"))
      this.rsa = getLongAttribute("rsa");
    this.setAttributeMap(null);
  }

  /**
   * The generic constructor used to correct road data.
   * @param line
   * @param attributes
   * @param type
   */
  public MGCPRailwayLine(ILineString line) {
    super();
    this.geoxObj = new TronconFerreImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
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
  public TronconFerre getGeoxObj() {
    return geoxObj;
  }

  public void setGeoxObj(TronconFerre geoxObj) {
    this.geoxObj = geoxObj;
  }

  @Override
  public INetworkNode getInitialNode() {
    return initialNode;
  }

  @Override
  public INetworkNode getFinalNode() {
    return finalNode;
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  public void setSideTrack(boolean sideTrack) {
    this.sideTrack = sideTrack;
  }

  @Override
  public double getWidth() {
    if (this.isSideTrack()) {
      return GeneralisationLegend.RES_FER_LARGEUR * 2 / 3;
    } else {
      return GeneralisationLegend.RES_FER_LARGEUR;
    }
  }

  @Override
  public double getInternWidth() {
    // no intern width for railroad symbols
    return 0.0;
  }

  @Override
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return this.networkSectionType;
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
  public boolean isDeadEnd() {
    return this.deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
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
  public void setNetworkSectionType(NetworkSectionType type) {
    this.networkSectionType = type;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  @Override
  public boolean isSideTrack() {
    return sideTrack;
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

  public long getRir() {
    return rir;
  }

  public void setRir(long rir) {
    this.rir = rir;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getUid() {
    return uid;
  }

  public void setUid(long uid) {
    this.uid = uid;
  }

  public long getAle_eval() {
    return ale_eval;
  }

  public void setAle_eval(long ale_eval) {
    this.ale_eval = ale_eval;
  }

  public long getAce_eval() {
    return ace_eval;
  }

  public void setAce_eval(long ace_eval) {
    this.ace_eval = ace_eval;
  }

  public String getAle() {
    return ale;
  }

  public void setAle(String ale) {
    this.ale = ale;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  public String getGaw() {
    return gaw;
  }

  public void setGaw(String gaw) {
    this.gaw = gaw;
  }

  public long getRta() {
    return rta;
  }

  public void setRta(long rta) {
    this.rta = rta;
  }

  public long getAcc() {
    return acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public String getAce() {
    return ace;
  }

  public void setAce(String ace) {
    this.ace = ace;
  }

  public long getRgc() {
    return rgc;
  }

  public void setRgc(long rgc) {
    this.rgc = rgc;
  }

  public long getRra() {
    return rra;
  }

  public void setRra(long rra) {
    this.rra = rra;
  }

  public long getRrc() {
    return rrc;
  }

  public void setRrc(long rrc) {
    this.rrc = rrc;
  }

  public String getLtn() {
    return ltn;
  }

  public void setLtn(String ltn) {
    this.ltn = ltn;
  }

  public long getCtl() {
    return ctl;
  }

  public void setCtl(long ctl) {
    this.ctl = ctl;
  }

  public long getRsa() {
    return rsa;
  }

  public void setRsa(long rsa) {
    this.rsa = rsa;
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

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public String getTier_note() {
    return tier_note;
  }

  public void setTier_note(String tier_note) {
    this.tier_note = tier_note;
  }

  public String getSrc_info() {
    return src_info;
  }

  public void setSrc_info(String src_info) {
    this.src_info = src_info;
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  @Override
  public void setSidetrack(boolean sidetrack) {
    // TODO Auto-generated method stub

  }

}
