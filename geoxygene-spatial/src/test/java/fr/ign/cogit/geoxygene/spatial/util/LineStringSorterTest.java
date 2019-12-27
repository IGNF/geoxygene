package fr.ign.cogit.geoxygene.spatial.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import junit.framework.Assert;

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
//    for (ILineString l : result) {
//      System.out.println(l);
//    }
    Assert.assertEquals(Arrays.asList(line4, line2, line3), result);
    ILineString l = new GM_LineString(
        new DirectPosition(651128.2399543846, 6861330.51367064),
        new DirectPosition(651128.2399543575, 6861330.513667467),
        new DirectPosition(651126.8, 6861362.7),
        new DirectPosition(651124.9, 6861379.5)
    );    
    /*    
    LINESTRING (
        651128.2399543846 6861330.51367064 208062339707572470000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 
        651128.2399543575 6861330.513667467 13710458074977423000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 
        651126.8 6861362.7 33233305273509640000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000, 
        651124.9 6861379.5 0
    )
    */
    ILineString l1 = new GM_LineString(
        new DirectPosition(651122.5650124006, 6861360.588720706),
        new DirectPosition(651131.5104028855, 6861365.078859089)
    );
    ILineString l2 = new GM_LineString(
        new DirectPosition(651131.6845765973, 6861360.470596566),
        new DirectPosition(651122.7929930952, 6861360.134530621)
    );
    ILineString l3 = new GM_LineString(
        new DirectPosition(651123.5411303148, 6861357.7533064), 
        new DirectPosition(651131.6902512497, 6861360.3204574445)
    );
    ILineString l4 = new GM_LineString(
        new DirectPosition(651131.7755160853, 6861358.064532925),
        new DirectPosition(651123.5411303148, 6861357.7533064)
    );
    ILineString l5 = new GM_LineString(
        new DirectPosition(651132.0299365349, 6861351.333113982),
        new DirectPosition(651124.042100489, 6861351.031206041)
    );
    ILineString l6 = new GM_LineString(
        new DirectPosition(651124.0589372854, 6861350.533388453),
        new DirectPosition(651132.049947978, 6861350.803654156)
    );
    ILineString l7 = new GM_LineString(
        new DirectPosition(651132.1906160566, 6861347.081878759),
        new DirectPosition(651124.1859040088, 6861346.779332973)
    );
    ILineString l8 = new GM_LineString(
        new DirectPosition(651124.2198938279, 6861345.774347863),
        new DirectPosition(651132.2297963421, 6861346.045252511)
    );
    ILineString l9 = new GM_LineString(
        new DirectPosition(651132.3512955783, 6861342.830643537),
        new DirectPosition(651124.3297075287, 6861342.527459906)
    );
    ILineString l10 = new GM_LineString(
        new DirectPosition(651124.3808503704, 6861341.015307275),
        new DirectPosition(651132.4096447063, 6861341.286850867)
    );
    ILineString l11 = new GM_LineString(
        new DirectPosition(651132.2142040946, 6861337.837054882),
        new DirectPosition(651124.4735110486, 6861338.275586839)
    );
    ILineString l12 = new GM_LineString(
        new DirectPosition(651124.5907630872, 6861335.499412037),
        new DirectPosition(651132.0997370766, 6861335.816554354)
    );
    ILineString l13 = new GM_LineString(
        new DirectPosition(651131.9308314561, 6861333.578202283),
        new DirectPosition(651124.6486970368, 6861334.127710758)
    );
    List<ILineString> list = Arrays.asList(l1, l2, l3, l8, l4, l12, l5, l11, l10, l6, l9, l7, l13);
    Collections.shuffle(list);
    result = LineStringSorter.sort(l, list);
//    System.out.println(result.get(0).intersection(l));
//    System.out.println(result.get(result.size() - 1).intersection(l));
//    for (ILineString l : result) {
//      System.out.println(l);
//    }
    Assert.assertEquals(Arrays.asList(l13, l12, l11, l10, l9, l8, l7, l6, l5, l4, l3, l2, l1), result);
    ILineString ll = new GM_LineString(
        new DirectPosition(651036.3, 6861368.800000001, Double.POSITIVE_INFINITY),
        new DirectPosition(651039.4, 6861386.800000001, 0),
        new DirectPosition(651047.7000000001, 6861432.800000001, 0),
        new DirectPosition(651048.49003641, 6861442.991466861, 0),
        new DirectPosition(651048.4900364373, 6861442.991470035, 0));
    ILineString ll1 = new GM_LineString(
        new DirectPosition(651044.082420469, 6861436.968906329),
        new DirectPosition(651054.2437587668, 6861435.603961451));
    ILineString ll2 = new GM_LineString(
        new DirectPosition(651035.4940586238, 6861383.924357968),
        new DirectPosition(651047.1717791874, 6861381.963138461));
    list = Arrays.asList(ll1, ll2);
    result = LineStringSorter.sort(ll, list);
    Assert.assertEquals(Arrays.asList(ll2, ll1), result);    
  }
}
