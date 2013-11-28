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
  public void testSetParameter() {
    Parameters p = new Parameters();
    p.set("A", "B");
    assertEquals(p.get("A").toString(), "B");
  }
  
  @Test 
  public void testUnmarshallFileWithoutValidation() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("simple.xml").getPath()));
      Assert.assertEquals(p.get("A").toString(), "B");
      Assert.assertEquals(p.get("C").toString(), "D");
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test 
  public void testUnmarshallFileWithValidation() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("simple.xml").getPath()), PARAMETERS_SCHEMA);
      assertEquals(p.get("A").toString(), "B");
      assertEquals(p.get("C").toString(), "D");
    } catch(Exception e) {
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallBuildingParameterFile() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("building_parameters.xml").getPath()));
      assertEquals(p.get("A").toString(), "B");
      // String
      assertEquals(p.getString("C"), "D");
      // Boolean
      assertEquals(p.getBoolean("bool"), true);
      // Double
      assertEquals(p.getDouble("pbirth"), 0.1);
      // Integer
      assertEquals(p.getInteger("poisson"), 200);
      // Float
      assertEquals(p.getFloat("sigmaD"), 1f);
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallString() {
    StringBuffer xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
    xmlResult.append("</parameters>");
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
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"D\"/>");
    xmlResult.append("<param key=\"B\" value=\"E\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
    xmlResult.append("</parameters>");
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
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key2=\"C\" value2=\"D\"/>");
    xmlResult.append("</parameters>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : two parameters with the same key
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"A\" value=\"D\"/>");
    xmlResult.append("</parameters>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue("Erreur 2 : ", false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : parameter without key element
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param value=\"B\"/>");
    xmlResult.append("</parameters>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : parameter without value element
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<parameters>");
    xmlResult.append("<param key=\"toto\"/>");
    xmlResult.append("</parameters>");
    try {
      Parameters.unmarshall(xmlResult.toString(), PARAMETERS_SCHEMA);
      Assert.assertTrue(false);
    } catch(Exception e) {
      Assert.assertTrue(true);
    }
    
    // Test : without root tag
    xmlResult = new StringBuffer();
    xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlResult.append("<param key=\"A\" value=\"B\"/>");
    xmlResult.append("<param key=\"C\" value=\"D\"/>");
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
    p.marshall();
    
    // test.xml =? test2.xml
    try {
      
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      
      File xml1 = new File(ParametersTest.class.getClassLoader().getResource("simple.xml").getPath());
      Document doc1 = db.parse(xml1);
      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 5);
      
      File xml2 = new File("target/test-classes/test2.xml");
      Document doc2 = db.parse(xml2);
      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 5);
      
      // assertXMLEqual(doc1, doc2);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
