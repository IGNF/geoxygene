package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class InterpolationPoint {
  public InterpolationPoint() {
  }
  public InterpolationPoint(double data, String value) {
    this.data = data;
    this.value = value;
  }
  public InterpolationPoint(double data, Color color) {
    this.data = data;
    String rgb = Integer.toHexString(color.getRGB());
    rgb = "#" + rgb.substring(2, rgb.length()); //$NON-NLS-1$
    this.value = rgb;
    this.color = color;
  }

  @XmlElement(name = "Data")
  private double data;

  public double getData() {
    return this.data;
  }

  public void setData(double data) {
    this.data = data;
  }

  @XmlElement(name = "Value")
  private String value;

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  @XmlTransient
  private Color color = null;
  public Color getColor() {
    if (this.color == null) {
      this.color = Color.decode(this.value);
    }
    return this.color;
  }
}
