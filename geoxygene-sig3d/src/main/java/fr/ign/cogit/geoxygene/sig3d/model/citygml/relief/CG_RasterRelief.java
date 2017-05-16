package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.relief.RasterRelief;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_RasterRelief extends CG_AbstractReliefComponent {

  protected CG_GridProperty grid;

  public CG_RasterRelief(RasterRelief rR) {
    super(rR);
    /*
     * this.setGrid(new GridPropertyType())
     */
    System.out.println("Classe non gérée : RasterReliefType ");

  }

  /**
   * Gets the value of the grid property.
   * 
   * @return possible object is {@link CG_GridProperty }
   * 
   */
  public CG_GridProperty getGrid() {
    return this.grid;
  }

  /**
   * Sets the value of the grid property.
   * 
   * @param value allowed object is {@link CG_GridProperty }
   * 
   */
  public void setGrid(CG_GridProperty value) {
    this.grid = value;
  }

  public boolean isSetGrid() {
    return (this.grid != null);
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
