package fr.ign.cogit.geoxygene.spatial.coordgeom;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;

public class GM_LineStringTest {
  IDirectPosition p1;
  IDirectPosition p2;
  IDirectPosition p3;
  IDirectPosition p4;
  IDirectPosition p5;
  IDirectPosition p6;
  GM_LineString line1;
  GM_LineString line2;
  GM_Curve curve;

  @Before
  public void setUp() throws Exception {
    p1 = new DirectPosition(0, 0, 0);
    p2 = new DirectPosition(1, 0, 0);
    p3 = new DirectPosition(1, 1, 0);
    p4 = new DirectPosition(0, 1, 0);
    p5 = new DirectPosition(-1, 1, 0);
    p6 = new DirectPosition(-1, 0, 0);
    line1 = new GM_LineString(p1, p2, p3, p4);
    line2 = new GM_LineString(p4, p5, p6, p1);
    curve = new GM_Curve();
    curve.addSegment(line1);
    curve.addSegment(line2);
  }

  @Test
  public void testParamForPoint() {
    double param = line1.paramForPoint(new DirectPosition(1, 0.5, 0))[0];
    Assert.assertEquals(1.5, param, 0.01);
    param = curve.paramForPoint(new DirectPosition(1, 0.5, 0))[0];
    Assert.assertEquals(1.5, param, 0.01);
    param = line2.paramForPoint(p5)[0];
    Assert.assertEquals(1, param, 0.01);
    param = curve.paramForPoint(p5)[0];
    Assert.assertEquals(4, param, 0.01);
  }

  @Test
  public void testStartPoint() {
    Assert.assertEquals(p1, line1.startPoint());
  }

  @Test
  public void testEndPoint() {
    Assert.assertEquals(p4, line1.endPoint());
  }

  @Test
  public void testConstrParam() {
    IDirectPosition param1 = line1.constrParam(0);
    IDirectPosition param2 = line1.constrParam(0.5);
    IDirectPosition param3 = line1.constrParam(1);
    Assert.assertEquals(p1, param1);
    Assert.assertEquals(new DirectPosition(1, 0.5, 0), param2);
    Assert.assertEquals(p4, param3);
    param1 = curve.constrParam(0);
    param2 = curve.constrParam(0.5);
    param3 = curve.constrParam(1);
    Assert.assertEquals(p1, param1);
    Assert.assertEquals(p4, param2);
    Assert.assertEquals(p1, param3);
  }

  @Test
  public void testEndConstrParam() {
    Assert.assertEquals(1, line1.endConstrParam(), 0.01);
  }

  @Test
  public void testEndParam() {
    Assert.assertEquals(line1.length(), line1.endParam(), 0.01);
  }

  @Test
  public void testLengthIDirectPositionIDirectPosition() {
    Assert.assertEquals(1, line1.length(p2, p3), 0.01);
    Assert.assertEquals(4, curve.length(p2, p6), 0.01);
  }

  @Test
  public void testLengthDoubleDouble() {
    Assert.assertEquals(1, line1.length(0.3333, 0.6666), 0.01);
    Assert.assertEquals(2, curve.length(0.3333, 0.6666), 0.01);
  }

  @Test
  public void testParam() {
    IDirectPosition param1 = line1.param(0);
    IDirectPosition param2 = line1.param(1);
    IDirectPosition param3 = line1.param(2);
    IDirectPosition param4 = line1.param(3);
    Assert.assertEquals(p1, param1);
    Assert.assertEquals(p2, param2);
    Assert.assertEquals(p3, param3);
    Assert.assertEquals(p4, param4);
    param1 = curve.param(0);
    param2 = curve.param(2);
    param3 = curve.param(4);
    param4 = curve.param(6);
    Assert.assertEquals(p1, param1);
    Assert.assertEquals(p3, param2);
    Assert.assertEquals(p5, param3);
    Assert.assertEquals(p1, param4);
  }

  @Test
  public void testStartConstrParam() {
    Assert.assertEquals(0, line1.startConstrParam(), 0.01);
  }

  @Test
  public void testStartParam() {
    Assert.assertEquals(0, line1.startParam(), 0.01);
  }
  
  @Test
  public void testInside() {
    IPolygon polygon = new GM_Polygon(new GM_LineString(p1,p2,p3,p4,p5,p6,p1));
    ILineString line3 = new GM_LineString(p1,p4);
    System.out.println(polygon.contains(line1));
    System.out.println(polygon.contains(line2));
    System.out.println(polygon.contains(line3));
  }
}
