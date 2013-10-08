/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.hibernate.HibernateException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationLeftPanelComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.CartAGenProgressBar;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.cartagen.util.XMLUtil;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class ShapeFileDB extends CartAGenDB {

  private static Logger logger = Logger.getLogger(ShapeFileDB.class.getName());

  /**
   * Override the classes field with shapefile classes.
   */
  private List<ShapeFileClass> classes;

  private String systemPath;

  private OpenDatasetTask task;

  public ShapeFileDB(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    super(file);
  }

  public ShapeFileDB(String name) {
    this.classes = new ArrayList<ShapeFileClass>();
    this.setName(name);
    this.setPersistentClasses(new HashSet<Class<?>>());
  }

  @Override
  public void openFromXml(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    // first open the XML document in order to parse it
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    // then read the document to fill the fields
    Element root = (Element) doc.getElementsByTagName("cartagen-dataset").item(
        0);
    // The DataSet type
    Element typeElem = (Element) root.getElementsByTagName("type").item(0);
    String type = typeElem.getChildNodes().item(0).getNodeValue();
    if (!this.getClass().getName().equals(type)) {
      ShapeFileDB.logger
          .warning("The file does not correspond to a ShapeFile Dataset !");
      return;
    }
    // The DataSet name
    Element nameElem = (Element) root.getElementsByTagName("name").item(0);
    this.setName(nameElem.getChildNodes().item(0).getNodeValue());

    // The DataSet symbolisation scale
    Element scaleElem = (Element) root.getElementsByTagName("scale").item(0);
    this.setSymboScale(Integer.valueOf(scaleElem.getChildNodes().item(0)
        .getNodeValue()));

    // The DataSet system path
    Element systemPathElem = (Element) root.getElementsByTagName("system-path")
        .item(0);
    this.setSystemPath(systemPathElem.getChildNodes().item(0).getNodeValue());

    // the source DLM
    Element sourceElem = (Element) root.getElementsByTagName("source-dlm")
        .item(0);
    SourceDLM source = SourceDLM.valueOf(sourceElem.getChildNodes().item(0)
        .getNodeValue());
    this.setSourceDLM(source);

    // the list of classes
    this.classes = new ArrayList<ShapeFileClass>();
    Element classesElem = (Element) root.getElementsByTagName("classes-list")
        .item(0);
    for (int i = 0; i < classesElem.getElementsByTagName("class").getLength(); i++) {
      Element classElem = (Element) classesElem.getElementsByTagName("class")
          .item(i);
      Element pathElem = (Element) classElem.getElementsByTagName("path").item(
          0);
      String path = pathElem.getChildNodes().item(0).getNodeValue();
      Element popElem = (Element) classElem
          .getElementsByTagName("feature-type").item(0);
      String featureType = popElem.getChildNodes().item(0).getNodeValue();
      Class<? extends IGeometry> geometryType = IGeometry.class;
      if (classElem.getElementsByTagName("geometry-type").getLength() != 0) {
        // TODO
      }
      this.classes
          .add(new ShapeFileClass(this, path, featureType, geometryType));
    }

    // the enrichments
    this.enrichments = new HashSet<CartAGenEnrichment>();
    for (int i = 0; i < root.getElementsByTagName("enrichment").getLength(); i++) {
      Element enrichElem = (Element) root.getElementsByTagName("enrichment")
          .item(i);
      String enrich = enrichElem.getChildNodes().item(0).getNodeValue();
      this.enrichments.add(CartAGenEnrichment.valueOf(enrich));
    }

    // the GeneObjImplementation
    Element implElem = (Element) root.getElementsByTagName(
        "geneobj-implementation").item(0);
    Element implNameElem = (Element) implElem.getElementsByTagName(
        "implementation-name").item(0);
    String implName = implNameElem.getChildNodes().item(0).getNodeValue();
    Element implPackElem = (Element) implElem.getElementsByTagName(
        "implementation-package").item(0);
    String packName = implPackElem.getChildNodes().item(0).getNodeValue();
    Package rootPackage = Package.getPackage(packName);
    Element implClassElem = (Element) implElem.getElementsByTagName(
        "implementation-root-class").item(0);
    String className = implClassElem.getChildNodes().item(0).getNodeValue();
    Class<?> rootClass = Class.forName(className);
    Element factClassElem = (Element) implElem.getElementsByTagName(
        "implementation-factory").item(0);
    String factClassName = factClassElem.getChildNodes().item(0).getNodeValue();
    Class<?> factClass = Class.forName(factClassName);
    try {
      this.setGeneObjImpl(new GeneObjImplementation(implName, rootPackage,
          rootClass, (AbstractCreationFactory) factClass.getConstructor()
              .newInstance()));
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

    // the persistent classes
    Element persistElem = (Element) root.getElementsByTagName("persistent")
        .item(0);
    this.setPersistent(Boolean.valueOf(persistElem.getChildNodes().item(0)
        .getNodeValue()));
    this.setPersistentClasses(new HashSet<Class<?>>());
    Element persistClassesElem = (Element) root.getElementsByTagName(
        "persistent-classes").item(0);
    // get the class loader for the geoxygene-cartagen project
    ClassLoader loader = IGeneObj.class.getClassLoader();
    for (int i = 0; i < persistClassesElem.getElementsByTagName(
        "persistent-class").getLength(); i++) {
      Element persistClassElem = (Element) persistClassesElem
          .getElementsByTagName("persistent-class").item(i);
      String className1 = persistClassElem.getChildNodes().item(0)
          .getNodeValue();
      this.getPersistentClasses().add(Class.forName(className1, true, loader));
    }
    this.setPersistentClasses(this.getGeneObjImpl().filterClasses(
        getPersistentClasses()));

    this.setXmlFile(file);
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * @throws TransformerException
   * 
   */
  @Override
  public void saveToXml(File file) throws IOException, TransformerException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("cartagen-dataset");
    // The DataSet name
    Element nameElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.getName());
    nameElem.appendChild(n);
    root.appendChild(nameElem);
    // The DataSet system path
    Element pathElem = xmlDoc.createElement("system-path");
    n = xmlDoc.createTextNode(this.getSystemPath());
    pathElem.appendChild(n);
    root.appendChild(pathElem);
    // The DataSet type
    Element typeElem = xmlDoc.createElement("type");
    n = xmlDoc.createTextNode(this.getClass().getName());
    typeElem.appendChild(n);
    root.appendChild(typeElem);
    // The DataSet type
    Element datasetTypeElem = xmlDoc.createElement("dataset-type");
    n = xmlDoc.createTextNode(this.getDataSet().getClass().getName());
    datasetTypeElem.appendChild(n);
    root.appendChild(datasetTypeElem);
    // The symbolisation scale
    Element scaleElem = xmlDoc.createElement("scale");
    n = xmlDoc.createTextNode(String.valueOf(this.getSymboScale()));
    scaleElem.appendChild(n);
    root.appendChild(scaleElem);

    // The source DLM
    Element dlmElem = xmlDoc.createElement("source-dlm");
    n = xmlDoc.createTextNode(this.getSourceDLM().name());
    dlmElem.appendChild(n);
    root.appendChild(dlmElem);

    // the list of classes
    Element classesElem = xmlDoc.createElement("classes-list");
    for (ShapeFileClass c : this.classes) {
      Element classeElem = xmlDoc.createElement("class");
      // the class path
      Element classPathElem = xmlDoc.createElement("path");
      n = xmlDoc.createTextNode(c.getPath());
      classPathElem.appendChild(n);
      classeElem.appendChild(classPathElem);
      // the population name
      Element popNameElem = xmlDoc.createElement("feature-type");
      n = xmlDoc.createTextNode(c.getFeatureTypeName());
      popNameElem.appendChild(n);
      classeElem.appendChild(popNameElem);
      classesElem.appendChild(classeElem);
    }
    root.appendChild(classesElem);

    // the enrichments
    for (CartAGenEnrichment enrich : this.getEnrichments()) {
      Element enrichElem = xmlDoc.createElement("enrichment");
      n = xmlDoc.createTextNode(enrich.name());
      enrichElem.appendChild(n);
      root.appendChild(enrichElem);
    }

    // the GeneObj implementation
    Element implElem = xmlDoc.createElement("geneobj-implementation");
    root.appendChild(implElem);
    Element implNameElem = xmlDoc.createElement("implementation-name");
    n = xmlDoc.createTextNode(this.getGeneObjImpl().getName());
    implNameElem.appendChild(n);
    implElem.appendChild(implNameElem);
    Element implPackElem = xmlDoc.createElement("implementation-package");
    n = xmlDoc.createTextNode(this.getGeneObjImpl().getRootPackage().getName());
    implPackElem.appendChild(n);
    implElem.appendChild(implPackElem);
    Element implClassElem = xmlDoc.createElement("implementation-root-class");
    n = xmlDoc.createTextNode(this.getGeneObjImpl().getRootClass().getName());
    implClassElem.appendChild(n);
    implElem.appendChild(implClassElem);
    Element factClassElem = xmlDoc.createElement("implementation-factory");
    n = xmlDoc.createTextNode(this.getGeneObjImpl().getCreationFactory()
        .getClass().getName());
    factClassElem.appendChild(n);
    implElem.appendChild(factClassElem);

    // the persistent classes
    Element persistElem = xmlDoc.createElement("persistent");
    n = xmlDoc.createTextNode(String.valueOf(this.isPersistent()));
    persistElem.appendChild(n);
    root.appendChild(persistElem);
    Element persistClassesElem = xmlDoc.createElement("persistent-classes");
    root.appendChild(persistClassesElem);
    for (Class<?> classObj : this.getPersistentClasses()) {
      Element persistClassElem = xmlDoc.createElement("persistent-class");
      n = xmlDoc.createTextNode(classObj.getName());
      persistClassElem.appendChild(n);
      persistClassesElem.appendChild(persistClassElem);
    }

    // ECRITURE DU FICHIER
    xmlDoc.appendChild(root);
    print();
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  /**
   * Add a new shapfile to the dataset
   * @param shape
   * @param populationName the name of the population of objects created from
   *          the Shapefile and stored in GeneralisationDataset
   */
  public void addShapeFile(String path, String populationName,
      Class<? extends IGeometry> geometryType) {
    this.classes.add(new ShapeFileClass(this, path, populationName,
        geometryType));
  }

  @Override
  public void populateDataset(int scale) {

    CartagenApplication.getInstance().getFrame()
        .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    this.task = new OpenDatasetTask(scale, this);
    this.task.addPropertyChangeListener(CartAGenProgressBar.getInstance());
    this.task.execute();
    while (!this.task.exit) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void load(GeographicClass geoClass, int scale) {
    ShapeFileClass shape = (ShapeFileClass) geoClass;

    SymbolList symbols = SymbolList.getSymbolList(SymbolsUtil.getSymbolGroup(
        this.getSourceDLM(), this.getSymboScale()));
    this.getDataSet().setSymbols(symbols);
    try {
      if (shape.getFeatureTypeName().equals(IBuilding.FEAT_TYPE_NAME)) {
        this.getDataSet().loadBuildingsFromSHP(shape.getPath());
      }
      if (shape.getFeatureTypeName().equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadRoadLinesFromSHP(shape.getPath(),
            this.getSourceDLM(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IWaterLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadWaterLinesFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IWaterArea.FEAT_TYPE_NAME)) {
        this.getDataSet().loadWaterAreasFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IRailwayLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadRailwayLineFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IElectricityLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadElectricityLinesFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IContourLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadContourLinesFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IReliefElementLine.FEAT_TYPE_NAME)) {
        this.getDataSet().loadReliefElementLinesFromSHP(shape.getPath(),
            symbols);
      }
      if (shape.getFeatureTypeName().equals(ISpotHeight.FEAT_TYPE_NAME)) {
        this.getDataSet().loadSpotHeightsFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IMask.FEAT_TYPE_NAME)) {
        this.getDataSet().loadMaskFromSHP(shape.getPath());
      }
      if (shape.getFeatureTypeName().equals(ISimpleLandUseArea.FEAT_TYPE_NAME)) {
        int type = 0;
        if (shape.getName().equals("ZONE_VEGETATION"))
          type = 1;
        if (shape.getName().equals("ZONE_ACTIVITE"))
          type = 2;
        this.getDataSet().loadLandUseAreasFromSHP(shape.getPath(), 1.0, type);
      }
      // add the unique Id in the ShapeFile
      addCartagenId();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public void overwrite(GeographicClass geoClass) {
    ShapeFileClass shape = (ShapeFileClass) geoClass;

    SymbolList symbols = SymbolList.getSymbolList(SymbolsUtil.getSymbolGroup(
        this.getSourceDLM(), this.getSymboScale()));

    try {
      if (shape.getFeatureTypeName().equals(IBuilding.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteBuildingsFromSHP(shape.getPath());
      }
      if (shape.getFeatureTypeName().equals(IRoadLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteRoadLinesFromSHP(shape.getPath(), 2.0,
            this.getSourceDLM(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IWaterLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteWaterLinesFromSHP(shape.getPath(), 2.0,
            symbols);
      }
      if (shape.getFeatureTypeName().equals(IWaterArea.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteWaterAreasFromSHP(shape.getPath(), 2.0,
            symbols);
      }
      if (shape.getFeatureTypeName().equals(IRailwayLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteRailwayLineFromSHP(shape.getPath(), 2.0,
            symbols);
      }
      if (shape.getFeatureTypeName().equals(IElectricityLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteElectricityLinesFromSHP(shape.getPath(),
            2.0, symbols);
      }
      if (shape.getFeatureTypeName().equals(IContourLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteContourLinesFromSHP(shape.getPath(), 2.0,
            symbols);
      }
      if (shape.getFeatureTypeName().equals(IReliefElementLine.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteReliefElementLinesFromSHP(shape.getPath(),
            2.0, symbols);
      }
      if (shape.getFeatureTypeName().equals(ISpotHeight.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteSpotHeightsFromSHP(shape.getPath(), symbols);
      }
      if (shape.getFeatureTypeName().equals(IMask.FEAT_TYPE_NAME)) {
        this.getDataSet().overwriteMaskFromSHP(shape.getPath());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void triggerEnrichments() {
    CartAGenEnrichment.setOtherToFalse(this.getEnrichments());
    CartagenApplication.getInstance().enrichData(this.getDataSet());
  }

  public void setSystemPath(String systemPath) {
    this.systemPath = systemPath;
  }

  public String getSystemPath() {
    return this.systemPath;
  }

  public void setTask(OpenDatasetTask task) {
    this.task = task;
  }

  public OpenDatasetTask getTask() {
    return this.task;
  }

  public class OpenDatasetTask extends SwingWorker<Void, Void> {

    private int scale;
    public boolean exit = false;

    @Override
    protected Void doInBackground() throws Exception {
      // loop on the classes to import them into the generalisation dataset
      int step = Math.round(100 / ShapeFileDB.this.classes.size());
      int value = 0;
      for (ShapeFileClass shapefile : ShapeFileDB.this.classes) {
        // test if the shapefile correspond to a persistent class
        boolean persistent = false;
        for (Class<?> classObj : ShapeFileDB.this.getPersistentClasses()) {
          if (!IGeneObj.class.isAssignableFrom(classObj)) {
            continue;
          }
          Field field = classObj.getField("FEAT_TYPE_NAME");
          String featType = (String) field.get(null);
          if (shapefile.getFeatureTypeName().equals(featType)) {
            persistent = true;
          }
        }
        if (persistent) {
          continue;
        }
        ShapeFileDB.this.load(shapefile, this.scale);
        value += step;
        this.setProgress(value);
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
          this.exit = true;
        }
      }
      // trigger the enrichments
      if (!ShapeFileDB.this.isPersistent()) {
        ShapeFileDB.this.triggerEnrichments();
      }
      // finally load persistent classes
      try {
        Set<Class<?>> displayedClasses = ShapeFileDB.this
            .loadPersistentClasses();
        for (Class<?> displayedClass : displayedClasses) {
          GeneralisationLeftPanelComplement.getInstance().setLayerDisplay(
              displayedClass);
        }
        // now build the dataset networks from the loaded data
        INetwork roadNet = ShapeFileDB.this.getDataSet().getRoadNetwork();
        for (IRoadLine road : ShapeFileDB.this.getDataSet().getRoads())
          roadNet.addSection(road);
        INetwork railNet = ShapeFileDB.this.getDataSet().getRailwayNetwork();
        for (IRailwayLine rail : ShapeFileDB.this.getDataSet()
            .getRailwayLines())
          railNet.addSection(rail);
        INetwork waterNet = ShapeFileDB.this.getDataSet().getHydroNetwork();
        for (IWaterLine water : ShapeFileDB.this.getDataSet().getWaterLines())
          waterNet.addSection(water);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (HibernateException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        /*
         * System.out.println("on passe"); CartagenApplication .getInstance()
         * .getLayerGroup() .loadLayers( getDataSet(),
         * CartagenApplication.getInstance
         * ().getFrame().getVisuPanel().symbolisationDisplay);
         * CartagenApplication.getInstance().getLayerGroup()
         * .loadInterfaceWithLayers(
         * CartagenApplication.getInstance().getFrame().getLayerManager());
         */
        this.exit = true;
        CartagenApplication.getInstance().initGeneralisation();
      }
      return null;
    }

    @Override
    protected void done() {
      CartAGenProgressBar.getInstance().setVisible(false);
      CartagenApplication.getInstance().getFrame()
          .setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    OpenDatasetTask(int scale, ShapeFileDB dataset) {
      super();
      this.scale = scale;
      dataset.task = this;
    }
  }

  @Override
  public void addCartagenId() {
    for (ShapeFileClass shape : this.classes) {
      shape.addCartAGenId();
    }
  }

  @Override
  public List<GeographicClass> getClasses() {
    return new ArrayList<GeographicClass>(classes);
  }

  public void print() {
    System.out.println("ShapeFileDB: " + this.getName());
    System.out.println("classes: " + classes);
    System.out.println("path: " + systemPath);
  }
}
