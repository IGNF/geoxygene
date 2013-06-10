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
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.util.FileUtil;

public class VMAP1Loader extends ShapeFileLoader {

  /**
   * Default constructor
   * @param dataset
   */
  public VMAP1Loader(SymbolGroup group, String dbName) {
    createNewDb(group, dbName);
    this.setProjEpsg("32631");
  }

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {

      File shapePath;

      if (listLayer.size() == 0) {

        // ground transportation loading
        shapePath = FileUtil.getNamedFileInDir(directory, "trans");
        loadLineStringClass(FileUtil.getNamedFileInDir(shapePath, "roadl.shp")
            .getAbsolutePath(), VMAPRoadLine.class, CartAGenDataSet.ROADS_POP,
            IRoadLine.FEAT_TYPE_NAME, this.getDataset().getRoadNetwork(),
            PeaRepDbType.VMAP1);

        // hydro loading
        shapePath = FileUtil.getNamedFileInDir(directory, "hydro");
        loadLineStringClass(
            FileUtil.getNamedFileInDir(shapePath, "watrcrsl.shp")
                .getAbsolutePath(), VMAPWaterLine.class,
            CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME, this
                .getDataset().getHydroNetwork(), PeaRepDbType.VMAP1);
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "watrcrsa.shp")
            .getAbsolutePath(), VMAPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1);
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "lakeresa.shp")
            .getAbsolutePath(), VMAPWaterArea.class,
            CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1);

        // elevation loading
        shapePath = FileUtil.getNamedFileInDir(directory, "elev");
        loadLineStringClass(
            FileUtil.getNamedFileInDir(shapePath, "contourl.shp")
                .getAbsolutePath(), VMAPContourLine.class,
            CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
            null, PeaRepDbType.VMAP1);

        // population loading
        shapePath = FileUtil.getNamedFileInDir(directory, "pop");
        loadPointClass(FileUtil.getNamedFileInDir(shapePath, "buildp.shp")
            .getAbsolutePath(), VMAPBuildPoint.class,
            CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1);
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "builda.shp")
            .getAbsolutePath(), VMAPBuilding.class,
            CartAGenDataSet.BUILDINGS_POP, IBuilding.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1);

        // builtuparea loading
        shapePath = FileUtil.getNamedFileInDir(directory, "pop");
        loadPolygonClass(FileUtil.getNamedFileInDir(shapePath, "builtupa.shp")
            .getAbsolutePath(), VMAPBuiltUpArea.class,
            CartAGenDataSet.LANDUSE_AREAS_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1);
      } else {

        for (String layerName : listLayer) {

          if (layerName.equals("roadl")) {
            // ground transportation loading
            shapePath = FileUtil.getNamedFileInDir(directory, "trans");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "roadl.shp")
                    .getAbsolutePath(), VMAPRoadLine.class,
                CartAGenDataSet.ROADS_POP, IRoadLine.FEAT_TYPE_NAME, this
                    .getDataset().getRoadNetwork(), PeaRepDbType.VMAP1);
          }

          if (layerName.equals("watrcrsl")) {
            // hydro loading
            shapePath = FileUtil.getNamedFileInDir(directory, "hydro");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "watrcrsl.shp")
                    .getAbsolutePath(), VMAPWaterLine.class,
                CartAGenDataSet.WATER_LINES_POP, IWaterLine.FEAT_TYPE_NAME,
                this.getDataset().getHydroNetwork(), PeaRepDbType.VMAP1);
          }

          if (layerName.equals("watrcrsa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "hydro");
            loadPolygonClass(
                FileUtil.getNamedFileInDir(shapePath, "watrcrsa.shp")
                    .getAbsolutePath(), VMAPWaterArea.class,
                CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP1);
          }

          if (layerName.equals("lakeresa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "hydro");
            loadPolygonClass(
                FileUtil.getNamedFileInDir(shapePath, "lakeresa.shp")
                    .getAbsolutePath(), VMAPWaterArea.class,
                CartAGenDataSet.WATER_AREAS_POP, IWaterArea.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP1);
          }

          if (layerName.equals("contourl")) {
            // elevation loading
            shapePath = FileUtil.getNamedFileInDir(directory, "elev");
            loadLineStringClass(
                FileUtil.getNamedFileInDir(shapePath, "contourl.shp")
                    .getAbsolutePath(), VMAPContourLine.class,
                CartAGenDataSet.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME,
                null, PeaRepDbType.VMAP1);
          }

          if (layerName.equals("buildp")) {
            // population loading
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPointClass(FileUtil.getNamedFileInDir(shapePath, "buildp.shp")
                .getAbsolutePath(), VMAPBuildPoint.class,
                CartAGenDataSet.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
                PeaRepDbType.VMAP1);
          }

          if (layerName.equals("builda")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPolygonClass(FileUtil
                .getNamedFileInDir(shapePath, "builda.shp").getAbsolutePath(),
                VMAPBuilding.class, CartAGenDataSet.BUILDINGS_POP,
                IBuilding.FEAT_TYPE_NAME, PeaRepDbType.VMAP1);
          }

          // builtuparea loading
          if (layerName.equals("builtupa")) {
            shapePath = FileUtil.getNamedFileInDir(directory, "pop");
            loadPolygonClass(
                FileUtil.getNamedFileInDir(shapePath, "builtupa.shp")
                    .getAbsolutePath(), VMAPBuiltUpArea.class,
                CartAGenDataSet.LANDUSE_AREAS_POP,
                ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1);
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

  public void createNewDb(SymbolGroup group, String name) {
    // create the new CartAGen dataset
    PeaRepDB database = new PeaRepDB(name);
    database.setSourceDLM(SourceDLM.VMAP1);
    database.setSymboScale(500000);
    database.setDocument(CartAGenDoc.getInstance());
    CartAGenDataSet dataset = new PeaRepDataset();
    dataset.setSymbols(SymbolList.getSymbolList(group));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
  }

}
