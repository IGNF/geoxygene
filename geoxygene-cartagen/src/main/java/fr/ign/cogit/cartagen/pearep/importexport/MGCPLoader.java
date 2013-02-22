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
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuildPoint;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPContourLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPWaterArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPWaterLine;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPAirport;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPRunwayArea;
import fr.ign.cogit.cartagen.pearep.mgcp.aer.MGCPTaxiwayArea;
import fr.ign.cogit.cartagen.pearep.mgcp.energy.MGCPElectricityLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPPathLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRailwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRoadLine;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
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

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {

      if ((listLayer.size() == 0) || (listLayer.contains("LAP030"))) {
        // ground transportation loading
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP030.shp")
                .getAbsolutePath(), MGCPRoadLine.class,
            CartAGenDataSet.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, this
                .getDataset().getRoadNetwork(), PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAP010"))) {
        // ground transportation loading
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP010.shp")
                .getAbsolutePath(), MGCPPathLine.class,
            CartAGenDataSet.PATHS_POP, IPathLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("LAP050"))) {
        // ground transportation loading
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAP050.shp")
                .getAbsolutePath(), MGCPPathLine.class,
            CartAGenDataSet.PATHS_POP, IPathLine.FEAT_TYPE_NAME, null,
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
                .getAbsolutePath(), MGCPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }
      if ((listLayer.size() == 0) || (listLayer.contains("ABH140"))) {
        this.loadPolygonClassUnionMulti(
            FileUtil.getNamedFileInDir(directory, "ABH140.shp")
                .getAbsolutePath(), MGCPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      // elevation loading
      if ((listLayer.size() == 0) || (listLayer.contains("LCA010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LCA010.shp")
                .getAbsolutePath(), MGCPContourLine.class,
            CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.MGCPPlusPlus);
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

      // population loading
      if ((listLayer.size() == 0) || (listLayer.contains("PAL015"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "PAL015.shp")
            .getAbsolutePath(), MGCPBuildPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.MGCPPlusPlus);
      }

      if ((listLayer.size() == 0) || (listLayer.contains("AAL020"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "AAL020.shp")
                .getAbsolutePath(), MGCPBuiltUpArea.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.MGCPPlusPlus);
      }

      // railway
      if ((listLayer.size() == 0) || (listLayer.contains("LAN010"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "LAN010.shp")
                .getAbsolutePath(), MGCPRailwayLine.class,
            CartAGenDataSet.RAILWAY_LINES_POP, IRailwayLine.FEAT_TYPE_NAME,
            this.getDataset().getRailwayNetwork(), PeaRepDbType.MGCPPlusPlus);
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
    CartAGenDataSet dataset = new CartAGenDataSet();
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
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
        }
      }
    }

  }
}
