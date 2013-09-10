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
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayPoint;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPipeLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPowerStationArea;
import fr.ign.cogit.cartagen.core.genericschema.energy.IPowerStationPoint;
import fr.ign.cogit.cartagen.core.genericschema.harbour.ISeawallLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IInundationArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterBasin;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IEmbankmentLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadFacilityPoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPAerofacP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPAgristrP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPAquedctL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBarrierL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBluffL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBridgeC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBridgeL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuildA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuildP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuiltUpA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuiltUpP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCoastA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCoastL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCommP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPContourL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCropA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDamC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDamL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDangerA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDangerL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDangerP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDepareA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDepthL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDisposeA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPElevP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPEmbankmentL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPExtractA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPExtractP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFerryC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFerryL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFordC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFordL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFortA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFortP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPGroundA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPInterC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPInundA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLakeresA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLandmrkA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLandmrkL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLandmrkP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLiftL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLndareA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLndfrm1A;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLndfrm2A;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLndfrmL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLndfrmP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPLockL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPMarkersP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPMisaeroP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPMiscL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPMiscP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPMtnP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPOasisP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPObstrP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPOrchardA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPierL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPipeL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPolbndA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPolbndL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPowerA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPowerL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPowerP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPProcessA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPProcessP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPPumpingP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRailrdL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRapidsC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRestP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRigwellP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRoadL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRuinsP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRunwayL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPRunwayP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPSeastrtL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPStorageP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPSwampA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTeleL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPThermalP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTowerP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTrackL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTrailL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTreatA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTreesA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTreesL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTunnelC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPTunnelL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPWatrcrsA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPWatrcrsL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPWellsprP;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class VMAP1PlusPlusLoader extends ShapeFileLoader {

  private IPolygon envelopeTotale;
  private List<IPolygon> cellsGrid;

  /**
   * Default constructor
   * @param dataset
   */
  public VMAP1PlusPlusLoader(SymbolGroup symbGroup, String dbName) {
    this.createNewDb(symbGroup, dbName);
  }

  /**
   * Default constructor
   */
  public VMAP1PlusPlusLoader() {
  }

  /**
   * Method to compute the total envelope of VMAP1++ data.
   * @param directory
   * @param listLayer
   * @throws ShapefileException
   * @throws IOException
   */
  public void computeEnvelopeTotale(File directory, List<String> listLayer)
      throws ShapefileException, IOException {

    IFeatureCollection<IFeature> ftColPolyEnvelope = new FT_FeatureCollection<IFeature>();

    // Airport Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("AerofacP")))
        && (FileUtil.getNamedFileInDir(directory, "AerofacP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "AerofacP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Grain Bin / Silo Point loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("AgristrP")))
        && (FileUtil.getNamedFileInDir(directory, "AgristrP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "AgristrP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Aquedec Line loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("AquedctL")))
        && (FileUtil.getNamedFileInDir(directory, "AquedctL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "AquedctL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Bluff / Cliff Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("BluffL")))
        && (FileUtil.getNamedFileInDir(directory, "BluffL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BluffL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Bridge Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("BridgeC")))
        && (FileUtil.getNamedFileInDir(directory, "BridgeC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BridgeC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Bridge Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("BridgeL")))
        && (FileUtil.getNamedFileInDir(directory, "BridgeL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BridgeL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Buid Area loading
    if (((listLayer.size() == 0) || (listLayer.contains("BuildA")))
        && (FileUtil.getNamedFileInDir(directory, "BuildA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BuildA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Build Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("BuildP")))
        && (FileUtil.getNamedFileInDir(directory, "BuildP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BuildP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Built-up Area loading
    if (((listLayer.size() == 0) || (listLayer.contains("BuiltupA")))
        && (FileUtil.getNamedFileInDir(directory, "BuiltupA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BuiltupA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Built-up Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("BuiltupP")))
        && (FileUtil.getNamedFileInDir(directory, "BuiltupP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BuiltupP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Island Area loading
    if (((listLayer.size() == 0) || (listLayer.contains("CoastA")))
        && (FileUtil.getNamedFileInDir(directory, "CoastA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "CoastA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Coast Line loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("CoastL")))
        && (FileUtil.getNamedFileInDir(directory, "CoastL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "CoastL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Radar (Communication) Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("CommP")))
        && (FileUtil.getNamedFileInDir(directory, "CommP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "CommP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Contour Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("ContourL")))
        && (FileUtil.getNamedFileInDir(directory, "ContourL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ContourL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Crop Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("CropA")))
        && (FileUtil.getNamedFileInDir(directory, "CropA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "CropA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Dam Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("DamC")))
        && (FileUtil.getNamedFileInDir(directory, "DamC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DamC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Dam Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("DamL")))
        && (FileUtil.getNamedFileInDir(directory, "DamL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DamL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Danger (Reef, Island, Rock) Area loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("DangerA")))
        && (FileUtil.getNamedFileInDir(directory, "DangerA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DangerA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Danger (Reef, Island, Rock) Line loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("DangerL")))
        && (FileUtil.getNamedFileInDir(directory, "DangerL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DangerL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Danger (Reef, Island, Rock) Point loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("DangerP")))
        && (FileUtil.getNamedFileInDir(directory, "DangerP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DangerP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Dispose Area loading (!!! vide)
    if (((listLayer.size() == 0) || (listLayer.contains("DisposeA")))
        && (FileUtil.getNamedFileInDir(directory, "DisposeA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DisposeA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Elevation Point loading
    if (((listLayer.size() == 0) || (listLayer.contains("ElevP")))
        && (FileUtil.getNamedFileInDir(directory, "ElevP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ElevP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Embankment (Cut, fill...) Line loading
    if (((listLayer.size() == 0) || (listLayer.contains("EmbankL")))
        && (FileUtil.getNamedFileInDir(directory, "EmbankL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "EmbankL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Mine / Quarry Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ExtractA")))
        && (FileUtil.getNamedFileInDir(directory, "ExtractA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ExtractA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Mine / Quarry Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ExtractP")))
        && (FileUtil.getNamedFileInDir(directory, "ExtractP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ExtractP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ferry Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FerryC")))
        && (FileUtil.getNamedFileInDir(directory, "FerryC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FerryC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ferry Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FerryL")))
        && (FileUtil.getNamedFileInDir(directory, "FerryL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FerryL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ford Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FordC")))
        && (FileUtil.getNamedFileInDir(directory, "FordC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FordC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ford Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FordL")))
        && (FileUtil.getNamedFileInDir(directory, "FordL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FordL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Fort Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FordA")))
        && (FileUtil.getNamedFileInDir(directory, "FordA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FordA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Fort Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("FortP")))
        && (FileUtil.getNamedFileInDir(directory, "FortP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "FortP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ground Surface Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("GroundA")))
        && (FileUtil.getNamedFileInDir(directory, "GroundA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "GroundA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Interchange Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("InterC")))
        && (FileUtil.getNamedFileInDir(directory, "InterC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "InterC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Inundation Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("InundA")))
        && (FileUtil.getNamedFileInDir(directory, "InundA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "InundA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Lake/Reservoir Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LakeresA")))
        && (FileUtil.getNamedFileInDir(directory, "LakeresA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LakeresA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Landmark Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LandmrkA")))
        && (FileUtil.getNamedFileInDir(directory, "LandmrkA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LandmrkA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Landmark Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LandmrkL")))
        && (FileUtil.getNamedFileInDir(directory, "LandmrkL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LandmrkL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Landmark Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LandmrkP")))
        && (FileUtil.getNamedFileInDir(directory, "LandmrkP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LandmrkP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Aerial Cable Way Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LiftL")))
        && (FileUtil.getNamedFileInDir(directory, "LiftL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LiftL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Sebkha
    if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm1A")))
        && (FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "Lndfrm1A.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Moraine
    if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm2A")))
        && (FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "Lndfrm2A.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // LandForm (Ice Cliff) Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LndfrmL")))
        && (FileUtil.getNamedFileInDir(directory, "LndfrmL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LndfrmL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // LandForm (Ice Peak / Nunatak / Rock Strata / Rock Formation) Point
    // Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LndfrmP")))
        && (FileUtil.getNamedFileInDir(directory, "LndfrmP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LndfrmP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Lock Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LockL")))
        && (FileUtil.getNamedFileInDir(directory, "LockL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LockL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Cairn Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("MarkersP")))
        && (FileUtil.getNamedFileInDir(directory, "MarkersP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "MarkersP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Control Tower Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("MisaeroP")))
        && (FileUtil.getNamedFileInDir(directory, "MisaeroP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "MisaeroP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Miscellaneous Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("MiscL")))
        && (FileUtil.getNamedFileInDir(directory, "MiscL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "MiscL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Miscellaneous Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("MiscP")))
        && (FileUtil.getNamedFileInDir(directory, "MiscP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "MiscP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Oasis Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("OasisP")))
        && (FileUtil.getNamedFileInDir(directory, "OasisP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "OasisP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Oasis Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ObstrP")))
        && (FileUtil.getNamedFileInDir(directory, "ObstrP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ObstrP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Orchard / Plantation Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("OrchardA")))
        && (FileUtil.getNamedFileInDir(directory, "OrchardA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "OrchardA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Pier / Quay Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PierL")))
        && (FileUtil.getNamedFileInDir(directory, "PierL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PierL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Pipe Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PipeL")))
        && (FileUtil.getNamedFileInDir(directory, "PipeL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PipeL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Administrative Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PolbndA")))
        && (FileUtil.getNamedFileInDir(directory, "PolbndA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PolbndA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Administrative Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PolbndL")))
        && (FileUtil.getNamedFileInDir(directory, "PolbndL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PolbndL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Power Plant Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PowerA")))
        && (FileUtil.getNamedFileInDir(directory, "PowerA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PowerA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Power Transmission Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PowerL")))
        && (FileUtil.getNamedFileInDir(directory, "PowerL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PowerL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Power Plant Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PowerP")))
        && (FileUtil.getNamedFileInDir(directory, "PowerP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PowerP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Processing/Treatment Plant Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ProcessA")))
        && (FileUtil.getNamedFileInDir(directory, "ProcessA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ProcessA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Processing/Treatment Plant Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ProcessP")))
        && (FileUtil.getNamedFileInDir(directory, "ProcessP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ProcessP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Pumping Station Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("PumpingP")))
        && (FileUtil.getNamedFileInDir(directory, "PumpingP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "PumpingP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Rail Way Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RailrdL")))
        && (FileUtil.getNamedFileInDir(directory, "RailrdL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RailrdL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Rapids (Hydro Equipement) Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RapidsC")))
        && (FileUtil.getNamedFileInDir(directory, "RapidsC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RapidsC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Rest area Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RestP")))
        && (FileUtil.getNamedFileInDir(directory, "RestP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RestP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Rig / Well Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RigwellP")))
        && (FileUtil.getNamedFileInDir(directory, "RigwellP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RigwellP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Road Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RoadL")))
        && (FileUtil.getNamedFileInDir(directory, "RoadL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RoadL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Ruins Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RuinsP")))
        && (FileUtil.getNamedFileInDir(directory, "RuinsP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RuinsP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Runway Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RunwayL")))
        && (FileUtil.getNamedFileInDir(directory, "RunwayL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RunwayL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Runway Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("RunwayP")))
        && (FileUtil.getNamedFileInDir(directory, "RunwayP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "RunwayP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Breakwater / Groyne Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("SeastrtL")))
        && (FileUtil.getNamedFileInDir(directory, "SeastrtL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "SeastrtL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Storage / Tank Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("StorageP")))
        && (FileUtil.getNamedFileInDir(directory, "StorageP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "StorageP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Swamp Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("SwampA")))
        && (FileUtil.getNamedFileInDir(directory, "SwampA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "SwampA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Breakwater / Groyne Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TeleL")))
        && (FileUtil.getNamedFileInDir(directory, "TeleL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TeleL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Geothermal Ferature / Volcano Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("ThermalP")))
        && (FileUtil.getNamedFileInDir(directory, "ThermalP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "ThermalP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Tower Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TowerP")))
        && (FileUtil.getNamedFileInDir(directory, "TowerP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TowerP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Track Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TrackL")))
        && (FileUtil.getNamedFileInDir(directory, "TrackL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TrackL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Trail Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TrailL")))
        && (FileUtil.getNamedFileInDir(directory, "TrailL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TrailL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Settling Basin / Sludge Pond Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TreatA")))
        && (FileUtil.getNamedFileInDir(directory, "TreatA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TreatA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Trees Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TreesA")))
        && (FileUtil.getNamedFileInDir(directory, "TreesA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TreesA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Trees Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TreesL")))
        && (FileUtil.getNamedFileInDir(directory, "TreesL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TreesL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Tunnel Entrance / Exit Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TunnelC")))
        && (FileUtil.getNamedFileInDir(directory, "TunnelC.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TunnelC.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Tunnel Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("TunnelL")))
        && (FileUtil.getNamedFileInDir(directory, "TunnelL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "TunnelL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Water Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsA")))
        && (FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "WatrcrsA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Water Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsL")))
        && (FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "WatrcrsL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // Well Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("WellP")))
        && (FileUtil.getNamedFileInDir(directory, "WellP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "WellP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    // /PAS DANS LE MODELE VMAP1
    // ???? Point Loading
    if (((listLayer.size() == 0) || (listLayer.contains("MtnP")))
        && (FileUtil.getNamedFileInDir(directory, "MtnP.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "MtnP.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }
    // ???? Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("BarrierL")))
        && (FileUtil.getNamedFileInDir(directory, "BarrierL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "BarrierL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }
    // ???? Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("DepareA")))
        && (FileUtil.getNamedFileInDir(directory, "DepareA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DepareA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }
    // ???? Depth Line Loading
    if (((listLayer.size() == 0) || (listLayer.contains("DepthL")))
        && (FileUtil.getNamedFileInDir(directory, "DepthL.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "DepthL.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }
    // ???? Area Loading
    if (((listLayer.size() == 0) || (listLayer.contains("LndareA")))
        && (FileUtil.getNamedFileInDir(directory, "LndareA.shp") != null)) {
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          "LndareA.shp"));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    IPolygon envelopeTotaleVMAP = new GM_Polygon(
        ftColPolyEnvelope.getEnvelope());
    this.setEnvelopeTotale(envelopeTotaleVMAP);
  }

  /**
   * * Method to load each VMAP1++ data.
   */
  @Override
  public void loadData(File directory, List<String> listLayer) throws Exception {
    try {

      // Chargement dans l'ordre alphabétique (JFG)

      // Airport Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("AerofacP")))
          && (FileUtil.getNamedFileInDir(directory, "AerofacP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "AerofacP.shp")
                .getAbsolutePath(), VMAP1PPAerofacP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Grain Bin / Silo Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AgristrP")))
          && (FileUtil.getNamedFileInDir(directory, "AgristrP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "AgristrP.shp")
                .getAbsolutePath(), VMAP1PPAgristrP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Aquedec Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AquedctL")))
          && (FileUtil.getNamedFileInDir(directory, "AquedctL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "AquedctL.shp")
                .getAbsolutePath(), VMAP1PPAquedctL.class,
            CartAGenDataSet.POI_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Bluff / Cliff Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BluffL")))
          && (FileUtil.getNamedFileInDir(directory, "BluffL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BluffL.shp")
                .getAbsolutePath(), VMAP1PPBluffL.class,
            CartAGenDataSet.RELIEF_LINES_POP,
            IReliefElementLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeC")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeC.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "BridgeC.shp").getAbsolutePath(),
            VMAP1PPBridgeC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeL")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BridgeL.shp")
                .getAbsolutePath(), VMAP1PPBridgeL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Buid Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildA")))
          && (FileUtil.getNamedFileInDir(directory, "BuildA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuildA.shp")
                .getAbsolutePath(), VMAP1PPBuildA.class,
            PeaRepDataset.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Build Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildP")))
          && (FileUtil.getNamedFileInDir(directory, "BuildP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "BuildP.shp")
            .getAbsolutePath(), VMAP1PPBuildP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupA")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupA.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupP")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupP.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Island Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("CoastA")))
          && (FileUtil.getNamedFileInDir(directory, "CoastA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "CoastA.shp")
                .getAbsolutePath(), VMAP1PPCoastA.class,
            PeaRepDataset.WATER_ISLAND_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Coast Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("CoastL")))
          && (FileUtil.getNamedFileInDir(directory, "CoastL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "CoastL.shp")
                .getAbsolutePath(), VMAP1PPCoastL.class,
            PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Radar (Communication) Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("CommP")))
          && (FileUtil.getNamedFileInDir(directory, "CommP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "CommP.shp")
            .getAbsolutePath(), VMAP1PPCommP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Contour Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("ContourL")))
          && (FileUtil.getNamedFileInDir(directory, "ContourL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "ContourL.shp")
                .getAbsolutePath(), VMAP1PPContourL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Crop Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("CropA")))
          && (FileUtil.getNamedFileInDir(directory, "CropA.shp") != null)) {
        this.loadPolygonClass(FileUtil
            .getNamedFileInDir(directory, "CropA.shp").getAbsolutePath(),
            VMAP1PPCropA.class, PeaRepDataset.MISC_AREA_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamC")))
          && (FileUtil.getNamedFileInDir(directory, "DamC.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "DamC.shp")
            .getAbsolutePath(), VMAP1PPDamC.class,
            PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamL")))
          && (FileUtil.getNamedFileInDir(directory, "DamL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DamL.shp").getAbsolutePath(),
            VMAP1PPDamL.class, PeaRepDataset.BRIDGE_LINE_POP,
            IBridgeLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerA")))
          && (FileUtil.getNamedFileInDir(directory, "DangerA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DangerA.shp")
                .getAbsolutePath(), VMAP1PPDangerA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerL")))
          && (FileUtil.getNamedFileInDir(directory, "DangerL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DangerL.shp")
                .getAbsolutePath(), VMAP1PPDangerL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerP")))
          && (FileUtil.getNamedFileInDir(directory, "DangerP.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "DangerP.shp").getAbsolutePath(),
            VMAP1PPDangerP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Dispose Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DisposeA")))
          && (FileUtil.getNamedFileInDir(directory, "DisposeA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DisposeA.shp")
                .getAbsolutePath(), VMAP1PPDisposeA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Elevation Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("ElevP")))
          && (FileUtil.getNamedFileInDir(directory, "ElevP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "ElevP.shp")
            .getAbsolutePath(), VMAP1PPElevP.class,
            PeaRepDataset.SPOT_HEIGHTS_POP, ISpotHeight.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Embankment (Cut, fill...) Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("EmbankL")))
          && (FileUtil.getNamedFileInDir(directory, "EmbankL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "EmbankL.shp")
                .getAbsolutePath(), VMAP1PPEmbankmentL.class,
            PeaRepDataset.MISC_LINE_POP, IEmbankmentLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Mine / Quarry Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractA")))
          && (FileUtil.getNamedFileInDir(directory, "LAP030.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ExtractA.shp")
                .getAbsolutePath(), VMAP1PPExtractA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Mine / Quarry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractP")))
          && (FileUtil.getNamedFileInDir(directory, "ExtractP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ExtractP.shp")
                .getAbsolutePath(), VMAP1PPExtractP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ferry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryC")))
          && (FileUtil.getNamedFileInDir(directory, "FerryC.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FerryC.shp")
            .getAbsolutePath(), VMAP1PPFerryC.class, PeaRepDataset.MISC_PT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ferry Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryL")))
          && (FileUtil.getNamedFileInDir(directory, "FerryL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "FerryL.shp")
                .getAbsolutePath(), VMAP1PPFerryL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ford Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordC")))
          && (FileUtil.getNamedFileInDir(directory, "FordC.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FordC.shp")
            .getAbsolutePath(), VMAP1PPFordC.class, PeaRepDataset.MISC_PT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ford Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordL")))
          && (FileUtil.getNamedFileInDir(directory, "FordL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "FordL.shp")
                .getAbsolutePath(), VMAP1PPFordL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Fort Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordA")))
          && (FileUtil.getNamedFileInDir(directory, "FordA.shp") != null)) {
        this.loadPolygonClass(FileUtil
            .getNamedFileInDir(directory, "FortA.shp").getAbsolutePath(),
            VMAP1PPFortA.class, PeaRepDataset.BUILD_AREA_POP,
            IBuildArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Fort Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FortP")))
          && (FileUtil.getNamedFileInDir(directory, "FortP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FortP.shp")
            .getAbsolutePath(), VMAP1PPFortP.class, PeaRepDataset.BUILD_PT_POP,
            IBuildArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ground Surface Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("GroundA")))
          && (FileUtil.getNamedFileInDir(directory, "GroundA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "GroundA.shp")
                .getAbsolutePath(), VMAP1PPGroundA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Interchange Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InterC")))
          && (FileUtil.getNamedFileInDir(directory, "InterC.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "InterC.shp")
            .getAbsolutePath(), VMAP1PPInterC.class,
            PeaRepDataset.ROAD_NODES_POP, IRoadNode.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Inundation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InundA")))
          && (FileUtil.getNamedFileInDir(directory, "InundA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "InundA.shp")
                .getAbsolutePath(), VMAP1PPInundA.class,
            PeaRepDataset.MISC_AREA_POP, IInundationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Lake/Reservoir Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LakeresA")))
          && (FileUtil.getNamedFileInDir(directory, "LakeresA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LakeresA.shp")
                .getAbsolutePath(), VMAP1PPLakeresA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkA")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkA.shp")
                .getAbsolutePath(), VMAP1PPLandmrkA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkL")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkL.shp")
                .getAbsolutePath(), VMAP1PPLandmrkL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkP")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkP.shp")
                .getAbsolutePath(), VMAP1PPLandmrkP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Aerial Cable Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LiftL")))
          && (FileUtil.getNamedFileInDir(directory, "LiftL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LiftL.shp")
                .getAbsolutePath(), VMAP1PPLiftL.class,
            PeaRepDataset.CABLE_POP, ICable.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Sebkha
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm1A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm1A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Moraine
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm2A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm2A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // LandForm (Ice Cliff) Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmL")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LndfrmL.shp")
                .getAbsolutePath(), VMAP1PPLndfrmL.class,
            PeaRepDataset.RELIEF_LINES_POP, IReliefElementLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.VMAP1PlusPlus);
      }

      // LandForm (Ice Peak / Nunatak / Rock Strata / Rock Formation) Point
      // Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmP")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmP.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "LndfrmP.shp").getAbsolutePath(),
            VMAP1PPLndfrmP.class, PeaRepDataset.RELIEF_PTS_POP,
            IReliefElementPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Lock Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LockL")))
          && (FileUtil.getNamedFileInDir(directory, "LockL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LockL.shp")
                .getAbsolutePath(), VMAP1PPLockL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Cairn Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MarkersP")))
          && (FileUtil.getNamedFileInDir(directory, "MarkersP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "MarkersP.shp")
                .getAbsolutePath(), VMAP1PPMarkersP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Control Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MisaeroP")))
          && (FileUtil.getNamedFileInDir(directory, "MisaeroP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "MisaeroP.shp")
                .getAbsolutePath(), VMAP1PPMisaeroP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Miscellaneous Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscL")))
          && (FileUtil.getNamedFileInDir(directory, "MiscL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "MiscL.shp")
                .getAbsolutePath(), VMAP1PPMiscL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Miscellaneous Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscP")))
          && (FileUtil.getNamedFileInDir(directory, "MiscP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "MiscP.shp")
            .getAbsolutePath(), VMAP1PPMiscP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OasisP")))
          && (FileUtil.getNamedFileInDir(directory, "OasisP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "OasisP.shp")
            .getAbsolutePath(), VMAP1PPOasisP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ObstrP")))
          && (FileUtil.getNamedFileInDir(directory, "ObstrP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "ObstrP.shp")
            .getAbsolutePath(), VMAP1PPObstrP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Orchard / Plantation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OrchardA")))
          && (FileUtil.getNamedFileInDir(directory, "OrchardA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "OrchardA.shp")
                .getAbsolutePath(), VMAP1PPOrchardA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pier / Quay Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PierL")))
          && (FileUtil.getNamedFileInDir(directory, "PierL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PierL.shp")
                .getAbsolutePath(), VMAP1PPPierL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pipe Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PipeL")))
          && (FileUtil.getNamedFileInDir(directory, "PipeL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PipeL.shp")
                .getAbsolutePath(), VMAP1PPPipeL.class,
            PeaRepDataset.PIPELINES_POP, IPipeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Administrative Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndA")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "PolbndA.shp")
                .getAbsolutePath(), VMAP1PPPolbndA.class,
            PeaRepDataset.ADMIN_UNIT_POP, ISimpleAdminUnit.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Administrative Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndL")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PolbndL.shp")
                .getAbsolutePath(), VMAP1PPPolbndL.class,
            PeaRepDataset.ADMIN_LIMIT_POP, IAdminLimit.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerA")))
          && (FileUtil.getNamedFileInDir(directory, "PowerA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "PowerA.shp")
                .getAbsolutePath(), VMAP1PPPowerA.class,
            PeaRepDataset.MISC_AREA_POP, IPowerStationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Transmission Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerL")))
          && (FileUtil.getNamedFileInDir(directory, "PowerL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PowerL.shp")
                .getAbsolutePath(), VMAP1PPPowerL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerP")))
          && (FileUtil.getNamedFileInDir(directory, "PowerP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PowerP.shp")
            .getAbsolutePath(), VMAP1PPPowerP.class, PeaRepDataset.MISC_PT_POP,
            IPowerStationPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Processing/Treatment Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessA")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ProcessA.shp")
                .getAbsolutePath(), VMAP1PPProcessA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Processing/Treatment Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessP")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ProcessP.shp")
                .getAbsolutePath(), VMAP1PPProcessP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pumping Station Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PumpingP")))
          && (FileUtil.getNamedFileInDir(directory, "PumpingP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "PumpingP.shp")
                .getAbsolutePath(), VMAP1PPPumpingP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Rail Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RailrdL")))
          && (FileUtil.getNamedFileInDir(directory, "RailrdL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RailrdL.shp")
                .getAbsolutePath(), VMAP1PPRailrdL.class,
            PeaRepDataset.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Rapids (Hydro Equipement) Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RapidsC")))
          && (FileUtil.getNamedFileInDir(directory, "RapidsC.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "RapidsC.shp").getAbsolutePath(),
            VMAP1PPRapidsC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Rest area Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RestP")))
          && (FileUtil.getNamedFileInDir(directory, "RestP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "RestP.shp")
            .getAbsolutePath(), VMAP1PPRestP.class,
            PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Rig / Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RigwellP")))
          && (FileUtil.getNamedFileInDir(directory, "RigwellP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "RigwellP.shp")
                .getAbsolutePath(), VMAP1PPRigwellP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Road Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RoadL")))
          && (FileUtil.getNamedFileInDir(directory, "RoadL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RoadL.shp")
                .getAbsolutePath(), VMAP1PPRoadL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ruins Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RuinsP")))
          && (FileUtil.getNamedFileInDir(directory, "RuinsP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "RuinsP.shp")
            .getAbsolutePath(), VMAP1PPRuinsP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Runway Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayL")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RunwayL.shp")
                .getAbsolutePath(), VMAP1PPRunwayL.class,
            PeaRepDataset.RUNWAY_LINE_POP, IRunwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Runway Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayP")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayP.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "RunwayP.shp").getAbsolutePath(),
            VMAP1PPRunwayP.class, PeaRepDataset.MISC_PT_POP,
            IRunwayPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SeastrtL")))
          && (FileUtil.getNamedFileInDir(directory, "SeastrtL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "SeastrtL.shp")
                .getAbsolutePath(), VMAP1PPSeastrtL.class,
            PeaRepDataset.MISC_LINE_POP, ISeawallLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Storage / Tank Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("StorageP")))
          && (FileUtil.getNamedFileInDir(directory, "StorageP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "StorageP.shp")
                .getAbsolutePath(), VMAP1PPStorageP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Swamp Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SwampA")))
          && (FileUtil.getNamedFileInDir(directory, "SwampA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "SwampA.shp")
                .getAbsolutePath(), VMAP1PPSwampA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TeleL")))
          && (FileUtil.getNamedFileInDir(directory, "TeleL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TeleL.shp")
                .getAbsolutePath(), VMAP1PPTeleL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Geothermal Ferature / Volcano Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ThermalP")))
          && (FileUtil.getNamedFileInDir(directory, "ThermalP.shp") != null)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ThermalP.shp")
                .getAbsolutePath(), VMAP1PPThermalP.class,
            PeaRepDataset.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TowerP")))
          && (FileUtil.getNamedFileInDir(directory, "TowerP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "TowerP.shp")
            .getAbsolutePath(), VMAP1PPTowerP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Track Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrackL")))
          && (FileUtil.getNamedFileInDir(directory, "TrackL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TrackL.shp")
                .getAbsolutePath(), VMAP1PPTrackL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trail Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrailL")))
          && (FileUtil.getNamedFileInDir(directory, "TrailL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TrailL.shp")
                .getAbsolutePath(), VMAP1PPTrailL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Settling Basin / Sludge Pond Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreatA")))
          && (FileUtil.getNamedFileInDir(directory, "TreatA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "TreatA.shp")
                .getAbsolutePath(), VMAP1PPTreatA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterBasin.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trees Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesA")))
          && (FileUtil.getNamedFileInDir(directory, "TreesA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "TreesA.shp")
                .getAbsolutePath(), VMAP1PPTreesA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trees Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesL")))
          && (FileUtil.getNamedFileInDir(directory, "TreesL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TreesL.shp")
                .getAbsolutePath(), VMAP1PPTreesL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Tunnel Entrance / Exit Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelC")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelC.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "TunnelC.shp").getAbsolutePath(),
            VMAP1PPTunnelC.class, PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Tunnel Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelL")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelL.shp") != null)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "TunnelL.shp").getAbsolutePath(),
            VMAP1PPTunnelL.class, PeaRepDataset.ROADS_POP,
            IRoadLine.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Water Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsA")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Water Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsL")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsL.class,
            PeaRepDataset.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WellP")))
          && (FileUtil.getNamedFileInDir(directory, "WellP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "WellP.shp")
            .getAbsolutePath(), VMAP1PPWellsprP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // /PAS DANS LE MODELE VMAP1
      // ???? Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MtnP")))
          && (FileUtil.getNamedFileInDir(directory, "MtnP.shp") != null)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "MtnP.shp")
            .getAbsolutePath(), VMAP1PPMtnP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("BarrierL")))
          && (FileUtil.getNamedFileInDir(directory, "BarrierL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BarrierL.shp")
                .getAbsolutePath(), VMAP1PPBarrierL.class,
            PeaRepDataset.MISC_PT_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepareA")))
          && (FileUtil.getNamedFileInDir(directory, "DepareA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DepareA.shp")
                .getAbsolutePath(), VMAP1PPDepareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Depth Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepthL")))
          && (FileUtil.getNamedFileInDir(directory, "DepthL.shp") != null)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DepthL.shp")
                .getAbsolutePath(), VMAP1PPDepthL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndareA")))
          && (FileUtil.getNamedFileInDir(directory, "LndareA.shp") != null)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LndareA.shp")
                .getAbsolutePath(), VMAP1PPLndareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
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

  public void loadData(File directory, List<String> listLayer, Class<?> classObj)
      throws Exception {
    try {

      String ft = (String) classObj.getField("FEAT_TYPE_NAME").get(null);

      // Airport Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("AerofacP")))
          && (FileUtil.getNamedFileInDir(directory, "AerofacP.shp") != null)
          && ft.equals(IPointOfInterest.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "AerofacP.shp")
                .getAbsolutePath(), VMAP1PPAerofacP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Grain Bin / Silo Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AgristrP")))
          && (FileUtil.getNamedFileInDir(directory, "AgristrP.shp") != null)
          && ft.equals(IPointOfInterest.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "AgristrP.shp")
                .getAbsolutePath(), VMAP1PPAgristrP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Aquedec Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AquedctL")))
          && (FileUtil.getNamedFileInDir(directory, "AquedctL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "AquedctL.shp")
                .getAbsolutePath(), VMAP1PPAquedctL.class,
            CartAGenDataSet.POI_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Bluff / Cliff Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BluffL")))
          && (FileUtil.getNamedFileInDir(directory, "BluffL.shp") != null)
          && ft.equals(IReliefElementLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BluffL.shp")
                .getAbsolutePath(), VMAP1PPBluffL.class,
            CartAGenDataSet.RELIEF_LINES_POP,
            IReliefElementLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeC")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeC.shp") != null)
          && ft.equals(IBridgePoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "BridgeC.shp").getAbsolutePath(),
            VMAP1PPBridgeC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeL")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BridgeL.shp")
                .getAbsolutePath(), VMAP1PPBridgeL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Buid Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildA")))
          && (FileUtil.getNamedFileInDir(directory, "BuildA.shp") != null)
          && ft.equals(IBuildArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuildA.shp")
                .getAbsolutePath(), VMAP1PPBuildA.class,
            PeaRepDataset.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Build Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildP")))
          && (FileUtil.getNamedFileInDir(directory, "BuildP.shp") != null)
          && ft.equals(IBuildPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "BuildP.shp")
            .getAbsolutePath(), VMAP1PPBuildP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupA")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupA.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupP")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupP.shp") != null)
          && ft.equals(IBuildPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupP.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Island Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("CoastA")))
          && (FileUtil.getNamedFileInDir(directory, "CoastA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "CoastA.shp")
                .getAbsolutePath(), VMAP1PPCoastA.class,
            PeaRepDataset.WATER_ISLAND_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Coast Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("CoastL")))
          && (FileUtil.getNamedFileInDir(directory, "CoastL.shp") != null)
          && ft.equals(ICoastLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "CoastL.shp")
                .getAbsolutePath(), VMAP1PPCoastL.class,
            PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Radar (Communication) Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("CommP")))
          && (FileUtil.getNamedFileInDir(directory, "CommP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "CommP.shp")
            .getAbsolutePath(), VMAP1PPCommP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Contour Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("ContourL")))
          && (FileUtil.getNamedFileInDir(directory, "ContourL.shp") != null)
          && ft.equals(IContourLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "ContourL.shp")
                .getAbsolutePath(), VMAP1PPContourL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Crop Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("CropA")))
          && (FileUtil.getNamedFileInDir(directory, "CropA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(FileUtil
            .getNamedFileInDir(directory, "CropA.shp").getAbsolutePath(),
            VMAP1PPCropA.class, PeaRepDataset.MISC_AREA_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamC")))
          && (FileUtil.getNamedFileInDir(directory, "DamC.shp") != null)
          && ft.equals(IBridgePoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "DamC.shp")
            .getAbsolutePath(), VMAP1PPDamC.class,
            PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamL")))
          && (FileUtil.getNamedFileInDir(directory, "DamL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DamL.shp").getAbsolutePath(),
            VMAP1PPDamL.class, PeaRepDataset.BRIDGE_LINE_POP,
            IBridgeLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerA")))
          && (FileUtil.getNamedFileInDir(directory, "DangerA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DangerA.shp")
                .getAbsolutePath(), VMAP1PPDangerA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerL")))
          && (FileUtil.getNamedFileInDir(directory, "DangerL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DangerL.shp")
                .getAbsolutePath(), VMAP1PPDangerL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerP")))
          && (FileUtil.getNamedFileInDir(directory, "DangerP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "DangerP.shp").getAbsolutePath(),
            VMAP1PPDangerP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Dispose Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DisposeA")))
          && (FileUtil.getNamedFileInDir(directory, "DisposeA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DisposeA.shp")
                .getAbsolutePath(), VMAP1PPDisposeA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Elevation Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("ElevP")))
          && (FileUtil.getNamedFileInDir(directory, "ElevP.shp") != null)
          && ft.equals(ISpotHeight.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "ElevP.shp")
            .getAbsolutePath(), VMAP1PPElevP.class,
            PeaRepDataset.SPOT_HEIGHTS_POP, ISpotHeight.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Embankment (Cut, fill...) Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("EmbankL")))
          && (FileUtil.getNamedFileInDir(directory, "EmbankL.shp") != null)
          && ft.equals(IEmbankmentLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "EmbankL.shp")
                .getAbsolutePath(), VMAP1PPEmbankmentL.class,
            PeaRepDataset.MISC_LINE_POP, IEmbankmentLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Mine / Quarry Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractA")))
          && (FileUtil.getNamedFileInDir(directory, "LAP030.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ExtractA.shp")
                .getAbsolutePath(), VMAP1PPExtractA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Mine / Quarry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractP")))
          && (FileUtil.getNamedFileInDir(directory, "ExtractP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ExtractP.shp")
                .getAbsolutePath(), VMAP1PPExtractP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ferry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryC")))
          && (FileUtil.getNamedFileInDir(directory, "FerryC.shp") != null)
          && ft.equals(IBridgePoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FerryC.shp")
            .getAbsolutePath(), VMAP1PPFerryC.class, PeaRepDataset.MISC_PT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ferry Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryL")))
          && (FileUtil.getNamedFileInDir(directory, "FerryL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "FerryL.shp")
                .getAbsolutePath(), VMAP1PPFerryL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ford Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordC")))
          && (FileUtil.getNamedFileInDir(directory, "FordC.shp") != null)
          && ft.equals(IBridgePoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FordC.shp")
            .getAbsolutePath(), VMAP1PPFordC.class, PeaRepDataset.MISC_PT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ford Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordL")))
          && (FileUtil.getNamedFileInDir(directory, "FordL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "FordL.shp")
                .getAbsolutePath(), VMAP1PPFordL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Fort Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordA")))
          && (FileUtil.getNamedFileInDir(directory, "FordA.shp") != null)
          && ft.equals(IBuildArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(FileUtil
            .getNamedFileInDir(directory, "FortA.shp").getAbsolutePath(),
            VMAP1PPFortA.class, PeaRepDataset.BUILD_AREA_POP,
            IBuildArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Fort Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FortP")))
          && (FileUtil.getNamedFileInDir(directory, "FortP.shp") != null)
          && ft.equals(IBuildArea.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "FortP.shp")
            .getAbsolutePath(), VMAP1PPFortP.class, PeaRepDataset.BUILD_PT_POP,
            IBuildArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Ground Surface Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("GroundA")))
          && (FileUtil.getNamedFileInDir(directory, "GroundA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "GroundA.shp")
                .getAbsolutePath(), VMAP1PPGroundA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Interchange Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InterC")))
          && (FileUtil.getNamedFileInDir(directory, "InterC.shp") != null)
          && ft.equals(IRoadNode.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "InterC.shp")
            .getAbsolutePath(), VMAP1PPInterC.class,
            PeaRepDataset.ROAD_NODES_POP, IRoadNode.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Inundation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InundA")))
          && (FileUtil.getNamedFileInDir(directory, "InundA.shp") != null)
          && ft.equals(IInundationArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "InundA.shp")
                .getAbsolutePath(), VMAP1PPInundA.class,
            PeaRepDataset.MISC_AREA_POP, IInundationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Lake/Reservoir Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LakeresA")))
          && (FileUtil.getNamedFileInDir(directory, "LakeresA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LakeresA.shp")
                .getAbsolutePath(), VMAP1PPLakeresA.class,
            PeaRepDataset.WATER_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkA")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkA.shp")
                .getAbsolutePath(), VMAP1PPLandmrkA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkL")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkL.shp")
                .getAbsolutePath(), VMAP1PPLandmrkL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Landmark Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkP")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "LandmrkP.shp")
                .getAbsolutePath(), VMAP1PPLandmrkP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Aerial Cable Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LiftL")))
          && (FileUtil.getNamedFileInDir(directory, "LiftL.shp") != null)
          && ft.equals(ICable.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LiftL.shp")
                .getAbsolutePath(), VMAP1PPLiftL.class,
            PeaRepDataset.CABLE_POP, ICable.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Sebkha
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm1A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp") != null)
          && ft.equals(IReliefElementArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm1A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Moraine
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm2A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp") != null)
          && ft.equals(IReliefElementArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm2A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // LandForm (Ice Cliff) Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmL")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmL.shp") != null)
          && ft.equals(IReliefElementLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LndfrmL.shp")
                .getAbsolutePath(), VMAP1PPLndfrmL.class,
            PeaRepDataset.RELIEF_LINES_POP, IReliefElementLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.VMAP1PlusPlus);
      }

      // LandForm (Ice Peak / Nunatak / Rock Strata / Rock Formation) Point
      // Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmP")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmP.shp") != null)
          && ft.equals(IReliefElementPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "LndfrmP.shp").getAbsolutePath(),
            VMAP1PPLndfrmP.class, PeaRepDataset.RELIEF_PTS_POP,
            IReliefElementPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Lock Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LockL")))
          && (FileUtil.getNamedFileInDir(directory, "LockL.shp") != null)
          && ft.equals(IBridgeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LockL.shp")
                .getAbsolutePath(), VMAP1PPLockL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Cairn Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MarkersP")))
          && (FileUtil.getNamedFileInDir(directory, "MarkersP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "MarkersP.shp")
                .getAbsolutePath(), VMAP1PPMarkersP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Control Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MisaeroP")))
          && (FileUtil.getNamedFileInDir(directory, "MisaeroP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "MisaeroP.shp")
                .getAbsolutePath(), VMAP1PPMisaeroP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Miscellaneous Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscL")))
          && (FileUtil.getNamedFileInDir(directory, "MiscL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "MiscL.shp")
                .getAbsolutePath(), VMAP1PPMiscL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Miscellaneous Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscP")))
          && (FileUtil.getNamedFileInDir(directory, "MiscP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "MiscP.shp")
            .getAbsolutePath(), VMAP1PPMiscP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OasisP")))
          && (FileUtil.getNamedFileInDir(directory, "OasisP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "OasisP.shp")
            .getAbsolutePath(), VMAP1PPOasisP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ObstrP")))
          && (FileUtil.getNamedFileInDir(directory, "ObstrP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "ObstrP.shp")
            .getAbsolutePath(), VMAP1PPObstrP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Orchard / Plantation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OrchardA")))
          && (FileUtil.getNamedFileInDir(directory, "OrchardA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "OrchardA.shp")
                .getAbsolutePath(), VMAP1PPOrchardA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pier / Quay Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PierL")))
          && (FileUtil.getNamedFileInDir(directory, "PierL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PierL.shp")
                .getAbsolutePath(), VMAP1PPPierL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pipe Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PipeL")))
          && (FileUtil.getNamedFileInDir(directory, "PipeL.shp") != null)
          && ft.equals(IPipeLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PipeL.shp")
                .getAbsolutePath(), VMAP1PPPipeL.class,
            PeaRepDataset.PIPELINES_POP, IPipeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Administrative Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndA")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndA.shp") != null)
          && ft.equals(ISimpleAdminUnit.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "PolbndA.shp")
                .getAbsolutePath(), VMAP1PPPolbndA.class,
            PeaRepDataset.ADMIN_UNIT_POP, ISimpleAdminUnit.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Administrative Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndL")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndL.shp") != null)
          && ft.equals(IAdminLimit.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PolbndL.shp")
                .getAbsolutePath(), VMAP1PPPolbndL.class,
            PeaRepDataset.ADMIN_LIMIT_POP, IAdminLimit.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerA")))
          && (FileUtil.getNamedFileInDir(directory, "PowerA.shp") != null)
          && ft.equals(IPowerStationArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "PowerA.shp")
                .getAbsolutePath(), VMAP1PPPowerA.class,
            PeaRepDataset.MISC_AREA_POP, IPowerStationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Transmission Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerL")))
          && (FileUtil.getNamedFileInDir(directory, "PowerL.shp") != null)
          && ft.equals(IElectricityLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "PowerL.shp")
                .getAbsolutePath(), VMAP1PPPowerL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Power Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerP")))
          && (FileUtil.getNamedFileInDir(directory, "PowerP.shp") != null)
          && ft.equals(IPowerStationPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PowerP.shp")
            .getAbsolutePath(), VMAP1PPPowerP.class, PeaRepDataset.MISC_PT_POP,
            IPowerStationPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Processing/Treatment Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessA")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "ProcessA.shp")
                .getAbsolutePath(), VMAP1PPProcessA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Processing/Treatment Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessP")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ProcessP.shp")
                .getAbsolutePath(), VMAP1PPProcessP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Pumping Station Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PumpingP")))
          && (FileUtil.getNamedFileInDir(directory, "PumpingP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "PumpingP.shp")
                .getAbsolutePath(), VMAP1PPPumpingP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Rail Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RailrdL")))
          && (FileUtil.getNamedFileInDir(directory, "RailrdL.shp") != null)
          && ft.equals(IRailwayLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RailrdL.shp")
                .getAbsolutePath(), VMAP1PPRailrdL.class,
            PeaRepDataset.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Rapids (Hydro Equipement) Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RapidsC")))
          && (FileUtil.getNamedFileInDir(directory, "RapidsC.shp") != null)
          && ft.equals(IBridgePoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "RapidsC.shp").getAbsolutePath(),
            VMAP1PPRapidsC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Rest area Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RestP")))
          && (FileUtil.getNamedFileInDir(directory, "RestP.shp") != null)
          && ft.equals(IRoadFacilityPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "RestP.shp")
            .getAbsolutePath(), VMAP1PPRestP.class,
            PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Rig / Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RigwellP")))
          && (FileUtil.getNamedFileInDir(directory, "RigwellP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "RigwellP.shp")
                .getAbsolutePath(), VMAP1PPRigwellP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Road Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RoadL")))
          && (FileUtil.getNamedFileInDir(directory, "RoadL.shp") != null)
          && ft.equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RoadL.shp")
                .getAbsolutePath(), VMAP1PPRoadL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Ruins Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RuinsP")))
          && (FileUtil.getNamedFileInDir(directory, "RuinsP.shp") != null)
          && ft.equals(IBuildPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "RuinsP.shp")
            .getAbsolutePath(), VMAP1PPRuinsP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Runway Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayL")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayL.shp") != null)
          && ft.equals(IRunwayLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "RunwayL.shp")
                .getAbsolutePath(), VMAP1PPRunwayL.class,
            PeaRepDataset.RUNWAY_LINE_POP, IRunwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Runway Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayP")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayP.shp") != null)
          && ft.equals(IRunwayPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "RunwayP.shp").getAbsolutePath(),
            VMAP1PPRunwayP.class, PeaRepDataset.MISC_PT_POP,
            IRunwayPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SeastrtL")))
          && (FileUtil.getNamedFileInDir(directory, "SeastrtL.shp") != null)
          && ft.equals(ISeawallLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "SeastrtL.shp")
                .getAbsolutePath(), VMAP1PPSeastrtL.class,
            PeaRepDataset.MISC_LINE_POP, ISeawallLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Storage / Tank Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("StorageP")))
          && (FileUtil.getNamedFileInDir(directory, "StorageP.shp") != null)
          && ft.equals(IBuildPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "StorageP.shp")
                .getAbsolutePath(), VMAP1PPStorageP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Swamp Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SwampA")))
          && (FileUtil.getNamedFileInDir(directory, "SwampA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "SwampA.shp")
                .getAbsolutePath(), VMAP1PPSwampA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TeleL")))
          && (FileUtil.getNamedFileInDir(directory, "TeleL.shp") != null)
          && ft.equals(IElectricityLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TeleL.shp")
                .getAbsolutePath(), VMAP1PPTeleL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Geothermal Ferature / Volcano Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ThermalP")))
          && (FileUtil.getNamedFileInDir(directory, "ThermalP.shp") != null)
          && ft.equals(IReliefElementPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "ThermalP.shp")
                .getAbsolutePath(), VMAP1PPThermalP.class,
            PeaRepDataset.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TowerP")))
          && (FileUtil.getNamedFileInDir(directory, "TowerP.shp") != null)
          && ft.equals(IBuildPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "TowerP.shp")
            .getAbsolutePath(), VMAP1PPTowerP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Track Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrackL")))
          && (FileUtil.getNamedFileInDir(directory, "TrackL.shp") != null)
          && ft.equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TrackL.shp")
                .getAbsolutePath(), VMAP1PPTrackL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trail Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrailL")))
          && (FileUtil.getNamedFileInDir(directory, "TrailL.shp") != null)
          && ft.equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TrailL.shp")
                .getAbsolutePath(), VMAP1PPTrailL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Settling Basin / Sludge Pond Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreatA")))
          && (FileUtil.getNamedFileInDir(directory, "TreatA.shp") != null)
          && ft.equals(IWaterBasin.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "TreatA.shp")
                .getAbsolutePath(), VMAP1PPTreatA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterBasin.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trees Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesA")))
          && (FileUtil.getNamedFileInDir(directory, "TreesA.shp") != null)
          && ft.equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "TreesA.shp")
                .getAbsolutePath(), VMAP1PPTreesA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Trees Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesL")))
          && (FileUtil.getNamedFileInDir(directory, "TreesL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "TreesL.shp")
                .getAbsolutePath(), VMAP1PPTreesL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Tunnel Entrance / Exit Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelC")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelC.shp") != null)
          && ft.equals(IRoadFacilityPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "TunnelC.shp").getAbsolutePath(),
            VMAP1PPTunnelC.class, PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Tunnel Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelL")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelL.shp") != null)
          && ft.equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "TunnelL.shp").getAbsolutePath(),
            VMAP1PPTunnelL.class, PeaRepDataset.ROADS_POP,
            IRoadLine.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Water Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsA")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp") != null)
          && ft.equals(IWaterArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Water Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsL")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp") != null)
          && ft.equals(IWaterLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsL.class,
            PeaRepDataset.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WellP")))
          && (FileUtil.getNamedFileInDir(directory, "WellP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "WellP.shp")
            .getAbsolutePath(), VMAP1PPWellsprP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // /PAS DANS LE MODELE VMAP1
      // ???? Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MtnP")))
          && (FileUtil.getNamedFileInDir(directory, "MtnP.shp") != null)
          && ft.equals(IMiscPoint.FEAT_TYPE_NAME)) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "MtnP.shp")
            .getAbsolutePath(), VMAP1PPMtnP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("BarrierL")))
          && (FileUtil.getNamedFileInDir(directory, "BarrierL.shp") != null)
          && ft.equals(IMiscLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BarrierL.shp")
                .getAbsolutePath(), VMAP1PPBarrierL.class,
            PeaRepDataset.MISC_PT_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepareA")))
          && (FileUtil.getNamedFileInDir(directory, "DepareA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "DepareA.shp")
                .getAbsolutePath(), VMAP1PPDepareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Depth Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepthL")))
          && (FileUtil.getNamedFileInDir(directory, "DepthL.shp") != null)
          && ft.equals(IContourLine.FEAT_TYPE_NAME)) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DepthL.shp")
                .getAbsolutePath(), VMAP1PPDepthL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndareA")))
          && (FileUtil.getNamedFileInDir(directory, "LndareA.shp") != null)
          && ft.equals(IMiscArea.FEAT_TYPE_NAME)) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "LndareA.shp")
                .getAbsolutePath(), VMAP1PPLndareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
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
    VMAP1PlusPlusDB database = new VMAP1PlusPlusDB(name);
    database.setSourceDLM(SourceDLM.VMAP1PlusPlus);
    database.setSymboScale(300000);
    database.setDocument(CartAGenDoc.getInstance());
    CartAGenDataSet dataset = new PeaRepDataset();
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
    // database.setGeneObjImpl(new GeneObjImplementation("vmap1++",
    // VMAP1PPFeature.javaFeature.class.getPackage(), VMAP1PPFeature.class));

  }

  /**
   * * * Method to load each VMAP1++ data with a grid partition.
   */
  @Override
  public void loadDataPartition(File directory, List<String> listLayer,
      IPolygon partition) throws ShapefileException, IOException, Exception {
    // TODO Auto-generated method stub

    try {

      // Airport Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("AerofacP")))
          && (FileUtil.getNamedFileInDir(directory, "AerofacP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "AerofacP.shp")
                .getAbsolutePath(), VMAP1PPAerofacP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Grain Bin / Silo Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AgristrP")))
          && (FileUtil.getNamedFileInDir(directory, "AgristrP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "AgristrP.shp")
                .getAbsolutePath(), VMAP1PPAgristrP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Aquedec Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("AquedctL")))
          && (FileUtil.getNamedFileInDir(directory, "AquedctL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "AquedctL.shp")
                .getAbsolutePath(), VMAP1PPAquedctL.class,
            CartAGenDataSet.POI_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Bluff / Cliff Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BluffL")))
          && (FileUtil.getNamedFileInDir(directory, "BluffL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "BluffL.shp")
                .getAbsolutePath(), VMAP1PPBluffL.class,
            CartAGenDataSet.RELIEF_LINES_POP,
            IReliefElementLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Bridge Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeC")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "BridgeC.shp")
                .getAbsolutePath(), VMAP1PPBridgeC.class,
            PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Bridge Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("BridgeL")))
          && (FileUtil.getNamedFileInDir(directory, "BridgeL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "BridgeL.shp")
                .getAbsolutePath(), VMAP1PPBridgeL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Buid Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildA")))
          && (FileUtil.getNamedFileInDir(directory, "BuildA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "BuildA.shp")
                .getAbsolutePath(), VMAP1PPBuildA.class,
            PeaRepDataset.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Build Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuildP")))
          && (FileUtil.getNamedFileInDir(directory, "BuildP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "BuildP.shp")
                .getAbsolutePath(), VMAP1PPBuildP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Built-up Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupA")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "BuiltupA.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Built-up Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("BuiltupP")))
          && (FileUtil.getNamedFileInDir(directory, "BuiltupP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "BuiltupP.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Island Area loading
      if (((listLayer.size() == 0) || (listLayer.contains("CoastA")))
          && (FileUtil.getNamedFileInDir(directory, "CoastA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "CoastA.shp")
                .getAbsolutePath(), VMAP1PPCoastA.class,
            PeaRepDataset.WATER_ISLAND_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Coast Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("CoastL")))
          && (FileUtil.getNamedFileInDir(directory, "CoastL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "CoastL.shp")
                .getAbsolutePath(), VMAP1PPCoastL.class,
            PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Radar (Communication) Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("CommP")))
          && (FileUtil.getNamedFileInDir(directory, "CommP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "CommP.shp")
                .getAbsolutePath(), VMAP1PPCommP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Contour Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("ContourL")))
          && (FileUtil.getNamedFileInDir(directory, "ContourL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "ContourL.shp")
                .getAbsolutePath(), VMAP1PPContourL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Crop Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("CropA")))
          && (FileUtil.getNamedFileInDir(directory, "CropA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "CropA.shp")
                .getAbsolutePath(), VMAP1PPCropA.class,
            PeaRepDataset.MISC_AREA_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Dam Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamC")))
          && (FileUtil.getNamedFileInDir(directory, "DamC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "DamC.shp").getAbsolutePath(),
            VMAP1PPDamC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Dam Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("DamL")))
          && (FileUtil.getNamedFileInDir(directory, "DamL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "DamL.shp").getAbsolutePath(),
            VMAP1PPDamL.class, PeaRepDataset.BRIDGE_LINE_POP,
            IBridgeLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus,
            partition);
      }

      // Danger (Reef, Island, Rock) Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerA")))
          && (FileUtil.getNamedFileInDir(directory, "DangerA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "DangerA.shp")
                .getAbsolutePath(), VMAP1PPDangerA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Danger (Reef, Island, Rock) Line loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerL")))
          && (FileUtil.getNamedFileInDir(directory, "DangerL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "DangerL.shp")
                .getAbsolutePath(), VMAP1PPDangerL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Danger (Reef, Island, Rock) Point loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DangerP")))
          && (FileUtil.getNamedFileInDir(directory, "DangerP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "DangerP.shp")
                .getAbsolutePath(), VMAP1PPDangerP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Dispose Area loading (!!! vide)
      if (((listLayer.size() == 0) || (listLayer.contains("DisposeA")))
          && (FileUtil.getNamedFileInDir(directory, "DisposeA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "DisposeA.shp")
                .getAbsolutePath(), VMAP1PPDisposeA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Elevation Point loading
      if (((listLayer.size() == 0) || (listLayer.contains("ElevP")))
          && (FileUtil.getNamedFileInDir(directory, "ElevP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "ElevP.shp")
                .getAbsolutePath(), VMAP1PPElevP.class,
            PeaRepDataset.SPOT_HEIGHTS_POP, ISpotHeight.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Embankment (Cut, fill...) Line loading
      if (((listLayer.size() == 0) || (listLayer.contains("EmbankL")))
          && (FileUtil.getNamedFileInDir(directory, "EmbankL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "EmbankL.shp")
                .getAbsolutePath(), VMAP1PPEmbankmentL.class,
            PeaRepDataset.MISC_LINE_POP, IEmbankmentLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Mine / Quarry Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractA")))
          && (FileUtil.getNamedFileInDir(directory, "LAP030.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "ExtractA.shp")
                .getAbsolutePath(), VMAP1PPExtractA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Mine / Quarry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ExtractP")))
          && (FileUtil.getNamedFileInDir(directory, "ExtractP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "ExtractP.shp")
                .getAbsolutePath(), VMAP1PPExtractP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ferry Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryC")))
          && (FileUtil.getNamedFileInDir(directory, "FerryC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "FerryC.shp")
                .getAbsolutePath(), VMAP1PPFerryC.class,
            PeaRepDataset.MISC_PT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ferry Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FerryL")))
          && (FileUtil.getNamedFileInDir(directory, "FerryL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "FerryL.shp")
                .getAbsolutePath(), VMAP1PPFerryL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ford Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordC")))
          && (FileUtil.getNamedFileInDir(directory, "FordC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "FordC.shp")
                .getAbsolutePath(), VMAP1PPFordC.class,
            PeaRepDataset.MISC_PT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ford Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordL")))
          && (FileUtil.getNamedFileInDir(directory, "FordL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "FordL.shp")
                .getAbsolutePath(), VMAP1PPFordL.class,
            PeaRepDataset.MISC_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Fort Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FordA")))
          && (FileUtil.getNamedFileInDir(directory, "FordA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "FortA.shp")
                .getAbsolutePath(), VMAP1PPFortA.class,
            PeaRepDataset.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Fort Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("FortP")))
          && (FileUtil.getNamedFileInDir(directory, "FortP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "FortP.shp")
                .getAbsolutePath(), VMAP1PPFortP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ground Surface Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("GroundA")))
          && (FileUtil.getNamedFileInDir(directory, "GroundA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "GroundA.shp")
                .getAbsolutePath(), VMAP1PPGroundA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Interchange Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InterC")))
          && (FileUtil.getNamedFileInDir(directory, "InterC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "InterC.shp")
                .getAbsolutePath(), VMAP1PPInterC.class,
            PeaRepDataset.ROAD_NODES_POP, IRoadNode.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Inundation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("InundA")))
          && (FileUtil.getNamedFileInDir(directory, "InundA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "InundA.shp")
                .getAbsolutePath(), VMAP1PPInundA.class,
            PeaRepDataset.MISC_AREA_POP, IInundationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Lake/Reservoir Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LakeresA")))
          && (FileUtil.getNamedFileInDir(directory, "LakeresA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "LakeresA.shp")
                .getAbsolutePath(), VMAP1PPLakeresA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Landmark Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkA")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "LandmrkA.shp")
                .getAbsolutePath(), VMAP1PPLandmrkA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Landmark Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkL")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "LandmrkL.shp")
                .getAbsolutePath(), VMAP1PPLandmrkL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Landmark Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LandmrkP")))
          && (FileUtil.getNamedFileInDir(directory, "LandmrkP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "LandmrkP.shp")
                .getAbsolutePath(), VMAP1PPLandmrkP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Aerial Cable Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LiftL")))
          && (FileUtil.getNamedFileInDir(directory, "LiftL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "LiftL.shp")
                .getAbsolutePath(), VMAP1PPLiftL.class,
            PeaRepDataset.CABLE_POP, ICable.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Sebkha
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm1A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "Lndfrm1A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm1A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Moraine
      if (((listLayer.size() == 0) || (listLayer.contains("Lndfrm2A")))
          && (FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "Lndfrm2A.shp")
                .getAbsolutePath(), VMAP1PPLndfrm2A.class,
            PeaRepDataset.MISC_AREA_POP, IReliefElementArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // LandForm (Ice Cliff) Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmL")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "LndfrmL.shp")
                .getAbsolutePath(), VMAP1PPLndfrmL.class,
            PeaRepDataset.RELIEF_LINES_POP, IReliefElementLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // LandForm (Ice Peak / Nunatak / Rock Strata / Rock Formation) Point
      // Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndfrmP")))
          && (FileUtil.getNamedFileInDir(directory, "LndfrmP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "LndfrmP.shp")
                .getAbsolutePath(), VMAP1PPLndfrmP.class,
            PeaRepDataset.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Lock Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LockL")))
          && (FileUtil.getNamedFileInDir(directory, "LockL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "LockL.shp")
                .getAbsolutePath(), VMAP1PPLockL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Cairn Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MarkersP")))
          && (FileUtil.getNamedFileInDir(directory, "MarkersP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "MarkersP.shp")
                .getAbsolutePath(), VMAP1PPMarkersP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Control Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MisaeroP")))
          && (FileUtil.getNamedFileInDir(directory, "MisaeroP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "MisaeroP.shp")
                .getAbsolutePath(), VMAP1PPMisaeroP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Miscellaneous Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscL")))
          && (FileUtil.getNamedFileInDir(directory, "MiscL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "MiscL.shp")
                .getAbsolutePath(), VMAP1PPMiscL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Miscellaneous Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MiscP")))
          && (FileUtil.getNamedFileInDir(directory, "MiscP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "MiscP.shp")
                .getAbsolutePath(), VMAP1PPMiscP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OasisP")))
          && (FileUtil.getNamedFileInDir(directory, "OasisP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "OasisP.shp")
                .getAbsolutePath(), VMAP1PPOasisP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Oasis Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ObstrP")))
          && (FileUtil.getNamedFileInDir(directory, "ObstrP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "ObstrP.shp")
                .getAbsolutePath(), VMAP1PPObstrP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Orchard / Plantation Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("OrchardA")))
          && (FileUtil.getNamedFileInDir(directory, "OrchardA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "OrchardA.shp")
                .getAbsolutePath(), VMAP1PPOrchardA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Pier / Quay Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PierL")))
          && (FileUtil.getNamedFileInDir(directory, "PierL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "PierL.shp")
                .getAbsolutePath(), VMAP1PPPierL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Pipe Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PipeL")))
          && (FileUtil.getNamedFileInDir(directory, "PipeL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "PipeL.shp")
                .getAbsolutePath(), VMAP1PPPipeL.class,
            PeaRepDataset.PIPELINES_POP, IPipeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Administrative Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndA")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "PolbndA.shp")
                .getAbsolutePath(), VMAP1PPPolbndA.class,
            PeaRepDataset.ADMIN_UNIT_POP, ISimpleAdminUnit.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Administrative Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PolbndL")))
          && (FileUtil.getNamedFileInDir(directory, "PolbndL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "PolbndL.shp")
                .getAbsolutePath(), VMAP1PPPolbndL.class,
            PeaRepDataset.ADMIN_LIMIT_POP, IAdminLimit.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Power Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerA")))
          && (FileUtil.getNamedFileInDir(directory, "PowerA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "PowerA.shp")
                .getAbsolutePath(), VMAP1PPPowerA.class,
            PeaRepDataset.MISC_AREA_POP, IPowerStationArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Power Transmission Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerL")))
          && (FileUtil.getNamedFileInDir(directory, "PowerL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "PowerL.shp")
                .getAbsolutePath(), VMAP1PPPowerL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus,
            partition);
      }

      // Power Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PowerP")))
          && (FileUtil.getNamedFileInDir(directory, "PowerP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "PowerP.shp")
                .getAbsolutePath(), VMAP1PPPowerP.class,
            PeaRepDataset.MISC_PT_POP, IPowerStationPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Processing/Treatment Plant Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessA")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "ProcessA.shp")
                .getAbsolutePath(), VMAP1PPProcessA.class,
            PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Processing/Treatment Plant Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ProcessP")))
          && (FileUtil.getNamedFileInDir(directory, "ProcessP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "ProcessP.shp")
                .getAbsolutePath(), VMAP1PPProcessP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Pumping Station Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("PumpingP")))
          && (FileUtil.getNamedFileInDir(directory, "PumpingP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "PumpingP.shp")
                .getAbsolutePath(), VMAP1PPPumpingP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Rail Way Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RailrdL")))
          && (FileUtil.getNamedFileInDir(directory, "RailrdL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "RailrdL.shp")
                .getAbsolutePath(), VMAP1PPRailrdL.class,
            PeaRepDataset.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Rapids (Hydro Equipement) Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RapidsC")))
          && (FileUtil.getNamedFileInDir(directory, "RapidsC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "RapidsC.shp")
                .getAbsolutePath(), VMAP1PPRapidsC.class,
            PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Rest area Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RestP")))
          && (FileUtil.getNamedFileInDir(directory, "RestP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "RestP.shp")
                .getAbsolutePath(), VMAP1PPRestP.class,
            PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus,
            partition);
      }

      // Rig / Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RigwellP")))
          && (FileUtil.getNamedFileInDir(directory, "RigwellP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "RigwellP.shp")
                .getAbsolutePath(), VMAP1PPRigwellP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Road Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RoadL")))
          && (FileUtil.getNamedFileInDir(directory, "RoadL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "RoadL.shp")
                .getAbsolutePath(), VMAP1PPRoadL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Ruins Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RuinsP")))
          && (FileUtil.getNamedFileInDir(directory, "RuinsP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "RuinsP.shp")
                .getAbsolutePath(), VMAP1PPRuinsP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Runway Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayL")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "RunwayL.shp")
                .getAbsolutePath(), VMAP1PPRunwayL.class,
            PeaRepDataset.RUNWAY_LINE_POP, IRunwayLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Runway Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("RunwayP")))
          && (FileUtil.getNamedFileInDir(directory, "RunwayP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "RunwayP.shp")
                .getAbsolutePath(), VMAP1PPRunwayP.class,
            PeaRepDataset.MISC_PT_POP, IRunwayPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SeastrtL")))
          && (FileUtil.getNamedFileInDir(directory, "SeastrtL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "SeastrtL.shp")
                .getAbsolutePath(), VMAP1PPSeastrtL.class,
            PeaRepDataset.MISC_LINE_POP, ISeawallLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Storage / Tank Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("StorageP")))
          && (FileUtil.getNamedFileInDir(directory, "StorageP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "StorageP.shp")
                .getAbsolutePath(), VMAP1PPStorageP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Swamp Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("SwampA")))
          && (FileUtil.getNamedFileInDir(directory, "SwampA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "SwampA.shp")
                .getAbsolutePath(), VMAP1PPSwampA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Breakwater / Groyne Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TeleL")))
          && (FileUtil.getNamedFileInDir(directory, "TeleL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "TeleL.shp")
                .getAbsolutePath(), VMAP1PPTeleL.class,
            PeaRepDataset.ELECTRICITY_LINES_POP,
            IElectricityLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus,
            partition);
      }

      // Geothermal Ferature / Volcano Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("ThermalP")))
          && (FileUtil.getNamedFileInDir(directory, "ThermalP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "ThermalP.shp")
                .getAbsolutePath(), VMAP1PPThermalP.class,
            PeaRepDataset.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Tower Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TowerP")))
          && (FileUtil.getNamedFileInDir(directory, "TowerP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "TowerP.shp")
                .getAbsolutePath(), VMAP1PPTowerP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Track Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrackL")))
          && (FileUtil.getNamedFileInDir(directory, "TrackL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "TrackL.shp")
                .getAbsolutePath(), VMAP1PPTrackL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Trail Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TrailL")))
          && (FileUtil.getNamedFileInDir(directory, "TrailL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "TrailL.shp")
                .getAbsolutePath(), VMAP1PPTrailL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Settling Basin / Sludge Pond Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreatA")))
          && (FileUtil.getNamedFileInDir(directory, "TreatA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "TreatA.shp")
                .getAbsolutePath(), VMAP1PPTreatA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterBasin.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Trees Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesA")))
          && (FileUtil.getNamedFileInDir(directory, "TreesA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "TreesA.shp")
                .getAbsolutePath(), VMAP1PPTreesA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Trees Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TreesL")))
          && (FileUtil.getNamedFileInDir(directory, "TreesL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "TreesL.shp")
                .getAbsolutePath(), VMAP1PPTreesL.class,
            PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Tunnel Entrance / Exit Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelC")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelC.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "TunnelC.shp")
                .getAbsolutePath(), VMAP1PPTunnelC.class,
            PeaRepDataset.ROAD_FACILITY_PT_POP,
            IRoadFacilityPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus,
            partition);
      }

      // Tunnel Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("TunnelL")))
          && (FileUtil.getNamedFileInDir(directory, "TunnelL.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "TunnelL.shp")
                .getAbsolutePath(), VMAP1PPTunnelL.class,
            PeaRepDataset.ROADS_POP, IRoadLine.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Water Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsA")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "WatrcrsA.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsA.class,
            PeaRepDataset.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Water Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WatrcrsL")))
          && (FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "WatrcrsL.shp")
                .getAbsolutePath(), VMAP1PPWatrcrsL.class,
            PeaRepDataset.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // Well Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("WellP")))
          && (FileUtil.getNamedFileInDir(directory, "WellP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "WellP.shp")
                .getAbsolutePath(), VMAP1PPWellsprP.class,
            PeaRepDataset.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }

      // /PAS DANS LE MODELE VMAP1
      // ???? Point Loading
      if (((listLayer.size() == 0) || (listLayer.contains("MtnP")))
          && (FileUtil.getNamedFileInDir(directory, "MtnP.shp") != null)) {
        this.loadPointClassPartition(
            FileUtil.getNamedFileInDir(directory, "MtnP.shp").getAbsolutePath(),
            VMAP1PPMtnP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus, partition);
      }
      // ???? Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("BarrierL")))
          && (FileUtil.getNamedFileInDir(directory, "BarrierL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "BarrierL.shp")
                .getAbsolutePath(), VMAP1PPBarrierL.class,
            PeaRepDataset.MISC_PT_POP, IMiscLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepareA")))
          && (FileUtil.getNamedFileInDir(directory, "DepareA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "DepareA.shp")
                .getAbsolutePath(), VMAP1PPDepareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }
      // ???? Depth Line Loading
      if (((listLayer.size() == 0) || (listLayer.contains("DepthL")))
          && (FileUtil.getNamedFileInDir(directory, "DepthL.shp") != null)) {
        this.loadLineStringClassPartition(
            FileUtil.getNamedFileInDir(directory, "DepthL.shp")
                .getAbsolutePath(), VMAP1PPDepthL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus, partition);
      }
      // ???? Area Loading
      if (((listLayer.size() == 0) || (listLayer.contains("LndareA")))
          && (FileUtil.getNamedFileInDir(directory, "LndareA.shp") != null)) {
        this.loadPolygonClassPartition(
            FileUtil.getNamedFileInDir(directory, "LndareA.shp")
                .getAbsolutePath(), VMAP1PPLndareA.class,
            PeaRepDataset.MISC_PT_POP, IMiscArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus, partition);
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

  /**
   * Method to compute the envelope of each shapefile.
   */
  public IPolygon computeEnvelope(ShapefileReader shr) {

    double minX, minY, maxX, maxY;
    minX = shr.getHeader().minX();
    minY = shr.getHeader().minY();
    maxX = shr.getHeader().maxX();
    maxY = shr.getHeader().maxY();

    IDirectPositionList dpl = new DirectPositionList();
    dpl.add(new DirectPosition(minX, minY));
    dpl.add(new DirectPosition(minX, maxY));
    dpl.add(new DirectPosition(maxX, maxY));
    dpl.add(new DirectPosition(maxX, minY));

    IPolygon envelope = new GM_Polygon(new GM_LineString(dpl));

    return envelope;
  }

  /**
   * Method to compute the cells of the grid partition.
   * @param cellSize
   */
  public void computeCellsGrid(double cellSize) {

    List<IPolygon> cellsGrid = new ArrayList<IPolygon>();
    IPolygon grid = this.envelopeTotale;

    // compute the number of path / row
    double path = (grid.envelope().maxX() - grid.envelope().minX()) / cellSize;
    double row = (grid.envelope().maxY() - grid.envelope().minY()) / cellSize;

    for (int j = 0; j < row; j++) {
      for (int i = 0; i < path; i++) {
        double minX = grid.envelope().minX() + i * cellSize;
        double maxX = grid.envelope().minX() + (i + 1) * cellSize;
        double minY = grid.envelope().minY() + j * cellSize;
        double maxY = grid.envelope().minY() + (j + 1) * cellSize;

        IDirectPositionList dplCell = new DirectPositionList();
        dplCell.add(new DirectPosition(minX, minY));
        dplCell.add(new DirectPosition(minX, maxY));
        dplCell.add(new DirectPosition(maxX, maxY));
        dplCell.add(new DirectPosition(maxX, minY));
        IPolygon cell = new GM_Polygon(new GM_LineString(dplCell));

        cellsGrid.add(cell);
      }
    }

    this.setCellsGrid(cellsGrid);
  }

  public IPolygon getEnvelopeTotale() {
    return envelopeTotale;
  }

  public void setEnvelopeTotale(IPolygon envelopeTotale) {
    this.envelopeTotale = envelopeTotale;
  }

  public List<IPolygon> getCellsGrid() {
    return cellsGrid;
  }

  public void setCellsGrid(List<IPolygon> cellsGrid) {
    this.cellsGrid = cellsGrid;
  }

}
