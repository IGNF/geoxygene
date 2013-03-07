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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPLandUse;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPLandUseType;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.util.CRSConversion;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;

public abstract class ShapeFileLoader {

  private static Logger logger = Logger.getLogger(ShapeFileLoader.class
      .getName());

  private CartAGenDataSet dataset;
  private String projEpsg;

  public void setDataset(CartAGenDataSet dataset) {
    this.dataset = dataset;
  }

  public CartAGenDataSet getDataset() {
    return this.dataset;
  }

  /**
   * Load the shapefiles inside the given directory in {@code this} dataset.
   * @param directory the path of the directory containing the shapefiles to
   *          load
   * @throws IOException
   * @throws ShapefileException
   */
  public abstract void loadData(File directory, List<String> listLayer)
      throws ShapefileException, IOException;

  /**
   * Get the attributes of shapefile stream entry and put them in a map.
   * 
   * @param dbr the reader stream to the shapefiles attribute table.
   * @return
   * @throws IOException
   */
  public Map<String, Object> getShapeFileFields(DbaseFileReader dbr)
      throws IOException {
    // read values of the current dbr entry
    Object[] values = new Object[dbr.getHeader().getNumFields()];
    dbr.readEntry(values);
    // fill the map with the dbase values
    Map<String, Object> fields = new HashMap<String, Object>();
    for (int i = 0; i < dbr.getHeader().getNumFields(); i++) {
      fields.put(dbr.getHeader().getFieldName(i), values[i]);
    }

    return fields;
  }

  /**
   * Generic method to load VMAP ILineString classes from shapefiles.
   * @param path
   * @param geneObjClass
   * @param nomPopulation
   * @param net
   * @throws ShapefileException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  @SuppressWarnings("unchecked")
  public void loadLineStringClass(String path, Class<?> geneObjClass,
      String nomPopulation, String featureTypeName, INetwork net,
      PeaRepDbType type) throws ShapefileException, IOException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException, SecurityException, NoSuchMethodException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(path);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (ShapeFileLoader.logger.isLoggable(Level.FINEST)) {
        ShapeFileLoader.logger.finest("fichier " + path + " non trouve.");
      }
      return;
    }

    if (ShapeFileLoader.logger.isLoggable(Level.INFO)) {
      ShapeFileLoader.logger.info("Loading: " + path);
    }

    // get the road population of the dataset
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataset()
        .getCartagenPop(nomPopulation, featureTypeName);

    // case of an empty shapefile: return!
    if (!shr.hasNext()) {
      shr.close();
      dbr.close();
      return;
    }

    // Get the Lat/Long coordinates of the first object of the population
    // and compute the associated utm zone
    Record object1 = shr.nextRecord();
    Geometry geomJTS1 = (Geometry) object1.shape();
    if (this.projEpsg == null) {
      String zone = this.getZoneUtm(geomJTS1.getCentroid().getX(), geomJTS1
          .getCentroid().getY());
      this.setProjEpsg(CRSConversion.getEPSGFromUTMZone(zone));
    }

    // loop on the shapefile records
    while (shr.hasNext() && dbr.hasNext()) {
      Record object = shr.nextRecord();

      // get the record attributes
      Map<String, Object> attributes = this.getShapeFileFields(dbr);

      // get the record geometry
      IGeometry geom = null;
      try {
        // coordinates transformation
        Geometry geomJTS = (Geometry) object.shape();
        geom = CRSConversion.changeCRS(geomJTS, "4326", this.projEpsg, false,
            true);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      // build the line string object
      Constructor<?> constructor = geneObjClass.getConstructor(
          ILineString.class, HashMap.class, PeaRepDbType.class);
      if (geom instanceof ILineString) {
        IGeneObj tr = (IGeneObj) constructor
            .newInstance(geom, attributes, type);

        pop.add(tr);
        if (net != null) {
          net.addSection((INetworkSection) tr);
        }
      } else {
        for (int i = 0; i < ((IMultiCurve<?>) geom).size(); i++) {
          IGeneObj tr = (IGeneObj) constructor.newInstance(
              ((IMultiCurve<?>) geom).get(i), attributes, type);

          pop.add(tr);
          if (net != null) {
            net.addSection((INetworkSection) tr);
          }
        }
      }
    }
    shr.close();
    dbr.close();
  }

  @SuppressWarnings("unchecked")
  public void loadPolygonClass(String path, Class<?> geneObjClass,
      String nomPopulation, String featureTypeName, PeaRepDbType type)
      throws ShapefileException, IOException, IllegalArgumentException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, SecurityException, NoSuchMethodException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(path);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (ShapeFileLoader.logger.isLoggable(Level.FINEST)) {
        ShapeFileLoader.logger.finest("fichier " + path + " non trouve.");
      }
      return;
    }

    if (ShapeFileLoader.logger.isLoggable(Level.INFO)) {
      ShapeFileLoader.logger.info("Loading: " + path);
    }

    // get the road population of the dataset
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataset()
        .getCartagenPop(nomPopulation, featureTypeName);

    // Get the Lat/Long coordinates of the first object of the population
    // and compute the associated utm zone
    Record object1 = shr.nextRecord();
    Geometry geomJTS1 = (Geometry) object1.shape();
    if (this.projEpsg == null) {
      String zone = this.getZoneUtm(geomJTS1.getCentroid().getX(), geomJTS1
          .getCentroid().getY());
      this.setProjEpsg(CRSConversion.getEPSGFromUTMZone(zone));
    }
    shr.close();
    ShpFiles shpf = new ShpFiles(path);
    shr = new ShapefileReader(shpf, true, false, new GeometryFactory());

    // loop on the shapefile records
    while (shr.hasNext() && dbr.hasNext()) {
      Record object = shr.nextRecord();

      // get the record attributes
      Map<String, Object> attributes = this.getShapeFileFields(dbr);

      // get the record geometry
      IGeometry geom = null;
      try {
        // coordinates transformation
        Geometry geomJTS = (Geometry) object.shape();
        geom = CRSConversion.changeCRS(geomJTS, "4326", this.projEpsg, false,
            true);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      // build the line string object
      Constructor<?> constructor = geneObjClass.getConstructor(IPolygon.class,
          HashMap.class, PeaRepDbType.class);
      if (geom instanceof IPolygon) {
        IGeneObj tr = (IGeneObj) constructor
            .newInstance(geom, attributes, type);

        pop.add(tr);
      } else {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IGeneObj tr = (IGeneObj) constructor.newInstance(
              ((IMultiSurface<?>) geom).get(i), attributes, type);

          pop.add(tr);
        }
      }
    }
    shr.close();
    dbr.close();
  }

  @SuppressWarnings("unchecked")
  public void loadLandUseClass(String path, Class<?> geneObjClass,
      String nomPopulation, String featureTypeName, PeaRepDbType type,
      MGCPLandUseType landUseType) throws ShapefileException, IOException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException, SecurityException, NoSuchMethodException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(path);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (ShapeFileLoader.logger.isLoggable(Level.FINEST)) {
        ShapeFileLoader.logger.finest("fichier " + path + " non trouve.");
      }
      return;
    }

    if (ShapeFileLoader.logger.isLoggable(Level.INFO)) {
      ShapeFileLoader.logger.info("Loading: " + path);
    }

    // get the road population of the dataset
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataset()
        .getCartagenPop(nomPopulation, featureTypeName);

    // Get the Lat/Long coordinates of the first object of the population
    // and compute the associated utm zone
    Record object1 = shr.nextRecord();
    Geometry geomJTS1 = (Geometry) object1.shape();
    if (this.projEpsg == null) {
      String zone = this.getZoneUtm(geomJTS1.getCentroid().getX(), geomJTS1
          .getCentroid().getY());
      this.setProjEpsg(CRSConversion.getEPSGFromUTMZone(zone));
    }
    shr.close();
    ShpFiles shpf = new ShpFiles(path);
    shr = new ShapefileReader(shpf, true, false, new GeometryFactory());

    // loop on the shapefile records
    while (shr.hasNext() && dbr.hasNext()) {
      Record object = shr.nextRecord();

      // get the record attributes
      Map<String, Object> attributes = this.getShapeFileFields(dbr);

      // get the record geometry
      IGeometry geom = null;
      try {
        // coordinates transformation
        Geometry geomJTS = (Geometry) object.shape();
        geom = CRSConversion.changeCRS(geomJTS, "4326", this.projEpsg, false,
            true);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      // build the line string object
      Constructor<?> constructor = geneObjClass.getConstructor(IPolygon.class,
          HashMap.class, PeaRepDbType.class);
      if (geom instanceof IPolygon) {
        IGeneObj tr = (IGeneObj) constructor
            .newInstance(geom, attributes, type);
        ((MGCPLandUse) tr).setLandUseType(landUseType);
        pop.add(tr);
      } else {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          IGeneObj tr = (IGeneObj) constructor.newInstance(
              ((IMultiSurface<?>) geom).get(i), attributes, type);
          ((MGCPLandUse) tr).setLandUseType(landUseType);
          pop.add(tr);
        }
      }
    }
    shr.close();
    dbr.close();
  }

  @SuppressWarnings("unchecked")
  public void loadPolygonClassUnionMulti(String path, Class<?> geneObjClass,
      String nomPopulation, String featureTypeName, PeaRepDbType type)
      throws ShapefileException, IOException, IllegalArgumentException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, SecurityException, NoSuchMethodException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(path);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (ShapeFileLoader.logger.isLoggable(Level.FINEST)) {
        ShapeFileLoader.logger.finest("fichier " + path + " non trouve.");
      }
      return;
    }

    if (ShapeFileLoader.logger.isLoggable(Level.INFO)) {
      ShapeFileLoader.logger.info("Loading: " + path);
    }

    // get the road population of the dataset
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataset()
        .getCartagenPop(nomPopulation, featureTypeName);

    // Get the Lat/Long coordinates of the first object of the population
    // and compute the associated utm zone
    Record object1 = shr.nextRecord();
    Geometry geomJTS1 = (Geometry) object1.shape();
    if (this.projEpsg == null) {
      String zone = this.getZoneUtm(geomJTS1.getCentroid().getX(), geomJTS1
          .getCentroid().getY());
      this.setProjEpsg(CRSConversion.getEPSGFromUTMZone(zone));
    }

    // loop on the shapefile records
    while (shr.hasNext() && dbr.hasNext()) {
      Record object = shr.nextRecord();

      // get the record attributes
      Map<String, Object> attributes = this.getShapeFileFields(dbr);

      // get the record geometry
      IGeometry geom = null;
      try {
        // coordinates transformation
        Geometry geomJTS = (Geometry) object.shape();

        geom = CRSConversion.changeCRS(geomJTS, "4326", this.projEpsg, false,
            true);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      // build the line string object
      Constructor<?> constructor = geneObjClass.getConstructor(IPolygon.class,
          HashMap.class, PeaRepDbType.class);
      if (geom instanceof IPolygon) {
        IGeneObj tr = (IGeneObj) constructor
            .newInstance(geom, attributes, type);

        pop.add(tr);
      } else {
        Collection<IGeometry> coln = new HashSet<IGeometry>();
        coln.add(geom);
        IPolygon geomUnion = JTSAlgorithms.unionAdjacentPolygons(coln)
            .iterator().next();
        IGeneObj tr = (IGeneObj) constructor.newInstance(geomUnion, attributes,
            type);

        pop.add(tr);
      }
    }

    shr.close();
    dbr.close();
  }

  @SuppressWarnings("unchecked")
  public void loadPointClass(String path, Class<?> geneObjClass,
      String nomPopulation, String featureTypeName, PeaRepDbType type)
      throws ShapefileException, IOException, IllegalArgumentException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, SecurityException, NoSuchMethodException {
    ShapefileReader shr = null;
    DbaseFileReader dbr = null;
    try {
      ShpFiles shpf = new ShpFiles(path);
      shr = new ShapefileReader(shpf, true, false, new GeometryFactory());
      dbr = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1"));
    } catch (FileNotFoundException e) {
      if (ShapeFileLoader.logger.isLoggable(Level.FINEST)) {
        ShapeFileLoader.logger.finest("fichier " + path + " non trouve.");
      }
      return;
    }

    if (ShapeFileLoader.logger.isLoggable(Level.INFO)) {
      ShapeFileLoader.logger.info("Loading: " + path);
    }

    // get the road population of the dataset
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataset()
        .getCartagenPop(nomPopulation, featureTypeName);

    // Get the Lat/Long coordinates of the first object of the population
    // and compute the associated utm zone
    Record object1 = shr.nextRecord();
    Geometry geomJTS1 = (Geometry) object1.shape();
    if (this.projEpsg == null) {
      String zone = this.getZoneUtm(geomJTS1.getCentroid().getX(), geomJTS1
          .getCentroid().getY());
      this.setProjEpsg(CRSConversion.getEPSGFromUTMZone(zone));
    }

    // loop on the shapefile records
    while (shr.hasNext() && dbr.hasNext()) {
      Record object = shr.nextRecord();

      // get the record attributes
      Map<String, Object> attributes = this.getShapeFileFields(dbr);

      // get the record geometry
      IGeometry geom = null;
      try {
        // coordinates transformation
        Geometry geomJTS = (Geometry) object.shape();
        geom = CRSConversion.changeCRS(geomJTS, "4326", this.projEpsg, false,
            true);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      // build the line string object
      Constructor<?> constructor = geneObjClass.getConstructor(IPoint.class,
          HashMap.class, PeaRepDbType.class);
      if (geom instanceof IPoint) {
        IGeneObj tr = (IGeneObj) constructor
            .newInstance(geom, attributes, type);

        pop.add(tr);
      } else {
        for (int i = 0; i < ((IMultiPoint) geom).size(); i++) {
          IGeneObj tr = (IGeneObj) constructor.newInstance(
              ((IMultiPoint) geom).get(i), attributes);

          pop.add(tr);
        }
      }
    }
    shr.close();
    dbr.close();
  }

  public void setProjEpsg(String projEpsg) {
    this.projEpsg = projEpsg;
  }

  public String getProjEpsg() {
    return this.projEpsg;
  }

  public String getZoneUtm(Double x, Double y) {
    String zoneUtm = null;
    String zone = null;
    String hemis = null;

    if (x >= 0 && x < 6) {
      zone = "31";
    } else if (x >= 6 && x < 12) {
      zone = "32";
    } else if (x >= 12 && x < 18) {
      zone = "33";
    } else if (x >= 18 && x < 24) {
      zone = "34";
    } else if (x >= 24 && x < 30) {
      zone = "35";
    } else if (x >= 30 && x < 36) {
      zone = "36";
    } else if (x >= 36 && x < 42) {
      zone = "37";
    } else if (x >= 42 && x < 48) {
      zone = "38";
    } else if (x >= 48 && x < 54) {
      zone = "39";
    } else if (x >= 54 && x < 60) {
      zone = "40";
    } else if (x >= 60 && x < 66) {
      zone = "41";
    } else if (x >= 66 && x < 72) {
      zone = "42";
    } else if (x >= 72 && x < 78) {
      zone = "43";
    } else if (x >= 78 && x < 84) {
      zone = "44";
    } else if (x >= 84 && x < 90) {
      zone = "45";
    } else if (x >= 90 && x < 96) {
      zone = "46";
    } else if (x >= 96 && x < 102) {
      zone = "47";
    } else if (x >= 102 && x < 108) {
      zone = "48";
    } else if (x >= 108 && x < 114) {
      zone = "49";
    } else if (x >= 114 && x < 120) {
      zone = "50";
    } else if (x >= 120 && x < 126) {
      zone = "51";
    } else if (x >= 126 && x < 132) {
      zone = "52";
    } else if (x >= 132 && x < 138) {
      zone = "53";
    } else if (x >= 138 && x < 144) {
      zone = "54";
    } else if (x >= 144 && x < 150) {
      zone = "55";
    } else if (x >= 150 && x < 156) {
      zone = "56";
    } else if (x >= 156 && x < 162) {
      zone = "57";
    } else if (x >= 162 && x < 168) {
      zone = "58";
    } else if (x >= 168 && x < 174) {
      zone = "59";
    } else if (x >= 174 && x < 180) {
      zone = "60";
    } else if (x < 0 && x >= -6) {
      zone = "30";
    } else if (x < -6 && x >= -12) {
      zone = "29";
    } else if (x < -12 && x >= -18) {
      zone = "28";
    } else if (x < -18 && x >= -24) {
      zone = "27";
    } else if (x < -24 && x >= -30) {
      zone = "26";
    } else if (x < -30 && x >= -36) {
      zone = "25";
    } else if (x < -36 && x >= -42) {
      zone = "24";
    } else if (x < -42 && x >= -48) {
      zone = "23";
    } else if (x < -48 && x >= -54) {
      zone = "22";
    } else if (x < -54 && x >= -60) {
      zone = "21";
    } else if (x < -60 && x >= -66) {
      zone = "20";
    } else if (x < -66 && x >= -72) {
      zone = "19";
    } else if (x < -72 && x >= -78) {
      zone = "18";
    } else if (x < -78 && x >= -84) {
      zone = "17";
    } else if (x < -84 && x >= -90) {
      zone = "16";
    } else if (x < -90 && x >= -96) {
      zone = "15";
    } else if (x < -96 && x >= -102) {
      zone = "14";
    } else if (x < -102 && x >= -108) {
      zone = "13";
    } else if (x < -108 && x >= -114) {
      zone = "12";
    } else if (x < -114 && x >= -120) {
      zone = "11";
    } else if (x < -120 && x >= -126) {
      zone = "10";
    } else if (x < -126 && x >= -132) {
      zone = "9";
    } else if (x < -132 && x >= -138) {
      zone = "8";
    } else if (x < -138 && x >= -144) {
      zone = "7";
    } else if (x < -144 && x >= -150) {
      zone = "6";
    } else if (x < -150 && x >= -156) {
      zone = "5";
    } else if (x < -156 && x >= -162) {
      zone = "4";
    } else if (x < -162 && x >= -168) {
      zone = "3";
    } else if (x < -168 && x >= -174) {
      zone = "2";
    } else if (x < -174 && x >= -180) {
      zone = "1";
    }

    if (zone == null) {
      return null;
    }

    if (y >= 0) {
      hemis = "N";
    } else if (y < 0) {
      hemis = "S";
    }

    zoneUtm = zone.concat(hemis);
    System.out.println("Zone UTM = " + zoneUtm);

    return zoneUtm;
  }

}
