/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.vmap1PlusPlus;

import java.util.HashMap;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

public class VMAP1PPLndareA extends VMAP1PPFeature implements IMiscArea {

  private AutreConstruction geoxObj;

  // VMAP1PlusPlus attributes
  private String nam, status, src_date, upd_date;
  private long exs, originform, targetscal, scamax, scamin;
  private double area;

  /**
   * @param type
   */
  public VMAP1PPLndareA(IPolygon polygon, HashMap<String, Object> attributes,
      @SuppressWarnings("unused") PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(polygon);
    this.setInitialGeom(polygon);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//
    this.setArea(polygon.area());

    this.src_date = (String) attributes.get("src_date");
    this.upd_date = (String) attributes.get("upd_date");
    this.nam = (String) attributes.get("nam");
    this.status = (String) attributes.get("status");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");
    this.scamax = getLongAttribute("scamax");
    this.scamin = getLongAttribute("scamin");
    this.exs = getLongAttribute("exs");

    this.setAttributeMap(null);

  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public long getOriginform() {
    return originform;
  }

  public void setOriginform(long originform) {
    this.originform = originform;
  }

  public long getTargetscal() {
    return targetscal;
  }

  public void setTargetscal(long targetscal) {
    this.targetscal = targetscal;
  }

  public long getScamax() {
    return scamax;
  }

  public void setScamax(long scamax) {
    this.scamax = scamax;
  }

  public long getScamin() {
    return scamin;
  }

  public void setScamin(long scamin) {
    this.scamin = scamin;
  }

  public String getNam() {
    return nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getExs() {
    return exs;
  }

  public void setExs(long exs) {
    this.exs = exs;
  }

  public double getArea() {
    return area;
  }

  public void setArea(double area) {
    this.area = area;
  }

}
