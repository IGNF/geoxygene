package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenEnrichment;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class PeaRepDB extends ShapeFileDB {

  private List<String> loadedClasses;
  private String projEpsg;

  public PeaRepDB(File file) throws ParserConfigurationException, SAXException,
      IOException, ClassNotFoundException {
    super(file);
  }

  public PeaRepDB(String name) {
    super(name);
    this.loadedClasses = new ArrayList<String>();
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
    this.setSymboScale(Double.valueOf(
        scaleElem.getChildNodes().item(0).getNodeValue()).intValue());

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
    this.loadedClasses = new ArrayList<String>();
    Element classesElem = (Element) root.getElementsByTagName("classes-list")
        .item(0);
    for (int i = 0; i < classesElem.getElementsByTagName("class").getLength(); i++) {
      Element classElem = (Element) classesElem.getElementsByTagName("class")
          .item(i);
      String className = classElem.getChildNodes().item(0).getNodeValue();
      this.loadedClasses.add(className);
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
    n = xmlDoc.createTextNode(String.valueOf(Legend.getSYMBOLISATI0N_SCALE()));
    scaleElem.appendChild(n);
    root.appendChild(scaleElem);
    // The symbolisation scale
    Element projElem = xmlDoc.createElement("projection-epsg");
    n = xmlDoc.createTextNode(String.valueOf(this.getProjEpsg()));
    projElem.appendChild(n);
    root.appendChild(projElem);

    // The source DLM
    Element dlmElem = xmlDoc.createElement("source-dlm");
    n = xmlDoc.createTextNode(this.getSourceDLM().name());
    dlmElem.appendChild(n);
    root.appendChild(dlmElem);

    // the list of classes
    Element classesElem = xmlDoc.createElement("classes-list");
    for (String c : this.loadedClasses) {
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
   * @param className the name of the shapefile without ".shp"
   */
  public void addShapeFile(String className) {
    this.loadedClasses.add(className);
  }

  public String getProjEpsg() {
    return projEpsg;
  }

  public void setProjEpsg(String projEpsg) {
    this.projEpsg = projEpsg;
  }

  public List<String> getLoadedClasses() {
    return loadedClasses;
  }

  public void setLoadedClasses(List<String> loadedClasses) {
    this.loadedClasses = loadedClasses;
  }

  public boolean isDbFile(File file) {
    int indexPt = file.getName().lastIndexOf(".");
    String extension = file.getName().substring(indexPt + 1);
    if (!extension.equals("shp"))
      return false;
    return true;
  }

}
