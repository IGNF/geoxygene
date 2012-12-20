/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.graph.triangulation;

/**
 * The triangle factory interface
 * 
 * @author jgaffuri
 * 
 */
public interface TriangulationTriangleFactory {

  /**
   * Create a triangle composed of 3 specified points
   * 
   * @param p1
   * @param p2
   * @param p3
   * @return
   */
  public TriangulationTriangle create(TriangulationPoint p1,
      TriangulationPoint p2, TriangulationPoint p3);

}
