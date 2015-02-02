package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.model.citygml.building.Opening;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractOpening extends CG_CityObject {

  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;

  public CG_AbstractOpening(Opening op) {
    super(op);
    if (op.isSetLod3MultiSurface()) {
      this.lod3MultiSurface = ConvertyCityGMLGeometry.convertGMLMultiSurface(op
          .getLod3MultiSurface());

    }

    if (op.isSetLod4MultiSurface()) {
      this.lod4MultiSurface = ConvertyCityGMLGeometry.convertGMLMultiSurface(op
          .getLod4MultiSurface());
    }
  }

  /**
   * Gets the value of the lod3MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod3MultiSurface() {
    return this.lod3MultiSurface;
  }

  /**
   * Sets the value of the lod3MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod3MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod3MultiSurface = value;
  }

  public boolean isSetLod3MultiSurface() {
    return (this.lod3MultiSurface != null);
  }

  /**
   * Gets the value of the lod4MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod4MultiSurface() {
    return this.lod4MultiSurface;
  }

  /**
   * Sets the value of the lod4MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod4MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod4MultiSurface = value;
  }

  public boolean isSetLod4MultiSurface() {
    return (this.lod4MultiSurface != null);
  }

}
