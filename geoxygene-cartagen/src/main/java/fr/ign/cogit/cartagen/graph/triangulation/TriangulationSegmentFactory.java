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
 * The segment factory interface
 * 
 * @author jgaffuri
 * 
 */
public interface TriangulationSegmentFactory {

  /**
   * Create a segment composed of 2 specified points
   * 
   * @param p1
   * @param p2
   * @return
   */
  public TriangulationSegment create(TriangulationPoint p1,
      TriangulationPoint p2);

}
