/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap.transport;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.VMAPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.routier.SurfaceRoute;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.SurfaceRouteImpl;

/**
 * VMAP implementation of the road area, that contains vehicle storage/parking
 * areas.
 * @author GTouya
 * 
 */
public class VMAPRoadArea extends GeneObjDefault implements IRoadArea,
    VMAPFeature {

  /**
   * Associated Geoxygene schema object
   */
  private SurfaceRoute geoxObj;

  // VMAP attributes
  private String fCode;
  private int cod, tuc, exs, smc;

  public VMAPRoadArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.geoxObj = new SurfaceRouteImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.cod = (Integer) attributes.get("cod");
    this.tuc = (Integer) attributes.get("tuc");
    this.exs = (Integer) attributes.get("exs");
    this.smc = (Integer) attributes.get("smc");
    this.fCode = (String) attributes.get("f_code");
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public void setGeoxObj(SurfaceRoute geoxObj) {
    this.geoxObj = geoxObj;
  }

  @Override
  public SurfaceRoute getGeoxObj() {
    return this.geoxObj;
  }

  public String getfCode() {
    return this.fCode;
  }

  public void setfCode(String fCode) {
    this.fCode = fCode;
  }

  public int getCod() {
    return this.cod;
  }

  public void setCod(int cod) {
    this.cod = cod;
  }

  public int getTuc() {
    return this.tuc;
  }

  public void setTuc(int tuc) {
    this.tuc = tuc;
  }

  public int getExs() {
    return this.exs;
  }

  public void setExs(int exs) {
    this.exs = exs;
  }

  public int getSmc() {
    return this.smc;
  }

  public void setSmc(int smc) {
    this.smc = smc;
  }

  @Override
  public Map<String, Object> getAttributeMap(VMAPFeature feat) {
    // TODO Auto-generated method stub
    return null;
  }

}
