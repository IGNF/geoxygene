/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/*
 * ###### IGN / CartAGen ###### Title: IFace Description: A face of a Graph
 * object. Author: J. Renard Version: 1.0 Changes: 1.0 (13/01/12) : creation
 */

public interface IFace {

  /**
   * The universal id of the face
   */
  public int getId();

  public void setId(int id);

  /**
   * The geometry of the face
   */
  public IPolygon getGeom();

  public void setGeom(IPolygon geom);

  /**
   * The graph the face is part of
   */
  public IGraph getGraph();

  public void setGraph(IGraph graph);

}
