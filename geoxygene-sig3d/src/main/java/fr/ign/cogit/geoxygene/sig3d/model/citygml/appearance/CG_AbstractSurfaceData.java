package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.AbstractSurfaceData;
import org.citygml4j.model.citygml.appearance.AbstractTexture;
import org.citygml4j.model.citygml.appearance.X3DMaterial;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractSurfaceData extends FT_Feature {

  protected Boolean isFront;

  protected CG_AbstractSurfaceData() {
    super();
  }

  public CG_AbstractSurfaceData(AbstractSurfaceData sD) {
    super();
    if (sD.isSetIsFront()) {
      this.setIsFront(sD.getIsFront());
    }
  }

  public static CG_AbstractSurfaceData generateAbstractSurfaceData(
      AbstractSurfaceData sD) {

    if (sD instanceof X3DMaterial) {
      return new CG_X3DMaterial((X3DMaterial) sD);
    } else if (sD instanceof AbstractTexture) {
      return CG_AbstractTexture.generateAbstractTexture((AbstractTexture) sD);
    }

    System.out.println("Classe non gérée CG_AbstractSurfaceData "
        + sD.getCityGMLClass());

    return null;
  }

  /**
   * Gets the value of the isFront property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isIsFront() {
    return this.isFront;
  }

  /**
   * Sets the value of the isFront property.
   * 
   * @param value allowed object is {@link Boolean }
   * 
   */
  public void setIsFront(Boolean value) {
    this.isFront = value;
  }

  public boolean isSetIsFront() {
    return (this.isFront != null);
  }

}
