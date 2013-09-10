/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.hydro;

import java.util.HashMap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.TronconHydrographiqueImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterLine Description: Tronçons
 * hydrographiques Author: J. Renard Date: 18/09/2009
 */
@Access(AccessType.PROPERTY)
public class VMAPWaterLine extends VMAPFeature implements IWaterLine {

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
  private String name = "";
  private int importance;
  private NetworkSectionType type;

  // VMAP attributes
  private int acc, dir, exs, hfc, hyc, loc, prc, smc, tid, wid, tileId;
  private String fCode, nfi, nfn;
  private long edgeId;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public VMAPWaterLine() {
    super();
  }

  public VMAPWaterLine(ILineString line, String name, String fCode, int acc,
      int dir, int exs, int hfc, int hyc, int loc, int prc, int smc, int tid,
      int wid, String nfi, String nfn) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.name = name;
    this.fCode = fCode;
    this.acc = acc;
    this.dir = dir;
    this.exs = exs;
    this.hfc = hfc;
    this.hyc = hyc;
    this.loc = loc;
    this.prc = prc;
    this.smc = smc;
    this.tid = tid;
    this.wid = wid;
    this.nfi = nfi;
    this.nfn = nfn;
  }

  public VMAPWaterLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);

    // attributes present in Vmap2i, Vmap1 and Vmap0
    this.fCode = getStringAttribute("f_code_des");
    this.hyc = (Integer) attributes.get("hyc");
    this.name = getStringAttribute("nam");

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.acc = (Integer) attributes.get("acc");
      this.dir = (Integer) attributes.get("dir");
      this.exs = (Integer) attributes.get("exs");
      this.hfc = (Integer) attributes.get("hfc");
      this.loc = (Integer) attributes.get("loc");
      this.nfi = getStringAttribute("nfi");
      this.nfn = getStringAttribute("nfn");
      this.prc = (Integer) attributes.get("prc");
      this.smc = (Integer) attributes.get("smc");
      this.tid = (Integer) attributes.get("tid");
      this.wid = (Integer) attributes.get("wid");
    }
    if (type.equals(PeaRepDbType.VMAP1)) {
      this.exs = (Integer) attributes.get("exs");
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
      this.tid = (Integer) attributes.get("tid");
      this.wid = (Integer) attributes.get("wid");
    }
    if (type.equals(PeaRepDbType.VMAP0)) {
      this.tileId = (Integer) attributes.get("tile_id");
      this.edgeId = (Long) attributes.get("edg_id");
    }
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false,
        this.getGeom());
    this.geoxObj.setNom(name);
  }

  @Override
  public int getImportance() {
    return importance;
  }

  @Override
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return type;
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

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getDir() {
    return dir;
  }

  public void setDir(int dir) {
    this.dir = dir;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getHfc() {
    return hfc;
  }

  public void setHfc(int hfc) {
    this.hfc = hfc;
  }

  public int getHyc() {
    return hyc;
  }

  public void setHyc(int hyc) {
    this.hyc = hyc;
  }

  public int getLoc() {
    return loc;
  }

  public void setLoc(int loc) {
    this.loc = loc;
  }

  public int getPrc() {
    return prc;
  }

  public void setPrc(int prc) {
    this.prc = prc;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public int getWid() {
    return wid;
  }

  public void setWid(int wid) {
    this.wid = wid;
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

  public Integer getTileId() {
    return tileId;
  }

  public void setTileId(Integer tileId) {
    this.tileId = tileId;
  }

  public long getEdgeId() {
    return edgeId;
  }

  public void setEdgeId(long edgeId) {
    this.edgeId = edgeId;
  }

}
