package fr.ign.cogit.geoxygene.style;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlAccessorType(XmlAccessType.FIELD)
public class Tide {
  // TODO Formula : 
  
  // WaterHeight mean
  @XmlTransient
  private double waterHeightMean = 0.0;
  
  @XmlElement(name = "WaterHeightMean")
  public double getWaterHeightMean() {
    return this.waterHeightMean;
  }

  public void setWaterHeightMean(double waterHeightMean) {
    this.waterHeightMean = waterHeightMean;
  }
  
  // TideRange
  @XmlTransient
  private double tideRange = 0.0;
  
  @XmlElement(name = "TideRange") 
  public double getTideRange() {
    return this.tideRange;
  }

  public void setTideRange(double tideRange) {
    this.tideRange = tideRange;
  }

  // Time acceleration
  @XmlTransient
  private double timeAcceleration = 1.0;
  
  @XmlElement(name = "TimeAcceleration")  
  public double getTimeAcceleration() {
    return this.timeAcceleration;
  }

  public void setTimeAcceleration(double timeAcceleration) {
    this.timeAcceleration = timeAcceleration;
  }
  
  // Tide Cycle Length
  @XmlTransient
  private double tideCycleLength = 43200.0;
  
  @XmlElement(name = "TideCycleLength")  
  public double getTideCycleLength() {
    return this.tideCycleLength;
  }

  public void setTideCycleLength(double tideCycleLength) {
    this.tideCycleLength = tideCycleLength;
  }
  
  // Tide Phase
  @XmlTransient
  private double tidePhase = 0.0;
  
  @XmlElement(name = "TidePhase")
  public double getTidePhase() {
    return this.tidePhase;
  }

  public void setTidePhase(Float tidePhase) {
    this.tidePhase = tidePhase;
  }
  
}
