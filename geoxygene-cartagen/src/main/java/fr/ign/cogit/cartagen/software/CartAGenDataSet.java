/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 16 juin 2009
 */
package fr.ign.cogit.cartagen.software;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminCapital;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IRiverSimpleIsland;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.ITriageArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadArea;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadFacilityPoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.cartagen.software.dataset.SpecialPoint;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Dataset dedicated to generalisation
 * @author julien Gaffuri 16/06/2009, major update JRenard 12/2011
 * 
 */
public class CartAGenDataSet extends DataSet {
  private static Logger logger = Logger.getLogger(CartAGenDataSet.class
      .getName());

  /**
   * Default constructor
   */
  public CartAGenDataSet() {
    super();
    IPopulation<IFeature> geomPool = new Population<IFeature>(GEOM_POOL);
    addPopulation(geomPool);
    geometryPool = new GeometryPool(this, sld);
  }

  /**
   * @return The dataset of the unique document named 'name'.
   */
  public static CartAGenDataSet getInstance(String name) {
    return CartAGenDoc.getInstance().getDataset(name);
  }

  /**
   * Associated CartAGen dataset
   */

  private CartAGenDB cartagenDB;

  public CartAGenDB getCartAGenDB() {
    return this.cartagenDB;
  }

  private StyledLayerDescriptor sld;

  public StyledLayerDescriptor getSld() {
    return sld;
  }

  public void setSld(StyledLayerDescriptor sld) {
    this.sld = sld;
    this.geometryPool.setSld(sld);
  }

  private GeometryPool geometryPool;

  // the symbols used for the database
  private SymbolList symbols = new SymbolList();

  public SymbolList getSymbols() {
    return this.symbols;
  }

  public void setSymbols(SymbolList symbols) {
    this.symbols = symbols;
  }

  public void setCartAGenDB(CartAGenDB database) {
    this.cartagenDB = database;
    if (database.getDataSet() == null) {
      database.setDataSet(this);
    } else if (!database.getDataSet().equals(this)) {
      database.setDataSet(this);
    }
  }

  // ///////////////////////////////////////
  // STANDARD NAMES OF DATASET POPULATIONS
  // ///////////////////////////////////////

  public static final String BUILDINGS_POP = "buildings";
  public static final String BLOCKS_POP = "blocks";
  public static final String TOWNS_POP = "towns";
  public static final String URBAN_ALIGNMENTS_POP = "urbanAlignments";
  public static final String BUILD_PT_POP = "buildingPoints";
  public static final String SPORTS_FIELDS_POP = "sportsFields";
  public static final String BUILD_AREA_POP = "buildingAreas";
  public static final String SQUARE_AREA_POP = "squareAreas";
  public static final String CEMETERY_POP = "cemeteries";

  public static final String ROADS_POP = "roads";
  public static final String ROAD_NODES_POP = "roadNodes";
  public static final String ROAD_FACILITY_PT_POP = "roadFacilityPoints";
  public static final String ROAD_AREA_POP = "roadAreas";
  public static final String PATHS_POP = "paths";
  public static final String BRIDGE_PT_POP = "bridgePoints";

  public static final String WATER_LINES_POP = "waterLines";
  public static final String WATER_NODES_POP = "waterNodes";
  public static final String WATER_AREAS_POP = "waterAreas";
  public static final String WATER_PT_POP = "waterPoints";
  public static final String WATER_ISLAND_POP = "waterIslands";

  public static final String RAILWAY_LINES_POP = "railwayLines";
  public static final String RAILWAY_NODE_POP = "railwayNodes";
  public static final String TRIAGE_AREA_POP = "triageAreas";
  public static final String CABLE_POP = "cables";
  public static final String ELECTRICITY_LINES_POP = "electricityLines";

  public static final String CONTOUR_LINES_POP = "contourLines";
  public static final String RELIEF_LINES_POP = "reliefLines";
  public static final String SPOT_HEIGHTS_POP = "heightSpots";
  public static final String DEM_PIXELS_POP = "DEMpixels";
  public static final String RELIEF_PTS_POP = "reliefPoints";

  public static final String LANDUSE_AREAS_POP = "landUseAreas";
  public static final String ADMIN_FIELDS_POP = "adminFields";

  public static final String NETWORK_FACES_POP = "networkFaces";
  public static final String SPECIAL_POINTS_POP = "SpecialPoints";

  public static final String BOUNDED_AREA_POP = "boundedAreas";
  public static final String LABEL_PT_POP = "labelPoints";
  public static final String MISC_AREA_POP = "miscAreas";
  public static final String MISC_LINE_POP = "miscLines";
  public static final String MISC_PT_POP = "miscPoints";
  public static final String AIRPORT_AREA_POP = "airportAreas";
  public static final String RUNWAY_AREA_POP = "runwayAreas";
  public static final String RUNWAY_LINE_POP = "runwayLines";
  public static final String TAXIWAY_AREA_POP = "taxiwayAreas";
  public static final String TAXIWAY_LINE_POP = "taxiwayLines";
  public static final String POI_POP = "pointsOfInterest";

  public static final String ROAD_STROKES_POP = "strokes";
  public static final String ROUNDABOUTS_POP = "roundabouts";
  public static final String BRANCHINGS_POP = "branchingCrossroads";

  public static final String ADMIN_CAP_POP = "adminCapitals";
  public static final String ADMIN_UNIT_POP = "adminUnits";
  public static final String ADMIN_LIMIT_POP = "adminLimits";
  public static final String COASTLINE_POP = "coastlines";

  public static final String MASK = "mask";

  public static final String GEOM_POOL = "Geometry Pool";

  // ///////////////////////////////////////
  // GETTERS FOR DATASET POPULATIONS
  // ///////////////////////////////////////

  /**
   * Gets the population name of an object of the dataset
   * @param obj an object of the dataset
   * @return
   */
  public String getPopNameFromObj(IFeature obj) {
    if (obj instanceof IBuilding) {
      return CartAGenDataSet.BUILDINGS_POP;
    } else if (obj instanceof IUrbanBlock) {
      return CartAGenDataSet.BLOCKS_POP;
    } else if (obj instanceof ITown) {
      return CartAGenDataSet.TOWNS_POP;
    } else if (obj instanceof IUrbanAlignment) {
      return CartAGenDataSet.URBAN_ALIGNMENTS_POP;
    } else if (obj instanceof IRoadLine) {
      return CartAGenDataSet.ROADS_POP;
    } else if (obj instanceof IRoadNode) {
      return CartAGenDataSet.ROAD_NODES_POP;
    } else if (obj instanceof IPathLine) {
      return CartAGenDataSet.PATHS_POP;
    } else if (obj instanceof IWaterLine) {
      return CartAGenDataSet.WATER_LINES_POP;
    } else if (obj instanceof IWaterNode) {
      return CartAGenDataSet.WATER_NODES_POP;
    } else if (obj instanceof IWaterArea) {
      return CartAGenDataSet.WATER_AREAS_POP;
    } else if (obj instanceof IRiverSimpleIsland) {
      return CartAGenDataSet.WATER_ISLAND_POP;
    } else if (obj instanceof IRailwayLine) {
      return CartAGenDataSet.RAILWAY_LINES_POP;
    } else if (obj instanceof IElectricityLine) {
      return CartAGenDataSet.ELECTRICITY_LINES_POP;
    } else if (obj instanceof IContourLine) {
      return CartAGenDataSet.CONTOUR_LINES_POP;
    } else if (obj instanceof IReliefElementLine) {
      return CartAGenDataSet.RELIEF_LINES_POP;
    } else if (obj instanceof ISpotHeight) {
      return CartAGenDataSet.SPOT_HEIGHTS_POP;
    } else if (obj instanceof ISimpleLandUseArea) {
      return CartAGenDataSet.LANDUSE_AREAS_POP;
    } else if (obj instanceof INetworkFace) {
      return CartAGenDataSet.NETWORK_FACES_POP;
    } else if (obj instanceof SpecialPoint) {
      return CartAGenDataSet.SPECIAL_POINTS_POP;
    } else if (obj instanceof IRoadStroke) {
      return CartAGenDataSet.ROAD_STROKES_POP;
    } else if (obj instanceof IRoundAbout) {
      return CartAGenDataSet.ROUNDABOUTS_POP;
    } else if (obj instanceof IBranchingCrossroad) {
      return CartAGenDataSet.BRANCHINGS_POP;
    } else if (obj instanceof IMask) {
      return CartAGenDataSet.MASK;
    } else if (obj instanceof IRoadFacilityPoint) {
      return CartAGenDataSet.ROAD_FACILITY_PT_POP;
    } else if (obj instanceof IRoadArea) {
      return CartAGenDataSet.ROAD_AREA_POP;
    } else if (obj instanceof IRailwayNode) {
      return CartAGenDataSet.RAILWAY_NODE_POP;
    } else if (obj instanceof ITriageArea) {
      return CartAGenDataSet.TRIAGE_AREA_POP;
    } else if (obj instanceof ICable) {
      return CartAGenDataSet.CABLE_POP;
    } else if (obj instanceof IBoundedArea) {
      return CartAGenDataSet.BOUNDED_AREA_POP;
    } else if (obj instanceof ILabelPoint) {
      return CartAGenDataSet.LABEL_PT_POP;
    } else if (obj instanceof IMiscArea) {
      return CartAGenDataSet.MISC_AREA_POP;
    } else if (obj instanceof IMiscLine) {
      return CartAGenDataSet.MISC_LINE_POP;
    } else if (obj instanceof IMiscPoint) {
      return CartAGenDataSet.MISC_PT_POP;
    } else if (obj instanceof IAirportArea) {
      return CartAGenDataSet.AIRPORT_AREA_POP;
    } else if (obj instanceof IRunwayArea) {
      return CartAGenDataSet.RUNWAY_AREA_POP;
    } else if (obj instanceof IRunwayLine) {
      return CartAGenDataSet.RUNWAY_LINE_POP;
    } else if (obj instanceof ITaxiwayArea) {
      return CartAGenDataSet.TAXIWAY_AREA_POP;
    } else if (obj instanceof ITaxiwayLine) {
      return CartAGenDataSet.TAXIWAY_LINE_POP;
    } else if (obj instanceof IWaterPoint) {
      return CartAGenDataSet.WATER_PT_POP;
    } else if (obj instanceof IAdminCapital) {
      return CartAGenDataSet.ADMIN_CAP_POP;
    } else if (obj instanceof ISimpleAdminUnit) {
      return CartAGenDataSet.ADMIN_UNIT_POP;
    } else if (obj instanceof IAdminLimit) {
      return CartAGenDataSet.ADMIN_LIMIT_POP;
    } else if (obj instanceof IBuildPoint) {
      return CartAGenDataSet.BUILD_PT_POP;
    } else if (obj instanceof ISportsField) {
      return CartAGenDataSet.SPORTS_FIELDS_POP;
    } else if (obj instanceof IReliefElementPoint) {
      return CartAGenDataSet.RELIEF_PTS_POP;
    } else if (obj instanceof IBuildArea) {
      return CartAGenDataSet.BUILD_AREA_POP;
    } else if (obj instanceof IPointOfInterest) {
      return CartAGenDataSet.POI_POP;
    } else if (obj instanceof IBridgePoint) {
      return CartAGenDataSet.BRIDGE_PT_POP;
    } else if (obj instanceof ICoastLine) {
      return CartAGenDataSet.COASTLINE_POP;
    } else if (obj instanceof ISquareArea) {
      return CartAGenDataSet.SQUARE_AREA_POP;
    } else if (obj instanceof ICemetery) {
      return CartAGenDataSet.CEMETERY_POP;
    }
    return null;
  }

  /**
   * Gets the population name of a feature type name
   * @param featureType the name of the feature type
   * @return
   */
  public String getPopNameFromFeatType(String featureType) {
    if (featureType.equals(IBuilding.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BUILDINGS_POP;
    } else if (featureType.equals(IUrbanBlock.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BLOCKS_POP;
    } else if (featureType.equals(ITown.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.TOWNS_POP;
    } else if (featureType.equals(IUrbanAlignment.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.URBAN_ALIGNMENTS_POP;
    } else if (featureType.equals(IRoadLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROADS_POP;
    } else if (featureType.equals(IRoadNode.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROAD_NODES_POP;
    } else if (featureType.equals(IPathLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.PATHS_POP;
    } else if (featureType.equals(IWaterLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.WATER_LINES_POP;
    } else if (featureType.equals(IWaterNode.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.WATER_NODES_POP;
    } else if (featureType.equals(IWaterArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.WATER_AREAS_POP;
    } else if (featureType.equals(IRiverSimpleIsland.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.WATER_ISLAND_POP;
    } else if (featureType.equals(IRailwayLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RAILWAY_LINES_POP;
    } else if (featureType.equals(IElectricityLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ELECTRICITY_LINES_POP;
    } else if (featureType.equals(IContourLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.CONTOUR_LINES_POP;
    } else if (featureType.equals(IReliefElementLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RELIEF_LINES_POP;
    } else if (featureType.equals(ISpotHeight.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.SPOT_HEIGHTS_POP;
    } else if (featureType.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.LANDUSE_AREAS_POP;
    } else if (featureType.equals(INetworkFace.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.NETWORK_FACES_POP;
    } else if (featureType.equals(SpecialPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.SPECIAL_POINTS_POP;
    } else if (featureType.equals(IRoadStroke.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROAD_STROKES_POP;
    } else if (featureType.equals(IRoundAbout.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROUNDABOUTS_POP;
    } else if (featureType.equals(IBranchingCrossroad.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BRANCHINGS_POP;
    } else if (featureType.equals(IMask.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.MASK;
    } else if (featureType.equals(IRoadFacilityPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROAD_FACILITY_PT_POP;
    } else if (featureType.equals(IRoadArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ROAD_AREA_POP;
    } else if (featureType.equals(IRailwayNode.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RAILWAY_NODE_POP;
    } else if (featureType.equals(ITriageArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.TRIAGE_AREA_POP;
    } else if (featureType.equals(ICable.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.CABLE_POP;
    } else if (featureType.equals(IBoundedArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BOUNDED_AREA_POP;
    } else if (featureType.equals(ILabelPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.LABEL_PT_POP;
    } else if (featureType.equals(IMiscArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.MISC_AREA_POP;
    } else if (featureType.equals(IMiscLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.MISC_LINE_POP;
    } else if (featureType.equals(IMiscPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.MISC_PT_POP;
    } else if (featureType.equals(IAirportArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.AIRPORT_AREA_POP;
    } else if (featureType.equals(IRunwayArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RUNWAY_AREA_POP;
    } else if (featureType.equals(IRunwayLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RUNWAY_LINE_POP;
    } else if (featureType.equals(ITaxiwayArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.TAXIWAY_AREA_POP;
    } else if (featureType.equals(ITaxiwayLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.TAXIWAY_LINE_POP;
    } else if (featureType.equals(IWaterPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.WATER_PT_POP;
    } else if (featureType.equals(IAdminCapital.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ADMIN_CAP_POP;
    } else if (featureType.equals(ISimpleAdminUnit.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ADMIN_UNIT_POP;
    } else if (featureType.equals(IAdminLimit.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.ADMIN_LIMIT_POP;
    } else if (featureType.equals(IBuildPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BUILD_PT_POP;
    } else if (featureType.equals(ISportsField.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.SPORTS_FIELDS_POP;
    } else if (featureType.equals(IReliefElementPoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.RELIEF_PTS_POP;
    } else if (featureType.equals(IBuildArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BUILD_AREA_POP;
    } else if (featureType.equals(IPointOfInterest.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.POI_POP;
    } else if (featureType.equals(IBridgePoint.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.BRIDGE_PT_POP;
    } else if (featureType.equals(ICoastLine.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.COASTLINE_POP;
    } else if (featureType.equals(ISquareArea.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.SQUARE_AREA_POP;
    } else if (featureType.equals(ICemetery.FEAT_TYPE_NAME)) {
      return CartAGenDataSet.CEMETERY_POP;
    }
    return null;
  }

  /**
   * Gets the population name associated with a class of the dataset
   * @param classObj a class of objects of the dataset
   * @return
   */
  public String getPopNameFromClass(Class<?> classObj) {
    if (IBuilding.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BUILDINGS_POP;
    } else if (IUrbanBlock.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BLOCKS_POP;
    } else if (ITown.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.TOWNS_POP;
    } else if (IUrbanAlignment.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.URBAN_ALIGNMENTS_POP;
    } else if (IRoadLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROADS_POP;
    } else if (IPathLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.PATHS_POP;
    } else if (IRoadNode.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROAD_NODES_POP;
    } else if (IWaterLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.WATER_LINES_POP;
    } else if (IWaterNode.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.WATER_NODES_POP;
    } else if (IWaterArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.WATER_AREAS_POP;
    } else if (IRiverSimpleIsland.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.WATER_ISLAND_POP;
    } else if (IRailwayLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RAILWAY_LINES_POP;
    } else if (IElectricityLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ELECTRICITY_LINES_POP;
    } else if (IContourLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.CONTOUR_LINES_POP;
    } else if (IReliefElementLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RELIEF_LINES_POP;
    } else if (ISpotHeight.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.SPOT_HEIGHTS_POP;
    } else if (ISimpleLandUseArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.LANDUSE_AREAS_POP;
    } else if (INetworkFace.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.NETWORK_FACES_POP;
    } else if (SpecialPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.SPECIAL_POINTS_POP;
    } else if (IRoadStroke.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROAD_STROKES_POP;
    } else if (IRoundAbout.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROUNDABOUTS_POP;
    } else if (IBranchingCrossroad.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BRANCHINGS_POP;
    } else if (IMask.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.MASK;
    } else if (IRoadFacilityPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROAD_FACILITY_PT_POP;
    } else if (IRoadArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ROAD_AREA_POP;
    } else if (IRailwayNode.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RAILWAY_NODE_POP;
    } else if (ITriageArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.TRIAGE_AREA_POP;
    } else if (ICable.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.CABLE_POP;
    } else if (IBoundedArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BOUNDED_AREA_POP;
    } else if (ILabelPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.LABEL_PT_POP;
    } else if (IMiscArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.MISC_AREA_POP;
    } else if (IMiscLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.MISC_LINE_POP;
    } else if (IMiscPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.MISC_PT_POP;
    } else if (IRunwayArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RUNWAY_AREA_POP;
    } else if (IRunwayLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RUNWAY_LINE_POP;
    } else if (ITaxiwayArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.TAXIWAY_AREA_POP;
    } else if (ITaxiwayLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.TAXIWAY_LINE_POP;
    } else if (IAirportArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.AIRPORT_AREA_POP;
    } else if (IWaterPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.WATER_PT_POP;
    } else if (IAdminCapital.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ADMIN_CAP_POP;
    } else if (ISimpleAdminUnit.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ADMIN_UNIT_POP;
    } else if (IAdminLimit.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.ADMIN_LIMIT_POP;
    } else if (IBuildPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BUILD_PT_POP;
    } else if (ISportsField.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.SPORTS_FIELDS_POP;
    } else if (IReliefElementPoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.RELIEF_PTS_POP;
    } else if (IBuildArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BUILD_AREA_POP;
    } else if (IPointOfInterest.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.POI_POP;
    } else if (IBridgePoint.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.BRIDGE_PT_POP;
    } else if (ICoastLine.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.COASTLINE_POP;
    } else if (ISquareArea.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.SQUARE_AREA_POP;
    } else if (ICemetery.class.isAssignableFrom(classObj)) {
      return CartAGenDataSet.CEMETERY_POP;
    }
    return null;
  }

  /**
   * Gets the feature type name from the population name.
   * @param popName the name of the population
   * @return
   */
  public String getFeatureTypeFromPopName(String popName) {
    if (popName.equals(CartAGenDataSet.BUILDINGS_POP)) {
      return IBuilding.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BLOCKS_POP)) {
      return IUrbanBlock.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.TOWNS_POP)) {
      return ITown.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.URBAN_ALIGNMENTS_POP)) {
      return IUrbanAlignment.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROADS_POP)) {
      return IRoadLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROAD_NODES_POP)) {
      return IRoadNode.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.PATHS_POP)) {
      return IPathLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.WATER_LINES_POP)) {
      return IWaterLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.WATER_NODES_POP)) {
      return IWaterNode.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.WATER_AREAS_POP)) {
      return IWaterArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.WATER_ISLAND_POP)) {
      return IRiverSimpleIsland.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RAILWAY_LINES_POP)) {
      return IRailwayLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ELECTRICITY_LINES_POP)) {
      return IElectricityLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.CONTOUR_LINES_POP)) {
      return IContourLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RELIEF_LINES_POP)) {
      return IReliefElementLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.SPOT_HEIGHTS_POP)) {
      return ISpotHeight.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.LANDUSE_AREAS_POP)) {
      return ISimpleLandUseArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.NETWORK_FACES_POP)) {
      return INetworkFace.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.SPECIAL_POINTS_POP)) {
      return SpecialPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROAD_STROKES_POP)) {
      return IRoadStroke.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROUNDABOUTS_POP)) {
      return IRoundAbout.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BRANCHINGS_POP)) {
      return IBranchingCrossroad.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.MASK)) {
      return IMask.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROAD_FACILITY_PT_POP)) {
      return IRoadFacilityPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ROAD_AREA_POP)) {
      return IRoadArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RAILWAY_NODE_POP)) {
      return IRailwayNode.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.TRIAGE_AREA_POP)) {
      return ITriageArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.CABLE_POP)) {
      return ICable.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BOUNDED_AREA_POP)) {
      return IBoundedArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.LABEL_PT_POP)) {
      return ILabelPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.MISC_AREA_POP)) {
      return IMiscArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.MISC_LINE_POP)) {
      return IMiscLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.MISC_PT_POP)) {
      return IMiscPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.AIRPORT_AREA_POP)) {
      return IAirportArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RUNWAY_AREA_POP)) {
      return IRunwayArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RUNWAY_LINE_POP)) {
      return IRunwayLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.TAXIWAY_AREA_POP)) {
      return ITaxiwayArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.TAXIWAY_LINE_POP)) {
      return ITaxiwayLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.WATER_PT_POP)) {
      return IWaterPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ADMIN_CAP_POP)) {
      return IAdminCapital.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ADMIN_UNIT_POP)) {
      return ISimpleAdminUnit.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.ADMIN_LIMIT_POP)) {
      return IAdminLimit.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BUILD_PT_POP)) {
      return IBuildPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.SPORTS_FIELDS_POP)) {
      return ISportsField.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.RELIEF_PTS_POP)) {
      return IReliefElementPoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BUILD_AREA_POP)) {
      return IBuildArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.POI_POP)) {
      return IPointOfInterest.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.BRIDGE_PT_POP)) {
      return IBridgePoint.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.COASTLINE_POP)) {
      return ICoastLine.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.SQUARE_AREA_POP)) {
      return ISquareArea.FEAT_TYPE_NAME;
    } else if (popName.equals(CartAGenDataSet.CEMETERY_POP)) {
      return ICemetery.FEAT_TYPE_NAME;
    }
    return null;
  }

  /**
   * Generic getter for each population
   * @param nomPopulation
   * @param featureTypeName
   * @return
   */

  public IPopulation<? extends IGeneObj> getCartagenPop(String nomPopulation,
      String featureTypeName) {
    IPopulation<IGeneObj> pop = this.getCartagenPop(nomPopulation);
    FeatureType ft = new FeatureType();
    ft.setTypeName(featureTypeName);
    if (pop == null) {
      pop = new Population<IGeneObj>();
      pop.setNom(nomPopulation);
      pop.setFeatureType(ft);
      this.addPopulation(pop);
    } else {
      pop.setFeatureType(ft);
    }
    return pop;
  }

  /**
   * Generic getter for each population
   * @param nomPopulation
   * @return
   */

  @SuppressWarnings("unchecked")
  public IPopulation<IGeneObj> getCartagenPop(String nomPopulation) {
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) super
        .getPopulation(nomPopulation);
    return pop;
  }

  /**
   * Gets the buildings of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBuilding> getBuildings() {
    return (IPopulation<IBuilding>) this.getCartagenPop(
        CartAGenDataSet.BUILDINGS_POP, IBuilding.FEAT_TYPE_NAME);
  }

  /**
   * Gets the blocks of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IUrbanBlock> getBlocks() {
    return (IPopulation<IUrbanBlock>) this.getCartagenPop(
        CartAGenDataSet.BLOCKS_POP, IUrbanBlock.FEAT_TYPE_NAME);
  }

  /**
   * Gets the towns of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ITown> getTowns() {
    return (IPopulation<ITown>) this.getCartagenPop(CartAGenDataSet.TOWNS_POP,
        ITown.FEAT_TYPE_NAME);
  }

  /**
   * Gets the urban alignments of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IUrbanAlignment> getUrbanAlignments() {
    return (IPopulation<IUrbanAlignment>) this.getCartagenPop(
        CartAGenDataSet.URBAN_ALIGNMENTS_POP, IUrbanAlignment.FEAT_TYPE_NAME);
  }

  /**
   * Gets the roads of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRoadLine> getRoads() {
    return (IPopulation<IRoadLine>) this.getCartagenPop(
        CartAGenDataSet.ROADS_POP, IRoadLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the road nodes of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRoadNode> getRoadNodes() {
    return (IPopulation<IRoadNode>) this.getCartagenPop(
        CartAGenDataSet.ROAD_NODES_POP, IRoadNode.FEAT_TYPE_NAME);
  }

  /**
   * Gets the roads of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IPathLine> getPaths() {
    return (IPopulation<IPathLine>) this.getCartagenPop(
        CartAGenDataSet.PATHS_POP, IPathLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the water segments of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IWaterLine> getWaterLines() {
    return (IPopulation<IWaterLine>) this.getCartagenPop(
        CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the water nodes of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IWaterNode> getWaterNodes() {
    return (IPopulation<IWaterNode>) this.getCartagenPop(
        CartAGenDataSet.WATER_NODES_POP, IWaterNode.FEAT_TYPE_NAME);
  }

  /**
   * Gets the water nodes of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IWaterArea> getWaterAreas() {
    return (IPopulation<IWaterArea>) this.getCartagenPop(
        CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the railway lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRailwayLine> getRailwayLines() {
    return (IPopulation<IRailwayLine>) this.getCartagenPop(
        CartAGenDataSet.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the electricity lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IElectricityLine> getElectricityLines() {
    return (IPopulation<IElectricityLine>) this.getCartagenPop(
        CartAGenDataSet.ELECTRICITY_LINES_POP, IElectricityLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the contour lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IContourLine> getContourLines() {
    return (IPopulation<IContourLine>) this.getCartagenPop(
        CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the relief lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IReliefElementLine> getReliefLines() {
    return (IPopulation<IReliefElementLine>) this.getCartagenPop(
        CartAGenDataSet.RELIEF_LINES_POP, IReliefElementLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the spot heights of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISpotHeight> getSpotHeights() {
    return (IPopulation<ISpotHeight>) this.getCartagenPop(
        CartAGenDataSet.SPOT_HEIGHTS_POP, ISpotHeight.FEAT_TYPE_NAME);
  }

  /**
   * Gets the DEM pixels of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IDEMPixel> getDEMPixels() {
    return (IPopulation<IDEMPixel>) this.getCartagenPop(
        CartAGenDataSet.DEM_PIXELS_POP, IDEMPixel.FEAT_TYPE_NAME);
  }

  /**
   * Gets the landuse areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISimpleLandUseArea> getLandUseAreas() {
    return (IPopulation<ISimpleLandUseArea>) this.getCartagenPop(
        CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the landuse areas of the dataset of one given type.
   * @return
   */
  public IFeatureCollection<ISimpleLandUseArea> getLandUseAreas(int type) {
    IFeatureCollection<ISimpleLandUseArea> landuse = new FT_FeatureCollection<ISimpleLandUseArea>();
    for (IGeneObj obj : this.getCartagenPop(CartAGenDataSet.LANDUSE_AREAS_POP,
        ISimpleLandUseArea.FEAT_TYPE_NAME)) {
      ISimpleLandUseArea parcel = (ISimpleLandUseArea) obj;
      if (parcel.getType() == type)
        landuse.add(parcel);
    }
    return landuse;
  }

  /**
   * Gets the administrative areas of the dataset A REVOIR
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IGeneObj> getAdminAreas() {
    return (IPopulation<IGeneObj>) this.getCartagenPop(
        CartAGenDataSet.ADMIN_FIELDS_POP, IGeneObj.FEAT_TYPE_NAME);
  }

  /**
   * Gets the network faces of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<INetworkFace> getNetworkFaces() {
    return (IPopulation<INetworkFace>) this.getCartagenPop(
        CartAGenDataSet.NETWORK_FACES_POP, INetworkFace.FEAT_TYPE_NAME);
  }

  /**
   * Gets the special points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<SpecialPoint> getSpecialPoints() {

    return (IPopulation<SpecialPoint>) this.getCartagenPop(
        CartAGenDataSet.SPECIAL_POINTS_POP, SpecialPoint.FEAT_TYPE_NAME);

  }

  /**
   * Gets the raod strokes of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRoadStroke> getRoadStrokes() {
    return (IPopulation<IRoadStroke>) this.getCartagenPop(
        CartAGenDataSet.ROAD_STROKES_POP, IRoadStroke.FEAT_TYPE_NAME);
  }

  /**
   * Gets the roundaboutss of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRoundAbout> getRoundabouts() {
    return (IPopulation<IRoundAbout>) this.getCartagenPop(
        CartAGenDataSet.ROUNDABOUTS_POP, IRoundAbout.FEAT_TYPE_NAME);
  }

  /**
   * Gets the branching crossroads of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBranchingCrossroad> getBranchings() {
    return (IPopulation<IBranchingCrossroad>) this.getCartagenPop(
        CartAGenDataSet.BRANCHINGS_POP, IBranchingCrossroad.FEAT_TYPE_NAME);
  }

  /**
   * Gets the mask of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IMask> getMasks() {
    return (IPopulation<IMask>) this.getCartagenPop(CartAGenDataSet.MASK,
        IMask.FEAT_TYPE_NAME);
  }

  /**
   * Gets the airports of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IAirportArea> getAirports() {
    return (IPopulation<IAirportArea>) this.getCartagenPop(
        CartAGenDataSet.AIRPORT_AREA_POP, IAirportArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the airport runways of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRunwayArea> getRunways() {
    return (IPopulation<IRunwayArea>) this.getCartagenPop(
        CartAGenDataSet.RUNWAY_AREA_POP, IRunwayArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the airport runways of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRunwayLine> getRunwayLines() {
    return (IPopulation<IRunwayLine>) this.getCartagenPop(
        CartAGenDataSet.RUNWAY_LINE_POP, IRunwayLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the airport taxiway lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ITaxiwayLine> getTaxiwayLines() {
    return (IPopulation<ITaxiwayLine>) this.getCartagenPop(
        CartAGenDataSet.TAXIWAY_LINE_POP, ITaxiwayLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the airport taxiway areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ITaxiwayArea> getTaxiwayAreas() {
    return (IPopulation<ITaxiwayArea>) this.getCartagenPop(
        CartAGenDataSet.TAXIWAY_AREA_POP, ITaxiwayArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the islands of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IRiverSimpleIsland> getIslands() {
    return (IPopulation<IRiverSimpleIsland>) this.getCartagenPop(
        CartAGenDataSet.WATER_ISLAND_POP, IRiverSimpleIsland.FEAT_TYPE_NAME);
  }

  /**
   * Gets the admin limits of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IAdminLimit> getAdminLimits() {
    return (IPopulation<IAdminLimit>) this.getCartagenPop(
        CartAGenDataSet.ADMIN_LIMIT_POP, IAdminLimit.FEAT_TYPE_NAME);
  }

  /**
   * Gets the admin areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISimpleAdminUnit> getAdminUnits() {
    return (IPopulation<ISimpleAdminUnit>) this.getCartagenPop(
        CartAGenDataSet.ADMIN_UNIT_POP, ISimpleAdminUnit.FEAT_TYPE_NAME);
  }

  /**
   * Gets the building points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBuildPoint> getBuildPts() {
    return (IPopulation<IBuildPoint>) this.getCartagenPop(
        CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME);
  }

  /**
   * Gets the building areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBuildArea> getBuildAreas() {
    return (IPopulation<IBuildArea>) this.getCartagenPop(
        CartAGenDataSet.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the sports fields of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISportsField> getSportsFields() {
    return (IPopulation<ISportsField>) this.getCartagenPop(
        CartAGenDataSet.SPORTS_FIELDS_POP, ISportsField.FEAT_TYPE_NAME);
  }

  /**
   * Gets the relief characteristic points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IReliefElementPoint> getReliefPts() {
    return (IPopulation<IReliefElementPoint>) this.getCartagenPop(
        CartAGenDataSet.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME);
  }

  /**
   * Gets the points of interest of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IPointOfInterest> getPOIs() {
    return (IPopulation<IPointOfInterest>) this.getCartagenPop(
        CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME);
  }

  /**
   * Gets the bridge points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBridgePoint> getBridgePoints() {
    return (IPopulation<IBridgePoint>) this.getCartagenPop(
        CartAGenDataSet.BRIDGE_PT_POP, IBridgePoint.FEAT_TYPE_NAME);
  }

  /**
   * Gets the coastline objects of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICoastLine> getCoastlines() {
    return (IPopulation<ICoastLine>) this.getCartagenPop(
        CartAGenDataSet.COASTLINE_POP, ICoastLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the square areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISquareArea> getSquareAreas() {
    return (IPopulation<ISquareArea>) this.getCartagenPop(
        CartAGenDataSet.SQUARE_AREA_POP, ISquareArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the cemeterey areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICemetery> getCemeteries() {
    return (IPopulation<ICemetery>) this.getCartagenPop(
        CartAGenDataSet.CEMETERY_POP, ICemetery.FEAT_TYPE_NAME);
  }

  /**
   * Gets the label points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ILabelPoint> getLabelPoints() {
    return (IPopulation<ILabelPoint>) this.getCartagenPop(
        CartAGenDataSet.LABEL_PT_POP, ILabelPoint.FEAT_TYPE_NAME);
  }

  /**
   * Erases the towns of the dataset
   */
  public void eraseTowns() {
    this.getTowns().clear();
  }

  /**
   * les faces reseau du jeu de données
   */
  // private IPopulation<INetworkFace> listeFacesReseau = new
  // Population<INetworkFace>();

  public IPopulation<INetworkFace> getFacesReseau() {
    return this.getNetworkFaces();
  }

  public void eraseFacesReseau() {
    this.getNetworkFaces().clear();
  }

  // ///////////////////////////////////////
  // Road network
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ReseauRoutier.
   * @return instance unique (singleton) de ReseauRoutier.
   */
  private INetwork roadNetwork = null;

  public INetwork getRoadNetwork() {
    if (this.roadNetwork == null) {
      this.roadNetwork = this.getCartAGenDB().getGeneObjImpl()
          .getCreationFactory().createNetwork(new ReseauImpl());
      for (INetworkSection section : this.getRoads()) {
        this.roadNetwork.addSection(section);
      }
    }
    return this.roadNetwork;
  }

  // ///////////////////////////////////////
  // Relief field
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ChampRelief.
   * @return instance unique (singleton) de ChampRelief.
   */
  private IReliefField reliefField = null;

  public IReliefField getReliefField() {
    if (this.reliefField == null) {
      synchronized (CartAGenDataSet.class) {
        if (this.reliefField == null) {
          this.reliefField = this.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createReliefField(new ChampContinuImpl());
        }
      }
    }
    return this.reliefField;
  }

  // ///////////////////////////////////////
  // Land use
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ChampOccSol.
   * @return instance unique (singleton) de ChampOccSol.
   */
  private IGeneObj landUseField = null;

  public IGeneObj getLandUseField() {
    if (this.landUseField == null) {
      synchronized (CartAGenDataSet.class) {
        this.landUseField = new GeneObjDefault();
      }
    }
    return this.landUseField;
  }

  /***
   * Total reset of the dataset
   */

  public void resetDataSet() {

    // TODO

  }

  public void deleteAllObjects() {

    for (IPopulation<? extends IFeature> population : this.getPopulations()) {
      for (IFeature ft : population) {
        ft.setDeleted(true);
      }
    }

  }

  // ///////////////////////////////////////
  // Admin
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ChampAdmin.
   * @return instance unique (singleton) de ChampAdmin.
   */
  private IGeneObj adminField = null;

  public IGeneObj getAdminField() {
    if (this.adminField == null) {
      synchronized (CartAGenDataSet.class) {
        if (this.adminField == null) {
          this.adminField = new GeneObjDefault();
        }
      }
    }
    return this.adminField;
  }

  // ///////////////////////////////////////
  // Hydro network
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ReseauHydrographique.
   * @return instance unique (singleton) de ReseauHydrographique.
   */
  private INetwork hydroNetwork = null;

  public INetwork getHydroNetwork() {
    if (this.hydroNetwork == null) {
      this.hydroNetwork = this.getCartAGenDB().getGeneObjImpl()
          .getCreationFactory().createNetwork(new ReseauImpl());
      for (INetworkSection section : this.getWaterLines()) {
        this.hydroNetwork.addSection(section);
      }
    }
    return this.hydroNetwork;
  }

  // ///////////////////////////////////////
  // Railway network
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ReseauFerroviaire.
   * @return instance unique (singleton) de ReseauFerroviaire.
   */
  private INetwork railwayNetwork = null;

  public INetwork getRailwayNetwork() {
    if (this.railwayNetwork == null) {
      this.railwayNetwork = this.getCartAGenDB().getGeneObjImpl()
          .getCreationFactory().createNetwork(new ReseauImpl());
    }
    return this.railwayNetwork;
  }

  // ///////////////////////////////////////
  // Electricity network
  // ///////////////////////////////////////

  /**
   * Récupération de l'instance unique (singleton) de ReseauElectricite.
   * @return instance unique (singleton) de ReseauElectricite.
   */
  private INetwork electricityNetwork = null;

  public INetwork getElectricityNetwork() {
    if (this.electricityNetwork == null) {
      this.electricityNetwork = this.getCartAGenDB().getGeneObjImpl()
          .getCreationFactory().createNetwork(new ReseauImpl());
    }
    return this.electricityNetwork;
  }

  /**
   * Get the good network feature (e.g. road network, river network) from a
   * class instance.
   * @param classObj
   * @return
   */
  public INetwork getNetworkFromClass(Class<? extends IGeneObj> classObj) {
    if (IRoadLine.class.isAssignableFrom(classObj))
      return getRoadNetwork();
    if (IWaterLine.class.isAssignableFrom(classObj))
      return getHydroNetwork();
    if (IRailwayLine.class.isAssignableFrom(classObj))
      return getRailwayNetwork();
    if (IElectricityLine.class.isAssignableFrom(classObj))
      return getElectricityNetwork();
    return null;
  }

  /**
   * Gets the geometry pool of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IFeature> getGeometryPoolPop() {
    return (IPopulation<IFeature>) this.getPopulation(GEOM_POOL);
  }

  public GeometryPool getGeometryPool() {
    return geometryPool;
  }

  public void setGeometryPool(GeometryPool geometryPool) {
    this.geometryPool = geometryPool;
  }

  // ///////////////////////////////////////
  // SAVE POPULATIONS OF THE DATASET
  // ///////////////////////////////////////

  public void saveRoadLinesInSHP(String chemin) {

    try {

      IFeatureCollection<IFeature> coll = new FT_FeatureCollection<IFeature>();
      for (IRoadLine section : CartAGenDoc.getInstance().getCurrentDataset()
          .getRoads()) {
        coll.add(section);
      }

      // GeometryCollection geom=
      ShapefileWriter.write(coll, chemin + ".shp");
    } finally {
    }
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation(String nomPopulation) {
    IPopulation<? extends IFeature> pop = super.getPopulation(nomPopulation);
    FeatureType ft = new FeatureType();
    ft.setTypeName(this.getFeatureTypeFromPopName(nomPopulation));
    if (pop != null) {
      pop.setFeatureType(ft);
      return pop;
    } else {
      pop = new Population<IGeneObj>();
      pop.setNom(nomPopulation);
      pop.setFeatureType(ft);
      this.addPopulation(pop);
      return pop;
    }
  }

}
