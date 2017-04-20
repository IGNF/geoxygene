package fr.ign.cogit.geoxygene.sig3d.model.citygml.landuse;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_LandUse extends CG_CityObject {
  
  
  public CG_LandUse(){
    super();
  }
  

  public CG_LandUse(LandUse build) {
    super(build);

    if (build.isSetClazz()) {
      this.clazz = build.getClazz();
    }

    if (build.isSetFunction()) {

      this.function = build.getFunction();
    }

    if (build.isSetUsage()) {
      this.usage = build.getUsage();
    }

    if (build.isSetLod0MultiSurface()) {

      this.lod0MultiSurface = ConvertyCityGMLGeometry
          .convertGMLMultiSurface(build.getLod0MultiSurface());

    }

    if (build.isSetLod1MultiSurface()) {

      this.lod1MultiSurface = ConvertyCityGMLGeometry
          .convertGMLMultiSurface(build.getLod1MultiSurface());

    }

    if (build.isSetLod2MultiSurface()) {
      this.lod2MultiSurface = ConvertyCityGMLGeometry
          .convertGMLMultiSurface(build.getLod2MultiSurface());
    }

    if (build.isSetLod3MultiSurface()) {
      this.lod3MultiSurface = ConvertyCityGMLGeometry
          .convertGMLMultiSurface(build.getLod3MultiSurface());

    }

    if (build.isSetLod4MultiSurface()) {
      this.lod4MultiSurface = ConvertyCityGMLGeometry
          .convertGMLMultiSurface(build.getLod4MultiSurface());
    }

  }

  protected Code clazz;
  protected List<Code> function;
  protected List<Code> usage;
  protected IMultiSurface<IOrientableSurface> lod0MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod1MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;

  /**
   * Gets the value of the clazz property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public Code getClazz() {
    return this.clazz;
  }

  /**
   * Sets the value of the clazz property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setClazz(Code value) {
    this.clazz = value;
  }

  public boolean isSetClazz() {
    return (this.clazz != null);
  }

  /**
   * Gets the value of the function property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the function property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getFunction().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   * 
   * 
   */
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
   * Gets the value of the usage property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the usage property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getUsage().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   * 
   * 
   */
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

  /**
   * Gets the value of the lod0MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod0MultiSurface() {
    return this.lod0MultiSurface;
  }

  /**
   * Sets the value of the lod0MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod0MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod0MultiSurface = value;
  }

  public boolean isSetLod0MultiSurface() {
    return (this.lod0MultiSurface != null);
  }

  /**
   * Gets the value of the lod1MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod1MultiSurface() {
    return this.lod1MultiSurface;
  }

  /**
   * Sets the value of the lod1MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod1MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod1MultiSurface = value;
  }

  public boolean isSetLod1MultiSurface() {
    return (this.lod1MultiSurface != null);
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
