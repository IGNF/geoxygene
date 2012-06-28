package fr.ign.cogit.geoxygene.spatial.util;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class LineStringSorterTest {
  IDirectPosition p1;
  IDirectPosition p2;
  IDirectPosition p3;
  IDirectPosition p4;
  IDirectPosition p5;
  IDirectPosition p6;
  IDirectPosition p7;
  ILineString line1;
  ILineString line2;
  ILineString line3;
  ILineString line4;
  ILineString line5;

  @Before
  public void setUp() throws Exception {
    p1 = new DirectPosition(0, 0, 0);
    p2 = new DirectPosition(1, 0, 0);
    p3 = new DirectPosition(1, 1, 0);
    p4 = new DirectPosition(0, 1, 0);
    p5 = new DirectPosition(0, -1, 0);
    p6 = new DirectPosition(0.5, 0.5, 0);
    p7 = new DirectPosition(1, -1, 0);
    line1 = new GM_LineString(p1, p2, p3);
    line2 = new GM_LineString(p5, p6);
    line3 = new GM_LineString(p6, p7);
    line4 = new GM_LineString(p4, p5);
    line5 = new GM_LineString(p5, p7);
  }

  @Test
  public void testSort() {
    List<ILineString> result = LineStringSorter.sort(line1, Arrays.asList(line2, line3, line4,
        line5));
    for (ILineString l : result) {
      System.out.println(l);
    }
    Assert.assertEquals(Arrays.asList(line4, line2, line3), result);
  }
}
