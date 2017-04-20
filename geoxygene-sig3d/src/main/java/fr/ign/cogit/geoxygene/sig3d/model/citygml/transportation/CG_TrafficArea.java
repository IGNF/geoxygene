package fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.transportation.TrafficArea;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TrafficArea extends CG_AbstractTransportation {

  protected List<Code> usage;
  protected List<Code> function;
  protected Code surfaceMaterial;
  protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;

  public CG_TrafficArea(TrafficArea tO) {
    super(tO);

    if (tO.isSetUsage()) {
      this.getUsage().addAll(tO.getUsage());
    }

    if (tO.isSetFunction()) {
      this.getFunction().addAll(tO.getFunction());
    }

    if (tO.isSetSurfaceMaterial()) {
      this.setSurfaceMaterial(tO.getSurfaceMaterial());
    }

    if (tO.isSetLod2MultiSurface()) {
      this.setLod2MultiSurface(ConvertCityGMLtoGeometry
          .convertGMLMultiSurface(tO.getLod2MultiSurface()));
    }

    if (tO.isSetLod3MultiSurface()) {
      this.setLod3MultiSurface(ConvertCityGMLtoGeometry
          .convertGMLMultiSurface(tO.getLod3MultiSurface()));
    }

    if (tO.isSetLod4MultiSurface()) {
      this.setLod4MultiSurface(ConvertCityGMLtoGeometry
          .convertGMLMultiSurface(tO.getLod4MultiSurface()));
    }

  }

  public List<Code> getUsage() {
    if (this.usage == null) {
      this.usage = new ArrayList<Code>();
    }
    return this.usage;
  }

  public boolean isSetUsage() {
    return ((this.usage != null) && (!this.usage.isEmpty()));
  }

  public void unsetUsage() {
    this.usage = null;
  }

  public List<Code> getFunction() {
    if (this.function == null) {
      this.function = new ArrayList<Code>();
    }
    return this.function;
  }

  public boolean isSetFunction() {
    return ((this.function != null) && (!this.function.isEmpty()));
  }

  public void unsetFunction() {
    this.function = null;
  }

  /**
   * Gets the value of the surfaceMaterial property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public Code getSurfaceMaterial() {
    return this.surfaceMaterial;
  }

  /**
   * Sets the value of the surfaceMaterial property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setSurfaceMaterial(Code value) {
    this.surfaceMaterial = value;
  }

  public boolean isSetSurfaceMaterial() {
    return (this.surfaceMaterial != null);
  }

  /**
   * Gets the value of the lod2MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod2MultiSurface() {
    return this.lod2MultiSurface;
  }

  /**
   * Sets the value of the lod2MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod2MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod2MultiSurface = value;
  }

  public boolean isSetLod2MultiSurface() {
    return (this.lod2MultiSurface != null);
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

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }
}
