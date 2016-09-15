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

  public void setColor(Color c) {
    this.color = c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.color == null) ? 0 : this.color.hashCode());
    long temp;
    temp = Double.doubleToLongBits(this.data);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result
        + ((this.value == null) ? 0 : this.value.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    InterpolationPoint other = (InterpolationPoint) obj;
    if (this.color == null) {
      if (other.color != null) {
        return false;
      }
    } else if (!this.color.equals(other.color)) {
      return false;
    }
    if (Double.doubleToLongBits(this.data) != Double
        .doubleToLongBits(other.data)) {
      return false;
    }
    if (this.value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
