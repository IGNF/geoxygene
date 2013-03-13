package fr.ign.cogit.cartagen.pearep.mgcp.transport;

import java.awt.Color;
import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class MGCPPathLine extends MGCPFeature implements IPathLine {

  /**
   * Associated Geoxygene schema object
   */
  private TronconDeRoute geoxObj;
  private boolean deadEnd;
  private INetworkNode initialNode;
  private INetworkNode finalNode;
  private Direction direction;
  private int importance;
  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;

  private String upd_date, upd_info, cpyrt_note, src_info, txt, wd1, nfi, nfn,
      tier_note, nam, src_date, ace, ale;
  private long acc, ace_eval, ale_eval, uid, upd_name, src_name, zval_type,
      wtc;

  /**
   * @param type
   */
  public MGCPPathLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setGeoxObj(new TronconDeRouteImpl(new ReseauImpl(), false, line));
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setAttributeMap(attributes);

    // attributes present in Mgcp++
    Object accAttr = attributes.get("acc");
    if (accAttr instanceof Long)
      this.acc = (Long) accAttr;
    else
      this.acc = new Long((Integer) accAttr);
    Object ace_evalAttr = attributes.get("ace_eval");
    if (ace_evalAttr instanceof Long)
      this.ace_eval = (Long) ace_evalAttr;
    else
      this.ace_eval = new Long((Integer) ace_evalAttr);
    this.ale_eval = getLongAttribute("ale_eval");
    if (attributes.containsKey("uid"))
      this.uid = getLongAttribute("uid");
    this.src_name = getLongAttribute("src_name");
    this.zval_type = getLongAttribute("zval_type");
    if (attributes.containsKey("wd1"))
      this.wd1 = (String) attributes.get("wd1");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_info = (String) attributes.get("upd_info");
    this.wtc = getLongAttribute("wtc");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.upd_name = getLongAttribute("upd_name");
    this.src_info = (String) attributes.get("src_info");
    this.txt = (String) attributes.get("txt");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.tier_note = (String) attributes.get("tier_note");
    this.nam = (String) attributes.get("nam");
    this.src_date = (String) attributes.get("src_date");
    this.ace = (String) attributes.get("ace");
    this.ale = (String) attributes.get("ale");

    this.computeImportance();
  }

  private void computeImportance() {
    if (this.wd1 == null)
      importance = 0;
    else {
      importance = 2;
    }
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  @Override
  public double getWidth() {
    if (this.getImportance() == 3) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_1;
    }
    if (this.getImportance() == 2) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
    }
    if (this.getImportance() == 1) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0 * 2 / 3;
    } else {
      return 0.08;
    }
  }

  @Override
  public double getInternWidth() {
    // no intern width for paths symbols
    return 0.0;
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return false;
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
  public INetworkNode getInitialNode() {
    return initialNode;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = node;
  }

  @Override
  public INetworkNode getFinalNode() {
    return finalNode;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    this.finalNode = node;
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
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public INetworkFace getRightFace() {
    return null;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return networkSectionType;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.networkSectionType = type;
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
  public Color getFrontColor() {
    return null;
  }

  @Override
  public IFeature getGeoxObj() {
    return geoxObj;
  }

  public void setGeoxObj(TronconDeRoute tronconDeRoute) {
    this.geoxObj = tronconDeRoute;
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

  public long getWtc() {
    return wtc;
  }

  public void setWtc(long wtc) {
    this.wtc = wtc;
  }

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
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

  public String getWd1() {
    return wd1;
  }

  public void setWd1(String wd1) {
    this.wd1 = wd1;
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

}
