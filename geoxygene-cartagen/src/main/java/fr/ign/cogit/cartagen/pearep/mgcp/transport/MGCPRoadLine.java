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
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
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

  private long acc;
  // private int coe;
  private long fun;
  private long loc;
  private String ltn;
  private long mes;
  private String nam;
  private String nfi;
  private String nfn;
  private long rst;
  private long hct;
  private long smc;
  private long trs;
  private long caa;
  private String wd1;
  private long wtc;

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
    Object accAttr = attributes.get("acc");
    if (accAttr instanceof Long)
      this.acc = (Long) accAttr;
    else
      this.acc = new Long((Integer) accAttr);
    // this.coe = (Integer) attributes.get("coe");
    Object funAttr = attributes.get("fun");
    if (funAttr instanceof Long)
      this.fun = (Long) funAttr;
    else
      this.fun = new Long((Integer) funAttr);
    Object locAttr = attributes.get("loc");
    if (locAttr instanceof Long)
      this.loc = (Long) locAttr;
    else
      this.loc = new Long((Integer) locAttr);
    this.ltn = (String) attributes.get("ltn");
    Object mesAttr = attributes.get("mes");
    if (mesAttr instanceof Long)
      this.mes = (Long) mesAttr;
    else
      this.mes = new Long((Integer) mesAttr);
    this.nam = (String) attributes.get("nam");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    Object rstAttr = attributes.get("rst");
    if (rstAttr instanceof Long)
      this.rst = (Long) rstAttr;
    else
      this.rst = new Long((Integer) rstAttr);
    Object hctAttr = attributes.get("hct");
    if (hctAttr instanceof Long)
      this.hct = (Long) hctAttr;
    else
      this.hct = new Long((Integer) hctAttr);
    Object smcAttr = attributes.get("smc");
    if (smcAttr instanceof Long)
      this.smc = (Long) smcAttr;
    else
      this.smc = new Long((Integer) smcAttr);
    Object trsAttr = attributes.get("trs");
    if (trsAttr instanceof Long)
      this.trs = (Long) trsAttr;
    else
      this.trs = new Long((Integer) trsAttr);
    Object caaAttr = attributes.get("caa");
    if (caaAttr instanceof Long)
      this.caa = (Long) caaAttr;
    else
      this.caa = new Long((Integer) caaAttr);
    this.wd1 = (String) attributes.get("wd1");
    Object wtcAttr = attributes.get("wtc");
    if (wtcAttr instanceof Long)
      this.wtc = (Long) wtcAttr;
    else
      this.wtc = new Long((Integer) wtcAttr);
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
      SymbolShape symbolShape = CartAGenDoc.getInstance().getCurrentDataset()
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
      SymbolShape symbolShape = CartAGenDoc.getInstance().getCurrentDataset()
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

}
