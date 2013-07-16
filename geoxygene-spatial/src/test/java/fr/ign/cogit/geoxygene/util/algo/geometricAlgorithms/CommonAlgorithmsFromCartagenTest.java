package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

public class CommonAlgorithmsFromCartagenTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testAreLinesSameDirection() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 0.0);
    IDirectPosition p3 = new DirectPosition(20.0, 0.0);
    IDirectPosition p4 = new DirectPosition(30.0, 5.0);
    IDirectPosition p5 = new DirectPosition(40.0, 10.0);
    IDirectPositionList list1 = new DirectPositionList();
    IDirectPositionList list2 = new DirectPositionList();
    IDirectPositionList list3 = new DirectPositionList();
    list1.add(p1);
    list1.add(p2);
    list1.add(p3);
    list1.add(p4);
    list1.add(p5);
    ILineString line1 = new GM_LineString(list1);
    list2.add(p2);
    list2.add(p3);
    list2.add(p4);
    ILineString line2 = new GM_LineString(list2);
    list3.add(p4);
    list3.add(p3);
    list3.add(p2);
    ILineString line3 = new GM_LineString(list3);
    Assert.assertTrue(CommonAlgorithmsFromCartAGen.areLinesSameDirection(line1,
        line2));
    Assert.assertFalse(CommonAlgorithmsFromCartAGen.areLinesSameDirection(
        line1, line3));
  }

  @Test
  public void testOpenRingAtIndex() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 0.0);
    IDirectPosition p3 = new DirectPosition(20.0, 0.0);
    IDirectPosition p4 = new DirectPosition(30.0, 5.0);
    IDirectPosition p5 = new DirectPosition(40.0, 10.0);
    IDirectPosition p6 = new DirectPosition(0.0, 10.0);
    IDirectPositionList list1 = new DirectPositionList();
    list1.add(p1);
    list1.add(p2);
    list1.add(p3);
    list1.add(p4);
    list1.add(p5);
    list1.add(p6);
    list1.add(p1);
    IRing line1 = new GM_Ring(new GM_LineString(list1));
    IDirectPositionList list2 = new DirectPositionList();
    list2.add(p3);
    list2.add(p4);
    list2.add(p5);
    list2.add(p6);
    list2.add(p1);
    list2.add(p2);
    ILineString line2 = new GM_LineString(list2);
    Assert.assertTrue(line2.equals(CommonAlgorithmsFromCartAGen
        .openRingAtIndex(line1, 2)));
  }

  @Test
  public void testInsertVertex() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 10.0);
    IDirectPosition p3 = new DirectPosition(20.0, 20.0);
    IDirectPosition p4 = new DirectPosition(30.0, 30.0);
    IDirectPosition p5 = new DirectPosition(40.0, 50.0);
    IDirectPosition p6 = new DirectPosition(50.0, 55.0);
    IDirectPosition newVertex = new DirectPosition(25.0, 25.0);
    IDirectPositionList list1 = new DirectPositionList();
    list1.add(p1);
    list1.add(p2);
    list1.add(p3);
    list1.add(p4);
    list1.add(p5);
    list1.add(p6);
    ILineString line1 = new GM_LineString(list1);
    IDirectPositionList list2 = new DirectPositionList();
    list2.add(p1);
    list2.add(p2);
    list2.add(p3);
    list2.add(newVertex);
    list2.add(p4);
    list2.add(p5);
    list2.add(p6);
    ILineString line2 = new GM_LineString(list2);
    ILineString line3 = CommonAlgorithmsFromCartAGen.insertVertex(line1,
        newVertex);
    Assert.assertTrue(line2.equals(line3));
  }

}
