package fr.ign.cogit.geoxygene.style.colorimetry;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This Class represents a color Slice, it is a component of a
 * {@link ColorWheel}.
 * 
 * @author Charlotte Hoarau
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorSlice", propOrder = { "hue", "colors",
    "idSaturationParentWheel" })
public class ColorSlice {

  /**
   * Common hue of the colors of the slice.
   */
  @XmlElement(name = "Hue")
  protected String hue;

  /**
   * Return the common hue of the colors of the slice.
   * @return The common hue of the colors of the slice.
   */
  public String getHue() {
    return this.hue;
  }

  /**
   * Set the common hue of the colors of the slice.
   * @param hue The common hue of the colors of the slice.
   */
  public void setHue(String hue) {
    this.hue = hue;
  }

  /**
   * List of the colors of the slice.
   */
  @XmlElement(name = "Color")
  protected List<ColorimetricColor> colors;

  /**
   * Return the list of the colors of the slice.
   * @return colors the list of the colors of the slice
   */
  public List<ColorimetricColor> getColors() {
    if (this.colors == null) {
      this.colors = new ArrayList<ColorimetricColor>();
    }
    return this.colors;
  }

  /**
   * Set the list of the colors of the slice.
   * @param colors the list of the colors of the slice
   */
  public void setColors(List<ColorimetricColor> colors) {
    this.colors = colors;
  }

  @XmlElement(name = "IdSaturationParentWheel")
  protected int idSaturationParentWheel;

  public int getIdSaturationParentWheel() {
    return this.idSaturationParentWheel;
  }

  public void setIdSaturationParentWheel(int idSaturationParentWheel) {
    this.idSaturationParentWheel = idSaturationParentWheel;
  }

}
