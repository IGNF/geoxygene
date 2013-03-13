/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp.hydro;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.SurfaceDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.SurfaceDEauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterArea Description: Surfaces d'eau
 * Author: J. Renard Date: 18/09/2009
 */
public class MGCPRiverArea extends MGCPFeature implements IWaterArea {

  /**
   * Associated Geoxygene schema object
   */
  private SurfaceDEau geoxObj;
  private String name;

  public enum WaterAreaType {
    LAKE, RIVER
  }

  // VMAP attributes
  private long acc, hyp, prc, scc, smc;
  private String nfi, nfn;
  private double length, width;
  private WaterAreaType type = WaterAreaType.RIVER;

  /**
   * Cosntructor from lakeresa class from VMAP2i model
   * @param type
   */
  public MGCPRiverArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new SurfaceDEauImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    // attributes present in Mgcp++
    this.name = (String) attributes.get("nam");
    this.acc = getLongAttribute("acc");
    this.hyp = getLongAttribute("hyp");
    this.prc = getLongAttribute("prc");
    if (attributes.containsKey("scc")) {
      this.scc = getLongAttribute("scc");
      this.setType(WaterAreaType.LAKE);
    }
    this.smc = getLongAttribute("smc");

    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");

    // computeLengthWidth();
  }

  /**
   * Compute the length of the water area using its skeleton and its width
   * divising the area by the computed length.
   */
  @SuppressWarnings("unused")
  private void computeLengthWidth() {
    // TODO
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public MGCPRiverArea() {
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
    return this.name;
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

  public long getScc() {
    return this.scc;
  }

  public void setScc(long scc) {
    this.scc = scc;
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
  public void restoreGeoxObjects() {
    this.geoxObj = new SurfaceDEauImpl(new ReseauImpl(), this.getGeom());
    this.geoxObj.setNom(this.name);
  }

  /**
   * Useful to query on feature area.
   * @return
   */
  public double getArea() {
    return this.getGeom().area();
  }

  public void setLength(double length) {
    this.length = length;
  }

  public double getLength() {
    return this.length;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getWidth() {
    return this.width;
  }

  public long getAcc() {
    return this.acc;
  }

  public void setAcc(long acc) {
    this.acc = acc;
  }

  public long getHyp() {
    return this.hyp;
  }

  public void setHyp(long hyp) {
    this.hyp = hyp;
  }

  public WaterAreaType getType() {
    return type;
  }

  public void setType(WaterAreaType type) {
    this.type = type;
  }
}
