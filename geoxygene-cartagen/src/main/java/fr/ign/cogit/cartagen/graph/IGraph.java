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

import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public interface IGraph {

  /**
   * the id of the graph
   */
  public int getId();

  public void setId(int id);

  /**
   * the edges of the graph
   */
  public Set<IEdge> getEdges();

  public void setEdges(Set<IEdge> arcs);

  /**
   * the nodes of the graph
   */
  public Set<INode> getNodes();

  public void setNodes(Set<INode> nodes);

  /**
   * the name of the graph
   */
  public String getName();

  public void setName(String name);

  /**
   * is the graph oriented ?
   */
  public boolean isOriented();

  public void setOriented(boolean oriented);

  /**
   * the paths within the graph
   */
  public Set<GraphPath> getPaths();

  public void setPaths(Set<GraphPath> paths);

  /**
   * Get all the edges connecting two nodes.
   * @param node1
   * @param node2
   * @return
   */
  public Set<IEdge> getEdgesConnectingNodes(INode node1, INode node2);

  /**
   * Get the node of the graph that is located at a given position.
   * @param pt
   * @return
   */
  public INode getNodeAt(IDirectPosition pt);
}
