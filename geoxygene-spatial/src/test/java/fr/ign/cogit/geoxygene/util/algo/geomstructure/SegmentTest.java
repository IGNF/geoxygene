package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class SegmentTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testIntersectionWithCircle() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 0.0);
    IDirectPosition p3 = new DirectPosition(5.0, 0.0);
    IDirectPosition p4 = new DirectPosition(15.0, 0.0);
    Segment segment = new Segment(p1, p2);
    System.out.println(segment.getCoefA());
    System.out.println(segment.getCoefB());
    System.out.println(segment.getCoefC());
    Set<IDirectPosition> inter = segment.intersectionWithCircle(p2, 5.0);
    Iterator<IDirectPosition> iter = inter.iterator();
    IDirectPosition inter1 = iter.next();
    IDirectPosition inter2 = iter.next();
    Assert.assertTrue(inter1.equals(p3) || inter2.equals(p3));
    Assert.assertTrue(inter1.equals(p4) || inter2.equals(p4));
  }

  @Test
  public void testContainsPoint() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 10.0);
    IDirectPosition p3 = new DirectPosition(5.0, 5.0);
    IDirectPosition p4 = new DirectPosition(25.0, 25.0);
    Segment segment = new Segment(p1, p2);
    Assert.assertTrue(segment.containsPoint(p3));
    Assert.assertFalse(segment.containsPoint(p4));
  }
}
