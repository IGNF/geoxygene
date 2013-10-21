/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp.transport;

import java.awt.Color;
import java.util.HashMap;

import javax.persistence.Transient;

import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class MGCPRoadLine extends MGCPFeature implements IRoadLine {

  /**
   * Associated Geoxygene schema object
   */
  private TronconDeRoute geoxObj;
  private boolean deadEnd;
  private IRoadNode initialNode;
  private IRoadNode finalNode;
  private Direction direction;
  private int importance;
  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;

  private long acc, ace_eval, ale_eval, caa, fcsubtype, fun, hct, loc, mes,
      rst, smc, src_name, trs, upd_name, valid_stat, wtc, zval_type, scamax,
      scamin, originform, targetscal, idapp;

  private String ace, ale, cpyrt_note, date_bdi, hgt, ltn, nam, nfi, nfn,
      src_date, src_info, tier_note, txt, uid, upd_date, upd_info, valid_date,
      valid_info, wd1, natcon, status;

  /**
   * The generic constructor used to import VMAP data.
   * @param line
   * @param attributes
   * @param type
   */
  public MGCPRoadLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.computeImportance();
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setAttributeMap(attributes);//

    // attributes present in Mgcp++
    this.acc = getLongAttribute("acc");
    this.ace_eval = getLongAttribute("ace_eval");
    this.ale_eval = getLongAttribute("ale_eval");
    this.caa = getLongAttribute("caa");
    this.fcsubtype = getLongAttribute("fcsubtype");
    this.fun = getLongAttribute("fun");
    this.hct = getLongAttribute("hct");
    this.loc = getLongAttribute("loc");
    this.mes = getLongAttribute("mes");
    this.rst = getLongAttribute("rst");
    this.smc = getLongAttribute("smc");
    this.src_name = getLongAttribute("src_name");
    this.trs = getLongAttribute("trs");
    this.upd_name = getLongAttribute("upd_name");
    this.valid_stat = getLongAttribute("valid_stat");
    this.wtc = getLongAttribute("wtc");
    this.zval_type = getLongAttribute("zval_type");
    this.scamax = getLongAttribute("scamax");
    this.scamin = getLongAttribute("scamin");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");

    this.idapp = getLongAttribute("idapp");

    this.ace = getStringAttribute("ace");
    this.ale = getStringAttribute("ale");
    this.cpyrt_note = getStringAttribute("cpyrt_note");
    this.date_bdi = getStringAttribute("date_bdi");
    this.hgt = getStringAttribute("hgt");
    this.ltn = getStringAttribute("ltn");
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
    this.wd1 = getStringAttribute("wd1");
    this.natcon = getStringAttribute("natcon");
    this.status = getStringAttribute("status");

    this.setAttributeMap(null);
  }

  /**
   * The generic constructor used to correct road data.
   * @param line
   * @param attributes
   * @param type
   */
  public MGCPRoadLine(ILineString line) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.computeImportance();
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  @Override
  public int getImportance() {
    return this.importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  public INetworkNode getFinalNode() {
    return this.finalNode;
  }

  @Override
  public INetworkNode getInitialNode() {
    return this.initialNode;
  }

  @Override
  @Transient
  public double getWidth() {
    if (this.getSymbolId() == -1) {// old way of calculating the width

      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_1;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_2;
      }
      if (this.getImportance() == 3) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_3;
      }
      if (this.getImportance() == 4) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_4;
      }

    } else { // the new way of calculating the width
      SymbolShape symbolShape = CartAGenDocOld.getInstance().getCurrentDataset()
          .getSymbols().getSymbolShapeBySymbolID(this.getSymbolId());
      return symbolShape.ext_width;
    }

    return 0.0;
  }

  @Override
  @Transient
  public double getInternWidth() {
    if (this.getSymbolId() == -1) {// old way
      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_1;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_2;
      }
      if (this.getImportance() == 3) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_3;
      }
      if (this.getImportance() == 4) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_4;
      }
    } else { // the new way of calculating the width
      SymbolShape symbolShape = CartAGenDocOld.getInstance().getCurrentDataset()
          .getSymbols().getSymbolShapeBySymbolID(this.getSymbolId());
      return symbolShape.int_width;
    }
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
    this.finalNode = (IRoadNode) node;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = (IRoadNode) node;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.networkSectionType = type;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  public long getRst() {
    return this.rst;
  }

  public void setRst(long rst) {
    this.rst = rst;
  }

  public long getWtc() {
    return this.wtc;
  }

  public void setWtc(long wtc) {
    this.wtc = wtc;
  }

  public void setGeoxObj(TronconDeRoute geoxObj) {
    this.geoxObj = geoxObj;
  }

  @Override
  public TronconDeRoute getGeoxObj() {
    return this.geoxObj;
  }

  private void computeImportance() {
    this.setImportance(1);
  }

  @Override
  public Color getFrontColor() {
    if (this.getImportance() == 0) {
      return GeneralisationLegend.ROUTIER_COULEUR_0;
    }
    if (this.getImportance() == 1) {
      return GeneralisationLegend.ROUTIER_COULEUR_1;
    }
    if (this.getImportance() == 2) {
      return GeneralisationLegend.ROUTIER_COULEUR_2;
    }
    if (this.getImportance() == 3) {
      return GeneralisationLegend.ROUTIER_COULEUR_3;
    }
    if (this.getImportance() == 4) {
      return GeneralisationLegend.ROUTIER_COULEUR_4;
    }
    return null;
  }

  @Override
  public Color getSeparatorColor() {
    if (this.getImportance() == 4) {
      return GeneralisationLegend.ROUTIER_COULEUR_SEPARATEUR_4;
    }
    return null;
  }

  @Override
  public int getSymbolId() {
    return -1;
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  /*
   * public int getCoe() { return this.coe; }
   * 
   * public void setCoe(int coe) { this.coe = coe; }
   */

  public long getLoc() {
    return this.loc;
  }

  public void setLoc(long loc) {
    this.loc = loc;
  }

  public String getLtn() {
    return this.ltn;
  }

  public void setLtn(String ltn) {
    this.ltn = ltn;
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

  public long getSmc() {
    return this.smc;
  }

  public void setSmc(long smc) {
    this.smc = smc;
  }

  public String getWd1() {
    return this.wd1;
  }

  public void setWd1(String wd1) {
    this.wd1 = wd1;
  }

  public long getFun() {
    return this.fun;
  }

  public void setFun(long fun) {
    this.fun = fun;
  }

  public long getMes() {
    return this.mes;
  }

  public void setMes(long mes) {
    this.mes = mes;
  }

  public long getHct() {
    return this.hct;
  }

  public void setHct(long hct) {
    this.hct = hct;
  }

  public long getTrs() {
    return this.trs;
  }

  public void setTrs(long trs) {
    this.trs = trs;
  }

  public long getCaa() {
    return this.caa;
  }

  public void setCaa(long caa) {
    this.caa = caa;
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

  public String getHgt() {
    return hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
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

  public String getNatcon() {
    return natcon;
  }

  public void setNatcon(String natcon) {
    this.natcon = natcon;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getIdapp() {
    return idapp;
  }

  public void setIdapp(long idapp) {
    this.idapp = idapp;
  }

}
