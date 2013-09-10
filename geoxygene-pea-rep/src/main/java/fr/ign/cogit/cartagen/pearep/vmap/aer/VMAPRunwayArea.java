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

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPRunwayArea extends VMAPFeature implements IRunwayArea {

  private IAirportArea airport;
  private int z;

  // VMAP2i attributes
  private String fCode, name;
  private int cod, aoo, exs, len, rst, smc, wid;

  /**
   * @param type
   */
  public VMAPRunwayArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setGeom(poly);
    this.fCode = getStringAttribute("f_code");
    this.name = getStringAttribute("nam");
    this.cod = (Integer) attributes.get("cod");
    this.exs = (Integer) attributes.get("exs");
    this.aoo = (Integer) attributes.get("aoo");
    this.len = (Integer) attributes.get("len");
    this.rst = (Integer) attributes.get("rst");
    this.smc = (Integer) attributes.get("smc");
    this.wid = (Integer) attributes.get("wid");
    this.z = (Integer) attributes.get("zv2");
  }

  @Override
  public IAirportArea getAirport() {
    return airport;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getAoo() {
    return aoo;
  }

  public void setAoo(int aoo) {
    this.aoo = aoo;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }

  public int getRst() {
    return rst;
  }

  public void setRst(int rst) {
    this.rst = rst;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getWid() {
    return wid;
  }

  public void setWid(int wid) {
    this.wid = wid;
  }

  public void setAirport(IAirportArea airport) {
    this.airport = airport;
  }

  public void setZ(int z) {
    this.z = z;
  }

}
