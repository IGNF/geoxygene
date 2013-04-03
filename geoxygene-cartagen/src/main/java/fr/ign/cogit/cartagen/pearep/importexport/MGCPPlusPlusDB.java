package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.software.dataset.CartAGenEnrichment;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.util.XMLUtil;

public class MGCPPlusPlusDB extends ShapeFileDB {

  private List<String> classes;
  private String projEpsg;

  public MGCPPlusPlusDB(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    super(file);
    this.setGeneObjImpl(new GeneObjImplementation("mgcp++", MGCPFeature.class
        .getPackage(), MGCPFeature.class));
  }

  public MGCPPlusPlusDB(String name) {
    super(name);
    this.classes = new ArrayList<String>();
    this.setGeneObjImpl(new GeneObjImplementation("mgcp++", MGCPFeature.class
        .getPackage(), MGCPFeature.class));
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

    // The projection EPSG
    Element projElem = (Element) root.getElementsByTagName("projection-epsg")
        .item(0);
    this.setProjEpsg(projElem.getChildNodes().item(0).getNodeValue());

    // the list of classes
    this.classes = new ArrayList<String>();
    Element classesElem = (Element) root.getElementsByTagName("classes-list")
        .item(0);
    for (int i = 0; i < classesElem.getElementsByTagName("class").getLength(); i++) {
      Element classElem = (Element) classesElem.getElementsByTagName("class")
          .item(i);
      String className = classElem.getChildNodes().item(0).getNodeValue();
      this.classes.add(className);
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
    this.setGeneObjImpl(new GeneObjImplementation(implName, rootPackage,
        rootClass));

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
    // The DataBase type
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
    for (String c : this.classes) {
      Element classeElem = xmlDoc.createElement("class");
      n = xmlDoc.createTextNode(c);
      classeElem.appendChild(n);
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
   * @param className the name of the shapefile without ".shp"
   */
  public void addShapeFile(String className) {
    this.classes.add(className);
  }

  @Override
  public void populateDataset(int scale) {

    MGCPLoader loader = new MGCPLoader();
    loader.setDataset(MGCPPlusPlusDB.this.getDataSet());
    try {
      loader.loadData(new File(this.getSystemPath()), this.classes);
    } catch (ShapefileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getProjEpsg() {
    return projEpsg;
  }

  public void setProjEpsg(String projEpsg) {
    this.projEpsg = projEpsg;
  }

}
