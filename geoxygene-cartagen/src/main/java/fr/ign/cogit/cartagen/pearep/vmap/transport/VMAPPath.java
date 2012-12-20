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
import java.util.Map;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IPath;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class VMAPPath extends GeneObjDefault implements IPath, VMAPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private ArcReseau geoxObj;
  private boolean deadEnd;
  private IRoadNode initialNode;
  private IRoadNode finalNode;
  private Direction direction;
  private int importance;
  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;

  private String fCode;
  private int wid, acc, coe, exs, smc;
  private int tuc;
  private int wtc;

  /**
   * The generic constructor used to import VMAP data.
   * @param line
   * @param attributes
   */
  public VMAPPath(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.wid = (Integer) attributes.get("wid");
    this.fCode = (String) attributes.get("f_code");
    this.acc = (Integer) attributes.get("acc");
    this.exs = (Integer) attributes.get("exs");
    if (attributes.containsKey("tuc")) {
      this.tuc = (Integer) attributes.get("tuc");
    }
    this.smc = (Integer) attributes.get("smc");
    this.wtc = (Integer) attributes.get("wtc");
    this.coe = (Integer) attributes.get("coe");
    this.computeImportance();
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
  public double getInternWidth() {

    if (this.getSymbolId() == -1) {// old way
      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0 * 0.9;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0 * 1.1;
      }
    } else // the new way
    {
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
  public double getWidth() {
    if (this.getSymbolId() == -1) {// old way of calculating the width

      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0 * 0.9;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0 * 1.1;
      }

    } else // the new way of calculating the width
    {
      SymbolShape symbolShape = CartAGenDoc.getInstance().getCurrentDataset()
          .getSymbols().getSymbolShapeBySymbolID(this.getSymbolId());
      return symbolShape.ext_width;
    }
    return 0.0;
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

  public String getfCode() {
    return this.fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getWid() {
    return this.wid;
  }

  public void setWid(int wid) {
    this.wid = wid;
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

  public int getSmc() {
    return this.smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public void setGeoxObj(TronconDeRoute geoxObj) {
    this.geoxObj = geoxObj;
  }

  @Override
  public ArcReseau getGeoxObj() {
    return this.geoxObj;
  }

  private void computeImportance() {
    if (this.fCode.equals("AP050")) {
      this.setImportance(0);
    }
    this.setImportance(1);
  }

  @Override
  public Color getFrontColor() {
    return GeneralisationLegend.ROUTIER_COULEUR_0;
  }

  @Override
  public int getSymbolId() {
    return -1;
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
