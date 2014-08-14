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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationConfigurationFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.XMLUtil;

/**
 * this class represents work documents in CartAGen, as .mxd files in ArcGIS or
 * .got files in Clarity. The document has information on the area of imported
 * data and is related to the CartAGenDatabase objects opened. It also refers to
 * the PostGIS DB used to store persistently data within the document.
 * @author Guillaume
 * 
 */
public class CartAGenDoc {

  /**
   * Get the unique instance of CartAGenDoc class.
   * <p>
   * Remarque : le constructeur est rendu inaccessible
   */
  public static CartAGenDoc getInstance() {
    if (null == instance) { // Premier appel
      instance = new CartAGenDoc();
    }
    return instance;
  }

  /**
   * Constructeur redéfini comme étant privé pour interdire son appel et forcer
   * à passer par la méthode getInstance()
   */
  private CartAGenDoc() {
    // open a Cartagen doc file
    // TODO
    this.databases = new HashMap<String, CartAGenDB>();
    // this.postGisDb = PostgisDB.get(getName(), true);
  }

  /** L'instance statique */
  private static CartAGenDoc instance;

  /**
   * The name of the document, used as name for the PostGIS db related to this
   * document.
   */
  private String name;

  /**
   * Description of the zone represented by this data set (source DLM, extent).
   */
  private DataSetZone zone;

  /**
   * The CartAGenDB objects related to this document
   */
  private Map<String, CartAGenDB> databases;

  /**
   * The {@link DatabaseView} objects related to the CartAGenDB objects of
   * {@code this} document.
   */
  private Map<String, DatabaseView> databaseViews = new HashMap<String, DatabaseView>();

  /**
   * The PostGis database related to this dataset for storing persistent
   * objects.
   */
  private PostgisDB postGisDb;

  /**
   * The xml File that stores the document information
   */
  private File xmlFile = null;

  /**
   * Only one dataset can be generalised at a time, the current one, stored in
   * this field of the document.
   */
  private CartAGenDataSet currentDataset = null;

  /**
   * The DLM typed initial dataset of the document (unique) that contains data
   * before generalisation.
   */
  private CartAGenDataSet initialDataset = null;

  /**
   * The PostGIS session that allows to open transactions and make IGeneObj
   * persistent.
   */
  private Session postGisSession = null;

  public void setPostGisDb(PostgisDB postGisDb) {
    this.postGisDb = postGisDb;
  }

  public PostgisDB getPostGisDb() {
    return postGisDb;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, CartAGenDB> getDatabases() {
    return databases;
  }

  public void setDatabases(Map<String, CartAGenDB> databases) {
    this.databases = databases;
  }

  public void setZone(DataSetZone zone) {
    this.zone = zone;
  }

  public DataSetZone getZone() {
    return zone;
  }

  public Map<String, DatabaseView> getDatabaseViews() {
    return databaseViews;
  }

  public void setDatabaseViews(Map<String, DatabaseView> databaseViews) {
    this.databaseViews = databaseViews;
  }

  public void setXmlFile(File xmlFile) {
    this.xmlFile = xmlFile;
  }

  public File getXmlFile() {
    return xmlFile;
  }

  public void setCurrentDataset(CartAGenDataSet currentDataset) {
    this.currentDataset = currentDataset;
  }

  public CartAGenDataSet getCurrentDataset() {
    if (currentDataset == null) {
      if (databases.size() == 0)
        return null;
      CartAGenDB db = databases.values().iterator().next();
      return db.getDataSet();
    }
    return currentDataset;
  }

  public void setInitialDataset(CartAGenDataSet initialDataset) {
    this.initialDataset = initialDataset;
  }

  public CartAGenDataSet getInitialDataset() {
    return initialDataset;
  }

  public void setPostGisSession(Session postGisSession) {
    this.postGisSession = postGisSession;
  }

  public Session getPostGisSession() {
    return postGisSession;
  }

  /**
   * Saves the document as an XML file in order to be able to load the same
   * document in another session
   * @param file
   * @throws TransformerException
   */
  public void saveToXml(File file) throws IOException, TransformerException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("cartagen-document");

    // The Document name
    Element nameElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.getName());
    nameElem.appendChild(n);
    root.appendChild(nameElem);

    // the dataset zone
    Element zoneElem = xmlDoc.createElement("dataset-zone");
    root.appendChild(zoneElem);
    Element zoneNameElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.getZone().getName());
    zoneNameElem.appendChild(n);
    zoneElem.appendChild(zoneNameElem);
    if (this.getZone().getExtent() != null) {
      Element extentElem = xmlDoc.createElement("extent");
      zoneElem.appendChild(extentElem);
      for (IDirectPosition pt : this.getZone().getExtent().coord()) {
        Element xExtentElem = xmlDoc.createElement("x");
        n = xmlDoc.createTextNode(String.valueOf(pt.getX()));
        xExtentElem.appendChild(n);
        extentElem.appendChild(xExtentElem);
        Element yExtentElem = xmlDoc.createElement("y");
        n = xmlDoc.createTextNode(String.valueOf(pt.getY()));
        yExtentElem.appendChild(n);
        extentElem.appendChild(yExtentElem);
      }
    }

    // Databases
    StringBuffer xmlPath = new StringBuffer();
    xmlPath.append("src/main/resources/XML/Cartagen_dbs/");
    Element dbsElem = xmlDoc.createElement("databases");
    root.appendChild(dbsElem);
    File dir = new File(xmlPath.toString());
    if (!dir.exists())
      dir.mkdir();
    for (CartAGenDB db : databases.values()) {
      File dbFile = db.getXmlFile();
      String path = null;
      if (dbFile == null) {
        path = xmlPath.append(db.getName() + ".xml").toString();
        dbFile = new File(path);
      } else
        path = dbFile.getPath();
      db.saveToXml(dbFile);
      // store the path in this file
      Element dbElem = xmlDoc.createElement("database");
      n = xmlDoc.createTextNode(path);
      dbElem.appendChild(n);
      dbsElem.appendChild(dbElem);
      // the database view for this database
      this.getDatabaseViews().get(db.getName()).saveToXml(dbElem, xmlDoc, path);
    }

    // File writing
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  /**
   * 
   * @param file
   * @return
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IllegalArgumentException
   */
  public static CartAGenDoc loadDocFromXml(File file)
      throws ParserConfigurationException, SAXException, IOException,
      SecurityException, NoSuchMethodException, ClassNotFoundException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    instance = new CartAGenDoc();
    instance.setXmlFile(file);

    // first open the XML document in order to parse it
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    // then read the document to fill the fields
    Element root = (Element) doc.getElementsByTagName("cartagen-document")
        .item(0);

    // The document name
    Element nameElem = (Element) root.getElementsByTagName("name").item(0);
    instance.setName(nameElem.getChildNodes().item(0).getNodeValue());
    try {
      instance.postGisDb = PostgisDB.get(instance.getName(), true);
    } catch (Exception e) {
      // do nothing
    }

    // the dataset zone
    Element zoneElem = (Element) root.getElementsByTagName("dataset-zone")
        .item(0);
    Element zoneNameElem = (Element) zoneElem.getElementsByTagName("name")
        .item(0);
    String zoneName = zoneNameElem.getChildNodes().item(0).getNodeValue();
    IPolygon extent = null;
    if (zoneElem.getElementsByTagName("extent").getLength() > 0) {
      Element extentElem = (Element) zoneElem.getElementsByTagName("extent")
          .item(0);
      IDirectPositionList coords = new DirectPositionList();
      for (int i = 0; i < extentElem.getElementsByTagName("x").getLength(); i++) {
        Element xElem = (Element) extentElem.getElementsByTagName("x").item(i);
        Element yElem = (Element) extentElem.getElementsByTagName("y").item(i);
        double x = Double.valueOf(xElem.getChildNodes().item(0).getNodeValue());
        double y = Double.valueOf(yElem.getChildNodes().item(0).getNodeValue());
        coords.add(new DirectPosition(x, y));
      }
      extent = new GM_Polygon(new GM_LineString(coords));
    }
    instance.setZone(new DataSetZone(zoneName, extent));

    // load databases
    Element dbsElem = (Element) root.getElementsByTagName("databases").item(0);
    for (int i = 0; i < dbsElem.getElementsByTagName("database").getLength(); i++) {
      Element dbElem = (Element) dbsElem.getElementsByTagName("database").item(
          0);
      String path = dbElem.getChildNodes().item(0).getNodeValue();
      File dbFile = new File(path);
      Class<? extends CartAGenDB> dbClass = CartAGenDB.readType(dbFile);
      Constructor<? extends CartAGenDB> construct = dbClass
          .getConstructor(File.class);
      CartAGenDB database = construct.newInstance(dbFile);
      instance.databases.put(database.getName(), database);
      // populate the dataset of the loaded database
      Class<?> datasetClass = CartAGenDB.readDatasetType(dbFile);
      CartAGenDataSet dataset = (CartAGenDataSet) datasetClass.getConstructor()
          .newInstance();
      database.setDataSet(dataset);
      instance.currentDataset = dataset;
      database.populateDataset(database.getSymboScale());
      Legend.setSYMBOLISATI0N_SCALE(database.getSymboScale());
      GeneralisationConfigurationFrame.getInstance().resetValues();
      // read the database view
      instance.getDatabaseViews().put(database.getName(),
          new DatabaseView(instance, dbElem));
    }

    return instance;
  }

  /**
   * Get the dataset of the database named 'name'.
   * @param name1
   * @return
   */
  public CartAGenDataSet getDataset(String name1) {
    if (databases.containsKey(name1))
      return databases.get(name1).getDataSet();
    return null;
  }

  /**
   * Checks if there is a database that has a given {@link SourceDLM} type in
   * the databases of {@code this}.
   * @param source
   * @return
   */
  public boolean containsSourceDLM(SourceDLM source) {
    for (CartAGenDB db : databases.values()) {
      if (db.getSourceDLM().equals(source))
        return true;
    }
    return false;
  }

  /**
   * Test if there is a database named 'name1'.
   * @param name1
   * @return
   */
  public boolean hasDataset(String name1) {
    CartAGenDB dataset = databases.get(name1);
    if (dataset == null)
      return false;
    return true;
  }

  /**
   * Add a new database to the document.
   * @param name1
   * @param database
   */
  public void addDatabase(String name1, CartAGenDB database) {
    this.databases.put(name1, database);
  }

  /**
   * Get the first database that has a given {@link SourceDLM} type in the
   * databases of {@code this}.
   * @param source
   * @return
   */
  public CartAGenDB getDatabaseFromSource(SourceDLM source) {
    for (CartAGenDB db : databases.values()) {
      if (db.getSourceDLM().equals(source))
        return db;
    }
    return null;
  }

}
