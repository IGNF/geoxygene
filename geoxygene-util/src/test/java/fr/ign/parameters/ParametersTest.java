package fr.ign.parameters;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * 
 * 
 *
 */
public class ParametersTest extends XMLTestCase {
  
  private Schema PARAMETERS_SCHEMA = null; 

  public void setUp() {
    try {
      PARAMETERS_SCHEMA = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
          .newSchema(new File(Parameters.class.getClassLoader().getResource("parameters.xsd").getPath()));
    } catch (Exception e) {
      fail();
    }
  }
  
  @Test
  public void testSetGetParameter() {
    Parameters p = new Parameters();
    p.set("A", "B");
    String result = p.get("A").toString();
    assertEquals("B", result);
  }
  
  @Test 
  public void testUnmarshallFileWithoutValidation() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("test1.xml").getPath()));
      String result = p.get("A").toString();
      Assert.assertEquals("B", result);
      result = p.get("C").toString();
      Assert.assertEquals("D", result);
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test 
  public void testUnmarshallFileWithValidation() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("test1.xml").getPath()), "parameters.xsd");
      String result = p.get("A").toString();
      assertEquals("B", result);
      result = p.get("C").toString();
      assertEquals("D", result);
    } catch(Exception e) {
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallBuildingParameterFile() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("building_parameters.xml").getPath()));
      String result = p.get("A").toString();
      assertEquals("B", result);
      result = p.get("C").toString();
      assertEquals("D", result);
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallString() {
    StringBuffer xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters p = Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      assertEquals("B", p.get("A").toString());
      assertEquals("D", p.get("C").toString());
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
    
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"D\"/>");
    xmlResult.append("<param key=\"B\" value=\"E\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters p = Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      assertEquals("D", p.get("A").toString());
      assertEquals("E", p.get("B").toString());
      assertEquals("D", p.get("C").toString());
    } catch(Exception e) {
      fail();
    }
  }
  
  @Test 
  public void testNonValidXml() {
    
    // Test : other attributes in param tag
    StringBuffer xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key2=\"C\" value2=\"D\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : two parameters with the same key
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"A\" value=\"D\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : parameter without key element
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param value=\"B\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : parameter without value element
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameter-configuration>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"toto\"/>");
    xmlResult.append("</parameters>");
    xmlResult.append("</parameter-configuration>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : without root tag
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
    xmlResult.append("</parameters>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
  }
  
  @Test
  public void testMarshall() {
    Parameters p = new Parameters();
    p.set("A", "B");
    p.set("C", "D");
    p.marshall("target/test-classes/test2.xml");
    
    // test.xml =? test2.xml
    try {
      
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      
      File xml1 = new File(ParametersTest.class.getClassLoader().getResource("test1.xml").getPath());
      Document doc1 = db.parse(xml1);
      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 3);
      
      File xml2 = new File("target/test-classes/test2.xml");
      Document doc2 = db.parse(xml2);
      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 3);
      
      assertXMLEqual(doc1, doc2);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
