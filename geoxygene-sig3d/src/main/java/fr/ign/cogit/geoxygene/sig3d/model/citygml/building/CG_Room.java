package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.citygml.building.BoundarySurfacePropertyImpl;
import org.citygml4j.impl.citygml.building.IntBuildingInstallationPropertyImpl;
import org.citygml4j.impl.citygml.building.InteriorFurniturePropertyImpl;
import org.citygml4j.impl.citygml.building.RoomImpl;
import org.citygml4j.jaxb.citygml._0_4.BoundarySurfacePropertyType;
import org.citygml4j.jaxb.citygml._0_4.IntBuildingInstallationPropertyType;
import org.citygml4j.jaxb.citygml._0_4.InteriorFurniturePropertyType;
import org.citygml4j.model.citygml.building.BoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.BuildingFurniture;
import org.citygml4j.model.citygml.building.CeilingSurface;
import org.citygml4j.model.citygml.building.ClosureSurface;
import org.citygml4j.model.citygml.building.FloorSurface;
import org.citygml4j.model.citygml.building.GroundSurface;
import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.building.IntBuildingInstallationProperty;
import org.citygml4j.model.citygml.building.InteriorFurnitureProperty;
import org.citygml4j.model.citygml.building.InteriorWallSurface;
import org.citygml4j.model.citygml.building.RoofSurface;
import org.citygml4j.model.citygml.building.Room;
import org.citygml4j.model.citygml.building.WallSurface;
import org.citygml4j.model.citygml.core.CityObject;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Room extends CG_CityObject {

  protected String clazz;
  protected List<String> function;
  protected List<String> usage;
  protected ISolid lod4Solid;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;

  protected List<CG_AbstractBoundarySurface> boundedBySurfaces;

  protected List<CG_BuildingFurniture> interiorFurniture;
  protected List<CG_IntBuildingInstallation> roomInstallation;

  public CG_Room(Room r) {

    super(r);
    this.clazz = r.getClazz();
    this.getFunction().addAll(r.getFunction());
    this.getUsage().addAll(r.getUsage());
    this.setLod4Solid(ConvertyCityGMLGeometry.convertGMLSolid(r.getLod4Solid()));
    this.setLod4MultiSurface(ConvertyCityGMLGeometry.convertGMLMultiSurface(r
        .getLod4MultiSurface()));

    if (r.getInteriorFurniture() != null) {

      int nbOuterBuildingInstallation = r.getInteriorFurniture().size();
      this.interiorFurniture = new ArrayList<CG_BuildingFurniture>();

      if (nbOuterBuildingInstallation > 0) {

        for (int i = 0; i < nbOuterBuildingInstallation; i++) {

          this.interiorFurniture.add(new CG_BuildingFurniture(r
              .getInteriorFurniture().get(i).getObject()));

        }

      }
    }

    if (r.getRoomInstallation() != null) {

      int nbIntBuildingInstallation = r.getRoomInstallation().size();
      this.roomInstallation = new ArrayList<CG_IntBuildingInstallation>();

      if (nbIntBuildingInstallation > 0) {

        for (int i = 0; i < nbIntBuildingInstallation; i++) {

          this.roomInstallation.add(new CG_IntBuildingInstallation(r
              .getRoomInstallation().get(i).getObject()));

        }

      }
    }

    if (r.getBoundedBySurface() != null) {

      int nbIntBuildingInstallation = r.getBoundedBySurface().size();

      if (nbIntBuildingInstallation > 0) {

        int nbBoundingSurface = r.getBoundedBySurface().size();

        for (int i = 0; i < nbBoundingSurface; i++) {
          BoundarySurface bs = r.getBoundedBySurface().get(i)
              .getBoundarySurface();

          if (bs instanceof RoofSurface) {

            this.getBoundedBySurfaces().add(
                new CG_RoofSurface((RoofSurface) bs));

          } else if (bs instanceof WallSurface) {

            this.getBoundedBySurfaces().add(
                new CG_WallSurface((WallSurface) bs));

          } else if (bs instanceof GroundSurface) {

            this.getBoundedBySurfaces().add(
                new CG_GroundSurface((GroundSurface) bs));

          } else if (bs instanceof ClosureSurface) {

            this.getBoundedBySurfaces().add(
                new CG_ClosureSurface((ClosureSurface) bs));

          } else if (bs instanceof CeilingSurface) {

            this.getBoundedBySurfaces().add(
                new CG_CeilingSurface((CeilingSurface) bs));

          } else if (bs instanceof InteriorWallSurface) {

            this.getBoundedBySurfaces().add(
                new CG_InteriorWallSurface((InteriorWallSurface) bs));

          } else if (bs instanceof FloorSurface) {

            this.getBoundedBySurfaces().add(
                new CG_FloorSurface((FloorSurface) bs));

          }

        }

      }
    }

  }

  @Override
  public CityObject export() {
    Room rOut = new RoomImpl();
    rOut.setClazz(this.getClazz());
    rOut.setFunction(this.getFunction());

    if (this.isSetLod4Solid()) {
      rOut.setLod4Solid(ConvertToCityGMLGeometry.convertSolidProperty(this
          .getLod4Solid()));
    }

    if (this.isSetLod4MultiSurface()) {
      rOut.setLod4MultiSurface(ConvertToCityGMLGeometry
          .convertMultiSurfaceProperty(this.getLod4MultiSurface()));
    }

    if (this.getInteriorFurniture() != null) {

      int nbOuterBuildingInstallation = this.getInteriorFurniture().size();
      rOut.setInteriorFurniture(new ArrayList<InteriorFurnitureProperty>());

      if (nbOuterBuildingInstallation > 0) {

        for (int i = 0; i < nbOuterBuildingInstallation; i++) {

          InteriorFurnitureProperty iFP = new InteriorFurniturePropertyImpl();

          iFP.setBuildingFurniture((BuildingFurniture) this
              .getInteriorFurniture().get(i).export());

          rOut.addInteriorFurniture(iFP);

        }

      }
    }

    if (this.getRoomInstallation() != null) {

      int nbIntBuildingInstallation = this.getRoomInstallation().size();
      rOut.setRoomInstallation(new ArrayList<IntBuildingInstallationProperty>());

      if (nbIntBuildingInstallation > 0) {

        for (int i = 0; i < nbIntBuildingInstallation; i++) {

          IntBuildingInstallationProperty iBIP = new IntBuildingInstallationPropertyImpl();
          
          
          iBIP.setIntBuildingInstallation((IntBuildingInstallation) this.getRoomInstallation().get(i).export());
          
          
          rOut.addRoomInstallation(iBIP);

        }

      }
    }

    if (this.getBoundedBySurfaces() != null) {

      int nbIntBuildingInstallation = this.getBoundedBySurfaces().size();

      if (nbIntBuildingInstallation > 0) {

        int nbBoundingSurface = this.getBoundedBySurfaces().size();
        
        rOut.setBoundedBySurface(new ArrayList<BoundarySurfaceProperty>());

        for (int i = 0; i < nbBoundingSurface; i++) {
          CG_AbstractBoundarySurface bs = this.getBoundedBySurfaces().get(i);
         BoundarySurface boundSurfOut =  (BoundarySurface) bs.export();
       
         BoundarySurfaceProperty eSP = new BoundarySurfacePropertyImpl();
         eSP.setBoundarySurface(boundSurfOut);
         rOut.getBoundedBySurface().add(eSP);
         
         

        }

      }
    }

    return rOut;
  }

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
   * Gets the value of the lod4Solid property.
   * 
   * @return possible object is {@link ISolid }
   * 
   */
  public ISolid getLod4Solid() {
    return this.lod4Solid;
  }

  /**
   * Sets the value of the lod4Solid property.
   * 
   * @param value allowed object is {@link ISolid }
   * 
   */
  public void setLod4Solid(ISolid value) {
    this.lod4Solid = value;
  }

  public boolean isSetLod4Solid() {
    return (this.lod4Solid != null);
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

  /**
   * Gets the value of the boundedBySurfaces property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the boundedBySurfaces property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getBoundedBySurfaces().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link BoundarySurfacePropertyType }
   * 
   * 
   */
  public List<CG_AbstractBoundarySurface> getBoundedBySurfaces() {
    if (this.boundedBySurfaces == null) {
      this.boundedBySurfaces = new ArrayList<CG_AbstractBoundarySurface>();
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

  /**
   * Gets the value of the interiorFurniture property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the interiorFurniture property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getInteriorFurniture().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link InteriorFurniturePropertyType }
   * 
   * 
   */
  public List<CG_BuildingFurniture> getInteriorFurniture() {
    if (this.interiorFurniture == null) {
      this.interiorFurniture = new ArrayList<CG_BuildingFurniture>();
    }
    return this.interiorFurniture;
  }

  public boolean isSetInteriorFurniture() {
    return ((this.interiorFurniture != null) && (!this.interiorFurniture
        .isEmpty()));
  }

  public void unsetInteriorFurniture() {
    this.interiorFurniture = null;
  }

  /**
   * Gets the value of the roomInstallation property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the roomInstallation property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getRoomInstallation().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link IntBuildingInstallationPropertyType }
   * 
   * 
   */
  public List<CG_IntBuildingInstallation> getRoomInstallation() {
    if (this.roomInstallation == null) {
      this.roomInstallation = new ArrayList<CG_IntBuildingInstallation>();
    }
    return this.roomInstallation;
  }

  public boolean isSetRoomInstallation() {
    return ((this.roomInstallation != null) && (!this.roomInstallation
        .isEmpty()));
  }

  public void unsetRoomInstallation() {
    this.roomInstallation = null;
  }

}
