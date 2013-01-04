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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminCapital;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IRiverSimpleIsland;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.misc.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IElectricityLine;
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
import fr.ign.cogit.cartagen.core.genericschema.road.IPath;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadArea;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadFacilityPoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.dataset.SpecialPoint;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationLeftPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Layer;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LoadedLayer;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.RoadSymbolResult;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.TronconFerreImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.SurfaceDEauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.TronconHydrographiqueImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.PointCoteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
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
    System.out.println(this.getPopulations().size() + " populations créées");
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

  public static final String ROADS_POP = "roads";
  public static final String ROAD_NODES_POP = "roadNodes";
  public static final String ROAD_FACILITY_PT_POP = "roadFacilityPoints";
  public static final String ROAD_AREA_POP = "roadAreas";
  public static final String PATHS_POP = "paths";

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
  public static final String POI_POP = "pointsOfInterest";

  public static final String ROAD_STROKES_POP = "strokes";
  public static final String ROUNDABOUTS_POP = "roundabouts";
  public static final String BRANCHINGS_POP = "branchingCrossroads";

  public static final String ADMIN_CAP_POP = "adminCapitals";
  public static final String ADMIN_UNIT_POP = "adminUnits";
  public static final String ADMIN_LIMIT_POP = "adminLimits";

  public static final String MASK = "mask";

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
    } else if (obj instanceof IPath) {
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
    }
    return null;
  }

  /**
   * Gets the population name of an object of the dataset
   * @param obj an object of the dataset
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
    } else if (IPath.class.isAssignableFrom(classObj)) {
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
    } else
      pop.setFeatureType(ft);
    return pop;
  }

  /**
   * Generic getter for each population
   * @param nomPopulation
   * @return
   */

  @SuppressWarnings("unchecked")
  public IPopulation<IGeneObj> getCartagenPop(String nomPopulation) {
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this
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
  public IPopulation<IPath> getPaths() {
    return (IPopulation<IPath>) this.getCartagenPop(CartAGenDataSet.PATHS_POP,
        IPath.FEAT_TYPE_NAME);
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
    return (IPopulation<SpecialPoint>) this
        .getPopulation(CartAGenDataSet.SPECIAL_POINTS_POP);
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
   * Erases the towns of the dataset
   */
  public void eraseTowns() {
    this.getTowns().clear();
  }

  // ///////////////////////////////////////
  // DATA LOADING METHODS
  // ///////////////////////////////////////

  // ///////////////////////////////////////
  // Buildings
  // ///////////////////////////////////////

  /**
   * Charge des batiments depuis un shapefile surfacique
   * @param chemin
   * @throws IOException
   */
  public boolean loadBuildingsFromSHP(String chemin) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IBuilding> buildPop = this.getBuildings();

    int j = 0;
    while (shr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      // get the building nature
      String nature = "Indifferencie";
      if (fields.containsKey("NATURE"))
        nature = (String) fields.get("NATURE");
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPolygon) {
        IBuilding building = CartagenApplication.getInstance()
            .getCreationFactory().createBuilding(new BatimentImpl(geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          building.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          building.setShapeId(j);
        }
        building.setId(this.getBuildings().size() + 1);
        building.setNature(nature);
        buildPop.add(building);

      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IBuilding building = CartagenApplication.getInstance()
              .getCreationFactory().createBuilding(
                  new BatimentImpl(((IMultiSurface<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            building.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            building.setShapeId(j);
          }
          building.setId(this.getBuildings().size() + 1);
          building.setNature(nature);
          buildPop.add(building);
        }

      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    CartagenApplication.getInstance().getLayerGroup().cVoirBati
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirBati.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirBati.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectBati
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lBati.setEnabled(true);
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des batiments depuis un shapefile surfacique
   * @param chemin
   * @throws IOException
   */
  public boolean overwriteBuildingsFromSHP(String chemin) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IBuilding> buildPop = this.getBuildings();

    int j = 0;
    while (shr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IBuilding geneObj = null;
      for (IBuilding r : buildPop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof IPolygon) {
        geneObj.setGeom(geom);

      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * les faces reseau du jeu de données
   */
  private IPopulation<INetworkFace> listeFacesReseau = new Population<INetworkFace>();

  public IPopulation<INetworkFace> getFacesReseau() {
    return this.listeFacesReseau;
  }

  public void eraseFacesReseau() {
    this.listeFacesReseau.clear();
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
      this.roadNetwork = CartagenApplication.getInstance().getCreationFactory()
          .createNetwork(new ReseauImpl());
      for (INetworkSection section : this.getRoads()) {
        this.roadNetwork.addSection(section);
      }
    }
    return this.roadNetwork;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. recupere le
   * premier attribut qui doit etre entier et traduit l'imporance de chaque
   * troncon applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public boolean loadRoadLinesFromSHP(String chemin, SourceDLM sourceDlm,
      SymbolList symbols) throws IOException {
    if (sourceDlm.equals(SourceDLM.BD_TOPO_V2))
      return this.loadRoadLinesBDTopoV2_25FromSHP(chemin, sourceDlm, symbols);
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IRoadLine> pop = this.getRoads();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();
      // compute the symbol from the fields according to source DLM
      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getRoadSymbolFromFields(sourceDlm,
          symbols, fields);
      int importance = result.importance;

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                    .getGeoxObj(), false, (ILineString) geom), importance,
                result.symbolId);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        this.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = CartagenApplication
              .getInstance()
              .getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), importance);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          this.getRoadNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. recupere le
   * premier attribut qui doit etre entier et traduit l'imporance de chaque
   * troncon applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public boolean overwriteRoadLinesFromSHP(String chemin, double doug,
      SourceDLM sourceDlm, SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IRoadLine> pop = this.getRoads();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      // compute the symbol from the fields according to source DLM
      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getRoadSymbolFromFields(sourceDlm,
          symbols, fields);
      int importance = result.importance;

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IRoadLine geneObj = null;
      for (IRoadLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(geom);
        geneObj.setImportance(importance);
        geneObj.setSymbolId(result.symbolId);

      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. recupere le
   * premier attribut qui doit etre entier et traduit l'imporance de chaque
   * troncon
   * @param chemin
   * @throws IOException
   */
  public boolean loadRoadLinesFromSHPBasic(String chemin) throws IOException {
    return this.loadRoadLinesFromSHPBasic(chemin, 0);
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. recupere le
   * premier attribut qui doit etre entier et traduit l'imporance de chaque
   * troncon applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public boolean loadRoadLinesFromSHPBasic(String chemin, int SymbolId)
      throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    while (shr.hasNext() && dbr.hasNext()) {

      // String SymbolName="";
      Record objet = shr.nextRecord();

      /*
       * if (ATT_Importance == 4) { symbolVarName = "VAL_rte_p_4"; } else if
       * (ATT_Importance == 3) { symbolVarName = "VAL_rte_p_3"; } else if
       * (ATT_Importance == 2) { symbolVarName = "VAL_rte_l_2"; } else if
       * (ATT_Importance == 1) { symbolVarName = "VAL_rte_l_1"; } else if
       * (ATT_Importance == 0) { symbolVarName = "VAL_rte_l_1"; }
       */

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {

        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork(), false,
                    (ILineString) geom), 4, SymbolId);

        this.getRoads().add(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {

          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), 4,
                  SymbolId);

          this.getRoads().add(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. Adapte a la
   * BDTopo V2 pour de la symbo 25K
   * @param chemin
   * @param doug
   * @param sourceDlm
   * @param symbols
   * @throws IOException
   */
  public boolean loadRoadLinesBDTopoV2_25FromSHP(String chemin,
      SourceDLM sourceDlm, SymbolList symbols) throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    IPopulation<IRoadLine> popRoadLines = this.getRoads();

    while (shr.hasNext() && dbr.hasNext()) {

      Record objet = shr.nextRecord();

      // recupere le champ importance
      Object[] champs = new Object[nbFields];

      dbr.readEntry(champs);

      /*
       * RoadSymbolResult result =SymbolsUtil.getRoadSymbolFromFields(
       * SourceDLM.BD_TOPO_V2, CartagenApplication.getSymbols(), champs,
       * fieldNames);
       */

      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getRoadSymbolFromFields(sourceDlm,
          symbols, fields);

      if (result.symbolId == -1) {
        System.err
            .println("symbolVarName do not exist in the symbols XML file");
        continue; // erreur
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                    .getGeoxObj(), false, (ILineString) geom),
                result.importance, result.symbolId);

        popRoadLines.add(tr);
        this.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)),
                  result.importance, result.symbolId);

          popRoadLines.add(tr);
          this.getRoadNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  public boolean loadRoadLinesBDTopoVTemp_25FromSHP(String chemin,
      SourceDLM sourceDlm, SymbolList symbols) throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    IPopulation<IRoadLine> popRoadLines = this.getRoads();

    while (shr.hasNext() && dbr.hasNext()) {

      Record objet = shr.nextRecord();

      // recupere le champ importance
      Object[] champs = new Object[nbFields];

      dbr.readEntry(champs);

      /*
       * RoadSymbolResult result =SymbolsUtil.getRoadSymbolFromFields(
       * SourceDLM.BD_TOPO_V2, CartagenApplication.getSymbols(), champs,
       * fieldNames);
       */

      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getRoadSymbolFromFields(sourceDlm,
          symbols, fields);

      if (result.symbolId == -1) {
        System.err
            .println("symbolVarName do not exist in the symbols XML file");
        continue; // erreur
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                    .getGeoxObj(), false, (ILineString) geom),
                result.importance, result.symbolId);

        popRoadLines.add(tr);
        this.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)),
                  result.importance, result.symbolId);

          popRoadLines.add(tr);
          this.getRoadNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. Adapte a la
   * BDTopo V2 pour de la symbo 25K
   * @param chemin
   * @param doug
   * @param sourceDlm
   * @param symbols
   * @throws IOException
   */
  public boolean loadPathsBDTopoV2_25FromSHP(String chemin,
      SourceDLM sourceDlm, SymbolList symbols) throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    IPopulation<IPath> popPaths = this.getPaths();

    while (shr.hasNext() && dbr.hasNext()) {

      Record objet = shr.nextRecord();

      // recupere le champ importance
      Object[] champs = new Object[nbFields];

      dbr.readEntry(champs);

      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getPathSymbolFromFields(sourceDlm,
          symbols, fields);

      if (result.symbolId == -1) {
        System.err
            .println("symbolVarName do not exist in the symbols XML file");
        continue; // erreur
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IPath tr = CartagenApplication.getInstance().getCreationFactory()
            .createPath((ILineString) geom, result.importance, result.symbolId);

        popPaths.add(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IPath tr = CartagenApplication.getInstance().getCreationFactory()
              .createPath((ILineString) ((IMultiCurve<?>) geom).get(i),
                  result.importance, result.symbolId);

          popPaths.add(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. Adapte a la
   * BDTopo V1 pour de la symbo 25K
   * @param chemin
   * @param doug
   * @param sourceDlm
   * @param symbols
   * @throws IOException
   */
  public boolean loadRoadLinesBDTopoV1_25FromSHP(String chemin, double doug,
      SourceDLM sourceDlm, SymbolList symbols) throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    while (shr.hasNext() && dbr.hasNext()) {

      Record objet = shr.nextRecord();

      // recupere le champ importance
      Object[] champs = new Object[nbFields];

      dbr.readEntry(champs);

      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      RoadSymbolResult result = SymbolsUtil.getRoadSymbolFromFields(sourceDlm,
          symbols, fields);

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                    .getGeoxObj(), false, (ILineString) geom),
                result.importance, result.symbolId);
        this.getRoads().add(tr);
        this.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)),
                  result.importance, result.symbolId);
          this.getRoads().add(tr);
          this.getRoadNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de route depuis un shapefile lineaire. Adapte a la
   * BDCarto
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public boolean loadRoadLinesBDCartoFromSHP(String chemin, double doug)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IRoadLine> pop = this.getRoads();

    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      // recupere le champ importance
      Object[] champs = dbr.readEntry();
      int importance = Integer.parseInt(champs[0].toString());

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createRoadLine(
                new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                    .getGeoxObj(), false, (ILineString) geom), importance);
        pop.add(tr);
        this.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = CartagenApplication
              .getInstance()
              .getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), importance);

          pop.add(tr);
          this.getRoadNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);
    return true;
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
      this.hydroNetwork = CartagenApplication.getInstance()
          .getCreationFactory().createNetwork(new ReseauImpl());
      for (INetworkSection section : this.getWaterLines()) {
        this.hydroNetwork.addSection(section);
      }
    }
    return this.hydroNetwork;
  }

  /**
   * Charge des troncons de cours d'eau depuis un shapefile lineaire. applique
   * un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official list
   * @throws IOException
   */
  public boolean loadWaterLinesFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    if (CartAGenDataSet.logger.isDebugEnabled()) {
      CartAGenDataSet.logger.debug("Loading: "
          + IWaterLine.class.getSimpleName());
    }
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterLine> pop = this.getWaterLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IWaterLine tr = CartagenApplication.getInstance().getCreationFactory()
            .createWaterLine(
                new TronconHydrographiqueImpl((Reseau) this.getHydroNetwork()
                    .getGeoxObj(), false, (ILineString) geom), 0);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        this.getHydroNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IWaterLine tr = CartagenApplication.getInstance()
              .getCreationFactory().createWaterLine(
                  new TronconHydrographiqueImpl((Reseau) this.getHydroNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          this.getHydroNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRH.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRH.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRHInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRH.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de cours d'eau depuis un shapefile lineaire. applique
   * un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official list
   * @throws IOException
   */
  public boolean overwriteWaterLinesFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    if (CartAGenDataSet.logger.isDebugEnabled()) {
      CartAGenDataSet.logger.debug("Loading: "
          + IWaterLine.class.getSimpleName());
    }
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterLine> pop = this.getWaterLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IWaterLine geneObj = null;
      for (IWaterLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des surfaces hydrographiques depuis un shapefile surfacique.
   * applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official list
   * @throws IOException
   */
  public boolean loadWaterAreasFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterArea> pop = this.getWaterAreas();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPolygon) {
        IWaterArea surf = CartagenApplication.getInstance()
            .getCreationFactory().createWaterArea(
                new SurfaceDEauImpl((Reseau) this.getHydroNetwork()
                    .getGeoxObj(), (IPolygon) geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          surf.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          surf.setShapeId(j);
        }
        pop.add(surf);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IPolygon polygon = (IPolygon) ((IMultiSurface<?>) geom).get(i);
          IWaterArea surf = CartagenApplication.getInstance()
              .getCreationFactory().createWaterArea(
                  new SurfaceDEauImpl((Reseau) this.getHydroNetwork()
                      .getGeoxObj(), polygon));
          if (fields.containsKey("CARTAGEN_ID")) {
            surf.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            surf.setShapeId(j);
          }
          pop.add(surf);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRH.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRH.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRHInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRH.setEnabled(true);
    return true;
  }

  /**
   * Charge des surfaces hydrographiques depuis un shapefile surfacique.
   * applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official list
   * @throws IOException
   */
  public boolean overwriteWaterAreasFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterArea> pop = this.getWaterAreas();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IWaterArea geneObj = null;
      for (IWaterArea r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof IPolygon) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
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
      this.railwayNetwork = CartagenApplication.getInstance()
          .getCreationFactory().createNetwork(new ReseauImpl());
    }
    return this.railwayNetwork;
  }

  /**
   * Charge des troncons de voie ferree depuis un shapefile lineaire. applique
   * un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean loadRailwayLineFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IRailwayLine> pop = this.getRailwayLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRailwayLine tr = CartagenApplication.getInstance()
            .getCreationFactory().createRailwayLine(
                new TronconFerreImpl((Reseau) this.getRailwayNetwork()
                    .getGeoxObj(), false, (ILineString) geom), 0);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        this.getRailwayNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRailwayLine tr = CartagenApplication.getInstance()
              .getCreationFactory().createRailwayLine(
                  new TronconFerreImpl((Reseau) this.getRailwayNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          this.getRailwayNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRF.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRF.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRF.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRF.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRFInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRF.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons de voie ferree depuis un shapefile lineaire. applique
   * un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean overwriteRailwayLineFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IRailwayLine> pop = this.getRailwayLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IRailwayLine geneObj = null;
      for (IRailwayLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
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
      this.electricityNetwork = CartagenApplication.getInstance()
          .getCreationFactory().createNetwork(new ReseauImpl());
    }
    return this.electricityNetwork;
  }

  /**
   * Charge des troncons electriques depuis un shapefile lineaire. applique un
   * filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean loadElectricityLinesFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IElectricityLine> pop = this.getElectricityLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IElectricityLine tr = CartagenApplication.getInstance()
            .getCreationFactory().createElectricityLine(
                new ArcReseauImpl((Reseau) this.getElectricityNetwork()
                    .getGeoxObj(), false, (ILineString) geom), 0);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        this.getElectricityNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IElectricityLine tr = CartagenApplication.getInstance()
              .getCreationFactory().createElectricityLine(
                  new ArcReseauImpl((Reseau) this.getElectricityNetwork()
                      .getGeoxObj(), false,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          this.getElectricityNetwork().addSection(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirRE.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRE.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRE.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRE.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirREInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRE.setEnabled(true);
    return true;
  }

  /**
   * Charge des troncons electriques depuis un shapefile lineaire. applique un
   * filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean overwriteElectricityLinesFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IElectricityLine> pop = this.getElectricityLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IElectricityLine geneObj = null;
      for (IElectricityLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
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
          this.reliefField = CartagenApplication.getInstance()
              .getCreationFactory().createReliefField(new ChampContinuImpl());
        }
      }
    }
    return this.reliefField;
  }

  /**
   * Charge des courbes de niveau depuis un shapefile lineaire. le premier champ
   * doit etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean loadContourLinesFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IContourLine> pop = this.getContourLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      // recupere le champ altitude
      double z = 0;
      try {
        z = Double.parseDouble(fields.get("ALTITUDE").toString());
      } catch (Exception e) {
        CartAGenDataSet.logger
            .debug("No altitude attached to the contour line");
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IContourLine cn = CartagenApplication.getInstance()
            .getCreationFactory().createContourLine(
                new CourbeDeNiveauImpl(this.getReliefField().getChampContinu(),
                    z, (ILineString) geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          cn.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          cn.setShapeId(j);
        }
        pop.add(cn);
        this.getReliefField().addContourLine(cn);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IContourLine cn = CartagenApplication.getInstance()
              .getCreationFactory().createContourLine(
                  new CourbeDeNiveauImpl(this.getReliefField()
                      .getChampContinu(), z,
                      (ILineString) ((IMultiCurve<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            cn.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            cn.setShapeId(j);
          }
          pop.add(cn);
          this.getReliefField().addContourLine(cn);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirCN.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectCN.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCNInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lCN.setEnabled(true);
    return true;
  }

  /**
   * Charge des courbes de niveau depuis un shapefile lineaire. le premier champ
   * doit etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean overwriteContourLinesFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IContourLine> pop = this.getContourLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      // recupere le champ altitude
      double z = 0;
      try {
        z = Double.parseDouble(fields.get("ALTITUDE").toString());
      } catch (Exception e) {
        CartAGenDataSet.logger
            .debug("No altitude attached to the contour line");
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IContourLine geneObj = null;
      for (IContourLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(CommonAlgorithms.filtreDouglasPeucker(geom, doug));
        geneObj.setAltitude(z);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des courbes de niveau depuis un shapefile lineaire. le premier champ
   * doit etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean loadReliefElementLinesFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IReliefElementLine> pop = this.getReliefLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IReliefElementLine line = CartagenApplication.getInstance()
            .getCreationFactory().createReliefElementLine(
                new ElementCaracteristiqueDuReliefImpl(this.getReliefField()
                    .getChampContinu(), geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          line.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          line.setShapeId(j);
        }
        pop.add(line);
        this.getReliefField().addReliefElementLine(line);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IReliefElementLine line = CartagenApplication.getInstance()
              .getCreationFactory().createReliefElementLine(
                  new ElementCaracteristiqueDuReliefImpl(this.getReliefField()
                      .getChampContinu(), ((IMultiCurve<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            line.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            line.setShapeId(j);
          }
          pop.add(line);
          this.getReliefField().addReliefElementLine(line);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirCN.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectCN.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirCNInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lCN.setEnabled(true);
    return true;
  }

  /**
   * Charge des courbes de niveau depuis un shapefile lineaire. le premier champ
   * doit etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param doug
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean overwriteReliefElementLinesFromSHP(String chemin, double doug,
      SymbolList symbols) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IReliefElementLine> pop = this.getReliefLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      // recupere la geometrie
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IReliefElementLine geneObj = null;
      for (IReliefElementLine r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof ILineString) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des points cotes depuis un shapefile ponctuel. le premier champ doit
   * etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean loadSpotHeightsFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<ISpotHeight> pop = this.getSpotHeights();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      double z = Double.parseDouble(champs[0].toString());
      // System.out.println("z="+z);
      if (geom instanceof IPoint) {
        ISpotHeight pt = CartagenApplication.getInstance().getCreationFactory()
            .createSpotHeight(
                new PointCoteImpl(this.getReliefField().getChampContinu(), z,
                    (IPoint) geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          pt.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          pt.setShapeId(j);
        }
        pop.add(pt);
        this.getReliefField().addSpotHeight(pt);
      } else if (geom instanceof IMultiPoint) {
        for (int i = 0; i < ((IMultiPoint) geom).size(); i++) {
          ISpotHeight pt = CartagenApplication.getInstance()
              .getCreationFactory().createSpotHeight(
                  new PointCoteImpl(this.getReliefField().getChampContinu(), z,
                      ((IMultiPoint) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            pt.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            pt.setShapeId(j);
          }
          pop.add(pt);
          this.getReliefField().addSpotHeight(pt);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirPointCote
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCote
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCote
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectPointCote
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCoteInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lPointCote.setEnabled(true);
    return true;
  }

  /**
   * Charge des points cotes depuis un shapefile ponctuel. le premier champ doit
   * etre numerique et donner l'altitude applique un filtre de dp a chaque
   * geometrie
   * @param chemin
   * @param symbols TODO used to get symbol from official map list
   * @throws IOException
   */
  public boolean overwriteSpotHeightsFromSHP(String chemin, SymbolList symbols)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<ISpotHeight> pop = this.getSpotHeights();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      double z = Double.parseDouble(champs[0].toString());

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      ISpotHeight geneObj = null;
      for (ISpotHeight r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof IPoint) {
        geneObj.setGeom(geom);
        geneObj.setZ(z);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge un MNT grille a partir d'un fichier au format xyz
   * @param chemin
   */
  public boolean loadDEMPixelsFromSHP(String chemin) {

    FileReader fr = null;
    try {
      fr = new FileReader(chemin + ".xyz");
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    BufferedReader br = new BufferedReader(fr);

    try {
      String s = br.readLine();
      StringTokenizer st;
      double x, y, z;
      while (s != null) {
        st = new StringTokenizer(s);

        if (!st.hasMoreTokens()) {
          continue;
        }
        x = Double.parseDouble(st.nextToken());
        if (!st.hasMoreTokens()) {
          continue;
        }
        y = Double.parseDouble(st.nextToken());
        if (!st.hasMoreTokens()) {
          continue;
        }
        z = Double.parseDouble(st.nextToken());

        IDEMPixel pix = CartagenApplication.getInstance().getCreationFactory()
            .createDEMPixel(x, y, z);
        this.getDEMPixels().add(pix);
        this.getReliefField().addDEMPixel(pix);

        s = br.readLine();
      }
      br.close();
      fr.close();
      GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegrade
          .setEnabled(true);
      GeneralisationLeftPanelComplement.getInstance().lMNTDegrade
          .setEnabled(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  // ///////////////////////////////////////
  // Mask
  // ///////////////////////////////////////

  /**
   * Charge masque depuis un shapefile surfacique.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public boolean loadMaskFromSHP(String chemin) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IMask> pop = this.getMasks();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      // int type=1;
      if (geom instanceof IPolygon) {
        IMask tr = CartagenApplication.getInstance().getCreationFactory()
            .createMask(new GM_LineString(geom.coord()));
        CartagenApplication.getInstance().getFrame().getLayerManager()
            .addMasque(tr);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IMask tr = CartagenApplication.getInstance().getCreationFactory()
              .createMask(
                  new GM_LineString(((IMultiSurface<?>) geom).get(i).coord()));
          CartagenApplication.getInstance().getFrame().getLayerManager()
              .addMasque(tr);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    CartagenApplication.getInstance().getLayerGroup().cVoirMasque
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirMask.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirMask.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().lMask.setEnabled(true);

    return true;
  }

  /**
   * Charge masque depuis un shapefile surfacique.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public boolean overwriteMaskFromSHP(String chemin) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<IMask> pop = this.getMasks();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      // get the object from its ID
      Integer id1 = (Integer) fields.get(GeographicClass.ID_NAME);
      IMask geneObj = null;
      for (IMask r : pop) {
        if (r.getId() == id1.intValue()) {
          geneObj = r;
          break;
        }
      }

      if (geneObj != null && geom instanceof IPolygon) {
        geneObj.setGeom(geom);
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    return true;
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

  /**
   * Charge zones occ sol depuis un shapefile surfacique. applique un filtre de
   * dp a chaque geometrie
   * @param chemin chemin du shapefile
   * @param dp seuil utilisé par l'algorithme de DouglasPeucker
   * @throws IOException
   */
  public boolean loadLandUseAreasFromSHP(String chemin, double dp)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<ISimpleLandUseArea> pop = this.getLandUseAreas();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      int type = 1;
      if (geom instanceof IPolygon) {
        ISimpleLandUseArea area = CartagenApplication.getInstance()
            .getCreationFactory().createSimpleLandUseArea(
                (IPolygon) CommonAlgorithms.filtreDouglasPeucker(geom, dp),
                type);
        if (fields.containsKey("CARTAGEN_ID")) {
          area.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          area.setShapeId(j);
        }
        pop.add(area);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          ISimpleLandUseArea area = CartagenApplication.getInstance()
              .getCreationFactory().createSimpleLandUseArea(
                  (IPolygon) CommonAlgorithms.filtreDouglasPeucker(
                      ((IMultiSurface<?>) geom).get(i), dp), type);
          if (fields.containsKey("CARTAGEN_ID")) {
            area.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            area.setShapeId(j);
          }
          pop.add(area);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectOccSol
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSolInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lOccSol.setEnabled(true);
    return true;
  }

  /**
   * Charge zones occ sol depuis un shapefile surfacique. applique un filtre de
   * dp a chaque geometrie.
   * @param chemin chemin du shapefile
   * @param dp seuil utilisé par l'algorithme de DouglasPeucker
   * @param type de zone d'occ sol (1=VEGET;2=ZONE D'ACTIVITE)
   * @throws IOException
   */
  public boolean loadLandUseAreasFromSHP(String chemin, double dp, int type)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    IPopulation<ISimpleLandUseArea> pop = this.getLandUseAreas();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPolygon) {
        ISimpleLandUseArea area = CartagenApplication.getInstance()
            .getCreationFactory().createSimpleLandUseArea(
                (IPolygon) CommonAlgorithms.filtreDouglasPeucker(geom, dp),
                type);
        if (fields.containsKey("CARTAGEN_ID")) {
          area.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          area.setShapeId(j);
        }
        pop.add(area);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          ISimpleLandUseArea area = CartagenApplication.getInstance()
              .getCreationFactory().createSimpleLandUseArea(
                  (IPolygon) CommonAlgorithms.filtreDouglasPeucker(
                      ((IMultiSurface<?>) geom).get(i), dp), type);
          if (fields.containsKey("CARTAGEN_ID")) {
            area.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            area.setShapeId(j);
          }
          pop.add(area);
        }
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectOccSol
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSolInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lOccSol.setEnabled(true);
    return true;
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

  /**
   * Charge les zones administratives depuis un shapefile.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public boolean loadAdminAreasFromSHP(String chemin) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      if (geom instanceof IPolygon) {
        CartAGenDataSet.logger.error("a revoir");
      } else if (geom instanceof IMultiSurface<?>) {
        CartAGenDataSet.logger.error("a revoir");
      } else {
        CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    CartagenApplication.getInstance().getLayerGroup().cVoirAdmin
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdmin.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdmin
        .setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectAdmin
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdminInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lAdmin.setEnabled(true);
    return true;
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

  // ///////////////////////////////////////
  // RESET OF DATASET (to be improved)
  // ///////////////////////////////////////

  /***
   * Total reset of the dataset
   */

  public void resetDataSet() {

    // Eliminates the objects and commits the Gothic cache
    for (Layer lay : CartagenApplication.getInstance().getFrame()
        .getLayerManager().getLayers()) {
      if (lay instanceof LoadedLayer) {
        if (((LoadedLayer) lay).getFeatures() == null)
          continue;
        for (IFeature feat : ((LoadedLayer) lay).getFeatures()) {
          if (!(feat instanceof IGeneObj)) {
            continue;
          }
          ((IGeneObj) feat).eliminate();
        }
      }
    }

    // Destructs the objects and empties the layers
    for (Layer lay : CartagenApplication.getInstance().getFrame()
        .getLayerManager().getLayers()) {
      if (lay instanceof LoadedLayer) {
        for (@SuppressWarnings("unused")
        IFeature feat : ((LoadedLayer) lay).getFeatures()) {
          feat = null;
        }
        ((LoadedLayer) lay).getFeatures().clear();
      }
      lay.emptyDisplayCache();
    }

    // Disable layers management

    GeneralisationLeftPanelComplement.getInstance().cVoirBati
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirBati.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectBati
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectBati
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirBatiInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lBati.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirBati
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirBati
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirVille
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirVille
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectVille
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectVille
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirVilleInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirVilleInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lVille.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirVille
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirVille
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirIlot
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirIlot.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectIlot
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectIlot
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirIlotInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirIlotInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lIlot.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirIlot
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirIlot
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirAlign
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAlign
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectAlign
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectAlign
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAlignInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAlignInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lAlign.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirAlign
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirAlign
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRR
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRR
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirRF.setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRF.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRF
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRF.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRFInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRFInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lRF.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRF
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRF
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRH.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRH
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRH.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRHInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRHInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lRH.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRH
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRH
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirRE.setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirRE.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRE
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectRE.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirREInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirREInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lRE.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirRE
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirRE
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirCN.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectCN
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectCN.setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirCNInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirCNInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lCN.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirCN
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirCN
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirPointCote
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCote
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectPointCote
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectPointCote
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCoteInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirPointCoteInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lPointCote
        .setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirPointCote
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirPointCote
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparent
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparent
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectOmbrageTransparent
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectOmbrageTransparent
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparentInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOmbrageTransparentInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lOmbrageTransparent
        .setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirOmbrageTransparent
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirOmbrageTransparent
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegrade
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegrade
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectMNTDegrade
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectMNTDegrade
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegradeInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirMNTDegradeInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lMNTDegrade
        .setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirMNTDegrade
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirMNTDegrade
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrie
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrie
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectHypsometrie
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectHypsometrie
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrieInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirHypsometrieInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lHypsometrie
        .setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirHypsometrie
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirHypsometrie
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSol
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectOccSol
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectOccSol
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSolInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirOccSolInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lOccSol.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirOccSol
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirOccSol
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirAdmin
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdmin
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectAdmin
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectAdmin
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdminInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirAdminInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lAdmin.setEnabled(false);
    CartagenApplication.getInstance().getLayerGroup().cVoirAdmin
        .setSelected(false);
    CartagenApplication.getInstance().getInitialLayerGroup().cVoirAdmin
        .setSelected(false);

    GeneralisationLeftPanelComplement.getInstance().cVoirNetworkFaces
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirNetworkFaces
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectNetworkFaces
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cSelectNetworkFaces
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirNetworkFacesInitial
        .setSelected(false);
    GeneralisationLeftPanelComplement.getInstance().cVoirNetworkFacesInitial
        .setEnabled(false);
    GeneralisationLeftPanelComplement.getInstance().lNetworkFaces
        .setEnabled(false);

  }

  public void loadOSCyclePath(String chemin, int symbolID) {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (Exception e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    try {
      while (shr.hasNext() && dbr.hasNext()) {

        // String SymbolName="";

        Record objet = shr.nextRecord();

        Object[] champs = new Object[nbFields];

        dbr.readEntry(champs);

        // ATT_Importance = Integer.parseInt(champs[0].toString());

        int id = Integer.parseInt(champs[0].toString());

        // recupere la geometrie
        IGeometry geom = null;
        try {
          geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }

        if (geom instanceof ILineString) {
          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork(), false,
                      (ILineString) geom), 4, symbolID);
          tr.setId(id);
          this.getRoads().add(tr);

        } else if (geom instanceof IMultiCurve<?>) {
          for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
            IRoadLine tr = CartagenApplication.getInstance()
                .getCreationFactory().createRoadLine(
                    new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                        .getGeoxObj(), false,
                        (ILineString) ((IMultiCurve<?>) geom).get(i)), 4,
                    symbolID);
            tr.setId(id);
            this.getRoads().add(tr);
          }
        } else {
          CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
              + chemin + ". Type de geometrie " + geom.getClass().getName()
              + " non gere.");
        }
      }

      shr.close();
      dbr.close();

    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);

  }

  public void loadOSRoads(String chemin, int symbolID,
      @SuppressWarnings("unused") String IDAtribbutName) {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (Exception e) {
      if (CartAGenDataSet.logger.isDebugEnabled()) {
        CartAGenDataSet.logger.debug("fichier " + chemin + " non trouve.");
      }
      return;
    }

    if (CartAGenDataSet.logger.isInfoEnabled()) {
      CartAGenDataSet.logger.info("Loading: " + chemin);
    }

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    int nbFields = dbr.getHeader().getNumFields();
    String[] fieldNames = new String[nbFields];
    Class<?>[] fieldClasses = new Class<?>[nbFields];
    for (int i = 0; i < nbFields; i++) {
      fieldNames[i] = dbr.getHeader().getFieldName(i);
      fieldClasses[i] = dbr.getHeader().getFieldClass(i);
    }

    try {
      while (shr.hasNext() && dbr.hasNext()) {

        // String SymbolName="";

        Record objet = shr.nextRecord();

        Object[] champs = new Object[nbFields];

        dbr.readEntry(champs);

        // ATT_Importance = Integer.parseInt(champs[0].toString());

        String key = champs[1].toString();

        // recupere la geometrie
        IGeometry geom = null;
        try {
          geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
        } catch (Exception e) {
          e.printStackTrace();
          return;
        }

        if (geom instanceof ILineString) {
          IRoadLine tr = CartagenApplication.getInstance().getCreationFactory()
              .createRoadLine(
                  new TronconDeRouteImpl((Reseau) this.getRoadNetwork(), false,
                      (ILineString) geom), 4, symbolID);

          tr.setAttribute(new AttributeType("ID", "String"), key);
          this.getRoads().add(tr);

        } else if (geom instanceof IMultiCurve<?>) {
          for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
            IRoadLine tr = CartagenApplication.getInstance()
                .getCreationFactory().createRoadLine(
                    new TronconDeRouteImpl((Reseau) this.getRoadNetwork()
                        .getGeoxObj(), false,
                        (ILineString) ((IMultiCurve<?>) geom).get(i)), 4,
                    symbolID);
            // tr.setAttribute(new AttributeType("KEY","KEY"), key);
            this.getRoads().add(tr);
          }
        } else {
          CartAGenDataSet.logger.error("ERREUR lors du chargement de shp "
              + chemin + ". Type de geometrie " + geom.getClass().getName()
              + " non gere.");
        }
      }

      shr.close();
      dbr.close();

    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    CartagenApplication.getInstance().getLayerGroup().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRR.setSelected(true);
    GeneralisationLeftPanelComplement.getInstance().cSelectRR.setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().cVoirRRInitial
        .setEnabled(true);
    GeneralisationLeftPanelComplement.getInstance().lRR.setEnabled(true);

  }

}
