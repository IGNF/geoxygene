package fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody;

import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.waterbody.WaterSurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_WaterSurface extends CG_WaterBoundarySurface {

  protected String waterLevel;

  public CG_WaterSurface(WaterSurface wS) {
    super(wS);
    this.setWaterLevel(wS.getWaterLevel());
  }

  /**
   * Gets the value of the waterLevel property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getWaterLevel() {
    return this.waterLevel;
  }

  /**
   * Sets the value of the waterLevel property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setWaterLevel(String value) {
    this.waterLevel = value;
  }

  public boolean isSetWaterLevel() {
    return (this.waterLevel != null);
  }

  @Override
  public CityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
