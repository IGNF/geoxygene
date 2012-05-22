package fr.ign.cogit.geoxygene.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Interpolate {
  @XmlElement(name = "LookupValue")
  String lookupvalue;

  public String getLookupvalue() {
    return this.lookupvalue;
  }

  public void setLookupvalue(String lookupvalue) {
    this.lookupvalue = lookupvalue;
  }

  @XmlElement(name = "InterpolationPoint")
  private List<InterpolationPoint> interpolationPoint = new ArrayList<InterpolationPoint>(
      0);

  public List<InterpolationPoint> getInterpolationPoint() {
    return this.interpolationPoint;
  }

  public void setInterpolationPoint(List<InterpolationPoint> interpolationPoint) {
    this.interpolationPoint = interpolationPoint;
  }
}
