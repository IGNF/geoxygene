package fr.ign.cogit.osm.importexport;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

import fr.ign.cogit.geoxygene.http.OsmXmlHttpClient;
import fr.ign.cogit.geoxygene.osm.importexport.OsmXmlParser;

/**
 *
 */
public class OsmXmlLoaderTest {
  
  @Test
  public void testHttpRequestWay() {
    System.out.println("=== Way ===");
    try {
      String data = "way(50.746,7.154,50.748,7.157);out body;";
      String xml = OsmXmlHttpClient.getOsmXML(data, true);
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      
      OsmXmlParser loader = new OsmXmlParser();
      loader.loadOsm(doc);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testHttpRequestNode() {
    System.out.println("=== Noeud ===");
    try {
      String data = "node(50.746,7.154,50.748,7.157);out body;";
      String xml = OsmXmlHttpClient.getOsmXML(data, true);
            
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      
      OsmXmlParser loader = new OsmXmlParser();
      loader.loadOsm(doc);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testHttpRequestAll() {
    System.out.println("=== Tout ===");
    try {
      String data = "area[name=\"Hoogstade\"];(node(area);<;);out meta qt;";
      String xml = OsmXmlHttpClient.getOsmXML(data, true);
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      
      OsmXmlParser loader = new OsmXmlParser();
      loader.loadOsm(doc);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
