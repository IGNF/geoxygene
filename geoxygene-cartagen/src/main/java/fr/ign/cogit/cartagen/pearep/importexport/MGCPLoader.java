/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.geotools.data.shapefile.shp.ShapefileException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPipeLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPowerStationArea;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPowerStationPoint;
import fr.ign.cogit.cartagen.core.genericschema.harbour.IBerthingArea;
import fr.ign.cogit.cartagen.core.genericschema.harbour.IBerthingLine;
import fr.ign.cogit.cartagen.core.genericschema.harbour.IDryDockArea;
import fr.ign.cogit.cartagen.core.genericschema.harbour.ITrainingWallArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IDitchLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IInundationArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterBasin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.IWoodLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuildPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuilding;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPCommunicationStation;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPCommunicationStationPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPControlTowerPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPCranePoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPDryDockArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFlarePipePoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFortification;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPLandUse;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPLandUseType;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPLighthousePoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPMineralPile;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPNonCommunicationTowerPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPPowerStationArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPPowerStationPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPProcessingFacility;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPPumpingStation;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPRailwayYard;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSeaPlaneBasePoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSettlement;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSettlementPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSettlingPond;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSmockestackPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSquare;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPStadium;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPStorageDepot;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPTrainingWallArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPVehicleLot;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPAirport;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPRunwayArea;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPTaxiwayArea;
import fr.ign.cogit.cartagen.pearep.mgcp.building.MGCPBerthingArea;
import fr.ign.cogit.cartagen.pearep.mgcp.building.MGCPBerthingLine;
import fr.ign.cogit.cartagen.pearep.mgcp.building.MGCPBuildLine;
import fr.ign.cogit.cartagen.pearep.mgcp.building.MGCPCemetery;
import fr.ign.cogit.cartagen.pearep.mgcp.energy.MGCPElectricityLine;
import fr.ign.cogit.cartagen.pearep.mgcp.energy.MGCPPipeLine;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPCoastLine;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPDitchLine;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPInundationArea;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPLakeArea;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPRiverArea;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPWaterLine;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPWaterTreatmentBed;
import fr.ign.cogit.cartagen.pearep.mgcp.land.MGCPWoodLine;
import fr.ign.cogit.cartagen.pearep.mgcp.relief.MGCPContourLine;
import fr.ign.cogit.cartagen.pearep.mgcp.relief.MGCPReliefElementLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPBridgeLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPPathLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRailwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRoadLine;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public class MGCPLoader extends ShapeFileLoader {

  /**
   * Default constructor
   * @param dataset
   */
  public MGCPLoader(SymbolGroup symbGroup, String dbName) {
    this.createNewDb(symbGroup, dbName);
    this.setProjEpsg("32629");
  }

  /**
   * Default constructor
   */
  public MGCPLoader() {
    this.setProjEpsg("32629");
  }

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {

      // Chargement dans l'ordre alphabétique (JFG)

      if ((listLayer.size() == 0) || (listLayer.contains("LAP030"))) {
        // ground transportation loading
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP030.shp")
                .getAbsolutePath(), MGCPRoadLine.class,
            CartAGenDataSet.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, this
                .getDataset().getRoadNetwork(), PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAP010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP010.shp")
                .getAbsolutePath(), MGCPPathLine.class,
            CartAGenDataSet.PATHS_POP, IPathLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAP050"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP050.shp")
                .getAbsolutePath(), MGCPPathLine.class,
            CartAGenDataSet.PATHS_POP, IPathLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAQ040"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAQ040.shp")
                .getAbsolutePath(), MGCPBridgeLine.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBH070"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBH070.shp")
                .getAbsolutePath(), MGCPBridgeLine.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }

      // hydro loading
      if ((listLayer.size() == 0) || (listLayer.contains("LBH140"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBH140.shp")
                .getAbsolutePath(), MGCPWaterLine.class,
            CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, this
                .getDataset().getHydroNetwork(), PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("ABH080"))) {
        this.loadPolygonClassUnionMulti(
            FileUtil.getNamedFileInDir(directory, "ABH080.shp")
                .getAbsolutePath(), MGCPLakeArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("ABH140"))) {
        this.loadPolygonClassUnionMulti(
            FileUtil.getNamedFileInDir(directory, "ABH140.shp")
                .getAbsolutePath(), MGCPRiverArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBH030"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBH030.shp")
                .getAbsolutePath(), MGCPDitchLine.class,
            PeaRepDataset.DITCH_LINE_POP, IDitchLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBA010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBA010.shp")
                .getAbsolutePath(), MGCPCoastLine.class,
            PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBH020"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBH020.shp")
                .getAbsolutePath(), MGCPWaterLine.class,
            CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, this
                .getDataset().getHydroNetwork(), PeaRepDbType.MGCPPlusPlus);
      }

      // elevation loading
      if ((listLayer.size() == 0) || (listLayer.contains("LCA010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LCA010.shp")
                .getAbsolutePath(), MGCPContourLine.class,
            CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LDB010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LDB010.shp")
                .getAbsolutePath(), MGCPReliefElementLine.class,
            CartAGenDataSet.RELIEF_LINES_POP,
            IReliefElementLine.FEAT_TYPE_NAME, null, PeaRepDbType.MGCPPlusPlus);
      }

      // aero loading
      if ((listLayer.size() == 0) || (listLayer.contains("AGB005")))
        loadAirports(directory);

      // energy
      if ((listLayer.size() == 0) || (listLayer.contains("LAT030"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAT030.shp")
                .getAbsolutePath(), MGCPElectricityLine.class,
            CartAGenDataSet.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, this.getDataset()
                .getElectricityNetwork(), PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAQ113"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAQ113.shp")
                .getAbsolutePath(), MGCPPipeLine.class,
            PeaRepDataset.PIPELINES_POP, IPipeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }

      // population loading
      if ((listLayer.size() == 0) || (listLayer.contains("PAL015"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAL015.shp")
            .getAbsolutePath(), MGCPBuildPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Building Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAL015"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL015.shp")
                .getAbsolutePath(), MGCPBuilding.class,
            CartAGenDataSet.BUILD_AREA_POP, IBuilding.FEAT_TYPE_NAME,

            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBB041"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBB041.shp")
                .getAbsolutePath(), MGCPBuildLine.class,
            PeaRepDataset.BUILD_LINE_POP, IBuildLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBB230"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBB230.shp")
                .getAbsolutePath(), MGCPBuildLine.class,
            PeaRepDataset.BUILD_LINE_POP, IBuildLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LBB190"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LBB190.shp")
                .getAbsolutePath(), MGCPBerthingLine.class,
            PeaRepDataset.BERTHING_LINES, IBerthingLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }

      // PowerStation loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAD010"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAD010.shp")
                .getAbsolutePath(), MGCPPowerStationArea.class,
            CartAGenDataSet.BUILD_AREA_POP, IPowerStationArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("AAD030"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAD030.shp")
                .getAbsolutePath(), MGCPPowerStationArea.class,
            CartAGenDataSet.BUILD_AREA_POP, IPowerStationArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Cemetery Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAL030"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL030.shp")
                .getAbsolutePath(), MGCPCemetery.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Fortification Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAH050"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAH050.shp")
                .getAbsolutePath(), MGCPFortification.class,
            CartAGenDataSet.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Processing Facility Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAC000"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAC000.shp")
                .getAbsolutePath(), MGCPProcessingFacility.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // StorageDepot Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAM010"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAM010.shp")
                .getAbsolutePath(), MGCPStorageDepot.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // MineralPile Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAM040"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAM040.shp")
                .getAbsolutePath(), MGCPMineralPile.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Pumping Station Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAQ116"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAQ116.shp")
                .getAbsolutePath(), MGCPPumpingStation.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Vehicle Lot (Parking) Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAQ140"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAQ140.shp")
                .getAbsolutePath(), MGCPVehicleLot.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Communication Station (?) Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAT050"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAT050.shp")
                .getAbsolutePath(), MGCPCommunicationStation.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // RailwayYard Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAN060"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAN060.shp")
                .getAbsolutePath(), MGCPRailwayYard.class,
            CartAGenDataSet.BOUNDED_AREA_POP, IBoundedArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Square Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAL170"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL170.shp")
                .getAbsolutePath(), MGCPSquare.class, PeaRepDataset.SQUARE_POP,
            ISquareArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus);
      }

      // Stadium Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAK160"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAK160.shp")
                .getAbsolutePath(), MGCPStadium.class,
            CartAGenDataSet.BUILD_PT_POP, ISportsField.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Settlement Loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAL105"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL105.shp")
                .getAbsolutePath(), MGCPSettlement.class,
            CartAGenDataSet.TOWNS_POP, ITown.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Harbour loading
      // Dry Dock
      if ((listLayer.size() == 0) || (listLayer.contains("ABB090"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ABB090.shp")
                .getAbsolutePath(), MGCPDryDockArea.class,
            CartAGenDataSet.BUILD_AREA_POP, IDryDockArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Training Wall Area
      if ((listLayer.size() == 0) || (listLayer.contains("ABB140"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ABB140.shp")
                .getAbsolutePath(), MGCPTrainingWallArea.class,
            CartAGenDataSet.BUILD_AREA_POP, ITrainingWallArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Berthing Area
      if ((listLayer.size() == 0) || (listLayer.contains("ABB190"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ABB190.shp")
                .getAbsolutePath(), MGCPBerthingArea.class,
            CartAGenDataSet.BUILD_AREA_POP, IBerthingArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Water Treatment loading
      // Settling Pond
      if ((listLayer.size() == 0) || (listLayer.contains("AAC030"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAC030.shp")
                .getAbsolutePath(), MGCPSettlingPond.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterBasin.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Water Treatment Bed
      if ((listLayer.size() == 0) || (listLayer.contains("ABH040"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ABH040.shp")
                .getAbsolutePath(), MGCPWaterTreatmentBed.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterBasin.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // Inundation Area loading
      if ((listLayer.size() == 0) || (listLayer.contains("ABH090"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ABH090.shp")
                .getAbsolutePath(), MGCPInundationArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IInundationArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // built-up areas loading
      if ((listLayer.size() == 0) || (listLayer.contains("AAL020"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL020.shp")
                .getAbsolutePath(), MGCPBuiltUpArea.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus);
      }

      // landcover loading
      // quarry areas
      if ((listLayer.size() == 0) || (listLayer.contains("AAA012"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AAA012.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.QUARRY);
      }

      // built-up areas
      if ((listLayer.size() == 0) || (listLayer.contains("AAL020"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AAL020.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.BUILT_UP);
      }

      // lake areas
      if ((listLayer.size() == 0) || (listLayer.contains("ABH080"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "ABH080.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.LAKE);
      }

      // reservoir areas
      if ((listLayer.size() == 0) || (listLayer.contains("ABH130"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "ABH130.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.RESERVOIR);
      }

      // river areas
      if ((listLayer.size() == 0) || (listLayer.contains("ABH140"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "ABH140.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.RIVER);
      }

      // soil surface areas
      if ((listLayer.size() == 0) || (listLayer.contains("ADA010"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "ADA010.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.SOIL_SURFACE);
      }

      // crop areas
      if ((listLayer.size() == 0) || (listLayer.contains("AEA010"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AEA010.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.CROP);
      }

      // grassland areas
      if ((listLayer.size() == 0) || (listLayer.contains("AEB010"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AEB010.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.GRASSLAND);
      }

      // thicket areas
      if ((listLayer.size() == 0) || (listLayer.contains("AEB020"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AEB020.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.THICKET);
      }

      // wooded areas
      if ((listLayer.size() == 0) || (listLayer.contains("AEC030"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AEC030.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.WOODED);
      }

      // marsh areas
      if ((listLayer.size() == 0) || (listLayer.contains("AED010"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AED010.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.MARSH);
      }

      // swamp areas
      if ((listLayer.size() == 0) || (listLayer.contains("AED020"))) {
        this.loadLandUseClass(
            FileUtil.getNamedFileInDir(directory, "AED020.shp")
                .getAbsolutePath(), MGCPLandUse.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus,
            MGCPLandUseType.SWAMP);
      }

      // wood lines
      if ((listLayer.size() == 0) || (listLayer.contains("LEC030"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LEC030.shp")
                .getAbsolutePath(), MGCPWoodLine.class,
            PeaRepDataset.WOODLINES_POP, IWoodLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }

      // railway
      if ((listLayer.size() == 0) || (listLayer.contains("LAN010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAN010.shp")
                .getAbsolutePath(), MGCPRailwayLine.class,
            CartAGenDataSet.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME,
            this.getDataset().getRailwayNetwork(), PeaRepDbType.MGCPPlusPlus);
      }

      // point loading
      // PowerSubstation
      if ((listLayer.size() == 0) || (listLayer.contains("PAD030"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAD030.shp")
            .getAbsolutePath(), MGCPPowerStationPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IPowerStationPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Smockstack
      if ((listLayer.size() == 0) || (listLayer.contains("PAF010"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAF010.shp")
            .getAbsolutePath(), MGCPSmockestackPoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Crane
      if ((listLayer.size() == 0) || (listLayer.contains("PAF040"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAF040.shp")
            .getAbsolutePath(), MGCPCranePoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // FlarePipe
      if ((listLayer.size() == 0) || (listLayer.contains("PAF070"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAF070.shp")
            .getAbsolutePath(), MGCPFlarePipePoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Settlement
      if ((listLayer.size() == 0) || (listLayer.contains("PAL105"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAL105.shp")
            .getAbsolutePath(), MGCPSettlementPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Non-Communication Tower Point
      if ((listLayer.size() == 0) || (listLayer.contains("PAL240"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAL240.shp")
            .getAbsolutePath(), MGCPNonCommunicationTowerPoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Control Tower Point
      if ((listLayer.size() == 0) || (listLayer.contains("PAQ060"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAQ060.shp")
            .getAbsolutePath(), MGCPControlTowerPoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Communication Station Point
      if ((listLayer.size() == 0) || (listLayer.contains("PAT080"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAT080.shp")
            .getAbsolutePath(), MGCPCommunicationStationPoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Lighthouse Point
      if ((listLayer.size() == 0) || (listLayer.contains("PBC050"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PBC050.shp")
            .getAbsolutePath(), MGCPLighthousePoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      // Seaplane Base Point
      if ((listLayer.size() == 0) || (listLayer.contains("PGB065"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PGB065.shp")
            .getAbsolutePath(), MGCPSeaPlaneBasePoint.class,
            CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  public void createNewDb(SymbolGroup symbGroup, String name) {
    // create the new CartAGen dataset
    ShapeFileDB database = new ShapeFileDB(name);
    database.setSourceDLM(SourceDLM.MGCPPlusPlus);
    database.setSymboScale(100000);
    database.setDocument(CartAGenDoc.getInstance());
    CartAGenDataSet dataset = new PeaRepDataset();
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
    database.setGeneObjImpl(new GeneObjImplementation("mgcp++",
        MGCPFeature.class.getPackage(), MGCPFeature.class));
  }

  private void loadAirports(File directory) throws ShapefileException,
      IllegalArgumentException, SecurityException, IOException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, NoSuchMethodException {
    // first load airport areas
    loadPolygonClass(FileUtil.getNamedFileInDir(directory, "AGB005.shp")
        .getAbsolutePath(), MGCPAirport.class,
        CartAGenDataSet.AIRPORT_AREA_POP, IAirportArea.FEAT_TYPE_NAME,
        PeaRepDbType.MGCPPlusPlus);

    // then load runways
    loadPolygonClass(FileUtil.getNamedFileInDir(directory, "AGB055.shp")
        .getAbsolutePath(), MGCPRunwayArea.class,
        CartAGenDataSet.RUNWAY_AREA_POP, IRunwayArea.FEAT_TYPE_NAME,
        PeaRepDbType.MGCPPlusPlus);
    loadPolygonClass(FileUtil.getNamedFileInDir(directory, "AGB045.shp")
        .getAbsolutePath(), MGCPRunwayArea.class,
        CartAGenDataSet.RUNWAY_AREA_POP, IRunwayArea.FEAT_TYPE_NAME,
        PeaRepDbType.MGCPPlusPlus);

    // then load taxiways
    loadPolygonClass(FileUtil.getNamedFileInDir(directory, "AGB075.shp")
        .getAbsolutePath(), MGCPTaxiwayArea.class,
        CartAGenDataSet.RUNWAY_AREA_POP, ITaxiwayArea.FEAT_TYPE_NAME,
        PeaRepDbType.MGCPPlusPlus);
    loadPolygonClass(FileUtil.getNamedFileInDir(directory, "AGB015.shp")
        .getAbsolutePath(), MGCPTaxiwayArea.class,
        CartAGenDataSet.RUNWAY_AREA_POP, ITaxiwayArea.FEAT_TYPE_NAME,
        PeaRepDbType.MGCPPlusPlus);

    // then load helipads
    // TODO

    // finally builds the airport complex object
    IPopulation<IGeneObj> runPop = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartagenPop(CartAGenDataSet.RUNWAY_AREA_POP);
    for (IGeneObj airport : CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(CartAGenDataSet.AIRPORT_AREA_POP)) {
      Collection<IGeneObj> inside = runPop.select(airport.getGeom());
      for (IGeneObj obj : inside) {
        if (obj instanceof ITaxiwayArea) {
          ((IAirportArea) airport).getTaxiwayAreas().add((ITaxiwayArea) obj);
        }
        if (obj instanceof IRunwayArea) {
          ((IAirportArea) airport).getRunwayAreas().add((IRunwayArea) obj);
          ((IRunwayArea) obj).setAirport((IAirportArea) airport);
        }
      }
    }

  }
}
