package fr.ign.cogit.geoxygene.style.thematic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class DiagramSizeElement {
  @XmlValue()
  private double value;

  public void setValue(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }
}
