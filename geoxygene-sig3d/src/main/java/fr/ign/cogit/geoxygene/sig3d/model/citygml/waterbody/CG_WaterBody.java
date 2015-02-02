package fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.jaxb.gml._3_1_1.MultiCurvePropertyType;
import org.citygml4j.jaxb.gml._3_1_1.MultiSurfacePropertyType;
import org.citygml4j.jaxb.gml._3_1_1.SolidPropertyType;
import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.waterbody.WaterBody;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

public class CG_WaterBody extends CG_AbstractWaterObject {

  public CG_WaterBody(WaterBody wB) {
    super(wB);

    if (wB.isSetClazz()) {
      this.clazz = wB.getClazz();
    }

    if (wB.isSetFunction()) {
      this.getFunction().addAll(wB.getFunction());
    }

    if (wB.isSetUsage()) {
      this.getUsage().addAll(wB.getUsage());
    }

    if (wB.isSetLod0MultiCurve()) {
      this.setLod0MultiCurve(ConvertyCityGMLGeometry.convertGMLMultiCurve(wB
          .getLod0MultiCurve()));
    }

    if (wB.isSetLod1MultiCurve()) {
      this.setLod1MultiCurve(ConvertyCityGMLGeometry.convertGMLMultiCurve(wB
          .getLod1MultiCurve()));
    }

    if (wB.isSetLod0MultiSurface()) {
      this.setLod0MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(wB.getLod0MultiSurface()));
    }

    if (wB.isSetLod1MultiSurface()) {
      this.setLod1MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(wB.getLod1MultiSurface()));
    }

    if (wB.isSetLod1Solid()) {
      this.setLod1Solid(ConvertyCityGMLGeometry.convertGMLSolid(wB
          .getLod1Solid()));
    }

    if (wB.isSetLod2Solid()) {
      this.setLod2Solid(ConvertyCityGMLGeometry.convertGMLSolid(wB
          .getLod2Solid()));
    }

    if (wB.isSetLod3Solid()) {
      this.setLod3Solid(ConvertyCityGMLGeometry.convertGMLSolid(wB
          .getLod3Solid()));
    }

    if (wB.isSetLod4Solid()) {
      this.setLod4Solid(ConvertyCityGMLGeometry.convertGMLSolid(wB
          .getLod4Solid()));
    }

    if (wB.isSetBoundedBySurface()) {

      int nbBS = wB.getBoundedBySurface().size();

      for (int i = 0; i < nbBS; i++) {

        this.getBoundedBySurfaces().add(
            CG_WaterBoundarySurface.generateAbstractWaterBoundarySurface(wB
                .getBoundedBySurface().get(i).getWaterBoundarySurface()));
      }

    }

  }

  protected String clazz;
  protected List<String> function;
  protected List<String> usage;
  protected IMultiCurve<IOrientableCurve> lod0MultiCurve;
  protected IMultiSurface<IOrientableSurface> lod0MultiSurface;
  protected IMultiCurve<IOrientableCurve> lod1MultiCurve;
  protected IMultiSurface<IOrientableSurface> lod1MultiSurface;
  protected ISolid lod1Solid;
  protected ISolid lod2Solid;
  protected ISolid lod3Solid;
  protected ISolid lod4Solid;
  protected List<CG_WaterBoundarySurface> boundedBySurfaces;

  /**
   * Gets the value of the clazz property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getClazz() {
    return this.clazz;
  }

  /**
   * Sets the value of the clazz property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setClazz(String value) {
    this.clazz = value;
  }

  public boolean isSetClazz() {
    return (this.clazz != null);
  }

  public List<String> getFunction() {
    if (this.function == null) {
      this.function = new ArrayList<String>();
    }
    return this.function;
  }

  public boolean isSetFunction() {
    return ((this.function != null) && (!this.function.isEmpty()));
  }

  public void unsetFunction() {
    this.function = null;
  }

  public List<String> getUsage() {
    if (this.usage == null) {
      this.usage = new ArrayList<String>();
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
   * Gets the value of the lod0MultiCurve property.
   * 
   * @return possible object is {@link MultiCurvePropertyType }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod0MultiCurve() {
    return this.lod0MultiCurve;
  }

  /**
   * Sets the value of the lod0MultiCurve property.
   * 
   * @param value allowed object is {@link MultiCurvePropertyType }
   * 
   */
  public void setLod0MultiCurve(IMultiCurve<IOrientableCurve> value) {
    this.lod0MultiCurve = value;
  }

  public boolean isSetLod0MultiCurve() {
    return (this.lod0MultiCurve != null);
  }

  /**
   * Gets the value of the lod0MultiSurface property.
   * 
   * @return possible object is {@link MultiSurfacePropertyType }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod0MultiSurface() {
    return this.lod0MultiSurface;
  }

  /**
   * Sets the value of the lod0MultiSurface property.
   * 
   * @param value allowed object is {@link MultiSurfacePropertyType }
   * 
   */
  public void setLod0MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod0MultiSurface = value;
  }

  public boolean isSetLod0MultiSurface() {
    return (this.lod0MultiSurface != null);
  }

  /**
   * Gets the value of the lod1MultiCurve property.
   * 
   * @return possible object is {@link MultiCurvePropertyType }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod1MultiCurve() {
    return this.lod1MultiCurve;
  }

  /**
   * Sets the value of the lod1MultiCurve property.
   * 
   * @param value allowed object is {@link MultiCurvePropertyType }
   * 
   */
  public void setLod1MultiCurve(IMultiCurve<IOrientableCurve> value) {
    this.lod1MultiCurve = value;
  }

  public boolean isSetLod1MultiCurve() {
    return (this.lod1MultiCurve != null);
  }

  /**
   * Gets the value of the lod1MultiSurface property.
   * 
   * @return possible object is {@link MultiSurfacePropertyType }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod1MultiSurface() {
    return this.lod1MultiSurface;
  }

  /**
   * Sets the value of the lod1MultiSurface property.
   * 
   * @param value allowed object is {@link MultiSurfacePropertyType }
   * 
   */
  public void setLod1MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod1MultiSurface = value;
  }

  public boolean isSetLod1MultiSurface() {
    return (this.lod1MultiSurface != null);
  }

  /**
   * Gets the value of the lod1Solid property.
   * 
   * @return possible object is {@link SolidPropertyType }
   * 
   */
  public ISolid getLod1Solid() {
    return this.lod1Solid;
  }

  /**
   * Sets the value of the lod1Solid property.
   * 
   * @param value allowed object is {@link SolidPropertyType }
   * 
   */
  public void setLod1Solid(ISolid value) {
    this.lod1Solid = value;
  }

  public boolean isSetLod1Solid() {
    return (this.lod1Solid != null);
  }

  /**
   * Gets the value of the lod2Solid property.
   * 
   * @return possible object is {@link SolidPropertyType }
   * 
   */
  public ISolid getLod2Solid() {
    return this.lod2Solid;
  }

  /**
   * Sets the value of the lod2Solid property.
   * 
   * @param value allowed object is {@link SolidPropertyType }
   * 
   */
  public void setLod2Solid(ISolid value) {
    this.lod2Solid = value;
  }

  public boolean isSetLod2Solid() {
    return (this.lod2Solid != null);
  }

  /**
   * Gets the value of the lod3Solid property.
   * 
   * @return possible object is {@link SolidPropertyType }
   * 
   */
  public ISolid getLod3Solid() {
    return this.lod3Solid;
  }

  /**
   * Sets the value of the lod3Solid property.
   * 
   * @param value allowed object is {@link SolidPropertyType }
   * 
   */
  public void setLod3Solid(ISolid value) {
    this.lod3Solid = value;
  }

  public boolean isSetLod3Solid() {
    return (this.lod3Solid != null);
  }

  /**
   * Gets the value of the lod4Solid property.
   * 
   * @return possible object is {@link SolidPropertyType }
   * 
   */
  public ISolid getLod4Solid() {
    return this.lod4Solid;
  }

  /**
   * Sets the value of the lod4Solid property.
   * 
   * @param value allowed object is {@link SolidPropertyType }
   * 
   */
  public void setLod4Solid(ISolid value) {
    this.lod4Solid = value;
  }

  public boolean isSetLod4Solid() {
    return (this.lod4Solid != null);
  }

  public List<CG_WaterBoundarySurface> getBoundedBySurfaces() {
    if (this.boundedBySurfaces == null) {
      this.boundedBySurfaces = new ArrayList<CG_WaterBoundarySurface>();
    }
    return this.boundedBySurfaces;
  }

  public boolean isSetBoundedBySurfaces() {
    return ((this.boundedBySurfaces != null) && (!this.boundedBySurfaces
        .isEmpty()));
  }

  public void unsetBoundedBySurfaces() {
    this.boundedBySurfaces = null;
  }

  @Override
  public CityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
