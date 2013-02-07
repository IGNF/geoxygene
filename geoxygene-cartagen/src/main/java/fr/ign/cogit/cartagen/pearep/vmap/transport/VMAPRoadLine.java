/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.transport;

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
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class VMAPRoadLine extends VMAPFeature implements IRoadLine {

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
  private String fCode;
  private int acc;
  private int coe;
  private int exs;
  private int hgt;
  private int loc;
  private int ltn;
  private int med;
  private String name;
  private String nfi;
  private String nfn;
  private int rst;
  private int rtt;
  private int smc;
  private int tuc;
  private int use;
  private int wd1;
  private int wtc;
  private int tileId;
  private long edgeId;

  /**
   * The generic constructor used to import VMAP data.
   * @param line
   * @param attributes
   */
  public VMAPRoadLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.computeImportance();
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;

    // attributes present in Vmap2i, Vmap1 and Vmap0
    this.fCode = (String) attributes.get("f_code");
    this.acc = (Integer) attributes.get("acc");
    this.exs = (Integer) attributes.get("exs");
    this.med = (Integer) attributes.get("med");
    this.rtt = (Integer) attributes.get("rtt");

    // attributes present in Vmap2i
    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.loc = (Integer) attributes.get("loc");
      this.ltn = (Integer) attributes.get("ltn");
      this.coe = (Integer) attributes.get("coe");
      this.hgt = (Integer) attributes.get("hgt");
      this.name = (String) attributes.get("nam");
      this.nfi = (String) attributes.get("nfi");
      this.nfn = (String) attributes.get("nfn");
      this.rst = (Integer) attributes.get("rst");
      this.smc = (Integer) attributes.get("smc");
      this.tuc = (Integer) attributes.get("tuc");
      this.use = (Integer) attributes.get("use");
      this.wtc = (Integer) attributes.get("wtc");
      this.wd1 = (Integer) attributes.get("wd1");
    }
    // attributes present in Vmap1
    if (type.equals(PeaRepDbType.VMAP1)) {
      this.loc = (Integer) attributes.get("loc");
      this.ltn = (Integer) attributes.get("ltn");
      this.name = (String) attributes.get("nam");
      this.rst = (Integer) attributes.get("rst");
      this.use = (Integer) attributes.get("use");
      this.wtc = (Integer) attributes.get("wtc");
      this.wd1 = (Integer) attributes.get("wd1");
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
    }
    // attributes present in Vmap0
    if (type.equals(PeaRepDbType.VMAP0)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
    }
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

  public int getRst() {
    return this.rst;
  }

  public void setRst(int rst) {
    this.rst = rst;
  }

  public int getRtt() {
    return this.rtt;
  }

  public void setRtt(int rtt) {
    this.rtt = rtt;
  }

  public int getTuc() {
    return this.tuc;
  }

  public void setTuc(int tuc) {
    this.tuc = tuc;
  }

  public int getWtc() {
    return this.wtc;
  }

  public void setWtc(int wtc) {
    this.wtc = wtc;
  }

  public int getMed() {
    return this.med;
  }

  public void setMed(int med) {
    this.med = med;
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

  public String getFcode() {
    return this.fCode;
  }

  public void setFcode(String fcode) {
    this.fCode = fcode;
  }

  public int getAcc() {
    return this.acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getCoe() {
    return this.coe;
  }

  public void setCoe(int coe) {
    this.coe = coe;
  }

  public int getExs() {
    return this.exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getHgt() {
    return this.hgt;
  }

  public void setHgt(int hgt) {
    this.hgt = hgt;
  }

  public int getLoc() {
    return this.loc;
  }

  public void setLoc(int loc) {
    this.loc = loc;
  }

  public int getLtn() {
    return this.ltn;
  }

  public void setLtn(int ltn) {
    this.ltn = ltn;
  }

  public String getNam() {
    return this.name;
  }

  public void setNam(String nam) {
    this.name = nam;
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

  public int getSmc() {
    return this.smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getUse() {
    return this.use;
  }

  public void setUse(int use) {
    this.use = use;
  }

  public int getWd1() {
    return this.wd1;
  }

  public void setWd1(int wd1) {
    this.wd1 = wd1;
  }

  public int getTileid() {
    return this.tileId;
  }

  public void setTileid(int tileid) {
    this.tileId = tileid;
  }

  public long getEdgeid() {
    return this.edgeId;
  }

  public void setEdgeid(long edgeid) {
    this.edgeId = edgeid;
  }

}
