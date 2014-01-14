package fr.ign.parameters;

import java.io.File;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;

/**
 *
 */
public class ParameterTest extends XMLTestCase {
  
  @Test
  public void testSetGetParameter()  {
    Parameters parameters = new Parameters();
    parameters.set("A", "1");
    parameters.set("B", "2");
    Parameters direction = new Parameters();
    direction.set("sens", "true");
    direction.set("attribut", "circulation");
    direction.set("direct", "1");
    parameters.add(direction);
    // parameters.marshall();
    assertEquals(parameters.get("A").toString(), "1");
    assertEquals(parameters.getString("B"), "2");
    assertEquals(parameters.getString("attribut"), "circulation");
    assertEquals(parameters.getBoolean("sens"), true);
    assertEquals(parameters.getInteger("direct"), 1);
  }
  
  @Test
  public void testLevel4Parameter()  {
    Parameters p1 = new Parameters();
    Parameters p2 = new Parameters();
    Parameters p3 = new Parameters();
    Parameters p4 = new Parameters();
    p4.set("truc", "machin");
    p3.add(p4);
    p2.add(p3);
    p1.add(p2);
    // p1.marshall();
    assertEquals(p1.getString("truc"), "machin");
    assertEquals(p1.getString("machin"), "");
  }
  
  @Test 
  public void testUnmarshallFileWithoutValidation() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("complex.xml").getPath()));
      assertEquals(p.getString("A"), "1");
      assertEquals(p.getString("B"), "2");
      assertEquals(true, p.getString("C").equals("13") || p.getString("C").equals("23") || p.getString("C").equals("33")
          || p.getString("C").equals("43"));
      assertEquals(p.getString("D"), "14");
      assertEquals(p.getString("E"), "25");
      assertEquals(p.getString("F"), "6");
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test 
  public void testUnmarshallBuildingParameterFile() {
    try {
      Parameters p = Parameters.unmarshall(new File(ParametersTest.class.getClassLoader().getResource("network_matching_parameters.xml").getPath()));
      
      // Test value
      assertEquals(p.getFloat("distanceNoeudsMax"), new Float(150.0));
      assertEquals(p.getBoolean("topologieGraphePlanaire1"), false);
      assertEquals(p.getBoolean("varianteChercheRondsPoints"), false);
      
      // Test direction values
      assertEquals(p.getBoolean("direction1", "populationsArcsAvecOrientationDouble"), true);
      assertEquals(p.getString("direction1", "attributOrientation"), "");
      
      assertEquals(p.getBoolean("direction2", "populationsArcsAvecOrientationDouble"), false);
      assertEquals(p.getString("direction2", "attributOrientation"), "orientation");
      assertEquals(p.getString("direction2", "SENS_DIRECT"), "1");
      assertEquals(p.getString("direction2", "DOUBLE_SENS"), "2");
      
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test 
  public void testReturnObject() {
    Direction direction = new Direction();
    direction.setDoubleSens(false);
    direction.setValueSensDirect("T");
    
    // New parameter
    Parameters p = new Parameters();
    p.set("direction1", direction);
    p.marshall();
    
    Direction retour = (Direction)p.get("direction1");
    retour.toString();
    assertEquals(retour.toString(), "SimpleSens ('T')");
    
    System.out.println("---------------------");
    
    // Old parameter
    /*ParametersOld po = new ParametersOld();
    po.set("direction1", direction);
    po.marshall();
    
    retour = (Direction)po.get("direction1");
    retour.toString();
    assertEquals(retour.toString(), "SimpleSens ('T')");*/
  }

}
