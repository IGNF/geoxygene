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

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.activite.PointRepresentatifActiviteInteret;
import fr.ign.cogit.geoxygene.schemageo.impl.activite.PointRepresentatifActiviteInteretImpl;

public class VMAPMiscPopPoint extends VMAPFeature implements IMiscPoint {

  private PointRepresentatifActiviteInteret geoxObj;

  // VMAP attributes
  private String fCode, name, nfi, nfn;
  private int acc, fuc, exs, pfh, ppt, smc, stl;
  private long ara;

  /**
   * @param type
   */
  public VMAPMiscPopPoint(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new PointRepresentatifActiviteInteretImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.acc = (Integer) attributes.get("acc");
    this.ara = (Long) attributes.get("ara");
    this.fuc = (Integer) attributes.get("fuc");
    this.smc = (Integer) attributes.get("smc");
    this.exs = (Integer) attributes.get("exs");
    this.pfh = (Integer) attributes.get("pfh");
    this.ppt = (Integer) attributes.get("ppt");
    this.stl = (Integer) attributes.get("stl");
    this.fCode = (String) attributes.get("f_code");
    this.name = (String) attributes.get("nam");
    this.nfn = (String) attributes.get("nfn");
    this.nfi = (String) attributes.get("nfi");
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  public String getfCode() {
    return fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getExs() {
    return exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
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

  public long getAra() {
    return ara;
  }

  public void setAra(long ara) {
    this.ara = ara;
  }

  public int getFuc() {
    return fuc;
  }

  public void setFuc(int fuc) {
    this.fuc = fuc;
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

  public int getStl() {
    return stl;
  }

  public void setStl(int stl) {
    this.stl = stl;
  }

}
