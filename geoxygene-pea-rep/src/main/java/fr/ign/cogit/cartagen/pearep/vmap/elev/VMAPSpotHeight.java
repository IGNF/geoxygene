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

import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.PointCote;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.PointCoteImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public class VMAPSpotHeight extends VMAPFeature implements ISpotHeight {

  /**
   * Associated Geoxygene schema object
   */
  private PointCote geoxObj;

  private String fCode;
  private int acc, ela, mcc, zv2;

  /**
   * Constructor
   */
  public VMAPSpotHeight(PointCote geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public VMAPSpotHeight(IPoint point, double value) {
    super();
    this.geoxObj = new PointCoteImpl(new ChampContinuImpl(), value, point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  /**
   * @param type
   */
  public VMAPSpotHeight(IPoint point, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new PointCoteImpl(new ChampContinuImpl(),
        (Integer) attributes.get("zv2"), point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.setZ((Integer) attributes.get("zv2"));
    this.acc = (Integer) attributes.get("acc");
    this.ela = (Integer) attributes.get("ela");
    this.mcc = (Integer) attributes.get("mcc");
    this.setZv2((Integer) attributes.get("zv2"));
    this.fCode = getStringAttribute("f_code");
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

  @Override
  public double getZ() {
    return this.geoxObj.getValeur();
  }

  @Override
  public void setZ(double z) {
    this.geoxObj.setValeur(z);
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

  public int getEla() {
    return ela;
  }

  public void setEla(int ela) {
    this.ela = ela;
  }

  public int getMcc() {
    return mcc;
  }

  public void setMcc(int mcc) {
    this.mcc = mcc;
  }

  public void setZv2(int zv2) {
    this.zv2 = zv2;
  }

  public int getZv2() {
    return zv2;
  }

}
