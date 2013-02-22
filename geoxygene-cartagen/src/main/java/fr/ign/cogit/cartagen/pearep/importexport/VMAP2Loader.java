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
import java.util.List;

import org.geotools.data.shapefile.shp.ShapefileException;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap.elev.VMAPContourLine;
import fr.ign.cogit.cartagen.pearep.vmap.hydro.VMAPWaterArea;
import fr.ign.cogit.cartagen.pearep.vmap.hydro.VMAPWaterLine;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuildPoint;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuilding;
import fr.ign.cogit.cartagen.pearep.vmap.pop.VMAPBuiltUpArea;
import fr.ign.cogit.cartagen.pearep.vmap.transport.VMAPRoadLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.util.FileUtil;

public class VMAP2Loader extends ShapeFileLoader {

  /**
   * Default constructor
   * @param dataset
   */
  public VMAP2Loader(SymbolGroup symbGroup, String dbName) {
    createNewDb(symbGroup, dbName);
    this.setProjEpsg("32631");
  }

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {
      File shapePath;

      if (listLayer.size() == 0) {
        // ground transportation loading
        shapePath = FileUtil.getNamedFileInDir(directory, "gtr");
        loadLineStringClass(FileUtil.getNamedFileInDir(shapePath, "roadl.shp")
            .getAbsolutePath(), VMAPRoadLine.class, CartAGenDataSet.ROADS_POP,
            IRoadLine.FEAT_TYPE_NAME, this.getDataset().getRoadNetwork(),
            PeaRepDbType.VMAP2i);
        // loadLineStringClass(FileUtil.getNamedFileInDir(shapePath,
        // "trackl.shp")
        // .getAbsolutePath(), VMAPPath.class, CartAGenDataSet.PATHS_POP,
        // IPath.FEAT_TYPE_NAME, null, true);
        // loadLineStringClass(FileUtil.getNamedFileInDir(shapePath,
        // "traill.shp")
        // .getAbsolutePath(), VMAPPath.class, CartAGenDataSet.PATHS_POP,
        // IPath.FEAT_TYPE_NAME, null, true);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath,
        // "vehstora.shp")
        // .getAbsolutePath(), VMAPRoadArea.class,
        // CartAGenDataSet.ROAD_AREA_POP, IRoadArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // hydro loading
        shapePath = FileUtil.getNamedFileInDir(directory, "iwa");
        loadLineStringClass(
            FileUtil.getNamedFileInDir(shapePath, "watrcrsl.shp")
                .getAbsolutePath(), VMAPWaterLine.class,
            CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, this
                .getDataset().getHydroNetwork(), PeaRepDbType.VMAP2i);
        loadPolygonClassUnionMulti(
            FileUtil.getNamedFileInDir(shapePath, "watrcrsa.shp")
                .getAbsolutePath(), VMAPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP2i);
        loadPolygonClassUnionMulti(
            FileUtil.getNamedFileInDir(shapePath, "lakeresa.shp")
                .getAbsolutePath(), VMAPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "springp.shp")
        // .getAbsolutePath(), VMAPWaterPoint.class,
        // CartAGenDataSet.WATER_PT_POP, IWaterPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "inunda.shp")
        // .getAbsolutePath(), VMAPFloodArea.class,
        // CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // elevation loading
        shapePath = FileUtil.getNamedFileInDir(directory, "ele");
        loadLineStringClass(
            FileUtil.getNamedFileInDir(shapePath, "contourl.shp")
                .getAbsolutePath(), VMAPContourLine.class,
            CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "elevp.shp")
        // .getAbsolutePath(), VMAPSpotHeight.class,
        // CartAGenDataSet.SPOT_HEIGHTS_POP, ISpotHeight.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // aero loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "aer");
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "runwaya.shp")
        // .getAbsolutePath(), VMAPRunwayArea.class,
        // CartAGenDataSet.RUNWAY_AREA_POP, IRunwayArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath,
        // "aerofaca.shp")
        // .getAbsolutePath(), VMAPAirportArea.class,
        // CartAGenDataSet.AIRPORT_AREA_POP, IAirportArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // administrative layers loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "clb");
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "coasta.shp")
        // .getAbsolutePath(), VMAPIsland.class,
        // CartAGenDataSet.WATER_ISLAND_POP, IRiverSimpleIsland.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "polbnda.shp")
        // .getAbsolutePath(), VMAPAdminUnit.class,
        // CartAGenDataSet.ADMIN_UNIT_POP, ISimpleAdminUnit.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadLineStringClass(FileUtil.getNamedFileInDir(shapePath,
        // "polbndl.shp")
        // .getAbsolutePath(), VMAPAdminLimit.class,
        // CartAGenDataSet.ADMIN_LIMIT_POP, IAdminLimit.FEAT_TYPE_NAME, null,
        // DbType.VMAP2i);

        // gob package loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "gob");
        // loadLineStringClass(FileUtil.getNamedFileInDir(shapePath,
        // "bluffl.shp")
        // .getAbsolutePath(), VMAPBluffLine.class,
        // CartAGenDataSet.RELIEF_LINES_POP, IReliefElementLine.FEAT_TYPE_NAME,
        // null, DbType.VMAP2i);

        // ind package loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "ind");
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath,
        // "storagea.shp")
        // .getAbsolutePath(), VMAPStorageArea.class,
        // CartAGenDataSet.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath,
        // "processa.shp")
        // .getAbsolutePath(), VMAPProcessArea.class,
        // CartAGenDataSet.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "storagep.shp")
        // .getAbsolutePath(), VMAPStoragePoint.class,
        // CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "obstrp.shp")
        // .getAbsolutePath(), VMAPObstrPoint.class,
        // CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // geology loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "phy");
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "grounda.shp")
        // .getAbsolutePath(), VMAPGround.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "lndfrmp.shp")
        // .getAbsolutePath(), VMAPLandFormPoint.class,
        // CartAGenDataSet.RELIEF_PTS_POP, IReliefElementPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // population loading
        shapePath = FileUtil.getNamedFileInDir(directory, "pop");
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "builda.shp")
            .getAbsolutePath(), VMAPBuilding.class,
            CartAGenDataSet.BUILDINGS_POP, IBuilding.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "sporta.shp")
        // .getAbsolutePath(), VMAPSportsField.class,
        // CartAGenDataSet.SPORTS_FIELDS_POP, ISportsField.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "sportp.shp")
        // .getAbsolutePath(), VMAPSportPoint.class,
        // CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        loadPointClass(FileUtil.getNamedFileInDir(shapePath, "buildp.shp")
            .getAbsolutePath(), VMAPBuildPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "mispopp.shp")
        // .getAbsolutePath(), VMAPMiscPopPoint.class,
        // CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "towerp.shp")
        // .getAbsolutePath(), VMAPTower.class, CartAGenDataSet.MISC_PT_POP,
        // IMiscPoint.FEAT_TYPE_NAME, DbType.VMAP2i);
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "builtupa.shp")
            .getAbsolutePath(), VMAPBuiltUpArea.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "mispopa.shp")
        // .getAbsolutePath(), VMAPMiscPopArea.class,
        // CartAGenDataSet.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);

        // uti package loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "uti");
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "powerp.shp")
        // .getAbsolutePath(), VMAPPowerPoint.class,
        // CartAGenDataSet.MISC_PT_POP, IMiscPoint.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPointClass(FileUtil.getNamedFileInDir(shapePath, "transevp.shp")
        // .getAbsolutePath(), VMAPCommPoint.class, CartAGenDataSet.MISC_PT_POP,
        // IMiscPoint.FEAT_TYPE_NAME, DbType.VMAP2i);

        // vegetation loading
        // shapePath = FileUtil.getNamedFileInDir(directory, "veg");
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "cropa.shp")
        // .getAbsolutePath(), VMAPCrop.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "grassa.shp")
        // .getAbsolutePath(), VMAPGrass.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath,
        // "orcharda.shp")
        // .getAbsolutePath(), VMAPOrchard.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
        // loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "swampa.shp")
        // .getAbsolutePath(), VMAPSwamp.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
        // DbType.VMAP2i);
      } else {

        for (String layerName : listLayer) {

          if (layerName.equals("roadl")) {
            // ground transportation loading
            shapePath = FileUtil.getNamedFileInDir(directory, "gtr");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "roadl.shp")
                    .getAbsolutePath(), VMAPRoadLine.class,
                CartAGenDataSet.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, this
                    .getDataset().getRoadNetwork(), PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("watrcrsl")) {
            // hydro loading
            shapePath = FileUtil.getNamedFileInDir(directory, "iwa");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "watrcrsl.shp")
                    .getAbsolutePath(), VMAPWaterLine.class,
                CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME,
                this.getDataset().getHydroNetwork(), PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("watrcrsa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "iwa");
            loadPolygonClassUnionMulti(
                FileUtil.getNamedFileInDir(shapePath, "watrcrsa.shp")
                    .getAbsolutePath(), VMAPWaterArea.class,
                CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("lakeresa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "iwa");
            loadPolygonClassUnionMulti(
                FileUtil.getNamedFileInDir(shapePath, "lakeresa.shp")
                    .getAbsolutePath(), VMAPWaterArea.class,
                CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("contourl")) {
            // elevation loading
            shapePath = FileUtil.getNamedFileInDir(directory, "ele");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "contourl.shp")
                    .getAbsolutePath(), VMAPContourLine.class,
                CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
                null, PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("builda")) {
            // population loading
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPolygonClass(FileUtil
                .getNamedFileInDir(shapePath, "builda.shp").getAbsolutePath(),
                VMAPBuilding.class, CartAGenDataSet.BUILDINGS_POP,
                IBuilding.FEAT_TYPE_NAME, PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("buildp")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPointClass(FileUtil.getNamedFileInDir(shapePath, "buildp.shp")
                .getAbsolutePath(), VMAPBuildPoint.class,
                CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP2i);
          }

          if (layerName.equals("builtupa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPolygonClass(
                FileUtil.getNamedFileInDir(shapePath, "builtupa.shp")
                    .getAbsolutePath(), VMAPBuiltUpArea.class,
                CartAGenDataSet.LANDUSE_AREAS_POP,
                ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP2i);
          }

        }

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
    database.setSourceDLM(SourceDLM.VMAP2i);
    database.setSymboScale(100000);
    database.setDocument(CartAGenDoc.getInstance());
    CartAGenDataSet dataset = new PeaRepDataset();
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
  }

}
