package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class OperateursTest {
  IDirectPosition point1;
  IDirectPosition point2;
  IDirectPosition point3;
  IDirectPosition point4;
  IDirectPosition point5;
  IDirectPosition point6;
  IDirectPosition point7;
  ILineString line1;

  @Before
  public void setUp() throws Exception {
    this.point1 = new DirectPosition(1, 0);
    this.point2 = new DirectPosition(3, 0);
    this.point3 = new DirectPosition(0, 1);
    this.point4 = new DirectPosition(1, 1);
    this.point5 = new DirectPosition(2, 1);
    this.point6 = new DirectPosition(3, 1);
    this.point7 = new DirectPosition(4, 1);
    this.line1 = new GM_LineString(this.point1, this.point2);
  }

  @Test
  public void testProjectionIDirectPositionIDirectPositionIDirectPosition() {
    IDirectPosition projection1 = Operateurs.projection(this.point3,
        this.point1, this.point2);
    Assert.assertTrue(projection1.equals(this.point1));
    IDirectPosition projection2 = Operateurs.projection(this.point4,
        this.point1, this.point2);
    Assert.assertTrue(projection2.equals(this.point1));
    IDirectPosition projection3 = Operateurs.projection(this.point5,
        this.point1, this.point2);
    Assert.assertTrue(projection3.equals(new DirectPosition(2, 0)));
    IDirectPosition projection4 = Operateurs.projection(this.point6,
        this.point1, this.point2);
    Assert.assertTrue(projection4.equals(this.point2));
    IDirectPosition projection5 = Operateurs.projection(this.point7,
        this.point1, this.point2);
    Assert.assertTrue(projection5.equals(this.point2));
  }

  @Test
  public void testProjectionIDirectPositionILineString() {
    IDirectPosition projection1 = Operateurs
        .projection(this.point3, this.line1);
    Assert.assertTrue(projection1.equals(this.point1));
    IDirectPosition projection2 = Operateurs
        .projection(this.point4, this.line1);
    Assert.assertTrue(projection2.equals(this.point1));
    IDirectPosition projection3 = Operateurs
        .projection(this.point5, this.line1);
    Assert.assertTrue(projection3.equals(new DirectPosition(2, 0)));
    IDirectPosition projection4 = Operateurs
        .projection(this.point6, this.line1);
    Assert.assertTrue(projection4.equals(this.point2));
    IDirectPosition projection5 = Operateurs
        .projection(this.point7, this.line1);
    Assert.assertTrue(projection5.equals(this.point2));
  }

  @Test
  public void testMilieuIDirectPositionIDirectPosition() {
    IDirectPosition projection1 = Operateurs.milieu(this.point1, this.point2);
    Assert.assertTrue(projection1.equals(new DirectPosition(2, 0)));
  }

  /**
   * Test de fusion d'une liste de Linestrings
   */
  @Test
  public void testCompileArcs() {
    WKTReader reader = new WKTReader();
    try {
      Geometry g1 = reader
          .read("LINESTRING ( 651124.9 6861379.5 1365, 651134 6861382.300000001 1364, 651156.2000000001 6861384.5 1363 )");
      Geometry g2 = reader
          .read("LINESTRING ( 651156.2000000001 6861384.5 1363, 651189.3 6861387.9 1361 )");
      List<ILineString> edges = new ArrayList<ILineString>();
      ILineString line1 = (ILineString) AdapterFactory.toGM_Object(g1);
      ILineString line2 = (ILineString) AdapterFactory.toGM_Object(g2);
      edges.add(line1);
      edges.add(line2);
      ILineString result = Operateurs.compileArcs(edges);
      System.out.println(result);
      edges.remove(line1);
      edges.add(line1);
      result = Operateurs.compileArcs(edges);
      Assert.assertNotNull(result);
      System.out.println(result);
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testCompileArcWithTolerance() {
    WKTReader reader = new WKTReader();
    try {
      Geometry g1 = reader
          .read("LINESTRING (601103.3340069023 129288.05055561525, 601102.0842804064 129856.67611130093, 601320.7864172085 129316.79426502353, 601323.263544294 129847.90736367383)");
      Geometry g2 = reader
          .read("LINESTRING (601323.2858702005 129847.92802582885, 601824.4261951016 129860.42529078897, 601968.144742143 129494.25542745732, 601806.9300241574 129236.81176927875, 601447.0087933057 129430.51937616068, 601422.0142633856 129672.9663163871, 601549.4863659788 129830.43185488468)");

      ILineString line1 = (ILineString) AdapterFactory.toGM_Object(g1);
      ILineString line2 = (ILineString) AdapterFactory.toGM_Object(g2);
      List<ILineString> list = new ArrayList<ILineString>();
      list.add(line1);
      list.add(line2);
      ILineString result = Operateurs.compileArcs(list, 1.0);
      Assert.assertNotNull(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCompileArcsColn() {
    IDirectPositionList list1 = new DirectPositionList();
    IDirectPositionList list2 = new DirectPositionList();
    IDirectPositionList list3 = new DirectPositionList();
    IDirectPositionList list4 = new DirectPositionList();
    list1.add(new DirectPosition(0.0, 0.0));
    list4.add(new DirectPosition(0.0, 0.0));
    list1.add(new DirectPosition(2.0, 2.0));
    list4.add(new DirectPosition(2.0, 2.0));
    list1.add(new DirectPosition(4.0, 5.0));
    list4.add(new DirectPosition(4.0, 5.0));

    list2.add(new DirectPosition(4.0, 5.0));
    list2.add(new DirectPosition(8.0, 7.0));
    list4.add(new DirectPosition(8.0, 7.0));
    list2.add(new DirectPosition(10.0, 10.0));
    list4.add(new DirectPosition(10.0, 10.0));

    list3.add(new DirectPosition(10.0, 10.0));
    list3.add(new DirectPosition(15.0, 13.0));
    list4.add(new DirectPosition(15.0, 13.0));
    list3.add(new DirectPosition(20.0, 20.0));
    list4.add(new DirectPosition(20.0, 20.0));

    ILineString line1 = new GM_LineString(list1);
    ILineString line2 = new GM_LineString(list2);
    ILineString line3 = new GM_LineString(list3);
    ILineString finalLine = new GM_LineString(list4);
    Set<ILineString> linesToCompile = new HashSet<>();
    linesToCompile.add(line3);
    linesToCompile.add(line1);
    linesToCompile.add(line2);
    ILineString compiled = Operateurs.compileArcs(linesToCompile, 0.1);
    Assert.assertEquals(finalLine, compiled);
  }

  @Test
  public void testUnionWithTolerance() {
    WKTReader reader = new WKTReader();
    try {
      Geometry g1 = reader
          .read("LINESTRING (601103.3340069023 129288.05055561525, 601102.0842804064 129856.67611130093, 601320.7864172085 129316.79426502353, 601323.263544294 129847.90736367383)");
      Geometry g2 = reader
          .read("LINESTRING (601323.2858702005 129847.92802582885, 601824.4261951016 129860.42529078897, 601968.144742143 129494.25542745732, 601806.9300241574 129236.81176927875, 601447.0087933057 129430.51937616068, 601422.0142633856 129672.9663163871, 601549.4863659788 129830.43185488468)");

      ILineString line1 = (ILineString) AdapterFactory.toGM_Object(g1);
      ILineString line2 = (ILineString) AdapterFactory.toGM_Object(g2);
      List<ILineString> list = new ArrayList<ILineString>();
      list.add(line1);
      list.add(line2);
      ILineString result = Operateurs.union(list, 1.0);
      if (result.equals(line1)) {
        Assert.fail();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
