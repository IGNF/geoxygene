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
package fr.ign.cogit.cartagen.graph.triangulation;

import fr.ign.cogit.cartagen.graph.ITriangleFace;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * The triangle interface
 * @author JGaffuri
 */
public interface TriangulationTriangle extends ITriangleFace {

  /**
	 */
  public TriangulationPoint getPoint1();

  /**
	 */
  public TriangulationPoint getPoint2();

  /**
	 */
  public TriangulationPoint getPoint3();

  /**
   * @return The geometry of the triangle
   */
  @Override
  public IPolygon getGeom();

}
