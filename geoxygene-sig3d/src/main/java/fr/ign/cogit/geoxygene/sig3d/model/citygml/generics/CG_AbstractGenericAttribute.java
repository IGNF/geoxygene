package fr.ign.cogit.geoxygene.sig3d.model.citygml.generics;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class CG_AbstractGenericAttribute {

  @XmlAttribute(name = "name", required = true)
  protected String name;

  /**
   * Gets the value of the name property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the value of the name property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setName(String value) {
    this.name = value;
  }

  public boolean isSetName() {
    return (this.name != null);
  }

}
