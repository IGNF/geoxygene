package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExportPostGISMappingXMLParser {

  private File xmlFile;

  public ExportPostGISMappingXMLParser(File xmlFile) {
    super();
    this.xmlFile = xmlFile;
  }

  /**
   * Parse the XML File to produce a {@link CalacMapping} instance, if the file
   * has the correct structure.
   * @return
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public Hashtable<String, ArrayList<ArrayList<String>>> parsePostGISMapping()
      throws ParserConfigurationException, SAXException, IOException {

    // open the xml file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    org.w3c.dom.Document doc;
    doc = db.parse(this.xmlFile);
    doc.getDocumentElement().normalize();

    // get the root of the XML document
    Element root = (Element) doc.getElementsByTagName("mapping").item(0);
    Hashtable<String, ArrayList<ArrayList<String>>> popWithAttributes = new Hashtable<String, ArrayList<ArrayList<String>>>();

    // for every matching
    for (int itMatchings = 0; itMatchings < root.getElementsByTagName(
        "matching").getLength(); itMatchings++) {
      Element matching = (Element) root.getElementsByTagName("matching").item(
          itMatchings);
      // get matching properties
      String pop = matching.getElementsByTagName("population").item(0)
          .getTextContent();
      // get matching attributes mapping
      Element attributes = (Element) matching
          .getElementsByTagName("attributes").item(0);
      // get and store each attribute mapping
      ArrayList<ArrayList<String>> attrMappingStorage = new ArrayList<ArrayList<String>>();
      for (int itAttrMapping = 0; itAttrMapping < attributes
          .getElementsByTagName("attribute").getLength(); itAttrMapping++) {
        Element attribute = (Element) attributes.getElementsByTagName(
            "attribute").item(itAttrMapping);
        ArrayList<String> attrMapping = new ArrayList<String>();
        String postgisAttr = attribute.getElementsByTagName("postgisAttr")
            .item(0).getTextContent();
        String javaAttr = attribute.getElementsByTagName("javaAttr").item(0)
            .getTextContent();
        String type = attribute.getElementsByTagName("type").item(0)
            .getTextContent();
        attrMapping.add(postgisAttr);
        attrMapping.add(javaAttr);
        attrMapping.add(type);
        attrMappingStorage.add(attrMapping);
      }
      popWithAttributes.put(pop, attrMappingStorage);

    }
    return popWithAttributes;
  }
}
