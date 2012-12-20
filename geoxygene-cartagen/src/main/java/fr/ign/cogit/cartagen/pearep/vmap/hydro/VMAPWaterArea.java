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
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.SurfaceDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.SurfaceDEauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterArea Description: Surfaces d'eau
 * Author: J. Renard Date: 18/09/2009
 */
@Access(AccessType.PROPERTY)
public class VMAPWaterArea extends GeneObjDefault implements IWaterArea,
    VMAPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private SurfaceDEau geoxObj;
  private String name;

  // VMAP attributes
  private int rtp = 0, coe = 0, cod, exs, hfc = 8, hyc, loc, prc, smc,
      tid = 997, wid = -3276, scc = 997;
  private String fCode, nfi, nfn;
  private Integer tileId;
  private Long facId;
  private Integer zv2;
  private double length, width;

  /**
   * Cosntructor from lakeresa class from VMAP2i model
   */
  public VMAPWaterArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new SurfaceDEauImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);

    // attributes present in Vmap2i, Vmap1 and Vmap0
    this.fCode = (String) attributes.get("f_code");
    this.hyc = (Integer) attributes.get("hyc");
    this.name = (String) attributes.get("nam");

    // attributes present in lakeresa
    if (attributes.containsKey("scc"))
      this.scc = (Integer) attributes.get("scc");

    // attributes present in watrcrsa
    if (attributes.containsKey("exs"))
      this.exs = (Integer) attributes.get("exs");
    if (attributes.containsKey("tid"))
      this.tid = (Integer) attributes.get("tid");

    if (type.equals(PeaRepDbType.VMAP2i)) {
      this.cod = (Integer) attributes.get("cod");
      if (attributes.containsKey("coe"))
        this.coe = (Integer) attributes.get("coe");
      if (attributes.containsKey("hfc"))
        this.hfc = (Integer) attributes.get("hfc");
      this.loc = (Integer) attributes.get("loc");
      this.nfi = (String) attributes.get("nfi");
      this.nfn = (String) attributes.get("nfn");
      this.prc = (Integer) attributes.get("prc");
      if (attributes.containsKey("rtp"))
        this.rtp = (Integer) attributes.get("rtp");
      if (attributes.containsKey("scc"))
        this.scc = (Integer) attributes.get("scc");
      this.smc = (Integer) attributes.get("smc");
      if (attributes.containsKey("wid"))
        this.wid = (Integer) attributes.get("wid");
    }

    if (type.equals(PeaRepDbType.VMAP1)) {
      this.setZv2((Integer) attributes.get("zv2"));
      this.setTileId((Integer) attributes.get("tile_id"));
      this.setFacId((Long) attributes.get("fac_id"));
    }
    // computeLengthWidth();
  }

  /**
   * Compute the length of the water area using its skeleton and its width
   * divising the area by the computed length.
   */
  private void computeLengthWidth() {
    // TODO

  }

  /**
   * Default constructor, used by Hibernate.
   */
  public VMAPWaterArea() {
    super();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

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
  public boolean isEliminated() {
    return super.isEliminated();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getRtp() {
    return rtp;
  }

  public void setRtp(int rtp) {
    this.rtp = rtp;
  }

  public int getCoe() {
    return coe;
  }

  public void setCoe(int coe) {
    this.coe = coe;
  }

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
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

  public int getScc() {
    return scc;
  }

  public void setScc(int scc) {
    this.scc = scc;
  }

  public String getFCode() {
    return fCode;
  }

  public void setFCode(String fCode) {
    this.fCode = fCode;
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

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new SurfaceDEauImpl(new ReseauImpl(), this.getGeom());
    this.geoxObj.setNom(name);
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

  public Integer getTileId() {
    return tileId;
  }

  public void setTileId(Integer tileId) {
    this.tileId = tileId;
  }

  public Long getFacId() {
    return facId;
  }

  public void setFacId(Long facId) {
    this.facId = facId;
  }

  public Integer getZv2() {
    return zv2;
  }

  public void setZv2(Integer zv2) {
    this.zv2 = zv2;
  }

  /**
   * Useful to query on feature area.
   * @return
   */
  public double getArea() {
    return getGeom().area();
  }

  public void setLength(double length) {
    this.length = length;
  }

  public double getLength() {
    return length;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getWidth() {
    return width;
  }
}
