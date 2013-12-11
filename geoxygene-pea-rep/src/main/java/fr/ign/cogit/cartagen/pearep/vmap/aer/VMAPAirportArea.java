/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.aer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IHelipadArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IHelipadPoint;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPAirportArea extends VMAPFeature implements IAirportArea {

  private String name;
  private int z;
  private Set<IRunwayArea> runwayAreas;
  private Set<IRunwayLine> runwayLines;
  private Set<ITaxiwayArea> taxiwayAreas;
  private Set<ITaxiwayLine> taxiwayLines;
  private Set<IHelipadArea> helipadAreas;
  private Set<IHelipadPoint> helipadPoint;

  // VMAP2i attributes
  private String fCode, iko;
  private int cod, fpt, exs, use, smc, zv2;

  /**
   * @param type
   */
  public VMAPAirportArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setGeom(poly);
    this.fCode = getStringAttribute("f_code");
    this.name = getStringAttribute("nam");
    this.cod = (Integer) attributes.get("cod");
    this.exs = (Integer) attributes.get("exs");
    this.fpt = (Integer) attributes.get("fpt");
    this.iko = getStringAttribute("iko");
    this.use = (Integer) attributes.get("use");
    this.smc = (Integer) attributes.get("smc");
    this.zv2 = (Integer) attributes.get("zv2");
    this.z = (Integer) attributes.get("zv3");
    runwayAreas = new HashSet<IRunwayArea>();
    runwayLines = new HashSet<IRunwayLine>();
    taxiwayAreas = new HashSet<ITaxiwayArea>();
    taxiwayLines = new HashSet<ITaxiwayLine>();
    helipadAreas = new HashSet<IHelipadArea>();
    helipadPoint = new HashSet<IHelipadPoint>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getFpt() {
    return fpt;
  }

  public void setFpt(int fpt) {
    this.fpt = fpt;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public String getIko() {
    return iko;
  }

  public void setIko(String iko) {
    this.iko = iko;
  }

  public int getUse() {
    return use;
  }

  public void setUse(int use) {
    this.use = use;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getZv2() {
    return zv2;
  }

  public void setZv2(int zv2) {
    this.zv2 = zv2;
  }

  @Override
  public Map<String, Object> getAttributeMap() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IRunwayLine> getRunwayLines() {
    return runwayLines;
  }

  @Override
  public Set<IRunwayArea> getRunwayAreas() {
    return runwayAreas;
  }

  @Override
  public Set<ITaxiwayArea> getTaxiwayAreas() {
    return taxiwayAreas;
  }

  @Override
  public Set<ITaxiwayLine> getTaxiwayLines() {
    return taxiwayLines;
  }

  @Override
  public Set<IHelipadArea> getHelipadAreas() {
    return helipadAreas;
  }

  @Override
  public Set<IHelipadPoint> getHelipadPoints() {
    return helipadPoint;
  }

  @Override
  public Set<IBuilding> getTerminals() {
    // TODO Auto-generated method stub
    return null;
  }

}
