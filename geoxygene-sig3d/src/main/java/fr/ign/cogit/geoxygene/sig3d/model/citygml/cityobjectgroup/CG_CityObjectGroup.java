package fr.ign.cogit.geoxygene.sig3d.model.citygml.cityobjectgroup;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_CityObjectGroup extends CG_CityObject {

  public CG_CityObjectGroup(CityObjectGroup cOG) {
    super(cOG);
    this.clazz = cOG.getClazz();
    this.getFunction().addAll(cOG.getFunction());
    this.getUsage().addAll(cOG.getUsage());
    this.geometry = ConvertyCityGMLGeometry.convertGMLGeometry(cOG
        .getGeometry());

    // int nbTyp = cOG.getGroupMember().size();

    System.out.println("Pour l'instant non géré");

  }

  protected Code clazz;
  protected List<Code> function;
  protected List<Code> usage;

  protected IGeometry geometry;

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
   * Gets the value of the geometry property.
   * 
   * @return possible object is {@link GeometryPropertyType }
   * 
   */
  public IGeometry getGeometry() {
    return this.geometry;
  }

  /**
   * Sets the value of the geometry property.
   * 
   * @param value allowed object is {@link GeometryPropertyType }
   * 
   */
  public void setGeometry(IGeometry value) {
    this.geometry = value;
  }

  public boolean isSetGeometry() {
    return (this.geometry != null);
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
