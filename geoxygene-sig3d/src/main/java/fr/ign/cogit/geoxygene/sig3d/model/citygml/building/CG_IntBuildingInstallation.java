package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_IntBuildingInstallation extends CG_CityObject {

  protected Code clazz;
  protected List<Code> function;
  protected List<Code> usage;
  protected IGeometry lod4Geometry;

  public CG_IntBuildingInstallation(IntBuildingInstallation iBI) {

    super(iBI);

    this.clazz = iBI.getClazz();
    this.getFunction().addAll(iBI.getFunction());
    this.getUsage().addAll(iBI.getUsage());
    this.setLod4Geometry(ConvertCityGMLtoGeometry.convertGMLGeometry(iBI
        .getLod4Geometry()));

  }

  @Override
  public AbstractCityObject export() {
    IntBuildingInstallation iBout = new IntBuildingInstallation();
    iBout.setClazz(this.getClazz());
    iBout.setFunction(this.getFunction());
    iBout.setUsage(this.getUsage());

    iBout.setLod4Geometry(ConvertToCityGMLGeometry.convertGeometryProperty(this
        .getLod4Geometry()));

    return iBout;
  }

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
   * Gets the value of the lod4Geometry property.
   * 
   * @return possible object is {@link GeometryPropertyType }
   * 
   */
  public IGeometry getLod4Geometry() {
    return this.lod4Geometry;
  }

  /**
   * Sets the value of the lod4Geometry property.
   * 
   * @param value allowed object is {@link GeometryPropertyType }
   * 
   */
  public void setLod4Geometry(IGeometry value) {
    this.lod4Geometry = value;
  }

  public boolean isSetLod4Geometry() {
    return (this.lod4Geometry != null);
  }

}
