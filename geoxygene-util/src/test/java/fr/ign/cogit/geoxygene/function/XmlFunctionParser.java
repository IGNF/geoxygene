package fr.ign.cogit.geoxygene.function;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.w3c.dom.Document;

import junit.framework.Assert;

/**
 *
 */
public class XmlFunctionParser extends XMLTestCase {
  
  @Test 
  public void testUnmarshallConstantFunctionFile() {
    try {
      ConstantFunction p = (ConstantFunction) ConstantFunction.unmarshall(new File(XmlFunctionParser.class.getClassLoader().getResource("ConstantFunction.xml").getPath()));
      
      Assert.assertEquals(p.getShift(), 0.1);
      Assert.assertEquals(p.getLowerBoundDF(), 800.0);
      Assert.assertEquals(p.getUpperBoundDF(), 1500.0);
    
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallLinearFunctionFile() {
    try {
      LinearFunction p = (LinearFunction) LinearFunction.unmarshall(new File(
          XmlFunctionParser.class.getClassLoader().getResource("LinearFunction.xml").getPath()));
      
      Assert.assertEquals(p.getA(), -0.001125);
      Assert.assertEquals(p.getB(), 1.0);
      Assert.assertEquals(p.getLowerBoundDF(), 0.0);
      Assert.assertEquals(p.getUpperBoundDF(), 400.0);
    
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testMarshallConstantFunction() {
    
    ConstantFunction cf = new ConstantFunction(0.1);
    cf.setDomainOfFunction(800., 1500., true, true);
    cf.marshall("target/test-classes/ConstantFunction2.xml");
    
    try {
      
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      
      File xml1 = new File(XmlFunctionParser.class.getClassLoader().getResource("ConstantFunction.xml").getPath());
      Document doc1 = db.parse(xml1);
      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 3);
      
      File xml2 = new File("target/test-classes/ConstantFunction2.xml");
      Document doc2 = db.parse(xml2);
      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 3);
      
      assertXMLEqual(doc1, doc2);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

}
