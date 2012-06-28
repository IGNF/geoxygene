package fr.ign.cogit.geoxygene.util.algo;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class JtsAlgorithmsTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testOffsetCurveILineStringDouble() {
    IDirectPosition p1 = new DirectPosition(651049.4584816409, 6861902.601655778);
    IDirectPosition p2 = new DirectPosition(651069.3941174329, 6861891.803186391);
    ILineString line = new GM_LineString(p1, p2);
    double distance = 3.0 / 4.0;
    IMultiCurve<ILineString> offset1 = JtsAlgorithms.offsetCurve(line, distance);
    IMultiCurve<ILineString> offset2 = JtsAlgorithms.offsetCurve(line.reverse(), distance);
    System.out.println(offset1);
    System.out.println(offset2);
    
    IDirectPosition p3 = new DirectPosition(651345.6220778765, 6861505.810661668);
    IDirectPosition p4 = new DirectPosition(651246.3, 6861498.9);
    IDirectPosition p5 = new DirectPosition(651271.8, 6861500.7);
    IDirectPosition p6 = new DirectPosition(651304.3, 6861502.9);
    IDirectPosition p7 = new DirectPosition(651327.2000000001, 6861504.5);
    IDirectPosition p8 = new DirectPosition(651230.7842965396, 6861497.820646716);
    line = new GM_LineString(p3, p7, p6, p5, p4, p8);
    System.out.println(line);
    distance = 1.0;
    offset1 = JtsAlgorithms.offsetCurve(line, distance);
    offset2 = JtsAlgorithms.offsetCurve(line.reverse(), distance);
    System.out.println(offset1);
    System.out.println(offset2);
    
  }

}
