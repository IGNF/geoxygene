/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.elev;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementLine Description: Eléments
 * linéaires caractéristiques du relief Author: J. Renard Date: 18/09/2009
 */

public class VMAPBluffLine extends VMAPFeature implements IReliefElementLine {

  /**
   * Associated Geoxygene schema object
   */
  private ElementCaracteristiqueDuRelief geoxObj;

  private String fCode;
  private int acc, dir, pfh, smc, sgc, wid;

  /**
   * Constructor
   */
  public VMAPBluffLine(ElementCaracteristiqueDuRelief geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public VMAPBluffLine(ILineString line) {
    super();
    this.geoxObj = new ElementCaracteristiqueDuReliefImpl(
        new ChampContinuImpl(), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  /**
   * @param type
   */
  public VMAPBluffLine(ILineString line, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new ElementCaracteristiqueDuReliefImpl(
        new ChampContinuImpl(), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.acc = (Integer) attributes.get("acc");
    this.dir = (Integer) attributes.get("dir");
    this.pfh = (Integer) attributes.get("pfh");
    this.smc = (Integer) attributes.get("smc");
    this.sgc = (Integer) attributes.get("sgc");
    this.wid = (Integer) attributes.get("wid");
    this.fCode = getStringAttribute("f_code");
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

  public int getAcc() {
    return acc;
  }

  public void setAcc(int acc) {
    this.acc = acc;
  }

  public int getDir() {
    return dir;
  }

  public void setDir(int dir) {
    this.dir = dir;
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

  public int getSgc() {
    return sgc;
  }

  public void setSgc(int sgc) {
    this.sgc = sgc;
  }

  public int getWid() {
    return wid;
  }

  public void setWid(int wid) {
    this.wid = wid;
  }

}
