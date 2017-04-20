package fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody;


import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.waterbody.WaterSurface;
import org.citygml4j.model.gml.basicTypes.Code;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_WaterSurface extends CG_WaterBoundarySurface {

  protected Code waterLevel;

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
  public Code getWaterLevel() {
    return this.waterLevel;
  }

  /**
   * Sets the value of the waterLevel property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setWaterLevel(Code value) {
    this.waterLevel = value;
  }

  public boolean isSetWaterLevel() {
    return (this.waterLevel != null);
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
