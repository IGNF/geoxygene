package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class BufferComputingTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testBuildHalfOffsetLine() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 0.0);
    IDirectPosition p3 = new DirectPosition(20.0, 0.0);
    IDirectPosition p4 = new DirectPosition(20.0, 10.0);
    IDirectPosition p6 = new DirectPosition(0.0, 5.0);
    IDirectPosition p7 = new DirectPosition(10.0, 5.0);
    IDirectPosition p8 = new DirectPosition(20.0, 5.0);
    IDirectPosition p9 = new DirectPosition(0.0, 5.0);
    IDirectPosition p10 = new DirectPosition(10.0, 5.0);
    IDirectPosition p11 = new DirectPosition(15.0, 5.0);
    IDirectPosition p12 = new DirectPosition(15.0, 10.0);
    IDirectPositionList list1 = new DirectPositionList();
    IDirectPositionList list2 = new DirectPositionList();
    IDirectPositionList list3 = new DirectPositionList();
    IDirectPositionList list4 = new DirectPositionList();
    list1.add(p1);
    list1.add(p2);
    list1.add(p3);
    ILineString line1 = new GM_LineString(list1);
    list2.add(p6);
    list2.add(p7);
    list2.add(p8);
    ILineString line2 = new GM_LineString(list2);
    Assert.assertTrue(line2.equals(BufferComputing.buildHalfOffsetLine(
        Side.LEFT, line1, 5.0)));
    Assert.assertTrue(line1.equals(BufferComputing.buildHalfOffsetLine(
        Side.RIGHT, line2, 5.0)));
    list3.add(p1);
    list3.add(p2);
    list3.add(p3);
    list3.add(p4);
    ILineString line3 = new GM_LineString(list3);
    list4.add(p9);
    list4.add(p10);
    list4.add(p11);
    list4.add(p12);
    ILineString line4 = new GM_LineString(list4);
    Assert.assertTrue(line4.equals((IGeometry) BufferComputing
        .buildHalfOffsetLine(Side.LEFT, line3, 5.0)));
    IDirectPositionList list5 = new DirectPositionList();
    list5.add(new DirectPosition(0.0, 10.0));
    list5.add(new DirectPosition(20.0, 10.0));
    list5.add(new DirectPosition(20.0, 0.0));
    ILineString line5 = new GM_LineString(list5);
    IDirectPositionList list6 = new DirectPositionList();
    list6.add(new DirectPosition(0.0, 5.0));
    list6.add(new DirectPosition(15.0, 5.0));
    list6.add(new DirectPosition(15.0, 0.0));
    ILineString line6 = new GM_LineString(list6);
    Assert.assertTrue(line6.equals(BufferComputing.buildHalfOffsetLine(
        Side.RIGHT, line5, 5.0)));
  }
}
