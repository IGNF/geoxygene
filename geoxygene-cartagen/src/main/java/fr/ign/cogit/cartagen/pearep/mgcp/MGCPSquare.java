/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class MGCPSquare extends MGCPFeature implements ISquareArea {

  // MGCP attributes
  private String ace, ale, cpyrt_note, nam, nfi, nfn, src_date, src_info,
      tier_note, txt, uid, upd_date, upd_info;
  private long acc, ace_eval, ale_eval, src_name, upd_name, zval_type;
  private double area;

  public MGCPSquare(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());
    this.getSymbolGeom();
  }

  /**
   * @param type
   */
  public MGCPSquare(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    this(poly);

    this.acc = (Long) attributes.get("acc");
    this.ace = (String) attributes.get("ace");
    this.ace_eval = (Long) attributes.get("ace_eval");
    this.ale = (String) attributes.get("ale");
    this.ale_eval = (Long) attributes.get("ale_eval");
    this.nfi = (String) attributes.get("nfi");
    this.cpyrt_note = (String) attributes.get("cpyrt_note");
    this.nfn = (String) attributes.get("nfn");
    this.nam = (String) attributes.get("nam");
    this.src_date = (String) attributes.get("src_date");
    this.src_info = (String) attributes.get("src_info");
    this.src_name = (Long) attributes.get("src_name");
    this.txt = (String) attributes.get("txt");
    this.tier_note = (String) attributes.get("tier_note");
    this.uid = (String) attributes.get("uid");
    this.upd_date = (String) attributes.get("upd_date");
    this.upd_name = (Long) attributes.get("upd_name");
    this.upd_info = (String) attributes.get("upd_info");
    this.zval_type = (Long) attributes.get("zval_type");

  }

  public String getNam() {
    return this.nam;
  }

  public void setNam(String nam) {
    this.nam = nam;
  }

  public String getNfi() {
    return this.nfi;
  }

  public void setNfi(String nfi) {
    this.nfi = nfi;
  }

  public String getNfn() {
    return this.nfn;
  }

  public void setNfn(String nfn) {
    this.nfn = nfn;
  }

  public double getArea() {
    return this.area;
  }

  public void setArea(double area) {
    this.area = area;
  }

  public Long getAcc() {
    return this.acc;
  }

  public void setAcc(Long acc) {
    this.acc = acc;
  }

  public String getAce() {
    return ace;
  }

  public void setAce(String ace) {
    this.ace = ace;
  }

  public String getAle() {
    return ale;
  }

  public void setAle(String ale) {
    this.ale = ale;
  }

  public String getCpyrt_note() {
    return cpyrt_note;
  }

  public void setCpyrt_note(String cpyrt_note) {
    this.cpyrt_note = cpyrt_note;
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

  public String getTier_note() {
    return tier_note;
  }

  public void setTier_note(String tier_note) {
    this.tier_note = tier_note;
  }

  public long getAce_eval() {
    return ace_eval;
  }

  public void setAce_eval(long ace_eval) {
    this.ace_eval = ace_eval;
  }

  public long getAle_eval() {
    return ale_eval;
  }

  public void setAle_eval(long ale_eval) {
    this.ale_eval = ale_eval;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUpd_info() {
    return upd_info;
  }

  public void setUpd_info(String upd_info) {
    this.upd_info = upd_info;
  }

  public long getSrc_name() {
    return src_name;
  }

  public void setSrc_name(long src_name) {
    this.src_name = src_name;
  }

  public long getUpd_name() {
    return upd_name;
  }

  public void setUpd_name(long upd_name) {
    this.upd_name = upd_name;
  }

  public long getZval_type() {
    return zval_type;
  }

  public void setZval_type(long zval_type) {
    this.zval_type = zval_type;
  }

  public String getUpd_date() {
    return upd_date;
  }

  public void setUpd_date(String upd_date) {
    this.upd_date = upd_date;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return (IPolygon) getGeom();
  }

  @Override
  public IUrbanBlock getBlock() {
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {

  }

}
