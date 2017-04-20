package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.relief.MassPointRelief;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_MassPointRelief extends CG_AbstractReliefComponent {

  protected GM_MultiPoint reliefPoints;

  public CG_MassPointRelief(MassPointRelief mass) {
    super(mass);

    if (mass.isSetReliefPoints()) {
      this.getReliefPoints().addAll(
          ConvertCityGMLtoGeometry.convertGMLMultiPoint(mass.getReliefPoints()));
    }

  }

  /**
   * Gets the value of the reliefPoints property.
   * 
   * @return possible object is {@link GM_MultiPoint }
   * 
   */
  public GM_MultiPoint getReliefPoints() {
    return this.reliefPoints;
  }

  /**
   * Sets the value of the reliefPoints property.
   * 
   * @param value allowed object is {@link GM_MultiPoint }
   * 
   */
  public void setReliefPoints(GM_MultiPoint value) {
    this.reliefPoints = value;
  }

  public boolean isSetReliefPoints() {
    return (this.reliefPoints != null);
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
