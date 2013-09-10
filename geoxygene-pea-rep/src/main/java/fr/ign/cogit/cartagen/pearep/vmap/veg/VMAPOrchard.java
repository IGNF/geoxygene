/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.veg;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPLandUseType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPOrchard extends VMAPFeature implements ISimpleLandUseArea {

  // VMAP attributes
  private String fCode, name, nfi, nfn;
  private int cod, dmt, pfh, smc, pro;

  /**
   * @param type
   */
  public VMAPOrchard(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.cod = (Integer) attributes.get("cod");
    this.dmt = (Integer) attributes.get("dmt");
    this.pfh = (Integer) attributes.get("pfh");
    this.smc = (Integer) attributes.get("smc");
    this.pro = (Integer) attributes.get("pro");
    this.fCode = getStringAttribute("f_code");
    this.name = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
  }

  @Override
  public int getType() {
    return VMAPLandUseType.ORCHARD.ordinal();
  }

  @Override
  public void setType(int type) {
  }

  @Override
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

  public int getDmt() {
    return dmt;
  }

  public void setDmt(int dmt) {
    this.dmt = dmt;
  }

  public int getPfh() {
    return pfh;
  }

  public void setPfh(int pfh) {
    this.pfh = pfh;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
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

  public int getPro() {
    return pro;
  }

  public void setPro(int pro) {
    this.pro = pro;
  }

}
