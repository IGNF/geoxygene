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
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangleFactory;

/**
 * A default triangle factory implementation
 * 
 * @author jgaffuri
 * 
 */
public class TriangulationTriangleFactoryImpl implements
    TriangulationTriangleFactory {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationTriangleFactory
   * #create(fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint,
   * fr.ign.cogit.generalisation.lib.triangulation.TriangulationPoint)
   */
  @Override
  public TriangulationTriangle create(TriangulationPoint point1,
      TriangulationPoint point2, TriangulationPoint point3) {
    return new TriangulationTriangleImpl(point1, point2, point3);
  }

}
