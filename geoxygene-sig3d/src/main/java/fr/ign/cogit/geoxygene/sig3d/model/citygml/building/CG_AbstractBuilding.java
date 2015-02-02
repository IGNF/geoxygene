package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.citygml4j.impl.citygml.building.BoundarySurfaceImpl;
import org.citygml4j.impl.citygml.building.BoundarySurfacePropertyImpl;
import org.citygml4j.impl.citygml.building.BuildingInstallationPropertyImpl;
import org.citygml4j.impl.citygml.building.BuildingPartPropertyImpl;
import org.citygml4j.impl.citygml.building.IntBuildingInstallationPropertyImpl;
import org.citygml4j.impl.citygml.building.InteriorRoomPropertyImpl;
import org.citygml4j.impl.gml.DoubleOrNullImpl;
import org.citygml4j.impl.gml.LengthImpl;
import org.citygml4j.impl.gml.MeasureOrNullListImpl;
import org.citygml4j.jaxb.gml._3_1_1.LengthType;
import org.citygml4j.jaxb.gml._3_1_1.MeasureOrNullListType;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.BuildingInstallation;
import org.citygml4j.model.citygml.building.BuildingInstallationProperty;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.building.IntBuildingInstallationProperty;
import org.citygml4j.model.citygml.building.InteriorRoomProperty;
import org.citygml4j.model.citygml.building.Room;
import org.citygml4j.model.gml.DoubleOrNull;
import org.citygml4j.model.gml.Length;
import org.citygml4j.model.gml.MeasureOrNullList;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_AbstractSite;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_Address;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractBuilding extends CG_AbstractSite {

  public CG_AbstractBuilding(){
    super();
  }
 
  public void complete(AbstractBuilding aB) {

    if (this.isSetClazz()) {
      aB.setClazz(this.getClazz());
    }

    if (this.isSetFunction()) {
      aB.setFunction(this.getFunction());
    }

    if (this.isSetUsage()) {
      aB.setUsage(this.getUsage());
    }

    if (this.isSetYearOfConstruction()) {
      aB.setYearOfConstruction(this.getYearOfConstruction());
    }

    if (this.isSetYearOfDemolition()) {
      aB.setYearOfDemolition(this.getYearOfDemolition());
    }

    if (this.isSetRoofType()) {
      aB.setRoofType(this.getRoofType());
    }

    if (this.isSetMeasuredHeight()) {

      Length l = new LengthImpl();
      l.setValue(this.getMeasuredHeight());
      aB.setMeasuredHeight(l);
    }

    if (this.isSetStoreyHeightsAboveGround()) {

      MeasureOrNullList mNL = new MeasureOrNullListImpl();

      double d = this.getStoreyHeightsAboveGround();
      List<DoubleOrNull> lD = new ArrayList<DoubleOrNull>();
      lD.add(new DoubleOrNullImpl(d));
      mNL.setDoubleOrNull(lD);
      aB.setStoreyHeightsAboveGround(mNL);
    }

    if (this.isSetStoreyHeightsBelowGround()) {

      MeasureOrNullList mNL = new MeasureOrNullListImpl();

      double d = this.getStoreyHeightsBelowGround();
      List<DoubleOrNull> lD = new ArrayList<DoubleOrNull>();
      lD.add(new DoubleOrNullImpl(d));
      mNL.setDoubleOrNull(lD);
      aB.setStoreyHeightsBelowGround(mNL);
    }

    if (this.isSetStoreysAboveGround()) {

      aB.setStoreysAboveGround(this.getStoreysAboveGround());
    }

    if (this.isSetStoreyHeightsBelowGround()) {
      aB.setStoreysBelowGround(this.getStoreysAboveGround());
    }

    if (this.isSetLod1Solid()) {
      aB.setLod1Solid(ConvertToCityGMLGeometry.convertSolidProperty(this
          .getLod1Solid()));
    }

    if (this.isSetLod2Solid()) {
      aB.setLod2Solid(ConvertToCityGMLGeometry.convertSolidProperty(this
          .getLod2Solid()));
    }

    if (this.isSetLod3Solid()) {
      aB.setLod3Solid(ConvertToCityGMLGeometry.convertSolidProperty(this
          .getLod3Solid()));
    }

    if (this.isSetLod4Solid()) {
      aB.setLod4Solid(ConvertToCityGMLGeometry.convertSolidProperty(this
          .getLod4Solid()));
    }

    if (this.isSetLod1MultiSurface()) {
      aB.setLod1MultiSurface( ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod1MultiSurface()));
    }
    
    
    if (this.isSetLod2MultiSurface()) {
      aB.setLod2MultiSurface( ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod2MultiSurface()));
    }


    
    if (this.isSetLod3MultiSurface()) {
      aB.setLod3MultiSurface( ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod3MultiSurface()));
    }

    
    if (this.isSetLod4MultiSurface()) {
      aB.setLod4MultiSurface( ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod4MultiSurface()));
    }


    if (this.isSetLod1TerrainIntersection()) {

      aB.setLod1TerrainIntersection(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod1TerrainIntersection()));
    }
    

    if (this.isSetLod2TerrainIntersection()) {

      aB.setLod2TerrainIntersection(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod2TerrainIntersection()));
    }
    
    
    if (this.isSetLod3TerrainIntersection()) {

      aB.setLod3TerrainIntersection(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod3TerrainIntersection()));
    }
    
    
    if (this.isSetLod4TerrainIntersection()) {

      aB.setLod4TerrainIntersection(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod4TerrainIntersection()));
    }

    
    if(this.isSetLod2MultiCurve()){
      aB.setLod2MultiCurve(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod2MultiCurve()));
    }
   
    if(this.isSetLod3MultiCurve()){
      aB.setLod3MultiCurve(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod3MultiCurve()));
    }
   

    if(this.isSetLod4MultiCurve()){
      aB.setLod4MultiCurve(ConvertToCityGMLGeometry.convertMultiCurveProperty(this.getLod4MultiCurve()));
    }
   

  
    if (this.getOuterBuildingInstallation() != null) {

      int nbOuterBuildingInstallation = this.getOuterBuildingInstallation()
          .size();
      
      

      aB.setOuterBuildingInstallation(new ArrayList<BuildingInstallationProperty>());
      
      if (nbOuterBuildingInstallation > 0) {

        for (int i = 0; i < nbOuterBuildingInstallation; i++) {
          
          BuildingInstallationProperty bIP = new BuildingInstallationPropertyImpl();
          
          bIP.setBuildingInstallation(    ((BuildingInstallation)this.getOuterBuildingInstallation().get(i).export()) );
          
          aB.addOuterBuildingInstallation(bIP);

        }

      }
    }

    if (this.getInteriorBuildingInstallation() != null) {

      int nbIntBuildingInstallation = this.getInteriorBuildingInstallation()
          .size();
      
      aB.setInteriorBuildingInstallation(new ArrayList<IntBuildingInstallationProperty>());

      if (nbIntBuildingInstallation > 0) {

        for (int i = 0; i < nbIntBuildingInstallation; i++) {
          
          IntBuildingInstallationProperty iBIP = new IntBuildingInstallationPropertyImpl();
          
          iBIP.setIntBuildingInstallation((IntBuildingInstallation) this.getInteriorBuildingInstallation().get(i).export()  );
          
          aB.addInteriorBuildingInstallation(iBIP);

        }

      }
    }

    if (this.getInteriorRoom() != null) {

      int nbRooms = this.getInteriorRoom().size();
      aB.setInteriorRoom(new ArrayList<InteriorRoomProperty>());

      for (int i = 0; i < nbRooms; i++) {
        
        InteriorRoomProperty iRP = new InteriorRoomPropertyImpl();
        
        
        iRP.setRoom((Room)this.getInteriorRoom().get(i).export());
        
        aB.addInteriorRoom(iRP);


      }
    }

    if (this.getConsistsOfBuildingPart() != null) {
      int nbPart = this.getConsistsOfBuildingPart().size();
      aB.setConsistsOfBuildingPart(new ArrayList<BuildingPartProperty>());

      for (int i = 0; i < nbPart; i++) {
        BuildingPartProperty bPP = new BuildingPartPropertyImpl();
          
        
        
        aB.addConsistsOfBuildingPart(bPP);
        
        
      }
    }

    if (this.getBoundedBySurfaces() != null) {
      int nbBoundingSurface = this.getBoundedBySurfaces().size();

      
      aB.setBoundedBySurface(new ArrayList<BoundarySurfaceProperty>());
      
      
      for (int i = 0; i < nbBoundingSurface; i++) {
        BoundarySurfaceImpl bs = (BoundarySurfaceImpl) this.getBoundedBySurfaces().get(i)
            .export();
        
        
        BoundarySurfaceProperty bSP = new BoundarySurfacePropertyImpl();
        bSP.setObject(bs);
        

        aB.getBoundedBySurface().add(bSP );
        


      }
    }

  }

  public CG_AbstractBuilding(AbstractBuilding build) {

    super(build);
    this.clazz = build.getClazz();
    this.function = build.getFunction();
    this.usage = build.getUsage();
    this.yearOfConstruction = build.getYearOfDemolition();
    this.yearOfDemolition = build.getYearOfDemolition();
    this.roofType = build.getRoofType();

    if (build.isSetMeasuredHeight()) {
      this.measuredHeight = build.getMeasuredHeight().getValue();
    }

    if (build.isSetStoreyHeightsAboveGround()) {
      this.storeyHeightsAboveGround = build.getStoreyHeightsAboveGround()
          .getDoubleOrNull().get(0).getDouble();
    }

    if (build.isSetStoreyHeightsBelowGround()) {
      this.storeyHeightsBelowGround = build.getStoreyHeightsAboveGround()
          .getDoubleOrNull().get(0).getDouble();
    }

    if (build.isSetStoreysAboveGround()) {
      this.storeysAboveGround = build.getStoreysAboveGround().intValue();
    }

    if (build.isSetStoreysBelowGround()) {
      this.storeysBelowGround = build.getStoreysBelowGround().intValue();
    }

    if (build.isSetLod1Solid()) {
      this.lod1Solid = ConvertyCityGMLGeometry.convertGMLSolid(build
          .getLod1Solid());
    }

    if (build.isSetLod2Solid()) {
      this.lod2Solid = ConvertyCityGMLGeometry.convertGMLSolid(build
          .getLod2Solid());
    }

    if (build.isSetLod3Solid()) {
      this.lod3Solid = ConvertyCityGMLGeometry.convertGMLSolid(build
          .getLod3Solid());
    }

    if (build.isSetLod4Solid()) {
      this.lod4Solid = ConvertyCityGMLGeometry.convertGMLSolid(build
          .getLod4Solid());
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

    if (build.isSetLod1TerrainIntersection()) {

      this.lod1TerrainIntersection = ConvertyCityGMLGeometry
          .convertGMLMultiCurve(build.getLod1TerrainIntersection());
    }

    if (build.isSetLod2TerrainIntersection()) {
      this.lod2TerrainIntersection = ConvertyCityGMLGeometry
          .convertGMLMultiCurve(build.getLod2TerrainIntersection());
    }

    if (build.isSetLod3TerrainIntersection()) {
      this.lod3TerrainIntersection = ConvertyCityGMLGeometry
          .convertGMLMultiCurve(build.getLod3TerrainIntersection());
    }

    if (build.isSetLod4TerrainIntersection()) {
      this.lod4TerrainIntersection = ConvertyCityGMLGeometry
          .convertGMLMultiCurve(build.getLod4TerrainIntersection());
    }

    if (build.isSetLod2MultiCurve()) {
      this.lod2MultiCurve = ConvertyCityGMLGeometry.convertGMLMultiCurve(build
          .getLod2MultiCurve());
    }

    if (build.isSetLod3MultiCurve()) {
      this.lod3MultiCurve = ConvertyCityGMLGeometry.convertGMLMultiCurve(build
          .getLod3MultiCurve());
    }

    if (build.isSetLod4MultiCurve()) {
      this.lod4MultiCurve = ConvertyCityGMLGeometry.convertGMLMultiCurve(build
          .getLod4MultiCurve());
    }

    if (build.getOuterBuildingInstallation() != null) {

      int nbOuterBuildingInstallation = build.getOuterBuildingInstallation()
          .size();
      this.outerBuildingInstallation = new ArrayList<CG_BuildingInstallation>();

      if (nbOuterBuildingInstallation > 0) {

        for (int i = 0; i < nbOuterBuildingInstallation; i++) {

          this.outerBuildingInstallation.add(new CG_BuildingInstallation(build
              .getOuterBuildingInstallation().get(i).getObject()));

        }

      }
    }

    if (build.getInteriorBuildingInstallation() != null) {

      int nbIntBuildingInstallation = build.getInteriorBuildingInstallation()
          .size();
      this.interiorBuildingInstallation = new ArrayList<CG_IntBuildingInstallation>();

      if (nbIntBuildingInstallation > 0) {

        for (int i = 0; i < nbIntBuildingInstallation; i++) {

          this.interiorBuildingInstallation.add(new CG_IntBuildingInstallation(
              build.getInteriorBuildingInstallation().get(i).getObject()));

        }

      }
    }

    if (build.getInteriorRoom() != null) {

      int nbRooms = build.getInteriorRoom().size();

      for (int i = 0; i < nbRooms; i++) {
        this.getInteriorRoom().add(
            new CG_Room(build.getInteriorRoom().get(i).getRoom()));

      }
    }

    if (build.getConsistsOfBuildingPart() != null) {
      int nbPart = build.getConsistsOfBuildingPart().size();

      for (int i = 0; i < nbPart; i++) {

        this.getConsistsOfBuildingPart().add(
            new CG_BuildingPart(build.getConsistsOfBuildingPart().get(i)
                .getBuildingPart()));
      }
    }

    if (build.getAddress() != null) {
      int nbAdress = build.getAddress().size();
      for (int i = 0; i < nbAdress; i++) {

        this.getAddress().add(
            new CG_Address(build.getAddress().get(i).getAddress()));
      }

    }

    if (build.getBoundedBySurface() != null) {
      int nbBoundingSurface = build.getBoundedBySurface().size();

      for (int i = 0; i < nbBoundingSurface; i++) {
        BoundarySurface bs = build.getBoundedBySurface().get(i)
            .getBoundarySurface();

        this.getBoundedBySurfaces().add(
            CG_AbstractBoundarySurface.generateBoundarySurface(bs));

      }
    }

  }

  protected String clazz;
  protected List<String> function;
  protected List<String> usage;

  protected GregorianCalendar yearOfConstruction;

  protected GregorianCalendar yearOfDemolition;
  protected String roofType;
  protected Double /* LengthType */measuredHeight = Double.NaN;

  protected int storeysAboveGround = -1;

  protected int storeysBelowGround = -1;
  protected double storeyHeightsAboveGround = -1;
  protected double storeyHeightsBelowGround = -1;
  protected ISolid lod1Solid;
  protected IMultiSurface<IOrientableSurface> lod1MultiSurface;
  protected IMultiCurve<IOrientableCurve> lod1TerrainIntersection;
  protected ISolid lod2Solid;
  protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
  protected IMultiCurve<IOrientableCurve> lod2MultiCurve;
  protected IMultiCurve<IOrientableCurve> lod2TerrainIntersection;
  protected List<CG_BuildingInstallation> outerBuildingInstallation;
  protected List<CG_IntBuildingInstallation> interiorBuildingInstallation;
  protected List<CG_AbstractBoundarySurface> boundedBySurfaces;
  protected ISolid lod3Solid;
  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiCurve<IOrientableCurve> lod3MultiCurve;
  protected IMultiCurve<IOrientableCurve> lod3TerrainIntersection;
  protected ISolid lod4Solid;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;
  protected IMultiCurve<IOrientableCurve> lod4MultiCurve;
  protected IMultiCurve<IOrientableCurve> lod4TerrainIntersection;
  protected List<CG_Room> interiorRoom;
  protected List<CG_BuildingPart> consistsOfBuildingPart;
  protected List<CG_Address> address;
  protected List<Object> genericApplicationPropertyOfAbstractBuilding;

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
   * Gets the value of the yearOfConstruction property.
   * 
   * @return possible object is {@link XMLGregorianCalendar }
   * 
   */
  public GregorianCalendar getYearOfConstruction() {
    return this.yearOfConstruction;
  }

  /**
   * Sets the value of the yearOfConstruction property.
   * 
   * @param value allowed object is {@link XMLGregorianCalendar }
   * 
   */
  public void setYearOfConstruction(GregorianCalendar value) {
    this.yearOfConstruction = value;
  }

  public boolean isSetYearOfConstruction() {
    return (this.yearOfConstruction != null);
  }

  /**
   * Gets the value of the yearOfDemolition property.
   * 
   * @return possible object is {@link XMLGregorianCalendar }
   * 
   */
  public GregorianCalendar getYearOfDemolition() {
    return this.yearOfDemolition;
  }

  /**
   * Sets the value of the yearOfDemolition property.
   * 
   * @param value allowed object is {@link XMLGregorianCalendar }
   * 
   */
  public void setYearOfDemolition(GregorianCalendar value) {
    this.yearOfDemolition = value;
  }

  public boolean isSetYearOfDemolition() {
    return (this.yearOfDemolition != null);
  }

  /**
   * Gets the value of the roofType property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getRoofType() {
    return this.roofType;
  }

  /**
   * Sets the value of the roofType property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setRoofType(String value) {
    this.roofType = value;
  }

  public boolean isSetRoofType() {
    return (this.roofType != null);
  }

  /**
   * Gets the value of the measuredHeight property.
   * 
   * @return possible object is {@link LengthType }
   * 
   */
  public double getMeasuredHeight() {
    return this.measuredHeight;
  }

  /**
   * Sets the value of the measuredHeight property.
   * 
   * @param value allowed object is {@link LengthType }
   * 
   */
  public void setMeasuredHeight(double value) {
    this.measuredHeight = value;
  }

  public boolean isSetMeasuredHeight() {
    return (!Double.isNaN(this.measuredHeight));
  }

  /**
   * Gets the value of the storeysAboveGround property.
   * 
   * @return possible object is {@link BigInteger }
   * 
   */
  public int getStoreysAboveGround() {
    return this.storeysAboveGround;
  }

  /**
   * Sets the value of the storeysAboveGround property.
   * 
   * @param value allowed object is {@link BigInteger }
   * 
   */
  public void setStoreysAboveGround(int value) {
    this.storeysAboveGround = value;
  }

  public boolean isSetStoreysAboveGround() {
    return (this.storeysAboveGround != -1);
  }

  /**
   * Gets the value of the storeysBelowGround property.
   * 
   * @return possible object is {@link BigInteger }
   * 
   */
  public int getStoreysBelowGround() {
    return this.storeysBelowGround;
  }

  /**
   * Sets the value of the storeysBelowGround property.
   * 
   * @param value allowed object is {@link BigInteger }
   * 
   */
  public void setStoreysBelowGround(int value) {
    this.storeysBelowGround = value;
  }

  public boolean isSetStoreysBelowGround() {
    return (this.storeysBelowGround != -1);
  }

  /**
   * Gets the value of the storeyHeightsAboveGround property.
   * 
   * @return possible object is {@link MeasureOrNullListType }
   * 
   */
  public double getStoreyHeightsAboveGround() {
    return this.storeyHeightsAboveGround;
  }

  /**
   * Sets the value of the storeyHeightsAboveGround property.
   * 
   * @param value allowed object is {@link MeasureOrNullListType }
   * 
   */
  public void setStoreyHeightsAboveGround(double value) {
    this.storeyHeightsAboveGround = value;
  }

  public boolean isSetStoreyHeightsAboveGround() {
    return (this.storeyHeightsAboveGround != -1);
  }

  /**
   * Gets the value of the storeyHeightsBelowGround property.
   * 
   * @return possible object is {@link MeasureOrNullListType }
   * 
   */
  public double getStoreyHeightsBelowGround() {
    return this.storeyHeightsBelowGround;
  }

  /**
   * Sets the value of the storeyHeightsBelowGround property.
   * 
   * @param value allowed object is {@link MeasureOrNullListType }
   * 
   */
  public void setStoreyHeightsBelowGround(double value) {
    this.storeyHeightsBelowGround = value;
  }

  public boolean isSetStoreyHeightsBelowGround() {
    return (this.storeyHeightsBelowGround != -1);
  }

  /**
   * Gets the value of the lod1Solid property.
   * 
   * @return possible object is {@link ISolid }
   * 
   */
  public ISolid getLod1Solid() {
    return this.lod1Solid;
  }

  /**
   * Sets the value of the lod1Solid property.
   * 
   * @param value allowed object is {@link ISolid }
   * 
   */
  public void setLod1Solid(ISolid value) {
    this.lod1Solid = value;
  }

  public boolean isSetLod1Solid() {
    return (this.lod1Solid != null);
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
   * Gets the value of the lod1TerrainIntersection property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod1TerrainIntersection() {
    return this.lod1TerrainIntersection;
  }

  /**
   * Sets the value of the lod1TerrainIntersection property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setLod1TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
    this.lod1TerrainIntersection = value;
  }

  public boolean isSetLod1TerrainIntersection() {
    return (this.lod1TerrainIntersection != null);
  }

  /**
   * Gets the value of the lod2Solid property.
   * 
   * @return possible object is {@link ISolid }
   * 
   */
  public ISolid getLod2Solid() {
    return this.lod2Solid;
  }

  /**
   * Sets the value of the lod2Solid property.
   * 
   * @param value allowed object is {@link ISolid }
   * 
   */
  public void setLod2Solid(ISolid value) {
    this.lod2Solid = value;
  }

  public boolean isSetLod2Solid() {
    return (this.lod2Solid != null);
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
   * Gets the value of the lod2MultiCurve property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod2MultiCurve() {
    return this.lod2MultiCurve;
  }

  /**
   * Sets the value of the lod2MultiCurve property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setLod2MultiCurve(IMultiCurve<IOrientableCurve> value) {
    this.lod2MultiCurve = value;
  }

  public boolean isSetLod2MultiCurve() {
    return (this.lod2MultiCurve != null);
  }

  /**
   * Gets the value of the lod2TerrainIntersection property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod2TerrainIntersection() {
    return this.lod2TerrainIntersection;
  }

  /**
   * Sets the value of the lod2TerrainIntersection property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setLod2TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
    this.lod2TerrainIntersection = value;
  }

  public boolean isSetLod2TerrainIntersection() {
    return (this.lod2TerrainIntersection != null);
  }

  /**
   * Gets the value of the outerBuildingInstallation property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the outerBuildingInstallation property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getOuterBuildingInstallation().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link BuildingInstallationPropertyType }
   * 
   * 
   */
  public List<CG_BuildingInstallation> getOuterBuildingInstallation() {
    if (this.outerBuildingInstallation == null) {
      this.outerBuildingInstallation = new ArrayList<CG_BuildingInstallation>();
    }
    return this.outerBuildingInstallation;
  }

  public boolean isSetOuterBuildingInstallation() {
    return ((this.outerBuildingInstallation != null) && (!this.outerBuildingInstallation
        .isEmpty()));
  }

  public void unsetOuterBuildingInstallation() {
    this.outerBuildingInstallation = null;
  }

  /**
   * Gets the value of the interiorBuildingInstallation property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the interiorBuildingInstallation property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getInteriorBuildingInstallation().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link IntBuildingInstallationPropertyType }
   * 
   * 
   */
  public List<CG_IntBuildingInstallation> getInteriorBuildingInstallation() {
    if (this.interiorBuildingInstallation == null) {
      this.interiorBuildingInstallation = new ArrayList<CG_IntBuildingInstallation>();
    }
    return this.interiorBuildingInstallation;
  }

  public boolean isSetInteriorBuildingInstallation() {
    return ((this.interiorBuildingInstallation != null) && (!this.interiorBuildingInstallation
        .isEmpty()));
  }

  public void unsetInteriorBuildingInstallation() {
    this.interiorBuildingInstallation = null;
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
   * Gets the value of the lod3Solid property.
   * 
   * @return possible object is {@link ISolid }
   * 
   */
  public ISolid getLod3Solid() {
    return this.lod3Solid;
  }

  /**
   * Sets the value of the lod3Solid property.
   * 
   * @param value allowed object is {@link ISolid }
   * 
   */
  public void setLod3Solid(ISolid value) {
    this.lod3Solid = value;
  }

  public boolean isSetLod3Solid() {
    return (this.lod3Solid != null);
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
   * Gets the value of the lod3MultiCurve property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod3MultiCurve() {
    return this.lod3MultiCurve;
  }

  /**
   * Sets the value of the lod3MultiCurve property.
   * 
   * @param value allowed object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public void setLod3MultiCurve(IMultiCurve<IOrientableCurve> value) {
    this.lod3MultiCurve = value;
  }

  public boolean isSetLod3MultiCurve() {
    return (this.lod3MultiCurve != null);
  }

  /**
   * Gets the value of the lod3TerrainIntersection property.
   * 
   * @return possible object is {@link IMultiCurve<IOrientableCurve> }
   * 
   */
  public IMultiCurve<IOrientableCurve> getLod3TerrainIntersection() {
    return this.lod3TerrainIntersection;
  }

  public void setLod3TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
    this.lod3TerrainIntersection = value;
  }

  public boolean isSetLod3TerrainIntersection() {
    return (this.lod3TerrainIntersection != null);
  }

  public ISolid getLod4Solid() {
    return this.lod4Solid;
  }

  public void setLod4Solid(ISolid value) {
    this.lod4Solid = value;
  }

  public boolean isSetLod4Solid() {
    return (this.lod4Solid != null);
  }

  public IMultiSurface<IOrientableSurface> getLod4MultiSurface() {
    return this.lod4MultiSurface;
  }

  public void setLod4MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod4MultiSurface = value;
  }

  public boolean isSetLod4MultiSurface() {
    return (this.lod4MultiSurface != null);
  }

  public IMultiCurve<IOrientableCurve> getLod4MultiCurve() {
    return this.lod4MultiCurve;
  }

  public void setLod4MultiCurve(IMultiCurve<IOrientableCurve> value) {
    this.lod4MultiCurve = value;
  }

  public boolean isSetLod4MultiCurve() {
    return (this.lod4MultiCurve != null);
  }

  public IMultiCurve<IOrientableCurve> getLod4TerrainIntersection() {
    return this.lod4TerrainIntersection;
  }

  public void setLod4TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
    this.lod4TerrainIntersection = value;
  }

  public boolean isSetLod4TerrainIntersection() {
    return (this.lod4TerrainIntersection != null);
  }

  public List<CG_Room> getInteriorRoom() {
    if (this.interiorRoom == null) {
      this.interiorRoom = new ArrayList<CG_Room>();
    }
    return this.interiorRoom;
  }

  public boolean isSetInteriorRoom() {
    return ((this.interiorRoom != null) && (!this.interiorRoom.isEmpty()));
  }

  public void unsetInteriorRoom() {
    this.interiorRoom = null;
  }

  public List<CG_BuildingPart> getConsistsOfBuildingPart() {
    if (this.consistsOfBuildingPart == null) {
      this.consistsOfBuildingPart = new ArrayList<CG_BuildingPart>();
    }
    return this.consistsOfBuildingPart;
  }

  public boolean isSetConsistsOfBuildingPart() {
    return ((this.consistsOfBuildingPart != null) && (!this.consistsOfBuildingPart
        .isEmpty()));
  }

  public void unsetConsistsOfBuildingPart() {
    this.consistsOfBuildingPart = null;
  }

  public List<CG_Address> getAddress() {
    if (this.address == null) {
      this.address = new ArrayList<CG_Address>();
    }
    return this.address;
  }

  public boolean isSetAddress() {
    return ((this.address != null) && (!this.address.isEmpty()));
  }

  public void unsetAddress() {
    this.address = null;
  }

}
