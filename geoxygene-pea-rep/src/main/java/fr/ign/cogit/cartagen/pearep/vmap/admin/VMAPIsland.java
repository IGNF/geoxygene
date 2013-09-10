/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.admin;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IRiverSimpleIsland;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPIsland extends VMAPFeature implements IRiverSimpleIsland {

  // VMAP2i attributes
  private String fCode, name, nfi, nfn;
  private int cod, mcc, mcs, prc, smc;

  /**
   * @param type
   */
  public VMAPIsland(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setGeom(poly);
    this.fCode = getStringAttribute("f_code");
    this.name = getStringAttribute("nam");
    this.cod = (Integer) attributes.get("cod");
    this.mcc = (Integer) attributes.get("mcc");
    this.mcs = (Integer) attributes.get("mcs");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.prc = (Integer) attributes.get("prc");
    this.smc = (Integer) attributes.get("smc");
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

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getMcc() {
    return mcc;
  }

  public void setMcc(int mcc) {
    this.mcc = mcc;
  }

  public int getMcs() {
    return mcs;
  }

  public void setMcs(int mcs) {
    this.mcs = mcs;
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

}
