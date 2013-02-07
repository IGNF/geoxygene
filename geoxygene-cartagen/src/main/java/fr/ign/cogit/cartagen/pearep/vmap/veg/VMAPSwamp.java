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

public class VMAPSwamp extends VMAPFeature implements ISimpleLandUseArea {

  // VMAP attributes
  private String fCode;
  private int cod, dmt, veg, smc;

  /**
   * @param type
   */
  public VMAPSwamp(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.cod = (Integer) attributes.get("cod");
    this.dmt = (Integer) attributes.get("dmt");
    this.veg = (Integer) attributes.get("veg");
    this.smc = (Integer) attributes.get("smc");
    this.fCode = (String) attributes.get("f_code");
  }

  @Override
  public int getType() {
    return VMAPLandUseType.SWAMP.ordinal();
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

  public int getSmc() {
    return smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  public int getDmt() {
    return dmt;
  }

  public void setDmt(int dmt) {
    this.dmt = dmt;
  }

  public int getVeg() {
    return veg;
  }

  public void setVeg(int veg) {
    this.veg = veg;
  }

}
