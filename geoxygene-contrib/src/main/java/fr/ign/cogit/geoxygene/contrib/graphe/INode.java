/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.graphe;

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/*
 * ###### IGN / CartAGen ###### Title: INode Description: A node of a Graph
 * object. Author: J. Renard Version: 1.0 Changes: 1.0 (13/01/12) : creation
 */

public interface INode {

  /**
   * The universal id of the node
   */
  public int getId();

  public void setId(int id);

  /**
   * the possible features attached to the node
   */
  public Set<IFeature> getGeoObjects();

  public void setGeoObjects(Set<IFeature> geoObjects);

  /**
   * 
   */
  public IGraphLinkableFeature getGraphLinkableFeature();

  public void setGraphLinkableFeature(IGraphLinkableFeature feature);

  /**
   * the edges entering the node
   */
  public Set<IEdge> getEdgesIn();

  public void setEdgesIn(Set<IEdge> edgesIn);

  public void addEdgeIn(IEdge edgeIn);

  /**
   * the edges exiting the node
   */
  public Set<IEdge> getEdgesOut();

  public void setEdgesOut(Set<IEdge> edgesOut);

  public void addEdgeOut(IEdge edgeOut);

  /**
   * Edges and degree of the node
   */
  public Set<IEdge> getEdges();

  public int getDegree();

  /**
   * The geometry of the node
   */
  public IPoint getGeom();

  public void setGeom(IPoint geom);

  public IDirectPosition getPosition();

  public IDirectPosition getPositionIni();

  /**
   * The graph the node is part of
   */
  public IGraph getGraph();

  public void setGraph(IGraph graph);

  /**
   * The centralities of the node in the graph
   */
  public double getProximityCentrality();

  public double getBetweenCentrality();

  /**
   * gets the next nodes on the graph
   */
  public Set<INode> getNextNodes();

  /***
   * gets the neighbour edges and nodes in the graph
   */
  public Map<INode, IEdge> getNeighbourEdgeNode();

}
