/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
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
public class MGCPWaterLine extends GeneObjDefault implements IWaterLine,
    MGCPFeature {

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

  private long acc, dir, wcc, hyp, loc, prc, smc, tid;
  private String nfi, nfn, wid;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public MGCPWaterLine() {
    super();
  }

  public MGCPWaterLine(ILineString line, String name, int acc, int dir,
      int wcc, int hyp, int loc, int prc, int smc, int tid, String wid,
      String nfi, String nfn) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.name = name;
    this.acc = acc;
    this.dir = dir;
    this.wcc = wcc;
    this.hyp = hyp;
    this.loc = loc;
    this.prc = prc;
    this.smc = smc;
    this.tid = tid;
    this.wid = wid;
    this.nfi = nfi;
    this.nfn = nfn;
  }

  public MGCPWaterLine(ILineString line, HashMap<String, Object> attributes,
      @SuppressWarnings("unused") PeaRepDbType type) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);

    // attributes present in Mgcp++
    this.acc = (Long) attributes.get("acc");
    this.dir = (Long) attributes.get("dir");
    this.wcc = (Long) attributes.get("wcc");
    this.hyp = (Long) attributes.get("hyp");
    this.loc = (Long) attributes.get("loc");
    this.name = (String) attributes.get("nam");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.prc = (Long) attributes.get("prc");
    this.smc = (Long) attributes.get("smc");
    this.tid = (Long) attributes.get("tid");
    this.wid = (String) attributes.get("wid");

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
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false,
        this.getGeom());
    this.geoxObj.setNom(this.name);
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

  @Override
  public Map<String, Object> getAttributeMap(MGCPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
