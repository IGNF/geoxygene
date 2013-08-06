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

import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

public class VMAP1PPThermalP extends VMAP1PPFeature implements
    IReliefElementPoint {

  private AutreConstruction geoxObj;

  // VMAP1PlusPlus attributes
  private String date_bdi, f_code, gfid_v2i, src_date, src_info, txt, uid_,
      upd_date, upd_info, v2i_f_code, valid_date, valid_info;
  private long fcsubtype, keep, obj_rmq, src_dim, src_name, swt, upd_name,
      valid_stat, originform, targetscal;

  /**
   * @param type
   */
  public VMAP1PPThermalP(IPoint point, HashMap<String, Object> attributes,
      @SuppressWarnings("unused") PeaRepDbType type) {
    super();
    this.geoxObj = new AutreConstructionImpl(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
    this.setAttributeMap(attributes);//

    this.date_bdi = getStringAttribute("date_bdi");
    this.f_code = getStringAttribute("f_code");
    this.gfid_v2i = getStringAttribute("gfid_v2i");
    this.src_date = getStringAttribute("src_date");
    this.src_info = getStringAttribute("src_info");
    this.txt = getStringAttribute("txt");
    this.uid_ = getStringAttribute("uid_");
    this.upd_date = getStringAttribute("upd_date");
    this.upd_info = getStringAttribute("upd_info");
    this.v2i_f_code = getStringAttribute("v2i_f_code");
    this.valid_date = getStringAttribute("valid_date");
    this.valid_info = getStringAttribute("valid_info");
    this.src_info = getStringAttribute("src_info");

    this.fcsubtype = getLongAttribute("fcsubtype");
    this.keep = getLongAttribute("keep");
    this.obj_rmq = getLongAttribute("obj_rmq");
    this.src_dim = getLongAttribute("src_dim");
    this.src_name = getLongAttribute("src_name");
    this.valid_stat = getLongAttribute("valid_stat");
    this.originform = getLongAttribute("originform");
    this.targetscal = getLongAttribute("targetscal");
    this.swt = getLongAttribute("swt");

    this.setAttributeMap(null);

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

  public String getDate_bdi() {
    return date_bdi;
  }

  public void setDate_bdi(String date_bdi) {
    this.date_bdi = date_bdi;
  }

  public String getF_code() {
    return f_code;
  }

  public void setF_code(String f_code) {
    this.f_code = f_code;
  }

  public String getGfid_v2i() {
    return gfid_v2i;
  }

  public void setGfid_v2i(String gfid_v2i) {
    this.gfid_v2i = gfid_v2i;
  }

  public String getSrc_date() {
    return src_date;
  }

  public void setSrc_date(String src_date) {
    this.src_date = src_date;
  }

  public String getSrc_info() {
    return src_info;
  }

  public void setSrc_info(String src_info) {
    this.src_info = src_info;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public String getUid_() {
    return uid_;
  }

  public void setUid_(String uid_) {
    this.uid_ = uid_;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  public String getV2i_f_code() {
    return v2i_f_code;
  }

  public void setV2i_f_code(String v2i_f_code) {
    this.v2i_f_code = v2i_f_code;
  }

  public String getValid_date() {
    return valid_date;
  }

  public void setValid_date(String valid_date) {
    this.valid_date = valid_date;
  }

  public String getValid_info() {
    return valid_info;
  }

  public void setValid_info(String valid_info) {
    this.valid_info = valid_info;
  }

  public long getFcsubtype() {
    return fcsubtype;
  }

  public void setFcsubtype(long fcsubtype) {
    this.fcsubtype = fcsubtype;
  }

  public long getKeep() {
    return keep;
  }

  public void setKeep(long keep) {
    this.keep = keep;
  }

  public long getObj_rmq() {
    return obj_rmq;
  }

  public void setObj_rmq(long obj_rmq) {
    this.obj_rmq = obj_rmq;
  }

  public long getSrc_dim() {
    return src_dim;
  }

  public void setSrc_dim(long src_dim) {
    this.src_dim = src_dim;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getValid_stat() {
    return valid_stat;
  }

  public void setValid_stat(long valid_stat) {
    this.valid_stat = valid_stat;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
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

  public long getSwt() {
    return swt;
  }

  public void setSwt(long swt) {
    this.swt = swt;
  }

}
