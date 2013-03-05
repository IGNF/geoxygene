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

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPAerofacP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBluffL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBridgeC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBridgeL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuildA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuildP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuiltUpA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPBuiltUpP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCoastA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCommP;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPContourL;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPCropA;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDamC;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPDamL;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.util.FileUtil;

public class VMAP1PPLoader extends ShapeFileLoader {

  /**
   * Default constructor
   * @param dataset
   */
  public VMAP1PPLoader(SymbolGroup symbGroup, String dbName) {
    this.createNewDb(symbGroup, dbName);
    this.setProjEpsg("32629");
  }

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {

      // Chargement dans l'ordre alphabétique (JFG)

      // Airport Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("AerofacP"))) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "AerofacP.shp")
                .getAbsolutePath(), VMAP1PPAerofacP.class,
            CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Grain Bin / Silo Point loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("AgristrP"))) {
      // this.loadPointClass(
      // FileUtil.getNamedFileInDir(directory, "AgristrP.shp")
      // .getAbsolutePath(), VMAP1PPAgristrP.class,
      // CartAGenDataSet.POI_POP, IPointOfInterest.FEAT_TYPE_NAME,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

      // Aquedec Line loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("AquedctL"))) {
      // this.loadLineStringClass(
      // FileUtil.getNamedFileInDir(directory, "AquedctL.shp")
      // .getAbsolutePath(), VMAP1PPAquedctL.class,
      // CartAGenDataSet.POI_POP, IBridgeLine.FEAT_TYPE_NAME, null,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

      // Bluff / Cliff Line loading
      if ((listLayer.size() == 0) || (listLayer.contains("BluffL"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BluffL.shp")
                .getAbsolutePath(), VMAP1PPBluffL.class,
            CartAGenDataSet.RELIEF_LINES_POP,
            IReliefElementLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("BridgeC"))) {
        this.loadPointClass(FileUtil
            .getNamedFileInDir(directory, "BridgeC.shp").getAbsolutePath(),
            VMAP1PPBridgeC.class, PeaRepDataset.BRIDGE_POINT_POP,
            IBridgePoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Bridge Line loading
      if ((listLayer.size() == 0) || (listLayer.contains("BridgeL"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "BridgeL.shp")
                .getAbsolutePath(), VMAP1PPBridgeL.class,
            PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Buid Area loading
      if ((listLayer.size() == 0) || (listLayer.contains("BuildA"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuildA.shp")
                .getAbsolutePath(), VMAP1PPBuildA.class,
            PeaRepDataset.BUILD_AREA_POP, IBuildArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Build Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("BuildP"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "BuildP.shp")
            .getAbsolutePath(), VMAP1PPBuildP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Area loading
      if ((listLayer.size() == 0) || (listLayer.contains("BuiltupA"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupA.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpA.class,
            PeaRepDataset.LANDUSE_AREAS_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Built-up Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("BuiltupP"))) {
        this.loadPointClass(
            FileUtil.getNamedFileInDir(directory, "BuiltupP.shp")
                .getAbsolutePath(), VMAP1PPBuiltUpP.class,
            PeaRepDataset.BUILD_PT_POP, IBuildPoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Island Area loading
      if ((listLayer.size() == 0) || (listLayer.contains("CoastA"))) {
        this.loadPolygonClass(
            FileUtil.getNamedFileInDir(directory, "CoastA.shp")
                .getAbsolutePath(), VMAP1PPCoastA.class,
            PeaRepDataset.WATER_ISLAND_POP, ISimpleLandUseArea.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Coast Line loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("CoastL"))) {
      // this.loadLineStringClass(
      // FileUtil.getNamedFileInDir(directory, "CoastL.shp")
      // .getAbsolutePath(), VMAP1PPCoastL.class,
      // PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME, null,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

      // Radar (Communication) Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("CommP"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "CommP.shp")
            .getAbsolutePath(), VMAP1PPCommP.class, PeaRepDataset.MISC_PT_POP,
            IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Contour Line loading
      if ((listLayer.size() == 0) || (listLayer.contains("ContourL"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "ContourL.shp")
                .getAbsolutePath(), VMAP1PPContourL.class,
            PeaRepDataset.CONTOUR_LINES_POP, IContourLine.FEAT_TYPE_NAME, null,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Crop Line loading
      if ((listLayer.size() == 0) || (listLayer.contains("CropA"))) {
        this.loadPolygonClass(FileUtil
            .getNamedFileInDir(directory, "CropA.shp").getAbsolutePath(),
            VMAP1PPCropA.class, PeaRepDataset.MISC_AREA_POP,
            ISimpleLandUseArea.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Point loading
      if ((listLayer.size() == 0) || (listLayer.contains("DamC"))) {
        this.loadPointClass(FileUtil.getNamedFileInDir(directory, "DamC.shp")
            .getAbsolutePath(), VMAP1PPDamC.class,
            PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME,
            PeaRepDbType.VMAP1PlusPlus);
      }

      // Dam Line loading
      if ((listLayer.size() == 0) || (listLayer.contains("DamL"))) {
        this.loadLineStringClass(
            FileUtil.getNamedFileInDir(directory, "DamL.shp").getAbsolutePath(),
            VMAP1PPDamL.class, PeaRepDataset.BRIDGE_LINE_POP,
            IBridgeLine.FEAT_TYPE_NAME, null, PeaRepDbType.VMAP1PlusPlus);
      }

      // Danger (Reef, Island, Rock) Area loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("DangerA"))) {
      // this.loadPolygonClass(
      // FileUtil.getNamedFileInDir(directory, "DangerA.shp")
      // .getAbsolutePath(), VMAP1PPDangerA.class,
      // PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

      // Danger (Reef, Island, Rock) Line loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("DangerL"))) {
      // this.loadLineStringClass(
      // FileUtil.getNamedFileInDir(directory, "DangerL.shp")
      // .getAbsolutePath(), VMAP1PPDangerL.class,
      // PeaRepDataset.MISC_LINE_POP, IMiscLine.FEAT_TYPE_NAME, null,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

      // Danger (Reef, Island, Rock) Point loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("DangerP"))) {
      // this.loadPointClass(FileUtil
      // .getNamedFileInDir(directory, "DangerP.shp").getAbsolutePath(),
      // VMAP1PPDangerP.class, PeaRepDataset.MISC_PT_POP,
      // IMiscPoint.FEAT_TYPE_NAME, PeaRepDbType.VMAP1PlusPlus);
      // }

      // Dispose Area loading (!!! vide)
      // if ((listLayer.size() == 0) || (listLayer.contains("DisposeA"))) {
      // this.loadPolygonClass(
      // FileUtil.getNamedFileInDir(directory, "DisposeA.shp")
      // .getAbsolutePath(), VMAP1PPDisposeA.class,
      // PeaRepDataset.MISC_AREA_POP, IMiscArea.FEAT_TYPE_NAME,
      // PeaRepDbType.VMAP1PlusPlus);
      // }

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
  }
}
