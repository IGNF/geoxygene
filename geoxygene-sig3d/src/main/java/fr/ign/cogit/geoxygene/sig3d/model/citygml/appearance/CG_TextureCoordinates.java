package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.TextureCoordinates;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TextureCoordinates {

  protected List<Double> value;
  protected String ring;

  public CG_TextureCoordinates(TextureCoordinates textureCoordinates) {

    if (textureCoordinates.isSetRing()) {

      this.ring = textureCoordinates.getRing();

    }

    if (textureCoordinates.isSetValue()) {

      this.getValue().addAll(textureCoordinates.getValue());
    }

  }

  public List<Double> getValue() {
    if (this.value == null) {
      this.value = new ArrayList<Double>();
    }
    return this.value;
  }

  public boolean isSetValue() {
    return ((this.value != null) && (!this.value.isEmpty()));
  }

  public void unsetValue() {
    this.value = null;
  }

  /**
   * Gets the value of the ring property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getRing() {
    return this.ring;
  }

  /**
   * Sets the value of the ring property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setRing(String value) {
    this.ring = value;
  }

  public boolean isSetRing() {
    return (this.ring != null);
  }

}
