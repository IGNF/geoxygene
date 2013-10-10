package fr.ign.parameters;

import junit.framework.Assert;

import org.junit.Test;

public class ParametersTest {

  @Test
  public void testSetGetParameter() {
    Parameters p = new Parameters();
    p.set("A", "B");
    String result = p.get("A").toString();
    Assert.assertEquals("B", result);
  }

  @Test 
  public void testUnmarshall() {
    Parameters p = Parameters.unmarshall("./src/test/resources/test.xml");
    String result = p.get("A").toString();
    Assert.assertEquals("B", result);
    result = p.get("C").toString();
    Assert.assertEquals("D", result);
  }

  @Test
  public void testMarshall() {
    Parameters p = new Parameters();
    p.set("A", "B");
    p.set("C", "D");
    p.marshall("./src/test/resources/test.xml");
  }

}
