/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.pop;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class VMAPMiscPopArea extends VMAPFeature implements IMiscArea {

  // VMAP2i attributes
  private String fCode, name, nfi, nfn;
  private int cod, fuc, nas, pfh, ppt, smc, stl;

  /**
   * @param type
   */
  public VMAPMiscPopArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setGeom(poly);
    this.fCode = (String) attributes.get("f_code");
    this.name = (String) attributes.get("nam");
    this.cod = (Integer) attributes.get("cod");
    this.fuc = (Integer) attributes.get("fuc");
    this.nas = (Integer) attributes.get("nas");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.pfh = (Integer) attributes.get("pfh");
    this.smc = (Integer) attributes.get("smc");
    this.stl = (Integer) attributes.get("stl");
    this.ppt = (Integer) attributes.get("ppt");
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

  public int getFuc() {
    return fuc;
  }

  public void setFuc(int fuc) {
    this.fuc = fuc;
  }

  public int getNas() {
    return nas;
  }

  public void setNas(int nas) {
    this.nas = nas;
  }

  public int getPfh() {
    return pfh;
  }

  public void setPfh(int pfh) {
    this.pfh = pfh;
  }

  public int getPpt() {
    return ppt;
  }

  public void setPpt(int ppt) {
    this.ppt = ppt;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getStl() {
    return stl;
  }

  public void setStl(int stl) {
    this.stl = stl;
  }

}
