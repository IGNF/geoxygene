/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.shom;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class SHOMPebbleGravelArea extends SHOMFeature implements
    ISimpleLandUseArea {

  // VMAP attributes
  private String typevale, ref_bdss;
  private double codesed, area;

  public SHOMPebbleGravelArea(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());

  }

  /**
   * @param type
   */
  public SHOMPebbleGravelArea(IPolygon poly,
      HashMap<String, Object> attributes, PeaRepDbType type) {
    this(poly);
    this.setAttributeMap(attributes);//

    this.typevale = getStringAttribute("TYPEVALE");
    this.ref_bdss = getStringAttribute("REF_BDSS");

    this.codesed = getDoubleAttribute("CodeSed");

    this.setAttributeMap(null);
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public void setGeom(IGeometry geom) {
    super.setGeom(geom);
    this.setArea(geom.area());
  }

  public double getArea() {
    return this.area;
  }

  public void setArea(double area) {
    this.area = area;
  }

  @Override
  public int getType() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setType(int type) {
    // TODO Auto-generated method stub

  }

  public String getTypevale() {
    return typevale;
  }

  public void setTypevale(String typevale) {
    this.typevale = typevale;
  }

  public String getRef_bdss() {
    return ref_bdss;
  }

  public void setRef_bdss(String ref_bdss) {
    this.ref_bdss = ref_bdss;
  }

  public double getCodesed() {
    return codesed;
  }

  public void setCodesed(double codesed) {
    this.codesed = codesed;
  }

}
