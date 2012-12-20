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

import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * The point interface
 * @author jgaffuri
 */
public interface TriangulationPoint extends INode {

  /**
   * @return the position of the point
   */
  @Override
  public IDirectPosition getPosition();

  /**
   * @return An index used to improve the triangulation process speed
   */
  public int getIndex();

  public void setIndex(int i);

  /**
   * @param point
   * @return True if there is a segment in the triangulation linking the point
   *         to the given other
   */
  public boolean isLinkedBySegment(TriangulationPoint point);

  /**
   * @return The geometry of the point
   */
  @Override
  public IPoint getGeom();

  public void setGeom(IGeometry geom);

}
