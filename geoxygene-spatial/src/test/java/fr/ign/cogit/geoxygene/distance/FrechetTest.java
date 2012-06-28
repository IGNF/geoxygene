/**
 * 
 */
package fr.ign.cogit.geoxygene.distance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * @author Julien Perret
 *
 */
public class FrechetTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.distance.Frechet#discreteFrechet(fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString, fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString)}.
   */
  @Test
  public void testDiscreteFrechet() {
    ILineString p = new GM_LineString(new DirectPosition(311, 747), new DirectPosition(805, 746),
        new DirectPosition(285, 424), new DirectPosition(990, 428));
    ILineString q = new GM_LineString(new DirectPosition(301, 629), new DirectPosition(438, 773),
        new DirectPosition(528, 727), new DirectPosition(674, 769), new DirectPosition(829, 742),
        new DirectPosition(282, 506), new DirectPosition(903, 401), new DirectPosition(954, 483));
//    p = p.asLineString(2.0, 0);
//    q = q.asLineString(2.0, 0);
//    List<IDirectPosition> listq = new ArrayList<IDirectPosition>(q.getControlPoint());
//    for (IDirectPosition point : p.getControlPoint()) {
//      q = Operateurs.projectionEtInsertion(point, q);
//    }
//    for (IDirectPosition point : listq) {
//      p = Operateurs.projectionEtInsertion(point, p);
//    }
    System.out.println(p);
    System.out.println(q);
    double d = Frechet.discreteFrechet(p, q);
    System.out.println(d);
    d = Frechet.discreteFrechetWithProjection(p, q);
    System.out.println(d);
    
    p = new GM_LineString(new DirectPosition(283, 253), new DirectPosition(835, 296),
        new DirectPosition(859, 52));
    q = new GM_LineString(new DirectPosition(282, 264), new DirectPosition(470.35780999672346,
        267.5949018656868), new DirectPosition(537, 186), new DirectPosition(682, 303),
        new DirectPosition(911, 266), new DirectPosition(811, 41));
    d = Frechet.discreteFrechet(p, q);
    System.out.println(d);
    d = Frechet.discreteFrechetWithProjection(p, q);
    System.out.println(d);

  }

}
