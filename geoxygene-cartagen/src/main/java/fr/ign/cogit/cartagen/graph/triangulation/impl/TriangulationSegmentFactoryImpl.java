/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.graph.triangulation.impl;

import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegment;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationSegmentFactory;

/**
 * A default segment factory implementation
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationSegmentFactoryImpl implements
    TriangulationSegmentFactory {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationSegmentFactory
   * #create(fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint)
   */
  @Override
  public TriangulationSegment create(TriangulationPoint point1,
      TriangulationPoint point2) {
    return new TriangulationSegmentImpl(point1, point2);
  }

}
