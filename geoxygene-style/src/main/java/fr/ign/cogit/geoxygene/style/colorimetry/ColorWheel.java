package fr.ign.cogit.geoxygene.style.colorimetry;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This Class represents a color Wheel, it is a component of a
 * {@link ColorReferenceSystem}.
 * 
 * @author Charlotte Hoarau
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorWheel", propOrder = { "idSaturation", "slices" })
public class ColorWheel {
  /**
   * Saturation level of the color wheel.
   */
  @XmlElement(name = "Saturation")
  protected int idSaturation;

  /**
   * Return the saturation level of the color wheel.
   * @return idSaturation
   */
  public int getidSaturation() {
    return this.idSaturation;
  }

  /**
   * Set the saturation level of the chromatic wheel.
   * @param idSaturation the identifier of the saturation level of the color
   *          wheel.
   */
  public void setidSaturation(int idSaturation) {
    this.idSaturation = idSaturation;
  }

  /**
   * List of the slices of this color Wheel
   */
  @XmlElement(name = "Slice")
  protected List<ColorSlice> slices;

  /**
   * Return the list of the slices of this color Wheel.
   * @return slices the list of the slices of this color Wheel
   */
  public List<ColorSlice> getSlices() {
    if (this.slices == null) {
      this.slices = new ArrayList<ColorSlice>();
    }
    return this.slices;
  }

  /**
   * Set the list of the slices of this color Wheel.
   * @param slices the list of the slices of this color Wheel
   */
  public void setSlices(List<ColorSlice> slices) {
    this.slices = slices;
  }

}
