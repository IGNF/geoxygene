package fr.ign.cogit.cartagen.software.dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLineWithAttributes;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea.WaterAreaNature;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint.LabelCategory;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery.CemeteryType;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField.SportsFieldType;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationLeftPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.RoadSymbolResult;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.TronconFerreImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.TronconHydrographiqueImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.CourbeDeNiveauImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.PointCoteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class ShapeFileLoader {

  private static Logger logger = Logger
      .getLogger(ShapeFileLoader.class.getName());

  // ///////////////////////////////////////
  // Buildings
  // ///////////////////////////////////////

  /**
   * Charge des batiments depuis un shapefile surfacique
   * @param chemin
   * @throws IOException
   */
  public static boolean loadBuildingsFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IBuilding> buildPop = dataset.getBuildings();

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
      if (fields.containsKey("NATURE")) {
        nature = (String) fields.get("NATURE");
      }
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPolygon) {
        IBuilding building = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createBuilding(new BatimentImpl(geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          building.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          building.setShapeId(j);
        }
        building.setId(dataset.getBuildings().size() + 1);
        building.setNature(nature);
        buildPop.add(building);

      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IBuilding building = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createBuilding(
                  new BatimentImpl(((IMultiSurface<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            building.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            building.setShapeId(j);
          }
          building.setId(dataset.getBuildings().size() + 1);
          building.setNature(nature);
          buildPop.add(building);
        }

      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des batiments depuis un shapefile surfacique
   * @param chemin
   * @throws IOException
   */
  public static boolean loadBuildingsFromSHP(String chemin,
      CartAGenDataSet dataset, String natureName) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    if (!chemin.endsWith(".shp"))
      chemin = chemin + ".shp";
    try {
      ShpFiles shpf = new ShpFiles(chemin);
      System.out.println(chemin);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      System.out.println("file not found");
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IBuilding> buildPop = dataset.getBuildings();

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
      if (fields.containsKey(natureName)) {
        nature = (String) fields.get(natureName);
      }
      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPolygon) {
        IBuilding building = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createBuilding(new BatimentImpl(geom));
        building.setId(dataset.getBuildings().size() + 1);
        building.setNature(nature);
        buildPop.add(building);

      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IBuilding building = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createBuilding(
                  new BatimentImpl(((IMultiSurface<?>) geom).get(i)));
          building.setId(dataset.getBuildings().size() + 1);
          building.setNature(nature);
          buildPop.add(building);
        }

      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des batiments depuis un shapefile surfacique
   * @param chemin
   * @throws IOException
   */
  public static boolean overwriteBuildingsFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IBuilding> buildPop = dataset.getBuildings();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

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
  public static boolean loadRoadLinesFromSHP(String chemin, SourceDLM sourceDlm,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    if (sourceDlm.equals(SourceDLM.BD_TOPO_V2)) {
      return loadRoadLinesShapeFile(chemin, sourceDlm, symbols, dataset);
    }
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IRoadLine> pop = dataset.getRoads();
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
      if (geom == null) {
        continue;
      }
      if (geom instanceof ILineString) {
        IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createRoadLine(new TronconDeRouteImpl(
                (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                (ILineString) geom), importance, result.symbolId);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        dataset.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createRoadLine(new TronconDeRouteImpl(
                  (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)), importance);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          dataset.getRoadNetwork().addSection(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
   * troncon applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public static boolean overwriteRoadLinesFromSHP(String chemin, double doug,
      SourceDLM sourceDlm, SymbolList symbols, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IRoadLine> pop = dataset.getRoads();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
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
  public static boolean loadRoadLinesFromSHPBasic(String chemin,
      CartAGenDataSet dataset) throws IOException {
    return loadRoadLinesFromSHPBasic(chemin, 0, dataset);
  }

  /**
   * generic road loader
   * @param chemin
   * @param SymbolId
   * @return
   */

  public static boolean loadRoadLinesShapeFile(String chemin,
      SourceDLM sourceDlm, SymbolList symbols, CartAGenDataSet dataset)
      throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    FeatureType ft = new FeatureType();
    // ft.setSchema(schema);

    // Code récupéré de GeOxygene lecture SHP: recupere noms et types des
    // attributs

    for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {

      AttributeType att = new AttributeType();

      att.setMemberName(dbr.getHeader().getFieldName(i));
      att.setNomField(dbr.getHeader().getFieldName(i));
      att.setValueType("String");

      ft.addFeatureAttribute(att);

    }

    IPopulation<IRoadLine> pop = dataset.getRoads();

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
        IRoadLine tr = null;

        if (sourceDlm != SourceDLM.BD_TOPO_V2)
          tr = dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory()
              .createRoadLine(new TronconDeRouteImpl(
                  (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                  (ILineString) geom), importance, result.symbolId);
        else {

          tr = new RoadLineWithAttributes(

              new TronconDeRouteImpl(
                  (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                  (ILineString) geom),
              result.importance, result.symbolId);
          tr.setFeatureType(ft);

          for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
            tr.setAttribute(ft.getFeatureAttributeI(i), champs[i]);
          }

          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }

          pop.add(tr);
          dataset.getRoadNetwork().addSection(tr);
        }

        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        dataset.getRoadNetwork().addSection(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {

          IRoadLine tr = null;
          if (sourceDlm != SourceDLM.BD_TOPO_V2)
            tr = dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory()
                .createRoadLine(
                    new TronconDeRouteImpl(
                        (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                        (ILineString) ((IMultiCurve<?>) geom).get(i)),
                    importance);

          else {

            tr = new RoadLineWithAttributes(
                new TronconDeRouteImpl((Reseau) dataset.getRoadNetwork()

                    .getGeoxObj(), false,
                    (ILineString) ((IMultiCurve<?>) geom).get(i)),
                result.importance, result.symbolId);

            tr.setFeatureType(ft);
            for (int k = 0; k < dbr.getHeader().getNumFields(); k++) {
              tr.setAttribute(ft.getFeatureAttributeI(k), champs[k]);
            }

          }

          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          dataset.getRoadNetwork().addSection(tr);
        }

      }

      else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
   * troncon applique un filtre de dp a chaque geometrie
   * @param chemin
   * @param doug
   * @throws IOException
   */
  public static boolean loadRoadLinesFromSHPBasic(String chemin, int SymbolId,
      CartAGenDataSet dataset) throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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

        IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createRoadLine(
                new TronconDeRouteImpl((Reseau) dataset.getRoadNetwork(), false,
                    (ILineString) geom),
                4, SymbolId);

        dataset.getRoads().add(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {

          IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createRoadLine(new TronconDeRouteImpl(
                  (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)), 4, SymbolId);

          dataset.getRoads().add(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

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
  public static boolean loadPathsBDTopoV2_25FromSHP(String chemin,
      SourceDLM sourceDlm, SymbolList symbols, CartAGenDataSet dataset)
      throws IOException {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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

    IPopulation<IPathLine> popPaths = dataset.getPaths();

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
        IPathLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createPath((ILineString) geom, result.importance, result.symbolId);

        popPaths.add(tr);

      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IPathLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createPath((ILineString) ((IMultiCurve<?>) geom).get(i),
                  result.importance, result.symbolId);

          popPaths.add(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

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
  public static boolean loadWaterLinesFromSHP(String chemin, SymbolList symbols,
      CartAGenDataSet dataset) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("Loading: " + IWaterLine.class.getSimpleName());
    }
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterLine> pop = dataset.getWaterLines();

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
      if (geom == null) {
        continue;
      } else if (geom instanceof ILineString) {
        IWaterLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createWaterLine(new TronconHydrographiqueImpl(
                (Reseau) dataset.getHydroNetwork().getGeoxObj(), false,
                (ILineString) geom), 0);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        dataset.getHydroNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IWaterLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createWaterLine(new TronconHydrographiqueImpl(
                  (Reseau) dataset.getHydroNetwork().getGeoxObj(), false,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          dataset.getHydroNetwork().addSection(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

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
  public static boolean overwriteWaterLinesFromSHP(String chemin, double doug,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    if (logger.isDebugEnabled()) {
      logger.debug("Loading: " + IWaterLine.class.getSimpleName());
    }
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterLine> pop = dataset.getWaterLines();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
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
  public static boolean loadWaterAreasFromSHP(String chemin, SymbolList symbols,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterArea> pop = dataset.getWaterAreas();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }

      WaterAreaNature nature = WaterAreaNature.UNKNOWN;
      if (fields.containsKey("NATURE")) {
        String value = (String) fields.get("NATURE");
        if (value.equals("Riviere"))
          nature = WaterAreaNature.RIVER;
        else if (value.equals("Bassin"))
          nature = WaterAreaNature.LAKE;
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      if (geom == null) {
        continue;
      } else if (geom instanceof IPolygon) {
        IWaterArea surf = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createWaterArea((IPolygon) geom, nature);
        if (fields.containsKey("CARTAGEN_ID")) {
          surf.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          surf.setShapeId(j);
        }
        pop.add(surf);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IPolygon polygon = (IPolygon) ((IMultiSurface<?>) geom).get(i);
          IWaterArea surf = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createWaterArea(polygon, nature);
          if (fields.containsKey("CARTAGEN_ID")) {
            surf.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            surf.setShapeId(j);
          }
          pop.add(surf);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
  public static boolean overwriteWaterAreasFromSHP(String chemin, double doug,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IWaterArea> pop = dataset.getWaterAreas();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

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
  public static boolean loadRailwayLineFromSHP(String chemin,
      SymbolList symbols, String sidetrackField, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    String sidetrackValue = null;
    if (chemin.contains(".shp"))
      chemin = chemin.substring(0, chemin.length() - 4);
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IRailwayLine> pop = dataset.getRailwayLines();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
        if (sidetrackField != null) {
          if (dbr.getHeader().getFieldName(i).equals(sidetrackField)) {
            if (champs[i] instanceof Boolean) {
              if ((Boolean) champs[i] == false)
                sidetrackValue = "false";
              else
                sidetrackValue = "true";
            } else if (champs[i] instanceof String)
              sidetrackValue = (String) champs[i];
            else if (champs[i] instanceof Integer) {
              if ((Integer) champs[i] == 0)
                sidetrackValue = "false";
              else
                sidetrackValue = "true";
            }
          }
        }
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof ILineString) {
        IRailwayLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createRailwayLine(new TronconFerreImpl(
                (Reseau) dataset.getRailwayNetwork().getGeoxObj(), false,
                (ILineString) geom), 0);
        if (sidetrackValue != null)
          tr.setSidetrack(new Boolean(sidetrackValue));
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        dataset.getRailwayNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IRailwayLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createRailwayLine(new TronconFerreImpl(
                  (Reseau) dataset.getRailwayNetwork().getGeoxObj(), false,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (sidetrackValue != null)
            tr.setSidetrack(new Boolean(sidetrackValue));
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          dataset.getRailwayNetwork().addSection(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

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
  public static boolean overwriteRailwayLineFromSHP(String chemin, double doug,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IRailwayLine> pop = dataset.getRailwayLines();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

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
  public static boolean loadElectricityLinesFromSHP(String chemin,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IElectricityLine> pop = dataset.getElectricityLines();

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
        IElectricityLine tr = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createElectricityLine(new ArcReseauImpl(
                (Reseau) dataset.getElectricityNetwork().getGeoxObj(), false,
                (ILineString) geom), 0);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
        dataset.getElectricityNetwork().addSection(tr);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IElectricityLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createElectricityLine(new ArcReseauImpl(
                  (Reseau) dataset.getElectricityNetwork().getGeoxObj(), false,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)), 0);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
          dataset.getElectricityNetwork().addSection(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

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
  public static boolean overwriteElectricityLinesFromSHP(String chemin,
      double doug, SymbolList symbols, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IElectricityLine> pop = dataset.getElectricityLines();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
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
  public static boolean loadContourLinesFromSHP(String chemin,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IContourLine> pop = dataset.getContourLines();

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
        logger.debug("No altitude attached to the contour line");
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
        IContourLine cn = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createContourLine(new CourbeDeNiveauImpl(
                dataset.getReliefField().getChampContinu(), z,
                (ILineString) geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          cn.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          cn.setShapeId(j);
        }
        pop.add(cn);
        dataset.getReliefField().addContourLine(cn);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IContourLine cn = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createContourLine(new CourbeDeNiveauImpl(
                  dataset.getReliefField().getChampContinu(), z,
                  (ILineString) ((IMultiCurve<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            cn.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            cn.setShapeId(j);
          }
          pop.add(cn);
          dataset.getReliefField().addContourLine(cn);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
  public static boolean overwriteContourLinesFromSHP(String chemin, double doug,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IContourLine> pop = dataset.getContourLines();

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
        logger.debug("No altitude attached to the contour line");
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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
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
  public static boolean loadReliefElementLinesFromSHP(String chemin,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IReliefElementLine> pop = dataset.getReliefLines();

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
        IReliefElementLine line = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory()
            .createReliefElementLine(new ElementCaracteristiqueDuReliefImpl(
                dataset.getReliefField().getChampContinu(), geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          line.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          line.setShapeId(j);
        }
        pop.add(line);
        dataset.getReliefField().addReliefElementLine(line);
      } else if (geom instanceof IMultiCurve<?>) {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IReliefElementLine line = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createReliefElementLine(new ElementCaracteristiqueDuReliefImpl(
                  dataset.getReliefField().getChampContinu(),
                  ((IMultiCurve<?>) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            line.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            line.setShapeId(j);
          }
          pop.add(line);
          dataset.getReliefField().addReliefElementLine(line);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
  public static boolean overwriteReliefElementLinesFromSHP(String chemin,
      double doug, SymbolList symbols, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IReliefElementLine> pop = dataset.getReliefLines();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
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
  public static boolean loadSpotHeightsFromSHP(String chemin,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ISpotHeight> pop = dataset.getSpotHeights();

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
        ISpotHeight pt = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createSpotHeight(new PointCoteImpl(
                dataset.getReliefField().getChampContinu(), z, (IPoint) geom));
        if (fields.containsKey("CARTAGEN_ID")) {
          pt.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          pt.setShapeId(j);
        }
        pop.add(pt);
        dataset.getReliefField().addSpotHeight(pt);
      } else if (geom instanceof IMultiPoint) {
        for (int i = 0; i < ((IMultiPoint) geom).size(); i++) {
          ISpotHeight pt = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createSpotHeight(
                  new PointCoteImpl(dataset.getReliefField().getChampContinu(),
                      z, ((IMultiPoint) geom).get(i)));
          if (fields.containsKey("CARTAGEN_ID")) {
            pt.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            pt.setShapeId(j);
          }
          pop.add(pt);
          dataset.getReliefField().addSpotHeight(pt);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
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
  public static boolean overwriteSpotHeightsFromSHP(String chemin,
      SymbolList symbols, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ISpotHeight> pop = dataset.getSpotHeights();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge un MNT grille a partir d'un fichier au format xyz
   * @param chemin
   */
  public static boolean loadDEMPixelsFromSHP(String chemin,
      CartAGenDataSet dataset) {

    FileReader fr = null;
    try {
      fr = new FileReader(chemin + ".xyz");
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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

        IDEMPixel pix = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createDEMPixel(x, y, z);
        dataset.getDEMPixels().add(pix);
        dataset.getReliefField().addDEMPixel(pix);

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
  public static boolean loadMaskFromSHP(String chemin, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IMask> pop = dataset.getMasks();

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
        IMask tr = dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory()
            .createMask(new GM_LineString(geom.coord()));
        // CartagenApplication.getInstance().getFrame().getLayerManager()
        // .addMasque(tr);
        if (fields.containsKey("CARTAGEN_ID")) {
          tr.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          tr.setShapeId(j);
        }
        pop.add(tr);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IMask tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createMask(
                  new GM_LineString(((IMultiSurface<?>) geom).get(i).coord()));
          // CartagenApplication.getInstance().getFrame().getLayerManager()
          // .addMasque(tr);
          if (fields.containsKey("CARTAGEN_ID")) {
            tr.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            tr.setShapeId(j);
          }
          pop.add(tr);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge masque depuis un shapefile surfacique.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public static boolean overwriteMaskFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IMask> pop = dataset.getMasks();

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
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();
    return true;
  }

  /**
   * Charge zones occ sol depuis un shapefile surfacique. applique un filtre de
   * dp a chaque geometrie
   * @param chemin chemin du shapefile
   * @param dp seuil utilisé par l'algorithme de DouglasPeucker
   * @throws IOException
   */
  public static boolean loadLandUseAreasFromSHP(String chemin, double dp,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ISimpleLandUseArea> pop = dataset.getLandUseAreas();

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
        ISimpleLandUseArea area = dataset.getCartAGenDB().getGeneObjImpl()
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
          ISimpleLandUseArea area = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createSimpleLandUseArea((IPolygon) CommonAlgorithms
                  .filtreDouglasPeucker(((IMultiSurface<?>) geom).get(i), dp),
                  type);
          if (fields.containsKey("CARTAGEN_ID")) {
            area.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            area.setShapeId(j);
          }
          pop.add(area);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

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
  public static boolean loadLandUseAreasFromSHP(String chemin, double dp,
      int type, CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ISimpleLandUseArea> pop = dataset.getLandUseAreas();

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
        ISimpleLandUseArea area = dataset.getCartAGenDB().getGeneObjImpl()
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
          ISimpleLandUseArea area = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createSimpleLandUseArea((IPolygon) CommonAlgorithms
                  .filtreDouglasPeucker(((IMultiSurface<?>) geom).get(i), dp),
                  type);
          if (fields.containsKey("CARTAGEN_ID")) {
            area.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            area.setShapeId(j);
          }
          pop.add(area);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge les zones administratives depuis un shapefile.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public static boolean loadAdminAreasFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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
        logger.error("a revoir");
      } else if (geom instanceof IMultiSurface<?>) {
        logger.error("a revoir");
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge les cimetières depuis un shapefile BD TOPO.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public static boolean loadCemeteriesBDTFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ICemetery> pop = dataset.getCemeteries();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      CemeteryType type = CemeteryType.UNKNOWN;
      Object value = fields.get("NATURE");
      if (value != null) {
        String typeValue = (String) value;
        if (typeValue.equals("Militaire"))
          type = CemeteryType.MILITARY;
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      if (geom instanceof IPolygon) {
        ICemetery cemetery = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createCemetery((IPolygon) geom, type);
        if (fields.containsKey("CARTAGEN_ID")) {
          cemetery.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          cemetery.setShapeId(j);
        }
        j++;
        pop.add(cemetery);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          ICemetery cemetery = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createCemetery(
                  (IPolygon) ((IMultiSurface<?>) geom).get(i), type);
          if (fields.containsKey("CARTAGEN_ID")) {
            cemetery.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            cemetery.setShapeId(j);
          }
          j++;
          pop.add(cemetery);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge les terrains de sport depuis un shapefile BD TOPO.
   * @param chemin chemin du shapefile
   * @throws IOException
   */
  public static boolean loadSportsFieldsBDTFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ISportsField> pop = dataset.getSportsFields();

    int j = 0;
    while (shr.hasNext() && dbr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      SportsFieldType type = SportsFieldType.UNKNOWN;
      Object value = fields.get("NATURE");
      if (value != null) {
        String typeValue = (String) value;
        if (typeValue.equals("Bassin de natation"))
          type = SportsFieldType.SWIMMINGPOOL;
        if (typeValue.equals("Terrain de tennis"))
          type = SportsFieldType.TENNIS;
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      if (geom instanceof IPolygon) {
        ISportsField field = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createSportsField((IPolygon) geom, type);
        if (fields.containsKey("CARTAGEN_ID")) {
          field.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          field.setShapeId(j);
        }
        j++;
        pop.add(field);
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          ISportsField field = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createSportsField(
                  (IPolygon) ((IMultiSurface<?>) geom).get(i), type);
          if (fields.containsKey("CARTAGEN_ID")) {
            field.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            field.setShapeId(j);
          }
          j++;
          pop.add(field);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    dbr.close();

    return true;
  }

  public static void loadOSCyclePath(String chemin, int symbolID,
      CartAGenDataSet dataset) {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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
          IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createRoadLine(
                  new TronconDeRouteImpl((Reseau) dataset.getRoadNetwork(),
                      false, (ILineString) geom),
                  4, symbolID);
          tr.setId(id);
          dataset.getRoads().add(tr);

        } else if (geom instanceof IMultiCurve<?>) {
          for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
            IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
                .getCreationFactory()
                .createRoadLine(new TronconDeRouteImpl(
                    (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                    (ILineString) ((IMultiCurve<?>) geom).get(i)), 4, symbolID);
            tr.setId(id);
            dataset.getRoads().add(tr);
          }
        } else {
          logger.error("ERREUR lors du chargement de shp " + chemin
              + ". Type de geometrie " + geom.getClass().getName()
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

  }

  public static void loadOSRoads(String chemin, int symbolID,
      String IDAtribbutName, CartAGenDataSet dataset) {

    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
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
          IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory().createRoadLine(
                  new TronconDeRouteImpl((Reseau) dataset.getRoadNetwork(),
                      false, (ILineString) geom),
                  4, symbolID);

          tr.setAttribute(new AttributeType("ID", "String"), key);
          dataset.getRoads().add(tr);

        } else if (geom instanceof IMultiCurve<?>) {
          for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
            IRoadLine tr = dataset.getCartAGenDB().getGeneObjImpl()
                .getCreationFactory()
                .createRoadLine(new TronconDeRouteImpl(
                    (Reseau) dataset.getRoadNetwork().getGeoxObj(), false,
                    (ILineString) ((IMultiCurve<?>) geom).get(i)), 4, symbolID);
            // tr.setAttribute(new AttributeType("KEY","KEY"), key);
            dataset.getRoads().add(tr);
          }
        } else {
          logger.error("ERREUR lors du chargement de shp " + chemin
              + ". Type de geometrie " + geom.getClass().getName()
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

  }

  // ///////////////////////////////////////
  // Label Points
  // ///////////////////////////////////////

  /**
   * Charge des toponymes depuis un shapefile.
   * @param chemin
   * @throws IOException
   */
  public static boolean loadLabelPointsFromSHP(String chemin,
      CartAGenDataSet dataset, LabelCategory category) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(chemin + ".shp");
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ILabelPoint> pop = dataset.getLabelPoints();

    int j = 0;
    while (shr.hasNext()) {
      Record objet = shr.nextRecord();

      Object[] champs = dbr.readEntry();
      Map<String, Object> fields = new HashMap<String, Object>();
      for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
        fields.put(dbr.getHeader().getFieldName(i), champs[i]);
      }
      // get the nature
      String nature = "Indifferencie";
      if (fields.containsKey("NATURE")) {
        nature = (String) fields.get("NATURE");
      }
      // get the name attribute
      String name = "";
      if (fields.containsKey("NOM")) {
        name = (String) fields.get("NOM");
      }
      // get the importance attribute
      String importance = "8";
      if (fields.containsKey("IMPORTANCE")) {
        importance = (String) fields.get("IMPORTANCE");
      }

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

      if (geom instanceof IPoint) {
        ILabelPoint label = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createLabelPoint((IPoint) geom, category,
                name, nature, Integer.valueOf(importance));
        if (fields.containsKey("CARTAGEN_ID")) {
          label.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          label.setShapeId(j);
        }
        label.setId(pop.size() + 1);
        pop.add(label);

      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des bâtiments ponctuels depuis un shapefile.
   * @param chemin
   * @throws IOException
   */
  public static boolean loadBuildingPointsFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    if (!chemin.endsWith(".shp"))
      chemin = chemin + ".shp";
    try {
      ShpFiles shpf = new ShpFiles(chemin);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      e.printStackTrace();
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IBuildPoint> pop = dataset.getBuildPts();

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

      if (geom instanceof IPoint) {
        IBuildPoint label = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createBuildPoint((IPoint) geom);

        if (fields.containsKey("CARTAGEN_ID")) {
          label.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          label.setShapeId(j);
        }
        label.setId(pop.size() + 1);
        pop.add(label);

      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des blocks depuis un shapefile sans remplir les liens avec bâtiments
   * et villes.
   * @param chemin
   * @throws IOException
   */
  public static boolean loadBlocksFromSHP(String chemin,
      CartAGenDataSet dataset) throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    if (!chemin.endsWith(".shp"))
      chemin = chemin + ".shp";
    try {
      ShpFiles shpf = new ShpFiles(chemin);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      e.printStackTrace();
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<IUrbanBlock> pop = dataset.getBlocks();

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

      if (geom instanceof IPolygon) {
        IUrbanBlock block = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createUrbanBlock((IPolygon) geom,
                new FT_FeatureCollection<IUrbanElement>(),
                new FT_FeatureCollection<INetworkSection>());

        if (fields.containsKey("CARTAGEN_ID")) {
          block.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          block.setShapeId(j);
        }
        block.setId(pop.size() + 1);
        pop.add(block);

      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IUrbanBlock block = dataset.getCartAGenDB().getGeneObjImpl()
              .getCreationFactory()
              .createUrbanBlock((IPolygon) ((IMultiSurface<?>) geom).get(i),
                  new FT_FeatureCollection<IUrbanElement>(),
                  new FT_FeatureCollection<INetworkSection>());
          if (fields.containsKey("CARTAGEN_ID")) {
            block.setId((Integer) fields.get("CARTAGEN_ID"));
          } else {
            block.setShapeId(j);
          }
          j++;
          pop.add(block);
        }
      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }

  /**
   * Charge des villes depuis un shapefile sans remplir les liens avec
   * bâtiments, îlots et réseau.
   * @param chemin
   * @throws IOException
   */
  public static boolean loadTownsFromSHP(String chemin, CartAGenDataSet dataset)
      throws IOException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    if (!chemin.endsWith(".shp"))
      chemin = chemin + ".shp";
    try {
      ShpFiles shpf = new ShpFiles(chemin);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset());
    } catch (FileNotFoundException e) {
      if (logger.isDebugEnabled()) {
        logger.debug("fichier " + chemin + " non trouve.");
      }
      e.printStackTrace();
      return false;
    }

    if (logger.isInfoEnabled()) {
      logger.info("Loading: " + chemin);
    }

    IPopulation<ITown> pop = dataset.getTowns();

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

      if (geom instanceof IPolygon) {
        ITown block = dataset.getCartAGenDB().getGeneObjImpl()
            .getCreationFactory().createTown(((IPolygon) geom));

        if (fields.containsKey("CARTAGEN_ID")) {
          block.setId((Integer) fields.get("CARTAGEN_ID"));
        } else {
          block.setShapeId(j);
        }
        block.setId(pop.size() + 1);
        pop.add(block);

      } else {
        logger.error("ERREUR lors du chargement de shp " + chemin
            + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
      j++;
    }

    shr.close();
    dbr.close();

    return true;
  }
}
