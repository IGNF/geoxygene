/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.ign.cogit.geoxygene.util.XMLUtil;

public class LastSessionParameters {

  private Set<SessionParameter> parameters = new HashSet<SessionParameter>();
  private File file;

  private static final String filePath = "src/main/resources/xml/last_session_parameters.xml";
  private static LastSessionParameters instance = null;

  protected LastSessionParameters() {
    // Exists only to defeat instantiation.
    this.parseXML();
  }

  public static LastSessionParameters getInstance() {
    if (LastSessionParameters.instance == null) {
      LastSessionParameters.instance = new LastSessionParameters();
    }
    return LastSessionParameters.instance;
  }

  private void parseXML() {
    this.file = new File(LastSessionParameters.filePath);
    // case where the file does not exist
    if (!file.exists())
      try {
        file.createNewFile();
        writeToXml();
        return;
      } catch (IOException e1) {
        e1.printStackTrace();
      } catch (TransformerException e) {
        e.printStackTrace();
      }
    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new FileInputStream(this.file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      return;
    }

    Element root = (Element) docXML.getElementsByTagName(
        "cartagen-last-session-params").item(0);
    if (root.getElementsByTagName("parameter").getLength() != 0) {
      for (int i = 0; i < root.getElementsByTagName("parameter").getLength(); i++) {
        Element paramElem = (Element) root.getElementsByTagName("parameter")
            .item(i);
        Object value = paramElem.getChildNodes().item(0).getNodeValue();
        Map<String, String> attributes = new HashMap<String, String>();
        for (int j = 0; j < paramElem.getAttributes().getLength(); j++) {
          attributes.put(paramElem.getAttributes().item(j).getNodeName(),
              paramElem.getAttributes().item(j).getNodeValue());
        }
        this.parameters.add(new SessionParameter(
            paramElem.getAttribute("name"), value, attributes));
      }
    }
  }

  private void writeToXml() throws TransformerException, IOException {
    // build the DOM document
    Document xmlDoc = new DocumentImpl();
    Element root = xmlDoc.createElement("cartagen-last-session-params");
    for (SessionParameter param : this.parameters) {
      Element paramElem = xmlDoc.createElement("parameter");
      Node n = xmlDoc.createTextNode(param.getValue().toString());
      paramElem.appendChild(n);
      paramElem.setAttribute("name", param.getName());
      for (String attr : param.getAttributes().keySet()) {
        paramElem.setAttribute(attr, param.getAttributes().get(attr));
      }
      root.appendChild(paramElem);
    }
    xmlDoc.appendChild(root);
    // write to XML file
    XMLUtil.writeDocumentToXml(xmlDoc, this.file);
  }

  public void setParameter(String name, Object value,
      Map<String, String> attributes) throws TransformerException, IOException {
    SessionParameter param = new SessionParameter(name, value, attributes);
    if (parameters.contains(param))
      parameters.remove(param);
    this.parameters.add(param);
    this.writeToXml();
  }

  public Object getParameterValue(String name) {
    for (SessionParameter param : parameters) {
      if (param.getName().equals(name))
        return param.getValue();
    }
    return null;
  }

  public Map<String, String> getParameterAttributes(String name) {
    for (SessionParameter param : parameters) {
      if (param.getName().equals(name))
        return param.getAttributes();
    }
    return null;
  }

  /**
   * Checks if a given parameter has been stored in last session.
   * @param name
   * @return
   */
  public boolean hasParameter(String name) {
    for (SessionParameter param : parameters) {
      if (param.getName().equals(name))
        return true;
    }
    return false;
  }

  /**
   * a parameter for a GeOxygene session, that is stored in XML and loaded when
   * the application is launched.
   * @author GTouya
   * 
   */
  public class SessionParameter {
    private String name;
    private Object value;
    private Map<String, String> attributes;

    public SessionParameter(String name, Object value,
        Map<String, String> attributes) {
      super();
      this.setName(name);
      this.setValue(value);
      this.setAttributes(attributes);
    }

    public Map<String, String> getAttributes() {
      return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
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
      SessionParameter other = (SessionParameter) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }

    private LastSessionParameters getOuterType() {
      return LastSessionParameters.this;
    }

  }
}
