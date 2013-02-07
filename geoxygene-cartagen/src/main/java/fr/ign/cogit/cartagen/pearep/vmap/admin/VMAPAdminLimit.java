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

import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.LimiteAdministrative;
import fr.ign.cogit.geoxygene.schemageo.impl.administratif.LimiteAdministrativeImpl;

public class VMAPAdminLimit extends VMAPFeature implements IAdminLimit {

  /**
   * Associated Geoxygene schema object
   */
  private LimiteAdministrative geoxObj;

  // VMAP2i attributes
  private String fCode, nm3, nm4;
  private int acc, bst, use;

  /**
   * @param type
   */
  public VMAPAdminLimit(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new LimiteAdministrativeImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.fCode = (String) attributes.get("f_code");
    this.nm3 = (String) attributes.get("nm3");
    this.acc = (Integer) attributes.get("acc");
    this.bst = (Integer) attributes.get("bst");
    this.use = (Integer) attributes.get("use");
    this.nm4 = (String) attributes.get("nm4");
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public String getNm3() {
    return nm3;
  }

  public void setNm3(String nm3) {
    this.nm3 = nm3;
  }

  public String getNm4() {
    return nm4;
  }

  public void setNm4(String nm4) {
    this.nm4 = nm4;
  }

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getBst() {
    return bst;
  }

  public void setBst(int bst) {
    this.bst = bst;
  }

  public int getUse() {
    return use;
  }

  public void setUse(int use) {
    this.use = use;
  }

}
