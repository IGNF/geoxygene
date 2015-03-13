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
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.AbstractLayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.LayerGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
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
public class CartAGenDocOld {

  /**
   * Get the unique instance of CartAGenDocOld class.
   * <p>
   * Remarque : le constructeur est rendu inaccessible
   */
  public static CartAGenDocOld getInstance() {
    if (null == CartAGenDocOld.instance) { // Premier appel
      CartAGenDocOld.instance = new CartAGenDocOld();
    }
    return CartAGenDocOld.instance;
  }

  /**
   * Constructeur redéfini comme étant privé pour interdire son appel et forcer
   * à passer par la méthode getInstance()
   */
  private CartAGenDocOld() {
    // open a Cartagen doc file
    // TODO
    this.databases = new HashMap<String, CartAGenDB>();
    // this.postGisDb = PostgisDB.get(getName(), true);
  }

  /** L'instance statique */
  private static CartAGenDocOld instance;

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
   * geographic coordinates of the center of the visualisation panel.
   */
  private IDirectPosition geoCenter = new DirectPosition(0.0, 0.0);

  /**
   * geometry of the display window.
   */
  private IEnvelope displayEnvelope = null;

  /**
   * The CartAGenDB objects related to this document
   */
  private Map<String, CartAGenDB> databases;

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

  /**
   * The {@link LayerGroup} used in this document.
   */
  private AbstractLayerGroup layerGroup = new LayerGroup();

  public void setPostGisDb(PostgisDB postGisDb) {
    this.postGisDb = postGisDb;
  }

  public PostgisDB getPostGisDb() {
    return this.postGisDb;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, CartAGenDB> getDatabases() {
    return this.databases;
  }

  public void setDatabases(Map<String, CartAGenDB> databases) {
    this.databases = databases;
  }

  public void setZone(DataSetZone zone) {
    this.zone = zone;
  }

  public DataSetZone getZone() {
    return this.zone;
  }

  public IDirectPosition getGeoCenter() {
    return this.geoCenter;
  }

  public void setGeoCenter(IDirectPosition geoCenter) {
    this.geoCenter = geoCenter;
  }

  public IEnvelope getDisplayEnvelope() {
    return this.displayEnvelope;
  }

  public void setDisplayEnvelope(IEnvelope displayEnvelope) {
    this.displayEnvelope = displayEnvelope;
  }

  public void setXmlFile(File xmlFile) {
    this.xmlFile = xmlFile;
  }

  public File getXmlFile() {
    return this.xmlFile;
  }

  public void setCurrentDataset(CartAGenDataSet currentDataset) {
    this.currentDataset = currentDataset;
  }

  public CartAGenDataSet getCurrentDataset() {
    if (this.currentDataset == null) {
      if (this.databases.size() == 0) {
        // FIXME patch pour permettre de maintenir les deux interfaces
        return CartAGenDoc.getInstance().getCurrentDataset();
      }

      CartAGenDB db = this.databases.values().iterator().next();
      return db.getDataSet();
    }
    return this.currentDataset;
  }

  public void setInitialDataset(CartAGenDataSet initialDataset) {
    this.initialDataset = initialDataset;
  }

  public CartAGenDataSet getInitialDataset() {
    return this.initialDataset;
  }

  public void setPostGisSession(Session postGisSession) {
    this.postGisSession = postGisSession;
  }

  public Session getPostGisSession() {
    return this.postGisSession;
  }

  public AbstractLayerGroup getLayerGroup() {
    return this.layerGroup;
  }

  public void setLayerGroup(AbstractLayerGroup layerGroup) {
    this.layerGroup = layerGroup;
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

    // The Display window
    Element centerElem = xmlDoc.createElement("geo-center");
    root.appendChild(centerElem);
    Element xElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getGeoCenter().getX()));
    xElem.appendChild(n);
    centerElem.appendChild(xElem);
    Element yElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getGeoCenter().getY()));
    yElem.appendChild(n);
    centerElem.appendChild(yElem);
    Element envElem = xmlDoc.createElement("envelope");
    root.appendChild(envElem);
    Element lCornerElem = xmlDoc.createElement("lower-corner");
    envElem.appendChild(lCornerElem);
    Element xlcElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getLowerCorner().getX()));
    xlcElem.appendChild(n);
    lCornerElem.appendChild(xlcElem);
    Element ylcElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getLowerCorner().getY()));
    ylcElem.appendChild(n);
    lCornerElem.appendChild(ylcElem);
    Element uCornerElem = xmlDoc.createElement("upper-corner");
    envElem.appendChild(uCornerElem);
    Element xucElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getUpperCorner().getX()));
    xucElem.appendChild(n);
    uCornerElem.appendChild(xucElem);
    Element yucElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getUpperCorner().getY()));
    yucElem.appendChild(n);
    uCornerElem.appendChild(yucElem);

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
    xmlPath.append("src/main/resources/xml/Cartagen_dbs/");
    Element dbsElem = xmlDoc.createElement("databases");
    root.appendChild(dbsElem);
    for (CartAGenDB db : this.databases.values()) {
      File dbFile = db.getXmlFile();
      String path = null;
      if (dbFile == null) {
        path = xmlPath.append(db.getName() + ".xml").toString();
        dbFile = new File(path);
      } else {
        path = dbFile.getPath();
      }
      // System.out.println(dbFile.toString());
      // System.out.println(((ShapeFileDB) db).getSystemPath());
      db.saveToXml(dbFile);
      // store the path in this file
      Element dbElem = xmlDoc.createElement("database");
      n = xmlDoc.createTextNode(path);
      dbElem.appendChild(n);
      dbsElem.appendChild(dbElem);
    }

    // The Document LayerGroup
    Element layerGroupElem = xmlDoc.createElement("layer-group");
    n = xmlDoc.createTextNode(this.getLayerGroup().getClass().getName());
    layerGroupElem.appendChild(n);
    root.appendChild(layerGroupElem);

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
  public static CartAGenDocOld loadDocFromXml(File file)
      throws ParserConfigurationException, SAXException, IOException,
      SecurityException, NoSuchMethodException, ClassNotFoundException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    CartAGenDocOld.instance = new CartAGenDocOld();
    CartAGenDocOld.instance.setXmlFile(file);

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
    CartAGenDocOld.instance.setName(nameElem.getChildNodes().item(0)
        .getNodeValue());
    CartAGenDocOld.instance.postGisDb = PostgisDB.get(
        CartAGenDocOld.instance.getName(), true);

    // the geoCenter
    Element centerElem = (Element) root.getElementsByTagName("geo-center")
        .item(0);
    Element xCenterElem = (Element) centerElem.getElementsByTagName("x")
        .item(0);
    double xCenter = Double.valueOf(xCenterElem.getChildNodes().item(0)
        .getNodeValue());
    Element yCenterElem = (Element) centerElem.getElementsByTagName("y")
        .item(0);
    double yCenter = Double.valueOf(yCenterElem.getChildNodes().item(0)
        .getNodeValue());
    CartAGenDocOld.instance.setGeoCenter(new DirectPosition(xCenter, yCenter));

    // the display envelope
    Element envElem = (Element) root.getElementsByTagName("envelope").item(0);
    Element lCornerElem = (Element) envElem
        .getElementsByTagName("lower-corner").item(0);
    Element xlcElem = (Element) lCornerElem.getElementsByTagName("x").item(0);
    double xlCorner = Double.valueOf(xlcElem.getChildNodes().item(0)
        .getNodeValue());
    Element ylcElem = (Element) lCornerElem.getElementsByTagName("y").item(0);
    double ylCorner = Double.valueOf(ylcElem.getChildNodes().item(0)
        .getNodeValue());
    Element uCornerElem = (Element) envElem
        .getElementsByTagName("upper-corner").item(0);
    Element xucElem = (Element) uCornerElem.getElementsByTagName("x").item(0);
    double xuCorner = Double.valueOf(xucElem.getChildNodes().item(0)
        .getNodeValue());
    Element yucElem = (Element) uCornerElem.getElementsByTagName("y").item(0);
    double yuCorner = Double.valueOf(yucElem.getChildNodes().item(0)
        .getNodeValue());
    IDirectPosition lCorner = new DirectPosition(xlCorner, ylCorner);
    IDirectPosition uCorner = new DirectPosition(xuCorner, yuCorner);
    CartAGenDocOld.instance
        .setDisplayEnvelope(new GM_Envelope(uCorner, lCorner));

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
    CartAGenDocOld.instance.setZone(new DataSetZone(zoneName, extent));

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
      CartAGenDocOld.instance.databases.put(database.getName(), database);
      // populate the dataset of the loaded database
      Class<?> datasetClass = CartAGenDB.readDatasetType(dbFile);
      CartAGenDataSet dataset = (CartAGenDataSet) datasetClass.getConstructor()
          .newInstance();
      database.setDataSet(dataset);
      CartAGenDocOld.instance.currentDataset = dataset;
      database.populateDataset(database.getSymboScale());
      Legend.setSYMBOLISATI0N_SCALE(database.getSymboScale());
    }
    if (root.getElementsByTagName("layer-group").getLength() != 0) {
      Element layerGroupElem = (Element) root.getElementsByTagName(
          "layer-group").item(0);
      String layerGroupName = layerGroupElem.getChildNodes().item(0)
          .getNodeValue();
      AbstractLayerGroup layerGroup = (AbstractLayerGroup) Class
          .forName(layerGroupName).getConstructor().newInstance();
      CartAGenDocOld.instance.setLayerGroup(layerGroup);
    }

    return CartAGenDocOld.instance;
  }

  /**
   * Saves the current display window into the document.
   * @param application
   */
  public void saveWindow(CartagenApplication application) {
    this.geoCenter = application.getFrame().getVisuPanel().getGeoCenter();
    this.displayEnvelope = application.getFrame().getVisuPanel()
        .getDisplayEnvelope();
  }

  /**
   * Get the dataset of the database named 'name'.
   * @param name1
   * @return
   */
  public CartAGenDataSet getDataset(String name1) {
    if (this.databases.containsKey(name1)) {
      return this.databases.get(name1).getDataSet();
    }
    return null;
  }

  /**
   * Checks if there is a database that has a given {@link SourceDLM} type in
   * the databases of {@code this}.
   * @param source
   * @return
   */
  public boolean containsSourceDLM(SourceDLM source) {
    for (CartAGenDB db : this.databases.values()) {
      if (db.getSourceDLM().equals(source)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if there is a database named 'name1'.
   * @param name1
   * @return
   */
  public boolean hasDataset(String name1) {
    CartAGenDB dataset = this.databases.get(name1);
    if (dataset == null) {
      return false;
    }
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
