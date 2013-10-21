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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public abstract class CartAGenDB {

  /**
   * List of the geographic classes that the data set is composed of. Geographic
   * classes can be ShapeFiles, PostGIS table, etc.
   */
  private List<GeographicClass> classes;

  /**
   * The enrichments added to the dataset.
   */
  protected Set<CartAGenEnrichment> enrichments = new HashSet<CartAGenEnrichment>();

  /**
   * The current GeneralisationDataSet object that gathers all the geo objects.
   */
  private CartAGenDataSet dataSet;

  /**
   * The DB type of the CartAGen database, e.g. DLM or DCM.
   */
  private DBType type;

  /**
   * The document this database is opened in.
   */
  private CartAGenDocOld document;

  /**
   * The source DLM the database derives from (e.g. BD_CARTO).
   */
  private SourceDLM sourceDLM;

  /**
   * The name of the dataset.
   */
  private String name;

  /**
   * The xml File that stores the database information
   */
  private File xmlFile = null;

  private int symboScale;

  /**
   * The persistent classes in this database.
   */
  private Set<Class<?>> persistentClasses;
  private boolean persistent = false;

  /**
   * The {@link GeneObjImplementation} for {@code this} database. e. g. the
   * default implementation, i.e. the classes extending {@link GeneObjDefault}.
   */
  private GeneObjImplementation geneObjImpl;

  /**
   * Constructeur à partir d'un fichier XML détaillant les classes à intégrer au
   * jeux de données ainsi que les détails de la zone. <en> Constructor from the
   * XML file storing the classes to integrate to the dataset and the details of
   * the zone.
   * @param file
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ClassNotFoundException
   */
  public CartAGenDB(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    openFromXml(file);
  }

  public CartAGenDB() {
    this.geneObjImpl = CartagenApplication.getInstance()
        .getStandardImplementation();
  }

  /**
   * Fills the zone and classes fields of the data set from XML file.
   * @param file
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ClassNotFoundException
   */
  public abstract void openFromXml(File file)
      throws ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException;

  /**
   * Stores the data set into XML file.
   * @param file
   * @throws IOException
   * @throws TransformerException
   */
  public abstract void saveToXml(File file) throws IOException,
      TransformerException;

  /**
   * Populate the Generalisation Dataset related to this by importing data
   * according to the classes.
   */
  public abstract void populateDataset(int scale);

  /**
   * Load a geographic class into the dataset using the appropriate loading
   * method
   * @param geoClass
   */
  protected abstract void load(GeographicClass geoClass, int scale);

  /**
   * Overwrite a geographic class into the dataset re-importing the initial
   * data.
   * @param geoClass
   */
  public abstract void overwrite(GeographicClass geoClass);

  /**
   * Trigger the automatic enrichments that were stored as non persistent.
   */
  protected abstract void triggerEnrichments();

  /**
   * Add the generated cartagen id to the files storing geo data (e.g.
   * shapefiles...)
   */
  public abstract void addCartagenId();

  /**
   * Load the features of the persistent classes into the dataset related to
   * {@code this} {@link ShapeFileDB}
   * @throws NoSuchFieldException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("unchecked")
  protected Set<Class<?>> loadPersistentClasses() throws SecurityException,
      NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
      HibernateException {
    // open a connection with the current PostGISDB
    AnnotationConfiguration hibConfig = new AnnotationConfiguration();
    hibConfig = hibConfig.configure(new File(PostgisDB.class.getResource(
        PostgisDB.getDefaultConfigPath()).getFile()));
    hibConfig.setProperty("hibernate.connection.url", PostgisDB.getUrl());

    // loop on the persistent classes
    Set<Class<?>> displayedClasses = new HashSet<Class<?>>();
    HashSet<IGeneObj> persistObjs = new HashSet<IGeneObj>();
    for (Class<?> classObj : getPersistentClasses()) {
      // TODO only the cartagen geo classes are loaded for now
      if (!IGeneObj.class.isAssignableFrom(classObj))
        continue;
      if (!classObj.isAnnotationPresent(Entity.class))
        continue;
      // first, get the population related to classObj
      String popName = this.getDataSet().getPopNameFromClass(classObj);
      Field field = classObj.getField("FEAT_TYPE_NAME");
      String featType = (String) field.get(null);
      IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) this.getDataSet()
          .getCartagenPop(popName, featType);
      // then, get the features stored in PostGIS
      hibConfig.addAnnotatedClass(classObj);
      Session session = hibConfig.buildSessionFactory().openSession(
          PostgisDB.getConnection());
      CartAGenDocOld.getInstance().setPostGisSession(session);

      // query the objects of this class in this DB
      Query q = session.createQuery("from " + classObj.getSimpleName());
      for (Object o : q.list()) {
        IGeneObj geneObj = (IGeneObj) o;
        if (geneObj.getDbName().equals(getName())) {
          if (!displayedClasses.contains(geneObj.getClass().getSimpleName())) {
            displayedClasses.add(geneObj.getClass());
          }
          pop.add(geneObj);
          persistObjs.add(geneObj);
        }
      }
    }

    // fill the relations between the persistent objects
    for (IGeneObj obj : persistObjs) {
      try {
        obj.fillRelationsFromIds();
        obj.restoreGeoxObjects();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    // fill the relations between the geoxygene objects
    for (IGeneObj obj : persistObjs) {
      obj.restoreGeoxRelations();
    }

    return displayedClasses;
  }

  public void setClasses(List<GeographicClass> classes) {
    this.classes = classes;
  }

  public List<GeographicClass> getClasses() {
    return classes;
  }

  public void setDataSet(CartAGenDataSet genDataSet) {
    this.dataSet = genDataSet;
    if (genDataSet.getCartAGenDB() == null)
      genDataSet.setCartAGenDB(this);
    else if (!genDataSet.getCartAGenDB().equals(this))
      genDataSet.setCartAGenDB(this);
  }

  public CartAGenDataSet getDataSet() {
    return dataSet;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public DBType getType() {
    return type;
  }

  public void setType(DBType type) {
    this.type = type;
  }

  public CartAGenDocOld getDocument() {
    return document;
  }

  public void setDocument(CartAGenDocOld document) {
    this.document = document;
  }

  public SourceDLM getSourceDLM() {
    return sourceDLM;
  }

  public void setSourceDLM(SourceDLM sourceDLM) {
    this.sourceDLM = sourceDLM;
  }

  public Set<CartAGenEnrichment> getEnrichments() {
    return enrichments;
  }

  public void setEnrichments(Set<CartAGenEnrichment> enrichments) {
    this.enrichments = enrichments;
  }

  public void setXmlFile(File xmlFile) {
    this.xmlFile = xmlFile;
  }

  public File getXmlFile() {
    return xmlFile;
  }

  public void setSymboScale(int symboScale) {
    this.symboScale = symboScale;
  }

  public int getSymboScale() {
    return symboScale;
  }

  public void setPersistentClasses(Set<Class<?>> persistentClasses) {
    this.persistentClasses = persistentClasses;
  }

  public Set<Class<?>> getPersistentClasses() {
    return persistentClasses;
  }

  public void setPersistent(boolean persistent) {
    this.persistent = persistent;
  }

  public boolean isPersistent() {
    return persistent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((classes == null) ? 0 : classes.hashCode());
    result = prime * result + ((dataSet == null) ? 0 : dataSet.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CartAGenDB other = (CartAGenDB) obj;
    if (classes == null) {
      if (other.classes != null)
        return false;
    } else if (!classes.equals(other.classes))
      return false;
    if (dataSet == null) {
      if (other.dataSet != null)
        return false;
    } else if (!dataSet.equals(other.dataSet))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  /**
   * Give the Class of a CartAGenDataSet stored in the input XML file.
   * @param file
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends CartAGenDB> readType(File file)
      throws ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
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
    return (Class<? extends CartAGenDB>) Class.forName(type);
  }

  /**
   * Give the Class of a CartAGenDataSet stored in the input XML file.
   * @param file
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ClassNotFoundException
   */
  public static Class<?> readDatasetType(File file)
      throws ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
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
    if (root.getElementsByTagName("dataset-type").getLength() == 0)
      return CartAGenDataSet.class;
    Element typeElem = (Element) root.getElementsByTagName("dataset-type")
        .item(0);
    String type = typeElem.getChildNodes().item(0).getNodeValue();
    return (Class<?>) Class.forName(type);
  }

  public static int readScale(File file) throws ParserConfigurationException,
      SAXException, IOException {
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
    Element scaleElem = (Element) root.getElementsByTagName("scale").item(0);
    return Integer.valueOf(scaleElem.getChildNodes().item(0).getNodeValue());
  }

  @Override
  public String toString() {
    return name;
  }

  public void setGeneObjImpl(GeneObjImplementation geneObjImpl) {
    this.geneObjImpl = geneObjImpl;
  }

  public GeneObjImplementation getGeneObjImpl() {
    return geneObjImpl;
  }

}
