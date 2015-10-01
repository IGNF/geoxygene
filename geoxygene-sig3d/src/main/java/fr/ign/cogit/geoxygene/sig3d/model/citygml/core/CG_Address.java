package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import org.citygml4j.model.citygml.core.Address;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Address extends FT_Feature {

  public CG_Address(Address a) {

    if (a.isSetMultiPoint()) {
      this.setMultiPoint(ConvertyCityGMLGeometry.convertGMLMultiPoint(a
          .getMultiPoint()));
    }

    if (a.isSetXalAddress()) {
      this.xalAddress = new CG_XalAddressPropertyType(a.getXalAddress());
    }

  }

  protected CG_XalAddressPropertyType xalAddress;
  protected GM_MultiPoint multiPoint;

  public CG_XalAddressPropertyType getXalAddress() {
    return this.xalAddress;
  }

  public void setXalAddress(CG_XalAddressPropertyType value) {
    this.xalAddress = value;
  }

  public boolean isSetXalAddress() {
    return (this.xalAddress != null);
  }

  public GM_MultiPoint getMultiPoint() {
    return this.multiPoint;
  }

  public void setMultiPoint(GM_MultiPoint value) {
    this.multiPoint = value;
  }

  public boolean isSetMultiPoint() {
    return (this.multiPoint != null);
  }

}
