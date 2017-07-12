/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.landmarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ign.cogit.cartagen.spatialanalysis.learningdescriptor.LearningDescriptor;
import fr.ign.cogit.geoxygene.util.XMLUtil;

public class LandmarksFinderTrainer {

  private List<Map<String, Double>> examples;
  private List<Boolean> responses;
  private Set<LearningDescriptor> descriptors;

  public LandmarksFinderTrainer(Set<LearningDescriptor> descriptors, File file)
      throws ParserConfigurationException, SAXException, IOException {
    this.descriptors = descriptors;
    this.examples = new ArrayList<>();
    this.responses = new ArrayList<>();

    // load the file
    if (file.exists()) {
      if (file.length() < 5000000)
        this.DOMLoader(file);
      else {
        System.out.println("use the SAX reader");
        this.SAXLoader(file);
      }
    }
  }

  private void SAXLoader(File file)
      throws ParserConfigurationException, SAXException, IOException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();

    DefaultHandler handler = new LandmarkResourceHandler(this);
    parser.parse(file, handler);
    System.out.println(this.examples.size() + " examples parsed in the file");
  }

  private void DOMLoader(File file)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    // parse the file
    Element root = (Element) doc.getElementsByTagName("training-dataset")
        .item(0);
    for (int i = 0; i < root.getElementsByTagName("example").getLength(); i++) {
      Element exElem = (Element) root.getElementsByTagName("example").item(i);
      Element classElem = (Element) exElem.getElementsByTagName("response")
          .item(0);
      Boolean response = Boolean
          .valueOf(classElem.getChildNodes().item(0).getNodeValue());
      Map<String, Double> map = new HashMap<>();
      Element descrsElem = (Element) exElem.getElementsByTagName("descriptors")
          .item(0);
      for (int j = 0; j < descrsElem.getElementsByTagName("descriptor")
          .getLength(); j++) {
        Element descrElem = (Element) descrsElem
            .getElementsByTagName("descriptor").item(j);
        Element nameElem = (Element) descrElem.getElementsByTagName("name")
            .item(0);
        String name = nameElem.getChildNodes().item(0).getNodeValue();
        Element valueElem = (Element) descrElem.getElementsByTagName("value")
            .item(0);
        Double value = Double
            .valueOf(valueElem.getChildNodes().item(0).getNodeValue());
        if (isDescriptorUsed(name))
          map.put(name, value);
      }
      this.examples.add(map);
      this.responses.add(response);
    }
  }

  public int[] getResponses() {
    int[] result = new int[responses.size()];
    for (int i = 0; i < responses.size(); i++) {
      if (responses.get(i))
        result[i] = 1;
      else
        result[i] = 0;
    }
    return result;
  }

  public void setResponses(int[] responses) {
    this.responses = new ArrayList<>();
    for (int i = 0; i < responses.length; i++) {
      if (responses[i] == 1)
        this.responses.add(true);
      else
        this.responses.add(false);
    }
  }

  public double[][] getExamples() {
    double[][] result = new double[examples.size()][descriptors.size()];
    for (int i = 0; i < examples.size(); i++) {
      int j = 0;
      for (LearningDescriptor descr : descriptors) {
        result[i][j] = examples.get(i).get(descr.getName());
        j++;
      }
    }
    return result;
  }

  public void writeToXml(File file) throws TransformerException, IOException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("training-dataset");

    for (int i = 0; i < examples.size(); i++) {
      Map<String, Double> example = examples.get(i);
      Element exElem = xmlDoc.createElement("example");
      root.appendChild(exElem);
      Element classElem = xmlDoc.createElement("response");
      n = xmlDoc.createTextNode(responses.get(i).toString());
      classElem.appendChild(n);
      exElem.appendChild(classElem);
      Element descrsElem = xmlDoc.createElement("descriptors");
      exElem.appendChild(descrsElem);
      for (LearningDescriptor descr : descriptors) {
        Element descrElem = xmlDoc.createElement("descriptor");
        descrsElem.appendChild(descrElem);
        Element nameElem = xmlDoc.createElement("name");
        n = xmlDoc.createTextNode(descr.getName());
        nameElem.appendChild(n);
        descrElem.appendChild(nameElem);
        Element valueElem = xmlDoc.createElement("value");
        n = xmlDoc.createTextNode(example.get(descr.getName()).toString());
        valueElem.appendChild(n);
        descrElem.appendChild(valueElem);
      }
    }

    // File writing
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  public boolean isDescriptorUsed(String descrName) {
    for (LearningDescriptor descr : this.descriptors) {
      if (descr.getName().equals(descrName))
        return true;
    }
    return false;
  }

  public void addExample(Map<String, Double> descrValues, Boolean response) {
    this.examples.add(descrValues);
    this.responses.add(response);
  }

  public class LandmarkResourceHandler extends DefaultHandler {

    private LandmarksFinderTrainer trainer;
    private StringBuffer buffer;
    private Double value;
    private String name;
    private Map<String, Double> map = new HashMap<>();
    private boolean response;

    public LandmarkResourceHandler(LandmarksFinderTrainer trainer) {
      super();
      this.trainer = trainer;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException {
      buffer = new StringBuffer();
      super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException {
      String lecture = new String(ch, start, length);
      if (buffer != null)
        buffer.append(lecture);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException {

      if (qName.equals("example")) {
        trainer.examples.add(new HashMap<>(map));
        map.clear();
      } else if (qName.equals("response")) {
        response = new Boolean(buffer.toString());
        trainer.responses.add(response);
      } else if (qName.equals("descriptor")) {
        if (isDescriptorUsed(name))
          map.put(name, value);
      } else if (qName.equals("name")) {
        name = buffer.toString();
      } else if (qName.equals("value")) {
        value = new Double(buffer.toString());
      } else {
        // do nothing
      }
    }

  }

}
