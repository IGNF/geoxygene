/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.geometrie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * @author Julien Perret
 *
 */
public class DistancesTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.contrib.geometrie.Distances#ecartSurface(fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString, fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString)}.
   */
  @Test
  public void testEcartSurface() {
    ILineString l1 = new GM_LineString(new DirectPosition(0,0), new DirectPosition(10,0));
    ILineString l2 = new GM_LineString(new DirectPosition(0,10), new DirectPosition(10,10), new DirectPosition(10,30));
    double ecart = Distances.ecartSurface(l1, l2);
    Assert.assertEquals(100, ecart, 0.001);
    ILineString l3 = new GM_LineString(new DirectPosition(0,0), new DirectPosition(0,10));
    ecart = Distances.ecartSurface(l1, l3);
    Assert.assertEquals(50, ecart, 0.001);
    ILineString l4 = new GM_LineString(new DirectPosition(0,0), new DirectPosition(0,10), new DirectPosition(10,10), new DirectPosition(10,0));
    ILineString l5 = new GM_LineString(new DirectPosition(0,0), new DirectPosition(10,0));
    ecart = Distances.ecartSurface(l5, l4);
    Assert.assertEquals(25, ecart, 0.001);
  }
}
