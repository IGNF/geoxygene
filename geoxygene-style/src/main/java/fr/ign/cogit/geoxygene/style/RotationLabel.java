package fr.ign.cogit.geoxygene.style;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Rotation")
public class RotationLabel {

  @XmlElement(name = "PropertyName")
  private PropertyName propertyName;

  public void setPropertyName(PropertyName propertyName) {
    this.propertyName = propertyName;
  }

  public PropertyName getPropertyName() {
    return this.propertyName;
  }

  /**
   * The rotation value in degrees.
   */
  @XmlTransient
  private double rotangle = 0.0;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.propertyName == null) ? 0 : this.propertyName.hashCode());
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
    RotationLabel other = (RotationLabel) obj;
    if (this.propertyName == null) {
      if (other.propertyName != null) {
        return false;
      }
    } else if (!this.propertyName.equals(other.propertyName)) {
      return false;
    }
    return true;
  }

  public double getRotationValue() {
    return this.rotangle;
  }

  /**
   * Returns the rotation value in degrees.
   * 
   * @return The rotation angle in degrees.
   */
  public double getRotationValue(Object object) {
    if (object == null) {
      return this.getRotationValue();
    }
    Object value = this.propertyName.evaluate(object);
    if (value == null) {
      return this.rotangle;
    }
    if (value instanceof BigDecimal) {
      return ((BigDecimal) value).doubleValue();
    }
    return (Double) this.propertyName.evaluate(object);

  }

  /**
   * @param newColor
   */
  public void setRotationValue(double rotangle) {
    this.rotangle = rotangle;
  }

}
