package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.relief.BreaklineRelief;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_BreaklineRelief extends CG_AbstractReliefComponent {

  protected IMultiCurve<IOrientableCurve> ridgeOrValleyLines;
  protected IMultiCurve<IOrientableCurve> breaklines;

  public CG_BreaklineRelief(BreaklineRelief bR) {

    super(bR);

    if (bR.isSetRidgeOrValleyLines()) {

      this.getRidgeOrValleyLines().addAll(
          ConvertyCityGMLGeometry.convertGMLMultiCurve(bR
              .getRidgeOrValleyLines()));

    }

    if (bR.isSetBreaklines()) {
      this.getBreaklines().addAll(
          ConvertyCityGMLGeometry.convertGMLMultiCurve(bR.getBreaklines()));

    }

  }

  /**
   * Gets the value of the ridgeOrValleyLines property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getRidgeOrValleyLines() {
    return this.ridgeOrValleyLines;
  }

  /**
   * Sets the value of the ridgeOrValleyLines property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setRidgeOrValleyLines(IMultiCurve<IOrientableCurve> value) {
    this.ridgeOrValleyLines = value;
  }

  public boolean isSetRidgeOrValleyLines() {
    return (this.ridgeOrValleyLines != null);
  }

  /**
   * Gets the value of the breaklines property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getBreaklines() {
    return this.breaklines;
  }

  /**
   * Sets the value of the breaklines property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setBreaklines(IMultiCurve<IOrientableCurve> value) {
    this.breaklines = value;
  }

  public boolean isSetBreaklines() {
    return (this.breaklines != null);
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
