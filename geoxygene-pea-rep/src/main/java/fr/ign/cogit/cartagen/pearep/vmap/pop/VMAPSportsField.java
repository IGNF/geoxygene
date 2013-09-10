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

import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;

public class VMAPSportsField extends VMAPFeature implements ISportsField {

  /**
   * Associated Geoxygene schema object
   */
  private Batiment geoxObj;

  // VMAP attributes
  private String fCode, name, nfi, nfn, voi;
  private int cod, coe, exs, hgt, smc, ssc, z;

  /**
   * @param type
   */
  public VMAPSportsField(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new BatimentImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.cod = (Integer) attributes.get("cod");
    this.coe = (Integer) attributes.get("coe");
    this.smc = (Integer) attributes.get("smc");
    this.exs = (Integer) attributes.get("exs");
    this.hgt = (Integer) attributes.get("hgt");
    this.ssc = (Integer) attributes.get("ssc");
    this.z = (Integer) attributes.get("zv2");
    this.fCode = getStringAttribute("f_code");
    this.name = getStringAttribute("nam");
    this.nfi = getStringAttribute("nfi");
    this.nfn = getStringAttribute("nfn");
    this.voi = getStringAttribute("voi");
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IPolygon getSymbolGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public IFeature getGeoxObj() {
    return geoxObj;
  }

  @Override
  public IUrbanBlock getBlock() {
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
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

  public String getVoi() {
    return voi;
  }

  public void setVoi(String voi) {
    this.voi = voi;
  }

  public int getCod() {
    return cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getCoe() {
    return coe;
  }

  public void setCoe(int coe) {
    this.coe = coe;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getHgt() {
    return hgt;
  }

  public void setHgt(int hgt) {
    this.hgt = hgt;
  }

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getSsc() {
    return ssc;
  }

  public void setSsc(int ssc) {
    this.ssc = ssc;
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

}
