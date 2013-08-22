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

import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMClayArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMClaySiltArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMFeature;
import fr.ign.cogit.cartagen.pearep.shom.SHOMFineSandArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMFineSandPebbleArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMFineSandSiltArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMFineSandVaseArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMGravelArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMGravelPebbleArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMGravelSandArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMGravelVaseArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMPebbleArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMPebbleGravelArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMPebbleSandArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMPebbleVaseArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMRockArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSandArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSandGravelArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSandSiltArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSandVaseArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSchemaFactory;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSiltArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSiltClayArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMVaseArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMVaseFineSandArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMVaseGravelArea;
import fr.ign.cogit.cartagen.pearep.shom.SHOMVaseSandArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
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

public class SHOMLoader extends ShapeFileLoader {

  private boolean loadLand = true;
  private IPolygon envelopeTotale;
  private List<IPolygon> cellsGrid;

  private String nameLayer = "Iroise_sedim_extract";
  private String nameShp = "Iroise_sedim_extract.shp";

  /**
   * Default constructor
   * @param dataset
   */
  public SHOMLoader(SymbolGroup symbGroup, String dbName) {
    this.createNewDb(symbGroup, dbName);
  }

  /**
   * Default constructor
   */
  public SHOMLoader() {
  }

  /**
   * Default constructor
   */
  public SHOMLoader(boolean loadLand) {
    this.loadLand = loadLand;
  }

  public void computeEnvelopeTotale(File directory, List<String> listLayer)
      throws ShapefileException, IOException {

    IFeatureCollection<IFeature> ftColPolyEnvelope = new FT_FeatureCollection<IFeature>();

    if (((listLayer.size() == 0) || (listLayer.contains(nameLayer)))
        && (FileUtil.getNamedFileInDir(directory, nameShp) != null)) {
      // rock area
      ShpFiles shpf = new ShpFiles(FileUtil.getNamedFileInDir(directory,
          nameShp));
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      IPolygon envelope = computeEnvelope(shr);
      ftColPolyEnvelope.add(new DefaultFeature(envelope));
    }

    IPolygon envelopeTotaleSHOM = new GM_Polygon(
        ftColPolyEnvelope.getEnvelope());
    this.setEnvelopeTotale(envelopeTotaleSHOM);
  }

  @Override
  public void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException {
    try {

      // Chargement dans l'ordre alphabétique (JFG)

      if (((listLayer.size() == 0) || (listLayer.contains(nameLayer)))
          && (FileUtil.getNamedFileInDir(directory, nameShp) != null)) {
        // rock area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMRockArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFRoche");

        // pebble area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFC");
        // pebble - gravel area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMPebbleGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCG");
        // pebble - sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMPebbleSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCS");
        // pebble - vase area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMPebbleVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCV");

        // gravel area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFG");
        // gravel - pebble area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMGravelPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGC");
        // gravel - sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMGravelSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGS");
        // gravel - vase area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMGravelVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGV");

        // sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFS");
        // sand - gravel area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSandGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSG");

        // vase area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFV");
        // vase - gravel area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMVaseGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVG");
        // vase - sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMVaseSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVS");
        // vase - fine sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMVaseFineSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVSF");

        // silt - clay area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSiltClayArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSiA");
        // clay - silt area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMClaySiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFASi");
        // silt area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSi");
        // clay area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMClayArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFA");

        // sand - vase area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSandVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSV");
        // sand - silt area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMSandSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSSi");

        // fine sand area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMFineSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSF");
        // fine sand - pebble area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMFineSandPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFC");
        // fine sand - vase area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMFineSandVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFV");
        // fine sand - silt area
        this.loadSedimentologyClass(
            FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
            SHOMFineSandSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
            IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFSi");

        // // undefined area
        // this.loadSedimentologyClass(
        // FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
        // SHOMUndefinedArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
        // IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSTF");
        //
        // if (loadLand) {
        // // land area
        // this.loadSedimentologyClass(
        // FileUtil.getNamedFileInDir(directory, pathLayer)
        // .getAbsolutePath(), SHOMLandArea.class,
        // CartAGenDataSet.LANDUSE_AREAS_POP, IMiscArea.FEAT_TYPE_NAME,
        // PeaRepDbType.SHOM, "NFTC");
        // }

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

  public void createNewDb(SymbolGroup symbGroup, String name) {
    // create the new CartAGen dataset
    SHOMDB database = new SHOMDB(name);
    database.setSourceDLM(SourceDLM.SHOM);
    database.setSymboScale(50000);
    database.setDocument(CartAGenDoc.getInstance());
    CartAGenDataSet dataset = new PeaRepDataset();
    dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
    CartAGenDoc.getInstance().addDatabase(name, database);
    CartAGenDoc.getInstance().setCurrentDataset(dataset);
    database.setDataSet(dataset);
    database.setType(new DigitalLandscapeModel());
    this.setDataset(dataset);
    database.setGeneObjImpl(new GeneObjImplementation("shom", SHOMFeature.class
        .getPackage(), SHOMFeature.class, new SHOMSchemaFactory()));
  }

  @Override
  public void loadDataPartition(File directory, List<String> listLayer,
      IPolygon partition) throws ShapefileException, IOException, Exception {
    // TODO Auto-generated method stub

    // Chargement dans l'ordre alphabétique (JFG)

    if (((listLayer.size() == 0) || (listLayer.contains(nameLayer)))
        && (FileUtil.getNamedFileInDir(directory, nameShp) != null)) {
      // rock area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMRockArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFRoche", partition);

      // pebble area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFC", partition);
      // pebble - gravel area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMPebbleGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCG", partition);
      // pebble - sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMPebbleSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCS", partition);
      // pebble - vase area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMPebbleVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFCV", partition);

      // gravel area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFG", partition);
      // gravel - pebble area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMGravelPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGC", partition);
      // gravel - sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMGravelSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGS", partition);
      // gravel - vase area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMGravelVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFGV", partition);

      // sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFS", partition);
      // sand - gravel area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSandGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSG", partition);

      // vase area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFV", partition);
      // vase - gravel area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMVaseGravelArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVG", partition);
      // vase - sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMVaseSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVS", partition);
      // vase - fine sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMVaseFineSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFVSF", partition);

      // silt - clay area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSiltClayArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSiA", partition);
      // clay - silt area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMClaySiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFASi", partition);
      // silt area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSi", partition);
      // clay area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMClayArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFA", partition);

      // sand - vase area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSandVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSV", partition);
      // sand - silt area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMSandSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSSi", partition);

      // fine sand area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMFineSandArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSF", partition);
      // fine sand - pebble area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMFineSandPebbleArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFC", partition);
      // fine sand - vase area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMFineSandVaseArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFV", partition);
      // fine sand - silt area
      this.loadSedimentologyClassPartition(
          FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
          SHOMFineSandSiltArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
          IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSFSi", partition);

      // // undefined area
      // this.loadSedimentologyClassPartition(
      // FileUtil.getNamedFileInDir(directory, nameShp).getAbsolutePath(),
      // SHOMUndefinedArea.class, CartAGenDataSet.LANDUSE_AREAS_POP,
      // IMiscArea.FEAT_TYPE_NAME, PeaRepDbType.SHOM, "NFSTF", partition);
      //
      // if (loadLand) {
      // // land area
      // this.loadSedimentologyClassPartition(
      // FileUtil.getNamedFileInDir(directory, pathLayer)
      // .getAbsolutePath(), SHOMLandArea.class,
      // CartAGenDataSet.LANDUSE_AREAS_POP, IMiscArea.FEAT_TYPE_NAME,
      // PeaRepDbType.SHOM, "NFTC", partition);
      // }

    }

  }

  public List<IPolygon> getCellsGrid() {
    return cellsGrid;
  }

  public void setCellsGrid(List<IPolygon> cellsGrid) {
    this.cellsGrid = cellsGrid;
  }

  public IPolygon getEnvelopeTotale() {
    return envelopeTotale;
  }

  public void setEnvelopeTotale(IPolygon envelopeTotale) {
    this.envelopeTotale = envelopeTotale;
  }

  public String getNameLayer() {
    return nameLayer;
  }

  public void setNameLayer(String nameLayer) {
    this.nameLayer = nameLayer;
  }

  public String getNameShp() {
    return nameShp;
  }

  public void setNameShp(String nameShp) {
    this.nameShp = nameShp;
  }

}
