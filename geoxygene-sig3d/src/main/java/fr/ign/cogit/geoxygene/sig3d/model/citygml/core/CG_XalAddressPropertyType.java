package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import org.citygml4j.model.citygml.core.XalAddressProperty;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_XalAddressPropertyType {

  protected String addressDetails;

  public CG_XalAddressPropertyType(XalAddressProperty xAP) {
    this.addressDetails = xAP.getAddressDetails().toString();
  }

  /**
   * Gets the value of the addressDetails property.
   * 
   * @return possible object is {@link AddressDetails }
   * 
   */
  public String getAddressDetails() {
    return this.addressDetails;
  }

  /**
   * Sets the value of the addressDetails property.
   * 
   * @param value allowed object is {@link AddressDetails }
   * 
   */
  public void setAddressDetails(String value) {
    this.addressDetails = value;
  }

  public boolean isSetAddressDetails() {
    return (this.addressDetails != null);
  }

}
