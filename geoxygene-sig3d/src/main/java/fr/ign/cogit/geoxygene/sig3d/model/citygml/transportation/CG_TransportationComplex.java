package fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.citygml4j.model.citygml.transportation.Railway;
import org.citygml4j.model.citygml.transportation.Road;
import org.citygml4j.model.citygml.transportation.Square;
import org.citygml4j.model.citygml.transportation.Track;
import org.citygml4j.model.citygml.transportation.TransportationComplex;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

@XmlSeeAlso({ CG_Square.class, CG_Track.class, CG_Railway.class, CG_Road.class })
public abstract class CG_TransportationComplex extends
    CG_AbstractTransportation {

  public void setFunction(List<String> function) {
    this.function = function;
  }


  public void setUsage(List<String> usage) {
    this.usage = usage;
  }

  protected List<String> function;
  protected List<String> usage;
  protected IMultiSurface<IOrientableSurface> lod1MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;
  protected List<IGeometry> lod0Network;

  protected List<CG_TrafficArea> trafficArea;
  protected List<CG_AuxiliaryTrafficArea> auxiliaryTrafficArea;
  
  
  public CG_TransportationComplex(){
    super();
  }
  

  public CG_TransportationComplex(TransportationComplex tO) {
    super(tO);

    if (tO.isSetFunction()) {
      this.getFunction().addAll(tO.getFunction());
    }

    if (tO.isSetUsage()) {
      this.getUsage().addAll(tO.getUsage());
    }

    if (tO.isSetLod1MultiSurface()) {
      this.setLod1MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(tO.getLod1MultiSurface()));
    }

    if (tO.isSetLod2MultiSurface()) {
      this.setLod2MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(tO.getLod2MultiSurface()));
    }

    if (tO.isSetLod3MultiSurface()) {
      this.setLod3MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(tO.getLod3MultiSurface()));
    }

    if (tO.isSetLod4MultiSurface()) {
      this.setLod4MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(tO.getLod4MultiSurface()));
    }

    if (tO.isSetLod0Network()) {
      int nbGeom = tO.getLod0Network().size();

      for (int i = 0; i < nbGeom; i++) {

        this.getLod0Network().add(
            ConvertyCityGMLGeometry.convertGMLGeometry(tO.getLod0Network()
                .get(i).getGeometricComplex()));

      }

    }

    if (tO.isSetTrafficArea()) {
      int nbGeom = tO.getTrafficArea().size();

      for (int i = 0; i < nbGeom; i++) {

        this.getTrafficArea().add(
            new CG_TrafficArea(tO.getTrafficArea().get(i).getObject()));

      }

    }

    if (tO.isSetAuxiliaryTrafficArea()) {
      int nbGeom = tO.getAuxiliaryTrafficArea().size();

      for (int i = 0; i < nbGeom; i++) {

        this.getAuxiliaryTrafficArea().add(
            new CG_AuxiliaryTrafficArea(tO.getAuxiliaryTrafficArea().get(i)
                .getObject()));

      }

    }

  }

  public static CG_TransportationComplex generateTransportationComplex(
      TransportationComplex tO) {

    if (tO instanceof Track) {

      return new CG_Track((Track) tO);

    } else if (tO instanceof Road) {

      return new CG_Road((Road) tO);

    } else if (tO instanceof Railway) {
      return new CG_Railway((Railway) tO);

    } else if (tO instanceof Square) {
      return new CG_Square((Square) tO);

    }

    System.out.println("Classe nongérée" + tO.getCityGMLClass());
    return null;

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

  public List<CG_TrafficArea> getTrafficArea() {
    if (this.trafficArea == null) {
      this.trafficArea = new ArrayList<CG_TrafficArea>();
    }
    return this.trafficArea;
  }

  public boolean isSetTrafficArea() {
    return ((this.trafficArea != null) && (!this.trafficArea.isEmpty()));
  }

  public void unsetTrafficArea() {
    this.trafficArea = null;
  }

  /**
   * Gets the value of the auxiliaryTrafficArea property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the auxiliaryTrafficArea property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getAuxiliaryTrafficArea().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link CG_AuxiliaryTrafficAreaProperty }
   * 
   * 
   */
  public List<CG_AuxiliaryTrafficArea> getAuxiliaryTrafficArea() {
    if (this.auxiliaryTrafficArea == null) {
      this.auxiliaryTrafficArea = new ArrayList<CG_AuxiliaryTrafficArea>();
    }
    return this.auxiliaryTrafficArea;
  }

  public boolean isSetAuxiliaryTrafficArea() {
    return ((this.auxiliaryTrafficArea != null) && (!this.auxiliaryTrafficArea
        .isEmpty()));
  }

  public void unsetAuxiliaryTrafficArea() {
    this.auxiliaryTrafficArea = null;
  }

  /**
   * Gets the value of the lod0Network property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the lod0Network property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getLod0Network().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link IGeometry }
   * 
   * 
   */
  public List<IGeometry> getLod0Network() {
    if (this.lod0Network == null) {
      this.lod0Network = new ArrayList<IGeometry>();
    }
    return this.lod0Network;
  }

  public boolean isSetLod0Network() {
    return ((this.lod0Network != null) && (!this.lod0Network.isEmpty()));
  }

  public void unsetLod0Network() {
    this.lod0Network = null;
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

}
