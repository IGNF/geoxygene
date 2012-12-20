/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LastSessionParameters {

  private Map<String, Object> parameters = new HashMap<String, Object>();
  private File file;

  private static final String filePath = "src/main/resources/XML/last_session_parameters.xml";
  private static LastSessionParameters instance = null;

  protected LastSessionParameters() {
    // Exists only to defeat instantiation.
    parseXML();
  }

  public static LastSessionParameters getInstance() {
    if (instance == null) {
      instance = new LastSessionParameters();
    }
    return instance;
  }

  private void parseXML() {
    file = new File(filePath);
    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
          new FileInputStream(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Element root = (Element) docXML.getElementsByTagName(
        "cartagen-last-session-params").item(0);
    if (root.getElementsByTagName("parameter").getLength() != 0) {
      for (int i = 0; i < root.getElementsByTagName("parameter").getLength(); i++) {
        Element paramElem = (Element) root.getElementsByTagName("parameter")
            .item(i);
        Object value = paramElem.getChildNodes().item(0).getNodeValue();
        this.parameters.put(paramElem.getAttribute("name"), value);
      }
    }
  }

  private void writeToXml() throws TransformerException, IOException {
    // build the DOM document
    Document xmlDoc = new DocumentImpl();
    Element root = xmlDoc.createElement("cartagen-last-session-params");
    for (String name : parameters.keySet()) {
      Element paramElem = xmlDoc.createElement("parameter");
      Node n = xmlDoc.createTextNode(parameters.get(name).toString());
      paramElem.appendChild(n);
      paramElem.setAttribute("name", name);
      root.appendChild(paramElem);
    }
    xmlDoc.appendChild(root);
    // write to XML file
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  public void setParameter(String name, Object value)
      throws TransformerException, IOException {
    parameters.put(name, value);
    writeToXml();
  }

  public Object getParameter(String name) {
    return this.parameters.get(name);
  }
}
